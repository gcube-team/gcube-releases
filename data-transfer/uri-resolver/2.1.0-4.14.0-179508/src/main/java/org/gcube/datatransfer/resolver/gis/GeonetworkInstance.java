package org.gcube.datatransfer.resolver.gis;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datatransfer.resolver.gis.exception.GeonetworkInstanceException;
import org.gcube.spatial.data.geonetwork.GeoNetwork;
import org.gcube.spatial.data.geonetwork.GeoNetworkPublisher;
import org.gcube.spatial.data.geonetwork.LoginLevel;
import org.gcube.spatial.data.geonetwork.configuration.Configuration;
import org.gcube.spatial.data.geonetwork.extension.ServerAccess.Version;
import org.gcube.spatial.data.geonetwork.model.Account;
import org.gcube.spatial.data.geonetwork.model.Account.Type;
import org.gcube.spatial.data.geonetwork.model.faults.AuthorizationException;
import org.gcube.spatial.data.geonetwork.model.faults.MissingConfigurationException;
import org.gcube.spatial.data.geonetwork.model.faults.MissingServiceEndpointException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class GeonetworkInstance.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * 
 * May 21, 2019
 */
public class GeonetworkInstance {

	private GeoNetworkPublisher geonetworkPublisher = null;

	private Logger logger = LoggerFactory.getLogger(GeonetworkInstance.class);
	private String scope;
	private Account account;
	private LoginLevel level;
	private Type type;
	private Configuration config;
	private Version version;
	private String endPoint;

	/**
	 * Instantiates a new geonetwork instance.
	 *
	 * @param scope the scope
	 * @param authenticate the authenticate
	 * @param level the level
	 * @param type the type
	 * @throws GeonetworkInstanceException the geonetwork instance exception
	 */
	public GeonetworkInstance(String scope, boolean authenticate, LoginLevel level, Type type) throws GeonetworkInstanceException {
		this.scope = scope;
		this.level = level;
		this.type = type;
		String originalScope =  ScopeProvider.instance.get();
		logger.debug("Instancing  GeonetworkInstance with scope: "+scope + ", authenticate: "+authenticate +", login level: "+level);
		try {
			ScopeProvider.instance.set(scope);
			logger.info("setting scope "+scope);
			createInstanceGeonetworkPublisher(authenticate, level);
			this.config = geonetworkPublisher.getConfiguration();
			this.version = this.config.getGeoNetworkVersion();
			this.endPoint = this.config.getGeoNetworkEndpoint();
			if(this.type!=null){
				this.account=config.getScopeConfiguration().getAccounts().get(this.type);
			}
			//logger.info("Admin: "+config.getAdminAccount().getUser()+", Pwd: "+config.getAdminAccount().getPassword());
		} catch (Exception e) {
			String message = "Sorry, an error occurred in instancing geonetwork";
			logger.warn(message, e);
			throw new GeonetworkInstanceException(message);
		}finally{
			if(originalScope!=null){
				ScopeProvider.instance.set(originalScope);
				logger.info("scope provider set to orginal scope: "+originalScope);
			}else{
				ScopeProvider.instance.reset();
				logger.info("scope provider reset");
			}
		}
	}

	/**
	 * Creates the instance geonetwork publisher.
	 *
	 * @param authenticate the authenticate
	 * @param level the level
	 * @throws Exception the exception
	 */
	private void createInstanceGeonetworkPublisher(boolean authenticate, LoginLevel level) throws Exception {
		logger.debug("creating new geonetworkPublisher..");
		this.geonetworkPublisher = GeoNetwork.get();
		if(authenticate && this.level!=null)
			authenticateOnGeoenetwork(level);
	}

	/**
	 * Authenticate on geoenetwork.
	 *
	 * @param level the level
	 * @throws GeonetworkInstanceException the geonetwork instance exception
	 */
	private void authenticateOnGeoenetwork(LoginLevel level) throws GeonetworkInstanceException {
		logger.trace("authenticating.. geonetworkPublisher is null? "+(this.geonetworkPublisher==null));
		if(geonetworkPublisher==null){
			logger.trace("skipping authentication.. please createInstace");
			return;
		}

		try {
			try {
				logger.info("Authenticating with login level: "+level);
				this.geonetworkPublisher.login(level);
			}
			catch (MissingConfigurationException | MissingServiceEndpointException e) {
				logger.error("MissingConfigurationException | MissingServiceEndpointException exception ", e);
				throw new GeonetworkInstanceException("Geonetwork authentication failed");
			}
		} catch (AuthorizationException e) {
			logger.error("AuthorizationException ",e);
			throw new GeonetworkInstanceException("Geonetwork authentication failed");
		}
		logger.trace("authentication on geonetwork completed");

	}


	/**
	 * Gets the geonetwork publisher.
	 *
	 * @return the geonetwork publisher
	 */
	public GeoNetworkPublisher getGeonetworkPublisher() {
		return geonetworkPublisher;
	}
	
	
	/**
	 * Gets the version.
	 *
	 * @return the version
	 */
	public Version getVersion() {
		return version;
	}
	
	
	/**
	 * Gets the level.
	 *
	 * @return the level
	 */
	public LoginLevel getLevel() {
		return level;
	}
	/**
	 * Gets the account.
	 *
	 * @return the account
	 */
	public Account getAccount() {

		return account;
	}
	
	
	/**
	 * Gets the end point.
	 *
	 * @return the end point
	 */
	public String getEndPoint() {
		return endPoint;
	}
	
	/**
	 * Gets the config.
	 *
	 * @return the config
	 */
	public Configuration getConfig() {
		return config;
	}

	/**
	 * Gets the scope.
	 *
	 * @return the scope
	 */
	public String getScope() {

		return scope;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GeonetworkInstance [geonetworkPublisher=");
		builder.append(geonetworkPublisher);
		builder.append(", scope=");
		builder.append(scope);
		builder.append(", account=");
		builder.append(account);
		builder.append(", level=");
		builder.append(level);
		builder.append(", type=");
		builder.append(type);
		builder.append(", config=");
		builder.append(config);
		builder.append("]");
		return builder.toString();
	}
	
	

}
