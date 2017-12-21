package org.gcube.datacatalogue.ckanutillibrary.server;

import static org.gcube.resources.discovery.icclient.ICFactory.client;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.Property;
import org.gcube.common.resources.gcore.utils.XPathHelper;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datacatalogue.ckanutillibrary.shared.ex.ApplicationProfileNotFoundException;
import org.gcube.datacatalogue.ckanutillibrary.shared.ex.NoApplicationProfileMasterException;
import org.gcube.datacatalogue.ckanutillibrary.shared.ex.NoDataCatalogueRuntimeResourceException;
import org.gcube.datacatalogue.ckanutillibrary.shared.ex.ServiceEndPointException;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.Query;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.resources.discovery.client.queries.impl.QueryBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Retrieve ckan running instance information in the infrastructure (for both its database and data catalogue url)
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class DataCatalogueRunningCluster {

	//logger
	private static final Logger logger = LoggerFactory.getLogger(DataCatalogueRunningCluster.class);

	// database of the datacatalogue info
	private final static String RUNTIME_DB_RESOURCE_NAME = "CKanDatabase";
	private final static String PLATFORM_DB_NAME = "postgres";
	private final static String APPLICATION_PROFILE_NAME = "CkanPortlet";

	// data catalogue info
	private final static String RUNTIME_CATALOGUE_RESOURCE_NAME = "CKanDataCatalogue";
	private final static String PLATFORM_CATALOGUE_NAME = "Tomcat"; 

	// api key property for SYSADMIN
	private final static String API_KEY_PROPERTY = "API_KEY";

	// property to retrieve the master service endpoint into the /root scope
	private final static String IS_MASTER_ROOT_KEY_PROPERTY = "IS_ROOT_MASTER"; // true, false.. missing means false as well
	private final static String IS_MANAGE_PRODUCT_ENABLED = "IS_MANAGE_PRODUCT_ENABLED"; // true, false.. missing means false as well (for GRSF records)
	private final static String ALERT_USERS_ON_POST_CREATION = "ALERT_USERS_ON_POST_CREATION";

	// url of the http uri for this scope
	private final static String URL_RESOLVER = "URL_RESOLVER";

	// Other generic resource for delegating roles in groups to users
	private final static String CATALOGUE_EXTENDING_ROLES = "CatalogueDelegateRoles"; 

	// retrieved data
	private List<String> datacatalogueUrls = new ArrayList<String>();
	private List<String> hostsDB = new ArrayList<String>();
	private List<Integer> portsDB = new ArrayList<Integer>();
	private String nameDB;
	private String userDB;
	private String passwordDB;
	private String portletUrl;
	private boolean manageProductEnabled;
	private String urlResolver;
	private boolean alertUsers;
	private static Map<String, String> extendRoleInOrganization = new HashMap<String, String>(0);
	
	// generic role key
	public static final String CKAN_GENERIC_ROLE = "*";	
	public static final String ROLE_ORGANIZATION_SEPARATOR = "|";
	public static final String TUPLES_SEPARATOR = ",";

	// this token is needed in order to assign roles to user
	private String sysAdminToken;

	public DataCatalogueRunningCluster(String scope) throws Exception{

		if(scope == null || scope.isEmpty())
			throw new Exception("Invalid scope!!");

		// retrieve the current scope and save it (it will be reset later)
		String currentScope = ScopeProvider.instance.get();

		logger.info("Retrieving ckan database service end point information for scope " + scope);
		try {

			// set the scope
			ScopeProvider.instance.set(scope);

			logger.debug("Retrieving database information.");

			List<ServiceEndpoint> resources = getConfigurationFromISFORDB();
			evaluateRightConfigurationDB(resources);

			logger.debug("Retrieving ckan data catalogue service end point information and sysadmin token.");

			resources = getConfigurationFromISFORCatalogueUrl();
			evaluateRightConfigurationCatalogue(resources);

			// finally get the url in which the ckan portlet is deployed

			logger.debug("Looking for portlet url in " + ScopeProvider.instance.get() + " scope" );

			portletUrl = getPortletUrlFromInfrastrucure();

			// and parse the CatalogueDelegateRole resource, if any, in this context
			parseExtendingRoles();

		}catch(Exception e) {
			logger.warn("The following error occurred: " + e.toString());
			throw e;
		}finally{
			ScopeProvider.instance.set(currentScope);
		}
	}


	/**
	 * Evaluate the right configuration about ckan
	 * @param resources
	 * @throws NoDataCatalogueRuntimeResourceException 
	 * @throws ServiceEndPointException 
	 */
	private void evaluateRightConfigurationCatalogue(
			List<ServiceEndpoint> resources) throws NoDataCatalogueRuntimeResourceException, ServiceEndPointException {
		if (resources.size() == 0){
			logger.error("There is no Runtime Resource having name " + RUNTIME_CATALOGUE_RESOURCE_NAME +" and Platform " + PLATFORM_CATALOGUE_NAME + " in this scope.");
			throw new NoDataCatalogueRuntimeResourceException();
		}
		else {
			logger.debug(resources.toString());
			try{
				if(resources.size() > 1){
					boolean oneWasMaster = false;

					logger.info("Too many Runtime Resource having name " + RUNTIME_CATALOGUE_RESOURCE_NAME +" in this scope.. Looking for the one that has the property " + IS_MASTER_ROOT_KEY_PROPERTY);

					for (ServiceEndpoint res : resources) {

						Iterator<AccessPoint> accessPointIterator = res.profile().accessPoints().iterator();

						while (accessPointIterator.hasNext()) {
							ServiceEndpoint.AccessPoint accessPoint = (ServiceEndpoint.AccessPoint) accessPointIterator
									.next();

							// get the is master property
							Property entry = accessPoint.propertyMap().get(IS_MASTER_ROOT_KEY_PROPERTY);
							String isMaster = entry != null ?  entry.value() : null;

							if(isMaster == null || !isMaster.equals("true"))
								continue;

							// set this variable
							oneWasMaster = true;

							// add this host
							datacatalogueUrls.add(accessPoint.address());

							// retrieve sys admin token
							sysAdminToken = accessPoint.propertyMap().get(API_KEY_PROPERTY).value();
							sysAdminToken = StringEncrypter.getEncrypter().decrypt(sysAdminToken);

							// retrieve URL_RESOLVER
							if(accessPoint.propertyMap().containsKey(URL_RESOLVER))
								urlResolver = accessPoint.propertyMap().get(URL_RESOLVER).value();

							// break now
							break;
						}
					}

					// if none of them was master, throw an exception
					if(!oneWasMaster)
						throw new NoApplicationProfileMasterException("There is no application profile with MASTER property");

				}else{

					ServiceEndpoint res = resources.get(0);
					Iterator<AccessPoint> accessPointIterator = res.profile().accessPoints().iterator();

					while (accessPointIterator.hasNext()) {
						ServiceEndpoint.AccessPoint accessPoint = (ServiceEndpoint.AccessPoint) accessPointIterator
								.next();

						// add this host
						datacatalogueUrls.add(accessPoint.address());

						// retrieve sys admin token
						sysAdminToken = accessPoint.propertyMap().get(API_KEY_PROPERTY).value();
						sysAdminToken = StringEncrypter.getEncrypter().decrypt(sysAdminToken);

						// get the is manage product property
						Property entry = accessPoint.propertyMap().get(IS_MANAGE_PRODUCT_ENABLED);
						String isManageProduct = entry != null ?  entry.value() : null;

						if(isManageProduct != null && isManageProduct.equals("true")){
							logger.info("Manage product is enabled in this scope");
							manageProductEnabled = true;
						}

						// retrieve URL_RESOLVER
						if(accessPoint.propertyMap().containsKey(ALERT_USERS_ON_POST_CREATION))
							if(accessPoint.propertyMap().get(ALERT_USERS_ON_POST_CREATION).value().trim().equalsIgnoreCase("true"))
								alertUsers = true;

						// retrieve URL_RESOLVER
						if(accessPoint.propertyMap().containsKey(URL_RESOLVER))
							urlResolver = accessPoint.propertyMap().get(URL_RESOLVER).value();
					}
				}

			}catch(Exception e){
				throw new ServiceEndPointException("There is no service end point for such information");
			}
		}


	}

	/**
	 * Retrieve the right DB information
	 * @param resources
	 * @throws ServiceEndPointException
	 * @throws NoDataCatalogueRuntimeResourceException
	 */
	private void evaluateRightConfigurationDB(List<ServiceEndpoint> resources) throws ServiceEndPointException, NoDataCatalogueRuntimeResourceException {

		if (resources.size() == 0){
			throw new NoDataCatalogueRuntimeResourceException("There is no Runtime Resource having name " + RUNTIME_DB_RESOURCE_NAME +" and Platform " + PLATFORM_DB_NAME + " in this scope.");
		}
		else {
			try{

				if(resources.size() > 1){
					boolean oneWasMaster = false;

					logger.info("Too many Runtime Resource having name " + RUNTIME_DB_RESOURCE_NAME +" in this scope.. Looking for the one that has the property " + IS_MASTER_ROOT_KEY_PROPERTY);

					for (ServiceEndpoint res : resources) {

						Iterator<AccessPoint> accessPointIterator = res.profile().accessPoints().iterator();

						while (accessPointIterator.hasNext()) {
							ServiceEndpoint.AccessPoint accessPoint = (ServiceEndpoint.AccessPoint) accessPointIterator
									.next();

							// get the is master property
							Property entry = accessPoint.propertyMap().get(IS_MASTER_ROOT_KEY_PROPERTY);
							String isMaster = entry != null ?  entry.value() : null;

							if(isMaster == null || !isMaster.equals("true"))
								continue;

							// set this variable
							oneWasMaster = true;

							// add this host
							hostsDB.add(accessPoint.address().split(":")[0]);

							// save the port
							int port = Integer.parseInt(accessPoint.address().split(":")[1]);
							portsDB.add(port);

							// save the name of the cluster (this should be unique)
							nameDB = accessPoint.name();

							// save user and password
							passwordDB = StringEncrypter.getEncrypter().decrypt(accessPoint.password());
							userDB = accessPoint.username();

							// now break
							break;
						}
					}

					// if none of them was master, throw an exception
					if(!oneWasMaster)
						throw new NoApplicationProfileMasterException("There is no application profile with MASTER property");
				}else{
					logger.debug(resources.toString());
					ServiceEndpoint res = resources.get(0);
					Iterator<AccessPoint> accessPointIterator = res.profile().accessPoints().iterator();

					while (accessPointIterator.hasNext()) {
						ServiceEndpoint.AccessPoint accessPoint = (ServiceEndpoint.AccessPoint) accessPointIterator
								.next();

						// add this host
						hostsDB.add(accessPoint.address().split(":")[0]);

						// save the port
						int port = Integer.parseInt(accessPoint.address().split(":")[1]);
						portsDB.add(port);

						// save the name of the cluster (this should be unique)
						nameDB = accessPoint.name();

						// save user and password
						passwordDB = StringEncrypter.getEncrypter().decrypt(accessPoint.password());
						userDB = accessPoint.username();

					}
				}
			}catch(Exception e ){
				throw new ServiceEndPointException(e.toString());
			}
		}
	}

	/**
	 * Retrieve endpoints information from IS for DB
	 * @return list of endpoints for ckan database
	 * @throws Exception
	 */
	private static List<ServiceEndpoint> getConfigurationFromISFORDB() throws Exception{

		SimpleQuery query = queryFor(ServiceEndpoint.class);
		query.addCondition("$resource/Profile/Name/text() eq '"+ RUNTIME_DB_RESOURCE_NAME +"'");
		query.addCondition("$resource/Profile/Platform/Name/text() eq '"+ PLATFORM_DB_NAME +"'");
		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
		List<ServiceEndpoint> toReturn = client.submit(query);
		return toReturn;

	}

	/**
	 * Retrieve endpoints information from IS for DataCatalogue URL
	 * @return list of endpoints for ckan data catalogue
	 * @throws Exception
	 */
	private static List<ServiceEndpoint> getConfigurationFromISFORCatalogueUrl() throws Exception{

		SimpleQuery query = queryFor(ServiceEndpoint.class);
		query.addCondition("$resource/Profile/Name/text() eq '"+ RUNTIME_CATALOGUE_RESOURCE_NAME +"'");
		query.addCondition("$resource/Profile/Platform/Name/text() eq '"+ PLATFORM_CATALOGUE_NAME +"'");
		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
		List<ServiceEndpoint> toReturn = client.submit(query);
		return toReturn;

	}	

	/**
	 * Retrieve the url of the ckan portlet deployed into this scope
	 * @return
	 */
	private static String getPortletUrlFromInfrastrucure() {

		String scope = ScopeProvider.instance.get();
		logger.debug("Trying to fetch applicationProfile profile from the infrastructure for " + APPLICATION_PROFILE_NAME + " scope: " +  scope);

		try {
			Query q = new QueryBox("for $profile in collection('/db/Profiles/GenericResource')//Resource " +
					"where $profile/Profile/SecondaryType/string() eq 'ApplicationProfile' and  $profile/Profile/Name/string() " +
					" eq '" + APPLICATION_PROFILE_NAME + "'" +
					"return $profile");

			DiscoveryClient<String> client = client();
			List<String> appProfile = client.submit(q);

			if (appProfile == null || appProfile.size() == 0) 
				throw new ApplicationProfileNotFoundException("Your applicationProfile is not registered in the infrastructure");
			else {
				String elem = appProfile.get(0);
				DocumentBuilder docBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Node node = docBuilder.parse(new InputSource(new StringReader(elem))).getDocumentElement();
				XPathHelper helper = new XPathHelper(node);

				List<String> currValue = null;
				currValue = helper.evaluate("/Resource/Profile/Body/url/text()");
				if (currValue != null && currValue.size() > 0) {
					logger.debug("Portlet url found is " + currValue.get(0));
					return currValue.get(0);
				} 

			}

		} catch (Exception e) {
			logger.error("Error while trying to fetch applicationProfile profile from the infrastructure", e);
		} 

		return null;

	}

	/**
	 * Parse the CatalogueDelegateRoles in this context
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 */
	private static void parseExtendingRoles() throws ParserConfigurationException, SAXException, IOException {

		Query q = new QueryBox("for $profile in collection('/db/Profiles/GenericResource')//Resource " +
				"where $profile/Profile/SecondaryType/string() eq 'ApplicationProfile' and  $profile/Profile/Name/string() " +
				" eq '" + CATALOGUE_EXTENDING_ROLES + "'" +
				"return $profile");

		DiscoveryClient<String> client = client();
		List<String> appProfile = client.submit(q);
		
		logger.debug("Resource for extending role has size " + appProfile.size());

		if (appProfile == null || appProfile.size() == 0) 
			return;
		else {
			String profile = appProfile.get(0);
			DocumentBuilder docBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Node node = docBuilder.parse(new InputSource(new StringReader(profile))).getDocumentElement();
			XPathHelper helper = new XPathHelper(node);

			// fetch delegate elements
			NodeList delegates = helper.evaluateForNodes("/Resource/Profile/Body/delegates/delegate");
			if (delegates != null && delegates.getLength() > 0) {

				for(int i = 0; i < delegates.getLength(); i++){

					Node nodeI = delegates.item(i);

					if(nodeI.getNodeType() == Node.ELEMENT_NODE) {

						Element elem = (Element)nodeI;     
						String role = elem.getElementsByTagName("sourceRole").item(0).getTextContent();
						String destOrg = elem.getElementsByTagName("destOrganization").item(0).getTextContent();
						String sourceOrg = elem.getElementsByTagName("sourceOrganization").item(0).getTextContent();

						if(destOrg == null || sourceOrg == null){
							logger.warn("DestOrg or SourceOrg parameters missing");
							continue;
						}else{
							String currentValueForKey = extendRoleInOrganization.get(sourceOrg);
							
							if(currentValueForKey == null)
								currentValueForKey  = "";
							else
								currentValueForKey += TUPLES_SEPARATOR;
							
							currentValueForKey = destOrg + ROLE_ORGANIZATION_SEPARATOR + role;
							extendRoleInOrganization.put(sourceOrg, currentValueForKey);
						}
					}
				}
			}

		}
		
		logger.debug("Extended role map in this scope is " + extendRoleInOrganization);
	}
	
	/** Retrieve the ckan portlet url
	 * @return the portletUrl
	 */
	public String getPortletUrl() {
		return portletUrl;
	}

	/**
	 * Retrieve data catalogue url
	 */
	public List<String> getDataCatalogueUrl() {
		return datacatalogueUrls;
	}

	/**
	 * Get the hosts for such resource.
	 * @return
	 */
	public List<String> getDatabaseHosts() {
		return hostsDB;
	}

	/**
	 * Get the ports for such resource.
	 * @return
	 */
	public List<Integer> getDatabasePorts() {
		return portsDB;
	}

	/**
	 * Get the database name.
	 * @return
	 */
	public String getDataBaseName() {
		return nameDB;
	}

	/**
	 * Get the database's user.
	 * @return
	 */
	public String getDataBaseUser() {
		return userDB;
	}

	/**
	 * Get the database's password.
	 * @return
	 */
	public String getDataBasePassword() {
		return passwordDB;
	}

	/**
	 * @return the sysAdminToken
	 */
	public String getSysAdminToken() {
		return sysAdminToken;
	}

	/**
	 * Is manager product enabled (e.g., for GRSF records)
	 * @return
	 */
	public boolean isManageProductEnabled() {
		return manageProductEnabled;
	}

	/**
	 * Get the url of the uri resolver for this instance/scope
	 * @return
	 */
	public String getUrlResolver() {
		return urlResolver;
	}

	/**
	 * Check if alert user members is enabled
	 * @return
	 */
	public boolean isAlertEnabled() {
		return alertUsers;
	}


	/**
	 * Get roles to extend
	 * @return Map<String, String>
	 */
	public Map<String, String> getExtendRoleInOrganization() {
		return extendRoleInOrganization;
	}

}
