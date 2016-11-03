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
 * Filename: StatefulBrokerContext.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.impl.contexts;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GCUBEStatefulPortTypeContext;
import org.gcube.common.core.state.GCUBEWSResourceKey;
import org.gcube.vremanagement.resourcebroker.impl.configuration.BrokerConfiguration;

/**
 * The context associated to the {@link org.gcube.vremanagement.resourcebroker.impl.services.BrokerService} used to
 * keep persistence of internal resources.
 *
 * @author Daniele Strollo (ISTI-CNR)
 */
public class StatefulBrokerContext extends GCUBEStatefulPortTypeContext {

	/**
	 * The singleton instance.
	 */
	private static final StatefulBrokerContext CACHE = new StatefulBrokerContext();
	/**
	 * The singleton internal persistent resource.
	 */
	private static GCUBEWSResourceKey resPlanKey = null;

	@Override
	public final String getJNDIName() {
		return BrokerConfiguration.getProperty("JNDI_SERVICE_NAME");
	}

	@Override
	public final String getNamespace() {
		return BrokerConfiguration.getProperty("NS_CONTEXT");
	}

	@Override
	public final GCUBEServiceContext getServiceContext() {
		return ServiceContext.getContext();
	}

	/**
	 *
	 * @return the stateful context
	 */
	public static GCUBEStatefulPortTypeContext getContext() {
		return CACHE;
	}

	/**
	 * Returns the unique key to use to retrieve from the scope
	 * the singleton persistent resource.
	 * @return GCUBEWSResourceKey the key.
	 */
	public static synchronized GCUBEWSResourceKey getResPlanKey() {
		if (resPlanKey == null) {
			resPlanKey = StatefulBrokerContext.getContext().makeKey(BrokerConfiguration.getProperty("SINGLETON_RESOURCE_KEY"));
		}
		return resPlanKey;
	}


}

