package org.gcube.common.handlers;


import static org.gcube.common.authorization.client.Constants.authorizationService;
import static org.gcube.common.core.contexts.GCUBERemotePortTypeContext.AUTH_TOKEN_HEADER_NAME;
import static org.gcube.common.core.contexts.GCUBERemotePortTypeContext.CALLED_METHOD_HEADER_NAME;
import static org.gcube.common.core.contexts.GCUBERemotePortTypeContext.CALLER_HEADER_NAME;
import static org.gcube.common.core.contexts.GCUBERemotePortTypeContext.CLASS_HEADER_NAME;
import static org.gcube.common.core.contexts.GCUBERemotePortTypeContext.NAME_HEADER_NAME;
import static org.gcube.common.core.contexts.GCUBERemotePortTypeContext.SCOPE_HEADER_NAME;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;
import org.gcube.accounting.datamodel.UsageRecord.OperationResult;
import org.gcube.accounting.datamodel.usagerecords.ServiceUsageRecord;
import org.gcube.accounting.persistence.AccountingPersistence;
import org.gcube.accounting.persistence.AccountingPersistenceFactory;
import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.PolicyUtils;
import org.gcube.common.authorization.library.policies.Policy;
import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.authorization.library.provider.ServiceIdentifier;
import org.gcube.common.authorization.library.provider.ServiceInfo;
import org.gcube.common.authorization.library.utils.Caller;
import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.faults.GCUBEException;
import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.faults.GCUBERetryEquivalentFault;
import org.gcube.common.core.faults.GCUBERetrySameFault;
import org.gcube.common.core.faults.GCUBEUnrecoverableException;
import org.gcube.common.core.faults.GCUBEUnrecoverableFault;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.security.GCUBEServiceAuthenticationController;
import org.gcube.common.core.security.GCUBEServiceAuthorizationController;
import org.gcube.common.core.security.GCUBEServiceSecurityController;
import org.gcube.common.core.utils.InnerMethodName;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.scope.api.ScopeProvider;
import org.globus.wsrf.impl.security.authentication.Constants;


public class GCUBEHandler  extends BasicHandler {

	/**Property of the message context that identifies services across requests and responses.*/
	public static final String SERVICECONTEXT_PROPERTY = "receiver";
	/**Property of the message context that identifies services across requests and responses.*/
	public static final String CALLER_PROPERTY = "caller";
	/**Class logger.*/
	protected static GCUBELog logger = new GCUBELog(GCUBEHandler.class);
	/**Serialisation ID*/
	private static final long serialVersionUID = 1L;


	private static ThreadLocal<Long> startCallThreadLocal = new ThreadLocal<Long>(); 

	/**
	 * Handles scope information and times gCube calls.
	 * @param mc the message context.
	 */
	public void invoke(MessageContext mc) throws GCUBEFault 
	{

		logger.debug("GCube Handler invoked");
		if (mc.getPastPivot()) 
		{
			logger.trace("Past Pivot");
			this.onResponse(mc);
			return;
		}//are we intercepting a response? then dispatch otherwise it's a request

		startCallThreadLocal.set(System.currentTimeMillis());
		//parse call headers
		SOAPElement header = null;
		Map<String,String> headers = new HashMap<String,String>();
		try 
		{
			logger.trace("Getting headers");
			header = MessageContext.getCurrentContext().getCurrentMessage().getSOAPHeader();
		}
		catch (SOAPException e1) 
		{
			throw new GCUBEUnrecoverableFault("call is malformed: could not process headers");
		}	
		Iterator<?> i = header.getChildElements();
		while (i.hasNext()) 
		{
			SOAPElement child = (SOAPElement) i.next();
			String name = child.getElementName().getLocalName();
			String value = child.getValue();
			logger.trace("Header name = "+name+" value "+value);
			headers.put(name, value);	
		}	

		//retrieving called method in header
		String calledMethod = headers.get(CALLED_METHOD_HEADER_NAME)!=null?headers.get(CALLED_METHOD_HEADER_NAME):"UNKNOWN";
		InnerMethodName.instance.set(calledMethod);

		//if it's not a gCube call, will let it pass.
		// 1) it's not for a gCube service: no need to intrude
		// 2) it's a for a gCube service: proceed at own risk with no propagation of scope, outgoing calls are likely to fail.
		// N.B 2) is the case of provider's calls (e.g. getResourceProperties). in this case, tolerance is necessary and likely to be safe.
		if ((headers.get(CLASS_HEADER_NAME)==null || headers.get(NAME_HEADER_NAME)==null) && !GHNContext.getContext().isSecurityEnabled()){
			logger.debug("Security not enabled: the call without service header passes");
			return;
		}
		else if ((headers.get(CLASS_HEADER_NAME)==null || headers.get(NAME_HEADER_NAME)==null) && GHNContext.getContext().isSecurityEnabled())
		{
			logger.error("Unable to determine the correct service security configuration and to find the correct security controller: the calla will not pass");
			throw new GCUBEUnrecoverableException("Unable to determine the service").toFault();
		}

		//if it's a gCube call, let us obtain the target service
		GCUBEServiceContext context;
		try {context = GHNContext.getContext().getServiceContext(headers.get(CLASS_HEADER_NAME), headers.get(NAME_HEADER_NAME));} //may not exist
		catch (Exception e) {throw new GCUBEUnrecoverableException(e).toFault("Could not dispatch gCube call to service "+headers.get(NAME_HEADER_NAME));}

		if (context.getStatus()==GCUBEServiceContext.Status.FAILED || //may have failed
				context.getStatus()==GCUBEServiceContext.Status.DOWN) throw new GCUBERetryEquivalentFault("Service "+headers.get(NAME_HEADER_NAME)+" is not operational");
		if (context.getStatus()!=GCUBEServiceContext.Status.READIED) throw new GCUBERetrySameFault("Service "+headers.get(NAME_HEADER_NAME)+" is not ready yet"); 

		mc.setProperty(SERVICECONTEXT_PROPERTY, context); //set service calls
		//logger.trace("Service context set");
		try {
			String caller = headers.get(CALLER_HEADER_NAME);
			mc.setProperty(CALLER_PROPERTY, caller);
			String target = context.getServiceClass()+":"+context.getName()+":"+ InnerMethodName.instance.get();

			String token = headers.get(AUTH_TOKEN_HEADER_NAME);

			//scope has priority over token 
			if (headers.get(SCOPE_HEADER_NAME)!=null){
				//parse scope expression
				GCUBEScope scope  = GCUBEScope.getScope(headers.get(SCOPE_HEADER_NAME));		

				//set scope
				logger.trace("Scope = "+scope);
				context.setScope(scope);		

				//authorise call
				if (GHNContext.getContext().isSecurityEnabled())
				{
					logger.trace("peer subject "+mc.getProperty(Constants.PEER_SUBJECT));
					logger.trace("Setting security");
					Map<String, Object> securityParameterMap = new HashMap<String, Object>();
					securityParameterMap.put(GCUBEServiceSecurityController.CONTEXT, context);
					securityParameterMap.put(GCUBEServiceSecurityController.HEADERS, headers);
					securityParameterMap.put(GCUBEServiceSecurityController.MESSAGE_CONTEXT, mc);
					GCUBEServiceAuthenticationController authenticationManager = context.getAuthenticationManager();
					GCUBEServiceAuthorizationController authorizationManager = context.getAuthorizationManager();
					authenticationManager.authenticateCall(securityParameterMap);
					authorizationManager.authoriseCall(securityParameterMap);
					logger.trace("Security set");
				}

				if (token!=null){
					AuthorizationEntry info = retrieveTokenInfo(token);
					checkPolicies(info, context);
					AuthorizationProvider.instance.set(new Caller(info.getClientInfo(), info.getQualifier()));
					SecurityTokenProvider.instance.set(token);
				}

			}else if (!validateToken(token, caller, context))
				throw new Exception("gCube call is unscoped");

			logger.info("START CALL FROM ("+caller+") TO ("+ target +"),"+ScopeProvider.instance.get()+","+Thread.currentThread());
		}
		catch(GCUBEException e) 
		{
			logger.error("Error in GCubeHandler",e);
			{
				this.onResponse(mc);
				throw e.toFault();
			}
		}
		catch(Exception e) 
		{
			logger.error("General exception in GCubeHandler",e);
			this.onResponse(mc);
			throw new GCUBEUnrecoverableException(e).toFault();
		}

	}

	private boolean validateToken(String token, String caller, GCUBEServiceContext context) throws Exception{


		if (token!=null){
			AuthorizationEntry info = retrieveTokenInfo(token);
			if (info==null){
				logger.info("rejecting call to "+caller+", invalid token "+token);
				throw new Exception("invalid token "+token);
			}
			logger.info("CALL ARRIVED WITH A TOKEN FROM "+info.getClientInfo().getId()+" in scope "+info.getContext()+" in thread "+Thread.currentThread());
			checkPolicies(info, context);
			AuthorizationProvider.instance.set(new Caller(info.getClientInfo(), info.getQualifier()));
			//parse scope expression
			GCUBEScope scope  = GCUBEScope.getScope(info.getContext());

			//set scope
			logger.trace("Scope = "+scope);
			context.setScope(scope);	
			logger.info("retrieved request authorization info "+AuthorizationProvider.instance.get()+" in scope "+ScopeProvider.instance.get());
			SecurityTokenProvider.instance.set(token);
			return true;

		} 
		logger.info("token not found");
		return false;
	}

	private AuthorizationEntry retrieveTokenInfo(String token) throws Exception{
		return authorizationService().get(token);	
	}


	/** {@inheritDoc} */
	@Override public void onFault(MessageContext mc) {
		super.onFault(mc);
		this.onResponse(mc,true);
	}

	/**
	 * Intercepts call responses.
	 * @param mc the message context.
	 * @param failure (optional) <code>true</code> if this is a response to failed call, <code>false</code> otherwise (default).
	 */
	private void onResponse(MessageContext mc, boolean ... failure) {
		if (!mc.isPropertyTrue(SERVICECONTEXT_PROPERTY)) return; //nothing to do if response is to legacy call
		GCUBEServiceContext context = (GCUBEServiceContext) mc.getProperty(SERVICECONTEXT_PROPERTY);
		String caller = (String) mc.getProperty(CALLER_PROPERTY);
		String target = context.getServiceClass()+":"+context.getName()+":"+ InnerMethodName.instance.get();

		String currentScope = ScopeProvider.instance.get();

		boolean callIsFailed=false;
		context.getManagementBean().setLastResponseTime(context.getTiming()); //logs time
		context.getManagementBean().addCall(); //logs call
		if (failure!=null && failure.length>0 && failure[0]){
			callIsFailed = true;
			context.getManagementBean().addFailedCall(); //logs failures
		}
		context.resetTimer(); //reset timer


		logger.info("END CALL FROM ("+caller+") TO ("+ target +"),"+(currentScope==null?"INVALID":currentScope+","+Thread.currentThread()+",["+context.getTiming()+"]"));


		if (GHNContext.getContext().isAccountingEnabled()){
			if (AuthorizationProvider.instance.get()!=null){
				Caller authEntry = AuthorizationProvider.instance.get();
				generateAccounting(authEntry.getClient().getId(), authEntry.getTokenQualifier(),  caller, context, callIsFailed);
			}else generateAccounting("UNKNOWN","DEFAULT", caller, context, callIsFailed);
		}


		//zeroes every threadScoped so that later legacy calls in this thread will not find it accidentally set
		ScopeProvider.instance.reset(); 
		startCallThreadLocal.remove();
		SecurityTokenProvider.instance.reset();
		InnerMethodName.instance.reset();
		AuthorizationProvider.instance.reset();
	}

	void generateAccounting(String caller, String callerQualifier, String remoteHost,  GCUBEServiceContext serviceContext, boolean isFailed){
		AccountingPersistenceFactory.setFallbackLocation(GHNContext.getContext().getStorageRoot());
		AccountingPersistence persistence = AccountingPersistenceFactory.getPersistence();
		ServiceUsageRecord serviceUsageRecord = new ServiceUsageRecord();
		try{
			serviceUsageRecord.setConsumerId(caller);
			serviceUsageRecord.setCallerQualifier(callerQualifier);
			serviceUsageRecord.setScope(ScopeProvider.instance.get());
			serviceUsageRecord.setServiceClass(serviceContext.getServiceClass());
			serviceUsageRecord.setServiceName(serviceContext.getName());

			serviceUsageRecord.setHost(GHNContext.getContext().getHostnameAndPort());
			serviceUsageRecord.setCalledMethod(InnerMethodName.instance.get());
			serviceUsageRecord.setCallerHost(remoteHost);
			serviceUsageRecord.setOperationResult(isFailed?OperationResult.FAILED:OperationResult.SUCCESS);
			//TODO: da rivedere
			serviceUsageRecord.setDuration(System.currentTimeMillis()-startCallThreadLocal.get());

			persistence.account(serviceUsageRecord);

		}catch(Exception ex){
			logger.warn("invalid record passed to accounting ",ex);
		}
	}

	private void checkPolicies(AuthorizationEntry info, GCUBEServiceContext context){
		ServiceIdentifier serviceIdentifier = getServiceInfo(context).getServiceIdentifier();

		for (Policy policy: info.getPolicies())
			if (PolicyUtils.isPolicyValidForClient(policy.getServiceAccess(), serviceIdentifier)){
				logger.error("rejecting call to "+context.getName()+" : "+info.getClientInfo().getId()+" is not allowed to contact the service ");
				throw new RuntimeException("rejecting call to "+context.getName()+": "+info.getClientInfo().getId()+" is not allowed to contact the service");
			}	

	}

	public static ServiceInfo getServiceInfo(GCUBEServiceContext context){
		String hostedin = String.format("%s_%d", GHNContext.getContext().getHostname(), GHNContext.getContext().getPort());
		return new ServiceInfo(new ServiceIdentifier(context.getName(), context.getServiceClass(), hostedin));
	}
}

