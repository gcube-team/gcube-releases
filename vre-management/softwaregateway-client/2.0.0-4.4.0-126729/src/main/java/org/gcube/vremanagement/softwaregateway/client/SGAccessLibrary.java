package org.gcube.vremanagement.softwaregateway.client;

import java.util.List;

import org.gcube.common.clients.Call;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.common.clients.exceptions.ServiceException;
import org.gcube.vremanagement.softwaregateway.client.fws.SGAccessServiceJAXWSStubs;
import org.gcube.vremanagement.softwaregateway.client.fws.Types.DependenciesCoordinates;
import org.gcube.vremanagement.softwaregateway.client.fws.Types.LocationItem;
import org.gcube.vremanagement.softwaregateway.client.fws.Types.PackageCoordinates;
import org.gcube.vremanagement.softwaregateway.client.fws.Types.PluginCoordinates;
import org.gcube.vremanagement.softwaregateway.client.fws.Types.SACoordinates;
import org.gcube.vremanagement.softwaregateway.client.fws.Types.ServiceCoordinates;
import org.gcube.vremanagement.softwaregateway.client.fws.Types.getPackagesResponse;
import org.gcube.vremanagement.softwaregateway.client.fws.Types.getPluginResponse;
import org.gcube.vremanagement.softwaregateway.client.interfaces.SGAccessInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Roberto Cirillo ( ISTI - CNR) 
 *
 */
public class SGAccessLibrary implements SGAccessInterface {
	
	private final ProxyDelegate<SGAccessServiceJAXWSStubs> delegate;

	Logger logger = LoggerFactory.getLogger(this.getClass().toString());

	
	public SGAccessLibrary(ProxyDelegate<SGAccessServiceJAXWSStubs> delegate) {
		this.delegate=delegate;

	}
	
	@Override
	public String getLocation(final PackageCoordinates packageCoordinates) {
		
		Call<SGAccessServiceJAXWSStubs,String> call = new Call<SGAccessServiceJAXWSStubs,String>() {
			@Override 
			public String call(SGAccessServiceJAXWSStubs endpoint) throws Exception {
				return  endpoint.getLocation(packageCoordinates);

			}
		};
		try {
			return delegate.make(call);
		}

		catch(Exception e) {
			throw new ServiceException (e);
		}
		
	}
	
	@Override
	public String getSALocation(final SACoordinates saCoordinates) {
		
		Call<SGAccessServiceJAXWSStubs,String> call = new Call<SGAccessServiceJAXWSStubs,String>() {
			@Override 
			public String call(SGAccessServiceJAXWSStubs endpoint) throws Exception {
				return  endpoint.getSALocation(saCoordinates);

			}
		};
		try {
			return delegate.make(call);
		}

		catch(Exception e) {
			throw new ServiceException (e);
		}
		
	}
	
	
	@Override
	public String getDependencies(final DependenciesCoordinates packageCoordinates) {
		
		Call<SGAccessServiceJAXWSStubs,String> call = new Call<SGAccessServiceJAXWSStubs,String>() {
			@Override 
			public String call(SGAccessServiceJAXWSStubs endpoint) throws Exception {
				return  endpoint.getDependencies(packageCoordinates);

			}
		};
		try {
			return delegate.make(call);
		}

		catch(Exception e) {
			throw new ServiceException (e);
		}
		
	}
	

	@Override
	public getPackagesResponse getPackages(final ServiceCoordinates serviceCoordinates) {
		
		Call<SGAccessServiceJAXWSStubs,getPackagesResponse> call = new Call<SGAccessServiceJAXWSStubs,getPackagesResponse>() {
			@Override 
			public getPackagesResponse call(SGAccessServiceJAXWSStubs endpoint) throws Exception {
				return  endpoint.getPackages(serviceCoordinates);

			}
		};
		try {
			return delegate.make(call);
		}

		catch(Exception e) {
			throw new ServiceException (e);
		}
		
	}
	

	@Override
	public getPluginResponse getPlugins(final PluginCoordinates serviceCoordinates) {
		
		Call<SGAccessServiceJAXWSStubs,getPluginResponse> call = new Call<SGAccessServiceJAXWSStubs,getPluginResponse>() {
			@Override 
			public getPluginResponse call(SGAccessServiceJAXWSStubs endpoint) throws Exception {
				return  endpoint.getPlugins(serviceCoordinates);

			}
		};
		try {
			return delegate.make(call);
		}

		catch(Exception e) {
			throw new ServiceException (e);
		}
		
	}

}
