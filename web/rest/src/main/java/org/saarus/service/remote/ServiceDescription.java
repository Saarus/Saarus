package org.saarus.service.remote;

public class ServiceDescription {

  private String name;
  private String description ;

  public ServiceDescription() {}

  public ServiceDescription(String name, String desc) {
    this.name = name ;
    this.description = desc ;
  }
  
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
}
