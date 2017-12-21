package org.gcube.portlets.user.td.gwtservice.server.opexecution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.portlets.user.td.gwtservice.server.trservice.ExpressionGenerator;
import org.gcube.portlets.user.td.gwtservice.server.trservice.OperationDefinitionMap;
import org.gcube.portlets.user.td.gwtservice.shared.Constants;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.batch.ReplaceBatchColumnSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.batch.ReplaceEntry;
import org.gcube.portlets.user.td.widgetcommonevent.shared.operations.OperationsId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Operation Execution for replace batch
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class OpExecution4ReplaceBatch  extends OpExecutionBuilder {
	private static Logger logger = LoggerFactory
			.getLogger(OpExecution4ReplaceBatch.class);

	private TabularDataService service;
	private ReplaceBatchColumnSession replaceBatchColumnSession;
	
	public OpExecution4ReplaceBatch(TabularDataService service,
			ReplaceBatchColumnSession replaceBatchColumnSession){
		this.service=service;
		this.replaceBatchColumnSession=replaceBatchColumnSession;
	}
	
	@Override
	public void buildOpEx() throws TDGWTServiceException {
		ArrayList<OperationExecution> invocations = new ArrayList<OperationExecution>();
		logger.debug(replaceBatchColumnSession.toString());
		for (ReplaceEntry re : replaceBatchColumnSession.getReplaceEntryList()) {
			OperationExecution invocation = null;

			OperationDefinition operationDefinition;
			Map<String, Object> map = new HashMap<String, Object>();

			if (replaceBatchColumnSession.isReplaceDimension()) {
				logger.debug("Is a Replace of view column");
				operationDefinition = OperationDefinitionMap.map(
						OperationsId.ReplaceColumnByExpression.toString(),
						service);

				Expression condition = ExpressionGenerator
						.genReplaceValueParameterCondition(
								replaceBatchColumnSession, re);
				Expression value = ExpressionGenerator
						.genReplaceBatchValueParameterValue(
								replaceBatchColumnSession, re);

				map.put(Constants.PARAMETER_REPLACE_BY_EXPRESSION_COLUMN_CONDITION,
						condition);
				map.put(Constants.PARAMETER_REPLACE_BY_EXPRESSION_COLUMN_VALUE,
						value);

				invocation = new OperationExecution(replaceBatchColumnSession
						.getColumnData().getColumnViewData()
						.getSourceTableDimensionColumnId(),
						operationDefinition.getOperationId(), map);

			} else {
				logger.debug("Is a Replace of basic column");

				operationDefinition = OperationDefinitionMap.map(
						OperationsId.ReplaceColumnByExpression.toString(),
						service);

				Expression condition = ExpressionGenerator
						.genReplaceValueParameterCondition(
								replaceBatchColumnSession, re);
				Expression value = ExpressionGenerator
						.genReplaceBatchValueParameterValue(
								replaceBatchColumnSession, re);

				map.put(Constants.PARAMETER_REPLACE_BY_EXPRESSION_COLUMN_CONDITION,
						condition);
				map.put(Constants.PARAMETER_REPLACE_BY_EXPRESSION_COLUMN_VALUE,
						value);

				invocation = new OperationExecution(replaceBatchColumnSession
						.getColumnData().getColumnId(),
						operationDefinition.getOperationId(), map);
			}
			invocations.add(invocation);
		}
		
		
		operationExecutionSpec.setOps(invocations);
		
	}

}
