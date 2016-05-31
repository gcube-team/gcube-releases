package org.gcube.data.spd.manager;

import org.gcube.common.core.contexts.GCUBEStatefulPortTypeContext;
import org.gcube.common.core.state.GCUBEWSHome;

public class ManagerHome extends GCUBEWSHome {

	@Override
	public GCUBEStatefulPortTypeContext getPortTypeContext() {
		return ManagerPTContext.getContext();
	}

}
