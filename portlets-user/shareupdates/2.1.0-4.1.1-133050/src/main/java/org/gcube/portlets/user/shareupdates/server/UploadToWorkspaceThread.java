package org.gcube.portlets.user.shareupdates.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.accessmanager.ACLType;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.homelibrary.util.WorkspaceUtil;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.scope.api.ScopeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Massimiliano Assante ISTI-CNR
 *
 */
public class UploadToWorkspaceThread implements Runnable {
	private static Logger _log = LoggerFactory.getLogger(UploadToWorkspaceThread.class);
	public static final String ATTACHMENT_FOLDER ="Shared attachments";
	/**
	 * the identifier of the workspace you are putting
	 */
	private String username;
	/**
	 * the identifier of the workspace you are putting
	 */
	private String fullName;
	/**
	 * the name of the file you are putting
	 */
	private String fileName;
	/**
	 * the path (with name) of the file you are putting
	 */
	private String fileabsolutePathOnServer;

	/**
	 * 
	 * @param sClient the instance of the storage client
	 * @param fileToUpload the absolute path of the file
	 */
	public UploadToWorkspaceThread(String fullName, String username, String fileName, String fileabsolutePathOnServer) {
		super();
		this.username = username;
		this.fullName = fullName;
		this.fileName = fileName;
		this.fileabsolutePathOnServer = fileabsolutePathOnServer;
	}

	@Override
	public void run() {
		try {
			String currScope = ScopeProvider.instance.get();
			ScopeProvider.instance.set("/"+PortalContext.getConfiguration().getInfrastructureName());
			
			Workspace ws = HomeLibrary
					.getHomeManagerFactory()
					.getHomeManager()
					.getHome(username).getWorkspace();
			
			_log.info("File to upload="+fileabsolutePathOnServer);
			File file = new File(fileabsolutePathOnServer);
			InputStream fileData = new FileInputStream(file);
						
			_log.info("Trying to get Group folder for scope="+currScope);
			WorkspaceSharedFolder folder = ws.getVREFolderByScope(currScope);
			
			Workspace ownerWS = HomeLibrary
					.getHomeManagerFactory()
					.getHomeManager()
					.getHome(folder.getOwner().getPortalLogin()).getWorkspace();
			
			WorkspaceFolder attachment = null;
			try{
				attachment = (WorkspaceFolder) ownerWS.getItemByPath(folder.getPath() + "/" + ATTACHMENT_FOLDER);
			} catch (ItemNotFoundException e) {
				_log.info(ATTACHMENT_FOLDER + " Workspace Folder does not exists, creating it for "+currScope);
				attachment = ownerWS.createFolder(ATTACHMENT_FOLDER, "Folder created automatically by the System", folder.getId());
				attachment.setACL(folder.getUsers(), ACLType.WRITE_OWNER);
			}
			
			WorkspaceFolder theFolderToWriteIn = (WorkspaceFolder) ws.getItemByPath(folder.getPath() + "/" + ATTACHMENT_FOLDER);
			String itemName = WorkspaceUtil.getUniqueName(fileName, theFolderToWriteIn);
			FolderItem item = WorkspaceUtil.createExternalFile(theFolderToWriteIn, itemName, "File shared by " + fullName + "("+username+")", null, fileData);
			
			_log.debug("Uploaded " + item.getName() + " - Returned Workspace id=" + item.getId());
			ScopeProvider.instance.set(currScope);			
		}
		catch (Exception e) {			
			e.printStackTrace();
			_log.error("Something wrong while uploading " + fileName + " in Workspace " + e.getMessage());
		}
	}
}
