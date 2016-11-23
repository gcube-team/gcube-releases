package org.gcube.portlets.user.td.gwtservice.server.opexecution;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.portlets.user.td.gwtservice.server.SessionUtil;
import org.gcube.portlets.user.td.gwtservice.server.trservice.OperationDefinitionMap;
import org.gcube.portlets.user.td.gwtservice.shared.Constants;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.gwtservice.shared.json.JSONExportSession;
import org.gcube.portlets.user.td.widgetcommonevent.shared.operations.OperationsId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operation Execution for JSON Export
 * 
 * @author "Giancarlo Panichi" email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class OpExecution4JSONExport extends OpExecutionBuilder {
	protected static Logger logger = LoggerFactory
			.getLogger(OpExecution4JSONExport.class);

	private HttpSession session;
	private TabularDataService service;
	private JSONExportSession jsonExportSession;

	
	public OpExecution4JSONExport(HttpSession session, TabularDataService service,
			JSONExportSession jsonExportSession) {
		this.service = service;
		this.jsonExportSession = jsonExportSession;
		this.session=session;
	}

	@Override
	public void buildOpEx() throws TDGWTServiceException {
		logger.debug(jsonExportSession.toString());	
		
		SessionUtil.setJSONExportEnd(session, false);
		
		OperationDefinition operationDefinition;
		operationDefinition = OperationDefinitionMap.map(
				OperationsId.JSONExport.toString(), service);
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put(Constants.PARAMETER_RESOURCE_NAME, jsonExportSession.getFileName());
		
		map.put(Constants.PARAMETER_RESOURCE_DESCRIPTION, jsonExportSession.getFileDescription());
		
		map.put(Constants.PARAMETER_VIEW_COLUMNS,
				jsonExportSession.isExportViewColumns());
		map.put(Constants.PARAMETER_COLUMNS,
				jsonExportSession.getColumnsAsString());
		
		OperationExecution invocation = new OperationExecution(
				operationDefinition.getOperationId(), map);
		
		operationExecutionSpec.setOp(invocation);
	}

}
