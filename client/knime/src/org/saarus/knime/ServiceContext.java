package org.saarus.knime;

import org.saarus.client.ClientContext;


public class ServiceContext {
  static ServiceContext instance = new ServiceContext() ;
  
  private ClientContext clientContext ;
  
  private ServiceContext() {
    clientContext = new ClientContext() ;
  }
  
  public ClientContext getClientContext() { return this.clientContext ; }

  static public ServiceContext getInstance() { return instance ; }
}
