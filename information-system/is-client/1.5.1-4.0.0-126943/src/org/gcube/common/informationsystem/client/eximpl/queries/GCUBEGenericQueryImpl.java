package org.gcube.common.informationsystem.client.eximpl.queries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.informationsystem.ISException;
import org.gcube.common.core.informationsystem.client.XMLResult;
import org.gcube.common.core.informationsystem.client.ISClient.ISMalformedResultException;
import org.gcube.common.core.informationsystem.client.impl.AbstractXMLResult;
import org.gcube.common.core.informationsystem.client.queries.GCUBEGenericQuery;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.scope.GCUBEScopeNotSupportedException;
import org.gcube.common.core.scope.ServiceMap;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.informationsystem.client.eximpl.ExistQuery;


public class GCUBEGenericQueryImpl extends ExistQuery<XMLResult> implements GCUBEGenericQuery {

	protected  GCUBELog logger= new GCUBELog(GCUBEGenericQueryImpl.class);

	private static Map<String, List<EndpointReferenceType>> addressMap= new HashMap<String, List<EndpointReferenceType>>();
	
	protected XMLResult parseResult(String result) throws ISMalformedResultException {
		try{return new AbstractXMLResult(result);}catch(Exception e) {throw new ISMalformedResultException(e);}
	}
	
	protected List<EndpointReferenceType> getISICEPRs(GCUBEScope scope) throws ISException{
		if (addressMap.containsKey(scope.toString()))
			return addressMap.get(scope.toString());
		else{
			ServiceMap map = null;
			List<EndpointReferenceType> returnList=new ArrayList<EndpointReferenceType>();
			logger.trace("Getting IS information for GCUBEScope "+scope.toString());
			try {
				map =scope.getServiceMap();
			} catch (GCUBEScopeNotSupportedException e) {
				throw new ISException(e.getMessage());
			}
			if (map.getEndpoints(ServiceMap.ServiceType.ISICAllQueryPT)!=null)
				returnList.addAll(map.getEndpoints(ServiceMap.ServiceType.ISICAllQueryPT));
			if (returnList.size()==0) throw new ISException("Impossible to retrieve a valid address of IS-IC in the Service Map");
			addressMap.put(scope.toString(), returnList);
			return returnList;
		}
	}
}
