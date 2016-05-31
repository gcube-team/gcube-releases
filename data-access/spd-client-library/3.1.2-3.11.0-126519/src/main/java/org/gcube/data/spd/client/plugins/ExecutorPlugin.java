package org.gcube.data.spd.client.plugins;

import static org.gcube.common.clients.stubs.jaxws.StubFactory.stubFor;
import static org.gcube.data.spd.client.Constants.executor;

import javax.xml.ws.EndpointReference;

import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.data.spd.client.proxies.DefaultExecutor;
import org.gcube.data.spd.client.proxies.Executor;
import org.gcube.data.spd.stubs.ExecutorStub;

public class ExecutorPlugin extends AbstractPlugin<ExecutorStub, Executor> {

	public ExecutorPlugin() {
		super("gcube/data/speciesproductsdiscovery/executor");
	}

	@Override
	public Exception convert(Exception fault, ProxyConfig<?, ?> config) {
		return fault;
	}

	@Override
	public ExecutorStub resolve(EndpointReference address,
			ProxyConfig<?, ?> config) throws Exception {
		return stubFor(executor).at(address);
	}

	@Override
	public Executor newProxy(ProxyDelegate<ExecutorStub> delegate) {
		return new DefaultExecutor(delegate);
	}

}
