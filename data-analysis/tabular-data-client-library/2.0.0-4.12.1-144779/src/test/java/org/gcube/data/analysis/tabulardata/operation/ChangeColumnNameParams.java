package org.gcube.data.analysis.tabulardata.operation;

import java.util.Collections;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.table.Table;

public class ChangeColumnNameParams extends ParameterRetriever {

	@Override
	public Map<String, Object> getParameter(Object... objs) {
		return Collections.singletonMap("NAME_PARAMETER_ID", (Object)new ImmutableLocalizedText("newColumnName"));
	}

	@Override
	protected long getOperationId() {
		return 710044479;
	}

	@Override
	public boolean verifyTable(Table lastTable) {
		ColumnLocalId localId = null;
		for (Column column : lastTable.getColumns())
			if (!(column.getColumnType() instanceof IdColumnType)){
				localId = column.getLocalId();
				break;
			}

		if (localId==null) 
			return false;
		else return true;
	}

	@Override
	public OperationExecution getInvocation(Map<String, Object> parameters,
			Object... objs) throws Exception {
		if (objs.length==0) throw new IllegalArgumentException("export need a table"); 
		Table table = (Table) objs[0];
		
		ColumnLocalId localId = null;
		for (Column column : table.getColumns())
			if (!(column.getColumnType() instanceof IdColumnType)){
				localId = column.getLocalId();
				break;
			}
		
		if (localId==null) throw new IllegalArgumentException("no valid columns are present on selected table");  
		
		return new OperationExecution(localId.getValue(), getOperationId(), parameters);
	}

}
