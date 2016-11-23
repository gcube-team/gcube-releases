package org.gcube.vremanagement.vremodeler.impl;

import org.gcube.common.core.state.GCUBEWSHome;

public class ModelerHome extends GCUBEWSHome{

	@Override
	public ModelerContext getPortTypeContext() {
		return ModelerContext.getPortTypeContext();
	}

}
