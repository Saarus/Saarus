package org.saarus.mahout.classifier.sgd;

import org.junit.Test;

public class MRLogisticRegressionDecoderUnitTest {
  @Test
  public void test() throws Exception {
    MRLogisticRegressionDecoder decoder = new MRLogisticRegressionDecoder() ;
    //  decoder.
    //    setInputUri("src/test/resources/donutmr").
    //    setOutputUri("target/output").
    //    setModelUri("dfs:/tmp/donut.model").
    //    setColumnHeaders("x,y,shape,color,xx,xy,yy,c,a,b".split(",")).
    //    setClusterMode(true) ;
    //  decoder.run() ;
    decoder.
      setInputUri("/user/hive/donutdb/test").
      setOutputUri("/tmp/donut/output").
      setModelUri("dfs:/tmp/donut.model").
      setColumnHeaders("x,y,shape,color,xx,xy,yy,c,a,b".split(",")).
      setClusterMode(true) ;
    decoder.run() ;
  }
}
