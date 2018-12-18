package org.gcube.portlets.user.performfish;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
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
import org.gcube.portlets.user.performfish.util.PFISHConstants;
import org.gcube.portlets.user.performfish.util.Utils;
import org.gcube.portlets.user.performfish.util.comparators.WSItemComparator;
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
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class CompanyFarmRepository
 */
public class CompanyFarmRepository extends MVCPortlet {
	public static final String PHASE_PREFERENCE_ATTR_NAME = "phase";
	private static com.liferay.portal.kernel.log.Log _log = LogFactoryUtil.getLog(CompanyFarmRepository.class);

	private static RoleManager RM = new LiferayRoleManager();

	@Override
	public void render(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {
		_log.info(" This is render method of CompanyFarmRepository");

		try {
			GCubeTeam theFarm = null;
			HttpServletRequest httpReq = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(request));
			long[] farmIds = ParamUtil.getLongValues(httpReq, "farmId");
			String selectedItemId = (String) request.getAttribute("itemId");
			if (selectedItemId == null) {//this handles when show version is clicked
				if (farmIds == null || farmIds.length == 0) {
					List<GCubeTeam> theFarms = Utils.getUserFarms(request, response, this); //if passes here only one farm is returned
					if (theFarms != null && ! theFarms.isEmpty()) {
						theFarm = theFarms.get(0);
						request.setAttribute("theFarm", theFarms.get(0)); //pass to the JSP
					}
				}
				else { //the farmId is passed via param on the query string
					theFarm = RM.getTeam(farmIds[0]);
					if ( Utils.checkBelongsToTeam(PortalUtil.getUserId(request), theFarm.getTeamId(), PortalUtil.getScopeGroupId(request)) ) //check that the user belong ot the farm
						request.setAttribute("theFarm", theFarm); //pass to the JSP
					else {
						PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher(Utils.NOT_BELONGING_ANY_FARM_PAGE_PATH);
						dispatcher.include(request, response);	
					}					
				}	
				_log.info(" initialise of CompanyFarmRepository Page done");
				long groupId = PortalUtil.getScopeGroupId(request);
				GCubeTeam theCompany = Utils.checkBelongingToOneCompanyOnly(request, response, this);
				request.setAttribute("theCompany", theCompany);
				if (theFarm != null ) {
					//check folder exists
					WorkspaceFolder sharedFolder = Utils.getWSFarmFolder(
							Utils.getCurrentUser(request).getUsername(), 
							Utils.getCurrentContext(request), 
							theCompany, theFarm);
					List<WorkspaceItem> companyFiles = sharedFolder.getChildren(true);
					Collections.sort(companyFiles, new WSItemComparator());
					List<ExternalFile> filteredFiles = new ArrayList<>();
					PortletPreferences portletPreferences = request.getPreferences();
					String selectedPhase = GetterUtil.getString(portletPreferences.getValue(PHASE_PREFERENCE_ATTR_NAME, StringPool.BLANK));
					if (selectedPhase.compareTo(PFISHConstants.SHOW_ALL_PHASES) != 0) {
						for (WorkspaceItem workspaceItem : companyFiles) {
							if (workspaceItem.getName().startsWith(selectedPhase)) {
								if (workspaceItem instanceof ExternalFile) {
									ExternalFile file = (ExternalFile) workspaceItem;
									filteredFiles.add(file);
								}
							}
						}
						request.setAttribute("companyFiles", filteredFiles); //pass to the JSP
					}
					else {
						for (WorkspaceItem workspaceItem : companyFiles) {
							if (workspaceItem instanceof ExternalFile) {
								ExternalFile file = (ExternalFile) workspaceItem;
								filteredFiles.add(file);
							}
						}
						request.setAttribute("companyFiles", filteredFiles); //pass to the JSP
					}

					String imageUrl = Utils.getCompanyLogoURL(theCompany.getTeamName(), groupId, request);
					request.setAttribute("companyLogoURL", imageUrl); //pass to logo URL to the JSP
				} 
			} 
			super.render(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void displayVersions(ActionRequest request, ActionResponse response) throws Exception {
		System.out.println("**** displayVersionsdisplayVersionsdisplayVersionsdisplayVersions");
		String itemId = ParamUtil.getString(request, "fileItem");
		long farmId = ParamUtil.getLong(request, "farmId");

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
		request.setAttribute("farmId", farmId);
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
