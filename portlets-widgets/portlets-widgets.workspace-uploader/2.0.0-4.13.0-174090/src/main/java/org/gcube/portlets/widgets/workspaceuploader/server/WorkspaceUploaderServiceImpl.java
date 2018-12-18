package org.gcube.portlets.widgets.workspaceuploader.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.gcube.common.portal.PortalContext;
import org.gcube.common.storagehub.model.types.WorkspaceItemType;
import org.gcube.common.storagehubwrapper.server.StorageHubWrapper;
import org.gcube.common.storagehubwrapper.server.tohl.Workspace;
import org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceFolder;
import org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem;
import org.gcube.portlets.widgets.workspaceuploader.client.WorkspaceUploaderService;
import org.gcube.portlets.widgets.workspaceuploader.server.util.WsUtil;
import org.gcube.portlets.widgets.workspaceuploader.shared.UploadProgress;
import org.gcube.portlets.widgets.workspaceuploader.shared.WorkspaceUploaderItem;
import org.gcube.portlets.widgets.workspaceuploader.shared.WorkspaceUploaderItem.UPLOAD_STATUS;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;


/**
 * The Class WorkspaceUploaderServiceImpl.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jun 25, 2018
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
		WorkspaceUploaderItem uploader = WsUtil.getWorkspaceUploaderInSession(getThreadLocalRequest(), identifier);
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

		checkUploaderErasable(this.getThreadLocalRequest(), uploader);
		return uploader;

	}


	/**
	 * Check uploader erasable.
	 *
	 * @param httpRequest the http request
	 * @param uploader the uploader
	 */
	private void checkUploaderErasable(final HttpServletRequest httpRequest, final WorkspaceUploaderItem uploader){
		logger.trace("Checking Uploader erasable...");

		if(uploader==null){
			logger.error("Uploader is null, returning..");
			return;
		}

		final HttpSession session = httpRequest.getSession();

		new Thread(){
			@Override
			public void run() {
				try {

					logger.trace("Uploader: "+uploader.getClientUploadKey() +", is erasable? "+uploader.isErasable());
					WsUtil.eraseWorkspaceUploaderInSession(session, uploader);
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
		logger.debug("Getting the workspaceId");
		try {
			String scopeGroupId = ""+PortalContext.getConfiguration().getCurrentGroupId(getThreadLocalRequest());
			GCubeUser currUser = PortalContext.getConfiguration().getCurrentUser(getThreadLocalRequest());
			StorageHubWrapper stHubWrapper = WsUtil.getStorageHubWrapper(getThreadLocalRequest(), scopeGroupId, currUser);
			Workspace workspace = stHubWrapper.getWorkspace();
			if(workspace!=null){
				WorkspaceFolder root = workspace.getRoot();
				logger.debug("Retrieve the root "+root+" correctly");
				return root.getId();
			}
			return null;
		} catch (Exception e) {
			logger.error("Get workspace id error", e);
			String error = "An error occurred getting root id";
			throw new Exception(error);
		}
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#itemExistsInWorkpaceFolder(java.lang.String, java.lang.String)
	 */
	@Override
	public String itemExistsInWorkpaceFolder(String parentId, String itemName) throws Exception {
		logger.trace("get itemExistsInWorkpace by parentId: "+parentId);
		try {
			String scopeGroupId = ""+PortalContext.getConfiguration().getCurrentGroupId(getThreadLocalRequest());
			GCubeUser currUser = PortalContext.getConfiguration().getCurrentUser(getThreadLocalRequest());
			StorageHubWrapper stHubWrapper = WsUtil.getStorageHubWrapper(getThreadLocalRequest(), scopeGroupId, currUser);
			WorkspaceItem wsItem = stHubWrapper.getWorkspace().getItem(parentId); //GET PARENT

			if(wsItem.getType().equals(WorkspaceItemType.FOLDER) || wsItem.getType().equals(WorkspaceItemType.SHARED_FOLDER)){

				//TODO workspace.find(itemName, parentId);
				/*WorkspaceItem itemFound = workspace.find(itemName, parentId);

				if(itemFound==null)
					return null;

				return itemFound.getId();*/

				return null;
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
