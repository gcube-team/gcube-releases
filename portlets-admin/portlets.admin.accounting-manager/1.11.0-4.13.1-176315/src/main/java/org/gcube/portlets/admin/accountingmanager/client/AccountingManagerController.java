package org.gcube.portlets.admin.accountingmanager.client;

import java.util.ArrayList;
import java.util.Date;

import org.gcube.portal.clientcontext.client.GCubeClientContext;
import org.gcube.portlets.admin.accountingmanager.client.event.AccountingMenuEvent;
import org.gcube.portlets.admin.accountingmanager.client.event.EnableTabsEvent;
import org.gcube.portlets.admin.accountingmanager.client.event.ExportRequestEvent;
import org.gcube.portlets.admin.accountingmanager.client.event.FiltersChangeEvent;
import org.gcube.portlets.admin.accountingmanager.client.event.SessionExpiredEvent;
import org.gcube.portlets.admin.accountingmanager.client.event.StateChangeEvent;
import org.gcube.portlets.admin.accountingmanager.client.event.UIStateEvent;
import org.gcube.portlets.admin.accountingmanager.client.monitor.AccountingMonitor;
import org.gcube.portlets.admin.accountingmanager.client.monitor.MonitorRequest;
import org.gcube.portlets.admin.accountingmanager.client.monitor.MonitorRequestEvent;
import org.gcube.portlets.admin.accountingmanager.client.monitor.MonitorRequestEvent.MonitorRequestEventHandler;
import org.gcube.portlets.admin.accountingmanager.client.rpc.AccountingManagerServiceAsync;
import org.gcube.portlets.admin.accountingmanager.client.state.AccountingClientState;
import org.gcube.portlets.admin.accountingmanager.client.state.AccountingClientStateData;
import org.gcube.portlets.admin.accountingmanager.client.type.SessionExpiredType;
import org.gcube.portlets.admin.accountingmanager.client.type.StateChangeType;
import org.gcube.portlets.admin.accountingmanager.client.type.UIStateType;
import org.gcube.portlets.admin.accountingmanager.client.util.UtilsGXT3;
import org.gcube.portlets.admin.accountingmanager.shared.Constants;
import org.gcube.portlets.admin.accountingmanager.shared.data.AccountingFilterBasic;
import org.gcube.portlets.admin.accountingmanager.shared.data.AccountingFilterSpaces;
import org.gcube.portlets.admin.accountingmanager.shared.data.AccountingPeriod;
import org.gcube.portlets.admin.accountingmanager.shared.data.AccountingPeriodMode;
import org.gcube.portlets.admin.accountingmanager.shared.data.AccountingType;
import org.gcube.portlets.admin.accountingmanager.shared.data.Context;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterKey;
import org.gcube.portlets.admin.accountingmanager.shared.data.Spaces;
import org.gcube.portlets.admin.accountingmanager.shared.data.query.SeriesRequest;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesResponse;
import org.gcube.portlets.admin.accountingmanager.shared.exception.SessionExpiredException;
import org.gcube.portlets.admin.accountingmanager.shared.session.UserInfo;
import org.gcube.portlets.admin.accountingmanager.shared.tabs.EnableTabs;
import org.gcube.portlets.admin.accountingmanager.shared.workspace.ItemDescription;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class AccountingManagerController {

	private SimpleEventBus eventBus;
	private UserInfo userInfo;
	private Boolean rootScope;
	private AccountingClientState accountingState;
	private AccountingType accountingType;
	@SuppressWarnings("unused")
	private BorderLayoutContainer mainPanel;
	private AccountingMonitor accountingMonitor;
	private EnableTabs enableTabs;

	public AccountingManagerController() {
		eventBus = new SimpleEventBus();
		accountingState = new AccountingClientState();
		init();
	}

	private void init() {
		callHello();
		bindToEvents();
	}

	/*
	 * private void checkSession() { // if you do not need to something when the
	 * session expire // CheckSession.getInstance().startPolling(); }
	 */

	private void sessionExpiredShow() {
		Log.error("Session expired");
		// CheckSession.showLogoutDialog();
	}

	/**
	 * @return the eventBus
	 */
	public EventBus getEventBus() {
		return eventBus;
	}

	public void setMainPanelLayout(BorderLayoutContainer mainPanel) {
		this.mainPanel = mainPanel;
	}

	private void callHello() {
		AccountingManagerServiceAsync.INSTANCE.hello(new AsyncCallback<UserInfo>() {

			@Override
			public void onFailure(Throwable caught) {
				Log.info("No valid user found: " + caught.getMessage());
				if (caught instanceof SessionExpiredException) {
					UtilsGXT3.alert("Error", "Expired Session");
					sessionExpiredShowDelayed();
				} else {
					UtilsGXT3.alert("Error", "No user found");
				}
			}

			@Override
			public void onSuccess(UserInfo result) {
				userInfo = result;
				Log.info("Hello: " + userInfo.getUsername());
				isRootScope();
			}

		});

	}

	private void isRootScope() {
		AccountingManagerServiceAsync.INSTANCE.isRootScope(new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				Log.info("No valid user found: " + caught.getMessage());
				if (caught instanceof SessionExpiredException) {
					UtilsGXT3.alert("Error", "Expired Session");
					sessionExpiredShowDelayed();
				} else {
					UtilsGXT3.alert("Error", "Error checking the scope!");
				}
			}

			@Override
			public void onSuccess(Boolean result) {
				rootScope = result;
				Log.info("RootScope: " + result);
				retrieveEnableTabs();
			}

		});

	}

	private void retrieveEnableTabs() {
		AccountingManagerServiceAsync.INSTANCE.getEnableTabs(new AsyncCallback<EnableTabs>() {

			@Override
			public void onFailure(Throwable caught) {
				Log.error("Error retrieving enable tabs: " + caught.getLocalizedMessage());
				if (caught instanceof SessionExpiredException) {
					UtilsGXT3.alert("Error", "Expired Session");
					sessionExpiredShowDelayed();
				} else {
					UtilsGXT3.alert("Error", caught.getLocalizedMessage());
				}
			}

			@Override
			public void onSuccess(EnableTabs enableTabs) {
				Log.info("Enable Tabs: " + enableTabs);
				doEnableTabs(enableTabs);
			}

		});

	}

	private void doEnableTabs(EnableTabs enableTabs) {
		if (enableTabs == null || enableTabs.getTabs() == null || enableTabs.getTabs().isEmpty()) {
			UtilsGXT3.alert("Attention", "You don't have permissions to see Accounting Information in this scope!");
		} else {
			this.enableTabs = enableTabs;
			EnableTabsEvent event = new EnableTabsEvent(enableTabs);
			Log.debug("Fire: " + event);
			eventBus.fireEvent(event);

		}
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

	private void checkLocale() {
		String[] locales = LocaleInfo.getAvailableLocaleNames();

		for (String locale : locales) {
			Log.debug("Locale avaible:" + locale);
		}

		String currentLocaleCookie = Cookies.getCookie(LocaleInfo.getLocaleCookieName());
		Log.debug(Constants.AM_LANG_COOKIE + ":" + currentLocaleCookie);

		LocaleInfo currentLocaleInfo = LocaleInfo.getCurrentLocale();
		Log.debug("Current Locale:" + currentLocaleInfo.getLocaleName());

	}

	protected void changeLanguage(String localeName) {
		Date now = new Date();
		long nowLong = now.getTime();
		nowLong = nowLong + (1000 * 60 * 60 * 24 * 21);
		now.setTime(nowLong);
		String cookieLang = Cookies.getCookie(Constants.AM_LANG_COOKIE);
		if (cookieLang != null) {
			Cookies.removeCookie(Constants.AM_LANG_COOKIE);
		}
		Cookies.setCookie(Constants.AM_LANG_COOKIE, localeName, now);
		com.google.gwt.user.client.Window.Location.reload();
	}

	//
	public void restoreUISession() {
		checkLocale();
		showDefault();
	}

	// Bind Controller to events on bus
	private void bindToEvents() {
		eventBus.addHandler(SessionExpiredEvent.TYPE, new SessionExpiredEvent.SessionExpiredEventHandler() {

			@Override
			public void onSessionExpired(SessionExpiredEvent event) {
				Log.debug("Catch Event SessionExpiredEvent");
				doSessionExpiredCommand(event);

			}
		});

		eventBus.addHandler(AccountingMenuEvent.TYPE, new AccountingMenuEvent.AccountingMenuEventHandler() {

			public void onMenu(AccountingMenuEvent event) {
				Log.debug("Catch Event AccountingMenuEvent");
				doMenuCommand(event);

			}
		});

		eventBus.addHandler(FiltersChangeEvent.TYPE, new FiltersChangeEvent.FiltersChangeEventHandler() {

			public void onFiltersChange(FiltersChangeEvent event) {
				Log.debug("Catch Event FiltersChangeEvent");
				doFiltersChangeCommand(event);

			}
		});

		eventBus.addHandler(ExportRequestEvent.TYPE, new ExportRequestEvent.ExportRequestEventHandler() {

			@Override
			public void onExport(ExportRequestEvent event) {
				Log.debug("Catch ExportRequestEvent");
				doExportRequest(event);

			}
		});

		eventBus.fireEvent(new UIStateEvent(UIStateType.START));

	}

	private void doMenuCommand(AccountingMenuEvent event) {
		AccountingClientStateData accountingStateData = null;
		if (event == null || event.getAccountingType() == null) {
			return;
		}
		switch (event.getAccountingType()) {
		case PORTLET:
		case SERVICE:
		case STORAGE:
		case TASK:
		case JOB:
		case SPACE:
			Log.debug("AccountingType: " + event.getAccountingType());
			accountingStateData = accountingState.getState(event.getAccountingType());
			if (accountingStateData == null) {
				createDefaultChart(event.getAccountingType());
			} else {
				accountingType = event.getAccountingType();
				StateChangeEvent stateChangeEvent = new StateChangeEvent(StateChangeType.Restore, accountingStateData);
				eventBus.fireEvent(stateChangeEvent);
			}
			break;
		default:
			break;
		}

	}

	public void showDefault() {
		if (enableTabs != null && enableTabs.getTabs() != null && enableTabs.getTabs().size() > 0
				&& enableTabs.getTabs().get(0) != null) {
			Log.debug("Create DefaultChart");
			createDefaultChart(enableTabs.getTabs().get(0).getAccountingType());
		}
	}

	private void createDefaultChart(AccountingType accountingType) {
		accountingMonitor = new AccountingMonitor();
		this.accountingType = accountingType;

		AccountingClientStateData accountingStateData = new AccountingClientStateData(accountingType, rootScope);
		accountingState.setState(accountingType, accountingStateData);

		retrieveFilterKey();

	}

	private void retrieveFilterKey() {

		AccountingManagerServiceAsync.INSTANCE.getFilterKeys(accountingType, new AsyncCallback<ArrayList<FilterKey>>() {

			@Override
			public void onFailure(Throwable caught) {
				accountingMonitor.hide();
				if (caught instanceof SessionExpiredException) {
					eventBus.fireEvent(new SessionExpiredEvent(SessionExpiredType.EXPIREDONSERVER));
				} else {
					Log.error(
							"Error retrieving filter keys for " + accountingType + ":" + caught.getLocalizedMessage());
					UtilsGXT3.alert("Error retrieving filter keys", caught.getLocalizedMessage());
				}

			}

			@Override
			public void onSuccess(ArrayList<FilterKey> result) {
				Log.debug("FilterKeys: " + result);
				AccountingClientStateData accountingStateData = accountingState.getState(accountingType);
				accountingStateData.setAvailableFilterKeys(result);
				accountingState.setState(accountingType, accountingStateData);
				retrieveContext();
			}
		});

	}

	private void retrieveContext() {

		AccountingManagerServiceAsync.INSTANCE.getContext(new AsyncCallback<Context>() {

			@Override
			public void onFailure(Throwable caught) {
				accountingMonitor.hide();
				if (caught instanceof SessionExpiredException) {
					eventBus.fireEvent(new SessionExpiredEvent(SessionExpiredType.EXPIREDONSERVER));
				} else {
					Log.error("Error retrieving contexts for " + accountingType + ":" + caught.getLocalizedMessage());
					UtilsGXT3.alert("Error retrieving contexts ", caught.getLocalizedMessage());
				}

			}

			@Override
			public void onSuccess(Context result) {
				Log.debug("Available Context: " + result);
				AccountingClientStateData accountingStateData = accountingState.getState(accountingType);
				accountingStateData.setAvailableContext(result);
				accountingState.setState(accountingType, accountingStateData);
				retrieveSpace();
			}
		});

	}

	private void retrieveSpace() {
		if (rootScope) {

			AccountingManagerServiceAsync.INSTANCE.getSpaces(new AsyncCallback<Spaces>() {

				@Override
				public void onFailure(Throwable caught) {
					accountingMonitor.hide();
					if (caught instanceof SessionExpiredException) {
						eventBus.fireEvent(new SessionExpiredEvent(SessionExpiredType.EXPIREDONSERVER));
					} else {
						Log.error("Error retrieving sapces for " + accountingType + ":" + caught.getLocalizedMessage());
						UtilsGXT3.alert("Error retrieving spaces ", caught.getLocalizedMessage());
					}

				}

				@Override
				public void onSuccess(Spaces result) {
					Log.debug("Available Spaces: " + result);
					createDefaultSeriesRequest(result);
				}
			});
		} else {
			createDefaultSeriesRequest(null);
		}
	}

	private void createDefaultSeriesRequest(Spaces spaces) {
		Date now = new Date();
		DateTimeFormat dtf = DateTimeFormat.getFormat(PredefinedFormat.YEAR_MONTH_DAY);
		String currentDate = dtf.format(now);
		Date date = dtf.parse(currentDate);
		Date lastMonth = new Date(date.getTime());
		CalendarUtil.addMonthsToDate(lastMonth, -1);
		Log.debug("LastMoth= " + dtf.format(lastMonth) + " , date=" + dtf.format(date));

		AccountingClientStateData accountingStateData = accountingState.getState(accountingType);
		SeriesRequest seriesRequest = null;

		switch (accountingType) {
		case JOB:
		case PORTLET:
		case SERVICE:
		case STORAGE:
		case TASK:
			seriesRequest = new SeriesRequest(
					new AccountingPeriod(dtf.format(lastMonth), dtf.format(date), AccountingPeriodMode.DAILY),
					new AccountingFilterBasic());
			Log.debug("DefaultSeriesRequest: " + seriesRequest);

			break;
		case SPACE:
			accountingStateData.setAvailableSpaces(spaces);
			seriesRequest = new SeriesRequest(
					new AccountingPeriod(dtf.format(lastMonth), dtf.format(date), AccountingPeriodMode.DAILY),
					new AccountingFilterSpaces(spaces));
			Log.debug("DefaultSeriesRequest: " + seriesRequest);
			break;
		default:
			break;

		}

		accountingStateData.setSeriesRequest(seriesRequest);
		accountingState.setState(accountingType, accountingStateData);
		callDefaultSeriesRequest();
	}

	private void callDefaultSeriesRequest() {

		AccountingManagerServiceAsync.INSTANCE.getSeriesInCache(accountingType,
				accountingState.getState(accountingType).getSeriesRequest(), new AsyncCallback<SeriesResponse>() {

					@Override
					public void onSuccess(SeriesResponse seriesResponse) {
						Log.debug("SeriesResponse: " + seriesResponse);
						if (seriesResponse == null) {
							callDefaultSeriesRequestNoCache();
						} else {
							AccountingClientStateData accountingStateData = accountingState.getState(accountingType);
							accountingStateData.setSeriesResponse(seriesResponse);
							accountingState.setState(accountingType, accountingStateData);
							StateChangeEvent stateChangeEvent = new StateChangeEvent(StateChangeType.Restore,
									accountingStateData);
							eventBus.fireEvent(stateChangeEvent);
							accountingMonitor.hide();
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						accountingMonitor.hide();
						if (caught instanceof SessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(SessionExpiredType.EXPIREDONSERVER));
						} else {
							Log.error("Error:" + caught.getLocalizedMessage());
							caught.printStackTrace();
							UtilsGXT3.alert("Error", caught.getLocalizedMessage());
							AccountingClientStateData accountingStateData = accountingState.getState(accountingType);
							accountingStateData.setSeriesResponse(null);
							accountingState.setState(accountingType, accountingStateData);
							StateChangeEvent stateChangeEvent = new StateChangeEvent(StateChangeType.Restore,
									accountingState.getState(accountingType));
							eventBus.fireEvent(stateChangeEvent);

						}

					}
				});

	}

	private void callDefaultSeriesRequestNoCache() {

		AccountingManagerServiceAsync.INSTANCE.getSeries(accountingType,
				accountingState.getState(accountingType).getSeriesRequest(), new AsyncCallback<String>() {

					@Override
					public void onSuccess(String operationId) {
						Log.debug("Service OperationId: " + operationId);
						if (operationId == null || operationId.isEmpty()) {
							accountingMonitor.hide();
							Log.error("Invalid Operation Id: " + operationId);
							UtilsGXT3.alert("Error", "Invalid Operation Id: " + operationId);
							AccountingClientStateData accountingStateData = accountingState.getState(accountingType);
							accountingStateData.setSeriesResponse(null);
							accountingState.setState(accountingType, accountingStateData);
							StateChangeEvent stateChangeEvent = new StateChangeEvent(StateChangeType.Restore,
									accountingState.getState(accountingType));
							eventBus.fireEvent(stateChangeEvent);
						} else {
							defaultOperationMonitor(operationId);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						accountingMonitor.hide();
						if (caught instanceof SessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(SessionExpiredType.EXPIREDONSERVER));
						} else {
							Log.error("Error:" + caught.getLocalizedMessage());
							caught.printStackTrace();
							UtilsGXT3.alert("Error", caught.getLocalizedMessage());
							AccountingClientStateData accountingStateData = accountingState.getState(accountingType);
							accountingStateData.setSeriesResponse(null);
							accountingState.setState(accountingType, accountingStateData);
							StateChangeEvent stateChangeEvent = new StateChangeEvent(StateChangeType.Restore,
									accountingState.getState(accountingType));
							eventBus.fireEvent(stateChangeEvent);

						}

					}
				});

	}

	private void defaultOperationMonitor(final String operationId) {
		final MonitorRequest monitorRequest = new MonitorRequest();
		MonitorRequestEventHandler handler = new MonitorRequestEventHandler() {

			@Override
			public void onMonitor(MonitorRequestEvent event) {
				switch (event.getMonitorRequestType()) {
				case Period:
					AccountingManagerServiceAsync.INSTANCE.operationMonitor(operationId,
							new AsyncCallback<SeriesResponse>() {

								@Override
								public void onSuccess(SeriesResponse seriesResponse) {
									Log.debug("SeriesResponse: " + seriesResponse);
									if (seriesResponse != null) {
										monitorRequest.stop();
										AccountingClientStateData accountingStateData = accountingState
												.getState(accountingType);
										accountingStateData.setSeriesResponse(seriesResponse);
										accountingState.setState(accountingType, accountingStateData);
										StateChangeEvent stateChangeEvent = new StateChangeEvent(
												StateChangeType.Restore, accountingStateData);
										eventBus.fireEvent(stateChangeEvent);
										accountingMonitor.hide();
									} else {
										monitorRequest.repeat();
									}
								}

								@Override
								public void onFailure(Throwable caught) {
									monitorRequest.stop();
									accountingMonitor.hide();
									if (caught instanceof SessionExpiredException) {
										eventBus.fireEvent(new SessionExpiredEvent(SessionExpiredType.EXPIREDONSERVER));
									} else {
										Log.error("Error:" + caught.getLocalizedMessage());
										caught.printStackTrace();
										UtilsGXT3.alert("Error", caught.getLocalizedMessage());
										AccountingClientStateData accountingStateData = accountingState
												.getState(accountingType);
										accountingStateData.setSeriesResponse(null);
										accountingState.setState(accountingType, accountingStateData);
										StateChangeEvent stateChangeEvent = new StateChangeEvent(
												StateChangeType.Restore, accountingState.getState(accountingType));
										eventBus.fireEvent(stateChangeEvent);

									}

								}
							});

					break;
				case TimeOut:
				default:
					monitorRequest.stop();
					accountingMonitor.hide();
					Log.error("Error: TimeOut!");
					UtilsGXT3.alert("Error Time Out", "Time Out");
					AccountingClientStateData accountingStateData = accountingState.getState(accountingType);
					accountingStateData.setSeriesResponse(null);
					accountingState.setState(accountingType, accountingStateData);
					StateChangeEvent stateChangeEvent = new StateChangeEvent(StateChangeType.Restore,
							accountingState.getState(accountingType));
					eventBus.fireEvent(stateChangeEvent);
					break;

				}

			}
		};

		monitorRequest.addHandler(handler);
		monitorRequest.start();

	}

	private void doFiltersChangeCommand(FiltersChangeEvent event) {
		if (event == null || event.getFiltersChangeType() == null) {
			return;
		}
		switch (event.getFiltersChangeType()) {
		case Update:
			SeriesRequest seriesRequest = event.getSeriesRequest();
			AccountingClientStateData accountingStateData = accountingState.getState(accountingType);
			if (accountingStateData != null) {
				accountingMonitor = new AccountingMonitor();
				accountingStateData.setSeriesRequest(seriesRequest);
				accountingState.setState(accountingType, accountingStateData);
				callSeriesRequest();

			}

			break;
		default:
			break;
		}

	}

	private void callSeriesRequest() {
		Log.debug("Call getSeries on server, params: " + accountingType + ", "
				+ accountingState.getState(accountingType).getSeriesRequest());

		AccountingManagerServiceAsync.INSTANCE.getSeriesInCache(accountingType,
				accountingState.getState(accountingType).getSeriesRequest(), new AsyncCallback<SeriesResponse>() {

					@Override
					public void onSuccess(SeriesResponse seriesResponse) {
						Log.debug("SeriesResponse: " + seriesResponse);
						if (seriesResponse == null) {
							// TODO
							callSeriesRequestNoCache();
						} else {
							AccountingClientStateData accountingStateData = accountingState.getState(accountingType);
							accountingStateData.setSeriesResponse(seriesResponse);
							accountingState.setState(accountingType, accountingStateData);
							StateChangeEvent stateChangeEvent = new StateChangeEvent(StateChangeType.Update,
									accountingStateData);
							eventBus.fireEvent(stateChangeEvent);
							accountingMonitor.hide();
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						accountingMonitor.hide();
						if (caught instanceof SessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(SessionExpiredType.EXPIREDONSERVER));
						} else {
							Log.error("Error:" + caught.getLocalizedMessage());
							UtilsGXT3.alert("Error", caught.getLocalizedMessage());
							caught.printStackTrace();
							AccountingClientStateData accountingStateData = accountingState.getState(accountingType);
							accountingStateData.setSeriesResponse(null);
							accountingState.setState(accountingType, accountingStateData);
							StateChangeEvent stateChangeEvent = new StateChangeEvent(StateChangeType.Update,
									accountingState.getState(accountingType));
							eventBus.fireEvent(stateChangeEvent);
						}

					}
				});

	}

	private void callSeriesRequestNoCache() {

		AccountingManagerServiceAsync.INSTANCE.getSeries(accountingType,
				accountingState.getState(accountingType).getSeriesRequest(), new AsyncCallback<String>() {

					@Override
					public void onSuccess(String operationId) {
						Log.debug("Service OperationId: " + operationId);
						if (operationId == null || operationId.isEmpty()) {
							accountingMonitor.hide();
							Log.error("Invalid Operation Id: " + operationId);
							UtilsGXT3.alert("Error", "Invalid Operation Id: " + operationId);
							AccountingClientStateData accountingStateData = accountingState.getState(accountingType);
							accountingStateData.setSeriesResponse(null);
							accountingState.setState(accountingType, accountingStateData);
							StateChangeEvent stateChangeEvent = new StateChangeEvent(StateChangeType.Update,
									accountingState.getState(accountingType));
							eventBus.fireEvent(stateChangeEvent);
						} else {
							operationMonitor(operationId);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						accountingMonitor.hide();
						if (caught instanceof SessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(SessionExpiredType.EXPIREDONSERVER));
						} else {
							Log.error("Error:" + caught.getLocalizedMessage());
							UtilsGXT3.alert("Error", caught.getLocalizedMessage());
							caught.printStackTrace();
							AccountingClientStateData accountingStateData = accountingState.getState(accountingType);
							accountingStateData.setSeriesResponse(null);
							accountingState.setState(accountingType, accountingStateData);
							StateChangeEvent stateChangeEvent = new StateChangeEvent(StateChangeType.Update,
									accountingState.getState(accountingType));
							eventBus.fireEvent(stateChangeEvent);
						}

					}
				});

	}

	private void operationMonitor(final String operationId) {
		final MonitorRequest monitorRequest = new MonitorRequest();
		MonitorRequestEventHandler handler = new MonitorRequestEventHandler() {

			@Override
			public void onMonitor(MonitorRequestEvent event) {
				switch (event.getMonitorRequestType()) {
				case Period:
					AccountingManagerServiceAsync.INSTANCE.operationMonitor(operationId,
							new AsyncCallback<SeriesResponse>() {

								@Override
								public void onSuccess(SeriesResponse seriesResponse) {
									Log.debug("SeriesResponse: " + seriesResponse);
									if (seriesResponse != null) {
										monitorRequest.stop();
										AccountingClientStateData accountingStateData = accountingState
												.getState(accountingType);
										accountingStateData.setSeriesResponse(seriesResponse);
										accountingState.setState(accountingType, accountingStateData);
										StateChangeEvent stateChangeEvent = new StateChangeEvent(StateChangeType.Update,
												accountingStateData);
										eventBus.fireEvent(stateChangeEvent);
										accountingMonitor.hide();
									} else {
										monitorRequest.repeat();
									}
								}

								@Override
								public void onFailure(Throwable caught) {
									monitorRequest.stop();
									accountingMonitor.hide();
									if (caught instanceof SessionExpiredException) {
										eventBus.fireEvent(new SessionExpiredEvent(SessionExpiredType.EXPIREDONSERVER));
									} else {
										Log.error("Error:" + caught.getLocalizedMessage());
										UtilsGXT3.alert("Error", caught.getLocalizedMessage());
										caught.printStackTrace();
										AccountingClientStateData accountingStateData = accountingState
												.getState(accountingType);
										accountingStateData.setSeriesResponse(null);
										accountingState.setState(accountingType, accountingStateData);
										StateChangeEvent stateChangeEvent = new StateChangeEvent(StateChangeType.Update,
												accountingState.getState(accountingType));
										eventBus.fireEvent(stateChangeEvent);
									}

								}
							});

					break;
				case TimeOut:
				default:
					monitorRequest.stop();
					accountingMonitor.hide();
					Log.error("Error: TimeOut!");
					UtilsGXT3.alert("Error Time Out", "Time Out");
					AccountingClientStateData accountingStateData = accountingState.getState(accountingType);
					accountingStateData.setSeriesResponse(null);
					accountingState.setState(accountingType, accountingStateData);
					StateChangeEvent stateChangeEvent = new StateChangeEvent(StateChangeType.Update,
							accountingState.getState(accountingType));
					eventBus.fireEvent(stateChangeEvent);
					break;

				}

			}
		};

		monitorRequest.addHandler(handler);
		monitorRequest.start();

	}

	// TODO save on workspace
	@SuppressWarnings("unused")
	private void doSaveDataOnWorkspace(ExportRequestEvent event) {
		accountingMonitor = new AccountingMonitor();
		Log.debug("Call saveCSVOnWorkspace on server, params: " + event.getAccountingType());

		AccountingManagerServiceAsync.INSTANCE.saveCSVOnWorkspace(event.getAccountingType(),
				new AsyncCallback<ItemDescription>() {

					@Override
					public void onFailure(Throwable caught) {
						accountingMonitor.hide();
						if (caught instanceof SessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(SessionExpiredType.EXPIREDONSERVER));
						} else {
							Log.error("Error:" + caught.getLocalizedMessage());
							UtilsGXT3.alert("Error", caught.getLocalizedMessage());
							caught.printStackTrace();
						}

					}

					@Override
					public void onSuccess(ItemDescription result) {
						Log.debug("ItemDescription: " + result);
						// doDownloadCSVFromWorkspace(result);
					}
				});
	}



	private void doExportRequest(ExportRequestEvent event) {
		StringBuilder actionUrl = new StringBuilder();
		actionUrl.append(GWT.getModuleBaseURL());
		actionUrl.append(Constants.EXPORT_SERVLET + "?" + Constants.EXPORT_SERVLET_TYPE_PARAMETER + "="
				+ event.getExportType().name() + "&" + Constants.EXPORT_SERVLET_ACCOUNTING_TYPE_PARAMETER + "="
				+ event.getAccountingType().name() + "&" + Constants.CURR_GROUP_ID + "="
				+ GCubeClientContext.getCurrentContextId());

		// + "&"
		// + Constants.CURR_USER_ID + "="
		// + GCubeClientContext.getCurrentUserId());

		Log.debug("Retrieved link: " + actionUrl);
		Window.open(actionUrl.toString(), "_blank", "");

	}

	private void doSessionExpiredCommand(SessionExpiredEvent event) {
		Log.debug("Session Expired Event: " + event.getSessionExpiredType());
		sessionExpiredShow();

	}

	@SuppressWarnings("unused")
	private void asyncCodeLoadingFailed(Throwable reason) {
		Log.error("Async code loading failed", reason);
		eventBus.fireEvent(new SessionExpiredEvent(SessionExpiredType.EXPIREDONSERVER));

	}

}
