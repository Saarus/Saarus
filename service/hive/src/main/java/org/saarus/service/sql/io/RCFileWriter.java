package org.saarus.service.sql.io;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.io.RCFile;
import org.apache.hadoop.hive.ql.io.RCFileOutputFormat;
import org.apache.hadoop.hive.serde.serdeConstants;
import org.apache.hadoop.hive.serde2.columnar.BytesRefArrayWritable;
import org.apache.hadoop.hive.serde2.columnar.BytesRefWritable;
import org.apache.hadoop.hive.serde2.columnar.ColumnarSerDe;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.DefaultCodec;

public class RCFileWriter {
  private FileSystem fs ;
  private String file ;
  private RCFile.Writer writer ;
  private ColumnarSerDe serDe ;
  
  public RCFileWriter(FileSystem fs, String file, int numberOfColumn, Map<String, String> meta) throws Exception {
    this.fs = fs ;
    this.file = file ;
    Configuration conf = fs.getConf() ;
    Text[] textHeader = null ;
    if(meta == null ) {
      textHeader = new Text[] {} ;
    } else {
      textHeader = new Text[meta.size() * 2] ;
      Iterator<Map.Entry<String, String>> i = meta.entrySet().iterator() ;
      int count = 0 ;
      while(i.hasNext()) {
        Map.Entry<String, String> entry = i.next() ;
        textHeader[count] = new Text(entry.getKey()) ;
        textHeader[count + 1] = new Text(entry.getValue()) ;
        count += 2 ;
      }
    }
    conf.setInt(RCFile.COLUMN_NUMBER_CONF_STR, numberOfColumn) ;
    writer = new RCFile.Writer(fs, conf, new Path(file), null, RCFile.createMetadata(textHeader), new DefaultCodec());
    
    serDe = new ColumnarSerDe();
    serDe.initialize(conf, createProperties());
  }
  
  public void append(String ... data) throws IOException {
    BytesRefArrayWritable bytes = new BytesRefArrayWritable(data.length);
    for(int i = 0; i < data.length; i++) {
      byte[] buf = null ;
      if(data[i] == null) buf = "NULL".getBytes("UTF-8"); 
      else buf = data[i].getBytes("UTF-8") ;
      bytes.set(i, new BytesRefWritable(buf, 0, buf.length));
    }
    writer.append(bytes);
    bytes.clear();
  }
  
  public void close() throws IOException {
    writer.close() ;
  }
  
  static Properties createProperties() {
    Properties tbl = new Properties();
    // Set the configuration parameters
    tbl.setProperty(serdeConstants.SERIALIZATION_FORMAT, "9");
    tbl.setProperty("columns", "abyte,ashort,aint,along,adouble,astring,anullint,anullstring");
    tbl.setProperty("columns.types","tinyint:smallint:int:bigint:double:string:int:string");
    tbl.setProperty(serdeConstants.SERIALIZATION_NULL_FORMAT, "NULL");
    return tbl;
  }
}
