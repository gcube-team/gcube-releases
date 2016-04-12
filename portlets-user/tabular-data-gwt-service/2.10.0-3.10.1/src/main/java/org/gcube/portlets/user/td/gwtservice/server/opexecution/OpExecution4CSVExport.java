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
import org.gcube.portlets.user.td.gwtservice.shared.csv.CSVExportSession;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.widgetcommonevent.shared.operations.OperationsId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operation Execution for CSV Export
 * 
 * @author "Giancarlo Panichi" email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class OpExecution4CSVExport extends OpExecutionBuilder {
	protected static Logger logger = LoggerFactory
			.getLogger(OpExecution4CSVExport.class);

	private HttpSession session;
	private TabularDataService service;
	private CSVExportSession csvExportSession;

	public OpExecution4CSVExport(HttpSession session,
			TabularDataService service, CSVExportSession csvExportSession) {
		this.service = service;
		this.csvExportSession = csvExportSession;
		this.session = session;
	}

	@Override
	public void buildOpEx() throws TDGWTServiceException {
		logger.debug("CSV Export: " + csvExportSession);

		SessionUtil.setCSVExportEnd(session, false);

		OperationDefinition operationDefinition;
		operationDefinition = OperationDefinitionMap.map(
				OperationsId.CSVExport.toString(), service);
		Map<String, Object> map = new HashMap<String, Object>();

		map.put(Constants.PARAMETER_RESOURCE_NAME,
				csvExportSession.getFileName());
		map.put(Constants.PARAMETER_RESOURCE_DESCRIPTION,
				csvExportSession.getFileDescription());
		map.put(Constants.PARAMETER_ENCODING, csvExportSession.getEncoding());
		map.put(Constants.PARAMETER_SEPARATOR, csvExportSession.getSeparator());
		map.put(Constants.PARAMETER_VIEW_COLUMNS,
				csvExportSession.isExportViewColumns());
		map.put(Constants.PARAMETER_COLUMNS,
				csvExportSession.getColumnsAsString());

		OperationExecution invocation = new OperationExecution(
				operationDefinition.getOperationId(), map);

		operationExecutionSpec.setOp(invocation);
	}

}
