package org.gcube.portlets.user.td.gwtservice.server.opexecution;

import java.util.HashMap;
import java.util.Map;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDNumeric;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.portlets.user.td.gwtservice.server.trservice.OperationDefinitionMap;
import org.gcube.portlets.user.td.gwtservice.shared.Constants;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.gwtservice.shared.geospatial.GeospatialCreateCoordinatesSession;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.operations.OperationsId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operation Execution for change table type
 * 
 * @author "Giancarlo Panichi" email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class OpExecution4GeospatialCreateCoordinates extends OpExecutionBuilder {
	protected static Logger logger = LoggerFactory
			.getLogger(OpExecution4GeospatialCreateCoordinates.class);

	private TabularDataService service;
	private GeospatialCreateCoordinatesSession geospatialCreateCoordinatesSession;
	private ASLSession aslSession;

	public OpExecution4GeospatialCreateCoordinates(
			ASLSession aslSession,
			TabularDataService service,
			GeospatialCreateCoordinatesSession geospatialCreateCoordinatesSession) {
		this.service = service;
		this.geospatialCreateCoordinatesSession = geospatialCreateCoordinatesSession;
		this.aslSession = aslSession;
	}

	@Override
	public void buildOpEx() throws TDGWTServiceException {
		OperationExecution invocation = null;

		logger.debug(geospatialCreateCoordinatesSession.toString());
		OperationDefinition operationDefinition;
		Map<String, Object> map = new HashMap<String, Object>();

		TRId trId = geospatialCreateCoordinatesSession.getTrId();
		logger.debug("trID: " + trId);
		if (trId == null) {
			logger.error("Error Creating Geospatial Coordinates: trId is null");
			throw new TDGWTServiceException("No tabular resource set");
		}

		long tabId;
		if (trId.isViewTable()) {
			tabId = new Long(trId.getReferenceTargetTableId());
		} else {
			tabId = new Long(trId.getTableId());

		}
		TableId tId = new TableId(tabId);

		ColumnData latitudeColumn = geospatialCreateCoordinatesSession
				.getLatitude();
		logger.debug("Latitude Column: " + latitudeColumn);
		if (latitudeColumn == null) {
			logger.error("Error Creating Geospatial Coordinates: Latitude Column is null");
			throw new TDGWTServiceException("No latitude column set");
		}
		ColumnLocalId latitudeId = new ColumnLocalId(
				latitudeColumn.getColumnId());
		ColumnReference latitudeColumnReference = new ColumnReference(tId,
				latitudeId);

		ColumnData longitudeColumn = geospatialCreateCoordinatesSession
				.getLongitude();
		logger.debug("Longitude Column: " + longitudeColumn);
		if (longitudeColumn == null) {
			logger.error("Error Creating Geospatial Coordinates: Longitude Column is null");
			throw new TDGWTServiceException("No longitude column set");
		}
		ColumnLocalId longitudeId = new ColumnLocalId(
				longitudeColumn.getColumnId());
		ColumnReference longitudeColumnReference = new ColumnReference(tId,
				longitudeId);

		operationDefinition = OperationDefinitionMap.map(
				OperationsId.EnhanceLatLong.toString(), service);
		map.put(Constants.PARAMETER_GEOSPATIAL_CREATE_COORDINATES_LATITUDE,
				latitudeColumnReference);
		map.put(Constants.PARAMETER_GEOSPATIAL_CREATE_COORDINATES_LONGITUDE,
				longitudeColumnReference);
		
		map.put(Constants.PARAMETER_GEOSPATIAL_CREATE_COORDINATES_USER,
				aslSession.getUsername());
		
		map.put(Constants.PARAMETER_GEOSPATIAL_CREATE_COORDINATES_RESOLUTION,
				new TDNumeric(geospatialCreateCoordinatesSession.getResolution()));
		
		
		switch(geospatialCreateCoordinatesSession.getType()){
		case C_SQUARE:
			map.put(Constants.PARAMETER_GEOSPATIAL_CREATE_COORDINATES_FEATURE,
					geospatialCreateCoordinatesSession.getType().getId());
			break;
		case OCEAN_AREA:
			map.put(Constants.PARAMETER_GEOSPATIAL_CREATE_COORDINATES_FEATURE,
					geospatialCreateCoordinatesSession.getType().getId());
			if(geospatialCreateCoordinatesSession.isHasQuadrant()){
				ColumnData quadrantColumn = geospatialCreateCoordinatesSession
						.getQuadrant();
				logger.debug("Quadrant Column: " + quadrantColumn);
				if (quadrantColumn == null) {
					logger.error("Error Creating Geospatial Coordinates: Quadrant Column is null");
					throw new TDGWTServiceException("No quadrant column set");
				}
				ColumnLocalId quadrantId = new ColumnLocalId(
						quadrantColumn.getColumnId());
				ColumnReference quadrantColumnReference = new ColumnReference(tId,
						quadrantId);

				
				
				map.put(Constants.PARAMETER_GEOSPATIAL_CREATE_COORDINATES_QUADRANT,
						quadrantColumnReference);
			} 
			break;
		default:
			break;
		
		}
	
		invocation = new OperationExecution(
				operationDefinition.getOperationId(), map);

		operationExecutionSpec.setOp(invocation);

	}

}
