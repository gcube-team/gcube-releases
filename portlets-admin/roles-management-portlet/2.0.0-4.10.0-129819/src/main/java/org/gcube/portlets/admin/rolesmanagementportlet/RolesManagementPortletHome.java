package org.gcube.portlets.admin.rolesmanagementportlet;

import java.io.IOException;
import java.util.List;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.RoleManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.exception.RoleRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayRoleManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeRole;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;

public class RolesManagementPortletHome extends GenericPortlet {
	protected String viewTemplate;
	protected boolean once = true;
	protected RoleManager rm = new LiferayRoleManager();
	protected UserManager um = new LiferayUserManager();
	protected GroupManager gm = new LiferayGroupManager();
	private static Log _log = LogFactoryUtil.getLog(RolesManagementPortletHome.class);
	private static final int RETRIEVE_ROLES = 0;
	private static final int EDIT_ROLE = 1;
	private static final int DELETE_ROLE = 2;
	private static final int ADD_ROLE = 3;
	
	public void init() {
		viewTemplate = getInitParameter("view-template");
	}
	
	public void doView(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException {
		include(viewTemplate, renderRequest, renderResponse);
	}
	
	protected void include(String path, RenderRequest renderRequest, RenderResponse renderResponse) throws IOException,PortletException { 
		PortletRequestDispatcher portletRequestDispatcher = getPortletContext().getRequestDispatcher(path);
		
		if (portletRequestDispatcher == null) {
		_log.error(path + " is not a valid include");
		}
		else {
			ScopeHelper.setContext(renderRequest); // <-- Static method which sets the username in the session and the scope depending on the context automatically	
			portletRequestDispatcher.include(renderRequest, renderResponse);
		}
	}
	
	@Override
	public void serveResource(ResourceRequest request, ResourceResponse response) throws PortletException, IOException {
		PortletRequest portletRequest = (PortletRequest)request.getAttribute("javax.portlet.request");
		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();
		ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
//		System.out.println(themeDisplay.getScopeGroupId());
		
		int mode = ParamUtil.getInteger(request, "mode");
		long roleID = ParamUtil.getLong(request, "roleID");
		String roleName = ParamUtil.getString(request, "roleName");
		String roleDescription = ParamUtil.getString(request, "roleDescription");
		_log.info("mode: " + mode);
		switch (mode) {
			case RETRIEVE_ROLES:
				jsonObject.put("groupRoles",retrieveAllRoles());
				break;
			case EDIT_ROLE:
				jsonObject.put("editRole",editRole(roleID, roleName, roleDescription));
				break;
			case DELETE_ROLE:
				jsonObject.put("deleteRole",deleteRole(roleID));
				break;
			case ADD_ROLE:
				jsonObject.put("addRole",createRole(roleName, roleDescription));
				break;
			default:
				break;
		}
		
		response.getWriter().println(jsonObject);
		super.serveResource(request, response);
	}
	
	public JSONArray retrieveAllRoles(){
		JSONArray ja = JSONFactoryUtil.createJSONArray();
		List<GCubeRole> groupRoles = rm.listAllGroupRoles();
		for(GCubeRole gcr : groupRoles){
			JSONObject joRole = JSONFactoryUtil.createJSONObject();
			joRole.put("name", gcr.getRoleName());
			joRole.put("description", gcr.getDescription());
			joRole.put("roleID", gcr.getRoleId());
			JSONObject jo = JSONFactoryUtil.createJSONObject();
			jo.put("role", joRole);
			ja.put(jo);
		}
		
		return ja;
	}
	
	public boolean editRole(long roleID, String roleName, String  roleDescription) {
		
		GCubeRole gcr;
		String formerRoleName = "";
		String formerDescription = "";
		try {
			gcr = rm.getRole(roleID);
			formerRoleName = gcr.getRoleName();
			formerDescription = gcr.getDescription();
		} catch (UserManagementSystemException | RoleRetrievalFault e1) {
			e1.printStackTrace();
		}
		
		try {
			rm.updateRole(roleID, roleName, roleDescription);
			_log.info("The role " + formerRoleName + " with description: " + formerDescription + " changed to " + roleName + " and description: " + roleDescription);
			return true;
		} catch (RoleRetrievalFault e) {
			_log.info("The role " + formerRoleName + " with description: " + formerDescription + " failed to change to " + roleName + " and description: " + roleDescription);
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean deleteRole(long roleID) {
		String roleName = "";
		String roleDescription = "";
		try {
			GCubeRole gcr = rm.getRole(roleID);
			roleName = gcr.getRoleName();
			roleDescription = gcr.getDescription();
		} catch (UserManagementSystemException | RoleRetrievalFault e1) {
			e1.printStackTrace();
		}
		
		try {
			rm.deleteRole(roleID);
			_log.info("Role: " + roleName + " with description: " + roleDescription + " was deleted successfully.");
			return true;
		} catch (RoleRetrievalFault | UserManagementSystemException e) {
			_log.info("Deletion failed for role with name: " + roleName + " and description: " + roleDescription);
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean createRole(String roleName, String roleDescription){
		try {
			rm.createRole(roleName, roleDescription);
			_log.info("New role with name: " + roleName + " and description: " + roleDescription + " was created successfully");
			return true;
		} catch (UserManagementSystemException e) {
			_log.info("Failed to create new role with name: " + roleName + " and description: " + roleDescription);
			e.printStackTrace();
			return false;
		}
	}
	
}