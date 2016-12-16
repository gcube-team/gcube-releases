package org.gcube.portlets.user.td.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.gcube.portlets.user.td.chartswidget.client.ChartsWidgetTD;
import org.gcube.portlets.user.td.client.logs.TDMLogs;
import org.gcube.portlets.user.td.client.rstudio.RStudio;
import org.gcube.portlets.user.td.client.template.TemplateApplyDialog;
import org.gcube.portlets.user.td.client.template.TemplateDeleteDialog;
import org.gcube.portlets.user.td.client.template.TemplateOpenDialog;
import org.gcube.portlets.user.td.client.template.TemplateShareDialog;
import org.gcube.portlets.user.td.codelistmappingimportwidget.client.CodelistMappingImportWizardTD;
import org.gcube.portlets.user.td.columnwidget.client.batch.ReplaceBatchDialog;
import org.gcube.portlets.user.td.columnwidget.client.replace.ReplaceAllDialog;
import org.gcube.portlets.user.td.csvexportwidget.client.CSVExportWizardTD;
import org.gcube.portlets.user.td.csvimportwidget.client.CSVImportWizardTD;
import org.gcube.portlets.user.td.expressionwidget.client.MultiColumnFilterDialog;
import org.gcube.portlets.user.td.expressionwidget.client.ReplaceColumnByMultiColumnExpressionDialog;
import org.gcube.portlets.user.td.expressionwidget.client.RowsDeleteByMultiColumnExpressionDialog;
import org.gcube.portlets.user.td.expressionwidget.client.RuleOnColumnCreateDialog;
import org.gcube.portlets.user.td.extractcodelistwidget.client.ExtractCodelistWizardTD;
import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.i18n.InfoLocale;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;
import org.gcube.portlets.user.td.gwtservice.shared.user.UserInfo;
import org.gcube.portlets.user.td.jsonexportwidget.client.JSONExportWidgetTD;
import org.gcube.portlets.user.td.mainboxwidget.client.MainBoxPanel;
import org.gcube.portlets.user.td.mainboxwidget.client.tdx.TDXTabPanelDialog;
import org.gcube.portlets.user.td.mapwidget.client.MapWidgetTD;
import org.gcube.portlets.user.td.openwidget.client.TDOpen;
import org.gcube.portlets.user.td.replacebyexternalwidget.client.ReplaceByExternalTD;
import org.gcube.portlets.user.td.rulewidget.client.RuleActiveDialog;
import org.gcube.portlets.user.td.rulewidget.client.RuleDeleteDialog;
import org.gcube.portlets.user.td.rulewidget.client.RuleOnColumnApplyDialog;
import org.gcube.portlets.user.td.rulewidget.client.RuleOpenDialog;
import org.gcube.portlets.user.td.rulewidget.client.RuleShareDialog;
import org.gcube.portlets.user.td.rulewidget.client.multicolumn.RuleOnTableApplyWizard;
import org.gcube.portlets.user.td.rulewidget.client.multicolumn.RuleOnTableNewWizard;
import org.gcube.portlets.user.td.sdmxexportwidget.client.SDMXExportWizardTD;
import org.gcube.portlets.user.td.sdmximportwidget.client.SDMXImportWizardTD;
import org.gcube.portlets.user.td.sharewidget.client.TRShare;
import org.gcube.portlets.user.td.statisticalwidget.client.StatisticalWidget;
import org.gcube.portlets.user.td.tablewidget.client.CloneTabularResource;
import org.gcube.portlets.user.td.tablewidget.client.history.HistoryDiscard;
import org.gcube.portlets.user.td.tablewidget.client.rows.DeleteRows;
import org.gcube.portlets.user.td.tablewidget.client.rows.EditRowDialog;
import org.gcube.portlets.user.td.tablewidget.client.util.UtilsGXT3;
import org.gcube.portlets.user.td.tablewidget.client.validation.ValidationsDelete;
import org.gcube.portlets.user.td.taskswidget.client.TdTaskController;
import org.gcube.portlets.user.td.unionwizardwidget.client.UnionWizardTD;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.BackgroundRequestEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.ChangeTableRequestEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.DataViewActiveEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.DataViewRequestEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.GridContextMenuItemEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.GridHeaderColumnMenuItemEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.RibbonEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.TasksMonitorEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.UIStateEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.WidgetRequestEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.BackgroundRequestType;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.DataViewRequestType;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.RibbonType;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.TaskType;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.UIStateType;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.WidgetRequestType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.CellData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.Constants;
import org.gcube.portlets.user.td.widgetcommonevent.shared.GridHeaderOperationId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.GridOperationId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.RequestProperties;
import org.gcube.portlets.user.td.widgetcommonevent.shared.RequestPropertiesParameterType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.dataview.DataView;
import org.gcube.portlets.user.td.widgetcommonevent.shared.dataview.DataViewType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.dataview.TabularResourceDataView;
import org.gcube.portlets.user.td.widgetcommonevent.shared.geospatial.GeospatialCoordinatesType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.grid.model.RowRaw;
import org.gcube.portlets.user.td.wizardwidget.client.WizardListener;
import org.gcube.portlets.user.tdtemplate.client.TdTemplateController;
import org.gcube.portlets.widgets.sessionchecker.client.CheckSession;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent.DialogHideHandler;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class TabularDataController {

	private SimpleEventBus eventBus;
	private MainBoxPanel mainBoxPanel;
	private UIStateType uiState = UIStateType.START;

	private boolean taskManagerInit = false;
	private TdTaskController tdTaskController;

	private ContentPanel toolBoxPanel;
	private BorderLayoutData westData;

	/**
	 * Current user
	 */
	private UserInfo userInfo;

	/**
	 * Tabular Resource Id
	 */
	private TRId trId;

	/**
	 * Grid TableId
	 */
	private TRId openTRIdAfterServerUpdate;

	/**
	 * Messages
	 * 
	 */
	private TabularDataControllerMessages msgs;
	private CommonMessages msgsCommon;

	private enum CheckFor {
		ApplyColumnRule, ApplyTableRule, ApplyTemplate;
	}

	public TabularDataController() {
		eventBus = new SimpleEventBus();
		initMessages();
		callHello();
		checkSession();
		pendingTasksRetrieve();
		bindToEvents();
	}

	private void initMessages() {
		msgs = GWT.create(TabularDataControllerMessages.class);
		msgsCommon = GWT.create(CommonMessages.class);
	}

	private void checkSession() {
		// if you do not need to something when the session expire
		CheckSession.getInstance().startPolling();
	}

	private void sessionExpiredShow() {
		CheckSession.showLogoutDialog();
	}

	/**
	 * @return the eventBus
	 */
	public EventBus getEventBus() {
		return eventBus;
	}

	public ContentPanel getToolBoxPanel() {
		return toolBoxPanel;
	}

	public void setToolBoxPanel(ContentPanel toolBoxPanel) {
		this.toolBoxPanel = toolBoxPanel;
	}

	public BorderLayoutData getWestData() {
		return westData;
	}

	public void setWestData(BorderLayoutData westData) {
		this.westData = westData;
	}

	public void setMainBoxPanel(MainBoxPanel mainBoxPanel) {
		this.mainBoxPanel = mainBoxPanel;

	}

	private void callHello() {
		TDGWTServiceAsync.INSTANCE.hello(new AsyncCallback<UserInfo>() {

			@Override
			public void onFailure(Throwable caught) {
				Log.info("No valid user found: " + caught.getMessage());
				if (caught instanceof TDGWTSessionExpiredException) {
					UtilsGXT3.alert(msgsCommon.error(),
							msgsCommon.expiredSession());
					sessionExpiredShowDelayed();
				} else {
					UtilsGXT3.alert(msgsCommon.error(),
							msgsCommon.noUserFound());
				}
			}

			@Override
			public void onSuccess(UserInfo result) {
				userInfo = result;
				Log.info("Hello: " + result.getUsername());

			}

		});

	}

	private void pendingTasksRetrieve() {
		TDGWTServiceAsync.INSTANCE
				.pendingTasksRetrieve(new AsyncCallback<Integer>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.info("No valid user found: " + caught.getMessage());
						if (caught instanceof TDGWTSessionExpiredException) {
							UtilsGXT3.alert(msgsCommon.error(),
									msgsCommon.expiredSession());
							sessionExpiredShowDelayed();
						} else {
							UtilsGXT3.alert(msgsCommon.error(),
									caught.getLocalizedMessage());
						}
					}

					@Override
					public void onSuccess(Integer pending) {
						Log.info("Pending Tasks Retrieved: " + pending);

					}

				});

	}

	private void sessionExpiredShowDelayed() {
		Timer timeoutTimer = new Timer() {
			public void run() {
				sessionExpiredShow();

			}
		};
		int TIMEOUT = 3; // 3 second timeout

		timeoutTimer.schedule(TIMEOUT * 1000); // timeout is in milliseconds

	}

	// Bind Controller to events on bus
	private void bindToEvents() {
		eventBus.addHandler(SessionExpiredEvent.TYPE,
				new SessionExpiredEvent.SessionExpiredEventHandler() {

					@Override
					public void onSessionExpired(SessionExpiredEvent event) {
						Log.debug("Catch Event SessionExpiredEvent");
						doSessionExpiredCommand(event);

					}
				});

		eventBus.addHandler(DataViewActiveEvent.TYPE,
				new DataViewActiveEvent.DataViewActiveEventHandler() {

					@Override
					public void onDataViewActive(DataViewActiveEvent event) {
						Log.debug("Catch Event DataViewActiveEvent");
						doDataViewActiveCommand(event);

					}
				});

		eventBus.addHandler(
				GridHeaderColumnMenuItemEvent.TYPE,
				new GridHeaderColumnMenuItemEvent.GridHeaderColumnMenuItemEventHandler() {

					public void onGridHeaderColumnMenuItemEvent(
							GridHeaderColumnMenuItemEvent event) {
						Log.debug("Catch Event GridHeaderColumnMenuItemEvent");
						doGridHeaderColumnMenuCommand(event);

					}
				});

		eventBus.addHandler(GridContextMenuItemEvent.TYPE,
				new GridContextMenuItemEvent.GridContextMenuItemEventHandler() {

					public void onGridContextMenuItemEvent(
							GridContextMenuItemEvent event) {
						Log.debug("Catch Event ContextMenuItemEvent");
						doGridContextMenuCommand(event);

					}
				});

		eventBus.addHandler(ChangeTableRequestEvent.TYPE,
				new ChangeTableRequestEvent.ChangeTableRequestEventHandler() {

					public void onChangeTableRequestEvent(
							ChangeTableRequestEvent event) {
						Log.debug("Catch Event ChangeTableRequestEvent");
						doChangeTableRequestEventCommand(event);

					}
				});

		eventBus.addHandler(RibbonEvent.TYPE,
				new RibbonEvent.RibbonEventHandler() {

					public void onRibbon(RibbonEvent event) {
						Log.debug("Catch Event RibbonEvent");
						doRibbonCommand(event);

					}
				});

		eventBus.addHandler(TasksMonitorEvent.TYPE,
				new TasksMonitorEvent.TasksMonitorEventHandler() {

					public void onTasksMonitorEvent(TasksMonitorEvent event) {
						Log.debug("Catch Event TasksMonitorEvent");
						doTasksMonitorCommand(event);

					}

				});

		eventBus.addHandler(WidgetRequestEvent.TYPE,
				new WidgetRequestEvent.WidgetRequestEventHandler() {

					@Override
					public void onWidgetRequest(WidgetRequestEvent event) {
						Log.debug("Catch Event WidgetRequestEvent");
						doWidgetRequestCommand(event);

					}

				});

		eventBus.addHandler(BackgroundRequestEvent.TYPE,
				new BackgroundRequestEvent.BackgroundRequestEventHandler() {

					@Override
					public void onBackgroundRequest(BackgroundRequestEvent event) {
						Log.debug("Catch Event BackgroundRequestEvent");
						doBackgroundRequestCommand(event);

					}

				});

		eventBus.fireEvent(new UIStateEvent(UIStateType.START));

	}

	protected void checkLocale() {
		String[] locales = LocaleInfo.getAvailableLocaleNames();

		for (String locale : locales) {
			Log.debug("Locale avaible:" + locale);
		}

		String currentLocaleCookie = Cookies.getCookie(LocaleInfo
				.getLocaleCookieName());
		Log.debug(Constants.TD_LANG_COOKIE + ":" + currentLocaleCookie);

		LocaleInfo currentLocaleInfo = LocaleInfo.getCurrentLocale();
		Log.debug("Current Locale:" + currentLocaleInfo.getLocaleName());

		String localeName = currentLocaleInfo.getLocaleName();
		InfoLocale infoLocale;
		if (localeName == null || localeName.isEmpty()) {
			infoLocale = new InfoLocale("en");
		} else {
			infoLocale = new InfoLocale(localeName.substring(0, 2));
		}
		setInfoLocale(infoLocale);

	}

	protected void changeLanguage(String localeName) {
		Date now = new Date();
		long nowLong = now.getTime();
		nowLong = nowLong + (1000 * 60 * 60 * 24 * 21);
		now.setTime(nowLong);
		String cookieLang = Cookies.getCookie(Constants.TD_LANG_COOKIE);
		if (cookieLang != null) {
			Cookies.removeCookie(Constants.TD_LANG_COOKIE);
		}
		Cookies.setCookie(Constants.TD_LANG_COOKIE, localeName, now);
		com.google.gwt.user.client.Window.Location.reload();

		InfoLocale infoLocale;
		if (localeName == null || localeName.isEmpty()) {
			infoLocale = new InfoLocale("en");
		} else {
			infoLocale = new InfoLocale(localeName);
		}
		setInfoLocale(infoLocale);

	}

	protected void setInfoLocale(InfoLocale infoLocale) {
		TDGWTServiceAsync.INSTANCE.setLocale(infoLocale,
				new AsyncCallback<Void>() {

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								UtilsGXT3.alert(msgsCommon.errorLocked(),
										caught.getLocalizedMessage());
							} else {
								UtilsGXT3.alert(msgsCommon.error(),
										caught.getLocalizedMessage());
							}
						}
					}

					public void onSuccess(Void result) {
						Log.debug("InfoLocale set");
					}

				});
	}

	//
	public void restoreUISession() {
		checkLocale();

		String value = com.google.gwt.user.client.Window.Location
				.getParameter(Constants.TABULAR_RESOURCE_ID);
		TRId startTRId = new TRId(value);

		TDGWTServiceAsync.INSTANCE.restoreUISession(startTRId,
				new AsyncCallback<TRId>() {

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								UtilsGXT3.alert(msgsCommon.errorLocked(),
										caught.getLocalizedMessage());
							} else {
								UtilsGXT3.alert(msgsCommon.error(),
										caught.getLocalizedMessage());
							}
						}
					}

					public void onSuccess(TRId trId) {
						if (trId != null) {
							if (trId.getId() != null && !trId.getId().isEmpty()) {
								openTable(trId);
							}
						}

					}

				});

	}

	// TODO
	private void putInBackgroundUIState() {
		try {
			requestCloseCurrent();
			// closeAllTabularResource();
			// openBackgroundMonitor();
			// openTabularResource(true);

		} catch (Exception e) {
			Log.debug("Put In Background :" + e.getLocalizedMessage());
		}
	}

	// Resume state of user interface
	private void resumeUIState() {
		try {
			UIStateEvent uiStateEvent;
			switch (uiState) {
			case START:
				eventBus.fireEvent(new UIStateEvent(UIStateType.START));
				break;
			case TABLECURATION:
				uiStateEvent = new UIStateEvent(UIStateType.TABLECURATION,
						trId, DataViewType.GRID);
				Log.debug("ResumeUIState Fire: " + uiStateEvent);
				eventBus.fireEvent(uiStateEvent);
				break;
			case TABLEUPDATE:
				uiStateEvent = new UIStateEvent(UIStateType.TABLEUPDATE, trId,
						DataViewType.GRID);
				Log.debug("ResumeUIState Fire: " + uiStateEvent);
				eventBus.fireEvent(uiStateEvent);
				break;
			case TR_CLOSE:
				uiStateEvent = new UIStateEvent(UIStateType.TR_CLOSE, trId,
						DataViewType.GRID);
				eventBus.fireEvent(uiStateEvent);
				break;
			case TR_OPEN:
				uiStateEvent = new UIStateEvent(UIStateType.TR_OPEN, trId,
						DataViewType.GRID);
				Log.debug("ResumeUIState Fire: " + uiStateEvent);
				eventBus.fireEvent(uiStateEvent);
				break;
			case TR_READONLY:
				uiStateEvent = new UIStateEvent(UIStateType.TR_READONLY, trId,
						DataViewType.GRID);
				eventBus.fireEvent(uiStateEvent);
				break;
			case WIZARD_OPEN:
				break;
			default:
				break;

			}

		} catch (Exception e) {
			Log.debug("Resume :" + e.getLocalizedMessage());
		}
	}

	// Open Table
	private void openTable(TRId tabularResourceId) {
		Log.debug("openTable: " + tabularResourceId);
		trId = tabularResourceId;
		uiState = UIStateType.TR_OPEN;
		UIStateEvent uiStateEvent = new UIStateEvent(UIStateType.TR_OPEN,
				tabularResourceId, DataViewType.GRID);
		eventBus.fireEvent(uiStateEvent);
	}

	// Update Table
	private void updateTable(TRId tabularResourceId) {
		Log.debug("updateTable: " + tabularResourceId);
		trId = tabularResourceId;
		uiState = UIStateType.TABLEUPDATE;
		UIStateEvent uiStateEvent = new UIStateEvent(UIStateType.TABLEUPDATE,
				tabularResourceId, DataViewType.GRID);
		Log.debug("UpdateTable Fire: " + uiStateEvent);
		eventBus.fireEvent(uiStateEvent);
	}

	// Update Table For Curation
	private void updateTableForCuration(TRId tabularResourceId) {
		Log.debug("updateTableForCuration: " + tabularResourceId);
		trId = tabularResourceId;
		uiState = UIStateType.TABLECURATION;
		UIStateEvent uiStateEvent = new UIStateEvent(UIStateType.TABLECURATION,
				tabularResourceId, DataViewType.GRID);
		Log.debug("UpdateTableForCuration Fire: " + uiStateEvent);
		eventBus.fireEvent(uiStateEvent);
	}

	// Close Tabular Resource
	private void closeAllTabularResource() {
		switch (uiState) {
		case START:
			break;
		case TABLECURATION:
		case TABLEUPDATE:
		case TR_OPEN:
		case TR_READONLY:
		case WIZARD_OPEN:
			Log.debug("CloseTabularResource");
			UIStateEvent uiStateEvent = new UIStateEvent(UIStateType.TR_CLOSE,
					trId, DataViewType.GRID);
			trId = null;
			uiState = UIStateType.TR_CLOSE;
			Log.debug("fireEvent TR_CLOSE");
			try {
				eventBus.fireEvent(uiStateEvent);
			} catch (Exception e) {
				Log.debug("Bus Error: " + e.getMessage());
			}
			closeAllTabularResourceOnServer();
			break;
		case TR_CLOSE:
			break;
		default:
			break;

		}

	}

	private void closeAllTabularResourceOnServer() {
		TDGWTServiceAsync.INSTANCE
				.closeAllTabularResources(new AsyncCallback<Void>() {

					public void onFailure(Throwable caught) {
						UtilsGXT3.alert(msgs.errorClosingAllTabularResource(),
								caught.getLocalizedMessage());
					}

					public void onSuccess(Void result) {

					}

				});

	}

	// TODO
	private void doActiveAfterClose(DataViewActiveEvent event) {
		Log.debug("doActiveAfterClose: " + event);

		DataView dataView = event.getDataView();
		if (dataView instanceof TabularResourceDataView) {
			TabularResourceDataView tabularResourceDataView = (TabularResourceDataView) dataView;
			Log.debug("New TabularResourceDataView:" + tabularResourceDataView);
			openTRIdAfterServerUpdate = tabularResourceDataView.getTrId();
			DataView olderDataView = event.getOldDataView();
			if (olderDataView instanceof TabularResourceDataView) {
				TabularResourceDataView olderTabularResourceDataView = (TabularResourceDataView) olderDataView;
				Log.debug("New TabularResourceDataView:"
						+ olderTabularResourceDataView);
				TDGWTServiceAsync.INSTANCE.closeTabularResourceAndOpen(
						openTRIdAfterServerUpdate,
						olderTabularResourceDataView.getTrId(),

						new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable caught) {

								if (caught instanceof TDGWTSessionExpiredException) {
									eventBus.fireEvent(new SessionExpiredEvent(
											SessionExpiredType.EXPIREDONSERVER));
								} else {
									Log.error("Error in Close and Open TR "
											+ caught.getLocalizedMessage());
									caught.printStackTrace();
									UtilsGXT3.alert(msgsCommon.error(), msgs
											.errorInCloseTR(caught
													.getLocalizedMessage()));
								}

							}

							@Override
							public void onSuccess(Void result) {
								doOpenTRIdAfterServerUpdate();

							}
						}

				);

			} else {

			}
		} else {

		}

	}

	private void doOpenTRIdAfterServerUpdate() {
		if (trId != null
				&& trId.getId().compareTo(openTRIdAfterServerUpdate.getId()) == 0
				&& trId.getTableId().compareTo(trId.getTableId()) == 0) {
			Log.debug("Open Table Break table just open");
		} else {
			Log.debug("Open Table new Table for TR");
			openTable(openTRIdAfterServerUpdate);
		}
	}

	private void doNewActive(DataViewActiveEvent event) {
		Log.debug("doNewActive: " + event);
		DataView dataView = event.getDataView();
		if (dataView instanceof TabularResourceDataView) {
			TabularResourceDataView tabularResourceDataView = (TabularResourceDataView) dataView;
			Log.debug("TabularResourceDataView:" + tabularResourceDataView);
			openTRIdAfterServerUpdate = tabularResourceDataView.getTrId();
			TDGWTServiceAsync.INSTANCE.setActiveTabularResource(
					openTRIdAfterServerUpdate, new AsyncCallback<TRId>() {

						@Override
						public void onFailure(Throwable caught) {

							if (caught instanceof TDGWTSessionExpiredException) {
								eventBus.fireEvent(new SessionExpiredEvent(
										SessionExpiredType.EXPIREDONSERVER));
							} else {
								Log.error(msgs.errorInSetActiveTR(caught
										.getLocalizedMessage()));
								caught.printStackTrace();
								UtilsGXT3.alert(msgsCommon.error(), msgs
										.errorInSetActiveTR(caught
												.getLocalizedMessage()));
							}

						}

						@Override
						public void onSuccess(TRId result) {
							openTRIdAfterServerUpdate = result;
							doOpenTRIdAfterServerUpdate();

						}
					}

			);

		}
	}

	private void doDataViewClose(DataViewActiveEvent event) {
		Log.debug("doDataViewClose: " + event);
		DataView oldDataView = event.getOldDataView();
		if (oldDataView instanceof TabularResourceDataView) {
			closeAllTabularResource();
		} else {

		}
	}

	private void requestCloseCurrent() {
		DataViewRequestEvent dataViewRequestEvent = new DataViewRequestEvent();
		dataViewRequestEvent.setDataViewRequestType(DataViewRequestType.CLOSE);
		dataViewRequestEvent.setDataView(new TabularResourceDataView(trId));
		eventBus.fireEvent(dataViewRequestEvent);
	}

	private void deleteTabularResource() {

		final ConfirmMessageBox mb = new ConfirmMessageBox(msgs.confirm(),
				msgs.areYouSureYouWantToDeleteTheTabularResource());

		mb.addDialogHideHandler(new DialogHideHandler() {

			@Override
			public void onDialogHide(DialogHideEvent event) {
				switch (event.getHideButton()) {
				case NO:
					mb.hide();
					break;
				case YES:
					Log.debug("Remove TR:" + trId);
					TDGWTServiceAsync.INSTANCE.removeTabularResource(trId,
							new AsyncCallback<Void>() {

								public void onFailure(Throwable caught) {
									UtilsGXT3.alert(msgsCommon.error(),
											caught.getLocalizedMessage());
								}

								public void onSuccess(Void result) {
									requestCloseCurrent();
								}

							});

					mb.hide();
					break;
				default:
					break;

				}

			}
		});

		mb.setWidth(300);
		mb.show();

	}

	private void openWizard() {
		eventBus.fireEvent(new UIStateEvent(UIStateType.WIZARD_OPEN));
	}

	private void doRibbonCommand(RibbonEvent event) {
		RibbonType type = event.getRibbonType();
		Log.trace("doRibbonEvent ribbonType: " + type);
		try {
			switch (type) {
			case OPEN:
				openTabularResource(false);
				break;
			case CLONE:
				cloneTabularResource();
				break;
			case CLOSE:
				closeAllTabularResource();
				break;
			case DELETE:
				deleteTabularResource();
				break;
			case PROPERTIES:
				break;
			case IMPORT_SDMX:
				openSDMXImportWizard();
				break;
			case IMPORT_CSV:
				openCSVImportWizard();
				break;
			case IMPORT_JSON:
				break;
			case EXPORT_SDMX:
				openSDMXExportWizard();
				break;
			case EXPORT_CSV:
				openCSVExportWizard();
				break;
			case EXPORT_JSON:
				openJSONExportWizard();
				break;
			case SHARE:
				openShareWindow();
				break;
			case TIMELINE:
				openTasksManagerWizard();
				break;
			case BACKGROUND_TASKS:
				openBackgroundMonitor();
				break;
			case HISTORY:
				break;
			case UNDO:
				callDiscard();
				break;
			case DISCARD_ALL:
				break;
			case HELP:
				break;
			case LOGS:
				openLogsWindow();
				break;
			case VALIDATIONS_SHOW:
				openValidations();
				break;
			case VALIDATIONS_DELETE:
				validationsDelete();
				break;
			case DUPLICATE_DETECTION:
				openDuplicatesRowsDetection();
				break;
			case TABLE_TYPE:
				openTableType();
				break;
			case COLUMN_POSITION:
				openPositionColumn();
				break;
			case COLUMN_LABEL:
				openColumnLabel();
				break;
			case COLUMN_TYPE:
				openChangeColumnType();
				break;
			case COLUMN_ADD:
				openColumnAdd();
				break;
			case COLUMN_DELETE:
				openColumnDelete();
				break;
			case COLUMN_SPLIT:
				openColumnSplit();
				break;
			case COLUMN_MERGE:
				openColumnMerge();
				break;
			case DENORMALIZE:
				openDenormalize();
				break;
			case NORMALIZE:
				openNormalize();
				break;
			case EXTRACT_CODELIST:
				openExtractCodelistWizard();
				break;
			case CODELIST_MAPPING:
				openCodelistMappingWizard();
				break;
			case GENERATE_SUMMARY:
				break;
			case COLUMN_MULTI_FILTER:
				// Filter On multicolumn
				break;
			case COLUMN_REPLACE_BATCH:
				openColumnBatchReplace();
				break;
			case COLUMN_REPLACE_BY_EXPRESSION:
				openColumnReplaceByExpression();
				break;
			case COLUMN_REPLACE_BY_EXTERNAL_COL:
				openReplaceByExternalColWizard();
				break;
			case ROW_DELETE:
				doRowsDelete();
				break;
			case DUPLICATE_DELETE:
				openDuplicatesRowsDelete();
				break;
			case BYEXPRESSION_DELETE:
				openRowsDeleteByExpression();
				break;
			case ROW_ADD:
				doRowAdd();
				break;
			case COLUMN_FILTER:
				openColumnFilter();
				break;
			case TABLE_UNION:
				openTableUnionWizard();
				break;
			case TABLE_GROUPBY:
				openGroupBy();
				break;
			case TABLE_TIME_AGGREGATE:
				openTimeAggregate();
				break;
			case TABLE_AGGREAGETE:
				break;
			case GEOSPATIAL_CSQUARE:
				openGeospatialCSquare();
				break;
			case GEOSPATIAL_OCEAN_AREA:
				openGeospatialOceanArea();
				break;
			case DOWNSCALE_CSQUARE:
				openDownscaleCSquare();
				break;
			case GEOMETRY_POINT:
				openGeometryCreatePoint();
				break;
			case ANNOTATION_ADD:
				break;
			case COLUMN_BATCH_REPLACE:
				break;
			case RULE_MODIFY:
				openModifyRule();
				break;
			case RULE_DELETE:
				openDeleteRule();
				break;
			case RULES_ACTIVE:
				openRulesActiveOnTabularResource();
				break;
			case RULE_SHARE:
				openShareRule();
				break;
			case RULE_ON_COLUMN_NEW:
				openOnColumnNewRule();
				break;
			case RULE_ON_COLUMN_APPLY:
				openOnColumnApplyRule();
				break;
			case RULE_ON_TABLE_NEW:
				openOnTableNewRule();
				break;
			case RULE_ON_TABLE_APPLY:
				openOnTableApplyRule();
				break;
			case TEMPLATE_OPEN:
				openTemplateOpen();
				break;
			case TEMPLATE_NEW:
				openTemplateNew();
				break;
			case TEMPLATE_DELETE:
				openTemplateDelete();
				break;
			case TEMPLATE_APPLY:
				openTemplateApply();
				break;
			case TEMPLATE_SHARE:
				openTemplateShare();
				break;
			case ANALYSE_TABLE_FILTER:
				break;
			case ANALYSE_TABLE_UNION:
				break;
			case ANALYSE_TABLE_GROUPBY:
				break;
			case ANALYSE_TABLE_AGGREAGETE:
				break;
			case ANALYSE_ROW_ADD:
				break;
			case ANALYSE_ROW_DELETE:
				break;
			case ANALYSE_DUPLICATE_DELETE:
				break;
			case ANALYSE_ANNOTATION_ADD:
				break;
			case ANALYSE_COLUMN_FILTER:
				break;
			case ANALYSE_COLUMN_EDIT:
				break;
			case CHART_CREATE:
				openCreateChart();
				break;
			case R_STUDIO:
				openRStudio();
				break;
			case STATISTICAL:
				openStatistical();
				break;
			case SPREAD:
				break;
			case CREATE_GIS_MAP:
				openCreateMapWizard();
				break;
			case LANGUAGE_EN:
				changeLanguage("en");
				break;
			case LANGUAGE_ES:
				changeLanguage("es");
				break;
			case LANGUAGE_IT:
				changeLanguage("it");
				break;
			case TEST:
				testFeauture();
				break;
			default:
				break;
			}
		} catch (Throwable e) {
			Log.error("doRibbonCommand Error : " + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	private void doTasksMonitorCommand(TasksMonitorEvent event) {
		TaskType taskType = event.getTaskType();
		Log.debug("Received TaskMonitorEvent: " + taskType);
		switch (taskType) {
		case OPENTABLE:
			// openTable(event.getTrId());
			break;
		default:
			break;
		}
	}

	private void doWidgetRequestCommand(WidgetRequestEvent event) {
		WidgetRequestType widgetRequestType = event.getWidgetRequestType();
		switch (widgetRequestType) {
		case CHANGECOLUMNTYPEPANEL:
			break;
		case CHANGETABLETYPEPANEL:
			break;
		case CURATIONBYREPLACEBATCHDIALOG:
			openBatchReplace(event.getTrId(), event.getRequestProperties());
			break;
		case DELETECOLUMNPANEL:
			break;
		case DUPLICATESROWSDETECTIONPANEL:
			break;
		case LABELCOLUMNPANEL:
			break;
		case VALIDATIONSTASKSPANEL:
			break;
		default:
			break;

		}

	}


	private void doBackgroundRequestCommand(BackgroundRequestEvent event) {
		BackgroundRequestType type = event.getBackgroundRequestType();
		Log.trace("doBackgroundRequestEvent BackgroundRequestType: " + type);
		try {
			switch (type) {
			case BACKGROUND:
				putInBackgroundUIState();
				break;
			default:
				break;
			}
		} catch (Throwable e) {
			Log.error("doBackgroundRequestCommand Error : "
					+ e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	private void openLogsWindow() {
		Log.debug("Request Open Logs Window");
		TDMLogs tdmLogs = new TDMLogs(eventBus);
		tdmLogs.show();
	}

	private void openBatchReplace(TRId trId, RequestProperties requestProperties) {
		Log.debug("Request Open Batch Replace Dialog: " + trId + " "
				+ requestProperties);
		if (trId != null) {
			ReplaceBatchDialog dialog = new ReplaceBatchDialog(trId,
					requestProperties, eventBus);
			dialog.show();
		} else {
			Log.error("TRId is null");
			UtilsGXT3
					.alert(msgsCommon.error(), msgs.noTabularResourcePresent());
		}
	}

	private void openSDMXImportWizard() {
		GWT.runAsync(new RunAsyncCallback() {

			public void onSuccess() {
				openWizard();
				SDMXImportWizardTD importWizard = new SDMXImportWizardTD(msgs
						.sdmxImport(), eventBus);
				importWizard.addListener(new WizardListener() {

					@Override
					public void completed(TRId tabularResourceId) {
						openTable(tabularResourceId);
					}

					@Override
					public void putInBackground() {
						Log.debug("PutInBakground");
						resumeUIState();
					}

					@Override
					public void aborted() {
						resumeUIState();
					}

					@Override
					public void failed(String title, String message,
							String details, Throwable throwable) {
						UtilsGXT3.alert(title, message + " " + details);
						resumeUIState();
					}

				});

				importWizard.show();
			}

			public void onFailure(Throwable reason) {
				asyncCodeLoadingFailed(reason);
			}
		});

	}

	private void openCSVExportWizard() {
		GWT.runAsync(new RunAsyncCallback() {

			public void onSuccess() {
				openWizard();
				CSVExportWizardTD exportWizard = new CSVExportWizardTD(msgs
						.csvExport(), eventBus);

				exportWizard.addListener(new WizardListener() {

					@Override
					public void completed(TRId id) {
						resumeUIState();

					}

					@Override
					public void putInBackground() {
						Log.debug("PutInBakground");
						resumeUIState();
					}

					@Override
					public void aborted() {
						resumeUIState();
					}

					@Override
					public void failed(String title, String message,
							String details, Throwable throwable) {
						UtilsGXT3.alert(title, message + " " + details);
						resumeUIState();
					}

				});

				exportWizard.show();
			}

			public void onFailure(Throwable reason) {
				asyncCodeLoadingFailed(reason);
			}
		});

	}

	private void openJSONExportWizard() {
		GWT.runAsync(new RunAsyncCallback() {

			public void onSuccess() {
				openWizard();
				JSONExportWidgetTD exportWizard = new JSONExportWidgetTD(msgs
						.jsonExport(), eventBus);

				exportWizard.addListener(new WizardListener() {

					@Override
					public void completed(TRId id) {
						resumeUIState();

					}

					@Override
					public void putInBackground() {
						Log.debug("PutInBakground");
						resumeUIState();
					}

					@Override
					public void aborted() {
						resumeUIState();
					}

					@Override
					public void failed(String title, String message,
							String details, Throwable throwable) {
						UtilsGXT3.alert(title, message + " " + details);
						resumeUIState();
					}

				});

				exportWizard.show();
			}

			public void onFailure(Throwable reason) {
				asyncCodeLoadingFailed(reason);
			}
		});

	}

	private void openSDMXExportWizard() {
		GWT.runAsync(new RunAsyncCallback() {

			public void onSuccess() {
				openWizard();
				SDMXExportWizardTD exportWizard = new SDMXExportWizardTD(msgs
						.sdmxExport(), eventBus);

				exportWizard.addListener(new WizardListener() {
					public void failed(String title, String message,
							String details, Throwable throwable) {
						UtilsGXT3.alert(title, message + " " + details);
						resumeUIState();
					}

					public void completed(TRId id) {
						resumeUIState();

					}

					@Override
					public void putInBackground() {
						Log.debug("PutInBakground");
						resumeUIState();
					}

					public void aborted() {
						resumeUIState();
					}
				});

				exportWizard.show();
			}

			public void onFailure(Throwable reason) {
				asyncCodeLoadingFailed(reason);
			}
		});

	}

	private void openExtractCodelistWizard() {
		GWT.runAsync(new RunAsyncCallback() {

			public void onSuccess() {
				openWizard();
				ExtractCodelistWizardTD extractCodelistWizard = new ExtractCodelistWizardTD(
						trId, msgs.extractCodelist(), eventBus);

				extractCodelistWizard.addListener(new WizardListener() {
					public void failed(String title, String message,
							String details, Throwable throwable) {
						UtilsGXT3.alert(title, message + " " + details);
						resumeUIState();
					}

					public void completed(TRId id) {
						openTable(id);

					}

					@Override
					public void putInBackground() {
						Log.debug("PutInBakground");
						resumeUIState();
					}

					public void aborted() {
						resumeUIState();
					}
				});

				extractCodelistWizard.show();
			}

			public void onFailure(Throwable reason) {
				asyncCodeLoadingFailed(reason);
			}
		});

	}

	private void openCodelistMappingWizard() {
		GWT.runAsync(new RunAsyncCallback() {

			public void onSuccess() {
				openWizard();
				CodelistMappingImportWizardTD codelistMappingWizard = new CodelistMappingImportWizardTD(
						trId, msgs.codelistMappingImport(), eventBus);

				codelistMappingWizard.addListener(new WizardListener() {
					public void failed(String title, String message,
							String details, Throwable throwable) {
						UtilsGXT3.alert(title, message + " " + details);
						resumeUIState();
					}

					public void completed(TRId id) {
						openTable(id);

					}

					@Override
					public void putInBackground() {
						Log.debug("PutInBakground");
						resumeUIState();
					}

					public void aborted() {
						resumeUIState();
					}
				});

				codelistMappingWizard.show();
			}

			public void onFailure(Throwable reason) {
				asyncCodeLoadingFailed(reason);
			}
		});

	}

	private void openTableUnionWizard() {
		GWT.runAsync(new RunAsyncCallback() {

			public void onSuccess() {
				openWizard();
				UnionWizardTD unionWizard = new UnionWizardTD(trId, msgs
						.union(), eventBus);

				unionWizard.addListener(new WizardListener() {
					public void failed(String title, String message,
							String details, Throwable throwable) {
						UtilsGXT3.alert(title, message + " " + details);
						resumeUIState();
					}

					public void completed(TRId id) {
						openTable(id);

					}

					@Override
					public void putInBackground() {
						Log.debug("PutInBakground");
						resumeUIState();
					}

					public void aborted() {
						resumeUIState();
					}
				});

				unionWizard.show();
			}

			public void onFailure(Throwable reason) {
				asyncCodeLoadingFailed(reason);
			}
		});

	}

	private void openReplaceByExternalColWizard() {
		openReplaceByExternalColWizard(null, null);
	}

	private void openReplaceByExternalColWizard(final String columnLocalId,
			final String columnname) {
		GWT.runAsync(new RunAsyncCallback() {

			public void onSuccess() {
				openWizard();

				ReplaceByExternalTD replaceByExternalColWizard;
				if (columnname == null || columnname.isEmpty()) {

					replaceByExternalColWizard = new ReplaceByExternalTD(trId,
							msgs.replaceByExternalColumns(), eventBus);
				} else {
					replaceByExternalColWizard = new ReplaceByExternalTD(trId,
							columnname, msgs.replaceByExternalColumns(),
							eventBus);
				}

				replaceByExternalColWizard.addListener(new WizardListener() {
					public void failed(String title, String message,
							String details, Throwable throwable) {
						UtilsGXT3.alert(title, message + " " + details);
						resumeUIState();
					}

					public void completed(TRId id) {
						openTable(id);

					}

					@Override
					public void putInBackground() {
						Log.debug("PutInBakground");
						resumeUIState();
					}

					public void aborted() {
						resumeUIState();
					}
				});

				replaceByExternalColWizard.show();
			}

			public void onFailure(Throwable reason) {
				asyncCodeLoadingFailed(reason);
			}
		});

	}

	private void openCreateChart() {
		GWT.runAsync(new RunAsyncCallback() {

			public void onSuccess() {
				openWizard();
				ChartsWidgetTD chartWizard = new ChartsWidgetTD(trId, userInfo,
						msgs.chartsCreation(), eventBus);

				chartWizard.addListener(new WizardListener() {
					public void failed(String title, String message,
							String details, Throwable throwable) {
						UtilsGXT3.alert(title, message + " " + details);
						resumeUIState();
					}

					public void completed(TRId id) {
						trId = id;
						uiState = UIStateType.TR_OPEN;
						UIStateEvent uiStateEvent = new UIStateEvent(
								UIStateType.TR_OPEN, trId,
								DataViewType.RESOURCES);
						eventBus.fireEvent(uiStateEvent);

					}

					@Override
					public void putInBackground() {
						Log.debug("PutInBakground");
						resumeUIState();
					}

					public void aborted() {
						resumeUIState();
					}
				});

			}

			public void onFailure(Throwable reason) {
				asyncCodeLoadingFailed(reason);
			}
		});

	}

	private void openCreateMapWizard() {
		GWT.runAsync(new RunAsyncCallback() {

			public void onSuccess() {
				openWizard();
				MapWidgetTD mapWizard = new MapWidgetTD(trId, userInfo, msgs
						.mapCreation(), eventBus);

				mapWizard.addListener(new WizardListener() {
					public void failed(String title, String message,
							String details, Throwable throwable) {
						UtilsGXT3.alert(title, message + " " + details);
						resumeUIState();
					}

					public void completed(TRId id) {
						trId = id;
						uiState = UIStateType.TR_OPEN;
						UIStateEvent uiStateEvent = new UIStateEvent(
								UIStateType.TR_OPEN, trId,
								DataViewType.RESOURCES);
						eventBus.fireEvent(uiStateEvent);

					}

					@Override
					public void putInBackground() {
						Log.debug("PutInBakground");
						resumeUIState();
					}

					public void aborted() {
						resumeUIState();
					}
				});

			}

			public void onFailure(Throwable reason) {
				asyncCodeLoadingFailed(reason);
			}
		});

	}

	/**
	 * @param switchState
	 * 
	 */
	private void openTabularResource(final boolean switchState) {
		Log.info("Open Tabular Resources");

		GWT.runAsync(new RunAsyncCallback() {

			public void onSuccess() {
				openWizard();
				String title;
				if (switchState) {
					title = msgs.openSwitchesTR();
				} else {
					title = msgs.openTR();
				}
				TDOpen tdOpen = new TDOpen(trId, title, eventBus);
				tdOpen.addListener(new WizardListener() {

					@Override
					public void putInBackground() {
						Log.debug("PutInBakground");
						resumeUIState();
					}

					public void aborted() {
						resumeUIState();
					}

					public void failed(String title, String message,
							String details, Throwable throwable) {
						UtilsGXT3.alert(title, message + " " + details);
						resumeUIState();
					}

					public void completed(TRId tabularResourceId) {
						openTable(tabularResourceId);

					}

				});
				Log.info("TDOpen add Listener");
				tdOpen.show();
			}

			public void onFailure(Throwable reason) {
				asyncCodeLoadingFailed(reason);
			}
		});

	}

	private void openCSVImportWizard() {
		GWT.runAsync(new RunAsyncCallback() {

			public void onSuccess() {
				openWizard();
				CSVImportWizardTD importWizard = new CSVImportWizardTD(msgs
						.csvImport(), eventBus);

				importWizard.addListener(new WizardListener() {

					public void failed(String title, String message,
							String details, Throwable throwable) {
						UtilsGXT3.alert(title, message + " " + details);
						resumeUIState();
					}

					public void completed(TRId id) {
						openTable(id);

					}

					@Override
					public void putInBackground() {
						Log.debug("PutInBakground");
						resumeUIState();
					}

					public void aborted() {
						resumeUIState();
					}
				});

				importWizard.show();
			}

			public void onFailure(Throwable reason) {
				asyncCodeLoadingFailed(reason);
			}
		});

	}

	private com.extjs.gxt.ui.client.widget.Window tdTaskMainWindow;

	private void openTasksManagerWizard() {
		GWT.runAsync(new RunAsyncCallback() {

			public void onSuccess() {
				initTaskManager();
				tdTaskMainWindow.setPosition(
						(Window.getClientWidth() / 2) - 200,
						(Window.getClientHeight() / 2) - 300);
				tdTaskController.updateTasks(true);
				tdTaskMainWindow.setModal(true);
				tdTaskMainWindow.show();

			}

			public void onFailure(Throwable reason) {
				asyncCodeLoadingFailed(reason);
			}

		});
	}

	private void initTaskManager() {
		if (!taskManagerInit) {
			taskManagerInit = true;
			tdTaskController = TdTaskController.getInstance();
			TdTaskController.bindCommonBus(eventBus);
			tdTaskMainWindow = tdTaskController.getWindowTaskMonitor();
		} else {

		}
	}

	private void openTemplateNew() {
		GWT.runAsync(new RunAsyncCallback() {
			public void onSuccess() {
				TdTemplateController tdTemplateController = new TdTemplateController();
				TdTemplateController.bindCommonBus(eventBus);
				tdTemplateController.getWindowTemplatePanel().show();
			}

			public void onFailure(Throwable reason) {
				asyncCodeLoadingFailed(reason);
			}
		});
	}

	private void openTemplateDelete() {
		GWT.runAsync(new RunAsyncCallback() {
			public void onSuccess() {
				TemplateDeleteDialog tdDialog = new TemplateDeleteDialog(
						eventBus);
				tdDialog.show();
			}

			public void onFailure(Throwable reason) {
				asyncCodeLoadingFailed(reason);
			}
		});

	}

	private void openTemplateOpen() {
		GWT.runAsync(new RunAsyncCallback() {
			public void onSuccess() {
				Log.debug("Template Open call");
				TemplateOpenDialog tdDialog = new TemplateOpenDialog(eventBus);
				tdDialog.show();
			}

			public void onFailure(Throwable reason) {
				asyncCodeLoadingFailed(reason);
			}
		});

	}

	private void openTemplateApply() {
		GWT.runAsync(new RunAsyncCallback() {
			public void onSuccess() {
				Log.debug("Template Apply call");
				if (trId != null) {
					retrieveTabResourceInformation(CheckFor.ApplyTemplate);

				} else {
					Log.error("TRId is null");
					UtilsGXT3.alert(msgsCommon.error(),
							msgs.noCurrentTabularResourcePresent());
				}
			}

			public void onFailure(Throwable reason) {
				asyncCodeLoadingFailed(reason);
			}
		});

	}

	private void openTemplateApplyIsOwner(final TabResource tabResource) {
		if (userInfo.getUsername().compareTo(tabResource.getOwnerLogin()) == 0) {
			GWT.runAsync(new RunAsyncCallback() {
				public void onSuccess() {
					TemplateApplyDialog taDialog = new TemplateApplyDialog(
							trId, eventBus);
					taDialog.show();
				}

				public void onFailure(Throwable reason) {
					asyncCodeLoadingFailed(reason);
				}

			});
		} else {
			UtilsGXT3.info(msgsCommon.attention(),
					msgs.attentionNotOwnerTemplateApply());
		}

	}

	private void openTemplateShare() {
		GWT.runAsync(new RunAsyncCallback() {
			public void onSuccess() {
				Log.debug("Template Share call");
				TemplateShareDialog tsDialog = new TemplateShareDialog(
						userInfo, eventBus);
				tsDialog.show();

			}

			public void onFailure(Throwable reason) {
				asyncCodeLoadingFailed(reason);
			}
		});

	}

	private void openShareWindow() {
		GWT.runAsync(new RunAsyncCallback() {
			public void onSuccess() {
				Log.debug("Open Share Window");
				@SuppressWarnings("unused")
				TRShare trShare = new TRShare(userInfo, trId, eventBus);
			}

			public void onFailure(Throwable reason) {
				asyncCodeLoadingFailed(reason);
			}
		});
	}

	private void openModifyRule() {
		GWT.runAsync(new RunAsyncCallback() {
			public void onSuccess() {
				Log.debug("Request Open Modify Rule Dialog");
				RuleOpenDialog cfDialog = new RuleOpenDialog(eventBus);
				cfDialog.show();
			}

			public void onFailure(Throwable reason) {
				asyncCodeLoadingFailed(reason);
			}
		});

	}

	private void openDeleteRule() {
		GWT.runAsync(new RunAsyncCallback() {
			public void onSuccess() {
				Log.debug("Request Open Delete Rule Dialog");
				RuleDeleteDialog cfDialog = new RuleDeleteDialog(eventBus);
				cfDialog.show();
			}

			public void onFailure(Throwable reason) {
				asyncCodeLoadingFailed(reason);
			}
		});
	}

	private void openRulesActiveOnTabularResource() {
		GWT.runAsync(new RunAsyncCallback() {
			public void onSuccess() {
				Log.debug("Request Open Rules Active on TabularResource Dialog");
				if (trId != null) {
					RuleActiveDialog raDialog = new RuleActiveDialog(trId,
							eventBus);
					raDialog.show();
				} else {
					Log.error("TRId is null");
					UtilsGXT3.alert(msgsCommon.error(),
							msgs.noCurrentTabularResourcePresent());
				}
			}

			public void onFailure(Throwable reason) {
				asyncCodeLoadingFailed(reason);
			}
		});
	}

	private void openShareRule() {
		GWT.runAsync(new RunAsyncCallback() {
			public void onSuccess() {
				Log.debug("Request Open Share Rule Dialog");
				RuleShareDialog cfDialog = new RuleShareDialog(userInfo, eventBus);
				cfDialog.show();
			}

			public void onFailure(Throwable reason) {
				asyncCodeLoadingFailed(reason);
			}
		});
	}

	private void openOnColumnNewRule() {
		GWT.runAsync(new RunAsyncCallback() {
			public void onSuccess() {
				Log.debug("Request Open On Column New Rule Dialog");
				RuleOnColumnCreateDialog cfDialog = new RuleOnColumnCreateDialog(
						eventBus);
				cfDialog.show();
			}

			public void onFailure(Throwable reason) {
				asyncCodeLoadingFailed(reason);
			}
		});
	}

	private void openOnColumnApplyRule() {
		Log.debug("Request Open On Column Apply Rule Dialog");
		if (trId != null) {
			retrieveTabResourceInformation(CheckFor.ApplyColumnRule);
		} else {
			Log.error("TRId is null");
			UtilsGXT3.alert(msgsCommon.error(),
					msgs.noCurrentTabularResourcePresent());
		}

	}

	private void retrieveTabResourceInformation(final CheckFor checkFor) {
		TDGWTServiceAsync.INSTANCE.getTabResourceInformation(trId,
				new AsyncCallback<TabResource>() {

					public void onSuccess(TabResource tabResource) {
						Log.info("Retrieved TR: " + tabResource);
						if (checkFor == null) {
							UtilsGXT3.alert(msgsCommon.error(),
									"Error retrieving tabular resource informations!");
							Log.error("CheckFor is null");
							return;
						}
						switch (checkFor) {
						case ApplyColumnRule:
							openOnColumnApplyRuleIsOwner(tabResource);
							break;
						case ApplyTableRule:
							openOnTableApplyRuleIsOwner(tabResource);
							break;
						case ApplyTemplate:
							openTemplateApplyIsOwner(tabResource);
							break;
						default:
							break;
						}
					}

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								UtilsGXT3.alert(msgsCommon.errorLocked(),
										caught.getLocalizedMessage());
							} else {
								Log.error("Error retrienving properties: "
										+ caught.getLocalizedMessage());
							}

						}
					}

				});
	}

	private void openOnColumnApplyRuleIsOwner(final TabResource tabResource) {
		if (userInfo.getUsername().compareTo(tabResource.getOwnerLogin()) == 0) {
			GWT.runAsync(new RunAsyncCallback() {
				public void onSuccess() {
					RuleOnColumnApplyDialog raDialog = new RuleOnColumnApplyDialog(
							trId, eventBus);
					raDialog.show();
				}

				public void onFailure(Throwable reason) {
					asyncCodeLoadingFailed(reason);
				}

			});
		} else {
			UtilsGXT3.info(msgsCommon.attention(),
					msgs.attentionNotOwnerRuleApply());
		}

	}

	private void openOnTableNewRule() {
		Log.debug("Request Open On Table New Rule Dialog");
		GWT.runAsync(new RunAsyncCallback() {

			public void onSuccess() {
				openWizard();
				RuleOnTableNewWizard ruleOnTableNewWizard = new RuleOnTableNewWizard(
						eventBus);

				ruleOnTableNewWizard.addListener(new WizardListener() {
					public void failed(String title, String message,
							String details, Throwable throwable) {
						UtilsGXT3.alert(title, message + " " + details);
						resumeUIState();
					}

					public void completed(TRId id) {
						resumeUIState();

					}

					@Override
					public void putInBackground() {
						Log.debug("PutInBakground");
						resumeUIState();
					}

					public void aborted() {
						resumeUIState();
					}
				});

				ruleOnTableNewWizard.show();
			}

			public void onFailure(Throwable reason) {
				asyncCodeLoadingFailed(reason);
			}
		});

	}

	private void openOnTableApplyRule() {
		Log.debug("Request Open On Table Apply Rule Dialog");
		if (trId != null) {
			retrieveTabResourceInformation(CheckFor.ApplyTableRule);
		} else {
			Log.error("TRId is null");
			UtilsGXT3.alert(msgsCommon.error(),
					msgs.noCurrentTabularResourcePresent());
		}

	}

	private void openOnTableApplyRuleIsOwner(TabResource tabResource) {
		if (userInfo.getUsername().compareTo(tabResource.getOwnerLogin()) == 0) {
			GWT.runAsync(new RunAsyncCallback() {
				public void onSuccess() {
					openWizard();
					RuleOnTableApplyWizard ruleOnTableApplyWizard = new RuleOnTableApplyWizard(
							trId, eventBus);

					ruleOnTableApplyWizard.addListener(new WizardListener() {
						public void failed(String title, String message,
								String details, Throwable throwable) {
							UtilsGXT3.alert(title, message + " " + details);
							resumeUIState();
						}

						public void completed(TRId id) {
							openTable(id);

						}

						@Override
						public void putInBackground() {
							Log.debug("PutInBakground");
							resumeUIState();
						}

						public void aborted() {
							resumeUIState();
						}
					});

					ruleOnTableApplyWizard.show();
				}

				public void onFailure(Throwable reason) {
					asyncCodeLoadingFailed(reason);
				}
			});
		} else {
			UtilsGXT3.info(msgsCommon.attention(),
					msgs.attentionNotOwnerRuleApply());
		}

	}

	// TODO
	private void testFeauture() {
		GWT.runAsync(new RunAsyncCallback() {
			public void onSuccess() {
				Log.debug("ResourceListView call");
				if (trId != null) {
					/*
					 * ResourcesListViewDialog resourcesListViewDialog = new
					 * ResourcesListViewDialog(eventBus);
					 * resourcesListViewDialog.show();
					 * resourcesListViewDialog.open(trId);
					 */
					TDXTabPanelDialog d = new TDXTabPanelDialog(eventBus);
					d.show();
					d.open(trId);

				} else {
					Log.error("TRId is null");
					UtilsGXT3.alert(msgsCommon.error(),
							msgs.noCurrentTabularResourcePresent());
				}
			}

			public void onFailure(Throwable reason) {
				asyncCodeLoadingFailed(reason);
			}
		});

	}

	private void callDiscard() {
		HistoryDiscard historyDiscard = new HistoryDiscard(eventBus);
		historyDiscard.discard();
	}

	private void openRStudio() {
		Log.debug("Request Open RStudio");
		if (trId != null) {
			@SuppressWarnings("unused")
			RStudio rStudio = new RStudio(trId, eventBus);
		} else {
			Log.error("TRId is null");
			UtilsGXT3.alert(msgsCommon.error(),
					msgs.noCurrentTabularResourcePresent());
		}

	}

	private void openStatistical() {
		GWT.runAsync(new RunAsyncCallback() {
			public void onSuccess() {
				@SuppressWarnings("unused")
				StatisticalWidget statisticalWidget = new StatisticalWidget(
						trId, eventBus);
			}

			public void onFailure(Throwable reason) {
				asyncCodeLoadingFailed(reason);
			}
		});

	}

	@SuppressWarnings("unused")
	private void openMultiColumnFilter() {
		Log.debug("Request Open Multi Column Filter Dialog");
		if (trId != null) {
			MultiColumnFilterDialog mcfDialog = new MultiColumnFilterDialog(
					trId, eventBus);
			mcfDialog.show();
		} else {
			Log.error("TRId is null");
			UtilsGXT3.alert(msgsCommon.error(),
					msgs.noCurrentTabularResourcePresent());
		}
	}

	private void openColumnFilter() {
		openColumnFilter(null, null);
	}

	private void openColumnFilter(String columnLocalId, String columnName) {
		Log.debug("Request Open Column Filter Dialog");
		if (trId != null) {
			/*
			 * ColumnFilterDialog cfDialog = new ColumnFilterDialog(trId,
			 * columnLocalId, eventBus); cfDialog.show();
			 */
			MultiColumnFilterDialog mcfDialog = new MultiColumnFilterDialog(
					trId, eventBus);
			mcfDialog.show();

		} else {
			Log.error("TRId is null");
			UtilsGXT3.alert(msgsCommon.error(),
					msgs.noCurrentTabularResourcePresent());
		}
	}

	private void openRowsDeleteByExpression() {
		openRowsDeleteByExpression(null, null);
	}

	private void openRowsDeleteByExpression(String columnLocalId,
			String columnName) {
		Log.debug("Request Open Row Delete By Expression Dialog");
		if (trId != null) {
			RowsDeleteByMultiColumnExpressionDialog rowsDeleteDialog = new RowsDeleteByMultiColumnExpressionDialog(
					trId, eventBus);
			rowsDeleteDialog.show();
		} else {
			Log.error("TRId is null");
			UtilsGXT3.alert(msgsCommon.error(),
					msgs.noCurrentTabularResourcePresent());
		}
	}

	private void openColumnReplaceByExpression() {
		openColumnReplaceByExpression(null, null);
	}

	private void openColumnReplaceByExpression(String columnLocalId,
			String columnName) {
		Log.debug("Request Open Replace Column By Expression Dialog");
		if (trId != null) {
			ReplaceColumnByMultiColumnExpressionDialog rceDialog = new ReplaceColumnByMultiColumnExpressionDialog(
					trId, columnLocalId, eventBus);
			rceDialog.show();
		} else {
			Log.error("TRId is null");
			UtilsGXT3.alert(msgsCommon.error(),
					msgs.noCurrentTabularResourcePresent());
		}
	}

	private void openChangeColumnType() {
		openChangeColumnType(null, null);
	}

	private void openChangeColumnType(String columnLocalId, String columnName) {
		Log.debug("Request Change Column Type Tab");
		if (trId != null) {
			WidgetRequestEvent e = new WidgetRequestEvent(
					WidgetRequestType.CHANGECOLUMNTYPEPANEL);
			e.setTrId(trId);
			e.setColumnLocalId(columnLocalId);
			e.setColumnName(columnName);
			eventBus.fireEvent(e);
		} else {
			Log.error("TRId is null");
			UtilsGXT3.alert(msgsCommon.error(),
					msgs.noCurrentTabularResourcePresent());
		}
	}

	private void openNormalize() {
		openNormalize(null);
	}

	private void openNormalize(String columnName) {
		Log.debug("Request Open Normalize Tab");
		if (trId != null) {
			WidgetRequestEvent e = new WidgetRequestEvent(
					WidgetRequestType.NORMALIZEPANEL);
			e.setTrId(trId);
			e.setColumnName(columnName);
			eventBus.fireEvent(e);
		} else {
			Log.error("TRId is null");
			UtilsGXT3.alert(msgsCommon.error(),
					msgs.noCurrentTabularResourcePresent());
		}
	}

	private void openDenormalize() {
		openDenormalize(null);
	}

	private void openDenormalize(String columnName) {
		Log.debug("Request Open Denormalize Tab");
		if (trId != null) {
			WidgetRequestEvent e = new WidgetRequestEvent(
					WidgetRequestType.DENORMALIZEPANEL);
			e.setTrId(trId);
			e.setColumnName(columnName);
			eventBus.fireEvent(e);
		} else {
			Log.error("TRId is null");
			UtilsGXT3.alert(msgsCommon.error(),
					msgs.noCurrentTabularResourcePresent());
		}
	}

	private void cloneTabularResource() {
		CloneTabularResource cloneTR = new CloneTabularResource(trId, eventBus);
		cloneTR.cloneTR();
	}

	private void openColumnBatchReplace() {
		openColumnBatchReplace(null, null);
	}

	private void openColumnBatchReplace(String columnLocalId, String columnName) {
		Log.debug("Request Open Batch Replace Dialog");
		if (trId != null) {
			ReplaceBatchDialog dialog = new ReplaceBatchDialog(trId,
					columnLocalId, eventBus);
			dialog.show();
		} else {
			Log.error("TRId is null");
			UtilsGXT3
					.alert(msgsCommon.error(), msgs.noTabularResourcePresent());
		}
	};

	private void openColumnAdd() {
		openColumnAdd(null, null);
	}

	private void openColumnAdd(String columnLocalId, String columnName) {
		Log.debug("Request Add Column Tab");
		if (trId != null) {
			WidgetRequestEvent e = new WidgetRequestEvent(
					WidgetRequestType.ADDCOLUMNPANEL);
			e.setTrId(trId);
			eventBus.fireEvent(e);
		} else {
			Log.error("TRId is null");
			UtilsGXT3
					.alert(msgsCommon.error(), msgs.noTabularResourcePresent());
		}
	}

	private void openColumnDelete() {
		openColumnDelete(null, null);
	}

	private void openColumnDelete(String columnLocalId, String columnName) {
		Log.debug("Request Delete Column Tab");
		if (trId != null) {
			WidgetRequestEvent e = new WidgetRequestEvent(
					WidgetRequestType.DELETECOLUMNPANEL);
			e.setTrId(trId);
			e.setColumnLocalId(columnLocalId);
			e.setColumnName(columnName);
			eventBus.fireEvent(e);
		} else {
			Log.error("TRId is null");
			UtilsGXT3
					.alert(msgsCommon.error(), msgs.noTabularResourcePresent());
		}
	}

	private void openColumnSplit() {
		openColumnSplit(null, null);
	}

	private void openColumnSplit(String columnLocalId, String columnName) {
		Log.debug("Request Split Column Tab");
		if (trId != null) {
			WidgetRequestEvent e = new WidgetRequestEvent(
					WidgetRequestType.SPLITCOLUMNPANEL);
			e.setTrId(trId);
			e.setColumnLocalId(columnLocalId);
			e.setColumnName(columnName);
			eventBus.fireEvent(e);
		} else {
			Log.error("TRId is null");
			UtilsGXT3
					.alert(msgsCommon.error(), msgs.noTabularResourcePresent());
		}
	}

	private void openColumnMerge() {
		openColumnMerge(null, null);
	}

	private void openColumnMerge(String columnLocalId, String columnName) {
		Log.debug("Request Merge Column Tab");
		if (trId != null) {
			WidgetRequestEvent e = new WidgetRequestEvent(
					WidgetRequestType.MERGECOLUMNPANEL);
			e.setTrId(trId);
			e.setColumnLocalId(columnLocalId);
			e.setColumnName(columnName);
			eventBus.fireEvent(e);
		} else {
			Log.error("TRId is null");
			UtilsGXT3
					.alert(msgsCommon.error(), msgs.noTabularResourcePresent());
		}
	}

	private void openGroupBy() {
		openGroupBy(null);
	}

	private void openGroupBy(String columnName) {
		Log.debug("Request Group By Tab");
		if (trId != null) {
			WidgetRequestEvent e = new WidgetRequestEvent(
					WidgetRequestType.GROUPBYPANEL);
			e.setTrId(trId);
			e.setColumnName(columnName);
			eventBus.fireEvent(e);
		} else {
			Log.error("TRId is null");
			UtilsGXT3
					.alert(msgsCommon.error(), msgs.noTabularResourcePresent());
		}
	}

	private void openTimeAggregate() {
		openTimeAggregate(null);
	}

	private void openTimeAggregate(String columnName) {
		Log.debug("Request Time Aggregate Tab");
		if (trId != null) {
			WidgetRequestEvent e = new WidgetRequestEvent(
					WidgetRequestType.TIMEAGGREGATIONPANEL);
			e.setTrId(trId);
			e.setColumnName(columnName);
			eventBus.fireEvent(e);
		} else {
			Log.error("TRId is null");
			UtilsGXT3
					.alert(msgsCommon.error(), msgs.noTabularResourcePresent());
		}
	}

	private void openPositionColumn() {
		openPositionColumn(null, null);
	}

	private void openPositionColumn(String columnLocalId, String columnName) {
		Log.debug("Request Position Column Tab");
		if (trId != null) {
			WidgetRequestEvent e = new WidgetRequestEvent(
					WidgetRequestType.POSITIONCOLUMNPANEL);
			e.setTrId(trId);
			e.setColumnLocalId(columnLocalId);
			e.setColumnName(columnName);
			eventBus.fireEvent(e);
		} else {
			Log.error("TRId is null");
			UtilsGXT3
					.alert(msgsCommon.error(), msgs.noTabularResourcePresent());
		}
	}

	private void openColumnLabel() {
		openColumnLabel(null, null);
	}

	private void openColumnLabel(String columnLocalId, String columnName) {
		Log.debug("Request Column Label Tab");
		if (trId != null) {
			WidgetRequestEvent e = new WidgetRequestEvent(
					WidgetRequestType.LABELCOLUMNPANEL);
			e.setTrId(trId);
			e.setColumnLocalId(columnLocalId);
			e.setColumnName(columnName);
			eventBus.fireEvent(e);
		} else {
			Log.error("TRId is null");
			UtilsGXT3
					.alert(msgsCommon.error(), msgs.noTabularResourcePresent());
		}
	}

	private void openTableType() {
		Log.debug("Request Change Table Type Tab");
		if (trId != null) {
			WidgetRequestEvent e = new WidgetRequestEvent(
					WidgetRequestType.CHANGETABLETYPEPANEL);
			e.setTrId(trId);
			eventBus.fireEvent(e);
		} else {
			Log.error("TRId is null");
			UtilsGXT3
					.alert(msgsCommon.error(), msgs.noTabularResourcePresent());
		}
	}

	private void openDuplicatesRowsDetection() {
		Log.debug("Request Duplicates Rows Tab");
		if (trId != null) {
			WidgetRequestEvent e = new WidgetRequestEvent(
					WidgetRequestType.DUPLICATESROWSDETECTIONPANEL);
			e.setTrId(trId);
			eventBus.fireEvent(e);
		} else {
			Log.error("TRId is null");
			UtilsGXT3
					.alert(msgsCommon.error(), msgs.noTabularResourcePresent());
		}
	}

	private void openValidations() {
		Log.debug("Request Validations Tab");
		if (trId != null) {
			WidgetRequestEvent e = new WidgetRequestEvent(
					WidgetRequestType.VALIDATIONSTASKSPANEL);
			e.setTrId(trId);
			eventBus.fireEvent(e);
		} else {
			Log.error("TRId is null");
			UtilsGXT3
					.alert(msgsCommon.error(), msgs.noTabularResourcePresent());
		}
	}

	private void validationsDelete() {
		Log.debug("Call Validation Delete");
		if (trId != null) {
			new ValidationsDelete(trId, eventBus);
		} else {
			Log.error("TRId is null");
			UtilsGXT3
					.alert(msgsCommon.error(), msgs.noTabularResourcePresent());
		}
	}

	private void openDuplicatesRowsDelete() {
		Log.debug("Request Duplicates Rows Delete Tab");
		if (trId != null) {
			WidgetRequestEvent e = new WidgetRequestEvent(
					WidgetRequestType.DUPLICATESROWSDELETEPANEL);
			e.setTrId(trId);
			eventBus.fireEvent(e);
		} else {
			Log.error("TRId is null");
			UtilsGXT3
					.alert(msgsCommon.error(), msgs.noTabularResourcePresent());
		}
	}

	private void openGeospatialCSquare() {
		Log.debug("Request Geospatial Create C-Square Coordiantes Tab");
		if (trId != null) {
			WidgetRequestEvent e = new WidgetRequestEvent(
					WidgetRequestType.GEOSPATIALCREATECOORDINATESPANEL);
			e.setTrId(trId);
			HashMap<RequestPropertiesParameterType, Object> map = new HashMap<RequestPropertiesParameterType, Object>();
			map.put(RequestPropertiesParameterType.Coordinates,
					GeospatialCoordinatesType.C_SQUARE);
			RequestProperties requestProperties = new RequestProperties(map);
			e.setRequestProperties(requestProperties);
			eventBus.fireEvent(e);

		} else {
			Log.error("TRId is null");
			UtilsGXT3
					.alert(msgsCommon.error(), msgs.noTabularResourcePresent());
		}
	}

	private void openGeospatialOceanArea() {
		Log.debug("Request Geospatial Create Ocean Area Coordiantes Tab");
		if (trId != null) {
			WidgetRequestEvent e = new WidgetRequestEvent(
					WidgetRequestType.GEOSPATIALCREATECOORDINATESPANEL);
			e.setTrId(trId);
			HashMap<RequestPropertiesParameterType, Object> map = new HashMap<RequestPropertiesParameterType, Object>();
			map.put(RequestPropertiesParameterType.Coordinates,
					GeospatialCoordinatesType.OCEAN_AREA);
			RequestProperties requestProperties = new RequestProperties(map);
			e.setRequestProperties(requestProperties);
			eventBus.fireEvent(e);

		} else {
			Log.error("TRId is null");
			UtilsGXT3
					.alert(msgsCommon.error(), msgs.noTabularResourcePresent());
		}
	}

	private void openGeometryCreatePoint() {
		Log.debug("Request Geometry Create Point Tab");
		if (trId != null) {
			WidgetRequestEvent e = new WidgetRequestEvent(
					WidgetRequestType.GEOMETRYCREATEPOINTPANEL);
			e.setTrId(trId);
			eventBus.fireEvent(e);

		} else {
			Log.error("TRId is null");
			UtilsGXT3
					.alert(msgsCommon.error(), msgs.noTabularResourcePresent());
		}
	}

	private void openDownscaleCSquare() {
		openDownscaleCSquare(null, null);
	}

	private void openDownscaleCSquare(String columnLocalId, String columnName) {
		Log.debug("Request Downscale C-Square Tab");
		if (trId != null) {
			WidgetRequestEvent e = new WidgetRequestEvent(
					WidgetRequestType.DOWNSCALECSQUAREPANEL);
			e.setTrId(trId);
			e.setColumnLocalId(columnLocalId);
			e.setColumnName(columnName);
			eventBus.fireEvent(e);
		} else {
			Log.error("TRId is null");
			UtilsGXT3
					.alert(msgsCommon.error(), msgs.noTabularResourcePresent());
		}
	}

	private void openBackgroundMonitor() {
		Log.debug("Request Open Monitor Background Tab");
		WidgetRequestEvent e = new WidgetRequestEvent(
				WidgetRequestType.MONITORBACKGROUNDPANEL);
		eventBus.fireEvent(e);
	}

	private void doChangeTableRequestEventCommand(ChangeTableRequestEvent event) {
		Log.debug("Change Table Request: " + event);
		switch (event.getWhy()) {
		case TABLECURATION:
			updateTableForCuration(event.getTrId());
			break;
		case TABLEUPDATED:
			updateTable(event.getTrId());
			break;
		case TABLECLONED:
			addActiveTabularResourceAndOpen(event.getTrId());
			break;
		default:
			break;

		}

	};

	private void addActiveTabularResourceAndOpen(final TRId trId) {
		Log.debug("Add Active TabResource Search: " + trId);
		TDGWTServiceAsync.INSTANCE.getTabResourceInformation(trId,
				new AsyncCallback<TabResource>() {
					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							getEventBus()
									.fireEvent(
											new SessionExpiredEvent(
													SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								UtilsGXT3.alert(msgsCommon.errorLocked(),
										caught.getLocalizedMessage());

							} else {
								Log.error("Error on set TabResource: "
										+ caught.getLocalizedMessage());
								UtilsGXT3.alert(
										msgsCommon.error(),
										"Error on set TabResource: "
												+ caught.getLocalizedMessage());
							}

						}

					}

					public void onSuccess(TabResource result) {
						addActiveTabularResourceAndOpen(result);
					}

				});

	}

	private void addActiveTabularResourceAndOpen(final TabResource tabResource) {
		Log.debug("Add Active TabResource: " + tabResource);
		TDGWTServiceAsync.INSTANCE.setTabResource(tabResource,
				new AsyncCallback<Void>() {
					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							getEventBus()
									.fireEvent(
											new SessionExpiredEvent(
													SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								UtilsGXT3.alert(msgsCommon.errorLocked(),
										caught.getLocalizedMessage());

							} else {
								Log.error("Error on set TabResource: "
										+ caught.getLocalizedMessage());
								UtilsGXT3.alert(msgsCommon.error(), msgs
										.errorOnSetTabularResource(caught
												.getLocalizedMessage()));
							}

						}

					}

					public void onSuccess(Void result) {
						openTable(tabResource.getTrId());
					}

				});

	}

	private void doRowAdd() {
		onRowAdd();
	}

	private void doRowsDelete() {
		// ArrayList<String> rows =
		// tabularData.getGridPanel().getSelectedRowsId();
		ArrayList<String> rows = mainBoxPanel.getSelectedRowsId();

		onRowsDelete(rows);
	}

	/**
	 * Delete Rows
	 * 
	 * @param rows
	 */
	private void onRowsDelete(ArrayList<String> rows) {
		if (rows == null || rows.size() == 0) {
			UtilsGXT3.alert(msgsCommon.error(), msgs.noRowSelected());
		} else {
			DeleteRows deleteRows = new DeleteRows(trId, rows, eventBus);
			deleteRows.delete();
		}
	}

	/**
	 * Replace Row
	 * 
	 * @param cellData
	 */
	private void onReplace(CellData cellData) {
		if (cellData == null) {
			UtilsGXT3.alert(msgsCommon.error(), msgs.noCellSelected());
		} else {
			ReplaceAllDialog replaceDialog = new ReplaceAllDialog(cellData,
					trId, eventBus);
			replaceDialog.show();
		}
	}

	/**
	 * Edit Row
	 * 
	 * @param rowsRaw
	 */
	private void onRowEdit(ArrayList<RowRaw> rowsRaw) {
		if (rowsRaw == null || rowsRaw.isEmpty()) {
			UtilsGXT3.alert(msgsCommon.error(), msgs.noRowSelected());
		} else {
			EditRowDialog editRowDialog = new EditRowDialog(trId, rowsRaw,
					eventBus);
			editRowDialog.show();
		}
	}

	/**
	 * Add Row
	 */
	private void onRowAdd() {
		EditRowDialog editRowDialog = new EditRowDialog(trId, eventBus);
		editRowDialog.show();

	}

	private void doGridContextMenuCommand(GridContextMenuItemEvent event) {
		Log.debug("GridContextMenu Fire Event",
				"OperationId: " + event.getGridOperationId());
		GridOperationId gridOperationId = event.getGridOperationId();
		ArrayList<String> rows = event.getRows();
		ArrayList<RowRaw> rowsRaw = event.getRowsRaw();
		CellData cellData = event.getCellData();

		switch (gridOperationId) {
		case ROWADD:
			onRowAdd();
			break;
		case ROWEDIT:
			onRowEdit(rowsRaw);
			break;
		case ROWDELETE:
			onRowsDelete(rows);
			break;
		case REPLACE:
			onReplace(cellData);
		default:
			break;
		}

	}

	private void doSessionExpiredCommand(SessionExpiredEvent event) {
		Log.debug("Session Expired Event: " + event.getSessionExpiredType());
		sessionExpiredShow();

	}

	private void doDataViewActiveCommand(DataViewActiveEvent event) {
		Log.debug("DataViewActive Event: " + event);
		switch (event.getDataViewActiveType()) {
		case ACTIVEAFTERCLOSE:
			doActiveAfterClose(event);
			break;
		case NEWACTIVE:
			doNewActive(event);
			break;
		case CLOSE:
			doDataViewClose(event);
		default:
			break;

		}
	}

	private void doGridHeaderColumnMenuCommand(
			GridHeaderColumnMenuItemEvent event) {
		Log.debug("GridHeaderColumnMenu Fire Event",
				"OperationId: " + event.getOperationId() + " Column: \n"
						+ event.getColumnSelected());
		String opId = event.getOperationId();
		String columnLocalId = mainBoxPanel.getColumnLocalId(event
				.getColumnSelected());
		String columnName = mainBoxPanel.getColumnName(event
				.getColumnSelected());

		Log.debug("Selected column[ColumnLocalId: " + columnLocalId
				+ ", ColumnName: " + columnName + "]");
		if (opId.compareTo(GridHeaderOperationId.COLUMNPOSITION.toString()) == 0) {
			openPositionColumn(columnLocalId, columnName);
		} else {
			if (opId.compareTo(GridHeaderOperationId.COLUMNLABEL.toString()) == 0) {
				openColumnLabel(columnLocalId, columnName);
			} else {
				if (opId.compareTo(GridHeaderOperationId.COLUMNADD.toString()) == 0) {
					openColumnAdd(columnLocalId, columnName);
				} else {
					if (opId.compareTo(GridHeaderOperationId.COLUMNBATCHREPLACE
							.toString()) == 0) {
						openColumnBatchReplace(columnLocalId, columnName);
					} else {
						if (opId.compareTo(GridHeaderOperationId.COLUMNDELETE
								.toString()) == 0) {
							openColumnDelete(columnLocalId, columnName);
						} else {
							if (opId.compareTo(GridHeaderOperationId.COLUMNTYPE
									.toString()) == 0) {
								openChangeColumnType(columnLocalId, columnName);
							} else {
								if (opId.compareTo(GridHeaderOperationId.COLUMNFILTER
										.toString()) == 0) {
									openColumnFilter(columnLocalId, columnName);
								} else {
									if (opId.compareTo(GridHeaderOperationId.ANNOTATIONADD
											.toString()) == 0) {

									} else {
										if (opId.compareTo(GridHeaderOperationId.DUPLICATEDETECTION
												.toString()) == 0) {

										} else {
											if (opId.compareTo(GridHeaderOperationId.COLUMNSPLIT
													.toString()) == 0) {
												openColumnSplit(columnLocalId,
														columnName);
											} else {
												if (opId.compareTo(GridHeaderOperationId.COLUMNMERGE
														.toString()) == 0) {
													openColumnMerge(
															columnLocalId,
															columnName);
												} else {
													if (opId.compareTo(GridHeaderOperationId.COLUMNREPLACEBYEXPRESSION
															.toString()) == 0) {

														openColumnReplaceByExpression(
																columnLocalId,
																columnName);
													} else {
														if (opId.compareTo(GridHeaderOperationId.COLUMNREPLACEBYEXTERNAL
																.toString()) == 0) {

															openReplaceByExternalColWizard(
																	columnLocalId,
																	columnName);
														} else {
															if (opId.compareTo(GridHeaderOperationId.DOWNSCALECSQUARE
																	.toString()) == 0) {

																openDownscaleCSquare(
																		columnLocalId,
																		columnName);
															} else {

															}
														}

													}
												}
											}
										}

									}

								}

							}

						}
					}

				}
			}
		}

	}

	private void asyncCodeLoadingFailed(Throwable reason) {
		Log.error("Async code loading failed", reason);
		eventBus.fireEvent(new SessionExpiredEvent(
				SessionExpiredType.EXPIREDONSERVER));

	}

}
