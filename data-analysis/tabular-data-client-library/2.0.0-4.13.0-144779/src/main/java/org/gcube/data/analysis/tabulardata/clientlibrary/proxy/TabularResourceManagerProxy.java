package org.gcube.data.analysis.tabulardata.clientlibrary.proxy;

import java.util.List;

import org.gcube.data.analysis.tabulardata.commons.webservice.Sharable;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TabularResource;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TabularResourceType;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.notifications.Notification;

public interface TabularResourceManagerProxy extends Sharable<Long, TabularResource, NoSuchTabularResourceException> {

	TabularResource createTabularResource(TabularResourceType type);
		
	List<TabularResource> getAllTabularResources();
		
	List<TabularResource> getTabularResourcesByType(String type);
	
	void remove(long id) throws NoSuchTabularResourceException;
	
	TabularResource updateTabularResource(TabularResource tabularResource) throws NoSuchTabularResourceException;

	TabularResource getTabularResource(long id) throws NoSuchTabularResourceException;
	
	List<Notification> getNotificationPerTabularResource(long id);
		
	List<Notification> getNotificationPerUser();
	
	void cleanDatabase();
		
}
