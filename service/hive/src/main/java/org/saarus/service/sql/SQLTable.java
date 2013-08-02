package org.saarus.service.sql;

import java.util.HashMap;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.saarus.service.hadoop.util.HDFSUtil;
import org.saarus.service.sql.TableMetadata.FieldInfo;
import org.saarus.service.sql.io.TableRCFileWriter;

public class SQLTable {
  private SQLService    service ;
  private TableMetadata tableMetadata ;
  private String location ;
  
  public SQLTable(SQLService service, TableMetadata tableMetadata) {
    this.service = service ;
    this.tableMetadata = tableMetadata ;
  }
  
  public String getLocation() { return this.location ; }
  
  public boolean dropTable() throws Exception {
    return service.dropTable(tableMetadata.getTableName()) ;
  }
  
  public void createTable(String location) throws Exception {
    this.location = location ;
    service.createTable(tableMetadata, location) ;
  }
  
  public TableRCFileWriter createTableWriter(Configuration hadoopConf, String fileName) throws Exception {
    if(location == null) {
      throw new Exception("The table location is unknown") ;
    }
    if(hadoopConf == null) {
      hadoopConf = HDFSUtil.getConfiguration() ;
    }
    FileSystem fs = FileSystem.get(hadoopConf) ;
    List<FieldInfo> fields = tableMetadata.getFields() ;
    String[] colNames = new String[fields.size()] ;
    for(int i = 0; i < colNames.length; i++) {
      colNames[i] = fields.get(i).getName() ;
    }
    TableRCFileWriter writer = new TableRCFileWriter(fs, location + "/" + fileName, colNames, new HashMap<String, String>()) ;
    return writer ;
  }
}
