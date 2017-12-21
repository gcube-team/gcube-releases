package org.gcube.data.analysis.tabulardata.operation.sdmx.datastructuredefinition.resource;

import java.net.URI;
import java.net.URISyntaxException;

import org.gcube.data.analysis.tabulardata.model.resources.InternalURI;
import org.gcube.data.analysis.tabulardata.model.resources.ResourceType;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.sdmx.datastructuredefinition.beans.SDMXDataBean;
import org.gcube.data.analysis.tabulardata.operation.sdmx.template.TemplateWorkerUtils;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ResourcesResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.resources.ImmutableURIResult;

public class SDMXDataStructureDefinitionExcelResourceBuilder implements SDMXDataStructureDefinitionResourceBuilder {

	
	@Override
	public ResourcesResult buildResourceResult(OperationInvocation invocation, SDMXDataBean dataBean) throws WorkerException 
	{
		
		String uri = "/"+TemplateWorkerUtils.DEFAULT_EXCEL_FOLDER + "/"+dataBean.getID();

	
		InternalURI internalUri;
		try {
			internalUri = new InternalURI(new URI(uri),"application/xls");
			return new ResourcesResult(new ImmutableURIResult(internalUri, dataBean.getID() ,"SDMX Data Structure exported in Excel", ResourceType.SDMX));

		} catch (URISyntaxException e) {
			throw new WorkerException("Unable to create stored resource", e);
		}
		

	}

}
