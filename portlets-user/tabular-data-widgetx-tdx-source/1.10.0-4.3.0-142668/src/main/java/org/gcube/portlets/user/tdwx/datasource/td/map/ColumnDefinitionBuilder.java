package org.gcube.portlets.user.tdwx.datasource.td.map;

import java.util.List;

import org.gcube.data.analysis.tabulardata.metadata.NoSuchMetadataException;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.metadata.column.DataLocaleMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.column.PeriodTypeMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.column.ValidationReferencesMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.column.ViewColumnMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.NamesMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.Validation;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ValidationsMetadata;
import org.gcube.data.analysis.tabulardata.model.relationship.ColumnRelationship;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.time.PeriodType;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnTypeCode;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.RelationshipData;
import org.gcube.portlets.user.tdwx.server.datasource.DataSourceXException;
import org.gcube.portlets.user.tdwx.shared.model.ColumnDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ColumnDefinitionBuilder {
	private static final String PRIMARY_KEY_COLUMN = "id";

	private Logger logger = LoggerFactory
			.getLogger(ColumnDefinitionBuilder.class);

	private TabularDataService service;
	private Table serviceTable;
	private Column serviceColumn;
	private String tooltipMessage;
	private String columnName;
	private String columnLocalId;
	private boolean visible;
	private boolean editable;
	private org.gcube.portlets.user.tdwx.shared.model.ColumnType type;
	private String columnLabel;
	private String locale;
	private int ordinalPosition;

	private PeriodTypeMetadata periodTypeMetadata;
	protected PeriodType periodType;

	private RelationshipData relationshipData;

	private String sourceTableDimensionColumnId;
	private String targetTableColumnId;
	private long targetTableId;
	private boolean viewColumn;

	public ColumnDefinitionBuilder(TabularDataService service,
			Table serviceTable, Column serviceColumn, int ordinalPosition)
			throws DataSourceXException {

		this.service = service;
		this.serviceTable = serviceTable;
		this.serviceColumn = serviceColumn;
		this.ordinalPosition = ordinalPosition;

		visible = true;
		type = org.gcube.portlets.user.tdwx.shared.model.ColumnType.USER;
		editable = true;
		ColumnType ct = serviceColumn.getColumnType();
		if (ColumnTypeMap.isIdColumnType(ct)) {
			columnLabel = PRIMARY_KEY_COLUMN;
			visible = false;
			editable = false;
			type = org.gcube.portlets.user.tdwx.shared.model.ColumnType.COLUMNID;
		} else {
			periodTypeMetadata = null;
			if (serviceColumn.contains(PeriodTypeMetadata.class)) {
				periodTypeMetadata = serviceColumn
						.getMetadata(PeriodTypeMetadata.class);
				periodType = periodTypeMetadata.getType();
			}

			if (ColumnTypeMap.isValidationColumnType(ct)) {
				type = org.gcube.portlets.user.tdwx.shared.model.ColumnType.VALIDATION;
				editable = false;
			} else {
				if (ColumnTypeMap.isTimeDimensionColumnType(ct)) {
					type = org.gcube.portlets.user.tdwx.shared.model.ColumnType.TIMEDIMENSION;
					visible = false;
					retrieveRelationship();
				} else {
					if (ColumnTypeMap.isDimensionColumnType(ct)) {
						type = org.gcube.portlets.user.tdwx.shared.model.ColumnType.DIMENSION;
						visible = false;
						retrieveRelationship();
					} else {
						if (ColumnTypeMap.isMeasureColumnType(ct)) {
							type = org.gcube.portlets.user.tdwx.shared.model.ColumnType.MEASURE;
						} else {
							if (ColumnTypeMap.isCodeColumnType(ct)) {
								type = org.gcube.portlets.user.tdwx.shared.model.ColumnType.CODE;
							}

						}
					}
				}
			}

			if (serviceColumn.contains(ViewColumnMetadata.class)) {

				retrieveViewColumnMetadata();

				visible = false;
				viewColumn = true;
			} else {
				viewColumn = false;
			}

			NamesMetadata labelsMetadata = null;
			try {
				labelsMetadata = serviceColumn.getMetadata(NamesMetadata.class);
			} catch (NoSuchMetadataException e) {
				logger.debug("labelMetadata: NoSuchMetadataException "
						+ e.getLocalizedMessage());
			}

			if (labelsMetadata == null) {
				columnLabel = "nolabel";
			} else {
				LocalizedText cl = null;
				cl = labelsMetadata.getTextWithLocale("en");
				if (cl == null) {
					columnLabel = "nolabel";
					logger.debug("ColumnLabel no label in en");
				} else {
					columnLabel = cl.getValue();
					if (columnLabel == null || columnLabel.isEmpty()) {
						columnLabel = "nolabel";
					}
				}
			}

			DataLocaleMetadata dataLocaleMetadata = null;
			try {
				dataLocaleMetadata = serviceColumn
						.getMetadata(DataLocaleMetadata.class);
			} catch (NoSuchMetadataException e) {
				logger.debug("DataLocaleMetadata: NoSuchMetadataException "
						+ e.getLocalizedMessage());
			}

			if (dataLocaleMetadata == null) {
				logger.debug("No DataLocaleMetadata");
				locale = "";
			} else {
				locale = dataLocaleMetadata.getLocale();
			}

		}

	}

	private void retrieveViewColumnMetadata() throws DataSourceXException {
		ViewColumnMetadata viewMetadata = serviceColumn
				.getMetadata(ViewColumnMetadata.class);
		// logger.debug("ViewColumnMetadata: " + viewMetadata.toString());

		Column sourceColumn = serviceTable.getColumnById(viewMetadata
				.getSourceTableDimensionColumnId());
		if (sourceColumn.getColumnType().getCode()
				.compareTo(ColumnTypeCode.TIMEDIMENSION.toString()) == 0) {
			type = org.gcube.portlets.user.tdwx.shared.model.ColumnType.VIEWCOLUMN_OF_TIMEDIMENSION;
			PeriodTypeMetadata periodTypeMetadataSourceColumn = null;
			if (sourceColumn.contains(PeriodTypeMetadata.class)) {
				periodTypeMetadataSourceColumn = sourceColumn
						.getMetadata(PeriodTypeMetadata.class);

				Table timeTable = service
						.getTimeTable(periodTypeMetadataSourceColumn.getType());

				if (timeTable == null || timeTable.getId() == null) {
					throw new DataSourceXException(
							"Error retrieving Time Table: " + timeTable);
				}
				// logger.debug("Time Table Id: " + timeTable.getId());

				Column timeColumn = timeTable
						.getColumnByName(periodTypeMetadataSourceColumn
								.getType().getName());

				sourceTableDimensionColumnId = viewMetadata
						.getSourceTableDimensionColumnId().getValue();

				targetTableColumnId = timeColumn.getLocalId().getValue();

				targetTableId = timeTable.getId().getValue();

			} else {
				logger.error("Error retrieving Time Table for view column:"
						+ serviceColumn
						+ " , source column do not have a PeriodTypeMetadata: "
						+ sourceColumn);
				throw new DataSourceXException(
						"Error retrieving Time Table, source column do not have a PeriodTypeMetadata");
			}

		} else {
			type = org.gcube.portlets.user.tdwx.shared.model.ColumnType.VIEWCOLUMN_OF_DIMENSION;
			sourceTableDimensionColumnId = viewMetadata
					.getSourceTableDimensionColumnId().getValue();
			targetTableColumnId = viewMetadata.getTargetTableColumnId()
					.getValue();
			targetTableId = viewMetadata.getTargetTableId().getValue();

		}

	}

	private void retrieveRelationship() throws DataSourceXException {
		if (serviceColumn.getColumnType().getCode()
				.compareTo(ColumnTypeCode.TIMEDIMENSION.toString()) == 0) {
			Table timeTable = service
					.getTimeTable(periodTypeMetadata.getType());
			if (timeTable == null || timeTable.getId() == null) {
				throw new DataSourceXException("Error retrieving Time Table: "
						+ timeTable);
			}
			// logger.debug("Time Table Id: " + timeTable.getId());
			Column timeColumn = timeTable.getColumnByName(periodTypeMetadata
					.getType().getName());
			relationshipData = new RelationshipData(timeTable.getId()
					.getValue(), timeColumn.getLocalId().getValue());

		} else {
			ColumnRelationship rel = serviceColumn.getRelationship();
			if (rel != null) {
				relationshipData = new RelationshipData(rel.getTargetTableId()
						.getValue(), rel.getTargetColumnId().getValue());
			} else {
				logger.error("No valid relationship for column: "
						+ serviceColumn.toString());
				throw new DataSourceXException(
						"Column  has not valid relationship");
			}

		}

	}

	public boolean isVisible() {
		return visible;
	}

	public org.gcube.portlets.user.tdwx.shared.model.ColumnType getType() {
		return type;
	}

	public String getColumnLabel() {
		return columnLabel;
	}

	public String getColumnName() {
		columnName = serviceColumn.getName();
		return columnName;
	}

	public String getColumnLocalId() {
		columnLocalId = serviceColumn.getLocalId().getValue();
		return columnLocalId;
	}

	public ValidationReferencesMetadata getValidationReferencesMetadata() {
		ValidationReferencesMetadata refs = null;
		try {
			refs = serviceColumn
					.getMetadata(ValidationReferencesMetadata.class);
		} catch (NoSuchMetadataException e) {
			logger.debug("ValidationReferencesMetadata: NoSuchMetadataException "
					+ e.getLocalizedMessage());
		}

		return refs;
	}

	public String getTooltipMessage() {
		String valids = new String();
		ValidationsMetadata val = null;
		try {
			val = serviceColumn.getMetadata(ValidationsMetadata.class);
		} catch (NoSuchMetadataException e) {
			/*
			 * logger.debug("No validatationsMetadata present: " +
			 * e.getLocalizedMessage());
			 */
		}

		if (val != null) {
			List<Validation> listValidationsMetadata = ((ValidationsMetadata) val)
					.getValidations();
			for (Validation validation : listValidationsMetadata) {
				valids.concat(validation.getDescription() + "\n");
			}
		}
		tooltipMessage = valids;

		return tooltipMessage;
	}

	public ColumnDefinition build() {
		ColumnDefinition columnDefinition = new ColumnDefinition(
				getColumnName(), getColumnLocalId(), getColumnLabel());
		columnDefinition.setLocale(locale);

		columnDefinition.setRelationshipData(relationshipData);
		columnDefinition.setViewColumn(viewColumn);
		columnDefinition.setTargetTableId(targetTableId);
		columnDefinition.setTargetTableColumnId(targetTableColumnId);
		columnDefinition
				.setSourceTableDimensionColumnId(sourceTableDimensionColumnId);

		DataType dataType = serviceColumn.getDataType();
		columnDefinition.setVisible(visible);
		columnDefinition.setEditable(editable);
		columnDefinition.setValueType(DataTypeMap.getValueType(dataType));
		columnDefinition.setType(getType());
		columnDefinition.setPosition(ordinalPosition);
		columnDefinition.setTooltipMessage(getTooltipMessage());
		columnDefinition.setColumnTypeName(serviceColumn.getColumnType()
				.getName());
		columnDefinition.setColumnDataType(serviceColumn.getDataType()
				.getName());
		columnDefinition.setWidth(100);
		return columnDefinition;
	}

}
