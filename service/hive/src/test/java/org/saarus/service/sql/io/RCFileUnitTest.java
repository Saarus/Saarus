package org.saarus.service.sql.io;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.serde2.columnar.ColumnarSerDe;
import org.apache.hadoop.hive.serde2.io.ByteWritable;
import org.apache.hadoop.hive.serde2.io.DoubleWritable;
import org.apache.hadoop.hive.serde2.io.ShortWritable;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils.ObjectInspectorCopyOption;
import org.apache.hadoop.hive.serde2.objectinspector.StructField;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.junit.Assert;
import org.junit.Test;
import org.saarus.service.hadoop.util.HDFSUtil;
import org.saarus.service.sql.io.RCFileReader;
import org.saarus.service.sql.io.RCFileWriter;

public class RCFileUnitTest {
  @Test
  public void testSimpleReadAndWrite() throws Exception {
    Configuration conf = HDFSUtil.getDefaultConfiguration() ;
    FileSystem fs = FileSystem.get(conf) ;
    String file = "target/mapred/test_rcfile";
    fs.delete(new Path(file), true);
    

    String[] rec1 = {"123", "456", "789", "1000", "5.3", "hive and hadoop", "", "NULL"};
    String[] rec2 = {"100", "200", "123", "1000", "5.3", "hive and hadoop", "", null};
    
    Map<String, String> metaData = new HashMap<String, String>() ;
    metaData.put("apple", "block") ;
    metaData.put("cat",   "dog") ;
    RCFileWriter writer = new RCFileWriter(fs, file, rec1.length, metaData) ;
    writer.append(rec1) ;
    writer.append(rec2) ;
    writer.close() ;

    Object[] expectedRecord_1 = { new ByteWritable((byte) 123),
        new ShortWritable((short) 456), new IntWritable(789),
        new LongWritable(1000), new DoubleWritable(5.3),
        new Text("hive and hadoop"), null, null};

    Object[] expectedRecord_2 = {new ByteWritable((byte) 100),
        new ShortWritable((short) 200), new IntWritable(123),
        new LongWritable(1000), new DoubleWritable(5.3),
        new Text("hive and hadoop"), null, null};

    RCFileReader reader = new RCFileReader(fs, file, expectedRecord_1.length) ;
    ColumnarSerDe serDe = reader.getColumnarSerDe() ;
    Assert.assertEquals("block", reader.getMetadata("apple"));
    Assert.assertEquals("dog", reader.getMetadata("cat"));
   
    for (int i = 0; i < 2; i++) {
      Object row = reader.nextRow();
      StructObjectInspector oi = (StructObjectInspector) serDe.getObjectInspector();
      List<? extends StructField> fieldRefs = oi.getAllStructFieldRefs();
      Assert.assertEquals("Field size should be 8", 8, fieldRefs.size());
      for (int j = 0; j < fieldRefs.size(); j++) {
        Object fieldData = oi.getStructFieldData(row, fieldRefs.get(j));
        Object standardWritableData = 
          ObjectInspectorUtils.copyToStandardObject(fieldData, fieldRefs.get(j).getFieldObjectInspector(), ObjectInspectorCopyOption.WRITABLE);
        if (i == 0) {
          Assert.assertEquals("Field " + i, standardWritableData, expectedRecord_1[j]);
        } else {
          Assert.assertEquals("Field " + i, standardWritableData, expectedRecord_2[j]);
        }
      }
    }
    reader.close();
  }
}
