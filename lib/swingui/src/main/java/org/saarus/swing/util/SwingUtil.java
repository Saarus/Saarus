package org.saarus.swing.util ;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.List;

public class SwingUtil {
  static public <T> List<T> findDescendantOfType(Container parent, Class<T> type) {
    List<T> holder = new ArrayList<T>() ;
    findDescendantOfType(holder, parent, type) ;
    return holder ;
  }
  
  static public <T> void findDescendantOfType(List<T> holder, Container parent, Class<T> type) {
    if(type.isInstance(parent)) {
      holder.add((T)parent) ;
    }
    for(Component child : parent.getComponents()) {
      if(child instanceof Container) {
        findDescendantOfType(holder, (Container)child, type) ;
      } else {
        if(type.isInstance(child)) {
          holder.add((T) child) ;
        }
      }
    }
  }
}