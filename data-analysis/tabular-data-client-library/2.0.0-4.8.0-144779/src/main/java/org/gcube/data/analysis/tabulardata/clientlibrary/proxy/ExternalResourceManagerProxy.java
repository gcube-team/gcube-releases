package org.gcube.data.analysis.tabulardata.clientlibrary.proxy;

import java.util.List;

import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.resources.ResourceDescriptor;
import org.gcube.data.analysis.tabulardata.model.resources.ResourceType;

public interface ExternalResourceManagerProxy{


	List<ResourceDescriptor> getResourcePerTabularResource(
			long tabularResourceId) throws NoSuchTabularResourceException;

	List<ResourceDescriptor> getResourcePerTabularResourceAndType(
			long tabularResourceId, ResourceType type)
					throws NoSuchTabularResourceException;

	public ResourceDescriptor removeResource(long resourceId);


}
