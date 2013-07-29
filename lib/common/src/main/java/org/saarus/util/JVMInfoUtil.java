package org.saarus.util;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.List;

import org.saarus.util.text.NumberFormatter;
import org.saarus.util.text.StringUtil;

public class JVMInfoUtil {
	final static public String getMemoryUsageInfo() {
		MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
    MemoryUsage memory = mbean.getHeapMemoryUsage() ;
    StringBuilder b = new StringBuilder() ;
    b.append("Memory Usage:").
      append("  init = ").append(NumberFormatter.byteFormatAsHumanReadable(memory.getInit())).
      append(", max = ").append(NumberFormatter.byteFormatAsHumanReadable(memory.getMax())).
      append(", use = ").append(NumberFormatter.byteFormatAsHumanReadable(memory.getUsed())).
      append(", commited = " + NumberFormatter.byteFormatAsHumanReadable(memory.getCommitted())) ;
    return b.toString() ;
	}
	
	final static public String getThreadInfo(boolean staktrace) {
		StringBuilder b = new StringBuilder() ;
		ThreadMXBean mbean = ManagementFactory.getThreadMXBean() ;
		b.append("Thread Info:").
		  append(" thread started count: ").append(mbean.getTotalStartedThreadCount()).
		  append(", thread peak count: ").append(mbean.getPeakThreadCount()).
		  append(", thread count: ").append(mbean.getThreadCount()).
		  append(", thread deamon count: ").append(mbean.getDaemonThreadCount()).append("\n") ;
		long[] tid = mbean.getAllThreadIds() ;
		for(int i = 0; i < tid.length; i++) {
			if(i > 0) {
				b.append("\n===============================================================\n") ;
			}
			ThreadInfo tinfo = mbean.getThreadInfo(tid[i], 10) ;
			if(tinfo == null) continue ;
			b.append("  ").
			  append("Thread name: ").append(tinfo.getThreadName()).
			  append(", block count = ").append(tinfo.getBlockedCount()).
			  append(", block time = ").append(NumberFormatter.milliTimeAsHumanReadable(tinfo.getBlockedTime())).
			  append(", waited count = ").append(tinfo.getWaitedCount()).
			  append(", waited time  = ").append(NumberFormatter.milliTimeAsHumanReadable(tinfo.getWaitedTime())).
			  append(", thread state = ").append(tinfo.getThreadState()).
			  append(", cpu time = ").append(NumberFormatter.nanoTimeAsHumanReadable(mbean.getThreadCpuTime(tid[i]))).
			  append(", user time = ").append(NumberFormatter.nanoTimeAsHumanReadable(mbean.getThreadUserTime(tid[i]))).
			  append("\n") ;
				
			b.append("    Stack Trace:  \n") ;
			appendStackTrace(b, tinfo.getStackTrace()) ;
		}
		return b.toString() ;
	}
	
	final static public String getGarbageCollectorInfo() {
		StringBuilder b = new StringBuilder() ;
		b.append("Garbage Collector Info:\n") ;
		List<GarbageCollectorMXBean> gcbeans = ManagementFactory.getGarbageCollectorMXBeans() ; 
		for(int i = 0; i < gcbeans.size(); i++) {
			GarbageCollectorMXBean gcbean = gcbeans.get(i) ;
			b.append("  name = ").append(gcbean.getName()).
			  append(", collection count = ").append(gcbean.getCollectionCount()).
			  append(", collection time = ").append(NumberFormatter.milliTimeAsHumanReadable(gcbean.getCollectionTime())).
			  append(", pool names = ").append(StringUtil.joinStringArray(gcbean.getMemoryPoolNames(), "|")).append("\n") ;
		}
		return b.toString() ;
	}
	
	static void appendStackTrace(StringBuilder b, StackTraceElement[] elements) {
    for(StackTraceElement element : elements) {
      b.append("      ").append(element.toString()).append("\n") ;
    }
  }
}