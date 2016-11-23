package org.gcube.common.informationsystem.publisher.impl.generic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.informationsystem.publisher.ISGenericPublisher;
import org.gcube.common.core.informationsystem.publisher.ISPublisherException;
import org.gcube.common.core.informationsystem.publisher.ISResource;
import org.gcube.common.core.informationsystem.publisher.ISResource.ISRESOURCETYPE;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.informationsystem.publisher.impl.GCUBEPublisherException;

import org.w3c.dom.Document;

/**
 * 
 * Reference implementation of the {@link ISGenericPublisher} for WS-DAIX
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class GCUBEGenericPublisher implements ISGenericPublisher {

    protected static final GCUBELog logger = new GCUBELog(ISGenericPublisher.class);
        
    private static Map<GCUBEScope, Set<EndpointReferenceType>>rpdSinks = new HashMap<GCUBEScope, Set<EndpointReferenceType>>();

    private static Map<GCUBEScope, Set<EndpointReferenceType>> profileSinks = new HashMap<GCUBEScope, Set<EndpointReferenceType>>();

    private static Map<GCUBEScope, Set<EndpointReferenceType>> daixSinks = new HashMap<GCUBEScope, Set<EndpointReferenceType>>();


    /**
     * Registers a new resource 
     * @param resource the resource to register
     * @param scope the registration scope
     * @param type the type of the resource
     * @param metadata the optional metadata document
     * @throws Exception 
     */
    public void register(final ISResource resource, GCUBEScope scope, final Document metadata) throws ISPublisherException {
	List<ISResource> toRegister = new ArrayList<ISResource>();
	toRegister.add(resource);
	List<Document> toRegisterMetadata = new ArrayList<Document>();
	toRegisterMetadata.add(metadata);
	this.register(toRegister,resource.getCollection(), toRegisterMetadata, scope);
    }
    
    @Override
    public void register(final ISResource resource, GCUBEScope scope) throws ISPublisherException {
	this.register(resource, scope, null);
    }

    @Override
    public void register(List<ISResource> resources, GCUBEScope scope) throws ISPublisherException {
	//collection -> resources to add to the collection
	Map<String, List<ISResource>> map = new HashMap<String, List<ISResource>>();
	for (ISResource resource : resources) {
	    if (!map.containsKey(resource.getCollection()))
		map.put(resource.getCollection(), new ArrayList<ISResource>());
	    map.get(resource.getCollection()).add(resource);
	}
	for (String collection : map.keySet()) 
	    this.register(map.get(collection), collection, null, scope);
    }
    
   
    /**
     * Removes a registered resource 
     * @param resource the resource to remove
     * @param type the type of the resource
     * @param scope the unregistration scope
     * @throws ISPublisherException
     */
    public void remove(final ISResource resource, GCUBEScope scope) throws ISPublisherException  {
	List<ISResource> toRemove = new ArrayList<ISResource>();
	toRemove.add(resource);
	this.remove(toRemove, resource.getCollection(), scope);
    }    

    /**
     * Updates the resource
     */
    public void update(final ISResource resource, GCUBEScope scope) throws ISPublisherException  {	
	this.register(resource, scope);	
    }


    @Override
    public void update(List<ISResource> resources, GCUBEScope scope) throws ISPublisherException {
	this.register(resources, scope);
    }

    @Override
    public void remove(List<ISResource> resources, GCUBEScope scope) throws ISPublisherException {
	//collection -> resources to remove from that collection
	Map<String, List<ISResource>> map = new HashMap<String, List<ISResource>>();
	for (ISResource resource : resources) {
	    if (!map.containsKey(resource.getCollection()))
		map.put(resource.getCollection(), new ArrayList<ISResource>());
	    map.get(resource.getCollection()).add(resource);
	}
	for (String collection : map.keySet()) 
	    this.remove(map.get(collection), collection, scope);
	
    }
    
    private Set<EndpointReferenceType> getSinks(ISRESOURCETYPE isresourcetype, GCUBEScope scope) throws ISPublisherException  {
	switch (isresourcetype) { 
        	case PROFILE:
        	    //load the IC for Profiles
        	    if (!profileSinks.containsKey(scope))
        		profileSinks.put(scope, SinkLoader.loadResourceSinks(scope));
        	    return profileSinks.get(scope);
        	case RPD:
        	  //load the ICs for RPDs
        	    if (!rpdSinks.containsKey(scope))
        		rpdSinks.put(scope, SinkLoader.loadStateSinks(scope));
        	    return rpdSinks.get(scope);
        	case WSDAIX:
        	  //load generic ICs
        	    if (!daixSinks.containsKey(scope))
        		daixSinks.put(scope, SinkLoader.loadWSDAIXSinks(scope)); 
        	    return daixSinks.get(scope);		        		    
	}
	throw new GCUBEPublisherException("Unable to find any IC instance to publish");
    }


    /**
     * 
     * @param resources
     * @param type
     * @param collection
     * @param metadata
     * @param scope
     * @throws ISPublisherException
     */
    private void register(List<ISResource> resources,  
	    String collection, List<Document> metadata, GCUBEScope scope) throws ISPublisherException {
	Set<EndpointReferenceType> ICEprs = this.getSinks(resources.get(0).getType(), scope);
	//prepare the structures for the calls
	String[] resourcesID = new String[resources.size()];
	for (int i=0; i< resources.size(); i++)
	    resourcesID[i] = resources.get(i).getID();
	Document[] documents = new Document[resources.size()];
	for (int i=0; i< resources.size(); i++)
	    documents[i] = resources.get(i).getDocument();
	
	for (EndpointReferenceType sink : ICEprs) {
	    int attempts = 0;
	    boolean registered = false;
	    while (attempts ++ <3 && !registered) {
        	    try {
        		CollectorClient.addDocuments(sink, scope, 
        			    new org.apache.axis.types.URI("gcube://unused"),
        			    new org.apache.axis.types.URI("gcube://" + collection), 
        			    resourcesID, documents, metadata.toArray(new Document[metadata.size()]));
        		
        		registered = true;
        		break;
        	    } catch (Exception e) {
        		    logger.error("Failed to send the Resource to the IC instance in scope " + scope, e);
        		  
        	    }
	    }
	    if (!registered)
		throw new GCUBEPublisherException("Failed to send the Resource to the IC instance in scope " + scope);
	    
	}   
    }

 
    /**
     * Removes resources from the given collection in a bulk way
     * @param resources
     * @param type
     * @param collection
     * @param scope
     * @throws ISPublisherException
     */
    private void remove(List<ISResource> resources, String collection, GCUBEScope scope) throws ISPublisherException {
	Set<EndpointReferenceType> ICEprs = this.getSinks(resources.get(0).getType(), scope);	
	String[] resourcesID = new String[resources.size()];
	for (int i=0; i< resources.size(); i++)
	    resourcesID[i] = resources.get(i).getID();	
	for (EndpointReferenceType sink : ICEprs) {	    
		int attempts = 0;
		boolean unregistered = false;
		while (attempts++ < 3 && !unregistered) {
		    try {
        		CollectorClient.removeDocuments(sink, scope, new org.apache.axis.types.URI("gcube://unused"),
        		  new org.apache.axis.types.URI("gcube://" +collection),  resourcesID);
        		unregistered = true;
        		break;
		    } catch (Exception e) {
		        logger.error("Failed to remove the Resource from the IC instance in scope " + scope, e);		        
		    }
		}	
		if (!unregistered)
		    throw new GCUBEPublisherException("Failed to remove the Resources from the IC instance in scope " + scope);
	}   
    }
   
}
