package org.gcube.portlets.user.td.gwtservice.server.opexecution;

import java.util.HashMap;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.model.metadata.column.DataLocaleMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.portlets.user.td.gwtservice.server.trservice.ColumnDataTypeMap;
import org.gcube.portlets.user.td.gwtservice.server.trservice.ColumnTypeCodeMap;
import org.gcube.portlets.user.td.gwtservice.server.trservice.OperationDefinitionMap;
import org.gcube.portlets.user.td.gwtservice.server.trservice.TDTypeValueMap;
import org.gcube.portlets.user.td.gwtservice.shared.Constants;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.AddColumnSession;
import org.gcube.portlets.user.td.widgetcommonevent.shared.operations.OperationsId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnDataType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnMockUp;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnTypeCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operation Execution for add column
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class OpExecution4AddColumn extends OpExecutionBuilder {
	private static Logger logger = LoggerFactory
			.getLogger(OpExecution4AddColumn.class);

	private TabularDataService service;
	private AddColumnSession addColumnSession;
	private Expression expression;

	public OpExecution4AddColumn(TabularDataService service,
			AddColumnSession addColumnSession, Expression expression) {
		this.service = service;
		this.addColumnSession = addColumnSession;
		this.expression = expression;
	}

	@Override
	public void buildOpEx() throws TDGWTServiceException {
		logger.debug(addColumnSession.toString());

		OperationExecution invocation = null;

		OperationDefinition operationDefinition = OperationDefinitionMap.map(
				OperationsId.AddColumn.toString(), service);

		Map<String, Object> map = new HashMap<String, Object>();

		ColumnMockUp defNewColumn = addColumnSession.getColumnMockUp();

		ColumnTypeCode type = defNewColumn.getColumnType();

		switch (type) {
		case ANNOTATION:
			map.put(Constants.PARAMETER_ADD_COLUMN_COLUMN_TYPE,
					ColumnTypeCodeMap.getColumnType(defNewColumn
							.getColumnType()));
			map.put(Constants.PARAMETER_ADD_COLUMN_DATA_TYPE,
					ColumnDataTypeMap.map(ColumnDataType.Text));
			map.put(Constants.PARAMETER_ADD_COLUMN_LABEL,
					new ImmutableLocalizedText(defNewColumn.getLabel()));

			if (defNewColumn.hasExpression()) {
				map.put(Constants.PARAMETER_ADD_COLUMN_VALUE, expression);
			} else {
				if (defNewColumn.getDefaultValue() != null
						&& !defNewColumn.getDefaultValue().isEmpty()) {
					map.put(Constants.PARAMETER_ADD_COLUMN_VALUE,
							TDTypeValueMap.map(ColumnDataType.Text,
									defNewColumn.getDefaultValue()));
				} 

			}

			invocation = new OperationExecution(
					operationDefinition.getOperationId(), map);
			break;
		case ATTRIBUTE:
			map.put(Constants.PARAMETER_ADD_COLUMN_COLUMN_TYPE,
					ColumnTypeCodeMap.getColumnType(defNewColumn
							.getColumnType()));
			map.put(Constants.PARAMETER_ADD_COLUMN_DATA_TYPE,
					ColumnDataTypeMap.map(defNewColumn.getColumnDataType()));
			map.put(Constants.PARAMETER_ADD_COLUMN_LABEL,
					new ImmutableLocalizedText(defNewColumn.getLabel()));
			if (defNewColumn.hasExpression()) {
				map.put(Constants.PARAMETER_ADD_COLUMN_VALUE, expression);
			} else {
				if (defNewColumn.getDefaultValue() != null
						&& !defNewColumn.getDefaultValue().isEmpty()) {
					map.put(Constants.PARAMETER_ADD_COLUMN_VALUE,
							TDTypeValueMap.map(
									defNewColumn.getColumnDataType(),
									defNewColumn.getDefaultValue()));
				} 
			}
			invocation = new OperationExecution(
					operationDefinition.getOperationId(), map);
			break;
		case CODE:
			map.put(Constants.PARAMETER_ADD_COLUMN_COLUMN_TYPE,
					ColumnTypeCodeMap.getColumnType(defNewColumn
							.getColumnType()));
			map.put(Constants.PARAMETER_ADD_COLUMN_DATA_TYPE,
					ColumnDataTypeMap.map(ColumnDataType.Text));
			map.put(Constants.PARAMETER_ADD_COLUMN_LABEL,
					new ImmutableLocalizedText(defNewColumn.getLabel()));
			if (defNewColumn.hasExpression()) {
				map.put(Constants.PARAMETER_ADD_COLUMN_VALUE, expression);
			} else {
				if (defNewColumn.getDefaultValue() != null
						&& !defNewColumn.getDefaultValue().isEmpty()) {
					map.put(Constants.PARAMETER_ADD_COLUMN_VALUE,
							TDTypeValueMap.map(ColumnDataType.Text,
									defNewColumn.getDefaultValue()));
				}
			}
			invocation = new OperationExecution(
					operationDefinition.getOperationId(), map);
			break;
		case CODEDESCRIPTION:
			map.put(Constants.PARAMETER_ADD_COLUMN_COLUMN_TYPE,
					ColumnTypeCodeMap.getColumnType(defNewColumn
							.getColumnType()));
			map.put(Constants.PARAMETER_ADD_COLUMN_DATA_TYPE,
					ColumnDataTypeMap.map(ColumnDataType.Text));
			map.put(Constants.PARAMETER_ADD_COLUMN_LABEL,
					new ImmutableLocalizedText(defNewColumn.getLabel()));
			if (defNewColumn.hasExpression()) {
				map.put(Constants.PARAMETER_ADD_COLUMN_VALUE, expression);
			} else {
				if (defNewColumn.getDefaultValue() != null
						&& !defNewColumn.getDefaultValue().isEmpty()) {
					map.put(Constants.PARAMETER_ADD_COLUMN_VALUE,
							TDTypeValueMap.map(ColumnDataType.Text,
									defNewColumn.getDefaultValue()));
				}
			}
			invocation = new OperationExecution(
					operationDefinition.getOperationId(), map);
			break;
		case CODENAME:
			map.put(Constants.PARAMETER_ADD_COLUMN_COLUMN_TYPE,
					ColumnTypeCodeMap.getColumnType(defNewColumn
							.getColumnType()));
			map.put(Constants.PARAMETER_ADD_COLUMN_DATA_TYPE,
					ColumnDataTypeMap.map(ColumnDataType.Text));
			map.put(Constants.PARAMETER_ADD_COLUMN_LABEL,
					new ImmutableLocalizedText(defNewColumn.getLabel()));
			
			map.put(Constants.PARAMETER_ADD_COLUMN_META, new DataLocaleMetadata(defNewColumn.getLocaleName()));
			
			if (defNewColumn.hasExpression()) {
				map.put(Constants.PARAMETER_ADD_COLUMN_VALUE, expression);
			} else {
				map.put(Constants.PARAMETER_ADD_COLUMN_VALUE,
						TDTypeValueMap.map(ColumnDataType.Text,
								defNewColumn.getDefaultValue()));
			}
			invocation = new OperationExecution(
					operationDefinition.getOperationId(), map);
			break;
		case DIMENSION:
			break;
		case MEASURE:
			map.put(Constants.PARAMETER_ADD_COLUMN_COLUMN_TYPE,
					ColumnTypeCodeMap.getColumnType(defNewColumn
							.getColumnType()));
			map.put(Constants.PARAMETER_ADD_COLUMN_DATA_TYPE,
					ColumnDataTypeMap.map(defNewColumn.getColumnDataType()));
			map.put(Constants.PARAMETER_ADD_COLUMN_LABEL,
					new ImmutableLocalizedText(defNewColumn.getLabel()));
			if (defNewColumn.hasExpression()) {
				map.put(Constants.PARAMETER_ADD_COLUMN_VALUE, expression);

			} else {
				if (defNewColumn.getDefaultValue() != null
						&& !defNewColumn.getDefaultValue().isEmpty()) {
					map.put(Constants.PARAMETER_ADD_COLUMN_VALUE,
							TDTypeValueMap.map(
									defNewColumn.getColumnDataType(),
									defNewColumn.getDefaultValue()));
				}
			}
			invocation = new OperationExecution(
					operationDefinition.getOperationId(), map);
			break;
		case TIMEDIMENSION:
			break;
		default:
			break;

		}

		operationExecutionSpec.setOp(invocation);

	}

}
