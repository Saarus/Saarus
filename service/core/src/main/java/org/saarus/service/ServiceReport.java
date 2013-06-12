package org.saarus.service;

import java.util.ArrayList;
import java.util.List;

public class ServiceReport {
  private float progress ;
  private List<ProgressLog> logs = new ArrayList<ProgressLog>() ;
  
	public float getProgress() { return progress ; }
	public void setProgress(float f) { this.progress = f ; }
	
	public List<ProgressLog> getProgressLog() { return this.logs ; }
	public void setProgressLog(List<ProgressLog> logs) { this.logs = logs ; }
	
	public void addLog(int level, String message) {
	  logs.add(new ProgressLog(level, message)) ;
	}
	
	static public class ProgressLog {
	  private int level ;
	  private String message ;
	  
	  public ProgressLog() {
	  }

	  public ProgressLog(int level, String s) {
	    this.level = level ;
	    this.message = s ;
	  }
	  
    //log level should be INFO, WARN, ERROR
    public int getLogLevel() { return level ; }
    public void setLevel(int level) { this.level = level ; }
    
    public String getMessage() { return message ; }
    public void   setMessage(String s) { this.message = s ; }
  }

}
