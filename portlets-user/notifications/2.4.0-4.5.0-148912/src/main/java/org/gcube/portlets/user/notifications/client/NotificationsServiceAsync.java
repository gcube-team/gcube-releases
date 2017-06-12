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

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface NotificationsServiceAsync {

	void getUserInfo(AsyncCallback<UserInfo> callback);

	void getUserNotifications(
			AsyncCallback<HashMap<Date, ArrayList<Notification>>> callback);

	void setAllUserNotificationsRead(AsyncCallback<Boolean> callback);

	void getUserNotificationPreferences(
			AsyncCallback<LinkedHashMap<String, ArrayList<NotificationPreference>>> callback);

	void setUserNotificationPreferences(
			Map<NotificationType, NotificationChannelType[]> enabledChannels,
			AsyncCallback<Boolean> callback);

	void getUserNotificationsByRange(int from, int quantity,
			AsyncCallback<HashMap<Date, ArrayList<Notification>>> callback);

}
