package org.saarus.nlp.token.analyzer;

import org.saarus.nlp.token.IToken;
import org.saarus.nlp.token.TokenException;
import org.saarus.nlp.token.tag.CurrencyTag;
import org.saarus.nlp.token.tag.DigitTag;
import org.saarus.nlp.token.tag.NumberTag;
import org.saarus.util.text.CharacterSet;
import org.saarus.util.text.NumberUtil;
import org.saarus.util.text.StringUtil;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class USDTokenAnalyzer implements TokenAnalyzer {
  final static public String[] USD_TOKENS =   { 
    "$", "usd",  "$/m2"
  } ;
  final static public String[] DO_TOKENS =  { "đô", "đô la", "do la", "dollard" } ;

  final static public String[][] ALL_TOKENS = {USD_TOKENS, DO_TOKENS} ;

  public IToken[] analyze(IToken[] token) throws TokenException {
    for(int i = 0; i < token.length; i++) {
      if(detectByNumberToken(token, i)) continue ;
      else if(detectByUnknownToken(token, i)) continue ;
    }
    return token;
  }

  private boolean detectByNumberToken(IToken[] token, int pos) {
    double amount = getNumberValue(token[pos]) ;
    if(amount > 0) {
      if(pos + 1 >= token.length) return false ;
      String nextToken = token[pos + 1].getNormalizeForm() ;
      if(nextToken.length() == 1 && CharacterSet.isIn(nextToken.charAt(0), CharacterSet.BRACKET)) {
        if(pos + 2 >= token.length) return false ;
        nextToken = token[pos + 2].getNormalizeForm() ;
      }
      if(setCurrencyTag(token[pos], amount, nextToken)) return true ;

      if(pos - 1 < 0) return false ;
      nextToken = token[pos - 1].getNormalizeForm() ;
      if(setCurrencyTag(token[pos], amount, nextToken)) return true ;
    }
    return false; 
  }

  private boolean detectByUnknownToken(IToken[] token, int pos) {
    String norm = token[pos].getNormalizeForm() ;
    for(int i = 0; i < ALL_TOKENS.length; i++) {
      for(int j = 0; j < ALL_TOKENS[i].length; j++) {
        if(norm.endsWith(ALL_TOKENS[i][j])) {
          String number = norm.substring(0, norm.length() - ALL_TOKENS[i][j].length()) ;
          Double value = NumberUtil.parseRealNumber(number.toCharArray()) ;
          if(value == null) return false ;
          setCurrencyTag(token[pos], value, ALL_TOKENS[i][j]) ;
          return true ;
        } else if(norm.startsWith(ALL_TOKENS[i][j])) {
          String number = norm.substring(ALL_TOKENS[i][j].length()) ;
          Double value = NumberUtil.parseRealNumber(number.toCharArray()) ;
          if(value == null) return false ;
          setCurrencyTag(token[pos], value, ALL_TOKENS[i][j]) ;
          return true ;
        }
      }
    }
    return false; 
  }

  private double getNumberValue(IToken token) {
    DigitTag digitTag = (DigitTag) token.getFirstTagType(DigitTag.TYPE) ;
    if(digitTag != null){
      Long value = digitTag.getLongValue() ;
      if(value == null) return -1d ;
      return  value.doubleValue() ;
    }
    NumberTag number = (NumberTag) token.getFirstTagType(NumberTag.TYPE) ;
    if(number != null) return number.getValue() ;
    return -1d ;
  }

  private boolean setCurrencyTag(IToken token, double amount, String unit) {
    if(StringUtil.isIn(unit, USD_TOKENS)) token.add(new CurrencyTag(amount , "usd")) ;
    else if(StringUtil.isIn(unit, DO_TOKENS)) token.add(new CurrencyTag(amount, "usd")) ;
    else return false ;
    return true ;
  }
}