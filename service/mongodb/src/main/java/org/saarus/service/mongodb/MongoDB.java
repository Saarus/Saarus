package org.saarus.service.mongodb;

import java.util.Set;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;
import com.mongodb.MapReduceCommand;
import com.mongodb.MapReduceOutput;

public class MongoDB {
  static public void main(String[] args) throws Exception {
    // To directly connect to a single MongoDB server (note that this will not auto-discover the primary even
    // if it's a member of a replica set:
    // or
    MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
    mongoClient.setWriteConcern(WriteConcern.JOURNALED);
    
    DB db = mongoClient.getDB("yelpdb");
    //boolean auth = db.authenticate("username", "password".toCharArray());
    //System.out.println("auth = " + auth);
    
    DBCollection coll = db.getCollection("testCollection");
    coll.drop() ;
    for(int i = 0; i < 10; i ++) {
      BasicDBObject doc = new BasicDBObject("name", "test " + i).
          append("type", "database").
          append("number", i + 1).
          append("info", new BasicDBObject("x", 203).append("y", 102));
      coll.insert(doc);
    }
    

    coll = db.getCollection("testCollection");
    DBObject myDoc = coll.findOne();
    System.out.println(myDoc);
    
    
    BasicDBObject query = new BasicDBObject("name", "test 0");

    System.out.println("Query: ") ;
    DBCursor cursor = coll.find(query);
    
    try {
      int count = 0 ;
       while(cursor.hasNext()) {
         count++ ;  
         System.out.println(count + ". " + cursor.next());
       }
    } finally {
       cursor.close();
    }
    
    System.out.println("\nMap Reduce: \n");
    String map = 
        "function(){" +
        "  emit(this.type, {count: 1, sum: this.number});" +
        "};";
    
    String reduce = 
        "function(key , values ){" +
        "  var n = { count: 0, sum: 0}; " +
        "  for ( var i = 0; i < values.length; i ++ ) {" +
        "    n.sum += values[i].sum;" +
        "    n.count += values[i].count;" +
        "  };" +
        "  return n;" +
        "};";
    
    MapReduceCommand cmd = 
        new MapReduceCommand(coll, map, reduce, null, MapReduceCommand.OutputType.INLINE, null);
    MapReduceOutput out = coll.mapReduce(cmd);
    for ( DBObject obj : out.results() ) {
      System.out.println( obj );
    }
    
    System.out.println("-----------------------------------------------------------");
    //yagnus
    map = 
      "function() {" +
      "  print('\\nCall in map.....................'); " +
      "  emit(this.name, {us:[1], ms:[[1]], ds:[1]}) ; " +
      "}";
    //reduce
    reduce = 
      "function(k,vs) {" +
      "  var us =  oy.UVar();" +
      "  var ms =  oy.MVar();" +
      "  var ds =  oy.DVar();" +
      "  vs.forEach(function(v) {" +
      "    us.inc(v.us) ;" +
      "    ms.inc(v.ms) ;" +
      "    ds.inc(v.ds) ;" +
      "  });" +
      "  return {'us': us, 'ms': ms, 'vs': vs};" +
      "}" ;
    
    //options
    String options =
      "{" +
      "  finalize:  function(k,v) {" + 
      "    print('call in options..........................');" +
      "    /* v.us.calc();v.ms.calc();v.ds.calc() only if this table won't be reduced into again. */" +
      "    return v;" +
      "  }," +
      "  out: {reduce:  'statistics_table'}, " +
      "  query:  ord_date: { $gt: new Date('01/01/2013')}," + 
      "  /* scope:   {'org':org, 'oy':oy'} only if these were not installed in system.js*/" +
      "}" ;

    cmd = new MapReduceCommand(coll, map, reduce, null, MapReduceCommand.OutputType.INLINE, null);
    out = coll.mapReduce(cmd);
    
    System.out.println("\n Statistic MapReduce: \n");
    for ( DBObject obj : out.results() ) {
      System.out.println( obj );
    }
    
    System.out.println("\nCollection: ");
    Set<String> colls = db.getCollectionNames();
    for (String s : colls) {
        System.out.println( "  " + s );
    }
    
    db.eval("print('\\nThis message should go to the server side console or log\\n');", new Object[] {}) ;
    //coll.drop();
    //db.dropDatabase() ;
    mongoClient.close() ;
  }
}
