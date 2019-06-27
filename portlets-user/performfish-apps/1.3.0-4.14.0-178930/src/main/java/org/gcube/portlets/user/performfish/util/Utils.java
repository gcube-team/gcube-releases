package org.gcube.portlets.user.performfish.util;

import static org.gcube.common.authorization.client.Constants.authorizationService;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;

import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.servlet.http.HttpServletRequest;

import org.gcube.common.authorization.client.exceptions.ObjectNotFound;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.authorization.library.provider.UserInfo;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.accessmanager.ACLType;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.GCoreEndpoint.Profile.Endpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.user.performfish.bean.Association;
import org.gcube.portlets.user.performfish.bean.Company;
import org.gcube.portlets.user.performfish.bean.CompanyMember;
import org.gcube.portlets.user.performfish.bean.Farm;
import org.gcube.portlets.user.performfish.util.db.DBUtil;
import org.gcube.portlets.user.performfish.util.db.DatabaseConnection;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.vomanagement.usermanagement.RoleManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.RoleRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.LiferayRoleManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeRole;
import org.gcube.vomanagement.usermanagement.model.GCubeTeam;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFolderConstants;
import com.liferay.portlet.documentlibrary.service.DLAppServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;
/**
 * 
 * @author M. Assante CNR-ISTI
 */
public class Utils {
	private static Log _log = LogFactoryUtil.getLog(Utils.class);
	
	private static String SERVICE_ENDPOINT_CATEGORY = "DataAnalysis";
	private static String SERVICE_ENDPOINT_NAME = "DataMiner";
	public static final String ENCODED_FARM_PARAM = "ZmFybUlk";
	public static String ANALYTICAL_TOOLKIT_PORTLET_ENDPOINT = "performfish-analysis";
	private static String ANALYTICAL_TOOLKIT_SERVICE_GCORE_ENDPOINT_NAME = "perform-service";
	private static String ANALYTICAL_TOOLKIT_SERVICE_GCORE_ENDPOINT_CLASS = "Application";
	public static String ANALYTICAL_TOOLKIT_SERVICE_INTERFACE_NAME = "org.gcube.application.perform.service.PerformService";

	public static final String NOT_ADMIN_PAGE_PATH = "/html/error_pages/NotACompanyAdmin.jsp";
	public static final String NOT_FARM_ADMIN_PAGE_PATH = "/html/error_pages/NotAFarmAdmin.jsp";
	public static final String NOT_BELONGING_PAGE_PATH = "/html/error_pages/NotBelongingToAnyCompany.jsp";
	public static final String NOT_BELONGING_ANY_FARM_PAGE_PATH = "/html/error_pages/NotBelongingToAnyFarm.jsp";
	public static final String NOT_BELONGING_ANY_ASSO_PAGE_PATH = "/html/error_pages/NotBelongingToAnyAssociation.jsp";
	public static final String TOO_MANY_BELONGING_PAGE_PATH = "/html/error_pages/TooManyBelongingCompany.jsp";
	public static final String TOO_MANY_FARM_PAGE_PATH = "/html/error_pages/TooManyBelongingFarm.jsp";
	public static final String TOO_MANY_ASSO_PAGE_PATH = "/html/error_pages/TooManyBelongingAssociation.jsp";
	public static final String SELECT_FARM_PAGE_PATH = "/html/farmrepository/SelectBelongingFarm.jsp";

	private static UserManager UM = new LiferayUserManager();
	private static RoleManager RM =  new LiferayRoleManager();


	/**
	 * For being a Company admin in PerformFISHKPIs VRE you should have the Site Role defined in COMPANY_ADMIN_SITE_ROLE constant.
	 * The company the user belongs to instead is defined by the Team (VRE Group) to which the user belongs to. A user must belong to one Team only.
	 * @param currentUser
	 * @return the GCubeTeam to which the user belongs to, or null oterwise
	 * @throws GroupRetrievalFault 
	 * @throws UserRetrievalFault 
	 */
	public static GCubeTeam checkBelongingToOneCompanyOnly(RenderRequest request, RenderResponse response, MVCPortlet instance) throws PortletException, IOException {

		GCubeUser currentUser = getCurrentUser(request);
		long groupId = -1;
		try {
			groupId = PortalUtil.getScopeGroupId(request);
		} catch (PortalException | SystemException e) {
			e.printStackTrace();
		}
		_log.debug("user is" + currentUser);
		List<GCubeTeam> teams = new ArrayList<>();
		try {
			teams = new LiferayRoleManager().listTeamsByUserAndGroup(currentUser.getUserId(), groupId);
		} catch (UserRetrievalFault | GroupRetrievalFault e) {
			e.printStackTrace();
		}

		if (teams.isEmpty()) {
			_log.info("NOT BELONGING TO ANY TEAM");
			PortletRequestDispatcher dispatcher = instance.getPortletContext().getRequestDispatcher(NOT_BELONGING_PAGE_PATH);
			dispatcher.include(request, response);		
			return null;
		} 
		List<GCubeTeam> companiesChecker = new ArrayList<>(); //this at then end must be length 1
		if (teams.size() > 0) {
			_log.info("Checking company ...");
			Connection conn;
			try {
				conn = DatabaseConnection.getInstance(Utils.getCurrentContext(groupId)).getConnection();
				List<Company> companies = DBUtil.getCompanies(conn);
				for (GCubeTeam team : teams) {
					for (Company company : companies) {
						if (team.getTeamId() == company.getCompanyId()) {
							_log.info(currentUser.getUsername() + " belongs to company ..." + team.getTeamName());
							companiesChecker.add(team);
						}
					}
				}
			} catch (Exception e) {
				PortletRequestDispatcher dispatcher = instance.getPortletContext().getRequestDispatcher("/html/error_pages/operation-error.jsp");
				dispatcher.include(request, response);		
				e.printStackTrace();
			}			
		}
		if (companiesChecker.isEmpty()) {
			PortletRequestDispatcher dispatcher = instance.getPortletContext().getRequestDispatcher(NOT_BELONGING_PAGE_PATH);
			dispatcher.include(request, response);	
			return null;
		} else if (companiesChecker.size() > 1) {
			PortletRequestDispatcher dispatcher = instance.getPortletContext().getRequestDispatcher(TOO_MANY_BELONGING_PAGE_PATH);
			dispatcher.include(request, response);	
			return null;
		} else
			return companiesChecker.get(0);
	}
	/**
	 * 
	 * @param request
	 * @param response
	 * @param instance
	 * @return
	 * @throws PortletException
	 * @throws IOException
	 */
	public static GCubeTeam checkBelongingToOneAssociationOnly(RenderRequest request, RenderResponse response, MVCPortlet instance) throws PortletException, IOException {

		GCubeUser currentUser = getCurrentUser(request);
		long groupId = -1;
		try {
			groupId = PortalUtil.getScopeGroupId(request);
		} catch (PortalException | SystemException e) {
			e.printStackTrace();
		}
		_log.debug("user is" + currentUser);
		List<GCubeTeam> teams = new ArrayList<>();
		try {
			teams = new LiferayRoleManager().listTeamsByUserAndGroup(currentUser.getUserId(), groupId);
		} catch (UserRetrievalFault | GroupRetrievalFault e) {
			e.printStackTrace();
		}

		if (teams.isEmpty()) {
			_log.info("NOT BELONGING TO ANY TEAM");
			PortletRequestDispatcher dispatcher = instance.getPortletContext().getRequestDispatcher(NOT_BELONGING_PAGE_PATH);
			dispatcher.include(request, response);		
			return null;
		} 
		List<GCubeTeam> associationChecker = new ArrayList<>(); //this at then end must be length 1
		if (teams.size() > 0) {
			_log.info("Checking association ...");
			Connection conn;
			try {
				conn = DatabaseConnection.getInstance(Utils.getCurrentContext(groupId)).getConnection();
				List<Association> associations = DBUtil.getAllAssociations(conn);
				for (GCubeTeam team : teams) {
					for (Association ass : associations) {
						if (team.getTeamId() == ass.getAssociationId()) {
							_log.info(currentUser.getUsername() + " belongs to association ..." + team.getTeamName());
							associationChecker.add(team);
						}
					}
				}
			} catch (Exception e) {
				PortletRequestDispatcher dispatcher = instance.getPortletContext().getRequestDispatcher("/html/error_pages/operation-error.jsp");
				dispatcher.include(request, response);		
				e.printStackTrace();
			}			
		}
		if (associationChecker.isEmpty()) {
			PortletRequestDispatcher dispatcher = instance.getPortletContext().getRequestDispatcher(NOT_BELONGING_ANY_ASSO_PAGE_PATH);
			dispatcher.include(request, response);	
			return null;
		} else if (associationChecker.size() > 1) {
			PortletRequestDispatcher dispatcher = instance.getPortletContext().getRequestDispatcher(TOO_MANY_ASSO_PAGE_PATH);
			dispatcher.include(request, response);	
			return null;
		} else
			return associationChecker.get(0);
	
	}

	/**
	 * For being a Company FARM admin in PerformFISHKPIs VRE you should have the Site Role defined in FARM_ADMIN_SITE_ROLE constant.
	 * The farm the user belongs to instead is defined by the Team (VRE Group) to which the user belongs to. A user must belong to one Farm only.
	 * @param currentUser
	 * @return the GCubeTeam to which the user belongs to, or null oterwise
	 * @throws GroupRetrievalFault 
	 * @throws UserRetrievalFault 
	 */
	public static GCubeTeam checkBelongingToOneFarmOnly(RenderRequest request, RenderResponse response, MVCPortlet instance) throws PortletException, IOException {

		GCubeUser currentUser = getCurrentUser(request);
		long groupId = -1;
		try {
			groupId = PortalUtil.getScopeGroupId(request);
		} catch (PortalException | SystemException e) {
			e.printStackTrace();
		}
		_log.debug("user is" + currentUser);
		List<GCubeTeam> teams = new ArrayList<>();
		try {
			teams = new LiferayRoleManager().listTeamsByUserAndGroup(currentUser.getUserId(), groupId);
		} catch (UserRetrievalFault | GroupRetrievalFault e) {
			e.printStackTrace();
		}

		if (teams.isEmpty()) {
			_log.info("NOT BELONGING TO ANY TEAM");
			PortletRequestDispatcher dispatcher = instance.getPortletContext().getRequestDispatcher(NOT_BELONGING_ANY_FARM_PAGE_PATH);
			dispatcher.include(request, response);		
			return null;
		} 
		GCubeTeam theuserCompany = checkBelongingToOneCompanyOnly(request, response, instance);
		List<GCubeTeam> farmsChecker = new ArrayList<>(); //this at then end must be length 1
		if (teams.size() > 0) {
			_log.info("Checking farms ...");
			Connection conn;
			try {
				conn = DatabaseConnection.getInstance(Utils.getCurrentContext(groupId)).getConnection();
				List<Farm> farms = DBUtil.listFarmsByCompanyId(conn, theuserCompany.getTeamId());
				for (GCubeTeam team : teams) {
					for (Farm farm : farms) {
						if (team.getTeamId() == farm.getFarmId()) {
							_log.info(currentUser.getUsername() + " belongs to farm ..." + team.getTeamName());
							farmsChecker.add(team);
						}
					}
				}
			} catch (Exception e) {
				PortletRequestDispatcher dispatcher = instance.getPortletContext().getRequestDispatcher("/html/error_pages/operation-error.jsp");
				dispatcher.include(request, response);		
				e.printStackTrace();
			}			
		}
		if (farmsChecker.isEmpty()) {
			PortletRequestDispatcher dispatcher = instance.getPortletContext().getRequestDispatcher(NOT_BELONGING_ANY_FARM_PAGE_PATH);
			dispatcher.include(request, response);	
			return null;
		} else if (farmsChecker.size() > 1) {
			PortletRequestDispatcher dispatcher = instance.getPortletContext().getRequestDispatcher(TOO_MANY_FARM_PAGE_PATH);
			dispatcher.include(request, response);	
			return null;
		} else
			return farmsChecker.get(0);
	}

	public static boolean checkBelongsToTeam(long userId, long farmId, long groupid) throws Exception {
		List<GCubeTeam> teams = new LiferayRoleManager().listTeamsByUserAndGroup(userId, groupid);
		for (GCubeTeam farm : teams) {
			if (farm.getTeamId() == farmId)
				return true;
		}
		return false;
	}

	
	public static List<GCubeTeam> getUserFarms(RenderRequest request, RenderResponse response, MVCPortlet instance) throws PortletException, IOException {

		GCubeUser currentUser = getCurrentUser(request);
		long groupId = -1;
		try {
			groupId = PortalUtil.getScopeGroupId(request);
		} catch (PortalException | SystemException e) {
			e.printStackTrace();
		}
		_log.info("user is" + currentUser);
		List<GCubeTeam> teams = new ArrayList<>();
		try {
			teams = new LiferayRoleManager().listTeamsByUserAndGroup(currentUser.getUserId(), groupId);
		} catch (UserRetrievalFault | GroupRetrievalFault e) {
			e.printStackTrace();
		}

		if (teams.isEmpty()) {
			_log.info("NOT BELONGING TO ANY TEAM");
			PortletRequestDispatcher dispatcher = instance.getPortletContext().getRequestDispatcher(NOT_BELONGING_ANY_FARM_PAGE_PATH);
			dispatcher.include(request, response);		
			return null;
		} 
		GCubeTeam theuserCompany = checkBelongingToOneCompanyOnly(request, response, instance);
		List<GCubeTeam> farmsChecker = new ArrayList<>(); 
		if (teams.size() > 0) {
			_log.info("Checking farms ...");
			Connection conn;
			try {
				conn = DatabaseConnection.getInstance(Utils.getCurrentContext(groupId)).getConnection();
				List<Farm> farms = DBUtil.listFarmsByCompanyId(conn, theuserCompany.getTeamId());
				for (GCubeTeam team : teams) {
					for (Farm farm : farms) {
						if (team.getTeamId() == farm.getFarmId()) {
							_log.info(currentUser.getUsername() + " belongs to farm ..." + team.getTeamName());
							farmsChecker.add(team);
						}
					}
				}
			} catch (Exception e) {
				PortletRequestDispatcher dispatcher = instance.getPortletContext().getRequestDispatcher("/html/error_pages/operation-error.jsp");
				dispatcher.include(request, response);		
				e.printStackTrace();
			}			
		}
		if (farmsChecker.isEmpty()) {
			PortletRequestDispatcher dispatcher = instance.getPortletContext().getRequestDispatcher(NOT_BELONGING_ANY_FARM_PAGE_PATH);
			dispatcher.include(request, response);	
			return null;
		} else if (farmsChecker.size() > 1) { //executed when more than one farm exist per user
			request.setAttribute("theFarms", farmsChecker); //pass to the JSP
			PortletRequestDispatcher dispatcher = instance.getPortletContext().getRequestDispatcher(SELECT_FARM_PAGE_PATH);
			dispatcher.include(request, response);	
			return null;
		} else
			return farmsChecker;
	}

	public static int getUserFarmsNumber(RenderRequest request, RenderResponse response, MVCPortlet instance) throws PortletException, IOException {

		GCubeUser currentUser = getCurrentUser(request);
		long groupId = -1;
		try {
			groupId = PortalUtil.getScopeGroupId(request);
		} catch (PortalException | SystemException e) {
			e.printStackTrace();
		}
		_log.debug("user is" + currentUser);
		List<GCubeTeam> teams = new ArrayList<>();
		try {
			teams = new LiferayRoleManager().listTeamsByUserAndGroup(currentUser.getUserId(), groupId);
		} catch (UserRetrievalFault | GroupRetrievalFault e) {
			e.printStackTrace();
		}

		if (teams.isEmpty()) {
			_log.info("NOT BELONGING TO ANY TEAM");
			PortletRequestDispatcher dispatcher = instance.getPortletContext().getRequestDispatcher(NOT_BELONGING_ANY_FARM_PAGE_PATH);
			dispatcher.include(request, response);		
			return 0;
		} 
		GCubeTeam theuserCompany = checkBelongingToOneCompanyOnly(request, response, instance);
		List<GCubeTeam> farmsChecker = new ArrayList<>(); 
		if (teams.size() > 0) {
			_log.info("Checking farms ...");
			Connection conn;
			try {
				conn = DatabaseConnection.getInstance(Utils.getCurrentContext(groupId)).getConnection();
				List<Farm> farms = DBUtil.listFarmsByCompanyId(conn, theuserCompany.getTeamId());
				for (GCubeTeam team : teams) {
					for (Farm farm : farms) {
						if (team.getTeamId() == farm.getFarmId()) {
							_log.info(currentUser.getUsername() + " belongs to farm ..." + team.getTeamName());
							farmsChecker.add(team);
						}
					}
				}
			} catch (Exception e) {
				PortletRequestDispatcher dispatcher = instance.getPortletContext().getRequestDispatcher("/html/error_pages/operation-error.jsp");
				dispatcher.include(request, response);		
				e.printStackTrace();
			}			
		}
		if (farmsChecker.isEmpty()) {
			PortletRequestDispatcher dispatcher = instance.getPortletContext().getRequestDispatcher(NOT_BELONGING_ANY_FARM_PAGE_PATH);
			dispatcher.include(request, response);	
			return 0;
		} else 
			return farmsChecker.size();
	}


	/**
	 * 
	 * @param theTeam
	 * @param groupId
	 * @param request
	 * @return
	 * @throws PortalException
	 * @throws SystemException
	 */
	public static String getCompanyLogoURL(String companyName, long groupId, RenderRequest request) {
		String imageUrl = "";
		String fileName = companyName+".png";
		try {			
			Folder folder = DLAppServiceUtil.getFolder(groupId,DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, PFISHConstants.LOGO_FOLDER_NAME);
			DLFileEntry image = DLFileEntryLocalServiceUtil.getFileEntry(groupId, folder.getFolderId(), fileName);

			if (image != null) {
				imageUrl =
						PortalUtil.getPortalURL(request) + "/documents/" + image.getGroupId() + "/" +
								image.getFolderId() + "/" + image.getTitle() + "/" + image.getUuid() + "?t=" +
								System.currentTimeMillis();
			}	
		} catch (Exception e) {
			_log.warn("No Logo URL found for this Comany in the Site Logo folder, file expected to find: " + fileName);
		}
		return imageUrl;		
	}
	/**
	 * 
	 * @param team
	 * @return
	 * @throws GroupRetrievalFault 
	 * @throws RoleRetrievalFault 
	 */
	public static List<CompanyMember> getCompanyAdminTeamMembers(long teamId, long groupId) throws Exception {
		List<CompanyMember> toReturn = new ArrayList<>();
		GCubeRole adminRole = RM.getRole(PFISHConstants.COMPANY_ADMIN_SITE_ROLE, groupId);
		List<GCubeUser> companyUsers = UM.listUsersByTeam(teamId);
		for (GCubeUser member : companyUsers) {
			if (RM.hasRole(member.getUserId(), groupId, adminRole.getRoleId())) {
				toReturn.add(new CompanyMember(member));
			} 
		}
		return toReturn;
	}
	/**
	 * 
	 * @param team
	 * @return
	 * @throws GroupRetrievalFault 
	 * @throws RoleRetrievalFault 
	 */
	public static List<GCubeUser> getFarmAdminTeamMembers(GCubeTeam theCompany, GCubeTeam theFarm) throws Exception {
		long teamId = theFarm.getTeamId();
		List<GCubeUser> toReturn = new ArrayList<>();
		List<GCubeUser> farmUsers = UM.listUsersByTeam(teamId);
		for (GCubeUser member : farmUsers) {
			if (isFarmAdmin(member, theFarm.getGroupId(), theFarm))
				toReturn.add(member); 
		}
		return toReturn;
	}
	/**
	 * 
	 * @param team
	 * @return
	 * @throws GroupRetrievalFault 
	 * @throws RoleRetrievalFault 
	 */
	public static List<CompanyMember> getRegularCompanyMembers(long teamId, long groupId) throws Exception {
		List<CompanyMember> toReturn = new ArrayList<>();

		GCubeRole adminRole = RM.getRole(PFISHConstants.COMPANY_ADMIN_SITE_ROLE, groupId);
		List<GCubeUser> companyUsers = UM.listUsersByTeam(teamId);
		for (GCubeUser member : companyUsers) {
			if (!RM.hasRole(member.getUserId(), groupId, adminRole.getRoleId())) {
				toReturn.add(new CompanyMember(member));
			} 
		}
		return toReturn;
	}
	/**
	 * 
	 * @param team
	 * @return
	 * @throws GroupRetrievalFault 
	 * @throws RoleRetrievalFault 
	 */
	public static List<GCubeUser> getRegularFarmMembers(long farmId, long groupId) throws Exception {
		List<GCubeUser> toReturn = new ArrayList<>();

		//check that the user belongs to the company first
		Connection conn = DatabaseConnection.getInstance(Utils.getCurrentContext(groupId)).getConnection();
		long companyId = DBUtil.getCompanyByFarmId(conn, farmId);
		List<GCubeUser> companyMembers = UM.listUsersByTeam(companyId);

		GCubeRole adminRole = RM.getRole(PFISHConstants.FARM_ADMIN_SITE_ROLE, groupId);
		List<GCubeUser> farmUsers = UM.listUsersByTeam(farmId);
		for (GCubeUser member : farmUsers) {
			if (!RM.hasRole(member.getUserId(), groupId, adminRole.getRoleId()) && companyMembers.contains(member)) {
				toReturn.add(member);
			} 
		}
		return toReturn;
	}
	/**
	 * 
	 * @param teamId
	 * @param groupId
	 * @return the list of the company users eligible to become a farm members, eligible means they belong the company but not to this farm
	 * @throws Exception
	 */
	public static List<GCubeUser> getAvailableFarmMembersToAssociate(long farmId, long companyId, long groupId) throws Exception {
		List<GCubeUser> toReturn = new ArrayList<GCubeUser>();
		List<GCubeUser> companyUsers = UM.listUsersByTeam(companyId);
		List<GCubeUser> farmUsers = UM.listUsersByTeam(farmId);
		for (GCubeUser user : companyUsers) {
			boolean found = false;
			if (farmUsers.contains(user)) {
				found = true;
			}
			if (!found)
				toReturn.add(user);
		}
		return toReturn;
	}
	/**
	 * 
	 * @param team
	 * @return
	 * @throws GroupRetrievalFault 
	 * @throws RoleRetrievalFault 
	 */
	public static List<GCubeUser> listVREUsersNotAssociatedToAnyCompany(long teamId, long groupId) throws Exception {
		List<GCubeUser> toReturn = new ArrayList<>();
		List<GCubeUser> vreUsers = UM.listUsersByGroup(groupId);
		Connection conn = DatabaseConnection.getInstance(Utils.getCurrentContext(groupId)).getConnection();
		List<Company> allCompanies = DBUtil.getCompanies(conn);
		for (GCubeUser vreUser : vreUsers) {
			boolean found = false;
			for (Company company : allCompanies) {
				List<GCubeUser> companyUsers = UM.listUsersByTeam(company.getCompanyId());
				if (companyUsers.contains(vreUser)) {
					found = true;
					break;
				}
			}
			if (!found)
				toReturn.add(vreUser);
		}
		return toReturn;
	}
	/**
	 * 
	 * @param companyId
	 * @param groupId
	 * @return list of Company Users Not Associated To Any Farm of the company
	 * @throws Exception
	 */
	public static List<GCubeUser> listCompanyUsersNotAssociatedToAnyFarm(long companyId, long groupId) throws Exception {
		List<GCubeUser> toReturn = new ArrayList<>();
		List<GCubeUser> companyUsers = UM.listUsersByTeam(companyId);
		Connection conn = DatabaseConnection.getInstance(Utils.getCurrentContext(groupId)).getConnection();
		List<Farm> allFarms = DBUtil.listFarmsByCompanyId(conn, companyId);
		for (GCubeUser companyUser : companyUsers) {
			boolean found = false;
			for (Farm farm : allFarms) {
				List<GCubeUser> farmUsers = UM.listUsersByTeam(farm.getFarmId());
				if (farmUsers.contains(companyUser)) {
					found = true;
					break;
				}
			}
			if (!found)
				toReturn.add(companyUser);
		}
		return toReturn;
	}
	/**
	 * 
	 * @param teamToExclude
	 * @return the list of the user of the current Site withour the members of the team passes as paremeter
	 */
	public static List<GCubeUser> getAvailableSiteMembersToAssociate(long teamId, long groupId) throws Exception {
		List<GCubeUser> toReturn = new ArrayList<GCubeUser>();
		List<GCubeUser> companyUsers = UM.listUsersByTeam(teamId);
		List<GCubeUser> siteUsers = UM.listUsersByGroup(groupId, false);
		for (GCubeUser siteUser : siteUsers) {
			if (!companyUsers.contains(siteUser))
				toReturn.add(siteUser);
		}
		return toReturn;
	}

	public static String getUserProfileLink(String username) {
		return "profile?"+ new String(Base64.getEncoder().encode(PFISHConstants.USER_PROFILE_OID.getBytes()))+"="+new String(Base64.getEncoder().encode(username.getBytes()));
	}
	/**
	 * 
	 * @param currentUser
	 * @param groupId the site id
	 * @param team the Team to which the user belongs to
	 * @return
	 * @throws Exception
	 */
	public static boolean isCompanyAdmin(GCubeUser currentUser, long groupId, GCubeTeam team) throws Exception {
		// instanciate the interface

		long teamId = team.getTeamId();
		List<GCubeUser> companyUsers = UM.listUsersByTeam(teamId);
		//the team must be defined in the current group and the user must be part of the team
		if (team.getGroupId() == groupId && companyUsers.contains(currentUser)) {
			_log.info(currentUser.getUsername() + " belongs to " + team.getTeamName() + " checking Admin rights ...");
			GCubeRole adminRole = RM.getRole(PFISHConstants.COMPANY_ADMIN_SITE_ROLE, groupId);
			boolean isAdmin = RM.hasRole(currentUser.getUserId(), groupId, adminRole.getRoleId());
			_log.info(currentUser.getUsername() + " is Admin of Company " + team.getTeamName() + "? " + isAdmin);
			return isAdmin;
		}
		return false;
	}

	/**
	 * 
	 * @param userId
	 * @param companyId
	 * @param groupId
	 * @return the farm instance or null if any
	 * @throws Exception
	 */
	public static List<Farm> getFarmsByUserId(long userId, long companyId, long groupId) throws Exception {
		_log.debug("user is" + userId);
		List<GCubeTeam> teams = new ArrayList<>();
		List<Farm> toReturn = new ArrayList<>();
		try {
			teams = new LiferayRoleManager().listTeamsByUserAndGroup(userId, groupId);
		} catch (UserRetrievalFault | GroupRetrievalFault e) {
			e.printStackTrace();
		}
		Connection conn = DatabaseConnection.getInstance(Utils.getCurrentContext(groupId)).getConnection();
		List<Farm> allFarms = DBUtil.listFarmsByCompanyId(conn, companyId);
		for (Farm farm : allFarms) {
			for (GCubeTeam team : teams) {
				if (farm.getFarmId() == team.getTeamId()) {
					farm.setName(team.getTeamName());
					toReturn.add(farm);
				}
			}
		}
		return toReturn;
	}

	/**
	 * 
	 * @param theUser
	 * @param groupId the site id
	 * @param theFarm the Team to which the user belongs to
	 * @return
	 * @throws Exception
	 */
	public static boolean isFarmAdmin(GCubeUser theUser, long groupId, GCubeTeam theFarm) throws Exception {
		// instanciate the interface

		long teamId = theFarm.getTeamId();
		List<GCubeUser> farmUsers = UM.listUsersByTeam(teamId);
		//the team must be defined in the current group and the user must be part of the team
		if (theFarm.getGroupId() == groupId && farmUsers.contains(theUser)) {
			_log.info(theUser.getUsername() + " belongs to " + theFarm.getTeamName() + " checking Admin rights ...");
			GCubeRole adminRole = RM.getRole(PFISHConstants.FARM_ADMIN_SITE_ROLE, groupId);
			boolean isAdmin = RM.hasRole(theUser.getUserId(), groupId, adminRole.getRoleId());
			_log.info(theUser.getUsername() + " is Admin of FARM " + theFarm.getTeamName() + "? " + isAdmin);
			return isAdmin;
		}
		return false;
	}

	public static Workspace getWS(String currentUsername, String context) {
		String username = currentUsername;
		String authorizationToken = PortalContext.getConfiguration().getCurrentUserToken(context, username);
		SecurityTokenProvider.instance.set(authorizationToken);
		ScopeProvider.instance.set(context);
		try {		
			return HomeLibrary.getUserWorkspace(username);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * check whether the company folder exists and it creates it when not.
	 * @param currentUsername
	 * @param context
	 * @return
	 */
	public static synchronized WorkspaceFolder getWSCompanyFolder(String currentUsername, String context, GCubeTeam team) {
		String username = currentUsername;
		String authorizationToken = PortalContext.getConfiguration().getCurrentUserToken(context, username);
		SecurityTokenProvider.instance.set(authorizationToken);
		ScopeProvider.instance.set(context);
		Workspace ws = null;
		WorkspaceFolder folder = null;
		String companyName = team.getTeamName();
		try {		
			ws = HomeLibrary.getUserWorkspace(username);
			String destinationFolderId = ws.getRoot().getId();
			String name = companyName+PFISHConstants.COMPANY_WS_FOLDER_SUFFIX;
			String description = "Data belonging to " + companyName;			 

			if (!ws.exists(name, destinationFolderId)) {
				_log.info(companyName + " company folder does not exists, triggering creation ... ");
				folder = ws.createFolder(name, description, destinationFolderId);
				folder.setHidden(PFISHConstants.HIDE_COMPANY_SHARED_FOLDER); 
				_log.info("Company folder created succesfully for " + companyName + " adding other company users (if any)");
				List<String> users = new ArrayList<>();
				for (GCubeUser member : UM.listUsersByTeam(team.getTeamId())) {
					users.add(member.getUsername());
				}
				WorkspaceSharedFolder sharedFolder = folder.share(users);
				try {
					Thread.sleep(5000);
				} catch (InterruptedException ex) {}
				if (sharedFolder.isShared()) {
					sharedFolder.setACL(users, ACLType.WRITE_ALL);
					_log.info("ACL Set correctly ...");
				} else {
					while (!sharedFolder.isShared()) {
						_log.info("Trying to Set ACL ...");
						try {
							Thread.sleep(5000);
						} catch (InterruptedException ex) {}
						if (sharedFolder.isShared()) {
							sharedFolder.setACL(users, ACLType.WRITE_ALL);
							_log.info("ACL Set correctly after at least one attempt...");
						}
					}
				}
			}
			else {
				_log.info(companyName + " company folder exists, returning id ... ");
				folder = (WorkspaceFolder) ws.find(name, destinationFolderId);		
				folder.setHidden(PFISHConstants.HIDE_COMPANY_SHARED_FOLDER); 
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return folder;
	}

	/**
	 * check whether the farm folder exists and it creates it when not.
	 * @param currentUsername
	 * @param context
	 * @return
	 */
	public static synchronized WorkspaceFolder getWSFarmFolder(String currentUsername, String context, GCubeTeam company, GCubeTeam farm) {
		String username = currentUsername;
		String authorizationToken = PortalContext.getConfiguration().getCurrentUserToken(context, username);
		SecurityTokenProvider.instance.set(authorizationToken);
		ScopeProvider.instance.set(context);
	
		Workspace ws = null;
		WorkspaceFolder folder = null;
		String companyName = company.getTeamName();
		try {		
			WorkspaceFolder companyFolder = getWSCompanyFolder(currentUsername, context, company);
			ws = HomeLibrary.getUserWorkspace(username);
			String destinationFolderId = companyFolder.getId();
			String name = Long.toString(farm.getTeamId()); //the farm folder is the unique id of the farm
			String description = new StringBuilder("Forms submitted by farm ")
					.append(farm.getTeamName())
					.append(" belonging to company ").append(companyName).toString();			 
			if (!ws.exists(name, destinationFolderId)) {
				_log.info(companyName + " farm folder does not exists, triggering creation ... ");
				folder = ws.createFolder(name, description, destinationFolderId);
				folder.setHidden(PFISHConstants.HIDE_COMPANY_SHARED_FOLDER); 
				_log.info("Farm folder created succesfully for " + companyName);
			}
			else {
				_log.info(farm.getTeamName() + " farm folder exists, returning id ... ");
				SecurityTokenProvider.instance.set(authorizationToken);
				ScopeProvider.instance.set(context);
				folder = (WorkspaceFolder) ws.find(name, destinationFolderId);		
				folder.setHidden(PFISHConstants.HIDE_COMPANY_SHARED_FOLDER); 
				_log.info(farm.getTeamName() + " farm folder id = " + folder.getId());
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return folder;
	}

	public static GCubeUser getCurrentUser(RenderRequest request) {
		long userId;
		try {
			userId = PortalUtil.getUser(request).getUserId();
			return getCurrentUser(userId);
		} catch (Exception  e) {
			e.printStackTrace();
		}
		return null;		
	}

	public static GCubeUser getCurrentUser(HttpServletRequest request) {
		long userId;
		try {
			userId = PortalUtil.getUser(request).getUserId();
			return getCurrentUser(userId);
		} catch (Exception  e) {
			e.printStackTrace();
		}
		return null;		
	}

	public static GCubeUser getCurrentUser(ResourceRequest request) {
		long userId;
		try {
			userId = PortalUtil.getUser(request).getUserId();
			return getCurrentUser(userId);
		} catch (Exception  e) {
			e.printStackTrace();
		}
		return null;		
	}


	public static GCubeUser getUserByUsername(String username) {
		try {
			return new LiferayUserManager().getUserByUsername(username);
		} catch (Exception  e) {
			e.printStackTrace();
		}
		return null;		
	}

	public static GCubeUser getCurrentUser(long userId) {
		try {
			return new LiferayUserManager().getUserById(userId);
		} catch (Exception  e) {
			e.printStackTrace();
		}
		return null;		
	}

	public static String getCurrentContext(ResourceRequest request) {
		long groupId = -1;
		try {
			groupId = PortalUtil.getScopeGroupId(request);
			return getCurrentContext(groupId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getCurrentContext(RenderRequest request) {
		long groupId = -1;
		try {
			groupId = PortalUtil.getScopeGroupId(request);
			return getCurrentContext(groupId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getCurrentContext(HttpServletRequest request) {
		long groupId = -1;
		try {
			groupId = PortalUtil.getScopeGroupId(request);
			return getCurrentContext(groupId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getCurrentContext(long groupId) {
		try {
			PortalContext pContext = PortalContext.getConfiguration(); 
			return pContext.getCurrentScope(""+groupId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * <p>
	 * Returns the gCube authorisation token for the given user 
	 * </p>	
	 * @param scope infrastrucure context (scope)
	 * @param username the GCubeUser username @see {@link GCubeUser}
	 * @return the Token for the user in the context, or <code>null</code> if a token for this user could not be found
	 */
	public static String getCurrentUserToken(String scope, String username) {
		String userToken = null;

		try {
			ScopeProvider.instance.set(scope);
			userToken = authorizationService().resolveTokenByUserAndContext(username, scope);
			SecurityTokenProvider.instance.set(userToken);
		} 
		catch (ObjectNotFound ex) {
			userToken = generateAuthorizationToken(username, scope);
			SecurityTokenProvider.instance.set(userToken);
			_log.debug("generateAuthorizationToken OK for " + username + " in scope " + scope);
		}			 
		catch (Exception e) {
			_log.error("Error while trying to generate token for user " + username + "in scope " + scope);
			e.printStackTrace();
			return null;
		}

		return userToken;
	}
	
	public static String maskId(long idToMask) {
		String toMask = ""+idToMask;
		try {
			return URLEncoder.encode(Base64.getEncoder().encodeToString(toMask.getBytes("utf-8")), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	} 
	
	public static String maskId(String toMask) {
		try {
			return URLEncoder.encode(Base64.getEncoder().encodeToString(toMask.getBytes("utf-8")), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	} 
	
	public static long unmaskId(String idToUnmask) {
		String unmasked = new String(Base64.getDecoder().decode(idToUnmask));
		return Long.parseLong(unmasked);
	}
	
	
	public static List<GCoreEndpoint> getAnalyticalToolkitServiceInstance(String context) throws Exception  {
		String currScope = 	ScopeProvider.instance.get();
		ScopeProvider.instance.set(context);
		SimpleQuery query = queryFor(GCoreEndpoint.class);
		query.addCondition("$resource/Profile/ServiceClass/text() eq '"+ ANALYTICAL_TOOLKIT_SERVICE_GCORE_ENDPOINT_CLASS +"'");
		query.addCondition("$resource/Profile/ServiceName/text() eq '"+ ANALYTICAL_TOOLKIT_SERVICE_GCORE_ENDPOINT_NAME +"'");
		DiscoveryClient<GCoreEndpoint> client = clientFor(GCoreEndpoint.class);
		List<GCoreEndpoint> toReturn = client.submit(query);
		ScopeProvider.instance.set(currScope);
		return toReturn;
	}	
	
	public static String getAnalyticalToolkitEndpoint(String context) {
		List<GCoreEndpoint> analyticalServices = null;
		try {
			analyticalServices = getAnalyticalToolkitServiceInstance(context);
			if (analyticalServices == null || analyticalServices.isEmpty()) {
				return "Cound not find Analytical Toolkit service";
			}	
			GCoreEndpoint endpoint = analyticalServices.get(0);
			Collection<Endpoint> list = endpoint.profile().endpoints().asCollection();

			URI theURI = null;
			for (Endpoint ep : list) {
				if (ep.name().equals(Utils.ANALYTICAL_TOOLKIT_SERVICE_INTERFACE_NAME)) {
					_log.info("Analytical Toolkit GCoreEndpoint: "+ep.uri());
					theURI = ep.uri();
				}
			}
			String endpointSSL = "https://"+theURI.getHost()+theURI.getPath();
			return endpointSSL;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static List<ServiceEndpoint> getDataMinerInstance(String scope) throws Exception  {
		String currScope = 	ScopeProvider.instance.get();
		ScopeProvider.instance.set(scope);
		SimpleQuery query = queryFor(ServiceEndpoint.class);
		query.addCondition("$resource/Profile/Category/text() eq '"+ SERVICE_ENDPOINT_CATEGORY +"'");
		query.addCondition("$resource/Profile/Name/text() eq '"+ SERVICE_ENDPOINT_NAME +"'");
		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
		List<ServiceEndpoint> toReturn = client.submit(query);
		ScopeProvider.instance.set(currScope);
		return toReturn;
	}	

	/**
	 * 
	 * @param username
	 * @param scope
	 * @throws Exception
	 */
	private static String generateAuthorizationToken(String username, String scope) {
		List<String> userRoles = new ArrayList<>();
		userRoles.add(PFISHConstants.DEFAULT_ROLE);
		String token;
		try {
			token = authorizationService().generateUserToken(new UserInfo(username, userRoles), scope);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return token;
	}

	public static String getPhaseByFileName(String fileName) {
		if (fileName.contains(PFISHConstants.SHOW_HATCHERY)) {
			return PFISHConstants.SHOW_HATCHERY;
		}
		else if (fileName.contains(PFISHConstants.SHOW_PRE_ONGROWING)) {
			return PFISHConstants.SHOW_PRE_ONGROWING;
		}
		else 
			return PFISHConstants.SHOW_GROW_OUT;
	}
	
	public static String getBatchTypeName(String phase, String fileName) {
		switch (phase) {
		case PFISHConstants.SHOW_PRE_ONGROWING:
			if (fileName.toLowerCase().contains("close"))
				return PFISHConstants.TEMPLATE_PRE_ONGROWING_CLOSED;
			else
				return PFISHConstants.TEMPLATE_PRE_ONGROWING;
		case PFISHConstants.SHOW_HATCHERY: {
			if (fileName.toLowerCase().contains("aggregated")) {
				if (fileName.toLowerCase().contains("close"))
					return PFISHConstants.TEMPLATE_HATCHERY_AGGREGATED_CLOSED;
				else
					return PFISHConstants.TEMPLATE_HATCHERY_AGGREGATED;
			}
			else {
				if (fileName.toLowerCase().contains("close"))
					return PFISHConstants.TEMPLATE_HATCHERY_INDIVIDUAL_CLOSED;
				else
					return PFISHConstants.TEMPLATE_HATCHERY_INDIVIDUAL;
			}
		}
		case PFISHConstants.SHOW_GROW_OUT: {
			if (fileName.toLowerCase().contains("individual")){
				if (fileName.toLowerCase().contains("close"))
					return PFISHConstants.TEMPLATE_GROWOUT_INDIVIDUAL_CLOSED;
				else
					return PFISHConstants.TEMPLATE_GROWOUT_INDIVIDUAL;
			}
			else {
				if (fileName.toLowerCase().contains("close"))
					return PFISHConstants.TEMPLATE_GROWOUT_AGGREGATED_CLOSED;
				else
					return PFISHConstants.TEMPLATE_GROWOUT_AGGREGATED;
			}
		}
		default:
			return null;
		}
	}

}
