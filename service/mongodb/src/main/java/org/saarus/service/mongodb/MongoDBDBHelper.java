package org.saarus.service.mongodb;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MapReduceCommand;
import com.mongodb.MapReduceOutput;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;

public class MongoDBDBHelper {
  final static public String SAARUS_STATISTICS = "saarus_statistics" ;
  
  private MongoClient mongoClient ;
  
  public MongoDBDBHelper() throws Exception {
    this("localhost" , 27017 ) ;
  }
  
  public MongoDBDBHelper(String host, int port) throws Exception {
    mongoClient = new MongoClient("localhost" , 27017 );
    mongoClient.setWriteConcern(WriteConcern.ACKNOWLEDGED);
    
  }
  
  public void insert(String dbName, String collName, String jsonFile) throws Exception {
    JSONReader reader = new JSONReader(jsonFile) ;
    DB db = mongoClient.getDB(dbName);
    DBCollection coll = db.getCollection(collName);
    int count = 0 ;
    List<DBObject> holder = new ArrayList<DBObject>() ;
    while(reader.hasNext()) {
      DBObject obj = reader.next() ;
      holder.add(obj) ;
      count++ ;
      if(count % 500 == 0) {
        coll.insert(holder);
        holder.clear() ;
        System.out.println("Insert into db " + dbName + ", collection " + collName + ": " + count) ;
      }
    }
    if(holder.size() > 0) {
      coll.insert(holder);
      holder.clear() ;
    }
    System.out.println("Insert into db " + dbName + ", collection " + collName + ": " + count) ;
    reader.close() ;
  }
  
  public void deleteDb(String name) throws Exception {
    mongoClient.dropDatabase(name);
  }

  public void deleteCollection(String dbName, String name) throws Exception {
    mongoClient.getDB(dbName).getCollection(name).drop() ;
  }

  public void createStatistics(String dbName) throws Exception {
    DB db = mongoClient.getDB(dbName) ;
    Set<String> collectionNames = db.getCollectionNames() ;
    for(String name : collectionNames) {
      if(SAARUS_STATISTICS.equalsIgnoreCase(name)) continue ;
    }
  }
  
  public void createStatistics(String dbName, String collName) throws Exception {
    String map = 
        "function(){" +
        "  for(key in this) { " +
        "    if(key.startsWith('_')) continue ;" +
        "    var val = this[key]; " +
        "    if(val != null && val != 0) {" +
        "      emit(key, { count: 1 });" +
        "    }" +
        "  }" +
        "  emit('_recordCount', { count: 1});" +
        "};";
    
    String reduce = 
        "function(key , values ) {" +
        "  var field = { 'count': 0 };" +
        "  for ( var i = 0; i < values.length; i ++ ) {" +
        "    field.count += values[i].count ;" +
        "  };" +
        "  return field;" +
        "};" ;
        
    DB db = mongoClient.getDB(dbName) ;
    DBCollection coll = db.getCollection(collName) ;
    MapReduceCommand cmd = 
        new MapReduceCommand(coll, map, reduce, null, MapReduceCommand.OutputType.INLINE, null);
    MapReduceOutput out = coll.mapReduce(cmd);
    DBCollection statisticColl = db.getCollection(SAARUS_STATISTICS) ;
    BasicDBObject stats = new BasicDBObject("_id", collName);
    System.out.println("\nStatistics for " + collName + "\n") ;
    for ( DBObject obj : out.results() ) {
      String fieldName = (String) obj.get("_id") ;
      DBObject value = (DBObject)obj.get("value") ;
      BasicDBObject field = new BasicDBObject("count", value.get("count"));
      stats.append(fieldName, field) ;
    }
    statisticColl.insert(stats) ;
    System.out.println(stats);
  }
  
  public DBCursor query(String dbName, String collName, DBObject query) throws Exception {
    DB db = mongoClient.getDB(dbName);
    DBCollection coll = db.getCollection(collName);
    
    return coll.find(query) ;
  }
  
  public void close() throws Exception {
    mongoClient.close() ;
  }
}
