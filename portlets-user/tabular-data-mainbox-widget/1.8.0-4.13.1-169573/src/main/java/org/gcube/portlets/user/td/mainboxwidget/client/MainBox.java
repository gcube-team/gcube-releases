package org.gcube.portlets.user.td.mainboxwidget.client;

import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;
import org.gcube.portlets.user.td.mainboxwidget.client.resources.MainboxResources;
import org.gcube.portlets.user.td.mainboxwidget.client.tdx.TDXPanel;
import org.gcube.portlets.user.td.mainboxwidget.client.utils.UtilsGXT3;
import org.gcube.portlets.user.td.mainboxwidget.client.welcome.WelcomePanel;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.DataViewActiveEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.DataViewActiveType;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.dataview.DataView;
import org.gcube.portlets.user.td.widgetcommonevent.shared.dataview.TabularResourceDataView;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.theme.blue.client.tabs.BlueTabPanelBottomAppearance;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.event.BeforeCloseEvent;
import com.sencha.gxt.widget.core.client.event.BeforeCloseEvent.BeforeCloseHandler;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class MainBox extends TabPanel {

	private static final int MIN_TAB_WIDTH = 40;
	private static final int TAB_WIDTH = 160;

	private EventBus eventBus;

	private ArrayList<TDXPanel> tdxPanelList;

	public MainBox(String name, EventBus eventBus, MainBoxPanel mainBoxPanel) {
		super(
				GWT.<TabPanelAppearance> create(BlueTabPanelBottomAppearance.class));
		Log.debug("Create MainBox");
		setId(name);
		this.eventBus = eventBus;
		init();

	}

	protected void init() {

		setBodyBorder(false);
		setBorders(false);
		setAnimScroll(true);
		setTabScroll(true);
		setCloseContextMenu(true);
		setResizeTabs(true);
		setTabWidth(TAB_WIDTH);
		setMinTabWidth(MIN_TAB_WIDTH);

		tdxPanelList = new ArrayList<TDXPanel>();

		addSelectionHandler(new SelectionHandler<Widget>() {

			@Override
			public void onSelection(SelectionEvent<Widget> event) {
				Widget widget = event.getSelectedItem();
				if (widget instanceof TDXPanel) {
					TDXPanel tdxPanel = (TDXPanel) widget;
					TabularResourceDataView tabularResourceDataView = tdxPanel
							.getTabularResourceDataView();
					DataViewActiveEvent dataViewActiveEvent = new DataViewActiveEvent();
					dataViewActiveEvent.setDataView(tabularResourceDataView);
					dataViewActiveEvent
							.setDataViewActiveType(DataViewActiveType.NEWACTIVE);
					eventBus.fireEvent(dataViewActiveEvent);
				} else {

				}
				// forceLayout();

			}
		});
	}

	public void startTabs() {
		Log.debug("Start MainBox Tabs");
		// addWelcomeTab();
		// setActiveWidget(getWidget(0));
	}

	public void openTDXTab(DataView dataView) {
		if (dataView instanceof TabularResourceDataView) {
			TabularResourceDataView tabularResourceDataView = (TabularResourceDataView) dataView;
			Log.debug("Open TDX Tab");
			if (tdxPanelList == null) {
				tdxPanelList = new ArrayList<TDXPanel>();
			}
			addTDXPanel(tabularResourceDataView);
		}

	}

	// Close
	public void closeAllTabs() {
		Log.debug("Close TDX Tabs");
		if (tdxPanelList != null) {
			for (TDXPanel tdxPanel : tdxPanelList) {
				remove(tdxPanel);

			}
			tdxPanelList.clear();

		}
		forceLayout();

	}

	public void closeTab(DataView dataView) {
		if (dataView instanceof TabularResourceDataView) {
			TabularResourceDataView tabularResourceDataView = (TabularResourceDataView) dataView;
			if (tdxPanelList != null) {
				TDXPanel tdxPanelFound = null;
				for (TDXPanel tdxPanel : tdxPanelList) {
					if (tdxPanel
							.isValidDataViewRequest(tabularResourceDataView)) {
						tdxPanelFound = tdxPanel;
						break;
					}

				}
				if (tdxPanelFound != null) {
					remove(tdxPanelFound);
					tdxPanelList.remove(tdxPanelFound);
					TabularResourceDataView oldDataView = tdxPanelFound
							.getTabularResourceDataView();
					Widget active = getActiveWidget();
					if (active != null) {
						if (active instanceof TDXPanel) {
							TDXPanel tdxPanelActive = (TDXPanel) active;
							TabularResourceDataView newDataView = tdxPanelActive
									.getTabularResourceDataView();
							DataViewActiveEvent dataViewActiveEvent = new DataViewActiveEvent();
							dataViewActiveEvent.setDataView(newDataView);
							dataViewActiveEvent.setOldDataView(oldDataView);
							dataViewActiveEvent
									.setDataViewActiveType(DataViewActiveType.ACTIVEAFTERCLOSE);
							eventBus.fireEvent(dataViewActiveEvent);
						} else {

						}
					} else {
						DataViewActiveEvent dataViewActiveEvent = new DataViewActiveEvent();
						dataViewActiveEvent.setOldDataView(oldDataView);
						dataViewActiveEvent
								.setDataViewActiveType(DataViewActiveType.CLOSE);
						eventBus.fireEvent(dataViewActiveEvent);
					}

				}

			} else {

			}
		}
		forceLayout();
	}

	//
	public void updateTabName(DataView dataView) {
		if (dataView instanceof TabularResourceDataView) {
			TabularResourceDataView tabularResourceDataView = (TabularResourceDataView) dataView;
			if (tabularResourceDataView.getTabName() != null
					&& !tabularResourceDataView.getTabName().isEmpty()) {
				if (tdxPanelList != null) {

					TDXPanel tdxPanelFound = null;
					for (TDXPanel tdxPanel : tdxPanelList) {
						if (tdxPanel
								.isValidDataViewRequest(tabularResourceDataView)) {
							tdxPanelFound = tdxPanel;
							break;
						}

					}
					if (tdxPanelFound != null) {
						TabItemConfig tdxPanelItemConf = getConfig(tdxPanelFound);

						tdxPanelItemConf.setText(tabularResourceDataView
								.getTabName());
						update(tdxPanelFound, tdxPanelItemConf);
						forceLayout();
					}
				}
			}
		}
	}

	//

	public void updateTabs(DataView dataView) {
		try {
			Log.debug("Update MainBox Tabs");

			if (dataView instanceof TabularResourceDataView) {
				TabularResourceDataView tabularResourceDataView = (TabularResourceDataView) dataView;

				boolean found = false;

				for (TDXPanel tdxPanel : tdxPanelList) {
					if (tdxPanel
							.isValidDataViewRequest(tabularResourceDataView)) {
						tdxPanel.update(tabularResourceDataView);
						// TabItemConfig tabItemConfig=getConfig(tdxPanel);
						found = true;
						setActiveWidget(tdxPanel);
						break;
					}
				}
				if (!found) {
					retrieveTRName(tabularResourceDataView);
					// addTDXPanel(tabularResourceDataView);
				}

			}

		} catch (Throwable e) {
			Log.error("Error in MainBox: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	protected void addTDXPanel(TabularResourceDataView tabularResourceDataView) {
		for (TDXPanel tdxPanel : tdxPanelList) {
			if (tdxPanel.getTabularResourceDataView().getTrId().getId()
					.compareTo(tabularResourceDataView.getTrId().getId()) == 0) {
				tdxPanel.update(tabularResourceDataView);
				Log.debug("Set Active Widget: " + tdxPanel.getId());
				setActiveWidget(tdxPanel);
				return;
			}
		}

		retrieveTRName(tabularResourceDataView);

	};

	protected void addContentTDXPanel(
			TabularResourceDataView tabularResourceDataView,
			TabResource tabResource) {
		try {
			SafeHtml nameTR;
			if (tabResource != null
					&& (tabResource.getName() == null || tabResource.getName()
							.isEmpty())) {
				nameTR = SafeHtmlUtils.fromString("NoName");
			} else {
				nameTR = SafeHtmlUtils.fromString(tabResource.getName());
			}

			TabItemConfig tdxPanelItemConf = new TabItemConfig(
					nameTR.asString(), true);
			tdxPanelItemConf.setIcon(MainboxResources.INSTANCE
					.tabularResource());
			final TDXPanel tdxPanel = new TDXPanel(eventBus);
			tdxPanel.open(tabularResourceDataView);
			tdxPanelList.add(tdxPanel);

			add(tdxPanel, tdxPanelItemConf);

			addBeforeCloseHandler(new BeforeCloseHandler<Widget>() {

				public void onBeforeClose(BeforeCloseEvent<Widget> event) {
					if (tdxPanel == event.getItem()) {

						remove(tdxPanel);
						tdxPanelList.remove(tdxPanel);
						TabularResourceDataView oldDataView = tdxPanel
								.getTabularResourceDataView();
						Widget active = getActiveWidget();
						if (active != null) {
							if (active instanceof TDXPanel) {
								TDXPanel tdxPanelActive = (TDXPanel) active;
								TabularResourceDataView newDataView = tdxPanelActive
										.getTabularResourceDataView();
								DataViewActiveEvent dataViewActiveEvent = new DataViewActiveEvent();
								dataViewActiveEvent.setDataView(newDataView);
								dataViewActiveEvent.setOldDataView(oldDataView);
								dataViewActiveEvent
										.setDataViewActiveType(DataViewActiveType.ACTIVEAFTERCLOSE);
								eventBus.fireEvent(dataViewActiveEvent);
							} else {

							}
						} else {
							DataViewActiveEvent dataViewActiveEvent = new DataViewActiveEvent();
							dataViewActiveEvent.setOldDataView(oldDataView);
							dataViewActiveEvent
									.setDataViewActiveType(DataViewActiveType.CLOSE);
							eventBus.fireEvent(dataViewActiveEvent);
						}

					}

				}

			});
			setActiveWidget(tdxPanel);
			forceLayout();

		} catch (Throwable e) {
			Log.error("Error adding TDXPanel: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	protected void addWelcomeTab() {

		TabItemConfig welcomeItemConf = new TabItemConfig("Welcome", false);

		final WelcomePanel welcomePanel = new WelcomePanel(eventBus);

		add(welcomePanel, welcomeItemConf);

		addBeforeCloseHandler(new BeforeCloseHandler<Widget>() {

			public void onBeforeClose(BeforeCloseEvent<Widget> event) {
				if (welcomePanel == event.getItem()) {
					remove(welcomePanel);

				}

			}

		});
		setActiveWidget(welcomePanel);
	};

	public ArrayList<String> getSelectedRowsId() {
		ArrayList<String> rowsId = null;
		Widget active = getActiveWidget();
		if (active instanceof TDXPanel) {
			TDXPanel tdxPanel = (TDXPanel) active;
			rowsId = tdxPanel.getTabularData().getGridPanel()
					.getSelectedRowsId();
		}
		return rowsId;
	}

	/**
	 * Retrieve ColumnName of column selected
	 * 
	 * @param columnSelected Column selected
	 * @return Column name
	 */
	public String getColumnName(int columnSelected) {
		String columnName = null;
		Widget active = getActiveWidget();
		if (active instanceof TDXPanel) {
			TDXPanel tdxPanel = (TDXPanel) active;
			columnName = tdxPanel.getTabularData()
					.getColumnName(columnSelected);
		}
		return columnName;
	}

	/**
	 * Retrieve ColumnLocalId of column selected
	 * 
	 * @param columnSelected Column selected
	 * @return Column local id
	 */
	public String getColumnLocalId(int columnSelected) {
		String columnName = null;
		Widget active = getActiveWidget();
		if (active instanceof TDXPanel) {
			TDXPanel tdxPanel = (TDXPanel) active;
			columnName = tdxPanel.getTabularData().getColumnLocalId(
					columnSelected);
		}
		return columnName;
	}

	protected void retrieveTRName(
			final TabularResourceDataView tabularResourceDataView) {
		TDGWTServiceAsync.INSTANCE
				.getInSessionTabResourceInfo(new AsyncCallback<TabResource>() {

					@Override
					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error("Error: "
										+ caught.getLocalizedMessage());
								UtilsGXT3.alert("Error",
										caught.getLocalizedMessage());
							} else {
								Log.error("Error in get in Session Tabular Resource Information: "
										+ caught.getLocalizedMessage());
								UtilsGXT3.alert("Error",
										"Error in get in Session Tabular Resource Information: "
												+ caught.getLocalizedMessage());
							}
						}

					}

					@Override
					public void onSuccess(TabResource result) {
						if (result != null) {
							addContentTDXPanel(tabularResourceDataView, result);
						} else {
							Log.debug("No tabular resource In Session");
						}

					}
				});
	}

}
