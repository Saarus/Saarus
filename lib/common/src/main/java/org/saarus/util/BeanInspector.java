package org.saarus.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public class BeanInspector<T> {
  static Object[] EMPTY_ARGS = new Object[0] ;
  static WeakHashMap<String, BeanInspector> inspectors = new WeakHashMap<String, BeanInspector>() ;
  
  private Map<String, PropertyDescriptor> pdescriptors = new HashMap<String, PropertyDescriptor>() ;
 
  private BeanInspector(Class<T> type) {
    try {
      BeanInfo info = Introspector.getBeanInfo(type);
      for(PropertyDescriptor sel : info.getPropertyDescriptors()) {
        pdescriptors.put(sel.getName(), sel) ;
      }
    } catch (IntrospectionException e) {
      throw new RuntimeException(e) ;
    }
  }
  
  public Class getPropertyType(String property) {
    try {
      PropertyDescriptor descriptor = pdescriptors.get(property) ;
      return descriptor.getReadMethod().getReturnType() ;
    } catch (Throwable t) {
      throw new RuntimeException(t) ;
    }
  }
  
  public Object getValue(T target, String property) {
    try {
      PropertyDescriptor descriptor = pdescriptors.get(property) ;
      return descriptor.getReadMethod().invoke(target, EMPTY_ARGS) ;
    } catch (Throwable t) {
      t.printStackTrace() ;
      throw new RuntimeException(t) ;
    } 
  }
  
  public void setValue(T target, String property, Object value) {
    try {
      PropertyDescriptor descriptor = pdescriptors.get(property) ;
      descriptor.getWriteMethod().invoke(target, new Object[] { value }) ;
    } catch (Throwable t) {
      t.printStackTrace() ;
      throw new RuntimeException(t) ;
    }
  }
  
  static public <T> BeanInspector get(Class<T> type) {
    BeanInspector inspector = inspectors.get(type.getName()) ;
    if(inspector == null) {
      inspector = new BeanInspector<T>(type) ;
      inspectors.put(type.getName(), inspector) ;
    }
    return inspector ;
  }
}
