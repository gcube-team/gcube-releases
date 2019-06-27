package org.gcube.vremanagement.softwaregateway.webserver.impl.jetty;

import java.io.IOException;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.softwaregateway.webserver.WebServer;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ResourceHandler;
import org.mortbay.jetty.nio.SelectChannelConnector;
 
/**
 * @author Luca Frosini (ISTI-CNR)
 */
public class JettyWebServer implements WebServer {
	
	/** 
	 * Class logger. 
	 */
	protected static final GCUBELog logger = new GCUBELog(JettyWebServer.class);
	
	protected static final JettyWebServerLogger webServerLogger = new JettyWebServerLogger();
	
	private Server server = new Server(); 
	
	/**
	 * Initialize the WebServer with default connector (SelectChannelConnector) on specified port
	 * and with default Handler
	 * {@inheritDoc}
	 */
	public void initDefaults(String basePath,int port){
		
		Connector connector = new SelectChannelConnector();
		connector.setPort(port);
		server.setConnectors(new Connector[]{connector});
		
		
		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setResourceBase(basePath);
		try {
			logger.debug("HTTP Server Base Path : " + resourceHandler.getBaseResource().getFile().getAbsolutePath());
		} catch (IOException e) {
			logger.error(e);
		}
		
		server.setHandler(resourceHandler);
		
	}
	
	/**
	 * @param connectors Connectors
	 */
	public void setConnectors(Connector[] connectors) {
		server.setConnectors(connectors);
	}
	
	/**
	 * @param handlers Handlers
	 */
	public void setHandlers(Handler[] handlers){
		server.setHandlers(handlers);
	}

	
	/**
	 * {@inheritDoc}
	 */
	public void startServer() throws Exception {
		try {
			server.start();
		} catch (Exception e) {
			String error = "Error while starting WebServer";
			logger.error(error,e);
		}
		
		/*
		try {
			server.join();
		} catch (InterruptedException e) {
			String error = "Error while joining WebServer";
			logger.error(error,e);
		}
		*/
	}
	
	public String getBaseUrl(){
		ResourceHandler rh=(ResourceHandler)server.getHandler();
		return rh.getBaseResource().getURL().toString();
	}
	
}
