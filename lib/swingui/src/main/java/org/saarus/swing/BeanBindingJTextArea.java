package org.saarus.swing;

import javax.swing.JTextArea;

import org.saarus.swing.listener.JTextFieldChangeTextListener;
import org.saarus.util.BeanInspector;

public class BeanBindingJTextArea<T> extends JTextArea {
  private T bean ;
  private String beanProperty ;
  private BeanInspector<T> beanInspector ;
  
  public BeanBindingJTextArea(T aBean, String property) {
    bean = aBean ;
    beanProperty = property ;
    beanInspector = BeanInspector.get(bean.getClass()) ;
    setText(beanInspector.getValue(bean, property)) ;
    getDocument().addDocumentListener(new JTextFieldChangeTextListener() {
      public void onChange(String text) { 
        beanInspector.setValue(bean, beanProperty,  text) ;
      }
    });
  }
  
  public void setText(Object value) {
    if(value == null) setText("") ;
    else setText(value.toString()) ;
  }
  
  public void setValue(Object val) {
    beanInspector.setValue(bean, beanProperty,  val) ;
    setText(val) ;
  }
}
