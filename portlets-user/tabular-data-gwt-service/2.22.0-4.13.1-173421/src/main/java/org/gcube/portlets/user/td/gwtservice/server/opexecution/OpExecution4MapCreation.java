package org.gcube.portlets.user.td.gwtservice.server.opexecution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.portlets.user.td.gwtservice.server.trservice.OperationDefinitionMap;
import org.gcube.portlets.user.td.gwtservice.shared.Constants;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.gwtservice.shared.map.MapCreationSession;
import org.gcube.portlets.user.td.widgetcommonevent.shared.operations.OperationsId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operation Execution for Map Creation
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class OpExecution4MapCreation extends OpExecutionBuilder {
	private static Logger logger = LoggerFactory
			.getLogger(OpExecution4MapCreation.class);

	private TabularDataService service;
	private MapCreationSession mapCreationSession;

	public OpExecution4MapCreation(TabularDataService service,
			MapCreationSession mapCreationSession) {
		this.service = service;
		this.mapCreationSession = mapCreationSession;
	}

	@Override
	public void buildOpEx() throws TDGWTServiceException {
		logger.debug(mapCreationSession.toString());

		OperationDefinition operationDefinition;
		operationDefinition = OperationDefinitionMap.map(
				OperationsId.GenerateMap.toString(), service);
		Map<String, Object> map = new HashMap<String, Object>();

		ArrayList<ColumnData> fut = mapCreationSession.getFeature();
		ArrayList<ColumnReference> futures = new ArrayList<ColumnReference>();

		String tableIdS = null;
		if (mapCreationSession.getTrId().isViewTable()) {
			tableIdS = mapCreationSession.getTrId().getReferenceTargetTableId();
		} else {
			tableIdS = mapCreationSession.getTrId().getTableId();
		}
		TableId tableId = new TableId(Long.valueOf(tableIdS));

		for (ColumnData c : fut) {
			ColumnLocalId columnId = new ColumnLocalId(c.getColumnId());
			ColumnReference columnRef = new ColumnReference(tableId, columnId);
			futures.add(columnRef);
		}

		ColumnLocalId columnGeometryId = new ColumnLocalId(mapCreationSession
				.getGeometry().getColumnId());
		ColumnReference columnGeometryRef = new ColumnReference(tableId,
				columnGeometryId);

		map.put(Constants.PARAMETER_GENERATEMAP_MAPNAME,
				mapCreationSession.getName());
		map.put(Constants.PARAMETER_GENERATEMAP_FEATURE, futures);
		map.put(Constants.PARAMETER_GENERATEMAP_GEOM, columnGeometryRef);
		map.put(Constants.PARAMETER_GENERATEMAP_USEVIEW, new Boolean(
				mapCreationSession.isUseView()));
		map.put(Constants.PARAMETER_GENERATEMAP_METAABSTRACT,
				mapCreationSession.getMetaAbstract());
		map.put(Constants.PARAMETER_GENERATEMAP_METAPURPOSE,
				mapCreationSession.getMetaPurpose());
		map.put(Constants.PARAMETER_GENERATEMAP_USER,
				mapCreationSession.getUsername());
		map.put(Constants.PARAMETER_GENERATEMAP_METACREDITS,
				mapCreationSession.getMetaCredits());
		map.put(Constants.PARAMETER_GENERATEMAP_METAKEYWORDS,
				mapCreationSession.getMetaKeywords());

		OperationExecution invocation = new OperationExecution(
				operationDefinition.getOperationId(), map);

		operationExecutionSpec.setOp(invocation);
	}

}
