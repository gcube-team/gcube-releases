package org.gcube.data.analysis.tabulardata.clientlibrary.proxy;

import static org.gcube.common.clients.exceptions.FaultDSL.again;

import java.util.List;

import org.gcube.common.calls.jaxws.JAXWSUtils.Empty;
import org.gcube.common.clients.Call;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.data.analysis.tabulardata.commons.utils.SharingEntity;
import org.gcube.data.analysis.tabulardata.commons.webservice.TabularResourceManager;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.InternalSecurityException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TabularResource;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TabularResourceType;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.notifications.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultTabularResourceManagerProxy implements TabularResourceManagerProxy {

	private static Logger logger = LoggerFactory.getLogger(DefaultTabularResourceManagerProxy.class);
	
	private final ProxyDelegate<TabularResourceManager> delegate;
		
	public DefaultTabularResourceManagerProxy(ProxyDelegate<TabularResourceManager> config){
		this.delegate = config;
	}

	@Override
	public TabularResource createTabularResource(final TabularResourceType type) {
		Call<TabularResourceManager, TabularResource> call = new Call<TabularResourceManager, TabularResource>() {

			@Override
			public TabularResource call(TabularResourceManager endpoint) throws Exception {
				return endpoint.createTabularResource(type);
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

	@Override
	public void remove(final long tabularResourceId) throws NoSuchTabularResourceException {
		Call<TabularResourceManager, Empty> call = new Call<TabularResourceManager, Empty>() {

			@Override
			public Empty call(TabularResourceManager endpoint) throws Exception {
				endpoint.remove(tabularResourceId);
				return new Empty();
			}
		};
		try {
			delegate.make(call);
		}catch (NoSuchTabularResourceException e) {
			logger.error("no tabular resource found with id {}",tabularResourceId);
			throw e;
		}catch (InternalSecurityException e) {
			throw new SecurityException(e);
		}catch (Exception e) {
			throw again(e).asServiceException();
		}
	}

/*	@Override
	public TabularResource cloneTabularResource(final long tabularResourceId)
			throws NoSuchTabularResourceException {
		Call<TabularResourceManager, TabularResource> call = new Call<TabularResourceManager, TabularResource>() {

			@Override
			public TabularResource call(TabularResourceManager endpoint) throws Exception {
				return endpoint.cloneTabularResource(tabularResourceId);
			}
		};
		try {
			return delegate.make(call);
		}catch (InternalSecurityException e) {
			throw new SecurityException(e);
		}catch (NoSuchTabularResourceException e) {
			logger.error("no tabular resource found with id {}",tabularResourceId);
			throw e;
		}catch (Exception e) {
			throw again(e).asServiceException();
		}
	}*/

	@Override
	public List<TabularResource> getAllTabularResources() {
		Call<TabularResourceManager, List<TabularResource>> call = new Call<TabularResourceManager, List<TabularResource>>() {

			@Override
			public List<TabularResource> call(TabularResourceManager endpoint) throws Exception {
				return endpoint.getAllTabularResources();
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

	@Override
	public TabularResource updateTabularResource(final TabularResource tabularResource)
			throws NoSuchTabularResourceException{
		Call<TabularResourceManager, TabularResource> call = new Call<TabularResourceManager, TabularResource>() {

			@Override
			public TabularResource call(TabularResourceManager endpoint) throws Exception {
				return endpoint.updateTabularResource(tabularResource);
			}
		};
		try {
			return delegate.make(call);
		}catch (InternalSecurityException e) {
			throw new SecurityException(e);
		}catch (NoSuchTabularResourceException st) {
			throw st;
		}catch (Exception e) {
			throw again(e).asServiceException();
		}
		
	}

	@Override
	public TabularResource getTabularResource(final long id)
			throws NoSuchTabularResourceException {
		Call<TabularResourceManager, TabularResource> call = new Call<TabularResourceManager, TabularResource>() {

			@Override
			public TabularResource call(TabularResourceManager endpoint) throws Exception {
				return endpoint.getTabularResource(id);
			}
		};
		try {
			return delegate.make(call);
		}catch (NoSuchTabularResourceException e) {
			logger.error("no tabular resource found with id {}",id);
			throw e;
		}catch (Exception e) {
			throw again(e).asServiceException();
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.td.commons.webservice.TabularResourceManager#share(org.gcube.data.td.commons.utils.AuthorizationToken[])
	 */
	@Override
	public TabularResource share(final Long tabularResourceId, final SharingEntity... entities)
			throws NoSuchTabularResourceException {
		Call<TabularResourceManager, TabularResource> call = new Call<TabularResourceManager, TabularResource>() {

			@Override
			public TabularResource call(TabularResourceManager endpoint) throws Exception {
				return endpoint.share(tabularResourceId, entities);
			}
		};
		try {
			return delegate.make(call);
		}catch (InternalSecurityException se) {
			throw new SecurityException(se);
		}catch (NoSuchTabularResourceException nte) {
			throw nte;
		}catch (Exception e) {
			throw again(e).asServiceException();
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.td.commons.webservice.TabularResourceManager#unshare(org.gcube.data.td.commons.utils.AuthorizationToken[])
	 */
	@Override
	public TabularResource unshare(final Long tabularResourceId, final SharingEntity... entities)
			throws NoSuchTabularResourceException {
		Call<TabularResourceManager, TabularResource> call = new Call<TabularResourceManager, TabularResource>() {

			@Override
			public TabularResource call(TabularResourceManager endpoint) throws Exception {
				return endpoint.unshare(tabularResourceId, entities);
			}
		};
		try {
			return delegate.make(call);
		}catch (NoSuchTabularResourceException nte) {
			throw nte;
		}catch (InternalSecurityException e) {
			throw new SecurityException(e);
		}catch (Exception e) {
			throw again(e).asServiceException();
		}
	}

	@Override
	public List<TabularResource> getTabularResourcesByType(final String type) {
		Call<TabularResourceManager, List<TabularResource>> call = new Call<TabularResourceManager, List<TabularResource>>() {

			@Override
			public List<TabularResource> call(TabularResourceManager endpoint) throws Exception {
				return endpoint.getTabularResourcesByType(type);
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

	@Override
	public List<Notification> getNotificationPerTabularResource(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Notification> getNotificationPerUser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void cleanDatabase() {
		Call<TabularResourceManager, Empty> call = new Call<TabularResourceManager, Empty>() {

			@Override
			public Empty call(TabularResourceManager endpoint) throws Exception {
				endpoint.cleanDatabase();
				return new Empty();
			}
		};
		try {
			delegate.make(call);
		}catch (InternalSecurityException e) {
			throw new SecurityException(e);
		}catch (Exception e) {
			throw again(e).asServiceException();
		}
	}
		
}
