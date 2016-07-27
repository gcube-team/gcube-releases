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
 * Filename: RICreationSubscriber.java
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
import org.gcube.vremanagement.resourcebroker.impl.services.ISClientRequester;
import org.gcube.vremanagement.resourcebroker.impl.support.types.GHNDescriptor;
import org.gcube.vremanagement.resourcebroker.impl.support.types.Tuple;

/**
 * Monitors creation of Running Instances.
 * 
 * @author Daniele Strollo (ISTI-CNR)
 */
public class RICreationSubscriber extends BaseNotificationConsumer {

	private GCUBELog logger = new GCUBELog(this, BrokerConfiguration.getProperty("LOGGING_PREFIX"));
	private GCUBEScope scope = null;
	private long sleepDelay = 0;

	public RICreationSubscriber(final GCUBEScope scope, final long sleepDelay) {
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
			qnames.add(new GCUBENotificationTopic(new QName(BrokerConfiguration.getProperty("NS_REGISTRY"), "RunningInstance")));			
			
			GCUBESecurityManager secMan = new GCUBESecurityManagerImpl() {
				@Override
				public boolean isSecurityEnabled() {
					return false;
				}
			};

			notifier.registerToISNotification(
			// new Vector<QName>().add()),
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

		String riID = event.getPayload().getMessage()[0].getChildNodes()
				.item(0).getChildNodes().item(0).getNodeValue();
		String operation = event.getPayload().getMessage()[0].getChildNodes()
				.item(1).getChildNodes().item(0).getNodeValue();

		for (int i = 0; i < event.getPayload().getMessage()[0].getChildNodes()
				.getLength(); i++) {
			logger.debug("*** [NOTIFICATION #"
					+ i
					+ "]"
					+ event.getPayload().getMessage()[0].getChildNodes()
							.item(i));
		}

		logger.debug("*** Operation [" + operation + "]");

		try {
			// A GHN has been updated
			if (operation.compareTo("create") == 0) {
				// Gets from the IS the information about the newly created
				// RunningInstance.
				Tuple<String> retval = ISClientRequester.getRIAndGHN(scope,
						riID);
				String ghnToUpdate = null;
				if (retval != null && retval.size() == 2) {
					ghnToUpdate = retval.get(1);
				}
				logger.debug("*** The RI: " + riID + " is running on: "
						+ ghnToUpdate);
				// Gets the corresponding GHN on which the RI is running and
				// refreshes
				// it allocated resource number.
				if (ghnToUpdate != null) {
					logger
							.debug("*** Increasing the GHN running instance number");
					GHNDescriptor ghnDescr = GHNReservationHandler
							.getInstance().getGHNByID(null, scope, ghnToUpdate);
					if (ghnDescr != null) {
						ghnDescr.increaseRICount();
					}
				}
			}
		} catch (Exception e) {
			logger.error(e);
			return;
		}

		if (this.sleepDelay > 0) {
			try {
				// waits 1/3 with respect to the minutes
				// required as TTL for reservations
				Thread.sleep(this.sleepDelay);
			} catch (InterruptedException e) {
			}
		}
	}

	public final GCUBEScope getScope() {
		return this.scope;
	}

}
