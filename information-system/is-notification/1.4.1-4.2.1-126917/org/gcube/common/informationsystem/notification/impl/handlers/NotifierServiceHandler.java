package org.gcube.common.informationsystem.notification.impl.handlers;

import java.util.ArrayList;
import java.util.List;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.client.AtomicCondition;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.queries.GCUBERIQuery;
import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.security.GCUBESecurityManager;
import org.gcube.common.core.utils.handlers.GCUBEServiceClient;
import org.gcube.common.core.utils.handlers.GCUBEServiceClientImpl;
import org.gcube.common.core.utils.handlers.GCUBEServiceHandler;
import org.gcube.informationsystem.notifier.stubs.NotifierPortType;
import org.gcube.informationsystem.notifier.stubs.service.NotifierServiceAddressingLocator;


public abstract class NotifierServiceHandler<PARAMETER, RETURN> extends GCUBEServiceHandler<GCUBEServiceClient> {

	private static GCUBEServiceClient serviceClient= new GCUBEServiceClientImpl();
	
	private RETURN returnValue;
	
	private PARAMETER parameterValue;
	
	private GCUBEScope scope;
	
	private GCUBESecurityManager securityManager;
	
	public NotifierServiceHandler(){super(serviceClient);}
	
	
	protected PARAMETER getParameter(){return this.parameterValue;}
	
	protected void setReturnValue(RETURN returnValue){this.returnValue=returnValue;}
	
	protected void setScope(GCUBEScope scope){this.scope=scope;}
	
	public void setSecurityManager(GCUBESecurityManager securityManager){this.securityManager=securityManager;}
	
	@Override
	protected List<EndpointReferenceType> findInstances() throws Exception {
		ISClient client = GHNContext.getImplementation(ISClient.class);
		GCUBERIQuery query = client.getQuery(GCUBERIQuery.class);
		query.addAtomicConditions(new AtomicCondition("//ServiceName","IS-Notifier"));
		List<EndpointReferenceType> eprs = new ArrayList<EndpointReferenceType>();
		for (GCUBERunningInstance instance : client.execute(query, this.getScope()))
		    eprs.add(instance.getAccessPoint().getEndpoint("gcube/informationsystem/notifier/Notifier"));
		return eprs;
	}

	@Override
	protected void interact(EndpointReferenceType epr) throws Exception {
		NotifierServiceAddressingLocator notifierLocator = new NotifierServiceAddressingLocator();
        NotifierPortType port = notifierLocator.getNotifierPortTypePort(epr);
        port= GCUBERemotePortTypeContext.getProxy(port, this.getScope(), this.getSecurityManager());
		this.setReturnValue(makeCall(port));
	}
	
	@Override
	protected String getCacheKey() {
		String name = this.getTargetPortTypeName();
		GCUBEScope scope= this.getScope();
		return scope==null?name:name+scope.toString();
	}
	
	protected abstract RETURN makeCall(NotifierPortType notifierPort)throws Exception;
	
	public RETURN getReturnValue(){return this.returnValue;}
	
	public GCUBEScope getScope(){return scope;}
	
	public GCUBESecurityManager getSecurityManager(){return this.securityManager;}
	
	public void setParameter(PARAMETER parameter){this.parameterValue=parameter;}
		
}
