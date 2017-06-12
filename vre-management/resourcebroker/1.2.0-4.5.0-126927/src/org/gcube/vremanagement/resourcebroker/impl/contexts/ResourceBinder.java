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
 * Filename: ResourceBinder.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.impl.contexts;

import java.util.Iterator;
import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.handlers.GCUBEHandler;
import org.gcube.common.core.utils.handlers.GCUBEScheduledHandler;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.resourcebroker.impl.configuration.BrokerConfiguration;
import org.gcube.vremanagement.resourcebroker.impl.resources.ResourceStorageManager;
import org.gcube.vremanagement.resourcebroker.impl.services.BrokerService;
import org.gcube.vremanagement.resourcebroker.utils.assertions.Assertion;
import org.gcube.vremanagement.resourcebroker.utils.console.PrettyFormatter;
import org.globus.wsrf.ResourceException;


/**
 * Tries to create the resource in all the instance scopes.
 * The task is delayed and the priority is given according to the
 * policies specified at {@link ResourceBinder} instantiation.
 * The delay is specified by {@link Configuration#SLEEP_TIME}.
 * The resource binding is done by {@link ServiceContext#onReady()}.
 *
 * @author Daniele Strollo (ISTI-CNR)
 */
class ResourceBinderTask extends GCUBEHandler<Object> {
	private GCUBELog logger = new GCUBELog(this, PrettyFormatter.bold(BrokerConfiguration.getProperty("LOGGING_PREFIX") + "::[RES-BINDER]"));

	/**
	 * Internally used to publish a singleton resource in the
	 * scope passed in input.
	 * As resource publication key the value of
	 * {@link StatefulBrokerContext#getResPlanKey()} will be used.
	 * @param scope the scope the singleton resource will be bound to.
	 * @throws ResourceException when resource publication fails.
	 */
	private void publishResource(final GCUBEScope scope) throws ResourceException {
		Assertion<ResourceException> checker = new Assertion<ResourceException>();
		checker.validate(scope != null, new ResourceException("The given parameter is null."));
		ServiceContext.getContext().setScope(scope);
		StatefulBrokerContext.getContext().getWSHome().create(StatefulBrokerContext.getResPlanKey());
		logger.debug("[PUBLISH] Binding Stateful Resource BOUND to scope [" + scope + "]");
	}

	/**
	 * Externally invoked by the framework run-time support
	 * to initialize the publication of resources.
	 * @throws GCUBEFault if the publication to the required
	 * scope goes wrong.
	 */
	public void run() throws GCUBEFault {
		// -- CHECKS
		Assertion<GCUBEFault> checker = new Assertion<GCUBEFault>();
		checker.validate(ServiceContext.getContext() != null, new GCUBEFault("Context not defined"));
		checker.validate(ServiceContext.getContext().getInstance() != null, new GCUBEFault("Context instance not defined"));
		checker.validate(ServiceContext.getContext().getInstance().getScopes() != null, new GCUBEFault("Context instance scopes not defined"));
		checker.validate(ServiceContext.getContext().getInstance().getScopes().values().size() > 0, new GCUBEFault("Context instance scopes not defined"));

		logger.info("[BINDING-LOOP] Available scopes: " + ServiceContext.getContext().getInstance().getScopes().values());

		if (ServiceContext.getContext().getInstance().getScopes().values().size() != 1) {
			logger.error(PrettyFormatter.underlined(BrokerService.class.getSimpleName() + " has been configured to join more than one scope, while it can work only in ONE single scope. Configure the deploy-jndi-config.xml file of broker properly."));
			if (BrokerConfiguration.getProperty("CONTEXT_SCOPE") != null) {
				logger.warn("Trying with default configuration in broker.properties.");
				try {
					this.publishResource(GCUBEScope.getScope(BrokerConfiguration.getProperty("CONTEXT_SCOPE")));
				} catch (Exception e) {
					throw new GCUBEFault(e, BrokerService.class.getSimpleName() + " failed with default scope binding");
				}
			} else {
				throw new GCUBEFault(BrokerService.class.getSimpleName() + " has been configured to join more than one scope, while it can work only in ONE single scope");
			}
			return;
		}


		// FIXME to better fix the code.
		// the try in find (the while guard) must not throw an exception because
		// otherwise the loop will not be re-executed
		// Tries to bind the resource to the several scopes declared
		boolean done = false;
		Iterator<GCUBEScope> scopes = ServiceContext.getContext().getInstance().getScopes().values().iterator();
		GCUBEScope scope = scopes.next();
		while (!done && scope != null) {
			try {
				this.publishResource(scope);
				done = true;
				ResourceStorageManager.INSTANCE.getResource();
			} catch (ResourceException e) {
				logger.error("Cannot bind the resource to the proposed scope " + scope.toString());
				logger.error(e);
				// after error assign a new scope
				scope = scopes.next();
			}
		}
	}
}

/**
 * The agent responsible to publish and retrieve the persistent
 * WS-Resources stored in the stateful broker context.
 * It is demanded to properly initialize the resource in the
 * appropriate scope.
 *
 * @author Daniele Strollo (ISTI-CNR)
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ResourceBinder extends GCUBEScheduledHandler {
	private GCUBELog logger = new GCUBELog(this, BrokerConfiguration.getProperty("LOGGING_PREFIX") + "::[RES-BINDER]");
	private static final int MAX_EXCEPTION_COUNT = 3;

	/**
	 * For internal use only.
	 * Periodically tries to create the resource if something goes wrong.
	 * @param interval the delay for further tries.
	 * @param mode the loop mode.
	 */
	public ResourceBinder(final long interval, final Mode mode) {
		super(interval, mode);
	}

	@Override
	protected final boolean repeat(final Exception exception, final int exceptionCount) {
		logger.debug("[RES-BINDER] Scheduled Handler: repeat loop");
		if (exception == null) {
			return false;
		}

		logger.debug("[INIT] Failed to create the broker resource (attempt "
				+ exceptionCount + " out of " + exceptionCount + ")",
				exception);
		if (exceptionCount >= MAX_EXCEPTION_COUNT) {
			logger.error("[INIT] Max attempts reached, no more chance to register the VREManager resource, the service startup failed");
			ServiceContext.getContext().setStatus(GCUBEServiceContext.Status.FAILED);
			return false;
		}
		return true;
	}
}
