package org.gcube.informationsystem.registry.impl.state;

import org.gcube.common.core.contexts.GCUBEStatefulPortTypeContext;

import org.gcube.common.core.state.GCUBEWSHome;
import org.gcube.informationsystem.registry.impl.contexts.ProfileContext;

/**
 * 
 * @author lucio
 *
 */
public class ProfileResourceHome  extends GCUBEWSHome {

	/** {@inheritDoc}*/
	public GCUBEStatefulPortTypeContext getPortTypeContext() {
		return ProfileContext.getContext();
	}

}
