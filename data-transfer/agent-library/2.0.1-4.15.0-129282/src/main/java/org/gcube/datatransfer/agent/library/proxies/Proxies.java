package org.gcube.datatransfer.agent.library.proxies;

import org.gcube.common.clients.fw.builders.StatelessBuilder;
import org.gcube.common.clients.fw.builders.StatelessBuilderImpl;
import org.gcube.datatransfer.agent.library.AgentLibrary;
import org.gcube.datatransfer.agent.library.fws.AgentServiceJAXWSStubs;
import org.gcube.datatransfer.agent.library.plugins.AgentServicePlugin;

public class Proxies {
	
	private static final AgentServicePlugin plugin = new AgentServicePlugin();
	 
	public static StatelessBuilder<AgentLibrary> transferAgent() {
	    return new StatelessBuilderImpl<AgentServiceJAXWSStubs,AgentLibrary>(plugin);
	}

}
