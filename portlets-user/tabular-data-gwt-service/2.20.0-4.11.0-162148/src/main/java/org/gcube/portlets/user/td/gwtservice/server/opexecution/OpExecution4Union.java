package org.gcube.portlets.user.td.gwtservice.server.opexecution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.portlets.user.td.gwtservice.server.trservice.ColumnMap;
import org.gcube.portlets.user.td.gwtservice.server.trservice.OperationDefinitionMap;
import org.gcube.portlets.user.td.gwtservice.shared.Constants;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.union.UnionSession;
import org.gcube.portlets.user.td.widgetcommonevent.shared.operations.OperationsId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operation Execution for union
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class OpExecution4Union extends OpExecutionBuilder {
	private static Logger logger = LoggerFactory
			.getLogger(OpExecution4Union.class);

	private TabularDataService service;
	private UnionSession unionSession;

	public OpExecution4Union(
			TabularDataService service,
			UnionSession unionSession) {
		this.service = service;
		this.unionSession = unionSession;
	}

	@Override
	public void buildOpEx() throws TDGWTServiceException {
		OperationExecution invocation = null;

		logger.debug(unionSession.toString());
		OperationDefinition operationDefinition;
		Map<String, Object> map = new HashMap<String, Object>();

		operationDefinition = OperationDefinitionMap.map(
				OperationsId.Union.toString(), service);
		
		ColumnMap columnMap = new ColumnMap();
		ArrayList<Map<String, Object>> compositeColumnMap = columnMap
				.genColumnMap(unionSession);
		
		map.put(Constants.PARAMETER_UNION_COMPOSITE, compositeColumnMap);

		invocation = new OperationExecution(
				operationDefinition.getOperationId(), map);

		
		operationExecutionSpec.setOp(invocation);

	}

}
