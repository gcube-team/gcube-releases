package org.gcube.portlets.user.notifications.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.gcube.portal.databook.shared.Notification;
import org.gcube.portal.databook.shared.NotificationChannelType;
import org.gcube.portal.databook.shared.NotificationType;
import org.gcube.portal.databook.shared.UserInfo;
import org.gcube.portlets.user.notifications.shared.NotificationPreference;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("notificationsServlet")
public interface NotificationsService extends RemoteService {
	UserInfo getUserInfo();
	HashMap<Date, ArrayList<Notification>> getUserNotifications();
	
	HashMap<Date, ArrayList<Notification>> getUserNotificationsByRange(int from, int quantity);
	
	boolean setAllUserNotificationsRead();
	
	LinkedHashMap<String, ArrayList<NotificationPreference>> getUserNotificationPreferences();
	
	boolean setUserNotificationPreferences(Map<NotificationType, NotificationChannelType[]> enabledChannels);
}
