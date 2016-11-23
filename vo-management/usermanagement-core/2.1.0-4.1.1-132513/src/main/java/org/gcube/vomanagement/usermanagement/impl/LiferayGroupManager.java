package org.gcube.vomanagement.usermanagement.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementNameException;
import org.gcube.vomanagement.usermanagement.exception.UserManagementPortalException;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.VirtualGroupNotExistingException;
import org.gcube.vomanagement.usermanagement.model.CustomAttributeKeys;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;
import org.gcube.vomanagement.usermanagement.model.GCubeRole;
import org.gcube.vomanagement.usermanagement.model.GroupMembershipType;
import org.gcube.vomanagement.usermanagement.model.VirtualGroup;
import org.gcube.vomanagement.usermanagement.util.ManagementUtils;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.GroupConstants;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.security.auth.PrincipalThreadLocal;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.security.permission.PermissionThreadLocal;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.LayoutSetLocalServiceUtil;
import com.liferay.portal.service.RoleServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.webserver.WebServerServletTokenUtil;
import com.liferay.portlet.expando.model.ExpandoBridge;
import com.liferay.portlet.expando.util.ExpandoBridgeFactoryUtil;

public class LiferayGroupManager implements GroupManager {
	/**
	 * logger
	 */
	private static final Log _log = LogFactoryUtil.getLog(LiferayGroupManager.class);
	// group mapping
	private GCubeGroup mapLRGroup(Group g) throws PortalException, SystemException, UserManagementSystemException, GroupRetrievalFault {
		if (g != null) {
			long logoId = LayoutSetLocalServiceUtil.getLayoutSet(g.getGroupId(), true).getLogoId();
			if (isVRE(g.getGroupId())) {
				return new GCubeGroup(g.getGroupId(), g.getParentGroupId(), g.getName(), g.getDescription(), g.getFriendlyURL(), logoId, null, getMappedGroupMembershipType(g.getType()));
			}
			else if (isVO(g.getGroupId())) {
				List<GCubeGroup> vres = new ArrayList<GCubeGroup>();
				List<Group> VREs = g.getChildren(true);
				for (Group vre : VREs) {
					vres.add(mapLRGroup(vre));
				}
				return new GCubeGroup(g.getGroupId(), g.getParentGroupId(), g.getName(), g.getDescription(), g.getFriendlyURL(), logoId, vres, getMappedGroupMembershipType(g.getType()));
			} else if (isRootVO(g.getGroupId())) {
				List<GCubeGroup> vos = new ArrayList<GCubeGroup>();
				List<Group> children = g.getChildren(true);
				for (Group vo : children) 
					vos.add(mapLRGroup(vo));
				return new GCubeGroup(g.getGroupId(), -1, g.getName(), g.getDescription(), g.getFriendlyURL(), logoId, vos, getMappedGroupMembershipType(g.getType()));
			} else{
				_log.warn("This groupId does not correspond to a VO ora VRE");
				return null;
			}
		}
		else 
			return null;
	}
	/**
	 * 
	 * @param type
	 * @return the correspondent mapping to the gcube model
	 */
	private GroupMembershipType getMappedGroupMembershipType(int type) {
		switch (type) {
		case GroupConstants.TYPE_SITE_RESTRICTED:
			return GroupMembershipType.RESTRICTED;
		case GroupConstants.TYPE_SITE_OPEN:
			return GroupMembershipType.OPEN;
		default:
			return GroupMembershipType.PRIVATE;
		}
	}
	/**
	 * 
	 * @param groupName
	 * @param description
	 * @param parentGroupId
	 * @return
	 */
	private Group createGroup(String groupName, String description, long parentGroupId) {
		Group group = null;
		if (parentGroupId < 0)
			parentGroupId = GroupConstants.DEFAULT_PARENT_GROUP_ID;
		try {
			//get the userId for the default user
			final long companyId = PortalUtil.getDefaultCompanyId();
			long defaultUserId = UserLocalServiceUtil.getDefaultUserId(companyId);
			group = GroupLocalServiceUtil.addGroup(defaultUserId, 
					parentGroupId, 
					Group.class.getName(), 0, 
					GroupConstants.DEFAULT_LIVE_GROUP_ID, 
					groupName, 
					description, 
					GroupConstants.TYPE_SITE_RESTRICTED, 
					true, 
					GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION, "/" + groupName, true, true, 					
					new ServiceContext());
			_log.info("Created Group with name " + groupName);
			return group;
		} catch (PortalException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return group;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public VirtualGroup getVirtualGroup(long actualGroupId) throws GroupRetrievalFault, VirtualGroupNotExistingException {
		VirtualGroup toReturn = new VirtualGroup();
		try {
			long userId = LiferayUserManager.getAdmin().getUserId();
			PrincipalThreadLocal.setName(userId);
			PermissionChecker permissionChecker = PermissionCheckerFactoryUtil.create(UserLocalServiceUtil.getUser(userId));
			PermissionThreadLocal.setPermissionChecker(permissionChecker); 
			Group site = GroupLocalServiceUtil.getGroup(actualGroupId);
			_log.debug("Set Thread Permission done, getVirtual Group of " + site.getName());
			if (site.getExpandoBridge().getAttribute(CustomAttributeKeys.VIRTUAL_GROUP.getKeyName()) == null ||  site.getExpandoBridge().getAttribute(CustomAttributeKeys.VIRTUAL_GROUP.getKeyName()).equals("")) {
				String warningMessage = String.format("Attribute %s not initialized.", CustomAttributeKeys.VIRTUAL_GROUP.getKeyName());
				_log.warn(warningMessage); 
				throw new VirtualGroupNotExistingException(warningMessage);
			} else {
				String[] values = (String[]) site.getExpandoBridge().getAttribute(CustomAttributeKeys.VIRTUAL_GROUP.getKeyName());  
				if (values != null && values.length > 0) {
					String[] splits = values[0].split("\\|");
					toReturn.setName(splits[0]);
					toReturn.setDescription(splits[1]);
				} else {
					toReturn.setName("NoVirtualGroupAssigned");
					toReturn.setDescription("NoVirtualGroupDescription");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return toReturn;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<VirtualGroup> getVirtualGroups() throws VirtualGroupNotExistingException {
		ExpandoBridge expandoBridge = null;
		List<VirtualGroup> toReturn = new ArrayList<VirtualGroup>();
		try {
			expandoBridge = ExpandoBridgeFactoryUtil.getExpandoBridge(ManagementUtils.getCompany().getCompanyId(), Group.class.getName());
			String[] groups = (String[]) expandoBridge.getAttributeDefault(CustomAttributeKeys.VIRTUAL_GROUP.getKeyName());	
			List<String> virtualGroups = Arrays.asList(groups);

			for (String vg : virtualGroups) {
				String[] splits = vg.split("\\|");
				String gName = splits[0];
				String gDescription = splits[1];
				toReturn.add(new VirtualGroup(gName, gDescription));
			}
		} catch (PortalException e) {
			throw new VirtualGroupNotExistingException("", e);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return toReturn;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public GCubeGroup createRootVO(String rootVOName, String description) throws UserManagementNameException,	UserManagementSystemException, UserRetrievalFault, GroupRetrievalFault, UserManagementPortalException {
		Group group = null;
		try {
			group = createGroup(rootVOName, description, -1);
			_log.info("Created RootVO with name " + rootVOName);
			return mapLRGroup(group);
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
	public GCubeGroup createVO(String virtualOrgName, long rootVOGroupId, String description) throws UserManagementNameException,
	UserManagementSystemException, UserRetrievalFault,
	GroupRetrievalFault, UserManagementPortalException {
		Group group = null;
		try {
			group = createGroup(virtualOrgName, description, rootVOGroupId);
			_log.info("Created VO with name " + virtualOrgName);
			return mapLRGroup(group);
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
	public GCubeGroup createVRE(String virtualResearchEnvName, long virtualOrgGroupId, String description) throws UserManagementNameException,
	UserManagementSystemException, UserRetrievalFault,
	GroupRetrievalFault, UserManagementPortalException {
		Group group = null;
		try {
			group = createGroup(virtualResearchEnvName, description, virtualOrgGroupId);
			_log.info("Created VO with name " + virtualResearchEnvName);
			return mapLRGroup(group);
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
	public long getGroupParentId(long groupId) throws UserManagementSystemException, GroupRetrievalFault {
		try {
			GroupLocalServiceUtil.getGroup(groupId).getParentGroupId();
		} catch (PortalException e) {
			throw new GroupRetrievalFault("Group not existing ", e);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return -1;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getGroupId(String groupName) throws UserManagementSystemException, GroupRetrievalFault {
		_log.debug("looking for groupId of " + groupName);
		Group g;
		try {
			g = GroupLocalServiceUtil.getGroup(ManagementUtils.getCompany().getCompanyId(), groupName);
			return g.getGroupId();
		} catch (PortalException e) {
			_log.warn(groupName + " Group not existing -> "+ groupName);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return -1;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public GCubeGroup getGroup(long groupId) throws UserManagementSystemException, GroupRetrievalFault {
		_log.debug("looking for group having id " + groupId);
		Group g;
		try {
			g = GroupLocalServiceUtil.getGroup(groupId);
			return mapLRGroup(g);
		} catch (PortalException e) {
			throw new GroupRetrievalFault("Group not existing", e);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * {@inheritDoc}
	 * @throws GroupRetrievalFault 
	 * @throws UserManagementSystemException 
	 */
	@Override
	public long getGroupIdFromInfrastructureScope(String scope) throws IllegalArgumentException, UserManagementSystemException, GroupRetrievalFault {
		_log.debug("called getGroupIdFromInfrastructureScope on " + scope);
		if (! scope.startsWith("/")) {
			throw new IllegalArgumentException("Scope should start with '/' ->" + scope);
		}
		if (scope.endsWith("/")) {
			throw new IllegalArgumentException("Scope should not end with '/' ->" + scope);
		}
		String[] splits = scope.split("/");
		if (splits.length > 4)
			throw new IllegalArgumentException("Scope is invalid, too many '/' ->" + scope);
		if (splits.length == 2) //is a root VO 
			return getGroupId(splits[1]);
		else if (splits.length == 3) {//is a VO 
			long parentGroupId = getGroupId(splits[1]);
			List<Group> vos = null;
			try {
				vos = GroupLocalServiceUtil.getGroups(ManagementUtils.getCompany().getCompanyId(), parentGroupId, true);
			} catch (SystemException e) {
				e.printStackTrace();
			} catch (PortalException e) {
				e.printStackTrace();
			}
			for (Group group : vos) {
				if (group.getName().compareTo(splits[2])==0) 
					return group.getGroupId();
			}
		}
		else if (splits.length == 4) {//is a VRE 
			_log.debug("is a VRE scope " + scope);
			long parentGroupId = getGroupId(splits[2]);
			List<Group> vres = null;
			try {
				vres = GroupLocalServiceUtil.getGroups(ManagementUtils.getCompany().getCompanyId(), parentGroupId, true);
			} catch (SystemException e) {
				e.printStackTrace();
			} catch (PortalException e) {
				e.printStackTrace();
			}
			for (Group group : vres) {
				if (group.getName().compareTo(splits[3])==0) {
					long groupId = group.getGroupId();
					_log.debug("groupId found: " + groupId);
					return groupId;
				}
			}
		}
		return -1;		
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public GCubeGroup getRootVO() throws UserManagementSystemException, GroupRetrievalFault {
		String infraName = readInfrastructureNameFromPropertyfile();
		long rootVOGroupId = getGroupId(infraName);
		return getGroup(rootVOGroupId);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getRootVOName() throws UserManagementSystemException,	GroupRetrievalFault {
		return  getRootVO().getGroupName();
	}
	/**
	 * {@inheritDoc}
	 * @throws GroupRetrievalFault 
	 */
	@Override
	public List<GCubeGroup> listGroups() throws UserManagementSystemException, GroupRetrievalFault {
		List<GCubeGroup> toReturn = new ArrayList<GCubeGroup>();
		GCubeGroup root = getRootVO();
		toReturn.add(root);
		try {
			List<Group> VOs = GroupLocalServiceUtil.getGroup(root.getGroupId()).getChildren(true);
			for (Group vo : VOs) {
				toReturn.add(mapLRGroup(vo));
				List<Group> VREs = vo.getChildren(true);
				for (Group vre : VREs) {
					toReturn.add(mapLRGroup(vre));
				}
			}
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
	public List<GCubeGroup> listGroupsByUser(long userId) throws UserRetrievalFault, UserManagementSystemException, GroupRetrievalFault {
		List<GCubeGroup> toReturn = new ArrayList<GCubeGroup>();
		try {
			for (Group g : GroupLocalServiceUtil.getUserGroups(userId)) {
				toReturn.add(mapLRGroup(g));
			}
		} catch (SystemException e) {
			throw new UserManagementSystemException("Please check that the userId exists", e);
		} catch (PortalException e) {
			e.printStackTrace();
		}
		return toReturn;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<GCubeGroup> listGroupsByUserAndSite(long userId, final String serverName) throws UserRetrievalFault, UserManagementSystemException, GroupRetrievalFault, VirtualGroupNotExistingException {
		Set<GCubeGroup> toReturn = new HashSet<>();
		try {
			List<VirtualGroup> currSiteVirtualGroups = ManagementUtils.getVirtualGroupsBySiteGroupId(ManagementUtils.getSiteGroupIdFromServletRequest(serverName));
			for (GCubeGroup userGroup : listGroupsByUser(userId)) {
				if (isVRE(userGroup.getGroupId())) {
					for (VirtualGroup vg : currSiteVirtualGroups) 
						if (getVirtualGroup(userGroup.getGroupId()).getName().compareTo(vg.getName()) == 0) 
							toReturn.add(userGroup);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return toReturn;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<GCubeGroup, List<GCubeRole>> listGroupsAndRolesByUser(long userId) throws UserManagementSystemException {
		Map<GCubeGroup, List<GCubeRole>> toReturn = new HashMap<GCubeGroup, List<GCubeRole>>();
		try {
			List<Group> userGroups = GroupLocalServiceUtil.getUserGroups(userId);
			for (Group group : userGroups) {
				//doAsAdmin();
				List<Role> userRoles = RoleServiceUtil.getUserGroupRoles(userId, group.getGroupId());
				List<GCubeRole> toAdd = new ArrayList<GCubeRole>();
				for (Role role : userRoles) {
					toAdd.add(LiferayRoleManager.mapLRRole(role));
				}
				toReturn.put(mapLRGroup(group), toAdd);
			}
		} catch (SystemException e) {
			throw new UserManagementSystemException("Error in listGroupsAndRolesByUser", e);
		} catch (PortalException e) {
			e.printStackTrace();
		} catch (GroupRetrievalFault e) {
			e.printStackTrace();
		}
		return toReturn;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean isRootVO(long groupId) throws UserManagementSystemException, GroupRetrievalFault {
		try {
			Group g = GroupLocalServiceUtil.getGroup(groupId);
			return (g.getParentGroup() == null);
		} catch (PortalException e1) {
			throw new GroupRetrievalFault("Group not existing (I think you better check)", e1);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return false;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean isVO(long groupId) throws UserManagementSystemException,	GroupRetrievalFault {
		try {
			Group g = GroupLocalServiceUtil.getGroup(groupId);
			if (g.getParentGroup() != null) {
				return !isVRE(groupId);
			}
		} catch (PortalException e1) {
			throw new GroupRetrievalFault("Group not existing (I think you better check)", e1);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return false;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean isVRE(long groupId) throws UserManagementSystemException, GroupRetrievalFault {
		try {
			Group g = GroupLocalServiceUtil.getGroup(groupId);
			if (g.getParentGroup() != null) {
				return (g.getParentGroup().getParentGroup() != null); 
			}
		} catch (PortalException e1) {
			throw new GroupRetrievalFault("Group not existing (I think you better check)", e1);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return false;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getInfrastructureScope(long groupId)	throws UserManagementSystemException, GroupRetrievalFault {
		try {
			Group g = GroupLocalServiceUtil.getGroup(groupId);
			if (isVRE(groupId)) 
				return "/" + g.getParentGroup().getParentGroup().getName() + "/" + g.getParentGroup().getName() + "/" + g.getName();
			if (isVO(groupId)) 
				return "/" + g.getParentGroup().getName() + "/" + g.getName();
			if (isRootVO(groupId)) 
				return "/"+g.getName();

		} catch (PortalException e1) {
			throw new GroupRetrievalFault("Group not existing (I think you better check)", e1);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
	}
	/**
	 * {@inheritDoc}
	 * @deprecated 
	 * please use getInfrastructureScope(long groupId)
	 */
	@Override
	public String getScope(long groupId) throws UserManagementSystemException, GroupRetrievalFault {
		return getInfrastructureScope(groupId);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Serializable readCustomAttr(long groupId, String attributeKey) throws GroupRetrievalFault {
		try {
			doAsAdmin();
			Group g = GroupLocalServiceUtil.getGroup(groupId);
			if (g.getExpandoBridge().hasAttribute(attributeKey)) {
				_log.trace("Attribute found: " + attributeKey + " trying read value");
				return g.getExpandoBridge().getAttribute(attributeKey);
			} else
				return null;
		} catch (PortalException e1) {
			throw new GroupRetrievalFault("Group not existing (I think you better check)", e1);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void saveCustomAttr(long groupId, String attributeKey, Serializable value) throws GroupRetrievalFault {
		try {
			doAsAdmin();
			Group g = GroupLocalServiceUtil.getGroup(groupId);
			g.getExpandoBridge().setAttribute(attributeKey, value);
			_log.trace("Custom Attribute set OK: " + attributeKey + " value: " + value);
		} catch (PortalException e1) {
			throw new GroupRetrievalFault("Group not existing (I think you better check)", e1);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String updateGroupDescription(long groupId, String description) throws GroupRetrievalFault {
		try {
			Group g = GroupLocalServiceUtil.getGroup(groupId);
			Group modifiedGroup = GroupLocalServiceUtil.updateGroup(
					groupId, 
					g.getParentGroupId(), 
					g.getName(), 
					description, 
					g.getType(), 
					g.getManualMembership(), 
					g.getMembershipRestriction(), 
					g.getFriendlyURL(), 
					g.isActive(), 
					new ServiceContext());
			return modifiedGroup.getDescription();
		} catch (PortalException e1) {
			throw new GroupRetrievalFault("Group not existing (I think you better check)", e1);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
	}
	/**
	 * this method sets the Admin privileges in the local thread, needed to perform such operations.
	 */
	private void doAsAdmin() {
		try {			
			User admin = LiferayUserManager.getAdmin();
			_log.debug("Admin found: " + admin.getScreenName());
			long userId = admin.getUserId();
			PrincipalThreadLocal.setName(userId);
			PermissionChecker permissionChecker = PermissionCheckerFactoryUtil.create(UserLocalServiceUtil.getUser(userId));
			PermissionThreadLocal.setPermissionChecker(permissionChecker); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static final String DEFAULT_INFRA_NAME = "gcube";
	public static final String INFRASTRUCTURE_NAME = "infrastructure";
	/**
	 * read the infrastructure name from a property file and returns it
	 */
	private static String readInfrastructureNameFromPropertyfile() {
		Properties props = new Properties();
		String toReturn = DEFAULT_INFRA_NAME;
		try {
			String propertyfile = getCatalinaHome() + File.separator + "conf" + File.separator + "infrastructure.properties";			
			File propsFile = new File(propertyfile);
			FileInputStream fis = new FileInputStream(propsFile);
			props.load( fis);
			toReturn  = props.getProperty(INFRASTRUCTURE_NAME);
		}
		catch(IOException e) {
			_log.error("infrastructure.properties file not found under $CATALINA_HOME/conf/ dir, returning default infrastructure Name " + toReturn);
			return toReturn;
		}		
		return toReturn;
	}
	/**
	 * 
	 * @return $CATALINA_HOME
	 */
	private static String getCatalinaHome() {
		return (System.getenv("CATALINA_HOME").endsWith("/") ? System.getenv("CATALINA_HOME") : System.getenv("CATALINA_HOME")+"/");
	}
	/**
	 * {@inheritDoc}
	 */
	public String getGroupLogoURL(long logoId) {
		String layoutSetLogo = "";
		StringBundler sb = new StringBundler(5);
		String imagePath=PortalUtil.getPathImage();
		sb.append(imagePath);
		sb.append("/layout_set_logo?img_id=");
		sb.append(logoId);
		sb.append("&t=");
		sb.append(WebServerServletTokenUtil.getToken(logoId));
		layoutSetLogo = sb.toString();
		return layoutSetLogo;
	}


}
