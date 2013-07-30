package sample.nlp.twitter.normalizing;

import org.tartarus.snowball.ext.EnglishStemmer;
import edu.stanford.nlp.process.Morphology;

public class MorphologyProcessing {
  Morphology     lemma = new Morphology();
  EnglishStemmer stemmer = new EnglishStemmer();

  public String lemma(String token) { return lemma.stem(token); }

  synchronized public String stem(String token) { 
    stemmer.setCurrent(token) ;
    if(stemmer.stem()) token = stemmer.getCurrent() ;
    return token ; 
  }

}
