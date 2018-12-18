package org.gcube.portlets.user.performfish;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
import org.gcube.common.homelibrary.util.WorkspaceUtil;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.user.performfish.util.PFISHConstants;
import org.gcube.portlets.user.performfish.util.Utils;
import org.gcube.vomanagement.usermanagement.RoleManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayRoleManager;
import org.gcube.vomanagement.usermanagement.model.GCubeTeam;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.upload.FileItem;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class EmptyFormsDocumentsDisplay
 */
public class EmptyFormsDocumentsDisplay extends MVCPortlet {
	private static com.liferay.portal.kernel.log.Log _log = LogFactoryUtil.getLog(EmptyFormsDocumentsDisplay.class);

	public static final String FOLDERID_PREFERENCE_ATTR_NAME = "folderId";
	private final static String RESOURCE_URL_ID = "uploadFiles";
	private final static String UPLOADED_FILE_ATTR_NAME = "myfile";
	private static String XLS_MIMETYPE = "application/vnd.ms-excel";
	private static String XLSX_MIMETYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
	public static final String ATTACHMENT_FOLDER ="_uploaded_forms";
	private static RoleManager RM = new LiferayRoleManager();
	@Override
	public void render(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {
		_log.info(" This is render method of EmptyFormsDocumentsDisplay");
		try {
			HttpServletRequest httpReq = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(request));
			long[] farmIds = ParamUtil.getLongValues(httpReq, "farmId");
			GCubeTeam theFarm = null;
			if (farmIds == null || farmIds.length == 0 ) {
				if (Utils.getUserFarmsNumber(request, response, this) < 2) {
					theFarm = Utils.checkBelongingToOneFarmOnly(request, response, this);
					request.setAttribute("theFarm", theFarm); //pass to the JSP
				}					
			}
			else { //the farmId is passed via param on the query string
				theFarm = RM.getTeam(farmIds[0]);
				if (Utils.checkBelongsToTeam(PortalUtil.getUserId(request), theFarm.getTeamId(), PortalUtil.getScopeGroupId(request)) ) {//check that the user belong ot the farm
					request.setAttribute("theFarm", theFarm); //pass to the JSP
				}
				else {
					PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher(Utils.NOT_BELONGING_ANY_FARM_PAGE_PATH);
					dispatcher.include(request, response);	
				}					
			}
			if (theFarm != null) {
				setGuestAuthorizationToken(request);
				PortletPreferences portletPreferences = request.getPreferences();
				String folderId = GetterUtil.getString(portletPreferences.getValue(FOLDERID_PREFERENCE_ATTR_NAME, StringPool.BLANK));
				if (folderId != null && !folderId.equals("")) {
					String context = Utils.getCurrentContext(request);
					Workspace ws = Utils.getWS("guest", context) ;
					String selectedPhase = GetterUtil.getString(portletPreferences.getValue(PFISHConstants.PHASE_PREFERENCE_ATTR_NAME, StringPool.BLANK));
					//in theChildren expected to find 3 subfolders: Utils.SHOW_HATCHERY, SHOW_PRE_ONGROWING ..
					for (WorkspaceItem item : ws.getItem(folderId).getChildren()) {
						if (item.getName().equalsIgnoreCase(selectedPhase)) { 
							List<? extends WorkspaceItem> thefiles = item.getChildren();
							List<ExternalFile> theSheets = new ArrayList<>();
							List<ExternalFile> theInstructions = new ArrayList<>();
							List<String >theSheetNames = new ArrayList<>();
							//separate from instructions
							for (WorkspaceItem workspaceItem : thefiles) {
								if (workspaceItem instanceof ExternalFile) {
									ExternalFile file = (ExternalFile) workspaceItem;
									if (file.getMimeType().equals(XLS_MIMETYPE) || file.getMimeType().equals(XLSX_MIMETYPE)) { //is a form
										theSheets.add(file);
										theSheetNames.add("'"+file.getName()+"'"); //serializing for Javascript
									}
									else 
										theInstructions.add(file);
								}
							}
							request.setAttribute("theSheetNames", theSheetNames); //pass to the JSP
							request.setAttribute("theSheets", theSheets); //pass to the JSP
							request.setAttribute("theInstructions", theInstructions); //pass to the JSP
							break;
						}
					}
				}
				super.render(request, response);
			}
			else {
				PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher(Utils.TOO_MANY_FARM_PAGE_PATH);
				dispatcher.include(request, response);	
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse) throws IOException, PortletException {
		_log.debug("serveResource ...");
		String resourceID = resourceRequest.getResourceID();
		if (resourceID != null && resourceID.equals(RESOURCE_URL_ID)) {
			_log.debug("Upload File");
			UploadPortletRequest uploadRequest = PortalUtil.getUploadPortletRequest(resourceRequest);
			File multipartFile = uploadRequest.getFile(UPLOADED_FILE_ATTR_NAME);

			FileItem[] items = uploadRequest.getMultipartParameterMap().get(UPLOADED_FILE_ATTR_NAME);
			String fileName = "";
			String contentType = "";
			for (int i = 0; i < items.length; i++) {
				fileName = items[i].getFileName();
				contentType = items[i].getContentType();
				_log.debug(fileName);
				_log.debug(contentType);
			}
			JSONObject fileObject = JSONFactoryUtil.createJSONObject();
			try {
				String context = Utils.getCurrentContext(resourceRequest);
				_log.debug("upload file, context="+context);
				String username = Utils.getCurrentUser(resourceRequest).getUsername();
				String authorizationToken = Utils.getCurrentUserToken(context, username );
				SecurityTokenProvider.instance.set(authorizationToken);
				ScopeProvider.instance.set(context);
				Workspace workspace = Utils.getWS(username, context) ;
				String wsItemId = uploadToUserWorkspace(workspace, fileName, contentType, new ByteArrayInputStream(FileUtil.getBytes(multipartFile)));
				String toReturn = workspace.getItem(wsItemId).getPublicLink(false);
				fileObject.put("fileName", fileName);
				fileObject.put("urlEncoded", URLEncoder.encode(toReturn, "UTF-8"));
			} catch (Exception e) {
				_log.error("There was an error uploading the file " + fileName, e);
			}

			resourceResponse.getWriter().println(fileObject);	
		} 
		else {
			_log.debug("Download Original File");
			//if download file
			String fileToDownloadId = ParamUtil.getString(resourceRequest, "fileToDownloadId", null);
			String context = Utils.getCurrentContext(resourceRequest);
			Workspace ws = Utils.getWS("guest", context) ;
			WorkspaceItem theItem;
			try {
				theItem = ws.getItem(fileToDownloadId);
				if (theItem instanceof ExternalFile) {
					ExternalFile file = (ExternalFile) theItem;	
					InputStream is = file.getData();
					HttpServletResponse httpRes = PortalUtil.getHttpServletResponse(resourceResponse);
					HttpServletRequest httpReq = PortalUtil.getHttpServletRequest(resourceRequest);
					ServletResponseUtil.sendFile(httpReq,httpRes, file.getName(), is, "application/download");
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
	private void setGuestAuthorizationToken(RenderRequest request) {
		String scope = Utils.getCurrentContext(request);
		String authorizationToken = Utils.getCurrentUserToken(scope, "guest");
		SecurityTokenProvider.instance.set(authorizationToken);
	}

	private String uploadToUserWorkspace(Workspace ownerWS, String fileName, String mimeType,InputStream fileData) throws Exception {
		WorkspaceFolder attachmentDirectory = null;
		try{
			attachmentDirectory = (WorkspaceFolder) ownerWS.getItemByPath(ownerWS.getRoot().getPath() + "/" + ATTACHMENT_FOLDER);
		} catch (ItemNotFoundException e) {
			_log.info(ATTACHMENT_FOLDER + " Workspace Folder does not exists, creating it ");
			attachmentDirectory = ownerWS.getRoot().createFolder(ATTACHMENT_FOLDER, "Folder created automatically by the System");
		}
		System.out.println("attachmentDirectory="+attachmentDirectory);
		String itemName = WorkspaceUtil.getUniqueName(fileName, attachmentDirectory);
		FolderItem item = WorkspaceUtil.createExternalFile(attachmentDirectory, itemName, "System created upon user form submission " + new Date(), mimeType, fileData);
		String toReturn = item.getId();
		_log.debug("Uploaded " + item.getName() + " - Returned Workspace id=" +toReturn);
		return toReturn;
	}
}
