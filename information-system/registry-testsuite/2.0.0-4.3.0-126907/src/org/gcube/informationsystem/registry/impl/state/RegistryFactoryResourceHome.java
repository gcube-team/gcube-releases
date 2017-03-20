package org.gcube.informationsystem.registry.impl.state;

import org.gcube.common.core.contexts.GCUBEStatefulPortTypeContext;
import org.gcube.common.core.state.GCUBEWSHome;
import org.gcube.informationsystem.registry.impl.contexts.FactoryContext;

/**
 * 
 * @author lucio
 *
 */
public class RegistryFactoryResourceHome  extends GCUBEWSHome {

	/** {@inheritDoc}*/
	public GCUBEStatefulPortTypeContext getPortTypeContext() {
		return FactoryContext.getContext();
	}
}
