package org.saarus.service.sql;

import java.sql.ResultSet;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.junit.Test;
import org.saarus.service.hadoop.util.FSResource;
import org.saarus.service.hadoop.util.HDFSUtil;
import org.saarus.service.sql.io.JSONImporter;
import org.saarus.service.sql.io.Progressable;
import org.saarus.service.sql.io.Progressable.DebugProgressable;
import org.saarus.service.sql.io.TableRCFileWriter;
import org.saarus.service.sql.io.TableWriter;

public class YelpTableUnitTest {
  @Test
  public void test() throws Exception {
    SQLService hservice  = new SQLService("jdbc:hive2://hadoop1.saarus.org:10000", "hive", "");
    String userTableSQL = 
        "CREATE TABLE user_test (" +
        "  user_id        STRING," +
        "  name           STRING," +
        "  average_stars  FLOAT," +
        "  review_count   INT," +
        "  vote_funny     INT," +
        "  vote_useful    INT," +
        "  vote_cool      INT)" +
        "  STORED AS RCFILE LOCATION '/tmp/yelpdb/user_test'" ;
    
    System.out.println(hservice.dropTable("user_test")) ;
    hservice.executeSQL(userTableSQL);
    
    String userSetFile     = "src/test/resources/yelpdb/user.json" ;
    Configuration conf = HDFSUtil.getConfiguration() ;
    FileSystem fs = FileSystem.get(conf) ;
    String[] properties = {
      "user_id", "name", "average_stars", "review_count", "votes.funny", "votes.useful", "votes.cool"
    } ;
    
    Progressable progressable = new DebugProgressable() ;
    TableWriter writer = new TableRCFileWriter(fs, "/tmp/yelpdb/user_test/data0.rcfile", properties, null) ;
    JSONImporter importer = new JSONImporter(writer, progressable) ;
    FSResource resource = FSResource.get(userSetFile) ;
    importer.doImport(resource.getInputStream(), properties) ;
    importer.close() ;
    
    ResultSet rset = hservice.executeQuerySQL("SELECT * FROM user_test LIMIT 100") ;
    rset.setFetchSize(1000) ;
    while(rset.next()) {
      System.out.println(String.format("%s %s %s %s", rset.getObject(1), rset.getObject(5), rset.getObject(6), rset.getObject(7)));
    }
    rset.close() ;
  }
}