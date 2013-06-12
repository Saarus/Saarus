package org.saarus.service.remote.hive;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.saarus.service.hive.TableMetadata;
import org.saarus.service.remote.ServiceDescription;
import org.saarus.service.task.TaskUnitResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/test-beans.xml" })
public class HiveControllerUnitTest {
  @Autowired
  @Qualifier("restTemplate")
  private RestTemplate restTemplate;

  @Test
  public void testMethodCall() {
    ServiceDescription serviceDesc = 
      restTemplate.getForObject("http://localhost:7080/hive/help.json", ServiceDescription.class);
    Assert.assertNotNull(serviceDesc);
    
    TaskUnitResult<List<String>> listResult = 
      restTemplate.getForObject("http://localhost:7080/hive/table/list?forceUpdate=true", TaskUnitResult.class);
    System.out.println(listResult);
    
    List<String> tables = listResult.getResult() ;
    TaskUnitResult<TableMetadata> tableResult = 
      restTemplate.getForObject("http://localhost:7080/hive/table/desc/" + tables.get(0), TaskUnitResult.class);
    System.out.println(tableResult.getResult());
  }
}