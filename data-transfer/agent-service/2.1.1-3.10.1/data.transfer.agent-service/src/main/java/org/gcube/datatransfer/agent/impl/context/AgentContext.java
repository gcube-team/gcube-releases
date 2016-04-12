package org.gcube.datatransfer.agent.impl.context;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GCUBEStatefulPortTypeContext;
import org.gcube.datatransfer.agent.impl.state.AgentResource;

/**
 * 
 * 
 * @author Andrea Manzi (CERN)
 *
 */
public class AgentContext extends GCUBEStatefulPortTypeContext{
	
	private static final String SINGLETON_AGENT_ID = "agent";
	
	static private final String PORTTYPE_NAME = "gcube/datatransfer/agent/DataTransferAgent";
	
	private AgentContext(){
		
	}
	
	protected static final AgentContext cache = new AgentContext();

	/**
	 * 
	 * @return profile Context
	 */
	public static AgentContext getContext() {
		return cache;
	}
	
	
	/**
	 * 
	 * @return the port type name
	 */
	public final String getJNDIName() {
		return PORTTYPE_NAME;
	}
	
	/**
	 * 
	 * @return the namespace
	 */
	public final String getNamespace() {
		return "http://gcube-system.org/namespaces/datatransfer/agent/datatransferagent";
	}
	
	/**
	 * 
	 * @return the ServiceContext
	 */
	public GCUBEServiceContext getServiceContext() {
		return ServiceContext.getContext();
	}
	

	public AgentResource getAgent() throws Exception {
		AgentResource resource = null;
		try {
			 resource = (AgentResource) getWSHome().find(makeKey(SINGLETON_AGENT_ID));
		} catch (org.globus.wsrf.NoSuchResourceException ex){
			 resource = createAgent() ;
		}
		return resource;
	}	

	public AgentResource createAgent() throws Exception {
		return (AgentResource) getWSHome().create(makeKey(SINGLETON_AGENT_ID));
	}
	
}


