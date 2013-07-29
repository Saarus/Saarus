package org.saarus.nlp.token.tag;

import org.saarus.nlp.dict.Meaning;
import org.saarus.nlp.util.StringPool;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class MeaningTag extends TokenTag {
	private Meaning    meaning ;
	
  public MeaningTag(Meaning meaning) {
  	this.meaning = meaning ;
  }
  
  public Meaning getMeaning() { return this.meaning ; }
  
	public String getTagValue() { return meaning.getName() ; }

	public String getOType() { return meaning.getOType() ; }
	
  public boolean isTypeOf(String type) {
	  return meaning.getOType().equals(type);
  }
  
  public void optimize(StringPool pool) { meaning.optimize(pool) ; }
}