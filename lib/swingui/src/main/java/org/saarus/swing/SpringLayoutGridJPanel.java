package org.saarus.swing;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.saarus.swing.util.SpringUtilities;

public class SpringLayoutGridJPanel extends JPanel {
  private int numberOfRow = 0;
  private int numberOfColumn = -1 ;
  
  public SpringLayoutGridJPanel() {
    setLayout(new SpringLayout()) ;
  }
  
  public void addRow(Object ... comp) {
    if(numberOfColumn < 0) numberOfColumn = comp.length ;
    if(numberOfColumn != comp.length) {
      throw new RuntimeException("Expect " + numberOfColumn + " columns") ;
    }
    for(Object sel : comp) {
      if(sel instanceof String) {
        add(new JLabel((String) sel)) ;
      } else if(sel instanceof Component) {
        add((Component) sel) ;
      } else {
        throw new RuntimeException("Not support type " + sel.getClass()) ;
      }
    }
    numberOfRow++ ;
  }
  
  public void makeGrid() {
    if(numberOfColumn < 0) {
      throw new RuntimeException("No components are added") ;
    }
    SpringUtilities.makeCompactGrid(this, /*beans, cols*/numberOfRow, numberOfColumn,  /*initX, initY*/ 3, 3, /*xPad, yPad*/3, 3);
  }
  
  public void makeGrid(int initX, int initY, int xPad, int yPad) {
    if(numberOfColumn < 0) {
      throw new RuntimeException("No components are added") ;
    }
    SpringUtilities.makeCompactGrid(this, /*beans, cols*/numberOfRow, numberOfColumn,  initX, initY, xPad, yPad);
  }
  
  public void createBorder(String title) {
    setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), title));
  }
  
  public void createBorder() {
    setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder()));
  }
}
