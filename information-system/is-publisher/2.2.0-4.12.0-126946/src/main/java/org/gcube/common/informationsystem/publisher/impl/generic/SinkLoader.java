package org.gcube.common.informationsystem.publisher.impl.generic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.informationsystem.publisher.ISPublisherException;
import org.gcube.common.core.informationsystem.publisher.ISResource;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.scope.GCUBEScopeNotSupportedException;
import org.gcube.common.core.scope.ServiceMap;
import org.gcube.common.core.scope.ServiceMap.ServiceType;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.informationsystem.publisher.impl.GCUBEPublisherException;

/**
 * 
 * Loads the sinks where to publish an {@link ISResource}
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class SinkLoader {

    protected static final GCUBELog logger = new GCUBELog(SinkLoader.class);
    
    private static Map<String, Set<EndpointReferenceType>> cachedInstanceAddressMap = new HashMap<String, Set<EndpointReferenceType>>();
    
    private static Map<String, Set<EndpointReferenceType>> cachedResourceAddressMap = new HashMap<String, Set<EndpointReferenceType>>();
       
    private static Map<String, Set<EndpointReferenceType>> cachedWSDAIXAddressMap = new HashMap<String, Set<EndpointReferenceType>>();

    
    public static Set<EndpointReferenceType> loadStateSinks(GCUBEScope publishingScope) throws ISPublisherException {
	
	ServiceMap map = getMap(publishingScope);
	Set<EndpointReferenceType> ICEprs = new HashSet<EndpointReferenceType>();		
	if (cachedInstanceAddressMap.containsKey(publishingScope.toString()))
	    return cachedInstanceAddressMap.get(publishingScope.toString());
	else {
	    // get the list of IC where to register the RPs
	    if (map.getEndpoints(ServiceType.ISICStateCollectionPT) != null)
		ICEprs.addAll(map.getEndpoints(ServiceType.ISICStateCollectionPT));
	    if (map.getEndpoints(ServiceType.ISICAllCollectionPT) != null)
		ICEprs.addAll(map.getEndpoints(ServiceType.ISICAllCollectionPT));
	    if (ICEprs.size() == 0)
		throw new GCUBEPublisherException("Unable to find any IC instance to publish RPDocuments");
	    cachedInstanceAddressMap.put(publishingScope.toString(),ICEprs);
	    return ICEprs;
	}
    }

    public static Set<EndpointReferenceType> loadResourceSinks(GCUBEScope publishingScope) throws ISPublisherException {

	ServiceMap map = getMap(publishingScope);
	Set<EndpointReferenceType> ICEprs = new HashSet<EndpointReferenceType>();
	if (cachedResourceAddressMap.containsKey(publishingScope.toString()))
	    return cachedResourceAddressMap.get(publishingScope.toString());
	else {

	    if (map.getEndpoints(ServiceMap.ServiceType.ISICProfileCollectionPT) != null)
		ICEprs.addAll(map.getEndpoints(ServiceMap.ServiceType.ISICProfileCollectionPT));
	    if (map.getEndpoints(ServiceMap.ServiceType.ISICAllCollectionPT) != null)
		ICEprs.addAll(map.getEndpoints(ServiceMap.ServiceType.ISICAllCollectionPT));
	    if (ICEprs.size() == 0)
		throw new GCUBEPublisherException("Unable to find any IC instance to publish GCUBE Resources");
	    cachedResourceAddressMap.put(publishingScope.toString(),ICEprs);
	    return ICEprs;
	}
    }
    

    public static Set<EndpointReferenceType> loadWSDAIXSinks(GCUBEScope publishingScope) throws ISPublisherException {

	ServiceMap map = getMap(publishingScope);	
	Set<EndpointReferenceType> ICEprs = new HashSet<EndpointReferenceType>();
	if (cachedWSDAIXAddressMap.containsKey(publishingScope.toString()))
	    return cachedWSDAIXAddressMap.get(publishingScope.toString());
	else {

	    if (map.getEndpoints(ServiceMap.ServiceType.ISICWSDAIXCollectionPT) != null)
		ICEprs.addAll(map.getEndpoints(ServiceMap.ServiceType.ISICWSDAIXCollectionPT));
	    if (map.getEndpoints(ServiceMap.ServiceType.ISICAllCollectionPT) != null)
		ICEprs.addAll(map.getEndpoints(ServiceMap.ServiceType.ISICAllCollectionPT));
	    if (ICEprs.size() == 0)
		throw new GCUBEPublisherException("Unable to find any IC instance to publish DAIX Resources");
	    cachedWSDAIXAddressMap.put(publishingScope.toString(),ICEprs);
	    return ICEprs;
	}
    }

    private static ServiceMap getMap(GCUBEScope scope) throws ISPublisherException {
	ServiceMap map = null;
	try {
	    map = scope.getServiceMap();
	} catch (GCUBEScopeNotSupportedException e) {
	    logger.error("error retrieving service map for scope " + scope.toString(), e);
	    throw new GCUBEPublisherException(e.getMessage());
	}
	
	return map;
    }
}
