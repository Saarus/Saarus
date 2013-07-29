package sample.nlp.twitter.normalizing;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

// TODO: Add a similarity method for tweet normalizing
public class TweetNormalizing {
  static String dictionaryFile = "models/NormalizeDictionary.txt";
  Map<String, String> dictionary = new HashMap<String, String>();

  public TweetNormalizing() throws Exception {
    this(Thread.currentThread().getContextClassLoader().getResourceAsStream(dictionaryFile));
  }

  public TweetNormalizing(String dictionaryFile) throws Exception {
    loadDictionary(new FileInputStream(dictionaryFile)) ;
  }
  
  public TweetNormalizing(InputStream is) throws Exception {
    loadDictionary(is) ;
  }

  void loadDictionary(InputStream is) throws Exception {
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));		
    String line = "";
    while ((line = reader.readLine()) != null) {
      if (line.trim().equals("")) continue;
      String[] segs = line.split("\t");
      dictionary.put(segs[0], segs[1]);
    }
    reader.close();
  }

  public String normalize(String token) {
    if (dictionary.containsKey(token.toLowerCase()))
      return dictionary.get(token.toLowerCase());
    String newToken = removeDuplicates(token.toLowerCase());
    if (dictionary.containsKey(newToken.toLowerCase()))
      return dictionary.get(newToken.toLowerCase());
    if (dictionary.containsValue(newToken))
    	return newToken;
    return token;
  }

  public static String removeDuplicates(String token) {
    StringBuilder noDupes = new StringBuilder();
    int pos = 0;
    int len = token.length();
    int curLen = 0;
    while (pos < len) {
      if (pos == 0 || token.charAt(pos) != token.charAt(pos - 1)) {
        curLen = 1;
      } else {
        ++curLen;
      }
      if (curLen < 3) {
        noDupes.append(token.charAt(pos));
      }
      ++pos;
    }
    return noDupes.toString();
  }

  public static void main(String[] args) throws Exception {
    TweetNormalizing normalizer = new TweetNormalizing();
    System.out.println(normalizer.normalize("victoryyyy"));
  }
}
