package org.gcube.portlets.user.td.gwtservice.server.opexecution;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.portlets.user.td.gwtservice.server.trservice.OperationDefinitionMap;
import org.gcube.portlets.user.td.gwtservice.server.util.ServiceCredentials;
import org.gcube.portlets.user.td.gwtservice.shared.Constants;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.gwtservice.shared.statistical.DataMinerOperationSession;
import org.gcube.portlets.user.td.widgetcommonevent.shared.operations.OperationsId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operation DataMiner
 * 
 * @author "Giancarlo Panichi" email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class OpExecution4DataMinerOperation extends OpExecutionBuilder {
	private static Logger logger = LoggerFactory
			.getLogger(OpExecution4DataMinerOperation.class);

	private TabularDataService service;
	// private ServiceCredentials serviceCredentials;
	private DataMinerOperationSession dataMinerOperationSession;

	public OpExecution4DataMinerOperation(TabularDataService service,
			ServiceCredentials serviceCredentials,
			DataMinerOperationSession dataMinerOperationSession) {
		this.service = service;
		// this.serviceCredentials = serviceCredentials;
		this.dataMinerOperationSession = dataMinerOperationSession;
	}

	@Override
	public void buildOpEx() throws TDGWTServiceException {
		OperationExecution invocation = null;

		logger.debug(dataMinerOperationSession.toString());
		OperationDefinition operationDefinition;
		Map<String, Object> map = new HashMap<String, Object>();

		operationDefinition = OperationDefinitionMap.map(
				OperationsId.StatisticalOperation.toString(), service);

		// map.put(Constants.PARAMETER_DATAMINER_GCUBETOKEN,
		// serviceCredentials.getToken());
		map.put(Constants.PARAMETER_DATAMINER_OPERATOR, Collections
				.singletonMap(Constants.PARAMETER_DATAMINER_OPERATOR,
						dataMinerOperationSession.getOperator()));

		invocation = new OperationExecution(
				operationDefinition.getOperationId(), map);

		operationExecutionSpec.setOp(invocation);

	}

}
