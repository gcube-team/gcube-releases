package org.gcube.portlets.user.td.gwtservice.server.opexecution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.portlets.user.td.gwtservice.server.trservice.OperationDefinitionMap;
import org.gcube.portlets.user.td.gwtservice.shared.Constants;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.ChangeColumnsPositionSession;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.operations.OperationsId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operation Execution for change column position
 * 
 * @author "Giancarlo Panichi" email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class OpExecution4ChangeColumnsPosition extends OpExecutionBuilder {
	private static Logger logger = LoggerFactory
			.getLogger(OpExecution4ChangeColumnsPosition.class);

	private TabularDataService service;
	private ChangeColumnsPositionSession changeColumnsPositionSession;

	public OpExecution4ChangeColumnsPosition(TabularDataService service,
			ChangeColumnsPositionSession changeColumnsPositionSession) {
		this.service = service;
		this.changeColumnsPositionSession = changeColumnsPositionSession;
	}

	@Override
	public void buildOpEx() throws TDGWTServiceException {
		logger.debug("Change Position: " + changeColumnsPositionSession);

		OperationDefinition operationDefinition;
		operationDefinition = OperationDefinitionMap.map(
				OperationsId.ChangeColumnPosition.toString(), service);
		Map<String, Object> map = new HashMap<String, Object>();

		TRId trId = changeColumnsPositionSession.getTrId();
		logger.debug("trID: " + trId);
		if (trId == null) {
			logger.error("Error in change position: trId is null");
			throw new TDGWTServiceException("No tabular resource set");
		}

		long tabId;
		if (trId.isViewTable()) {
			tabId = new Long(trId.getReferenceTargetTableId());
		} else {
			tabId = new Long(trId.getTableId());

		}
		TableId tId = new TableId(tabId);

		if (changeColumnsPositionSession.getColumns() == null) {
			logger.error("Error in change position: no column set");
			throw new TDGWTServiceException("No column set");
		}
		
		if (changeColumnsPositionSession.getColumns().size() <=1) {
			logger.error("Error in change position: Columns <= 1");
			throw new TDGWTServiceException("There are not enough columns");
		}

		
		ArrayList<ColumnReference> columns = new ArrayList<ColumnReference>();
		for (ColumnData col : changeColumnsPositionSession.getColumns()) {
			ColumnLocalId columnId = new ColumnLocalId(col.getColumnId());
			ColumnReference columnReference = new ColumnReference(tId, columnId);

			columns.add(columnReference);
		}

		map.put(Constants.PARAMETER_CHANGE_COLUMN_POSITION_ORDER, columns);

		OperationExecution invocation = new OperationExecution(
				operationDefinition.getOperationId(), map);

		operationExecutionSpec.setOp(invocation);

	}
}
