package sample.nlp.twitter.parsing;

import java.util.HashMap;
import java.util.Map;

public class TweetToken {
  int id;
  String token;
  Map<String, String> tokenNormalized;
  Map<String, String> tokenAnnotations;

  public TweetToken() {
    tokenNormalized = new HashMap<String, String>();
    tokenAnnotations = new HashMap<String, String>();
  }

  public int getId() { return id; }
  public void setId(int id) { this.id = id; }

  public String getToken() { return token; }
  public void setToken(String token) { this.token = token; }

  public Map<String, String> getTokenNormalized() { return tokenNormalized; }

  public void addTokenNormalized(String label, String normalized) {
    tokenNormalized.put(label, normalized);
  }

  public void setTokenNormalized(Map<String, String> tokenNormalized) {
    this.tokenNormalized = tokenNormalized;
  }

  public Map<String, String> getTokenAnnotations() {
    return tokenAnnotations;
  }

  public void addTokenAnnotations(String label, String annotation) {
    tokenAnnotations.put(label, annotation);
  }

  public void setTokenAnnotations(Map<String, String> tokenAnnotations) {
    this.tokenAnnotations = tokenAnnotations;
  }

  @Override
  public String toString() {
    return "TweetToken [id=" + id + ", token=" + token
        + ", tokenNormalized=" + tokenNormalized
        + ", tokenAnnotations=" + tokenAnnotations + "]";
  }	
}
