package org.saarus.nlp.pos;

import java.io.PrintStream;

import org.junit.Test;
import org.saarus.nlp.token.IToken;
import org.saarus.nlp.token.TabularTokenPrinter;
import org.saarus.nlp.token.TextSegmenter;
import org.saarus.nlp.token.analyzer.CommonTokenAnalyzer;
import org.saarus.nlp.token.analyzer.GroupTokenMergerAnalyzer;
import org.saarus.nlp.token.analyzer.PunctuationTokenAnalyzer;
import org.saarus.nlp.token.analyzer.TokenAnalyzer;
import org.saarus.util.ConsoleUtil;

public class OpenNLPPOSTaggerUnitTest {
  @Test
  public void testWords() throws Exception {
    OpenNLPPOSTagger tagger = new OpenNLPPOSTagger() ;
    String[] token = new String[]{
        "Most", "large", "cities", "in", "the", "US", "had", "morning", "and", "afternoon", "newspapers", "."
    };     
    String[] tag = tagger.tag(token);
    for(int i = 0; i < token.length; i++) {
      System.out.println(token[i] + ": " + tag[i]);
    }
  }
  
  @Test
  public void testIToken() throws Exception {
    TokenAnalyzer[] analyzer = {
      new PunctuationTokenAnalyzer(), new CommonTokenAnalyzer(),
      new GroupTokenMergerAnalyzer(),
    };
    OpenNLPPOSTagger tagger = new OpenNLPPOSTagger() ;
    TextSegmenter textSegmenter = new TextSegmenter(analyzer);
    String text = "Most large cities in the US had morning and afternoon newspapers.";
    IToken[] token = textSegmenter.segment(text) ;
    token = tagger.tag(token) ;
    PrintStream out = ConsoleUtil.getUTF8SuportOutput() ;
    TabularTokenPrinter printer = new TabularTokenPrinter();
    printer.print(out, token) ;
  }
}
