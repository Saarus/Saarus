package org.saarus.nlp.en;

import java.io.PrintStream;

import junit.framework.Assert;

import org.junit.Test;
import org.saarus.nlp.token.IToken;
import org.saarus.nlp.token.TabularTokenPrinter;
import org.saarus.nlp.token.TextSegmenter;
import org.saarus.nlp.token.analyzer.CommonTokenAnalyzer;
import org.saarus.nlp.token.analyzer.PunctuationTokenAnalyzer;
import org.saarus.nlp.token.analyzer.TokenAnalyzer;
import org.saarus.util.ConsoleUtil;

public class EnglishMorphologyProcessorUnitTest {
  @Test
  public void testEnglishMorphologyAnalyzer() throws Exception {
    EnglishMorphologyProcessor morphologyProcessor = new EnglishMorphologyProcessor() ;
    Assert.assertEquals("run", morphologyProcessor.process("running")) ;
    Assert.assertEquals("city", morphologyProcessor.process("cities")) ;
    //"morning" should not be stemed to "morn"
    //Assert.assertEquals("morning", morphologyProcessor.process("morning")) ;
    Assert.assertEquals("be", morphologyProcessor.process("are")) ;
    Assert.assertEquals("be", morphologyProcessor.process("is")) ;
    
    TokenAnalyzer[] analyzer = { new PunctuationTokenAnalyzer(), new CommonTokenAnalyzer() };
    TextSegmenter textSegmenter = new TextSegmenter(analyzer);
    String text = "Most large cities in the US had morning and afternoon newspapers.";
    IToken[] token = textSegmenter.segment(text) ;
    for(IToken sel : token) {
      sel.setNormalizeForm(morphologyProcessor.process(sel.getNormalizeForm())) ;
    }
    PrintStream out = ConsoleUtil.getUTF8SuportOutput() ;
    TabularTokenPrinter printer = new TabularTokenPrinter();
    printer.print(out, token) ;
  }
}
