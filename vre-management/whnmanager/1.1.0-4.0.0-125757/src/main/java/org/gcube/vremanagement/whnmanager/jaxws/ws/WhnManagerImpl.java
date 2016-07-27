package org.gcube.vremanagement.whnmanager.jaxws.ws;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;

import javax.jws.WebService;


import org.gcube.smartgears.ContextProvider;
import org.gcube.smartgears.configuration.application.ApplicationConfiguration;
import org.gcube.smartgears.context.application.ApplicationContext;
import org.gcube.smartgears.context.container.ContainerContext;
import org.gcube.smartgears.extensions.resource.ScopesResource.Scope;
import org.gcube.smartgears.handlers.container.lifecycle.ProfilePublisher;
import org.gcube.vremanagement.whnmanager.utils.ValidationUtils;
//import org.gcube.common.calls.jaxws.Constants;
import org.gcube.common.resources.gcore.HostingNode;
import org.gcube.common.resources.gcore.Resources;
import org.gcube.common.resources.gcore.ScopeGroup;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;
import org.gcube.resourcemanagement.whnmanager.api.exception.GCUBEUnrecoverableException;
import org.gcube.resourcemanagement.whnmanager.api.WhnManager;
import org.gcube.resourcemanagement.whnmanager.api.types.AddScopeInputParams;
import org.gcube.resourcemanagement.whnmanager.api.types.ScopeRIParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebService(portName = "WhnManagerIOPort", 
serviceName = WhnManager.SERVICE_NAME, 
targetNamespace = WhnManager.TNS, 
endpointInterface = "org.gcube.resourcemanagement.whnmanager.api.WhnManager" )
//@Singleton
public class WhnManagerImpl implements WhnManager{

	
	private static Logger logger=LoggerFactory.getLogger(WhnManagerImpl.class);
	 public static final String SCOPE_HEADER_ENTRY = "gcube-scope";
	
	/**
	 * Add a scope to the ghn profile and publish it on IS
	 */
	@Override
	public boolean addScope(AddScopeInputParams params) throws GCUBEUnrecoverableException{
		logger.trace("WHNManager: addScope method invokation");
		String scope=params.getScope();
		ValidationUtils.valid("scope", scope);
		ApplicationContext context = ContextProvider.get();
		if(context!=null){
			HostingNode ghn=context.container().profile(HostingNode.class);
			if(new ScopeBean(scope).is(Type.VRE)){
				logger.debug("addScope operation on VRE scope. Check if present VO scope");
				scope=new ScopeBean(scope).enclosingScope().toString();
				logger.debug("VO scope: "+scope);
			}
			if(!ValidationUtils.isPresent(ghn, scope)){
				ValidationUtils.addEnclosingScopesOnResource(ghn, scope);
				logger.debug("addScope method: add scope "+scope+" to resource with id: "+ghn.id());
				ghn.scopes().asCollection().add(scope);
				ScopeGroup<String> scopes=ghn.scopes();
				logger.debug(" resource will be  published in scopes: ");
				for(Iterator it=scopes.iterator(); it.hasNext();){
					String scopeFound=(String)it.next();
					logger.debug(" - "+scopeFound);
				}
				ContainerContext container=context.container()/*.configuration().apps()*/;
				ProfilePublisher publisher= new ProfilePublisher(container);
				publisher.update();
			}else{
				logger.warn("the scope "+scope+" is already present on ghn profile with id: "+ ghn.id());
			}
		}else{
			logger.warn("addScope method: context is null");
		}
		return true;
	}

	/**
	 * Remove a scope from ghn profile and publish the new profile on IS
	 */
	@Override
	public boolean removeScope(String scope)  throws GCUBEUnrecoverableException {
		logger.trace("WHN-Manager: removeScope method invokation");
		ValidationUtils.valid("scope", scope);
		if(new ScopeBean(scope).is(Type.VRE)){
			logger.debug("this is a VRE scope. The request will be ignored ");
			return true;
		}
		ApplicationContext context = ContextProvider.get();
		if(context!=null){
			HostingNode ghn=context.container().profile(HostingNode.class);
			if(ValidationUtils.isPresent(ghn, scope)){
				logger.debug("removeScope method: remove scope "+scope+" to resource with id: "+ghn.id());
				ContainerContext container=context.container();
				ProfilePublisher publisher= new ProfilePublisher(container);
				publisher.removeFrom(Arrays.asList(scope));
			}else{
				logger.warn("scope is not present in the resource");
			}
		}else{
			logger.warn("addScope method: context is null");
		}
		return true;
	}
	
	@Override
	public boolean addRIToScope(ScopeRIParams params)  throws GCUBEUnrecoverableException{
		logger.debug("addRIToScope method: Adding scope "	+ params.getScope() + " to RI <" + params.getClazz() +","+ params.getName() +">");
		try {
			HttpURLConnection connection =getConnectionToScopeManager(params.name);
			logger.debug("adding scope: "+params.scope);
			Scope newScope = new Scope(params.scope);
			try (OutputStream output = connection.getOutputStream()) {
				Resources.marshal(newScope, output);
			}
			logger.info("addScope operation ended with response code: "+connection.getResponseCode());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
//	
//	@Override
//	public boolean activateRI(RIData ri)  throws GCUBEUnrecoverableException{
//		logger.debug("dummy activereRI method with param "+ri);
//		return true;
//	}
//	
//
//	@Override
//	public boolean deactivateRI(RIData ri)  throws GCUBEUnrecoverableException{
//		logger.debug("dummy deactivateRI method with param "+ri);
//		return true;
//	}
//
	@Override
	public boolean removeRIFromScope(ScopeRIParams params)  throws GCUBEUnrecoverableException{
		logger.debug("removeRIFromScope method with param "+params);
		try {
			HttpURLConnection connection =getConnectionToScopeManager(params.name);
			logger.debug("adding scope: "+params.scope);
			Scope newScope = new Scope(params.scope);
			newScope.delete=true;
			try (OutputStream output = connection.getOutputStream()) {
				Resources.marshal(newScope, output);
			}
			logger.info("addScope operation ended with response code: "+connection.getResponseCode());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	private void addScope(String appName, String currentScope, String scopeToAdd ) throws Exception{
		ApplicationContext context = ContextProvider.get();	
		String hostname = context.container().configuration().hostname();
		int port = context.container().configuration().port();
		ApplicationConfiguration app=getAppConfiguration(appName, context);
		if(app!=null){
			if(logger.isDebugEnabled())
				logger.debug("http call to: http://"+hostname+":"+port+"/"+app.context()+"/gcube/resource/scopes");
//			URL url = new URL(String.format("http://%s:%d/%s/gcube/resource/scopes", "dlib29.isti.cnr.it", 8080, "authorization-service" ));
			URL url = new URL(String.format("http://%s:%d/%s/gcube/resource/scopes", hostname, port, app.context()));
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestProperty("Content-Type", "application/xml");
			connection.setRequestProperty(SCOPE_HEADER_ENTRY, currentScope);
			Scope newScope = new Scope(scopeToAdd);
			try (OutputStream output = connection.getOutputStream()) {
				Resources.marshal(newScope, output);
			}
			logger.info("addScope operation ended with response code: "+connection.getResponseCode());

		}else{
			throw new RuntimeException("applicationConfiguration not found ");
		}
	}
	
	private HttpURLConnection getConnectionToScopeManager(String appName) throws Exception{
		ApplicationContext context = ContextProvider.get();	
		String hostname = context.container().configuration().hostname();
		int port = context.container().configuration().port();
		String currentScope=ScopeProvider.instance.get();
		ApplicationConfiguration app=getAppConfiguration(appName, context);
		if(app!=null){
			if(logger.isDebugEnabled())
				logger.debug("http call to: http://"+hostname+":"+port+"/"+app.context()+"/gcube/resource/scopes");
//			URL url = new URL(String.format("http://%s:%d/%s/gcube/resource/scopes", "dlib29.isti.cnr.it", 8080, "authorization-service" ));
			URL url = new URL(String.format("http://%s:%d/%s/gcube/resource/scopes", hostname, port, app.context()));
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestProperty("Content-Type", "application/xml");
			connection.setRequestProperty(SCOPE_HEADER_ENTRY, currentScope);
			return connection;
		}else{
			throw new RuntimeException("applicationConfiguration not found ");
		}
	}
	
	private ApplicationConfiguration getAppConfiguration(String appName, ApplicationContext context){
		logger.debug("get application configuration");
		for (ApplicationConfiguration app : context.container().configuration().apps()){
			logger.debug("check app "+ app.name());
			if (app.name().equals(appName)){
				logger.debug("application configuration is "+app.name());
				return app;
			}
		}
		return null;
					
	}
	
}
