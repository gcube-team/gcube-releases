package org.gcube.common.portal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




/**
 * Clients can obtain the single instance of the {@link PortalContext} by invoking its static method {@link #getConfiguration()}. 
 * The first invocation of the method triggers the initialisation of the instance.
 * 
 * @author Massimiliano Assante (ISTI-CNR)
 *
 */
public class PortalContext {
	private static final Logger _log = LoggerFactory.getLogger(PortalContext.class);

	private static final String INFRASTRUCTURE_NAME = "infrastructure";  
	private static final String SCOPES = "scopes";

	private static final String SENDER_EMAIL = "notificationSenderEmail";
	private static final String GATEWAY_NAME = "portalinstancename";
	
	private static PortalContext singleton = new PortalContext();

	private String infra;
	private String vos;

	private PortalContext() {
		initialize();
	}
	/**
	 * 
	 * @return the instance
	 */
	public synchronized static PortalContext getConfiguration() {
		return singleton == null ? new PortalContext() : singleton;
	}
	
	private void initialize() {
		Properties props = new Properties();
		try {
			String propertyfile = getCatalinaHome() + File.separator + "conf" + File.separator + "infrastructure.properties";			
			File propsFile = new File(propertyfile);
			FileInputStream fis = new FileInputStream(propsFile);
			props.load( fis);
			infra  = props.getProperty(INFRASTRUCTURE_NAME);
			vos = props.getProperty(SCOPES);
		}
		catch(IOException e) {
			infra = "gcube";
			vos = "devsec";
			_log.error("infrastructure.properties file not found under $CATALINA_HOME/conf/ dir, setting default infrastructure Name and devsec as VO Name" + infra);
		}		
		_log.info("PortalContext configurator correctly initialized on " + infra);
	}
	/**
	 * 
	 * @return the infrastructure name in which your client runs
	 */
	public String getInfrastructureName() {
		return this.infra;
	}
	/**
	 * 
	 * @return the value of the scopes as it is in the property file (a string with comma separated vales)
	 */
	public String getVOsAsString() {
		return this.vos;
	}
	/**
	 * 
	 * @return the value of the scopes
	 */
	public List<String> getVOs() {
		List<String> toReturn = new ArrayList<String>();
		if (vos == null || vos.equals(""))
			return toReturn;
		String[] split = vos.split(",");
		for (int i = 0; i < split.length; i++) {
			toReturn.add(split[i].trim());
		}
		return toReturn;
	}
	/**
	 * read the portal instance name from a property file and returns it
	 */
	public static String getPortalInstanceName() {
		//get the portles to look for from the property file
		Properties props = new Properties();
		String toReturn = "";

		try {
			String propertyfile =  getCatalinaHome() + File.separator + "conf" + File.separator + "gcube-data.properties";			
			File propsFile = new File(propertyfile);
			FileInputStream fis = new FileInputStream(propsFile);
			props.load( fis);
			toReturn = props.getProperty(GATEWAY_NAME);
		}
		//catch exception in case properties file does not exist
		catch(IOException e) {
			toReturn = "D4science Gateway";
			_log.error("gcube-data.properties file not found under $CATALINA_HOME/conf dir, returning default Portal Name " + toReturn);
			return toReturn;
		}
		_log.debug("Returning Gateway Name: " + toReturn );
		return toReturn;
	}
	/**
	 * read the sender (from) email address for notifications name from a property file and returns it
	 */
	public String getSenderEmail() {
		//get the portles to look for from the property file
		Properties props = new Properties();
		String toReturn = "";

		try {
			String propertyfile = getCatalinaHome() + File.separator + "conf" + File.separator + "gcube-data.properties";			
			File propsFile = new File(propertyfile);
			FileInputStream fis = new FileInputStream(propsFile);
			props.load( fis);
			toReturn = props.getProperty(SENDER_EMAIL);
		}
		//catch exception in case properties file does not exist
		catch(IOException e) {
			toReturn = "do-not-reply@d4science.org";
			_log.error("gcube-data.properties file not found under $CATALINA_HOME/conf dir, returning default Email" + toReturn);
			return toReturn;
		}
		_log.debug("Returning SENDER_EMAIL: " + toReturn );
		return toReturn;
	}
	/**
	 * 
	 * @return $CATALINA_HOME
	 */
	private static String getCatalinaHome() {
		return (System.getenv("CATALINA_HOME").endsWith("/") ? System.getenv("CATALINA_HOME") : System.getenv("CATALINA_HOME")+"/");
	}
}
