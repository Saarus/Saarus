package org.saarus.service.mongodb;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;


import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

public class YelpMLData {
  private void copyField(String[] field, DBObject src, BasicDBObject dest) {
    if(field == null) {
      //copy all
      Iterator<String> keys = src.keySet().iterator() ;
      while(keys.hasNext()) {
        String key = keys.next() ;
        dest.append(key, src.get(key)) ;
      }
    } else {
      for(String key : field) {
        dest.append(key, src.get(key)) ;
      }
    }
  }
  
  
  void merge(Collection<DBObject> coll, Map<String, DBObject> map, String matchField, String[] mergeField) {
    Iterator<DBObject> i = coll.iterator() ;
    int missCount = 0;
    while(i.hasNext()) {
      BasicDBObject obj = (BasicDBObject) i.next() ;
      String matchFieldValue = (String) obj.get(matchField) ;
      if(matchFieldValue != null) {
        DBObject obj2 = map.get(matchFieldValue) ;
        if(obj2 == null) {
          missCount++ ;
          i.remove();
        } else {
          copyField(mergeField, obj2, obj) ;
        }
      }
    }
    System.out.println("could not find " + missCount + " record with field " + matchField + " to merge") ;
  }
  
  Map<String, DBObject> index(Collection<DBObject> collection, String field) {
    SortedMap<String, DBObject> index = new TreeMap<String, DBObject>() ;
    Iterator<DBObject> i = collection.iterator() ;
    while(i.hasNext()) {
      DBObject dbobj = i.next() ;
      String value = (String) dbobj.get(field) ;
      if(index.containsKey(value)) {
        throw new RuntimeException() ;
      }
      index.put(value, dbobj) ;
    }
    return index ;
  }
  
  public void dump(Collection<DBObject> coll, int limit) {
    Iterator<DBObject> i = coll.iterator() ;
    int count = 0 ;
    while(i.hasNext() && count < limit) {
      System.out.println(JSON.serialize(i.next())) ;
      count++ ;
    }
  }
  
  public Collection<DBObject> readJSONFile(String file) throws Exception {
    JSONReader reader = new JSONReader(file) ;
    return readJSONFile(reader)  ;
  }
  
  public Collection<DBObject> readJSONFile(JSONReader reader) throws Exception {
    int count = 0 ;
    List<DBObject> holder = new ArrayList<DBObject>() ;
    while(reader.hasNext()) {
      DBObject obj = reader.next() ;
      holder.add(obj) ;
      count++ ;
      if(count % 50000 == 0) {
        System.out.println("Resource " + reader.getSource() + ": " + count + " records");
      }
    }
    System.out.println("Resource " + reader.getSource() + ": " + count + " records \n");
    return holder  ;
  }

  public void saveCSV(String file, Collection<DBObject> coll, String[] field) throws Exception {
    BufferedWriter w = new BufferedWriter(new FileWriter(file));
    for(int i = 0; i < field.length; i++) {
      if(i > 0) w.append(',') ;
      w.append(field[i]) ;
    }
    Iterator<DBObject> objItr = coll.iterator() ;
    while(objItr.hasNext()) {
      w.append('\n') ;
      DBObject obj = objItr.next() ;
      for(int i = 0; i < field.length; i++) {
        if(i > 0) w.append(",") ;
        String val = "" ;
        Object fval = obj.get(field[i]) ;
        if(fval != null) val = fval.toString() ;
        w.append(val) ;
      }
    }
    w.close() ;
  }
  
  static Object getObjectProperty(DBObject o, String s) {
    String[] a = s.split("\\.");
    int idx = 0 ;
    while(idx < a.length) {
      String n = a[idx];
      Object r = o.get(n) ;
      idx++ ;
      if(idx == a.length) return r ;
      if(r == null) return null ;
      o = (DBObject) r ;
    }
    return null;
  }
  
  static public void main(String[] args) throws Exception {
    String baseDataDir     = "d:/projects/saarus/yelp/yelp_test_set" ;
    String userSetFile     = baseDataDir + "/yelp_test_set_user.json" ;
    String reviewSetFile   = baseDataDir + "/yelp_test_set_review.json" ;
    String checkinSetFile  = baseDataDir + "/yelp_test_set_checkin.json" ;
    String busineseSetFile = baseDataDir + "/yelp_test_set_business.json" ;
    
    YelpMLData mlData = new YelpMLData() ;
    JSONReader reviewReader = new JSONReader(reviewSetFile) {
      public  DBObject next() throws Exception {
        DBObject obj = super.next() ;
        BasicDBObject result = new BasicDBObject() ;
        result.append("review_id", obj.get("review_id")) ;
        result.append("user_id", obj.get("user_id")) ;
        result.append("business_id", obj.get("business_id")) ;
        result.append("stars", obj.get("stars")) ;
        if(obj.get("text") != null) {
          result.append("text", 1) ;
        } else {
          result.append("text", 0) ;
        }
        result.append("vote_funny", getObjectProperty(obj, "votes.funny")) ;
        result.append("vote_useful", getObjectProperty(obj, "votes.useful")) ;
        result.append("vote_cool", getObjectProperty(obj, "votes.cool")) ;
        return result ;
      }
    };
    Collection<DBObject> reviewColl = mlData.readJSONFile(reviewReader) ;
    
    JSONReader userReader = new JSONReader(userSetFile) {
      public  DBObject next() throws Exception {
        DBObject obj = super.next() ;
        BasicDBObject result = new BasicDBObject() ;
        result.append("user_id", obj.get("user_id")) ;
        result.append("user_review_count", obj.get("review_count")) ;
        result.append("user_average_stars", obj.get("average_stars")) ;
        result.append("user_vote_funny", getObjectProperty(obj, "votes.funny")) ;
        result.append("user_vote_useful", getObjectProperty(obj, "votes.useful")) ;
        result.append("user_vote_cool", getObjectProperty(obj, "votes.cool")) ;
        return result ;
      }
    } ;
    
    Collection<DBObject> userColl = mlData.readJSONFile(userReader) ;
    String[] userMergeField = {"user_review_count", "user_average_stars", "user_vote_funny", "user_vote_useful", "user_vote_cool" } ;
    mlData.merge(reviewColl, mlData.index(userColl, "user_id"), "user_id", userMergeField) ;
    
    JSONReader businessReader = new JSONReader(busineseSetFile) {
      public  DBObject next() throws Exception {
        DBObject obj = super.next() ;
        BasicDBObject result = new BasicDBObject() ;
        result.append("business_id", obj.get("business_id")) ;
        result.append("business_review_count", obj.get("review_count")) ;
        result.append("business_stars", obj.get("stars")) ;
        result.append("business_city", obj.get("city")) ;
        result.append("business_state", obj.get("state")) ;
        result.append("business_open", obj.get("open")) ;
        return result ;
      }
    } ;
    
    Collection<DBObject> businessColl = mlData.readJSONFile(businessReader) ;
    String[] businessMergeField = {
      "business_city", "business_state", "business_open", "business_review_count", "business_stars"
    } ;
    mlData.merge(reviewColl, mlData.index(businessColl, "business_id"), "business_id", businessMergeField) ;
    
    String[] saveField = {
      /*"review_id", */"stars", "text", "vote_funny", "vote_useful", "vote_cool",
      "business_id", "business_city", "business_state", "business_open", "business_review_count", "business_stars",
      /*"user_id",*/ "user_review_count", "user_average_stars"/*, "user_vote_funny", "user_vote_useful", "user_vote_cool"*/
    } ;
    mlData.saveCSV("target/review-test.csv", reviewColl, saveField) ;
    mlData.dump(reviewColl, 3) ;
    
  }
}