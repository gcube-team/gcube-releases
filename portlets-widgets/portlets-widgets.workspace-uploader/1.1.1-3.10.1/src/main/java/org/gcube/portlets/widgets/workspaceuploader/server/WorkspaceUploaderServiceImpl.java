package org.gcube.portlets.widgets.workspaceuploader.server;

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
 */
@SuppressWarnings("serial")
public class WorkspaceUploaderServiceImpl extends RemoteServiceServlet implements WorkspaceUploaderService {

	public static Logger logger = LoggerFactory.getLogger(WorkspaceUploaderServiceImpl.class);

	@Override
	public WorkspaceUploaderItem getUploadStatus(String identifier) throws Exception {

		if (identifier == null || identifier.isEmpty()) {
			String exception = "Invalid upload identifier, it is null or empty";
			logger.error(exception);
			throw new Exception(exception);
		}
		logger.trace("Get UploadStatus for id: "+identifier);
		WorkspaceUploaderItem uploader = WsUtil.getWorkspaceUploaderInSession(getThreadLocalRequest().getSession(), identifier);

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
		return uploader;

	}
	
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
