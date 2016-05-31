/**
 * 
 */
package org.gcube.portlets.user.workspace.server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileUploadException;
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
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jan 21, 2014
 *
 */
public class LocalUploadServlet extends HttpServlet {

	/**
	 * 
	 */
	protected static final String UTF_8 = "UTF-8";

	public static final String UPLOAD_TYPE = ConstantsExplorer.UPLOAD_TYPE;

	public static final String ID_FOLDER = ConstantsExplorer.ID_FOLDER;

	public static final String UPLOAD_FORM_ELEMENT = ConstantsExplorer.UPLOAD_FORM_ELEMENT;

	public static final String IS_OVERWRITE = ConstantsExplorer.IS_OVERWRITE;
	
	public static final String FILE = "File";

	protected static Logger logger = Logger.getLogger(LocalUploadServlet.class);
	
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

		logger.trace("Workspace "+LocalUploadServlet.class+" ready.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		System.out.println("GET on LocalUploadServlet");
		logger.trace("GET method in LocalUploadServlet is running");
		String absolutePathFile = "";
		String destinationId = "";
		String uploadType = "";
		boolean isOverwrite = false;
		try{
			absolutePathFile = request.getParameter(UPLOAD_FORM_ELEMENT);
			destinationId = request.getParameter(ID_FOLDER);
			uploadType = request.getParameter(UPLOAD_TYPE);
			isOverwrite = Boolean.parseBoolean(request.getParameter(IS_OVERWRITE));
		
		}catch (Exception e) {
			logger.error("Error processing GET parameters", e);
			sendError(response, "Internal error: Error during request processing");
			return;
		}
		
		uploadData(request, response, absolutePathFile, destinationId, uploadType, isOverwrite);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("rawtypes")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.trace("POST method in LocalUploadServlet is running");
		String absolutePathFile = "";
		String destinationId = "";
		String uploadType = "";
		boolean isOverwrite = false;
		try{
		
			absolutePathFile = request.getParameter(UPLOAD_FORM_ELEMENT);
			destinationId = request.getParameter(ID_FOLDER);
			uploadType = request.getParameter(UPLOAD_TYPE);
			isOverwrite = Boolean.parseBoolean(request.getParameter(IS_OVERWRITE));
		
		}catch (Exception e) {
			logger.error("Error processing POST parameters", e);
			sendError(response, "Internal error: Error during request processing");
			return;
		}
		uploadData(request, response, absolutePathFile, destinationId, uploadType, isOverwrite);
	}
	
	
	
	private void uploadData(HttpServletRequest request, HttpServletResponse response, String absolutePathFile, String destinationId,String uploadType,boolean isOverwrite) throws ServletException, IOException{

		File file = null;
		InputStream fileUploadIS = null;
		
		try {
			logger.info("Upload servlet parameters: [uploadItem: "+absolutePathFile +", destinationId: "+destinationId +", uploadType: "+uploadType+", isOverwrite: "+isOverwrite+"]");
			
			if(absolutePathFile==null || absolutePathFile.isEmpty())
				throw new FileUploadException("Absolute path is null or empty");
			

			file = new File(absolutePathFile);
			
			fileUploadIS = openInputStream(file);
			
		} catch (Exception e) {
			logger.error("Error processing request in upload servlet", e);
			sendError(response, "Internal error: Error during request processing");
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
				itemName = WorkspaceUtil.getUniqueName(file.getName(), destinationFolder);
			else
				itemName = file.getName();
			
			String contentType = MimeTypeUtil.getMimeType(itemName, new BufferedInputStream(fileUploadIS));
			logger.trace("Content type (mime type): "+contentType + " unique name: "+itemName);
			
			String extension = FilenameUtils.getExtension(itemName);
			logger.trace("extension: "+extension);

			if(uploadType.compareTo(FILE)==0) {//IS FILE UPLOAD
				
				boolean isZipFile = MimeTypeUtil.isZipContentType(contentType);
				
				if(isZipFile && (extension.compareToIgnoreCase(D4ST)==0)){  //Create REPORT TEMPLATE

					String newItemName = itemName;
					logger.trace("createTemplate: "+newItemName);
					createTemplate(request.getSession(), wa, newItemName, openInputStream(file), destinationFolder, response, isOverwrite);
				
				}else if(isZipFile && (extension.compareToIgnoreCase(D4SR)==0)){ //Create REPORT

					String newItemName = itemName;
					logger.trace("createReport: "+newItemName);
					createReport(request.getSession(), wa, newItemName, openInputStream(file), destinationFolder, response, isOverwrite);
				}else{ //CREATE AN EXTERNAL FILE
					
					createExternalFile(request.getSession(), wa, itemName, openInputStream(file), destinationFolder, contentType, response, isOverwrite);
				}
			
			}else {//IS ARCHIVE UPLOAD
				
				if (MimeTypeUtil.isZipContentType(contentType)){
					logger.trace("Unziping content");
					UnzipUtil.unzip(destinationFolder, openInputStream(file), itemName);
					
					//TODO NOTIFY UPLOAD ARCHIVE
					sendMessage(response, "Archive "+absolutePathFile+" imported correctly in "+destinationFolder.getPath());
				} else
					createExternalFile(request.getSession(), wa, itemName, openInputStream(file), destinationFolder, contentType, response, isOverwrite);
			}

			file.delete();
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
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public FileInputStream openInputStream(File file) throws IOException {
		if (file.exists()) {
			if (file.isDirectory()) {
				throw new IOException("File '" + file
						+ "' exists but is a directory");
			}
			if (file.canRead() == false) {
				throw new IOException("File '" + file + "' cannot be read");
			}
		} else {
			throw new FileNotFoundException("File '" + file
					+ "' does not exist");
		}
		return new FileInputStream(file);
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
	
	private void createExternalFile(HttpSession httpSession, Workspace wa, String itemName, InputStream uploadItem, WorkspaceFolder destinationFolder, String contentType,  HttpServletResponse response, boolean isOverwrite) throws InternalErrorException, InsufficientPrivilegesException, ItemAlreadyExistException, IOException {
		
		FolderItem createdItem = null;
		
		if(!isOverwrite){
			//we need to recalculate the item name
			itemName = WorkspaceUtil.getUniqueName(itemName, destinationFolder);
			logger.trace("before calling createExternalFile - [itemName: "+itemName+", contentType: "+contentType+"]");
			createdItem = WorkspaceUtil.createExternalFile(destinationFolder, itemName, "", contentType, uploadItem);
			if(createdItem!=null)
				logger.info("Item "+createdItem.getName() + " uploaded correctly");
		}
		else{
			createdItem = overwriteItem(wa, itemName, uploadItem, destinationFolder); 	//CASE OVERWRITE
			if(createdItem!=null)
				logger.info("Overwrite item "+createdItem.getName() + " uploaded correctly");
		}
		
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
	
	
	protected void sendError(HttpServletResponse response, String message) throws IOException{
		try {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			HandlerResultMessage resultMessage = HandlerResultMessage.errorResult(message);
			response.getWriter().write(resultMessage.toString());
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
	
	protected void sendMessage(HttpServletResponse response, String message) throws IOException{
		try {
			response.setStatus(HttpServletResponse.SC_ACCEPTED);
			HandlerResultMessage resultMessage = HandlerResultMessage.okResult(message);
			response.getWriter().write(resultMessage.toString());
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
	
	protected void sendWarnMessage(HttpServletResponse response, String message) throws IOException{
		try {
			response.setStatus(HttpServletResponse.SC_ACCEPTED);
			HandlerResultMessage resultMessage = HandlerResultMessage.warnResult(message);
			response.getWriter().write(resultMessage.toString());
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
