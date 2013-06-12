set yelp.db.location=/user/hive/yelpdb/json/training ;
-- user table
DROP TABLE if exists user_json ;
CREATE EXTERNAL TABLE user_json (json STRING) 
  LOCATION '${hiveconf:yelp.db.location}/user';


-- business table
DROP TABLE if exists business_json ;
CREATE EXTERNAL TABLE business_json (json STRING) 
  LOCATION '${hiveconf:yelp.db.location}/business';

-- checkin table
DROP TABLE if exists checkin_json;
CREATE EXTERNAL TABLE checkin_json (json STRING) 
  LOCATION '${hiveconf:yelp.db.location}/checkin';

-- review table
DROP TABLE if exists review_json;
CREATE EXTERNAL TABLE review_json (json STRING) 
  LOCATION '${hiveconf:yelp.db.location}/review';

