
package org.gcube.data.analysis.tabulardata.operation;

import java.util.HashMap;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.model.table.Table;

public class ImportSDMXParams extends ParameterRetriever {


	public static final String AGENCY = "agency";
	public static final String VERSION = "version";
	public static final String REGISTRY = "registryBaseUrl";
	public static final String ID = "id";

	private long operationId = 200l; 

	@Override
	public Map<String, Object> getParameter(Object ... obj) {
		Map<String, Object> parameterInstances = new HashMap<String, Object>();
		parameterInstances.put(REGISTRY, "http://node8.d.d4science.research-infrastructures.eu:8080/FusionRegistry/ws/rest/");
		parameterInstances.put(VERSION, 1);
		parameterInstances.put(ID, "Degree");
		parameterInstances.put(AGENCY, "SDMX");
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
