package org.gcube.portlets.user.uriresolvermanager.readers;

import static org.gcube.resources.discovery.icclient.ICFactory.client;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.gcube.common.resources.gcore.utils.XPathHelper;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.user.uriresolvermanager.entity.Resolver;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.Query;
import org.gcube.resources.discovery.client.queries.impl.QueryBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;


/**
 * The Class UriResolverMapReader.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 4, 2015
 */
public class UriResolverMapReader {

	/**
	 *
	 */
	public static final String URIRESOLVERMAP_SECONDARY_TYPE = "UriResolverMap";
	public static final String URI_RESOLVER_MAP_RESOURCE_NAME = "Uri-Resolver-Map";

//	private Logger logger = LoggerFactory.getLogger(UriResolverMapReader.class);
	//TODO TEMP SOLUTION IN ORDER TO PRINT USING ALSO LOG4J INTO GEOEXPLORER PORTLET
	private Logger logger = LoggerFactory.getLogger(UriResolverMapReader.class);
	private String secondaryType;
	private String scope;
	private String resourceName;
	private Map<String, Resolver> applicationTypes; //A map ApplicationType - Resolver


	/**
	 * Instantiates a new uri resolver map reader.
	 *
	 * @throws Exception the exception
	 */
	public UriResolverMapReader() throws Exception {

		this.resourceName = URI_RESOLVER_MAP_RESOURCE_NAME;
		this.secondaryType = URIRESOLVERMAP_SECONDARY_TYPE;
		readProfileFromInfrastrucure();
	}


	/**
	 * this method looks up the generic resource among the ones available in the infrastructure using scope provider {@link  ScopeProvider.instance.get()}
	 * resource name {@value #URI_RESOLVER_MAP_RESOURCE_NAME} and secondaryType {@value #URIRESOLVERMAP_SECONDARY_TYPE}
	 *
	 * @return the applicationProfile profile
	 * @throws Exception the exception
	 */
	private void readProfileFromInfrastrucure() throws Exception {

			String queryString = getGcubeGenericQueryString(secondaryType, resourceName);

			logger.info("Trying to fetch in the scope: "+ScopeProvider.instance.get()+" the Generic Resouce with name: "+resourceName + " secondary type: "+secondaryType);
			logger.info(queryString);
			try {

				Query q = new QueryBox(queryString);
				logger.debug("new query box works");

				DiscoveryClient<String> client = client();
				logger.info("submitting query is: "+queryString);
			 	List<String> appUriResolverMap = client.submit(q);
			 	logger.debug("submit query works");

				if (appUriResolverMap == null || appUriResolverMap.size() == 0){
					logger.error("ApplicationProfile with secondaryType: "+secondaryType+" and name: "+resourceName+" is not registered in the infrastructure, scope: "+ScopeProvider.instance.get());
					throw new ApplicationProfileException("ApplicationProfile with secondaryType: "+secondaryType+" and name: "+resourceName+" is not registered in the scope: "+ScopeProvider.instance.get());
				}else {
					logger.info("Building map applications type - resource");
					logger.debug("Building new DocumentBuilder..");
					String elem = appUriResolverMap.get(0);
					DocumentBuilder docBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();
					Node node = docBuilder.parse(new InputSource(new StringReader(elem))).getDocumentElement();
					logger.debug("Building new XPathHelper..");
					XPathHelper helper = new XPathHelper(node);
					List<String> currValue = null;
					logger.debug("Evaluating XPath..");
					currValue = helper.evaluate("/Resource/Profile/Body/access_point/application_type/text()");
					if (currValue != null && currValue.size() > 0) {
						logger.info("Application Types are: "+currValue.size());
						applicationTypes = new HashMap<String, Resolver>(currValue.size());
//						List<String> appTypes = currValue;
						//FOR EACH APPLICATION TYPE
						for (String at : currValue) {
							logger.info("Application Type "+at);
//							currValue = helper.evaluate("/Resource/Profile/Body/EndPoint[Scope='"+scope.toString()+"']/Scope/text()");
							List<String> resources = helper.evaluate("/Resource/Profile/Body/access_point[application_type='"+at+"']/resource/text()");
							List<String> entryNames = helper.evaluate("/Resource/Profile/Body/access_point[application_type='"+at+"']/entryname/text()");
							if(resources!=null && resources.size()>0){
								Resolver resolver = new Resolver(resources.get(0), entryNames.get(0));
								applicationTypes.put(at, resolver);
								logger.info("Stored: "+at +" -> Resolver: "+ resolver);
							}else
								logger.warn("Skipping Type "+at+" mapping to runtime resource not found!");
						}
					}
			}

		} catch (Exception e) {
			logger.error("Error while trying to fetch Generic Resource with secondaryType: "+secondaryType+" and name "+resourceName+" from the infrastructure", e);
			throw new ApplicationProfileException("Error while trying to fetch  Generic Resourc with secondaryType: "+secondaryType+" and name "+resourceName+" from the infrastructure");
		}

	}

	/**
	 * Gets the gcube generic query string.
	 *
	 * @param secondaryType the secondary type
	 * @param name the name
	 * @return the gcube generic query string
	 */
	public static String getGcubeGenericQueryString(String secondaryType, String name){

		return "for $profile in collection('/db/Profiles/GenericResource')//Resource " +
				"where $profile/Profile/SecondaryType/string() eq '"+secondaryType+"' and  $profile/Profile/Name/string() " +
				" eq '" + name + "'" +
				"return $profile";

	}

	/**
	 * Gets the application types.
	 *
	 * @return the applicationTypes
	 */
	public Map<String, Resolver> getApplicationTypes() {
		return applicationTypes;
	}

	/**
	 * Gets the secondary type.
	 *
	 * @return the secondary type
	 */
	public String getSecondaryType() {
		return secondaryType;
	}


	/**
	 * Gets the scope.
	 *
	 * @return the scope
	 */
	public String getScope() {
		return scope;
	}

	/**
	 * Gets the resource name.
	 *
	 * @return the resource name
	 */
	public String getResourceName() {
		return resourceName;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UriResolverMapReader [secondaryType=");
		builder.append(secondaryType);
		builder.append(", scope=");
		builder.append(scope);
		builder.append(", resourceName=");
		builder.append(resourceName);
		builder.append(", applicationTypes=");
		builder.append(applicationTypes);
		builder.append("]");
		return builder.toString();
	}
}
