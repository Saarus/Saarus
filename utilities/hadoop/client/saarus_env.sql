ADD JAR '/opt/saarus/lib//trove4j-3.0.3.jar' ;
ADD JAR '/opt/saarus/lib//stanford-corenlp-1.3.5.jar' ;
ADD JAR '/opt/saarus/lib//lucene-snowball-3.0.3.jar' ;
ADD JAR '/opt/saarus/lib//liblinear-1.92.jar' ;
ADD JAR '/opt/saarus/lib//saarus.lib.common-1.0.jar' ;
ADD JAR '/opt/saarus/lib//saarus.lib.nlp.core-1.0.jar' ;
ADD JAR '/opt/saarus/lib//saarus.lib.nlp.classify-1.0.jar' ;
ADD JAR '/opt/saarus/lib//saarus.service.hadoop-1.0.jar' ;
ADD JAR '/opt/saarus/lib//saarus.service.nlp-1.0.jar' ;

CREATE FUNCTION nlp_classify AS 'org.saarus.service.nlp.hive.udf.UDFLiblinearTextClassify'
