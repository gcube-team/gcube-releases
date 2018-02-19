package org.gcube.rest.resourcemanager.is.publisher.is;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.common.resources.gcore.Resource;
import org.gcube.common.resources.gcore.ResourceMediator;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.informationsystem.publisher.RegistryPublisherFactory;
import org.gcube.informationsystem.publisher.ScopedPublisher;
import org.gcube.informationsystem.publisher.exception.RegistryNotFoundException;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.rest.commons.helpers.XMLConverter;
import org.gcube.rest.commons.resourceawareservice.resources.GeneralResource;
import org.gcube.rest.commons.resourceawareservice.resources.RunInstance;
import org.gcube.rest.resourcemanager.publisher.ResourcePublisher;
import org.gcube.rest.resourcemanager.publisher.ResourcePublisherException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.google.common.collect.Lists;

@Singleton
public class PublisherISimpl<T extends GeneralResource> extends
		ResourcePublisher<T> {

	private static final Logger logger = LoggerFactory
			.getLogger(PublisherISimpl.class);

	
	@Inject
	public PublisherISimpl() {
	}
	
	public static void main(String[] args) throws RegistryNotFoundException {
		String[] ids = new String[]{ "4cd5a1bd-c09d-4bb6-ba8a-d06ec2249654", "b89ecbef-88d2-4c6d-8aee-cc389e796017" };
		String[] scopes = new String[]{"/gcube/devNext/NextNext", "/gcube/devNext", "/gcube"};

		for (String id : ids)
			for (String scope : scopes)
				remove(id, scope);
	}
	
	
//	public static void main(String[] args) throws JAXBException {
//		ScopedPublisher publish = RegistryPublisherFactory.scopedPublisher();
//
//		Resource r = null;
//
//		try {
//			GenericResource genericResource = XMLConverter.fromXML("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + 
//					"<Resource version=\"0.4.x\">\n" + 
//					"    <ID>workspace</ID>\n" + 
//					"    <Type>GenericResource</Type>\n" + 
////					"    <Scopes>\n" + 
////					"        <Scope>/gcube/devNext/NextNext</Scope>\n" + 
////					"        <Scope>/gcube/devNext</Scope>\n" + 
////					"    </Scopes>\n" + 
//					"    <Profile>\n" + 
//					"        <SecondaryType>DataSource</SecondaryType>\n" + 
//					"        <Name>Workspace</Name>\n" + 
//					"        <Description>Placeholder for the workspace treated as a DataSource</Description>\n" + 
//					"        <Body>\n" + 
//					"            <SourceProperties>\n" + 
//					"                <creationTime>2014-09-02T14:04:22.193+01:00</creationTime>\n" + 
//					"                <user>true</user>\n" + 
//					"                <type>workspace</type>\n" + 
//					"            </SourceProperties>\n" + 
//					"        </Body>\n" + 
//					"    </Profile>\n" + 
//					"</Resource>" 
//					, GenericResource.class);
//			r = publish.create(genericResource, Arrays.asList("/gcube/devNext/NextNext", "/gcube/devNext"));
//			
//			logger.info("resource updated with id : " + r.id());
//			
//			try {
//				String genericResourceXML = XMLConverter.convertToXML(genericResource);
//				logger.trace("generic resource to be updated is        : " + genericResourceXML);
//				
////				GenericResource isResource = getResourceByID(resource.getResourceID(), scope);
////				if (isResource == null){
////					logger.trace("generic resource has not been synced to IS yet. Comparisson is skipped");
////				} else {
////					String genericResourceXMLFromIS = XMLConverter.convertToXML(isResource);
////					
////					logger.trace("generic resource as retrieved from IS is : " + genericResourceXMLFromIS);
////				}
//				
//			} catch (Exception e) {
//				logger.error("error while converting resource to generic resource", e);
//			}
//			
//		} catch (RegistryNotFoundException e) {
////			throw new ResourcePublisherException(e);
//		}
//	
//	}
	
	public static void remove(String id, String scope) throws RegistryNotFoundException {
		System.out.println("ID: " + id);
		System.out.println("scope: " + scope);
		GCoreEndpoint gce = null;
		
		ScopeProvider.instance.set(scope);
		SimpleQuery query = queryFor(GCoreEndpoint.class);
		query.addCondition("$resource/ID/text() eq '" + id + "'");
		DiscoveryClient<GCoreEndpoint> client = clientFor(GCoreEndpoint.class);
		List<GCoreEndpoint> resources = client.submit(query);
		
		if (resources.size() > 0){
			gce = resources.get(0);
			
			List<String> scopes = Arrays.asList(scope);

			ScopedPublisher publish = RegistryPublisherFactory.scopedPublisher();
			publish.remove(gce, scopes);
		}
	}

	@Override
	public void deleteResource(String resourceID, String scope)
			throws ResourcePublisherException {
		logger.info("deleting resource with ID : " + resourceID + " from IS for scope : " + scope);
		
		
		GenericResource genericResource = getResourceByID(resourceID, scope);
		if (genericResource == null) {
			throw new ResourcePublisherException("resource with id : " + resourceID + " not found");
		}
		deleteFromScopes(genericResource, scope);
		
	}
	
	private void deleteFromScopes(GenericResource genericResource, String scope) throws ResourcePublisherException {
		ScopeProvider.instance.set(scope);

		List<String> scopesList = Arrays.asList(scope);

		ScopedPublisher publish = RegistryPublisherFactory.scopedPublisher();

		Resource r = null;

		try {
			r = publish.remove(genericResource, scopesList);
			logger.info("resource removed with id : " + r.id());
		} catch (RegistryNotFoundException e) {
			throw new ResourcePublisherException(e);
		}
	}
	
//	private void deleteResource(T resource) throws ResourcePublisherException {
//		GenericResource genericResource = null;
//		try {
//			genericResource = convertToGenericResource(resource, this.resourceClass, this.resourceNamePref);
//		} catch (SAXException | IOException | ParserConfigurationException
//				| JAXBException e) {
//			logger.error("error while converting resource to generic resource", e);
//		}
//		
//		if (genericResource == null) {
//			throw new ResourcePublisherException("resource with id : " + resource.getResourceID() + " not found");
//		}
//		
//		deleteFromScopes(genericResource, scope);
//	}

	@Override
	public void publishResource(T resource, String resourceClass, String resourceNamePref, String scope, boolean includeIdInName, boolean onlyBody)
			throws ResourcePublisherException {
		logger.info("creating resource with ID : " + resource.getResourceID()
				+ " to IS for scope : " + scope);
		
		GenericResource genericResource = null;
		try {
			genericResource = convertToGenericResource(resource, resourceClass, resourceNamePref, includeIdInName, onlyBody);
			
		} catch (SAXException | IOException | ParserConfigurationException
				| JAXBException e) {
			logger.error("error while converting resource to generic resource", e);
			//e.printStackTrace();
			throw new ResourcePublisherException("error while converting resource to generic resource", e );
		}
		
		logger.info(" resource to generic resource : " + genericResource);

		ScopeProvider.instance.set(scope);

		List<String> scopes = Arrays.asList(scope);

		ScopedPublisher publish = RegistryPublisherFactory.scopedPublisher();

		Resource r = null;

		try {
			r = publish.create(genericResource, scopes);
			
			logger.info("resource updated with id : " + r.id());
			
			try {
				String genericResourceXML = XMLConverter.convertToXML(genericResource);
				logger.trace("generic resource to be updated is        : " + genericResourceXML);
				
				GenericResource isResource = getResourceByID(resource.getResourceID(), scope);
				if (isResource == null){
					logger.trace("generic resource has not been synced to IS yet. Comparisson is skipped");
				} else {
					String genericResourceXMLFromIS = XMLConverter.convertToXML(isResource);
					
					logger.trace("generic resource as retrieved from IS is : " + genericResourceXMLFromIS);
				}
				
			} catch (Exception e) {
				logger.error("error while converting resource to generic resource", e);
			}
			
		} catch (RegistryNotFoundException e) {
			throw new ResourcePublisherException(e);
		}
	}

	
	@Override
	public void updateResource(T resource, String resourceClass, String resourceNamePref, String scope, boolean includeIdinName, boolean onlyBody)
			throws ResourcePublisherException {
		logger.info("updating resource with ID : " + resource.getResourceID()
				+ " to IS for scope : " + scope);
		
		ScopeProvider.instance.set(scope);

		ScopedPublisher publish = RegistryPublisherFactory.scopedPublisher();

		GenericResource genericResource = null;
		try {
			genericResource = convertToGenericResource(resource, resourceClass, resourceNamePref, includeIdinName, onlyBody);
			
		} catch (SAXException | IOException | ParserConfigurationException
				| JAXBException e) {
			logger.error("error while converting resource to generic resource", e);
			throw new ResourcePublisherException(e);
		}
		
		try {
			Resource r = null;
			
			if (!genericResource.scopes().asCollection().contains(scope))
				 genericResource.scopes().asCollection().add(scope);
			r = publish.update(genericResource);
			
			//TODO: check!
//			if (genericResource.scopes().contains(scope)){
//				logger.info("scope in resource. updating with id : " + genericResource.id());
//				r = publish.update(genericResource);
//			} else {
//				logger.info("scope not in resource. creating resource with id : " + genericResource.id());
//				List<String> scopes = Arrays.asList(scope);
//				r = publish.create(genericResource, scopes);
//			}
			
			logger.info("resource updated with id : " + r.id());
			
			try {
				String genericResourceXML = XMLConverter.convertToXML(genericResource);
				logger.trace("generic resource to be updated is        : " + genericResourceXML);
				
				GenericResource isResource = getResourceByID(resource.getResourceID(), scope);
				if (isResource == null){
					logger.trace("generic resource has not been synced to IS yet. Comparisson is skipped");
				} else {
				
					String genericResourceXMLFromIS = XMLConverter.convertToXML(isResource);
					
					
					logger.trace("generic resource as retrieved from IS is : " + genericResourceXMLFromIS);
				}
				
			} catch (Exception e) {
				logger.error("error while converting resource to generic resource", e);
			}
			
		} catch (RegistryNotFoundException e) {
			throw new ResourcePublisherException(e);
		}
	}
	
	private static GenericResource getResourceByID(String resourceID, String scope) {
		ScopeProvider.instance.set(scope);
		SimpleQuery query = queryFor(GenericResource.class);
		query.addCondition("$resource/ID/text() eq '" + resourceID + "'");
		DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);
		List<GenericResource> resources = client.submit(query);
		
		if (resources.size() > 0)
			return resources.get(0);
		else {
			logger.warn("Could not find resource with ID : " + resourceID);
			return null;
		}
			
	}
	
	private GCoreEndpoint convertToGcoreEndpoint(RunInstance ri) {
		GCoreEndpoint ge = null;
		
		Node node;
		try {
			node = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(XMLConverter.convertToXML(ri).getBytes())).getDocumentElement();
		} catch (SAXException | IOException | ParserConfigurationException | JAXBException e1) {
			e1.printStackTrace();
			return ge;
		}
		node.getOwnerDocument().renameNode(node, null, "Resource");

		try {
			ge = XMLConverter.fromXML(node, GCoreEndpoint.class);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		
		return ge;
	}
	
	@Override
	public void updateResource(T ri, String scope) throws ResourcePublisherException {
		if (ri instanceof RunInstance) {
			ScopeProvider.instance.set(scope);
			ScopedPublisher publish = RegistryPublisherFactory.scopedPublisher();

			try {
				publish.update(convertToGcoreEndpoint((RunInstance) ri));
			} catch (RegistryNotFoundException e) {
				throw new ResourcePublisherException("could not update resource", e);
			}
		} else
			throw new ResourcePublisherException("resource type not supported yet: " + ri.getClass().getCanonicalName());
	}
	
//	public static void main(String[] args) throws JAXBException {
//		String str = "<RunInstance version=\"0.4.x\">\n" + 
//				"<ID>7acd6645-403a-4e7e-80e3-d54600d7f75f</ID>\n" + 
//				"<Type>RunningInstance</Type>\n" + 
//				"<Scopes>\n" + 
//				"   <Scope>/gcube/devNext</Scope>\n" + 
//				"   <Scope>/gcube</Scope>\n" + 
//				"</Scopes>\n" + 
//				"<Profile>\n" + 
//				"   <Description>RESTful Version of ExecutionEngine</Description>\n" + 
//				"   <Version>2.0.1-SNAPSHOT</Version>\n" + 
//				"   <GHN UniqueID=\"2f137d0e-f04b-44c0-ba74-67069b7d3c8a\" />\n" + 
//				"   <Service UniqueID=\"ExecutionEngineServiceExecution2.0.1-SNAPSHOT\" />\n" + 
//				"   <ServiceName>ExecutionEngineService</ServiceName>\n" + 
//				"   <ServiceClass>Execution</ServiceClass>\n" + 
//				"   <DeploymentData>\n" + 
//				"      <ActivationTime value=\"2014-05-27T17:18:11+02:00\" />\n" + 
//				"      <Status>ready</Status>\n" + 
//				"   </DeploymentData>\n" + 
//				"   <AccessPoint>\n" + 
//				"      <RunningInstanceInterfaces>\n" + 
//				"         <Endpoint EntryName=\"ExecutionEngineService-remote-management\">http://meteora.di.uoa.gr:8080/executionengineservice-2.0.1-SNAPSHOT/gcube/resource</Endpoint>\n" + 
//				"         <Endpoint EntryName=\"resteasy-servlet\">http://meteora.di.uoa.gr:8080/executionengineservice-2.0.1-SNAPSHOT</Endpoint>\n" + 
//				"      </RunningInstanceInterfaces>\n" + 
//				"   </AccessPoint>\n" + 
//				"</Profile>\n" + 
//				"</RunInstance>";
//		
//		new PublisherISimpl<RunInstance>().publishRunInstance(XMLConverter.fromXML(str, RunInstance.class), "/gCube/devNext");
//	}
	
	
//	private GenericResource convertToGenericResource(T resource, String clazz, String name, boolean includeIdinName) throws SAXException, IOException, ParserConfigurationException, JAXBException{
//		return convertToGenericResource(resource, clazz, name, includeIdinName, false);
//	}
	
	private GenericResource convertToGenericResource(T resource, String clazz, String name, boolean includeIdinName, boolean onlyBody) throws SAXException, IOException, ParserConfigurationException, JAXBException {
		GenericResource genericResource = new GenericResource();
		ResourceMediator.setId(genericResource, resource.getResourceID());
		
		genericResource.newProfile();
		
		String genericResourceName = includeIdinName ? name + "." + resource.getResourceID() : name;

		logger.info("generic resource with id : " + resource.getResourceID() + " will have name : " + genericResourceName + " includeIdinName : " + includeIdinName + " : onlybody : " + onlyBody);
		
//		if (includeIdinName){
//			try {
//				throw new Exception();
//			} catch (Exception e){
//				System.out.println("---------------------");
//				e.printStackTrace();
//				System.out.println("---------------------");
//				logger.warn("---------------------");
//				logger.warn("", e);
//				logger.warn("---------------------");
//				
//			}
//		}
		
		genericResource.profile().name(genericResourceName);
		genericResource.profile().type(clazz);
		
		String serialization = resource.toXML();
		
		if (resource instanceof org.gcube.rest.commons.resourceawareservice.resources.Resource){
			org.gcube.rest.commons.resourceawareservice.resources.Resource r = (org.gcube.rest.commons.resourceawareservice.resources.Resource)resource;
			genericResource.profile().description(r.getDescription());

			genericResource.scopes().asCollection().addAll(r.getScopes());
			
			if (onlyBody)
				serialization = r.getBodyAsString();
		}
		
		
		logger.info("resource serialization : " + serialization);
		
		Element newBody = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(serialization.getBytes())).getDocumentElement();
		genericResource.profile().newBody();
		Node importedNode = genericResource.profile().body().getOwnerDocument().importNode(newBody, true);
		genericResource.profile().body().appendChild(importedNode);
		
		return genericResource;
	}
}
