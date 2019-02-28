package org.gcube.smartgears.handlers.application.lifecycle;

import static org.gcube.smartgears.handlers.ProfileEvents.published;
import static org.gcube.smartgears.utils.Utils.notEmpty;
import static org.gcube.smartgears.utils.Utils.rethrowUnchecked;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.gcube.common.authorization.client.proxy.AuthorizationProxy;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.informationsystem.publisher.ScopedPublisher;
import org.gcube.smartgears.context.application.ApplicationContext;
import org.gcube.smartgears.provider.ProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Publishes the current resource profile of the application.
 * <p>
 *Distinguishes publication in new scopes ({@link #addTo(List)} from publication updates in existing scopes ({@link #update()}.
 * 
 * 
 * @author Fabio Simeoni
 * 
 */
public class ProfilePublisher {

	private static final Logger log = LoggerFactory.getLogger(ProfilePublisher.class);

	//the underlying IS publisher
	private final ScopedPublisher publisher;

	private final ApplicationContext context;

	private AuthorizationProxy authProxy  ;
	
	/**
	 * Creates an instance for a given application.
	 * @param context the context of the application
	 */
	public ProfilePublisher(ApplicationContext context) {
		this.context = context;
		this.publisher=ProviderFactory.provider().publisherFor(context);
		this.authProxy = ProviderFactory.provider().authorizationProxy();
	}

	/**
	 * Adds for the first time the current resource profile of the application in one or more scopes.
	 * @param scopes the scopes
	 */
	public void addTo(Collection<String> tokens) {

		notEmpty("tokens",tokens);

		GCoreEndpoint profile = context.profile(GCoreEndpoint.class);

		/* TODO: reintroduce it when scope will be removed
		//TODO: remove when move to new IS
		Collection<String> retainedContexts = new ArrayList<String>(context.container().configuration().allowedContexts());
		retainedContexts.removeAll(profile.scopes().asCollection());
		profile.scopes().asCollection().addAll(retainedContexts);
		
		
		String previousToken = SecurityTokenProvider.instance.get();
		try {
			for (String token: tokens){
				log.info("creating profile with token {}", token);
				SecurityTokenProvider.instance.set(token);
				profile = publisher.create(profile);
				SecurityTokenProvider.instance.reset();
			}
		}catch (Exception e) {
			rethrowUnchecked(e);
		} finally{
			SecurityTokenProvider.instance.set(previousToken);
		}		
		*/	
				
		ClassLoader contextCL = Thread.currentThread().getContextClassLoader();
		
		String previousToken = SecurityTokenProvider.instance.get();
		try{//This classloader set is needed for the jaxb context
			if (previousToken==null)
				SecurityTokenProvider.instance.set((String)tokens.toArray()[0]); 
			Thread.currentThread().setContextClassLoader(ProfilePublisher.class.getClassLoader());
			profile = publisher.create(profile, resolveScopesFromTokens(tokens));
					
		} catch (Exception e) {
			rethrowUnchecked(e);
		}  finally{
			SecurityTokenProvider.instance.set(previousToken);
			Thread.currentThread().setContextClassLoader(contextCL);
		}
		
		sharePublished(profile);
		log.debug("shared profile with scopes {}", profile.scopes().asCollection());
	} 

	public void addToAll() {
		this.addTo(context.configuration().startTokens());
	}


	public void update() {


		GCoreEndpoint profile = context.profile(GCoreEndpoint.class);

		/* TODO: reintroduce it when scope will be removed
		String previousToken = SecurityTokenProvider.instance.get();
		try {
			
			for (String token: context.configuration().startTokens()){
				SecurityTokenProvider.instance.set(token);
				profile = publisher.update(profile);
				SecurityTokenProvider.instance.reset();
			}
	
		} 
		catch (Exception e) {
			rethrowUnchecked(e);
		} finally{
			SecurityTokenProvider.instance.set(previousToken);
		}
		*/
		
		ClassLoader contextCL = Thread.currentThread().getContextClassLoader();
		
		String previousToken = SecurityTokenProvider.instance.get();
		try{//This classloader set is needed for the jaxb context
			if (previousToken==null)
				SecurityTokenProvider.instance.set((String)context.configuration().startTokens().toArray()[0]); 

			Thread.currentThread().setContextClassLoader(ProfilePublisher.class.getClassLoader());
			profile = publisher.update(profile);
					
		} catch (Exception e) {
			rethrowUnchecked(e);
		}  finally{
			SecurityTokenProvider.instance.set(previousToken);
			Thread.currentThread().setContextClassLoader(contextCL);
		}
		
		sharePublished(profile);
	}


	/**
	 * Removes the application from one or more scopes.
	 * @param scopes the scopes
	 */
	public void removeFrom(Collection<String> tokens) {

		GCoreEndpoint profile = context.profile(GCoreEndpoint.class);
		
		/* TODO: reintroduce it when scope will be removed
		String previousToken = SecurityTokenProvider.instance.get();
		try {
		
			for (String token: tokens){
				SecurityTokenProvider.instance.set(token);
				profile = publisher.remove(profile);
				SecurityTokenProvider.instance.reset();
			}				
						
		} 
		catch (Exception e) {

			rethrowUnchecked(e);

		} finally{
			SecurityTokenProvider.instance.set(previousToken);
		}
		*/
		
		ClassLoader contextCL = Thread.currentThread().getContextClassLoader();
		
		String previousToken = SecurityTokenProvider.instance.get();
		try{//This classloader set is needed for the jaxb context
			if (previousToken==null)
				SecurityTokenProvider.instance.set((String)tokens.toArray()[0]); 
			Thread.currentThread().setContextClassLoader(ProfilePublisher.class.getClassLoader());
			profile = publisher.remove(profile, resolveScopesFromTokens(tokens));
					
		} catch (Exception e) {
			rethrowUnchecked(e);
		}  finally{
			SecurityTokenProvider.instance.set(previousToken);
			Thread.currentThread().setContextClassLoader(contextCL);
		}
		log.debug("after remove application profile contains scopes {}",profile.scopes().asCollection());
		sharePublished(profile);
	}



	private void sharePublished(GCoreEndpoint profile) {
		context.events().fire(profile,published);
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
