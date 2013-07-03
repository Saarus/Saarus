package org.saarus.service.hadoop;

public class HadoopInfo {
  static public String MASTER_NODE = "hadoop1.saarus.org" ;
  static public String HDFS_URL = "hdfs://" + MASTER_NODE + ":8020" ;
  static public String HIVE_CONNECTION_URL = "jdbc:hive2://" + MASTER_NODE + ":10000" ;
  static public String IMPALA_CONNECTION_URL = "jdbc:hive2://hadoop2.saarus.org:21000" ;
}
