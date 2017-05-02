/**
 * 
 */
package org.gcube.informationsystem.resource_checker;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.common.resources.gcore.HostingNode;
import org.gcube.common.resources.gcore.Resource;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.Software;
import org.gcube.common.resources.gcore.utils.XPathHelper;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.informationsystem.publisher.RegistryPublisher;
import org.gcube.informationsystem.publisher.RegistryPublisherFactory;
import org.gcube.informationsystem.resource_checker.utils.BasicFunctionalitiesMandatoryReader;
import org.gcube.informationsystem.resource_checker.utils.BasicFunctionalityBean;
import org.gcube.informationsystem.resource_checker.utils.RetrieveContextsList;
import org.gcube.informationsystem.resource_checker.utils.SendNotification;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.resources.discovery.icclient.ICFactory;
import org.gcube.vremanagement.executor.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * The resource-checker-se-plugin implementation class.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
@SuppressWarnings("rawtypes")
public class ResourceCheckerPlugin extends Plugin<ResourceCheckerPluginDeclaration>{

	private static final int SECONDS2WAIT = 2; // seconds to wait among IS requests
	private static final String INITIAL_ERRORS_STATEMENT = "Other errors: ";
	public static final String ROLE_TO_NOTIFY = "role";
	private static final String NAGIOS_PROPERTY_FILE = "/META-INF/plugin_resources/nagios-report-location.properties";
	private static final Logger logger = LoggerFactory.getLogger(ResourceCheckerPlugin.class);

	public ResourceCheckerPlugin(ResourceCheckerPluginDeclaration pluginDeclaration){

		super(pluginDeclaration);
		logger.info("Constructor invoked");

	}

	/**{@inheritDoc}
	 * @throws Exception */
	@Override
	public void launch(Map<String, Object> inputs) throws Exception{

		String originalContext = ScopeProvider.instance.get();
		try{

			logger.info("Starting plugin with inputs: " + inputs);

			List<BasicFunctionalityBean> mandatoryFunctionalities = new BasicFunctionalitiesMandatoryReader().getMandatoryFunctionalities();
			List<String> contexts = RetrieveContextsList.getContexts();

			logger.info("Contexts are " + contexts); 
			logger.info("Mandatory functionalities are " + mandatoryFunctionalities); 

			Map<String, List<BasicFunctionalityBean>> missingResourcesPerContext = new HashMap<String, List<BasicFunctionalityBean>>(contexts.size());

			String otherFailures = INITIAL_ERRORS_STATEMENT;
			DiscoveryClient client = ICFactory.client();
			DocumentBuilder docBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();

			Map<String, String> resourceToIdentifierMap = new HashMap<String, String>(mandatoryFunctionalities.size());
			Set<String> missingResourcesKey = new HashSet<String>(mandatoryFunctionalities.size());

			for (String context : contexts) {
				// switch context
				ScopeProvider.instance.set(context);
				for (BasicFunctionalityBean service : mandatoryFunctionalities) {
					try{

						String resourceId = null;
						String resourceKey = service.getName() + ":" + service.getCategory();
						if((resourceId = isServicePresent(service, client, docBuilder)) == null){

							List<BasicFunctionalityBean> missingServices = null;
							if(missingResourcesPerContext.containsKey(context))
								missingServices = missingResourcesPerContext.get(context);
							else
								missingServices = new ArrayList<BasicFunctionalityBean>();

							// add among missing resources
							missingResourcesKey.add(resourceKey);

							missingServices.add(service);
							missingResourcesPerContext.put(context, missingServices);

						}else{
							resourceToIdentifierMap.put(resourceKey, resourceId);
						}

						Thread.sleep(1000 * SECONDS2WAIT);

					}catch(Exception e){
						if(!(e instanceof InterruptedException)){
							logger.warn("An error arose when checking for resource " + service + " into context " + context);
							otherFailures += "\nAn error arose while checking for resource with name/category " 
									+ service.getName() + "/" + service.getCategory() + " into context " + context + "(stack trace is : " + e.getMessage() + ")";
						}
					}
				}
			}

			if(otherFailures.equals(INITIAL_ERRORS_STATEMENT))
				otherFailures += "none.\n";
			else
				otherFailures += "\n";

			// write nagios report
			List<String> identifiers = new ArrayList<String>(mandatoryFunctionalities.size());
			for (String key : missingResourcesKey) {
				identifiers.add(resourceToIdentifierMap.get(key));
			}

			writeReport4Nagios(identifiers);

			if(!identifiers.isEmpty()){
				try{
					logger.info("Going to execute code to re-add them");
					List<String> vosContexts = new ArrayList<String>();
					RetrieveContextsList.loadVOs(vosContexts);
					accessPointQuery(vosContexts, identifiers , ServiceEndpoint.class);
					otherFailures = "\nThe above resources have been readded in the scopes they were missing!.";
				}catch(Exception e){
					otherFailures = "\n\nMoreover, while trying to re-add the following identifiers " + identifiers + " there was this error " + e.getMessage();
					logger.error("While readding the resources this error arose", e);
				}
			}

			// if there are missing resources, notify administrators via mail
			if(!missingResourcesPerContext.isEmpty() || !otherFailures.equals(INITIAL_ERRORS_STATEMENT + "none.\n"))
				SendNotification.sendMessage(missingResourcesPerContext, otherFailures, (String)inputs.get(ROLE_TO_NOTIFY));

			logger.info("Plugin's execution ended. Map of not available services per scope is the following: \n" + missingResourcesPerContext);
		}catch(Exception e){
			logger.error("The following error arose during the execution of the plugin", e);
		}finally{
			logger.debug("Resetting original scope in thread");
			ScopeProvider.instance.set(originalContext);
		}
	}

	/**
	 * Check if a given resource is present or is missing.
	 * @param service
	 * @param client
	 * @param docBuilder 
	 * @return the identifier of the resource
	 * @throws Exception
	 */
	private String isServicePresent(BasicFunctionalityBean service, DiscoveryClient client, DocumentBuilder docBuilder) throws Exception {

		Class classFor = service.getType() == null? ServiceEndpoint.class : service.getType(); // default is service end point
		SimpleQuery q = ICFactory.queryFor(classFor);

		List result = null;
		if(classFor.equals(ServiceEndpoint.class)){

			q.addCondition("$resource/Profile/Name/text() eq '"+ service.getName() +"'");
			q.addCondition("$resource/Profile/Category/text() eq '"+ service.getCategory() +"'");
			result = client.submit(q);

		}else if(classFor.equals(GCoreEndpoint.class)){

			// TODO
			logger.error("There is no implementation yet to check for GCoreEndpoint");
			throw new Exception("There is no implementation yet to check for GCoreEndpoint");

		}else if(classFor.equals(GenericResource.class)){

			// TODO
			logger.error("There is no implementation yet to check for GenericResource");
			throw new Exception("There is no implementation yet to check for GenericResource");

		}else if(classFor.equals(HostingNode.class)){

			// TODO
			logger.error("There is no implementation yet to check for HostingNode");
			throw new Exception("There is no implementation yet to check for HostingNode");

		}else if(classFor.equals(Software.class)){

			// TODO
			logger.error("There is no implementation yet to check for Software");
			throw new Exception("There is no implementation yet to check for Software");

		}else
			throw new Exception("Unable to check resource because its type is missing " + service);


		if(result == null || result.isEmpty())
			return null;
		else{

			String elem = (String) result.get(0);
			Node node = docBuilder.parse(new InputSource(new StringReader(elem))).getDocumentElement();
			XPathHelper helper = new XPathHelper(node);

			List<String> currValue = null;
			currValue = helper.evaluate("/Resource/ID/text()");
			if (currValue != null && currValue.size() > 0) {
				logger.debug("Id is " + currValue.get(0));
				return currValue.get(0);
			} 
			else throw new Exception("ID property is missing!");

		}
	}

	/**
	 * Write a report for nagios
	 * @throws IOException 
	 */
	private void writeReport4Nagios(List<String> idsMissingResources) throws Exception{

		try{
			Properties prop = new Properties();
			prop.load(getClass().getResourceAsStream(NAGIOS_PROPERTY_FILE));
			String location = prop.getProperty("location");

			logger.trace("File location for nagios is " + location);

			File f =  new File(location);
			f.getParentFile().mkdirs();
			f.delete();
			f.createNewFile();

			FileWriter writer = new FileWriter(f, false);

			if (idsMissingResources == null || idsMissingResources.isEmpty()) {
				writer.write("none");
			}else{
				for (String idMissing : idsMissingResources) {
					writer.write(idMissing + "\n");
				}
			}
			writer.close();
		}catch(Exception e){
			logger.error("Failed to write the report file for nagios", e);
		}
	}


	/**
	 * Add the missing ids.
	 * @param voScopes
	 * @param resourceIds
	 * @param resourceType
	 */
	private <T extends Resource>  void accessPointQuery(List<String> voScopes, List<String> resourceIds, Class<T> resourceType){

		String currentScope = voScopes.get(0);
		HashSet<String> scopeSet = new HashSet<String>();

		SimpleQuery queryVRE = ICFactory.queryFor(GenericResource.class);
		queryVRE.addCondition("$resource/Profile/SecondaryType/text() = 'VRE'");
		queryVRE.setResult("$resource/Profile/Body/Scope/string()");
		DiscoveryClient<String> vREScopeClient = ICFactory.client();

		scopeSet.addAll(voScopes);

		for (String scope: voScopes){
			ScopeProvider.instance.set(scope);
			List<String> vresscope = vREScopeClient.submit(queryVRE);
			System.out.println("found "+vresscope.size()+ " vres in "+scope);
			scopeSet.addAll(vresscope);
		}

		for (String resourceId : resourceIds){

			ScopeProvider.instance.set(currentScope);
			SimpleQuery query = ICFactory.queryFor(resourceType);
			DiscoveryClient<T> client = ICFactory.clientFor(resourceType);
			query.addCondition("$resource/ID/text() = '"+resourceId+"'");
			T resource = client.submit(query).get(0);

			resource.scopes().asCollection().retainAll(Collections.emptyList());
			resource.scopes().asCollection().addAll(scopeSet);

			RegistryPublisher pub = RegistryPublisherFactory.create();
			pub.vosUpdate(resource);
		}

	}

	/**{@inheritDoc}*/
	@Override
	protected void onStop() throws Exception {
		logger.debug("onStop() invoked");
		Thread.currentThread().interrupt();
	}

}