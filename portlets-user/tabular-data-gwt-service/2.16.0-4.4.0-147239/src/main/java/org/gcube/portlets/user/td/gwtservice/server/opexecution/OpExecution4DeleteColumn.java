package org.gcube.portlets.user.td.gwtservice.server.opexecution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.portlets.user.td.gwtservice.server.trservice.OperationDefinitionMap;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.DeleteColumnSession;
import org.gcube.portlets.user.td.widgetcommonevent.shared.operations.OperationsId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operation Execution for delete column
 * 
 * @author "Giancarlo Panichi" email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class OpExecution4DeleteColumn extends OpExecutionBuilder {
	private static Logger logger = LoggerFactory
			.getLogger(OpExecution4DeleteColumn.class);

	private TabularDataService service;
	private DeleteColumnSession deleteColumnSession;

	public OpExecution4DeleteColumn(TabularDataService service,
			DeleteColumnSession deleteColumnSession) {
		this.service = service;
		this.deleteColumnSession = deleteColumnSession;
	}

	@Override
	public void buildOpEx() throws TDGWTServiceException {
		logger.debug(deleteColumnSession.toString());

		OperationDefinition operationDefinition;
		operationDefinition = OperationDefinitionMap.map(
				OperationsId.RemoveColumn.toString(), service);
		Map<String, Object> map = new HashMap<String, Object>();

		ArrayList<OperationExecution> invocations = new ArrayList<OperationExecution>();
		for (ColumnData col : deleteColumnSession.getColumns()) {

			OperationExecution invocation = null;

			invocation = new OperationExecution(col.getColumnId(),
					operationDefinition.getOperationId(), map);
			invocations.add(invocation);
		}

		operationExecutionSpec.setOps(invocations);
	}

}
