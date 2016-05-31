package org.gcube.data.analysis.tabulardata.operation;

import java.util.List;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;

public interface OperationDescriptor {

	public OperationId getOperationId();

	public String getName();

	public String getDescription();

	public OperationScope getScope();

	public OperationType getType();
	
	public List<Parameter> getParameters();

}