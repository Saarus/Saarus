package org.saarus.nlp.classify.liblinear;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.saarus.nlp.classify.liblinear.DataReader.Record;

public class TextClassifierUnitTest {
  @Test
  public void testClassifier() throws Exception {
    DataReaderHolder dataHolder = new DataReaderHolder() ;
    dataHolder.add(new Record("POSITIVE", "I like my iphone")) ;
    dataHolder.add(new Record("POSITIVE", "the iphone is very light and slim")) ;
    dataHolder.add(new Record("NEGATIVE", "iphone battery is drained very fast")) ;
    dataHolder.add(new Record("NEGATIVE", "The iphone has a high resolution screen")) ;
    
    TextTrainer trainer = new TextTrainer() ;
    trainer.train(dataHolder, "target/iphone") ;
    
    System.out.println("\nCLASSIFY\n") ;
    
    InputStream modelStream = new FileInputStream("target/iphone/text-classify.model") ;
    InputStream dictStream = new FileInputStream("target/iphone/text-classify.dict") ;
    TextClassifier classifier = new TextClassifier(modelStream, dictStream) ;
    
    verify(classifier, "iphone battery issue", "NEGATIVE") ;
    verify(classifier, "iphone look slim", "POSITIVE") ;
    verify(classifier, "I hate the new iphone(should be NEGATIVE but...)", "POSITIVE") ;
  }
  
  private void verify(TextClassifier classifier, String text, String expectLabel) throws Exception {
    double predict  = classifier.classify(text) ;
    String label = classifier.getFeatureSet().getLabels().get((int) predict);
    Assert.assertEquals(expectLabel, label) ;
    System.out.println(text + ": predict = " + predict + ", label = " + label);
  }
  
  static public class DataReaderHolder implements DataReader {
    private int currentPos = 0 ;
    private List<Record> holder = new ArrayList<Record>() ;
    
    public void add(Record record) { holder.add(record) ; }
    
    public Record next() throws Exception {
      if(currentPos < holder.size()) return holder.get(currentPos++) ;
      return null;
    }

    public void close() throws Exception {
      currentPos = 0 ;
    }
  }
}
