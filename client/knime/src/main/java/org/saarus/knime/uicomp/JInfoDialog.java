package org.saarus.knime.uicomp;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.saarus.swing.sql.SQLQueryBuilder;

public class JInfoDialog extends JDialog {
  private static final long serialVersionUID = 1L;
  
  static private JInfoDialog instance = new JInfoDialog() ; 
  
  private JTextArea infoTextArea ;
  private SQLQueryBuilder queryBuilder ;
  
  private JInfoDialog() {
    setTitle("Info") ;
    setLayout(new BorderLayout()) ;
    setAlwaysOnTop(true) ;
    
    queryBuilder = new SQLQueryBuilder() ;
    
    JButton okButton = new JButton("OK") ;
    okButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent event) {
        setVisible(false) ;
      }
    }) ;
    infoTextArea = new JTextArea() ;
    add(new JScrollPane(infoTextArea), BorderLayout.CENTER) ;
    add(okButton, BorderLayout.SOUTH) ;
  }
  
  public void setInfo(String text) {
    infoTextArea.setText(text) ;
  }
  
  
  static public JInfoDialog getInstance() { return instance ; }
}