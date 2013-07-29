package org.saarus.nlp.token.tag;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class NumberTag extends QuantityTag {
  final static public String TYPE = "number" ;
  private double value ;

  public NumberTag(double value) {
    super("number");
    this.value = value ;
  }

  public double getValue() { return this.value ; }

  public String getTagValue() { return Double.toString(value) ; }
}
