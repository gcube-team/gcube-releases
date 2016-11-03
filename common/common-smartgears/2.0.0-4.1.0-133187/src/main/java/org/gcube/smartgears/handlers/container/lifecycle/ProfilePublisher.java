package org.gcube.smartgears.handlers.container.lifecycle;

import static org.gcube.smartgears.utils.Utils.notEmpty;
import static org.gcube.smartgears.utils.Utils.rethrowUnchecked;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.gcube.common.authorization.client.proxy.AuthorizationProxy;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.resources.gcore.HostingNode;
import org.gcube.informationsystem.publisher.ScopedPublisher;
import org.gcube.smartgears.context.container.ContainerContext;
import org.gcube.smartgears.handlers.ProfileEvents;
import org.gcube.smartgears.provider.ProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Publishes the resource profile of the container.
 * <p>
 * Distinguishes publication in new scopes ({@link #addTo(List)} from publication updates in existing scopes ({@link #update(List)}.
 * 
 * @author Fabio Simeoni
 * 
 */
public class ProfilePublisher {

	private static final Logger log = LoggerFactory.getLogger(ProfilePublisher.class);

	//the underlying IS publisher
	private final ScopedPublisher publisher;
	//private final AuthorizationProvider authorization;
	private final ContainerContext context;

	private AuthorizationProxy authProxy  ;
	
	/**
	 * Creates an instance for the container.
	 * @param context the context of the application
	 */
	public ProfilePublisher(ContainerContext context) {
		this.context = context;
		this.publisher=ProviderFactory.provider().publisherFor(context);
		this.authProxy = ProviderFactory.provider().authorizationProxy();
	}

	/**
	 * Adds the current resource profile of the application in one or more 
	 * scopes. The scopes are retrieved from tokens 
	 * @param tokens the tokens
	 */
	public void addTo(Collection<String> tokens) {

		notEmpty("tokens",tokens);

		log.info("publishing container with tokens {}", tokens);

		HostingNode profile = context.profile(HostingNode.class);
		
		/* TODO: reintroduce it when scope will be removed
		//TODO: remove when move to new IS
		Collection<String> retainedContexts = new ArrayList<String>(context.configuration().allowedContexts());
		retainedContexts.removeAll(profile.scopes().asCollection());
		profile.scopes().asCollection().addAll(retainedContexts);
		
		log.trace("profile scopes on create are {} ",profile.scopes().asCollection());
		
		String previousToken = SecurityTokenProvider.instance.get();
		
		try {
			for (String token: tokens){
				log.info("creating profile with token {}", token);
				SecurityTokenProvider.instance.set(token);
				profile = publisher.create(profile);
				SecurityTokenProvider.instance.reset();
			}				
			
			update();
			
		} catch (Exception e) {
			log.warn("error adding scopes",e);
			rethrowUnchecked(e);

		} finally{
			SecurityTokenProvider.instance.set(previousToken);
		}*/
		
		ClassLoader contextCL = Thread.currentThread().getContextClassLoader();
		
		String previousToken = SecurityTokenProvider.instance.get();
		try{//This classloader set is needed for the jaxb context
			if (previousToken==null)
				SecurityTokenProvider.instance.set((String)tokens.toArray()[0]); 
			Thread.currentThread().setContextClassLoader(ProfilePublisher.class.getClassLoader());
			profile = publisher.create(profile, resolveScopesFromTokens(tokens));
		} catch (Exception e) {
			rethrowUnchecked(e);
		} finally {
			SecurityTokenProvider.instance.set(previousToken);
			Thread.currentThread().setContextClassLoader(contextCL);
		}
		
		sharePublished(profile);
		
	}

	/**
	 * Adds the current resource profile of the application in one or more scopes.
	 */
	public void addToAll() {
		addTo(context.configuration().startTokens());
	}

	/**
	 * Updates the current resource profile of the application in its current scopes.
	 */
	public void update() {

		HostingNode profile = context.profile(HostingNode.class);
		/* TODO: reintroduce it when scope will be removed
		Collection<String> tokens = context.configuration().startTokens();
				
		log.info("updating container with tokens {}", tokens);
 				
		String previousToken = SecurityTokenProvider.instance.get();
		
		try {
			for (String token: tokens){
				SecurityTokenProvider.instance.set(token);
				profile = publisher.update(profile);
				SecurityTokenProvider.instance.reset();
			}							
			sharePublished(profile);

		} 
		catch (Exception e) {
			log.warn("error updating container",e);
			rethrowUnchecked(e);

		} finally{
			SecurityTokenProvider.instance.set(previousToken);
		}*/
		
		log.debug("resource scopes are : {} ",profile.scopes().asCollection());
		
		ClassLoader contextCL = Thread.currentThread().getContextClassLoader();
		
		String previousToken = SecurityTokenProvider.instance.get();
		try{//This classloader set is needed for the jaxb context
			if (previousToken==null)
				SecurityTokenProvider.instance.set((String)context.configuration().startTokens().toArray()[0]); 
			
			Thread.currentThread().setContextClassLoader(ProfilePublisher.class.getClassLoader());
			profile = publisher.update(profile);
		} catch (Exception e) {
			rethrowUnchecked(e);
		} finally {
			SecurityTokenProvider.instance.set(previousToken);
			Thread.currentThread().setContextClassLoader(contextCL);
		}
		
		sharePublished(profile);
	}

	/**
	 * Removes the container from one or more scopes.
	 * @param tokens the tokens
	 */
	public void removeFrom(Collection<String> tokens) {

		HostingNode profile = context.profile(HostingNode.class);
		
		log.info("removing container with tokens {}", tokens);
		
		/* TODO: reintroduce it when scope will be removed
		String previousToken = SecurityTokenProvider.instance.get();
		
		try {
			
			for (String token: tokens){
				SecurityTokenProvider.instance.set(token);
				profile = publisher.remove(profile);
				SecurityTokenProvider.instance.reset();
			}				

			update();

		} 
		catch (Exception e) {
			log.warn("error removing scopes",e);
			rethrowUnchecked(e);

		} finally{
			SecurityTokenProvider.instance.set(previousToken);
		} */
		
		ClassLoader contextCL = Thread.currentThread().getContextClassLoader();
		
		String previousToken = SecurityTokenProvider.instance.get();
		try{//This classloader set is needed for the jaxb context
			if (previousToken==null)
				SecurityTokenProvider.instance.set((String)tokens.toArray()[0]); 
			Thread.currentThread().setContextClassLoader(ProfilePublisher.class.getClassLoader());
			profile = publisher.remove(profile, resolveScopesFromTokens(tokens));
		} catch (Exception e) {
			rethrowUnchecked(e);
		} finally {
			SecurityTokenProvider.instance.set(previousToken);
			Thread.currentThread().setContextClassLoader(contextCL);
		} 
		
		log.debug("after remove container profile contains scopes {}",profile.scopes().asCollection());
		sharePublished(profile);
	}

	private void sharePublished(HostingNode profile) {
		context.events().fire(profile,ProfileEvents.published);
	}

	private List<String> resolveScopesFromTokens(Collection<String> tokens){
		List<String> scopes = new ArrayList<String>(tokens.size());
		for (String token: tokens)
			try{
				scopes.add(this.authProxy.get(token).getContext());
			}catch (Exception e) {
				log.warn("error retrieving token {} , it should never happen",token);
			}
		return scopes;
	}
}
