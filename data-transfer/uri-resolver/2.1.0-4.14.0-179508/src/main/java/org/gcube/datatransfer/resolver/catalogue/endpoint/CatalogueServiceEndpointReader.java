/**
 *
 */
package org.gcube.datatransfer.resolver.catalogue.endpoint;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogueImpl;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class CatalogueServiceEndpointReader.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Apr 20, 2018
 */
public class CatalogueServiceEndpointReader {

	// data catalogue info
	private final static String RUNTIME_CATALOGUE_RESOURCE_NAME = "CKanDataCatalogue";
	private final static String PLATFORM_CATALOGUE_NAME = "Tomcat";
	private final static String CKAN_IS_ROOT_MASTER = "IS_ROOT_MASTER";
	private static final Logger logger = LoggerFactory.getLogger(CatalogueServiceEndpointReader.class);


	/** A map to cache couple SCOPE - THE CKAN PORTLET URL OPERATING IN THE SCOPE*/
	private static Map<String, String> cacheCkanDataCatalogue = new HashMap<String, String>();


	/**
	 * Instantiates a new catalogue service endpoint reader.
	 */
	public CatalogueServiceEndpointReader(){
	}


	/**
	 * Retrieve endpoints information from IS for DataCatalogue URL.
	 *
	 * @return list of endpoints for ckan data catalogue
	 * @throws Exception the exception
	 */
	public static List<ServiceEndpoint> getConfigurationFromISFORCatalogueUrl() throws Exception{

		logger.info("Searching SE "+RUNTIME_CATALOGUE_RESOURCE_NAME+" configurations in the scope: "+ScopeProvider.instance.get());

		SimpleQuery query = queryFor(ServiceEndpoint.class);
		query.addCondition("$resource/Profile/Name/text() eq '"+ RUNTIME_CATALOGUE_RESOURCE_NAME +"'");
		query.addCondition("$resource/Profile/Platform/Name/text() eq '"+ PLATFORM_CATALOGUE_NAME +"'");
		query.addVariable("$prop", "$resource/Profile/AccessPoint/Properties/Property")
	       .addCondition("$prop/Name/text() eq '"+CKAN_IS_ROOT_MASTER+"'")
	       .addCondition("$prop/Value/text() eq 'true'");

		logger.info("Performing query 1 with property '"+CKAN_IS_ROOT_MASTER+"': "+query);

		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
		List<ServiceEndpoint> toReturn = client.submit(query);

		logger.info("The query 1 returned "+toReturn.size()+ " ServiceEndpoint/s");

		if(toReturn.size()==0){
			logger.info("NO "+RUNTIME_CATALOGUE_RESOURCE_NAME+" having "+CKAN_IS_ROOT_MASTER+" property");
			query = queryFor(ServiceEndpoint.class);
			query.addCondition("$resource/Profile/Name/text() eq '"+ RUNTIME_CATALOGUE_RESOURCE_NAME +"'");
			query.addCondition("$resource/Profile/Platform/Name/text() eq '"+ PLATFORM_CATALOGUE_NAME +"'");
			logger.info("Performing query 2: "+query);
			client = clientFor(ServiceEndpoint.class);
			toReturn = client.submit(query);
			logger.info("The query 2 returned "+toReturn.size()+ " ServiceEndpoint/s");
		}

		return toReturn;

	}

	/**
	 * Gets the catalogue url.
	 *
	 * @return the catalogue url
	 */
	public static String getCatalogueUrl() {
		String scope = ScopeProvider.instance.get();
		logger.debug("Getting Catalogue URL for scope: "+scope +" read from CacheCkanDataCatalogue");
		String catalogueURLForScope = cacheCkanDataCatalogue.get(scope);

		if(catalogueURLForScope==null){
			logger.debug("Catalogue URL not found in cache, loading from IS");

			try{
				logger.debug("Instancing again the scope provider with scope value: "+scope);
				ScopeProvider.instance.set(scope);
				DataCatalogueImpl utilCKAN = new DataCatalogueImpl(scope);
				catalogueURLForScope = utilCKAN.getCatalogueUrl();
				if(catalogueURLForScope==null)
					throw new Exception("No cataluge url found in the scope: "+scope);

				cacheCkanDataCatalogue.put(scope, catalogueURLForScope);
			}catch(Exception e){
				logger.error("Error on getting the catalogue url in the scope: "+scope, e);
			}

		}
		logger.info("Returning Catalogue URL: "+catalogueURLForScope +" for the scope: "+scope);
		return catalogueURLForScope;
	}



	/**
	 * Gets the data catalogue impl.
	 *
	 * @return the data catalogue impl
	 * @throws Exception
	 */
	public static DataCatalogueImpl getDataCatalogueImpl() throws Exception {
		String scope = ScopeProvider.instance.get();
		return new DataCatalogueImpl(scope);
	}




//	/**
//	 * The main method.
//	 *
//	 * @param args the arguments
//	 */
//	public static void main(String[] args) {
//
//		String scope = "/d4science.research-infrastructures.eu/gCubeApps/BiodiversityLab";
//		ScopeProvider.instance.set(scope);
//		try {
//			String catalogueURL = CatalogueServiceEndpointReader.getCatalogueUrl(scope);
//			System.out.println(catalogueURL);
//		}
//		catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}
