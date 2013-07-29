package sample.nlp.sentiment;

import java.util.ArrayList;
import java.util.List;

import sample.ml.classifier.FeatureGenerator;
import sample.nlp.twitter.parsing.TweetToken;

public class SentimentContextGenerator {
  private FeatureGenerator<List<TweetToken>, String>[] mFeatureGenerators;

  public SentimentContextGenerator(FeatureGenerator<List<TweetToken>, String>[] mFeatureGenerators) {		
    this.mFeatureGenerators = mFeatureGenerators;
  }

  public ArrayList<String> getContext(List<TweetToken> tokens, String sentence) {
    ArrayList<String> context = new ArrayList<String>();
    for (FeatureGenerator<List<TweetToken>, String> generator : mFeatureGenerators) {
      ArrayList<String> extractedFeatures = generator.extractFeatures(tokens, sentence);
      context.addAll(extractedFeatures);
    }
    return context;
  }
}
