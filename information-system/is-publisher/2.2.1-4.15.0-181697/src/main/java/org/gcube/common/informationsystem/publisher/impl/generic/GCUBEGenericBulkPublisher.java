package org.gcube.common.informationsystem.publisher.impl.generic;


import ise.antelope.tasks.FinallyTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.informationsystem.publisher.ISGenericPublisher;
import org.gcube.common.core.informationsystem.publisher.ISPublisherException;
import org.gcube.common.core.informationsystem.publisher.ISResource;
import org.gcube.common.core.informationsystem.publisher.ISResource.ISRESOURCETYPE;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.informationsystem.publisher.impl.GCUBEPublisherException;
import org.gcube.common.informationsystem.publisher.impl.context.ISPublisherContext;
import org.w3c.dom.Document;

/**
 * A publisher that manages registrations/updates/removals in bulk. 
 * Operations are grouped and periodically executed on bunch of resources per call
 * 
 * @author Manuele Simi (ISTI-CNR)
 * 
 */
public class GCUBEGenericBulkPublisher implements ISGenericPublisher {

    protected static final GCUBELog logger = new GCUBELog(GCUBEGenericBulkPublisher.class);

    private static Map<GCUBEScope, ReentrantLock> locks = new HashMap<GCUBEScope, ReentrantLock>();

    // holds per each collection the list of the resources to be registered
    static Map<GCUBEScope, Map<String, Set<ResourceData>>> toRegisterQueue = 
	    Collections.synchronizedMap(new HashMap<GCUBEScope, Map<String, Set<ResourceData>>>());

    // holds per each collection the list of the resources to be removed
    static Map<GCUBEScope, Map<String, Set<ISResource>>> toRemoveQueue = 
	    Collections.synchronizedMap(new HashMap<GCUBEScope, Map<String, Set<ISResource>>>());

    static final long PUBLICATION_INTERVAL = 20000;

    private static Map<GCUBEScope, Set<EndpointReferenceType>>rpdSinks = new HashMap<GCUBEScope, Set<EndpointReferenceType>>();

    private static Map<GCUBEScope, Set<EndpointReferenceType>> profileSinks = new HashMap<GCUBEScope, Set<EndpointReferenceType>>();

    private static Map<GCUBEScope, Set<EndpointReferenceType>> daixSinks = new HashMap<GCUBEScope, Set<EndpointReferenceType>>();

    static {
	new ParallelPublisher("BulkPublisher").start();

    }


    synchronized public void register(ISResource resource, GCUBEScope scope, Document metadata) throws ISPublisherException {
	synchronized (locks) {
	    if (!locks.containsKey(scope))
		locks.put(scope, new ReentrantLock());
	}
	logger.trace("Schedule registration of resource " + resource.getID() + " to  " + resource.getCollection() + " in scope " + scope );
	if (!toRegisterQueue.containsKey(scope))
	    toRegisterQueue.put(scope, new HashMap<String, Set<ResourceData>>());

	if (!toRegisterQueue.get(scope).containsKey(resource.getCollection()))
	    toRegisterQueue.get(scope).put(resource.getCollection(), new HashSet<ResourceData>());
	//remove the data if it was already in the queue
	ResourceData data = new ResourceData(resource,metadata);
	logger.trace("Queue size before clean up is " + toRegisterQueue.get(scope).get(resource.getCollection()).size());
	ReentrantLock lock = locks.get(scope);
	lock.lock();
	try{
	    if (toRegisterQueue.get(scope).get(resource.getCollection()).contains(data)) 
		toRegisterQueue.get(scope).get(resource.getCollection()).remove(data);
	    logger.trace("Adding " + resource.getID() + " to the registration queue");
	    logger.trace("Queue size before is " + toRegisterQueue.get(scope).get(resource.getCollection()).size());
	    toRegisterQueue.get(scope).get(resource.getCollection()).add(data);
	}finally{
	    lock.unlock();
	}
	logger.trace("Queue size after is " + toRegisterQueue.get(scope).get(resource.getCollection()).size());

    }

    @Override
    synchronized public void register(final ISResource resource, GCUBEScope scope) throws ISPublisherException {
	this.register(resource, scope, null);
    }

    @Override
    public void register(List<ISResource> resources, GCUBEScope scope) throws ISPublisherException {
	for (ISResource resource : resources)
	    this.register(resource, scope);
    }

    @Override
    public void update(List<ISResource> resources, GCUBEScope scope) throws ISPublisherException {
	for (ISResource resource : resources)
	    this.update(resource, scope);
    }

    @Override
    public void remove(List<ISResource> resources, GCUBEScope scope) throws ISPublisherException {
	for (ISResource resource : resources)
	    this.remove(resource, scope);
    }

    @Override
    synchronized public void update(ISResource resource, GCUBEScope scope) throws ISPublisherException {
	this.register(resource, scope);
    }

    @Override
    synchronized public void remove(ISResource resource, GCUBEScope scope) throws ISPublisherException {
	logger.trace("Schedule removal of resource " + resource.getID() + " from  " + resource.getCollection() + " in scope " + scope );
	if (!toRemoveQueue.containsKey(scope))
	    toRemoveQueue.put(scope, new HashMap<String, Set<ISResource>>());

	if (!toRemoveQueue.get(scope).containsKey(resource.getCollection()))
	    toRemoveQueue.get(scope).put(resource.getCollection(), new HashSet<ISResource>());

	toRemoveQueue.get(scope).get(resource.getCollection()).add(resource);
    }


    /**
     * 
     * @author Manuele Simi (ISTI-CNR)
     */
    class ResourceData {
	ISResource resource;
	Document metadata;

	ResourceData(ISResource resource, Document metadata) {
	    this.resource = resource;
	    this.metadata = metadata;
	}
	@Override
	public int hashCode() {
	    return resource.getID().hashCode();
	}
	@Override
	public boolean equals(Object obj) {
	    if (this == obj)
		return true;
	    if (obj == null)
		return false;
	    if (getClass() != obj.getClass())
		return false;
	    ResourceData other = (ResourceData) obj;
	    if (resource == null) {
		if (other.resource != null)
		    return false;
	    } else if (!resource.getID().equals(other.resource.getID()))
		return false;
	    return true;
	}
    }
    static class ParallelPublisher extends Thread {

	public ParallelPublisher(String name) {
	    super(name);
	}

	@Override
	public void run() {
	    int interval = this.getInterval();
	    while (true) {
		try {
		    Thread.sleep(interval);
		} catch (InterruptedException e) {
		    new ParallelPublisher("BulkPublisher").start();
		    break;
		}
		this.manageRegistrations();
		this.manageDeletions();
	    }
	}

	private int getInterval() {		
	    try {
		return (Integer)ISPublisherContext.getContext().getProperty(ISPublisherContext.BULK_PUBLICATIONS_INTERVAL_PROP_NAME);
	    } catch (Exception e) {
		return 20000;
	    }
	}

	private void manageRegistrations() {
	    List<GCUBEScope> scopes = new ArrayList<GCUBEScope>(toRegisterQueue.keySet());
	    for (GCUBEScope scope : scopes ) {
		synchronized (locks) {
		    if (!locks.containsKey(scope))
			locks.put(scope, new ReentrantLock());
		}
		ReentrantLock lock = locks.get(scope);
		Map<String, Set<ResourceData>> collections = toRegisterQueue.get(scope);
		for (String collection : collections.keySet()) {
		    if (collections.get(collection).size() > 0) {
			Set<ResourceData> resourcesToPublish= null;
			try {
			    logger.trace("Re-publishing " + collections.get(collection).size() + " documents in " +collection);
			    lock.lock();
			    try{
				resourcesToPublish = Collections.unmodifiableSet(new HashSet<ResourceData>(collections.get(collection)));
				collections.get(collection).clear();	
			    }finally{
				lock.unlock();
			    }
			    publish(scope, collection, resourcesToPublish);
			} catch (Exception e) {
			    logger.error("Unable to publish resources for " + collection + " in scope " + scope, e);
			    lock.lock();
			    try{
				if (resourcesToPublish!=null && resourcesToPublish.size()>0)
				    collections.get(collection).addAll(resourcesToPublish);
			    }finally{
				lock.unlock();
			    }
			}
			// a pause to do not block the insertions in the queue for a long time
			// and to relax the IC service instances
			try {
			    Thread.sleep(500);
			} catch (InterruptedException e) {
			    new ParallelPublisher("BulkPublisher").start();
			}
		    }
		}

	    }
	}

	private void manageDeletions() {
	    synchronized (toRemoveQueue) {
		for (GCUBEScope scope : toRemoveQueue.keySet()) {
		    Map<String, Set<ISResource>> collections = toRemoveQueue.get(scope);
		    for (String collection : collections.keySet()) {
			if (collections.get(collection).size() > 0) {
			    try {
				erase(scope, collection, collections.get(collection));
				collections.get(collection).clear();
			    } catch (Exception e) {
				logger.error("Unable to remove resources for " + collection + " in scope " + scope, e);
			    }
			    // a pause to do not block the insertions in the queue for a long time
			    try {
				Thread.sleep(500);
			    } catch (InterruptedException e) {
				new ParallelPublisher("BulkPublisher").start();
			    }
			}
		    }
		}
	    }
	}

	private void publish(GCUBEScope scope, String collection, Set<ResourceData> resources) throws Exception {
	    Document[] documents = new Document[resources.size()];
	    Document[] metadata = new Document[resources.size()];
	    String[] documentNames = new String[resources.size()];
	    int i = 0;
	    for (ResourceData data : resources) {
		documents[i] = data.resource.getDocument();
		metadata[i] = data.metadata;
		documentNames[i] = data.resource.getID();
		i++;
	    }

	    Set<EndpointReferenceType> ICEprs = this.getSinks(resources.iterator().next().resource.getType(), scope);	

	    for (EndpointReferenceType sink : ICEprs) {
		int attempts = 0;
		boolean registered = false;
		while (attempts ++ <3 && !registered) {
		    logger.trace("Adding "+ resources.size() +" resources to " + collection + " in scope " + scope);
		    try {
			CollectorClient.addDocuments(sink, scope, 
				new org.apache.axis.types.URI("gcube://unused"),
				new org.apache.axis.types.URI("gcube://" + collection), 
				documentNames, documents, metadata);

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

	private void erase(GCUBEScope scope, String collection, Set<ISResource> resources) throws Exception {
	    String[] documentNames = new String[resources.size()];
	    int i = 0;
	    for (ISResource resource : resources)
		documentNames[i++] = resource.getID();

	    Set<EndpointReferenceType> ICEprs = this.getSinks(resources.iterator().next().getType(), scope);		    
	    for (EndpointReferenceType sink : ICEprs) {	    
		int attempts = 0;
		boolean unregistered = false;
		while (attempts++ < 3 && !unregistered) {
		    logger.trace("Removing "+ resources.size() +" resources from " + collection + " in scope " + scope);
		    try {
			CollectorClient.removeDocuments(sink, scope, new org.apache.axis.types.URI("gcube://unused"),
				new org.apache.axis.types.URI("gcube://" +collection), documentNames);
			unregistered = true;
			break;
		    } catch (Exception e) {
			logger.error("Failed to remove the Resources from the IC instance in scope " + scope, e);		        
		    }
		}	
		if (!unregistered)
		    throw new GCUBEPublisherException("Failed to remove the Resources from the IC instance in scope " + scope);
	    }   
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
    }


}
