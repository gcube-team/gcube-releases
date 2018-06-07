package org.gcube.informationsystem.resource_checker;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.informationsystem.publisher.RegistryPublisher;
import org.gcube.informationsystem.publisher.RegistryPublisherFactory;
import org.gcube.informationsystem.resource_checker.beans.BasicFunctionalityBean;
import org.gcube.informationsystem.resource_checker.beans.ContextLevel;
import org.gcube.informationsystem.resource_checker.beans.OperationLevel;
import org.gcube.informationsystem.resource_checker.utils.BasicFunctionalitiesMandatoryReader;
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
	private static final DiscoveryClient client = ICFactory.client();

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
			DocumentBuilder docBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();

			// write nagios report
			Set<String> missingResourceIds = new HashSet<String>(mandatoryFunctionalities.size());
			Map<String, BasicFunctionalityBean> idToResource = new HashMap<String, BasicFunctionalityBean>(mandatoryFunctionalities.size());

			boolean missingToReadArePresent = false;

			// loop over contexts and resources
			for (String context : contexts) {
				ScopeProvider.instance.set(context);
				for (BasicFunctionalityBean service : mandatoryFunctionalities) {

					// check if this resource should be checked in this scope
					ScopeBean sb = new ScopeBean(context);
					ContextLevel contextDepth = service.getCtxLevel();

					if(contextDepth.equals(ContextLevel.VO) && sb.type().equals(ScopeBean.Type.VRE)){
						logger.info("Resource " + service + " doesn't need to be checked in a VRE context (" + context + ")");
						continue;
					}
					try{
						String resourceId = null;
						if((resourceId = isServicePresent(service, client, docBuilder)) != null){
							service.setResourceId(resourceId);
							idToResource.put(resourceId, service);
						}else{
							List<BasicFunctionalityBean> missingServices = missingResourcesPerContext.containsKey(context) ? 
									missingResourcesPerContext.get(context) : new ArrayList<BasicFunctionalityBean>();
									missingServices.add(service);
									missingResourcesPerContext.put(context, missingServices);

									if(service.getOpLevel().equals(OperationLevel.ALERT_READD))
										missingToReadArePresent = true;
						}
						Thread.sleep(1000 * SECONDS2WAIT);
					}catch(Exception e){
						if(!(e instanceof InterruptedException)){
							logger.warn("An error arose when checking for resource " + service + " into context " + context);
							otherFailures += "\nAn error arose while checking for resource with name/category " 
									+ service.getName() + "/" + service.getCategory() + " and type " + service.getType().getCanonicalName() + " into context " + context + "(stack trace is : " + e.getMessage() + ")";
						}
					}
				}
			}

			if(otherFailures.equals(INITIAL_ERRORS_STATEMENT))
				otherFailures += "none.\n";
			else
				otherFailures += "\n";

			// evaluate missing resources from the map
			evaluateMissingIds(missingResourceIds, missingResourcesPerContext);

			// update the file used by nagios
			writeReport4Nagios(missingResourceIds);

			if(!missingResourceIds.isEmpty() && missingToReadArePresent){
				logger.info("Going to perform add and alert or just alert of missing resources");
				otherFailures = "Re-add operation on some resources has been executed. This is the result:";
				for(String resId: missingResourceIds){

					// check if it must be readded or just alert is needed
					if(idToResource.get(resId).getOpLevel().equals(OperationLevel.ALERT)){
						logger.info("Resource " + idToResource.get(resId) + " doesn't need to be readded into the scopes it is missing");
						continue;
					}

					try{
						readdResource(contexts, resId, idToResource.get(resId));
						otherFailures += "\n- resource with identifier " + resId + " has been readded in the scopes it was missing;";
					}catch(Exception e){
						otherFailures += "\n- while trying to re-add the resource with the following identifier " + resId + " there was this error: " + e.getMessage() +";";
						logger.error("While readding a resource this error arose", e);
					}
				}
			}

			// if there are missing resources, notify administrators via mail
			if(!missingResourcesPerContext.isEmpty() || !otherFailures.equals(INITIAL_ERRORS_STATEMENT + "none.\n"))
				SendNotification.sendMessage(missingResourcesPerContext, otherFailures, (String)inputs.get(ROLE_TO_NOTIFY));

			logger.info("Plugin's execution ended.");

		}catch(Exception e){
			logger.error("The following error arose during the execution of the plugin", e);
		}finally{
			logger.debug("Resetting original scope in thread");
			ScopeProvider.instance.set(originalContext);
		}
	}

	/**
	 * Fetch the ids of the missing resources
	 * @param missingResourceIds
	 * @param missingResourcesPerContext
	 */
	private void evaluateMissingIds(Set<String> missingResourceIds,
			Map<String, List<BasicFunctionalityBean>> missingResourcesPerContext) {

		Iterator<Entry<String, List<BasicFunctionalityBean>>> iterator = missingResourcesPerContext.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<java.lang.String, java.util.List<org.gcube.informationsystem.resource_checker.beans.BasicFunctionalityBean>> entry = (Map.Entry<java.lang.String, java.util.List<org.gcube.informationsystem.resource_checker.beans.BasicFunctionalityBean>>) iterator
					.next();
			List<BasicFunctionalityBean> missingResources = entry.getValue();
			for (BasicFunctionalityBean basicFunctionalityBean : missingResources) {
				missingResourceIds.add(basicFunctionalityBean.getResourceId());
			}
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

			q.addCondition("$resource/Profile/ServiceName/text() eq '"+ service.getName() +"'");
			q.addCondition("$resource/Profile/ServiceClass/text() eq '"+ service.getCategory() +"'");
			result = client.submit(q);

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
			else 
				throw new Exception("ID property is missing!");

		}
	}

	/**
	 * Write a report for nagios
	 * @throws IOException 
	 */
	private void writeReport4Nagios(Set<String> idsMissingResources) throws Exception{

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
				writer.write("none\n");
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
	 * Add the missing resources back.
	 * @param voScopes
	 * @param resourceIds
	 * @param idToResource 
	 * @param resourceType
	 */
	@SuppressWarnings("unchecked")
	private <T extends Resource>  void readdResource(List<String> allScopes, String resourceId, BasicFunctionalityBean service){

		logger.info("Adding back resource with id " + resourceId + "("+ service + ") to scopes " + allScopes);

		String currentScope = allScopes.get(0); // infrastructure root
		ScopeProvider.instance.set(currentScope);

		SimpleQuery query = ICFactory.queryFor(service.getType());
		DiscoveryClient<T> client = ICFactory.clientFor(service.getType());
		query.addCondition("$resource/ID/text() = '"+resourceId+"'");
		T resource = client.submit(query).get(0);

		resource.scopes().asCollection().retainAll(Collections.emptyList()); // remove all scopes
		resource.scopes().asCollection().addAll(allScopes); // add them all again

		RegistryPublisher pub = RegistryPublisherFactory.create();
		pub.vosUpdate(resource);


	}

	/**{@inheritDoc}*/
	@Override
	protected void onStop() throws Exception {
		Thread.currentThread().interrupt();
		logger.debug("onStop() invoked");
	}

}
