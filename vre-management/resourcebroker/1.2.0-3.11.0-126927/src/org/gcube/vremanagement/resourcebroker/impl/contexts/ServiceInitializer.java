/****************************************************************************
 *  This software is part of the gCube Project.
 *  Site: http://www.gcube-system.org/
 ****************************************************************************
 * The gCube/gCore software is licensed as Free Open Source software
 * conveying to the EUPL (http://ec.europa.eu/idabc/eupl).
 * The software and documentation is provided by its authors/distributors
 * "as is" and no expressed or
 * implied warranty is given for its use, quality or fitness for a
 * particular case.
 ****************************************************************************
 * Filename: ServiceInitializer.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.impl.contexts;

import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.resourcebroker.impl.configuration.BrokerConfiguration;
import org.gcube.vremanagement.resourcebroker.impl.resources.ResourceStorageManager;
import org.gcube.vremanagement.resourcebroker.impl.services.GHNReservationHandler;
import org.gcube.vremanagement.resourcebroker.impl.support.threads.TPersistentResourceRefresh;
import org.gcube.vremanagement.resourcebroker.impl.support.threads.TRevokeReservations;
import org.gcube.vremanagement.resourcebroker.impl.support.threads.TimedThread;
import org.gcube.vremanagement.resourcebroker.impl.support.threads.TimedThreadsStorage;

/**
 * Is a support class where reside all the services and functionalities
 * to activate at instantiation time of ResourceBroker.
 *
 * @author Daniele Strollo (ISTI-CNR)
 */
public class ServiceInitializer {
	private static GCUBELog logger = new GCUBELog(ServiceInitializer.class, BrokerConfiguration.getProperty("LOGGING_PREFIX") + "::[SERV-INIT]");

	/**
	 * @deprecated for internal use only
	 */
	public static void start() throws Exception {

		// Starts the revoker for expired reservations
		if (BrokerConfiguration.getBoolProperty("ENABLE_REVOKE_RESERVATION_HANDLER")) {
			// FIXME
			// the reactivation delay of revocation handler
			// is set to 1/3 of the expiration time for
			// reservations.
			TimedThread reservationRevoker = new TRevokeReservations((long) (BrokerConfiguration.getIntProperty("GHN_RESERVATION_TTL_MINUTES")) * 20 * 1000);
			// Register the timed thread for a clean shutdown
			TimedThreadsStorage.registerThread(reservationRevoker, true);
		}

		if (BrokerConfiguration.getBoolProperty("ENABLE_PERSISTENCE_REFRESH")) {
			TimedThread persistence = new TPersistentResourceRefresh((long) (BrokerConfiguration.getIntProperty("PERSISTENCE_REFRESH_TTL_MINUTES")) * 60000);
			// Register the timed thread for a clean shutdown
			TimedThreadsStorage.registerThread(persistence, true);
		}

		if (BrokerConfiguration.getBoolProperty("PREFETCH_GHNS")) {
			new Thread() {
				public void run() {
					boolean done = false;
					while (!done) {
						try {
							sleep(10000);
						} catch (InterruptedException e1) {
						}
						logger.debug("[CTX-INIT] Prefetching GHN information from IS.");
						logger.info("[INFO] This functionality can be disabled by setting PREFETCH_GHNS=false in the $GLOBUS_LOCATION/etc/org.gcube.vremanagement.resourcebroker/broker.properties file.");

						if (ServiceContext.getContext().getInstance().getScopes().values().size() == 0) {
							return;
						}
						for (GCUBEScope scope : ServiceContext.getContext().getInstance().getScopes().values()) {
							logger.debug("[CTX-INIT] Prefetching GHN information from IS for scope "
											+ scope);
							try {
								GHNReservationHandler.getInstance().getGlobalGHNsForScope(scope, false);
								done = true;
							} catch (Exception e) {
							}
						}

					}
				}
			}.start();
		}
	}

	/**
	 * @deprecated for internal use only
	 */
	public static void stop() {
		logger.debug("Shutting down the ResourceBroker");
		logger.debug("Storing persistent resource");
		try {
			ResourceStorageManager.INSTANCE.storeScores();
		} catch (Exception e) {
			logger.error(e);
		}

		logger.debug("[CTX-STOP] Shutting down");
		TimedThreadsStorage.stopAll();
	}
}
