package org.gcube.data.analysis.statisticalmanager.plugins;


import static org.gcube.common.clients.stubs.jaxws.StubFactory.stubFor;
import static org.gcube.data.analysis.statisticalmanager.stubs.SMConstants.dataspace;

import javax.xml.ws.EndpointReference;

import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.data.analysis.statisticalmanager.proxies.StatisticalManagerDefaultDataSpace;
import org.gcube.data.analysis.statisticalmanager.stubs.DataSpaceStub;



public class StatisticalManagerDataSpacePlugin extends AbstractPlugin<DataSpaceStub, StatisticalManagerDefaultDataSpace> {

	private static final String SERVICE_NAME = "gcube/data/analysis/statisticalmanager/statisticalmanagerdataspace";

	public StatisticalManagerDataSpacePlugin() {
		super(SERVICE_NAME);
		
	}

	@Override
	public Exception convert(Exception fault, ProxyConfig<?, ?> config) {
		// TODO Auto-generated method stub
	//	fault.printStackTrace();
		
//		if (fault instanceof SMResourceNotFoundFault) {
//			return new ResourceNotFoundException();
//		}
		return fault;
	}

	@Override
	public DataSpaceStub resolve(EndpointReference address,
			ProxyConfig<?, ?> config) throws Exception {
		return stubFor(dataspace).at(address);
		//return new DataSpaceServiceAddressingLocator().getDataSpacePortTypePort(address);
	}

	@Override
	public StatisticalManagerDefaultDataSpace newProxy(
			ProxyDelegate<DataSpaceStub> delegate) {
		return new StatisticalManagerDefaultDataSpace(delegate);
	}

}
