package org.gcube.portlets.user.td.gwtservice.server.opexecution;

import java.util.HashMap;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.portlets.user.td.gwtservice.server.trservice.OperationDefinitionMap;
import org.gcube.portlets.user.td.gwtservice.shared.Constants;
import org.gcube.portlets.user.td.gwtservice.shared.chart.ChartTopRatingSession;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.widgetcommonevent.shared.operations.OperationsId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operation Execution for Top Rating Chart creation
 * 
 * @author "Giancarlo Panichi" email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class OpExecution4ChartTopRating extends OpExecutionBuilder {
	private static Logger logger = LoggerFactory
			.getLogger(OpExecution4ChartTopRating.class);

	private TabularDataService service;
	private ChartTopRatingSession chartTopRatingSession;

	public OpExecution4ChartTopRating(
			TabularDataService service,
			ChartTopRatingSession chartTopRatingSession) {
		this.service = service;
		this.chartTopRatingSession = chartTopRatingSession;
	}

	@Override
	public void buildOpEx() throws TDGWTServiceException {
		OperationExecution invocation = null;

		logger.debug(chartTopRatingSession.toString());
		OperationDefinition operationDefinition;
		Map<String, Object> map = new HashMap<String, Object>();

		operationDefinition = OperationDefinitionMap.map(
				OperationsId.TopRatingChart.toString(), service);
		
		
		map.put(Constants.PARAMETER_CHART_TOPRATING_SAMPLESIZE,
				chartTopRatingSession.getSampleSize());
		map.put(Constants.PARAMETER_CHART_TOPRATING_VALUEOPERATION,
				chartTopRatingSession.getValueOperation());

		
		invocation = new OperationExecution(chartTopRatingSession.getColumn().getColumnId(),
				operationDefinition.getOperationId(), map);

		operationExecutionSpec.setOp(invocation);

	}

}
