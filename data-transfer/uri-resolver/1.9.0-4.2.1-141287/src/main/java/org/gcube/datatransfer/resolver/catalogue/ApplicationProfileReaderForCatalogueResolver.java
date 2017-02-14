/*
 *
 */
package org.gcube.datatransfer.resolver.catalogue;

import static org.gcube.resources.discovery.icclient.ICFactory.client;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.gcube.common.resources.gcore.utils.XPathHelper;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datatransfer.resolver.applicationprofile.ApplicationProfileNotFoundException;
import org.gcube.datatransfer.resolver.applicationprofile.GcubeQuery;
import org.gcube.datatransfer.resolver.applicationprofile.GcubeQuery.FIELD_TYPE;
import org.gcube.datatransfer.resolver.applicationprofile.ScopeUtil;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.Query;
import org.gcube.resources.discovery.client.queries.impl.QueryBox;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;


/**
 * The Class ApplicationProfileReaderForCatalogueResolver.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Dec 20, 2016
 */
public class ApplicationProfileReaderForCatalogueResolver {

	public static final String SECONDARY_TYPE = "ApplicationProfile";
	public static final String RESOURCE_NAME = "Catalogue-Resolver";
	protected static final String RESOURCE_PROFILE_NAME_TEXT = "/Resource/Profile/Name/text()";
	protected static final String RESOURCE_PROFILE_DESCRIPTION_TEXT = "/Resource/Profile/Description/text()";
	protected static final String RESOURCE_PROFILE_BODY_END_POINT_SCOPE_TEXT = "/Resource/Profile/Body/EndPoint/SCOPE/text()";
	protected static final String RESOURCE_PROFILE_BODY_END_POINT_VRE_NAME_TEXT = "/Resource/Profile/Body/EndPoint/VRE_NAME/text()";

	private Logger logger = Logger.getLogger(ApplicationProfileReaderForCatalogueResolver.class);
	private String secondaryType;
	private String resourceName;
	private String scope;
	private boolean useRootScope = false;

	/** The hash vre name scope.
	 *  Used by Catalogue Resolver for mapping VRE NAME with its SCOPE where to fetch the Data Catalogue Portlet so that resolve correctly URL of
	 *  kind: http://[CATALOGUE_RESOLVER_SERVLET]/[VRE_NAME]/[entity_context value]/[entity_name value]
	 * */
	private Map<String, String> hashVreNameScope = new HashMap<String, String>();

	/**
	 * Instantiates a new application profile reader for catalogue resolver.
	 *
	 * @param scope the scope
	 * @param useRootScope the use root scope
	 */
	public ApplicationProfileReaderForCatalogueResolver(String scope, boolean useRootScope) {
		this.scope = scope;
		this.secondaryType = SECONDARY_TYPE;
		this.resourceName = RESOURCE_NAME;
		this.useRootScope = useRootScope;
		this.readProfileFromInfrastrucure();
	}

	/**
	 * Read profile from infrastrucure and fills the map {@link #hashVreNameScope}
	 */
	private void readProfileFromInfrastrucure() {

			String queryString = GcubeQuery.getGcubeGenericResource(secondaryType, FIELD_TYPE.RESOURCE_NAME, resourceName);
			String originalScope = null;

			try {

				originalScope = ScopeProvider.instance.get();
				String discoveryScope = useRootScope?ScopeUtil.getInfrastructureNameFromScope(scope):scope;
				ScopeProvider.instance.set(discoveryScope);
				logger.info("Trying to fetch ApplicationProfile in the scope: "+discoveryScope+", SecondaryType: " + secondaryType + ", ResourceName: " +  resourceName);
				Query q = new QueryBox(queryString);
				DiscoveryClient<String> client = client();
			 	List<String> appProfile = client.submit(q);

				if (appProfile == null || appProfile.size() == 0)
					throw new ApplicationProfileNotFoundException("ApplicationProfile with SecondaryType: " + secondaryType + ", ResourceName: " +  resourceName +" is not registered in the scope: "+discoveryScope);
				else {
					try{
					String elem = appProfile.get(0);
					DocumentBuilder docBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();
					Node node = docBuilder.parse(new InputSource(new StringReader(elem))).getDocumentElement();
					XPathHelper helper = new XPathHelper(node);
					List<String> scopes = helper.evaluate(RESOURCE_PROFILE_BODY_END_POINT_SCOPE_TEXT);
					for (String scopeFound : scopes) {
						List<String> vreName = helper.evaluate("/Resource/Profile/Body/EndPoint[SCOPE='"+scopeFound.toString()+"']/VRE_NAME/text()");
						logger.info("For scope: "+scopeFound+", found VRE_NAME "+vreName);
						hashVreNameScope.put(vreName.get(0), scopeFound);
					}
					}catch(Exception e){
						throw new ApplicationProfileNotFoundException("Error during parsing application profile with resource name: "+resourceName + " in the scope: "+scope.toString());
					}
			}

		} catch (Exception e) {
			logger.error("Error while trying to fetch applicationProfile profile from the infrastructure", e);
		}finally{
			if(originalScope!=null && !originalScope.isEmpty()){
				ScopeProvider.instance.set(originalScope);
				logger.info("scope provider setted to orginal scope: "+originalScope);
			}else{
				ScopeProvider.instance.reset();
				logger.info("scope provider reset");
			}
		}

	}


	/**
	 * @return the hashVreNameScope
	 */
	public Map<String, String> getHashVreNameScope() {

		return hashVreNameScope;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("ApplicationProfileReaderForCatalogueResolver [logger=");
		builder.append(logger);
		builder.append(", secondaryType=");
		builder.append(secondaryType);
		builder.append(", resourceName=");
		builder.append(resourceName);
		builder.append(", scope=");
		builder.append(scope);
		builder.append(", useRootScope=");
		builder.append(useRootScope);
		builder.append(", hashVreNameScope=");
		builder.append(hashVreNameScope);
		builder.append("]");
		return builder.toString();
	}


	/*public static void main(String[] args) {
		String scope ="/gcube";
		ApplicationProfileReaderForCatalogueResolver reader = new ApplicationProfileReaderForCatalogueResolver(scope, true);
		System.out.println(reader);
	}*/


}
