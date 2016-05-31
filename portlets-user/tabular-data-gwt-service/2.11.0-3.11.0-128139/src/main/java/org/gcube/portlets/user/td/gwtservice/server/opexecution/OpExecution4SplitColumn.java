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
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.SplitColumnSession;
import org.gcube.portlets.user.td.widgetcommonevent.shared.operations.OperationsId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operation Execution for split column
 * 
 * @author "Giancarlo Panichi" email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class OpExecution4SplitColumn extends OpExecutionBuilder {
	protected static Logger logger = LoggerFactory
			.getLogger(OpExecution4SplitColumn.class);

	private TabularDataService service;
	private SplitColumnSession splitColumnSession;

	public OpExecution4SplitColumn(TabularDataService service,
			SplitColumnSession splitColumnSession) {
		this.service = service;
		this.splitColumnSession = splitColumnSession;
	}

	@Override
	public void buildOpEx() throws TDGWTServiceException {

		ArrayList<OperationExecution> invocations = new ArrayList<OperationExecution>();

		logger.debug(splitColumnSession.toString());

		OperationDefinition operationDefinition = OperationDefinitionMap.map(
				OperationsId.AddColumn.toString(), service);

		Map<String, Object> map1 = new HashMap<String, Object>();
		Map<String, Object> map2 = new HashMap<String, Object>();

		ArrayList<Expression> expressions = splitColumnSession.getExpressions();

		if (expressions == null || expressions.size() <= 1) {
			throw new TDGWTServiceException(
					"No valid expressions in splitColumnSession");
		}

		map1.put(Constants.PARAMETER_ADD_COLUMN_COLUMN_TYPE, ColumnTypeCodeMap
				.getColumnType(splitColumnSession.getFirstSplitColumnType()));
		map1.put(Constants.PARAMETER_ADD_COLUMN_DATA_TYPE, ColumnDataTypeMap
				.map(splitColumnSession.getFirstSplitColumnDataType()));
		map1.put(Constants.PARAMETER_ADD_COLUMN_LABEL,
				new ImmutableLocalizedText(splitColumnSession.getLabel1()));
		map1.put(Constants.PARAMETER_ADD_COLUMN_VALUE, expressions.get(0));
		invocations.add(new OperationExecution(operationDefinition
				.getOperationId(), map1));
		map2.put(Constants.PARAMETER_ADD_COLUMN_COLUMN_TYPE, ColumnTypeCodeMap
				.getColumnType(splitColumnSession.getSecondSplitColumnType()));
		map2.put(Constants.PARAMETER_ADD_COLUMN_DATA_TYPE, ColumnDataTypeMap
				.map(splitColumnSession.getSecondSplitColumnDataType()));
		map2.put(Constants.PARAMETER_ADD_COLUMN_LABEL,
				new ImmutableLocalizedText(splitColumnSession.getLabel2()));
		map2.put(Constants.PARAMETER_ADD_COLUMN_VALUE, expressions.get(1));
		invocations.add(new OperationExecution(operationDefinition
				.getOperationId(), map2));

		if (splitColumnSession.isDeleteColumn()) {
			OperationDefinition operationDefinitionRemoveColumn = OperationDefinitionMap
					.map(OperationsId.RemoveColumn.toString(), service);
			Map<String, Object> mapRemoveColumn = new HashMap<String, Object>();

			OperationExecution invocationRemoveColumn = null;

			invocationRemoveColumn = new OperationExecution(splitColumnSession
					.getColumnData().getColumnId(),
					operationDefinitionRemoveColumn.getOperationId(),
					mapRemoveColumn);
			invocations.add(invocationRemoveColumn);

		}

		operationExecutionSpec.setOps(invocations);

	}

}
