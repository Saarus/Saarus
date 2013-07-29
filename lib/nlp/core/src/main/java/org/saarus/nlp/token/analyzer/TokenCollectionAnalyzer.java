package org.saarus.nlp.token.analyzer;

import org.saarus.nlp.token.IToken;
import org.saarus.nlp.token.TokenCollection;
import org.saarus.nlp.token.TokenException;
/**
 * $Author: Tuan Nguyen$ 
 **/
public interface TokenCollectionAnalyzer {
	public TokenCollection[] analyze(IToken[] tokens) throws TokenException ;
}