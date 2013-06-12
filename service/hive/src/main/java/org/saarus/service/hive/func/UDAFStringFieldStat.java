package org.saarus.service.hive.func;

import org.apache.hadoop.hive.ql.exec.UDAF;
import org.apache.hadoop.hive.ql.exec.UDAFEvaluator;

public final class UDAFStringFieldStat extends UDAF {
  static public class FieldStatistic {
    long recordCount = 0;
    long count = 0;
    long nullCount = 0;
    
    long sumLength ;
    int minLength ;
    int maxLength ;
    
    
    public void incObject(String o) {
      recordCount++ ;
      if (o != null) count++;
      else nullCount++ ;
      if(o == null) return ;
      if(o.length() < minLength) minLength = o.length() ;
      if(o.length() > maxLength) maxLength = o.length() ;
      sumLength += o.length() ;
    }
    
    public boolean merge(FieldStatistic o) {
      if(o == null) return true ;
      count += o.count;
      nullCount += o.nullCount ;
      recordCount +=  o.recordCount ;

      if(o.minLength < minLength) minLength = o.minLength ;
      if(o.maxLength > maxLength) maxLength = o.maxLength ;
      sumLength += o.sumLength ;
      return true;
    }
    
    public String toJSON() {
      StringBuilder b = new StringBuilder() ;
      b.append("{") ;
      b.append("\"recordCount\": ").append(recordCount).append(", ") ;
      b.append("\"count\": ").append(count).append(", ") ;
      b.append("\"nullCount\": ").append(nullCount).append(",") ;
      b.append("\"minLength\": ").append(minLength).append(", ") ;
      b.append("\"maxLength\": ").append(maxLength).append(", ") ;
      b.append("\"sumLength\": ").append(sumLength).append(", ") ;
      b.append("\"avgLength\": ").append(sumLength/count) ;
      b.append("}") ;
      return b.toString() ;
    }
  }
  
  public static class UDAFFieldStatEvaluator implements UDAFEvaluator {
    FieldStatistic state;

    public UDAFFieldStatEvaluator() {
      super();
      state = new FieldStatistic();
      init();
    }

    public void init() {
    }

    /**
     * Iterate through one row of original data. The number and type of arguments need to the same 
     * as we call this UDAF from Hive command line. This function should always return true.
     */
    public boolean iterate(String o) {
      state.incObject(o) ;
      return true;
    }

    /**
     * Terminate a partial aggregation and return the state. If the state is a
     * primitive, just return primitive Java classes like Integer or String.
     */
    public FieldStatistic terminatePartial() {
      // This is SQL standard - average of zero items should be null.
      return state.count == 0 ? null : state;
    }

    /**
     * Merge with a partial aggregation. This function should always have a single argument which 
     * has the same type as the return value of terminatePartial().
     */
    public boolean merge(FieldStatistic o) {
      state.merge(o ) ;
      return true;
    }

    /**
     * Terminates the aggregation and return the final result.
     */
    public String terminate() {
      return state.toJSON() ;
    }
  }

  private UDAFStringFieldStat() { /*prevent instantiation */ }
}