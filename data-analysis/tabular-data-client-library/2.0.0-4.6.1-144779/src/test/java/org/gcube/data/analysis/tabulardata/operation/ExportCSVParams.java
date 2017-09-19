package org.gcube.data.analysis.tabulardata.operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.table.Table;


public class ExportCSVParams extends ParameterRetriever {
	
	public static final String ENCODING = "encoding";
	public static final String HASHEADER = "hasHeader";
	public static final String SEPARATOR = "separator";
	public static final String COLUMNS = "columns";
	
		
	private long operationId = 101l; 
	
	
	@Override
	public long getOperationId() {
		return operationId;
	}

	@Override
	public Map<String, Object> getParameter(Object ... obj) {
		
		if (obj.length==0) throw new IllegalArgumentException("export need a table"); 
		
		Table table = (Table) obj[0];
		
		Map<String, Object> instances = new HashMap<String, Object>();
		instances.put(ENCODING, "UTF8");
		instances.put(SEPARATOR, ";");
		
		List<String> columns = new ArrayList<String>();
		for(Column column : table.getColumns())
			if (!(column.getColumnType() instanceof IdColumnType))
				columns.add(column.getName());
		
		instances.put(COLUMNS, Arrays.asList(columns.toArray(new String[columns.size()])));
		
		return instances;
	}

	@Override
	public boolean verifyTable(Table lastTable) {
		return lastTable!=null;
	}

	@Override
	public OperationExecution getInvocation(Map<String, Object> parameters, Object... objs) throws Exception {
		if (objs.length==0) throw new IllegalArgumentException("export need a table"); 
		return new OperationExecution(operationId, parameters);
	}

	
}
