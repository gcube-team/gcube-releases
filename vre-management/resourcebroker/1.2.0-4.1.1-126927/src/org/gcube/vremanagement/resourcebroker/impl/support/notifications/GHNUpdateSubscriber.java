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
 * Filename: GHNUpdateSubscriber.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.impl.support.notifications;



import java.util.List;
import java.util.Vector;

import javax.xml.namespace.QName;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.notifier.ISNotifier;
import org.gcube.common.core.informationsystem.notifier.ISNotifier.BaseNotificationConsumer;
import org.gcube.common.core.informationsystem.notifier.ISNotifier.GCUBENotificationTopic;
import org.gcube.common.core.informationsystem.notifier.ISNotifier.NotificationEvent;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.security.GCUBESecurityManager;
import org.gcube.common.core.security.GCUBESecurityManagerImpl;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.resourcebroker.impl.configuration.BrokerConfiguration;
import org.gcube.vremanagement.resourcebroker.impl.services.GHNReservationHandler;
import org.gcube.vremanagement.resourcebroker.impl.support.types.GHNDescriptor;

/**
 * Monitors changes applied to the GHNs.
 * It is implemented through the subscription to events regarding
 * GHN modifications.
 *
 * Starts once the {@link GHNReservationHandler} accesses to the list
 * of GHN registered for a new scope.
 *
 * There is one handler for each scope of GHN.
 *
 * @author Daniele Strollo (ISTI-CNR)
 */
public class GHNUpdateSubscriber extends BaseNotificationConsumer {

	private GCUBELog logger = new GCUBELog(this, BrokerConfiguration.getProperty("LOGGING_PREFIX"));
	private GCUBEScope scope = null;
	private long sleepDelay = 0;

	public GHNUpdateSubscriber(final GCUBEScope scope, final long sleepDelay) {
		super();
		logger.debug("[TTHREADS] Starting " + this.getClass().getSimpleName() + " delay: " + sleepDelay);
		this.scope = scope;
		this.sleepDelay = sleepDelay;
		this.init();
	}

	private void init() {
		try {
			ISNotifier notifier = GHNContext
					.getImplementation(ISNotifier.class);
			List<GCUBENotificationTopic> qnames = new Vector<GCUBENotificationTopic>();
			qnames.add(new GCUBENotificationTopic(new QName(BrokerConfiguration.getProperty("NS_REGISTRY"), "GHN")));

			GCUBESecurityManager secMan = new GCUBESecurityManagerImpl() {
				@Override
				public boolean isSecurityEnabled() {
					return false;
				}
			};
			notifier.registerToISNotification(
					this,
					qnames, 
					secMan, 
					scope);
		} catch (Exception e) {
			logger.error(
					"*** [ERROR] During NotificationHandler instantiation", e);
			e.printStackTrace();
		}
	}

	// @Override
	protected final void onNotificationReceived(final NotificationEvent event) {
		super.onNotificationReceived(event);
		logger.debug("*** [NEW NOTIFICATION] "
				+ this.getClass().getSimpleName());
		String ghnID = event.getPayload().getMessage()[0].getChildNodes().item(
				0).getChildNodes().item(0).getNodeValue();
		String operation = event.getPayload().getMessage()[0].getChildNodes()
				.item(1).getChildNodes().item(0).getNodeValue();

		for (int i = 0; i < event.getPayload().getMessage()[0].getChildNodes()
				.getLength(); i++) {
			logger.debug("*** [NOT_ "
					+ i
					+ "]"
					+ event.getPayload().getMessage()[0].getChildNodes()
							.item(i));
		}

		logger.debug("*** Operation [" + operation + "]");

		// A GHN has been updated
		if (operation.compareTo("update") == 0) {
			try {
				GHNDescriptor ghnDescr = GHNReservationHandler.getInstance()
						.getGHNByID(null, scope, ghnID);
				// If the update involves a GHN that is monitored
				// (for which exists a descriptor)
				if (ghnDescr != null) {
					logger.debug("*** UPDATING GHN: " + ghnID);
					// Tuple<String> updatedGHNInfo =
					// ISClientRequester.getRIOnGHNByID(scope, ghnID);
					// if (updatedGHNInfo != null && updatedGHNInfo.get(0) !=
					// null) {
					// logger.debug("*** UPDATING GHN resources allocated: " +
					// updatedGHNInfo.get(0));
					// ghnDescr.setSortIndex(Integer.parseInt(updatedGHNInfo.get(0)));
					// }
				}
			} catch (Exception e) {
				logger
						.error("*** [ERROR] During reception of a new notification");
				logger.error(e);
			}
		}
		try {
			// waits 1/3 with respect to the minutes
			// required as TTL for reservations
			Thread.sleep(this.sleepDelay);
		} catch (InterruptedException e) {
		}
	}

	public final GCUBEScope getScope() {
		return this.scope;
	}

}


