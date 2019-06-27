package org.gcube.vomanagement.usermanagement.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.RoleRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.TeamRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementPortalException;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.VirtualGroupNotExistingException;
import org.gcube.vomanagement.usermanagement.model.CustomAttributeKeys;
import org.gcube.vomanagement.usermanagement.model.Email;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;
import org.gcube.vomanagement.usermanagement.model.GCubeMembershipRequest;
import org.gcube.vomanagement.usermanagement.model.GCubeRole;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.gcube.vomanagement.usermanagement.model.MembershipRequestStatus;
import org.gcube.vomanagement.usermanagement.util.ManagementUtils;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.model.Contact;
import com.liferay.portal.model.EmailAddress;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.MembershipRequest;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.RoleConstants;
import com.liferay.portal.model.Team;
import com.liferay.portal.model.User;
import com.liferay.portal.security.auth.PrincipalThreadLocal;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.security.permission.PermissionThreadLocal;
import com.liferay.portal.service.ContactLocalServiceUtil;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.ImageLocalServiceUtil;
import com.liferay.portal.service.MembershipRequestLocalServiceUtil;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.RoleServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.TeamLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.webserver.WebServerServletTokenUtil;

public class LiferayUserManager implements UserManager {
	/**
	 * logger
	 */
	private static final Log _log = LogFactoryUtil.getLog(LiferayUserManager.class);
	/**
	 * this method sets the Admin privileges in the local thread, needed to perform such operations.
	 */
	private void doAsAdmin() {
		try {			
			User admin = getAdmin();
			long userId = admin.getUserId();
			PrincipalThreadLocal.setName(userId);
			PermissionChecker permissionChecker = PermissionCheckerFactoryUtil.create(UserLocalServiceUtil.getUser(userId));
			PermissionThreadLocal.setPermissionChecker(permissionChecker); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static User getAdmin() {
		final long companyId = PortalUtil.getDefaultCompanyId();
		Role role = null;
		try {
			role = getRoleById(companyId, RoleConstants.ADMINISTRATOR);
			for (final User admin : UserLocalServiceUtil.getRoleUsers(role.getRoleId())) {
				if (admin.isActive())
					return admin;
			}
		} catch (final Exception e) {
			_log.error("Utils::getAdmin Exception", e);
		}
		return null;
	}
	public static Role getRoleById(final long companyId, final String roleStrId) {
		try {
			return RoleLocalServiceUtil.getRole(companyId, roleStrId);
		} catch (final Exception e) {
			_log.error("Utils::getRoleById Exception", e);
		}
		return null;
	}
	//simple user mapping
	private GCubeUser mapLRUser(User u) throws PortalException, SystemException {
		if (u != null) {
			List<Email> emails = new ArrayList<Email>();
			for (EmailAddress e : u.getEmailAddresses()) {
				emails.add(new Email(e.getAddress(), e.getType().toString(), e.isPrimary()));
			}
			String locationIndustry = "";
			try {
				locationIndustry = (String) readCustomAttr(u.getUserId(), CustomAttributeKeys.USER_LOCATION_INDUSTRY.getKeyName());
			} catch (UserRetrievalFault e1) {
				e1.printStackTrace();
			}
			return new GCubeUser(
					u.getUserId(), 
					u.getScreenName(), 
					u.getEmailAddress(), 
					u.getFirstName(),
					u.getMiddleName(),
					u.getLastName(),
					u.getFullName(),
					u.getCreateDate().getTime(),
					getUserAvatarAbsoluteURL(u),
					u.isMale(),
					u.getJobTitle(),
					locationIndustry,
					emails);
		}
		else 
			return null;
	}
	/**
	 * 
	 * @param u
	 * @return the absolute path of the avatar URL comprising security token e.g. /image/user_male_portrait?img_id=22910&img_id_token=0RJ5WkeDV9F9bETGlqzb7LahygM%3D&t=1458899199747
	 * @throws PortalException
	 * @throws SystemException
	 */
	private static String getUserAvatarAbsoluteURL(User u) throws PortalException, SystemException {
		String pictureBaseURL = u.isMale() ? "/image/user_male_portrait?img_id=" : "/image/user_female_portrait?img_id=";
		String img_id_token = HttpUtil.encodeURL(DigesterUtil.digest(u.getUuid()));
		String token = WebServerServletTokenUtil.getToken(u.getPortraitId());
		return pictureBaseURL+u.getPortraitId()+"&img_id_token="+img_id_token+"&t="+token;
	}
	private GCubeMembershipRequest mapLRMembershipRequest(MembershipRequest req) throws PortalException, SystemException {
		MembershipRequestStatus requestStatus = MembershipRequestStatus.REQUEST;
		if (req.getStatusId() == 1) 
			requestStatus = MembershipRequestStatus.APPROVED;
		else if (req.getStatusId() == 2)
			requestStatus = MembershipRequestStatus.DENIED;

		//get the user requesting it 
		GCubeUser requestingUser = null;
		User theUser = UserLocalServiceUtil.getUser(req.getUserId());
		if (theUser.isActive())
			requestingUser = mapLRUser(theUser);
		GCubeUser replierUser = null;
		if (req.getReplierUserId() != 0) { //means there is a reply for this request
			User theReplier = UserLocalServiceUtil.getUser(req.getReplierUserId());
			if (theUser.isActive())
				replierUser = mapLRUser(theReplier);
		}

		GCubeMembershipRequest toReturn = new GCubeMembershipRequest(
				req.getMembershipRequestId(), 
				req.getGroupId(), 
				requestingUser, 
				req.getCreateDate(), 
				req.getComments(), 
				req.getReplyComments(), 
				replierUser, 
				req.getReplyDate(),
				requestStatus);
		return toReturn;		
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public GCubeUser createUser(boolean autoScreenName, String username, String email, String firstName, String middleName, String lastName, 
			String jobTitle, String location_industry, String backgroundSummary, boolean male, String reminderQuestion, String reminderAnswer)	throws UserManagementSystemException {
		return createUserBody(
				autoScreenName, 
				username, 
				email, 
				firstName, 
				middleName, 
				lastName, 
				jobTitle, 
				location_industry,
				backgroundSummary, 
				male, 
				reminderQuestion, 
				reminderAnswer, 
				false,
				false,
				null, null,
				null,  null,  null,
				null,  null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GCubeUser createUser(boolean autoScreenName, String username, String email, String firstName, String middleName, String lastName,
			String jobTitle, String location_industry, String backgroundSummary, boolean male, String reminderQuestion, String reminderAnswer, boolean sendEmail, boolean forcePasswordReset)
					throws UserManagementSystemException {
		return createUserBody(
				autoScreenName, 
				username, 
				email, 
				firstName, 
				middleName, 
				lastName, 
				jobTitle, 
				location_industry,
				backgroundSummary, 
				male, 
				reminderQuestion, 
				reminderAnswer, 
				sendEmail,
				forcePasswordReset,
				null, null,
				null,  null,  null,
				null,  null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GCubeUser createUser(boolean autoScreenName, String username, String email, String firstName, String middleName, String lastName,
			String jobTitle, String location_industry, String backgroundSummary, boolean male, String reminderQuestion, String reminderAnswer, boolean sendEmail, boolean forcePasswordReset,
			byte[] portraitBytes)
					throws UserManagementSystemException {
		return createUserBody(
				autoScreenName, 
				username, 
				email, 
				firstName, 
				middleName, 
				lastName, 
				jobTitle, 
				location_industry,
				backgroundSummary, 
				male, 
				reminderQuestion, 
				reminderAnswer, 
				sendEmail,
				forcePasswordReset,
				portraitBytes,  null,
				null,  null,  null,
				null,  null);
	}

	@Override
	public GCubeUser createUser(boolean autoScreenName, String username,
			String email, String firstName, String middleName, String lastName,
			String jobTitle, String location_industry,
			String backgroundSummary, boolean male, String reminderQuestion,
			String reminderAnswer, boolean sendEmail,
			boolean forcePasswordReset, byte[] portraitBytes, String mySpacesn,
			String twittersn, String facebooksn, String skypesn,
			String jabbersn, String aimsn)
					throws UserManagementSystemException {
		return createUserBody(
				autoScreenName, 
				username, 
				email, 
				firstName, 
				middleName, 
				lastName, 
				jobTitle,
				location_industry,
				backgroundSummary, 
				male, 
				reminderQuestion, 
				reminderAnswer, 
				sendEmail,
				forcePasswordReset,
				portraitBytes,  mySpacesn,
				twittersn,  facebooksn,  skypesn,
				jabbersn,  aimsn);
	}

	/**
	 * Body for the different createUser functions.
	 * @param autoScreenName
	 * @param username
	 * @param email
	 * @param firstName
	 * @param middleName
	 * @param lastName
	 * @param jobTitle
	 * @param location_industry
	 * @param backgroundSummary
	 * @param male
	 * @param reminderQuestion
	 * @param reminderAnswer
	 * @param sendEmail
	 * @param forcePasswordReset
	 * @param portraitBytes
	 * @param mySpacesn
	 * @param twittersn
	 * @param facebooksn
	 * @param skypesn
	 * @param jabbersn
	 * @param aimsn
	 * @param webSite
	 * @return
	 */
	private GCubeUser createUserBody(boolean autoScreenName, String username,
			String email, String firstName, String middleName, String lastName,
			String jobTitle,  String location_industry, String backgroundSummary, boolean male,
			String reminderQuestion, String reminderAnswer, boolean sendEmail, boolean forcePasswordReset, byte[] portraitBytes,
			String mySpacesn, String twittersn, String facebooksn, String skypesn, String jabbersn, String aimsn
			){

		GCubeUser toReturn = null;
		try {
			_log.debug("Trying createuser " + email);
			Long defaultCompanyId = PortalUtil.getDefaultCompanyId();
			Long defaultUserId = UserLocalServiceUtil.getDefaultUserId(defaultCompanyId);

			boolean  autoPassword = false;
			Locale locale = new Locale("en_US");
			int prefixId = 0;
			int suffixId = 0;
			int birthdayMonth = 1;
			int birthdayDay = 1;
			int birthdayYear = 1970;
			String password1 = "training1";
			String password2 = password1;
			User added = UserLocalServiceUtil.addUser(
					defaultUserId, 
					defaultCompanyId, 
					autoPassword, 
					password1, 
					password2, 
					autoScreenName, 
					username, 
					email, 
					0L, 
					"", 
					locale, 
					firstName, 
					middleName, 
					lastName, 
					prefixId, 
					suffixId, 
					male, 
					birthdayMonth,
					birthdayDay, 
					birthdayYear, 
					jobTitle, 
					null,
					null, 
					null, 
					null, 
					sendEmail,
					new ServiceContext());
			added.setComments(backgroundSummary);
			UserLocalServiceUtil.updateUser(added);
			_log.debug("CreateUser " + lastName + " SUCCESS");
			UserLocalServiceUtil.updateAgreedToTermsOfUse(added.getUserId(), true);
			UserLocalServiceUtil.updateEmailAddressVerified(added.getUserId(), true);
			UserLocalServiceUtil.updatePasswordReset(added.getUserId(), forcePasswordReset);
			if (reminderQuestion == null || reminderQuestion.compareTo("") == 0) 
				reminderQuestion = "Unknown question";				
			if (reminderAnswer == null || reminderAnswer.compareTo("") == 0) 
				reminderAnswer = "Unknown answer";				
			UserLocalServiceUtil.updateReminderQuery(added.getUserId(), reminderQuestion, reminderAnswer);
			_log.debug("User " + lastName + " has agreed to ToU");
			_log.debug("User " + lastName + " has verified the Email");
			_log.debug("User " + lastName + " updatePasswordReset & updateReminderQuery");

			//try save the location/industry
			saveCustomAttr(added.getUserId(), CustomAttributeKeys.USER_LOCATION_INDUSTRY.getKeyName(), location_industry);

			// try to change user's avatar
			if(portraitBytes != null){
				try{
					_log.debug("Updating user's avatar");
					UserLocalServiceUtil.updatePortrait(added.getUserId(), portraitBytes);
					_log.debug("User's avatar set OK");
				}catch(PortalException  e1){
					_log.debug("Unable to set user's avatar", e1);
				}
				catch(SystemException e1){
					_log.debug("Unable to set user's avatar", e1);
				}
			}

			// retrieve the contact
			Contact contact = added.getContact();

			// try to set contact information
			if (mySpacesn != null && mySpacesn.compareTo("") != 0)
				contact.setMySpaceSn(mySpacesn);
			if (twittersn != null && twittersn.compareTo("") != 0)
				contact.setTwitterSn(twittersn);
			if (facebooksn != null && facebooksn.compareTo("") != 0)
				contact.setFacebookSn(facebooksn);
			if (skypesn != null && skypesn.compareTo("") != 0)
				contact.setSkypeSn(skypesn);
			if (jabbersn != null && jabbersn.compareTo("") != 0)
				contact.setJabberSn(jabbersn);
			if (aimsn != null && aimsn.compareTo("") != 0)
				contact.setAimSn(aimsn);

			// update contact
			ContactLocalServiceUtil.updateContact(contact);

			return mapLRUser(added);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return toReturn;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GCubeUser getUserByUsername(String username) throws UserManagementSystemException, UserRetrievalFault {
		User toGet = null;
		GCubeUser toReturn = null;
		try {
			//_log.debug("Trying to fetch user by username = " + username);
			toGet = UserLocalServiceUtil.getUserByScreenName(ManagementUtils.getCompany().getCompanyId(), username);
			toReturn = mapLRUser(toGet);
		} catch (PortalException e1) {
			throw new UserRetrievalFault("User not existing", username, e1);
		} catch (SystemException e) {
			_log.error("SystemException: Failed to fetch user by username = " + username);
			throw new UserManagementSystemException(e.getMessage(), e);
		}
		return toReturn;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Deprecated
	public GCubeUser getUserByScreenName(String username) throws UserManagementSystemException, UserRetrievalFault {
		return getUserByUsername(username);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public GCubeUser getUserById(long userId) throws UserManagementSystemException, UserRetrievalFault {
		User toGet = null;
		GCubeUser toReturn = null;
		try {
			_log.debug("Trying to fetch user by LR Id = " + userId);
			toGet = UserLocalServiceUtil.getUser(userId);
			toReturn = mapLRUser(toGet);
		} catch (PortalException e) {
			throw new UserRetrievalFault("User not existing", userId, e);
		} catch (SystemException e) {
			throw new UserManagementSystemException(e.getMessage(), e);
		}
		return toReturn;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getUserProfessionalBackground(long userId) throws UserManagementSystemException, UserRetrievalFault {
		User toGet = null;
		String toReturn = null;
		try {
			_log.debug("Trying to fetch user Professional Background by LR Id = " + userId);
			toGet = UserLocalServiceUtil.getUser(userId);
			toReturn = toGet.getComments();
		} catch (PortalException e) {
			throw new UserRetrievalFault("User not existing", userId, e);
		} catch (SystemException e) {
			throw new UserManagementSystemException(e.getMessage(), e);
		}
		return toReturn;
	}

	@Override
	public void setUserProfessionalBackground(long userId, String summary)
			throws UserManagementSystemException, UserRetrievalFault {
		User user = null;
		try {
			_log.debug("Trying to set user Professional Background by LR Id = " + userId);
			user = UserLocalServiceUtil.getUser(userId);
			user.setComments(summary);
			UserLocalServiceUtil.updateUser(user);
		} catch (PortalException e) {
			throw new UserRetrievalFault("User not existing", userId, e);
		} catch (SystemException e) {
			throw new UserManagementSystemException(e.getMessage(), e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GCubeUser getUserByEmail(String emailAddress) throws UserManagementSystemException, UserRetrievalFault {
		User toGet = null;
		GCubeUser toReturn = null;
		try {
			_log.debug("Trying to fetch user by emailAddress = " + emailAddress);
			toGet = UserLocalServiceUtil.getUserByEmailAddress(ManagementUtils.getCompany().getCompanyId(), emailAddress);
			toReturn = mapLRUser(toGet);
		} catch (PortalException e) {
			throw new UserRetrievalFault("emailAddress not existing", e);
		} catch (SystemException e) {
			throw new UserManagementSystemException(e.getMessage(), e);
		}
		return toReturn;
	}
	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public long getUserId(String screenName) throws UserManagementSystemException, UserRetrievalFault {
		return getUserByUsername(screenName).getUserId();
	}
	/*
	 * LISTING ENTITIES 
	 */
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<GCubeUser> listUsers(boolean indexed) throws UserManagementSystemException {
		if (indexed)
			return listUsers();
		_log.debug("Asked for listUsers non indexed");	
		List<GCubeUser> toReturn = new ArrayList<GCubeUser>();
		try {
			OrderByComparator comparator = OrderByComparatorFactoryUtil.create("User_", "screenname", true);
			List<User> lrUsers = UserLocalServiceUtil.search(ManagementUtils.getCompany().getCompanyId(), "", 0, null, QueryUtil.ALL_POS, QueryUtil.ALL_POS, comparator);
			for (User user : lrUsers) {
				if (user.isActive())
					toReturn.add(mapLRUser(user));
			}
		} catch (SystemException e) {
			throw new UserManagementSystemException(e.getMessage(), e);
		} catch (PortalException e) {
			e.printStackTrace();
		}
		return toReturn;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<GCubeUser> listUsers() throws UserManagementSystemException {
		List<GCubeUser> toReturn = new ArrayList<GCubeUser>();
		try {
			List<User> lrUsers = UserLocalServiceUtil.getUsers(QueryUtil.ALL_POS,  QueryUtil.ALL_POS);
			for (User user : lrUsers) {
				if (user.isActive())
					toReturn.add(mapLRUser(user));
			}
		} catch (SystemException e) {
			throw new UserManagementSystemException(e.getMessage(), e);
		} catch (PortalException e) {
			e.printStackTrace();
		}
		return toReturn;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<GCubeUser> listUsers(int start, int end) throws UserManagementSystemException {
		List<GCubeUser> toReturn = new ArrayList<GCubeUser>();
		try {
			List<User> lrUsers = UserLocalServiceUtil.getUsers(start,  end);
			for (User user : lrUsers) {
				if (user.isActive())
					toReturn.add(mapLRUser(user));
			}
		} catch (SystemException e) {
			throw new UserManagementSystemException(e.getMessage(), e);
		} catch (PortalException e) {
			e.printStackTrace();
		}
		return toReturn;
	}
	/**
	 * {@inheritDoc}
	 */
	public int getUsersCount() throws UserManagementSystemException {
		try {
			return UserLocalServiceUtil.getUsersCount();
		} catch (SystemException e) {
			throw new UserManagementSystemException(e.getMessage(), e);
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<GCubeUser> listUsersByGroup(long groupId) throws UserManagementSystemException, GroupRetrievalFault, UserRetrievalFault {
		List<GCubeUser> toReturn = new ArrayList<GCubeUser>();
		try {
			for (User user : UserLocalServiceUtil.getGroupUsers(groupId)) {
				if (user.isActive())
					toReturn.add(mapLRUser(user));
			}
		} catch (PortalException e) {
			throw new GroupRetrievalFault("listUsersByGroup not existing group", groupId, e);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return toReturn;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<GCubeUser> listUsersByGroup(long groupId, int start, int end) throws UserManagementSystemException, GroupRetrievalFault, UserRetrievalFault {
		List<GCubeUser> toReturn = new ArrayList<GCubeUser>();
		try {
			for (User user : UserLocalServiceUtil.getGroupUsers(groupId, start, end)) {
				if (user.isActive())
					toReturn.add(mapLRUser(user));
			}
		} catch (PortalException e) {
			throw new GroupRetrievalFault("listUsersByGroup not existing group", groupId, e);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return toReturn;
	}
	/**
	 * {@inheritDoc}
	 */
	public int getGroupUsersCount(long groupId) throws GroupRetrievalFault {
		try {
			return UserLocalServiceUtil.getGroupUsersCount(groupId);
		} catch (SystemException e) {
			throw new GroupRetrievalFault("listUsersByGroup not existing group", groupId, e);
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<GCubeUser> listUsersByGroup(long groupId, boolean indexed) throws UserManagementSystemException, GroupRetrievalFault, UserRetrievalFault {
		if (indexed)
			return listUsersByGroup(groupId);
		List<GCubeUser> toReturn = new ArrayList<GCubeUser>();
		try {
			OrderByComparator comparator = OrderByComparatorFactoryUtil.create("User_", "screenname", true);
			LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();
			params.put("usersGroups", groupId);			
			List<User> usersByGroup = UserLocalServiceUtil.search(ManagementUtils.getCompany().getCompanyId(), "", 0, params, QueryUtil.ALL_POS, QueryUtil.ALL_POS, comparator);
			for (User user : usersByGroup) {
				if (user.isActive())
					toReturn.add(mapLRUser(user));
			}
		} catch (PortalException e) {
			throw new GroupRetrievalFault("listUsersByGroup not existing group", groupId, e);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return toReturn;
	}
	/**
	 * {@inheritDoc}
	 */
	public List<GCubeUser> searchUsersByGroup(String keywords, long groupId) throws GroupRetrievalFault {
		List<GCubeUser> toReturn = new ArrayList<GCubeUser>();
		try {
			OrderByComparator comparator = OrderByComparatorFactoryUtil.create("User_", "screenname", true);
			LinkedHashMap<String, Object> params = new LinkedHashMap<>();
			params.put("usersGroups", groupId); 
			List<User> usersByGroup = UserLocalServiceUtil.search(
					ManagementUtils.getCompany().getCompanyId(), keywords, 0, params, QueryUtil.ALL_POS, QueryUtil.ALL_POS, comparator);
			for (User user : usersByGroup) {
				if (user.isActive())
					toReturn.add(mapLRUser(user));
			}
		} catch (PortalException e) {
			throw new GroupRetrievalFault("listUsersByGroup not existing group", groupId, e);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return toReturn;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<GCubeUser> listUsersByGroupName(String name) throws UserManagementSystemException, GroupRetrievalFault, UserRetrievalFault {
		List<GCubeUser> toReturn = new ArrayList<GCubeUser>();
		try {
			Group g = (GroupLocalServiceUtil.getGroup(ManagementUtils.getCompany().getCompanyId(), name));
			return listUsersByGroup(g.getGroupId());
		} catch (PortalException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return toReturn;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<GCubeUser, List<GCubeRole>> listUsersAndRolesByGroup(long groupId) throws GroupRetrievalFault, UserManagementSystemException, UserRetrievalFault {
		Map<GCubeUser, List<GCubeRole>> toReturn = new HashMap<GCubeUser, List<GCubeRole>>();
		try {
			Group g = GroupLocalServiceUtil.getGroup(groupId);
			_log.debug("Asked for users and roles of group: "+g.getName());			

			List<User> usersByGroup = UserLocalServiceUtil.getGroupUsers(groupId);

			for (User user : usersByGroup) {
				if (user.isActive()) {
					long userId = user.getUserId();
					doAsAdmin();					
					List<Role> userRoles = RoleServiceUtil.getUserGroupRoles(userId, groupId);
					List<GCubeRole> toAdd = new ArrayList<GCubeRole>();
					for (Role role : userRoles) {
						if (! (role.getType() == LiferayRoleManager.ROLE_TYPE && role.getName().startsWith("Site"))) {
							toAdd.add(LiferayRoleManager.mapLRRole(role));
						}
					}
					toReturn.put(mapLRUser(user), toAdd);
				}					
			}			

		} catch (PortalException e1) {
			throw new GroupRetrievalFault("Group not existing (I think you better check)", groupId, e1);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return toReturn;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<GCubeUser> listUsersByGroupAndRole(long groupId, long roleId) throws UserManagementSystemException, RoleRetrievalFault,	GroupRetrievalFault, UserRetrievalFault {
		Map<GCubeUser, List<GCubeRole>> toIterate = listUsersAndRolesByGroup(groupId);
		List<GCubeUser> toReturn = new ArrayList<GCubeUser>();
		Role askedRole = null;
		try {
			askedRole = RoleServiceUtil.getRole(roleId);
		} catch (PortalException e) {
			throw new RoleRetrievalFault("Role not existing (I think you better check) roleId="+roleId, e);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		_log.debug("Asked for role: "+askedRole.getName());	
		for (GCubeUser u : toIterate.keySet()) {
			for (GCubeRole role : toIterate.get(u)) 
				if (role.getRoleId() == roleId) {
					toReturn.add(u);
					break;
				}				
		}		
		return toReturn;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<GCubeUser> listUsersByTeam(long teamId) throws UserManagementSystemException, TeamRetrievalFault, UserRetrievalFault {
		List<GCubeUser> toReturn = new ArrayList<GCubeUser>();
		Team askedTeam = null;
		try {
			askedTeam = TeamLocalServiceUtil.getTeam(teamId);
			_log.debug("Asked for users of team: "+askedTeam.getName());	
			List<User> users = UserLocalServiceUtil.getTeamUsers(teamId);	
			for (User user : users) {
				toReturn.add(mapLRUser(user));
			}
		} catch (PortalException e) {
			throw new TeamRetrievalFault("Team not existing (I think you better check) teamId="+teamId, e);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return toReturn;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<GCubeUser> getUserContactsByGroup(long userId, long scopeGroupId) throws UserManagementSystemException, GroupRetrievalFault, UserRetrievalFault {
		Set<GCubeUser> users = new HashSet<>();
		GroupManager gm = new LiferayGroupManager();
		try {
			if (gm.isRootVO(scopeGroupId)) {
				Set<GCubeGroup> userGroups = gm.listGroupsByUserAndSiteGroupId(userId, scopeGroupId);
				for (GCubeGroup userGroup : userGroups) {
					if (gm.isVRE(userGroup.getGroupId())) {
						users.addAll(listUsersByGroup(userGroup.getGroupId()));
						_log.debug("getUserContactsByGroup added users of group " + userGroup.getGroupId() + " for userid="+userId);
					}
				}
			} else { //is a VRE
				users.addAll(listUsersByGroup(scopeGroupId));
			}
		} catch (UserManagementSystemException | GroupRetrievalFault | UserRetrievalFault | VirtualGroupNotExistingException e) {
			e.printStackTrace();
		}
		return users;
	}
	/*
	 * END LISTING ENTITIES 
	 */



	/*
	 * MEMBERSHIP REQUESTS
	 */
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<GCubeMembershipRequest> listMembershipRequestsByGroup(long groupId) throws UserManagementSystemException, GroupRetrievalFault, UserRetrievalFault {
		try {
			Group g = GroupLocalServiceUtil.getGroup(groupId);
			_log.debug("Asked for pending users list of group: "+g.getName());
		} catch (PortalException e1) {
			throw new GroupRetrievalFault("Group not existing", groupId, e1);
		} catch (SystemException e) {
			e.printStackTrace();
		} 
		List<GCubeMembershipRequest> toReturn = new ArrayList<GCubeMembershipRequest>();
		try {
			int requestesNo = MembershipRequestLocalServiceUtil.getMembershipRequestsCount();
			for (MembershipRequest req : MembershipRequestLocalServiceUtil.getMembershipRequests(0, requestesNo)) {
				if (req.getGroupId() == groupId) {
					toReturn.add(mapLRMembershipRequest(req));
				}
			}

		} catch (SystemException e) {
			e.printStackTrace();
		} catch (PortalException e) {
			e.printStackTrace();
		}
		return toReturn;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public GCubeMembershipRequest requestMembership(long userId, long groupId, String comment) throws UserManagementSystemException, GroupRetrievalFault,	UserRetrievalFault {
		try {
			MembershipRequest req = MembershipRequestLocalServiceUtil.addMembershipRequest(userId, groupId, comment, new ServiceContext());
			return mapLRMembershipRequest(req);
		} catch (PortalException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public GCubeMembershipRequest acceptMembershipRequest(long requestUserId,long groupId, boolean addUserToGroup, String replyUsername, String replyComment) throws UserManagementSystemException, GroupRetrievalFault, UserManagementPortalException {
		try {
			GCubeUser replyMan = getUserByUsername(replyUsername);
			List<GCubeMembershipRequest> requests = listMembershipRequestsByGroup(groupId);
			for (GCubeMembershipRequest req : requests) {
				if (req.getRequestingUser().getUserId() == requestUserId) {
					MembershipRequestLocalServiceUtil.updateStatus(replyMan.getUserId(), req.getMembershipRequestId(), replyComment, 1, addUserToGroup, new ServiceContext());
					//the method above just adds the user to the VRE, it is needed to add the user to the parent Sites as well (VO and RootVO)
					assignUserToGroup(GroupLocalServiceUtil.getGroup(groupId).getParentGroupId(), requestUserId); 

					return mapLRMembershipRequest(MembershipRequestLocalServiceUtil.getMembershipRequest(req.getMembershipRequestId()));
				}
			}

		} catch (UserRetrievalFault e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} 		
		return null;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public GCubeMembershipRequest rejectMembershipRequest(long requestUserId, long groupId,  String replyUsername, String replyComment) throws UserManagementSystemException, GroupRetrievalFault,	UserManagementPortalException {
		try {
			GCubeUser replyMan = getUserByUsername(replyUsername);
			List<GCubeMembershipRequest> requests = listMembershipRequestsByGroup(groupId);
			for (GCubeMembershipRequest req : requests) {
				if (req.getRequestingUser().getUserId() == requestUserId) {
					MembershipRequestLocalServiceUtil.updateStatus(replyMan.getUserId(), req.getMembershipRequestId(), replyComment, 2, false, new ServiceContext());
					return mapLRMembershipRequest(MembershipRequestLocalServiceUtil.getMembershipRequest(req.getMembershipRequestId()));
				}
			}

		} catch (UserRetrievalFault e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} 		
		return null;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public GCubeMembershipRequest getMembershipRequestsById(long membershipRequestId) {
		GCubeMembershipRequest toReturn = null;
		try {
			toReturn = mapLRMembershipRequest(MembershipRequestLocalServiceUtil.getMembershipRequest(membershipRequestId));
		} catch (PortalException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return toReturn;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<GCubeMembershipRequest> getMembershipRequests(long userId, long groupId, MembershipRequestStatus requestStatus) throws UserManagementSystemException, GroupRetrievalFault, UserRetrievalFault {
		try {
			Group g = GroupLocalServiceUtil.getGroup(groupId);
			_log.debug("Asked for pending users list of group: "+g.getName());
		} catch (PortalException e1) {
			throw new GroupRetrievalFault("Group not existing", groupId, e1);
		} catch (SystemException e) {
			e.printStackTrace();
		} 
		List<GCubeMembershipRequest> toReturn = new ArrayList<GCubeMembershipRequest>();
		try {
			int statusId = 0;
			if (requestStatus == MembershipRequestStatus.APPROVED) {
				statusId = 1;
			}
			else if (requestStatus == MembershipRequestStatus.DENIED) {
				statusId = 2;
			}	
			for (MembershipRequest req : MembershipRequestLocalServiceUtil.getMembershipRequests(userId, groupId, statusId)) {
				toReturn.add(mapLRMembershipRequest(req));
			}
		} catch (SystemException e) {
			e.printStackTrace();
		} catch (PortalException e) {
			e.printStackTrace();
		}
		return toReturn;
	}
	/*
	 * END MEMBERSHIP REQUESTS
	 */

	/*
	 * USER TO Sites ASSIGNMENTS
	 */
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void assignUserToGroup(long groupId, long userId) throws UserManagementSystemException, GroupRetrievalFault,	UserRetrievalFault, UserManagementPortalException {
		try {
			GroupManager gm = new LiferayGroupManager();
			if (gm.isRootVO(groupId)) {
				UserLocalServiceUtil.addGroupUser(groupId, userId);
				return;
			}
			if (gm.isVO(groupId)) {
				long parentGroupId = gm.getGroup(groupId).getParentGroupId();
				UserLocalServiceUtil.addGroupUser(parentGroupId, userId);
				UserLocalServiceUtil.addGroupUser(groupId, userId);
				return;
			}
			if (gm.isVRE(groupId)) {
				long rootGroupId = gm.getGroup(gm.getGroup(groupId).getParentGroupId()).getParentGroupId();
				UserLocalServiceUtil.addGroupUser(rootGroupId, userId);
				long parentGroupId = gm.getGroup(groupId).getParentGroupId();
				UserLocalServiceUtil.addGroupUser(parentGroupId, userId);
				UserLocalServiceUtil.addGroupUser(groupId, userId);
			}
		} catch (SystemException e) {
			e.printStackTrace();
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dismissUserFromGroup(long groupId, long userId)	throws UserManagementSystemException, GroupRetrievalFault, UserRetrievalFault {
		try {
			UserLocalServiceUtil.deleteGroupUser(groupId, userId);
		} catch (SystemException e) {
			e.printStackTrace();
		}

	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<GCubeUser> listUnregisteredUsersByGroup(long groupId) throws UserManagementSystemException, GroupRetrievalFault, UserRetrievalFault {
		List<GCubeUser> toProcess = listUsers();
		toProcess.removeAll(listUsersByGroup(groupId));
		return toProcess;
	}

	@Override
	public boolean isPasswordChanged(String emailAddress) {
		_log.debug("Trying to fetch user by email = " + emailAddress);
		User user;
		try {

			user = UserLocalServiceUtil.getUserByEmailAddress(ManagementUtils.getCompany().getCompanyId(), emailAddress);

			// date are saved according GMT
			long creationTime = user.getCreateDate().getTime();

			long changedTime;
			if(user.getPasswordModifiedDate() != null)
				changedTime = user.getPasswordModifiedDate().getTime();
			else
				return false; // there was no change

			return changedTime  > creationTime;
		} catch (Exception e) {
			_log.error("Error while retrieving user with mail=" + emailAddress, e);
		}
		return false;
	}

	@Override
	public boolean userExistsByEmail(String emailAddress) {
		try {
			if(UserLocalServiceUtil.getUserByEmailAddress(ManagementUtils.getCompany().getCompanyId(), emailAddress) != null)
				return true;
		} catch (Exception e){
			_log.error("Error while retrieving user with mail=" + emailAddress, e);
		}
		return false;
	}

	@Override
	public String getFullNameFromEmail(String email) {
		try{
			User user;
			if((user = UserLocalServiceUtil.getUserByEmailAddress(ManagementUtils.getCompany().getCompanyId(), email))!= null){
				return user.getFullName();
			}
		}catch(Exception e){

			_log.error("Unable to find user with email " + email);

		}
		return null;
	}
	@Override
	public void deleteUserByEMail(String email)
			throws UserManagementSystemException,
			UserManagementPortalException, PortalException, SystemException {

		User user;
		if((user = UserLocalServiceUtil.getUserByEmailAddress(ManagementUtils.getCompany().getCompanyId(), email))!= null){

			_log.debug("Deleting user with email " + email);
			UserLocalServiceUtil.deleteUser(user);
			_log.debug("Delete user with email " + email);
		}
	}
	@Override
	public byte[] getUserAvatarBytes(String screenName) {
		try {
			User user = UserLocalServiceUtil.getUserByScreenName(ManagementUtils.getCompany().getCompanyId(), screenName);
			return ImageLocalServiceUtil.getImage(user.getPortraitId()).getTextObj();
		} catch (Exception e) {
			_log.debug("Unable to retrieve user's avatar", e);
		}
		return null;
	}
	@Override
	public String getUserOpenId(String screenName) {
		try {
			User user = UserLocalServiceUtil.getUserByScreenName(ManagementUtils.getCompany().getCompanyId(), screenName);
			return user.getOpenId();
		} catch (PortalException e) {
			_log.debug("Unable to retrieve user's openId", e);
		} catch (SystemException e) {
			_log.debug("Unable to retrieve user's openId", e);
		}
		return null;
	}

	@Override
	public boolean updateContactInformation(String screenName,
			String mySpacesn, String twittersn, String facebooksn,
			String skypesn, String jabbersn, String aimsn) {

		try{
			User user = UserLocalServiceUtil.getUserByScreenName(ManagementUtils.getCompany().getCompanyId(), screenName);

			// retrieve the contact
			Contact contact = user.getContact();

			// set those data
			contact.setMySpaceSn(mySpacesn);
			contact.setTwitterSn(twittersn);
			contact.setFacebookSn(facebooksn);
			contact.setSkypeSn(skypesn);
			contact.setJabberSn(jabbersn);
			contact.setAimSn(aimsn);

			// update contact
			ContactLocalServiceUtil.updateContact(contact);

			return true;
		}catch(Exception e){
			_log.error("Error while updating user " + screenName + " contact information");
			return false;
		}

	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Serializable readCustomAttr(long userId, String attributeKey) throws UserRetrievalFault {
		try {
			doAsAdmin();
			User u = UserLocalServiceUtil.getUser(userId);
			if (u.getExpandoBridge().hasAttribute(attributeKey)) {
				//_log.debug("User Attribute found: " + attributeKey + " trying read value");
				return u.getExpandoBridge().getAttribute(attributeKey);
			} else
				return null;
		} catch (PortalException e1) {
			throw new UserRetrievalFault("User not existing (I think you better check)", e1);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void saveCustomAttr(long userId, String attributeKey, Serializable value) throws UserRetrievalFault {
		try {
			doAsAdmin();
			User u = UserLocalServiceUtil.getUser(userId);
			u.getExpandoBridge().setAttribute(attributeKey, value);
		} catch (PortalException e1) {
			throw new UserRetrievalFault("User not existing (I think you better check)", e1);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	@Override
	public boolean updateJobTitle(long userId, String theJob) {
		try {
			UserLocalServiceUtil.updateJobTitle(userId, theJob);
		} catch (PortalException | SystemException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	@Override
	public List<GCubeUser> listUsersByGlobalRole(long roleId) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<Long> getUserIdsByGroup(long groupId) {
		// TODO Auto-generated method stub
		return null;
	}
}
