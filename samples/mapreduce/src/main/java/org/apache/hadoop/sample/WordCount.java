package org.apache.hadoop.sample;

import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
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

public class WordCount {

  public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable> {
    private final static IntWritable one = new IntWritable(1);
    private Text word = new Text();

    public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
      String line = value.toString();
      StringTokenizer tokenizer = new StringTokenizer(line);
      while (tokenizer.hasMoreTokens()) {
        word.set(tokenizer.nextToken());
        output.collect(word, one);
      }
    }
  }

  public static class Reduce extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable> {
    public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
      int sum = 0;
      while (values.hasNext()) {
        sum += values.next().get();
      }
      output.collect(key, new IntWritable(sum));
    }
  }

  public static void run(String inDir, String outDir, String jar, boolean clusterMode) throws Exception {
    Configuration conf = new Configuration(true) ;
    if(clusterMode) {
      conf.addResource("cluster/core-site.xml") ;
      conf.addResource("cluster/hdfs-site.xml") ;
      conf.addResource("cluster/mapred-site.xml") ;
      conf.addResource("cluster/yarn-site.xml") ;
    }
    FileSystem fs = FileSystem.get(conf) ;
    fs.delete(new Path(outDir), true) ;

    System.out.println("Properties = " + conf.size()) ;
    System.out.println(conf.get("yarn.application.classpath"));
    
    JobConf jconf = new JobConf(conf, WordCount.class);
    jconf.setJobName("wordcount");
    jconf.setUser("hadoop") ;
    if(jar != null) jconf.setJar(jar) ;
    jconf.setOutputKeyClass(Text.class);
    jconf.setOutputValueClass(IntWritable.class);

    jconf.setMapperClass(Map.class);
    jconf.setCombinerClass(Reduce.class);
    jconf.setReducerClass(Reduce.class);

    jconf.setInputFormat(TextInputFormat.class);
    jconf.setOutputFormat(TextOutputFormat.class);

    FileInputFormat.setInputPaths(jconf, new Path(inDir));
    FileOutputFormat.setOutputPath(jconf, new Path(outDir));
    JobClient.runJob(jconf);
  }

  public static void main(String[] args) throws Exception {
    if(args == null || args.length == 0) {
      args = new String[] {
        "src/main/resources/cluster",
        "target/output"
      };
    }
    run(args[0], args[1], null, false);
  }
}