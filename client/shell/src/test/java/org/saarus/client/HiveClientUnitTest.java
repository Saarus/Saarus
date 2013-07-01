package org.saarus.client;

import org.junit.Assert;
import org.junit.Test;
import org.saarus.service.sql.TableMetadata;
import org.saarus.service.util.JSONSerializer;

public class HiveClientUnitTest {
  @Test
  public void test() throws Exception {
    ClientContext clientContext = new ClientContext() ;
    RESTClient restClient = clientContext.getBean(RESTClient.class) ;
    HiveClient client = restClient.getHiveClient() ;
    TableMetadata tmeta = client.descTable("user", true) ;
    Assert.assertNotNull(tmeta) ;
    System.out.println(JSONSerializer.JSON_SERIALIZER.toString(tmeta));
  }
}
