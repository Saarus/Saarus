package org.saarus.nlp.dict;

import java.util.ArrayList;
import java.util.List;

import org.saarus.nlp.dict.ws.WordTreeMatchingAnalyzer;
import org.saarus.nlp.token.IToken;
import org.saarus.nlp.token.Token;
import org.saarus.nlp.token.TokenCollection;
import org.saarus.nlp.token.TokenException;
import org.saarus.nlp.token.analyzer.ChainTokenAnalyzer;
import org.saarus.nlp.token.analyzer.CommonTokenAnalyzer;
import org.saarus.nlp.token.analyzer.TokenAnalyzer;
import org.saarus.nlp.token.tag.MeaningTag;
import org.saarus.util.text.StringUtil;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class UnknownWordTokenSplitter implements TokenAnalyzer {
  private TokenAnalyzer splitAnalyzer ;
  private Dictionary dict ;

  public UnknownWordTokenSplitter(Dictionary dict) {
    this.dict = dict ;
    splitAnalyzer = new ChainTokenAnalyzer(new CommonTokenAnalyzer(), new WordTreeMatchingAnalyzer(dict)) ;
  }

  public IToken[] analyze(IToken[] tokens) throws TokenException {
    List<IToken> holder = new ArrayList<IToken>() ;
    for(int i = 0; i < tokens.length; i++) {
      MeaningTag ltag = tokens[i].getFirstTagType(MeaningTag.class) ;
      if(ltag != null || tokens[i].getWord().length == 1) {
        holder.add(tokens[i]) ;
      } else {
        if(tokens[i] instanceof TokenCollection) {
          holder.add(tokens[i]) ;
        } else {
          split(holder, tokens[i]) ;
        }
      }
    }
    return holder.toArray(new IToken[holder.size()]) ;
  }

  void split(List<IToken> holder, IToken token) throws TokenException {
    List<String> oword = StringUtil.split(token.getOriginalForm(), ' ') ;
    int icap = 0 ;
    for(int i = 0; i < oword.size(); i++) {
      String word = oword.get(i) ;
      if(word.length() > 0 && Character.isUpperCase(word.charAt(0))) {
        icap++  ;
      }
    }

    if(icap == oword.size()) {
      holder.add(token) ;
      return ;
    }

    IToken[] splitTokens = new IToken[oword.size()] ;
    for(int i = 0; i < splitTokens.length; i++) {
      splitTokens[i] = new Token(oword.get(i)) ;
    }
    splitTokens = splitAnalyzer.analyze(splitTokens) ;
    for(IToken sel : splitTokens) holder.add(sel) ;
  }
}