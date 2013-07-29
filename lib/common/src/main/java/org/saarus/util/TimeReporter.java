package org.saarus.util;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.saarus.util.text.DateUtil;
import org.saarus.util.text.NumberFormatter;
import org.saarus.util.text.TabularPrinter;

public class TimeReporter {
  Map<String, Time> times = new LinkedHashMap<String, Time>();
  
  public TimeReporter(){}
  
  public Time getTime(String timePoint){
    Time time = times.get(timePoint);
    if(time == null) {
      time = new Time();
      times.put(timePoint, time);
    } 
    return time ;
  }
  
  public void start(String timePoint){
    Time time = times.get(timePoint);
    if(time == null) {
      time = new Time();
      times.put(timePoint, time);
    } else { time.reset(); }
  }
  
  public void stop(String timePoint){
    Time time = times.get(timePoint);
    try {
      if(time == null) throw new Exception("No such the time point!");
      else { time.stop(); }
    } catch (Exception e) { e.printStackTrace(); }
  }
  
  public void create(String timePoint, String fromTimePoint){
    Time ftime = times.get(fromTimePoint);
    try {
      if(ftime == null) throw new Exception("No such the time point!");
      else { 
        Time time = new Time();
        time.setStart(ftime.getStart());
        time.stop();
        times.put(timePoint, time);
      }
    } catch (Exception e) { e.printStackTrace(); }
  }
  
  public void getTime(PrintStream out, String timePoint){
    Time time = times.get(timePoint);
    try {
      if(time == null) throw new Exception("No such the time point!");
      else { out.println(time.getExecTime()); }
    } catch (Exception e) { e.printStackTrace(); }
  }
  
  public void report(String file){
    try {
      PrintStream out = new PrintStream(file);
      report(out);
    } catch (FileNotFoundException e) { e.printStackTrace(); }
  }
  
  public void report(PrintStream out){
    int[] width = {50, 20, 20, 20} ;
    TabularPrinter printer = new TabularPrinter(out, width) ;
  	String[] header = {"", "Start", "Stop", "Exec Time" } ;
  	printer.printHeader(header);
    Iterator<Map.Entry<String, Time>> itr = times.entrySet().iterator();
    while(itr.hasNext()){
      Map.Entry<String, Time> entry = itr.next();
      Time time = entry.getValue() ;
      Object[] cols = {
      	entry.getKey(), 
      	time.getCompactStartTime(), 
      	time.getCompactStopTime(), 
      	time.getExecTime()
      } ;
      printer.printRow(cols, true) ;
    }
  }

  static public class Time {
    private long start;
    private long end;
    
    public Time(){ start = System.currentTimeMillis(); }

    public String getCompactStartTime() {
    	return DateUtil.asCompactDateTime(start) ;
    }
    public long getStart() { return start; }
    public void start(){ 
    	reset() ;
    	start = System.currentTimeMillis();
    }
    public void setStart(long start) { this.start = start; }
    
    public String getCompactStopTime() {
    	return DateUtil.asCompactDateTime(end) ;
    }
    public long getEnd() { return end; }
    public void stop(){ end = System.currentTimeMillis();}
    public void setEnd(long end) { this.end = end; }

    public void reset(){ start = System.currentTimeMillis(); }
    
    public String getExecTime(){
      long duration;
      if(end == 0) duration = System.currentTimeMillis() - getStart();
      else duration = getEnd() - getStart();
      
      String time = NumberFormatter.milliTimeAsHumanReadable(duration);
      return time;
    }
  } 
}