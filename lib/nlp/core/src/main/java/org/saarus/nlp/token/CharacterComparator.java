package org.saarus.nlp.token;


public interface CharacterComparator {
  final static CharacterComparator DEFAULT = new DefaultCharacterComparator() ;
  
  public int compare(char c1, char c2) ;
  
  static public class DefaultCharacterComparator implements CharacterComparator {
    public int compare(char c1, char c2) {
      c1 = Character.toLowerCase(c1) ;
      c2 = Character.toLowerCase(c2) ;
      return c1 - c2;
    }
  }
}
