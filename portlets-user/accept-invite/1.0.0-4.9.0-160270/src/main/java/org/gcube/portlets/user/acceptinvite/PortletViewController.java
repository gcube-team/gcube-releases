/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package org.gcube.portlets.user.acceptinvite;
import java.io.IOException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.gcube.common.portal.PortalContext;
import org.gcube.portal.databook.server.DBCassandraAstyanaxImpl;
import org.gcube.portal.databook.server.DatabookStore;
import org.gcube.portal.databook.shared.Invite;
import org.gcube.portal.databook.shared.InviteStatus;
import org.gcube.portal.databook.shared.ex.InviteIDNotFoundException;
import org.gcube.portal.databook.shared.ex.InviteStatusNotFoundException;
import org.gcube.portal.invites.InvitesManager;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.User;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;

@Controller(value = "PortletViewController")
@RequestMapping("VIEW")
public class PortletViewController {
	private static Log _log = LogFactoryUtil.getLog(PortletViewController.class);
	public static final Pattern VALID_EMAIL_ADDRESS_REGEX = 
			Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

	public static final String DEFAULT_COMPANY_WEB_ID = "liferay.com";

	private static String PAGE_NOT_AUTHORIZED = "not-authorized";	
	private static String PAGE_INVITE_NOTFOUND = "invite-notfound";	
	private static String PAGE_VRE_NOTFOUND = "vre-notfound";	
	private static String PAGE_INVITE_EXPIRED = "invite-expired";	
	private static String PAGE_INVITE_PROCESS = "view";	

	public static String INVITE_INSTANCE = "inviteInstance";	

	private static String MODEL_ATTR = "theModel";

	private static DatabookStore store;

	/**
	 * 
	 * @return the unique instance of the store
	 */
	public static synchronized DatabookStore getStore() {
		if (store == null) {
			store = new DBCassandraAstyanaxImpl();
		}
		return store;
	}

	@RenderMapping
	public String handleRenderRequest(RenderRequest request,RenderResponse response, Model model) {

		HttpServletRequest httpReq = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(request)); 

		final String INVITE_ID_ENCODED = new String(Base64.encodeBase64(InvitesManager.INVITEID_ATTR.getBytes()));
		final String SITE_ID_ENCODED = new String(Base64.encodeBase64(InvitesManager.SITEID_ATTR.getBytes()));
		if (httpReq.getParameter(INVITE_ID_ENCODED) == null || SITE_ID_ENCODED == null)
			return PAGE_NOT_AUTHORIZED;
		String inviteIdEncoded = (String) httpReq.getParameter(INVITE_ID_ENCODED);
		String siteIdEncoded = (String) httpReq.getParameter(SITE_ID_ENCODED);
		String inviteId= new String(Base64.decodeBase64(inviteIdEncoded));
		String groupId = new String(Base64.decodeBase64(siteIdEncoded));

		_log.info("GOT inviteId=" + inviteId);
		_log.info("siteId=" + groupId);

		Group site = null;
		try {
			site = GroupLocalServiceUtil.getGroup(Long.parseLong(groupId));
		} catch (Exception e1) {
			e1.printStackTrace();
			return PAGE_VRE_NOTFOUND;
		}

		Invite invite = null;

		try {
			invite =  getStore().readInvite(inviteId);
		} catch (InviteIDNotFoundException | InviteStatusNotFoundException e) {
			e.printStackTrace();
			return PAGE_INVITE_NOTFOUND;
		}

		if (invite.getStatus() == InviteStatus.ACCEPTED)
			return PAGE_INVITE_EXPIRED;


		GCubeUser invitedUser = null;
		try {
			invitedUser = new LiferayUserManager().getUserByEmail(invite.getInvitedEmail());
			model.addAttribute("invitedUser", invitedUser);
		} catch (UserManagementSystemException | UserRetrievalFault e) {
			_log.info("No user account exist with this email: " + invite.getInvitedEmail());
		}

		//we set the invite instance retrieved in the model
		model.addAttribute(INVITE_INSTANCE, invite);
		model.addAttribute("vreName", site.getName());
		model.addAttribute("vreFriendlyURL", site.getFriendlyURL());
		model.addAttribute("groupId", site.getGroupId());
		model.addAttribute("landingPage", PortalContext.getConfiguration().getSiteLandingPagePath(httpReq));

		HttpSession session = httpReq.getSession();
		session.setAttribute(MODEL_ATTR, model);

		return PAGE_INVITE_PROCESS;
	}




	@ResourceMapping(value="createAccount")
	public void createAccountForUser(ResourceRequest request, ResourceResponse response) throws IOException  {
		String email = ParamUtil.getString(request, "email");
		String firstName = ParamUtil.getString(request, "firstname");
		String lastName = ParamUtil.getString(request, "lastname");
		String password = ParamUtil.getString(request, "password");
		String repassword = ParamUtil.getString(request, "repassword");
		_log.info("firstName=" + firstName);
		_log.info("lastName=" + lastName);
		_log.info("email=" + email);
		_log.info("password=" + password);
		_log.info("repassword=" + repassword);


		HttpServletRequest httpReq = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(request)); 
		Model model = (Model) httpReq.getSession().getAttribute(MODEL_ATTR);
		Invite invite = (Invite) model.asMap().get(INVITE_INSTANCE);

		if (invite.getInvitedEmail().compareTo(email) != 0) {
			response.getWriter().println("The email address invited does not match or is empty.");
			return;
		}

		//check the fields before creating account
		if (firstName == null 
				|| firstName.equals("") 
				|| lastName == null 
				|| lastName.equals("")
				|| email.equals("")
				|| password.equals("")
				|| repassword.equals("")
				|| password.length() < 8) {
			response.getWriter().println("Not all the required fields have been filled.");
			return;
		}
		if (!validate(email)) {
			response.getWriter().println("The email address invited does not look like a valid email address.");
			return;
		}

		//checking if the user has been already registered or is already in the portal
		User theUser = register(firstName, lastName, email, password);
		if (theUser != null)
			response.getWriter().println("OK");	
		else
			response.getWriter().println("Something went wrong when creating your account during server communication, please check your connection.");
		return;
	}

	private static boolean validate(String emailStr) {
		Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
		return matcher.find();
	}

	private User register(String firstName, String lastName, String email, String password1) {
		User toReturn = null;
		try{
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
			String password2 = password1;
			toReturn = UserLocalServiceUtil.addUser(
					defaultUserId, 
					defaultCompanyId, 
					autoPassword, 
					password1, 
					password2, 
					true, 
					"", 
					email, 
					0L, 
					"", 
					locale, 
					firstName, 
					"", 
					lastName, 
					prefixId, 
					suffixId, 
					true, 
					birthdayMonth,
					birthdayDay, 
					birthdayYear, 
					"", 
					null,
					null, 
					null, 
					null, 
					true,
					new ServiceContext());
			_log.debug("CreateUser " + lastName + " SUCCESS");
			UserLocalServiceUtil.updateAgreedToTermsOfUse(toReturn.getUserId(), false);
			UserLocalServiceUtil.updatePasswordReset(toReturn.getUserId(), false);

		}
		catch(Exception e){
			// unable to create.. we need to delete it from the list of users
			_log.error("Unable to create the user " + email + " in liferay.", e);

		}
		return toReturn;
	}
}
