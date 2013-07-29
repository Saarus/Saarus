package org.saarus.service.sql.hive;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.fs.FileSystem;
import org.saarus.service.hadoop.util.FSResource;
import org.saarus.service.hadoop.util.HDFSUtil;
import org.saarus.service.sql.QueryResult;
import org.saarus.service.sql.SQLService;
import org.saarus.service.sql.TableMetadata;
import org.saarus.service.sql.io.JSONImporter;
import org.saarus.service.task.CallableTaskUnit;
import org.saarus.service.task.TaskUnit;
import org.saarus.service.task.TaskUnitHandler;
import org.saarus.service.task.TaskUnitResult;

public class HiveTaskHandler implements TaskUnitHandler {
  private SQLService sqlService ;
  
  public HiveTaskHandler() {

  }

  public HiveTaskHandler(SQLService sqlService) throws Exception {
    this.sqlService = sqlService ;
  }
  
  public String getName() { return "SQLService" ; }

  public SQLService getSqlService() { return this.sqlService ; }
  public void setSqlService(SQLService service) { this.sqlService = service ; }

  public CallableTaskUnit<?> getCallableTaskUnit(TaskUnit taskUnit) {
    String name = taskUnit.getName();
    if("executeQuery".equals(name))   return executeQuery(taskUnit) ;
    else if("execute".equals(name))   return execute(taskUnit) ;
    else if("dropTable".equals(name)) return dropTable(taskUnit) ;
    else if("listTable".equals(name)) return listTable(taskUnit) ;
    else if("descTable".equals(name)) return descTable(taskUnit);
    else if("descTables".equals(name)) return descTables(taskUnit);
    else if("insert".equals(name)) return insert(taskUnit);
    else if("importJson".equals(name)) return importJson(taskUnit);
    return null ;
  }

  private CallableTaskUnit<Boolean> dropTable(final TaskUnit tunit) {
    tunit.setTaskLine("drop table if exists " + tunit.getParameters().getString("tableName", null)) ;
    CallableTaskUnit<Boolean> callableUnit = new CallableTaskUnit<Boolean>(tunit, new TaskUnitResult<Boolean>()) {
      public Boolean doCall() throws Exception {
        return sqlService.executeSQL(tunit.getTaskLine()) ;
      }
    };
    return callableUnit ;
  }
  
  private CallableTaskUnit<TableMetadata> descTable(final TaskUnit tunit) {
    final String tableName = tunit.getParameters().getString("tableName", null) ;
    tunit.setTaskLine("describe " + tableName) ;
    CallableTaskUnit<TableMetadata> callableUnit = new CallableTaskUnit<TableMetadata>(tunit, new TaskUnitResult<TableMetadata>()) {
      public TableMetadata doCall() throws Exception {
        ResultSet res = sqlService.executeQuerySQL(taskUnit.getTaskLine());
        TableMetadata tinfo = null ;
        while (res.next()) {
          if(tinfo == null) tinfo = new TableMetadata(tableName) ;
          String name = res.getString(1) ;
          String type = res.getString(2);
          String comment = res.getString(3);
          tinfo.addField(name, type) ;
        }
        res.close() ;
        return tinfo ;
      }
    };
    return callableUnit ;
  }
  
  private CallableTaskUnit<TableMetadata[]> descTables(final TaskUnit tunit) {
    CallableTaskUnit<TableMetadata[]> callableUnit = new CallableTaskUnit<TableMetadata[]>(tunit, new TaskUnitResult<TableMetadata[]>()) {
      public TableMetadata[] doCall() throws Exception {
        String[] tables = tunit.getParameters().getStringArray("tableName", null) ;
        TableMetadata[] tinfo =  new TableMetadata[tables.length] ;
        for(int i = 0; i < tables.length; i++) {
          long start = System.currentTimeMillis() ;
          ResultSet res = sqlService.executeQuerySQL("DESCRIBE " + tables[i]);
          System.out.println("desc in " + (System.currentTimeMillis() - start) + "ms");
          tinfo[i] = new TableMetadata(tables[i]) ;
          while (res.next()) {
            String name    = res.getString(1) ;
            String type    = res.getString(2);
            String comment = res.getString(3);
            tinfo[i].addField(name, type) ;
          }
          res.close() ;
        }
        return tinfo ;
      }
    };
    return callableUnit ;
  }
  
  private CallableTaskUnit<Boolean> execute(final TaskUnit tunit) {
    CallableTaskUnit<Boolean> callableUnit = new CallableTaskUnit<Boolean>(tunit, new TaskUnitResult<Boolean>()) {
      public Boolean doCall() throws Exception {
        return sqlService.executeSQL(tunit.getTaskLine()) ;
      }
    };
    return callableUnit ;
  }
 
  private CallableTaskUnit<int[]> insert(final TaskUnit tunit) {
    CallableTaskUnit<int[]> callableUnit = new CallableTaskUnit<int[]>(tunit, new TaskUnitResult<int[]>()) {
      public int[] doCall() throws Exception {
        String insertSql = tunit.getTaskLine() ;
        List<Object[]> data = (List<Object[]>) tunit.getParameters().getObject("data") ;
        return sqlService.insert(insertSql, data) ;
      }
    };
    return callableUnit ;
  }
  
  private CallableTaskUnit<String> importJson(final TaskUnit tunit) {
    CallableTaskUnit<String> callableUnit = new CallableTaskUnit<String>(tunit, new TaskUnitResult<String>()) {
      public String doCall() throws Exception {
        FileSystem fs = HDFSUtil.getFileSystem() ;
        String jsonFile = tunit.getParameters().getString("jsonFile");
        String dbfile = tunit.getParameters().getString("dbfile");
        String[] properties = (String[]) tunit.getParameters().getStringArray("properties", new String[] {}) ; 
        JSONImporter.Progressable progressable = null ; //new JSONImporter.DebugProgressable() ;
        JSONImporter.Writer writer = new JSONImporter.RCWriter(fs, dbfile, properties, null) ;
        JSONImporter importer = new JSONImporter(writer, progressable) ;
        FSResource resource = FSResource.get(jsonFile) ;
        importer.doImport(resource.getInputStream(), properties) ;
        importer.close() ;
        return "Done" ;
      }
    };
    return callableUnit ;
  }
 
  
  private CallableTaskUnit<QueryResult> executeQuery(final TaskUnit tunit) {
    CallableTaskUnit<QueryResult> callableUnit = new CallableTaskUnit<QueryResult>(tunit, new TaskUnitResult<QueryResult>()) {
      public QueryResult doCall() throws Exception {
        ResultSet res = sqlService.executeQuerySQL(taskUnit.getTaskLine());
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
        return sqlService.listTables() ;
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

  public TaskUnitResult<TableMetadata> describeTable(String tname) throws Exception {
    TaskUnit task = new TaskUnit() ;
    task.setName("descTable") ;
    task.getParameters().setString("tableName", tname) ;
    return descTable(task).call() ;
  }
  
  public TaskUnitResult<TableMetadata[]> describeTables(String[] tname) throws Exception {
    TaskUnit task = new TaskUnit() ;
    task.setName("descTables") ;
    task.getParameters().setStringArray("tableName", tname) ;
    return descTables(task).call() ;
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
  
  public String toString() { return getName() ; }
}