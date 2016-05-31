package org.gcube.portlets.user.contactinformation.server;

import java.util.HashMap;
import java.util.List;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;
import org.gcube.portal.custom.communitymanager.OrganizationsUtil;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portal.databook.shared.UserInfo;
import org.gcube.portlets.user.contactinformation.client.ContactInfoService;
import org.gcube.portlets.user.contactinformation.shared.ContactType;
import org.gcube.portlets.user.contactinformation.shared.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Contact;
import com.liferay.portal.model.User;
import com.liferay.portal.model.Website;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.service.WebsiteLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ContactInfoServiceImpl extends RemoteServiceServlet implements ContactInfoService {

	private static final Logger _log = LoggerFactory.getLogger(ContactInfoServiceImpl.class);
	/**
	 * the current ASLSession
	 * @return the session
	 */
	private ASLSession getASLSession() {
		String sessionID = this.getThreadLocalRequest().getSession().getId();
		String user = (String) this.getThreadLocalRequest().getSession().getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);
		if (user == null) {
			_log.warn("USER IS NULL setting test.user and Running OUTSIDE PORTAL");
			user = getDevelopmentUser();
		}		
		return SessionManager.getInstance().getASLSession(sessionID, user);
	}
	/**
	 * when packaging test will fail if the user is not set to test.user
	 * @return .
	 */
	public String getDevelopmentUser() {
		String user = "test.user";
		//user = "massimiliano.assante";
		return user;
	}
	/**
	 * 
	 * @return true if you're running into the portal, false if in development
	 */
	private boolean isWithinPortal() {
		try {
			UserLocalServiceUtil.getService();
			return true;
		} 
		catch (com.liferay.portal.kernel.bean.BeanLocatorException ex) {			
			_log.trace("Development Mode ON");
			return false;
		}			
	}


	@Override
	public UserContext getUserContext(String userid) {
		if (userid == null || userid.equals("")) {			
			_log.info("Own Profile");
			return getOwnProfile();
		}
		else {		
			_log.info(userid + " Reading Profile");
			return getUserProfile(userid);
		}
	}

	private UserContext getOwnProfile() {
		try {
			ASLSession session = getASLSession();
			String username = session.getUsername();
			
			String fullName = username+" FULL";
			String thumbnailURL = "images/Avatar_default.png";

			if (isWithinPortal()) {
				com.liferay.portal.model.UserModel user = UserLocalServiceUtil.getUserByScreenName(OrganizationsUtil.getCompany().getCompanyId(), username);
				thumbnailURL = "/image/user_male_portrait?img_id="+user.getPortraitId();
				fullName = user.getFirstName() + " " + user.getLastName();
				ThemeDisplay themeDisplay = (ThemeDisplay) this.getThreadLocalRequest().getSession().getAttribute(WebKeys.THEME_DISPLAY);
				String accountURL = themeDisplay.getURLMyAccount().toString();
				HashMap<String, String> vreNames = new HashMap<String, String>();

				UserInfo userInfo = new UserInfo(username, fullName, thumbnailURL, user.getEmailAddress(), accountURL, true, false, vreNames);
				User theUser = OrganizationsUtil.validateUser(session.getUsername());
				return new UserContext(userInfo, getInformations(theUser), true, isInfrastructureScope());
			}
			else {
				_log.info("Returning test USER");
				HashMap<String, String> fakeVreNames = new  HashMap<String, String>();
				fakeVreNames.put("/gcube/devsec/devVRE","devVRE");
				UserInfo user =  new UserInfo(username, username+ "FULL", thumbnailURL, "", "fakeAccountUrl", true, false, fakeVreNames);
				HashMap<ContactType, String> info = new HashMap<>();
				info.put(ContactType.GOOGLE, "francesco.ciappi@gmail.com");
				info.put(ContactType.FB, "francesco.ciappi");
				return new UserContext(user, info, true, false);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return new UserContext();
	}

	private HashMap<ContactType, String> getInformations(User user) throws Exception {
		Contact theContact = user.getContact();
		HashMap<ContactType, String> toReturn = new HashMap<ContactType, String>();
		if (theContact.getMySpaceSn() != null && theContact.getMySpaceSn().compareTo("") != 0)
			toReturn.put(ContactType.IN, theContact.getMySpaceSn());
		if (theContact.getTwitterSn() != null && theContact.getTwitterSn().compareTo("") != 0)
			toReturn.put(ContactType.TWITTER, theContact.getTwitterSn());
		if (theContact.getFacebookSn() != null && theContact.getFacebookSn().compareTo("") != 0)
			toReturn.put(ContactType.FB, theContact.getFacebookSn());
		if (theContact.getSkypeSn() != null && theContact.getSkypeSn().compareTo("") != 0)
			toReturn.put(ContactType.SKYPE, theContact.getSkypeSn());
		if (theContact.getJabberSn() != null && theContact.getJabberSn().compareTo("") != 0)
			toReturn.put(ContactType.GOOGLE, theContact.getJabberSn());		
		if (theContact.getAimSn() != null && theContact.getAimSn().compareTo("") != 0)
			toReturn.put(ContactType.AIM, theContact.getAimSn());
		
		List<Website> websites = WebsiteLocalServiceUtil.getWebsites(user.getCompanyId(), "com.liferay.portal.model.Contact", theContact.getContactId());
		if (websites != null && websites.size() > 0)
			toReturn.put(ContactType.WEBSITE, websites.get(0).getUrl());
		return toReturn;
	}

	private UserContext getUserProfile(String username) {
		getASLSession();
		String fullName = username+" FULL";
		String thumbnailURL = "images/Avatar_default.png";
		if (isWithinPortal()) {		
			try {
				com.liferay.portal.model.UserModel user = UserLocalServiceUtil.getUserByScreenName(OrganizationsUtil.getCompany().getCompanyId(), username);
				thumbnailURL = "/image/user_male_portrait?img_id="+user.getPortraitId();
				fullName = user.getFirstName() + " " + user.getLastName();
				HashMap<String, String> vreNames = new HashMap<String, String>();
				UserInfo userInfo = new UserInfo(username, fullName, thumbnailURL, user.getEmailAddress(), "", true, false, vreNames);
				User theUser = OrganizationsUtil.validateUser(username);
				return new UserContext(userInfo, getInformations(theUser), false, isInfrastructureScope());

			} catch (Exception e) {
				e.printStackTrace();
				return new UserContext();
			} 
		} else {
			_log.info("Returning test USER");
			HashMap<String, String> fakeVreNames = new  HashMap<String, String>();
			fakeVreNames.put("/gcube/devsec/devVRE","devVRE");
			
			return null;
		}		
	}
	
	/**
	 * Indicates whether the scope is the whole infrastructure.
	 * @return <code>true</code> if it is, <code>false</code> otherwise.
	 */
	private boolean isInfrastructureScope() {
		ScopeBean scope = new ScopeBean(getASLSession().getScope());
		return 	scope.is(Type.INFRASTRUCTURE);
	}
}
