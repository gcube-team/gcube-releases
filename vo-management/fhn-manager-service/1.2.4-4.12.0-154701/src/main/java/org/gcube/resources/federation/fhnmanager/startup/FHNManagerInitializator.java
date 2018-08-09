package org.gcube.resources.federation.fhnmanager.startup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.common.authorization.client.Constants;
import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.ScopeGroup;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.federation.fhnmanager.impl.ISSynchronizer;
import org.gcube.smartgears.context.application.ApplicationContext;
import org.gcube.smartgears.handlers.application.ApplicationLifecycleEvent.Start;
import org.gcube.smartgears.handlers.application.ApplicationLifecycleEvent.Stop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gcube.smartgears.handlers.application.ApplicationLifecycleHandler;
import org.gcube.vomanagement.occi.TemplatesCache;

@XmlRootElement(name = "plugin-registration-handler")
public class FHNManagerInitializator extends ApplicationLifecycleHandler {
	
	private static Logger logger = LoggerFactory.getLogger(FHNManagerInitializator.class);
	
	private Thread thread = null;
	private ISSynchronizer isSyncrhonizer = null;

	@Override
	public void onStart(Start e) {
		System.out.println("ON START CALLED ###################");

		List<String> tokens  = getScopes(e.context());
		logger.debug("Token are:" +tokens);
		List <String> scope = new LinkedList<String>();
		
		System.out.println("Scopes:");
		for(String t: tokens){
			scope.add(this.getCurrentScope(t));
			System.out.println("\t" + scope);
		}
		
		isSyncrhonizer = new ISSynchronizer(scope, 30000);
		thread = new Thread(isSyncrhonizer);
		logger.debug("Starting thread: " + thread);
		thread.start();
		logger.debug("ISSynchronizer successfully started.");
		
	}

	@Override
	public void onStop(Stop e) {

		
		System.out.println("ON STOP CALLED ###################");

		
		logger.debug("Stopping thread: " + thread);
		if (thread != null) {
			isSyncrhonizer.terminate();
			try {
				thread.join();
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
			logger.debug("ISSynchronizer successfully stopped.");
		}
	}
	
	
	public String getCurrentScope(String token){
		AuthorizationEntry authorizationEntry;
		try {
			authorizationEntry = Constants.authorizationService().get(token);
		} catch (Exception e) {
			return ScopeProvider.instance.get();
		}
		return authorizationEntry.getContext();
	}
	
	
	
	public List<String> getScopes(ApplicationContext applicationContext){
		Collection<String> scopes; 
	
		
		ScopeGroup<String> scopeGroup = applicationContext.profile(GCoreEndpoint.class).scopes();
		if(scopeGroup==null || scopeGroup.isEmpty()){
			//Set<String> applicationScopes = applicationContext.configuration().startScopes();
			Set<String> applicationScopes = applicationContext.configuration().startTokens();

			//Set<String> containerScopes = applicationContext.container().configuration().startScopes();
			List<String> containerScopes = applicationContext.container().configuration().startTokens();

			if(applicationScopes==null || applicationScopes.isEmpty()){
				scopes = containerScopes;
				logger.debug("Application Scopes ({}). The Container Scopes ({}) will be used.", applicationScopes, scopes);
			} else{
				logger.debug("Container Scopes ({}). Application Scopes ({}) will be used.", containerScopes, applicationScopes);
				scopes = new HashSet<String>(applicationScopes);
			}
		}else {
			scopes = scopeGroup.asCollection();
		}
		
		return new ArrayList<String>(scopes);
	}	
	
}







/*package org.gcube.resources.federation.fhnmanager.startup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.common.authorization.client.Constants;
import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.ScopeGroup;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.federation.fhnmanager.impl.ISSynchronizer;
import org.gcube.smartgears.context.application.ApplicationContext;
import org.gcube.smartgears.handlers.application.ApplicationLifecycleEvent.Start;
import org.gcube.smartgears.handlers.application.ApplicationLifecycleEvent.Stop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gcube.smartgears.handlers.application.ApplicationLifecycleHandler;

@XmlRootElement(name = "plugin-registration-handler")
public class FHNManagerInitializator extends ApplicationLifecycleHandler {
	
	private static Logger logger = LoggerFactory.getLogger(FHNManagerInitializator.class);
	
	private Thread thread = null;
	private ISSynchronizer isSyncrhonizer = null;

	@Override
	public void onStart(Start e) {
		System.out.println("ON START CALLED ###################");

		List<String> scopes  = getScopes(e.context());
		
		System.out.println("Scopes:");
		for(String scope: scopes){
			System.out.println("\t" + scope);
		}
		
		
		isSyncrhonizer = new ISSynchronizer(scopes, 30000);
		thread = new Thread(isSyncrhonizer);
		logger.debug("Starting thread: " + thread);
		thread.start();
		logger.debug("ISSynchronizer successfully started.");
		
	}

	@Override
	public void onStop(Stop e) {
		System.out.println("ON STOP CALLED ###################");

		
		logger.debug("Stopping thread: " + thread);
		if (thread != null) {
			isSyncrhonizer.terminate();
			try {
				thread.join();
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
			logger.debug("ISSynchronizer successfully stopped.");
		}
	}
	
//	
//	public static String getCurrentScope(){
//		String token = SecurityTokenProvider.instance.get();
//		AuthorizationEntry authorizationEntry;
//		try {
//			authorizationEntry = Constants.authorizationService().get(token);
//		} catch (Exception e) {
//			return ScopeProvider.instance.get();
//		}
//		return authorizationEntry.getContext();
//	}
//	
	
	
	public List<String> getScopes(ApplicationContext applicationContext){
		Collection<String> scopes; 
	
		
		ScopeGroup<String> scopeGroup = applicationContext.profile(GCoreEndpoint.class).scopes();
		if(scopeGroup==null || scopeGroup.isEmpty()){
			Set<String> applicationScopes = applicationContext.configuration().startScopes();
			//Set<String> applicationScopes = applicationContext.configuration().startTokens();

			Set<String> containerScopes = applicationContext.container().configuration().startScopes();
			//List<String> containerScopes = applicationContext.container().configuration().startTokens();

			if(applicationScopes==null || applicationScopes.isEmpty()){
				scopes = containerScopes;
				logger.debug("Application Scopes ({}). The Container Scopes ({}) will be used.", applicationScopes, scopes);
			} else{
				logger.debug("Container Scopes ({}). Application Scopes ({}) will be used.", containerScopes, applicationScopes);
				scopes = new HashSet<String>(applicationScopes);
			}
		}else {
			scopes = scopeGroup.asCollection();
		}
		
		return new ArrayList<String>(scopes);
	}	
	
}


*/


