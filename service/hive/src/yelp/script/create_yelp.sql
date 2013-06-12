set yelp.db.location=/user/hive/yelpdb/warehouse ;

-- user table
DROP TABLE if exists user ;

CREATE TABLE user (     
     user_id        STRING,
     name           STRING,
     average_stars  FLOAT,
     review_count   INT,
     vote_funny     INT,
     vote_useful    INT,
     vote_cool      INT)
  ROW FORMAT DELIMITED FIELDS TERMINATED BY "," ESCAPED BY '\\' STORED AS TEXTFILE
  LOCATION '${hiveconf:yelp.db.location}/user';

INSERT OVERWRITE TABLE user 
  SELECT u.user_id, u.name, u.average_stars, u.review_count, v.funny, v.useful, v.cool
    FROM user_json 
    LATERAL VIEW json_tuple(
                   user_json.json, 'user_id', 'name', 'average_stars', 
                   'review_count', 'vote') 
      u AS user_id, name, average_stars, review_count, vote  
      LATERAL VIEW json_tuple(u.vote, 'funny', 'useful', 'cool') v AS funny, useful, cool;


-- business

DROP TABLE if exists business ;

CREATE TABLE business(
    business_id        STRING,
    name               STRING,
    full_address       STRING,
    city               STRING,
    state              STRING,
    longitude          DOUBLE,
    latitude           DOUBLE,
    categories         STRING,
    review_count       INT,
    stars              FLOAT,
    neighborhoods      STRING)
  ROW FORMAT DELIMITED FIELDS TERMINATED BY "," ESCAPED BY '\\' STORED AS TEXTFILE
  LOCATION '${hiveconf:yelp.db.location}/business';

INSERT OVERWRITE TABLE business   
  SELECT b.business_id, b.name, REGEXP_REPLACE(b.full_address,"\n", "\\n"), b.city, b.state,
         b.longitude, b.latitude, b.categories, b.review_count, b.stars, b.neighborhoods
  FROM business_json 
  LATERAL VIEW json_tuple(business_json.json, 'business_id', 'name', 'full_address', 'city', 'state',
                         'longitude', 'latitude', 'categories', 'review_count', 'stars', 'neighborhoods')
    b AS business_id, name, full_address, city, state, longitude, latitude, categories, 
         review_count, stars, neighborhoods ;

-- checkin

DROP TABLE if exists checkin ;
CREATE TABLE checkin(
     business_id          STRING,
     checkin_info         STRING)   
  ROW FORMAT DELIMITED FIELDS TERMINATED BY "," ESCAPED BY '\\' STORED AS TEXTFILE
  LOCATION '${hiveconf:yelp.db.location}/checkin';


INSERT OVERWRITE TABLE checkin
   SELECT c.business_id, c.checkin_info     
   FROM checkin_json
   LATERAL VIEW json_tuple(checkin_json.json, 'business_id', 'checkin_info')
     c AS business_id, checkin_info;

-- review

DROP TABLE if exists review ;

CREATE TABLE review(     
     review_id      STRING,
     business_id    STRING,
     user_id        STRING,
     stars          FLOAT,
     text           STRING,
     `date`         STRING,
     vote_funny     INT,
     vote_useful    INT,
     vote_cool      INT)
  ROW FORMAT DELIMITED FIELDS TERMINATED BY "," ESCAPED BY '\\' STORED AS TEXTFILE
  LOCATION '${hiveconf:yelp.db.location}/review';

INSERT OVERWRITE TABLE review
   SELECT r.review_id, r.business_id, r.user_id, r.stars, REGEXP_REPLACE(r.text, "\n|\r", "\\n"),
          r.review_date, v.funny, v.useful, v.cool
   FROM review_json
   LATERAL VIEW json_tuple(review_json.json, 'review_id', 'business_id','user_id', 'stars',
                           'text', 'date', 'votes')
       r AS review_id, business_id, user_id, stars, text, review_date, votes
       LATERAL VIEW json_tuple(r.votes, 'funny', 'useful', 'cool') v AS funny, useful, cool ;

-- SELECT r.review_id, r.user_id, u.name, r.stars, r.vote_useful FROM review r JOIN user u ON(r.user_id = u.user_id) ;
