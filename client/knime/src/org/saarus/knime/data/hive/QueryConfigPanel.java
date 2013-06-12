package org.saarus.knime.data.hive;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.saarus.knime.data.hive.QueryConfigs.QueryConfig;
import org.saarus.knime.uicomp.SpringUtilities;

public class QueryConfigPanel extends JPanel {
  final static int MAX_WIDTH = QueryNodeDialog.WIDTH ;
  
  private JTextField nameInput, descInput ;
  private JTextArea queryArea ;
  
  public QueryConfigPanel(QueryConfig config) {
    setLayout(new BorderLayout()) ;
    add(createDescBox(config),       BorderLayout.NORTH);
    add(createQueryBox(config), BorderLayout.CENTER) ;
  }
  
  private JPanel createDescBox(QueryConfig config) {
    JPanel panel = new JPanel(new SpringLayout()) ;
    panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Description"));
    
    nameInput = new JTextField(config.name) ;
    descInput = new JTextField(config.description) ;
    
    panel.add(new JLabel("Name")) ;
    panel.add(nameInput) ;
    
    panel.add(new JLabel("Description")) ;
    panel.add(descInput) ;
    
    SpringUtilities.makeCompactGrid(panel, /*rows, cols*/ 2, 2, /*initX, initY*/6, 6, /*xPad, yPad*/6, 6);
    return panel ;
  }
  
  private JPanel createQueryBox(QueryConfig config) {
    String query = 
      "CREATE TABLE yelp_features (\n" +
      "  review_id      STRING,\n" +
      "  user_id        STRING,\n" +
      "  user_name      STRING,\n" +
      "  business_id    STRING,\n" +
      "  business_name  STRING,\n" +
      "  vote_useful    INT,\n" +
      "  vote_cool      INT,\n" +
      "  vote_funny     INT,\n" +
      ") ROW FORMAT DELIMITED FIELDS TERMINATED BY ',' ; \n\n" +
      
      "INSERT OVERWRITE TABLE yelp_features \n" +
      "SELECT r.review_id, r.user_id, u.name, r.business_id, b.name, r.vote_funny, r.vote_useful, r.vote_cool\n" +
      "FROM review r \n " +
      "JOIN user u ON(r.user_id = u.user_id) \n " +
      "JOIN business b ON(r.business_id = b.business_id) \n " ;
    if(config.query != null && config.query.length() > 0) {
      query = config.query ;
    }
    
    queryArea = new JTextArea(query) ;
    JScrollPane scrollText = new JScrollPane(queryArea);
    JPanel panel = new JPanel(new BorderLayout()) ;
    panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Query"));
    panel.add(scrollText, BorderLayout.CENTER);
    return panel ;
  }
  
  QueryConfig getQueryConfig() {
    String name = nameInput.getText() ;
    String desc  = descInput.getText() ;
    String query = queryArea.getText()  ;
    QueryConfig config = new QueryConfig(name, desc, query) ;
    return config ;
  }
}