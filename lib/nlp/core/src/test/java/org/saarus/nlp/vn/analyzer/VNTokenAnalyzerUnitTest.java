package org.saarus.nlp.vn.analyzer;

import org.junit.Test;
import org.saarus.nlp.token.WordTokenizerVerifier;
import org.saarus.nlp.token.analyzer.CommonTokenAnalyzer;
import org.saarus.nlp.token.analyzer.GroupTokenMergerAnalyzer;
import org.saarus.nlp.token.analyzer.TimeTokenAnalyzer;
import org.saarus.nlp.token.analyzer.TokenAnalyzer;
import org.saarus.nlp.token.analyzer.USDTokenAnalyzer;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class VNTokenAnalyzerUnitTest {
  @Test
  public void tesṭ̣̣̣̣VNDTokenAnalyzer() throws Exception {
    TokenAnalyzer[] analyzer = {
        new CommonTokenAnalyzer(), new GroupTokenMergerAnalyzer(), new VNDTokenAnalyzer()
    } ;
    WordTokenizerVerifier verifier = new WordTokenizerVerifier(analyzer) ;
    verifier.verify(
        "1000 đồng 1000 (vnd) 1000(vnd)", 
        "1000{currency}", "đồng", "1000{currency}", "(", "vnd", ")", "1000{currency}", "(", "vnd", ")") ;
    verifier.verify(
        "100 ngàn 100 ngan", 
        "100{currency}", "ngàn", "100{currency}", "ngan") ;
    verifier.verify(
        "1.2 triệu 1.2triệu", 
        "1.2{currency}", "triệu", "1.2triệu{currency}") ;
    // TODO: Connector word between currency value.
    //		verifier.verify(
    //		  "3 hoặc 4 triệu",
    //		  "3{currency}", "hoặc", "4{currency}", "triệu");
  }

  @Test
  public void tesṭ̣̣̣̣VNMobileTokenAnalyzer() throws Exception {
    TokenAnalyzer[] analyzer = {
        new CommonTokenAnalyzer(), new GroupTokenMergerAnalyzer(), new VNMobileTokenAnalyzer()
    } ;
    WordTokenizerVerifier verifier = new WordTokenizerVerifier(analyzer) ;
    verifier.verify(
        "0988922860 098.892.2860 0988.922860 098-892-2860 098 892 2860 (098) 892 2860", 
        "0988922860{phone}", "098.892.2860{phone}", "0988.922860{phone}", "098-892-2860{phone}", "098 892 2860{phone}", "( 098 ) 892 2860{phone}") ;
  }

  @Test
  public void tesṭ̣̣̣̣VNPhoneTokenAnalyzer() throws Exception {
    TokenAnalyzer[] analyzer = {
        new CommonTokenAnalyzer(), new GroupTokenMergerAnalyzer(), new VNPhoneTokenAnalyzer()
    } ;
    WordTokenizerVerifier verifier = new WordTokenizerVerifier(analyzer) ;
    verifier.verify(
        "36634567 3663.4567", 
        "36634567{phone}", "3663.4567{phone}") ;
    verifier.verify(
        "(04)36634567 (04) 36634567 (04) 3663.4567 (04) 3663-4567", 
        "( 04 ) 36634567{phone}", "( 04 ) 36634567{phone}", "( 04 ) 3663.4567{phone}", "( 04 ) 3663-4567{phone}") ;
    verifier.verify(
        "04-3663-4567 04 3663-4567", 
        "04-3663-4567{phone}", "04 3663-4567{phone}") ;
  }

  @Test
  public void tesṭ̣̣̣̣VNNameTokenAnalyzer() throws Exception {
    TokenAnalyzer[] analyzer = {
        new CommonTokenAnalyzer(), new GroupTokenMergerAnalyzer(), new VNNameTokenAnalyzer()
    } ;
    WordTokenizerVerifier verifier = new WordTokenizerVerifier(analyzer) ;
    verifier.verify(
        "Nguyễn Tấn Dũng space Lê Lai", 
        "Nguyễn Tấn Dũng{vnname}", "space", "Lê Lai") ;
  }
}