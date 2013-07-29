package org.saarus.nlp.token.analyzer;

import java.util.ArrayList;
import java.util.List;

import org.saarus.nlp.token.IToken;
import org.saarus.nlp.token.TokenCollection;
import org.saarus.nlp.token.TokenException;
import org.saarus.nlp.token.tag.CharacterTag;
import org.saarus.util.text.CharacterSet;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class SentenceSplitterAnalyzer implements TokenCollectionAnalyzer {
  final static public SentenceSplitterAnalyzer INSTANCE = new SentenceSplitterAnalyzer() ;

  public TokenCollection[] analyze(IToken[] tokens) throws TokenException {
    List<TokenCollection> holder = new ArrayList<TokenCollection>() ;
    int i  = 0, start = 0 ;
    while(i < tokens.length) {
      if(isEndSentence(tokens, i)) {
        TokenCollection collection = new TokenCollection(tokens, start, ++i) ;
        holder.add(collection) ;
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


  static char[] END_SENT_CHAR = {';', '?', '!'} ;
  boolean isEndSentence(IToken[] token, int pos) {
    if(pos + 1 == token.length) return true ; 
    CharacterTag ctag = (CharacterTag)token[pos].getFirstTagType(CharacterTag.TYPE) ;
    if(ctag == null) return false ;
    char[] characters = token[pos].getNormalizeFormBuf() ;
    if(characters.length == 1) {
      if(CharacterSet.isIn(characters[0], END_SENT_CHAR)) return true ;
      if(characters[0] == '.') {
        String nToken = token[pos + 1].getOriginalForm() ;
        char nextFirstChar = nToken.charAt(0) ;
        if(Character.isUpperCase(nextFirstChar) || Character.isDigit(nextFirstChar)) {
          return true ;
        }
      }
    } else if(characters.length == 3) {
      if("...".equals(token[pos].getNormalizeForm())) return true ;
    }
    return false ;
  }
}