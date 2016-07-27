package org.gcube.portlets.user.td.gwtservice.server.opexecution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.portlets.user.td.gwtservice.server.trservice.ColumnDataTypeMap;
import org.gcube.portlets.user.td.gwtservice.server.trservice.ColumnTypeCodeMap;
import org.gcube.portlets.user.td.gwtservice.server.trservice.OperationDefinitionMap;
import org.gcube.portlets.user.td.gwtservice.shared.Constants;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.MergeColumnSession;
import org.gcube.portlets.user.td.widgetcommonevent.shared.operations.OperationsId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operation Execution for merge column
 * 
 * @author "Giancarlo Panichi" email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class OpExecution4MergeColumn extends OpExecutionBuilder {
	protected static Logger logger = LoggerFactory
			.getLogger(OpExecution4MergeColumn.class);

	private TabularDataService service;
	private MergeColumnSession mergeColumnSession;

	public OpExecution4MergeColumn(TabularDataService service,
			MergeColumnSession mergeColumnSession) {
		this.service = service;
		this.mergeColumnSession = mergeColumnSession;
	}

	@Override
	public void buildOpEx() throws TDGWTServiceException {
		ArrayList<OperationExecution> invocations = new ArrayList<OperationExecution>();

		logger.debug(mergeColumnSession.toString());

		OperationDefinition operationDefinition = OperationDefinitionMap.map(
				OperationsId.AddColumn.toString(), service);

		Map<String, Object> map = new HashMap<String, Object>();

		Expression expression = mergeColumnSession.getExpression();
		if (expression == null) {
			throw new TDGWTServiceException(
					"No valid expression in splitColumnSession");
		}

		map.put(Constants.PARAMETER_ADD_COLUMN_COLUMN_TYPE, ColumnTypeCodeMap
				.getColumnType(mergeColumnSession.getMergeColumnType()));
		map.put(Constants.PARAMETER_ADD_COLUMN_DATA_TYPE, ColumnDataTypeMap
				.map(mergeColumnSession.getMergeColumnDataType()));
		map.put(Constants.PARAMETER_ADD_COLUMN_LABEL,
				new ImmutableLocalizedText(mergeColumnSession.getLabel()));
		map.put(Constants.PARAMETER_ADD_COLUMN_VALUE, expression);
		invocations.add(new OperationExecution(operationDefinition
				.getOperationId(), map));
		
		if (mergeColumnSession.isDeleteColumn()) {
			OperationDefinition operationDefinitionRemoveColumn = OperationDefinitionMap
					.map(OperationsId.RemoveColumn.toString(), service);
			Map<String, Object> mapRemoveColumn = new HashMap<String, Object>();

			invocations.add(new OperationExecution(mergeColumnSession
					.getColumnDataSource1().getColumnId(),
					operationDefinitionRemoveColumn.getOperationId(),
					mapRemoveColumn));
			
			invocations.add(new OperationExecution(mergeColumnSession
					.getColumnDataSource2().getColumnId(),
					operationDefinitionRemoveColumn.getOperationId(),
					mapRemoveColumn));
		
		}

		operationExecutionSpec.setOps(invocations);

	}

}
