package org.gcube.portlets.user.performfish;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.homelibary.model.versioning.WorkspaceVersion;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
import org.gcube.common.storagehub.client.StreamDescriptor;
import org.gcube.common.storagehub.client.plugins.AbstractPlugin;
import org.gcube.common.storagehub.client.proxies.ItemManagerClient;
import org.gcube.portlets.user.performfish.bean.Farm;
import org.gcube.portlets.user.performfish.util.PFISHConstants;
import org.gcube.portlets.user.performfish.util.Utils;
import org.gcube.portlets.user.performfish.util.comparators.WSItemComparator;
import org.gcube.portlets.user.performfish.util.db.DBUtil;
import org.gcube.portlets.user.performfish.util.db.DatabaseConnection;
import org.gcube.vomanagement.usermanagement.RoleManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayRoleManager;
import org.gcube.vomanagement.usermanagement.model.GCubeTeam;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;

import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.service.TeamLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class CompanyPrivateRepository
 */
public class CompanyPrivateRepository extends MVCPortlet {

	private static com.liferay.portal.kernel.log.Log _log = LogFactoryUtil.getLog(CompanyFarmRepository.class);
	@Override
	public void render(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {
		_log.info(" This is render method of CompanyPrivateRepository");

		try {
			_log.info(" initialise of CompanyPrivateRepository done");

			long groupId = PortalUtil.getScopeGroupId(request);
			GCubeTeam theCompany = Utils.checkBelongingToOneCompanyOnly(request, response, this);
			boolean isCompanyAdmin = Utils.isCompanyAdmin(	Utils.getCurrentUser(request), groupId, theCompany);
			if (theCompany != null && isCompanyAdmin) { //check is Admin
				request.setAttribute("theCompany", theCompany);
				String currentContext = Utils.getCurrentContext(request);
				GCubeUser currentUser = Utils.getCurrentUser(request);
				RoleManager rm = new LiferayRoleManager();
				//check folder exists
				Utils.getWSCompanyFolder(currentUser.getUsername(), currentContext, theCompany);
				//look for company farms
				Connection conn = DatabaseConnection.getInstance(Utils.getCurrentContext(groupId)).getConnection();
				List<Farm> companyFarms = DBUtil.listFarmsByCompanyId(conn, theCompany.getTeamId());
				LinkedHashMap<Farm, List<ExternalFile>> theFiles = new LinkedHashMap<>();
				for (Farm farm : companyFarms) {
					GCubeTeam theFarm = rm.getTeam(farm.getFarmId());
					farm.setName(theFarm.getTeamName());
					WorkspaceFolder farmFolder = Utils.getWSFarmFolder(
							currentUser.getUsername(), 
							currentContext, 
							theCompany, theFarm);
					List<WorkspaceItem> farmItems = farmFolder.getChildren(true);
					Collections.sort(farmItems, new WSItemComparator());
					List<ExternalFile> farmFiles = new ArrayList<>();
					for (WorkspaceItem workspaceItem : farmItems) {
						if (workspaceItem instanceof ExternalFile) {
							ExternalFile file = (ExternalFile) workspaceItem;
							farmFiles.add(file);
						}
					}
					theFiles.put(farm, farmFiles);
				}
				request.setAttribute("companyPrivateFiles", theFiles); //pass to the JSP
				String imageUrl = Utils.getCompanyLogoURL(theCompany.getTeamName(), groupId, request);
				request.setAttribute("companyLogoURL", imageUrl); //pass to logo URL to the JSP
				//display the view.jsp
				super.render(request, response);
			}
			else {
				PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher(Utils.NOT_ADMIN_PAGE_PATH);
				dispatcher.include(request, response);		
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void displayVersions(ActionRequest request, ActionResponse response) throws Exception {
		String itemId = ParamUtil.getString(request, "fileItem");
		if (itemId == null || itemId.compareTo("")==0) {
			SessionErrors.add(request.getPortletSession(),"form-error");
			return;
		}
		HttpServletRequest httpReq = PortalUtil.getHttpServletRequest(request);
		GCubeUser currentUser = Utils.getCurrentUser(httpReq);
		String context = Utils.getCurrentContext(httpReq);
		Workspace ws = Utils.getWS(currentUser.getUsername(), context) ;
		WorkspaceItem theItem = ws.getItem(itemId);
		if (theItem instanceof ExternalFile) {
			ExternalFile file = (ExternalFile) theItem;	
			List<WorkspaceVersion> versions = file.getVersionHistory();
			request.setAttribute("versions", versions); //pass to the JSP
		} else { 
			response.setRenderParameter("jspPage", PFISHConstants.OPERATION_ERROR_PATH);
		}
		request.setAttribute("itemId", itemId);
		request.setAttribute("itemName", theItem.getName());
		response.setWindowState(WindowState.MAXIMIZED);
		response.setRenderParameter("jspPage", "/html/farmrepository/show_all_versions.jsp");
	}


	public void serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse) throws IOException, PortletException {
		//if download file
		String fileToDownloadId = ParamUtil.getString(resourceRequest, "fileToDownloadId", null);
		//if download version
		String versionDownloadItemId = ParamUtil.getString(resourceRequest, "versionDownloadItemId", null);
		String versionDownloadName = ParamUtil.getString(resourceRequest, "versionDownloadName", null);

		setAuthorizationToken(resourceRequest);
		//check if is a file download
		if (fileToDownloadId != null) { 	
			String selectedItemId =  fileToDownloadId;
			ItemManagerClient client = AbstractPlugin.item().build();
			StreamDescriptor streamDescr = client.download(selectedItemId);
			HttpServletResponse httpRes = PortalUtil.getHttpServletResponse(resourceResponse);
			HttpServletRequest httpReq = PortalUtil.getHttpServletRequest(resourceRequest);
			ServletResponseUtil.sendFile(httpReq,httpRes, streamDescr.getFileName(), streamDescr.getStream(), "application/download");
			streamDescr.getStream().close();
		}
		else if (versionDownloadItemId != null && versionDownloadName != null) {	//check if download version
			GCubeUser currentUser = Utils.getCurrentUser(resourceRequest);
			String context = Utils.getCurrentContext(resourceRequest);
			Workspace ws = Utils.getWS(currentUser.getUsername(), context) ;
			WorkspaceItem theItem;
			try {
				theItem = ws.getItem(versionDownloadItemId);
				if (theItem instanceof ExternalFile) {
					ExternalFile file = (ExternalFile) theItem;	
					InputStream is = file.downloadVersion(versionDownloadName);
					HttpServletResponse httpRes = PortalUtil.getHttpServletResponse(resourceResponse);
					HttpServletRequest httpReq = PortalUtil.getHttpServletRequest(resourceRequest);
					ServletResponseUtil.sendFile(httpReq,httpRes, versionDownloadName+"_"+file.getName(), is, "application/download");
				} 
			} catch (ItemNotFoundException | InternalErrorException e) {
				e.printStackTrace();
			}

		}

	}

	/**
	 * set the authorization token in Thread local and return the current {@link AuthorizedUser} instance
	 * @param request
	 * @return
	 */
	private void setAuthorizationToken(ResourceRequest request) {
		GCubeUser currentUser = Utils.getCurrentUser(request);
		String scope = Utils.getCurrentContext(request);
		String authorizationToken = Utils.getCurrentUserToken(scope,  currentUser.getUsername());
		SecurityTokenProvider.instance.set(authorizationToken);
	}

}
