package org.gcube.data.analysis.statisticalmanager.plugins;


import static org.gcube.common.clients.stubs.jaxws.StubFactory.stubFor;
import static org.gcube.data.analysis.statisticalmanager.stubs.SMConstants.computation_factory;

import javax.xml.ws.EndpointReference;

import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.data.analysis.statisticalmanager.proxies.StatisticalManagerDefaultFactory;
import org.gcube.data.analysis.statisticalmanager.stubs.ComputationFactoryStub;




public class StatisticalManagerFactoryPlugin extends AbstractPlugin<ComputationFactoryStub,StatisticalManagerDefaultFactory> {

	private static final String FACTORY_NAME = "gcube/data/analysis/statisticalmanager/statisticalmanagerfactory";
	
	public StatisticalManagerFactoryPlugin() {
		super(FACTORY_NAME);
	}
	
	@Override
	public Exception convert(Exception fault,ProxyConfig<?,?> config) {
		fault.printStackTrace();
		return fault;
	}
	
	@Override
	public ComputationFactoryStub resolve(EndpointReference address,ProxyConfig<?,?> config) throws Exception {
		//return new ComputationFactoryServiceAddressingLocator().getComputationFactoryPortTypePort(address);
		return stubFor(computation_factory).at(address);

	}
	
	@Override
	public StatisticalManagerDefaultFactory newProxy(ProxyDelegate<ComputationFactoryStub> delegate) {
		return new StatisticalManagerDefaultFactory(delegate);
	}
}
