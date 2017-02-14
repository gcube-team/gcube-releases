package org.gcube.common.informationsystem.publisher.impl.registrations.states;
//
//import java.io.StringWriter;
//import java.net.URL;
//import java.rmi.RemoteException;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Set;
//
//import org.apache.axis.message.MessageElement;
//import org.apache.axis.message.addressing.EndpointReferenceType;
//import org.apache.axis.message.addressing.ReferencePropertiesType;
//
//
//import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
//import org.gcube.common.core.resources.GCUBEResource;
//import org.gcube.common.core.scope.GCUBEScope;
//import org.gcube.common.core.scope.GCUBEScopeNotSupportedException;
//import org.gcube.common.core.scope.ServiceMap;
//import org.gcube.common.core.scope.ServiceMap.ServiceType;
//import org.gcube.common.core.state.GCUBEWSResource;
//import org.gcube.common.core.utils.logging.GCUBELog;
//import org.gcube.informationsystem.collector.stubs.DocumentNotFoundFaultType;
//import org.gcube.informationsystem.collector.stubs.GetProfileCriteria;
//import org.gcube.informationsystem.collector.stubs.GetProfileLastModificationTimeMsCriteria;
//import org.gcube.informationsystem.collector.stubs.GetResourceCriteria;
//import org.gcube.informationsystem.collector.stubs.XMLCollectionAccessPortType;
//import org.gcube.informationsystem.collector.stubs.service.XMLCollectionAccessServiceLocator;
//
///**
// * Check whether a resource (either a gcube or WS resource) is registered on the IS or not
// *
// * @author Manuele Simi (ISTI-CNR)
// *
// */
public class ResourceChecker {
//    
//    protected  GCUBEResource gcubeResource;
//    
//    protected  GCUBEWSResource wsResource;
//    
//    protected String ID;
//    
//    protected String type;
//    
//    protected GCUBEScope[] scopes;
//    
//    protected final long CHECKINTERVAL = 2000;
//        
//    protected static final GCUBELog logger = new GCUBELog(ResourceChecker.class);    
//    
//    private enum ACTION {REGISTERED, REMOVED, UPDATED}
//    
//    private Map<GCUBEScope, Long> oldProfiles;
//    
//    /**
//     * @param resource the resource to check
//     * @param scope the scope in which the resource is supposed to be modified
//     */
//    public ResourceChecker(final GCUBEResource resource, GCUBEScope scope) {
//	this.gcubeResource = resource;
//	this.ID = this.gcubeResource.getID();
//	this.type = this.gcubeResource.getType();
//	this.scopes = new GCUBEScope[1];
//	this.scopes[0] = scope;	
//    }
//    
//    /**
//     * @param resource the resource to check
//     * @param scope the scope in which the resource is supposed to be modified
//     * @throws Exception 
//     */
//    public ResourceChecker(final GCUBEWSResource resource, GCUBEScope ...  scope) throws Exception  {
//	this.wsResource = resource;
//	//build the ID following the IS-IC conventions, which is the following:
//	//resourceID = this.source.replace("http://", "").replace(":", "").replace("/", "-") + "-" + this.sourceKey.replace("http://", "").replace(":", "").replace("/", "-");
//	this.ID = this.wsResource.getEPR().getAddress().toString().replace("http://", "").replace(":", "").replace("/", "-");
//	this.ID += "-";
//	ReferencePropertiesType prop = this.wsResource.getEPR().getProperties();
//	if (prop != null) {
//		MessageElement[] any = prop.get_any();
//		if (any.length > 0) {this.ID += any[0].getValue().replace("http://", "").replace(":", "").replace("/", "-");}
//	}	
//	this.scopes = scope;
//    }
//    
//    /**
//     * @param id the resource ID
//     * @param type the resoruce type
//     * @param scope the scope in which the resource is supposed to be modified
//     */
//    public ResourceChecker(String id, String type, GCUBEScope scope) {
//	this.ID = id;
//	this.type = type;
//	this.scopes = new GCUBEScope[1];
//	this.scopes[0] = scope;
//    }
//
//    /**
//     * Checks if the resource is already registered in the IS
//     * @param port 
//     * @return true if the resource is registered, false otherwise
//     * @throws RemoteException 
//     */
//    private boolean isRegistered(XMLCollectionAccessPortType port) throws RemoteException {
//	String profile = null;
//	if (gcubeResource != null) {	    
//	    try {
//		profile = port.getProfile(new GetProfileCriteria(this.gcubeResource.getID(), this.type));		    
//	    } catch (DocumentNotFoundFaultType e) {
//		logger.trace("Resource "+ this.ID +" not found");
//		return false;		   
//	    } catch (RemoteException e) {
//		logger.error("Unable to check resource " + this.ID);
//		throw e;
//	    }	
//	    
//	 } else if (wsResource != null) {
//	     try {
//		 profile = port.getResource(new GetResourceCriteria(this.ID));
//	     } catch (DocumentNotFoundFaultType e) {
//		 logger.trace("Resource "+ this.ID +" not found");
//		 return false;		   
//	     } catch (RemoteException e) {
//		 logger.error("Unable to check resource " + this.ID);
//		 throw e;
//	     }	
//	 }
//	  if ((profile == null) || (profile.compareTo("") == 0)) {
//	      logger.trace("Resource "+ this.ID +" not found");
//	    	return false;
//	    } else {
//		logger.trace("Resource "+ this.ID +" found");
//		return true;
//	 }
//    }
//    /**
//     * @param port
//     * @return
//     */
//    private boolean isUpdated(XMLCollectionAccessPortType port, GCUBEScope scope) throws Exception {
//	long profileTime;
//	if (gcubeResource != null) {	    
//	    try {
//		profileTime = port.getProfileLastModificationTimeMs(new GetProfileLastModificationTimeMsCriteria(this.ID, this.type));		    
//	    } catch (DocumentNotFoundFaultType e) {
//		logger.trace("Resource "+ this.ID +" not found");
//		return false;		   
//	    } catch (RemoteException e) {
//		logger.error("Unable to check resource " + this.ID);
//		throw e;
//	    }	    
//	    StringWriter writer = new StringWriter();
//	    this.gcubeResource.store(writer);
//	    //logger.trace("Previous profile last modification time was " + oldProfiles.get(scope));
//	    //logger.trace("Actual profile last modification time " + profileTime);
//	    if (profileTime > oldProfiles.get(scope)) {//they are different, it has been updated
//		logger.trace("Resource "+ this.ID +" on IS has been updated");
//		return true;
//	    }
//	    else {
//		logger.trace("Resource "+ this.ID +" on IS has not been updated yet");
//		return false;
//	    }
//	    
//	 } else 
//	     throw new Exception("Unable to check the update of a WSResource");
//	
//    }
//    
//    /**
//     * Checks if the resource is available in the IS
//     * @param port 
//     * @return true if the resource is not in the IS, false otherwise
//     */
//    private boolean isRemoved(XMLCollectionAccessPortType port) throws RemoteException {
//	String profile = null;
//	if (gcubeResource != null) {	    
//	    try {
//		profile = port.getProfile(new GetProfileCriteria(this.gcubeResource.getID(), this.type));		    
//	    } catch (DocumentNotFoundFaultType e) {
//		logger.trace("Resource "+ this.ID +" not found");
//		return true;		   
//	    } catch (RemoteException e) {
//		logger.error("Unable to check resource " + this.ID);
//		throw e;
//	    }	
//	    
//	 } else if (wsResource != null) {
//	     try {
//		 profile = port.getResource(new GetResourceCriteria(this.ID));
//	     } catch (DocumentNotFoundFaultType e) {
//		 logger.trace("Resource"+ this.ID +" not found");
//		 return true;		   
//	     } catch (RemoteException e) {
//		 logger.error("Unable to check resource " + this.ID);
//		 throw e;
//	     }	
//	 }
//	  if ((profile == null) || (profile.compareTo("") == 0)) {
//	      logger.trace("Resource "+ this.ID +" not found");
//	    	return true;
//	    } else {
//		logger.trace("Resource "+ this.ID +" found");
//		return false;
//	 }
//    }
//    
//    /**
//     * Holds until the resource has been registered in the IS
//     * 
//     * @throws Exception 
//     * 
//     */
//    public void waitUntilRegistered() throws Exception {
//	while (true) {
//	    if (this.checkResource(ACTION.REGISTERED))
//		break;
//	    Thread.sleep(CHECKINTERVAL);
//	}
//    }        
//
//    /**
//     * Holds until the resource has been removed from the IS
//     */
//    public void waitUntilRemoved() throws Exception {
//	while (true) {
//	    if (this.checkResource(ACTION.REMOVED))
//		break;
//	    Thread.sleep(CHECKINTERVAL);
//	}
//    }
//
//    /**
//     * Holds until the resource has been updated in the IS
//     */
//    public void waitUntilUpdated() throws Exception {
//	if (oldProfiles == null) 
//		throw new Exception("Old profiles were not correctly harvested, invoke the harvest() method before");	    
//	while (true) {
//	    if (this.checkResource(ACTION.UPDATED))
//		break;
//	    Thread.sleep(CHECKINTERVAL);
//    	}
//    }
//    
//    private Set<EndpointReferenceType> loadICEPRs(GCUBEScope scope) throws Exception {
//	Set<EndpointReferenceType> EPRs = new HashSet<EndpointReferenceType>();
//	ServiceMap map;
//	try {
//	    map = scope.getServiceMap();
//	} catch (GCUBEScopeNotSupportedException e1) {
//	    logger.error("error retrieving service map for scope "   + scope.toString(), e1);
//	    return EPRs;
//	}
//	
//	// get the list of IC where to register the RPs
//	if (gcubeResource != null) {
//	    if (map.getEndpoints(ServiceType.ISICProfileCollectionPT) != null)
//		EPRs.addAll(map.getEndpoints(ServiceType.ISICProfileCollectionPT));
//	} else {
//	    if (map.getEndpoints(ServiceType.ISICStateCollectionPT) != null)
//		EPRs.addAll(map.getEndpoints(ServiceType.ISICStateCollectionPT));
//	}
//	
//	if (map.getEndpoints(ServiceType.ISICAllCollectionPT) != null)
//	    EPRs.addAll(map.getEndpoints(ServiceType.ISICAllCollectionPT));	
//	
//	if (EPRs == null || EPRs.size() == 0)
//	    throw new Exception("Unable to find a valid IC instance for scope " + scope.getName()+", please check the local Service Maps");
//	
//	 return EPRs;
//    }
//    
//    /**
//     * Checks if the action has been performed on the resource
//     * @param action the {@link ACTION} to check
//     * @return true if the action has been performed, false otherwise
//     * @throws RemoteException
//     * @throws Exception
//     */
//    private boolean checkResource(ACTION action) throws RemoteException, Exception {
//	for (GCUBEScope scope : scopes)
//        	for (EndpointReferenceType epr : this.loadICEPRs(scope)) {
//        	    String isIcAddress = epr.getAddress().toString();
//        	    logger.debug("Checking resource at " + isIcAddress);
//        	    XMLCollectionAccessPortType port = null;
//        	    try {
//        		    port = new XMLCollectionAccessServiceLocator().getXMLCollectionAccessPortTypePort(new URL(isIcAddress));
//        		    port = GCUBERemotePortTypeContext.getProxy(port, scope);        		    
//        	    } catch (Exception e) {
//        		    logger.fatal("Unable to create a valid portType for " + isIcAddress,e);
//        		    throw e;
//        	    }
//        	    if (action == ACTION.REGISTERED) 
//        		return this.isRegistered(port);
//        	    else if (action == ACTION.UPDATED)
//        		return this.isUpdated(port, scope);
//        	    else if (action == ACTION.REMOVED)
//        		return this.isRemoved(port);
//        	}
//	return true;
//    }
//
//    /**
//     * @throws Exception 
//     * 
//     */
//    public void harvest() throws Exception {
//	
//	oldProfiles = new HashMap<GCUBEScope, Long>();
//	for (GCUBEScope scope : scopes)
//    	 for (EndpointReferenceType epr : this.loadICEPRs(scope)) {
//    	    String isIcAddress = epr.getAddress().toString();
//    	    logger.debug("Checking resource at " + isIcAddress);
//    	    XMLCollectionAccessPortType port = null;
//    	    try {
//    		    port = new XMLCollectionAccessServiceLocator().getXMLCollectionAccessPortTypePort(new URL(isIcAddress));
//    		    port = GCUBERemotePortTypeContext.getProxy(port, scope);
//    		    long profileTime = port.getProfileLastModificationTimeMs(new GetProfileLastModificationTimeMsCriteria(this.ID, this.type));
//		    oldProfiles.put(scope, profileTime);
//    	    } catch (DocumentNotFoundFaultType e) {
//		 logger.trace("Resource"+ this.ID +" not found at harvesting time");
//		 oldProfiles.put(scope, 0L);
//    	    } catch (Exception e) {
//    		    logger.fatal("Unable to create a valid portType for " + isIcAddress,e);
//    		    throw e;
//    	    }
//    	    
//    	}
//	
//    }
//
// 
}
