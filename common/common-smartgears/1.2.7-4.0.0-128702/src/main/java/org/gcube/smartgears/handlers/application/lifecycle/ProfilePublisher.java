package org.gcube.smartgears.handlers.application.lifecycle;

import static org.gcube.smartgears.handlers.ProfileEvents.*;
import static org.gcube.smartgears.utils.Utils.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

	/**
	 * Creates an instance for a given application.
	 * @param context the context of the application
	 */
	public ProfilePublisher(ApplicationContext context) {
		this.context = context;
		this.publisher=ProviderFactory.provider().publisherFor(context);
	}

	/**
	 * Adds for the first time the current resource profile of the application in one or more scopes.
	 * @param scopes the scopes
	 */
	public void addTo(Collection<String> scopes) {
		
		notEmpty("scopes",scopes);
		
		GCoreEndpoint profile = context.profile(GCoreEndpoint.class);
		
		//get rid of duplicates in input
		Set<String> newscopes = new HashSet<String>(scopes);
		
		if (!newscopes.isEmpty())
			try {
	
				log.info("publishing {} in scopes {}", context.name(),newscopes);
				
				profile = publisher.create(profile, new ArrayList<String>(newscopes));
				
				sharePublished(profile);
				
			} 
			catch (Exception e) {
	
				rethrowUnchecked(e);
				
			}
	}
	

	public void update() {
		
		GCoreEndpoint profile = context.profile(GCoreEndpoint.class);
		
		log.info("publishing {} in scopes {}", context.name(),profile.scopes().asCollection());
		
		try {

			profile = publisher.update(profile);
			
			sharePublished(profile);
			
		} 
		catch (Exception e) {

			rethrowUnchecked(e);
			
		}
	}
	
	
	/**
	 * Removes the application from one or more scopes.
	 * @param scopes the scopes
	 */
	public void removeFrom(Collection<String> scopes) {
		
		GCoreEndpoint profile = context.profile(GCoreEndpoint.class);
		
		log.info("removing {} from scopes {}", context.name(), scopes);
		
		try {

			List<String> list = new ArrayList<String>(scopes); 

			profile = publisher.remove(profile,list);
			
			update();
			
		} 
		catch (Exception e) {

			rethrowUnchecked(e);
			
		}
	}
	
	
	
	private void sharePublished(GCoreEndpoint profile) {
		context.events().fire(profile,published);
	}

}
