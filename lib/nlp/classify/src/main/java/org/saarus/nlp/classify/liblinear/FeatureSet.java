package org.saarus.nlp.classify.liblinear;

import gnu.trove.map.hash.TObjectIntHashMap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;

public class FeatureSet implements java.io.Serializable {
  private static final long serialVersionUID = 1L;

  TObjectIntHashMap<String> wordlist;
  List<String> labels;

  int labelKey = 0;

  public FeatureSet() {
    wordlist = new TObjectIntHashMap<String>();
    wordlist.put("NO_USE", 0);
    labels = new ArrayList<String>();
  }

  public TObjectIntHashMap<String> getWordlist() { return wordlist; }
  public void setWordlist(TObjectIntHashMap<String> wordlist) { this.wordlist = wordlist; }

  public List<String> getLabels() { return labels; }
  public void setLabels(List<String> labels) { this.labels = labels; }

  public int getLabelKey() { return labelKey; }
  public void setLabelKey(int labelKey) { this.labelKey = labelKey; }

  public TreeMap<Integer, Integer> addStringFeatureVector(String[] strFeatures, String label, boolean flagTest) {
    HashSet<String> setFeatures = new HashSet<String>();
    TreeMap<Integer, Integer> vector = new TreeMap<Integer, Integer>();

    for (String feature : strFeatures) {
      setFeatures.add(feature);
    }
    
    if (setFeatures.size() == 0) return null;

    if (!label.equals(""))
      if (labels.contains(label)) {
        vector.put(labelKey, labels.indexOf(label));
      } else {
        if (!flagTest) {
          labels.add(label);
          vector.put(labelKey, labels.indexOf(label));
        } else {
          // throw new IllegalArgumentException("Label of Testing Data is error!!!");
          return null;
        }

      }

    for (String feature : setFeatures) {
      if (wordlist.contains(feature)) {
        vector.put(wordlist.get(feature), 1);
      } else {
        if (!flagTest) {
          wordlist.put(feature, wordlist.size());
          vector.put(wordlist.get(feature), 1);
        }
      }
    }
    return vector;
  }

  public TreeMap<Integer, Integer> addStringFeatureVector(List<String> strFeatures, String label, boolean flagTest) {
    if (strFeatures == null) return null;
    return addStringFeatureVector(strFeatures.toArray(new String[strFeatures.size()]), label, flagTest);
  }

  public String addprintVector(List<String> strFeatures, String label, boolean flagTest) {
    TreeMap<Integer, Integer> vector = addStringFeatureVector(strFeatures, label, flagTest);
    if (vector == null) return "";

    String text = "" + vector.get(labelKey);
    for (int key : vector.keySet()) {
      if (key == labelKey) continue;
      text += " " + key + ":" + vector.get(key);
    }
    return text;
  }

  public static void main(String[] args) {
    String[] strVector1 = "shuttle shuttl 2G:sh 2G:hu 2G:ut".split(" ");
    String[] strVector2 = "abt shuttl anf 2G:hu 2G:ut".split(" ");

    FeatureSet featureSet = new FeatureSet();
    TreeMap<Integer, Integer> vector1 = featureSet.addStringFeatureVector(strVector1, "ABC", false);
    TreeMap<Integer, Integer> vector2 = featureSet.addStringFeatureVector(strVector2, "XYZ", false);
    System.out.println(featureSet.getWordlist());
    System.out.println(vector1);
    System.out.println(vector2);
    System.out.println(featureSet.getLabels());
  }
}