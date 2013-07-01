package org.saarus.service.hadoop.util;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class HDFSUtil {
  private static FileSystem fileSystem ;
  private static FileSystem localFileSystem ;
  
  static public Configuration getDaultConfiguration() {
    Configuration conf = new Configuration(false) ;
    conf.addResource("core-default.xml") ;
    conf.addResource("hdfs-default.xml") ;
    conf.addResource("mapred-default.xml") ;
    conf.addResource("yarn-default.xml") ;
    return conf ;
  }
  
  static public Configuration getConfiguration() {
    Configuration conf = new Configuration(false) ;
    //System.out.println(conf.getResource("core-default.xml"));
    conf.addResource("core-default.xml") ;
    //System.out.println(conf.getResource("hdfs-default.xml"));
    conf.addResource("hdfs-default.xml") ;
    //System.out.println(conf.getResource("mapred-default.xml"));
    conf.addResource("mapred-default.xml") ;
    //System.out.println(conf.getResource("yarn-default.xml"));
    conf.addResource("yarn-default.xml") ;
    
    conf.addResource("hadoopconf/dev/core-site.xml") ;
    conf.addResource("hadoopconf/dev/hdfs-site.xml") ;
    conf.addResource("hadoopconf/dev/mapred-site.xml") ;
    conf.addResource("hadoopconf/dev/yarn-site.xml") ;
    return conf ;
  }
  
  static public FileSystem getLocalFileSystem() throws IOException {
    if(localFileSystem == null) {
      localFileSystem = FileSystem.get(getDaultConfiguration()) ;
    }
    return localFileSystem ;
  }
  
  static public FileSystem getFileSystem() throws IOException {
    if(fileSystem == null) {
      fileSystem = FileSystem.get(getConfiguration()) ;
    }
    return fileSystem ;
  }
  
  static public boolean removeIfExists(FileSystem fs, String path) throws IOException {
    Path p = new Path(path) ;
    if(fs.exists(p)) return fs.delete(p, true) ;
    return true ;
  }
  
  static public boolean removeIfExists(FileSystem fs, Path path) throws IOException {
    if(fs.exists(path)) return fs.delete(path, true) ;
    return true ;
  }
  
  static public boolean mkdirs(FileSystem fs, String path) throws IOException {
    Path p = new Path(path) ;
    if(!fs.exists(p)) return fs.mkdirs(p) ;
    return true ;
  }
  
  static public void mv(FileSystem fs, String src, String dest) throws IOException {
    Path destPath = new Path(dest) ;
    if(!fs.exists(destPath.getParent())) fs.mkdirs(destPath.getParent()) ;
    if(!fs.rename(new Path(src), destPath)) {
      throw new IOException("Cannot mv " + src  + " to " + dest) ;
    }
  }
  
  static public void mv(FileSystem fs, Path src, Path dest) throws IOException {
    if(!fs.exists(dest.getParent())) fs.mkdirs(dest.getParent()) ;
    if(!fs.rename(src, dest)) {
      throw new IOException("Cannot mv " + src  + " to " + dest) ;
    }
  }
  
  static public void copyFromLocalFile(FileSystem fs, String src, String dest, boolean overwrite) throws IOException {
    Path srcPath = new Path(src) ;
    Path destPath = new Path(dest) ;
    fs.copyFromLocalFile(false, overwrite, srcPath, destPath) ;
  }
  
  static public void findDescendantFiles(FileSystem fs, List<Path> holder, Path path, String ext) throws IOException {
    FileStatus[] children = fs.listStatus(path) ;
    if(children == null) return ;
    for(int i = 0; i < children.length; i++) {
      if(!children[i].isDirectory()) {
        Path childPath = children[i].getPath() ;
        if(ext == null) {
          holder.add(childPath) ;
        } else if(childPath.toString().endsWith(ext)){
          holder.add(childPath) ;
        }
      } else {
        findDescendantFiles(fs, holder, children[i].getPath(), ext) ;
      }
    }
  }
}