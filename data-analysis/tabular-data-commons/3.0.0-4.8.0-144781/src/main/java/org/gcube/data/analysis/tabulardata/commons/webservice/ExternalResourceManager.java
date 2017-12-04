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
import org.gcube.data.analysis.tabulardata.commons.webservice.types.resources.ResourceDescriptor;
import org.gcube.data.analysis.tabulardata.model.resources.ResourceType;

@WebService(targetNamespace=Constants.EXT_RESOURCE_TNS)
@SOAPBinding(style = Style.DOCUMENT, use=Use.LITERAL)
public interface ExternalResourceManager {

	public static final String SERVICE_NAME = "externalresourcemanager";

	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	List<ResourceDescriptor> getResourcePerTabularResource(long tabularResourceId) throws NoSuchTabularResourceException, InternalSecurityException;

	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	List<ResourceDescriptor> getResourcePerTabularResourceAndType(long tabularResourceId, ResourceType type) throws NoSuchTabularResourceException, InternalSecurityException;

	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	ResourceDescriptor removeResource(long resourceId) throws InternalSecurityException;
}
