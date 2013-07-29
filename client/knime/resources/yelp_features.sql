--features
DROP TABLE if exists features ;

CREATE TABLE features(     
     stars                  INT,
     text                   INT,
     vote_funny             INT,
     vote_useful            INT,
     vote_cool              INT,
     percentage_useful      FLOAT,
     cat_useful             INT,
     business_id            STRING,
     business_city          STRING,
     business_state         STRING,
     business_open          BOOLEAN,
     business_review_count  INT,
     business_stars         INT,
     user_review_count      INT,
     user_average_stars     INT,
     user_vote_funny        INT,
     user_vote_useful       INT,
     user_vote_cool         INT)
     ROW FORMAT DELIMITED FIELDS TERMINATED BY "," ESCAPED BY '\\' STORED AS TEXTFILE ;

INSERT OVERWRITE TABLE features
  SELECT r.stars, IF(r.text != null, 1, 0), r.vote_funny, r.vote_useful, r.vote_cool, 
         IF(r.vote_useful > 0, r.vote_useful/(r.vote_useful + r.vote_funny + r.vote_cool), 0),
         IF(r.vote_useful > 0 AND r.vote_useful/(r.vote_useful + r.vote_funny + r.vote_cool) > 0.4999, 1, 0),
         b.business_id, b.city, b.state, b.open, b.review_count, b.stars,
         u.review_count, u.average_stars, u.vote_funny, u.vote_useful, u.vote_cool
  FROM review r 
    JOIN business b ON(r.business_id = b.business_id)
    JOIN user u     ON(r.user_id = u.user_id);
