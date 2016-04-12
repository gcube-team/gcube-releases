package org.gcube.portlets.user.statisticalalgorithmsimporter.client;

import java.util.Date;

import org.gcube.portlets.user.statisticalalgorithmsimporter.client.event.DeleteItemEvent;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.event.InputReadyEvent;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.event.InputRequestEvent;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.event.MainCodeSetEvent;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.event.NewMainCodeEvent;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.event.StatAlgoImporterRibbonEvent;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.monitor.StatAlgoImporterMonitor;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.project.ProjectManager;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.resource.StatAlgoImporterResources;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.rpc.StatAlgoImporterServiceAsync;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.type.SessionExpiredType;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.type.StatAlgoImporterRibbonType;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.upload.CodeUploadDialog;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.utils.UtilsGXT3;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.Constants;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.exception.StatAlgoImporterSessionExpiredException;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.session.UserInfo;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.workspace.ItemDescription;
import org.gcube.portlets.widgets.sessionchecker.client.CheckSession;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.resources.client.ResourceCallback;
import com.google.gwt.resources.client.ResourceException;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent.DialogHideHandler;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class StatAlgoImporterController {

	private SimpleEventBus eventBus;
	private UserInfo userInfo;
	@SuppressWarnings("unused")
	private BorderLayoutContainer mainPanel;
	private ProjectManager pm;
	private StatAlgoImporterMonitor monitor;

	private enum InputRequestType {
		Save, SoftwareCreate;
	}

	private InputRequestType inputRequestType;

	public StatAlgoImporterController() {
		eventBus = new SimpleEventBus();
		pm = new ProjectManager(eventBus);
		init();
	}

	private void init() {
		callHello();
		checkSession();
		bindToEvents();
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

	public void setMainPanelLayout(BorderLayoutContainer mainPanel) {
		this.mainPanel = mainPanel;
	}

	private void callHello() {
		StatAlgoImporterServiceAsync.INSTANCE
				.hello(new AsyncCallback<UserInfo>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.info("No valid user found: " + caught.getMessage());
						if (caught instanceof StatAlgoImporterSessionExpiredException) {
							UtilsGXT3.alert("Error", "Expired Session");
							sessionExpiredShowDelayed();
						} else {
							UtilsGXT3.alert(
									"Error",
									"No user found: "
											+ caught.getLocalizedMessage());
						}
					}

					@Override
					public void onSuccess(UserInfo result) {
						userInfo = result;
						Log.info("Hello: " + userInfo.getUsername());

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

	protected void checkLocale() {
		String[] locales = LocaleInfo.getAvailableLocaleNames();

		for (String locale : locales) {
			Log.debug("Locale avaible:" + locale);
		}

		String currentLocaleCookie = Cookies.getCookie(LocaleInfo
				.getLocaleCookieName());
		Log.debug(Constants.STATISTICAL_ALGORITHMS_IMPORTER_COOKIE + ":"
				+ currentLocaleCookie);

		LocaleInfo currentLocaleInfo = LocaleInfo.getCurrentLocale();
		Log.debug("Current Locale:" + currentLocaleInfo.getLocaleName());

	}

	protected void changeLanguage(String localeName) {
		Date now = new Date();
		long nowLong = now.getTime();
		nowLong = nowLong + (1000 * 60 * 60 * 24 * 21);
		now.setTime(nowLong);
		String cookieLang = Cookies
				.getCookie(Constants.STATISTICAL_ALGORITHMS_IMPORTER_COOKIE);
		if (cookieLang != null) {
			Cookies.removeCookie(Constants.STATISTICAL_ALGORITHMS_IMPORTER_COOKIE);
		}
		Cookies.setCookie(Constants.STATISTICAL_ALGORITHMS_IMPORTER_COOKIE,
				localeName, now);
		com.google.gwt.user.client.Window.Location.reload();
	}

	//
	public void restoreUISession() {
		checkLocale();

		String value = com.google.gwt.user.client.Window.Location
				.getParameter(Constants.STATISTICAL_ALGORITHMS_IMPORTER_ID);

		pm.startProjectManager(value);

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

		eventBus.addHandler(StatAlgoImporterRibbonEvent.TYPE,
				new StatAlgoImporterRibbonEvent.StatRunnerRibbonEventHandler() {

					@Override
					public void onSelect(StatAlgoImporterRibbonEvent event) {
						Log.debug("Catch StatAlgoImportRibbonEvent");
						doMenuCommand(event);

					}
				});

		eventBus.addHandler(MainCodeSetEvent.TYPE,
				new MainCodeSetEvent.MainCodeSetEventHandler() {

					@Override
					public void onMainCodeSet(MainCodeSetEvent event) {
						Log.debug("Catch MainCodeSetEvent");
						doMainCodeSetCommand(event);

					}

				});

		eventBus.addHandler(DeleteItemEvent.TYPE,
				new DeleteItemEvent.DeleteItemEventHandler() {

					@Override
					public void onDelete(DeleteItemEvent event) {
						Log.debug("Catch DeleteItemEvent");
						doDeleteItemCommand(event);

					}

				});

		eventBus.addHandler(InputReadyEvent.TYPE,
				new InputReadyEvent.InputReadyEventHandler() {

					@Override
					public void onInputReady(InputReadyEvent event) {
						Log.debug("Catch InputReadyEvent");
						doInputReadyCommand(event);

					}
				});

		eventBus.addHandler(NewMainCodeEvent.TYPE,
				new NewMainCodeEvent.NewMainCodeEventHandler() {

					@Override
					public void onSet(NewMainCodeEvent event) {
						Log.debug("Catch SaveNewMainCodeEvent");
						doSetNewMainCodeEvent(event);

					}

				});

		pm.startProjectManager();

	}

	private void doMenuCommand(StatAlgoImporterRibbonEvent event) {
		StatAlgoImporterRibbonType eventType = event.getStatRunnerRibbonType();
		if (eventType == null) {
			return;
		}
		Log.debug("StatAlgoRibbonEvent: " + event);

		switch (eventType) {
		case PROJECT_CREATE:
			showCreateProjectDialog();
			break;
		case PROJECT_OPEN:
			showOpenProjectDialog();
			break;
		case PROJECT_SAVE:
			projectSaveRequest();
			break;
		case RESOURCE_ADD:
			showAddResourceDialog();
			break;
		case SOFTWARE_CREATE:
			softwareCreateRequest();
			break;
		case SOFTWARE_PUBLISH:
			softwarePublish();
			break;
		case SOFTWARE_REPACKAGE:
			softwareRepackage();
			break;
		case HELP:
			showHelp();
			break;
		default:
			break;

		}
	}

	private void showHelp() {

		try {
			StatAlgoImporterResources.INSTANCE.wikiLink().getText(
					new ResourceCallback<TextResource>() {
						public void onError(ResourceException e) {
							Log.error("Error retrieving wiki link!: "
									+ e.getLocalizedMessage());
							UtilsGXT3.alert("Error",
									"Error retrieving wiki link!");
						}

						public void onSuccess(TextResource r) {
							String s = r.getText();
							Window.open(s,
									"Statistical Algorithms Importer Wiki", "");
						}
					});
		} catch (ResourceException e) {
			Log.error("Error retrieving wiki link!: " + e.getLocalizedMessage());
			UtilsGXT3.alert("Error", "Error retrieving wiki link!");
			e.printStackTrace();

		}

	}

	private void doInputReadyCommand(InputReadyEvent event) {
		switch (inputRequestType) {
		case Save:
			pm.saveProject(event.getInputData(), monitor);
			break;
		case SoftwareCreate:
			pm.softwareCreate(event.getInputData(), monitor);
			break;
		default:
			break;

		}

	}

	private void projectSaveRequest() {
		monitor = new StatAlgoImporterMonitor();
		inputRequestType = InputRequestType.Save;
		InputRequestEvent inputRequestEvent = new InputRequestEvent();
		eventBus.fireEvent(inputRequestEvent);
		Log.debug("ProjectSaveRequest: " + inputRequestEvent);
	}

	private void softwareCreateRequest() {

		final ConfirmMessageBox mb = new ConfirmMessageBox(
				"Warning",
				"The creation of new software will overwrite the possible"
						+ " previous version and will require publication again."
						+ " Do you want to proceed anyway?");
		mb.addDialogHideHandler(new DialogHideHandler() {

			@Override
			public void onDialogHide(DialogHideEvent event) {
				switch (event.getHideButton()) {
				case NO:
					break;
				case YES:
					monitor = new StatAlgoImporterMonitor();
					inputRequestType = InputRequestType.SoftwareCreate;
					InputRequestEvent inputRequestEvent = new InputRequestEvent();
					eventBus.fireEvent(inputRequestEvent);
					Log.debug("SoftwareCreateRequest: " + inputRequestEvent);
					break;
				default:
					break;
				}

			}
		});
		mb.setWidth(300);
		mb.show();
	}

	private void softwarePublish() {
		pm.softwarePublish();

	}
	
	private void softwareRepackage() {
		pm.softwareRepackage();

	}

	private void showCreateProjectDialog() {
		pm.createProject();

	}

	private void showOpenProjectDialog() {
		pm.openProject();

	}

	private void showAddResourceDialog() {
		pm.addResource();
	}

	private void doMainCodeSetCommand(MainCodeSetEvent event) {
		ItemDescription itemDescription = event.getItemDescription();
		if (itemDescription != null && itemDescription.getId() != null) {
			pm.setMainCode(itemDescription);
		}

	}

	private void doSetNewMainCodeEvent(NewMainCodeEvent event) {
		monitor = new StatAlgoImporterMonitor();
		pm.setNewMainCode(event, monitor);

	}

	private void doDeleteItemCommand(DeleteItemEvent event) {
		ItemDescription itemDescription = event.getItemDescription();
		if (itemDescription != null && itemDescription.getId() != null) {
			pm.deleteItem(itemDescription);
		}

	}

	@SuppressWarnings("unused")
	private void showCodeUploadDialog() {
		CodeUploadDialog codeUploadDialog = new CodeUploadDialog(eventBus);
		codeUploadDialog.show();
	}

	private void doSessionExpiredCommand(SessionExpiredEvent event) {
		Log.debug("Session Expired Event: " + event.getSessionExpiredType());
		sessionExpiredShow();

	}

	@SuppressWarnings("unused")
	private void asyncCodeLoadingFailed(Throwable reason) {
		Log.error("Async code loading failed", reason);
		eventBus.fireEvent(new SessionExpiredEvent(
				SessionExpiredType.EXPIREDONSERVER));

	}

}
