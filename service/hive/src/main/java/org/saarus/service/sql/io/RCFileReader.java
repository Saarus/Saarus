package org.saarus.service.sql.io;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.io.RCFile;
import org.apache.hadoop.hive.serde2.columnar.BytesRefArrayWritable;
import org.apache.hadoop.hive.serde2.columnar.ColumnarSerDe;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

public class RCFileReader {
  private FileSystem fs ;
  private String file ;
  private int numberOfColumn;
  private RCFile.Reader reader ;
  private ColumnarSerDe serDe ;
  private LongWritable rowID = new LongWritable();
  
  public RCFileReader(FileSystem fs, String file, int numberOfColumn) throws Exception {
    this.fs = fs ;
    this.file = file ;
    this.numberOfColumn = numberOfColumn ;
    Configuration conf = fs.getConf() ;
    reader = new RCFile.Reader(fs, new Path(file), conf);
    serDe = new ColumnarSerDe();
    serDe.initialize(conf, TableRCFileWriter.createProperties());
  }
  
  public ColumnarSerDe getColumnarSerDe() { return this.serDe ; }
  
  public String getMetadata(String name) {
    Text data = reader.getMetadata().get(new Text(name)) ;
    if(data == null) return null ;
    return data.toString() ;
  }
  
  public Object nextRow() throws Exception {
    boolean next = reader.next(rowID);
    if(!next) return null ;
    BytesRefArrayWritable cols = new BytesRefArrayWritable();
    reader.getCurrentRow(cols);
    cols.resetValid(numberOfColumn);
    Object row = serDe.deserialize(cols);
    return row ;
  }
  
  public void close() throws IOException {
    reader.close() ;
  }
}
