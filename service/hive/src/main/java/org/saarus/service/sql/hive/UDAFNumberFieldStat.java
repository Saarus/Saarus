package org.saarus.service.sql.hive;

import org.apache.hadoop.hive.ql.exec.UDAF;
import org.apache.hadoop.hive.ql.exec.UDAFEvaluator;

public final class UDAFNumberFieldStat extends UDAF {
  static public class FieldStatistic {
    long recordCount = 0;
    long count = 0;
    long nullCount = 0;

    //number field ;
    double min = Double.MIN_VALUE;
    double max = Double.MIN_VALUE ;
    double sum = 0 ;

    public void incObject(Double o) {
      recordCount++ ;
      if (o != null) count++;
      else nullCount++ ;

      if(o == null) return ;

      if(o < min) min = o ;
      if(o > max) max = o ;
      sum += o ;
    }

    public boolean merge(FieldStatistic o) {
      if(o == null) return true ;
      count += o.count;
      nullCount += o.nullCount ;
      recordCount +=  o.recordCount ;

      if(o.min < min) min = o.min ;
      if(o.max > max) max = o.max ;
      sum += o.sum ;
      return true;
    }

    public String toJSON() {
      StringBuilder b = new StringBuilder() ;
      b.append("{") ;
      b.append("\"recordCount\": ").append(recordCount).append(", ") ;
      b.append("\"count\": ").append(count).append(", ") ;
      b.append("\"nullCount\": ").append(nullCount).append(",") ;
      b.append("\"min\": ").append(min).append(", ") ;
      b.append("\"max\": ").append(max).append(", ") ;
      b.append("\"sum\": ").append(sum).append(", ") ;
      b.append("\"avg\": ").append(sum/count) ;
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
    public boolean iterate(Integer o) {
      state.incObject(new Double(o == null ?  0 : o)) ;
      return true;
    }
    
    public boolean iterate(Double o) {
      state.incObject(o) ;
      return true;
    }

    public boolean iterate(Float o) {
      state.incObject(new Double(o == null ?  0 : o)) ;
      return true;
    }
    
    public boolean iterate(Long o) {
      state.incObject(new Double(o == null ?  0 : o)) ;
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

  private UDAFNumberFieldStat() { /*prevent instantiation */ }
}