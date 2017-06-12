package org.gcube.datatransfer.agent.impl.state;

import org.gcube.common.core.contexts.GCUBEStatefulPortTypeContext;
import org.gcube.common.core.state.GCUBEWSHome;
import org.gcube.datatransfer.agent.impl.context.AgentContext;

public class AgentResourceHome extends GCUBEWSHome {

	/** {@inheritDoc}*/
	public GCUBEStatefulPortTypeContext getPortTypeContext() {
		return AgentContext.getContext();
	}
}