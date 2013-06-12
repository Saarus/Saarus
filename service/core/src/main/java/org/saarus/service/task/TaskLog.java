package org.saarus.service.task;

public class TaskLog {
  public enum LogLevel { FATAL, ERROR, WARN, INFO, DEBUG };
  
  private LogLevel logLevel = LogLevel.INFO;
  private String log ;
  
  public TaskLog() { }
  
  public TaskLog(LogLevel level, String log) {
    this.logLevel = level ;
    this.log = log ;
  }
  
  public LogLevel getLogLevel() { return logLevel; }
  public void setLogLevel(LogLevel logLevel) { this.logLevel = logLevel; }
  
  public String getLog() { return log; }
  public void setLog(String log) { this.log = log; }
  
  public String toString() {
    StringBuilder b = new StringBuilder() ;
    if(logLevel == LogLevel.INFO) {
      b.append("[INFO]  ") ;
    } else if(logLevel == LogLevel.FATAL) {
      b.append("[FATAL] ") ;
    } else if(logLevel == LogLevel.ERROR) {
      b.append("[ERROR] ") ;
    } else if(logLevel == LogLevel.WARN) {
      b.append("[WARN]  ") ;
    } else {
      b.append("[DEBUG] ") ;
    }
    b.append(log) ;
    return b.toString() ;
  }
}
