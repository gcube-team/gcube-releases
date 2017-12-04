package org.gcube.portlets.user.td.gwtservice.server.opexecution;

import java.util.HashMap;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.portlets.user.td.gwtservice.server.trservice.OperationDefinitionMap;
import org.gcube.portlets.user.td.gwtservice.shared.Constants;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.FilterColumnSession;
import org.gcube.portlets.user.td.widgetcommonevent.shared.operations.OperationsId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operation Execution for delete column
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class OpExecution4FilterColumn extends OpExecutionBuilder {
	private static Logger logger = LoggerFactory
			.getLogger(OpExecution4FilterColumn.class);

	private TabularDataService service;
	private FilterColumnSession filterColumnSession;
	private Expression expression;
	
	public OpExecution4FilterColumn(TabularDataService service,
			FilterColumnSession filterColumnSession, Expression expression) {
		this.service = service;
		this.filterColumnSession = filterColumnSession;
		this.expression=expression;
	}

	@Override
	public void buildOpEx() throws TDGWTServiceException {
		logger.debug(filterColumnSession.toString());
		
		OperationDefinition operationDefinition;
		operationDefinition = OperationDefinitionMap.map(
				OperationsId.FilterByExpression.toString(), service);
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put(Constants.PARAMETER_EXPRESSION, expression);
	
		OperationExecution invocation = new OperationExecution(
				operationDefinition.getOperationId(), map);
	
		
		operationExecutionSpec.setOp(invocation);
	}

}
