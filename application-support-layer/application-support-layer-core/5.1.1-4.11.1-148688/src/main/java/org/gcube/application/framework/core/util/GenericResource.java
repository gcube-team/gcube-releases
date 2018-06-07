package org.gcube.application.framework.core.util;


import java.io.StringReader;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.gcube.application.framework.core.GenericResourceInfoI;
import org.gcube.application.framework.core.cache.CachesManager;
import org.gcube.application.framework.core.genericresources.model.ISGenericResource;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.application.framework.core.util.CacheEntryConstants;
import org.gcube.application.framework.core.util.QueryString;
import org.gcube.application.framework.core.util.SessionConstants;
import org.gcube.common.resources.gcore.Resources;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.informationsystem.publisher.RegistryPublisher;
import org.gcube.informationsystem.publisher.RegistryPublisherFactory;
import org.gcube.informationsystem.publisher.ScopedPublisher;
import org.gcube.informationsystem.publisher.stubs.registry.faults.PublisherException;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.gcube.resources.discovery.icclient.ICFactory.*;

import org.gcube.common.resources.gcore.Resource;
import org.gcube.common.resources.gcore.ServiceInstance;


/**
 * @author Valia Tsagkalidou (NKUA)
 * @author Nikolas Laskaris (NKUA) -- refactored August 2013 to conform to FeatherWeight stack standards
 */
public class GenericResource implements GenericResourceInfoI {
	
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(GenericResource.class);

	/**
	 * A static IS publisher in order to communicate with IS (FeatherWeight)
	 */
//	protected static ISPublisher publisher = null;
//	protected static ISClient client = null;
	
	protected static ScopedPublisher scopedPublisher = null;
	protected static RegistryPublisher publisher = null;
	protected static DiscoveryClient<org.gcube.common.resources.gcore.GenericResource> client = null;
	
	/**
	 * The D4Science session to be used
	 */
	ASLSession session;
	
	/**
	 * Constructs a GenericReosurce object
	 * @param extrenalSessionID the external session id which is used for the D4ScienceSession (usually the session.getId() of the HttpSession or PortletSession) 
	 * @param username the username of the user that called this constructor
	 */
	public GenericResource(String extrenalSessionID, String username)
	{

		session = SessionManager.getInstance().getASLSession(extrenalSessionID, username);
		try {
			ScopeProvider.instance.set(session.getScope());
			scopedPublisher = RegistryPublisherFactory.scopedPublisher(); 
			publisher = RegistryPublisherFactory.create();	
		} catch (Exception e) {
			logger.error("Exception:", e);
		}
		if(client == null)
		{
			try {
				client = clientFor(org.gcube.common.resources.gcore.GenericResource.class); //GHNContext.getImplementation(ISClient.class);
			} catch (Exception e) {
				logger.error("Exception:", e);
				client = null;
			}
		}
	}

	
	
	/**
	 * Constructs a GenericReosurce object
	 * @param session the D4Science session to be used for retrieving information needed
	 */
	public GenericResource(ASLSession session) {
		super();
		this.session = session;
		try {
			ScopeProvider.instance.set(session.getScope());
			scopedPublisher = RegistryPublisherFactory.scopedPublisher(); 
			publisher = RegistryPublisherFactory.create();
		} catch (Exception e) {
			logger.error("Exception:", e);
		}
		if(client == null)
		{
			try {
				client = clientFor(org.gcube.common.resources.gcore.GenericResource.class);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception:", e);
				client = null;
			}
		}
	}


	/**
	 * @return the name of the active VRE
	 */
	protected String getDLName()
	{
		//SOS
		//return session.getOriginalScopeName();
		return session.getOriginalScopeName();
	}
	
	/**
	 * @param query the query to retrieve generic resources from cache
	 * @return a list of generic resources
	 */
	protected List<ISGenericResource> getGenericResource(QueryString query)
	{
		List<ISGenericResource> resList = (List<ISGenericResource>) (CachesManager.getInstance().getGenericResourceCache().get(query).getValue());
		if(resList.isEmpty())
			logger.debug("no generic resources in cache "+query.get("name"));
		return  resList;
	}

	/**
	 *  retrieves from the IS all the Tree collections under the current scope
	 * @param onlyUserCollections if true, it returns only user tree collections, otherwise it returns all tree collections
	 * @return a hashmap containing the {collection id, collection resource}
	 */
	public HashMap<String,org.gcube.common.resources.gcore.GenericResource> getAllTreeCollections(boolean onlyUserCollections) {
		// maybe should add the ScopeProvider.instance.set(session.getScope()) here and not in the constructors.
		HashMap<String,org.gcube.common.resources.gcore.GenericResource> pairs = new HashMap<String, org.gcube.common.resources.gcore.GenericResource>();
		SimpleQuery query = null;
		try {
			query = queryFor(org.gcube.common.resources.gcore.GenericResource.class);
			query.addCondition("$resource/Profile/SecondaryType eq 'DataSource'"); //this brings all collections, need to filter out the opensearch ones.
//			if(onlyUserCollections)
//				query.addCondition("$resource/Profile/Body/SourceProperties/user eq 'true'");
			List<org.gcube.common.resources.gcore.GenericResource> results = client.submit(query);
			if (results == null || results.size() == 0)
				logger.debug("Couldn't find any tree collections within that scope! Will return empty list.");
			for (org.gcube.common.resources.gcore.GenericResource gr : results)
				pairs.put(gr.id(), gr);
		} catch (Exception e) {
			logger.debug("Remote Exception:" + e.toString());
		}
		//remove from all collections set, the opensearch ones !
		Iterator<Map.Entry<String,org.gcube.common.resources.gcore.GenericResource>> iter = pairs.entrySet().iterator();
		while (iter.hasNext()) {
			org.gcube.common.resources.gcore.GenericResource collection = iter.next().getValue();
			Element body = collection.profile().body();
			if(body.getElementsByTagName("type").getLength()>0){ //means opensearch collection
				logger.debug("Filtering opensearch collection with ID: "+collection.id()+" Type: "+collection.type());
				iter.remove();
			}
		}
		logger.debug("# of Tree Collections found: "+ pairs.size());
		return pairs;	
	}

	/**
	 * retrieves from the IS all the OpenSearch collections under the current scope
	 * @param onlyUserCollections if true, it returns only user opensearch collections, otherwise it returns all opensearch collections
	 * @return a hashmap containing the {collection id, collection resource}
	 */
	public HashMap<String,org.gcube.common.resources.gcore.GenericResource> getAllOpenSearchCollections(boolean onlyUserCollections) {
		// maybe should add the ScopeProvider.instance.set(session.getScope()) here and not in the constructors.
		HashMap<String,org.gcube.common.resources.gcore.GenericResource> pairs = new HashMap<String, org.gcube.common.resources.gcore.GenericResource>();
		SimpleQuery query = null;
		try {
			query = queryFor(org.gcube.common.resources.gcore.GenericResource.class);
//			query.addCondition("$resource/Profile/SecondaryType eq 'GCUBECollection'");
			query.addCondition("$resource/Profile/SecondaryType eq 'DataSource'");  //changed from GCUBECollection to DataSource (2 be same as the tree collections)
//			query.addCondition("$resource/Profile/Body/SourceProperties/type eq 'opensearch'");
//			if(onlyUserCollections)
//				query.addCondition("$resource/Profile/Body/CollectionInfo/user eq 'true'");
			List<org.gcube.common.resources.gcore.GenericResource> results = client.submit(query);
			if (results == null || results.size() == 0)
				logger.debug("Couldn't find any OpenSearch collections within that scope! Will return empty list.");
			for (org.gcube.common.resources.gcore.GenericResource gr : results)
				pairs.put(gr.id(), gr);
			//remove from all collections set, the tree collection ones !
			
			Iterator<Map.Entry<String,org.gcube.common.resources.gcore.GenericResource>> iter = pairs.entrySet().iterator();
			while (iter.hasNext()) {
				org.gcube.common.resources.gcore.GenericResource collection = iter.next().getValue();
				Element body = collection.profile().body();
				if(body.getElementsByTagName("type").getLength()==0){
					logger.debug("Removing non-opensearch collection: "+collection.id()+" Type: "+collection.type());
					iter.remove();
				}
			}
			logger.debug("# of Opensearch Collections found: "+ pairs.size());
		} catch (Exception e) {
			logger.debug("Remote Exception:" + e.toString());
		}
		return pairs;	
	}
	
	
	
	/**
	 * 
	 * @param collectionID The ID of the collection
	 * @return the cardinality of the collection stored within the IS
	 */
	
	public String getTreeResourceCardinality(String collectionID){
		SimpleQuery query = queryFor(ServiceInstance.class);
		query.addCondition("$resource/Data/child::*[local-name()='ServiceName']/string() eq 'tree-manager-service'");
		query.addCondition("$resource/Data/child::*[local-name()='SourceId']/string() eq '"+collectionID+"'");
		DiscoveryClient<ServiceInstance> client = clientFor(ServiceInstance.class);
		List<ServiceInstance> resources = client.submit(query);
//		logger.debug("Found " + resources.size() + " resources");
		String cardinality = "0";
		if(resources.size()==0)
			logger.debug("No collection '" + collectionID +"' found within scope " + ScopeProvider.instance.get());
		else if(resources.size()==1){
			NodeList nodes = resources.get(0).properties().customProperties().getChildNodes();
			for(int i=0;i<nodes.getLength();i++)
				if(nodes.item(i).getLocalName().equalsIgnoreCase("Cardinality"))
					cardinality = nodes.item(i).getTextContent();
			logger.debug("Cardinality of collection '" + collectionID +"' is " + cardinality);
		}
		else
			logger.debug("ERROR ! Found more than 1 resources '" + collectionID +"' within scope " + ScopeProvider.instance.get());
		return cardinality;
	}
	
	/**
	 * Retrieves the cardinality of all tree collections on the current scope
	 * 
	 * @return a HashMap containing the (collectionID, cardinality) pairs
	 */
	public HashMap<String, String> getTreeResourcesCardinalities(){
		HashMap<String, String> outputResults = new HashMap<String, String>(); //to hold (collectionID,cardinality)
		DiscoveryClient<CardinalitiesResultBean> client = clientFor(CardinalitiesResultBean.class);
		SimpleQuery query = queryFor(ServiceInstance.class);
		query.addCondition("$resource/Data/child::*[local-name()='ServiceName']/string() eq 'tree-manager-service'");
		query.setResult("<Result>" +
        "<CollectionID>{$resource/Data/child::*[local-name()='SourceId']/string()}</CollectionID>" +
        "<Cardinality>{$resource/Data/child::*[local-name()='Cardinality']/string()}</Cardinality>" +
                		"</Result>");
		List<CardinalitiesResultBean> beanResults = client.submit(query);
		if(beanResults.size()==0)
			logger.debug("There are no tree collection cardinality results");
		for(CardinalitiesResultBean beanResult : beanResults){
			outputResults.put(beanResult.collectionID, beanResult.cardinality);
//			logger.debug("Collection: " + beanResult.collectionID + " has cardinality: " + beanResult.cardinality);
		}
		return outputResults;
	}
	
	
	
	/**
	 * Gets the user field property from the generic resource body (profile/body) 
	 * @return the property value
	 * @throws XPathExpressionException 
	 */
	public boolean checkIfUser(org.gcube.common.resources.gcore.GenericResource resource) throws XPathExpressionException{
		XPathExpression userExpression = null;
		XPath xPath = XPathFactory.newInstance().newXPath();
		try {
			userExpression = xPath.compile("/SourceProperties/user");
		} catch (XPathExpressionException e) {
			logger.debug("Could not initiate the xpath expression for the user property");
			e.printStackTrace();
		}
		return Boolean.parseBoolean(userExpression.evaluate(new InputSource(new StringReader(resource.profile().bodyAsString()))));
	}
	
	/**
	 * Creates a new generic resource
	 * @param genericResource the new generic resource
	 * @return the ID of the newly created resource
	 * @throws RemoteException when an error has occurred while communicating with  IS
	 */
	public String createGenericResource(ISGenericResource genericResource) throws RemoteException {
		try {
			//maybe should add the ScopeProvider.instance.set(session.getScope()); here and not in the constructors.
			org.gcube.common.resources.gcore.GenericResource gCubeRes = new org.gcube.common.resources.gcore.GenericResource();			
			gCubeRes.newProfile();
			gCubeRes.profile().name(genericResource.getName());
			gCubeRes.profile().description(genericResource.getDescription());
			gCubeRes.profile().type(genericResource.getSecondaryType());
			gCubeRes.profile().newBody(genericResource.getBody());

			//maybe should change the visibility (into public) of addScope() within the Resource class of common-gcore-resources package in order to use it here.
			List<String> scopes=new ArrayList<String>();
		    scopes.add(session.getScope());
		    org.gcube.common.resources.gcore.GenericResource res = scopedPublisher.create(gCubeRes, scopes);
		    logger.info("Created Generic Resource with id: "+res.id()+" on scope: "+scopes.toString());
		    
		    // 1-oct-2013 -- adding generic resource to cache
		    logger.info("Adding it also in cache...");
		    List<ISGenericResource> genRes = new ArrayList<ISGenericResource>();
			genRes.add(genericResource);
			QueryString query1 = new QueryString();
			query1.addParameter(CacheEntryConstants.id, genericResource.getId());
			query1.addParameter(CacheEntryConstants.vre, session.getScope());
			CachesManager.getInstance().getGenericResourceCache().put(new net.sf.ehcache.Element(query1, genRes));
			logger.info("...added in cache");
			
		    return res.id();
		} catch (Exception e) {
			logger.error("Exception:", e);
			throw new RemoteException();
		}
	}
	

//	public String createGenericResourceOLD(ISGenericResource genericResource) throws RemoteException {
//		try {
//			//maybe should add the ScopeProvider.instance.set(session.getScope()); here and not in the constructors.
//			org.gcube.common.resources.gcore.GenericResource gCubeRes = Resources.unmarshal(org.gcube.common.resources.gcore.GenericResource.class, org.gcube.common.resources.gcore.GenericResource.class.getClassLoader().getResourceAsStream("generic.xml"));
//			gCubeRes.profile().name(genericResource.getName());
//			gCubeRes.profile().description(genericResource.getDescription());
//			gCubeRes.profile().body().setTextContent(genericResource.getBody());
//			gCubeRes.profile().type(genericResource.getSecondaryType());
//			//maybe should change the visibility (into public) of addScope() within the Resource class of common-gcore-resources package in order to use it here.
//			List<String> scopes=new ArrayList<String>();
//		    scopes.add(session.getScope());
//		    Resource res = scopedPublisher.create(gCubeRes, scopes);
//		    logger.info("Created Generic Resource with id: "+res.id()+" on scope: "+scopes.toString());
//		    
//		    //NIK-- 1/oct/2013 -- adding generic resource to cache
//		    logger.info("Adding it also in cache...");
//		    List<ISGenericResource> genRes = new ArrayList<ISGenericResource>();
//			genRes.add(genericResource);
//			QueryString query1 = new QueryString();
//			query1.addParameter(CacheEntryConstants.id, genericResource.getId());
//			query1.addParameter(CacheEntryConstants.vre, session.getScope());
//			CachesManager.getInstance().getGenericResourceCache().put(new net.sf.ehcache.Element(query1, genRes));
//			logger.info("...added in cache");
//			
//		    return res.id();
//		} catch (Exception e) {
//			logger.error("Exception:", e);
//			throw new RemoteException();
//		}
//	}

	/**
	 * @return a list containing all generic resources 
	 * @throws RemoteException when an error has occurred while communicating with  IS
	 */
	public List<org.gcube.common.resources.gcore.GenericResource> getAllGenericResources() throws RemoteException {
		SimpleQuery queryMan = null;
		try {
			queryMan = queryFor(org.gcube.common.resources.gcore.GenericResource.class);
			return client.submit(queryMan);
		} catch (Exception e) {
			logger.error("Exception:", e);
			throw new RemoteException();
		}
	}
	
	/**
	 * Updates a generic resource based on its ID
	 * @param genericResource the generic resource to be updated
	 * @throws RemoteException when an error has occurred while communicating with  IS
	 */
	public void updateGenericResourceByID(ISGenericResource genericResource) throws RemoteException {
		try {
			//maybe should add the ScopeProvider.instance.set(session.getScope()); here and not in the constructors.
			logger.info("Updating generic resource by ID");
			SimpleQuery query = queryFor(org.gcube.common.resources.gcore.GenericResource.class);
			query.addCondition("$resource/ID eq '"+genericResource.getId()+"'");
			
			List <org.gcube.common.resources.gcore.GenericResource> result = client.submit(query);
			
			if (result == null || result.size() == 0) {
				logger.info("Error during updateGenericResourceByID: Couldn't find the resource to update");
				return;
			}
			
			org.gcube.common.resources.gcore.GenericResource gCubeRes = result.get(0);
			
			logger.debug("Updating generic resource body from: "+gCubeRes.profile().bodyAsString());
			logger.debug("Updating generic resource body to: "+genericResource.getBody());
			
			gCubeRes.profile().name(genericResource.getName());
			gCubeRes.profile().description(genericResource.getDescription());
			gCubeRes.profile().newBody(genericResource.getBody());
			gCubeRes.profile().type(genericResource.getSecondaryType());
			
			try {
				//SOS!!!! uncomment this and erase the two lines below
				//publisher.updateGCUBEResource(gCubeRes, session.getScope(), new PortalSecurityManager(session));
				List<String> scopes = new ArrayList<String>();
				for(String scope : gCubeRes.scopes().asCollection())
					scopes.add(scope);
//				Resource res = scopedPublisher.update(gCubeRes, scopes);
				Resource res = publisher.update(gCubeRes);  //alternative of the above, if we don't want to define a scope -- better use scopedPublisher 
				logger.info("Updated generic resource on IS ! New ID: "+res.id());
			}catch (Exception e) {
				logger.error("Exception during update of generic resource:", e);
			}
			
			QueryString query1 = new QueryString();
			query1.addParameter(CacheEntryConstants.name, genericResource.getName());
			query1.addParameter(CacheEntryConstants.vre, session.getScope());
			if(CachesManager.getInstance().getGenericResourceCache().isElementInMemory(query1) || CachesManager.getInstance().getGenericResourceCache().isElementOnDisk(query1))
			{
				logger.info("Removing old generic resource from cache...");
				CachesManager.getInstance().getGenericResourceCache().get(query1).setTimeToLive(0);
			}
			else {
				logger.info("The generic resource to replace in cache was not found");
			}
			
			logger.info("...and adding new generic resource in cache.");
			QueryString query2 = new QueryString();
			List<ISGenericResource> genRes = new ArrayList<ISGenericResource>();
			genRes.add(genericResource);
			query2.addParameter(CacheEntryConstants.id, genericResource.getId());
			query2.addParameter(CacheEntryConstants.vre, session.getScope());
			CachesManager.getInstance().getGenericResourceCache().put(new net.sf.ehcache.Element(query2, genRes));
			

			
		} catch (Exception e) {
			logger.error("Exception:", e);
			throw new RemoteException();
		}
	}
	
	
	/**
	 * @return a list containing pairs of (name, id) of the available generic resources 
	 * @throws RemoteException when an error has occurred while communicating with  IS
	 */
	public List<Pair> getAvailableGenericResourceNames() throws RemoteException {
		//maybe should add the ScopeProvider.instance.set(session.getScope()); here and not in the constructors.
		List<Pair> pairs = new ArrayList<Pair>();
		SimpleQuery queryMan = null;
		try {
			queryMan = queryFor(org.gcube.common.resources.gcore.GenericResource.class);
			List <org.gcube.common.resources.gcore.GenericResource> results = client.submit(queryMan);
			for(org.gcube.common.resources.gcore.GenericResource gr : results)
				pairs.add(new Pair(gr.profile().name(), gr.id()));
			return pairs;
		} catch (Exception e) {
			logger.error("Exception:", e);
			throw new RemoteException();
		}
	}

	public List<ISGenericResource> getGenericResourcesByType(String type) throws RemoteException {
		return getGenericResourcesByType(session.getScope(), type);
	}
	
	
	public static List<ISGenericResource> getGenericResourcesByType(String scope, String type) throws RemoteException {
		ScopeProvider.instance.set(scope);
		List<ISGenericResource> output = new ArrayList<ISGenericResource>();
		SimpleQuery queryMan = null;
		try {
			DiscoveryClient<org.gcube.common.resources.gcore.GenericResource> client = clientFor(org.gcube.common.resources.gcore.GenericResource.class);
			queryMan = queryFor(org.gcube.common.resources.gcore.GenericResource.class);
			queryMan.addCondition("$resource/Profile/SecondaryType eq '"+type+"'");
			List <org.gcube.common.resources.gcore.GenericResource> results = client.submit(queryMan);
			for(org.gcube.common.resources.gcore.GenericResource res : results)
				output.add(new ISGenericResource(res.id(),res.profile().name(),res.profile().description(),res.profile().bodyAsString(),res.profile().type()));
			return output;
		} catch (Exception e) {
			logger.error("Exception:", e);
			throw new RemoteException();
		}
	}
	
	

	/**
	 * @param id the id of the generic resource
	 * @return a list containing the corresponding generic resources
	 * @throws RemoteException when an error has occurred while communicating with  IS
	 */
	public List<ISGenericResource> getGenericResourceByID(String id)
			throws RemoteException {
		QueryString query = new QueryString();
		query.put(CacheEntryConstants.id, id);
		query.put(CacheEntryConstants.vre, getDLName());
		return getGenericResource(query);
	}
	
	
	/**
	 * @param name the name of the generic resource
	 * @return a list containing the generic resources that have as name the given
	 * @throws RemoteException when an error has occurred while communicating with  IS
	 */
	public List<ISGenericResource> getGenericResourceByName(String name) throws RemoteException{
		//caching is done by (id,vre) as key pairs. so we cannot use cache to retrieve objects. 
		List<org.gcube.common.resources.gcore.GenericResource> allRes = getAllGenericResources();
		List<ISGenericResource> results = new ArrayList<ISGenericResource>();
		for(org.gcube.common.resources.gcore.GenericResource res : allRes)
			if(res.profile().name().equalsIgnoreCase(name))
				results.add(new ISGenericResource(res.id(),res.profile().name(),res.profile().description(),res.profile().bodyAsString(),res.profile().type()));
		return results;
	}

	
	/**
	 * @return a list containing the  generic resources that describe which collections are part of the active VRE as well as their hierarchical structure (the name of this generic resource is "ScenarioCollectionInfo")  
	 * @throws RemoteException when an error has occurred while communicating with  IS
	 */
	public List<ISGenericResource> getGenericResourceForScenario()
			throws RemoteException {
		QueryString query = new QueryString();
		query.put(CacheEntryConstants.name, SessionConstants.ScenarioSchemaInfo);
		query.put(CacheEntryConstants.vre, getDLName());
		return getGenericResource(query);
	}
	
	/**
	 * Removes an existing generic resource
	 * @param genericResource the generic resource to be removed
	 * @throws RemoteException when an error has occurred while communicating with  IS
	 */
	public void removeGenericResource(ISGenericResource genericResource) throws RemoteException {
		try {
			//maybe should add the ScopeProvider.instance.set(session.getScope()); here and not in the constructors.
			SimpleQuery query = queryFor(org.gcube.common.resources.gcore.GenericResource.class);
			query.addCondition("$resource/ID eq '"+genericResource.getId()+"'");
			List <org.gcube.common.resources.gcore.GenericResource> result = client.submit(query);
			if (result == null || result.size() == 0) {
				logger.error("Couldn't find the resource to delete! Returning...");
				return;
			}
			org.gcube.common.resources.gcore.GenericResource gCubeRes = result.get(0);
			publisher = RegistryPublisherFactory.create();
			Resource res = publisher.remove(gCubeRes);
			logger.info("Removed generic resource from IS");
			QueryString queryString = new QueryString();
			queryString.addParameter(CacheEntryConstants.id, genericResource.getId());
			queryString.addParameter(CacheEntryConstants.vre, session.getScope());
			if(CachesManager.getInstance().getGenericResourceCache().isElementInMemory(queryString) || CachesManager.getInstance().getGenericResourceCache().isElementOnDisk(queryString)){
				genericResource = ((List<ISGenericResource>)CachesManager.getInstance().getGenericResourceCache().get(queryString).getValue()).get(0);
				//TODO
				CachesManager.getInstance().getGenericResourceCache().get(queryString).setTimeToLive(0);
			}
//			queryString.clear();
//			queryString.put(CacheEntryConstants.name, genericResource.getName());
//			queryString.addParameter(CacheEntryConstants.vre, session.getScope());
//			if(CachesManager.getInstance().getGenericResourceCache().isElementInMemory(queryString) || CachesManager.getInstance().getGenericResourceCache().isElementOnDisk(queryString)){
//				//TODO
//				CachesManager.getInstance().getGenericResourceCache().get(queryString).setTimeToLive(0);			
//			}
		} catch (PublisherException e) {
			logger.error("Exception:", e);
			throw new RemoteException();
		}
		
	}
	
	
	/**
	 * Parses the xml and returns it as DOM object
	 * @param XMLdoc the xml as a string
	 * @return xml parsed as a Document
	 */
	static Document parseXMLFileToDOM(String XMLdoc) {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = dbFactory.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new StringReader(XMLdoc)));
		    return doc;
		} catch (Exception e) {
			return null;
		}	
	}
	

	
	/**
	 * 
	 * @param xsltType Presentation or Metadata. The type of the xslt
	 * @return A vector which contains all the generic resources, xslts of this type. The xslts are sorted by the schema.
	 */
	public HashMap<String,Vector<String[]>> getAllXslts(String xsltType) {
		HashMap<String,Vector<String[]>> schemas_xslts = new HashMap<String,Vector<String[]>>();
		Vector<String[]> xslts = null;
		

		List<String[]> result = null;
		try {
			result = GenericResource.retrieveGenericResourcesFromNameParts(xsltType, session.getScope());
		} catch (Exception e) {
			logger.error("Exception:", e);
		}
		
		if (result == null)  {
			return null;
		}
	/*	if (xsltType.equals("MetadataXSLT")) {
			for (int i=0; i<result.size(); i++) {
					String fullName = result.get(i)[0];
					String id = result.get(i)[1];
					String tmp = fullName.substring(xsltType.length()+1,fullName.length());
					String parts[] = tmp.split("_");
					String schemaName = parts[0];
					String xsltName = parts[1];
					xslts = schemas_xslts.get(schemaName.replaceFirst("%5f", "_"));
					if (xslts == null) {	
						xslts = new Vector<String[]>();
						schemas_xslts.put(schemaName.replaceFirst("%5f", "_"), xslts);
					}
					
					// add the xslt name and id
					String s[] = new String[2];
					s[0] = xsltName;
					s[1] = id;
					xslts.add(s);
				}
			return schemas_xslts;
		}*/
	//	else {
			// Presentation xslts
		logger.info("Inside getAllXslts");
		logger.info("The type is: " + xsltType); 
			for (int i=0; i<result.size(); i++) {
				String fullName = result.get(i)[0];
				logger.info("fullname: " + fullName);
				String id = result.get(i)[1];
				logger.info("id: " + id);
			//	String tmp = fullName.substring(xsltType.length()+1,fullName.length());
				String tmp = fullName.substring(xsltType.length()+3,fullName.length());
				logger.info("tmp: " + tmp);
				String parts[] = tmp.split("-\\|-");
				String schemaName = parts[0];
				for (int k = 0; k < parts.length; k++) {
					logger.info("part: " + parts[k]);
				}
				logger.info("schemaName: " + schemaName);
				String xsltName = parts[1];
				logger.info("xsltName: " + xsltName);
				xslts = schemas_xslts.get(schemaName);
				if (xslts == null) {	
					xslts = new Vector<String[]>();
					schemas_xslts.put(schemaName, xslts);
				}
				
				// add the xslt name and id
				String s[] = new String[2];
				s[0] = xsltName;
				s[1] = id;
				xslts.add(s);
			}
		return schemas_xslts;
	//	}
	}

	private static List<String[]> retrieveGenericResourcesFromNameParts(String nameParts, String scope) throws Exception {
		ScopeProvider.instance.set(scope);		
		logger.debug("retrieveGenericResourcesFromNameParts");
		SimpleQuery query = queryFor(GenericResource.class);
		//READ COMMENT BELOW !!!
		query.addCondition("$resource/Profile/Name eq '"+nameParts+"'");
		// on the above, we check if it is equal. what we want in order to have the previous functionality is to seaarch "like", not "eq"
		List <org.gcube.common.resources.gcore.GenericResource> queryResults = null;		
		queryResults = client.submit(query);
		if (queryResults == null || queryResults.size() == 0) {
			logger.error("Couldn't find the resource from name! Returning null");
			return null;
		}
		List<String[]> results = new LinkedList<String[]>(); 
		for(org.gcube.common.resources.gcore.GenericResource gr : queryResults){
			String[] nameAndID = { gr.profile().name() , gr.id() };
			results.add(nameAndID);
		}
		return results;
	}	

}
