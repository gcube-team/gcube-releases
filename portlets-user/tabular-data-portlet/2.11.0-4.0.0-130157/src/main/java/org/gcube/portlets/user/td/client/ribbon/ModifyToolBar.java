/**
 * 
 */
package org.gcube.portlets.user.td.client.ribbon;

import org.gcube.portlets.user.td.client.resource.TabularDataResources;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.RibbonEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.UIStateEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.RibbonType;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.UIStateType;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.ButtonCell.ButtonArrowAlign;
import com.sencha.gxt.cell.core.client.ButtonCell.ButtonScale;
import com.sencha.gxt.cell.core.client.ButtonCell.IconAlign;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.button.ButtonGroup;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class ModifyToolBar {

	private EventBus eventBus;
	private ToolBar toolBar;

	// Rows
	private TextButton replaceButton;
	private TextButton deleteButton;
	private TextButton addRowButton;
	private TextButton filterButton;
	

	// Table
	private TextButton unionButton;
	private TextButton groupbyButton;
	private TextButton timeAggregationButton;

	// Geospatial
	private TextButton csquareButton;
	private TextButton oceanAreaButton;
	private TextButton downscaleCSquareButton;

	// Geometry
	private TextButton pointButton;

	// Menu

	// Replace Menu
	private MenuItem replaceBatchItem;
	private MenuItem replaceByExpressionItem;
	private MenuItem replaceByExternalColItem;
	// Delete Menu

	private MenuItem deleteSelectedRowsItem;
	private MenuItem deleteDuplicateItem;
	private MenuItem deleteByExpressionItem;
	private ModifyToolBarMessages msgs;

	public ModifyToolBar(EventBus eventBus) {
		this.eventBus = eventBus;
		msgs = GWT.create(ModifyToolBarMessages.class);
		build();
	}

	public ToolBar getToolBar() {
		return toolBar;
	}

	protected void build() {
		toolBar = new ToolBar();
		toolBar.setSpacing(1);
		toolBar.setEnableOverflow(false);

		// Tools Group
		ButtonGroup rowsGroup = new ButtonGroup();
		rowsGroup.setId("Rows");
		rowsGroup.setStyleName("ribbon");
		rowsGroup.setHeadingText(msgs.rowsGroupHeadingText());
		rowsGroup.enable();
		toolBar.add(rowsGroup);

		FlexTable rowsLayout = new FlexTable();
		rowsGroup.add(rowsLayout);

		replaceButton = new TextButton(msgs.replaceButton(),
				TabularDataResources.INSTANCE.tableReplaceRows32());
		replaceButton.disable();
		replaceButton.setScale(ButtonScale.LARGE);
		replaceButton.setIconAlign(IconAlign.TOP);
		replaceButton.setToolTip(msgs.replaceButtonToolTip());
		replaceButton.setArrowAlign(ButtonArrowAlign.RIGHT);
		replaceButton.setMenu(createReplaceMenu());
		
		rowsLayout.setWidget(0, 0, replaceButton);
		rowsLayout.getFlexCellFormatter().setRowSpan(0, 0, 2);

		deleteButton = new TextButton(msgs.deleteButton(),
				TabularDataResources.INSTANCE.tableRowDelete32());
		deleteButton.disable();
		deleteButton.setScale(ButtonScale.LARGE);
		deleteButton.setIconAlign(IconAlign.TOP);
		deleteButton.setToolTip(msgs.deleteButtonToolTip());
		deleteButton.setArrowAlign(ButtonArrowAlign.RIGHT);
		deleteButton.setMenu(createDeleteMenu());

		rowsLayout.setWidget(0, 1, deleteButton);
		rowsLayout.getFlexCellFormatter().setRowSpan(0, 1, 2);

		
		addRowButton = new TextButton(msgs.addRowButton(),
				TabularDataResources.INSTANCE.rowInsert32());
		addRowButton.disable();
		addRowButton.setScale(ButtonScale.LARGE);
		addRowButton.setIconAlign(IconAlign.TOP);
		addRowButton.setToolTip(msgs.addRowButtonToolTip());
		addRowButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		addRowButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.ROW_ADD));
			}
		});

		rowsLayout.setWidget(0, 2, addRowButton);
		rowsLayout.getFlexCellFormatter().setRowSpan(0, 2, 2);
		
		
		filterButton = new TextButton(msgs.filterButton(),
				TabularDataResources.INSTANCE.filter32());
		filterButton.disable();
		filterButton.setScale(ButtonScale.LARGE);
		filterButton.setIconAlign(IconAlign.TOP);
		filterButton.setToolTip(msgs.filterButtonToolTip());
		filterButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		filterButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.COLUMN_FILTER));
			}
		});

		rowsLayout.setWidget(0, 3, filterButton);
		rowsLayout.getFlexCellFormatter().setRowSpan(0, 3, 2);


		cleanCells(rowsLayout.getElement());

		// Table Group
		ButtonGroup tableGroup = new ButtonGroup();
		tableGroup.setId("Table");
		tableGroup.setStyleName("ribbon");
		tableGroup.setHeadingText(msgs.tableGroupHeadingText());
		tableGroup.enable();
		toolBar.add(tableGroup);

		FlexTable tableLayout = new FlexTable();
		tableGroup.add(tableLayout);

		groupbyButton = new TextButton(msgs.groupbyButton(),
				TabularDataResources.INSTANCE.group32());
		groupbyButton.disable();
		groupbyButton.setScale(ButtonScale.LARGE);
		groupbyButton.setIconAlign(IconAlign.TOP);
		groupbyButton.setToolTip(msgs.groupbyButtonToolTip());
		groupbyButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		groupbyButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.TABLE_GROUPBY));
			}
		});

		tableLayout.setWidget(0, 0, groupbyButton);
		tableLayout.getFlexCellFormatter().setRowSpan(0, 0, 2);
		
		timeAggregationButton = new TextButton(msgs.timeAggregationButton(),
				TabularDataResources.INSTANCE.timeAggregate32());
		timeAggregationButton.disable();
		timeAggregationButton.setScale(ButtonScale.LARGE);
		timeAggregationButton.setIconAlign(IconAlign.TOP);
		timeAggregationButton.setToolTip(msgs.timeAggregationButtonToolTip());
		timeAggregationButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		timeAggregationButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(
						RibbonType.TABLE_TIME_AGGREGATE));
			}
		});

		tableLayout.setWidget(0, 1, timeAggregationButton);
		tableLayout.getFlexCellFormatter().setRowSpan(0, 1, 2);
		
		unionButton = new TextButton(msgs.unionButton(),
				TabularDataResources.INSTANCE.union32());
		unionButton.disable();
		unionButton.setScale(ButtonScale.LARGE);
		unionButton.setIconAlign(IconAlign.TOP);
		unionButton.setToolTip(msgs.unionButtonToolTip());
		unionButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		unionButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.TABLE_UNION));
			}
		});

		tableLayout.setWidget(0, 2, unionButton);
		tableLayout.getFlexCellFormatter().setRowSpan(0, 2, 2);
		
		cleanCells(tableLayout.getElement());

		// Geospatial Group
		ButtonGroup geospatialGroup = new ButtonGroup();
		geospatialGroup.setId("Geospatial");
		geospatialGroup.setStyleName("ribbon");
		geospatialGroup.setHeadingText(msgs.geospatialGroupHeadingText());
		geospatialGroup.enable();
		geospatialGroup.setVisible(true);
		toolBar.add(geospatialGroup);

		FlexTable geospatialLayout = new FlexTable();
		geospatialGroup.add(geospatialLayout);

		csquareButton = new TextButton(msgs.csquareButton(),
				TabularDataResources.INSTANCE.geospatialCSquare32());
		csquareButton.disable();
		csquareButton.setScale(ButtonScale.LARGE);
		csquareButton.setIconAlign(IconAlign.TOP);
		csquareButton.setToolTip(msgs.csquareButtonToolTip());
		csquareButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		csquareButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(
						RibbonType.GEOSPATIAL_CSQUARE));
			}
		});

		geospatialLayout.setWidget(0, 0, csquareButton);
		geospatialLayout.getFlexCellFormatter().setRowSpan(0, 0, 2);

		downscaleCSquareButton = new TextButton(msgs.downscaleCSquareButton(),
				TabularDataResources.INSTANCE.downscaleCSquare32());
		downscaleCSquareButton.disable();
		downscaleCSquareButton.setScale(ButtonScale.LARGE);
		downscaleCSquareButton.setIconAlign(IconAlign.TOP);
		downscaleCSquareButton.setToolTip(msgs.downscaleCSquareButtonToolTip());
		downscaleCSquareButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		downscaleCSquareButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.DOWNSCALE_CSQUARE));
			}
		});

		geospatialLayout.setWidget(0, 1, downscaleCSquareButton);
		geospatialLayout.getFlexCellFormatter().setRowSpan(0, 1, 2);
		
		
		oceanAreaButton = new TextButton(msgs.oceanAreaButton(),
				TabularDataResources.INSTANCE.geospatialOceanArea32());
		oceanAreaButton.disable();
		oceanAreaButton.setScale(ButtonScale.LARGE);
		oceanAreaButton.setIconAlign(IconAlign.TOP);
		oceanAreaButton.setToolTip(msgs.oceanAreaButtonToolTip());
		oceanAreaButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		oceanAreaButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(
						RibbonType.GEOSPATIAL_OCEAN_AREA));
			}
		});

		geospatialLayout.setWidget(0, 2, oceanAreaButton);
		geospatialLayout.getFlexCellFormatter().setRowSpan(0, 2, 2);

		cleanCells(geospatialLayout.getElement());
		
		// Geometry Group
		ButtonGroup geometryGroup = new ButtonGroup();
		geometryGroup.setId("Geometry");
		geometryGroup.setStyleName("ribbon");
		geometryGroup.setHeadingText(msgs.geometryGroupHeadingText());
		geometryGroup.enable();
		geometryGroup.setVisible(true);
		toolBar.add(geometryGroup);

		FlexTable geometryLayout = new FlexTable();
		geometryGroup.add(geometryLayout);

		pointButton = new TextButton(msgs.pointButton(),
				TabularDataResources.INSTANCE.geometryPoint32());
		pointButton.disable();
		pointButton.setScale(ButtonScale.LARGE);
		pointButton.setIconAlign(IconAlign.TOP);
		pointButton.setToolTip(msgs.pointButtonToolTip());
		pointButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		pointButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new RibbonEvent(RibbonType.GEOMETRY_POINT));
			}
		});

		geometryLayout.setWidget(0, 0, pointButton);
		geometryLayout.getFlexCellFormatter().setRowSpan(0, 0, 2);

		cleanCells(geospatialLayout.getElement());

		eventBus.addHandler(UIStateEvent.TYPE,
				new UIStateEvent.UIStateHandler() {

					public void onUIState(UIStateEvent event) {
						setUI(event);

					}
				});

	}

	private Menu createReplaceMenu() {
		Menu menuReplace = new Menu();
		replaceBatchItem = new MenuItem(msgs.replaceBatchItem(),
				TabularDataResources.INSTANCE.columnReplaceBatch());
		replaceByExpressionItem = new MenuItem(msgs.replaceByExpressionItem(),
				TabularDataResources.INSTANCE.columnReplaceByExpression());
		replaceByExternalColItem = new MenuItem(msgs.replaceByExternalColItem(),
				TabularDataResources.INSTANCE.replaceByExternalCol());

		replaceBatchItem
				.addSelectionHandler(new SelectionHandler<Item>() {

					@Override
					public void onSelection(SelectionEvent<Item> event) {
						eventBus.fireEvent(new RibbonEvent(
								RibbonType.COLUMN_REPLACE_BATCH));

					}
				});

		replaceByExpressionItem
				.addSelectionHandler(new SelectionHandler<Item>() {

					@Override
					public void onSelection(SelectionEvent<Item> event) {
						eventBus.fireEvent(new RibbonEvent(
								RibbonType.COLUMN_REPLACE_BY_EXPRESSION));

					}
				});

		replaceByExternalColItem
				.addSelectionHandler(new SelectionHandler<Item>() {

					@Override
					public void onSelection(SelectionEvent<Item> event) {
						eventBus.fireEvent(new RibbonEvent(
								RibbonType.COLUMN_REPLACE_BY_EXTERNAL_COL));

					}
				});

		menuReplace.add(replaceBatchItem);
		menuReplace.add(replaceByExpressionItem);
		menuReplace.add(replaceByExternalColItem);
		return menuReplace;
	}

	private Menu createDeleteMenu() {
		Menu menuDelete = new Menu();
		deleteSelectedRowsItem = new MenuItem(msgs.deleteSelectedRowsItem(),
				TabularDataResources.INSTANCE.tableRowDeleteSelected());
		deleteDuplicateItem = new MenuItem(msgs.deleteDuplicateItem(),
				TabularDataResources.INSTANCE.tableDuplicateRowsRemove());
		deleteByExpressionItem = new MenuItem(msgs.deleteByExpressionItem(),
				TabularDataResources.INSTANCE.tableRowDeleteByExpression());

		deleteSelectedRowsItem
				.addSelectionHandler(new SelectionHandler<Item>() {

					@Override
					public void onSelection(SelectionEvent<Item> event) {
						eventBus.fireEvent(new RibbonEvent(RibbonType.ROW_DELETE));

					}
				});

		deleteDuplicateItem
				.addSelectionHandler(new SelectionHandler<Item>() {

					@Override
					public void onSelection(SelectionEvent<Item> event) {
						eventBus.fireEvent(new RibbonEvent(RibbonType.DUPLICATE_DELETE));

					}
				});

		deleteByExpressionItem
				.addSelectionHandler(new SelectionHandler<Item>() {

					@Override
					public void onSelection(SelectionEvent<Item> event) {
						eventBus.fireEvent(new RibbonEvent(RibbonType.BYEXPRESSION_DELETE));

					}
				});

		menuDelete.add(deleteSelectedRowsItem);
		menuDelete.add(deleteDuplicateItem);
		menuDelete.add(deleteByExpressionItem);
		return menuDelete;
	}

	protected void cleanCells(Element elem) {
		NodeList<Element> tds = elem.<XElement> cast().select("td");
		for (int i = 0; i < tds.getLength(); i++) {
			Element td = tds.getItem(i);

			if (!td.hasChildNodes() && td.getClassName().equals("")) {
				td.removeFromParent();
			}
		}
	}

	

	public void setUI(UIStateEvent event) {
		UIStateType uiStateType = event.getUIStateType();
		try {
			switch (uiStateType) {
			case START:
				// Tools
				replaceButton.disable();
				deleteButton.disable();
				filterButton.disable();
				unionButton.disable();
				addRowButton.disable();
				//Group
				groupbyButton.disable();
				timeAggregationButton.disable();
				// Geospatial
				csquareButton.disable();
				downscaleCSquareButton.disable();
				oceanAreaButton.disable();
				// Geometry
				pointButton.disable();
				break;
			case TR_CLOSE:
			case TR_READONLY:
				// Tools
				replaceButton.disable();
				deleteButton.disable();
				filterButton.disable();
				unionButton.disable();
				addRowButton.disable();
				//Group
				groupbyButton.disable();
				timeAggregationButton.disable();
				// Geospatial
				csquareButton.disable();
				downscaleCSquareButton.disable();
				oceanAreaButton.disable();
				// Geometry
				pointButton.disable();
				break;
			case TR_OPEN:
			case TABLEUPDATE:
			case TABLECURATION:
				// Tools
				replaceButton.enable();
				deleteButton.enable();
				filterButton.enable();
				unionButton.enable();
				addRowButton.enable();
				//Group
				groupbyButton.enable();
				timeAggregationButton.enable();
				// Geospatial
				csquareButton.enable();
				downscaleCSquareButton.enable();
				oceanAreaButton.enable();
				// Geometry
				pointButton.enable();
				break;
			case WIZARD_OPEN:
				// Tools
				replaceButton.disable();
				deleteButton.disable();
				filterButton.disable();
				unionButton.disable();
				addRowButton.disable();
				//Group
				groupbyButton.disable();
				timeAggregationButton.disable();
				// Geospatial
				csquareButton.disable();
				downscaleCSquareButton.disable();
				oceanAreaButton.disable();
				// Geometry
				pointButton.disable();
				break;
			default:
				break;
			}
		} catch (Exception e) {
			Log.error("setUI Error on Ribbon Curation: "
					+ e.getLocalizedMessage());
		}
	}

}
