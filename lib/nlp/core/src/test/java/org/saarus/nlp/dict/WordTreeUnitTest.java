package org.saarus.nlp.dict;

import org.junit.Assert;
import org.junit.Test;
import org.saarus.nlp.token.Token;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class WordTreeUnitTest {
  @Test
  public void test() throws Exception {
    String[] words = {"today", "is", "a", "beautiful", "day"} ;
    Dictionary dict = new Dictionary() ;
    for(String sel : words) {
      Meaning meaning = new Meaning() ;
      meaning.setOType("lexicon") ;
      meaning.setName(sel) ;
      dict.add(meaning) ;
    }
    WordTree wordTree = dict.getWordTree() ;
    wordTree.dump(System.out, "") ;

    String text = "today is a beautiful day" ;
    String[] word = text.split(" ") ;
    Token[] token = new Token[word.length] ;
    for(int i = 0; i < word.length; i++) {
      token[i] = new Token(word[i]) ;
    }
    Assert.assertEquals("today", wordTree.matches(token, 0).getEntry().getName()) ;
    Assert.assertEquals("beautiful", wordTree.matches(token, 3).getEntry().getName()) ;
  
  }
}