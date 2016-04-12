package org.gcube.smartgears.handlers.container.lifecycle;

import static org.gcube.smartgears.utils.Utils.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
	
	private final ContainerContext context;

	/**
	 * Creates an instance for the container.
	 * @param context the context of the application
	 */
	public ProfilePublisher(ContainerContext context) {
		this.context = context;
		this.publisher=ProviderFactory.provider().publisherFor(context);
	}

	/**
	 * Adds the current resource profile of the application in one or more scopes.
	 * @param scopes the scopes
	 */
	public void addTo(Collection<String> scopes) {
		
		notEmpty("scopes",scopes);
		
		log.info("publishing container in scopes {}", scopes);
		
		HostingNode profile = context.profile(HostingNode.class);

		try {

			profile = publisher.create(profile, new ArrayList<String>(scopes));
			
			sharePublished(profile);
			
		} 
		catch (Exception e) {

			rethrowUnchecked(e);
			
		}
	}
	

	/**
	 * Updates the current resource profile of the application in its current scopes.
	 */
	public void update() {
		
		HostingNode profile = context.profile(HostingNode.class);
		
		log.info("publishing container in scopes {}", profile.scopes().asCollection());
		
		try {

			profile = publisher.update(profile);
			
			sharePublished(profile);
			
		} 
		catch (Exception e) {

			rethrowUnchecked(e);
			
		}
	}
	
	/**
	 * Removes the container from one or more scopes.
	 * @param scopes the scopes
	 */
	public void removeFrom(Collection<String> scopes) {
		
		HostingNode profile = context.profile(HostingNode.class);
		
		log.info("removing container from scopes {}", scopes);
		
		try {

			List<String> list = new ArrayList<String>(scopes); 

			profile = publisher.remove(profile,list);
			
			update();
			
		} 
		catch (Exception e) {

			rethrowUnchecked(e);
			
		}
	}
	
	private void sharePublished(HostingNode profile) {
		context.events().fire(profile,ProfileEvents.published);
	}

}
