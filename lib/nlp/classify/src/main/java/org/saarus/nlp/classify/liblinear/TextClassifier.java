package org.saarus.nlp.classify.liblinear;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.TreeMap;

import org.saarus.nlp.token.IToken;
import org.saarus.nlp.token.TextSegmenter;
import org.saarus.nlp.token.TokenException;

import de.bwaldvogel.liblinear.FeatureNode;
import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;

public class TextClassifier {
  private FeatureGenerators contextGenerator;
  private TextSegmenter textTokenizer ;
  private Model model;
  private FeatureSet featureSet;
  
  public TextClassifier(InputStream model, InputStream dict) throws Exception {
    this(TextTrainer.FEATURE_GENERATORS, TextTrainer.TEXT_TOKENIZER, model, dict) ;
  }
  
  public TextClassifier(FeatureGenerators fgenerator, TextSegmenter tokenizer, InputStream model, InputStream dict) throws Exception {
    this.contextGenerator = fgenerator; 
    this.textTokenizer = tokenizer ;
    this.model = Linear.loadModel(new BufferedReader(new InputStreamReader(model)));
    ObjectInputStream in = new ObjectInputStream(dict);
    featureSet = (FeatureSet) in.readObject();
    in.close();
  }
  
  public double classify(String sentence) throws TokenException {
    IToken[] tokens = textTokenizer.segment(sentence);
    TreeMap<Integer, Integer> vector = featureSet.addStringFeatureVector(contextGenerator.getContext(tokens, sentence), "", true);
    ArrayList<FeatureNode> vfeatures = new ArrayList<FeatureNode>();
    if (vector == null) return -1;		
    for (int key : vector.keySet()) {
      if (key == featureSet.getLabelKey()) continue;
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
