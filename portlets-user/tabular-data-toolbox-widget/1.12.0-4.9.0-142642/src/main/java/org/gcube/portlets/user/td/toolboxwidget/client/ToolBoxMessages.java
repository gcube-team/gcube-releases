package org.gcube.portlets.user.td.toolboxwidget.client;

import com.google.gwt.i18n.client.Messages;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public interface ToolBoxMessages extends Messages {

	//
	@DefaultMessage("Help")
	String helpItem();

	@DefaultMessage("Properties")
	String propertiesItem();

	@DefaultMessage("Background")
	String monitorBackgroundPanelItem();

	@DefaultMessage("History")
	String historyPanelItem();

	@DefaultMessage("Column Type")
	String changeColumnTypeItem();

	@DefaultMessage("Add Column")
	String addColumnItem();

	@DefaultMessage("Column Delete")
	String deleteColumnItem();

	@DefaultMessage("Column Split")
	String splitColumnItem();

	@DefaultMessage("Column Merge")
	String mergeColumnItem();

	@DefaultMessage("Group By")
	String groupByItem();

	@DefaultMessage("Time Aggregation")
	String timeAggregationItem();

	@DefaultMessage("Position Column")
	String positionColumnItem();

	@DefaultMessage("Column Label")
	String labelColumnItem();

	@DefaultMessage("Table Type")
	String changeTableTypeItem();

	@DefaultMessage("Validations")
	String validationsTableItem();

	@DefaultMessage("Validations")
	String validationsTasksItem();

	@DefaultMessage("Resources")
	String resourcesItem();

	@DefaultMessage("Coordinates")
	String geospatialCreateCoordinatesItem();

	@DefaultMessage("Points")
	String geometryCreatePointItem();

	@DefaultMessage("Downscale C-Square")
	String downscaleCSquareItem();

	@DefaultMessage("Duplicate Detection")
	String duplicatesRowsDetectionItem();

	@DefaultMessage("Delete Duplicate")
	String duplicatesRowsDeleteItem();

	@DefaultMessage("Normalize")
	String normalizeItem();

	@DefaultMessage("Denormalize")
	String denormalizeItem();
	
}