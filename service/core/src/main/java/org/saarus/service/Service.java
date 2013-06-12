package org.saarus.service;

abstract public class Service {
  private String serviceId ;
  private ServiceConfiguration serviceConfig ;

  public String getServiceId() { return this.serviceId ; }

  public ServiceConfiguration getServiceConfiguration() { return this.serviceConfig ; }

  public ServiceReport getServiceReport() { return null ; }

  public void addListener(ServiceListener listener) {
  }

  public void addListener(String clazz) {
  }

  public void broadcast(ServiceEvent event) {
  }

  abstract public void init() ;
  abstract public void reset() ;
  abstract public void start() ;
  abstract public void pause() ;
  abstract public void resume() ;
  abstract public void stop() ;
  abstract public void destroy() ;
}