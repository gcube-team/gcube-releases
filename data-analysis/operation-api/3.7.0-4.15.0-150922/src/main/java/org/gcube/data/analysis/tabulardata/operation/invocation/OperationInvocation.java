package org.gcube.data.analysis.tabulardata.operation.invocation;

import java.util.Map;

import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.OperationDescriptor;

public interface OperationInvocation {

	public TableId getTargetTableId();

	public ColumnLocalId getTargetColumnId();

	public OperationDescriptor getOperationDescriptor();

	public Map<String, Object> getParameterInstances();

}