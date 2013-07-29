package sample.nlp.twitter.parsing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cmu.arktweetnlp.Twokenize;
import cmu.arktweetnlp.impl.Model;
import cmu.arktweetnlp.impl.ModelSentence;
import cmu.arktweetnlp.impl.Sentence;
import cmu.arktweetnlp.impl.features.FeatureExtractor;


/**
 * Tagger object -- wraps up the entire tagger for easy usage from Java.
 * 
 * To use:
 * 
 * (1) call loadModel().
 * 
 * (2) call tokenizeAndTag() for every tweet.
 *  
 * See main() for example code.
 * 
 * (Note RunTagger.java has a more sophisticated runner.
 * This class is intended to be easiest to use in other applications.)
 */
public class Tagger {
  public Model model;
  public FeatureExtractor featureExtractor;

  /**
   * Loads a model from a file.  The tagger should be ready to tag after calling this.
   * 
   * @param modelFilename
   * @throws IOException
   */
  public void loadModel(String modelFilename) throws IOException {
    model = Model.loadModelFromText(modelFilename);
    featureExtractor = new FeatureExtractor(model, false);
  }

  /**
   * One token and its tag.
   **/
  public static class TaggedToken {
    public String token;
    public String tag;
  }


  /**
   * Run the tokenizer and tagger on one tweet's text.
   **/
  public List<TaggedToken> tokenizeAndTag(String text) {
    if (model == null) throw new RuntimeException("Must loadModel() first before tagging anything");
    
    List<String> tokens = Twokenize.tokenizeRawTweetText(text);

    Sentence sentence = new Sentence();
    sentence.tokens = tokens;
    ModelSentence ms = new ModelSentence(sentence.T());
    featureExtractor.computeFeatures(sentence, ms);
    model.greedyDecode(ms, false);

    ArrayList<TaggedToken> taggedTokens = new ArrayList<TaggedToken>();

    for (int t=0; t < sentence.T(); t++) {
      TaggedToken tt = new TaggedToken();
      tt.token = tokens.get(t);
      tt.tag = model.labelVocab.name( ms.labels[t] );
      taggedTokens.add(tt);
    }

    return taggedTokens;
  }
  
  /**
   * Run the tokenizer and tagger on one tweet's text.
   **/
  public List<String> tokenize(String text) {
    return Twokenize.tokenizeRawTweetText(text);
  }
}
