package org.gcube.vremanagement.softwaregateway.webserver;

/**
 * @author Luca Frosini (ISTI-CNR)
 */
public interface WebServer {
	
	/**
	 * Initialize the WebServer
	 * @param basePath Base Server Path
	 * @param port Server Port
	 */
	public void initDefaults(String basePath,int port);
	
	/**
	 * @throws Exception if fails
	 */
	public void startServer() throws Exception;
	
	public String getBaseUrl();
	
}
