package org.gcube.smartgears.handlers.application.lifecycle;

import static org.gcube.smartgears.Constants.profile_management;
import static org.gcube.smartgears.Constants.profile_property;
import static org.gcube.smartgears.handlers.ProfileEvents.changed;
import static org.gcube.smartgears.handlers.ProfileEvents.published;
import static org.gcube.smartgears.handlers.ProfileEvents.addToContext;
import static org.gcube.smartgears.handlers.ProfileEvents.removeFromContext;
import static org.gcube.smartgears.lifecycle.application.ApplicationLifecycle.activation;
import static org.gcube.smartgears.lifecycle.application.ApplicationLifecycle.failure;
import static org.gcube.smartgears.lifecycle.application.ApplicationLifecycle.stop;
import static org.gcube.smartgears.lifecycle.application.ApplicationState.failed;

import java.util.Collections;

import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.common.events.Observes;
import org.gcube.common.events.Observes.Kind;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.smartgears.context.Property;
import org.gcube.smartgears.context.application.ApplicationContext;
import org.gcube.smartgears.handlers.application.ApplicationLifecycleEvent;
import org.gcube.smartgears.handlers.application.ApplicationLifecycleHandler;
import org.gcube.smartgears.lifecycle.application.ApplicationLifecycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Manages the resource profile of the application.
 * <p>
 * 
 * The manager:
 * 
 * <ul>
 * <li>creates the profile when the application starts for the first time;
 * <li>loads the profile when the application restarts;
 * <li>publishes the profile when the application becomes active, and at any
 * lifecycle change thereafter;
 * <li>stores the profile locally after each publication;
 * </ul>
 * 
 * @author Fabio Simeoni
 * @see ProfileBuilder
 * @see ProfilePublisher
 */
@XmlRootElement(name = profile_management)
public class ProfileManager extends ApplicationLifecycleHandler {

	Logger log = LoggerFactory.getLogger(ProfileManager.class);

	private ApplicationContext context;
	private ProfileBuilder builder;
	private ProfilePublisher publisher;


	@Override
	public void onStart(ApplicationLifecycleEvent.Start e) {

		context = e.context();
		builder = new ProfileBuilder(context);

		activated();

		// note we don't fire profile events, but wait for the final startup
		// outcome which
		// will result in a state change. only then we publish and store the
		// profile
		// this avoids the redundancy and performance penalty of storing and
		// publishing multiple
		// times in rapid succession (which would be correct). Revise if proves
		// problematic in corner
		// cases.

	}


	private void activated(){
		GCoreEndpoint profile = loadOrCreateProfile();

		share(profile);

		publisher = new ProfilePublisher(context);

		registerObservers();
	}

	// helpers
	private void registerObservers() {

		context.events().subscribe(new Object() {

			@Observes({ activation, stop, failure })
			void onChanged(ApplicationLifecycle lc) {

				GCoreEndpoint profile = context.profile(GCoreEndpoint.class);

				profile.profile().deploymentData().status(lc.state().remoteForm());

				log.debug("moving app {} to {}",context.name(), lc.state().remoteForm());

				// since we do not know the observers, they will deal with
				// failures and their consequences
				// any that comes back will be logged in this event thread
				context.events().fire(profile, changed);
			}

			@Observes(value = published)
			void shareAfterPublish(GCoreEndpoint profile) {

				share(profile); // publish may produce a new profile instance

			}

			@Observes(value = changed, kind = Kind.safe)
			void publishAfterChange(GCoreEndpoint profile) {

				boolean firstPublication = profile.scopes().isEmpty();

				//if we've failed before first publication do not try to publish
				//(we may well have failed there)
				try {

					if (firstPublication) {
						if (context.lifecycle().state()!= failed)
							publishFirstTime(profile);
					}
					else{
						log.debug("publishing app {} profile",context.name());
						publisher.update(); // if successful, triggers share.
					}
				}
				catch (Exception e) {

					log.error("cannot publish "+context.name()+" (see details)", e);

					// since we've failed no published event is fired and profile
					// will not be stored.
					// we do it manually to ensure we leave some local trace of the
					// changed profile.
					//TODO: CHECK --- store(profile);
				}



			}

			@Observes(value = addToContext)
			void addTo(String token) {
				try {
					log.trace("publishing application with new token");
					publisher.addTo(Collections.singleton(token));
					publisher.update();
				}catch (Exception e) {

					log.error("cannot add token {} (see details)",token, e);

					// since we've failed no published event is fired and profile
					// will not be stored.
					// we do it manually to ensure we leave some local trace of the
					// changed profile.
					//TODO: CHECK --- store(profile);
				}

			}
			
			@Observes(value = removeFromContext)
			void removeFrom(String token) {
				try {
					log.trace("unpublishing application with token");
					publisher.removeFrom(Collections.singleton(token));
					publisher.update();
				}catch (Exception e) {

					log.error("cannot remove token {} (see details)",token, e);

					// since we've failed no published event is fired and profile
					// will not be stored.
					// we do it manually to ensure we leave some local trace of the
					// changed profile.
					//TODO: CHECK --- store(profile);
				}

			}
		});
	}


	private void share(GCoreEndpoint profile) {

		log.trace("sharing profile for {}", context.name());

		context.properties().add(new Property(profile_property, profile));
	}

	private void publishFirstTime(GCoreEndpoint profile) {

		try {

			publisher.addToAll();

		} catch (Exception e) {
			log.warn("publishing failed",e);
		}
	}

	private GCoreEndpoint loadOrCreateProfile() {

		return create();
	}

	private GCoreEndpoint create() {

		log.info("creating profile for {}", context.name());

		try {

			GCoreEndpoint profile = new GCoreEndpoint();
			profile.setId(context.id());

			builder.fill(profile);

			return profile;

		} catch (RuntimeException e) {

			// this is a critical startup failure: it will fail the application
			throw new RuntimeException("cannot create profile for " + context.name(), e);

		}

	}

	@Override
	public String toString() {
		return profile_management;
	}

}
