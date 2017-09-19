package org.gcube.portlets.user.td.gwtservice.server.opexecution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.portlets.user.td.gwtservice.server.trservice.OperationDefinitionMap;
import org.gcube.portlets.user.td.gwtservice.shared.Constants;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.rows.DeleteRowsSession;
import org.gcube.portlets.user.td.widgetcommonevent.shared.operations.OperationsId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operation Execution for delete rows
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class OpExecution4DeleteRows extends OpExecutionBuilder {
	private static Logger logger = LoggerFactory
			.getLogger(OpExecution4DeleteRows.class);

	private TabularDataService service;
	private DeleteRowsSession deleteRowsSession;

	public OpExecution4DeleteRows(TabularDataService service,
			DeleteRowsSession deleteRowsSession) {
		this.service = service;
		this.deleteRowsSession = deleteRowsSession;
	}

	@Override
	public void buildOpEx() throws TDGWTServiceException {
		OperationExecution invocation = null;

		logger.debug(deleteRowsSession.toString());
		OperationDefinition operationDefinition;
		Map<String, Object> map = new HashMap<String, Object>();

		ArrayList<String> rows = deleteRowsSession.getRows();
		ArrayList<Integer> rowsInt = new ArrayList<Integer>();
		for (String r : rows) {
			try {
				Integer row = new Integer(r);
				rowsInt.add(row);
			} catch (NumberFormatException e) {
				logger.error("Row:" + r + " is invalid Integer");
			}
		}
		operationDefinition = OperationDefinitionMap.map(
				OperationsId.RemoveRowById.toString(), service);
		map.put(Constants.PARAMETER_ROW_ID, rowsInt);

		invocation = new OperationExecution(
				operationDefinition.getOperationId(), map);


		operationExecutionSpec.setOp(invocation);

	}

}
