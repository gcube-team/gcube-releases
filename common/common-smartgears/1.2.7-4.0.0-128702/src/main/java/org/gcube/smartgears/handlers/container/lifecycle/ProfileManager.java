package org.gcube.smartgears.handlers.container.lifecycle;

import static java.util.concurrent.TimeUnit.*;
import static org.gcube.common.events.Observes.Kind.*;
import static org.gcube.smartgears.Constants.*;
import static org.gcube.smartgears.handlers.ProfileEvents.*;
import static org.gcube.smartgears.lifecycle.container.ContainerLifecycle.*;
import static org.gcube.smartgears.lifecycle.container.ContainerState.*;
import static org.gcube.smartgears.utils.Utils.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.common.events.Observes;
import org.gcube.common.resources.gcore.HostingNode;
import org.gcube.common.resources.gcore.Resources;
import org.gcube.smartgears.configuration.container.ContainerConfiguration;
import org.gcube.smartgears.context.Property;
import org.gcube.smartgears.context.container.ContainerContext;
import org.gcube.smartgears.handlers.container.ContainerHandler;
import org.gcube.smartgears.handlers.container.ContainerLifecycleEvent.Start;
import org.gcube.smartgears.lifecycle.container.ContainerLifecycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Manages the resource profile of the container.
 * <p>
 * 
 * The manager:
 * 
 * <ul>
 * <li>creates the profile when the container starts for the first time;
 * <li>loads the profile when the container restarts;
 * <li>publishes the profile when the container becomes active, and at any lifecycle change thereafter;
 * <li>stores the profile locally after each publication;
 * </ul>
 * 
 * @author Fabio Simeoni
 * @see ProfileBuilder
 */
@XmlRootElement(name = profile_management)
public class ProfileManager extends ContainerHandler {

	private static Logger log = LoggerFactory.getLogger(ProfileManager.class);

	private ContainerContext context;

	private ProfileBuilder builder;
	private ProfilePublisher publisher;

	private ScheduledFuture<?> periodicUpdates;

	@Override
	public void onStart(Start e) {

		context = e.context();
		builder = new ProfileBuilder(context);
		publisher = new ProfilePublisher(context);

		HostingNode profile = loadOrCreateProfile();

		share(profile);

		registerObservers();

		schedulePeriodicUpdates();

		// note we don't fire profile events, but wait for the final startup response which
		// will result in a state change. only then we publish and store the profile
		// this avoids the redundancy and performance penalty of storing and publishing multiple
		// times in rapid succession (which would be correct). Revise if proves problematic in corner
		// cases.

	}

	private void registerObservers() {

		context.events().subscribe(new Object() {

			@Observes({ activation, part_activation, shutdown, stop, failure })
			void onChanged(ContainerLifecycle lc) {

				HostingNode profile = context.profile(HostingNode.class);

				profile.profile().description().status(lc.state().remoteForm());

				// since we do not know the observers, they will deal with failures and their consequences
				// any that comes back will be logged in this event thread
				context.events().fire(profile, changed);
			}

			@Observes(value = published, kind = resilient)
			void storeAfterPublish(HostingNode profile) {

				// publish may change scopes, so we store after publication

				store(profile);

				// we are resilient: if a previous critical observer fails
				// we still try to leave local traces of the changed profile

			}

			@Observes(value = published)
			void shareAfterPublish(HostingNode profile) {

				share(profile); // publish may produce a new profile instance

			}

			@Observes(value = changed, kind = critical)
			void publishAfterChange(HostingNode profile) {

				publish(profile); // if successful, triggers share and store.

			}
		});
	}

	private HostingNode loadOrCreateProfile() {

		File file = context.configuration().persistence().file(container_profile_file_path);

		return file.exists() ? loadProfile(file) : createProfile();

	}

	private void share(HostingNode profile) {

		log.trace("sharing container profile");
		context.properties().add(new Property(container_profile_property, profile));
	}

	private HostingNode createProfile() {

		log.info("creating container profile");

		try {
			return builder.create();

		} catch (Throwable e) {

			// this is a critical startup failure: it will fail the application
			throw new RuntimeException("cannot create container profile", e);

		}

	}

	private HostingNode loadProfile(File file) {

		log.info("loading container profile @ {}", file.getAbsolutePath());

		FileInputStream in = null;
		try {

			in = new FileInputStream(file);

			HostingNode node = Resources.unmarshal(HostingNode.class, in);

			builder.update(node, true);

			return node;

		} catch (Throwable e) {

			// this is a critical startup failure: it will fail the whole application
			throw new RuntimeException("cannot load container profile @ " + file.getAbsolutePath(), e);

		} finally {
			closeSafely(in);
		}

	}

	private void store(HostingNode profile) {

		File file = context.persistence().writefile(container_profile_file_path);

		FileOutputStream out = null;

		try {

			out = new FileOutputStream(file);

			log.trace("storing container profile @ {}", file.getAbsolutePath());

			Resources.marshal(profile, out);

		} catch (Exception e) {

			// we absorb failure for extra resilience, in case problem is temporary.
			// however: if we go down before we can store again, the changes will be lost on restart.

			log.error("cannot store container profile @ {}", file.getAbsolutePath(), e);

		} finally {

			closeSafely(out);
		}
	}

	private void publish(HostingNode profile) {

		ContainerConfiguration configuration = context.configuration();

		// first-publication vs. routine publication: when we delete scopes let's make sure there is
		// at least one left of it will be re-triggered
		boolean firstPublication = profile.scopes().isEmpty();

		try {

			if (firstPublication)
				publisher.addTo(configuration.startScopes());
			else
				publisher.update();

		} catch (Exception e) {

			log.error("cannot publish container (see details)", e);

			// since we've failed no published event is fired and profile will not be stored.
			// we do it manually to ensure we leave some local trace of the changed profile.
			store(profile);

		}
	}

	private void schedulePeriodicUpdates() {

		// register to cancel updates
		context.events().subscribe(

		new Object() {

			final ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
			
			// we register it in response to lifecycle events so that we can stop and resume along with application
			@Observes(value = { activation, part_activation }, kind = resilient)
			void restartPeriodicUpdates(ContainerLifecycle lc) {

				//already running
				if (periodicUpdates!=null)
					return;
				
				if (lc.state()==active)
					log.info("scheduling periodic updates of container profile");
				
				else
					log.info("resuming periodic updates of container profile");
						
				final Runnable updateTask = new Runnable() {
					public void run() {

						HostingNode profile = context.profile(HostingNode.class);

						try {
							builder.update(profile, false);
						}
						catch(Exception e) {
							//we may fail in the update of the profile
							log.error("cannot complete periodic update of container profile",e);
						}
						
						//if handling of event generates failures these will be reported
						//for resilience we do not fail the application
						context.events().fire(profile,changed);
						
					}
				};

				periodicUpdates = service.scheduleAtFixedRate(updateTask, 3, context.configuration()
						.publicationFrequency(), SECONDS);
				

			}

			@Observes(value = { stop, failure, shutdown }, kind = resilient)
			void cancelPeriodicUpdates(ContainerLifecycle ignore) {
				
				if (periodicUpdates != null)
					log.trace("stopping periodic updates of container profile");
				
				try {
					periodicUpdates.cancel(false);
					service.shutdownNow();
					periodicUpdates=null;
				}
				catch(Exception e) {
					log.warn("could not stop periodic updates of container profile",e);
				}
			}

		});

	}

	@Override
	public String toString() {
		return profile_management;
	}
}
