package org.gcube.portlets.user.td.tablewidget.client;

import com.google.gwt.i18n.client.Messages;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public interface TableWidgetMessages extends Messages {

	@DefaultMessage("Error in clone operation! ")
	String errorInCloneFixed();

	@DefaultMessage("Select a Table Type...")
	String comboTableTypeEmptyText();

	@DefaultMessage("Apply")
	String btnApplyText();

	@DefaultMessage("Apply Table Type")
	String btnApplyToolTip();

	@DefaultMessage("Table Type")
	String comboTableTypeLabel();

	@DefaultMessage("Invalid table type!")
	String errorInvalidTableType();

	@DefaultMessage("Select a table type!")
	String attentionSelectATableType();

	@DefaultMessage("Error in invocation of Change Table Type operation.")
	String errorInChangeTableTypeOperationFixed();

	@DefaultMessage("Select a column...")
	String comboLatitudeEmptyText();

	@DefaultMessage("Select a column...")
	String comboLongitudeEmptyMessage();

	@DefaultMessage("Points")
	String columnFieldDefault();

	@DefaultMessage("Column Label")
	String columnFieldLabel();

	@DefaultMessage("Create")
	String btnCreatePointText();

	@DefaultMessage("Create Point")
	String btnCreatePointToolTip();

	@DefaultMessage("Longitude")
	String comboLongitudeLabel();

	@DefaultMessage("Latitude")
	String comboLatitudeLabel();

	@DefaultMessage("Select a column label!")
	String attentionSelectAColumnLabel();

	@DefaultMessage("Select Latitude!")
	String attentionSelectLatitude();

	@DefaultMessage("Select Longitude!")
	String attentionSelectLongitude();

	@DefaultMessage("Error creating a point: ")
	String errorCreatingAPointFixed();

	@DefaultMessage("Error retrieving columns of tabular resource: ")
	String errorRetrievingColumsOfTabularResourceFixed();

	@DefaultMessage("No Integer or Numeric column is present!")
	String attentionNoIntegerOrNumericColumnIsPresent();

	@DefaultMessage("Select a column...")
	String comboCSquareColumnEmptyText();

	@DefaultMessage("Select a resolution...")
	String comboDownscaleEmptyText();

	@DefaultMessage("Downscale")
	String btnDownscaleText();

	@DefaultMessage("Downscale C-Square")
	String btnDownscaleToolTip();

	@DefaultMessage("Column")
	String comboCSquareColumnLabel();

	@DefaultMessage("Resolution")
	String comboDownscaleLabel();

	@DefaultMessage("Select Resolution!")
	String attentionSelectResolution();

	@DefaultMessage("Select C-Square column!")
	String attentionSelectCSquareColumn();

	@DefaultMessage("No text column is present in the tabular resource. C-Square is a text column data type!")
	String attentionNoTextColumnIsPresentCSquareIsTextColumn();

	@DefaultMessage("Select a type...")
	String comboGsCoordinateTypeEmptyText();

	@DefaultMessage("Select a resolution...")
	String comboResolutionEmptyText();

	@DefaultMessage("Resolution")
	String comboResolutionLabel();

	@DefaultMessage("True")
	String hasQuadrantTrue();

	@DefaultMessage("False")
	String hasQuadrantFalse();

	@DefaultMessage("No Integer column is present in the tabular resource!")
	String attentionNoIntegerColumnIsPresent();

	@DefaultMessage("Has Quadrant")
	String hasQuadrantLabel();

	@DefaultMessage("Select true if you want select quadrant column")
	String hasQuadrantToolTip();

	@DefaultMessage("Select a column...")
	String comboQuadrantEmptyText();

	@DefaultMessage("Quadrant")
	String comboQuadrantLabel();

	@DefaultMessage("Create")
	String btnCreateCoordinatesText();

	@DefaultMessage("Create Geospatial Coordinates")
	String btnCreateCoordinatesToolTip();

	@DefaultMessage("Type")
	String comboGsCoordinatesTypeLabel();

	@DefaultMessage("Select Quadrant column!")
	String attentionSelectQuadrantColumn();

	@DefaultMessage("Select valid geospatial coordinates type!")
	String selectValidGeospatialCoordinatesType();

	@DefaultMessage("Invalid Geospatial Coordinates Type!")
	String attentionInvalidGeospatialCoordinateType();

	@DefaultMessage("Error creating geospatial coordinates: ")
	String errorCreatingGeospatialCoordinatesFixed();

	@DefaultMessage("Error retrieving current tabular resource id!")
	String errorRetrievingCurrentTabularResourceId();

	@DefaultMessage("Undo not applicable")
	String attentionUndoNotApplicable();

	@DefaultMessage("Date:")
	String dateFixed();

	@DefaultMessage("Description:")
	String descriptionFixed();

	@DefaultMessage("Step")
	String stepCol();

	@DefaultMessage("Date")
	String dateCol();

	@DefaultMessage("Undo")
	String rollBackCol();

	@DefaultMessage("Undo")
	String btnCellUndoTitle();

	@DefaultMessage("Empty")
	String gridHistoryEmptyText();

	@DefaultMessage("Error retrieving history!")
	String errorRetrievingHistory();

	@DefaultMessage("Denormalization")
	String denormalizationDialogHead();

	@DefaultMessage("Select a column...")
	String comboValueColumnEmptyText();

	@DefaultMessage("Value Column")
	String comboValueColumnLabel();

	@DefaultMessage("Select a column...")
	String comboAttributeColumnEmptyText();

	@DefaultMessage("Attribute Column")
	String comboAttributeColumnLabel();

	@DefaultMessage("Denormalize")
	String btnDenormalizeText();

	@DefaultMessage("Denormalize")
	String btnDenormalizeToolTip();

	@DefaultMessage("Attention no value column selected!")
	String attentionNoValueColumnSelected();

	@DefaultMessage("Attention no attribute column selected!")
	String attentionNoAttributeColumnSelected();

	@DefaultMessage("Normalization")
	String normalizeDialogHead();

	@DefaultMessage("Normalize")
	String btnNormalizeText();

	@DefaultMessage("Normalize")
	String btnNormalizeToolTip();

	@DefaultMessage("Normalized column")
	String normalizedColumnNameLabel();

	@DefaultMessage("Value column")
	String valueColumnNameLabel();

	@DefaultMessage("Columns to Normalize")
	String columnsToNormalizeLabel();

	@DefaultMessage("Error retrieving columns!")
	String errorRetrievingColumns();

	@DefaultMessage("This tabular resource has not Integer or Numeric columns, normalize is not applicable!")
	String attentionThisTabularResourceHasNotIntegerOrNumericColumnsNormalizeIsNotApplicable();

	@DefaultMessage("Attention no column selected!")
	String attentionNoColumnSelected();

}