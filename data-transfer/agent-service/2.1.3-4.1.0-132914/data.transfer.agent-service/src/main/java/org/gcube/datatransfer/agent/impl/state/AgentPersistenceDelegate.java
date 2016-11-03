package org.gcube.datatransfer.agent.impl.state;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.gcube.common.core.persistence.GCUBEWSFilePersistenceDelegate;


public class AgentPersistenceDelegate extends GCUBEWSFilePersistenceDelegate<AgentResource>{
	
	/**{@inheritDoc}*/
	@SuppressWarnings("unchecked")
	protected void onLoad(AgentResource resource, ObjectInputStream stream) throws Exception {
		super.onLoad(resource, stream);	
	}
	
	/**{@inheritDoc}*/
	protected void onStore(AgentResource resource, ObjectOutputStream stream) throws Exception {
		super.onStore(resource, stream);
	}
	
}