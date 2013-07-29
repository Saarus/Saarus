package org.saarus.nlp.token.analyzer;

import org.saarus.nlp.token.IToken;
import org.saarus.nlp.token.TokenException;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class ChainTokenAnalyzer implements TokenAnalyzer {
	private TokenAnalyzer[] analyzer ;
	
  public ChainTokenAnalyzer(TokenAnalyzer ... analyzer) {
  	this.analyzer = analyzer ;
  }
  
	public IToken[] analyze(IToken[] tokens) throws TokenException {
		for(TokenAnalyzer sel : analyzer) {
  		tokens = sel.analyze(tokens) ;
  	}
		return tokens ;
  }
	
}