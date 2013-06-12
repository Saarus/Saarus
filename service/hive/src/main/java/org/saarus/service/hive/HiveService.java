package org.saarus.service.hive;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class HiveService  {
  private Connection connection ;

  private String url ;
  private String username ;
  private String password ;
  
  public HiveService() {

  }

  public HiveService(String url, String username, String password) throws Exception {
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
  
  public ResultSet executeQuerySQL(String sql) throws Exception {
    Statement stmt = null ;
    stmt = connection.createStatement();
    ResultSet res = stmt.executeQuery(sql);
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
  
  public String getUrl() { return url; }
  public void setUrl(String url) { this.url = url; }

  public String getUsername() { return username; }
  public void setUsername(String username) { this.username = username; }

  public String getPassword() { return password; }
  public void setPassword(String password) { this.password = password; }
}