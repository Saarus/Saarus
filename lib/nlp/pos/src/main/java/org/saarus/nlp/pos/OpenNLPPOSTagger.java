package org.saarus.nlp.pos;

import java.io.FileInputStream;
import java.io.InputStream;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

import org.saarus.nlp.token.IToken;
import org.saarus.nlp.token.tag.PosTag;
import org.saarus.util.IOUtil;

public class OpenNLPPOSTagger {
  private POSTaggerME tagger ;
  
  public OpenNLPPOSTagger() throws Exception {
    InputStream is = IOUtil.loadRes("opennlp/en-pos-maxent-1.5.3.bin") ;
    POSModel model = new POSModel(is);
    tagger = new POSTaggerME(model);
    is.close() ;
  }
  
  public IToken[] tag(IToken[] token) {
    String[] tokenWord = new String[token.length] ;
    for(int i = 0; i < token.length; i++) {
      tokenWord[i] = token[i].getOriginalForm() ;
    }
    String[] tag = tagger.tag(tokenWord);
    for(int i = 0; i < token.length; i++) {
      PosTag ptag = new PosTag(tokenWord[i], tag[i]) ;
      token[i].add(ptag) ;
    }
    return token ;
  }
  
  public String[] tag(String[] token) {
    String[] tag = tagger.tag(token);
    return tag ;
  }
}
