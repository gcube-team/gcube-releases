package org.gcube.data.analysis.statisticalmanager.plugins;

import static org.gcube.common.clients.stubs.jaxws.StubFactory.stubFor;
import static org.gcube.data.analysis.statisticalmanager.stubs.SMConstants.computation;

import javax.xml.ws.EndpointReference;

import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.data.analysis.statisticalmanager.proxies.StatisticalManagerDefaultService;
import org.gcube.data.analysis.statisticalmanager.proxies.StatisticalManagerService;
import org.gcube.data.analysis.statisticalmanager.stubs.ComputationStub;


public class StatisticalManagerServicePlugin extends AbstractPlugin<ComputationStub, StatisticalManagerService> {

	private static final String SERVICE_NAME = "gcube/data/analysis/statisticalmanager/statisticalmanagerfactory";

	public StatisticalManagerServicePlugin() {
		super(SERVICE_NAME);
		
	}

	@Override
	public Exception convert(Exception fault, ProxyConfig<?, ?> config) {
		// TODO Auto-generated method stub
		fault.printStackTrace();
		return fault;
	}

	@Override
	public ComputationStub resolve(EndpointReference address,
			ProxyConfig<?, ?> config) throws Exception {
		//return new ComputationServiceAddressingLocator().getComputationPortTypePort(address);
		return stubFor(computation).at(address);
	}

	@Override
	public StatisticalManagerService newProxy(
			ProxyDelegate<ComputationStub> delegate) {
		return new StatisticalManagerDefaultService(delegate);
	}

}
