package org.gcube.smartgears.handlers.application.lifecycle;

import static org.gcube.common.events.Observes.Kind.*;
import static org.gcube.smartgears.Constants.*;
import static org.gcube.smartgears.handlers.ProfileEvents.*;
import static org.gcube.smartgears.lifecycle.application.ApplicationLifecycle.*;
import static org.gcube.smartgears.lifecycle.application.ApplicationState.*;
import static org.gcube.smartgears.utils.Utils.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.common.events.Observes;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.HostingNode;
import org.gcube.common.resources.gcore.Resources;
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
		publisher = new ProfilePublisher(context);

		GCoreEndpoint profile = loadOrCreateProfile();

		share(profile);

		registerObservers();

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

	// helpers

	private void registerObservers() {

		context.events().subscribe(new Object() {

			@Observes({ activation, stop, failure })
			void onChanged(ApplicationLifecycle lc) {

				GCoreEndpoint profile = context.profile(GCoreEndpoint.class);

				profile.profile().deploymentData().status(lc.state().remoteForm());

				// since we do not know the observers, they will deal with
				// failures and their consequences
				// any that comes back will be logged in this event thread
				context.events().fire(profile, changed);
			}

			@Observes(value = published, kind = resilient)
			void storeAfterPublish(GCoreEndpoint profile) {

				// publish may change scopes, so we store after publication

				store(profile);

				// we are resilient: if a previous critical observer fails
				// we still try to leave local traces of the changed profile

			}

			@Observes(value = published)
			void shareAfterPublish(GCoreEndpoint profile) {

				share(profile); // publish may produce a new profile instance

			}

			@Observes(value = changed, kind = critical)
			void publishAfterChange(GCoreEndpoint profile) {

				boolean firstPublication = profile.scopes().isEmpty();

				//if we've failed before first publication do not try to publish
				//(we may well have failed there)
				try {
					
					if (firstPublication) {
						if (context.lifecycle().state()!= failed)
							publishFirstTime(profile);
					}
					else
						publisher.update(); // if successful, triggers share and store.

				}
				catch (Exception e) {

					log.error("cannot publish "+context.name()+" (see details)", e);

					// since we've failed no published event is fired and profile
					// will not be stored.
					// we do it manually to ensure we leave some local trace of the
					// changed profile.
					store(profile);
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

			Set<String> startScopes = context.configuration().startScopes();

			Collection<String> containerScopes = context.container().profile(HostingNode.class).scopes()
					.asCollection();

			startScopes = startScopes.isEmpty() ? new HashSet<>(containerScopes) : validateStartScopes(startScopes,
					containerScopes);

			publisher.addTo(startScopes);

		} catch (Exception e) {

			context.lifecycle().moveTo(failed);
			
			throw e;
			
		}
	}

			

	private Set<String> validateStartScopes(Set<String> startScopes, Collection<String> containerScopes) {

		Set<String> validated = new HashSet<>();

		candidates: for (String candidate : startScopes) {
			for (String containerScope : containerScopes)
				if (candidate.contains(containerScope)) {
					validated.add(candidate);
					continue candidates;
				}

			log.error("cannot start {} in scope {}, as it is not compatible with the scopes of the container",
					context.name(), candidate);
		}

		if (validated.isEmpty())
			throw new IllegalStateException(context.name() + "'s start scopes are all invalid");

		return validated;

	}

	private GCoreEndpoint loadOrCreateProfile() {

		File file = context.configuration().persistence().file(profile_file_path);

		return file.exists() ? load(file) : create();
	}

	private GCoreEndpoint create() {

		log.info("creating profile for {}", context.name());

		try {

			GCoreEndpoint profile = new GCoreEndpoint();

			builder.fill(profile);

			return profile;

		} catch (RuntimeException e) {

			// this is a critical startup failure: it will fail the application
			throw new RuntimeException("cannot create profile for " + context.name(), e);

		}

	}

	private GCoreEndpoint load(File file) {

		FileInputStream in = null;

		try {

			in = new FileInputStream(file);

			log.info("loading profile for {} @ {}", context.name(), file.getAbsolutePath());

			GCoreEndpoint profile = Resources.unmarshal(GCoreEndpoint.class, in);

			builder.fill(profile);

			return profile;

		} catch (Throwable e) {

			// this is a critical startup failure: it will fail the whole
			// application
			throw new RuntimeException("cannot load profile for " + context.name() + " @ " + file.getAbsolutePath(), e);

		} finally {

			closeSafely(in);
		}

	}

	private void store(GCoreEndpoint profile) {

		File file = context.persistence().writefile(profile_file_path);

		FileOutputStream out = null;

		try {

			out = new FileOutputStream(file);

			log.trace("storing profile for {} @ {}", context.name(), file.getAbsolutePath());

			Resources.marshal(profile, out);

		} catch (Exception e) {

			// we absorb failure for extra resilience, in case problem is
			// temporary.
			// however: if we go down before we can store again, the changes
			// will be lost on restart.

			log.error("cannot store profile for {} @ {}", new Object[] { context.name(), file.getAbsolutePath() }, e);

		} finally {

			closeSafely(out);
		}
	}

	@Override
	public String toString() {
		return profile_management;
	}

}
