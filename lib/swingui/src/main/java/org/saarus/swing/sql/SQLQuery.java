package org.saarus.swing.sql;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.saarus.swing.sql.model.SQLTable;
import org.saarus.swing.sql.model.SQLTable.Field;

public class SQLQuery {
  SQLTable outputTable ;
  List<SQLTable> inputTables = new ArrayList<SQLTable>() ;
  private QueryTemplate queryTemplate ;
  private boolean createNewOutputTable = true ;
  
  public SQLQuery(SQLTable table)  {
    this.outputTable =  table ;
  }
  
  public SQLTable getOutputSQLTable() { return this.outputTable ; }
  public void     setOutputSQLTable(SQLTable table) { this.outputTable = table ; }
  
  public boolean getCreateNewOutputTable() { return createNewOutputTable ; }
  public void setCreateNewOutputTable(boolean b) { this.createNewOutputTable = b ; }
  
  public List<SQLTable> getInputTables() { return inputTables; }
  public void setInputTables(List<SQLTable> inputTables) { this.inputTables = inputTables; }

  
  public void addInputSQLTable(SQLTable table) {
    for(Field sel : table.getFields()) {
      if(sel.isSelect()) outputTable.addField(sel) ;
    }
    inputTables.add(table) ;
    if(queryTemplate != null) queryTemplate.onSqlQueryChange() ;
  }
  
  public void removeInputSQLTable(SQLTable table) {
    outputTable.removeFieldByMapFrom(table) ;
    for(Iterator<SQLTable> i = inputTables.iterator(); i.hasNext();) {
      SQLTable sel = i.next() ;
      if(sel.getTableName().equals(table.getTableName())) {
        i.remove() ;
        break ;
      }
    }
    if(queryTemplate != null) queryTemplate.onSqlQueryChange() ;
  }
  
  public JoinTemplate createJoinTemplate() {
    JoinTemplate joinTemplate = new JoinTemplate() ;
    joinTemplate.onSqlQueryChange() ;
    queryTemplate = joinTemplate ;
    return joinTemplate ;
  }

  public String buildInsertSQLQuery() {
    StringBuilder b = new StringBuilder() ;
    b.append("INSERT OVERWRITE TABLE ").append(this.outputTable.getTableName()).append("\n") ;
    b.append(buildSelectSQLQuery()) ;
    return b.toString() ;
  }
  
  public String buildSelectSQLQuery() {
    SQLStringBuilder out = new SQLStringBuilder() ;
    out.println("SELECT") ;
    List<Field> fields = this.outputTable.getFields() ;
    for(int i = 0; i < fields.size(); i++) {
      Field field = fields.get(i) ;
      String eol = "," ;
      if(i == fields.size() - 1) eol = "" ;
      String expression = field.getExpression() ;
      if(expression == null || expression.length() == 0) expression = field.getMapFromExpression() ;
      out.println("   ", expression , eol);
    }
    queryTemplate.buildSQLFragment(out) ;
    return out.toString().trim(); 
  }
  
  static public class FromTableClause {
    SQLTable table ;
    String   alias ;
    
    public FromTableClause(SQLTable table, String alias) {
      this.table = table ;
      this.alias = alias ;
    }
    
    public SQLTable getTable() { return table; }
    public void setTable(SQLTable table) { this.table = table; }
    
    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }
  }
  
  static public class ConditionClause {
    String leftOperand ;
    String operator    ;
    String rightOperand ;
    
    public String getLeftOperand() { return leftOperand; }
    public void setLeftOperand(String leftOperand) { this.leftOperand = leftOperand; }
    
    public String getOperator() { return operator; }
    public void   setOperator(String operator) { this.operator = operator; }
    
    public String getRightOperand() { return rightOperand; }
    public void setRightOperand(String rightOperand) { this.rightOperand = rightOperand; }
  }
  
  static abstract public class QueryTemplate {
    abstract public void onSqlQueryChange() ;
    abstract public void buildSQLFragment(SQLStringBuilder out); 
  }
  
  static public class WhereTemplate extends QueryTemplate {
    List<ConditionClause>  clauses ;
    
    public void onSqlQueryChange() {
      
    }
    
    public void buildSQLFragment(SQLStringBuilder out) {
      
    }
  }
  
  public class JoinClause  {
    private FromTableClause fromTable ;
    private FromTableClause onTable ;
    private String condition ;
    
    public JoinClause(FromTableClause fromTable, FromTableClause onTable) {
      this.fromTable = fromTable ;
      this.onTable = onTable ;
    }
    
    public FromTableClause getFromTable() { return this.fromTable ; }
    
    public FromTableClause getOnTable() { return this.onTable ; }
  
    public String getCondition() { return this.condition ; }
    public void   setCondition(String condition) { 
      this.condition = condition ; 
    }
    
    public void buildSQLFragment(SQLStringBuilder out) {
      out.println("JOIN", onTable.getTable().getTableName(), "ON", "(") ;
      out.println("   ",condition) ;
      out.println(")") ;
    }
  }
  
  public class JoinTemplate extends QueryTemplate {
    FromTableClause  fromTable  ;
    List<JoinClause> joinClauses = new ArrayList<JoinClause>() ;
    
    public FromTableClause getFromTableClause() { return fromTable; }
    public void setFromTableClause(FromTableClause fromClause) { this.fromTable = fromClause; }
    
    public List<JoinClause> getJoinClauses() { return joinClauses; }
    public void setJoinClauses(List<JoinClause> joinClauses) { this.joinClauses = joinClauses; }
    
    public void onSqlQueryChange() {
      fromTable = null ;
      joinClauses.clear() ;
      if(inputTables.size() > 0) {
        fromTable = new FromTableClause(inputTables.get(0), null) ;
        for(int i = 1; i < inputTables.size(); i++) {
          SQLTable inputTable = inputTables.get(i) ;
          FromTableClause onTable = new FromTableClause(inputTable, null) ;
          JoinClause joinClause = new JoinClause(fromTable, onTable) ;
          joinClauses.add(joinClause) ;
        }
      }
    }
    
    public void buildSQLFragment(SQLStringBuilder out) {
      out.println("FROM", fromTable.table.getTableName()) ;
      for(JoinClause sel : joinClauses) {
        sel.buildSQLFragment(out);
      }
    }
    
  }
}