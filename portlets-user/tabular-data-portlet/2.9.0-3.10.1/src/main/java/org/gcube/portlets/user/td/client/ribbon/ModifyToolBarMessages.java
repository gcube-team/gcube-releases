package org.gcube.portlets.user.td.client.ribbon;

import com.google.gwt.i18n.client.Messages;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public interface ModifyToolBarMessages extends Messages {

	@DefaultMessage("Rows")
	String rowsGroupHeadingText();

	@DefaultMessage("Replace")
	String replaceButton();

	@DefaultMessage("Replace rows")
	String replaceButtonToolTip();

	@DefaultMessage("Delete")
	String deleteButton();

	@DefaultMessage("Delete rows")
	String deleteButtonToolTip();

	@DefaultMessage("Add")
	String addRowButton();

	@DefaultMessage("Add row")
	String addRowButtonToolTip();

	@DefaultMessage("Filter")
	String filterButton();

	@DefaultMessage("Filter rows")
	String filterButtonToolTip();

	@DefaultMessage("Table")
	String tableGroupHeadingText();

	@DefaultMessage("Group By")
	String groupbyButton();

	@DefaultMessage("Perform data grouping")
	String groupbyButtonToolTip();

	@DefaultMessage("Time Aggregation")
	String timeAggregationButton();

	@DefaultMessage("Performs time aggregation")
	String timeAggregationButtonToolTip();

	@DefaultMessage("Union")
	String unionButton();

	@DefaultMessage("Merge tables data")
	String unionButtonToolTip();

	@DefaultMessage("Geospatial")
	String geospatialGroupHeadingText();

	@DefaultMessage("C-Square")
	String csquareButton();

	@DefaultMessage("Create C-Square Coordinates")
	String csquareButtonToolTip();

	@DefaultMessage("Downscale")
	String downscaleCSquareButton();

	@DefaultMessage("Downscale C-Square Coordinates")
	String downscaleCSquareButtonToolTip();

	@DefaultMessage("Ocean Area")
	String oceanAreaButton();

	@DefaultMessage("Create Ocean Area Coordinates")
	String oceanAreaButtonToolTip();

	@DefaultMessage("Geometry")
	String geometryGroupHeadingText();

	@DefaultMessage("Points")
	String pointButton();

	@DefaultMessage("Create Points")
	String pointButtonToolTip();

	// Menu Replace
	@DefaultMessage("Batch")
	String replaceBatchItem();

	@DefaultMessage("By Expression")
	String replaceByExpressionItem();

	@DefaultMessage("By External")
	String replaceByExternalColItem();

	// Menu Delete
	@DefaultMessage("Selected")
	String deleteSelectedRowsItem();

	@DefaultMessage("Duplicate")
	String deleteDuplicateItem();

	@DefaultMessage("By Expression")
	String deleteByExpressionItem();

}