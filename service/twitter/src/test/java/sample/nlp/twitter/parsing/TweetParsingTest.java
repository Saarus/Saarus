package sample.nlp.twitter.parsing;

import java.util.List;

import sample.nlp.twitter.parsing.Tagger.TaggedToken;


public class TweetParsingTest {	
  public static void main(String[] args) throws Exception {		
    String text = " Air Yeezy Khaki/Pink Colorway";
    TweetParsing parsing = new TweetParsing();
    
    for(int i = 0; i < 100; i++) {
      parsing.parsing(text);
      parsing.tokenize(text);
    }
    int LOOP = 10000;
    
    long start = System.currentTimeMillis() ;
    for(int i = 0; i < LOOP; i++) {
      //List<TweetToken> tokens = parsing.parsing(text);
      parsing.tokenize(text);
    }
    System.out.println("Tokenize " + LOOP + ", avg = " + ((System.currentTimeMillis() - start)/(double)LOOP) + "ms/sentence");
    
    start = System.currentTimeMillis() ;
    for(int i = 0; i < LOOP; i++) {
      //List<TweetToken> tokens = parsing.parsing(text);
      List<TaggedToken> taggedTokens = parsing.tokenizeAndTag(text);
    }
    System.out.println("Parse " + LOOP + ", avg = " + ((System.currentTimeMillis() - start)/(double)LOOP) + "ms/sentence");
    List<TweetToken> tokens = parsing.parsing(text);
    for (TweetToken token : tokens)
      System.out.println(token);
  }
}