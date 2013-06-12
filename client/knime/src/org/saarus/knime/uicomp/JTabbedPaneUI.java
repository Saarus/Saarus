package org.saarus.knime.uicomp;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class JTabbedPaneUI  extends JPanel {
  private JTabbedPane tabbedPane ;
  
  public JTabbedPaneUI() {
    setLayout(new BorderLayout()) ;
    tabbedPane = new JTabbedPane();
    tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
    
    add(tabbedPane, BorderLayout.CENTER) ;  
  }
  
  public void addTabView(String title, JComponent jpanel) {
    for(int i = 0; i < tabbedPane.getTabCount(); i++) {
      String checkTitle = tabbedPane.getTitleAt(i) ;
      if(title.equals(checkTitle)) {
        Component comp = tabbedPane.getComponentAt(i) ;
        tabbedPane.remove(i) ;
        break ;
      }
    }
    int index = tabbedPane.getTabCount() ;
    tabbedPane.add(title, jpanel);
    ClosableTabButton ctBtn = new ClosableTabButton(tabbedPane) ;
    tabbedPane.setTabComponentAt(index, ctBtn);
    tabbedPane.setSelectedIndex(index);
  }
  
  public void setSelectedTab(int idx) {
    tabbedPane.setSelectedIndex(idx) ;
  }
  
  public void addAddButton(ActionListener listener) {
    int selTab = tabbedPane.getSelectedIndex() ;
    addTabView("", null) ;
    JButton jbutton = new JButton("+") ;
    if(listener != null) {
      jbutton.addActionListener(listener) ;
    }
    tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, jbutton);
    tabbedPane.setSelectedIndex(selTab);
  }
  
  public int getTabCount() { return tabbedPane.getTabCount() ; }

  public Component getTabAt(int idx) {
    return tabbedPane.getComponentAt(idx) ;
  }
  
  public void removeTabAt(int idx) {
    tabbedPane.remove(idx) ;
  }
}
