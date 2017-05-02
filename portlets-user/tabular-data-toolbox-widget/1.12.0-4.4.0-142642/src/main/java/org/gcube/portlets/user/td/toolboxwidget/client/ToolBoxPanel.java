package org.gcube.portlets.user.td.toolboxwidget.client;

import org.gcube.portlets.user.td.widgetcommonevent.client.event.RibbonEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.UIStateEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.WidgetRequestEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.UIStateType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;

import com.allen_sauer.gwt.log.client.Log;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.ContentPanel;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class ToolBoxPanel extends ContentPanel {

	private EventBus eventBus;
	private ToolBox toolBox;
	private ToolBoxPanelState state;
	private UIStateType uiStateType;
	//private WidgetRequestType widgetRequestType;
	

	public ToolBoxPanel(String name, EventBus eventBus) {
		super();
		Log.debug("Open ToolBoxPanel");
		this.eventBus = eventBus;
		setId(name);
		setWidth(310);
		setHeaderVisible(false);
		setResize(true);
		setBodyBorder(false);
		setBorders(false);
		forceLayoutOnResize = true;
		toolBox = new ToolBox("ToolBox", eventBus, this);
		add(toolBox);
		startPanel();
		bindToEvents();

	}

	protected void bindToEvents() {
		eventBus.addHandler(RibbonEvent.TYPE,
				new RibbonEvent.RibbonEventHandler() {

					public void onRibbon(RibbonEvent event) {
						manageRibbonEvents(event);

					}
				});

		eventBus.addHandler(WidgetRequestEvent.TYPE,
				new WidgetRequestEvent.WidgetRequestEventHandler() {

					public void onWidgetRequest(WidgetRequestEvent event) {
						manageWidgetRequestEvents(event);

					}
				});

		eventBus.addHandler(UIStateEvent.TYPE,
				new UIStateEvent.UIStateHandler() {

					public void onUIState(UIStateEvent event) {
						manageUIStateEvents(event);

					}
				});
	}

	protected void manageRibbonEvents(RibbonEvent event) {
		Log.debug("ToolBox recieved event from Ribbon: " + event.getRibbonType().toString());
		switch (event.getRibbonType()) {
		case PROPERTIES:
			doPropertiesOpenTab();
			break;
		case HELP:
			doHelpTab();
			break;
		case HISTORY:
			doHistoryTab();
			break;
		default:
			break;
		}
	}

	protected void manageUIStateEvents(UIStateEvent event) {
		Log.debug("ToolBox recieved event from UI: "
				+ event.getUIStateType().toString());
		uiStateType = event.getUIStateType();
		switch (event.getUIStateType()) {
		case START:
			break;
		case TR_CLOSE:
			// this.enable();
			closePanel();
			break;
		case TR_OPEN:
			// this.enable();
			updateForOpenPanel();
			break;
		case TABLECURATION:
			updateForCurationPanel(event.getTrId());
			break;
		case TABLEUPDATE:
			updatePanel();
			break;
		case TR_READONLY:
			break;
		case WIZARD_OPEN:
			// this.disable();
			break;
		default:
			break;

		}
	}

	protected void manageWidgetRequestEvents(WidgetRequestEvent event) {
		Log.debug("ToolBox recieved event: "
				+ event.getWidgetRequestType().toString());
		switch (event.getWidgetRequestType()) {
		case CHANGECOLUMNTYPEPANEL:
			doColumnTypeChangeTab(event);
			break;
		case ADDCOLUMNPANEL:
			doAddColumnTab(event);
			break;
		case DELETECOLUMNPANEL:
			doDeleteColumnTab(event);
			break;
		case SPLITCOLUMNPANEL:
			doSplitColumnTab(event);
			break;
		case MERGECOLUMNPANEL:
			doMergeColumnTab(event);
			break;
		case GROUPBYPANEL:
			doGroupByTab(event);
			break;
		case TIMEAGGREGATIONPANEL:
			doTimeAggregateTab(event);
			break;	
		case LABELCOLUMNPANEL:
			doLabelColumnTab(event);
			break;
		case POSITIONCOLUMNPANEL:
			doPositionColumnTab(event);
			break;
		case CHANGETABLETYPEPANEL:
			doChangeTableTypeTab(event);
			break;
		case DUPLICATESROWSDETECTIONPANEL:
			doDuplicatesRowsDetectionTab(event);
			break;
		case DUPLICATESROWSDELETEPANEL:
			doDuplicatesRowsDeleteTab(event);	
			break;
		case VALIDATIONSTASKSPANEL:
			doValidationsTasksTab(event);
			break;
		case NORMALIZEPANEL:
			doNormalizeTab(event);
			break;
		case DENORMALIZEPANEL:
			doDenormalizeTab(event);
			break;
		case MONITORBACKGROUNDPANEL:
			doMonitorBackgroundTab(event);
			break;
		case RESOURCESPANEL:
			doResourcesTab(event);
			break;
		case GEOSPATIALCREATECOORDINATESPANEL:
			doGeospatialCreateCoordinatesTab(event);
			break;	
		case GEOMETRYCREATEPOINTPANEL:
			doGeometryCreatePointTab(event);
			break;		
		case DOWNSCALECSQUAREPANEL:
			doDownscaleCSquareTab(event);
			break;		
		default:
			break;

		}
	}

	protected void doHistoryTab() {
		if (uiStateType == UIStateType.TR_OPEN
				|| uiStateType == UIStateType.TABLECURATION
				|| uiStateType == UIStateType.TABLEUPDATE) {
			if (state == ToolBoxPanelState.OPENED) {
				toolBox.openHistoryTab();
				Log.debug("ToolBoxPanelSTate.OPENED Add Tabs");
			} else {
				enable();
				expand();
				state = ToolBoxPanelState.OPENED;
				toolBox.openHistoryTab();
			}
		} else {
			Log.debug("ToolBoxPanel No table open");
		}
	}

	protected void doColumnTypeChangeTab(WidgetRequestEvent event) {
		Log.debug("WidgetRequestEvent.CHANGECOLUMNTYPEPANEL : ["
				+ event.getTrId() + " ,columnName: " + event.getColumnName()
				+ "]");
		if (uiStateType == UIStateType.TR_OPEN
				|| uiStateType == UIStateType.TABLECURATION
				|| uiStateType == UIStateType.TABLEUPDATE) {
			if (state == ToolBoxPanelState.OPENED) {
				toolBox.openColumnTypeChangeTab(event.getTrId(),
						event.getColumnName());
				Log.debug("ToolBoxPanelSTate.OPENED Add Tabs");
			} else {
				enable();
				expand();
				state = ToolBoxPanelState.OPENED;
				toolBox.openColumnTypeChangeTab(event.getTrId(),
						event.getColumnName());
			}
		} else {
			Log.debug("ToolBoxPanel No table open");
		}
	}

	protected void doAddColumnTab(WidgetRequestEvent event) {
		Log.debug("WidgetRequestEvent.AddColumnPanel : [" + event.getTrId()
				+ "]");
		if (uiStateType == UIStateType.TR_OPEN
				|| uiStateType == UIStateType.TABLECURATION
				|| uiStateType == UIStateType.TABLEUPDATE) {
			if (state == ToolBoxPanelState.OPENED) {
				toolBox.openAddColumnTab(event.getTrId());
				Log.debug("ToolBoxPanelSTate.OPENED Add Tabs");
			} else {
				enable();
				expand();
				state = ToolBoxPanelState.OPENED;
				toolBox.openAddColumnTab(event.getTrId());
			}
		} else {
			Log.debug("ToolBoxPanel No table open");
		}
	}

	protected void doDeleteColumnTab(WidgetRequestEvent event) {
		Log.debug("WidgetRequestEvent.DeleteColumnPanel : [" + event.getTrId()
				+ " ,columnName: " + event.getColumnName() + "]");
		if (uiStateType == UIStateType.TR_OPEN
				|| uiStateType == UIStateType.TABLECURATION
				|| uiStateType == UIStateType.TABLEUPDATE) {
			if (state == ToolBoxPanelState.OPENED) {
				toolBox.openDeleteColumnTab(event.getTrId(),
						event.getColumnName());
				Log.debug("ToolBoxPanelSTate.OPENED Add Tabs");
			} else {
				enable();
				expand();
				state = ToolBoxPanelState.OPENED;
				toolBox.openDeleteColumnTab(event.getTrId(),
						event.getColumnName());
			}
		} else {
			Log.debug("ToolBoxPanel No table open");
		}
	}

	protected void doSplitColumnTab(WidgetRequestEvent event) {
		Log.debug("WidgetRequestEvent.SplitColumnPanel : [" + event.getTrId()
				+ " ,columnName: " + event.getColumnName() + "]");
		if (uiStateType == UIStateType.TR_OPEN
				|| uiStateType == UIStateType.TABLECURATION
				|| uiStateType == UIStateType.TABLEUPDATE) {
			if (state == ToolBoxPanelState.OPENED) {
				toolBox.openSplitColumnTab(event.getTrId(),
						event.getColumnName());
				Log.debug("ToolBoxPanelSTate.OPENED Add Tabs");
			} else {
				enable();
				expand();
				state = ToolBoxPanelState.OPENED;
				toolBox.openLabelColumnTab(event.getTrId(),
						event.getColumnName());
			}
		} else {
			Log.debug("ToolBoxPanel No table open");
		}
	}

	protected void doMergeColumnTab(WidgetRequestEvent event) {
		Log.debug("WidgetRequestEvent.MergeColumnPanel : [" + event.getTrId()
				+ " ,columnName: " + event.getColumnName() + "]");
		if (uiStateType == UIStateType.TR_OPEN
				|| uiStateType == UIStateType.TABLECURATION
				|| uiStateType == UIStateType.TABLEUPDATE) {
			if (state == ToolBoxPanelState.OPENED) {
				toolBox.openMergeColumnTab(event.getTrId(),
						event.getColumnName());
				Log.debug("ToolBoxPanelSTate.OPENED Add Tabs");
			} else {
				enable();
				expand();
				state = ToolBoxPanelState.OPENED;
				toolBox.openLabelColumnTab(event.getTrId(),
						event.getColumnName());
			}
		} else {
			Log.debug("ToolBoxPanel No table open");
		}
	}

	protected void doGroupByTab(WidgetRequestEvent event) {
		Log.debug("WidgetRequestEvent.GroupByPanel : [" + event.getTrId() + "]");
		if (uiStateType == UIStateType.TR_OPEN
				|| uiStateType == UIStateType.TABLECURATION
				|| uiStateType == UIStateType.TABLEUPDATE) {
			if (state == ToolBoxPanelState.OPENED) {
				toolBox.openGroupByTab(event.getTrId(), event.getColumnName());
				Log.debug("ToolBoxPanelSTate.OPENED Add Tabs");
			} else {
				enable();
				expand();
				state = ToolBoxPanelState.OPENED;
				toolBox.openGroupByTab(event.getTrId(), event.getColumnName());
			}
		} else {
			Log.debug("ToolBoxPanel No table open");
		}
	}
	
	protected void doTimeAggregateTab(WidgetRequestEvent event) {
		Log.debug("WidgetRequestEvent.TimeAggregatePanel : [" + event.getTrId() + "]");
		if (uiStateType == UIStateType.TR_OPEN
				|| uiStateType == UIStateType.TABLECURATION
				|| uiStateType == UIStateType.TABLEUPDATE) {
			if (state == ToolBoxPanelState.OPENED) {
				toolBox.openTimeAggregationTab(event.getTrId(), event.getColumnName());
				Log.debug("ToolBoxPanelSTate.OPENED Add Tabs");
			} else {
				enable();
				expand();
				state = ToolBoxPanelState.OPENED;
				toolBox.openTimeAggregationTab(event.getTrId(), event.getColumnName());
			}
		} else {
			Log.debug("ToolBoxPanel No table open");
		}
	}


	
	protected void doPositionColumnTab(WidgetRequestEvent event) {
		Log.debug("WidgetRequestEvent.PositionColumnPanel : [" + event.getTrId()
				+ " ,columnName: " + event.getColumnName() + "]");
		if (uiStateType == UIStateType.TR_OPEN
				|| uiStateType == UIStateType.TABLECURATION
				|| uiStateType == UIStateType.TABLEUPDATE) {
			if (state == ToolBoxPanelState.OPENED) {
				toolBox.openPositionColumnTab(event.getTrId(),
						event.getColumnLocalId());
				Log.debug("ToolBoxPanelSTate.OPENED Add Tabs");
			} else {
				enable();
				expand();
				state = ToolBoxPanelState.OPENED;
				toolBox.openPositionColumnTab(event.getTrId(),
						event.getColumnLocalId());
			}
		} else {
			Log.debug("ToolBoxPanel No table open");
		}
	}
	
	
	protected void doLabelColumnTab(WidgetRequestEvent event) {
		Log.debug("WidgetRequestEvent.LabelColumnPanel : [" + event.getTrId()
				+ " ,columnName: " + event.getColumnName() + "]");
		if (uiStateType == UIStateType.TR_OPEN
				|| uiStateType == UIStateType.TABLECURATION
				|| uiStateType == UIStateType.TABLEUPDATE) {
			if (state == ToolBoxPanelState.OPENED) {
				toolBox.openLabelColumnTab(event.getTrId(),
						event.getColumnName());
				Log.debug("ToolBoxPanelSTate.OPENED Add Tabs");
			} else {
				enable();
				expand();
				state = ToolBoxPanelState.OPENED;
				toolBox.openLabelColumnTab(event.getTrId(),
						event.getColumnName());
			}
		} else {
			Log.debug("ToolBoxPanel No table open");
		}
	}

	protected void doChangeTableTypeTab(WidgetRequestEvent event) {
		Log.debug("WidgetRequestEvent.ChangeTableTypePanel : ["
				+ event.getTrId() + "]");
		if (uiStateType == UIStateType.TR_OPEN
				|| uiStateType == UIStateType.TABLECURATION
				|| uiStateType == UIStateType.TABLEUPDATE) {
			if (state == ToolBoxPanelState.OPENED) {
				toolBox.openChangeTableTypeTab(event.getTrId());
				Log.debug("ToolBoxPanelSTate.OPENED Add Tabs");
			} else {
				enable();
				expand();
				state = ToolBoxPanelState.OPENED;
				toolBox.openChangeTableTypeTab(event.getTrId());
			}
		} else {
			Log.debug("ToolBoxPanel No table open");
		}
	}

	protected void doDuplicatesRowsDetectionTab(WidgetRequestEvent event) {
		Log.debug("WidgetRequestEvent.DuplicateRowsDetectionPanel : [" + event.getTrId()
				+ "]");
		if (uiStateType == UIStateType.TR_OPEN
				|| uiStateType == UIStateType.TABLECURATION
				|| uiStateType == UIStateType.TABLEUPDATE) {
			if (state == ToolBoxPanelState.OPENED) {
				toolBox.openDuplicatesRowsDetectionTab(event.getTrId());
				Log.debug("ToolBoxPanelSTate.OPENED Add Tabs");
			} else {
				enable();
				expand();
				state = ToolBoxPanelState.OPENED;
				toolBox.openDuplicatesRowsDetectionTab(event.getTrId());
			}
		} else {
			Log.debug("ToolBoxPanel No table open");
		}
	}
	
	
	protected void doDuplicatesRowsDeleteTab(WidgetRequestEvent event) {
		Log.debug("WidgetRequestEvent.DuplicateRowsDeletePanel : [" + event.getTrId()
				+ "]");
		if (uiStateType == UIStateType.TR_OPEN
				|| uiStateType == UIStateType.TABLECURATION
				|| uiStateType == UIStateType.TABLEUPDATE) {
			if (state == ToolBoxPanelState.OPENED) {
				toolBox.openDuplicatesRowsDeleteTab(event.getTrId());
				Log.debug("ToolBoxPanelSTate.OPENED Add Tabs");
			} else {
				enable();
				expand();
				state = ToolBoxPanelState.OPENED;
				toolBox.openDuplicatesRowsDeleteTab(event.getTrId());
			}
		} else {
			Log.debug("ToolBoxPanel No table open");
		}
	}
	
	
	
	
	

	protected void doValidationsTableTab(WidgetRequestEvent event) {
		Log.debug("WidgetRequestEvent.ValidationsTablePanel : ["
				+ event.getTrId() + "]");
		if (uiStateType == UIStateType.TR_OPEN
				|| uiStateType == UIStateType.TABLECURATION
				|| uiStateType == UIStateType.TABLEUPDATE) {
			if (state == ToolBoxPanelState.OPENED) {
				toolBox.openValidationsTableTab(event.getTrId());
				Log.debug("ToolBoxPanelSTate.OPENED Add Tabs");
			} else {
				enable();
				expand();
				state = ToolBoxPanelState.OPENED;
				toolBox.openValidationsTableTab(event.getTrId());
			}
		} else {
			Log.debug("ToolBoxPanel No table open");
		}
	}

	protected void doValidationsTasksTab(WidgetRequestEvent event) {
		Log.debug("WidgetRequestEvent.ValidationsTasksPanel : ["
				+ event.getTrId() + "]");
		if (uiStateType == UIStateType.TR_OPEN
				|| uiStateType == UIStateType.TABLECURATION
				|| uiStateType == UIStateType.TABLEUPDATE) {
			if (state == ToolBoxPanelState.OPENED) {
				toolBox.openValidationsTasksTab(event.getTrId());
				Log.debug("ToolBoxPanelSTate.OPENED Add Tabs");
			} else {
				enable();
				expand();
				state = ToolBoxPanelState.OPENED;
				toolBox.openValidationsTasksTab(event.getTrId());
			}
		} else {
			Log.debug("ToolBoxPanel No table open");
		}
	}
	
	protected void doResourcesTab(WidgetRequestEvent event) {
		Log.debug("WidgetRequestEvent.ResourcesPanel : ["
				+ event.getTrId() + "]");
		if (uiStateType == UIStateType.TR_OPEN
				|| uiStateType == UIStateType.TABLECURATION
				|| uiStateType == UIStateType.TABLEUPDATE) {
			if (state == ToolBoxPanelState.OPENED) {
				toolBox.openResourcesTab(event.getTrId());
				Log.debug("ToolBoxPanelSTate.OPENED Add Tabs");
			} else {
				enable();
				expand();
				state = ToolBoxPanelState.OPENED;
				toolBox.openResourcesTab(event.getTrId());
			}
		} else {
			Log.debug("ToolBoxPanel No table open");
		}
	}
	
	protected void doGeospatialCreateCoordinatesTab(WidgetRequestEvent event) {
		Log.debug("WidgetRequestEvent.GeospatialCreateCoordinatesPanel : ["
				+ event.getTrId() + "]");
		if (uiStateType == UIStateType.TR_OPEN
				|| uiStateType == UIStateType.TABLECURATION
				|| uiStateType == UIStateType.TABLEUPDATE) {
			if (state == ToolBoxPanelState.OPENED) {
				toolBox.openGeospatialCreateCoordinatesTab(event.getTrId(),
						event.getRequestProperties());
				Log.debug("ToolBoxPanelSTate.OPENED Add Tabs");
			} else {
				enable();
				expand();
				state = ToolBoxPanelState.OPENED;
				toolBox.openGeospatialCreateCoordinatesTab(event.getTrId(),
						event.getRequestProperties());
			}
		} else {
			Log.debug("ToolBoxPanel No table open");
		}
	}
	
	protected void doGeometryCreatePointTab(WidgetRequestEvent event) {
		Log.debug("WidgetRequestEvent.GeometryCreatePointPanel : ["
				+ event.getTrId() + "]");
		if (uiStateType == UIStateType.TR_OPEN
				|| uiStateType == UIStateType.TABLECURATION
				|| uiStateType == UIStateType.TABLEUPDATE) {
			if (state == ToolBoxPanelState.OPENED) {
				toolBox.openGeometryCreatePointTab(event.getTrId());
				Log.debug("ToolBoxPanelSTate.OPENED Add Tabs");
			} else {
				enable();
				expand();
				state = ToolBoxPanelState.OPENED;
				toolBox.openGeometryCreatePointTab(event.getTrId());
			}
		} else {
			Log.debug("ToolBoxPanel No table open");
		}
	}
	
	
	protected void doDownscaleCSquareTab(WidgetRequestEvent event) {
		Log.debug("WidgetRequestEvent.DOWNSCALECSQUAREPANEL : [" + event.getTrId()
				+ " ,columnName: " + event.getColumnName() + "]");
		if (uiStateType == UIStateType.TR_OPEN
				|| uiStateType == UIStateType.TABLECURATION
				|| uiStateType == UIStateType.TABLEUPDATE) {
			if (state == ToolBoxPanelState.OPENED) {
				toolBox.openDownscaleCSquareTab(event.getTrId(),
						event.getColumnLocalId());
				Log.debug("ToolBoxPanelSTate.OPENED Add Tabs");
			} else {
				enable();
				expand();
				state = ToolBoxPanelState.OPENED;
				toolBox.openDownscaleCSquareTab(event.getTrId(),
						event.getColumnLocalId());
			}
		} else {
			Log.debug("ToolBoxPanel No table open");
		}
	}
	

	protected void doNormalizeTab(WidgetRequestEvent event) {
		Log.debug("WidgetRequestEvent.NoramlizePanel : [" + event.getTrId()
				+ "]");
		if (uiStateType == UIStateType.TR_OPEN
				|| uiStateType == UIStateType.TABLECURATION
				|| uiStateType == UIStateType.TABLEUPDATE) {
			if (state == ToolBoxPanelState.OPENED) {
				toolBox.openNormalizeTab(event.getTrId());
				Log.debug("ToolBoxPanelSTate.OPENED Normalize Tab");
			} else {
				enable();
				expand();
				state = ToolBoxPanelState.OPENED;
				toolBox.openNormalizeTab(event.getTrId());
			}
		} else {
			Log.debug("ToolBoxPanel No table open");
		}
	}

	protected void doDenormalizeTab(WidgetRequestEvent event) {
		Log.debug("WidgetRequestEvent.DenoramlizePanel : [" + event.getTrId()
				+ "]");
		if (uiStateType == UIStateType.TR_OPEN
				|| uiStateType == UIStateType.TABLECURATION
				|| uiStateType == UIStateType.TABLEUPDATE) {
			if (state == ToolBoxPanelState.OPENED) {
				toolBox.openDenormalizeTab(event.getTrId());
				Log.debug("ToolBoxPanelSTate.OPENED Denormalize Tab");
			} else {
				enable();
				expand();
				state = ToolBoxPanelState.OPENED;
				toolBox.openDenormalizeTab(event.getTrId());
			}
		} else {
			Log.debug("ToolBoxPanel No table open");
		}
	}

	protected void doMonitorBackgroundTab(WidgetRequestEvent event) {
		Log.debug("WidgetRequestEvent.MonitorBackgroundPanel");
		if (state == ToolBoxPanelState.OPENED) {
			toolBox.openMonitorBackgroundTab();
			Log.debug("ToolBoxPanelSTate.OPENED Monitor Background Tab");
		} else {
			enable();
			expand();
			state = ToolBoxPanelState.OPENED;
			toolBox.openMonitorBackgroundTab();
		}

	}

	protected void doPropertiesOpenTab() {
		if (uiStateType == UIStateType.TR_OPEN
				|| uiStateType == UIStateType.TABLECURATION
				|| uiStateType == UIStateType.TABLEUPDATE) {
			if (state == ToolBoxPanelState.OPENED) {
				toolBox.openPropertiesTab();
				Log.debug("ToolBoxPanelSTate.OPENED Add Tabs");
			} else {
				enable();
				expand();
				state = ToolBoxPanelState.OPENED;
				toolBox.openPropertiesTab();
			}
		} else {
			Log.debug("ToolBoxPanel No table open");
		}

	}

	protected void doHelpTab() {
		Log.debug("ToolBoxPanel show help tab");
		enable();
		expand();
		toolBox.showHelpTab();
	}

	public void closePanelOnly() {
		collapse();
		disable();
		state = ToolBoxPanelState.CLOSED;
	}

	public void closePanel() {
		toolBox.closePropertiesTabs();
		toolBox.closeHistoryPanel();
		toolBox.closeChangeColumnTypePanel();
		toolBox.closeAddColumnPanel();
		toolBox.closeDeleteColumnPanel();
		toolBox.closeSplitColumnPanel();
		toolBox.closeMergeColumnPanel();
		toolBox.closeGroupByPanel();
		toolBox.closeTimeAggregationPanel();
		toolBox.closePositionColumnPanel();
		toolBox.closeLabelColumnPanel();
		toolBox.closeChangeTableTypePanel();
		toolBox.closeDuplicatesRowsDetectionPanel();
		toolBox.closeDuplicatesRowsDeletePanel();
		toolBox.closeValidationsTablePanel();
		toolBox.closeValidationsTasksPanel();
		toolBox.closeNormalizePanel();
		toolBox.closeDenormalizePanel();
		toolBox.closeResourcesPanel();
		toolBox.closeGeospatialCreateCoordinatesPanel();
		toolBox.closeGeometryCreatePointPanel();
		toolBox.closeDownscaleCSquarePanel();
		Log.debug("ToolBoxPanel Closed");
	}

	public void startPanel() {
		enable();
		expand();
		state = ToolBoxPanelState.OPENED;
		toolBox.startTabs();

	}

	public void updatePanel() {
		if (state == ToolBoxPanelState.OPENED) {
			toolBox.updateTabs();
			Log.debug("ToolBoxPanel Updated");
		}
	}

	public void updateForOpenPanel() {
		if (state == ToolBoxPanelState.OPENED) {
			toolBox.updateTabs();
			toolBox.openPropertiesTab();
			Log.debug("ToolBoxPanel Updated For Table");
		} else {
			enable();
			expand();
			state = ToolBoxPanelState.OPENED;
			toolBox.updateTabs();
			toolBox.openPropertiesTab();
			Log.debug("ToolBoxPanel Updated For Table");
		}
	}

	public void updateForCurationPanel(TRId trId) {
		if (state == ToolBoxPanelState.OPENED) {
			toolBox.updateTabs();
			toolBox.openValidationsTasksTab(trId);
			Log.debug("ToolBoxPanel Updated For Curation");
		} else {
			enable();
			expand();
			state = ToolBoxPanelState.OPENED;
			toolBox.updateTabs();
			toolBox.openValidationsTasksTab(trId);
			Log.debug("ToolBoxPanel Updated For Curation");
		}
	}

}
