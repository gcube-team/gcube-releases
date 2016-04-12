package org.gcube.portlets.user.td.gwtservice.server.opexecution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.composite.text.Concat;
import org.gcube.data.analysis.tabulardata.expression.functions.Cast;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.column.type.AttributeColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.GeometryType;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.portlets.user.td.gwtservice.server.trservice.OperationDefinitionMap;
import org.gcube.portlets.user.td.gwtservice.shared.Constants;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.gwtservice.shared.geometry.GeometryCreatePointSession;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.operations.OperationsId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.gcube.data.analysis.tabulardata.expression.dsl.Comparators.*;
import static org.gcube.data.analysis.tabulardata.expression.dsl.Logicals.*;
import static org.gcube.data.analysis.tabulardata.expression.dsl.Types.*;

/**
 * Operation Execution for change table type
 * 
 * @author "Giancarlo Panichi" email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class OpExecution4GeometryCreatePoint extends OpExecutionBuilder {
	

	protected static Logger logger = LoggerFactory
			.getLogger(OpExecution4GeometryCreatePoint.class);

	private TabularDataService service;
	private GeometryCreatePointSession geometryCreatePointSession;

	public OpExecution4GeometryCreatePoint(TabularDataService service,
			GeometryCreatePointSession geometryCreatePointSession) {
		this.service = service;
		this.geometryCreatePointSession = geometryCreatePointSession;

	}

	@Override
	public void buildOpEx() throws TDGWTServiceException {
		logger.debug(geometryCreatePointSession.toString());
		ArrayList<OperationExecution> invocations = new ArrayList<OperationExecution>();

		//Retrieve longitude and latitude
		TRId trId = geometryCreatePointSession.getTrId();
		logger.debug("trID: " + trId);
		if (trId == null) {
			logger.error("Error Creating Geometry: trId is null");
			throw new TDGWTServiceException("No tabular resource set");
		}

		long tabId;
		if (trId.isViewTable()) {
			tabId = new Long(trId.getReferenceTargetTableId());
		} else {
			tabId = new Long(trId.getTableId());

		}
		TableId tId = new TableId(tabId);

		ColumnData latitudeColumn = geometryCreatePointSession.getLatitude();
		logger.debug("Latitude Column: " + latitudeColumn);
		if (latitudeColumn == null) {
			logger.error("Error creating Geometry: Latitude Column is null");
			throw new TDGWTServiceException("No latitude column set");
		}
		ColumnLocalId latitudeId = new ColumnLocalId(
				latitudeColumn.getColumnId());
		ColumnReference latitudeColumnReference = new ColumnReference(tId,
				latitudeId);

		ColumnData longitudeColumn = geometryCreatePointSession.getLongitude();
		logger.debug("Longitude Column: " + longitudeColumn);
		if (longitudeColumn == null) {
			logger.error("Error creating Geometry: Longitude Column is null");
			throw new TDGWTServiceException("No longitude column set");
		}
		ColumnLocalId longitudeId = new ColumnLocalId(
				longitudeColumn.getColumnId());
		ColumnReference longitudeColumnReference = new ColumnReference(tId,
				longitudeId);

		
		
		//Validations
		OperationDefinition operationValidation;
		Map<String, Object> validationParameters = new HashMap<String, Object>();
		
		operationValidation = OperationDefinitionMap.map(
				OperationsId.ExpressionValidation.toString(), service);
		
		validationParameters.put(Constants.PARAMETER_EXPRESSION_VALIDATION_TITLE, "Coordinates validation");
		String description = new String("Longitude value must be between [-180, 180], Latitude value must be between [-90, 90]");
		Expression expression = and(lessEq(longitudeColumnReference, numeric(180)), greaterEq(longitudeColumnReference, numeric(-180)), lessEq(latitudeColumnReference, numeric(90)), greaterEq(latitudeColumnReference, numeric(-90)));
		validationParameters.put(Constants.PARAMETER_EXPRESSION_VALIDATION_DESCRIPTION, description);
		validationParameters.put(Constants.PARAMETER_EXPRESSION_VALIDATION_EXPRESSION, expression);
		
		invocations.add(new OperationExecution(
				operationValidation.getOperationId(), validationParameters));
		
		
		
		//Add Column
		Expression point = new Cast(new Concat(new TDText("POINT("), new Concat(
				longitudeColumnReference, new Concat(new TDText(" "),
						new Concat(latitudeColumnReference, new TDText(")"))))),new GeometryType());
		
		OperationDefinition operationDefinition;
		Map<String, Object> map = new HashMap<String, Object>();
		
		operationDefinition = OperationDefinitionMap.map(
				OperationsId.AddColumn.toString(), service);

		map.put(Constants.PARAMETER_ADD_COLUMN_COLUMN_TYPE,
				new AttributeColumnType());
		map.put(Constants.PARAMETER_ADD_COLUMN_DATA_TYPE, new GeometryType());
		map.put(Constants.PARAMETER_ADD_COLUMN_LABEL,
				new ImmutableLocalizedText(geometryCreatePointSession
						.getColumnLabel()));
		map.put(Constants.PARAMETER_ADD_COLUMN_VALUE, point);
		invocations.add(new OperationExecution(
				operationDefinition.getOperationId(), map));
		
		//Set ops
		operationExecutionSpec.setOps(invocations);

	}

}
