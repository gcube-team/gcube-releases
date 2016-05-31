/**
 * 
 */
package org.gcube.portlets.user.workspace.server;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongItemTypeException;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.Report;
import org.gcube.common.homelibrary.home.workspace.folder.items.ReportTemplate;
import org.gcube.common.homelibrary.util.Extensions;
import org.gcube.common.homelibrary.util.MimeTypeUtil;
import org.gcube.common.homelibrary.util.WorkspaceUtil;
import org.gcube.common.homelibrary.util.zip.UnzipUtil;
import org.gcube.portlets.user.workspace.client.ConstantsExplorer;
import org.gcube.portlets.user.workspace.server.notifications.NotificationsUtil;
import org.gcube.portlets.user.workspace.server.util.WsUtil;
import org.gcube.portlets.user.workspace.shared.HandlerResultMessage;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class UploadServlet extends HttpServlet {

	public static final String UPLOAD_TYPE = ConstantsExplorer.UPLOAD_TYPE;

	public static final String ID_FOLDER = ConstantsExplorer.ID_FOLDER;

	public static final String UPLOAD_FORM_ELEMENT = ConstantsExplorer.UPLOAD_FORM_ELEMENT;

	public static final String IS_OVERWRITE = ConstantsExplorer.IS_OVERWRITE;
	
	public static final String FILE = "File";

	protected static Logger logger = Logger.getLogger(UploadServlet.class);
	
	public static final String D4ST = Extensions.REPORT_TEMPLATE.getName(); //extension of Report Template type
	public static final String D4SR = Extensions.REPORT.getName(); //extension of Report type
	/**
	 * 
	 */
	private static final long serialVersionUID = -4197748678713054285L;


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() throws ServletException {
		super.init();

		System.out.println("Workspace UploadServlet ready.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("GET on UploadServlet");
	}


	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("rawtypes")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		System.out.println("POST on UploadServlet");
		logger.trace("POST on UploadServlet");
		
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);

		FileItem uploadItem = null;
		String destinationId = null;
		String uploadType = null;
		boolean isOverwrite = false;
		
		try {
			logger.trace("parsing request");
			List items = upload.parseRequest(request);

			Iterator it = items.iterator();
			while (it.hasNext()) {
				FileItem item = (FileItem) it.next();

				if (UPLOAD_FORM_ELEMENT.equals(item.getFieldName())) {
					uploadItem = item;
				}

				if (item.isFormField() && ID_FOLDER.equals(item.getFieldName())){
					destinationId = item.getString();
				}
				
				if (item.isFormField() && UPLOAD_TYPE.equals(item.getFieldName())){
					uploadType = item.getString();
				}
				
				if (item.isFormField() && IS_OVERWRITE.equals(item.getFieldName())){
					isOverwrite = Boolean.parseBoolean(item.getString());     
				}

			}
			
			logger.info("Upload servlet parameters: [uploadItem: "+uploadItem +", destinationId: "+destinationId +", uploadType: "+uploadType+", isOverwrite: "+isOverwrite+"]");
		} catch (FileUploadException e) {
			logger.error("Error processing request in upload servlet", e);
			sendError(response, "Internal error: Error during request processing");
			return;
		}


		if (uploadItem == null) {
			logger.error("Error processing request in upload servlet: No file to upload");
			sendError(response, "Internal error: No file to upload");
			return;
		}

		if (destinationId == null) {
			logger.error("Error processing request in upload servlet: No destination folder id found");
			sendError(response, "Internal error: No destination folder id found");
			return;
		}

		logger.trace("destination folder id: "+destinationId);
		logger.trace("uploadType: "+uploadType);

		Workspace wa = null;
		try {
			wa = WsUtil.getWorkspace(request.getSession());
		} catch (Exception e) {
			logger.error("Error during workspace retrieving", e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing request in upload servlet");
			return;
		}

		if (wa == null) {
			logger.error("Now workspace found in session");
			sendError(response, "Internal error: No workspace in session");
			return;
		}

		WorkspaceItem item;
		try {
			item = wa.getItem(destinationId);
		} catch (ItemNotFoundException e) {
			logger.error("Error, no destination folder found", e);
			sendError(response, "Internal error: No destination folder found");
			return;
		}

		if (item.getType() != WorkspaceItemType.FOLDER && item.getType() != WorkspaceItemType.SHARED_FOLDER)  {
			logger.error("Error processing request in upload servlet: Wrong destination item");
			sendError(response, "Internal error: Wrong destination item");
			return;
		}

		WorkspaceFolder destinationFolder = (WorkspaceFolder) item;

		try {
			//we calculate an unique name for the current destination
			String itemName = "";
			
			if(!isOverwrite)
				itemName = WorkspaceUtil.getUniqueName(uploadItem.getName(), destinationFolder);
			else
				itemName = uploadItem.getName();
			
			String contentType = MimeTypeUtil.getMimeType(itemName, new BufferedInputStream(uploadItem.getInputStream()));
			logger.trace("Content type (mime type): "+contentType + " unique name: "+itemName);
			
//			System.out.println("Content type: "+contentType + " unique name: "+itemName);

			String extension = FilenameUtils.getExtension(itemName);
			logger.trace("extension: "+extension);
			
//			System.out.println("extension: "+extension);
			
			if(uploadType.compareTo(FILE)==0) {//IS FILE UPLOAD
				
				boolean isZipFile = MimeTypeUtil.isZipContentType(contentType);
				
				if(isZipFile && (extension.compareToIgnoreCase(D4ST)==0)){  //Create REPORT TEMPLATE
					
//					String newItemName = "";
//					
//					if(!isOverwrite)
//						newItemName = itemName.substring(0,itemName.lastIndexOf(D4ST)-1); //remove extension
//					else
//						newItemName = item.getName();
					
//					System.out.println("itemwithoutext " +itemwithoutext);
					
					String newItemName = itemName;
					
					logger.trace("createTemplate: "+newItemName);
					createTemplate(request.getSession(), wa, newItemName, uploadItem.getInputStream(), destinationFolder, response, isOverwrite);
				
				}else if(isZipFile && (extension.compareToIgnoreCase(D4SR)==0)){ //Create REPORT
					
//					String newItemName = "";
//					
//					if(!isOverwrite)
//						newItemName = itemName.substring(0,itemName.lastIndexOf(D4SR)-1); //remove extension
//					else
//						newItemName = item.getName();
					
//					System.out.println("itemwithoutext " +itemwithoutext);
					String newItemName = itemName;
					logger.trace("createReport: "+newItemName);
					createReport(request.getSession(), wa, newItemName, uploadItem.getInputStream(), destinationFolder, response, isOverwrite);
					
				}else{ //CREATE AN EXTERNAL FILE
					
					createExternalFile(request.getSession(), wa, itemName, uploadItem, destinationFolder, contentType, response, isOverwrite);
				}
			
			}else {//IS ARCHIVE UPLOAD
				
				if (MimeTypeUtil.isZipContentType(contentType)){
					logger.trace("Unziping content");
					UnzipUtil.unzip(destinationFolder, uploadItem.getInputStream(), itemName);
					
					//TODO NOTIFY UPLOAD ARCHIVE
					sendMessage(response, "Archive "+uploadItem.getName()+" imported correctly in "+destinationFolder.getPath());
				} else
					createExternalFile(request.getSession(), wa, itemName, uploadItem, destinationFolder, contentType, response, isOverwrite);
			}

			uploadItem.delete();
		} catch (InsufficientPrivilegesException e) {
			logger.error("Error creating elements", e);
			sendError(response, "Internal error: Insufficient privileges");
			return;
		} catch (InternalErrorException e) {
			logger.error("Error creating elements", e);
			sendError(response, "Internal error: "+e.getMessage());
			return;
		} catch (ItemAlreadyExistException e) {
			logger.error("Error creating elements", e);
			sendError(response, "Internal error: An item with that name already exists");
			return;
		}catch (Exception e) {
			logger.error("Error creating elements", e);
			sendError(response, "Internal error: An error occurred on uploading the file, try again later");
			return;
		}
	}
	
	/**
	 * 
	 * @param httpSession
	 * @param workspace
	 * @param itemId
	 * @param destinationFolderId
	 */
	private void notifyUploadInSharedFolder(final HttpSession httpSession, final Workspace workspace, final String itemId, final String destinationFolderId, final boolean isOverwrite){
		
		new Thread(){
			
			public void run() {
				
				WorkspaceItem sourceItem;
				try {
					sourceItem = workspace.getItem(itemId);
					String sourceSharedId = sourceItem.getIdSharedFolder();
					WorkspaceItem folderDestinationItem = workspace.getItem(destinationFolderId);
					
					NotificationsUtil.checkSendNotifyChangedItemToShare(httpSession, sourceItem, sourceSharedId, folderDestinationItem,isOverwrite);
					
					
				} catch (Exception e) {
					logger.error("Error in notifyUploadInSharedFolder", e);
			
				}	
				
			};
			
		}.start();	
	}
	
	private void createExternalFile(HttpSession httpSession, Workspace wa, String itemName, FileItem uploadItem, WorkspaceFolder destinationFolder, String contentType,  HttpServletResponse response, boolean isOverwrite) throws InternalErrorException, InsufficientPrivilegesException, ItemAlreadyExistException, IOException {
		
		FolderItem createdItem = null;
		
		if(!isOverwrite){
			//we need to recalculate the item name
			itemName = WorkspaceUtil.getUniqueName(uploadItem.getName(), destinationFolder);
			createdItem = WorkspaceUtil.createExternalFile(destinationFolder, itemName, "", contentType, uploadItem.getInputStream());
		}
		else
			createdItem = overwriteItem(wa, itemName, uploadItem.getInputStream(), destinationFolder); 	//CASE OVERWRITE
		
		if(createdItem!=null){
			notifyUploadInSharedFolder(httpSession,wa,createdItem.getId(),destinationFolder.getId(), isOverwrite);
			
			sendMessage(response, "File "+createdItem.getName()+" imported correctly in "+destinationFolder.getPath());
		}
		else
			sendError(response,"Internal error: Workspace Item Not Found");
	}
	
	
	private FolderItem overwriteItem(Workspace wa, String itemName, InputStream fileData, WorkspaceFolder destinationFolder){
		
		FolderItem overwriteItem = null;
	
		try {
			logger.trace("case overwriting item.. "+itemName);
			overwriteItem = (FolderItem) wa.find(itemName, destinationFolder.getId());
			logger.trace("overwriteItem item was found, id is: "+overwriteItem.getId());
			wa.updateItem(overwriteItem.getId(), fileData);
			logger.trace("updateItem with id: "+overwriteItem.getId()+ ", is completed");
		} catch (ItemNotFoundException e) {
			logger.error("Error in createExternalFile, ItemNotFoundException", e);
		} catch (WrongItemTypeException e) {
			logger.error("Error in createExternalFile, WrongItemTypeException", e);
		} catch (WorkspaceFolderNotFoundException e) {
			logger.error("Error in createExternalFile, WorkspaceFolderNotFoundException", e);
		} catch (WrongDestinationException e) {
			logger.error("Error in createExternalFile, WrongDestinationException", e);
		} catch (InsufficientPrivilegesException e) {
			logger.error("Error in createExternalFile, InsufficientPrivilegesException", e);
		} catch (ItemAlreadyExistException e) {
			logger.error("Error in createExternalFile, ItemAlreadyExistException", e);
		} catch (InternalErrorException e) {
			logger.error("Error in createExternalFile, InternalErrorException", e);
		}catch (Exception e) {
			logger.error("Error in createExternalFile, Exception", e);
		}
		
		return overwriteItem;
		
	}

	
	private void createReport(HttpSession httpSession, Workspace wa, String itemName, InputStream stream, WorkspaceFolder destinationFolder, HttpServletResponse response, boolean isOverwrite) throws InsufficientPrivilegesException, ItemAlreadyExistException, InternalErrorException, IOException{
		
		try {
			
			Report report = null;
			
			if(!isOverwrite){
				itemName = WorkspaceUtil.getUniqueName(itemName, destinationFolder);
				report = wa.createReport(itemName, "", Calendar.getInstance(), Calendar.getInstance(), "", "", "", 0, "", stream, destinationFolder.getId());
				
				notifyUploadInSharedFolder(httpSession,wa,report.getId(),destinationFolder.getId(), isOverwrite);
				
				sendMessage(response, "File "+report.getName()+" imported correctly in "+destinationFolder.getPath());
			}
			else{ 	//CASE OVERWRITE
				FolderItem rep = overwriteItem(wa, itemName, stream, destinationFolder);
				
				if(rep!=null){
					
					notifyUploadInSharedFolder(httpSession,wa,rep.getId(),destinationFolder.getId(), isOverwrite);
					
					sendMessage(response, "File "+rep.getName()+" imported correctly in "+destinationFolder.getPath());
				}
				else
					sendError(response,"Internal error: Workspace Item Not Found");
			
			}
		} catch (WrongDestinationException e) {
			logger.error("Error creating elements", e);
			sendError(response, "Internal error: Wrong Destination");
		} catch (WorkspaceFolderNotFoundException e) {
			logger.error("Error creating elements", e);
			sendError(response, "Internal error: Workspace Folder Not Found");
		}
		
	}
	
	private void createTemplate(HttpSession httpSession, Workspace wa, String itemName, InputStream stream, WorkspaceFolder destinationFolder, HttpServletResponse response, boolean isOverwrite) throws InsufficientPrivilegesException, ItemAlreadyExistException, InternalErrorException, IOException{
		
		try {
			
			ReportTemplate template = null;
			
			if(!isOverwrite){
				itemName = WorkspaceUtil.getUniqueName(itemName, destinationFolder);
				template = wa.createReportTemplate(itemName, "", Calendar.getInstance(), Calendar.getInstance(), "", "", 0, "", stream, destinationFolder.getId());
				
				notifyUploadInSharedFolder(httpSession,wa,template.getId(),destinationFolder.getId(), isOverwrite);
				
				sendMessage(response, "File "+template.getName()+" imported correctly in "+destinationFolder.getPath());
				
			}else{ 	//CASE OVERWRITE
				FolderItem rep = overwriteItem(wa, itemName, stream, destinationFolder);
				
				if(rep!=null){
					
					notifyUploadInSharedFolder(httpSession,wa,rep.getId(),destinationFolder.getId(), isOverwrite);
					
					sendMessage(response, "File "+rep.getName()+" imported correctly in "+destinationFolder.getPath());
				}
				else
					sendError(response,"Internal error: Workspace Item Not Found");
			
			}

		} catch (WrongDestinationException e) {
			logger.error("Error creating elements", e);
			sendError(response, "Internal error: Wrong Destination");
		} catch (WorkspaceFolderNotFoundException e) {
			logger.error("Error creating elements", e);
			sendError(response, "Internal error: Workspace Folder Not Found");
		}
		
	}
	
	
	protected void sendError(HttpServletResponse response, String message) throws IOException
	{
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		HandlerResultMessage resultMessage = HandlerResultMessage.errorResult(message);
		response.getWriter().write(resultMessage.toString());
		response.flushBuffer();
	}
	
	protected void sendMessage(HttpServletResponse response, String message) throws IOException
	{
		response.setStatus(HttpServletResponse.SC_ACCEPTED);
		HandlerResultMessage resultMessage = HandlerResultMessage.okResult(message);
		response.getWriter().write(resultMessage.toString());
		response.flushBuffer();
	}
	
	protected void sendWarnMessage(HttpServletResponse response, String message) throws IOException
	{
		response.setStatus(HttpServletResponse.SC_ACCEPTED);
		HandlerResultMessage resultMessage = HandlerResultMessage.warnResult(message);
		response.getWriter().write(resultMessage.toString());
		response.flushBuffer();
	}


}
