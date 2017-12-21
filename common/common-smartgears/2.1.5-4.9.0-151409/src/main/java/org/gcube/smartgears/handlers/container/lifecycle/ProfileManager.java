package org.gcube.smartgears.handlers.container.lifecycle;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.gcube.common.events.Observes.Kind.critical;
import static org.gcube.common.events.Observes.Kind.resilient;
import static org.gcube.smartgears.Constants.container_profile_property;
import static org.gcube.smartgears.Constants.profile_management;
import static org.gcube.smartgears.handlers.ProfileEvents.addToContext;
import static org.gcube.smartgears.handlers.ProfileEvents.changed;
import static org.gcube.smartgears.handlers.ProfileEvents.published;
import static org.gcube.smartgears.handlers.ProfileEvents.removeFromContext;
import static org.gcube.smartgears.lifecycle.container.ContainerLifecycle.activation;
import static org.gcube.smartgears.lifecycle.container.ContainerLifecycle.failure;
import static org.gcube.smartgears.lifecycle.container.ContainerLifecycle.part_activation;
import static org.gcube.smartgears.lifecycle.container.ContainerLifecycle.shutdown;
import static org.gcube.smartgears.lifecycle.container.ContainerLifecycle.stop;
import static org.gcube.smartgears.lifecycle.container.ContainerState.active;

import java.util.Collections;
import java.util.concurrent.ScheduledFuture;

import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.common.events.Observes;
import org.gcube.common.resources.gcore.HostingNode;
import org.gcube.smartgears.context.Property;
import org.gcube.smartgears.context.container.ContainerContext;
import org.gcube.smartgears.handlers.container.ContainerHandler;
import org.gcube.smartgears.handlers.container.ContainerLifecycleEvent.Start;
import org.gcube.smartgears.lifecycle.container.ContainerLifecycle;
import org.gcube.smartgears.utils.Utils;
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

		activated();

		// note we don't fire profile events, but wait for the final startup response which
		// will result in a state change. only then we publish and store the profile
		// this avoids the redundancy and performance penalty of storing and publishing multiple
		// times in rapid succession (which would be correct). Revise if proves problematic in corner
		// cases.

	}

	private void activated(){
		HostingNode profile = loadOrCreateProfile();

		share(profile);

		publisher = new ProfilePublisher(context);

		registerObservers();

		schedulePeriodicUpdates();
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


			@Observes(value = published)
			void shareAfterPublish(HostingNode profile) {

				share(profile); // publish may produce a new profile instance

			}

			@Observes(value = changed, kind = critical)
			void publishAfterChange(HostingNode profile) {
				log.info("Publish after profile Change event called");
				publish(profile); // if successful, triggers share and store.

			}
			
			@Observes(value = addToContext)
			void addTo(String token) {
				try {
					log.trace("publishing container with new token");
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
					log.trace("unpublishing container with new token");
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

	private HostingNode loadOrCreateProfile() {
		
		return createProfile();

	}

	private void share(HostingNode profile) {

		log.trace("sharing container profile");
		context.properties().add(new Property(container_profile_property, profile));
	}

	private HostingNode createProfile() {

		log.info("creating container profile");

		try {
			HostingNode node = builder.create();
			node.setId(context.id());
			return node;
		} catch (Throwable e) {

			// this is a critical startup failure: it will fail the application
			throw new RuntimeException("cannot create container profile", e);

		}

	}

	private void publish(HostingNode profile) {

		//ContainerConfiguration configuration = context.configuration();

		// first-publication vs. routine publication: when we delete scopes let's make sure there is
		// at least one left of it will be re-triggered
		boolean firstPublication = profile.scopes().isEmpty();

		try {

			if (firstPublication)
				publisher.addToAll();
			else
				publisher.update();

		} catch (Exception e) {

			log.error("cannot publish container (see details)", e);

			// since we've failed no published event is fired and profile will not be stored.
			// we do it manually to ensure we leave some local trace of the changed profile.
			//store(profile);

		}
	}

	private void schedulePeriodicUpdates() {

		// register to cancel updates
		context.events().subscribe(

				new Object() {

					// we register it in response to lifecycle events so that we can stop and resume along with application
					@Observes(value = { activation, part_activation }, kind = resilient)
					synchronized void restartPeriodicUpdates(ContainerLifecycle lc) {

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
								log.trace("firing change event on container profile");
								context.events().fire(profile,changed);
							}
						};

						periodicUpdates = Utils.scheduledServicePool.scheduleAtFixedRate(updateTask, 3, context.configuration()
								.publicationFrequency(), SECONDS);

					}

					@Observes(value = { stop, failure, shutdown }, kind = resilient)
					synchronized void cancelPeriodicUpdates(ContainerLifecycle ignore) {

						if (periodicUpdates != null){
							log.trace("stopping periodic updates of container profile");

							try {
								periodicUpdates.cancel(true);
								periodicUpdates=null;
							}
							catch(Exception e) {
								log.warn("could not stop periodic updates of container profile",e);
							}
						}
					}

				});

	}

	@Override
	public String toString() {
		return profile_management;
	}
}
