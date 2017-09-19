package org.gcube.datatransfer.agent.library.plugins;

import static org.gcube.common.clients.stubs.jaxws.StubFactory.*;
import static org.gcube.datatransfer.agent.library.fws.Constants.*;

import javax.xml.ws.EndpointReference;

import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.datatransfer.agent.library.AgentLibrary;
import org.gcube.datatransfer.agent.library.Constants;
import org.gcube.datatransfer.agent.library.fws.AgentServiceJAXWSStubs;




public class AgentServicePlugin  extends AbstractPlugin<AgentServiceJAXWSStubs,AgentLibrary> {

	public AgentServicePlugin() {
		super(Constants.PORT_TYPE_NAME);
	}
	
	
	
	public AgentServiceJAXWSStubs resolve(EndpointReference address,ProxyConfig<?,?> config) throws Exception {
		return stubFor(agent).at(address);
		
	}
	
	@Override
	public AgentLibrary newProxy(ProxyDelegate<AgentServiceJAXWSStubs> delegate) {
		return new AgentLibrary(delegate);
	}



	@Override
	public Exception convert(Exception arg0, ProxyConfig<?, ?> arg1) {
		return arg0;
	}

}