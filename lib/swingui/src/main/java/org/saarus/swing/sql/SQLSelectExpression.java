package org.saarus.swing.sql;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.saarus.swing.sql.model.SQLTable.Field;

public class SQLSelectExpression extends JDialog {
  static Map<String, String> templates = new LinkedHashMap<String, String>() ;
  static {
    templates.put("Default", "") ;
    templates.put("IF", "IF(condition, trueValue, falseValue)") ;
    templates.put("REGEXP_REPLACE", "REGEXP_REPLACE(fieldName, 'regexp_pattern', 'value')") ;
    templates.put("NLP_CLASSIFY", "NLP_CLASSIFY(textField, 'dfs:/path/model')") ;
  }
  
  private Field field ;
  private JComboBox<String> expTemplate ;
  private TemplateExpression currentTemplate ;
  
  public SQLSelectExpression(Field aField) {
    this.field = aField ;
    setLayout(new BorderLayout()) ;
    setModalityType(ModalityType.APPLICATION_MODAL) ;
    setAlwaysOnTop(true) ;
    setDefaultCloseOperation(DISPOSE_ON_CLOSE) ;
    setMinimumSize(new Dimension(400, 250)) ;
    String[] templateName = templates.keySet().toArray(new String[templates.size()]) ;
    expTemplate = new JComboBox<String>(templateName) ;
    expTemplate.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String item = (String) expTemplate.getSelectedItem() ;
        setTemplate(item) ;
      }
    });
    add(expTemplate, BorderLayout.NORTH) ;

    currentTemplate = new TemplateExpression() ;
    currentTemplate.setExpression(field.getExpression()) ;
    add(new JScrollPane(currentTemplate), BorderLayout.CENTER) ;
    
    JButton okBtn = new JButton("OK") ;
    okBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        field.setExpression(currentTemplate.getExpression()) ;
        dispose() ;
      }
    }) ;
    add(okBtn, BorderLayout.SOUTH) ;
  }
  
  public void setTemplate(String name) {
    String template = templates.get(name) ;
    currentTemplate.setExpression(template) ;
  }

  static public interface Expression {
    public String getExpression() ;
    public void   setExpression(String exp) ;
  }
  
  static public class TemplateExpression extends JTextArea implements Expression {
    public TemplateExpression() {
      setAutoscrolls(true) ;
      setLineWrap(true) ;
      setWrapStyleWord(true) ;
    }
    
    public String getExpression() { return getText(); }
    public void   setExpression(String exp) { setText(exp) ; }
  }
}
