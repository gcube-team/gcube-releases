package org.gcube.common.informationsystem.client.eximpl;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.ISException;
import org.gcube.common.core.informationsystem.client.ISTemplateQuery;
import org.gcube.common.core.informationsystem.client.ISClient.ISMalformedResultException;
import org.gcube.common.core.resources.GCUBEResource;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.scope.GCUBEScopeNotSupportedException;
import org.gcube.common.core.scope.ServiceMap;
import org.gcube.common.core.utils.logging.GCUBELog;


public abstract class GCUBEResourceAbstractQueryImpl<RESOURCE extends GCUBEResource> extends ISTemplateQueryImpl<RESOURCE> implements ISTemplateQuery<RESOURCE> {

	private static Map<String, List<EndpointReferenceType>> addressMap= new HashMap<String, List<EndpointReferenceType>>();
	
	protected  GCUBELog logger= new GCUBELog(GCUBEResourceAbstractQueryImpl.class);
	
	protected RESOURCE parseResult(String result) throws ISMalformedResultException {
		try {
			RESOURCE resource = GHNContext.getImplementation(this.getResourceClass());
			resource.load(new StringReader(result));
			return resource;
		
		}catch(Exception e) {throw new ISMalformedResultException(new Exception(result, e));}
	}

	protected String getRoot() {return "Data/is:Profile/Resource";}	
	protected abstract Class<RESOURCE> getResourceClass();
	
	protected List<EndpointReferenceType> getISICEPRs(GCUBEScope scope) throws ISException{
		if (addressMap.containsKey(scope.toString()))
			return addressMap.get(scope.toString());
		else{
			ServiceMap map = null;
			List<EndpointReferenceType> returnList= new ArrayList<EndpointReferenceType>();
			logger.trace("Getting IS information for GCUBEScope "+scope.toString());
			try {
				map =scope.getServiceMap();
			} catch (GCUBEScopeNotSupportedException e) {
				throw new ISException(e.getMessage());
			}
			if(map.getEndpoints(ServiceMap.ServiceType.ISICProfileQueryPT)!=null)
				returnList.addAll(map.getEndpoints(ServiceMap.ServiceType.ISICProfileQueryPT));
			if(map.getEndpoints(ServiceMap.ServiceType.ISICAllQueryPT)!=null)
				returnList.addAll(map.getEndpoints(ServiceMap.ServiceType.ISICAllQueryPT));
			if (returnList.size()==0) throw new ISException("Impossible to retrieve a valid address of IS-IC in the Service Map");
			addressMap.put(scope.toString(), returnList);
			return returnList;
		}
	}
	
}

