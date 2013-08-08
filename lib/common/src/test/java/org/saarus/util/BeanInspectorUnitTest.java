package org.saarus.util;

import junit.framework.Assert;

import org.junit.Test;


public class BeanInspectorUnitTest {
 
  public class Bean {
    private String string;
    private int integer;

    public String getString() { return string; }
    public void setString(String string) { this.string = string; }

    public int getInteger() { return integer; }

    public void setInteger(int integer) { this.integer = integer; }
  }
  
  @Test
  public void testReadWriteProperty() {
    BeanInspector<Bean> inspector = BeanInspector.get(Bean.class) ;
    Bean bean = new Bean() ;
    Assert.assertNull(bean.getString()) ;
    Assert.assertEquals(0, bean.getInteger()) ;
    
    inspector.setValue(bean, "string", "string") ;
    Assert.assertEquals("string", bean.getString()) ;
    
    inspector.setValue(bean, "integer", 100) ;
    Assert.assertEquals(100, bean.getInteger()) ;
  }
}