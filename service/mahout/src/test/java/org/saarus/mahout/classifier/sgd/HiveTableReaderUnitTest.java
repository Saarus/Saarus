package org.saarus.mahout.classifier.sgd;

import java.util.List;

import org.junit.Test;
import org.saarus.service.hive.HiveService;

public class HiveTableReaderUnitTest {
  @Test
  public void test() throws Exception {
    HiveService hservice  = new HiveService("jdbc:hive2://198.154.60.252:10000", "hive", "");
    DataReader reader = 
        new HiveTableDataReader(hservice, "features", new String[] {"stars", "business_review_count", "vote_useful"}) ;
    reader.reset() ;
    System.out.println(reader.getHeaderNames());
    List<String> data = null ;
    int count = 0 ;
    System.out.println(reader.getHeaderNames());
    while(count < 100 && (data = reader.nextRow()) != null) {
      System.out.println(data) ;
      count++ ;
    }
  }
}
