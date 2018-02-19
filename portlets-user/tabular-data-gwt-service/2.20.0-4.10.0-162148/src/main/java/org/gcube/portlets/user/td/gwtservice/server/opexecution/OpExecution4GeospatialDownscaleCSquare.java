package org.gcube.portlets.user.td.gwtservice.server.opexecution;

import java.util.HashMap;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.portlets.user.td.gwtservice.server.trservice.OperationDefinitionMap;
import org.gcube.portlets.user.td.gwtservice.shared.Constants;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.gwtservice.shared.geospatial.GeospatialDownscaleCSquareSession;
import org.gcube.portlets.user.td.widgetcommonevent.shared.operations.OperationsId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operation Execution for change table type
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class OpExecution4GeospatialDownscaleCSquare extends OpExecutionBuilder {
	private static Logger logger = LoggerFactory
			.getLogger(OpExecution4GeospatialDownscaleCSquare.class);

	private TabularDataService service;
	private GeospatialDownscaleCSquareSession geospatialDownscaleCSquareSession;

	public OpExecution4GeospatialDownscaleCSquare(TabularDataService service,
			GeospatialDownscaleCSquareSession geospatialDownscaleCSquareSession) {
		this.service = service;
		this.geospatialDownscaleCSquareSession = geospatialDownscaleCSquareSession;
	}

	@Override
	public void buildOpEx() throws TDGWTServiceException {
		OperationExecution invocation = null;

		logger.debug("Downscale CSquare: " + geospatialDownscaleCSquareSession);
		OperationDefinition operationDefinition;
		Map<String, Object> map = new HashMap<String, Object>();

		ColumnData col = geospatialDownscaleCSquareSession.getCsquareColumn();

		if (col == null || col.getColumnId() == null
				|| col.getColumnId().isEmpty()) {
			logger.error("Error in downscale c-square: no valid column set");
			throw new TDGWTServiceException("No valid column set");
		}

		if (geospatialDownscaleCSquareSession.getResolution() == null
				|| geospatialDownscaleCSquareSession.getResolution().isEmpty()) {
			logger.error("Error in downscale c-square: no valid resolution set");
			throw new TDGWTServiceException("No valid resolution set");
		}

		operationDefinition = OperationDefinitionMap.map(
				OperationsId.DownscaleCSquare.toString(), service);
		map.put(Constants.PARAMETER_DOWNSCALE_CSQUARE_RESOLUTION,
				geospatialDownscaleCSquareSession.getResolution());

		invocation = new OperationExecution(col.getColumnId(),
				operationDefinition.getOperationId(), map);

		operationExecutionSpec.setOp(invocation);

	}

}
