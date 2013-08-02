package org.saarus.service.nlp;

import java.io.IOException;
import java.util.Iterator;

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
import org.saarus.service.hadoop.util.HDFSUtil;

public class LiblinearTextClassifyDecoder {
  private String    inputUri ;
  private String    outputUri ;
  private String[]  columnHeaders ;
  private String    modelUri ;
  private boolean   clusterMode ;
  
  public LiblinearTextClassifyDecoder setInputUri(String s) {
    this.inputUri = s ;
    return this ;
  }
  
  public LiblinearTextClassifyDecoder setOutputUri(String s) {
    this.outputUri = s ;
    return this ;
  }
  
  public LiblinearTextClassifyDecoder setColumnHeaders(String[] column) {
    this.columnHeaders = column ;
    return this ;
  }
  
  public LiblinearTextClassifyDecoder setModelUri(String s) {
    this.modelUri = s ;
    return this ;
  }
  
  public LiblinearTextClassifyDecoder setClusterMode(boolean b) {
    this.clusterMode = b ;
    return this ;
  }
  
  public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, LongWritable, Text> {
    @Override
    public void configure(JobConf job) {
      try {
      } catch(Exception ex) {
        ex.printStackTrace() ;
      }
    }
    
    public void map(LongWritable key, Text value, OutputCollector<LongWritable, Text> mapCollector, Reporter reporter) throws IOException {
    }

    @Override
    public void close() throws IOException {
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
    else conf = HDFSUtil.getDefaultConfiguration() ;
    String yarnClasspath = conf.get("yarn.application.classpath") ;
    yarnClasspath = yarnClasspath + ",/opt/saarus/lib/*" ;
    conf.set("yarn.application.classpath", yarnClasspath);
    
    FileSystem fs = FileSystem.get(conf) ;
    fs.delete(new Path(outputUri), true) ;
   
    JobConf jconf = new JobConf(conf, LiblinearTextClassifyDecoder.class);
    jconf.setJobName(LiblinearTextClassifyDecoder.class.getSimpleName());
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