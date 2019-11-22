package org.gcube.vremanagement.vremodel.cl.proxy;

import java.util.Collections;
import java.util.List;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import org.gcube.common.clients.Call;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.common.clients.stubs.jaxws.JAXWSUtils.Empty;
import org.gcube.vremanagement.vremodel.cl.stubs.FactoryStub;
import org.gcube.vremanagement.vremodel.cl.stubs.types.ExistingNames;
import org.gcube.vremanagement.vremodel.cl.stubs.types.Report;
import org.gcube.vremanagement.vremodel.cl.stubs.types.ReportList;
import static org.gcube.common.clients.exceptions.FaultDSL.again;
import static org.gcube.vremanagement.vremodel.cl.Constants.EMPTY_VALUE;

public class DefaultFactory implements Factory {


	private final ProxyDelegate<FactoryStub> delegate;

	public DefaultFactory(ProxyDelegate<FactoryStub> config){
		this.delegate = config;
	}

	@Override
	public W3CEndpointReference createResource() {
		Call<FactoryStub, W3CEndpointReference> call =  new Call<FactoryStub, W3CEndpointReference>(){
			@Override
			public W3CEndpointReference call(FactoryStub endpoint)
					throws Exception {
				return endpoint.createResource(EMPTY_VALUE);
			}
		};

		try {
			return delegate.make(call);
		} catch (Exception e) {
			throw again(e).asServiceException();
		}
	}

	@Override
	public List<Report> getAllVREs() {
		Call<FactoryStub, ReportList> call =  new Call<FactoryStub, ReportList>(){
			@Override
			public ReportList call(FactoryStub endpoint)
					throws Exception {
				return endpoint.getAllVREs(EMPTY_VALUE);
			}
		};

		try {
			ReportList reportList = delegate.make(call);
			if (reportList.reports()!=null)
				return reportList.reports();
			else return Collections.emptyList();
		} catch (Exception e) {
			throw again(e).asServiceException();
		}
	}

	@Override
	public List<String> getExistingNamesVREs() {
		Call<FactoryStub, ExistingNames> call =  new Call<FactoryStub, ExistingNames>(){
			@Override
			public ExistingNames call(FactoryStub endpoint)
					throws Exception {
				return endpoint.getExistingNamesVREs(EMPTY_VALUE);
			}
		};

		try {
			ExistingNames names = delegate.make(call);
			if (names.names()!=null)
				return names.names();
			else return Collections.emptyList();
		} catch (Exception e) {
			throw again(e).asServiceException();
		}
	}

	@Override
	public void removeVRE(final String id) {
		Call<FactoryStub, Empty> call =  new Call<FactoryStub, Empty>(){
			@Override
			public Empty call(FactoryStub endpoint)
					throws Exception {
				endpoint.removeVRE(id);
				return EMPTY_VALUE;
			}
		};
		try {
			delegate.make(call);
		} catch (Exception e) {
			throw again(e).asServiceException();
		}

	}

	@Override
	public void initDB() {
		Call<FactoryStub, Empty> call =  new Call<FactoryStub, Empty>(){
			@Override
			public Empty call(FactoryStub endpoint)
					throws Exception {
				endpoint.initDB(EMPTY_VALUE);
				return EMPTY_VALUE;
			}
		};
		try {
			delegate.make(call);
		} catch (Exception e) {
			throw again(e).asServiceException();
		}

	}

	@Override
	public W3CEndpointReference getEPRbyId(final String id) {
		Call<FactoryStub, W3CEndpointReference> call =  new Call<FactoryStub, W3CEndpointReference>(){
			@Override
			public W3CEndpointReference call(FactoryStub endpoint)
					throws Exception {
				return endpoint.getEPRbyId(id);
			}
		};
		try {
			return delegate.make(call);
		} catch (Exception e) {
			throw again(e).asServiceException();
		}
	}


}
