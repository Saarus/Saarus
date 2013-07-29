package org.saarus.nlp.vn.analyzer;

import org.junit.Test;
import org.saarus.nlp.token.WordTokenizerVerifier;
import org.saarus.nlp.token.analyzer.CommonTokenAnalyzer;
import org.saarus.nlp.token.analyzer.TokenAnalyzer;

public class VNWordNumberTokenAnalyzerUnitTest {

  @Test
  public void test() throws Exception{
    TokenAnalyzer[] analyzer = { new CommonTokenAnalyzer(), new VNWordNumberTokenAnalyzer() } ;
      WordTokenizerVerifier verifier = new WordTokenizerVerifier(analyzer) ;
      verifier.verify("chín", "chín{number}");
      verifier.verify("năm mươi", "năm mươi{number}");
      verifier.verify("hai mươi lăm", "hai mươi lăm{number}");
      verifier.verify("bảy mươi tám", "bảy mươi tám{number}");
      verifier.verify("một trăm hai mươi nhăm", "một trăm hai mươi nhăm{number}");
      verifier.verify("bốn nghìn linh ba", "bốn nghìn linh ba{number}");
     
      verifier.verify("hai tư ngày một tuần", "hai tư{number}", "ngày", "một{number}", "tuần");
      verifier.verify("một vạn sáu ngàn dặm", "một vạn sáu ngàn{number}", "dặm");
      verifier.verify("năm trăm chín hai nghìn", "năm trăm chín hai nghìn{number}");
      verifier.verify("tám mươi triệu người dân", "tám mươi triệu{number}", "người", "dân");
      verifier.verify("hai mươi năm", "hai mươi năm{number}");
  }
}
