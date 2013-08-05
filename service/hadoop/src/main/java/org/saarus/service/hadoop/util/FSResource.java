package org.saarus.service.hadoop.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.saarus.service.util.IOUtil;

abstract public class FSResource {
  private String uri ;
  private String scheme ;
  private String path ;

  private FSResource(String uri, String scheme, String path) {
    this.uri = uri ;
    this.scheme = scheme ;
    this.path = path ;
  }

  public String getUri() { return this.uri ; }

  public String getScheme() { return this.scheme ; }

  public String getPath() { return this.path ; }

  public String getContentAsString(String encoding) throws Exception {
    InputStream is = getInputStream() ;
    return IOUtil.getStreamContentAsString(is, encoding) ;
  }
  
  abstract public InputStream getInputStream() throws Exception ;

  abstract public int write(InputStream is) throws Exception ;
  
  abstract public void write(byte[] buf) throws Exception ;

  abstract public boolean exists() throws Exception ;
  
  abstract public boolean delete() throws Exception ;

  abstract public boolean mkdirs() throws Exception ;
  
  protected int write(OutputStream os, InputStream is) throws Exception {
    BufferedInputStream buffer = new BufferedInputStream(is);    
    byte[] data  = new byte[4096];      
    int available = -1, read = 0 ;
    while((available = buffer.read(data)) > -1){
      os.write(data, 0, available);
      read += available ;
    } 
    return read ;
  }

  static public FSResource get(String uri) {
    if(uri.startsWith("dfs:")) {
      return new DFSResource(uri, uri.substring("dfs:".length())) ;
    } else if(uri.startsWith("hdfs:")) {
      return new DFSResource(uri, uri) ;
    } else if(uri.startsWith("file:")) {
      return new LocalResource(uri, uri.substring("file:".length())) ;
    } else {
      return new LocalResource("file:" + uri, uri) ;
    }
  }

  static class LocalResource extends FSResource {
    private LocalResource(String uri, String path) {
      super(uri, "file", path) ;
    }

    public InputStream getInputStream() throws Exception {
      return new FileInputStream(getPath());
    }

    public int write(InputStream is) throws Exception {
      OutputStream out = new FileOutputStream(getPath()) ;
      int count = write(out, is) ;
      out.close() ;
      is.close() ;
      return count ;
    }
    
    public void write(byte[] buf) throws Exception {
      OutputStream out = new FileOutputStream(getPath()) ;
      out.write(buf) ;
      out.close() ;
    }
    
    public boolean exists() throws Exception {
      File file = new File(getPath()) ;
      return file.exists() ;
    }

    public boolean delete() throws Exception {
      File file = new File(getPath()) ;
      return file.delete() ;
    }

    public boolean mkdirs() throws Exception {
      File file = new File(getPath()) ;
      if(!file.exists()) {
        return file.mkdirs() ;
      }
      if(file.isDirectory()) return true ;
      return false ;
    }
  }

  static class DFSResource extends FSResource {
    private DFSResource(String uri, String path) {
      super(uri, "dfs", path) ;
    }

    public InputStream getInputStream() throws Exception {
      FileSystem fs = HDFSUtil.getFileSystem() ;
      return fs.open(new Path(getPath()));
    }

    public int write(InputStream is) throws Exception {
      Path src = new Path(getPath()) ;
      FileSystem fs = HDFSUtil.getFileSystem() ;
      FSDataOutputStream out = fs.create(src, true) ;
      int count  = write(out, is) ;
      out.hflush() ;
      out.close() ;
      is.close() ;
      return count ;
    }

    public void write(byte[] buf) throws Exception {
      Path src = new Path(getPath()) ;
      FileSystem fs = HDFSUtil.getFileSystem() ;
      FSDataOutputStream out = fs.create(src, true) ;
      out.write(buf) ;
      out.close() ;
    }
    
    public boolean exists() throws Exception {
      FileSystem fs = HDFSUtil.getFileSystem() ;
      return fs.exists(new Path(getPath())) ;
    }
    
    public boolean delete() throws Exception {
      FileSystem fs = HDFSUtil.getFileSystem() ;
      return HDFSUtil.removeIfExists(fs, getPath()) ;
    }
    

    public boolean mkdirs() throws Exception {
      FileSystem fs = HDFSUtil.getFileSystem() ;
      return HDFSUtil.mkdirs(fs, getPath()) ;
    }
  }
}
