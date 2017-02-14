package org.gcube.portlets.user.td.gwtservice.server.opexecution;

import java.util.HashMap;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.datapublishing.sdmx.api.model.SDMXRegistryDescriptor;
import org.gcube.datapublishing.sdmx.api.model.SDMXRegistryInterfaceType;
import org.gcube.datapublishing.sdmx.impl.model.GCubeSDMXRegistryDescriptor;
import org.gcube.portlets.user.td.gwtservice.server.trservice.OperationDefinitionMap;
import org.gcube.portlets.user.td.gwtservice.shared.Constants;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.gwtservice.shared.sdmx.SDMXImportSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.type.Codelist;
import org.gcube.portlets.user.td.widgetcommonevent.shared.operations.OperationsId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operation Execution for sdmx codelist import
 * 
 * @author "Giancarlo Panichi" email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class OpExecution4SDMXCodelistImport extends OpExecutionBuilder {
	protected static Logger logger = LoggerFactory
			.getLogger(OpExecution4SDMXCodelistImport.class);

	private TabularDataService service;
	private SDMXImportSession sdmxImportSession;

	public OpExecution4SDMXCodelistImport(TabularDataService service,
			SDMXImportSession sdmxImportSession) {
		this.service = service;
		this.sdmxImportSession = sdmxImportSession;
	}

	@Override
	public void buildOpEx() throws TDGWTServiceException {
		logger.debug(sdmxImportSession.toString());

		OperationDefinition operationDefinition= OperationDefinitionMap
				.map(OperationsId.SDMXCodelistImport.toString(), service);
		
		Map<String, Object> map = new HashMap<String, Object>();

		Codelist codelist = sdmxImportSession.getSelectedCodelist();

		map.put(Constants.PARAMETER_AGENCY, codelist.getAgencyId());
		map.put(Constants.PARAMETER_ID, codelist.getId());
		map.put(Constants.PARAMETER_VERSION, codelist.getVersion());

		// TODO: Get registry url from client

		SDMXRegistryDescriptor descriptor = new GCubeSDMXRegistryDescriptor();
		map.put(Constants.PARAMETER_REGISTRYBASEURL,
				descriptor.getUrl(SDMXRegistryInterfaceType.RESTV2_1));

		OperationExecution invocation = new OperationExecution(
				operationDefinition.getOperationId(), map);
		
		operationExecutionSpec.setOp(invocation);
	}

}
