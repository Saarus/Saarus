package org.saarus.service.hadoop.dfs;

public class DFSFile {
  private String        name ;
  private String        path ;
  private boolean       directory ;
  private long          accessTime ;
  private long          modifiedTime ;
  
  public DFSFile() {} 
  
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  
  public String getPath() { return path; }
  public void setPath(String path) { this.path = path; }
  
  public boolean isDirectory() { return directory; }
  public void setDirectory(boolean directory) { this.directory = directory; }
  
  public long getAccessTime() { return accessTime; }
  public void setAccessTime(long time) { this.accessTime = time; }
  
  public long getModifiedTime() { return modifiedTime; }
  public void setModifiedTime(long time) { this.modifiedTime = time; }
  
  public String toString() { return name ; }
}
