package org.gcube.portal.liferay.notifications;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.notifications.BaseUserNotificationHandler;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.model.UserNotificationEvent;
import com.liferay.portal.service.ServiceContext;

/**
 * Notification handler for liferay notifications on gcube portal.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class GCubeUserNotificationHandler extends BaseUserNotificationHandler {

	public static final String PORTLET_ID = "gcubenotificationsaction_WAR_GCubeCustomNotifications-portlet";

	public GCubeUserNotificationHandler() {
		setPortletId(org.gcube.portal.liferay.notifications.GCubeUserNotificationHandler.PORTLET_ID);
	}

	@Override
	/**
	 * Return the body of the notification to be shown
	 */
	protected String getBody(UserNotificationEvent userNotificationEvent,
			ServiceContext serviceContext) throws Exception {

		// create new json object
		JSONObject jsonNotification = JSONFactoryUtil
				.createJSONObject(userNotificationEvent.getPayload());

		// retrieve information from the notification
		String notificationText = jsonNotification.getString("notificationBody"); // what he did
		String notificationKey = jsonNotification.getString("notificationId");

		// replace the html body returned by the getBodyTemplate
		String body = StringUtil.replace(getBodyTemplate(), new String[] {"[$NOTIFICATION_KEY$]", "[$BODY_TEXT$]"},
				new String[] {notificationKey , notificationText});

		// return the result
		return body;
	}

	@Override
	/**
	 * Return the link used to redirect the user to the correct place when
	 * clicking on the notification.
	 */
	protected String getLink(UserNotificationEvent userNotificationEvent,
			ServiceContext serviceContext) throws Exception{

		// create new json object
		JSONObject jsonNotification = JSONFactoryUtil
				.createJSONObject(userNotificationEvent.getPayload());
		
		// url
		String url = jsonNotification.getString("notificationUrl"); 
		
		return url;
	}

	/**
	 * Used to define the html of the notification
	 * @return
	 * @throws Exception
	 */
	protected String getBodyTemplate() throws Exception {
		StringBundler sb = new StringBundler(5);
		sb.append("<div " + "id=\"[$NOTIFICATION_KEY$]\""
				+ "onclick=\"gcubeCustomRead('[$NOTIFICATION_KEY$]')\" class=\"custom-gcube-body\">[$BODY_TEXT$]</div>");
		return sb.toString();
	}
}

