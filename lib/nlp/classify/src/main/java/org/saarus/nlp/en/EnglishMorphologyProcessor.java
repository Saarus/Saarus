package org.saarus.nlp.en;

import org.tartarus.snowball.ext.EnglishStemmer;

import edu.stanford.nlp.process.Morphology;

public class EnglishMorphologyProcessor {
  Morphology     lemma = new Morphology();
  EnglishStemmer stemmer = new EnglishStemmer();

  public String process(String token)  {
    String normToken = lemma.stem(token);
    if(normToken != null) return normToken ;
    synchronized(this) {
      stemmer.setCurrent(token) ;
      if(stemmer.stem()) return  stemmer.getCurrent() ;
    }
    return token;
  }
}
