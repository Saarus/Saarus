package org.saarus.server.http;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.saarus.service.util.CommandParser;

/**
 * $Author: Tuan Nguyen$ 
 **/	
public class JettyWebServer {
  private Server server ;

  public JettyWebServer(String webappDir, int port) throws IOException {
    server = new Server();
    SelectChannelConnector connector = new SelectChannelConnector();
    connector.setPort(port);
    connector.setMaxIdleTime(30000);
    connector.setRequestHeaderSize(8192);

    server.setConnectors(new Connector[]{ connector});

    List<File> holder = new ArrayList<File>() ;
    File webappPath = new File(webappDir) ;
    if(webappPath.isFile()) holder.add(webappPath) ;
    else for(File sel : webappPath.listFiles()) holder.add(sel) ;

    List<WebAppContext> wcontextHolder = new ArrayList<WebAppContext>() ;
    for(File file : holder) {
      String name = file.getName() ;
      String contextPath = name ;
      if(contextPath.equalsIgnoreCase("root") || contextPath.equalsIgnoreCase("root.war")) {
        contextPath = "/" ;
      } else if(contextPath.endsWith(".war")) {
        contextPath = "/" + contextPath.substring(0, contextPath.length() - 4) ;
      } else {
        contextPath = "/" + contextPath ;
      }
      WebAppContext webapp = new WebAppContext();
      //webapp.getSessionHandler().getSessionManager().setSessionURL("none") ; 
      webapp.setContextPath(contextPath);
      webapp.setWar(file.getCanonicalPath());
      wcontextHolder.add(webapp) ;
      System.out.println("Add: " + contextPath + ", " + file.getCanonicalPath()) ;
    }

    ContextHandlerCollection contexts = new ContextHandlerCollection();
    contexts.setHandlers(wcontextHolder.toArray(new WebAppContext[wcontextHolder.size()]));

    server.setHandler(contexts);
  }

  public void start() throws Exception {
    server.start() ;
  }

  public void stop() throws Exception {
    server.stop() ;
  }

  static public void run(String[] args) throws Exception {
    CommandParser command = new CommandParser("server:") ;
    command.addMandatoryOption("webapp", true, "The directory contain the web application or war file") ;
    command.addOption("port", true, "The listen port") ;
    if(!command.parse(args)) return ;
    command.printHelp() ;
    String webapp = command.getOption("webapp", null) ;
    int port = command.getOptionAsInt("port", 8080) ;
    JettyWebServer server = new JettyWebServer(webapp, port) ;
    server.start() ;
  }

  static public void main(String[] args) throws Exception {
    if(args == null || args.length == 0) {
      args = new String[] {"-webapp", "src/test/resources/webapp"} ;
    }
    run(args) ;
    Thread.currentThread().join() ;
  }
}
