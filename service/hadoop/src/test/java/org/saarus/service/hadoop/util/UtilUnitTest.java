package org.saarus.service.hadoop.util;

import java.io.ByteArrayInputStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.junit.Assert;
import org.junit.Test;

public class UtilUnitTest {
  //@Test
  public void testHDFSOperation() throws Exception {
    Configuration conf = HDFSUtil.getConfiguration() ;
    FileSystem fs = FileSystem.get(conf) ;
    HDFSUtil.removeIfExists(fs, "/tmp/HDFSUtil") ;
    System.out.println(HDFSUtil.mkdirs(fs, "/tmp/HDFSUtil")) ;
    HDFSUtil.copyFromLocalFile(fs, "target/classes", "/tmp/HDFSUtil", true) ;
    
    HDFSUtil.removeIfExists(fs, "/tmp/HDFSUtil") ;
  }
  
  @Test
  public void testFSResource() throws Exception {
    testFSResource(FSResource.get("target/fsresource.local")) ;
    testFSResource(FSResource.get("dfs:/tmp/fsresource.dfs")) ;
  }
  
  private void testFSResource(FSResource res) throws Exception {
    String DATA = "test data" ;
    if(res.exists()) {
      res.delete() ;
    }
    Assert.assertTrue(!res.exists()) ;
    res.write(new ByteArrayInputStream(DATA.getBytes())) ;
    String content = res.getContentAsString("UTF-8") ;
    Assert.assertEquals(DATA, content) ;
    res.delete() ;
    Assert.assertTrue(!res.exists()) ;
  }
}
