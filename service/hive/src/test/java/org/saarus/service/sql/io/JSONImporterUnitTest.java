package org.saarus.service.sql.io;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.saarus.service.hadoop.util.FSResource;
import org.saarus.service.hadoop.util.HDFSUtil;

public class JSONImporterUnitTest {
  
  static public void main(String[] args) throws Exception {
    String userSetFile     = "../../../yelp/yelpdb/json/training/user/data.json" ;
    Configuration conf = HDFSUtil.getDefaultConfiguration() ;
    FileSystem fs = FileSystem.get(conf) ;
    String[] properties = {
      "user_id", "review_count", "average_stars", "votes.funny", "votes.useful", "votes.cool"
    } ;
    JSONImporter.Progressable progressable = new JSONImporter.DebugProgressable() ;
    JSONImporter.Writer writer = new JSONImporter.RCWriter(fs, "target/import-json.rcfile", properties, null) ;
    JSONImporter importer = new JSONImporter(writer, progressable) ;
    FSResource resource = FSResource.get(userSetFile) ;
    importer.doImport(resource.getInputStream(), properties) ;
    importer.close() ;
  }
}