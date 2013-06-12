package org.saarus.client;

import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;


public class ClientContext {
  static String[] res = { "classpath:META-INF/client-context.xml", }; 
  
  private GenericApplicationContext ctx ;
  
  public ClientContext() { this(res) ; }
  
  public ClientContext(String[] res) {
    this.ctx = new GenericApplicationContext();
    XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(ctx);
    xmlReader.loadBeanDefinitions(res);
    ctx.refresh();
    ctx.registerShutdownHook();
  }
  
  public <T> T getBean(Class<T> type) { return ctx.getBean(type) ; }
  
  public GenericApplicationContext getApplicationContext() { return this.ctx ; }
}
