package org.gcube.data.analysis.tabulardata.commons.webservice;


import java.util.List;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import org.gcube.data.analysis.tabulardata.commons.utils.Constants;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.InternalSecurityException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TabularResource;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TabularResourceType;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.notifications.Notification;

@WebService(targetNamespace=Constants.TABULAR_RESOURCE_TNS)
@SOAPBinding(style = Style.DOCUMENT, use=Use.LITERAL)
public interface TabularResourceManager extends Sharable<Long, TabularResource, NoSuchTabularResourceException>{

	public static final String SERVICE_NAME = "tabularresourcemanager";

	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	TabularResource createTabularResource(TabularResourceType tabularResourceType) throws InternalSecurityException ;

	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	List<TabularResource> getAllTabularResources() throws InternalSecurityException;

	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	List<TabularResource> getTabularResourcesByType(String type) throws InternalSecurityException;

	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	void remove(long id) throws NoSuchTabularResourceException, InternalSecurityException;

	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	TabularResource updateTabularResource(TabularResource tabularResource) throws NoSuchTabularResourceException, InternalSecurityException;

	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	TabularResource getTabularResource(long id) throws NoSuchTabularResourceException, InternalSecurityException;

	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	List<Notification> getNotificationPerTabularResource(long id) throws InternalSecurityException;

	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	List<Notification> getNotificationPerUser() throws InternalSecurityException;

	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	void cleanDatabase();
}
