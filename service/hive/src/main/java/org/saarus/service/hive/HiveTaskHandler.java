package org.saarus.service.hive;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

import org.saarus.service.task.CallableTaskUnit;
import org.saarus.service.task.TaskUnit;
import org.saarus.service.task.TaskUnitHandler;
import org.saarus.service.task.TaskUnitResult;

public class HiveTaskHandler implements TaskUnitHandler {
  private HiveService hservice ;
  
  public HiveTaskHandler() {

  }

  public HiveTaskHandler(HiveService hservice) throws Exception {
    this.hservice = hservice ;
  }
  
  public String getName() { return "HiveService" ; }

  public HiveService getHiveService() { return this.hservice ; }
  public void setHiveService(HiveService hservice) { this.hservice = hservice ; }

  public CallableTaskUnit<?> getCallableTaskUnit(TaskUnit taskUnit) {
    String name = taskUnit.getName();
    if("executeQuery".equals(name))   return executeQuery(taskUnit) ;
    else if("execute".equals(name))   return execute(taskUnit) ;
    else if("dropTable".equals(name)) return dropTable(taskUnit) ;
    else if("listTable".equals(name)) return listTable(taskUnit) ;
    else if("descTable".equals(name)) return descTable(taskUnit);
    return null ;
  }

  private CallableTaskUnit<Boolean> dropTable(final TaskUnit tunit) {
    tunit.setTaskLine("drop table if exists " + tunit.getParameters().getString("tableName", null)) ;
    CallableTaskUnit<Boolean> callableUnit = new CallableTaskUnit<Boolean>(tunit, new TaskUnitResult<Boolean>()) {
      public Boolean doCall() throws Exception {
        return hservice.executeSQL(tunit.getTaskLine()) ;
      }
    };
    return callableUnit ;
  }
  
  private CallableTaskUnit<TableMetadata> descTable(final TaskUnit tunit) {
    final String tableName = tunit.getParameters().getString("tableName", null) ;
    tunit.setTaskLine("describe " + tableName) ;
    CallableTaskUnit<TableMetadata> callableUnit = new CallableTaskUnit<TableMetadata>(tunit, new TaskUnitResult<TableMetadata>()) {
      public TableMetadata doCall() throws Exception {
        ResultSet res = hservice.executeQuerySQL(taskUnit.getTaskLine());
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
    };
    return callableUnit ;
  }
  
  private CallableTaskUnit<Boolean> execute(final TaskUnit tunit) {
    CallableTaskUnit<Boolean> callableUnit = new CallableTaskUnit<Boolean>(tunit, new TaskUnitResult<Boolean>()) {
      public Boolean doCall() throws Exception {
        return hservice.executeSQL(tunit.getTaskLine()) ;
      }
    };
    return callableUnit ;
  }
  
  private CallableTaskUnit<QueryResult> executeQuery(final TaskUnit tunit) {
    CallableTaskUnit<QueryResult> callableUnit = new CallableTaskUnit<QueryResult>(tunit, new TaskUnitResult<QueryResult>()) {
      public QueryResult doCall() throws Exception {
        ResultSet res = hservice.executeQuerySQL(taskUnit.getTaskLine());
        ResultSetMetaData rsmd = res.getMetaData() ;
        int columnCount = rsmd.getColumnCount() ;
        String[] columnNames = new String[columnCount];
        for(int i = 0; i < columnCount; i++) {
          columnNames[i] = rsmd.getColumnName(i + 1) ;
        }
        List<Object[]> rows = new ArrayList<Object[]>() ;
        while(res.next()) {
          Object[] row = new Object[columnCount] ;
          for(int i = 0; i < columnCount; i++) {
            row[i] = res.getObject(i + 1) ;
          }
          rows.add(row) ;
        }
        res.close() ;
        QueryResult qresult = new QueryResult() ;
        qresult.setQuery(taskUnit.getTaskLine()) ;
        qresult.setColumn(columnNames) ;
        Object[][] data = rows.toArray(new Object[rows.size()][]) ;
        qresult.setData(data) ;
        return  qresult ;
      }
    };
    return callableUnit ;
  }
  
  
  private CallableTaskUnit<List<String>> listTable(final TaskUnit tunit) {
    tunit.setTaskLine("SHOW TABLES") ;
    CallableTaskUnit<List<String>> callableUnit = new CallableTaskUnit<List<String>>(tunit, new TaskUnitResult<List<String>>()) {
      public List<String> doCall() throws Exception {
        return hservice.listTables() ;
      }
    };
    return callableUnit ;
  }
  
  
  public TaskUnitResult<Boolean> dropTable(String tname) throws Exception {
    TaskUnit task = new TaskUnit() ;
    task.setName("dropTable") ;
    task.getParameters().setString("tableName", tname) ;
    return dropTable(task).call() ;
  }

  public TaskUnitResult<TableMetadata> describeTable(final String tname) throws Exception {
    TaskUnit task = new TaskUnit() ;
    task.setName("describeTable") ;
    task.getParameters().setString("tableName", tname) ;
    return descTable(task).call() ;
  }

  public TaskUnitResult<List<String>> listTables() throws Exception {
    TaskUnit task = new TaskUnit() ;
    task.setName("listTables") ;
    return listTable(task).call() ;
  }

  public TaskUnitResult<Boolean> execute(String sql) throws Exception {
    TaskUnit task = new TaskUnit() ;
    task.setTaskLine(sql) ;
    return execute(task).call() ;
  }

  public TaskUnitResult<QueryResult> executeQuery(String sql) throws Exception {
    TaskUnit task = new TaskUnit() ;
    task.setTaskLine(sql) ;
    return executeQuery(task).call() ;
  }
}