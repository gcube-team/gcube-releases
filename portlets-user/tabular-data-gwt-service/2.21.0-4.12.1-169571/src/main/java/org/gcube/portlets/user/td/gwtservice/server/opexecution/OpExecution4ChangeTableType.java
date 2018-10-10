package org.gcube.portlets.user.td.gwtservice.server.opexecution;

import java.util.HashMap;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.portlets.user.td.gwtservice.server.trservice.OperationDefinitionMap;
import org.gcube.portlets.user.td.gwtservice.shared.Constants;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.table.ChangeTableTypeSession;
import org.gcube.portlets.user.td.widgetcommonevent.shared.operations.OperationsId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operation Execution for change table type
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class OpExecution4ChangeTableType extends OpExecutionBuilder {
	private static Logger logger = LoggerFactory
			.getLogger(OpExecution4ChangeTableType.class);

	private TabularDataService service;
	private ChangeTableTypeSession changeTableTypeSession;

	public OpExecution4ChangeTableType(
			TabularDataService service,
			ChangeTableTypeSession changeTableTypeSession) {
		this.service = service;
		this.changeTableTypeSession = changeTableTypeSession;
	}

	@Override
	public void buildOpEx() throws TDGWTServiceException {
		OperationExecution invocation = null;

		logger.debug(changeTableTypeSession.toString());
		OperationDefinition operationDefinition;
		Map<String, Object> map = new HashMap<String, Object>();

		operationDefinition = OperationDefinitionMap.map(
				OperationsId.ChangeTableType.toString(), service);
		map.put(Constants.PARAMETER_TABLE_TYPE, changeTableTypeSession
				.getTableType().toString());

		invocation = new OperationExecution(
				operationDefinition.getOperationId(), map);

		
		operationExecutionSpec.setOp(invocation);

	}

}
