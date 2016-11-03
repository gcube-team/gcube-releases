package org.gcube.portlets.widgets.workspaceuploader.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.portlets.widgets.workspaceuploader.client.ConstantsWorkspaceUploader;
import org.gcube.portlets.widgets.workspaceuploader.server.util.WsUtil;
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
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {
		String folderParentId = req.getParameter(ConstantsWorkspaceUploader.FOLDER_PARENT_ID);
		String itemName = req.getParameter(ConstantsWorkspaceUploader.ITEM_NAME);
		logger.debug("folderParentId: "+folderParentId);
		logger.debug("itemName: "+itemName);
		try {
			String itemId = itemExistsInWorkpaceFolder(req, folderParentId, itemName);
			sendOKMessage(resp, itemId);
		}
		catch (Exception e) {
			logger.error("An error occurred during item exists check",e);
			sendError(resp, "An error occurred during item exists check");
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#itemExistsInWorkpaceFolder(java.lang.String, java.lang.String)
	 */
	//@Override
	/**
	 * Item exists in workpace folder.
	 *
	 * @param req the req
	 * @param parentId the parent id
	 * @param itemName the item name
	 * @return the string
	 * @throws Exception the exception
	 */
	public String itemExistsInWorkpaceFolder(HttpServletRequest req, String parentId, String itemName) throws Exception {
		logger.trace("get itemExistsInWorkpace for name: "+itemName+", by parentId: "+parentId);
		try {
			HttpSession session = req.getSession();
			Workspace workspace = WsUtil.getWorkspace(session);
			WorkspaceItem wsItem = workspace.getItem(parentId); //GET PARENT

			if(wsItem.getType().equals(WorkspaceItemType.FOLDER) || wsItem.getType().equals(WorkspaceItemType.SHARED_FOLDER)){
				WorkspaceItem itemFound = workspace.find(itemName, parentId);
				if(itemFound==null){
					logger.trace("item: "+itemName+", not exists in parentId: "+parentId);
					return null;
				}

				logger.trace("item: "+itemName+", exists in parentId: "+parentId +", returning id:" +itemFound.getId());
				return itemFound.getId();
			}
			else
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
