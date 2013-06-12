package org.saarus.service.remote;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/test-beans.xml" })
public class ControllerUnitTest {

  @Autowired
  @Qualifier("restTemplate")
  private RestTemplate restTemplate;

  @Test
  public void testMethodCall() {
    List<ServiceDescription> serviceDescs= 
      restTemplate.getForObject("http://localhost:7080/help.json", List.class);
    Assert.assertNotNull(serviceDescs);
    Assert.assertTrue(serviceDescs.size() > 0);
    
    ServiceDescription sd = new ServiceDescription();
    sd.setName("My Service");
    sd.setDescription("My custom service help") ;
    ServiceDescription retBook = restTemplate.postForObject("http://localhost:7080/add.json", sd, ServiceDescription.class);
    System.out.println(retBook.getName());
  }
}
