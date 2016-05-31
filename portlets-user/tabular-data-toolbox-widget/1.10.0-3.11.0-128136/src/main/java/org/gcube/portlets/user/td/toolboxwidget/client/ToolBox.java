package org.gcube.portlets.user.td.toolboxwidget.client;

import org.gcube.portlets.user.td.columnwidget.client.ChangeColumnTypePanel;
import org.gcube.portlets.user.td.columnwidget.client.DeleteColumnPanel;
import org.gcube.portlets.user.td.columnwidget.client.LabelColumnPanel;
import org.gcube.portlets.user.td.columnwidget.client.PositionColumnPanel;
import org.gcube.portlets.user.td.columnwidget.client.create.AddColumnPanel;
import org.gcube.portlets.user.td.informationwidget.client.TabularResourceProperties;
import org.gcube.portlets.user.td.monitorwidget.client.background.MonitorBackgroundPanel;
import org.gcube.portlets.user.td.resourceswidget.client.ResourcesPanel;
import org.gcube.portlets.user.td.tablewidget.client.ChangeTableTypePanel;
import org.gcube.portlets.user.td.tablewidget.client.geometry.GeometryCreatePointPanel;
import org.gcube.portlets.user.td.tablewidget.client.geospatial.DownscaleCSquarePanel;
import org.gcube.portlets.user.td.tablewidget.client.geospatial.GeospatialCreateCoordinatesPanel;
import org.gcube.portlets.user.td.tablewidget.client.history.HistoryPanel;
import org.gcube.portlets.user.td.tablewidget.client.normalize.DenormalizePanel;
import org.gcube.portlets.user.td.tablewidget.client.normalize.NormalizePanel;
import org.gcube.portlets.user.td.tablewidget.client.rows.DuplicatesRowsDeletePanel;
import org.gcube.portlets.user.td.tablewidget.client.rows.DuplicatesRowsDetectionPanel;
import org.gcube.portlets.user.td.tablewidget.client.validation.ValidationsTablePanel;
import org.gcube.portlets.user.td.tablewidget.client.validation.ValidationsTasksPanel;
import org.gcube.portlets.user.td.toolboxwidget.client.help.HelpPanel;
import org.gcube.portlets.user.td.widgetcommonevent.shared.RequestProperties;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.tdcolumnoperation.client.AggregateByTimeColumnPanel;
import org.gcube.portlets.user.tdcolumnoperation.client.GroupByColumnPanel;
import org.gcube.portlets.user.tdcolumnoperation.client.MergeColumnPanel;
import org.gcube.portlets.user.tdcolumnoperation.client.SplitColumnPanel;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.event.BeforeCloseEvent;
import com.sencha.gxt.widget.core.client.event.BeforeCloseEvent.BeforeCloseHandler;

//import org.gcube.portlets.user.td.taskswidget.client.TdTaskController;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class ToolBox extends TabPanel {
	private ToolBoxMessages msgs;
	private EventBus eventBus;
	private TabularResourceProperties trProperties;
	private HelpPanel helpPanel;
	private ChangeColumnTypePanel changeColumnTypePanel;
	private AddColumnPanel addColumnPanel;
	private DeleteColumnPanel deleteColumnPanel;
	private LabelColumnPanel labelColumnPanel;
	private ChangeTableTypePanel changeTableTypePanel;
	private ValidationsTablePanel validationsTablePanel;
	private ValidationsTasksPanel validationsTasksPanel;
	private DuplicatesRowsDetectionPanel duplicatesRowsDetectionPanel;
	private DuplicatesRowsDeletePanel duplicatesRowsDeletePanel;
	private HistoryPanel historyPanel;
	private SplitColumnPanel splitColumnPanel;
	private MergeColumnPanel mergeColumnPanel;
	private GroupByColumnPanel groupByPanel;
	private AggregateByTimeColumnPanel timeAggregationPanel;
	private NormalizePanel normalizePanel;
	private DenormalizePanel denormalizePanel;
	private MonitorBackgroundPanel monitorBackgroundPanel;
	private ResourcesPanel resourcesPanel;
	private GeospatialCreateCoordinatesPanel geospatialCreateCoordinatesPanel;
	private GeometryCreatePointPanel geometryCreatePointPanel;
	private DownscaleCSquarePanel downscaleCSquarePanel;

	private PositionColumnPanel positionColumnPanel;

	public ToolBox(String name, EventBus eventBus, ToolBoxPanel toolBoxPanel) {
		super();
		Log.debug("Create ToolBox");
		this.eventBus = eventBus;
		this.msgs = GWT.create(ToolBoxMessages.class);
		setId(name);
		setBodyBorder(false);
		setBorders(false);
		setAnimScroll(true);
		setTabScroll(true);
		setCloseContextMenu(true);

	}

	public void startTabs() {
		Log.debug("Start ToolBox Tabs");
		addHelpPanel();
		setActiveWidget(getWidget(0));

	}

	public void showHelpTab() {
		Log.debug("Show Help Tab");
		setActiveWidget(helpPanel);
	}

	public void openPropertiesTab() {
		Log.debug("Open Properties Tab");
		if (trProperties == null) {
			addTrProperties();
		}
		setActiveWidget(trProperties);

	}

	public void openMonitorBackgroundTab() {
		Log.debug("Open Monitor Background Tab");
		if (monitorBackgroundPanel == null) {
			addMonitorBackgroundPanel();
		}
		setActiveWidget(monitorBackgroundPanel);
	}

	public void openHistoryTab() {
		Log.debug("Open History Tab");
		if (historyPanel == null) {
			addHistoryPanel();
		}
		setActiveWidget(historyPanel);

	}

	public void openColumnTypeChangeTab(TRId trId, String columnName) {
		Log.debug("Open Column Type Change Tab");
		if (changeColumnTypePanel == null) {
			addChangeColumnTypePanel(trId, columnName);
		} else {
			changeColumnTypePanel.update(trId, columnName);
		}
		setActiveWidget(changeColumnTypePanel);

	}

	public void openAddColumnTab(TRId trId) {
		Log.debug("Open Add Column Tab");
		if (addColumnPanel == null) {
			addAddColumnPanel(trId);
		} else {
			addColumnPanel.update(trId);
		}
		setActiveWidget(addColumnPanel);

	}

	public void openDeleteColumnTab(TRId trId, String columnName) {
		Log.debug("Open Delete Column Tab");
		if (deleteColumnPanel == null) {
			addDeleteColumnPanel(trId, columnName);
		} else {
			deleteColumnPanel.update(trId, columnName);
		}
		setActiveWidget(deleteColumnPanel);

	}

	public void openSplitColumnTab(TRId trId, String columnName) {
		Log.debug("Open Split Column Tab");
		if (splitColumnPanel == null) {
			addSplitColumnPanel(trId, columnName);
		} else {
			splitColumnPanel.update(trId, columnName);
		}
		setActiveWidget(splitColumnPanel);

	}

	public void openMergeColumnTab(TRId trId, String columnName) {
		Log.debug("Open Merge Column Tab");
		if (mergeColumnPanel == null) {
			addMergeColumnPanel(trId, columnName);
		} else {
			mergeColumnPanel.update(trId, columnName);
		}
		setActiveWidget(mergeColumnPanel);

	}

	public void openGroupByTab(TRId trId, String columnName) {
		Log.debug("Open GroupBy Tab");
		if (groupByPanel == null) {
			addGroupByPanel(trId, columnName);
		} else {
			groupByPanel.update(trId, columnName);
		}
		setActiveWidget(groupByPanel);

	}

	public void openTimeAggregationTab(TRId trId, String columnName) {
		Log.debug("Open Time Aggregation Tab");
		if (timeAggregationPanel == null) {
			addTimeAggregationPanel(trId, columnName);
		} else {
			timeAggregationPanel.update(trId, columnName);
		}
		setActiveWidget(timeAggregationPanel);

	}

	public void openPositionColumnTab(TRId trId, String columnLocalId) {
		Log.debug("Open Position Column Tab");
		if (positionColumnPanel == null) {
			addPositionColumnPanel(trId, columnLocalId);
		} else {
			positionColumnPanel.update(trId);
		}
		setActiveWidget(positionColumnPanel);

	}

	public void openLabelColumnTab(TRId trId, String columnName) {
		Log.debug("Open Label Column Tab");
		if (labelColumnPanel == null) {
			addLabelColumnPanel(trId, columnName);
		} else {
			labelColumnPanel.update(trId, columnName);
		}
		setActiveWidget(labelColumnPanel);

	}

	public void openChangeTableTypeTab(TRId trId) {
		Log.debug("Open Change Table Type Tab");
		if (changeTableTypePanel == null) {
			addChangeTableTypePanel(trId);
		} else {
			changeTableTypePanel.update(trId);
		}
		setActiveWidget(changeTableTypePanel);

	}

	public void openValidationsTableTab(TRId trId) {
		Log.debug("Open Validations Table Tab: " + trId);
		if (validationsTablePanel == null) {
			addValidationsTablePanel(trId);
		}
		setActiveWidget(validationsTablePanel);

	}

	public void openValidationsTasksTab(TRId trId) {
		Log.debug("Open Validations Tasks Tab: " + trId);
		if (validationsTasksPanel == null) {
			addValidationsTasksPanel(trId);
		}
		setActiveWidget(validationsTasksPanel);

	}

	public void openNormalizeTab(TRId trId) {
		Log.debug("Open Normalize Tab");
		if (normalizePanel == null) {
			addNormalizePanel(trId);
		} else {
			normalizePanel.update(trId);
		}
		setActiveWidget(normalizePanel);

	}

	public void openDenormalizeTab(TRId trId) {
		Log.debug("Open Denormalize Tab");
		if (denormalizePanel == null) {
			addDenormalizePanel(trId);
		} else {
			denormalizePanel.update(trId);
		}
		setActiveWidget(denormalizePanel);

	}

	public void openDuplicatesRowsDetectionTab(TRId trId) {
		Log.debug("Open Duplicates Rows Detection Tab");
		if (duplicatesRowsDetectionPanel == null) {
			addDuplicatesRowsDetectionPanel(trId);
		} else {
			duplicatesRowsDetectionPanel.update(trId);
		}
		setActiveWidget(duplicatesRowsDetectionPanel);

	}

	public void openDuplicatesRowsDeleteTab(TRId trId) {
		Log.debug("Open Duplicates Rows Delete Tab");
		if (duplicatesRowsDeletePanel == null) {
			addDuplicatesRowsDeletePanel(trId);
		} else {
			duplicatesRowsDeletePanel.update(trId);
		}
		setActiveWidget(duplicatesRowsDeletePanel);

	}

	public void openResourcesTab(TRId trId) {
		Log.debug("Open Resources Tab");
		if (resourcesPanel == null) {
			addResourcesPanel(trId);
		}
		setActiveWidget(resourcesPanel);

	}

	public void openGeospatialCreateCoordinatesTab(TRId trId,
			RequestProperties requestProperties) {
		Log.debug("Open GeospatialCreateCoordinates Tab");
		if (geospatialCreateCoordinatesPanel == null) {
			addGeospatialCreateCoordinatesPanel(trId, requestProperties);
		} else {
			geospatialCreateCoordinatesPanel.update(trId, requestProperties);
		}
		setActiveWidget(geospatialCreateCoordinatesPanel);

	}

	public void openGeometryCreatePointTab(TRId trId) {
		Log.debug("Open GeospatialCreateCoordinates Tab");
		if (geometryCreatePointPanel == null) {
			addGeometryCreatePointPanel(trId);
		} else {
			geometryCreatePointPanel.update(trId);
		}
		setActiveWidget(geometryCreatePointPanel);

	}

	public void openDownscaleCSquareTab(TRId trId, String columnLocalId) {
		Log.debug("Open DownscaleCSquare Tab");
		if (downscaleCSquarePanel == null) {
			addDownscaleCSquarePanel(trId, columnLocalId);
		} else {
			downscaleCSquarePanel.update(trId, columnLocalId);
		}
		setActiveWidget(downscaleCSquarePanel);

	}

	// Close
	public void closePropertiesTabs() {
		Log.debug("Close Properties Tab");
		if (trProperties != null) {
			remove(trProperties);
			trProperties = null;
		}

	}

	public void closeMonitorBackgroundPanel() {
		Log.debug("Close MonitorBackground Tab");
		if (monitorBackgroundPanel != null) {
			remove(monitorBackgroundPanel);
			monitorBackgroundPanel = null;
		}

	}

	public void closeHistoryPanel() {
		Log.debug("Close History Tab");
		if (historyPanel != null) {
			remove(historyPanel);
			historyPanel = null;
		}

	}

	public void closeChangeColumnTypePanel() {
		Log.debug("Close ChangeColumnTypePanel Tab");
		if (changeColumnTypePanel != null) {
			remove(changeColumnTypePanel);
			changeColumnTypePanel = null;
		}
	}

	public void closeDeleteColumnPanel() {
		Log.debug("Close DeleteColumnPanel Tab");
		if (deleteColumnPanel != null) {
			remove(deleteColumnPanel);
			deleteColumnPanel = null;
		}
	}

	public void closeSplitColumnPanel() {
		Log.debug("Close SplitColumnPanel Tab");
		if (splitColumnPanel != null) {
			remove(splitColumnPanel);
			splitColumnPanel = null;
		}
	}

	public void closeMergeColumnPanel() {
		Log.debug("Close MergeColumnPanel Tab");
		if (mergeColumnPanel != null) {
			remove(mergeColumnPanel);
			mergeColumnPanel = null;
		}
	}

	public void closeGroupByPanel() {
		Log.debug("Close GroupByPanel Tab");
		if (groupByPanel != null) {
			remove(groupByPanel);
			groupByPanel = null;
		}
	}

	public void closeTimeAggregationPanel() {
		Log.debug("Close TimeAggreagationPanel Tab");
		if (timeAggregationPanel != null) {
			remove(timeAggregationPanel);
			timeAggregationPanel = null;
		}
	}

	public void closeAddColumnPanel() {
		Log.debug("Close AddColumnPanel Tab");
		if (addColumnPanel != null) {
			remove(addColumnPanel);
			addColumnPanel = null;
		}
	}

	public void closePositionColumnPanel() {
		Log.debug("Close PositionColumnPanel Tab");
		if (positionColumnPanel != null) {
			remove(positionColumnPanel);
			positionColumnPanel = null;
		}
	}

	public void closeLabelColumnPanel() {
		Log.debug("Close LabelColumnPanel Tab");
		if (labelColumnPanel != null) {
			remove(labelColumnPanel);
			labelColumnPanel = null;
		}
	}

	public void closeChangeTableTypePanel() {
		Log.debug("Close ChangeTableTypePanel Tab");
		if (changeTableTypePanel != null) {
			remove(changeTableTypePanel);
			changeTableTypePanel = null;
		}
	}

	public void closeValidationsTablePanel() {
		Log.debug("Close Validations Table Panel Tab");
		if (validationsTablePanel != null) {
			remove(validationsTablePanel);
			validationsTablePanel = null;
		}
	}

	public void closeValidationsTasksPanel() {
		Log.debug("Close Validations Tasks Panel Tab");
		if (validationsTasksPanel != null) {
			remove(validationsTasksPanel);
			validationsTasksPanel = null;
		}
	}

	public void closeDuplicatesRowsDetectionPanel() {
		Log.debug("Close DuplicatesRowsDetectionPanel Tab");
		if (duplicatesRowsDetectionPanel != null) {
			remove(duplicatesRowsDetectionPanel);
			duplicatesRowsDetectionPanel = null;
		}
	}

	public void closeDuplicatesRowsDeletePanel() {
		Log.debug("Close DuplicatesRowsDeletePanel Tab");
		if (duplicatesRowsDeletePanel != null) {
			remove(duplicatesRowsDeletePanel);
			duplicatesRowsDeletePanel = null;
		}
	}

	public void closeNormalizePanel() {
		Log.debug("Close NormalizePanel Tab");
		if (normalizePanel != null) {
			remove(normalizePanel);
			normalizePanel = null;
		}
	}

	public void closeDenormalizePanel() {
		Log.debug("Close DenormalizePanel Tab");
		if (denormalizePanel != null) {
			remove(denormalizePanel);
			denormalizePanel = null;
		}
	}

	public void closeResourcesPanel() {
		Log.debug("Close ResourcesPanel Tab");
		if (resourcesPanel != null) {
			remove(resourcesPanel);
			resourcesPanel = null;
		}
	}

	public void closeGeospatialCreateCoordinatesPanel() {
		Log.debug("Close GeospatialCreateCoordinates Tab");
		if (geospatialCreateCoordinatesPanel != null) {
			remove(geospatialCreateCoordinatesPanel);
			geospatialCreateCoordinatesPanel = null;
		}
	}

	public void closeGeometryCreatePointPanel() {
		Log.debug("Close GeometryCreatePoint Tab");
		if (geometryCreatePointPanel != null) {
			remove(geometryCreatePointPanel);
			geometryCreatePointPanel = null;
		}

	}

	public void closeDownscaleCSquarePanel() {
		Log.debug("Close DownscaleCSquare Tab");
		if (downscaleCSquarePanel != null) {
			remove(downscaleCSquarePanel);
			downscaleCSquarePanel = null;
		}

	}

	//

	public void updateTabs() {
		try {
			Log.debug("Update ToolBox Tabs");
			if (trProperties != null) {
				trProperties.update();
			}
			if (monitorBackgroundPanel != null) {
				monitorBackgroundPanel.update();
			}

			if (historyPanel != null) {
				// historyPanel.update();
				remove(historyPanel);
				historyPanel = null;
			}

			if (validationsTablePanel != null) {
				validationsTablePanel.update();
				validationsTablePanel.forceLayout();
			}

			if (validationsTasksPanel != null) {
				validationsTasksPanel.update();

			}

			if (resourcesPanel != null) {
				remove(resourcesPanel);
				resourcesPanel = null;
			}

			if (changeColumnTypePanel != null) {
				remove(changeColumnTypePanel);
				changeColumnTypePanel = null;
			}
			if (deleteColumnPanel != null) {
				remove(deleteColumnPanel);
				deleteColumnPanel = null;
			}

			if (splitColumnPanel != null) {
				remove(splitColumnPanel);
				splitColumnPanel = null;
			}

			if (mergeColumnPanel != null) {
				remove(mergeColumnPanel);
				mergeColumnPanel = null;
			}

			if (groupByPanel != null) {
				remove(groupByPanel);
				groupByPanel = null;
			}

			if (timeAggregationPanel != null) {
				remove(timeAggregationPanel);
				timeAggregationPanel = null;
			}

			if (addColumnPanel != null) {
				remove(addColumnPanel);
				addColumnPanel = null;
			}

			if (positionColumnPanel != null) {
				remove(positionColumnPanel);
				positionColumnPanel = null;
			}

			if (labelColumnPanel != null) {
				remove(labelColumnPanel);
				labelColumnPanel = null;
			}

			if (changeTableTypePanel != null) {
				remove(changeTableTypePanel);
				changeTableTypePanel = null;
			}

			if (duplicatesRowsDetectionPanel != null) {
				remove(duplicatesRowsDetectionPanel);
				duplicatesRowsDetectionPanel = null;
			}

			if (duplicatesRowsDeletePanel != null) {
				remove(duplicatesRowsDeletePanel);
				duplicatesRowsDeletePanel = null;
			}

			if (normalizePanel != null) {
				remove(normalizePanel);
				normalizePanel = null;
			}

			if (denormalizePanel != null) {
				remove(denormalizePanel);
				denormalizePanel = null;
			}

			if (geospatialCreateCoordinatesPanel != null) {
				remove(geospatialCreateCoordinatesPanel);
				geospatialCreateCoordinatesPanel = null;
			}

			if (geometryCreatePointPanel != null) {
				remove(geometryCreatePointPanel);
				geometryCreatePointPanel = null;
			}

			if (downscaleCSquarePanel != null) {
				remove(downscaleCSquarePanel);
				downscaleCSquarePanel = null;
			}

			forceLayout();
		} catch (Throwable e) {
			Log.error("Error in ToolBox: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	// Add

	protected void addHelpPanel() {
		TabItemConfig helpItemConf = new TabItemConfig(msgs.helpItem(), false);

		helpPanel = new HelpPanel("Help", eventBus);
		helpPanel.setHeaderVisible(false);
		add(helpPanel, helpItemConf);

	}

	protected void addTrProperties() {
		TabItemConfig propertiesItemConf = new TabItemConfig(
				msgs.propertiesItem(), true);

		trProperties = new TabularResourceProperties("TRProperties", eventBus);
		trProperties.setHeaderVisible(false);
		add(trProperties, propertiesItemConf);

		addBeforeCloseHandler(new BeforeCloseHandler<Widget>() {

			public void onBeforeClose(BeforeCloseEvent<Widget> event) {
				if (trProperties == event.getItem()) {
					remove(trProperties);
					trProperties = null;
				}
			}

		});
	};

	protected void addMonitorBackgroundPanel() {
		TabItemConfig monitorBackgroundPanelItemConf = new TabItemConfig(
				msgs.monitorBackgroundPanelItem(), true);

		monitorBackgroundPanel = new MonitorBackgroundPanel(eventBus);
		add(monitorBackgroundPanel, monitorBackgroundPanelItemConf);
		addBeforeCloseHandler(new BeforeCloseHandler<Widget>() {

			public void onBeforeClose(BeforeCloseEvent<Widget> event) {
				if (monitorBackgroundPanel == event.getItem()) {
					monitorBackgroundPanel.close();
					remove(monitorBackgroundPanel);
					monitorBackgroundPanel = null;
				}
			}

		});
	};

	protected void addHistoryPanel() {
		TabItemConfig historyPanelItemConf = new TabItemConfig(
				msgs.historyPanelItem(), true);

		historyPanel = new HistoryPanel(eventBus);
		add(historyPanel, historyPanelItemConf);
		addBeforeCloseHandler(new BeforeCloseHandler<Widget>() {

			public void onBeforeClose(BeforeCloseEvent<Widget> event) {
				if (historyPanel == event.getItem()) {
					remove(historyPanel);
					historyPanel = null;
				}
			}

		});
	};

	protected void addChangeColumnTypePanel(TRId trId, String columnName) {
		TabItemConfig changeColumnTypeItemConf = new TabItemConfig(
				msgs.changeColumnTypeItem(), true);

		changeColumnTypePanel = new ChangeColumnTypePanel(trId, columnName,
				eventBus);
		add(changeColumnTypePanel, changeColumnTypeItemConf);

		addBeforeCloseHandler(new BeforeCloseHandler<Widget>() {

			public void onBeforeClose(BeforeCloseEvent<Widget> event) {
				if (changeColumnTypePanel == event.getItem()) {
					remove(changeColumnTypePanel);
					changeColumnTypePanel = null;

				}

			}

		});
	};

	protected void addAddColumnPanel(TRId trId) {
		TabItemConfig addColumnItemConf = new TabItemConfig(
				msgs.addColumnItem(), true);

		addColumnPanel = new AddColumnPanel(trId, eventBus);
		add(addColumnPanel, addColumnItemConf);

		addBeforeCloseHandler(new BeforeCloseHandler<Widget>() {

			public void onBeforeClose(BeforeCloseEvent<Widget> event) {
				if (addColumnPanel == event.getItem()) {
					remove(addColumnPanel);
					addColumnPanel = null;

				}

			}

		});
	};

	protected void addDeleteColumnPanel(TRId trId, String columnName) {
		TabItemConfig deleteColumnItemConf = new TabItemConfig(
				msgs.deleteColumnItem(), true);

		deleteColumnPanel = new DeleteColumnPanel(trId, columnName, eventBus);
		add(deleteColumnPanel, deleteColumnItemConf);

		addBeforeCloseHandler(new BeforeCloseHandler<Widget>() {

			public void onBeforeClose(BeforeCloseEvent<Widget> event) {
				if (deleteColumnPanel == event.getItem()) {
					remove(deleteColumnPanel);
					deleteColumnPanel = null;

				}

			}

		});
	};

	protected void addSplitColumnPanel(TRId trId, String columnName) {
		TabItemConfig splitColumnItemConf = new TabItemConfig(
				msgs.splitColumnItem(), true);

		splitColumnPanel = new SplitColumnPanel(trId, columnName, eventBus);
		add(splitColumnPanel, splitColumnItemConf);

		addBeforeCloseHandler(new BeforeCloseHandler<Widget>() {

			public void onBeforeClose(BeforeCloseEvent<Widget> event) {
				if (splitColumnPanel == event.getItem()) {
					remove(splitColumnPanel);
					splitColumnPanel = null;

				}

			}

		});
	};

	protected void addMergeColumnPanel(TRId trId, String columnName) {
		TabItemConfig mergeColumnItemConf = new TabItemConfig(
				msgs.mergeColumnItem(), true);

		mergeColumnPanel = new MergeColumnPanel(trId, columnName, eventBus);
		add(mergeColumnPanel, mergeColumnItemConf);

		addBeforeCloseHandler(new BeforeCloseHandler<Widget>() {

			public void onBeforeClose(BeforeCloseEvent<Widget> event) {
				if (mergeColumnPanel == event.getItem()) {
					remove(mergeColumnPanel);
					mergeColumnPanel = null;

				}

			}

		});
	};

	protected void addGroupByPanel(TRId trId, String columnName) {
		TabItemConfig groupByItemConf = new TabItemConfig(msgs.groupByItem(),
				true);

		try {
			groupByPanel = new GroupByColumnPanel(trId, columnName, eventBus);
		} catch (Throwable e) {
			Log.error("Error Creating GroupBy Panel:" + e.getLocalizedMessage());
			e.printStackTrace();
			return;
		}
		add(groupByPanel, groupByItemConf);

		addBeforeCloseHandler(new BeforeCloseHandler<Widget>() {

			public void onBeforeClose(BeforeCloseEvent<Widget> event) {
				if (groupByPanel == event.getItem()) {
					remove(groupByPanel);
					groupByPanel = null;

				}

			}

		});
	};

	protected void addTimeAggregationPanel(TRId trId, String columnName) {
		TabItemConfig timeAggregationItemConf = new TabItemConfig(
				msgs.timeAggregationItem(), true);

		try {
			timeAggregationPanel = new AggregateByTimeColumnPanel(trId,
					columnName, eventBus);
		} catch (Throwable e) {
			Log.error("Error Creating Time Aggregation Panel:"
					+ e.getLocalizedMessage());
			e.printStackTrace();
			return;
		}
		add(timeAggregationPanel, timeAggregationItemConf);

		addBeforeCloseHandler(new BeforeCloseHandler<Widget>() {

			public void onBeforeClose(BeforeCloseEvent<Widget> event) {
				if (timeAggregationPanel == event.getItem()) {
					remove(timeAggregationPanel);
					timeAggregationPanel = null;

				}

			}

		});
	};

	protected void addPositionColumnPanel(TRId trId, String columnLocalId) {
		TabItemConfig positionColumnItemConf = new TabItemConfig(
				msgs.positionColumnItem(), true);

		positionColumnPanel = new PositionColumnPanel(trId, eventBus);
		add(positionColumnPanel, positionColumnItemConf);

		addBeforeCloseHandler(new BeforeCloseHandler<Widget>() {

			public void onBeforeClose(BeforeCloseEvent<Widget> event) {
				if (positionColumnPanel == event.getItem()) {
					remove(positionColumnPanel);
					positionColumnPanel = null;

				}

			}

		});
	};

	protected void addLabelColumnPanel(TRId trId, String columnName) {
		TabItemConfig labelColumnItemConf = new TabItemConfig(
				msgs.labelColumnItem(), true);

		labelColumnPanel = new LabelColumnPanel(trId, columnName, eventBus);
		add(labelColumnPanel, labelColumnItemConf);

		addBeforeCloseHandler(new BeforeCloseHandler<Widget>() {

			public void onBeforeClose(BeforeCloseEvent<Widget> event) {
				if (labelColumnPanel == event.getItem()) {
					remove(labelColumnPanel);
					labelColumnPanel = null;

				}

			}

		});
	};

	protected void addChangeTableTypePanel(TRId trId) {
		TabItemConfig changeTableTypeItemConf = new TabItemConfig(
				msgs.changeTableTypeItem(), true);

		changeTableTypePanel = new ChangeTableTypePanel(trId, eventBus);
		add(changeTableTypePanel, changeTableTypeItemConf);

		addBeforeCloseHandler(new BeforeCloseHandler<Widget>() {

			public void onBeforeClose(BeforeCloseEvent<Widget> event) {
				if (changeTableTypePanel == event.getItem()) {
					remove(changeTableTypePanel);
					changeTableTypePanel = null;

				}

			}

		});
	};

	protected void addValidationsTablePanel(TRId trId) {
		TabItemConfig validationsTableItemConf = new TabItemConfig(
				msgs.validationsTableItem(), true);

		validationsTablePanel = new ValidationsTablePanel(trId, eventBus);
		add(validationsTablePanel, validationsTableItemConf);

		addBeforeCloseHandler(new BeforeCloseHandler<Widget>() {

			public void onBeforeClose(BeforeCloseEvent<Widget> event) {
				if (validationsTablePanel == event.getItem()) {
					remove(validationsTablePanel);
					validationsTablePanel = null;

				}

			}

		});
	};

	protected void addValidationsTasksPanel(TRId trId) {
		TabItemConfig validationsTasksItemConf = new TabItemConfig(
				msgs.validationsTasksItem(), true);

		validationsTasksPanel = new ValidationsTasksPanel(trId, eventBus);
		add(validationsTasksPanel, validationsTasksItemConf);

		addBeforeCloseHandler(new BeforeCloseHandler<Widget>() {

			public void onBeforeClose(BeforeCloseEvent<Widget> event) {
				if (validationsTasksPanel == event.getItem()) {
					remove(validationsTasksPanel);
					validationsTasksPanel = null;

				}

			}

		});
	};

	protected void addResourcesPanel(TRId trId) {
		TabItemConfig resourcesItemConf = new TabItemConfig(
				msgs.resourcesItem(), true);

		resourcesPanel = new ResourcesPanel(trId, eventBus);
		add(resourcesPanel, resourcesItemConf);

		addBeforeCloseHandler(new BeforeCloseHandler<Widget>() {

			public void onBeforeClose(BeforeCloseEvent<Widget> event) {
				if (resourcesPanel == event.getItem()) {
					remove(resourcesPanel);
					resourcesPanel = null;

				}

			}

		});
	}

	protected void addGeospatialCreateCoordinatesPanel(TRId trId,
			RequestProperties requestProperties) {
		TabItemConfig geospatialCreateCoordinatesItemConf = new TabItemConfig(
				msgs.geospatialCreateCoordinatesItem(), true);

		geospatialCreateCoordinatesPanel = new GeospatialCreateCoordinatesPanel(
				trId, requestProperties, eventBus);
		add(geospatialCreateCoordinatesPanel,
				geospatialCreateCoordinatesItemConf);

		addBeforeCloseHandler(new BeforeCloseHandler<Widget>() {

			public void onBeforeClose(BeforeCloseEvent<Widget> event) {
				if (geospatialCreateCoordinatesPanel == event.getItem()) {
					remove(geospatialCreateCoordinatesPanel);
					geospatialCreateCoordinatesPanel = null;

				}

			}

		});
	};

	private void addGeometryCreatePointPanel(TRId trId) {
		TabItemConfig geometryCreatePointItemConf = new TabItemConfig(
				msgs.geometryCreatePointItem(), true);

		geometryCreatePointPanel = new GeometryCreatePointPanel(trId, eventBus);
		add(geometryCreatePointPanel, geometryCreatePointItemConf);

		addBeforeCloseHandler(new BeforeCloseHandler<Widget>() {

			public void onBeforeClose(BeforeCloseEvent<Widget> event) {
				if (geometryCreatePointPanel == event.getItem()) {
					remove(geometryCreatePointPanel);
					geometryCreatePointPanel = null;

				}

			}

		});

	}

	private void addDownscaleCSquarePanel(TRId trId, String columnLocalId) {
		TabItemConfig downscaleCSquareItemConf = new TabItemConfig(
				msgs.downscaleCSquareItem(), true);

		downscaleCSquarePanel = new DownscaleCSquarePanel(trId, columnLocalId,
				eventBus);
		add(downscaleCSquarePanel, downscaleCSquareItemConf);

		addBeforeCloseHandler(new BeforeCloseHandler<Widget>() {

			public void onBeforeClose(BeforeCloseEvent<Widget> event) {
				if (downscaleCSquarePanel == event.getItem()) {
					remove(downscaleCSquarePanel);
					downscaleCSquarePanel = null;

				}

			}

		});

	}

	protected void addDuplicatesRowsDetectionPanel(TRId trId) {
		TabItemConfig duplicatesRowsDetectionItemConf = new TabItemConfig(
				msgs.duplicatesRowsDetectionItem(), true);

		duplicatesRowsDetectionPanel = new DuplicatesRowsDetectionPanel(trId,
				eventBus);
		add(duplicatesRowsDetectionPanel, duplicatesRowsDetectionItemConf);

		addBeforeCloseHandler(new BeforeCloseHandler<Widget>() {

			public void onBeforeClose(BeforeCloseEvent<Widget> event) {
				if (duplicatesRowsDetectionPanel == event.getItem()) {
					remove(duplicatesRowsDetectionPanel);
					duplicatesRowsDetectionPanel = null;

				}

			}

		});
	};

	protected void addDuplicatesRowsDeletePanel(TRId trId) {
		TabItemConfig duplicatesRowsDeleteItemConf = new TabItemConfig(
				msgs.duplicatesRowsDeleteItem(), true);

		duplicatesRowsDeletePanel = new DuplicatesRowsDeletePanel(trId,
				eventBus);
		add(duplicatesRowsDeletePanel, duplicatesRowsDeleteItemConf);

		addBeforeCloseHandler(new BeforeCloseHandler<Widget>() {

			public void onBeforeClose(BeforeCloseEvent<Widget> event) {
				if (duplicatesRowsDeletePanel == event.getItem()) {
					remove(duplicatesRowsDeletePanel);
					duplicatesRowsDeletePanel = null;

				}

			}

		});
	};

	protected void addNormalizePanel(TRId trId) {
		TabItemConfig normalizeItemConf = new TabItemConfig(
				msgs.normalizeItem(), true);

		normalizePanel = new NormalizePanel(trId, eventBus);
		add(normalizePanel, normalizeItemConf);

		addBeforeCloseHandler(new BeforeCloseHandler<Widget>() {

			public void onBeforeClose(BeforeCloseEvent<Widget> event) {
				if (normalizePanel == event.getItem()) {
					remove(normalizePanel);
					normalizePanel = null;

				}

			}

		});
	};

	protected void addDenormalizePanel(TRId trId) {
		TabItemConfig denormalizeItemConf = new TabItemConfig(
				msgs.denormalizeItem(), true);

		denormalizePanel = new DenormalizePanel(trId, eventBus);
		add(denormalizePanel, denormalizeItemConf);

		addBeforeCloseHandler(new BeforeCloseHandler<Widget>() {

			public void onBeforeClose(BeforeCloseEvent<Widget> event) {
				if (denormalizePanel == event.getItem()) {
					remove(denormalizePanel);
					denormalizePanel = null;

				}

			}

		});
	};

	/*
	 * protected void addTdTaskPanel() { // TODO // tdTaskController =
	 * TdTaskController.getInstance(); //
	 * tdTaskController.bindCommonBus(eventBus); // tdTaskMainWindow =
	 * tdTaskController.getWindowTaskMonitor(); // This // is main panel
	 * 
	 * taskPanel = new FramedPanel(); taskPanel.setHeaderVisible(false);
	 * TextButton buttonTask = new TextButton("Tasks");
	 * buttonTask.addSelectHandler(new SelectEvent.SelectHandler() {
	 * 
	 * public void onSelect(SelectEvent event) { // tdTaskMainWindow.show();
	 * 
	 * }
	 * 
	 * });
	 * 
	 * taskPanel.add(buttonTask);
	 * 
	 * TabItemConfig tdTasksItemConf = new TabItemConfig("Tasks", true);
	 * 
	 * add(taskPanel, tdTasksItemConf); addBeforeCloseHandler(new
	 * BeforeCloseHandler<Widget>() {
	 * 
	 * public void onBeforeClose(BeforeCloseEvent<Widget> event) { if
	 * (getWidgetCount() == 1) { toolBoxPanel.closePanelOnly();
	 * remove(taskPanel); }
	 * 
	 * }
	 * 
	 * });
	 * 
	 * }
	 */
}
