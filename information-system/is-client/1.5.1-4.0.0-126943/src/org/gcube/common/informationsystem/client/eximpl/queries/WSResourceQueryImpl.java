package org.gcube.common.informationsystem.client.eximpl.queries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.informationsystem.ISException;
import org.gcube.common.core.informationsystem.client.RPDocument;
import org.gcube.common.core.informationsystem.client.ISClient.ISMalformedResultException;
import org.gcube.common.core.informationsystem.client.queries.WSResourceQuery;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.scope.GCUBEScopeNotSupportedException;
import org.gcube.common.core.scope.ServiceMap;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.informationsystem.client.eximpl.ISTemplateQueryImpl;
import org.gcube.common.informationsystem.client.eximpl.RPDocumentImpl;



public class WSResourceQueryImpl extends ISTemplateQueryImpl<RPDocument> implements WSResourceQuery{
	
	protected  GCUBELog logger= new GCUBELog(WSResourceQueryImpl.class);
	
	private static Map<String, List<EndpointReferenceType>> addressMap= new HashMap<String, List<EndpointReferenceType>>();
	
	private static final String template = NS+"for $outer in collection(\"/db<COLLECTION/>\")//Document<ROOT/>, "+resultVar+" in  $outer/Data <FILTER/> return $outer";

	public WSResourceQueryImpl() {
		super();
		this.setExpression(template);
	}
	
	protected RPDocument parseResult(String result) throws ISMalformedResultException {
		try{return new RPDocumentImpl(result);}catch(Exception e) {throw new ISMalformedResultException(e);}
	}
	
	protected String getCollection() {return "Properties";}
	protected String getRoot() {return "";}

	
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
			if (map.getEndpoints(ServiceMap.ServiceType.ISICStateQueryPT)!=null)
				returnList.addAll(map.getEndpoints(ServiceMap.ServiceType.ISICStateQueryPT));
			if(map.getEndpoints(ServiceMap.ServiceType.ISICAllQueryPT)!=null)
				returnList.addAll(map.getEndpoints(ServiceMap.ServiceType.ISICAllQueryPT));
			if (returnList.size()==0) throw new ISException("Impossible to retrieve a valid address of IS-IC in the Service Map");
			addressMap.put(scope.toString(), returnList);
			return returnList ;
		}
	}
}