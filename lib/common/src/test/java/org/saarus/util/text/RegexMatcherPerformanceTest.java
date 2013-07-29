package org.saarus.util.text ;

import org.saarus.util.text.RegexMatcher;

public class RegexMatcherPerformanceTest {
  static class Report {
  	private String name ;
  	private RegexMatcher regex ;
  	
  	public Report(String name, RegexMatcher regex) { 
  		this.name  = name ;
  		this.regex = regex ; 
  	}
  	
  	public void report(String text, boolean expect, int loop) {
  		long start = System.currentTimeMillis();
    	for(int i = 0 ; i < loop; i++) {
        if(regex.matches(text) == expect) continue ;
        System.out.println("Fail Expect!!!!!!!!!!!!!!!!!") ;
        break ;
      }
    	System.out.println(name + "[" + expect +"]:" + (System.currentTimeMillis() - start) + " ms");
    }
  }
  
  static void run(int LOOP) {
  	String trueText = "acticle:kinh-te" ;
    String failText = "acticles:kinh-te" ;
    
    System.out.println("-----------------------LOOP " + LOOP + "--------------------------"); 
    Report customReport = new Report("custom", RegexMatcher.createCustom("acticle:*")) ;
    customReport.report(trueText, true, LOOP) ;
    customReport.report(failText, false, LOOP) ;
    
    
    Report patternReport = new Report("pattern", RegexMatcher.createPattern("acticle:.*")) ;
    patternReport.report(trueText, true, LOOP) ;
    patternReport.report(failText, false, LOOP) ;
    
    Report automatonReport = new Report("automaton", RegexMatcher.createAutomaton("acticle:.*")) ;
    automatonReport.report(trueText, true, LOOP) ;
    automatonReport.report(failText, false, LOOP) ;
  }
  
  public static void main(String[] args) {
  	run(1000) ;
  	run(10000000) ;
  }
}