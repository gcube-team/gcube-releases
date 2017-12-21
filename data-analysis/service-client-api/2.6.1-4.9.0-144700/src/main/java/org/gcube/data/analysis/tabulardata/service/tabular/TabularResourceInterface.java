package org.gcube.data.analysis.tabulardata.service.tabular;

import java.util.List;

import org.gcube.data.analysis.tabulardata.commons.utils.SharingEntity;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTableException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.resources.ResourceDescriptor;
import org.gcube.data.analysis.tabulardata.model.resources.ResourceType;
import org.gcube.data.analysis.tabulardata.model.table.Table;

public interface TabularResourceInterface {
	
	public List<TabularResource> getTabularResources();
	
	public List<TabularResource> getTabularResourcesByType(String type);
	
	public TabularResource getTabularResource(TabularResourceId id) throws NoSuchTabularResourceException;
	
	public void removeTabularResource(TabularResourceId id) throws NoSuchTabularResourceException;
	
	public TabularResource createTabularResource();
	
	public TabularResource createFlow();
	
	public Table getLastTable(TabularResourceId id) throws NoSuchTabularResourceException, NoSuchTableException;
	
	public TabularResource share(TabularResourceId tabularResourceId, SharingEntity... entities) throws NoSuchTabularResourceException, SecurityException;
	
	public TabularResource unshare(TabularResourceId tabularResourceId, SharingEntity... entities) throws NoSuchTabularResourceException, SecurityException;

	public List<ResourceDescriptor> getResources(TabularResourceId id) throws NoSuchTabularResourceException;
	
	public List<ResourceDescriptor> getResourcesByType(TabularResourceId id, ResourceType type) throws NoSuchTabularResourceException;

	ResourceDescriptor removeResurce(long resourceId);
	
}
