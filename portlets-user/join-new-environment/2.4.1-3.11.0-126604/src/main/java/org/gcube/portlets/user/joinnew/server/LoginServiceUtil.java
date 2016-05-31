package org.gcube.portlets.user.joinnew.server;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;
import org.gcube.portal.custom.communitymanager.OrganizationsUtil;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portal.databook.shared.Invite;
import org.gcube.portlets.user.joinnew.shared.UserBelonging;
import org.gcube.portlets.user.joinnew.shared.VO;
import org.gcube.portlets.user.joinnew.shared.VRE;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.RoleModel;
import org.gcube.vomanagement.usermanagement.model.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.bean.BeanLocatorException;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.User;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.security.permission.PermissionThreadLocal;
import com.liferay.portal.service.UserLocalServiceUtil;

/**
 * 
 * @author Massimiliano Assante ISTI-CNR
 * 
 */
public class LoginServiceUtil {
	/**
	 * 
	 */
	public static final String ROOT_ORG = "rootorganization";
	/**
	 * 
	 */
	public static final String PUBLIC_LAYOUT_NAME = "	Data e-Infrastructure gateway";
	/**
	 * 
	 */
	public static final String GUEST_COMMUNITY_NAME = "Guest";

	private static Logger _log = LoggerFactory.getLogger(JoinNewServiceImpl.class);	
	
	
	/**
	 * 
	 * @param screenName
	 * @param organizatioId
	 * @return
	 */
	protected static boolean checkPending(String screenName, long organizationId) {
		try {
			for (UserModel userModel : new LiferayUserManager().listPendingUsersByGroup(""+organizationId)) 
				if (userModel.getScreenName().compareTo(screenName) == 0) return true;
		} catch (UserManagementSystemException e) {
			e.printStackTrace();
		} catch (GroupRetrievalFault e) {
			e.printStackTrace();
		} catch (UserRetrievalFault e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * read the root VO name from a property file and retuns it
	 */
	protected static String getRootOrganizationName() {
		//get the portles to look for from the property file
		Properties props = new Properties();
		String toReturn = "";

		try {
			String propertyfile = OrganizationsUtil.getTomcatFolder()+"conf/gcube-data.properties";			
			File propsFile = new File(propertyfile);
			FileInputStream fis = new FileInputStream(propsFile);
			props.load( fis);
			toReturn = props.getProperty(ROOT_ORG);
		}
		//catch exception in case properties file does not exist
		catch(IOException e) {
			toReturn = "gcube";
			_log.error("gcube-data.properties file not found under $CATALINA_HOME/conf dir, returning default VO Name " + toReturn);
			return toReturn;
		}
		//_log.debug("Returning Root VO Name: " + toReturn );
		return toReturn;
	}
	/**
	 * simply returns fake VOS for debugging purpose
	 * @return
	 */
	protected static ArrayList<VO> getFakeVOs() {
		VO rootVO = new VO();
		rootVO.setRoot(true);
		rootVO.setName("/d4science.research-infrastructures.eu/");
		rootVO.setDescription("This is the description for the ROOT VO");
		rootVO.setImageURL("http://portal.d4science.research-infrastructures.eu/vologin/html/RedGrid.jpg");
		rootVO.setUserBelonging(UserBelonging.BELONGING);


		/***************************************/

		VO emVO = new VO();
		emVO.setRoot(false);
		emVO.setGroupName("/d4science.research-infrastructures.eu/EM/");
		emVO.setName("gCUBEApps VO");
		emVO.setDescription("EM and AEM Virtual Organisation The FARM Virtual Organisation is the dynamic group of individuals and/or institutions defined around a set of sharing rules in which resource providers and consumers specify clearly and carefully just what is shared, who is allowed to share, and the conditions under which sharing occurs to serve the needs of the Fisheries and Aquaculture Resources Management.");
		emVO.setImageURL("http://portal.d4science.research-infrastructures.eu/vologin/html/RedGrid.jpg");

		emVO.setUserBelonging(UserBelonging.BELONGING);
		

		VRE cool_EM_VRE2 = new VRE();
		cool_EM_VRE2.setName("COOL VRE 2");
		cool_EM_VRE2.setGroupName("/d4science.research-infrastructures.eu/EM/COOLEMVRE2");
		cool_EM_VRE2.setDescription("Cool VRE Description<br />"+ 
				"This Virtual Research Environment is for cool authors, managers and researchers who produce reports containing cool data.");
		cool_EM_VRE2.setUponRequest(false);

		cool_EM_VRE2.setImageURL("http://portal.d4science.research-infrastructures.eu/vologin/html/aquamaps-preview.jpg");
		cool_EM_VRE2.setUserBelonging(UserBelonging.NOT_BELONGING);

		VRE cool_EM_VRE3 = new VRE();
		cool_EM_VRE3.setName("COOL EM VRE TRE");
		cool_EM_VRE3.setGroupName("/d4science.research-infrastructures.eu/EM/COOlVRE3");
		cool_EM_VRE3.setDescription("Cool VRE Description<br />"+ 
				"This Virtual Research Environment is for cool authors, managers and researchers who produce reports containing cool data.");

		cool_EM_VRE3.setImageURL("http://portal.d4science.research-infrastructures.eu/vologin/html/aquamaps-preview.jpg");
		cool_EM_VRE3.setUserBelonging(UserBelonging.NOT_BELONGING);
		cool_EM_VRE3.setUponRequest(false);

		VRE demo = new VRE();
		demo.setName("Demo");
		demo.setGroupName("/d4science.research-infrastructures.eu/EM/Demo");
		demo.setDescription("Cool VRE Description<br />"+ 
				"This Virtual Research Environment is for cool authors, managers and researchers who produce reports containing cool data.");

		demo.setImageURL("http://portal.d4science.research-infrastructures.eu/vologin/html/RedGrid.jpg");
		demo.setUserBelonging(UserBelonging.NOT_BELONGING);
		demo.setUponRequest(true);

		VRE vreGCM = new VRE();
		vreGCM.setName("GCM");
		vreGCM.setGroupName("/d4science.research-infrastructures.eu/EM/GCM");
		vreGCM.setDescription("Global Ocean Chlorophyll Monitoring (GCM) Virtual Research Environment<br />" 
				+ "The phytoplankton plays a similar role to terrestrial green plants in the photosynthetic process and are credited with removing as much carbon dioxide from the atmosphere as their earthbound counterparts, making it important to monitor and model plankton into calculations of future climate change.");
		vreGCM.setImageURL("http://portal.d4science.research-infrastructures.eu/vologin/html/aquamaps-preview.jpg");
		vreGCM.setUserBelonging(UserBelonging.BELONGING);


		emVO.addVRE(cool_EM_VRE2);
		emVO.addVRE(cool_EM_VRE3);
		emVO.addVRE(demo);
		
		for (int i = 0; i < 10; i++) {
			vreGCM = new VRE();
			vreGCM.setName("GCM "+i);
			vreGCM.setGroupName("/d4science.research-infrastructures.eu/EM/GCM");
			vreGCM.setDescription("Global Ocean Chlorophyll Monitoring (GCM) Virtual Research Environment<br />" 
					+ "The phytoplankton plays a similar role to terrestrial green plants in the photosynthetic process and are credited with removing as much carbon dioxide from the atmosphere as their earthbound counterparts, making it important to monitor and model plankton into calculations of future climate change.");
			vreGCM.setImageURL("http://portal.d4science.research-infrastructures.eu/vologin/html/aquamaps-preview.jpg");
			vreGCM.setUserBelonging(UserBelonging.NOT_BELONGING);
			vreGCM.setUponRequest(true);
			emVO.addVRE(vreGCM);
		}
		
		
		

		VO farmVO = new VO();
		farmVO.setRoot(false);
		farmVO.setGroupName("/d4science.research-infrastructures.eu/FARM/");
		farmVO.setName("FARM");
		farmVO.setDescription("EM and AEM Virtual Organisation The FARM Virtual Organisation is the dynamic group of individuals and/or institutions defined around a set of sharing rules in which resource providers and consumers specify clearly and carefully just what is shared, who is allowed to share, and the conditions under which sharing occurs to serve the needs of the Fisheries and Aquaculture Resources Management.");
		farmVO.setImageURL("http://portal.d4science.research-infrastructures.eu/vologin/html/RedGrid.jpg");

		farmVO.setUserBelonging(UserBelonging.NOT_BELONGING);
		//			
		//			
		VRE farmVRE= new VRE();
		farmVRE.setName("VME-DB");
		farmVRE.setGroupName("/d4science.research-infrastructures.eu/EM/COOLEMVRE");
		farmVRE.setDescription("cool_EM_VRE VRE Description<br />"+ 
				"This Virtual Research Environment is for cool authors, managers and researchers who produce reports containing cool data.");
		farmVRE.setImageURL("http://portal.d4science.research-infrastructures.eu/vologin/html/gcm-preview.jpg");
		farmVRE.setUserBelonging(UserBelonging.NOT_BELONGING);
		farmVO.addVRE(farmVRE);
		VRE farmVR2E= new VRE();
		farmVR2E.setName("YEnvironmental Monitoring Maps");
		farmVR2E.setGroupName("/d4science.research-infrastructures.eu/EM/COOLEMVRE");
		farmVR2E.setDescription("cool_EM_VRE VRE Description<br />"+ 
				"This Virtual Research Environment is for cool authors, managers and researchers who produce reports containing cool data.");
		farmVR2E.setImageURL("https://dl.dropbox.com/u/15737233/aquamaps-preview.jpg");
		farmVO.addVRE(farmVR2E);
		

		ArrayList<VO> toReturn = new ArrayList<VO>();
		toReturn.add(rootVO);
		toReturn.add(emVO);
		toReturn.add(farmVO);
		return toReturn;
	}

	protected static ArrayList<String> getAdministratorsEmails(String scope) {
		LiferayUserManager userManager = new LiferayUserManager();
		LiferayGroupManager groupManager = new LiferayGroupManager();
		String groupId = null;
		try {
			List<org.gcube.vomanagement.usermanagement.model.GroupModel> allGroups = groupManager.listGroups();
			_log.debug("Number of groups retrieved: " + allGroups.size());
			for (int i = 0; i < allGroups.size(); i++) {
				String grId = allGroups.get(i).getGroupId();
				String groupScope = groupManager.getScope(grId);
				System.out.println("Comparing: " + groupScope + " " + scope);
				if (groupScope.equals(scope)) {
					groupId = allGroups.get(i).getGroupId();
					break;
				}
			}
		} catch (UserManagementSystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GroupRetrievalFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HashMap<UserModel, List<RoleModel>> usersAndRoles = null;
		try {
			usersAndRoles = userManager.listUsersAndRolesByGroup(groupId);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		Set<UserModel> users = usersAndRoles.keySet();
		ArrayList<String> adminEmailsList = new ArrayList<String>();
		for (UserModel usr:users) {
			List<RoleModel> roles = usersAndRoles.get(usr);
			for (int i = 0; i < roles.size(); i++) {
				if (roles.get(i).getRoleName().equals("VO-Admin") || roles.get(i).getRoleName().equals("VRE-Manager")) {
					adminEmailsList.add(usr.getEmail());
					_log.debug("Admin: " + usr.getFullname());
					break;
				}
			}
		}
		return adminEmailsList;
	}

	/**
	 * 
	 * @param scope .
	 * @param optionalMessage .
	 */
	public static void addMembershipRequest(String username, VO rootVO, String scope, String optionalMessage,String portalbasicurl, String gatewayName) {
		_log.info("gatewayName = " + gatewayName + " Message=" + optionalMessage);

		
		ArrayList<String> adminEmails = LoginServiceUtil.getAdministratorsEmails(scope);

		User currUser = null;
		try {
			currUser = UserLocalServiceUtil.getUserByScreenName(OrganizationsUtil.getCompany().getCompanyId(), username);
		} catch (Exception e) {

		}
		String name = currUser.getFirstName();
		String lastname = currUser.getLastName();



		String selectedVRE = scope.substring(scope.lastIndexOf("/")+1, scope.length());		
		_log.info("Request MEMBERSHIP for: " + selectedVRE + " scope: " +	scope);

		UserManager um = new LiferayUserManager();
		GroupManager gm  = new LiferayGroupManager();
		String userid = "";
		try {
			userid = um.getUserId(currUser.getScreenName());

			um.requestMembership(userid, gm.getGroupId(selectedVRE), optionalMessage );

		} catch (UserManagementSystemException e) {
			e.printStackTrace();		
		} catch (GroupRetrievalFault e) {
			e.printStackTrace();
		} catch (UserRetrievalFault e) {
			e.printStackTrace();
		}

		StringBuffer body = new StringBuffer();
		body.append("<p>Dear manager of "+ scope +",<br />this email message was automatically generated by " + portalbasicurl +" to inform you that ");
		body.append("</p>");
		body.append("<p>");
		body.append("<b>"+name + " " + lastname +"</b> has requested access to the following environment: ");
		body.append("<br /><br />");
		body.append("<b>" + scope+"</b>");
		body.append("<br />");
		body.append("<br />");
		body.append("<b>Username: </b>" + username);
		body.append("<br />");
		body.append("<b>e-mail: </b>" + currUser.getEmailAddress());
		body.append("</p>");
		body.append("<p>");
		body.append("The request is annotated with the following text: " + optionalMessage);
		body.append("</p>");
		body.append("<p>");
		body.append("You are kindly asked to manage such request by either approving or rejecting it through the user management " +
				"facilities available at ");
		body.append("<br />" + portalbasicurl);
		body.append("</p>");
		body.append("<p>");
		body.append("WARNING / LEGAL TEXT: This message is intended only for the use of the individual or entity to which it is addressed and may contain"+
		" information which is privileged, confidential, proprietary, or exempt from disclosure under applicable law. " +
		"If you are not the intended recipient or the person responsible for delivering the message to the intended recipient, you are strictly prohibited from disclosing, distributing, copying, or in any way using this message.");
		body.append("</p>");
		
		String[] allMails = new String[adminEmails.size()];

		adminEmails.toArray(allMails);

		EmailNotification mailToAdmin = new EmailNotification("no-reply@d4science.org", allMails , "[" + gatewayName + "] - Join Request", body.toString());

		mailToAdmin.sendEmail();
	}
	
	/**
	 * 
	 * @param scope .
	 * @param optionalMessage .
	 */
	public static void notifyUserSelfRegistration(String username, VO rootVO, String scope, String portalbasicurl, String gatewayName) {
			
		ArrayList<String> adminEmails = LoginServiceUtil.getAdministratorsEmails(scope);

		User currUser = null;
		try {
			currUser = UserLocalServiceUtil.getUserByScreenName(OrganizationsUtil.getCompany().getCompanyId(), username);
		} catch (Exception e) {

		}
		String name = currUser.getFirstName();
		String lastname = currUser.getLastName();
	

		StringBuffer body = new StringBuffer();
		body.append("<p>Dear manager of "+ scope +",<br />this email message was automatically generated by " + portalbasicurl +" to inform you that ");
		body.append("</p>");
		body.append("<p>");
		body.append("<b>"+name + " " + lastname +"</b> has self registered to the following environment: ");
		body.append("<br /><br />");
		body.append("<b>" + scope+"</b>");
		body.append("<br />");
		body.append("<br />");
		body.append("<b>Username: </b>" + username);
		body.append("<br />");
		body.append("<b>e-mail: </b>" + currUser.getEmailAddress());
		body.append("</p>");
		body.append("<p>");
		body.append("<br />" + portalbasicurl);
		body.append("</p>");
		body.append("<p>");
		body.append("WARNING / LEGAL TEXT: This message is intended only for the use of the individual or entity to which it is addressed and may contain"+
		" information which is privileged, confidential, proprietary, or exempt from disclosure under applicable law. " +
		"If you are not the intended recipient or the person responsible for delivering the message to the intended recipient, you are strictly prohibited from disclosing, distributing, copying, or in any way using this message.");
		body.append("</p>");
		
		String[] allMails = new String[adminEmails.size()];

		adminEmails.toArray(allMails);

		EmailNotification mailToAdmin = new EmailNotification("no-reply@d4science.org", allMails , "[" + gatewayName + "] - Self Registration", body.toString());

		mailToAdmin.sendEmail();
	}
	
	
	/**
	 * 
	 * @param scope .
	 * @param optionalMessage .
	 */
	public static void notifyUserAcceptedInvite(String username, VO rootVO, String scope, String portalbasicurl, String gatewayName, Invite invite) {
			
		ArrayList<String> adminEmails = LoginServiceUtil.getAdministratorsEmails(scope);

		User currUser = null;
		try {
			currUser = UserLocalServiceUtil.getUserByScreenName(OrganizationsUtil.getCompany().getCompanyId(), username);
		} catch (Exception e) {

		}
		String name = currUser.getFirstName();
		String lastname = currUser.getLastName();
	

		StringBuffer body = new StringBuffer();
		body.append("<p>Dear manager of "+ scope +",<br />this email message was automatically generated by " + portalbasicurl +" to inform you that ");
		body.append("</p>");
		body.append("<p>");
		body.append("<b>"+name + " " + lastname +"</b> has accepted an invitation to the following environment: ");
		body.append("<br /><br />");
		body.append("<b>" + scope+"</b>");
		body.append("<br />");
		body.append("<br />");
		body.append("<b>Username: </b>" + username);
		body.append("<br />");
		body.append("<b>e-mail: </b>" + currUser.getEmailAddress());
		body.append("</p>");
		body.append("<p>");
		body.append("<b>The invitation was sent by " + invite.getSenderFullName() +" (" + invite.getSenderUserId()+") on " + invite.getTime()+"</b>");
		body.append("</p>");
		body.append("<p>");
		body.append("WARNING / LEGAL TEXT: This message is intended only for the use of the individual or entity to which it is addressed and may contain"+
		" information which is privileged, confidential, proprietary, or exempt from disclosure under applicable law. " +
		"If you are not the intended recipient or the person responsible for delivering the message to the intended recipient, you are strictly prohibited from disclosing, distributing, copying, or in any way using this message.");
		body.append("</p>");
		
		String[] allMails = new String[adminEmails.size()];

		adminEmails.toArray(allMails);

		EmailNotification mailToAdmin = new EmailNotification("no-reply@d4science.org", allMails , "[" + gatewayName + "] - Accepted Invitation", body.toString());

		mailToAdmin.sendEmail();
	}
	
	
	/**
	 * 
	 * @param scopename a string
	 * @return an arraylist of <class>VRE</class> with just name and description filled
	 */
	/**
	 * 
	 * @param scopename a string
	 * @return an arraylist of <class>VRE</class> with just name and description filled
	 */
	protected static ArrayList<VRE> getVREsFromInfrastructure(String scopename) {
		ArrayList<VRE> toReturn = new  ArrayList<VRE>();
		ScopeBean scope = null;
		scope = new ScopeBean("/"+scopename);

		try {
			_log.info("Searching for VREs into " + scope.name());
			String currScope = ScopeProvider.instance.get();
			ScopeProvider.instance.set(scope.toString());	
			SimpleQuery query = queryFor(GenericResource.class);
			query.addCondition("$resource/Profile/SecondaryType/text() eq '"+ Type.VRE +"'");

			DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);
			for (GenericResource genres : client.submit(query)) {
				toReturn.add(new VRE(0L, genres.profile().name(), genres.profile().description(), "", "", "", null, true));
			}
			ScopeProvider.instance.set(currScope);	
			return toReturn;
		} catch (Exception e) {
			e.printStackTrace();
			_log.error("Generic Exception for " + scope.name()  + " " + e.getMessage());
			return null;
		}
	}
	
	protected static Boolean isEnabled(String username, Organization currOrg, String attrToCheck) {
		Boolean isEnabled = false;
		if (username.compareTo("test.user") == 0) {
			_log.warn("Found test.user maybe you are in dev mode, returning ... ");
			return true;
		}
		try {
			
			long companyId = OrganizationsUtil.getCompany().getCompanyId();
			_log.trace("Setting Thread Permission");
			User user = UserLocalServiceUtil.getUserByScreenName(companyId, ScopeHelper.getAdministratorUsername());
			PermissionChecker permissionChecker = PermissionCheckerFactoryUtil.create(user, false);
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
			_log.trace("Setting Permission ok!");

			User currentUser = OrganizationsUtil.validateUser(username);
			if (currOrg.getExpandoBridge().getAttribute(attrToCheck) == null || currOrg.getExpandoBridge().getAttribute(attrToCheck).equals("")) {
				_log.trace("Attribute " + attrToCheck + " must be initialized");
				setOrgCustomAttribute(username, currOrg, attrToCheck);
				isEnabled = true;
			}
			else {
				String currVal = (String) currOrg.getExpandoBridge().getAttribute(attrToCheck);
				isEnabled = (currVal.compareTo("true") == 0);
			}

			_log.trace("Setting Thread Permission back to regular");			
			permissionChecker = PermissionCheckerFactoryUtil.create(currentUser, false);
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
			_log.trace("Setting Permission ok! returning ...");
			System.out.println(" returning *********** isEnabled=" + isEnabled);			
			return isEnabled;
		} catch (BeanLocatorException ex) {
			ex.printStackTrace();
			_log.warn("Could not read the property " + attrToCheck + " from LR DB, maybe you are in dev mode, returning true");
			return true;
		}
		catch (Exception e) { 
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * 
	 * @param username
	 * @param attribute2Set
	 */
	protected static void setOrgCustomAttribute(String username,  Organization currOrg, String attribute2Set) {
		User currUser = null;
		if (username.compareTo("test.user") == 0) {
			_log.warn("Found Test User, returning ... ");
			return;
		}
		try {
			long companyId = OrganizationsUtil.getCompany().getCompanyId();
			_log.trace("Setting Thread Permission");
			User user = UserLocalServiceUtil.getUserByScreenName(companyId, ScopeHelper.getAdministratorUsername());
			PermissionChecker permissionChecker = PermissionCheckerFactoryUtil.create(user, false);
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
			_log.trace("Setting Permission ok!");
			
			_log.debug("Creating and Setting custom attribute for colName " + attribute2Set + " to " +true);
			//add the custom attrs
			currUser = UserLocalServiceUtil.getUserByScreenName(companyId, username);
			
			if (! currOrg.getExpandoBridge().hasAttribute(attribute2Set)) 	
				currOrg.getExpandoBridge().addAttribute(attribute2Set);
			
			currOrg.getExpandoBridge().setAttribute(attribute2Set, "true");
			_log.trace("setAttribute true");
			
			
			_log.trace("Setting Thread Permission back to regular");
			permissionChecker = PermissionCheckerFactoryUtil.create(currUser, false);
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
		}
		catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
}
