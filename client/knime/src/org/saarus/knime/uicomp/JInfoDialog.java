package org.saarus.knime.uicomp;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class JInfoDialog extends JDialog {
  private static final long serialVersionUID = 1L;

  private JTextArea infoPanel ;
  
  public JInfoDialog() {
    setTitle("Info") ;
    setMinimumSize(new Dimension(600, 500)) ;
    setLayout(new BorderLayout()) ;
    setAlwaysOnTop(true) ;
    JButton close = new JButton("OK") ;
    close.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent event) {
        onClose() ;
      }
    }) ;
    infoPanel = new JTextArea() ;
    infoPanel.setEditable(false) ;
    JScrollPane scrollText = new JScrollPane(infoPanel);
    add(scrollText, BorderLayout.CENTER) ;
    add(close, BorderLayout.SOUTH) ;
  }
  
  public void setInfo(String text) {
    infoPanel.setText(text) ;
  }
  
  public void onClose() {
    dispose() ;
  }
}