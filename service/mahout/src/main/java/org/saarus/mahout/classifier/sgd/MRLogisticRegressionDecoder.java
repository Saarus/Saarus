package org.saarus.mahout.classifier.sgd;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.RunningJob;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.mahout.classifier.evaluation.Auc;
import org.apache.mahout.classifier.sgd.OnlineLogisticRegression;
import org.apache.mahout.math.Matrix;
import org.apache.mahout.math.SequentialAccessSparseVector;
import org.apache.mahout.math.Vector;
import org.saarus.service.hadoop.util.FSResource;
import org.saarus.service.hadoop.util.HDFSUtil;

public class MRLogisticRegressionDecoder {
  private String    inputUri ;
  private String    outputUri ;
  private String[]  columnHeaders ;
  private String    modelUri ;
  private boolean   clusterMode ;
  
  public MRLogisticRegressionDecoder setInputUri(String s) {
    this.inputUri = s ;
    return this ;
  }
  
  public MRLogisticRegressionDecoder setOutputUri(String s) {
    this.outputUri = s ;
    return this ;
  }
  
  public MRLogisticRegressionDecoder setColumnHeaders(String[] column) {
    this.columnHeaders = column ;
    return this ;
  }
  
  public MRLogisticRegressionDecoder setModelUri(String s) {
    this.modelUri = s ;
    return this ;
  }
  
  public MRLogisticRegressionDecoder setClusterMode(boolean b) {
    this.clusterMode = b ;
    return this ;
  }
  
  public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, LongWritable, Text> {
    PrintWriter output ;
    LogisticModelParameters lmp ;
    RecordFactoryImpl csv ;
    OnlineLogisticRegression lr ;
    boolean showAuc = true;
    boolean showConfusion  = true;
    boolean showScores  = true;
    Auc collector ;
    int lineNum = 0;
    
    @Override
    public void configure(JobConf job) {
      try {
        output = new PrintWriter(System.out, true) ;
        String[] headers = job.getStrings("column.headers") ;
        String modelFile = job.get("model.file") ;
        String[] args = {
          "--input", "ignored",
          "--model", modelFile
        };
        RunLogisticArgumentParser argParser = new RunLogisticArgumentParser() ;
        argParser.parseArgs(args) ;
        collector = new Auc();
        FSResource modelFSResource = argParser.getModelFSResource() ;
        this.lmp = LogisticModelParameters.loadFrom(modelFSResource.getInputStream());
        this.csv = lmp.getCsvRecordFactory();
        this.lr = lmp.createRegression();
        
        csv.configVariableNames(Arrays.asList(headers)) ;
        if (showScores) {
          output.printf(Locale.ENGLISH, "%s, %s\n", "score", "label");
        }
      } catch(Exception ex) {
        ex.printStackTrace() ;
      }
    }
    
    public void map(LongWritable key, Text value, OutputCollector<LongWritable, Text> mapCollector, Reporter reporter) throws IOException {
      lineNum++ ;
      String line = value.toString() ;
      List<String> vals = Arrays.asList(line.split(",")) ;
      Vector v = new SequentialAccessSparseVector(lmp.getNumFeatures());
      int target = csv.processData(vals, v);

      double score = lr.classifyScalar(v);
      int targetScore = (int)Math.round(score) ;
      String targetLabel = csv.getTargetLabel(targetScore) ;
      if (showScores) {
        output.printf(Locale.ENGLISH, "%d %s %s %.3f, %s\n", lineNum, vals.get(1),vals.get(11), score, targetLabel);
       // output.printf(Locale.ENGLISH, "%d,%.3f,%.6f\n", target, score, lr.logLikelihood(target, v));
      }
      collector.add(target, score);
      String outValue = target + "," + score ;
      mapCollector.collect(key, new Text(outValue)) ;
    }

    @Override
    public void close() throws IOException {
      if (showAuc) {
        output.printf(Locale.ENGLISH, "AUC = %.2f\n", collector.auc());
      }
      if (showConfusion) {
        Matrix m = collector.confusion();
        output.printf(Locale.ENGLISH, "confusion: [[%.1f, %.1f], [%.1f, %.1f]]\n", m.get(0, 0), m.get(1, 0), m.get(0, 1), m.get(1, 1));
        m = collector.entropy();
        output.printf(Locale.ENGLISH, "entropy: [[%.1f, %.1f], [%.1f, %.1f]]\n", m.get(0, 0), m.get(1, 0), m.get(0, 1), m.get(1, 1));
      }
    }
  }

  public static class Reduce extends MapReduceBase implements Reducer<LongWritable, Text, LongWritable, Text> {
    public void reduce(LongWritable key, Iterator<Text> values, OutputCollector<LongWritable, Text> output, Reporter reporter) throws IOException {
      while (values.hasNext()) {
        output.collect(key, values.next());
      }
    }
  }

  public RunningJob run() throws Exception {
    Configuration conf = null ;
    if(clusterMode) conf = HDFSUtil.getConfiguration() ;
    else conf = HDFSUtil.getDaultConfiguration() ;
    String yarnClasspath = conf.get("yarn.application.classpath") ;
    yarnClasspath = yarnClasspath + ",/opt/saarus/lib/*" ;
    conf.set("yarn.application.classpath", yarnClasspath);
    
    FileSystem fs = FileSystem.get(conf) ;
    fs.delete(new Path(outputUri), true) ;
   
    JobConf jconf = new JobConf(conf, MRLogisticRegressionDecoder.class);
    jconf.setJobName(MRLogisticRegressionDecoder.class.getSimpleName());
   // jconf.setUser("hadoop") ;
    jconf.setStrings("column.headers", this.columnHeaders) ;
    jconf.set("model.file", this.modelUri) ;
    jconf.setMapperClass(Map.class);
    jconf.setMapOutputKeyClass(LongWritable.class) ;
    jconf.setMapOutputValueClass(Text.class) ;
    jconf.setCombinerClass(Reduce.class);
    
    jconf.setReducerClass(Reduce.class);
    jconf.setOutputKeyClass(LongWritable.class);
    jconf.setOutputValueClass(Text.class);

    
    jconf.setInputFormat(TextInputFormat.class);
    jconf.setOutputFormat(TextOutputFormat.class);

    FileInputFormat.setInputPaths(jconf, new Path(inputUri));
    FileOutputFormat.setOutputPath(jconf, new Path(outputUri));
    return JobClient.runJob(jconf) ;
  }
}