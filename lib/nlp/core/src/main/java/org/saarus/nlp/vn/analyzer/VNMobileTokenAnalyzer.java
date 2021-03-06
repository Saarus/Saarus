package org.saarus.nlp.vn.analyzer;

import java.util.ArrayList;
import java.util.List;

import org.saarus.nlp.token.IToken;
import org.saarus.nlp.token.Token;
import org.saarus.nlp.token.TokenException;
import org.saarus.nlp.token.analyzer.PhoneNumberUtil;
import org.saarus.nlp.token.analyzer.TokenAnalyzer;
import org.saarus.nlp.token.tag.DigitTag;
import org.saarus.nlp.token.tag.PhoneTag;
import org.saarus.nlp.token.tag.WordTag;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class VNMobileTokenAnalyzer implements TokenAnalyzer {
  final static String[] IGNORE_TYPE = { WordTag.WLETTER.getOType() } ;

  public IToken[] analyze(IToken[] token) throws TokenException {
    List<IToken> holder = new ArrayList<IToken>() ;
    for(int i = 0; i < token.length; i++) {
      if(token[i].hasTagType(IGNORE_TYPE)) {
        holder.add(token[i]) ;
        continue ;
      }
      String norm = token[i].getNormalizeForm() ;
      if(norm.length() == 1 && norm.charAt(0) == '(') {
        if(i + 3 < token.length && 
            token[i + 1].hasTagType(DigitTag.TYPE) && 
            token[i + 2].getNormalizeForm().charAt(0) == ')') {
          String test = token[i + 1].getNormalizeForm() + token[i + 3].getNormalizeForm() ;
          test = PhoneNumberUtil.normalize(test) ;
          String provider = PhoneNumberUtil.getMobileProvider(test) ;
          if(provider != null) {
            IToken newToken = new Token(token, i, i + 4) ;
            newToken.add(new PhoneTag(test, provider, "mobile")) ;
            holder.add(newToken) ;
            i += 3 ;
            continue ;
          }
        }
      } 
      if(norm.length() >= 10 && norm.length() < 15) {
        String string = PhoneNumberUtil.normalize(norm) ;
        String provider = PhoneNumberUtil.getMobileProvider(string) ;
        if(provider != null) {
          token[i].add(new PhoneTag(string, provider, "mobile")) ;
          holder.add(token[i]) ;
          continue ;
        }
      }
      holder.add(token[i]) ;
    }
    return holder.toArray(new IToken[holder.size()]);
  }
}