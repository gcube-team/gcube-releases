package org.gcube.data.analysis.statisticalmanager.experimentspace.computation;

import org.gcube.common.core.contexts.GCUBEStatefulPortTypeContext;
import org.gcube.common.core.state.GCUBEWSHome;

public class ComputationHome extends GCUBEWSHome {

	@Override
	public GCUBEStatefulPortTypeContext getPortTypeContext() {
		return ComputationContext.getContext();
	}

}
