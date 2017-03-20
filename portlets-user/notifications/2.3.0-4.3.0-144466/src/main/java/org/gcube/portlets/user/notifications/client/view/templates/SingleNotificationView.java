package org.gcube.portlets.user.notifications.client.view.templates;

import org.gcube.common.portal.GCubePortalConstants;
import org.gcube.portal.databook.client.GCubeSocialNetworking;
import org.gcube.portal.databook.client.util.Encoder;
import org.gcube.portal.databook.shared.Notification;
import org.gcube.portal.databook.shared.NotificationType;
import org.gcube.portlets.user.gcubewidgets.client.elements.Span;
import org.gcube.portlets.user.notifications.client.view.templates.images.NotificationImages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Massimiliano Assante ISTI-CNR
 *
 */
public class SingleNotificationView extends Composite {

	private final static  String LINK_TEXT = "likes your post: shared a link. ";

	private static NotificationsDayUiBinder uiBinder = GWT
			.create(NotificationsDayUiBinder.class);

	interface NotificationsDayUiBinder extends
	UiBinder<Widget, SingleNotificationView> {
	}

	NotificationImages images = GWT.create(NotificationImages.class);

	@UiField
	Image notificationImage;
	@UiField HTMLPanel mainPanel;
	@UiField Span notificationText;
	@UiField Span timeArea;
	@UiField Span goApp;

	public SingleNotificationView(Notification toShow) {
		initWidget(uiBinder.createAndBindUi(this));

		if (!toShow.isRead()) {
			mainPanel.addStyleName("unread-notification");
		}
		String notificationToShow = toShow.getDescription();
		GWT.log(notificationToShow);
		String removeMarkup = notificationToShow.replaceAll("&amp;", "&").replaceAll("&lt;","<").replaceAll("&gt;",">");
		String actualHTML =  new HTML(removeMarkup).getText();
		
		//in case of links behave differently, i know is terrible //TODO: write better code here i think
		if (new HTML(actualHTML).getText().equalsIgnoreCase(LINK_TEXT)) {
			actualHTML = actualHTML.replace("your post:", "");
			actualHTML = actualHTML.replace("shared", "");
			actualHTML = actualHTML.replace("link.", "link");
			actualHTML += " you shared.";
		}
		//shorten the notification text if greather  than 200 chars
		actualHTML = actualHTML.length() > 200 ? actualHTML.substring(0, 200) + " ..." : actualHTML;			

		String profilePageURL = GCubePortalConstants.PREFIX_GROUP_URL + 
				extractOrgFriendlyURL(Location.getHref()) +"/"+GCubePortalConstants.USER_PROFILE_FRIENDLY_URL;
		
		notificationText.setHTML(
				"<a class=\"link\" href=\""+profilePageURL+"?"+
						Encoder.encode(GCubeSocialNetworking.USER_PROFILE_OID)+"="+
						Encoder.encode(toShow.getSenderid())+"\">"+
						toShow.getSenderFullName()+"</a> " + actualHTML);
		

		timeArea.setHTML(DateTimeFormat.getFormat("h:mm a").format(toShow.getTime()));

		notificationImage.setResource(getImageType(toShow.getType()));

		switch (toShow.getType()) {
		case MENTION:
		case LIKE:
		case COMMENT:
		case OWN_COMMENT:
		case POST_ALERT:
			goApp.setHTML("<a class=\"link\" href=\""+toShow.getUri()+"\"> See this Post.</a>");
			break;
		case WP_FOLDER_SHARE:
		case WP_FOLDER_UNSHARE:
		case WP_FOLDER_ADDEDUSER:
		case WP_FOLDER_REMOVEDUSER:
		case WP_FOLDER_RENAMED:
		case WP_ITEM_DELETE:
		case WP_ITEM_NEW:
		case WP_ITEM_RENAMED:
		case WP_ITEM_UPDATED:
		case WP_ADMIN_UPGRADE:
		case WP_ADMIN_DOWNGRADE:
			goApp.setHTML("<a class=\"link\" href=\""+toShow.getUri()+"\"> Go to Folder.</a>");
			break;
		case CALENDAR_ADDED_EVENT:
		case CALENDAR_UPDATED_EVENT:
		case CALENDAR_DELETED_EVENT:
			goApp.setHTML("<a class=\"link\" href=\""+toShow.getUri()+"\"> Go to Calendar.</a>");	
			break;
		case TDM_TAB_RESOURCE_SHARE:
			goApp.setHTML("<a class=\"link\" href=\""+toShow.getUri()+"\"> See this Tabular Resource.</a>");	
			break;
		case TDM_RULE_SHARE:
		case TDM_TEMPLATE_SHARE:
			goApp.setHTML("<a class=\"link\" href=\""+toShow.getUri()+"\"> Go to Tabular Data Manager.</a>");	
			break;
		}
	}

	private ImageResource getImageType(NotificationType type) {
		switch (type) {
		case LIKE:
			return images.like();
		case COMMENT:
			return images.comment();
		case MENTION:
			return images.mention();
		case MESSAGE:
			return images.message();
		case WP_FOLDER_ADDEDUSER:
		case WP_FOLDER_REMOVEDUSER:
		case WP_FOLDER_SHARE:
		case WP_ITEM_NEW:
		case WP_ITEM_DELETE:
		case WP_ITEM_UPDATED:
			return images.share();
		case WP_FOLDER_UNSHARE:
			return images.unshare();
		case OWN_COMMENT:
			return images.comment();
		case REQUEST_CONNECTION:
			return images.connectionRequest();
		case JOB_COMPLETED_NOK:
			return images.jobNOK();
		case JOB_COMPLETED_OK:
			return images.jobOK();
		case CALENDAR_ADDED_EVENT:
			return images.calendar();
		case CALENDAR_UPDATED_EVENT:
			return images.calendarEdit();
		case CALENDAR_DELETED_EVENT:
			return images.calendarDelete();
		case POST_ALERT:
			return images.postAlert();
		case TDM_TAB_RESOURCE_SHARE:
		case TDM_RULE_SHARE:
		case TDM_TEMPLATE_SHARE:
			return images.tableShare();
		default:
			return images.generic();
		}
	}
	public static String extractOrgFriendlyURL(String portalURL) {
		String groupRegEx = "/group/";
		if (portalURL.contains(groupRegEx)) {
			String[] splits = portalURL.split(groupRegEx);
			String friendlyURL = splits[1];
			if (friendlyURL.contains("/")) {
				friendlyURL = friendlyURL.split("/")[0];
			} else {
				friendlyURL = friendlyURL.split("\\?")[0].split("\\#")[0];
			}
			return "/"+friendlyURL;
		}
		return null;
	}
}
