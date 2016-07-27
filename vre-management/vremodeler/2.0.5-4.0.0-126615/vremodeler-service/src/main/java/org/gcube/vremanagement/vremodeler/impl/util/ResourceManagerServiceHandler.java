package org.gcube.vremanagement.vremodeler.impl.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.queries.GCUBERIQuery;
import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.handlers.GCUBEServiceClient;
import org.gcube.common.core.utils.handlers.GCUBEServiceHandler;
import org.gcube.vremanagement.resourcemanager.stubs.binder.ResourceBinderPortType;
import org.gcube.vremanagement.resourcemanager.stubs.binder.service.ResourceBinderServiceAddressingLocator;
import org.gcube.vremanagement.vremodeler.impl.ServiceContext;

public abstract class ResourceManagerServiceHandler<T, P> extends GCUBEServiceHandler<GCUBEServiceClient> {

	private T returnValue;
	
	private P parameter;
	
	protected String usedhost="";
	
	public GCUBEScope scope= ServiceContext.getContext().getScope();
	public String relatedGhnId=null;
	public T getReturnValue(){
		return returnValue;
	}
	
	public void setParameter(P param){
		this.parameter= param;
	}
	
	protected P getParameter(){
		return this.parameter;
	}
	
	
	protected void setReturnValue(T returnValue){
		this.returnValue= returnValue;
	}
	
	@Override
	protected List<EndpointReferenceType> findInstances() throws Exception {
		logger.info("findinstance called");
		try{
			ISClient client = GHNContext.getImplementation(ISClient.class);
			GCUBERIQuery query = client.getQuery(GCUBERIQuery.class);
			
			query.setExpression("declare namespace is = 'http://gcube-system.org/namespaces/informationsystem/registry';" +
					" for $outer in collection(\"/db/Profiles/RunningInstance\")//Document/Data/is:Profile/Resource " +
					" let $scope:= $outer/Scopes/Scope[string() eq '"+scope.toString()+"'] " +
					" where count($scope)>0 " +
					" and $outer/Profile/ServiceName/string() eq 'ResourceManager' " +
					" return $outer");
			
			List<EndpointReferenceType> eprs = new ArrayList<EndpointReferenceType>();
			for (GCUBERunningInstance instance : client.execute(query, scope))
			    eprs.add(instance.getAccessPoint().getEndpoint("gcube/vremanagement/ResourceManager"));
			return eprs;
		}catch(Exception e){logger.error(e); throw e;}
	}
	
	protected abstract  T makeCall(ResourceBinderPortType rmPortType) throws Exception;
	
	
	protected void interact(EndpointReferenceType epr) throws Exception{
		ResourceBinderPortType rmpt= null;
		
		try{
			ResourceBinderServiceAddressingLocator vmsal= new ResourceBinderServiceAddressingLocator();
			rmpt= vmsal.getResourceBinderPortTypePort(epr);
			rmpt = GCUBERemotePortTypeContext.getProxy(rmpt,scope, Integer.parseInt((String)ServiceContext.getContext().getProperty("resourceManagerTimeout", true)));
			this.usedhost= epr.getAddress().getHost();
			this.setReturnValue(this.makeCall(rmpt));
		}catch(Exception e){logger.error(e);throw e; }
	}
	
}