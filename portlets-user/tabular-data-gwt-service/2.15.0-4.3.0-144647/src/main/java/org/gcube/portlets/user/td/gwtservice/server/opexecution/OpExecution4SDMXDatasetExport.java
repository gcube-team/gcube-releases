package org.gcube.portlets.user.td.gwtservice.server.opexecution;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.Profile;
import org.gcube.common.resources.gcore.utils.Group;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.portlets.user.td.gwtservice.server.trservice.OperationDefinitionMap;
import org.gcube.portlets.user.td.gwtservice.shared.Constants;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.gwtservice.shared.sdmx.SDMXExportSession;
import org.gcube.portlets.user.td.widgetcommonevent.shared.operations.OperationsId;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operation Execution for sdmx Dataset export
 * 
 * @author "Giancarlo Panichi" email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class OpExecution4SDMXDatasetExport extends OpExecutionBuilder {
	private static Logger logger = LoggerFactory
			.getLogger(OpExecution4SDMXDatasetExport.class);

	private TabularDataService service;
	private SDMXExportSession sdmxExportSession;

	public OpExecution4SDMXDatasetExport(TabularDataService service,
			SDMXExportSession sdmxExportSession) {
		this.service = service;
		this.sdmxExportSession = sdmxExportSession;
	}

	@Override
	public void buildOpEx() throws TDGWTServiceException {
		logger.debug(sdmxExportSession.toString());
		boolean internalRegistry = false;
		String destination = null;// Es:
									// http://pc-fortunati.isti.cnr.it:8080/FusionRegistry/ws/rest/

		if (sdmxExportSession != null) {
			if (sdmxExportSession.getRegistryBaseUrl() != null
					&& !sdmxExportSession.getRegistryBaseUrl().isEmpty()) {
				destination = sdmxExportSession.getRegistryBaseUrl();
			} else {
				internalRegistry = true;
			}
		} else {
			internalRegistry = true;
		}

		if (internalRegistry) {
			SimpleQuery query = queryFor(ServiceEndpoint.class);
			query.addCondition("$resource/Profile/Category/text() eq 'SDMX'")
					.addCondition(
							"$resource/Profile/Name/text() eq 'SDMXRegistry'");
			DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
			List<ServiceEndpoint> listService = client.submit(query);
			if (listService.size() > 0) {
				ServiceEndpoint serviceEnd = listService.get(0);
				if (serviceEnd != null) {
					Profile prof = serviceEnd.profile();
					Group<AccessPoint> groupA = prof.accessPoints();
					for (AccessPoint acc : groupA) {
						if (acc.description().compareTo("REST Interface v2.1") == 0) {
							destination = acc.address();
							break;
						}
					}
				} else {

				}
			} else {

			}
		}
		if (destination == null) {
			logger.debug("Destination: " + destination);
			throw new TDGWTServiceException("SDMX Service not discovered");
		}

		OperationDefinition operationDefinition = OperationDefinitionMap.map(
				OperationsId.SDMXDatasetExport.toString(), service);

		Map<String, Object> map = new HashMap<String, Object>();

		map.put(Constants.PARAMETER_REGISTRYBASEURL, destination);
		map.put(Constants.PARAMETER_AGENCY, sdmxExportSession.getAgencyId());
		map.put(Constants.PARAMETER_ID, sdmxExportSession.getId());
		map.put(Constants.PARAMETER_VERSION, sdmxExportSession.getVersion());
		map.put(Constants.PARAMETER_OBSVALUECOLUMN, sdmxExportSession.getObsValueColumn().getColumnId());
		OperationExecution invocation = new OperationExecution(
				operationDefinition.getOperationId(), map);

		operationExecutionSpec.setOp(invocation);
	}

}
