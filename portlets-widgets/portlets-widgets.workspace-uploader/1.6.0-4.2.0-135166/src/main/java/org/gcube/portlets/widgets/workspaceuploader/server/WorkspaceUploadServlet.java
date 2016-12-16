/**
 *
 */
package org.gcube.portlets.widgets.workspaceuploader.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItemStream;
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
import org.gcube.common.homelibrary.util.WorkspaceUtil;
import org.gcube.common.portal.PortalContext;
import org.gcube.portlets.widgets.workspaceuploader.client.ConstantsWorkspaceUploader;
import org.gcube.portlets.widgets.workspaceuploader.server.notification.NotificationsWorkspaceUploader;
import org.gcube.portlets.widgets.workspaceuploader.server.notification.NotificationsWorkspaceUploaderProducer;
import org.gcube.portlets.widgets.workspaceuploader.server.util.WsUtil;
import org.gcube.portlets.widgets.workspaceuploader.shared.HandlerResultMessage;
import org.gcube.portlets.widgets.workspaceuploader.shared.WorkspaceUploaderItem;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jan 21, 2014
 *
 */
public class WorkspaceUploadServlet extends HttpServlet {

	/**
	 *
	 */
	private static final long serialVersionUID = -7861878364437065019L;

	protected static final String UTF_8 = "UTF-8";

	public static final String UPLOAD_TYPE = ConstantsWorkspaceUploader.UPLOAD_TYPE;

	public static final String ID_FOLDER = ConstantsWorkspaceUploader.ID_FOLDER;

	public static final String UPLOAD_FORM_ELEMENT = ConstantsWorkspaceUploader.UPLOAD_FORM_ELEMENT;

	public static final String CLIENT_UPLOAD_KEY = ConstantsWorkspaceUploader.CLIENT_UPLOAD_KEYS;

	public static final String IS_OVERWRITE = ConstantsWorkspaceUploader.IS_OVERWRITE;

	public static final String FILE = "File";

	public static final String D4ST = Extensions.REPORT_TEMPLATE.getName(); //extension of Report Template type
	public static final String D4SR = Extensions.REPORT.getName(); //extension of Report type

	public static Logger logger = LoggerFactory.getLogger(WorkspaceUploadServlet.class);

	/*private static String UPLOAD_LOCATION = System.getProperty("java.io.tmpdir");
	@Override
	public void init() throws ServletException {
		try{
			System.out.println(WorkspaceUploadServlet.class.getName()+" ready.");
			logger.debug(WorkspaceUploadServlet.class.getName()+" ready.");
			if (System.getenv("CATALINA_TMPDIR") != null && System.getenv("CATALINA_TMPDIR").compareTo("") != 0) {
				UPLOAD_LOCATION = System.getenv("CATALINA_TMPDIR");
			}
			super.init();

		}catch(Exception e){
			e.printStackTrace();
		}
	}*/

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		System.out.println("GET on LocalUploadServlet");
		System.out.println("GET method in WorkspaceUploadServlet is running");
		sendError(response, "Internal error: GET method not supported");
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("rawtypes")
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

//		System.out.println("POST on UploadServlet");
//		logger.info("POST on UploadServlet");
//
//		if (!ServletFileUpload.isMultipartContent(request)) {
//			logger.error("ERROR: multipart request not found");
//			sendError(response, "ERROR: multipart request not found");
//		}
//
//		String destinationId = null;
//		String uploadType = null;
//		boolean isOverwrite = false;
//		String clientUploadKey = null;
//		FileItemStream uploadItem = null;
//
//		FileItemFactory factory = new DiskFileItemFactory();
//		ServletFileUpload servletFileUpload = new ServletFileUpload(factory);
//
//		try {
//
//			FileItemIterator fileItemIterator = servletFileUpload.getItemIterator(request);
//
//			//GET FILE STREAM
//			while (fileItemIterator.hasNext()) {
//				FileItemStream item = fileItemIterator.next();
//
//				if (item.isFormField() && ID_FOLDER.equals(item.getFieldName())){
//					destinationId = Streams.asString(item.openStream());
//					logger.debug("ID_FOLDER OK");
//				}
//
//				if (item.isFormField() && UPLOAD_TYPE.equals(item.getFieldName())){
//					uploadType = Streams.asString(item.openStream());
//					logger.debug("UPLOAD_TYPE OK");
//				}
//
//				if (item.isFormField() && IS_OVERWRITE.equals(item.getFieldName())){
//					isOverwrite = Boolean.parseBoolean(Streams.asString(item.openStream()));
//					logger.debug("IS_OVERWRITE OK");
//				}
//
//				if(item.isFormField() && CLIENT_UPLOAD_KEY.equals(item.getFieldName())){
//					clientUploadKey = Streams.asString(item.openStream());
//					logger.debug("CLIENT_UPLOAD_KEY OK");
//				}
//
//				//MUST BE THE LAST PARAMETER TRASMITTED
//				if (UPLOAD_FORM_ELEMENT.equals(item.getFieldName())){
//					logger.debug(UPLOAD_FORM_ELEMENT);
//					uploadItem = item;
//					break;
//				}
//			}
//			uploadData(request, response, uploadItem, destinationId, uploadType, clientUploadKey, isOverwrite);
//		} catch (FileUploadException e) {
//			logger.error("Error processing request in upload servlet", e);
//			sendError(response, "Internal error: Error during request processing");
//			return;
//		}
	}



	private void uploadData(HttpServletRequest request, HttpServletResponse response, FileItemStream uploadItem, String destinationId,String uploadType,String clientUploadKey, boolean isOverwrite) throws ServletException, IOException{

//		String fileName = uploadItem.getName();
//		logger.info("Upload servlet parameters: [fileName: "+fileName+ ", destinationId: "+destinationId +", uploadType: "+uploadType+", isOverwrite: "+isOverwrite+", clientUploadKey: "+clientUploadKey+"]");
//
//		if (uploadType == null || uploadType.isEmpty()) {
//			logger.error("Error processing request in upload servlet: No upload type found");
//			sendError(response, "Internal error: No upload type found");
//			return;
//		}
//
//		if(clientUploadKey==null || clientUploadKey.isEmpty()){
//			logger.error("Error processing request in upload servlet: No client upload key found");
//			sendError(response, "Internal error: No client upload key found");
//			return;
//		}
//
//
//		Workspace wa = null;
//		try {
//			logger.debug("getWorkspace from HL");
//			wa = WsUtil.getWorkspace(request.getSession());
//		} catch (Exception e) {
//			logger.error("Error during workspace retrieving", e);
//			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing request in upload servlet");
//			return;
//		}
//
//		if (wa == null) {
//			logger.error("Now workspace found in session");
//			sendError(response, "Internal error: No workspace in session");
//			return;
//		}
//
//		WorkspaceItem item;
//		try {
//			logger.debug("getWorkspace Item from HL");
//			item = wa.getItem(destinationId);
//		} catch (ItemNotFoundException e) {
//			logger.error("Error, no destination folder found", e);
//			sendError(response, "Internal error: No destination folder found");
//			return;
//		}
//
//		if (item.getType() != WorkspaceItemType.FOLDER && item.getType() != WorkspaceItemType.SHARED_FOLDER)  {
//			logger.error("Error processing request in upload servlet: Wrong destination item");
//			sendError(response, "Internal error: Wrong destination item");
//			return;
//		}
//
//		WorkspaceFolder destinationFolder = (WorkspaceFolder) item;
//
//		try {
//			//we calculate an unique name for the current destination
//			String itemName = "";
//
//			logger.debug("getItemName from HL");
//			if(!isOverwrite)
//				itemName = WorkspaceUtil.getUniqueName(fileName, destinationFolder);
//			else
//				itemName = fileName;
//
//			Long size = Long.parseLong(request.getHeader("Content-Length"));
//			logger.debug("size: " + size + " bytes");
//
//
//			//Create Item Uploader to read progress
//			WorkspaceUploadFile wsUploadFile = new WorkspaceUploadFile(destinationFolder.getId(), itemName);
//			String identifier = wsUploadFile.hashCode()+getRandom()+"";
//			WorkspaceUploaderItem workspaceUploader = new WorkspaceUploaderItem(identifier);
//			workspaceUploader.setUploadStatus(UPLOAD_STATUS.WAIT);
//			workspaceUploader.setFile(wsUploadFile);
//			workspaceUploader.setStatusDescription("Uploading "+itemName+" at 0%");
//			workspaceUploader.setClientUploadKey(clientUploadKey);
//
//			//instanciate the progress listener
//			AbstractUploadProgressListener uploadProgressListener = new AbstractUploadProgressListener(request, new UploadProgress(), 0, 70);
//			final UploadProgressInputStream inputStream = new UploadProgressInputStream(uploadItem.openStream(), size);
//			inputStream.addListener(uploadProgressListener);
//			workspaceUploader.setUploadProgress(uploadProgressListener.getUploadProgress());
//			saveWorkspaceUploaderInSession(workspaceUploader, request.getSession());
//
//			printStartTime();
//			File file = StreamUtils.stream2file(inputStream, uploadItem.getName()+UUID.randomUUID(), ".tmp");
//
//			logger.debug("getMimeType from HL");
//			String contentType = MimeTypeUtil.getMimeType(itemName, StreamUtils.openInputStream(file));
//			logger.debug("Content type (mime type): "+contentType + " unique name: "+itemName);
//
//			String extension = FilenameUtils.getExtension(itemName);
//			logger.debug("extension: "+extension);
//
//			if(uploadType.compareTo(FILE)==0) {//IS FILE UPLOAD
//
//				boolean isZipFile = MimeTypeUtil.isZipContentType(contentType);
//
//				if(isZipFile && (extension.compareToIgnoreCase(D4ST)==0)){  //Create REPORT TEMPLATE
//
//					String newItemName = itemName;
//					logger.debug("createTemplate: "+newItemName);
//					createTemplate(request.getSession(), wa, newItemName, file, destinationFolder, response, isOverwrite);
//
//				}else if(isZipFile && (extension.compareToIgnoreCase(D4SR)==0)){ //Create REPORT
//
//					String newItemName = itemName;
//					logger.debug("createReport: "+newItemName);
//					createReport(request.getSession(), wa, newItemName, file, destinationFolder, response, isOverwrite);
//				}else{ //CREATE AN EXTERNAL FILE
//
//					workspaceUploader = WorkspaceUploaderManager.uploadFile(request, workspaceUploader, wa, itemName, file, destinationFolder, contentType, isOverwrite, size);
//
//					if(workspaceUploader==null)
//						throw new Exception("Error when creating uploader, it is null!");
//
//					sendMessage(response, workspaceUploader.getIdentifier());
//				}
//
//			}else {//IS ARCHIVE UPLOAD
//
//				if (MimeTypeUtil.isZipContentType(contentType)){
//					logger.debug("Unziping content");
//					workspaceUploader = WorkspaceUploaderManager.uploadArchive(request, workspaceUploader, itemName, file, destinationFolder, size);
//
//					if(workspaceUploader==null)
//						throw new Exception("Error when creating uploader, it is null!");
//
//					sendMessage(response, workspaceUploader.getIdentifier());
//				} else{
//					workspaceUploader = WorkspaceUploaderManager.uploadFile(request, workspaceUploader, wa, itemName, file, destinationFolder, contentType, isOverwrite, size);
//
//					if(workspaceUploader==null)
//						throw new Exception("Error when creating uploader, it is null!");
//
//					sendMessage(response, workspaceUploader.getIdentifier());
//				}
//			}
//
////			file.delete();
//		} catch (InsufficientPrivilegesException e) {
//			logger.error("Error creating elements", e);
//			sendError(response, "Internal error: Insufficient privileges");
//			return;
//		} catch (InternalErrorException e) {
//			logger.error("Error creating elements", e);
//			sendError(response, "Internal error: "+e.getMessage());
//			return;
//		} catch (ItemAlreadyExistException e) {
//			logger.error("Error creating elements", e);
//			sendError(response, "Internal error: An item with that name already exists");
//			return;
//		}catch (Exception e) {
//			logger.error("Error creating elements", e);
//			sendError(response, "Internal error: An error occurred on uploading the file, try again later");
//			return;
//		}

	}

	public static WorkspaceUploaderItem saveWorkspaceUploaderInSession(WorkspaceUploaderItem workspaceUploader, HttpSession httpSession) throws Exception {

		if(workspaceUploader!=null){
			try {
				WsUtil.putWorkspaceUploaderInSession(httpSession, workspaceUploader);
				return workspaceUploader;
			} catch (Exception e) {
				logger.error("Error during WorkspaceUploaderItem save in session workspace uploader: "+workspaceUploader,e);
				throw new Exception("An error occurred in the upload. Try again");
			}
		}
		throw new Exception("An error occurredin the upload. Workspace Uploader not found. Abort and try again");
	}

	 /**
	 * Gets the random.
	 *
	 * @return the random
	 */
	private static int getRandom(){
		 Random randomGenerator = new Random();
		 return randomGenerator.nextInt(Integer.MAX_VALUE);
    }

	//TEST TIME
	public static Long startTime = new Long(0);
	//TEST TIME
	public static Long printStartTime(){
		startTime =  System.currentTimeMillis();
		logger.debug("Start time: "+startTime);
		return startTime;
	}
	//TEST TIME
	public static void printElapsedTime(long startTime){
		Long endTime = System.currentTimeMillis() - startTime;
		String time = String.format("%d msc %d sec", endTime, TimeUnit.MILLISECONDS.toSeconds(endTime));
		logger.debug("Elapsed Time: "+time);
	}


	/**
	 * Notify upload in shared folder.
	 *
	 * @param request the request
	 * @param httpSession the http session
	 * @param workspace the workspace
	 * @param itemId the item id
	 * @param destinationFolderId the destination folder id
	 * @param isOverwrite the is overwrite
	 */
	public static void notifyUploadInSharedFolder(final String scopeGroupId, final HttpServletRequest request, final HttpSession httpSession, final Workspace workspace, final String itemId, final String destinationFolderId, final boolean isOverwrite){
		final GCubeUser currUser = PortalContext.getConfiguration().getCurrentUser(request);
		System.out.println("\n\n*****\n\n notifyUploadInSharedFolder currUser=" + currUser.toString());
		boolean requestIsNull = request==null;
		logger.trace("[2] HttpServletRequest is: null? "+requestIsNull+", URI: "+request.getRequestURI() +", ServerName: "+request.getServerName());
		final NotificationsWorkspaceUploaderProducer np = new NotificationsWorkspaceUploaderProducer(scopeGroupId, httpSession,request);
		new Thread(){
			public void run() {
				WorkspaceItem sourceItem;
				try {
					sourceItem = workspace.getItem(itemId);
					String sourceSharedId = sourceItem.getIdSharedFolder();
					WorkspaceItem folderDestinationItem = workspace.getItem(destinationFolderId);
					logger.trace("[3] HttpServletRequest is: URI: "+request.getRequestURI() +", ServerName: "+request.getServerName());
					NotificationsWorkspaceUploader.checkSendNotifyChangedItemToShare(currUser, scopeGroupId, np, httpSession, sourceItem, sourceSharedId, folderDestinationItem,isOverwrite);
				} catch (Exception e) {
					logger.error("Error in notifyUploadInSharedFolder", e);
				}
			};

		}.start();
	}


	private FolderItem overwriteItem(Workspace wa, String itemName, InputStream fileData, WorkspaceFolder destinationFolder){

		FolderItem overwriteItem = null;

		try {
			logger.debug("case overwriting item.. "+itemName);
			overwriteItem = (FolderItem) wa.find(itemName, destinationFolder.getId());
			logger.debug("overwriteItem item was found, id is: "+overwriteItem.getId());
			wa.updateItem(overwriteItem.getId(), fileData);
			logger.debug("updateItem with id: "+overwriteItem.getId()+ ", is completed");
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


	private void createReport(String scopeGroupId, final HttpServletRequest request, HttpSession httpSession, Workspace wa, String itemName, File file, WorkspaceFolder destinationFolder, HttpServletResponse response, boolean isOverwrite) throws InsufficientPrivilegesException, ItemAlreadyExistException, InternalErrorException, IOException{

		try {

			Report report = null;

			if(!isOverwrite){
				itemName = WorkspaceUtil.getUniqueName(itemName, destinationFolder);
				report = wa.createReport(itemName, "", Calendar.getInstance(), Calendar.getInstance(), "", "", "", 0, "", StreamUtils.openInputStream(file), destinationFolder.getId());

				notifyUploadInSharedFolder(scopeGroupId, request, httpSession,wa,report.getId(),destinationFolder.getId(), isOverwrite);
				sendMessage(response, "File "+report.getName()+" imported correctly in "+destinationFolder.getPath());
			}
			else{ 	//CASE OVERWRITE
				FolderItem rep = overwriteItem(wa, itemName, StreamUtils.openInputStream(file), destinationFolder);

				if(rep!=null){

					notifyUploadInSharedFolder(scopeGroupId, request, httpSession,wa,rep.getId(),destinationFolder.getId(), isOverwrite);
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
		}finally{
			try {
				StreamUtils.deleteTempFile(file);
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}

	}

	private void createTemplate(String scopeGroupId, final HttpServletRequest request, HttpSession httpSession, Workspace wa, String itemName, File file, WorkspaceFolder destinationFolder, HttpServletResponse response, boolean isOverwrite) throws InsufficientPrivilegesException, ItemAlreadyExistException, InternalErrorException, IOException{

		try {

			ReportTemplate template = null;

			if(!isOverwrite){
				itemName = WorkspaceUtil.getUniqueName(itemName, destinationFolder);
				template = wa.createReportTemplate(itemName, "", Calendar.getInstance(), Calendar.getInstance(), "", "", 0, "", StreamUtils.openInputStream(file), destinationFolder.getId());

				notifyUploadInSharedFolder(scopeGroupId, request, httpSession,wa,template.getId(),destinationFolder.getId(), isOverwrite);
				sendMessage(response, "File "+template.getName()+" imported correctly in "+destinationFolder.getPath());

			}else{ 	//CASE OVERWRITE
				FolderItem rep = overwriteItem(wa, itemName, StreamUtils.openInputStream(file), destinationFolder);

				if(rep!=null){

					notifyUploadInSharedFolder(scopeGroupId, request, httpSession,wa,rep.getId(),destinationFolder.getId(), isOverwrite);
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
		}finally{
			try {
				StreamUtils.deleteTempFile(file);
			} catch (Exception e2) {
				// TODO: handle exception
			}
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
