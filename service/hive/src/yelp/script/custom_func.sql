ADD jar /home/ubuntu/yelp/saarus.service.hive-1.0.jar ;

CREATE TEMPORARY FUNCTION field_stat AS 'org.saarus.service.sql.hive.UDAFFieldStat';

DESCRIBE FUNCTION EXTENDED field_stat ;

EXPLAIN
  SELECT field_stat(user_name) AS user_name, field_stat(business_name) AS business_name FROM features ;

SELECT field_stat(user_name) AS user_name, field_stat(business_name) AS business_name FROM features ;

DROP TEMPORARY FUNCTION field_stat ;

