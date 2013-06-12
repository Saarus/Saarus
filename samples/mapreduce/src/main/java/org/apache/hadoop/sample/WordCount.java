package org.apache.hadoop.sample;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
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

	public static void run(String confLoc, String inDir, String outDir, String jar) throws Exception {
		Configuration conf = new Configuration(false) ;
		conf.addResource("core-default.xml") ;
    conf.addResource("hdfs-default.xml") ;
    conf.addResource("mapred-default.xml") ;
    conf.addResource("yarn-default.xml") ;
    
    if(jar == null) {
    	conf.addResource(new URL(confLoc + "/hadoop/conf/core-site.xml")) ;
    	conf.addResource(new URL(confLoc +  "/hadoop/conf/hdfs-site.xml")) ;
    	conf.addResource(new URL(confLoc +  "/hadoop/conf/mapred-site.xml")) ;
    	conf.addResource(new URL(confLoc +  "/hadoop/conf/yarn-site.xml")) ;
    }
    
		System.out.println("Properties = " + conf.size()) ;
		JobConf jconf = new JobConf(conf, WordCount.class);
		jconf.setJobName("wordcount");
		jconf.setUser("openstack") ;
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
		run("file:../", args[0], args[1], null);
	}
}