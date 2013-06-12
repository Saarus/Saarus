package org.saarus.mahout.classifier.sgd;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.mahout.classifier.CsvRecordFactory;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.Vector;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class CsvRecordFactoryUnitTest {
  @Test
  public void test() throws IOException {
    int maxTargetCategories = 2 ;
    String targetVariable = "color" ;
    boolean useBias = true ;
    int numFeatures = 20 ;
    List<String> typeList = Lists.newArrayList("n", "n");
    List<String> predictorList = Lists.newArrayList("x", "y", "xx", "xy", "yy", "a", "b", "c");
    Map<String, String> typeMap = Maps.newHashMap();
    Iterator<String> iTypes = typeList.iterator();
    String lastType = null;
    for (Object x : predictorList) {
      if (iTypes.hasNext()) {
        lastType = iTypes.next();
      }
      typeMap.put(x.toString(), lastType);
    }
    
    CsvRecordFactory csv = new CsvRecordFactory(targetVariable, typeMap) ;
    csv = csv.maxTargetValue(maxTargetCategories) ;
    csv = csv.includeBiasTerm(useBias);
    
    BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("src/test/resources/donut.csv"))) ;
    csv.firstLine(in.readLine()) ;
    for(int i = 0; i < 10; i++) {
      Vector input = new RandomAccessSparseVector(numFeatures);
      csv.processLine(in.readLine(), input);
    }
    in.close() ;
    System.out.println("Target Categories: " + csv.getTargetCategories());
  }
}
