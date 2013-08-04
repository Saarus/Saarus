package org.saarus.service.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SQLService  {
  private Connection connection ;

  private String url ;
  private String username ;
  private String password ;
  
  public SQLService() {

  }

  public SQLService(String url, String username, String password) throws Exception {
    this.url = url ;
    this.username = username ;
    this.password = password ;
    onInit() ;
  }
  
  public void onInit() throws Exception {
    connect(url, username, password) ;
  }
  
  public void connect(String url, String username, String password) throws Exception {
    Class.forName("org.apache.hive.jdbc.HiveDriver");
    connection = DriverManager.getConnection(url, username, password);
  }

  public boolean executeSQL(String sql) throws Exception {
    Statement stmt = null ;
    try {
      stmt = connection.createStatement();
      boolean ret = stmt.execute(sql);
      return ret ;
    } finally {
      if(stmt != null) stmt.close();
    }
  }
  
  public void addJar(String ... jarLoc) throws Exception {
    Statement stmt = null ;
    try {
      stmt = connection.createStatement();
      for(String selLoc : jarLoc) {
        boolean ret = stmt.execute("ADD jar " + selLoc);
      }
    } finally {
      if(stmt != null) stmt.close();
    }
  }
  
  public int[] insert(String sql, List<Object[]> paramHolder) throws Exception {
    PreparedStatement ps = connection.prepareStatement(sql);
    for(int i = 0; i < paramHolder.size(); i++) {
      Object[] param = paramHolder.get(i) ;
      for(int j = 0; j < param.length; i++) {
        if(param[0] instanceof String) ps.setNString(j + 1, (String)param[0]);
        else if(param[0] instanceof Integer) ps.setInt(j + 1, (Integer)param[0]);
        else if(param[0] instanceof Long) ps.setLong(j + 1, (Long)param[0]);
        else if(param[0] instanceof Float) ps.setFloat(j + 1, (Float)param[0]);
        else if(param[0] instanceof Double) ps.setDouble(j + 1, (Double)param[0]);
        else throw new Exception("Type " + param[0].getClass() + " is not supported") ;
      }
      ps.addBatch() ;
    }
    int[] ret = ps.executeBatch();
    ps.close() ;
    return ret ;
  }

  
  public ResultSet executeQuerySQL(String sql) throws Exception {
    Statement stmt = null ;
    stmt = connection.createStatement();
    ResultSet res = stmt.executeQuery(sql);
    res.setFetchSize(500) ;
    return res ;
  }

  public boolean dropTable(String tableName) throws Exception {
    return this.executeSQL("drop table if exists " + tableName) ;
  }
  
  public List<String> listTables() throws Exception {
    ResultSet res = executeQuerySQL("SHOW TABLES");
    ArrayList<String> holder = new ArrayList<String>() ;
    while(res.next()) {
      holder.add(res.getString(1));
    }
    res.close() ;
    return holder ;
  }
  
  public TableMetadata descTable(String tableName) throws Exception {
    ResultSet res = executeQuerySQL("DESCRIBE " + tableName);
    TableMetadata tinfo = null ;
    while (res.next()) {
      if(tinfo == null) tinfo = new TableMetadata(tableName) ;
      String name = res.getString(1) ;
      String type = res.getString(2);
      tinfo.addField(name, type) ;
    }
    res.close() ;
    return tinfo ;
  }
  
  public boolean createTable(TableMetadata tmeta, String location) throws Exception {
    return executeSQL(tmeta.createTableSQL(location)) ;
  }
  
  public String getUrl() { return url; }
  public void setUrl(String url) { this.url = url; }

  public String getUsername() { return username; }
  public void setUsername(String username) { this.username = username; }

  public String getPassword() { return password; }
  public void setPassword(String password) { this.password = password; }
}