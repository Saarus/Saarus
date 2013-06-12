package org.saarus.service.remote.mahout;

import org.saarus.service.remote.ServiceDescription;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@org.springframework.stereotype.Controller
@RequestMapping("/mahout")
public class MahoutController {
  final static public ServiceDescription HELP = 
      new ServiceDescription("mahout", "Mahout machine learning framework service") ;
  
  @RequestMapping(value="/help", method=RequestMethod.GET)
  public @ResponseBody ServiceDescription help() {
    return HELP ;
  }
  
  @RequestMapping(value="/add", method=RequestMethod.POST)
  public  @ResponseBody ServiceDescription add(@RequestBody ServiceDescription book) {
    System.out.println("Call add book " + book.getName()) ;
    book.setName(book.getName() + "(Added)") ;
    return book;
  }
}