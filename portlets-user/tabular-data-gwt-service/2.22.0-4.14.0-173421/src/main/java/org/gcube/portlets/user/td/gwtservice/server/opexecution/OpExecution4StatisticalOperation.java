package org.gcube.portlets.user.td.gwtservice.server.opexecution;

import java.util.HashMap;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.portlets.user.td.gwtservice.server.trservice.OperationDefinitionMap;
import org.gcube.portlets.user.td.gwtservice.server.util.ServiceCredentials;
import org.gcube.portlets.user.td.gwtservice.shared.Constants;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.gwtservice.shared.statistical.StatisticalOperationSession;
import org.gcube.portlets.user.td.widgetcommonevent.shared.operations.OperationsId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operation Execution for union
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class OpExecution4StatisticalOperation extends OpExecutionBuilder {
	private static Logger logger = LoggerFactory
			.getLogger(OpExecution4StatisticalOperation.class);

	private TabularDataService service;
	private ServiceCredentials serviceCredentials;
	private StatisticalOperationSession statisticalOperationSession;


	public OpExecution4StatisticalOperation(
			TabularDataService service,ServiceCredentials serviceCredentials,
			StatisticalOperationSession statisticalOperationSession) {
		this.service = service;
		this.serviceCredentials = serviceCredentials;
		this.statisticalOperationSession = statisticalOperationSession;
	}

	@Override
	public void buildOpEx() throws TDGWTServiceException {
		OperationExecution invocation = null;

		logger.debug(statisticalOperationSession.toString());
		OperationDefinition operationDefinition;
		Map<String, Object> map = new HashMap<String, Object>();

		operationDefinition = OperationDefinitionMap.map(
				OperationsId.StatisticalOperation.toString(), service);
		
		map.put(Constants.PARAMETER_STATISTICAL_OPERATION_USER, serviceCredentials.getUserName());
		map.put(Constants.PARAMETER_STATISTICAL_OPERATION_ALGORITHM, statisticalOperationSession.getOperatorId());
		
		
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, String> parameters=statisticalOperationSession.getParameters();
		for (String key : parameters.keySet()) {
			params.put(key.toString(), parameters.get(key));
		}		
		map.put(Constants.PARAMETER_STATISTICAL_OPERATION_PARAMETERS, params);

		map.put(Constants.PARAMETER_STATISTICAL_OPERATION_DESCRIPTION, statisticalOperationSession.getDescription());
		map.put(Constants.PARAMETER_STATISTICAL_OPERATION_TITLE, statisticalOperationSession.getTitle());
		
		
		invocation = new OperationExecution(
				operationDefinition.getOperationId(), map);

		
		operationExecutionSpec.setOp(invocation);

	}

}
