package org.gcube.portlets.user.td.gwtservice.server.opexecution;

import java.util.HashMap;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.portlets.user.td.gwtservice.server.trservice.OperationDefinitionMap;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.groupby.GroupBySession;
import org.gcube.portlets.user.td.widgetcommonevent.shared.operations.OperationsId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operation Execution for groupBy
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class OpExecution4GroupBy extends OpExecutionBuilder {
	private static Logger logger = LoggerFactory
			.getLogger(OpExecution4GroupBy.class);

	private TabularDataService service;
	private GroupBySession groupBySession;

	public OpExecution4GroupBy(TabularDataService service,
			GroupBySession groupBySession) {
		this.service = service;
		this.groupBySession = groupBySession;
	}

	@Override
	public void buildOpEx() throws TDGWTServiceException {

		OperationExecution invocation = null;

		logger.debug(groupBySession.toString());
		OperationDefinition operationDefinition;
		
		HashMap<String,Object> map=groupBySession.getMap();

		if(map==null){
			logger.error("In GroupBySession map is null");
			throw new TDGWTServiceException("In GroupBySession map is null");
		}		
		
		operationDefinition = OperationDefinitionMap.map(
				OperationsId.GroupBy.toString(), service);

		invocation = new OperationExecution(
				operationDefinition.getOperationId(), map);

		operationExecutionSpec.setOp(invocation);

	}

}
