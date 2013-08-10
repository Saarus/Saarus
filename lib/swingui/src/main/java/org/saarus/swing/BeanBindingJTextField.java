package org.saarus.swing;

import javax.swing.JTextField;

import org.saarus.swing.listener.JTextFieldChangeTextListener;
import org.saarus.util.BeanInspector;

public class BeanBindingJTextField<T> extends JTextField {
  private T bean ;
  private String beanProperty ;
  private BeanInspector<T> beanInspector ;
  
  public BeanBindingJTextField(T aBean, String property) {
    beanProperty = property ;
    beanInspector = BeanInspector.get(aBean.getClass()) ;
    setBean(aBean) ;
    getDocument().addDocumentListener(new JTextFieldChangeTextListener() {
      public void onChange(String text) { 
        text = onTextChange(text) ;
        beanInspector.setValue(bean, beanProperty,  text) ;
      }
    });
  }
  
  public void setText(Object value) {
    if(value == null) setText("") ;
    else setText(value.toString()) ;
  }
  
  public void setBean(T bean) {
    this.bean = bean ;
    setText(beanInspector.getValue(bean, beanProperty)) ;
  }
  
  public void setBeanValue(Object val) {
    setText(val) ;
  }
  
  public String onTextChange(String text) {
    return text; 
  }
}
