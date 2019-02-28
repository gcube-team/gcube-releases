/**
 *
 */

package org.gcube.datatransfer.resolver.catalogue.resource;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.gcube.resources.discovery.icclient.ICFactory.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URI;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.gcube.common.resources.gcore.utils.XPathHelper;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogueImpl;
import org.gcube.datatransfer.resolver.applicationprofile.ApplicationProfileNotFoundException;
import org.gcube.datatransfer.resolver.catalogue.endpoint.CatalogueServiceEndpointReader;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.Query;
import org.gcube.resources.discovery.client.queries.impl.QueryBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import eu.trentorise.opendata.jackan.CkanClient;
import eu.trentorise.opendata.jackan.exceptions.CkanException;
import eu.trentorise.opendata.jackan.model.CkanDataset;


/**
 * The Class CkanCatalogueConfigurationsReader.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Apr 20, 2018
 */
public class CkanCatalogueConfigurationsReader {

	private static final Logger logger = LoggerFactory.getLogger(CkanCatalogueConfigurationsReader.class);
	public final static String APPLICATION_PROFILE_NAME = "CkanPortlet";
	private static final String DATACATALOGUECONFIGURATION_PROPERTIES = "datacatalogueconfiguration.properties";



	/**
	 * Load catalogue end points.
	 *
	 * @return the gateway ckan catalogue reference
	 * @throws Exception the exception
	 */
	public static GatewayCKANCatalogueReference loadCatalogueEndPoints() throws Exception{
		GatewayCKANCatalogueReference links = new GatewayCKANCatalogueReference();
		links.setScope(ScopeProvider.instance.get());

		DataCatalogueImpl catalogueImpl = CatalogueServiceEndpointReader.getDataCatalogueImpl();

		String privatePortletURL = catalogueImpl.getPortletUrl();
		links.setPrivatePortletURL(privatePortletURL);

		//Building public URL from private portlet URL
		try{
			URI toURL = new URI(privatePortletURL);
			String publicURL = privatePortletURL.startsWith("https://")?"https://"+toURL.getHost():"http://"+toURL.getHost();
			//It returns the string "catalogue"
			String prefixToPublicCtlg = getRelativeURLToCatalogue();
			//Replacing for example "ckan-bb" with "catalogue-bb"
			String publicCatalogueName = extractCatalogueName(catalogueImpl.getCatalogueUrl(), prefixToPublicCtlg);
			links.setPublicPortletURL(publicURL+"/"+publicCatalogueName);
		}catch(Exception e){
			logger.warn("Erron on generating public catalogue URL from private URL: "+privatePortletURL, e);
		}

		//Getting the CKAN Portlet URL for current scope
		try{
			String ckanPortletURL = catalogueImpl.getCatalogueUrl();
			links.setCkanURL(ckanPortletURL);
		}catch(Exception e){
			logger.warn("Erron on getting CKAN Porlet URL for scope: "+ScopeProvider.instance.get(), e);
		}

		return links;
	}

	/**
	 * Extract catalogue name.
	 *
	 * @param privateCKANCatalogueURL the private ckan catalogue url
	 * @param replaceCKANWith the replace ckan with
	 * @return the string
	 */
	public static String extractCatalogueName(String privateCKANCatalogueURL, String replaceCKANWith){

		privateCKANCatalogueURL = privateCKANCatalogueURL.replaceFirst("https://ckan", replaceCKANWith);
		privateCKANCatalogueURL = privateCKANCatalogueURL.replaceFirst("http://ckan", replaceCKANWith);
		return privateCKANCatalogueURL.substring(0,privateCKANCatalogueURL.indexOf("."));

	}


	/**
	 * Retrieve a ckan dataset given its id. The CkanClient is used, without api key. The result is null also when the dataset is private.
	 *
	 * @param datasetIdorName the dataset idor name
	 * @param catalogueURL the catalogue url
	 * @return the dataset
	 * @throws Exception the exception
	 */
	public static CkanDataset getDataset(String datasetIdorName, String catalogueURL) throws Exception{
		logger.info("Performing request GET CKAN dataset with id: " + datasetIdorName);

		// checks
		checkNotNull(datasetIdorName);
		checkArgument(!datasetIdorName.isEmpty());
		try{
			CkanClient client = new CkanClient(catalogueURL);
			return client.getDataset(datasetIdorName);
		}catch(CkanException e){
			logger.info("Getting dataset "+datasetIdorName+" thrown a CkanException, returning null");
			return null;
		}

	}

	/**
	 * Gets the portlet url for scope from is.
	 *
	 * @return the portlet url for scope from is
	 * @throws Exception the exception
	 */
	protected static String getPortletUrlForScopeFromIS() throws Exception {

		String scope = ScopeProvider.instance.get();
		logger.debug("Trying to fetch applicationProfile profile from the infrastructure for " +
			APPLICATION_PROFILE_NAME + " scope: " + scope);
		try {
			Query q =
				new QueryBox(
					"for $profile in collection('/db/Profiles/GenericResource')//Resource " +
						"where $profile/Profile/SecondaryType/string() eq 'ApplicationProfile' and  $profile/Profile/Name/string() " +
						" eq '" +
						APPLICATION_PROFILE_NAME +
						"'" +
						"return $profile");
			DiscoveryClient<String> client = client();
			List<String> appProfile = client.submit(q);
			if (appProfile == null || appProfile.size() == 0)
				throw new ApplicationProfileNotFoundException(
					"Your applicationProfile is not registered in the infrastructure");
			else {
				String elem = appProfile.get(0);
				DocumentBuilder docBuilder =
					DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Node node = docBuilder.parse(new InputSource(new StringReader(elem))).getDocumentElement();
				XPathHelper helper = new XPathHelper(node);
				List<String> currValue = null;
				currValue =
					helper.evaluate("/Resource/Profile/Body/url/text()");
				if (currValue != null && currValue.size() > 0) {
					logger.debug("CKAN Portlet url found is " + currValue.get(0));
					return currValue.get(0);
				}
			}
		}
		catch (Exception e) {
			throw new Exception("Error while trying to fetch applicationProfile profile for name "+APPLICATION_PROFILE_NAME+"from the infrastructure, using scope: "+scope);
		}

		return null;
	}

	/**
	 * Gets the relative url to catalogue.
	 *
	 * @return the relative url to catalogue
	 */
	private static String getRelativeURLToCatalogue(){
		Properties prop = new Properties();
		String relativeURLToCatalogue = null;
		try {
			InputStream in = CkanCatalogueConfigurationsReader.class.getResourceAsStream(DATACATALOGUECONFIGURATION_PROPERTIES);
			// load a properties file
			prop.load(in);
			// get the property value
			relativeURLToCatalogue = prop.getProperty("PORTAL_RELATIVE_URL_TO_CATALOGUE");

			if(relativeURLToCatalogue==null || relativeURLToCatalogue.isEmpty())
				return "catalogue";

		} catch (IOException e) {
			logger.error("An error occurred on read property file: "+DATACATALOGUECONFIGURATION_PROPERTIES, e);
		}
		return relativeURLToCatalogue;
	}

//	/**
//	 * The main method.
//	 *
//	 * @param args the arguments
//	 */
//	public static void main(String[] args) {
//
//		ScopeProvider.instance.set("/gcube/devsec/devVRE");
//		try {
//			GatewayCKANCatalogueReference links = CkanCatalogueConfigurationsReader.loadCatalogueEndPoints();
//			System.out.println(links);
//		}
//		catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}
