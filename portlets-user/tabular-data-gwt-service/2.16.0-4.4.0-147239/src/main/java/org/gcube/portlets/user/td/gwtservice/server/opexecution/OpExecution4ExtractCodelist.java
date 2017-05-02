package org.gcube.portlets.user.td.gwtservice.server.opexecution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.portlets.user.td.gwtservice.server.trservice.ExtractCodelistOperationMap;
import org.gcube.portlets.user.td.gwtservice.server.trservice.OperationDefinitionMap;
import org.gcube.portlets.user.td.gwtservice.shared.Constants;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.gwtservice.shared.extract.ExtractCodelistSession;
import org.gcube.portlets.user.td.widgetcommonevent.shared.operations.OperationsId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operation Execution for extract codelist
 * 
 * @author "Giancarlo Panichi" email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class OpExecution4ExtractCodelist extends
		OpExecutionBuilder {
	private static Logger logger = LoggerFactory
			.getLogger(OpExecution4ExtractCodelist.class);

	private TabularDataService service;
	private ExtractCodelistSession extractCodelistSession;

	public OpExecution4ExtractCodelist(TabularDataService service,
			ExtractCodelistSession extractCodelistSession) {
		this.service = service;
		this.extractCodelistSession = extractCodelistSession;
	}

	@Override
	public void buildOpEx() throws TDGWTServiceException {
		OperationExecution invocation = null;

		logger.debug(extractCodelistSession.toString());
		OperationDefinition operationDefinition;
		
		String resourceName=null;
		if(extractCodelistSession.getTabResource()!=null){
			resourceName=extractCodelistSession.getTabResource().getName();
		} 
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		ExtractCodelistOperationMap extractMap = new ExtractCodelistOperationMap();
		ArrayList<Map<String, Object>> compositeValue = extractMap
				.genMap(extractCodelistSession);

		operationDefinition = OperationDefinitionMap.map(
				OperationsId.ExtractCodelist.toString(), service);
		
		map.put(Constants.PARAMETER_EXTRACT_CODELIST_RESOURCE_NAME, resourceName);
		map.put(Constants.PARAMETER_EXTRACT_CODELIST_COMPOSITE, compositeValue);

		invocation = new OperationExecution(
				operationDefinition.getOperationId(), map);

		operationExecutionSpec.setOp(invocation);

	}

}
