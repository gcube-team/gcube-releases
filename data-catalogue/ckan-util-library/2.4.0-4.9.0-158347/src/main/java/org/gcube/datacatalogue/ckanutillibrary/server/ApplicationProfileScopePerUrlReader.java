package org.gcube.datacatalogue.ckanutillibrary.server;

import static org.gcube.resources.discovery.icclient.ICFactory.client;

import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.gcube.common.portal.PortalContext;
import org.gcube.common.resources.gcore.utils.XPathHelper;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datacatalogue.ckanutillibrary.shared.ex.ApplicationProfileNotFoundException;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.Query;
import org.gcube.resources.discovery.client.queries.impl.QueryBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * This class has a method that tries to read the application profile whose structure contains
 * couples Scope/Current_Url to check where the the needed information needs to be discovered according to the current
 * portlet url. It means that the scope in which the ckan related information will be discovered could be different wrt the running one.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class ApplicationProfileScopePerUrlReader {

	private static final Logger logger = LoggerFactory.getLogger(ApplicationProfileScopePerUrlReader.class);
	private final static String APPLICATION_PROFILE_NAME = "DataCatalogueMapScopesUrls";
	private final static String QUERY = "for $profile in collection('/db/Profiles/GenericResource')//Resource " +
			"where $profile/Profile/SecondaryType/string() eq 'ApplicationProfile' and  $profile/Profile/Name/string() " +
			" eq '" + APPLICATION_PROFILE_NAME + "'" +
			"return $profile";

	/**
	 * Get the scope in which discover for this url. If the Application Profile doesn't contain it, the current scope (taken
	 * from ScopeProvider is returned).
	 * @param url
	 * @return the scope to be used for the given url
	 */
	public static String getScopePerUrl(String url){

		logger.info("Request scope for ckan portlet at url " + url);

		String scope = ScopeProvider.instance.get();
		String scopeToReturn = scope;
		String rootScopeForInfrastructure = "/" + PortalContext.getConfiguration().getInfrastructureName();

		if(url == null || url.isEmpty()){

			logger.info("The url passed is null or empty! Returning current scope ["  + scope + "]");
			return scope; 

		}

		// set this scope
		ScopeProvider.instance.set(rootScopeForInfrastructure);

		logger.debug("Trying to fetch applicationProfile profile from the infrastructure for " + APPLICATION_PROFILE_NAME + " scope: " +  rootScopeForInfrastructure);

		try {
			Query q = new QueryBox(QUERY);

			DiscoveryClient<String> client = client();
			List<String> appProfile = client.submit(q);

			if (appProfile == null || appProfile.size() == 0) 
				throw new ApplicationProfileNotFoundException("Your applicationProfile is not registered in the infrastructure");
			else{
				String elem = appProfile.get(0);
				DocumentBuilder docBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Node node = docBuilder.parse(new InputSource(new StringReader(elem))).getDocumentElement();
				XPathHelper helper = new XPathHelper(node);

				List<String> urls = helper.evaluate("/Resource/Profile/Body/EndPoint/URL/text()");
				if (urls != null && urls.size() > 0) {
					boolean foundScope = false;
					for (int i = 0; i < urls.size(); i++) {
						if (urls.get(i).trim().compareTo(url) == 0) {	// url found			
							scopeToReturn = helper.evaluate("/Resource/Profile/Body/EndPoint/Scope/text()").get(i);
							logger.debug("Found, returning " + scopeToReturn);
							foundScope = true;
							break;
						}						
					}
					if (!foundScope || scopeToReturn == null || scopeToReturn.isEmpty()){
						logger.debug("Scope is missing for url " + url + ". Returning " + scope);
					}
				}
				else 
					throw 
					new ApplicationProfileNotFoundException("Your applicationProfile EndPoint was not found in the profile, consider adding <EndPoint><Scope> element in <Body>");
			}
		} catch (Exception e) {
			logger.error("Error while trying to fetch applicationProfile profile from the infrastructure", e);
		}finally{
			ScopeProvider.instance.set(scope);
		}		

		return scopeToReturn;
	}
}
