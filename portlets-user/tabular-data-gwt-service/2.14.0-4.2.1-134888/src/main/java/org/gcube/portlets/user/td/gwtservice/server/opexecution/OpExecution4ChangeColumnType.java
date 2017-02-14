package org.gcube.portlets.user.td.gwtservice.server.opexecution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.metadata.column.DataLocaleMetadata;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.portlets.user.td.gwtservice.server.trservice.ColumnDataTypeMap;
import org.gcube.portlets.user.td.gwtservice.server.trservice.OperationDefinitionMap;
import org.gcube.portlets.user.td.gwtservice.shared.Constants;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.DimensionRow;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.mapping.ColumnMappingData;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.mapping.ColumnMappingList;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.type.ChangeColumnTypeSession;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.operations.OperationsId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnTypeCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operation Execution for change column type
 * 
 * @author "Giancarlo Panichi" email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class OpExecution4ChangeColumnType extends OpExecutionBuilder {
	protected static Logger logger = LoggerFactory
			.getLogger(OpExecution4ChangeColumnType.class);

	private TabularDataService service;
	private ChangeColumnTypeSession changeColumnTypeSession;

	public OpExecution4ChangeColumnType(TabularDataService service,
			ChangeColumnTypeSession changeColumnTypeSession) {
		this.service = service;
		this.changeColumnTypeSession = changeColumnTypeSession;
	}

	@Override
	public void buildOpEx() throws TDGWTServiceException {
		OperationExecution invocation = null;

		logger.debug(changeColumnTypeSession.toString());
		OperationDefinition operationDefinition;
		Map<String, Object> map = new HashMap<String, Object>();

		ColumnTypeCode type = changeColumnTypeSession.getColumnTypeCodeTarget();

		switch (type) {
		case ANNOTATION:
			operationDefinition = OperationDefinitionMap.map(
					OperationsId.ChangeToAnnotationColumn.toString(), service);
			invocation = new OperationExecution(changeColumnTypeSession
					.getColumnData().getColumnId(),
					operationDefinition.getOperationId(), map);
			break;
		case ATTRIBUTE:
			operationDefinition = OperationDefinitionMap.map(
					OperationsId.ChangeToAttributeColumn.toString(), service);

			map.put(Constants.PARAMETER_TARGET_DATA_TYPE, ColumnDataTypeMap
					.map(changeColumnTypeSession.getColumnDataTypeTarget()));
			if (changeColumnTypeSession.getValueDataFormat() != null) {
				map.put(Constants.PARAMETER_PERIOD_INPUT_FORMAT_ID,
						changeColumnTypeSession.getValueDataFormat().getId());
			}
			invocation = new OperationExecution(changeColumnTypeSession
					.getColumnData().getColumnId(),
					operationDefinition.getOperationId(), map);
			break;
		case CODE:
			operationDefinition = OperationDefinitionMap.map(
					OperationsId.ChangeToCodeColumn.toString(), service);
			invocation = new OperationExecution(changeColumnTypeSession
					.getColumnData().getColumnId(),
					operationDefinition.getOperationId(), map);
			break;
		case CODEDESCRIPTION:
			operationDefinition = OperationDefinitionMap.map(
					OperationsId.ChangeToCodeDescription.toString(), service);
			invocation = new OperationExecution(changeColumnTypeSession
					.getColumnData().getColumnId(),
					operationDefinition.getOperationId(), map);
			break;
		case CODENAME:
			DataLocaleMetadata locale = new DataLocaleMetadata(
					changeColumnTypeSession.getLocale());
			map.put(Constants.PARAMETER_ADDITIONAL_META, locale);
			operationDefinition = OperationDefinitionMap.map(
					OperationsId.ChangeToCodeName.toString(), service);
			invocation = new OperationExecution(changeColumnTypeSession
					.getColumnData().getColumnId(),
					operationDefinition.getOperationId(), map);
			break;
		case DIMENSION:
			operationDefinition = OperationDefinitionMap.map(
					OperationsId.ChangeToDimensionColumn.toString(), service);
			ColumnData col = changeColumnTypeSession
					.getCodelistColumnReference();
			logger.debug("ReferenceColumn To Set: " + col);
			ColumnLocalId cId = new ColumnLocalId(col.getColumnId());
			TRId trId = col.getTrId();
			logger.debug("trID: " + trId);
			long tabId;
			if (trId.isViewTable()) {
				tabId = new Long(trId.getReferenceTargetTableId());
			} else {
				tabId = new Long(trId.getTableId());
			}
			TableId tId = new TableId(tabId);
			ColumnReference columnReference = new ColumnReference(tId, cId);
			map.put(Constants.PARAMETER_REFERENCE_COLUMN, columnReference);

			ColumnMappingList columnMappingList = changeColumnTypeSession
					.getColumnMappingList();

			if (columnMappingList != null) {
				ArrayList<ColumnMappingData> columnMapping = columnMappingList
						.getMapping();
				if (columnMapping != null && columnMapping.size() > 0) {
					HashMap<Long, Long> mapping = new HashMap<Long, Long>();
					for (ColumnMappingData columnMappingData : columnMapping) {
						if (columnMappingData.getSourceArg() != null
								&& columnMappingData.getTargetArg() != null) {
							DimensionRow source = columnMappingData
									.getSourceArg();
							DimensionRow target = columnMappingData
									.getTargetArg();
							mapping.put(new Long(source.getRowId()), new Long(
									target.getRowId()));

						}

					}
					map.put(Constants.PARAMETER_COLUMN_MAPPING, mapping);
				}
			}

			invocation = new OperationExecution(changeColumnTypeSession
					.getColumnData().getColumnId(),
					operationDefinition.getOperationId(), map);
			break;
		case MEASURE:
			operationDefinition = OperationDefinitionMap.map(
					OperationsId.ChangeToMeasureColumn.toString(), service);

			map.put(Constants.PARAMETER_TARGET_DATA_TYPE, ColumnDataTypeMap
					.map(changeColumnTypeSession.getColumnDataTypeTarget()));
			if (changeColumnTypeSession.getValueDataFormat() != null) {
				map.put(Constants.PARAMETER_PERIOD_INPUT_FORMAT_ID,
						changeColumnTypeSession.getValueDataFormat().getId());
			}

			invocation = new OperationExecution(changeColumnTypeSession
					.getColumnData().getColumnId(),
					operationDefinition.getOperationId(), map);
			break;
		case TIMEDIMENSION:
			operationDefinition = OperationDefinitionMap.map(
					OperationsId.ChangeToTimeDimensionColumn.toString(),
					service);
			map.put(Constants.PARAMETER_PERIOD_FORMAT, changeColumnTypeSession
					.getPeriodDataType().getLabel());
			map.put(Constants.PARAMETER_PERIOD_INPUT_FORMAT_ID,
					changeColumnTypeSession.getValueDataFormat().getId());

			invocation = new OperationExecution(changeColumnTypeSession
					.getColumnData().getColumnId(),
					operationDefinition.getOperationId(), map);
			break;
		default:
			break;

		}

		operationExecutionSpec.setOp(invocation);

	}
}
