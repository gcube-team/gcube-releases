package org.gcube.data.analysis.tabulardata.clientlibrary.proxy;

import static org.gcube.common.clients.exceptions.FaultDSL.again;

import java.util.List;

import org.gcube.common.clients.Call;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.data.analysis.tabulardata.commons.webservice.ExternalResourceManager;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.InternalSecurityException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.resources.ResourceDescriptor;
import org.gcube.data.analysis.tabulardata.model.resources.ResourceType;

public class DefaultExternalResourceManagerProxy implements ExternalResourceManagerProxy {


	//private static Logger logger = LoggerFactory.getLogger(DefaultExternalResourceManagerProxy.class);

	private final ProxyDelegate<ExternalResourceManager> delegate;

	public DefaultExternalResourceManagerProxy(ProxyDelegate<ExternalResourceManager> config){
		this.delegate = config;
	}

	@Override
	public List<ResourceDescriptor> getResourcePerTabularResource(
			final long tabularResourceId) throws NoSuchTabularResourceException {
		Call<ExternalResourceManager, List<ResourceDescriptor>> call = new Call<ExternalResourceManager, List<ResourceDescriptor>>() {
			@Override
			public List<ResourceDescriptor> call(ExternalResourceManager endpoint) throws Exception {
				return endpoint.getResourcePerTabularResource(tabularResourceId);
			}
		};
		try {
			return delegate.make(call);
		}catch (InternalSecurityException e) {
			throw new SecurityException(e);
		}catch (NoSuchTabularResourceException nte) {
			throw nte;
		}catch (Exception e) {
			throw again(e).asServiceException();
		}		

	}

	@Override
	public List<ResourceDescriptor> getResourcePerTabularResourceAndType(
			final long tabularResourceId, final ResourceType type)
					throws NoSuchTabularResourceException {
		Call<ExternalResourceManager, List<ResourceDescriptor>> call = new Call<ExternalResourceManager, List<ResourceDescriptor>>() {
			@Override
			public List<ResourceDescriptor> call(ExternalResourceManager endpoint) throws Exception {
				return endpoint.getResourcePerTabularResourceAndType(tabularResourceId, type);
			}
		};
		try {
			return delegate.make(call);
		}catch (InternalSecurityException e) {
			throw new SecurityException(e);
		}catch (NoSuchTabularResourceException nte) {
			throw nte;
		}catch (Exception e) {
			throw again(e).asServiceException();
		}		
	}

	@Override
	public ResourceDescriptor removeResource(final long resourceId){
		Call<ExternalResourceManager, ResourceDescriptor> call = new Call<ExternalResourceManager, ResourceDescriptor>() {
			@Override
			public ResourceDescriptor call(ExternalResourceManager endpoint) throws Exception {
				return endpoint.removeResource(resourceId);
			}
		};
		try {
			return delegate.make(call);
		}catch (InternalSecurityException e) {
			throw new SecurityException(e);
		}catch (Exception e) {
			throw again(e).asServiceException();
		}		
	}	


}
