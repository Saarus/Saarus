package org.saarus.nlp.token.analyzer;

import java.util.ArrayList;
import java.util.List;

import org.saarus.nlp.token.IToken;
import org.saarus.nlp.token.Token;
import org.saarus.nlp.token.TokenException;
import org.saarus.nlp.token.tag.DigitTag;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class GroupTokenMergerAnalyzer implements TokenAnalyzer {
  public IToken[] analyze(IToken[] tokens) throws TokenException {
    int i = 0 ;
    List<IToken> holder = new ArrayList<IToken>() ;
    while(i < tokens.length) {
      if(tokens[i].hasTagType(DigitTag.TYPE)) {
        int limit = i  ;
        while(limit < tokens.length && limit < i + 8 && tokens[limit].hasTagType(DigitTag.TYPE)) {
          limit++ ;
        }
        if(limit > i + 1) {
          IToken newToken = new Token(tokens, i, limit) ;
          holder.add(newToken);
          i = limit ;
          continue ;
        }
      } 
      holder.add(tokens[i]) ;
      i++ ;
    }
    return holder.toArray(new IToken[holder.size()]) ;
  }
}