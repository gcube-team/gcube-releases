package org.gcube.data.analysis.tabulardata.operation;

import static org.gcube.data.analysis.tabulardata.clientlibrary.plugin.AbstractPlugin.operation;

import java.util.Map;

import org.gcube.data.analysis.tabulardata.clientlibrary.proxy.OperationManagerProxy;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.model.table.Table;

public abstract class ParameterRetriever {

	public abstract Map<String, Object> getParameter(Object ... objs);

	protected abstract long getOperationId();
	
	public abstract boolean verifyTable(Table lastTable);
	
	public abstract OperationExecution getInvocation(Map<String, Object> parameters, Object ... objs) throws Exception;
	
	public OperationDefinition getDescriptor() throws Exception{
		OperationManagerProxy operationProxy = operation().build();
		return operationProxy.getCapabilities(this.getOperationId());
	}
}
