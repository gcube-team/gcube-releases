package org.gcube.data.analysis.tabulardata.operation.sdmx.datastructuredefinition.resource;

import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.sdmx.datastructuredefinition.beans.SDMXDataBean;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ResourcesResult;

public interface SDMXDataStructureDefinitionResourceBuilder {

	public ResourcesResult buildResourceResult (OperationInvocation invocation, SDMXDataBean dataBean) throws WorkerException;
	
}
