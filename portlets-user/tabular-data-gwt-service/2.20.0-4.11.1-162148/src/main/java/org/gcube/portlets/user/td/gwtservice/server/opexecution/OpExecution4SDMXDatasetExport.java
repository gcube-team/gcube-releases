package org.gcube.portlets.user.td.gwtservice.server.opexecution;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.portlets.user.td.gwtservice.server.SessionUtil;
import org.gcube.portlets.user.td.gwtservice.server.is.ISUtils;
import org.gcube.portlets.user.td.gwtservice.server.trservice.OperationDefinitionMap;
import org.gcube.portlets.user.td.gwtservice.server.util.ServiceCredentials;
import org.gcube.portlets.user.td.gwtservice.shared.Constants;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.gwtservice.shared.sdmx.SDMXExportSession;
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
public class OpExecution4SDMXDatasetExport extends OpExecutionBuilder {
	private static Logger logger = LoggerFactory.getLogger(OpExecution4SDMXDatasetExport.class);

	private HttpServletRequest httpRequest;
	private ServiceCredentials serviceCredentials;
	private TabularDataService service;
	private SDMXExportSession sdmxExportSession;

	public OpExecution4SDMXDatasetExport(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			TabularDataService service, SDMXExportSession sdmxExportSession) {
		this.service = service;
		this.sdmxExportSession = sdmxExportSession;
		this.httpRequest=httpRequest;
		this.serviceCredentials=serviceCredentials;
	}

	@Override
	public void buildOpEx() throws TDGWTServiceException {
		logger.debug(sdmxExportSession.toString());
		boolean internalRegistry = false;
		String destination = null;// Es:
									// http://pc-fortunati.isti.cnr.it:8080/FusionRegistry/ws/rest/

		if (sdmxExportSession != null) {
			if (sdmxExportSession.getRegistryBaseUrl() != null && !sdmxExportSession.getRegistryBaseUrl().isEmpty()) {
				destination = sdmxExportSession.getRegistryBaseUrl();
			} else {
				internalRegistry = true;
			}
		} else {
			internalRegistry = true;
		}

		if (internalRegistry) {
			destination = SessionUtil.getInternalSDMXRegistryURLSession(httpRequest, serviceCredentials);
			if (destination == null || destination.isEmpty()) {
				destination = ISUtils.retrieveInternalSDMXRegistryURL();
				if(destination!=null&&!destination.isEmpty()){
					SessionUtil.setInternalSDMXRegistryURLSession(httpRequest, serviceCredentials, destination);
				}
			}
		}
		if (destination == null) {
			logger.debug("Destination: " + destination);
			throw new TDGWTServiceException("SDMX Service not discovered");
		}

		OperationDefinition operationDefinition = OperationDefinitionMap.map(OperationsId.SDMXDatasetExport.toString(),
				service);

		Map<String, Object> map = new HashMap<String, Object>();

		map.put(Constants.PARAMETER_REGISTRYBASEURL, destination);
		map.put(Constants.PARAMETER_AGENCY, sdmxExportSession.getAgencyId());
		map.put(Constants.PARAMETER_ID, sdmxExportSession.getId());
		map.put(Constants.PARAMETER_VERSION, sdmxExportSession.getVersion());
		map.put(Constants.PARAMETER_OBSVALUECOLUMN, sdmxExportSession.getObsValueColumn().getColumnId());
		map.put(Constants.PARAMETER_EXCEL, sdmxExportSession.isExcel());
		OperationExecution invocation = new OperationExecution(operationDefinition.getOperationId(), map);

		operationExecutionSpec.setOp(invocation);
	}

}
