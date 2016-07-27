package org.gcube.portlets.widgets.workspaceuploader.server;

import javax.servlet.http.HttpSession;

import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.portlets.widgets.workspaceuploader.client.WorkspaceUploaderService;
import org.gcube.portlets.widgets.workspaceuploader.server.util.WsUtil;
import org.gcube.portlets.widgets.workspaceuploader.shared.UploadProgress;
import org.gcube.portlets.widgets.workspaceuploader.shared.WorkspaceUploaderItem;
import org.gcube.portlets.widgets.workspaceuploader.shared.WorkspaceUploaderItem.UPLOAD_STATUS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 12, 2016
 */
@SuppressWarnings("serial")
public class WorkspaceUploaderServiceImpl extends RemoteServiceServlet implements WorkspaceUploaderService {

	public static Logger logger = LoggerFactory.getLogger(WorkspaceUploaderServiceImpl.class);

	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.workspaceuploader.client.WorkspaceUploaderService#getUploadStatus(java.lang.String)
	 */
	@Override
	public WorkspaceUploaderItem getUploadStatus(String identifier) throws Exception {

		if (identifier == null || identifier.isEmpty()) {
			String exception = "Invalid upload identifier, it is null or empty";
			logger.error(exception);
			throw new Exception(exception);
		}
		logger.trace("Get UploadStatus for id: "+identifier);
		WorkspaceUploaderItem uploader = WsUtil.getWorkspaceUploaderInSession(getThreadLocalRequest().getSession(), identifier);
		logger.trace("Uploader status for id: "+identifier +" returns "+uploader);

		if (uploader == null) {
			WorkspaceUploaderItem waitingUpload = new WorkspaceUploaderItem(identifier, null, UPLOAD_STATUS.WAIT, "Upload waiting..", new UploadProgress());
			waitingUpload.setClientUploadKey(identifier);
			return waitingUpload;
			/*
			String exception = "Item upload not found";
			logger.error(exception);
			throw new Exception(exception);*/
		}

//		getThreadIds();
		String progress = "";
		if(uploader.getUploadProgress()!=null && uploader.getUploadProgress().getLastEvent()!=null)
			progress = uploader.getUploadProgress().getLastEvent().toString();
		else
			progress = "upload progress is null or last event is null";

		logger.info("returning uploader: "+uploader.getClientUploadKey() +" status: "+uploader.getUploadStatus() +", file: "+uploader.getFile().toString() +", progress: "+progress);

		checkUploaderErasable(getThreadLocalRequest().getSession(), uploader);
		return uploader;

	}


	/**
	 * Check uploader erasable.
	 *
	 * @param httpSession the http session
	 * @param uploader the uploader
	 */
	private void checkUploaderErasable(final HttpSession httpSession, final WorkspaceUploaderItem uploader){
		logger.trace("Checking Uploader erasable...");

		if(uploader==null){
			logger.error("Uploader is null, returning..");
			return;
		}

		new Thread(){
			@Override
			public void run() {
				try {

					logger.trace("Uploader: "+uploader.getClientUploadKey() +", is erasable?");
					WsUtil.eraseWorkspaceUploaderInSession(httpSession, uploader);
				}
				catch (Exception e) {
					logger.warn("Error during checkUploaderErasable: ", e);
				}
			}
		}.start();
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.workspaceuploader.client.WorkspaceUploaderService#getWorkspaceId()
	 */
	@Override
	public String getWorkspaceId() throws Exception  {
		logger.trace("getWorkspaceId");
		Workspace workspace;
		try {
			workspace = WsUtil.getWorkspace(getThreadLocalRequest().getSession());
			if(workspace!=null)
				return workspace.getRoot().getId();
		} catch (Exception e) {
			logger.error("Get workspace id error", e);
			String error = "An error occurred getting root id";
			throw new Exception(error);
		}

		return null;
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#itemExistsInWorkpaceFolder(java.lang.String, java.lang.String)
	 */
	@Override
	public String itemExistsInWorkpaceFolder(String parentId, String itemName) throws Exception {
		logger.trace("get itemExistsInWorkpace by parentId: "+parentId);
		try {

			Workspace workspace = WsUtil.getWorkspace(getThreadLocalRequest().getSession());
			WorkspaceItem wsItem = workspace.getItem(parentId); //GET PARENT

			if(wsItem.getType().equals(WorkspaceItemType.FOLDER) || wsItem.getType().equals(WorkspaceItemType.SHARED_FOLDER)){

				WorkspaceItem itemFound = workspace.find(itemName, parentId);

				if(itemFound==null)
					return null;

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

//	protected void getThreadIds(){
//		WorkspaceUploadServletStream.printAllThreadIds();
//	}
}
