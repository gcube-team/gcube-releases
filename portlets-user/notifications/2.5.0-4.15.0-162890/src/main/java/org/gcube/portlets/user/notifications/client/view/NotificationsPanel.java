package org.gcube.portlets.user.notifications.client.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.gcube.portal.databook.shared.Notification;
import org.gcube.portal.databook.shared.UserInfo;
import org.gcube.portlets.user.notifications.client.NotificationsService;
import org.gcube.portlets.user.notifications.client.NotificationsServiceAsync;
import org.gcube.portlets.user.notifications.client.view.templates.DayWrapper;
import org.gcube.portlets.user.notifications.client.view.templates.ShowMoreNotifications;
import org.gcube.portlets.user.notifications.client.view.templates.SingleNotificationView;
import org.gcube.portlets.user.notifications.shared.NotificationConstants;
import org.gcube.portlets.user.notifications.shared.NotificationPreference;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.Window.ScrollEvent;
import com.google.gwt.user.client.Window.ScrollHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Massimiliano Assante ISTI-CNR
 *
 */
public class NotificationsPanel extends Composite {
	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final NotificationsServiceAsync notificationService = GWT.create(NotificationsService.class);

	public static final String loading = GWT.getModuleBaseURL() + "../images/feeds-loader.gif";
	private static final String spacer = GWT.getModuleBaseURL() + "../images/feeds-spacer.gif";
	private static final String warning = GWT.getModuleBaseURL() + "../images/warning_blue.png";
	private Image loadingImage;
	private ShowMoreNotifications showMoreWidget;
	//needed to know the next range start
	private int fromStartingPoint = 0;
	//needed to avoid to call the doShowMoreNotifications when the forst call has not returned yet
	private boolean lockNotificationUpdate = false;

	private VerticalPanel container = new VerticalPanel();
	private HorizontalPanel settingsPanel = new HorizontalPanel();
	private VerticalPanel showMoreNotificationsPanel = new VerticalPanel();
	private VerticalPanel loadingPanel = new VerticalPanel();

	private VerticalPanel mainPanel;

	private HTML notificationSettings = new HTML("<a class=\"notification-btn\">Notification Settings</a>");

	public NotificationsPanel() {
		notificationSettings.setVisible(false);
		mainPanel = new VerticalPanel();
		container.setWidth("100%");
		mainPanel.setWidth("100%");
		settingsPanel.setWidth("90%");
		showMoreNotificationsPanel.setWidth("100%");
		settingsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		settingsPanel.add(notificationSettings);
		container.add(settingsPanel);
		container.add(mainPanel);
		loadingImage = new Image(loading);
		showLoader();
		initWidget(container);

		notificationService.getUserInfo(new AsyncCallback<UserInfo>() {

			public void onFailure(Throwable caught) {				
			}
			public void onSuccess(UserInfo result) {
				if (result.getUsername().equals("test.user")) {
					Window.alert("Your session has expired, please log out and login again");
				} 
				else {
					showUserNotifications();
				}
			}
		});

		notificationSettings.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				showSettingsLoader(true);
				notificationService.getUserNotificationPreferences(new AsyncCallback<LinkedHashMap<String,ArrayList<NotificationPreference>>>() {
					@Override
					public void onFailure(Throwable caught) { 
						showSettingsLoader(false);
					}

					@Override
					public void onSuccess(LinkedHashMap<String, ArrayList<NotificationPreference>> result) {
						NotificationSettingsDialog dlg = new NotificationSettingsDialog(result, notificationService);
						dlg.show();			
						showSettingsLoader(false);
					}
				});				
			}
		});

		//this is for the automatic scroll of feeds
		Window.addWindowScrollHandler(new ScrollHandler() {
			@Override
			public void onWindowScroll(ScrollEvent event) {
				boolean isInView = isScrolledIntoView(showMoreWidget);
				if (isInView && !lockNotificationUpdate) {
					doShowMoreNotifications();
					lockNotificationUpdate = true;
				}
			}
		});

		// if the url ends with "showsettings=true" open the settings modal automatically
		if(Location.getHref().endsWith("showsettings=true")){
			Timer t = new Timer() {
				@Override
				public void run() {
					clickElement(notificationSettings.getElement());
				}
			};
			t.schedule(1000);
		}
	}

	/**
	 * Simulate native click event
	 * @param elem
	 */
	private static native void clickElement(Element elem) /*-{
    	elem.click();
	}-*/;

	private void showUserNotifications() {
		showLoader();
		notificationService.getUserNotifications(new AsyncCallback<HashMap<Date, ArrayList<Notification>>>() {

			public void onFailure(Throwable caught) {

			}

			public void onSuccess(HashMap<Date, ArrayList<Notification>> notificationsPerDay) {
				notificationSettings.setVisible(true);
				if (notificationsPerDay != null) {
					mainPanel.clear();
					if (notificationsPerDay.size() == 0) { 
						mainPanel.add(new HTML("<div class=\"no-notification-message\">" +
								"Looks like we've got nothing for you at the moment. <br> " +
								"You may begin by <strong>sharing</strong> an update!</div>"));
					}
					else {
						mainPanel.setHeight("");
						mainPanel.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
						mainPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);

						ArrayList<Date> sortedKeys=new ArrayList<Date>(notificationsPerDay.keySet());
						Collections.sort(sortedKeys, Collections.reverseOrder());

						int notCounter = 0;
						for (Date day : sortedKeys) {
							mainPanel.add(new DayWrapper(day));
							for (Notification notif : notificationsPerDay.get(day))  {
								mainPanel.add(new SingleNotificationView(notif));
								notCounter++;
							}
						}
						setNotificationsRead();
						if (notCounter < 5) {
							mainPanel.add(new Image(spacer));
							mainPanel.add(new Image(spacer));
						}
						if (notCounter > 5 && notCounter < 10)  
							mainPanel.add(new Image(spacer));

						//if you are showing more than NotificationConstants.NOTIFICATION_NUMBER
						if (notCounter >= NotificationConstants.NOTIFICATION_NUMBER_PRE) {
							GWT.log("Show MORE ");
							showMoreNotificationsPanel.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
							showMoreWidget = new ShowMoreNotifications();
							showMoreNotificationsPanel.add(showMoreWidget);
							mainPanel.add(showMoreNotificationsPanel);
						} 	
					}
				} 
				else
					loadingImage.setUrl(warning);	
			}
		});
	}

	/**
	 * called when a user scroll down the page to the bottom	
	 */
	private void doShowMoreNotifications() {
		showMoreNotificationsPanel.remove(0);
		loadingImage.getElement().getStyle().setMargin(10, Unit.PX);
		showMoreNotificationsPanel.add(loadingImage);
		int from = (fromStartingPoint == 0) ? NotificationConstants.NOTIFICATION_NUMBER_PRE+1 : fromStartingPoint;
		fromStartingPoint = from;
		final int quantity = NotificationConstants.NOTIFICATION_NUMBER_PER_REQUEST;

		notificationService.getUserNotificationsByRange(from, quantity, new AsyncCallback<HashMap<Date,ArrayList<Notification>>>() {
			@Override
			public void onSuccess(HashMap<Date, ArrayList<Notification>> notificationsPerDay) {
				mainPanel.remove(showMoreNotificationsPanel);
				if (notificationsPerDay != null) {
					ArrayList<Date> sortedKeys=new ArrayList<Date>(notificationsPerDay.keySet());
					Collections.sort(sortedKeys, Collections.reverseOrder());

					int notCounter = 0;
					for (Date day : sortedKeys) {
						mainPanel.add(new DayWrapper(day));
						for (Notification notif : notificationsPerDay.get(day))  {
							mainPanel.add(new SingleNotificationView(notif));
							notCounter++;
						}
					}
					fromStartingPoint += notCounter;					
					if (notCounter >= quantity) { //there could be more notifications
						showMoreNotificationsPanel.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
						showMoreWidget = new ShowMoreNotifications();
						showMoreNotificationsPanel.clear();
						showMoreNotificationsPanel.add(showMoreWidget);
						mainPanel.add(showMoreNotificationsPanel);
					}
					lockNotificationUpdate = false;
				}
			}
			@Override
			public void onFailure(Throwable caught) {
				showMoreNotificationsPanel.clear();
				mainPanel.add(new HTML("<div class=\"no-notification-message\">" +
						"Ops! There were problems while retrieving your Notifications!. <br> " +
						"Please try again in a short while.</div>"));				
			}
		});
	}

	/**
	 * @param widget the widget to check
	 * @returnn true if the widget is in the visible part of the page
	 */
	private boolean isScrolledIntoView(Widget widget) {
		if (widget != null) {
			int docViewTop = Window.getScrollTop();
			int docViewBottom = docViewTop + Window.getClientHeight();
			int elemTop = widget.getAbsoluteTop();
			int elemBottom = elemTop + widget.getOffsetHeight();
			return ((elemBottom <= docViewBottom) && (elemTop >= docViewTop));
		}		
		return false;
	}

	private void showLoader() {
		mainPanel.clear();
		mainPanel.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
		mainPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		mainPanel.add(loadingImage);
	}

	private void showSettingsLoader(boolean show) {
		if (show) {
			loadingPanel.setWidth("100%");
			loadingPanel.getElement().getStyle().setPosition(Position.ABSOLUTE);
			loadingPanel.getElement().getStyle().setTop(mainPanel.getAbsoluteTop()+200, Unit.PX);
			loadingPanel.getElement().getStyle().setLeft(mainPanel.getAbsoluteLeft(), Unit.PX);
			loadingPanel.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
			loadingPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			loadingPanel.add(loadingImage);
			mainPanel.add(loadingPanel);
		} else
			mainPanel.remove(loadingPanel);
	}

	Timer t;
	private void setNotificationsRead() {
		t = new Timer() {

			@Override
			public void run() {
				notificationService.setAllUserNotificationsRead(new AsyncCallback<Boolean>() {
					public void onFailure(Throwable caught) {
					}
					public void onSuccess(Boolean result) {
					}
				});

			}
		};

		t.schedule(500);
	}
}
