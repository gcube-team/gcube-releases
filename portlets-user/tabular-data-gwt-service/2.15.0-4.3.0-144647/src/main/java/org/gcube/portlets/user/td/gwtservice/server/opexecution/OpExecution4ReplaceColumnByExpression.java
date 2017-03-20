package org.gcube.portlets.user.td.gwtservice.server.opexecution;

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
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.ReplaceColumnByExpressionSession;
import org.gcube.portlets.user.td.widgetcommonevent.shared.operations.OperationsId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operation Execution for replace column
 * 
 * @author "Giancarlo Panichi" email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class OpExecution4ReplaceColumnByExpression extends OpExecutionBuilder {
	private static Logger logger = LoggerFactory
			.getLogger(OpExecution4ReplaceColumnByExpression.class);

	private TabularDataService service;
	private ReplaceColumnByExpressionSession replaceColumnByExpressionSession;
	private Expression conditionExpression;
	private Expression replaceExpression;

	public OpExecution4ReplaceColumnByExpression(TabularDataService service,
			ReplaceColumnByExpressionSession replaceColumnByExpressionSession,
			Expression conditionExpression, Expression replaceExpression) {
		this.service = service;
		this.replaceColumnByExpressionSession = replaceColumnByExpressionSession;
		this.conditionExpression = conditionExpression;
		this.replaceExpression = replaceExpression;
	}

	@Override
	public void buildOpEx() throws TDGWTServiceException {

		OperationExecution invocation = null;

		logger.debug(replaceColumnByExpressionSession.toString());
		OperationDefinition operationDefinition;
		Map<String, Object> map = new HashMap<String, Object>();

		logger.debug("Is a Replace of basic column");

		operationDefinition = OperationDefinitionMap.map(
				OperationsId.ReplaceColumnByExpression.toString(), service);

		if (replaceColumnByExpressionSession.isReplaceByValue()) {
			Expression value = ExpressionGenerator
					.genReplaceValueParameterValue(replaceColumnByExpressionSession);

			if (!replaceColumnByExpressionSession.isAllRows()) {
				map.put(Constants.PARAMETER_REPLACE_BY_EXPRESSION_COLUMN_CONDITION,
						conditionExpression);
			}
			map.put(Constants.PARAMETER_REPLACE_BY_EXPRESSION_COLUMN_VALUE,
					value);
		} else {
			if (!replaceColumnByExpressionSession.isAllRows()) {
				map.put(Constants.PARAMETER_REPLACE_BY_EXPRESSION_COLUMN_CONDITION,
						conditionExpression);
			}
			map.put(Constants.PARAMETER_REPLACE_BY_EXPRESSION_COLUMN_VALUE,
					replaceExpression);
		}
		invocation = new OperationExecution(replaceColumnByExpressionSession
				.getColumn().getColumnId(),
				operationDefinition.getOperationId(), map);

		operationExecutionSpec.setOp(invocation);

	}

}
