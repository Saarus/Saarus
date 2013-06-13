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

  public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, LongWritable, Text> {
    PrintWriter output ;
    LogisticModelParameters lmp ;
    RecordFactoryImpl csv ;
    OnlineLogisticRegression lr ;
    boolean showAuc = true;
    boolean showConfusion  = true;
    boolean showScores  = true;
    Auc collector ;
    
    @Override
    public void configure(JobConf job) {
      try {
        output = new PrintWriter(System.out, true) ;
        String headers = job.get("column.headers") ;
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
        
        csv.configVariableNames(Arrays.asList(headers.split(","))) ;
        if (showScores) {
          output.printf(Locale.ENGLISH, "\"%s\",\"%s\",\"%s\"\n", "target", "model-output", "log-likelihood");
        }
      } catch(Exception ex) {
        ex.printStackTrace() ;
      }
    }
    
    public void map(LongWritable key, Text value, OutputCollector<LongWritable, Text> mapCollector, Reporter reporter) throws IOException {
      String line = value.toString() ;
      List<String> vals = Arrays.asList(line.split(",")) ;
      Vector v = new SequentialAccessSparseVector(lmp.getNumFeatures());
      int target = csv.processData(vals, v);

      double score = lr.classifyScalar(v);
      if (showScores) {
        output.printf(Locale.ENGLISH, "%d,%.3f,%.6f\n", target, score, lr.logLikelihood(target, v));
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

  public static void run(String inDir, String outDir, String jar, boolean clusterMode) throws Exception {
    Configuration conf = null ;
    if(clusterMode) conf = HDFSUtil.getConfiguration() ;
    else conf = HDFSUtil.getDaultConfiguration() ;

    FileSystem fs = FileSystem.get(conf) ;
    fs.delete(new Path(outDir), true) ;

    System.out.println("Properties = " + conf.size()) ;
    
    JobConf jconf = new JobConf(conf, MRLogisticRegressionDecoder.class);
    jconf.setJobName(MRLogisticRegressionDecoder.class.getSimpleName());
    jconf.setUser("hadoop") ;
    jconf.set("column.headers", "x,y,shape,color,xx,xy,yy,c,a,b") ;
    jconf.set("model.file", "dfs:/tmp/donut.model") ;
    if(jar != null) jconf.setJar(jar) ;
    
    
    jconf.setMapperClass(Map.class);
    jconf.setMapOutputKeyClass(LongWritable.class) ;
    jconf.setMapOutputValueClass(Text.class) ;
    jconf.setCombinerClass(Reduce.class);
    
    jconf.setReducerClass(Reduce.class);
    jconf.setOutputKeyClass(LongWritable.class);
    jconf.setOutputValueClass(Text.class);

    
    jconf.setInputFormat(TextInputFormat.class);
    jconf.setOutputFormat(TextOutputFormat.class);

    FileInputFormat.setInputPaths(jconf, new Path(inDir));
    FileOutputFormat.setOutputPath(jconf, new Path(outDir));
    JobClient.runJob(jconf);
  }

  public static void main(String[] args) throws Exception {
    boolean clusterMode = true ;
    if(args == null || args.length == 0) {
      args = new String[] {
        "src/test/resources/donutmr",
        "target/output"
      };
      clusterMode = false ;
    }
    run(args[0], args[1], null, clusterMode);
  }
}