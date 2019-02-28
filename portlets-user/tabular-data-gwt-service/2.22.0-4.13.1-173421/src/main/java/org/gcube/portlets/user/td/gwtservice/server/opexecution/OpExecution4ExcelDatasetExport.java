package org.gcube.portlets.user.td.gwtservice.server.opexecution;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.portlets.user.td.gwtservice.server.trservice.OperationDefinitionMap;
import org.gcube.portlets.user.td.gwtservice.server.util.ServiceCredentials;
import org.gcube.portlets.user.td.gwtservice.shared.Constants;
import org.gcube.portlets.user.td.gwtservice.shared.excel.ExcelExportSession;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.widgetcommonevent.shared.operations.OperationsId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operation Execution for sdmx Dataset export
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class OpExecution4ExcelDatasetExport extends OpExecutionBuilder {
	private static Logger logger = LoggerFactory.getLogger(OpExecution4ExcelDatasetExport.class);

	@SuppressWarnings("unused")
	private HttpServletRequest httpRequest;
	@SuppressWarnings("unused")
	private ServiceCredentials serviceCredentials;
	private TabularDataService service;
	private ExcelExportSession excelExportSession;

	public OpExecution4ExcelDatasetExport(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			TabularDataService service, ExcelExportSession excelExportSession) {
		this.service = service;
		this.excelExportSession = excelExportSession;
		this.httpRequest=httpRequest;
		this.serviceCredentials=serviceCredentials;
	}

	@Override
	public void buildOpEx() throws TDGWTServiceException {
		logger.debug(excelExportSession.toString());
	
	
		OperationDefinition operationDefinition = OperationDefinitionMap.map(OperationsId.ExcelDatasetExport.toString(),
				service);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Constants.PARAMETER_ID, excelExportSession.getId());
		map.put(Constants.PARAMETER_AGENCY, excelExportSession.getAgencyId());
		map.put(Constants.PARAMETER_VERSION, excelExportSession.getVersion());
		map.put(Constants.PARAMETER_OBSVALUECOLUMN, excelExportSession.getObsValueColumn().getColumnId());
		OperationExecution invocation = new OperationExecution(operationDefinition.getOperationId(), map);

		operationExecutionSpec.setOp(invocation);
	}

}
