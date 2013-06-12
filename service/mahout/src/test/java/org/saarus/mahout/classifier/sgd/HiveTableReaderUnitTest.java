package org.saarus.mahout.classifier.sgd;

import java.util.List;

import org.junit.Test;
import org.saarus.service.hive.HiveService;

public class HiveTableReaderUnitTest {
  @Test
  public void test() throws Exception {
    HiveService hservice  = new HiveService("jdbc:hive2://198.154.60.252:10000", "hive", "");
    DataReader reader = 
        new HiveTableDataReader(hservice, "donut_train", new String[] {"x", "y", "a", "b", "c", "color"}) ;
    reader.reset() ;
    System.out.println(reader.getHeaderNames());
    List<String> data = null ;
    while((data = reader.nextRow()) != null) {
      System.out.println(data) ;
    }
  }
}
