package org.gcube.data.analysis.tabulardata.service.impl.tabular;

import static org.gcube.data.analysis.tabulardata.clientlibrary.plugin.AbstractPlugin.externalResource;
import static org.gcube.data.analysis.tabulardata.clientlibrary.plugin.AbstractPlugin.history;
import static org.gcube.data.analysis.tabulardata.clientlibrary.plugin.AbstractPlugin.tabularResource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gcube.data.analysis.tabulardata.clientlibrary.proxy.ExternalResourceManagerProxy;
import org.gcube.data.analysis.tabulardata.clientlibrary.proxy.HistoryManagerProxy;
import org.gcube.data.analysis.tabulardata.clientlibrary.proxy.TabularResourceManagerProxy;
import org.gcube.data.analysis.tabulardata.commons.utils.AuthorizationToken;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.InternalSecurityException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTableException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TabularResourceType;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.resources.ResourceDescriptor;
import org.gcube.data.analysis.tabulardata.model.resources.ResourceType;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResource;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceId;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TabularResourceImpl implements TabularResourceInterface {

	private static Logger logger = LoggerFactory.getLogger(TabularResourceImpl.class);

	private volatile HistoryManagerProxy historyManager = history().build();
	private volatile TabularResourceManagerProxy tabularResourceProxy = tabularResource().build();
	private volatile ExternalResourceManagerProxy externalResourceProxy = externalResource().build();

	public List<TabularResource> getTabularResources() {
		List<org.gcube.data.analysis.tabulardata.commons.webservice.types.TabularResource> remoteResources = tabularResourceProxy.getAllTabularResources();
		Iterator<org.gcube.data.analysis.tabulardata.commons.webservice.types.TabularResource> it =  remoteResources.iterator();
		List<TabularResource> tabularResources = new ArrayList<TabularResource>(remoteResources.size());
		while(it.hasNext())
			tabularResources.add(new TabularResourceObject(it.next()));

		return tabularResources;
	}

	public TabularResource getTabularResource(TabularResourceId id)
			throws NoSuchTabularResourceException {

		return new TabularResourceObject(tabularResourceProxy.getTabularResource(id.getValue()));

	}

	public void removeTabularResource(TabularResourceId id)
			throws NoSuchTabularResourceException {
		tabularResourceProxy.remove(id.getValue());
	}

	/*
	public TabularResource cloneTabularResource(TabularResourceId id)
			throws NoSuchTabularResourceException {

		try {
			return new TabularResourceObject(tabularResourceProxy.cloneTabularResource(id.getValue()));
		} catch (org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException e) {
			throw new NoSuchTabularResourceException(id);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}*/

	public TabularResource createTabularResource() {
		try {
			org.gcube.data.analysis.tabulardata.commons.webservice.types.TabularResource tabularResource = tabularResourceProxy.createTabularResource(TabularResourceType.STANDARD);
			return new TabularResourceObject(tabularResource);
		} catch (Exception e) {
			logger.error("error creating tabule resource on the service",e);
			throw new RuntimeException(e);
		}
	}

	public TabularResource createFlow() {
		try {
			org.gcube.data.analysis.tabulardata.commons.webservice.types.TabularResource tabularResource = tabularResourceProxy.createTabularResource(TabularResourceType.FLOW);
			return new TabularResourceObject(tabularResource);
		} catch (Exception e) {
			logger.error("error creating tabule resource on the service",e);
			throw new RuntimeException(e);
		}
	}

	public Table getLastTable(TabularResourceId id)
			throws NoSuchTabularResourceException, NoSuchTableException {
		return historyManager.getLastTable(id.getValue());

	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceInterface#share(java.lang.String[])
	 */
	public TabularResource share(TabularResourceId tabularResourceId, AuthorizationToken... tokens) throws NoSuchTabularResourceException, SecurityException {
		try {
			if (tokens==null || tokens.length ==0) throw new RuntimeException("list of users is empty");		
			return new TabularResourceObject(tabularResourceProxy.share(tabularResourceId.getValue(), tokens));
		} catch (InternalSecurityException e) {
			throw new SecurityException(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceInterface#unshare(java.lang.String[])
	 */
	public TabularResource unshare(TabularResourceId tabularResourceId,AuthorizationToken... tokens) throws NoSuchTabularResourceException, SecurityException{
		try {
			if (tokens==null || tokens.length ==0) throw new RuntimeException("list of users is empty");
			return new TabularResourceObject(tabularResourceProxy.unshare(tabularResourceId.getValue(), tokens));
		} catch (InternalSecurityException e) {
			throw new SecurityException(e.getMessage());
		}
	}

	public List<TabularResource> getTabularResourcesByType(String type) {
		List<org.gcube.data.analysis.tabulardata.commons.webservice.types.TabularResource> remoteResources = tabularResourceProxy.getTabularResourcesByType(type);;
		Iterator<org.gcube.data.analysis.tabulardata.commons.webservice.types.TabularResource> it =  remoteResources.iterator();
		List<TabularResource> tabularResources = new ArrayList<TabularResource>(remoteResources.size());
		while(it.hasNext())
			tabularResources.add(new TabularResourceObject(it.next()));

		return tabularResources;
	}

	@Override
	public List<ResourceDescriptor> getResources(TabularResourceId id)
			throws NoSuchTabularResourceException {
		return externalResourceProxy.getResourcePerTabularResource(id.getValue());
	}

	@Override
	public List<ResourceDescriptor> getResourcesByType(TabularResourceId id, ResourceType type)
			throws NoSuchTabularResourceException {
		return externalResourceProxy.getResourcePerTabularResourceAndType(id.getValue(), type);
	}

	@Override
	public ResourceDescriptor removeResurce(long resourceId){
		return externalResourceProxy.removeResource(resourceId); 
	}
}
