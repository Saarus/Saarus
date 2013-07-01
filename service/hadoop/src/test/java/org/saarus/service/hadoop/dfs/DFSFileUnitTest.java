package org.saarus.service.hadoop.dfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.junit.Assert;
import org.junit.Test;
import org.saarus.service.hadoop.util.FSResource;
import org.saarus.service.hadoop.util.HDFSUtil;
import org.saarus.service.task.TaskUnit;
import org.saarus.service.task.TaskUnitResult;

public class DFSFileUnitTest {
  @Test
  public void testHDFSOperation() throws Exception {
    Configuration conf = HDFSUtil.getConfiguration() ;
    FileSystem fs = FileSystem.get(conf) ;
    HDFSUtil.removeIfExists(fs, "/tmp/DFSFile") ;
    FSResource dir = FSResource.get("dfs:/tmp/DFSFile") ;
    dir.mkdirs() ;
    FSResource file1 = FSResource.get("dfs:/tmp/DFSFile/file1") ;
    file1.write("file1".getBytes()) ;
    FSResource file2 = FSResource.get("dfs:/tmp/DFSFile/file2") ;
    file2.write("file2".getBytes()) ;
    
    DFSTaskHandler handler  = new DFSTaskHandler();
    TaskUnit getTask = new TaskUnit("getFile", null) ;
    getTask.getParameters().setString("path", "/tmp/DFSFile") ;
    TaskUnitResult<DFSFile> getResult = (TaskUnitResult<DFSFile>) handler.getCallableTaskUnit(getTask).call() ;
    DFSFile dfsDir = getResult.getResult() ;
    Assert.assertNotNull(dfsDir) ;
    Assert.assertEquals("DFSFile", dfsDir.getName()) ;
    
    System.out.println("Path = " + dfsDir.getPath());

    TaskUnit listTask = new TaskUnit("listFiles", null) ;
    listTask.getParameters().setString("path", "/tmp/DFSFile") ;
    TaskUnitResult<DFSFile[]> listResult = (TaskUnitResult<DFSFile[]>) handler.getCallableTaskUnit(listTask).call() ;
    System.out.println(listResult);
    DFSFile[] dfsFiles = listResult.getResult() ;
    Assert.assertNotNull(dfsFiles) ;
    Assert.assertEquals(2, dfsFiles.length) ;
  }
}
