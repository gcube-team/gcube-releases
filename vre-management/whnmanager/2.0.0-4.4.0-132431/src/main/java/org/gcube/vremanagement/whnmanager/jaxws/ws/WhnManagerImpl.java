package org.gcube.vremanagement.whnmanager.jaxws.ws;

import static org.gcube.smartgears.provider.ProviderFactory.provider;

import javax.jws.WebService;

import org.gcube.common.authorization.client.proxy.AuthorizationProxy;
import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.authorization.library.provider.ContainerInfo;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.resourcemanagement.whnmanager.api.WhnManager;
import org.gcube.resourcemanagement.whnmanager.api.exception.GCUBEUnrecoverableException;
import org.gcube.resourcemanagement.whnmanager.api.exception.GCUBEUnrecoverableExceptionInfo;
import org.gcube.smartgears.ContextProvider;
import org.gcube.smartgears.context.application.ApplicationContext;
import org.gcube.smartgears.managers.ContextEvents;
import org.gcube.vremanagement.whnmanager.utils.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.gcube.common.calls.jaxws.Constants;

@WebService(portName = "WhnManagerIOPort", 
serviceName = WhnManager.SERVICE_NAME, 
targetNamespace = WhnManager.TNS, 
endpointInterface = "org.gcube.resourcemanagement.whnmanager.api.WhnManager" )
//@Singleton
public class WhnManagerImpl implements WhnManager{


	private static Logger logger=LoggerFactory.getLogger(WhnManagerImpl.class);
	//public static final String TOKEN_HEADER_ENTRY = "gcube-token";

	/**
	 * Add a scope to the ghn profile and publish it on IS
	 */
	@Override
	public boolean addToContext(String context) throws GCUBEUnrecoverableException{
		logger.trace("WHNManager: addToContext method invokation with parameters context :{} and caller: {} curentContext: {}",context, AuthorizationProvider.instance.get(), ScopeProvider.instance.get() );
		ValidationUtils.valid("context", context);
		ApplicationContext appContext = ContextProvider.get();
		if(context!=null){
			if(!appContext.container().configuration().allowedContexts().contains(context)){
				if (new ScopeBean(context).enclosingScope().toString().equals(ScopeProvider.instance.get())){
					AuthorizationProxy proxy = provider().authorizationProxy();
					try {
						String token = proxy.requestActivation(new ContainerInfo(appContext.container().configuration().hostname(), appContext.container().configuration().port()), context);
						logger.trace("generated token is {}",token);
						appContext.events().fire(token, ContextEvents.ADD_TOKEN_TO_CONTAINER);
					} catch (Exception e) {
						logger.error("error contacting authorization service",e);
						throw new GCUBEUnrecoverableException(new GCUBEUnrecoverableExceptionInfo("error contacting authorization service"));
					}
				} else {
					logger.error("the selected context {} is not enclosed in the context passed via token : authorization denied ", context);
					return false;
				}
			}else{
				logger.warn("the context {} is already present ", context);
				return false;
			}
		}else{
			logger.error("context is null");
			return false;
		}
		return true;
	}

	/**
	 * Remove a scope from ghn profile and publish the new profile on IS
	 */
	@Override
	public boolean removeFromContext(String context)  throws GCUBEUnrecoverableException {
		logger.trace("WHNManager: removeFromContext method invokation with parameters context :{} and caller: {} curentContext: {}",context, AuthorizationProvider.instance.get(), ScopeProvider.instance.get() );
		ValidationUtils.valid("context", context);
		ApplicationContext appContext = ContextProvider.get();
		if(context!=null){
			logger.trace("allowed container in context are {} ",appContext.container().configuration().allowedContexts());
			if(appContext.container().configuration().allowedContexts().contains(context)){
				if (new ScopeBean(context).enclosingScope().toString().equals(ScopeProvider.instance.get())){
					AuthorizationProxy proxy = provider().authorizationProxy();
					try {
						String token = proxy.requestActivation(new ContainerInfo(appContext.container().configuration().hostname(), appContext.container().configuration().port()), context);
						logger.trace("token to remove is {}",token);
						appContext.events().fire(token, ContextEvents.REMOVE_TOKEN_FROM_CONTAINER);
					} catch (Exception e) {
						logger.error("error contacting authorization service",e);
						throw new GCUBEUnrecoverableException(new GCUBEUnrecoverableExceptionInfo("error contacting authorization service"));
					}
				} else {
					logger.error("the selected context {} is not enclosed in the context passed via token : authorization denied ", context);
					return false;
				}
			}else{
				logger.warn("the context {} is not present ", context);
				return false;
			}
		}else{
			logger.error("context is null");
			return false;
		}
		return true;
	}


}
