package org.gcube.smartgears;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.gcube.common.authorization.client.exceptions.ObjectNotFound;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.events.Observes;
import org.gcube.common.events.Observes.Kind;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.smartgears.context.application.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterApplicationManagerObserver {

	private static Logger log = LoggerFactory.getLogger(RegisterApplicationManagerObserver.class);

	private static ExecutorService service = Executors.newCachedThreadPool();


	private Set<Class<? extends ApplicationManager>> managersClass ;

	private Map<String,  List<Future<ApplicationManager>>> instanciatedManagerPerScope = new HashMap<String, List<Future<ApplicationManager>>>();

	public RegisterApplicationManagerObserver(
			Set<Class<? extends ApplicationManager>> managersClass, Collection<String> startingTokens) {
		super();
		this.managersClass = managersClass;
		for (String startingToken : startingTokens )
			this.onRegistation(startingToken);

	}

	@Observes(value=Constants.token_registered, kind=Kind.safe)
	public synchronized void  onRegistation(final String securityToken){
		log.info("token registered called with token {}", securityToken);
		List<Future<ApplicationManager>> futureList = new ArrayList<Future<ApplicationManager>>();

		try {
			final String context = authorizationService().get(securityToken).getContext();


			for (Class<? extends ApplicationManager> appManager: managersClass){
				Future<ApplicationManager> appManagerFuture = service.submit(new InitAppManager(securityToken, context, appManager));
				log.info("intializing app in context {} with token {} ",context, securityToken);

				futureList.add(appManagerFuture);
				if (ApplicationManagerProvider.appManagerMap.containsKey(appManager.getCanonicalName()))
					ApplicationManagerProvider.appManagerMap.get(appManager.getCanonicalName()).put(context, appManagerFuture);
				else {
					Map<String, Future<ApplicationManager>> tokenFutureMap = new HashMap<String, Future<ApplicationManager>>();
					tokenFutureMap.put(context, appManagerFuture);
					ApplicationManagerProvider.appManagerMap.put(appManager.getCanonicalName(), tokenFutureMap);
				}
			}
			if (!futureList.isEmpty())
				instanciatedManagerPerScope.put(context, futureList);
		} catch (ObjectNotFound e1) {
			log.error("it should never happen (token has just been created)",e1);
			throw new RuntimeException("it should never happen (token has just been created",e1);
		} catch (Exception e1) {
			log.error("something failed getting token",e1);
			throw new RuntimeException("something failed getting token",e1);
		}
	}

	@Observes(value=Constants.token_removed, kind=Kind.critical)
	public synchronized void onRemove(final String securityToken){

		try {
			final String context = authorizationService().get(securityToken).getContext();

			for (Future<ApplicationManager> appManager: instanciatedManagerPerScope.get(context)){
				service.execute(new ShutDownAppManager(securityToken, context, appManager));
				ApplicationManagerProvider.appManagerMap.get(appManager).remove(context);
			}

			instanciatedManagerPerScope.remove(context);
			SecurityTokenProvider.instance.reset();
		} catch (ObjectNotFound e1) {
			log.error("it should never happen (token has just been created)",e1);
			throw new RuntimeException("it should never happen (token has just been created",e1);
		} catch (Exception e1) {
			log.error("something failed getting token",e1);
			throw new RuntimeException("something failed getting token",e1);
		}
	}

	public synchronized void  onStop(ApplicationContext appContext){

		for (String token :appContext.configuration().startTokens()){
			try {
				String context = authorizationService().get(token).getContext();
				for (Future<ApplicationManager> appManagerEntry: instanciatedManagerPerScope.get(context)){
					try{
						log.info("stoppping {} in context {} ",appContext.name(), context);

						SecurityTokenProvider.instance.set(token);
						ScopeProvider.instance.set(context);
						try {
							appManagerEntry.get().onShutdown();
							log.info("manager {} correctly suhtdown on context {}",appContext.name(),  context);
						} catch (Exception e){
							log.warn("problem calling onShutdown for context {}", context, e);
						}
					}catch(Exception e){
						log.error("error retrieving token on shutdown on context {}", context,e);
						throw new RuntimeException("error retrieving token on shutdown",e);
					}finally{
						ScopeProvider.instance.reset();
						SecurityTokenProvider.instance.reset();
					}
				}
			} catch (ObjectNotFound e1) {
				log.error("token not found : {}",token,e1);
			} catch (Exception e1) {
				log.error("something failed getting token {}",token,e1);
			}
		}
		unregister();
	}

	public void unregister(){
		service.shutdownNow();
	}

	public class InitAppManager implements Callable<ApplicationManager>{

		private Class<? extends ApplicationManager> managerClass;
		private String securityToken;
		private String context;

		public InitAppManager(String securityToken, String context, Class<? extends ApplicationManager> managerClass){
			this.managerClass = managerClass;
			this.securityToken = securityToken;
			this.context = context;
		}

		@Override
		public ApplicationManager call() throws Exception {
			SecurityTokenProvider.instance.set(securityToken);
			ScopeProvider.instance.set(context);
			ApplicationManager manager = managerClass.newInstance();
			try {
				log.info("calling on onInit of {} on token {}",manager.getClass().getCanonicalName(), securityToken);
				manager.onInit();
			} catch (Exception e) {
				log.warn("error on onInit of {} on token {}",manager.getClass().getCanonicalName(), securityToken, e);
			} finally{
				ScopeProvider.instance.reset();
				SecurityTokenProvider.instance.reset();					
			}
			return manager;
		}
	}

	public class ShutDownAppManager implements Runnable{

		private Future<ApplicationManager> appManager;
		private String securityToken;
		private String context;

		public ShutDownAppManager(String securityToken, String context,  Future<ApplicationManager> appManager){
			this.appManager = appManager;
			this.securityToken = securityToken;
			this.context = context;
		}

		@Override
		public void run() {
			SecurityTokenProvider.instance.set(securityToken);
			ScopeProvider.instance.set(context);
			try {
				log.info("calling on ShutDown of {} on token {}",appManager.getClass().getCanonicalName(), securityToken);
				appManager.get().onShutdown();
			} catch (Exception e) {
				log.warn("error on onShutdown of {} on token {}",appManager.getClass().getCanonicalName(), securityToken, e);
			} finally{
				ScopeProvider.instance.reset();
				SecurityTokenProvider.instance.reset();					
			}
		}
	}
}
