package org.saarus.service.mongodb;

public class Yelp {
  static public void main(String[] args) throws Exception {
    String baseDataDir     = "d:/projects/saarus/yelp/yelp_training_set" ;
    String userSetFile     = baseDataDir + "/yelp_training_set_user.json" ;
    String reviewSetFile   = baseDataDir + "/yelp_training_set_review.json" ;
    String checkinSetFile  = baseDataDir + "/yelp_training_set_checkin.json" ;
    String busineseSetFile = baseDataDir + "/yelp_training_set_business.json" ;
    
    MongoDBDBHelper dbHelper = new MongoDBDBHelper() ;
    dbHelper.deleteDb("yelpdb") ;
    dbHelper.insert("yelpdb", "user", userSetFile) ;
    dbHelper.insert("yelpdb", "businese", busineseSetFile) ;
    dbHelper.insert("yelpdb", "checkin",  checkinSetFile) ;
    dbHelper.insert("yelpdb", "review",   reviewSetFile) ;
    
    //dbHelper.createStatistics("yelpdb", "user") ;
    //dbHelper.createStatistics("yelpdb", "businese") ;
    //dbHelper.createStatistics("yelpdb", "checkin") ;
    //dbHelper.createStatistics("yelpdb", "review") ;
  }
}
