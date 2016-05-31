package org.gcube.portlets.user.notifications.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import javax.servlet.ServletContext;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.portal.custom.communitymanager.OrganizationsUtil;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portal.databook.server.DBCassandraAstyanaxImpl;
import org.gcube.portal.databook.server.DatabookStore;
import org.gcube.portal.databook.shared.Notification;
import org.gcube.portal.databook.shared.NotificationChannelType;
import org.gcube.portal.databook.shared.NotificationType;
import org.gcube.portal.databook.shared.UserInfo;
import org.gcube.portlets.user.notifications.client.NotificationsService;
import org.gcube.portlets.user.notifications.shared.NotificationConstants;
import org.gcube.portlets.user.notifications.shared.NotificationPreference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.UserModel;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class NotificationsServiceImpl extends RemoteServiceServlet implements NotificationsService {

	private static final Logger _log = LoggerFactory.getLogger(NotificationsServiceImpl.class);
	private final static String TYPE_CATEGORIES_FILENAME = "categorybytype.properties"; 
	private final static String TYPE_LABELS_FILENAME = "labelbytype.properties"; 
	private final static String TYPE_DESCRIPTIONS_FILENAME = "descbytype.properties"; 
	/**
	 * The store interface
	 */
	private DatabookStore store;
	/**
	 * used for debugging in eclipse
	 */
	private boolean withinPortal = false;
	/**
	 * connect to cassandra at startup
	 */
	public void init() {
		store = new DBCassandraAstyanaxImpl();
	}

	public void destroy() {
		store.closeConnection();
	}

	/**
	 * the current ASLSession
	 * @return the session
	 */
	private ASLSession getASLSession() {
		String sessionID = this.getThreadLocalRequest().getSession().getId();
		String user = (String) this.getThreadLocalRequest().getSession().getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);
		if (user == null) {
			user = getDevelopmentUser();
			_log.warn("USER IS NULL setting "+user+" and Running OUTSIDE PORTAL");
			withinPortal = false;
		}
		else {
			withinPortal = true;
		}
		System.out.println("SessionID = " + sessionID);
		return SessionManager.getInstance().getASLSession(sessionID, user);
	}
	/**
	 * set the user in development mode
	 * @return
	 */
	public String getDevelopmentUser() {
		String user = "test.user";
//		user = "massimiliano.assante";
		return user;
	}
	public UserInfo getUserInfo() {
		try {
			String username = getASLSession().getUsername();
			String email = username+"@isti.cnr.it";
			String fullName = username+" FULL";
			String thumbnailURL = "images/Avatar_default.png";

			if (withinPortal) {
				UserModel user = UserLocalServiceUtil.getUserByScreenName(OrganizationsUtil.getCompany().getCompanyId(), username);
				thumbnailURL = "/image/user_male_portrait?img_id="+user.getPortraitId();
				fullName = user.getFirstName() + " " + user.getLastName();
				email = user.getEmailAddress();
				ThemeDisplay themeDisplay = (ThemeDisplay) this.getThreadLocalRequest().getSession().getAttribute(WebKeys.THEME_DISPLAY);
				String accountURL = themeDisplay.getURLMyAccount().toString();
				UserInfo toReturn = new UserInfo(username, fullName, thumbnailURL, user.getEmailAddress(), accountURL, true, false, null);
				return toReturn;
			}
			else {
				_log.info("Returning test USER");
				return new UserInfo(getASLSession().getUsername(), fullName, thumbnailURL, email, "fakeAccountUrl", true, false, null);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return new UserInfo();
	}

	/**
	 * returns the notifications separated per day
	 */
	public HashMap<Date, ArrayList<Notification>> getUserNotifications() {
		HashMap<Date, ArrayList<Notification>> toReturn = new HashMap<Date, ArrayList<Notification>>();
		try {
			for (Notification notification : store.getAllNotificationByUser(getASLSession().getUsername(), NotificationConstants.NOTIFICATION_NUMBER_PRE)) {
				Date dateWithoutTime = removeTimePart(notification.getTime());
				notification.setDescription(replaceAmpersand(notification.getDescription()));
				if (! toReturn.containsKey(dateWithoutTime)) {
					ArrayList<Notification> nots = new ArrayList<Notification>();
					nots.add(notification);
					toReturn.put(dateWithoutTime, nots);
				} else {
					toReturn.get(dateWithoutTime).add(notification);
				}
				//System.out.println(notification.getDescription());
			}
		} catch (Exception e) {
			_log.error("While trying to get User notifications");
			e.printStackTrace();
		}
		return toReturn;
	}

	/**
	 * returns the notifications in the range separated per day
	 */
	public HashMap<Date, ArrayList<Notification>> getUserNotificationsByRange(int from, int quantity) {
		HashMap<Date, ArrayList<Notification>> toReturn = new HashMap<Date, ArrayList<Notification>>();
		try {
			for (Notification notification : store.getRangeNotificationsByUser(getASLSession().getUsername(), from, quantity)) {
				Date dateWithoutTime = removeTimePart(notification.getTime());
				if (! toReturn.containsKey(dateWithoutTime)) {
					ArrayList<Notification> nots = new ArrayList<Notification>();
					nots.add(notification);
					toReturn.put(dateWithoutTime, nots);
				} else {
					toReturn.get(dateWithoutTime).add(notification);
				}
			}

		} catch (Exception e) {
			_log.error("While trying to get User notifications");
			e.printStackTrace();
		}
		return toReturn;
	}

	/**
	 * we want notification split per day
	 * @param date
	 * @return the date passad as param with time part set to 00:00:00.0
	 */
	private Date removeTimePart(Date date) {
		Calendar cal = Calendar.getInstance();  
		cal.setTime(date);  

		// Set time fields to zero  
		cal.set(Calendar.HOUR_OF_DAY, 0);  
		cal.set(Calendar.MINUTE, 0);  
		cal.set(Calendar.SECOND, 0);  
		cal.set(Calendar.MILLISECOND, 0);  

		return cal.getTime();  
	}
	/**
	 * this sets all the notifications for this user read
	 */
	public boolean setAllUserNotificationsRead() {
		try {
			store.setAllNotificationReadByUser(getASLSession().getUsername());
		} catch (Exception e) {
			_log.error("While trying to set User notifications Read");
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public LinkedHashMap<String, ArrayList<NotificationPreference>> getUserNotificationPreferences() {
		String userid = getASLSession().getUsername();
		//load the 3 prop files
		Properties categories = getCategoriesByType();
		Properties labels = getLabelsByType();
		Properties descriptions = getDescriptionsByType();

		TreeMap<String, ArrayList<NotificationPreference>> treeMap = new TreeMap<String, ArrayList<NotificationPreference>>();

		try {
			Map<NotificationType, NotificationChannelType[]> storePreferences = store.getUserNotificationPreferences(userid);

			for (NotificationType type : storePreferences.keySet()) {
				String category = categories.getProperty(type.toString());
				String typeLabel = labels.getProperty(type.toString());
				String typeDesc = descriptions.getProperty(type.toString());
				if (category != null) {
					if (treeMap.containsKey(category)) {
						treeMap.get(category).add(new NotificationPreference(type, typeLabel, typeDesc, storePreferences.get(type)));
					} else {
						ArrayList<NotificationPreference> toAdd = new ArrayList<NotificationPreference>();
						toAdd.add(new NotificationPreference(type, typeLabel, typeDesc, storePreferences.get(type)));
						treeMap.put(category, toAdd);
					}
				}
			}
		} catch (Exception e) {
			_log.error("While trying to get getUser Notification Preferences for " + userid);		
			e.printStackTrace();
		}
		_log.debug("Got Notification Preferences, returning to the client for user: " + userid);		
		//need the key in revers order so that workspace appears first
		LinkedHashMap<String, ArrayList<NotificationPreference>> toReturn = new LinkedHashMap<String, ArrayList<NotificationPreference>>();
		for (String category : treeMap.descendingKeySet()) {
			ArrayList<NotificationPreference> toAdd = new ArrayList<NotificationPreference>();
			for (NotificationPreference pref : treeMap.get(category)) {
				toAdd.add(pref);
			}
			Collections.sort(toAdd); //sort the labels from the less length to the more length
			toReturn.put(category, toAdd);
		}		
		return toReturn;
	}
	private String replaceAmpersand(String toReplace) {
		String toReturn = toReplace.replaceAll("&amp;", "&");
		return toReturn;
	}
	
	@Override
	public boolean setUserNotificationPreferences(Map<NotificationType, NotificationChannelType[]> enabledChannels) {
		return store.setUserNotificationPreferences(getASLSession().getUsername(), enabledChannels);
	}
	/**
	 * 
	 * @param type the type to look for
	 * @return the Category if there is correspondance, null otherwise
	 */
	private Properties getCategoriesByType() {
		Properties props = new Properties();
		try {
			ServletContext servletContext = getServletContext();
			String contextPath = servletContext.getRealPath(File.separator);
			String propertyfile = contextPath + File.separator + "conf" +  File.separator + TYPE_CATEGORIES_FILENAME;		
			File propsFile = new File(propertyfile);
			FileInputStream fis = new FileInputStream(propsFile);
			props.load(fis);
			return props;
		}
		//catch exception in case properties file does not exist
		catch(IOException e) {
			_log.error(TYPE_CATEGORIES_FILENAME + "file not found under conf dir, returning null");
			return null;
		}
	}
	/**
	 * 
	 * @return the properties for labels
	 */
	private Properties getLabelsByType() {
		Properties props = new Properties();
		String propertyfile = "";
		try {
			ServletContext servletContext = getServletContext();
			String contextPath = servletContext.getRealPath(File.separator);
			propertyfile = contextPath + File.separator + "conf" +  File.separator + TYPE_LABELS_FILENAME;			
			File propsFile = new File(propertyfile);
			FileInputStream fis = new FileInputStream(propsFile);
			props.load( fis);
			return props;
		}
		//catch exception in case properties file does not exist
		catch(IOException e) {
			_log.error(TYPE_LABELS_FILENAME + "file not found under conf dir, returning null propertyfile -> " + propertyfile);
			return null;
		}
	}
	/**
	 * 
	 * @return the properties for descriptions
	 */
	private Properties getDescriptionsByType() {
		Properties props = new Properties();
		try {
			ServletContext servletContext = getServletContext();
			String contextPath = servletContext.getRealPath(File.separator);
			String propertyfile = contextPath + File.separator +"conf" +  File.separator + TYPE_DESCRIPTIONS_FILENAME;			
			File propsFile = new File(propertyfile);
			FileInputStream fis = new FileInputStream(propsFile);
			props.load( fis);
			return props;
		}
		//catch exception in case properties file does not exist
		catch(IOException e) {
			_log.error(TYPE_DESCRIPTIONS_FILENAME + "file not found under conf dir, returning null");
			return null;
		}
	}

}
