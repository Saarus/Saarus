package org.saarus.service.sql.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.hadoop.fs.FileSystem;
import org.codehaus.jackson.JsonNode;
import org.saarus.service.sql.TableMetadata;
import org.saarus.service.util.JSONReader;
import org.saarus.service.util.JSONSerializer;

public class JSONImporter {
  private Writer writer ;
  private Progressable progressable ;
  
  public JSONImporter(Writer writer, Progressable progressable) throws Exception {
    this.writer = writer ;
    this.progressable = progressable ;
  }
  
  public void doImport(InputStream is, String[] properties) throws Exception {
    doImport(new JSONReader(is), properties) ;
  }
  
  
  public void doImport(String jsonFile, String[] properties) throws Exception {
    doImport(new JSONReader(jsonFile), properties) ;
  }
  
  public void doImport(JSONReader reader, String[] properties) throws Exception {
    JsonNode node = null ;
    JSONPropertiesReader preader = new JSONPropertiesReader() ;
    while(((node = reader.read()) != null)) {
      String[] value = preader.read(node, properties) ;
      if(progressable != null) progressable.progress(properties, value) ;
      writer.write(value) ;
    }
  }
  
  public void close() throws Exception {
    writer.close() ;
  }
  
  static public interface Writer {
    public void write(String[] val) throws Exception ;
    public void close() throws Exception ;
  }
  
  static public class RCWriter implements Writer {
    private RCFileWriter writer ;

    public RCWriter(FileSystem fs, String file, String[] properties, Map<String, String> meta) throws Exception {
      this.writer = new RCFileWriter(fs, file, properties.length, meta) ;
    }

    public void write(String[] val) throws Exception { writer.append(val) ; }

    public void close() throws Exception { writer.close() ; }
  }

  static public interface Progressable {
    public void progress(String[] properties, String[] val) ;
  }
  
  static public class DebugProgressable implements Progressable {
    public void progress(String[] properties, String[] val) {
      StringBuilder b = new StringBuilder() ;
      for(int i = 0; i < val.length; i++) {
        if(i > 0 ) b.append(", ") ;
        b.append(properties[i]).append("=").append(val[i]);
      }
      System.out.println(b);
    }
  }
}