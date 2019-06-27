package org.gcube.portlets.user.wswidget;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.storagehub.client.StreamDescriptor;
import org.gcube.common.storagehub.client.dsl.FileContainer;
import org.gcube.common.storagehub.client.dsl.StorageHubClient;
import org.gcube.common.storagehub.client.plugins.AbstractPlugin;
import org.gcube.common.storagehub.client.proxies.WorkspaceManagerClient;
import org.gcube.common.storagehub.model.exceptions.StorageHubException;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.portlets.user.wswidget.shared.AuthorizedUser;
import org.gcube.portlets.user.wswidget.shared.Breadcrumb;
import org.gcube.portlets.user.wswidget.shared.WSItem;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class WorkspaceWidget
 */
public class WorkspaceWidget extends MVCPortlet {
	private static com.liferay.portal.kernel.log.Log _log = LogFactoryUtil.getLog(WorkspaceWidget.class);
	public static String BREADCRUMB_ATTR = "BREADCRUMB_ATTR_name";	
	public final static int LIMIT = 5;
	public final static String ITEM_URL_TYPE = "nthl:externalUrl";

	/**
	 * all the AJAX calls are served by this method, to discriminate the operation we use the parameters:
	 * - when fileToDownloadId parameters is not empty the request is for a file download
	 * - when 
	 */
	@Override
	public void serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse) throws IOException, PortletException {
		String fileToDownloadId = ParamUtil.getString(resourceRequest, "fileToDownloadId", null);
		//check if is a file download
		if (fileToDownloadId != null) {
			setAuthorizationToken(resourceRequest);
			String selectedItemId =  fileToDownloadId.split("=")[1];
			StorageHubClient client = new StorageHubClient();
			FileContainer fileContainer = null;
			StreamDescriptor streamDescr = null;
			try {
				fileContainer = client.open(selectedItemId).asFile();
				streamDescr = fileContainer.download(selectedItemId);
			} catch (StorageHubException e) {
				e.printStackTrace();
			}
			HttpServletResponse httpRes = PortalUtil.getHttpServletResponse(resourceResponse);
			if (ITEM_URL_TYPE.compareTo(fileContainer.get().getPrimaryType()) == 0) { //if is a type URL we open drectly the link
				Scanner sc = new Scanner( streamDescr.getStream());
				String text = sc.useDelimiter("\\A").next();
				sc.close();
				streamDescr.getStream().close();
				httpRes.sendRedirect(text);
			}
			else {				
				HttpServletRequest httpReq = PortalUtil.getHttpServletRequest(resourceRequest);
				ServletResponseUtil.sendFile(httpReq,httpRes, streamDescr.getFileName(), streamDescr.getStream(), "application/download");
				streamDescr.getStream().close();
			}
		}
		//the user is browsing the workspace or asking for recents
		else {
			String cmd = ParamUtil.getString(resourceRequest, "cmd", "");
			String selectedItemId = "root";
			String selectedItemName = "";
			if (cmd != null && cmd.split("_selectedName").length > 1) {
				String[] splits = cmd.split("_selectedName");
				selectedItemId = splits[0].split("=")[1];
				selectedItemName= splits[1].split("=")[1];
			}
			int start = ParamUtil.getInteger(resourceRequest, "start");
			int length = ParamUtil.getInteger(resourceRequest, "length");
			int draw = ParamUtil.getInteger(resourceRequest, "draw");
			listItems(resourceRequest, resourceResponse, selectedItemId, selectedItemName, start, length, draw);
		}
	}


	private void listItems(ResourceRequest request, ResourceResponse resourceResponse, String itemId, String itemName, int start, int offset, int draw) {
		try {
			AuthorizedUser authUser = setAuthorizationToken(request);
			List<WSItem> itemsList = new ArrayList<>();
			Breadcrumb toSet = null;		
			WSItem clicked = null;
			int count = 0;
			if (itemId.compareTo("root") == 0) {
				//here we have to check if is a VRE, in that case the content of the VRE Folder is the root
				long groupId = PortalUtil.getScopeGroupId(request);
				if (GroupLocalServiceUtil.getParentGroups(groupId).isEmpty()) { //is in Site / RootVO
					itemId = getRootItem(request).getId();
					itemsList = StorageHubServiceUtil.getRootChildren(authUser, start, offset);	
					toSet = new Breadcrumb(itemId, Utils.getCurrentUser(request).getFirstName()+"\'s home");
					request.getPortletSession().setAttribute(BREADCRUMB_ATTR, toSet, PortletSession.APPLICATION_SCOPE);
				} else { //is in a VRE
					WorkspaceManagerClient wsclient = AbstractPlugin.workspace().build();
					itemId = wsclient.getVreFolder("hl:accounting").getId();
					itemsList = StorageHubServiceUtil.getItemChildren(authUser, itemId, itemId, start, offset);
					String groupName = GroupLocalServiceUtil.getGroup(groupId).getName();
					toSet = new Breadcrumb(itemId, groupName+"\'s VRE home");
					request.getPortletSession().setAttribute(BREADCRUMB_ATTR, toSet, PortletSession.APPLICATION_SCOPE);
				}
				count = StorageHubServiceUtil.getItemChildrenCount(request, itemId);
			}
			else if (itemId.compareTo("recents") == 0) { //is in a VRE and asking for recents files				

				itemsList = StorageHubServiceUtil.getRecentItems(authUser);
				count = offset;
				toSet = (Breadcrumb) request.getPortletSession().getAttribute(BREADCRUMB_ATTR, PortletSession.APPLICATION_SCOPE);
				toSet.setChild(new Breadcrumb("", "Top 10 Recent"));
				request.getPortletSession().setAttribute(BREADCRUMB_ATTR, toSet, PortletSession.APPLICATION_SCOPE);
			}
			else {
				_log.debug("non root");
				itemsList = StorageHubServiceUtil.getItemChildren(authUser, itemId, itemId, start, offset);
				toSet = (Breadcrumb) request.getPortletSession().getAttribute(BREADCRUMB_ATTR, PortletSession.APPLICATION_SCOPE);
				clicked =  new WSItem(itemId, itemName, true);
				computeBreadcrumb(clicked, toSet);
				count = StorageHubServiceUtil.getItemChildrenCount(request, itemId);
			}

			JSONObject tableData = buildJSONResponse(itemsList, start, offset, draw, count, request);
			ServletResponseUtil.write(PortalUtil.getHttpServletResponse(resourceResponse),tableData.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	/**
	 * computes the breadcrumb
	 * @param toReturn
	 * @param toCompute
	 */
	public void computeBreadcrumb(WSItem toReturn, Breadcrumb toCompute) {
		boolean found = false;
		while (toCompute.hasChild()) {
			if (toReturn.getId().equals(toCompute.getId())) { //in case the user click on the breadcrumb
				toCompute.setChild(null);
				found = true;
				break;
			} else {
				toCompute = toCompute.getChild();
			}
		}				
		if (!found) {
			if (toReturn.getName().equals(Utils.SPECIAL_FOLDERS_NAME)) //in case the user clicked on the VRE Folders
				toReturn.setName(Utils.VRE_FOLDERS_LABEL);
			if (! toReturn.getId().equals(toCompute.getId()))
				toCompute.setChild(new Breadcrumb(toReturn.getId(), toReturn.getName()));
		}
	}

	private static String constructBreadcrumbHTML(Breadcrumb toSet, ResourceRequest request) {
		StringBuilder sb = 
				new StringBuilder("<ul class='breadcrumb'>");
		sb.append("<li>").append(getHREFJavascriptCall(toSet, false)).append("</li>"); //this is the root of the breadcrumb
		while (toSet.hasChild()) {
			toSet = toSet.getChild();
			if (!toSet.hasChild()) {
				sb.append("<li><span class='active'>&nbsp;/&nbsp;</span>").append(toSet.getName()).append("</li>");
			}
			else {
				sb.append("<li><span class='divider'>/</span>").append(getHREFJavascriptCall(toSet, true)).append("</li>");
			}
		}
		sb.append("</ul>");
		return sb.toString();
	}
	/**
	 * this method construct the breadcrumb server side, the resaulting code is added in the client DOM (once returned)
	 * @param toSet
	 * @return
	 */
	private static String getHREFJavascriptCall(Breadcrumb toSet, boolean hideVreFoldersDiv) {
		String escapedItemName = toSet.getName().replaceAll("\"", "\\\"").replaceAll("'", "\\\\'");
		StringBuilder sb = new StringBuilder
				("<a href=\"javascript:loadItemsListIntoTable('").append(toSet.getId()).append("', '").append(escapedItemName).append("', "+hideVreFoldersDiv+");\">")
				.append(toSet.getName()).append("</a>");
		return sb.toString();
	}
	/**
	 * 
	 * @param itemsList
	 * @param start
	 * @param offset
	 * @param draw
	 * @param count
	 * @param request
	 * @return the JSON response to the client
	 */
	private JSONObject buildJSONResponse(List<WSItem> itemsList,  int start, int offset, int draw, int count, ResourceRequest request){
		JSONArray usersJsonArray = JSONFactoryUtil.createJSONArray();
		JSONObject jsonUser = null;		
		for (WSItem item : itemsList) {
			JSONArray nameAndIconArray = JSONFactoryUtil.createJSONArray();
			JSONObject nameAndIconObject = JSONFactoryUtil.createJSONObject();
			nameAndIconObject.put("Icon", item.getIconURL());
			nameAndIconObject.put("IconColor", item.getIconColor());
			nameAndIconObject.put("Name", item.getName());
			nameAndIconObject.put("Id", item.getId());
			nameAndIconObject.put("isFolder", item.isFolder() || item.isSharedFolder() || item.isSpecialFolder());
			nameAndIconArray.put(nameAndIconObject);

			jsonUser = JSONFactoryUtil.createJSONObject();
			//we need to pass a JSON here as String without 
			String stringfiedJsonArray = nameAndIconArray.toString();
			jsonUser.put("Id",item.getId());
			jsonUser.put("Name", stringfiedJsonArray.substring(1, stringfiedJsonArray.length()-1)); //without square brackets so that it is parsable in the client
			jsonUser.put("Owner",item.getOwner());
			jsonUser.put("LastModified", item.getLastUpdated().getTime());
			jsonUser.put("isFolder", item.isFolder() || item.isSharedFolder() || item.isSpecialFolder());
			usersJsonArray.put(jsonUser);
		}

		JSONObject tableData = JSONFactoryUtil.createJSONObject();
		tableData.put("mytabledata", usersJsonArray);
		Breadcrumb bcBegin = (Breadcrumb) request.getPortletSession().getAttribute(BREADCRUMB_ATTR, PortletSession.APPLICATION_SCOPE);
		tableData.put("breadcrumb", constructBreadcrumbHTML(bcBegin, request));
		tableData.put("draw", draw);
		tableData.put("recordsTotal", count);
		tableData.put("recordsFiltered", count);
		_log.debug("tableData:"+tableData.toString());
		return tableData;
	}

	private  static WSItem getRootItem(ResourceRequest request) {
		String userName = Utils.getCurrentUser(request).getUsername();
		String scope = Utils.getCurrentContext(request);
		String authorizationToken = Utils.getCurrentUserToken(scope, userName);
		SecurityTokenProvider.instance.set(authorizationToken);
		WorkspaceManagerClient client = AbstractPlugin.workspace().build();
		Item itemRoot = client.getWorkspace("hl:accounting");
		WSItem root = new WSItem(itemRoot.getId(), Utils.HOME_LABEL, true);
		root.setIsRoot(true);
		root.setFolder(true);
		return root;
	}

	/**
	 * set the authorization token in Thread local and return the current {@link AuthorizedUser} instance
	 * @param request
	 * @return
	 */
	private AuthorizedUser setAuthorizationToken(ResourceRequest request) {
		GCubeUser currentUser = Utils.getCurrentUser(request);
		String scope = Utils.getCurrentContext(request);
		String authorizationToken = Utils.getCurrentUserToken(scope,  currentUser.getUsername());
		SecurityTokenProvider.instance.set(authorizationToken);
		return new AuthorizedUser(currentUser, authorizationToken, scope);
	}
}
