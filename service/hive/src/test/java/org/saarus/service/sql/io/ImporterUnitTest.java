package org.saarus.service.sql.io;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.junit.Test;
import org.saarus.service.hadoop.util.FSResource;
import org.saarus.service.hadoop.util.HDFSUtil;
import org.saarus.service.sql.io.Progressable.DebugProgressable;

public class ImporterUnitTest {
  @Test
  public void testJsonImport() throws Exception {
    String userSetFile     = "src/test/resources/yelpdb/user.json" ;
    Configuration conf = HDFSUtil.getDefaultConfiguration() ;
    FileSystem fs = FileSystem.get(conf) ;
    String[] properties = {
      "user_id", "review_count", "average_stars", "votes.funny", "votes.useful", "votes.cool"
    } ;
    Progressable progressable = new DebugProgressable() ;
    TableWriter writer = new TableRCFileWriter(fs, "target/import-json.rcfile", properties, null) ;
    JSONImporter importer = new JSONImporter(writer, progressable) ;
    FSResource resource = FSResource.get(userSetFile) ;
    importer.doImport(resource.getInputStream(), properties) ;
    importer.close() ;
  }
  
  @Test
  public void testCSVImport() throws Exception {
    String userSetFile     = "src/test/resources/yelpdb/user.csv" ;
    Configuration conf = HDFSUtil.getDefaultConfiguration() ;
    FileSystem fs = FileSystem.get(conf) ;
    String[] properties = {
      "user_id", "review_count", "average_stars", "vote_funny", "vote_useful", "vote_cool"
    } ;
    Progressable progressable = new DebugProgressable() ;
    TableWriter writer = new TableRCFileWriter(fs, "target/import-csv.rcfile", properties, null) ;
    CSVImporter importer = new CSVImporter(writer, progressable) ;
    FSResource resource = FSResource.get(userSetFile) ;
    importer.doImport(resource.getInputStream(), properties) ;
    importer.close() ;
  }
}