package org.gcube.data.analysis.tabulardata.operation.sdmx.datastructuredefinition.resource;

import java.net.MalformedURLException;
import java.net.URL;

import org.gcube.data.analysis.tabulardata.model.resources.ResourceType;
import org.gcube.data.analysis.tabulardata.model.resources.SDMXResource;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.sdmx.WorkerUtils;
import org.gcube.data.analysis.tabulardata.operation.sdmx.datastructuredefinition.DataStructureDefinitionWorkerUtils;
import org.gcube.data.analysis.tabulardata.operation.sdmx.datastructuredefinition.beans.SDMXDataBean;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ResourcesResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.resources.ImmutableSDMXResource;

public class SDMXDataStructureDefinitionPublisherResourceBuilder implements SDMXDataStructureDefinitionResourceBuilder {

	@Override
	public ResourcesResult buildResourceResult(OperationInvocation invocation, SDMXDataBean dataBean) throws WorkerException 
	{
		String registryUrl = (String) invocation.getParameterInstances().get(DataStructureDefinitionWorkerUtils.REGISTRY_BASE_URL);
		String targetAgency = (String) invocation.getParameterInstances().get(DataStructureDefinitionWorkerUtils.AGENCY);
		String targetVersion = (String) invocation.getParameterInstances().get(DataStructureDefinitionWorkerUtils.VERSION);
		
		try
		{
			SDMXResource sdmxResource = new SDMXResource(new URL(WorkerUtils.getResourceURI(registryUrl)), dataBean.getDsd().getId(), targetVersion, targetAgency,  SDMXResource.TYPE.DATA_STRUCTURE);
			return new ResourcesResult(new ImmutableSDMXResource(sdmxResource, "Data Structure SDMX export" , 
					sdmxResource.toString(), ResourceType.SDMX));
		}
		 catch (MalformedURLException e) 
		 {
				throw new WorkerException(String.format("exported url %s not valid",registryUrl),e);
			
		 }
	}

}
