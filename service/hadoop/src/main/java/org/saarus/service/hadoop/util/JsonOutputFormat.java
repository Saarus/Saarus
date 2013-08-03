/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.saarus.service.hadoop.util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FSDataOutputStream;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RecordWriter;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.util.*;
import org.codehaus.jettison.json.JSONObject;

/** An {@link JsonOutputFormat} that writes Json Objects as output. 
  */

public class JsonOutputFormat<K, V> extends FileOutputFormat<K, V> {

  protected static class JsonRecordWriter<K, V>
    implements RecordWriter<K, V> {
	  
	boolean firstRecord = true;
    private static final String utf8 = "UTF-8";
    private static final byte[] newline;
    static {
      try {
        newline = "\n".getBytes(utf8);
      } catch (UnsupportedEncodingException uee) {
        throw new IllegalArgumentException("can't find " + utf8 + " encoding");
      }
    }

    protected DataOutputStream out;
    private final byte[] keyValueSeparator;
    private String[] headers = null;
    
    public JsonRecordWriter(DataOutputStream out, String keyValueSeparator, String[] headers) {
      this.out = out;
      this.headers = headers;
      try {
        this.keyValueSeparator = keyValueSeparator.getBytes(utf8);
      } catch (UnsupportedEncodingException uee) {
        throw new IllegalArgumentException("can't find " + utf8 + " encoding");
      }
    }

    public JsonRecordWriter(DataOutputStream out) {
      this(out, "\t");
    }

    public JsonRecordWriter(DataOutputStream out, String keyValueSeparator) {
    	this.out = out;
        try {
          this.keyValueSeparator = keyValueSeparator.getBytes(utf8);
        } catch (UnsupportedEncodingException uee) {
          throw new IllegalArgumentException("can't find " + utf8 + " encoding");
        }
	}

	/**
     * Write the object to the byte stream, handling Text as a special
     * case.
     * @param o the object to print
     * @throws IOException if the write throws, we pass it on
     */
    private void writeObject(Object o) throws IOException {
      if (o instanceof Text) {
        Text to = (Text) o;
        out.write(to.getBytes(), 0, to.getLength());
      } else {
        out.write(o.toString().getBytes(utf8));
      }
    }

    /*public synchronized void write(K key, V value)
      throws IOException {

      boolean nullKey = key == null || key instanceof NullWritable;
      boolean nullValue = value == null || value instanceof NullWritable;
      if (nullKey && nullValue) {
        return;
      }
      if (!nullKey) {
        writeObject(key);
      }
      if (!(nullKey || nullValue)) {
        out.write(keyValueSeparator);
      }
      if (!nullValue) {
        writeObject(value);
      }
      out.write(newline);
    }
    */
    
    
    public synchronized void write(K key, V value)
    	      throws IOException {

    	      boolean nullKey = key == null || key instanceof NullWritable;
    	      boolean nullValue = value == null || value instanceof NullWritable;
    	      if (nullKey && nullValue) {
    	        return;
    	      }
    	      
    	      LinkedHashMap hMap = new LinkedHashMap();
    	        	      
    	      if (!nullKey) {
    	        //writeObject(key);
    	    	  hMap.put("key", key.toString()); 
    	      }else
    	      {
    	    	  hMap.put("key", "null");
    	      }
//    	      if (!(nullKey || nullValue)) {
//    	        out.write(keyValueSeparator);
//    	      }
    	      if (!nullValue) {
    	        //writeObject(value);
    	    	  
    	    	  if (value instanceof Text) {
    	    	        Text to = (Text) value;
    	    	        String[] vals = to.toString().split(",");
    	    	        for(int i = 0; i < headers.length; i++)
    	    	    	  {
    	    	    		  hMap.put(headers[i].toString(), vals[i]);
    	    	    	  }
    	    	        hMap.put("score", vals[(vals.length)-2]);
    	    	        hMap.put("target", vals[(vals.length)-1]);
    	    	        JSONObject jsn = new JSONObject(hMap);
    	    	        Text to1 = new Text(jsn.toString());
    	    	        out.write(to1.getBytes(), 0, to1.getLength());
    	    	      } else {
    	    	        out.write(value.toString().getBytes(utf8));
    	    	      }
    	    	  
    	    	   
    	    	  
    	      }
    	      out.write(newline);
    	    }

    @Override
    public synchronized void close(Reporter reporter) throws IOException {     
      out.close();
    }

	
  }

  @Override
  public RecordWriter<K, V> getRecordWriter(FileSystem ignored,
                                                  JobConf job,
                                                  String name,
                                                  Progressable progress)
    throws IOException {
    boolean isCompressed = getCompressOutput(job);
    String keyValueSeparator = job.get("mapred.textoutputformat.separator","\t");
    String[] headers = job.getStrings("column.headers") ;    
    if (!isCompressed) {
      Path file = FileOutputFormat.getTaskOutputPath(job, name);
      FileSystem fs = file.getFileSystem(job);
      FSDataOutputStream fileOut = fs.create(file, progress);
      return new JsonRecordWriter<K, V>(fileOut, keyValueSeparator,headers);
    } else {
      Class<? extends CompressionCodec> codecClass =
        getOutputCompressorClass(job, GzipCodec.class);
      // create the named codec
      CompressionCodec codec = ReflectionUtils.newInstance(codecClass, job);
      // build the filename including the extension
      Path file = 
        FileOutputFormat.getTaskOutputPath(job, 
                                           name + codec.getDefaultExtension());
      FileSystem fs = file.getFileSystem(job);
      FSDataOutputStream fileOut = fs.create(file, progress);
      return new JsonRecordWriter<K, V>(new DataOutputStream
                                        (codec.createOutputStream(fileOut)),
                                        keyValueSeparator,headers);
    }
  }
  
//  private static class JsonRecordWriter extends 
//  LineRecordWriter<Text,IntWritable>{
//public JsonRecordWriter(DataOutputStream out) {
//		super(out);
//		// TODO Auto-generated constructor stub
//	}
//boolean firstRecord = true;
//@Override
//public synchronized void close(TaskAttemptContext context)
//        throws IOException {
//    out.writeChar('{');
//    super.close(null);
//}


}

