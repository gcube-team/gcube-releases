package org.gcube.portlets.user.td.mainboxwidget.client;

import java.util.ArrayList;

import org.gcube.portlets.user.td.widgetcommonevent.client.event.DataViewRequestEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.UIStateEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.UIStateType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.dataview.DataView;
import org.gcube.portlets.user.td.widgetcommonevent.shared.dataview.TabularResourceDataView;

import com.allen_sauer.gwt.log.client.Log;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.ContentPanel;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class MainBoxPanel extends ContentPanel {
	
	protected EventBus eventBus;
	protected MainBox mainBox;
	protected MainBoxPanelState state;
	protected UIStateType uiStateType;
	protected DataView dataView;

	public MainBoxPanel(String name, EventBus eventBus) {
		super();
		Log.debug("Open MainBoxPanel");
		this.eventBus = eventBus;
		setId(name);
		setHeaderVisible(false);
		setBodyBorder(false);
		setBorders(false);
		setResize(true);
		forceLayoutOnResize = true;
		mainBox = new MainBox("MainBox", eventBus, this);
		add(mainBox);
		startPanel();
		bindToEvents();
		onResize();

	}

	protected void bindToEvents() {

		eventBus.addHandler(DataViewRequestEvent.TYPE,
				new DataViewRequestEvent.DataViewRequestEventHandler() {

					public void onDataViewRequest(DataViewRequestEvent event) {
						manageDataViewRequestEvents(event);

					}
				});

		eventBus.addHandler(UIStateEvent.TYPE,
				new UIStateEvent.UIStateHandler() {

					public void onUIState(UIStateEvent event) {
						manageUIStateEvents(event);

					}
				});
	}

	protected void manageUIStateEvents(UIStateEvent event) {
		Log.debug("MainBox recieved event: "
				+ event.getUIStateType().toString());
		uiStateType = event.getUIStateType();
		TRId trId = null;

		switch (event.getUIStateType()) {
		case START:
			break;
		case TR_CLOSE:
			// this.enable();
			closePanel();
			break;
		case TR_OPEN:
			// this.enable();
			trId = event.getTrId();
			updateForOpenPanel(new TabularResourceDataView(trId));
			break;
		case TABLECURATION:
			trId = event.getTrId();
			updateForCurationPanel(new TabularResourceDataView(trId));
			break;
		case TABLEUPDATE:
			trId = event.getTrId();
			updatePanel(new TabularResourceDataView(trId));
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

	protected void manageDataViewRequestEvents(DataViewRequestEvent event) {
		Log.debug("MainBox recieved event: " + event.getDataView().toString());
		dataView = event.getDataView();
		if (dataView == null || dataView.getDataViewType() == null) {
			return;
		}
		doTabularResourceTab(event);
	}

	protected void doTabularResourceTab(DataViewRequestEvent event) {
		Log.debug("DataViewRequestEvent.TDXPanel : [" + event.getDataView()
				+ "]");
		switch (event.getDataViewRequestType()) {
		case CLOSE:
			mainBox.closeTab(event.getDataView());
			break;
		case OPEN:
			if (uiStateType == UIStateType.TR_OPEN
					|| uiStateType == UIStateType.TABLECURATION
					|| uiStateType == UIStateType.TABLEUPDATE) {
				if (state == MainBoxPanelState.OPENED) {
					mainBox.openTDXTab(event.getDataView());
					Log.debug("MainBoxPanelState.OPENED Add Tabs");
				} else {
					enable();
					expand();
					state = MainBoxPanelState.OPENED;
					mainBox.openTDXTab(event.getDataView());
				}
			} else {
				Log.debug("MainBoxPanel No tabular resource open");
			}
			break;
		case UPDATE_TAB_NAME:
			mainBox.updateTabName(event.getDataView());
			break;
		default:
			break;

		}

	}

	public void closePanelOnly() {
		collapse();
		disable();
		state = MainBoxPanelState.CLOSED;
	}

	public void closePanel() {
		mainBox.closeAllTabs();
		Log.debug("MainBoxPanel Closed");
	}

	public void startPanel() {
		enable();
		expand();
		state = MainBoxPanelState.OPENED;
		mainBox.startTabs();

	}

	public void updatePanel(DataView dataView) {
		if (state == MainBoxPanelState.OPENED) {
			mainBox.updateTabs(dataView);
			Log.debug("MainBoxPanel Updated");
		}
	}

	public void updateForOpenPanel(DataView dataView) {
		if (state == MainBoxPanelState.OPENED) {
			mainBox.updateTabs(dataView);
			Log.debug("MainBoxPanel Updated For Table");
		} else {
			enable();
			expand();
			state = MainBoxPanelState.OPENED;
			mainBox.updateTabs(dataView);
			// mainBox.openPropertiesTab();
			Log.debug("MainBoxPanel Updated For Table");
		}
	}

	public void updateForCurationPanel(DataView dataView) {
		if (state == MainBoxPanelState.OPENED) {
			mainBox.updateTabs(dataView);
			// mainBox.openValidationsTasksTab(trId);
			Log.debug("MainBoxPanel Updated For Curation");
		} else {
			enable();
			expand();
			state = MainBoxPanelState.OPENED;
			mainBox.updateTabs(dataView);
			// mainBox.openValidationsTasksTab(trId);
			Log.debug("MainBoxPanel Updated For Curation");
		}
	}

	public ArrayList<String> getSelectedRowsId() {
		return mainBox.getSelectedRowsId();

	}

	public String getColumnName(int columnSelected) {
		return mainBox.getColumnName(columnSelected);
	}
	
	public String getColumnLocalId(int columnSelected) {
		return mainBox.getColumnLocalId(columnSelected);
	}
}
