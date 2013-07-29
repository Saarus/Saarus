package sample.nlp.sentiment;

import org.junit.Test;

public class SentimentClassifierTest {
  
  @Test
  public void test() throws Exception {
    SentimentClassifier classifier = 
      new SentimentClassifier("src/app/models/fullSentiment.model", "src/app/models/fullSentiment.wordList");
    String tweet = "I like this iphone";

    double output = classifier.classify(tweet);
    System.out.println("output = " + output);
    if (output != -1) {
      String label = classifier.getFeatureSet().getLabels().get((int) output);
      System.out.println(label);	
    }
  }
}