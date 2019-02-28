package org.gcube.datatransfer.resolver.catalogue.resource;
import static org.gcube.resources.discovery.icclient.ICFactory.client;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.gcube.common.resources.gcore.utils.XPathHelper;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.Query;
import org.gcube.resources.discovery.client.queries.impl.QueryBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;


/**
 * The Class GetAllInfrastructureVREs.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Nov 8, 2018
 */
/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Nov 8, 2018
 */
public class GetAllInfrastructureVREs {

	public static Logger logger = LoggerFactory.getLogger(GetAllInfrastructureVREs.class);

	protected static final String RESOURCE_PROFILE_NAME_TEXT = "/Resource/Profile/Name/text()";

	/**
	 * Load map of vre name to scope.
	 *
	 * @param rootScope the root scope
	 * @return the map of binding between (VRE_NAME, FULL_SCOPE_OF_VRE_NAME)
	 * @throws Exception
	 */
	public static Map<String, String> loadMapOFVreNameToScope(String rootScope) throws Exception{

		String originalScope = 	ScopeProvider.instance.get();

		try{

			ScopeProvider.instance.set(rootScope);
			String secondaryType = "INFRASTRUCTURE";
			List<String> listVOScopes = getListOfVOScopes(secondaryType);
			Map<String, String> vreNameFullScope = new HashMap<String,String>();
			logger.info("Searching for secondaryType="+secondaryType +" scope/s found is/are: " +listVOScopes);

			secondaryType = "VRE";
			//int noVOTypeCount = 0;
			for (String voScope : listVOScopes) {
				int count = voScope.length() - voScope.replace("/", "").length();
				//IS A VO
				if(count==2){
					logger.info(voScope +" is a VO...");
					ScopeProvider.instance.set(voScope);
					List<String> listVREs = getListOfResourcesForSecondaryType(secondaryType);
					logger.debug("VREs found for VO "+voScope+ " is/are "+listVREs.size()+ ": "+listVREs);
					for (String vreName : listVREs) {
						String vreScope = String.format("%s/%s", voScope,vreName);
						vreNameFullScope.put(vreName, vreScope);
					}

				}else{
					//noVOTypeCount++;
					logger.info(voScope +" is not a VO, skipping it");
				}
			}

			/*System.out.println("Total VO is: "+(listVOScopes.size()+noVOTypeCount));
			for (String vreName : vreNameFullScope.keySet()) {
				System.out.println("VRE Name: "+vreName + " has scope: "+vreNameFullScope.get(vreName));
			}*/

			logger.info("Total VRE is: "+vreNameFullScope.size());
			return vreNameFullScope;

		}catch(Exception e ){
			throw new Exception("Error on loading the map of VRE nameto scope: ", e);
		}
		finally{
			if(originalScope!=null && !originalScope.isEmpty()){
				ScopeProvider.instance.set(originalScope);
			}else
				ScopeProvider.instance.reset();
		}
	}


	/**
	 * Gets the list of resources for secondary type.
	 *
	 * @param secondaryType the secondary type
	 * @return the list of resource names for the input secondary type
	 */
	private static List<String> getListOfResourcesForSecondaryType(String secondaryType) {

		String queryString = "for $profile in collection('/db/Profiles/GenericResource')//Resource " +
						"where $profile/Profile/SecondaryType/string() eq '"+secondaryType+"' return $profile";

		List<String> listResourceName = new ArrayList<String>();

		try {
			logger.info("Trying to fetch GenericResource in the scope: "+ScopeProvider.instance.get()+", SecondaryType: " + secondaryType);
			Query q = new QueryBox(queryString);
			DiscoveryClient<String> client = client();
			List<String> listGenericResources = client.submit(q);

			logger.info("# of GenericResource returned are: "+listGenericResources.size());

			if (listGenericResources == null || listGenericResources.size() == 0)
				throw new Exception("GenericResource with SecondaryType: " + secondaryType + ", is not registered in the scope: "+ScopeProvider.instance.get());
			else {


					for (String genericResource : listGenericResources) {
						try{
							String elem = genericResource;
							DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
							Document document = docBuilder.parse(new InputSource(new StringReader(elem)));
							Element rootElement = document.getDocumentElement();
							XPathHelper helper = new XPathHelper(rootElement);
							List<String> resourceNames = helper.evaluate(RESOURCE_PROFILE_NAME_TEXT);

							if(resourceNames!=null && resourceNames.size()>0)
								listResourceName.add(resourceNames.get(0));

						}catch(Exception e){
							throw new Exception("Error during parsing the generic resource: "+genericResource + " in the scope: "+ScopeProvider.instance.get());
						}
					}

			}

		} catch (Exception e) {
			logger.error("Error while trying to fetch generic resource from the infrastructure", e);
		}

		return listResourceName;

	}


	/**
	 * Gets the list of resources for secondary type.
	 *
	 * @param secondaryType the secondary type
	 * @return the list of resource names for the input secondary type
	 */
	private static List<String> getListOfVOScopes(String secondaryType) {

		String queryString = "for $profile in collection('/db/Profiles/GenericResource')//Resource " +
						"where $profile/Profile/SecondaryType/string() eq '"+secondaryType+"' return $profile";

		List<String> listOfVOScopes = new ArrayList<String>();

		try {
			logger.info("Trying to fetch GenericResource in the scope: "+ScopeProvider.instance.get()+", SecondaryType: " + secondaryType);
			Query q = new QueryBox(queryString);
			DiscoveryClient<String> client = client();
			List<String> listGenericResources = client.submit(q);

			logger.info("# of GenericResource returned searching for secondaryType= "+secondaryType+" is/are: "+listGenericResources.size());

			if (listGenericResources == null || listGenericResources.size() == 0)
				throw new Exception("GenericResource with SecondaryType: " + secondaryType + ", is not registered in the scope: "+ScopeProvider.instance.get());
			else {


					for (String genericResource : listGenericResources) {
						try{
							String elem = genericResource;
							DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
							Document document = docBuilder.parse(new InputSource(new StringReader(elem)));
							Element rootElement = document.getDocumentElement();
							XPathHelper helper = new XPathHelper(rootElement);
//							List<String> resourceNames = helper.evaluate(RESOURCE_PROFILE_NAME_TEXT);
//
//							if(resourceNames!=null && resourceNames.size()>0)
//								listResourceName.add(resourceNames.get(0));

							List<String> scopes = helper.evaluate("/Resource/Profile/Body/infrastructures/infrastructure/vos/vo/scope/text()");
							for (String scopeFound : scopes) {
								listOfVOScopes.add(scopeFound);
							}

						}catch(Exception e){
							throw new Exception("Error during parsing the generic resource: "+genericResource + " in the scope: "+ScopeProvider.instance.get());
						}
					}

			}

		} catch (Exception e) {
			logger.error("Error while trying to fetch generic resource from the infrastructure", e);
		}

		return listOfVOScopes;

	}
}
