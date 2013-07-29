package org.saarus.nlp.token.analyzer;

import java.util.ArrayList;
import java.util.List;

import org.saarus.nlp.token.IToken;
import org.saarus.nlp.token.TokenCollection;
import org.saarus.nlp.token.TokenException;
import org.saarus.util.text.CharacterSet;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class LineAnalyzer implements TokenCollectionAnalyzer {
	final static public LineAnalyzer INSTANCE = new LineAnalyzer() ;
	
  public TokenCollection[] analyze(IToken[] tokens) throws TokenException {
  	List<TokenCollection> holder = new ArrayList<TokenCollection>() ;
  	int i  = 0, start = 0 ;
  	while(i < tokens.length) {
  		char[] buf = tokens[i].getNormalizeFormBuf() ;
  		if(buf.length == 1 && CharacterSet.isIn(buf[0], CharacterSet.NEW_LINE)) {
  			if(i > start) {
  				TokenCollection collection = new TokenCollection(tokens, start, i) ;
  				holder.add(collection) ;
  			}
  			i++ ;
  			while(i < tokens.length) {
  				buf = tokens[i].getNormalizeFormBuf() ;
  	  		if(buf.length != 1 && !CharacterSet.isIn(buf[0], CharacterSet.NEW_LINE)) {
  	  			break ;
  	  		}
  	  		i++ ;
  			}
  			start = i ;
  		} else {
  		  i++ ;
  		}
  	}
  	if(i > start) {
  		TokenCollection collection = new TokenCollection(tokens, start, i) ;
			holder.add(collection) ;
  	}
  	return holder.toArray(new TokenCollection[holder.size()]);
  }
}