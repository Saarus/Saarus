package sample.nlp.twitter.parsing;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import sample.nlp.twitter.normalizing.MorphologyProcessing;
import sample.nlp.twitter.normalizing.TweetNormalizing;
import sample.nlp.twitter.parsing.Tagger.TaggedToken;
import sample.nlp.twitter.util.FileUtil;
//import cmu.arktweetnlp.Tagger;
//import cmu.arktweetnlp.Tagger.TaggedToken;


public class TweetParsing {
  static String modelFile = "models/model.20120919";

  Tagger tagger = new Tagger();
  MorphologyProcessing morphology = new MorphologyProcessing();
  TweetNormalizing normalizer = new TweetNormalizing();

  public TweetParsing() throws Exception {
    InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(modelFile) ;
    String mfile = "target/tweet.model" ;
    if(!new File(mfile).exists()) {
      FileUtil.mkdirs("target") ;
      FileUtil.copyTo(is, mfile) ;
    }
    tagger.loadModel(mfile);
  }

  public List<TweetToken> parsing(String tweet) {
    List<TweetToken> tokens = new ArrayList<TweetToken>();
    List<TaggedToken> taggedTokens = tagger.tokenizeAndTag(tweet);
    for (int i = 0; i < taggedTokens.size(); i++) {
      TweetToken token = new TweetToken();
      token.setId(i);
      token.setToken(taggedTokens.get(i).token);
      
      String pos = taggedTokens.get(i).tag;
      token.addTokenAnnotations("POS", pos);
      if (pos.matches("[,|$|U|E|G|\\^]")) {
        tokens.add(token);
        continue;
      }     

      String text = taggedTokens.get(i).token;
      //if (pos.matches("[#]")) {
      if (pos.startsWith("#")) {
        text = text.substring(1);
      }

      String lemma = morphology.lemma(text);
      if (lemma == null) {
        tokens.add(token);
        continue;
      }
      
      token.addTokenNormalized("Lemma", normalizer.normalize(lemma));
      String stem = morphology.stem(text);
      if (stem == null) {
        tokens.add(token);
        continue;
      }
      token.addTokenNormalized("Stem", normalizer.normalize(stem));
      tokens.add(token);
    }

    return tokens;
  }
  
  public List<TaggedToken> tokenizeAndTag(String tweet) {
    List<TaggedToken> taggedTokens = tagger.tokenizeAndTag(tweet);
    return taggedTokens ;
  }
  
  public List<String> tokenize(String tweet) {
    return tagger.tokenize(tweet);
  }
}
