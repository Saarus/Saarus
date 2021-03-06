package org.saarus.service.sql;

import java.util.List;

import org.junit.Test;
import org.saarus.service.sql.DataReader;
import org.saarus.service.sql.HiveTableDataReader;
import org.saarus.service.sql.SQLService;

public class HiveTableReaderUnitTest {
  @Test
  public void test() throws Exception {
    SQLService hservice  = new SQLService("jdbc:hive2://hadoop1.saarus.org:10000", "hive", "");
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
