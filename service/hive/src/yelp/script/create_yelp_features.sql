set yelp.db.location=/user/hive/yelpdb/warehouse ;

-- yelp features

DROP TABLE if exists features ;

CREATE TABLE features(     
     review_id      STRING,
     user_id        STRING,
     user_name      STRING,
     business_id    STRING,
     business_name  STRING,
     stars          FLOAT,
     vote_funny     INT,
     vote_useful    INT,
     vote_cool      INT)
  ROW FORMAT DELIMITED FIELDS TERMINATED BY "," ESCAPED BY '\\' STORED AS TEXTFILE
  LOCATION '${hiveconf:yelp.db.location}/features';

INSERT OVERWRITE TABLE features
  SELECT r.review_id, r.user_id, u.name, r.business_id, b.name, r.stars, 
         r.vote_funny, r.vote_useful, r.vote_cool
  FROM review r 
    JOIN user u ON(r.user_id = u.user_id)
    JOIN business b ON(r.business_id = b.business_id);
