package org.gcube.datatransfer.resolver.gis;

import java.util.HashMap;
import java.util.Map;

import org.gcube.datatransfer.resolver.gis.exception.GeonetworkInstanceException;
import org.gcube.spatial.data.geonetwork.GeoNetwork;
import org.gcube.spatial.data.geonetwork.GeoNetworkPublisher;
import org.gcube.spatial.data.geonetwork.LoginLevel;
import org.gcube.spatial.data.geonetwork.configuration.AuthorizationException;
import org.gcube.spatial.data.geonetwork.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Oct 7, 2014
 *
 */
public class GeonetworkInstance {

	private GeoNetworkPublisher geonetworkPublisher = null;
	private String geoNetworkUrl;
	private String geoNetworkUser = "";
	private String geoNetworkPwd = "";
	
	private String scope = "noscope";
	
	public GeonetworkInstance(){} //FOR SERIALIZATION
	
	private Logger logger = LoggerFactory.getLogger(GeonetworkInstance.class);

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
	public GeonetworkInstance(String scope, String geoNetworkUrl, String user, String pwd, boolean authenticate) throws GeonetworkInstanceException {
		logger.trace("Instancing  GeonetworkInstance with specific parameters");
		this.geoNetworkUser = user;
		this.geoNetworkPwd = pwd;
		this.geoNetworkUrl = geoNetworkUrl;
		this.scope = scope;
		try {
			createInstance(authenticate);
		} catch (Exception e) {
			String message = "Sorry, an error occurred in instancing geonetwork";
			logger.warn(message, e);
			throw new GeonetworkInstanceException(message);
		}
	}

	/**
	 * 
	 * @param authenticate
	 * @throws Exception
	 */
	private void createInstance(boolean authenticate) throws GeonetworkInstanceException {
		this.geonetworkPublisher = GeoNetwork.get(new GeonetworkConfiguration());
		authenticateOnGeoenetwork(authenticate);
	}
	
	/**
	 * 
	 * @param authenticate
	 * @throws Exception
	 */
	public void authenticateOnGeoenetwork(boolean authenticate) throws GeonetworkInstanceException {
		
		logger.trace("authenticating.. geonetworkPublisher is null? "+(this.geonetworkPublisher==null));
		if(geonetworkPublisher==null){
			logger.trace("skipping authentication.. please createInstace");
			return;
		}
		
		if (authenticate){
			try {
				this.geonetworkPublisher.login(LoginLevel.DEFAULT);
			} catch (AuthorizationException e) {
				logger.error("authentication on geonetwork failed ",e);
				throw new GeonetworkInstanceException("Geonetwork authentication failed");
			}
			logger.trace("authentication on geonetwork completed");
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

			createInstance(authenticate);

		} catch (Exception e) {
			logger.error("Sorry, an error occurred in getting geonetwork instance",e);
			throw new Exception("Sorry, an error occurred in getting geonetwork instance",e);
		}
	}

	
	public class GeonetworkConfiguration implements Configuration {

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
	}

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
		builder.append(", scope=");
		builder.append(scope);
		builder.append(", logger=");
		builder.append(logger);
		builder.append("]");
		return builder.toString();
	}

}
