package org.gcube.vremanagement.resourcemanager.client;

import static org.gcube.common.clients.exceptions.FaultDSL.again;

import org.gcube.common.clients.Call;
import org.gcube.common.clients.delegates.AsyncProxyDelegate;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.common.clients.stubs.jaxws.JAXWSUtils.Empty;
import org.gcube.vremanagement.resourcemanager.client.exceptions.InvalidScopeException;
import org.gcube.vremanagement.resourcemanager.client.exceptions.NoSuchReportException;
import org.gcube.vremanagement.resourcemanager.client.fws.RMReportingServiceJAXWSStubs;
import org.gcube.vremanagement.resourcemanager.client.fws.Types.SendReportParameters;
import org.gcube.vremanagement.resourcemanager.client.interfaces.RMReportingInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * @author Andrea Manzi(CERN)
 *
 */
public class RMReportingLibrary implements RMReportingInterface{
	
	private final ProxyDelegate<RMReportingServiceJAXWSStubs> delegate;

	Logger logger = LoggerFactory.getLogger(this.getClass().toString());

	
	public RMReportingLibrary(ProxyDelegate<RMReportingServiceJAXWSStubs> delegate) {
		this.delegate=delegate;

	}


	@Override
	public Empty sendReport(final SendReportParameters params)
			throws InvalidScopeException {
		Call<RMReportingServiceJAXWSStubs,Empty> call = new Call<RMReportingServiceJAXWSStubs,Empty>() {
			@Override 
			public Empty call(RMReportingServiceJAXWSStubs endpoint) throws Exception {
				return  endpoint.SendReport(params);

			}
		};
		try {
			return delegate.make(call);
		}

		catch(Exception e) {
			throw again(e).as(InvalidScopeException.class);
		}
	}


	@Override
	public String getReport(final String reportId) throws InvalidScopeException,
			NoSuchReportException {
		Call<RMReportingServiceJAXWSStubs,String> call = new Call<RMReportingServiceJAXWSStubs,String>() {
			@Override 
			public String call(RMReportingServiceJAXWSStubs endpoint) throws Exception {
				return  endpoint.GetReport(reportId);

			}
		};
		try {
			return delegate.make(call);
		}

		catch(Exception e) {
			throw again(e).as(InvalidScopeException.class, NoSuchReportException.class);
		}
	}

}
