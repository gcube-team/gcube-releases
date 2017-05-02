package org.gcube.portal.social.networking.liferay.ws;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.Property;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.smartgears.ContextProvider;
import org.gcube.smartgears.context.application.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * This is a singleton bean instantiated at service start up. It contains
 * the credentials of the user who is allowed to perform calls to Liferay.
 * Its credentials are looked up from the infrastructure.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class LiferayJSONWsCredentials {

	private static final Logger logger = LoggerFactory.getLogger(LiferayJSONWsCredentials.class);

	// the singleton obj
	private static LiferayJSONWsCredentials singleton = new LiferayJSONWsCredentials();

	// properties that it contains
	private String host;
	private String schema;
	private String user;
	private String password;
	private int port;

	// The token of the user used to send notifications/messages when an application token is provided. (will be read from web.xml)
	private String notifierUserToken; 

	// Service endpoint properties
	private final static String RUNTIME_RESOURCE_NAME = "D4Science Infrastructure Gateway";
	private final static String CATEGORY = "Portal";

	/**
	 * Private constructor
	 */
	private LiferayJSONWsCredentials() {
		logger.info("Building LiferayJSONWsCredentials object");
		loadNotifierToken();
		lookupPropertiesFromIs();
		logger.info("LiferayJSONWsCredentials object built");
	}

	/**
	 * Load the token of the notifier user
	 */
	private void loadNotifierToken() {
		try{
			notifierUserToken = ServletContextClass.getNotifierToken();
			logger.debug("Token read " + notifierUserToken.substring(0, 5)+ "*********************");
		}catch(Exception e){
			logger.error("Failed to read notifier user token!", e);
		}
	}

	/**
	 * Read the properties from the infrastructure
	 */
	private void lookupPropertiesFromIs() {

		logger.info("Starting creating LiferayJSONWsCredentials");

		String oldContext = ScopeProvider.instance.get();
		ApplicationContext ctx = ContextProvider.get(); // get this info from SmartGears
		ScopeProvider.instance.set("/"+ctx.container().configuration().infrastructure());
		logger.info("Discovering liferay user's credentials in context " + ctx.container().configuration().infrastructure());

		try{
			List<ServiceEndpoint> resources = getConfigurationFromIS();
			if (resources.size() == 0){
				logger.error("There is no Runtime Resource having name " + RUNTIME_RESOURCE_NAME +" and Category " + CATEGORY + " in this scope.");
				throw new Exception("There is no Runtime Resource having name " + RUNTIME_RESOURCE_NAME +" and Category " + CATEGORY + " in this scope.");
			}
			else {
				for (ServiceEndpoint res : resources) {
					Iterator<AccessPoint> accessPointIterator = res.profile().accessPoints().iterator();
					while (accessPointIterator.hasNext()) {
						ServiceEndpoint.AccessPoint accessPoint = (ServiceEndpoint.AccessPoint) accessPointIterator
								.next();

						if(accessPoint.name().equals("JSONWSUser")){
							// get base path
							Map<String, Property> properties = accessPoint.propertyMap();
							host = res.profile().runtime().hostedOn();
							schema = (String)properties.get("schema").value();
							user = StringEncrypter.getEncrypter().decrypt((String)properties.get("username").value());
							password = StringEncrypter.getEncrypter().decrypt((String)properties.get("password").value());
							port = Integer.valueOf(properties.get("port").value());

							// break
							break;
						}
					}
				}
			}
		}catch(Exception e){
			logger.error("Unable to retrieve such service endpoint information! ", e);
			return;
		}finally{
			if(oldContext != null)
				ScopeProvider.instance.set(oldContext);
		}

		logger.info("Bean built " + toString());
	}

	/**
	 * Retrieve endpoints information from IS for DB
	 * @return list of endpoints for ckan database
	 * @throws Exception
	 */
	private List<ServiceEndpoint> getConfigurationFromIS() throws Exception{

		SimpleQuery query = queryFor(ServiceEndpoint.class);
		query.addCondition("$resource/Profile/Name/text() eq '"+ RUNTIME_RESOURCE_NAME +"'");
		query.addCondition("$resource/Profile/Category/text() eq '"+ CATEGORY +"'");
		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
		List<ServiceEndpoint> toReturn = client.submit(query);
		return toReturn;

	}

	public static  LiferayJSONWsCredentials getSingleton() {
		return singleton;
	}

	public  String getHost() {
		return host;
	}

	public  String getSchema() {
		return schema;
	}

	public  String getUser() {
		return user;
	}

	public  String getPassword() {
		return password;
	}

	public  int getPort() {
		return port;
	}

	public String getNotifierUserToken() {
		return notifierUserToken;
	}

	@Override
	public String toString() {
		return "LiferayJSONWsCredentials [host=" + host + ", schema=" + schema
				+ ", user=" + user + ", password=" + password + ", port="
				+ port + ", notifierUserToken=" + notifierUserToken + "]";
	}

}
