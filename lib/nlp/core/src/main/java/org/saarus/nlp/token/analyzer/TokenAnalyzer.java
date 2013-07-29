package org.saarus.nlp.token.analyzer;

import org.saarus.nlp.token.IToken;
import org.saarus.nlp.token.TokenException;
/**
 * $Author: Tuan Nguyen$ 
 **/
public interface TokenAnalyzer {
	static public TokenAnalyzer NONE = new TokenAnalyzer() {
    public IToken[] analyze(IToken[] unit) throws TokenException {
	    return unit;
    }
	};
	
	public IToken[] analyze(IToken[] unit) throws TokenException ;
}