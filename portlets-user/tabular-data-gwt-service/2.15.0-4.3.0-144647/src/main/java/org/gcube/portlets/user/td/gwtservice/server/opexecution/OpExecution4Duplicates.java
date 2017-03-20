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
import org.gcube.portlets.user.td.gwtservice.shared.tr.rows.DuplicatesSession;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.operations.OperationsId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operation Execution for duplicates
 * 
 * @author "Giancarlo Panichi" email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class OpExecution4Duplicates extends OpExecutionBuilder {
	private static Logger logger = LoggerFactory
			.getLogger(OpExecution4Duplicates.class);

	private TabularDataService service;
	private DuplicatesSession duplicatesSession;

	public OpExecution4Duplicates(TabularDataService service,
			DuplicatesSession duplicatesSession) {
		this.service = service;
		this.duplicatesSession = duplicatesSession;
	}

	@Override
	public void buildOpEx() throws TDGWTServiceException {
		OperationExecution invocation = null;

		logger.debug(duplicatesSession.toString());
		OperationDefinition operationDefinition;
		Map<String, Object> map = new HashMap<String, Object>();

		ArrayList<ColumnData> cols = duplicatesSession.getColumns();
		logger.debug("ReferenceColumn To Set: " + cols);
		ArrayList<ColumnReference> columnReferences = new ArrayList<ColumnReference>();

		for (ColumnData col : cols) {
			ColumnLocalId cId = new ColumnLocalId(col.getColumnId());
			TRId trId = col.getTrId();
			logger.debug("trID: " + trId);
			long tabId;
			
			if(trId.isViewTable()){
				tabId = new Long(trId.getReferenceTargetTableId());
			} else {
				tabId = new Long(trId.getTableId());
				
			}
			
			TableId tId = new TableId(tabId);
			ColumnReference columnReference = new ColumnReference(tId, cId);
			columnReferences.add(columnReference);
		}

		switch (duplicatesSession.getDuplicateOp()) {
		case VALIDATE:
			operationDefinition = OperationDefinitionMap.map(
					OperationsId.DuplicateTupleValidation.toString(), service);

			map.put(Constants.PARAMETER_KEY, columnReferences);

			invocation = new OperationExecution(
					operationDefinition.getOperationId(), map);

			break;
		case DELETE:
			operationDefinition = OperationDefinitionMap.map(
					OperationsId.RemoveDuplicateTuples.toString(), service);

			map.put(Constants.PARAMETER_KEY, columnReferences);

			invocation = new OperationExecution(
					operationDefinition.getOperationId(), map);

			break;
		default:
			break;

		}

		operationExecutionSpec.setOp(invocation);

	}

}
