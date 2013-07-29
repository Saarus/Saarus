package sample.nlp.sentiment;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import sample.ml.classifier.FeatureSet;
import sample.nlp.twitter.parsing.TweetParsing;
import sample.nlp.twitter.parsing.TweetToken;
import de.bwaldvogel.liblinear.FeatureNode;
import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;

public class SentimentClassifier {
  String modelFile = "models/fullSentiment.model";
  String wordListFile = "models/fullSentiment.wordlist";

  SentimentContextGenerator contextGenerator;
  Model model;
  FeatureSet featureSet;
  TweetParsing parsing;

  public void init() throws Exception {
    contextGenerator = new SentimentContextGenerator(SentimentTraining.mFeatureGenerators);
    parsing = new TweetParsing();
  }

  public SentimentClassifier() throws Exception {
    init();
    loadModel();
    loadWordlist();
  }

  public SentimentClassifier(String modelFile, String wordlistFile) throws Exception {
    this.modelFile = modelFile;
    this.wordListFile = wordlistFile;
    init();
    loadModel();
    loadWordlist();
  }

  void loadWordlist() throws Exception {
    FileInputStream fileIn = new FileInputStream(wordListFile);
    ObjectInputStream in = new ObjectInputStream(fileIn);
    featureSet = (FeatureSet) in.readObject();
    in.close();
    fileIn.close();
  }

  void loadModel() throws Exception {
    model = Linear.loadModel(new File(modelFile));
  }

  public double classify(String sentence) {
    List<TweetToken> tokens = parsing.parsing(sentence);
    TreeMap<Integer, Integer> vector = featureSet.addStringFeatureVector(
        contextGenerator.getContext(tokens, sentence), "", true);

    ArrayList<FeatureNode> vfeatures = new ArrayList<FeatureNode>();

    if (vector == null) return -1;		
    for (int key : vector.keySet()) {
      if (key == featureSet.getLabelKey())
        continue;
      FeatureNode featurenode = new FeatureNode(key, vector.get(key));
      vfeatures.add(featurenode);
    }

    double output = Linear.predict(model,
        vfeatures.toArray(new FeatureNode[vfeatures.size()]));
    return output;
  }

  public FeatureSet getFeatureSet() { return featureSet; }

  public void setFeatureSet(FeatureSet featureSet) { this.featureSet = featureSet; }
}
