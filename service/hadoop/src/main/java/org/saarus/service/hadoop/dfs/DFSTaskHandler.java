package org.saarus.service.hadoop.dfs;

import java.io.FileNotFoundException;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.saarus.service.hadoop.util.HDFSUtil;
import org.saarus.service.task.CallableTaskUnit;
import org.saarus.service.task.TaskLog.LogLevel;
import org.saarus.service.task.TaskUnit;
import org.saarus.service.task.TaskUnitHandler;
import org.saarus.service.task.TaskUnitResult;

public class DFSTaskHandler implements TaskUnitHandler {

  public String getName() { return "DFSTaskHandler" ; }

  public CallableTaskUnit<?> getCallableTaskUnit(TaskUnit taskUnit) {
    String name = taskUnit.getName();
    if("getFile".equals(name))   return getFile(taskUnit) ;
    else if("listFiles".equals(name))   return listFiles(taskUnit) ;
    return null ;
  }

  private CallableTaskUnit<DFSFile> getFile(final TaskUnit tunit) {
    CallableTaskUnit<DFSFile> callableUnit = new CallableTaskUnit<DFSFile>(tunit, new TaskUnitResult<DFSFile>()) {
      public DFSFile doCall() throws Exception {
        try {
          FileSystem fs = HDFSUtil.getFileSystem() ;
          Path path = new Path(tunit.getParameters().getString("path")) ;
          DFSFile dfsFile = toDFSFile(fs.getFileStatus(path)) ;
          return dfsFile ;
        } catch(FileNotFoundException ex) {
          getTaskUnitResult().add(LogLevel.WARN, ex.getMessage()) ;
          return null ;
        }
      }
    };
    return callableUnit ;
  }

  private CallableTaskUnit<DFSFile[]> listFiles(final TaskUnit tunit) {
    CallableTaskUnit<DFSFile[]> callableUnit = new CallableTaskUnit<DFSFile[]>(tunit, new TaskUnitResult<DFSFile[]>()) {
      public DFSFile[] doCall() throws Exception {
        try {
          FileSystem fs = HDFSUtil.getFileSystem() ;
          Path path = new Path(tunit.getParameters().getString("path")) ;
          FileStatus[] children = fs.listStatus(path) ;
          DFSFile[] dfsFile = new DFSFile[children.length] ;
          for(int i = 0; i < children.length; i++) {
            dfsFile[i] = toDFSFile(children[i]) ;
          }
          return dfsFile ;
        } catch(FileNotFoundException ex) {
          getTaskUnitResult().add(LogLevel.WARN, ex.getMessage()) ;
          return null ;
        }
      }
    };
    return callableUnit ;
  }


  private DFSFile toDFSFile(FileStatus fStatus) {
    Path path = fStatus.getPath() ;
    DFSFile dfsFile = new DFSFile() ;
    dfsFile.setName(path.getName()) ;
    dfsFile.setPath(path.toString()) ;
    dfsFile.setAccessTime(fStatus.getAccessTime()) ;
    dfsFile.setModifiedTime(fStatus.getModificationTime()) ;
    dfsFile.setDirectory(fStatus.isDirectory()) ;
    return dfsFile ;
  }

  public String toString() { return getName() ; }
}