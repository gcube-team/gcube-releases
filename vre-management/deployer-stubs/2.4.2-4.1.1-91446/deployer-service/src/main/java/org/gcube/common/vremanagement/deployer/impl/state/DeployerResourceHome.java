package org.gcube.common.vremanagement.deployer.impl.state;

import org.gcube.common.core.contexts.GCUBEStatefulPortTypeContext;
import org.gcube.common.core.state.GCUBEWSHome;
import org.gcube.common.vremanagement.deployer.impl.contexts.StatefulPortTypeContext;

/**
 * Home implementation for the singleton resource
 * @author Manuele Simi
 *
 */

public class DeployerResourceHome extends GCUBEWSHome {

	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public GCUBEStatefulPortTypeContext getPortTypeContext() {		
		return StatefulPortTypeContext.getContext();
	}

}
