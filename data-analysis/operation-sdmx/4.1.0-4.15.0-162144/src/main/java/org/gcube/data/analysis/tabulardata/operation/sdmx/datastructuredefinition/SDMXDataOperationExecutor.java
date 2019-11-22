package org.gcube.data.analysis.tabulardata.operation.sdmx.datastructuredefinition;

import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.sdmx.datastructuredefinition.beans.SDMXDataBean;
import org.gcube.data.analysis.tabulardata.operation.sdmx.datastructuredefinition.beans.SDMXDataResultBean;

public interface SDMXDataOperationExecutor {

	public String getOperationName ();
	
	public boolean isPrimaryOperation ();
	
	public SDMXDataResultBean executeOperation (SDMXDataBean inputData, OperationInvocation invocation);
	
	public boolean isDataAware ();
}
