package org.gcube.smartgears.managers;

import static org.gcube.smartgears.Constants.container_profile_file_path;
import static org.gcube.smartgears.lifecycle.container.ContainerState.active;
import static org.gcube.smartgears.lifecycle.container.ContainerState.down;
import static org.gcube.smartgears.lifecycle.container.ContainerState.failed;
import static org.gcube.smartgears.lifecycle.container.ContainerState.stopped;
import static org.gcube.smartgears.provider.ProviderFactory.provider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gcube.common.authorization.client.exceptions.ObjectNotFound;
import org.gcube.common.authorization.client.proxy.AuthorizationProxy;
import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.provider.ClientInfo;
import org.gcube.common.authorization.library.provider.ContainerInfo;
import org.gcube.common.events.Observes;
import org.gcube.common.events.Observes.Kind;
import org.gcube.smartgears.configuration.container.ContainerHandlers;
import org.gcube.smartgears.context.application.ApplicationContext;
import org.gcube.smartgears.context.container.ContainerContext;
import org.gcube.smartgears.handlers.ProfileEvents;
import org.gcube.smartgears.handlers.container.ContainerHandler;
import org.gcube.smartgears.handlers.container.ContainerLifecycleEvent;
import org.gcube.smartgears.handlers.container.ContainerPipeline;
import org.gcube.smartgears.lifecycle.application.ApplicationLifecycle;
import org.gcube.smartgears.lifecycle.container.ContainerState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Coordinates management of the container as a gCube resource.
 * 
 * @author Fabio Simeoni
 * 
 */
public class ContainerManager {

	private static Logger log = LoggerFactory.getLogger(ContainerManager.class);

	public static ContainerManager instance = new ContainerManager();

	private AuthorizationProxy authProvider = provider().authorizationProxy();

	private ContainerContext context;

	private ContainerPipeline pipeline;

	private ContainerManager() {}

	/**
	 * Starts container management.
	 */
	public ContainerContext start(ContainerContext context) {

		this.context = context;

		try {

			// TODO Ask if is not enough that is already done in 
			// Bootstrap.initialiseContainer() function;
			context.configuration().validate();

			validateContainer(context);

			saveContainerState();

			ContainerHandlers handlers = provider().containerHandlers();

			log.trace("managing container lifecycle with {}", handlers.get());

			startHandlers(handlers.get());

			context.lifecycle().moveTo(active);

			//loadKeyForToken(context.configuration().startTokens());

			return context;
		}
		catch(RuntimeException e) {

			log.error("cannot manage container (see cause)",e);

			if (context!=null)
				context.lifecycle().moveTo(failed);

			throw e;
		}

	}


	private void saveContainerState() {
		File file = context.configuration().persistence().file(container_profile_file_path);		
		try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))){
			oos.writeObject(context.id());
			oos.writeObject(context.configuration().startTokens());
		}catch (Exception e) {
			log.error("error serializing cointainer state");
			throw new RuntimeException(e);
		}

	}

	private void validateContainer(ContainerContext context) {
		List<String> tokensToRemove = new ArrayList<String>();
		Set<String> foundContexts= new HashSet<String>();

		for (String token : context.configuration().startTokens()){
			String tokenContext = resolveTokenForAdd(foundContexts, token);
			if (tokenContext!=null){
				log.info("the container will be started in context {}",tokenContext);
				foundContexts.add(tokenContext);
			} else
				tokensToRemove.add(token);
		}

		if (foundContexts.isEmpty()){
			log.error("no valid starting token are specified, moving the container to failed");
			throw new RuntimeException("no valid starting token are specified");
		}

		context.configuration().startTokens().remove(tokensToRemove);
		context.configuration().allowedContexts(foundContexts);
	}

	private String resolveTokenForAdd(Set<String> alreadyAddedContext, String token){
		try {
			AuthorizationEntry entry = authProvider.get(token);
			ClientInfo info = entry.getClientInfo();
			if (alreadyAddedContext.contains(entry.getContext())){
				log.warn("the token {} cannot be used, another token with the same context {} found ", entry.getContext());
			} else if(!entry.getContext().startsWith("/"+context.configuration().infrastructure())){
				log.warn("the token {} cannot be used, is not in the infrastructure {} of the container ", token,context.configuration().infrastructure());				
			}else if (!(info instanceof ContainerInfo)){ 
				log.warn("the token {} cannot be used, is not for a container token ", token);
			} else if (!((ContainerInfo)info).getHost().equals(context.configuration().hostname()) 
					&& context.configuration().port()!=((ContainerInfo)info).getPort()){
				log.warn("the token {} cannot be used, the client id {} resolved with the token is not the same of the one specified in this container  ", token, info.getId());
			} else
				return entry.getContext();
		}catch(ObjectNotFound onf){
			log.error("token {} not valid", token);
		} catch (Exception e) {
			log.error("error contacting authorization for token {}",token,e);
		}
		return null;
	}

	public void manage(ApplicationContext app) {

		app.events().subscribe(this); 

	}

	@Observes(value={ApplicationLifecycle.failure,ApplicationLifecycle.stop},kind=Kind.critical)
	void monitorApplication(ApplicationLifecycle lifecycle) {
		context.lifecycle().tryMoveTo(ContainerState.partActive);
	}

	@Observes(value=ContextEvents.ADD_TOKEN_TO_CONTAINER,kind=Kind.critical)
	void addToken(String token) {
		log.trace("adding token {} to container", token);
		String newContext;
		if ((newContext = resolveTokenForAdd(context.configuration().allowedContexts(), token))!=null) {
			context.configuration().startTokens().add(token);
			context.configuration().allowedContexts().add(newContext);
			saveContainerState();
			//loadKeyForToken(Arrays.asList(token));
			context.events().fire(token, ContextEvents.ADD_TOKEN_TO_APPLICATION);
			context.events().fire(token, ProfileEvents.addToContext);
			log.trace("token added and event fired");
		} else log.warn("trying to add an invalid token");
	}

	@Observes(value=ContextEvents.REMOVE_TOKEN_FROM_CONTAINER,kind=Kind.critical)
	void removeToken(String token) {
		log.trace("removing token {} from container", token);
		AuthorizationEntry entry;
		try {
			entry = authProvider.get(token);
		} catch (Exception e) {
			log.error("error resolving token to remove");
			return;
		}

		if (context.configuration().startTokens().contains(token)) {
			context.configuration().startTokens().remove(token);
			context.configuration().allowedContexts().remove(entry.getContext());
			saveContainerState();
			context.events().fire(token, ContextEvents.REMOVE_TOKEN_FROM_APPLICATION);
			context.events().fire(token, ProfileEvents.removeFromContext);
			log.trace("token removed and event fired");
		} else log.warn("cannot remove token, it is not present in the container");
	}

	/**
	 * Stops container management on remote request.
	 * 
	 */
	public void stop() {

		stop(false);
	}


	/**
	 * Stops container management on remote request or container shutdown.
	 * 
	 */
	public void stop(boolean shutdown) {

		//two cases: stop-on-shutdown and stop-on-request, some listeners will be selective about this,

		//shutdown is triggered by probe app, which is notified among other apps
		//if other app have been already notified, the container may already be part-active.
		//apps still to notify will listen only on stop, hence won't react to this but will go down when their turn arrives.

		if (context == null)
			return;

		log.info("stopping container management");

		try {

			context.lifecycle().tryMoveTo(shutdown?down:stopped);
			
			stopHandlers();

			//no further reactions
			log.info("stopping container events");
			context.events().stop();

		} 
		catch (RuntimeException e) {

			log.warn("cannot stop container management (see cause)", e);
		}

	}



	//helpers

	private void startHandlers(List<ContainerHandler> handlers) {

		try {

			pipeline = new ContainerPipeline(handlers);

			pipeline.forward(new ContainerLifecycleEvent.Start(context));

		} catch (RuntimeException e) {

			context.lifecycle().tryMoveTo(failed);
			throw e;
		}
	}


	private void stopHandlers() {

		if (pipeline == null)
			return;

		// copy pipeline, flip it, and
		ContainerPipeline returnPipeline = pipeline.reverse();

		// start lifetime pipeline in inverse order with stop event
		returnPipeline.forward(new ContainerLifecycleEvent.Stop(context));

	}
/*
	private void loadKeyForToken(List<String> tokens) {
		String initialToken = SecurityTokenProvider.instance.get();

		//TODO: change this
		String filePath = "/tmp";
		try{
			for (String token : tokens)	{
				try{
					SecurityTokenProvider.instance.set(token);
					authProvider.getSymmKey(filePath);				
				}catch(Exception e){
					log.warn("error loading key for token {}", token);
				}
			}
		}finally{
			SecurityTokenProvider.instance.set(initialToken);
		}
	}
*/

}
