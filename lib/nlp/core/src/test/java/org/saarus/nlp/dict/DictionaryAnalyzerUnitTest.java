package org.saarus.nlp.dict;

import java.io.PrintStream;

import org.junit.Test;
import org.saarus.nlp.dict.ws.WordTreeMatchingAnalyzer;
import org.saarus.nlp.token.IToken;
import org.saarus.nlp.token.TabularTokenPrinter;
import org.saarus.nlp.token.TextSegmenter;
import org.saarus.nlp.token.WordTokenizerVerifier;
import org.saarus.nlp.token.analyzer.CommonTokenAnalyzer;
import org.saarus.nlp.token.analyzer.GroupTokenMergerAnalyzer;
import org.saarus.nlp.token.analyzer.PunctuationTokenAnalyzer;
import org.saarus.nlp.token.analyzer.TokenAnalyzer;
import org.saarus.nlp.token.analyzer.USDTokenAnalyzer;
import org.saarus.nlp.vn.analyzer.VNDTokenAnalyzer;
import org.saarus.util.ConsoleUtil;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class DictionaryAnalyzerUnitTest {
  @Test
  public void test() throws Exception {
    String[] res = {"classpath:nlp/en.lexicon.json"} ;
    Dictionary dict = new Dictionary(res);
    
    dict.add(meaning("Saarus", "person")) ;
    dict.add(meaning("New York", "place")) ;

    TokenAnalyzer[] analyzer = {
        new PunctuationTokenAnalyzer(), new CommonTokenAnalyzer(),
        new GroupTokenMergerAnalyzer(),
        new VNDTokenAnalyzer(), new USDTokenAnalyzer(), 
        new WordTreeMatchingAnalyzer(dict),
        new UnknownWordTokenSplitter(dict)
    };
    
    WordTokenizerVerifier verifier = new WordTokenizerVerifier(analyzer) ;
    verifier.verify(
        "Today is a beautiful day. Saarus, you should come to New York", 
        "Today", "is", "a", "beautiful", "day", ".", "Saarus{postag, person}", ",", "you", "should", "come", "to", "New York{postag, place}") ;
    
    TextSegmenter textSegmenter = new TextSegmenter(analyzer);
    test(textSegmenter, "Today is a beautiful day. Saarus, you shoudl come to New York");
  }

  private void test(TextSegmenter textSegmenter, String text) throws Exception {
    IToken[] token = textSegmenter.segment(text) ;
    PrintStream out = ConsoleUtil.getUTF8SuportOutput() ;
    TabularTokenPrinter printer = new TabularTokenPrinter();
    printer.print(out, token) ;
  }
  
  private Meaning meaning(String word, String otype) {
    Meaning meaning = new Meaning() ;
    meaning.setName(word) ;
    meaning.setLang("en") ;
    meaning.setOType(otype) ;
    return meaning ;
  }
}
