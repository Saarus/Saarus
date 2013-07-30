package org.saarus.swing.sql;

import java.awt.event.ActionEvent;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.UIManager;

import org.junit.Test;

public class SQLQueryBuilderUnitTest {
  @Test
  public void test() throws Exception {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    final SQLQueryBuilder editor = new SQLQueryBuilder();
    editor.addSources(createTables(SQLTable.INPUT_TABLE_TYPE, 10)) ;

    editor.addOutput(createTable(SQLTable.OUTPUT_TABLE_TYPE, "New_Table", 10)) ;
    editor.addOutput(createTables(SQLTable.OUTPUT_TABLE_TYPE, 5)) ;
    
    editor.getToolbar().add(editor, "Add Sample", null, new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        editor.insertSample(createTables(SQLTable.INPUT_TABLE_TYPE, 3), createTable(SQLTable.OUTPUT_TABLE_TYPE, "Output", 10)) ;
      }
    });

    editor.getToolbar().add(editor, "Query", null, new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        SQLOutputTable outTable = SQLQueryBuilderUtil.getSQLOutputTable(editor) ;
        SQLQuery sqlQuery = outTable.getSQLQuery() ;
        System.out.println(sqlQuery.getOutputSQLTable().buildDropTableSQL());
        System.out.println(sqlQuery.getOutputSQLTable().buildCreateTable());
        System.out.println(sqlQuery.buildInsertSQLQuery());
      }
    });
    
    JFrame frame = new JFrame();
    frame.getContentPane().add(editor);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(900, 700);
    frame.setVisible(true) ;
    Thread.currentThread().join() ;
  }

  SQLTable[] createTables(String type, int num) {
    SQLTable[] table = new SQLTable[num] ;
    for(int i = 0; i < table.length; i++) {
      table[i] = createTable(type, "Table" + i, new Random().nextInt(10)) ;
    }
    return table ;
  }

  SQLTable createTable(String tableType, String tableName, int numOfField) {
    if(numOfField < 3) numOfField = 3 ;
    SQLTable table = new SQLTable(tableName) ;
    table.setType(tableType) ;
    for(int i = 0; i < numOfField; i++) {
      String fieldType = "string" ;
      double random = Math.random() ;
      if(random > 0.9) fieldType = "double" ;
      else if(random > 0.8) fieldType = "float" ;
      if(random > 0.7) fieldType = "int" ;
      if(random > 0.6) fieldType = "boolean" ;
      table.addField(fieldType + i, fieldType, null) ;
    }
    return table ;
  }
}