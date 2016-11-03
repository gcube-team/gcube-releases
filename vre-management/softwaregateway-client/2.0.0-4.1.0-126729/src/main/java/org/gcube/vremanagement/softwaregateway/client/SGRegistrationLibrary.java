package org.gcube.vremanagement.softwaregateway.client;

import org.gcube.common.clients.Call;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.common.clients.exceptions.ServiceException;
import org.gcube.common.clients.stubs.jaxws.JAXWSUtils.Empty;
import org.gcube.vremanagement.softwaregateway.client.fws.SGRegistrationServiceJAXWSStubs;
import org.gcube.vremanagement.softwaregateway.client.fws.Types.LocationCoordinates;
import org.gcube.vremanagement.softwaregateway.client.interfaces.SGRegistrationInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * @author Roberto Cirillo (ISTI - CNR)
 *
 */
public class SGRegistrationLibrary implements SGRegistrationInterface{
	
	private final ProxyDelegate<SGRegistrationServiceJAXWSStubs> delegate;

	Logger logger = LoggerFactory.getLogger(this.getClass().toString());

	
	public SGRegistrationLibrary(ProxyDelegate<SGRegistrationServiceJAXWSStubs> delegate) {
		this.delegate=delegate;

	}

	@Override
	public String register(final String registerRequest){
		Call<SGRegistrationServiceJAXWSStubs,String> call = new Call<SGRegistrationServiceJAXWSStubs,String>() {
			@Override 
			public String call(SGRegistrationServiceJAXWSStubs endpoint) throws Exception {
				return  endpoint.register(registerRequest);

			}
		};
		try {
			return delegate.make(call);
		}

		catch(Exception e) {
//			throw again(e).as(InvalidScopeException.class, NoSuchReportException.class);
			throw new ServiceException (e);
		}
	}


	@Override
	public Empty unregister(final LocationCoordinates  coordinates){
		Call<SGRegistrationServiceJAXWSStubs,Empty> call = new Call<SGRegistrationServiceJAXWSStubs,Empty>() {
			@Override 
			public Empty call(SGRegistrationServiceJAXWSStubs endpoint) throws Exception {
				return  endpoint.unregister(coordinates);

			}
		};
		try {
			return delegate.make(call);
		}

		catch(Exception e) {
//			throw again(e).as(InvalidScopeException.class);
			throw new ServiceException (e);
		}
	}

}
