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
 * Filename: ServiceContext.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.impl.contexts;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.utils.handlers.GCUBEScheduledHandler;
import org.gcube.vremanagement.resourcebroker.impl.configuration.BrokerConfiguration;

/**
 * ResourceBroker service context implementation.
 *
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class ServiceContext extends GCUBEServiceContext {

	/**
	 * The static context used by {@link org.gcube.vremanagement.resourcebroker.impl.services.BrokerService}
	 * and its related resources.
	 */
	private static ServiceContext theContext = new ServiceContext();

	/**
	 * Gets the current service context.
	 * @return the service context
	 */
	public static ServiceContext getContext() {
		return theContext;
	}

	@Override
	protected final String getJNDIName() {
		return BrokerConfiguration.getProperty("JNDI_SERVICE_NAME");
	}

	/**
	 * {@inheritDoc}
	 *
	 * It sequentially starts two {@link GCUBEScheduledHandler}:
	 * the first one creates the Deployer Resource, then, the second one adds to all the packages
	 * deployed before the last restart to their instructed scopes.
	 *
	 * Once the static service context is ready the tasks demanded to
	 * bind the resource are launched.
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	protected final void onReady() throws Exception {
		super.onReady();
		//creates the stateful resource for the service with a short delay
		ResourceBinder stateScheduler = new ResourceBinder(BrokerConfiguration.getIntProperty("SLEEP_TIME"), GCUBEScheduledHandler.Mode.LAZY);
		stateScheduler.setScheduled(new ResourceBinderTask());
		stateScheduler.run();

		ServiceInitializer.start();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected final void onShutdown() throws Exception {
		ServiceInitializer.stop();
		super.onShutdown();
	}


}
