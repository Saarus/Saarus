package org.saarus.nlp.dict.ws;

import java.util.ArrayList;
import java.util.List;

import org.saarus.nlp.dict.Dictionary;
import org.saarus.nlp.dict.Entry;
import org.saarus.nlp.dict.WordTree;
import org.saarus.nlp.token.IToken;
import org.saarus.nlp.token.Token;
import org.saarus.nlp.token.analyzer.TokenAnalyzer;
import org.saarus.nlp.token.tag.PunctuationTag;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class WordTreeMatchingAnalyzer implements TokenAnalyzer {
  private WordTree wtreeRoot ;

  public WordTreeMatchingAnalyzer(Dictionary dict) {
    wtreeRoot = dict.getWordTree() ;
  }

  public WordTreeMatchingAnalyzer(WordTree wtree) {
    wtreeRoot = wtree ;
  }

  public IToken[] analyze(IToken[] tokens)  {
    List<IToken> newList = new ArrayList<IToken>() ;
    int position = 0 ;
    while(position < tokens.length) {
      IToken token = tokens[position] ;
      if(token.hasTagType(PunctuationTag.TYPE)){
        newList.add(token) ;
        position++ ;
        continue ;
      } 
      WordTree foundTree = wtreeRoot.matches(tokens, position) ;
      if(foundTree != null) {
        Entry entry = foundTree.getEntry() ;
        int newPosition = position + entry.getWord().length ;
        Token newToken = new Token(tokens, position, newPosition) ;
        newToken.add(entry.getTag()) ;
        newList.add(newToken) ;
        position = newPosition ;
      } else {
        newList.add(token) ;
        position++ ;
      }
    }
    return newList.toArray(new Token[newList.size()]) ;
  }
}