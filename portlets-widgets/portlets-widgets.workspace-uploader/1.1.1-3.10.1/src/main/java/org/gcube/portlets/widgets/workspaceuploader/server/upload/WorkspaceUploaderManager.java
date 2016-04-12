/**
 * 
 */
package org.gcube.portlets.widgets.workspaceuploader.server.upload;

import java.io.File;
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
import org.gcube.portlets.widgets.workspaceuploader.server.util.WsUtil;
import org.gcube.portlets.widgets.workspaceuploader.shared.WorkspaceUploaderItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class WorkspaceUploaderManager.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 2, 2015
 */
public class WorkspaceUploaderManager {
	
	public static Logger logger = LoggerFactory.getLogger(WorkspaceUploaderManager.class);
	
	
	/**
	 * Instantiates a new workspace uploader manager.
	 */
	public WorkspaceUploaderManager() {
	}
	
	
	/**
	 * Creates the workspace uploader file.
	 *
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
	private static WorkspaceUploaderItem createWorkspaceUploaderFile(HttpServletRequest request, final WorkspaceUploaderItem workspaceUploader, final HttpSession httpSession, final boolean isOvewrite, final Workspace wa, final File uploadFile, final String itemName,  final WorkspaceFolder destinationFolder, final String contentType, final long totalBytes) throws InternalErrorException, IOException{
		logger.debug("Creating WorkspaceUploaderFile...");

//		AbstractUploadProgressListener uploadProgressListener = new AbstractUploadProgressListener(request, new UploadProgress(), 70, 30);
//		final UploadProgressInputStream inputStream = new UploadProgressInputStream(StreamUtils.openInputStream(uploadFile), totalBytes);
//		inputStream.addListener(uploadProgressListener);
//		
//		workspaceUploader.setUploadProgress(uploadProgressListener.getUploadProgress());
//		
//		Thread th = new Thread(){
//			public void run() {
//				
//				try {
//					workspaceUploader.setUploadStatus(UPLOAD_STATUS.IN_PROGRESS);
//					workspaceUploader.setStatusDescription("Uploading "+itemName);
//					FolderItem createdItem = null;
//					
//					if(!isOvewrite){
//						logger.debug("Calling HL createExternalFile - [itemName: "+itemName+", contentType: "+contentType+"]");
//						createdItem = WorkspaceUtil.createExternalFile(destinationFolder, itemName, "", contentType, inputStream);
//					}
//					else{
//						createdItem  = overwriteItem(wa, itemName, inputStream, destinationFolder); 	//CASE OVERWRITE
//					}
//					
//					if(createdItem!=null){
//						WorkspaceUploadServlet.printElapsedTime(WorkspaceUploadServlet.startTime);
//						logger.debug("HL file "+createdItem.getName() + " uploaded correctly");
//						workspaceUploader.setUploadStatus(UPLOAD_STATUS.COMPLETED);
//						workspaceUploader.setStatusDescription("File \""+createdItem.getName()+"\" uploaded correctly");
//						WorkspaceUploadServlet.notifyUploadInSharedFolder(httpSession, wa, createdItem.getId(), createdItem.getParent().getId(), isOvewrite);
//					}else{
//						workspaceUploader.setUploadStatus(UPLOAD_STATUS.FAILED);
//						workspaceUploader.setStatusDescription("An error occurred when uploading \""+itemName + "\". Try again");
//					}
//					try {
//						WsUtil.putWorkspaceUploaderInSession(httpSession, workspaceUploader);
//					} catch (Exception e1) {
//						logger.error("Error during WorkspaceUploaderItem session update: ",e1);
//					}
//				} catch (InsufficientPrivilegesException
//						| ItemAlreadyExistException | InternalErrorException
//						| IOException e) {
//					logger.error("Error during upload: ",e);
//					workspaceUploader.setUploadStatus(UPLOAD_STATUS.FAILED);
//					workspaceUploader.setStatusDescription("An error occurred when uploading "+itemName+": "+e.getMessage());
//					try {
//						WsUtil.putWorkspaceUploaderInSession(httpSession, workspaceUploader);
//					} catch (Exception e1) {
//						logger.error("Error during WorkspaceUploaderItem session update: ",e1);
//					}
//				} catch (ItemNotFoundException | WrongItemTypeException | WorkspaceFolderNotFoundException | WrongDestinationException e) {
//					logger.error("Error during overwrite: ",e);
//					workspaceUploader.setUploadStatus(UPLOAD_STATUS.FAILED);
//					workspaceUploader.setStatusDescription("An error occurred when uploading "+itemName+": "+e.getMessage());
//					try {
//						WsUtil.putWorkspaceUploaderInSession(httpSession, workspaceUploader);
//					} catch (Exception e1) {
//						logger.error("Error during WorkspaceUploaderItem session update: ",e1);
//					}
//				}finally{
//					try {
//						StreamUtils.deleteTempFile(uploadFile);
//						WsUtil.eraseWorkspaceUploaderInSession(httpSession, workspaceUploader.getIdentifier());
//					} catch (Exception e2) {
//					}
//				}
//			};
//		};
//		
//		logger.debug("start Thread uploading: "+workspaceUploader.getIdentifier());
//		th.start();
//		logger.debug("returning: "+workspaceUploader);
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
	private static WorkspaceUploaderItem createWorkspaceUploaderArchive(HttpServletRequest request, final WorkspaceUploaderItem workspaceUploader, final HttpSession httpSession, final File uploadFile, final String itemName, final WorkspaceFolder destinationFolder, final long totalBytes) throws InternalErrorException, IOException{
		
//		AbstractUploadProgressListener uploadProgressListener = new AbstractUploadProgressListener(request, new UploadProgress(), 70, 30);
//		final UploadProgressInputStream inputStream = new UploadProgressInputStream(StreamUtils.openInputStream(uploadFile), totalBytes);
//		inputStream.addListener(uploadProgressListener);
//		
//		workspaceUploader.setUploadProgress(uploadProgressListener.getUploadProgress());
//		
//		Thread th = new Thread(){
//			public void run() {
//				
//				logger.info("calling upload archive - [itemName: "+itemName+"]");
//				try {
//					workspaceUploader.setUploadStatus(UPLOAD_STATUS.IN_PROGRESS);
//					UnzipUtil.unzip(destinationFolder, inputStream, itemName);
//					
//					logger.info("Archive "+itemName+ " imported correctly in "+destinationFolder.getPath());
//					workspaceUploader.setUploadStatus(UPLOAD_STATUS.COMPLETED);
//					workspaceUploader.setStatusDescription("Archive "+itemName+ " imported correctly in "+destinationFolder.getPath());
//	
//					try {
//						WsUtil.putWorkspaceUploaderInSession(httpSession, workspaceUploader);
//					} catch (Exception e1) {
//						logger.error("Error during WorkspaceUploaderItem session update: ",e1);
//					}
//				} catch (InternalErrorException e) {
//					logger.error("Error during uploading: ",e);
//					workspaceUploader.setUploadStatus(UPLOAD_STATUS.FAILED);
//					workspaceUploader.setStatusDescription("An error occurred when uploading "+itemName+": "+e.getMessage());
//					try {
//						WsUtil.putWorkspaceUploaderInSession(httpSession, workspaceUploader);
//					} catch (Exception e1) {
//						logger.error("Error during WorkspaceUploaderItem session update: ",e1);
//					}
//				}finally{
//					try {
//						StreamUtils.deleteTempFile(uploadFile);
//						WsUtil.eraseWorkspaceUploaderInSession(httpSession, workspaceUploader.getIdentifier());
//					} catch (Exception e2) {
//						// TODO: handle exception
//					}
//				}
//			};
//		};
//		
//		logger.debug("start Thread uploading: "+workspaceUploader.getIdentifier());
//		th.start();
//		logger.debug("returning: "+workspaceUploader);
		return workspaceUploader;
	}

	/**
	 * Upload file.
	 *
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
	public static WorkspaceUploaderItem uploadFile(HttpServletRequest request, WorkspaceUploaderItem workspaceUploader, Workspace wa, String itemName, File file, WorkspaceFolder destinationFolder, String contentType, boolean isOverwrite, long totolaBytes) throws Exception {
		
		try {
			return createWorkspaceUploaderFile(request, workspaceUploader, request.getSession(), isOverwrite, wa, file, itemName, destinationFolder, contentType, totolaBytes);
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
	public static WorkspaceUploaderItem uploadArchive(HttpServletRequest request, WorkspaceUploaderItem workspaceUploader, String itemName, File file, WorkspaceFolder destinationFolder, long totalBytes) throws Exception {
		
		try {
			return createWorkspaceUploaderArchive(request, workspaceUploader, request.getSession(), file, itemName, destinationFolder, totalBytes);
			
		} catch (Exception e) {
			logger.error("Error when uploading Archive to HL creation: ",e);
			throw new Exception("An error occurred during upload: "+itemName+". Try again");
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
	public static WorkspaceUploaderItem uploadFileStatus(HttpSession httpSession, WorkspaceUploaderItem workspaceUploader) throws Exception {
		return WsUtil.getWorkspaceUploaderInSession(httpSession, workspaceUploader.getIdentifier());
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
