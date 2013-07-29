package org.saarus.util.text ;

import org.junit.Assert;
import org.junit.Test;
import org.saarus.util.text.RegexMatcher;

public class RegexMatcherUnitTest {
  @Test
  public void test() {
    verify("[0-9]{2}", "22", true) ;
    verify("[0-9]{0,2}", "22", true) ;
    verify("[0-9]{1,}", "22", true) ;
    verify("[0-9]{2,}", "22", true) ;
    verify("[0-9]{3,}", "22", false) ;

    verify("\\d{0,2}", "22", true) ;
    verify("\\d{2}", "22", true) ;

    verify("\\w{2}", "a2", true) ;
    verify("\\w{2}", "22", true) ;
    verify("\\w{2}", ".2", false) ;

    verify("[abc]{3}", "aaa", true) ;
    verify("[^abc]{3}", "def", true) ;

    verify("[a-zđ]+", "ađa", true) ;
    verify("[độ]*", "độ", true) ;
    verify("[a-zàảãáạăằẳẵắặâầẩẫấậbcdđeèẻẽéẹêềểễếệfghiìỉĩíịjklmnoòỏõóọôồổỗốộơờởỡớợpqrstuùủũúụưừửữứựvwxyỳỷỹýỵz]*", "động", true) ;
  }

  private void verify(String exp, String text, boolean match) {
    RegexMatcher matcher = RegexMatcher.createPattern(exp) ;
    Assert.assertTrue("Pattern", matcher.matches(text) == match) ;
    matcher = RegexMatcher.createAutomaton(exp) ;
    Assert.assertTrue("Automaton", matcher.matches(text) == match) ;
  }
}