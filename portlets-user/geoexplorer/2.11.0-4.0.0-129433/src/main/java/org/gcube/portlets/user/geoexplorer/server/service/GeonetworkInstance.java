/**
 *
 */
package org.gcube.portlets.user.geoexplorer.server.service;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.gcube.portlets.user.geoexplorer.server.resources.GeonetworkConfigurationPropertyReader;
import org.gcube.spatial.data.geonetwork.GeoNetwork;
import org.gcube.spatial.data.geonetwork.GeoNetworkPublisher;
import org.gcube.spatial.data.geonetwork.GeoNetworkReader;
import org.gcube.spatial.data.geonetwork.LoginLevel;

/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Apr 29, 2013
 *
 */
public class GeonetworkInstance {

	private GeoNetworkPublisher geonetworkPublisher = null;
	private String geoNetworkUrl;
	private String geoNetworkUser = "";
	private String geoNetworkPwd = "";

	private String geoserverUrl = "";
	private String geoserverUser = "";
	private String geoserverPwd = "";

	private String scope = "noscope";

	public GeonetworkInstance(){} //FOR SERIALIZATION

	private Logger logger = Logger.getLogger(GeonetworkInstance.class);

	public GeoNetworkReader getGeonetworkReader() {
		return geonetworkPublisher;
	}

	/**
	 *
	 * @param scope
	 */
	public GeonetworkInstance(String scope){
		this.scope = scope;
	}


	/**
	 * Creates a new configuration for Gis publisher with parameters passed in input. By default executes the login as user passed in input on geonetwork instance
	 * @param scope
	 * @param geoNetworkUrl
	 * @param user
	 * @param pwd
	 * @param geoserverUrl
	 * @param geoserverUser
	 * @param geoserverPwd
	 * @param httpSession
	 * @throws Exception
	 */
	public GeonetworkInstance(String scope, String geoNetworkUrl, String user, String pwd, String geoserverUrl, String geoserverUser, String geoserverPwd, HttpSession httpSession) throws Exception {
		try {
			logger.trace("Instancing  GeonetworkInstance with specific parameters");
			this.geoNetworkUser = user;
			this.geoNetworkPwd = pwd;
			this.geoNetworkUrl = geoNetworkUrl;

			this.geoserverUrl = geoserverUrl;
			this.geoserverUser = geoserverUser;
			this.geoserverPwd = geoserverPwd;
			this.scope = scope;


			//STORE PARAMETERS INTO PROPERTIES FILE
			GeonetworkConfigurationPropertyReader reader = new GeonetworkConfigurationPropertyReader(this.scope, httpSession);
			reader.saveGeonetworkProperties(scope, geoNetworkUrl, user, pwd);
			reader.saveGeoserverProperties(scope, geoserverUrl, geoserverUser, geoserverPwd);

			logger.trace("geoserverUrl is: "+geoserverUrl +" instancing geonetwork");
			if (geoNetworkUrl != null) {
				setGisPublisherConfiguration();
			}

		} catch (Exception e) {
			logger.error("Sorry, an error occurred in setting configuration of geonetwork instance",e);
			logger.trace("Tentaive of creation of the default instance of genetwork..");
			e.printStackTrace();
		}

		try {

			createInstance(true);

		} catch (Exception e) {
			String message = "Sorry, an error occurred in instancing geonetwork";
			logger.warn(message, e);
			throw new Exception(message);
		}
	}

	/**
	 * Loads the configurations from internal database.
	 * Executes the login in geonetwork if input parameter 'authenticate' is true, otherwise no login.
	 * @param authenticate
	 * @throws Exception
	 */
	public GeonetworkInstance(boolean authenticate, HttpSession httpSession) throws Exception {
		logger.trace("Instancing GeonetworkInstance with loader configurations from internal database");
		readConfigurationAndInstance(authenticate, httpSession);
	}

	/**
	 * Loads the configurations from internal database.
	 * @param authenticate - if true executes the login on geoenetwork, no otherwise
	 * @param httpSession
	 * @throws Exception
	 *
	 * TODO THIS METHOD SHOULD BE REMOVED IN THE FUTURE
	 *
	 */
	public void readConfigurationAndInstance(boolean authenticate, HttpSession httpSession) throws Exception{

		try {

			GeonetworkConfigurationPropertyReader reader = new GeonetworkConfigurationPropertyReader(scope, httpSession);

			this.geoNetworkUser = reader.getGeoNetworkUser();
			this.geoNetworkPwd = reader.getGeoNetworkPwd();
			this.geoNetworkUrl = reader.getGeoNetworkUrl();
			this.scope = reader.getGeonetworkScope();

			/*
			if(this.scope==null || this.scope.compareTo(reader.getGeonetworkScope())!=0){
				logger.warn("GeonetworkInstance scope is null or not equal to : " +reader.getGeonetworkScope() +", overriding");
				this.scope = reader.getGeonetworkScope();
			}
			*/

			this.geoserverUrl = reader.getGeoserverUrl();
			this.geoserverUser = reader.getGeoserverUser();
			this.geoserverPwd = reader.getGeoserverPwd();

			logger.trace("geonetwork instance read geonetwork url: "+geoNetworkUrl+" from file configuration");
			logger.trace("geonetwork user: "+geoNetworkUser);
			logger.trace("geonetwork pwd: "+geoNetworkPwd);
			logger.trace("scope: "+this.scope);


			logger.trace("geoserver instance read geoserver url: "+geoserverUrl+" from file configuration");
			logger.trace("geoserver user: "+geoserverUser);
			logger.trace("geoserver pwd: "+geoserverPwd);

			if(geoNetworkUrl != null)
				setGisPublisherConfiguration();
			else
				logger.warn("geonetwork url in configuration file is null. Instancing geonetwork publisher with default value");

		} catch (Exception e) {
			logger.error("Sorry, an error occurred in setting configuration of geonetwork instance",e);
			logger.trace("Tentaive of creation of the default instance of genetwork..");
			e.printStackTrace();
		}

		try {

			createInstance(authenticate);

		} catch (Exception e) {
			String message = "Sorry, an error occurred in instancing geonetwork";
			logger.warn(message, e);
			e.printStackTrace();
			throw new Exception(message);
		}
	}

	/**
	 *
	 * @param authenticate
	 * @throws Exception
	 */
	private void createInstance(boolean authenticate) throws Exception {

		this.geonetworkPublisher = GeoNetwork.get();
		authenticateOnGeoenetwork(authenticate);
	}

	/**
	 *
	 * @param authenticate
	 * @throws Exception
	 */
	public void authenticateOnGeoenetwork(boolean authenticate) throws Exception {

		logger.trace("authenticating.. geonetworkPublisher is null? "+(this.geonetworkPublisher==null));
		if(geonetworkPublisher==null){
			logger.trace("skipping authentication.. please createInstace");
			return;
		}

		if (authenticate){
			this.geonetworkPublisher.login(LoginLevel.SCOPE);
			logger.trace("authentication on geonetwork completed, login level: "+LoginLevel.SCOPE);
		}
	}


	/**
	 * Creates a new configuration for Gis publisher with parameter passed in input.  Executes the login on geonetwork instance if authenticate param is true, no otherwise
	 *
	 * @param geoNetworkUrl
	 * @param user
	 * @param pwd
	 * @throws Exception
	 */
	public GeonetworkInstance(String geoNetworkUrl, String user, String pwd, boolean authenticate) throws Exception {
		try {
			this.geoNetworkUrl = geoNetworkUrl;
			this.geoNetworkUser = user;
			this.geoNetworkPwd = pwd;

			if (geoNetworkUrl != null) {
				setGisPublisherConfiguration();
			}

			createInstance(true);

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Sorry, an error occurred in getting geonetwork instance",e);
		}
	}

	private void setGisPublisherConfiguration() {

//		logger.trace("ConfigurationManager setConfiguration instancing");
//		ConfigurationManager.setConfiguration(GeonetworkConfiguration.class);
	}


	/*public class GeonetworkConfiguration implements Configuration {

		public Map<LoginLevel,String> pwds=new HashMap<LoginLevel, String>();
		public Map<LoginLevel,String> usrs=new HashMap<LoginLevel, String>();

		@Override
		public String getGeoNetworkEndpoint() {
			logger.trace("geoNetworkUrl is: "+geoNetworkUrl);
			return geoNetworkUrl;
		}

		@Override
		public Map<LoginLevel, String> getGeoNetworkPasswords() {
			pwds.put(LoginLevel.DEFAULT, geoNetworkPwd);
			logger.trace("geoNetworkPwd is: "+geoNetworkPwd);
			return pwds;
		}

		@Override
		public Map<LoginLevel, String> getGeoNetworkUsers() {
			usrs.put(LoginLevel.DEFAULT, geoNetworkUser);
			logger.trace("geoNetworkUser is: "+geoNetworkUser);
			return usrs;
		}

		@Override
		public int getScopeGroup() {
			return 2;
		}
	}*/

	public GeoNetworkPublisher getGeonetworkPublisher() {
		return geonetworkPublisher;
	}

	public String getGeoNetworkUrl() {
		return geoNetworkUrl;
	}

	public String getGeoNetworkUser() {
		return geoNetworkUser;
	}

	public String getGeoNetworkPwd() {
		return geoNetworkPwd;
	}

	public String getGeoserverUrl() {
		return geoserverUrl;
	}

	public String getGeoserverUser() {
		return geoserverUser;
	}

	public String getGeoserverPwd() {
		return geoserverPwd;
	}

	public Logger getLogger() {
		return logger;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GeonetworkInstance [geonetworkPublisher=");
		builder.append(geonetworkPublisher);
		builder.append(", geoNetworkUrl=");
		builder.append(geoNetworkUrl);
		builder.append(", geoNetworkUser=");
		builder.append(geoNetworkUser);
		builder.append(", geoNetworkPwd=");
		builder.append(geoNetworkPwd);
		builder.append(", geoserverUrl=");
		builder.append(geoserverUrl);
		builder.append(", geoserverUser=");
		builder.append(geoserverUser);
		builder.append(", geoserverPwd=");
		builder.append(geoserverPwd);
		builder.append(", scope=");
		builder.append(scope);
		builder.append("]");
		return builder.toString();
	}

}
