/**
 *
 */
package org.gcube.portlets.widgets.workspaceuploader.server.upload;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongItemTypeException;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.homelibrary.util.WorkspaceUtil;
import org.gcube.common.homelibrary.util.zip.UnzipUtil;
import org.gcube.portlets.widgets.workspaceuploader.server.WorkspaceUploadServletStream;
import org.gcube.portlets.widgets.workspaceuploader.server.util.WsUtil;
import org.gcube.portlets.widgets.workspaceuploader.shared.WorkspaceUploaderItem;
import org.gcube.portlets.widgets.workspaceuploader.shared.WorkspaceUploaderItem.UPLOAD_STATUS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class WorkspaceUploaderManager.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 2, 2015
 */
public class WorkspaceUploaderMng {

	public static Logger logger = LoggerFactory.getLogger(WorkspaceUploaderMng.class);


	/**
	 * Instantiates a new workspace uploader manager.
	 */
	public WorkspaceUploaderMng() {
	}


	/**
	 * Creates the workspace uploader file.
	 *
	 * @param request the request
	 * @param workspaceUploader the workspace uploader
	 * @param httpSession the http session
	 * @param isOvewrite the is ovewrite
	 * @param wa the wa
	 * @param uploadFile the upload file
	 * @param itemName the item name
	 * @param destinationFolder the destination folder
	 * @param contentType the content type
	 * @param totalBytes the total bytes
	 * @return the workspace uploader item
	 * @throws InternalErrorException the internal error exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private static WorkspaceUploaderItem createWorkspaceUploaderFile(String scopeGroupId, HttpServletRequest request, final WorkspaceUploaderItem workspaceUploader, final HttpSession httpSession, final boolean isOvewrite, final Workspace wa, final InputStream uploadFile, final String itemName,  final WorkspaceFolder destinationFolder, final String contentType, final long totalBytes) throws InternalErrorException, IOException{
		logger.debug("Creating WorkspaceUploaderFile...");

		workspaceUploader.setUploadStatus(UPLOAD_STATUS.IN_PROGRESS);
		workspaceUploader.setStatusDescription("Uploading "+itemName);
		FolderItem createdItem = null;

		try{
			Long startTime = WorkspaceUploadServletStream.printStartTime();
			if(!isOvewrite){
				logger.debug("Calling HL createExternalFile - [itemName: "+itemName+", contentType: "+contentType+"]");
				createdItem = WorkspaceUtil.createExternalFile(destinationFolder, itemName, "", contentType, uploadFile);
			}
			else{
				createdItem  = overwriteItem(wa, itemName, uploadFile, destinationFolder); 	//CASE OVERWRITE
			}

			if(createdItem!=null){
				WorkspaceUploadServletStream.printElapsedTime(startTime);
				logger.debug("HL file: "+createdItem.getName() + " with id: "+createdItem.getId() + " uploaded correctly in "+destinationFolder.getPath());
				workspaceUploader.getFile().setItemId(createdItem.getId()); //SET HL ID
				workspaceUploader.getFile().setParentId(createdItem.getParent().getId());//SET PARENT ID
				workspaceUploader.setStatusDescription("File \""+createdItem.getName()+"\" uploaded correctly in "+destinationFolder.getPath());
				workspaceUploader.setUploadStatus(UPLOAD_STATUS.COMPLETED);
				WorkspaceUploadServletStream.notifyUploadInSharedFolder(scopeGroupId, request, httpSession, wa, createdItem.getId(), createdItem.getParent().getId(), isOvewrite);
			}else{
				workspaceUploader.setStatusDescription("An error occurred during upload: \""+itemName + "\". Try again");
				workspaceUploader.setUploadStatus(UPLOAD_STATUS.FAILED);
			}
			try {
				WsUtil.putWorkspaceUploaderInSession(httpSession, workspaceUploader);
			} catch (Exception e1) {
				logger.error("Error during WorkspaceUploaderItem session update: ",e1);
			}
		} catch (InsufficientPrivilegesException
				| ItemAlreadyExistException | InternalErrorException
				| IOException e) {
			logger.error("Error during upload: ",e);
			workspaceUploader.setStatusDescription("An error occurred during upload: "+itemName+". "+e.getMessage());
			workspaceUploader.setUploadStatus(UPLOAD_STATUS.FAILED);
			try {
				WsUtil.putWorkspaceUploaderInSession(httpSession, workspaceUploader);
			} catch (Exception e1) {
				logger.error("Error during WorkspaceUploaderItem session update: ",e1);
			}

		} catch (ItemNotFoundException | WrongItemTypeException | WorkspaceFolderNotFoundException | WrongDestinationException e) {
			logger.error("Error during overwrite: ",e);
			workspaceUploader.setStatusDescription("An error occurred during upload: "+itemName+". "+e.getMessage());
			workspaceUploader.setUploadStatus(UPLOAD_STATUS.FAILED);
			try {
				WsUtil.putWorkspaceUploaderInSession(httpSession, workspaceUploader);
			} catch (Exception e1) {
				logger.error("Error during WorkspaceUploaderItem session update: ",e1);
			}
			//IS unreachable
		}catch(UploadCanceledException e){
			logger.info("UploadCanceledException thrown by client..");
			workspaceUploader.setStatusDescription("Aborted upload: "+itemName);
			workspaceUploader.setUploadStatus(UPLOAD_STATUS.ABORTED);
			try {
//				WsUtil.putWorkspaceUploaderInSession(httpSession, workspaceUploader);
//				workspaceUploader.setErasable(true);
				WsUtil.forceEraseWorkspaceUploaderInSession(httpSession, workspaceUploader);
			} catch (Exception e1) {
				logger.error("Error during WorkspaceUploaderItem session update: ",e1);
			}
		}finally{
			try {
//				StreamUtils.deleteTempFile(uploadFile);
				WsUtil.setErasableWorkspaceUploaderInSession(request, workspaceUploader.getIdentifier());
			} catch (Exception e2) {
				logger.error("Error during setErasableWorkspaceUploaderInSession session update: ",e2);
			}
		}

		return workspaceUploader;
	}


	/**
	 * Creates the workspace uploader archive.
	 *
	 * @param workspaceUploader the workspace uploader
	 * @param httpSession the http session
	 * @param uploadFile the upload file
	 * @param itemName the item name
	 * @param destinationFolder the destination folder
	 * @param totalBytes the total bytes
	 * @return the workspace uploader item
	 * @throws InternalErrorException the internal error exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private static WorkspaceUploaderItem createWorkspaceUploaderArchive(final WorkspaceUploaderItem workspaceUploader, final HttpServletRequest request, final InputStream uploadFile, final String itemName, final WorkspaceFolder destinationFolder, final long totalBytes) throws InternalErrorException, IOException{
		HttpSession httpSession  = request.getSession();
		logger.info("calling upload archive - [itemName: "+itemName+"]");
		try {
			workspaceUploader.setUploadStatus(UPLOAD_STATUS.IN_PROGRESS);
			UnzipUtil.unzip(destinationFolder, uploadFile, itemName);

			logger.info("Archive "+itemName+ " imported correctly in "+destinationFolder.getPath());
			workspaceUploader.setStatusDescription("Archive "+itemName+ " imported correctly in "+destinationFolder.getPath());
			workspaceUploader.setUploadStatus(UPLOAD_STATUS.COMPLETED);

			try {
				WsUtil.putWorkspaceUploaderInSession(httpSession, workspaceUploader);
			} catch (Exception e1) {
				logger.error("Error during WorkspaceUploaderItem session update: ",e1);
			}
		} catch (InternalErrorException e) {
			logger.error("Error during uploading: ",e);
			workspaceUploader.setStatusDescription("An error occurred during upload: "+itemName+". "+e.getMessage());
			workspaceUploader.setUploadStatus(UPLOAD_STATUS.FAILED);
			try {
				WsUtil.putWorkspaceUploaderInSession(httpSession, workspaceUploader);
			} catch (Exception e1) {
				logger.error("Error during WorkspaceUploaderItem session update: ",e1);
			}
		}catch(UploadCanceledException e){
			logger.info("UploadCanceledException thrown by client..");
			workspaceUploader.setStatusDescription("Aborted upload: "+itemName);
			workspaceUploader.setUploadStatus(UPLOAD_STATUS.ABORTED);
			try {
				WsUtil.putWorkspaceUploaderInSession(httpSession, workspaceUploader);
			} catch (Exception e1) {
				logger.error("Error during WorkspaceUploaderItem session update: ",e1);
			}
		}finally{
			try {
//				StreamUtils.deleteTempFile(uploadFile);
				WsUtil.setErasableWorkspaceUploaderInSession(request, workspaceUploader.getIdentifier());
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		return workspaceUploader;
	}



	/**
	 * Upload file.
	 *
	 * @param request the request
	 * @param workspaceUploader the workspace uploader
	 * @param httpSession the http session
	 * @param wa the wa
	 * @param itemName the item name
	 * @param file the file
	 * @param destinationFolder the destination folder
	 * @param contentType the content type
	 * @param isOverwrite the is overwrite
	 * @param totolaBytes the totola bytes
	 * @return the workspace uploader item
	 * @throws Exception the exception
	 */
	public static WorkspaceUploaderItem uploadFile(String scopeGroupId, HttpServletRequest request, WorkspaceUploaderItem workspaceUploader, HttpSession httpSession, Workspace wa, String itemName, InputStream file, WorkspaceFolder destinationFolder, String contentType, boolean isOverwrite, long totolaBytes) throws Exception {

		try {
			return createWorkspaceUploaderFile(scopeGroupId, request, workspaceUploader, httpSession, isOverwrite, wa, file, itemName, destinationFolder, contentType, totolaBytes);
		} catch (Exception e) {
			logger.error("Error when uploading file to HL : ",e);
			throw new Exception("An error occurred during upload: "+itemName+". Try again");
		}
	}


	/**
	 * Upload archive.
	 *
	 * @param workspaceUploader the workspace uploader
	 * @param httpSession the http session
	 * @param itemName the item name
	 * @param file the file
	 * @param destinationFolder the destination folder
	 * @param totalBytes the total bytes
	 * @return the workspace uploader item
	 * @throws Exception the exception
	 */
	public static WorkspaceUploaderItem uploadArchive(WorkspaceUploaderItem workspaceUploader, HttpServletRequest request, String itemName, InputStream file, WorkspaceFolder destinationFolder, long totalBytes) throws Exception {

		try {
			return createWorkspaceUploaderArchive(workspaceUploader, request, file, itemName, destinationFolder, totalBytes);

		} catch (Exception e) {
			logger.error("Error when uploading Archive to HL creation: ",e);
			throw new Exception("An error occurred during upload:: "+itemName+". Try again");
		}
	}

	/**
	 * Upload file status.
	 *
	 * @param httpSession the http session
	 * @param workspaceUploader the workspace uploader
	 * @return the workspace uploader item
	 * @throws Exception the exception
	 */
	public static WorkspaceUploaderItem uploadFileStatus(HttpServletRequest request, WorkspaceUploaderItem workspaceUploader) throws Exception {
		return WsUtil.getWorkspaceUploaderInSession(request, workspaceUploader.getIdentifier());
	}

	/**
	 * Overwrite item.
	 *
	 * @param wa the wa
	 * @param itemName the item name
	 * @param fileData the file data
	 * @param destinationFolder the destination folder
	 * @return the folder item
	 * @throws ItemNotFoundException the item not found exception
	 * @throws WrongItemTypeException the wrong item type exception
	 * @throws InternalErrorException the internal error exception
	 * @throws InsufficientPrivilegesException the insufficient privileges exception
	 * @throws WorkspaceFolderNotFoundException the workspace folder not found exception
	 * @throws ItemAlreadyExistException the item already exist exception
	 * @throws WrongDestinationException the wrong destination exception
	 */
	private static FolderItem overwriteItem(Workspace wa, String itemName, InputStream fileData, WorkspaceFolder destinationFolder) throws ItemNotFoundException, WrongItemTypeException, InternalErrorException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException{

		FolderItem overwriteItem = null;
		logger.debug("case overwriting item.. "+itemName);
		overwriteItem = (FolderItem) wa.find(itemName, destinationFolder.getId());
		logger.debug("overwriteItem item was found, id is: "+overwriteItem.getId());
		wa.updateItem(overwriteItem.getId(), fileData);
		logger.debug("updateItem with id: "+overwriteItem.getId()+ ", is completed");
		return overwriteItem;

	}
}
