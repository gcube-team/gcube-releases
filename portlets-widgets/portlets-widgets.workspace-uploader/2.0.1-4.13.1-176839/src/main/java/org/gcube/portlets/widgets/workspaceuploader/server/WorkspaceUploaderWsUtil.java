package org.gcube.portlets.widgets.workspaceuploader.server;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gcube.common.portal.PortalContext;
import org.gcube.common.storagehub.model.types.WorkspaceItemType;
import org.gcube.common.storagehubwrapper.server.StorageHubWrapper;
import org.gcube.common.storagehubwrapper.server.tohl.Workspace;
import org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem;
import org.gcube.portlets.widgets.workspaceuploader.client.ConstantsWorkspaceUploader;
import org.gcube.portlets.widgets.workspaceuploader.server.util.WsUtil;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The server side implementation of the RPC service.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 7, 2016
 */
@SuppressWarnings("serial")
public class WorkspaceUploaderWsUtil extends HttpServlet {

	public static Logger logger = LoggerFactory.getLogger(WorkspaceUploaderWsUtil.class);
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		String folderParentId = request.getParameter(ConstantsWorkspaceUploader.FOLDER_PARENT_ID);
		String itemName = request.getParameter(ConstantsWorkspaceUploader.ITEM_NAME);
		String currGroupId = request.getParameter(ConstantsWorkspaceUploader.CURR_GROUP_ID);
		//String currUserId = req.getParameter(ConstantsWorkspaceUploader.CURR_USER_ID);
		logger.debug("folderParentId: "+folderParentId);
		logger.debug("itemName: "+itemName);
		logger.debug("currGroupId: "+currGroupId);

		if(currGroupId==null || currGroupId.isEmpty())
			sendError(response, "Parameter error: currGroupId is null or empty");
		//logger.debug("currUserId: "+currUserId);

		try {

			Workspace wa = null;
			//GCubeUser currUser;
			try {
				GCubeUser user = PortalContext.getConfiguration().getCurrentUser(request);
				StorageHubWrapper storageWrapper = WsUtil.getStorageHubWrapper(request, currGroupId, user);
				wa = storageWrapper.getWorkspace();
			} catch (Exception e) {
				logger.error("Error during workspace retrieving", e);
				sendError(response, "An error occurred during item exists check");
			}

			String itemId = itemExistsInWorkpaceFolder(wa, folderParentId, itemName);
			sendOKMessage(response, itemId);
		}
		catch (Exception e) {
			logger.error("An error occurred during item exists check",e);
			sendError(response, "An error occurred during item exists check");
		}
	}


	/**
	 * Item exists in workpace folder.
	 *
	 * @param currGroupId the curr group id
	 * @param req the req
	 * @param parentId the parent id
	 * @param itemName the item name
	 * @return the string
	 * @throws Exception the exception
	 */
	public String itemExistsInWorkpaceFolder(Workspace workspace, String parentId, String itemName) throws Exception {
		logger.trace("get itemExistsInWorkpace for name: "+itemName+", by parentId: "+parentId);
		try {
//			//IN THIS CASE THE SESSION EXPIRED IS MANAGED BY UPLOAD SERVLET
//			if(gcubeUser==null)
//				return null;

			WorkspaceItem wsItem = workspace.getItem(parentId); //GET PARENT

			if(wsItem.getType().equals(WorkspaceItemType.FOLDER) || wsItem.getType().equals(WorkspaceItemType.SHARED_FOLDER)){
				List<WorkspaceItem> foundItems = workspace.find(itemName, parentId);
				logger.debug("Is item: "+itemName+", existing in parentId: "+parentId +"? "+foundItems);

				if(foundItems!=null && foundItems.size()>0)
					return foundItems.get(0).getId();
				else
					return null;
			}else
				throw new Exception("Invalid Folder parent");

		} catch (Exception e) {
			String error = "Sorry an error occurred when searching item id, please refresh and try again";
			logger.error(error, e);
			throw new Exception(e.getMessage());
		}
	}


	/**
	 * Send error.
	 *
	 * @param response the response
	 * @param message the message
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected void sendError(HttpServletResponse response, String message) throws IOException{
		try {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().write(message);
			//5.6 Closure of Response Object:
			//When a response is closed, the container must immediately flush all remaining content in the response buffer to the client
//			response.flushBuffer();
		} catch (IOException e){
			logger.warn("IOException class name: "+e.getClass().getSimpleName());
			if (e.getClass().getSimpleName().equals("ClientAbortException"))
				logger.warn("Skipping ClientAbortException: "+e.getMessage());
			else
				throw e; //Sending Exceptions
		}
	}


	/**
	 * Send message.
	 *
	 * @param response the response
	 * @param message the message
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected void sendOKMessage(HttpServletResponse response, String message) throws IOException{
		try {
			response.setStatus(HttpServletResponse.SC_OK);
			if(message==null)
				message="null";
			response.getWriter().write(message);
			//5.6 Closure of Response Object:
			//When a response is closed, the container must immediately flush all remaining content in the response buffer to the client
//			response.flushBuffer();
		} catch (IOException e){
			logger.warn("IOException class name: "+e.getClass().getSimpleName());
			if (e.getClass().getSimpleName().equals("ClientAbortException"))
				logger.warn("Skipping ClientAbortException: "+e.getMessage());
			else
				throw e; //Sending Exceptions
		}
	}

}
