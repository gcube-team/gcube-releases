package org.gcube.data.analysis.tabulardata.operation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.model.table.Table;

public class ImportCSVParams extends ParameterRetriever {

	
	public static final String ENCODING = "encoding";
	public static final String HASHEADER = "hasHeader";
	public static final String SEPARATOR = "separator";
	public static final String URL = "url";
	
	private long operationId = 100l; 
	
	@Override
	public Map<String, Object> getParameter(Object ... obj) {
		Map<String, Object> parameterInstances = new HashMap<String, Object>();
		parameterInstances.put(URL, "https://dl.dropboxusercontent.com/u/27316518/2006.csv");
		parameterInstances.put(SEPARATOR, ",");
		parameterInstances.put(ENCODING, "UTF-8");
		parameterInstances.put(HASHEADER, true);
		parameterInstances.put("fieldMask", Arrays.asList(true, true, false, false, false, true, true));
		parameterInstances.put("skipError",  true);
		return parameterInstances;	
	}

	@Override
	public long getOperationId() {
		return operationId;
	}

	@Override
	public boolean verifyTable(Table lastTable) {
		return lastTable==null;
	}

	@Override
	public OperationExecution getInvocation(Map<String, Object> parameters,Object... objs) throws Exception {
		return new OperationExecution(operationId, parameters);
	}

}
