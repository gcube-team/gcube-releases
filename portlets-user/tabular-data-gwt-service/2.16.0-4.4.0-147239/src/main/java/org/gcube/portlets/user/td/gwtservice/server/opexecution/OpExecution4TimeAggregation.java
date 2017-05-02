package org.gcube.portlets.user.td.gwtservice.server.opexecution;

import java.util.HashMap;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.portlets.user.td.gwtservice.server.trservice.OperationDefinitionMap;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.groupby.TimeAggregationSession;
import org.gcube.portlets.user.td.widgetcommonevent.shared.operations.OperationsId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operation Execution for Time Aggregation
 * 
 * @author "Giancarlo Panichi" email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class OpExecution4TimeAggregation extends OpExecutionBuilder {
	private static Logger logger = LoggerFactory
			.getLogger(OpExecution4TimeAggregation.class);

	private TabularDataService service;
	private TimeAggregationSession timeAggregationSession;

	public OpExecution4TimeAggregation(TabularDataService service,
			TimeAggregationSession timeAggregationSession) {
		this.service = service;
		this.timeAggregationSession = timeAggregationSession;
	}

	@Override
	public void buildOpEx() throws TDGWTServiceException {

		OperationExecution invocation = null;

		logger.debug(timeAggregationSession.toString());
		OperationDefinition operationDefinition;

		HashMap<String, Object> map = timeAggregationSession.getMap();

		if (map == null) {
			logger.error("In TimeAggregationSession map is null");
			throw new TDGWTServiceException(
					"In TimeAggregationSession map is null");
		}

		operationDefinition = OperationDefinitionMap.map(
				OperationsId.TimeAggregation.toString(), service);

		invocation = new OperationExecution(timeAggregationSession.getColumn()
				.getColumnId(), operationDefinition.getOperationId(), map);

		operationExecutionSpec.setOp(invocation);

	}

}
