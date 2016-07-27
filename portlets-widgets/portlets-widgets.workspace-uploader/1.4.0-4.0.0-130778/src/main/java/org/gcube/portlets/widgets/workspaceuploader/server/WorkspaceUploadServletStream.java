/**
 *
 */
package org.gcube.portlets.widgets.workspaceuploader.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.FilenameUtils;
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
import org.gcube.portlets.widgets.workspaceuploader.client.ConstantsWorkspaceUploader;
import org.gcube.portlets.widgets.workspaceuploader.server.notification.NotificationsWorkspaceUploader;
import org.gcube.portlets.widgets.workspaceuploader.server.notification.NotificationsWorkspaceUploaderProducer;
import org.gcube.portlets.widgets.workspaceuploader.server.upload.AbstractUploadProgressListener;
import org.gcube.portlets.widgets.workspaceuploader.server.upload.MemoryUploadListener;
import org.gcube.portlets.widgets.workspaceuploader.server.upload.UploadCanceledException;
import org.gcube.portlets.widgets.workspaceuploader.server.upload.UploadProgressInputStream;
import org.gcube.portlets.widgets.workspaceuploader.server.upload.UploadProgressListener;
import org.gcube.portlets.widgets.workspaceuploader.server.upload.WorkspaceUploaderMng;
import org.gcube.portlets.widgets.workspaceuploader.server.util.WsUtil;
import org.gcube.portlets.widgets.workspaceuploader.shared.HandlerResultMessage;
import org.gcube.portlets.widgets.workspaceuploader.shared.WorkspaceUploadFile;
import org.gcube.portlets.widgets.workspaceuploader.shared.WorkspaceUploaderItem;
import org.gcube.portlets.widgets.workspaceuploader.shared.WorkspaceUploaderItem.UPLOAD_STATUS;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class WorkspaceUploadServletStream.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 10, 2016
 */
public class WorkspaceUploadServletStream extends HttpServlet implements Servlet{

	public static final String UNKNOWN_UNKNOWN = "unknown/unknown";

	private static final long serialVersionUID = 1778008252774571216L;

	protected static final String UTF_8 = "UTF-8";

	public static final String UPLOAD_TYPE = ConstantsWorkspaceUploader.UPLOAD_TYPE;

	public static final String ID_FOLDER = ConstantsWorkspaceUploader.ID_FOLDER;

	public static final String UPLOAD_FORM_ELEMENT = ConstantsWorkspaceUploader.UPLOAD_FORM_ELEMENT;

	public static final String CLIENT_UPLOAD_KEYS = ConstantsWorkspaceUploader.CLIENT_UPLOAD_KEYS;

	public static final String JSON_CLIENT_KEYS = ConstantsWorkspaceUploader.JSON_CLIENT_KEYS;

	public static final String IS_OVERWRITE = ConstantsWorkspaceUploader.IS_OVERWRITE;

	public static final String CANCEL_UPLOAD = ConstantsWorkspaceUploader.CANCEL_UPLOAD;

	public static final String FILE = "File";

	public static final String D4ST = Extensions.REPORT_TEMPLATE.getName(); //extension of Report Template type
	public static final String D4SR = Extensions.REPORT.getName(); //extension of Report type

	public static Logger logger = LoggerFactory.getLogger(WorkspaceUploadServletStream.class);

	private static boolean appEngine = false;

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.servlet.GenericServlet#init()
	 */
	@Override
	public void init() throws ServletException {
		super.init();

		String appe = getInitParameter("appEngine");
		if (appe != null) {
			appEngine = "true".equalsIgnoreCase(appe);
		} else {
			appEngine = isAppEngine();
		}

		logger.debug("init: appEngine is "+appEngine);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.debug("GET method in "+WorkspaceUploadServletStream.class.getName()+" is running");
		String clientUploadKey = request.getParameter(CLIENT_UPLOAD_KEYS);
        if (clientUploadKey == null){
        	sendError(response, "Internal error: UPLOAD KEY NOT FOUND");
        	return;
        }
        logger.debug("GET method CLIENT_UPLOAD_KEY "+clientUploadKey);

        boolean cancelUpload = Boolean.parseBoolean(request.getParameter(CANCEL_UPLOAD));
        logger.debug("GET method CANCEL_UPLOAD "+cancelUpload);
        if (cancelUpload) {
          boolean cancelled = cancelUpload(request.getSession(), clientUploadKey);
          if(cancelled){
        	  sendMessage(response, "Upload aborted "+clientUploadKey);
//        	  try {
////        		removeCurrentListener(request.getSession(), clientUploadKey);
//				WsUtil.eraseWorkspaceUploaderInSession(request.getSession(), clientUploadKey);
//			  }catch (Exception e) {
//				  logger.warn("An error occurred during removing cancelled upload from session ");
//			  }
          }
          else
        	  sendWarnMessage(response, "Upload aborted for id: "+clientUploadKey +" has skipped, already aborted or completed?");
        }else
        	logger.debug(CANCEL_UPLOAD + " param not found");
        return;
	}

	/**
	 * {@inheritDoc}
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		System.out.println("POST on UploadServlet");
		logger.info("POST on UploadServlet");
		boolean requestIsNull = request==null;
		logger.trace("[1] HttpServletRequest is: null? "+requestIsNull+", URI: "+request.getRequestURI() +", ServerName: "+request.getServerName());

		if (!ServletFileUpload.isMultipartContent(request)) {
			logger.error("ERROR: multipart request not found");
			sendError(response, "ERROR: multipart request not found");
		}

		try {

			logger.info("UPLOAD-SERVLET starting");
		    HttpSession session = request.getSession();
		    logger.info("UPLOAD-SERVLET session: "+session);
		    logger.debug("UPLOAD-SERVLET (" + session.getId() + ") new upload request received.");
			String destinationId = null;
			String uploadType = null;
			boolean isOverwrite = false;
	//		String clientUploadKey = null;
			FileItemStream uploadItem = null;
			ArrayList<String> listClientUploadKeys = null;

			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload servletFileUpload = new ServletFileUpload(factory);

			/**
			 * An iterator to instances of <code>FileItemStream</code>
			 * parsed from the request, in the order that they were
			 *transmitted.
			 */
			FileItemIterator fileItemIterator = servletFileUpload.getItemIterator(request);

			int uploadItemsCnt = 0;

			//GET FILE STREAM
			while (fileItemIterator.hasNext()) {
				FileItemStream item = fileItemIterator.next();

				if (item.isFormField() && ID_FOLDER.equals(item.getFieldName())){
					destinationId = Streams.asString(item.openStream());
					logger.debug("ID_FOLDER OK "+destinationId);
				}

				if (item.isFormField() && UPLOAD_TYPE.equals(item.getFieldName())){
					uploadType = Streams.asString(item.openStream());
					logger.debug("UPLOAD_TYPE OK " +uploadType);
				}

				if (item.isFormField() && IS_OVERWRITE.equals(item.getFieldName())){
					isOverwrite = Boolean.parseBoolean(Streams.asString(item.openStream()));
					logger.debug("IS_OVERWRITE OK "+ isOverwrite);
				}

				if(item.isFormField() && CLIENT_UPLOAD_KEYS.equals(item.getFieldName())){
					String jsonClientUploadKey = Streams.asString(item.openStream());
					logger.debug("CLIENT_UPLOAD_KEY OK "+jsonClientUploadKey);
					LinkedHashMap<String, String> mapKeys = parseJSONClientUploadKeys(jsonClientUploadKey);
					listClientUploadKeys = new ArrayList<String>(mapKeys.keySet());
					removeListenersIfDone(session, listClientUploadKeys);
					for (String clientUploadKey : listClientUploadKeys) {
						String fileName = mapKeys.get(clientUploadKey);
						WorkspaceUploaderItem workspaceUploader = createNewWorkspaceUploader(clientUploadKey,destinationId,mapKeys.get(clientUploadKey),isOverwrite);
						logger.debug("created "+workspaceUploader);
						saveWorkspaceUploaderStatus(workspaceUploader, UPLOAD_STATUS.WAIT, "Uploading "+fileName+" at 0%", request.getSession());
					}
				}

				//MUST BE THE LAST PARAMETER TRASMITTED
				if (UPLOAD_FORM_ELEMENT.equals(item.getFieldName())){
					uploadItem = item;
					logger.debug("UPLOAD_FORM_ELEMENT OK "+uploadItem.getName());
//					break;
					uploadData(request, response, uploadItem, destinationId, uploadType, listClientUploadKeys.get(uploadItemsCnt), isOverwrite);
					uploadItemsCnt++;
				}
			}

		} catch (FileUploadException e) {
			logger.error("Error processing request in upload servlet", e);
			sendError(response, "Internal error: Error during request processing");
			return;
		} catch (Exception e) {
			logger.error("Error processing request in upload servlet", e);
			sendError(response, "Internal error: Error during request processing");
			return;
		}
	}



	/**
	 * Removes the listener if done.
	 *
	 * @param session the session
	 * @param keys the keys
	 */
	private void removeListenersIfDone(HttpSession session, List<String> keys){

		for (String key : keys) {

		  AbstractUploadProgressListener listener = getCurrentListener(session, key);
		    if (listener != null) {
		      logger.debug("Listener found");
		      if (listener.isCanceled() || listener.getPercentage() >= 100){
		    	  logger.debug("Listener isCanceled or 100%, removing");
		    	  removeCurrentListener(session, key);
		      }
		    }else
		    	logger.debug("Session id: "+session.getId() +" - "+key+" - Listener not found");
		}

	}

	/**
	 * Parses the json client upload keys.
	 *
	 * @param jsonClientUploadKeys the json client upload keys
	 * @return the linked hash map
	 * @throws FileUploadException the file upload exception
	 */
	@SuppressWarnings("rawtypes")
	private static LinkedHashMap<String, String> parseJSONClientUploadKeys(final String jsonClientUploadKeys) throws FileUploadException{
		JSONTokener tokener = new JSONTokener(jsonClientUploadKeys);
		JSONObject root;
		LinkedHashMap<String, String> keyFiles = null;
		try {

			root = new JSONObject(tokener);
			JSONArray jsonArray = root.getJSONArray(JSON_CLIENT_KEYS);
			keyFiles = new LinkedHashMap<String, String>(jsonArray.length());
			logger.debug("jsonArray :"+jsonArray.toString());
			for (int i=0; i<jsonArray.length(); i++) {
				JSONObject object = jsonArray.getJSONObject(i);
				logger.debug("object :"+object);
				String key = (String) object.keys().next();
				String value = object.getString(key);
				logger.debug("key :"+key+", value: "+value);
				keyFiles.put(key, value);
			}
			/*
			JSONObject object = jsonArray.getJSONObject(0);
			Iterator it = object.keys();
			while(it.hasNext()){
				String key = (String) it.next();
				String value = object.getString(key);
				logger.debug("key :"+key+", value: "+value);
				keyFiles.put((String) key, value);
			}
		*/
		} catch (JSONException e) {
			logger.error("An error occurred during parsing file names: "+keyFiles, e);
			throw new FileUploadException("An error occurred during parsing file names");
		}

		logger.debug("keyFiles: "+keyFiles);
		return keyFiles;
	}

	/**
	 * Upload data.
	 *
	 * @param request the request
	 * @param response the response
	 * @param uploadItem the upload item
	 * @param destinationId the destination id
	 * @param uploadType the upload type
	 * @param clientUploadKey the client upload key
	 * @param isOverwrite the is overwrite
	 * @throws ServletException the servlet exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void uploadData(HttpServletRequest request, final HttpServletResponse response, final FileItemStream uploadItem, String destinationId,String uploadType,String clientUploadKey, boolean isOverwrite) throws ServletException, IOException{

		String fileName = uploadItem.getName();
		logger.info("Upload servlet parameters: [fileName: "+fileName+ ", destinationId: "+destinationId +", uploadType: "+uploadType+", isOverwrite: "+isOverwrite+", clientUploadKey: "+clientUploadKey+"]");

		if (uploadType == null || uploadType.isEmpty()) {
			logger.error("Error processing request in upload servlet for: "+fileName+". No upload type found");
			sendError(response, "Internal error: No upload type found");
			return;
		}

		if(clientUploadKey==null || clientUploadKey.isEmpty()){
			logger.error("Error processing request in upload servlet for: "+fileName+". No client upload key found");
			sendError(response, "Internal error: No client upload key found");
			return;
		}

		//CLIENT UPLOAD IS THE KEY
//		WorkspaceUploaderItem workspaceUploader = createNewWorkspaceUploader(clientUploadKey,destinationId,fileName);
//		saveWorkspaceUploaderStatus(workspaceUploader, UPLOAD_STATUS.WAIT, "Uploading "+fileName+" at 0%", request.getSession());


		//RETRIVE WORKSPACE UPLOADER FROM SESSION

		WorkspaceUploaderItem workspaceUploader = null;
		try {
			workspaceUploader = WsUtil.getWorkspaceUploaderInSession(request.getSession(), clientUploadKey);
			workspaceUploader.setIsOverwrite(isOverwrite); //SET IS OVERWRITE
		} catch (Exception e) {
			logger.error("Error during workspace uploader retrieving", e);
			saveWorkspaceUploaderStatus(workspaceUploader, UPLOAD_STATUS.FAILED, "An error occurred during upload: "+fileName+". Error processing request in upload servlet", request.getSession());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing request in upload servlet");
			return;
		}

		//TODO DEBUG REMOVE THIS
//		saveWorkspaceUploaderStatus(workspaceUploader, UPLOAD_STATUS.FAILED, "An error occurred during upload: "+fileName+". Error processing request in upload servlet", request.getSession());
//		response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing request in upload servlet");
//		return;

		Workspace wa = null;
		try {
			logger.debug("getWorkspace from HL");
			wa = WsUtil.getWorkspace(request.getSession());
		} catch (Exception e) {
			logger.error("Error during workspace retrieving", e);
			saveWorkspaceUploaderStatus(workspaceUploader, UPLOAD_STATUS.FAILED, "An error occurred during upload: "+fileName+". Error processing request in upload servlet", request.getSession());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing request in upload servlet");
			return;
		}

		if (wa == null) {
			logger.error("Now workspace found in session");
			saveWorkspaceUploaderStatus(workspaceUploader, UPLOAD_STATUS.FAILED, "An error occurred during upload: "+fileName+". No workspace in session", request.getSession());
			sendError(response, "Internal error: No workspace in session");
			return;
		}

		WorkspaceItem item;
		try {
			logger.debug("getWorkspaceItem destination id: "+destinationId+" from HL");
			item = wa.getItem(destinationId);
		} catch (ItemNotFoundException e) {
			logger.error("Error, no destination folder found", e);
			saveWorkspaceUploaderStatus(workspaceUploader, UPLOAD_STATUS.FAILED, "An error occurred during upload: "+fileName+". No destination folder found", request.getSession());
			sendError(response, "Internal error: No destination folder found");
			return;
		}

		if (item.getType() != WorkspaceItemType.FOLDER && item.getType() != WorkspaceItemType.SHARED_FOLDER)  {
			logger.error("Error processing request in upload servlet: Wrong destination item");
			saveWorkspaceUploaderStatus(workspaceUploader, UPLOAD_STATUS.FAILED, "An error occurred during upload: "+fileName+". Wrong destination item", request.getSession());
			sendError(response, "Internal error: Wrong destination item");
			return;
		}

		final WorkspaceFolder destinationFolder = (WorkspaceFolder) item;
		try {
			//we calculate an unique name for the current destination
			String itemName = "";

			logger.debug("getItemName from HL");
			if(!isOverwrite)
				itemName = WorkspaceUtil.getUniqueName(fileName, destinationFolder);
			else
				itemName = fileName;

			Long size = getContentLength(request);
			logger.debug("size: " + size + " bytes");

			/*
			String contentType = uploadItem.getContentType();
			logger.debug("Content type (mime type): "+contentType + " unique name: "+itemName);

			if(contentType==null || contentType.compareTo(UNKNOWN_UNKNOWN)==0){
				logger.warn("Content Type not detected forcing to null");
				contentType = null;
			}
			*/
			String extension = FilenameUtils.getExtension(itemName);
			logger.debug("extension: "+extension);

			//CONFIRM DESTINATION FOLDER
			workspaceUploader.getFile().setParentId(destinationFolder.getId());

			//Create Item Uploader to read progress
//			WorkspaceUploadFile wsUploadFile = new WorkspaceUploadFile(destinationFolder.getId(), itemName);
//			workspaceUploader.setFile(wsUploadFile);
//			workspaceUploader.setStatusDescription("Uploading "+itemName+" at 0%");

			//instanciate the progress listener
			final AbstractUploadProgressListener uploadProgressListener = createNewListener(request.getSession(), clientUploadKey);
			final UploadProgressInputStream inputStream = new UploadProgressInputStream(uploadItem.openStream(), size);
			inputStream.addListener(uploadProgressListener);
			workspaceUploader.setUploadProgress(uploadProgressListener.getUploadProgress());

			saveWorkspaceUploaderInSession(workspaceUploader, request.getSession());

			String contentType = null; //It's set into HL

			if(uploadType.compareTo(FILE)==0) {//IS FILE UPLOAD

				boolean isZipFile = MimeTypeUtil.isZipContentType(uploadItem.getContentType()); //UNZIP??

				if(isZipFile && extension.compareToIgnoreCase(D4ST)==0){  //Create REPORT TEMPLATE

					String newItemName = itemName;
					logger.debug("createTemplate: "+newItemName);

					createTemplate(request, workspaceUploader, request.getSession(), wa, newItemName, inputStream, destinationFolder, response, isOverwrite);

				}else if(isZipFile && extension.compareToIgnoreCase(D4SR)==0){ //Create REPORT

					String newItemName = itemName;
					logger.debug("createReport: "+newItemName);
					createReport(request, workspaceUploader, request.getSession(), wa, newItemName, inputStream, destinationFolder, response, isOverwrite);
				}else{ //CREATE AN EXTERNAL FILE

					workspaceUploader = WorkspaceUploaderMng.uploadFile(request, workspaceUploader, request.getSession(), wa, itemName, inputStream, destinationFolder, contentType, isOverwrite, size);

					if(workspaceUploader==null)
						throw new Exception("Error when creating uploader, it is null!");

					sendMessage(response, workspaceUploader.getIdentifier());
				}

			}else {//IS ARCHIVE UPLOAD

				logger.debug("Archive content type: "+uploadItem.getContentType());

				if (MimeTypeUtil.isZipContentType(uploadItem.getContentType())){ //UNZIP??
					logger.debug("Unziping content");
					workspaceUploader = WorkspaceUploaderMng.uploadArchive(workspaceUploader, request.getSession(), itemName, inputStream, destinationFolder, size);

					if(workspaceUploader==null)
						throw new Exception("Error when creating uploader, it is null!");

					sendMessage(response, workspaceUploader.getIdentifier());
				} else{
					workspaceUploader = WorkspaceUploaderMng.uploadFile(request, workspaceUploader, request.getSession(), wa, itemName, inputStream, destinationFolder, contentType, isOverwrite, size);

					if(workspaceUploader==null)
						throw new Exception("Error when creating uploader, it is null!");

					sendMessage(response, workspaceUploader.getIdentifier());
				}
			}

//			file.delete();
		} catch (InsufficientPrivilegesException e) {
			logger.error("Error creating elements", e);
			saveWorkspaceUploaderStatus(workspaceUploader, UPLOAD_STATUS.FAILED, "An error occurred in the upload. Insufficient privileges", request.getSession());
			sendError(response, "Internal error: Insufficient privileges");
			return;
		} catch (InternalErrorException e) {
			logger.error("Error creating elements", e);
			saveWorkspaceUploaderStatus(workspaceUploader, UPLOAD_STATUS.FAILED, "An error occurred in the upload:"+e.getMessage(), request.getSession());
			sendError(response, "Internal error: "+e.getMessage());
			return;
		} catch (ItemAlreadyExistException e) {
			logger.error("Error creating elements", e);
			saveWorkspaceUploaderStatus(workspaceUploader, UPLOAD_STATUS.FAILED, "An error occurred in the upload. An item with that name already exists", request.getSession());
			sendError(response, "Internal error: An item with that name already exists");
			return;
		} catch (IOException e){
			logger.error("Error creating elements, is it cancel?", e);
			saveWorkspaceUploaderStatus(workspaceUploader, UPLOAD_STATUS.FAILED, "An error occurred in the upload", request.getSession());
			sendError(response, "Internal error: An item with that name already exists");
			return;
//			sendError(response, "Internal error: An item with that name already exists");
//			return;
		}catch (Exception e) {
			logger.error("Error creating elements", e);
			saveWorkspaceUploaderStatus(workspaceUploader, UPLOAD_STATUS.FAILED, "An error occurred in the upload. "+e.getMessage(), request.getSession());
//			sendError(response, "Internal error: An error occurred on uploading the file, try again later");
//			return;
		}finally{
			removeCurrentListener(request.getSession(), clientUploadKey);
		}
	}


	/**
	 * Save workspace uploader status.
	 *
	 * @param workspaceUploader the workspace uploader
	 * @param status the status
	 * @param description the description
	 * @param session the session
	 * @return the workspace uploader item
	 */
	private synchronized WorkspaceUploaderItem saveWorkspaceUploaderStatus(WorkspaceUploaderItem workspaceUploader, UPLOAD_STATUS status, String description, HttpSession session){
		workspaceUploader.setUploadStatus(status);
		workspaceUploader.setStatusDescription(description);
		try {
			saveWorkspaceUploaderInSession(workspaceUploader, session);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return workspaceUploader;
	}


	/**
	 * Creates the new workspace uploader.
	 *
	 * @param clientUploadKey the client upload key
	 * @param folderParentId the folder parent id
	 * @param fileName the file name
	 * @param isOverwrite the is overwrite
	 * @return the workspace uploader item
	 */
	private WorkspaceUploaderItem createNewWorkspaceUploader(String clientUploadKey, String folderParentId, String fileName, boolean isOverwrite){
		//CLIENT UPLOAD IS THE KEY
		WorkspaceUploaderItem workspaceUploader = new WorkspaceUploaderItem(clientUploadKey);
		workspaceUploader.setClientUploadKey(clientUploadKey);
		//Create File
		WorkspaceUploadFile wsUploadFile = new WorkspaceUploadFile(folderParentId, null, fileName);
		workspaceUploader.setFile(wsUploadFile);
		workspaceUploader.setIsOverwrite(isOverwrite);
		return workspaceUploader;
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

	/**
	 * Save workspace uploader in session.
	 *
	 * @param workspaceUploader the workspace uploader
	 * @param httpSession the http session
	 * @return the workspace uploader item
	 * @throws Exception the exception
	 */
	public static void saveWorkspaceUploaderInSession(WorkspaceUploaderItem workspaceUploader, HttpSession httpSession) throws Exception {

		if(workspaceUploader!=null){
			try {
				WsUtil.putWorkspaceUploaderInSession(httpSession, workspaceUploader);
			} catch (Exception e) {
				logger.error("Error during WorkspaceUploaderItem save in session workspace uploader: "+workspaceUploader,e);
				throw new Exception("An error occurred in the upload. Try again");
			}
		}else
			throw new Exception("An error occurred in the upload. Workspace Uploader not found. Abort and try again");
	}

	//TEST TIME
	/**
	 * Prints the start time.
	 *
	 * @return the long
	 */
	public static Long printStartTime(){
		Long startTime =  System.currentTimeMillis();
		logger.debug("Start time: "+startTime);
		return startTime;
	}
	//TEST TIME
	/**
	 * Prints the elapsed time.
	 *
	 * @param startTime the start time
	 */
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
	public static void notifyUploadInSharedFolder(final HttpServletRequest request, final HttpSession httpSession, final Workspace workspace, final String itemId, final String destinationFolderId, final boolean isOverwrite){

		logger.trace("[2] HttpServletRequest is: URI: "+request.getRequestURI() +", ServerName: "+request.getServerName());
		final NotificationsWorkspaceUploaderProducer np = new NotificationsWorkspaceUploaderProducer(httpSession,request);
		new Thread(){
			public void run() {
				WorkspaceItem sourceItem;
				try {
					sourceItem = workspace.getItem(itemId);
					String sourceSharedId = sourceItem.getIdSharedFolder();
					WorkspaceItem folderDestinationItem = workspace.getItem(destinationFolderId);
					logger.trace("[3] HttpServletRequest is: URI: "+request.getRequestURI() +", ServerName: "+request.getServerName());
					NotificationsWorkspaceUploader.checkSendNotifyChangedItemToShare(np, request, httpSession, sourceItem, sourceSharedId, folderDestinationItem,isOverwrite);
				} catch (Exception e) {
					logger.error("Error in notifyUploadInSharedFolder", e);
				}
			};

		}.start();
	}

	/**
	 * Overwrite item.
	 *
	 * @param wa the wa
	 * @param itemName the item name
	 * @param fileData the file data
	 * @param destinationFolder the destination folder
	 * @return the folder item
	 */
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


	/**
	 * Creates the report.
	 *
	 * @param request the request
	 * @param workspaceUploader the workspace uploader
	 * @param httpSession the http session
	 * @param wa the wa
	 * @param itemName the item name
	 * @param fileInputStream the file input stream
	 * @param destinationFolder the destination folder
	 * @param response the response
	 * @param isOverwrite the is overwrite
	 * @throws InsufficientPrivilegesException the insufficient privileges exception
	 * @throws ItemAlreadyExistException the item already exist exception
	 * @throws InternalErrorException the internal error exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void createReport(final HttpServletRequest request, WorkspaceUploaderItem workspaceUploader, HttpSession httpSession, Workspace wa, String itemName, InputStream fileInputStream, WorkspaceFolder destinationFolder, HttpServletResponse response, boolean isOverwrite) throws InsufficientPrivilegesException, ItemAlreadyExistException, InternalErrorException, IOException{

		try {

			Report report = null;

			if(!isOverwrite){
				itemName = WorkspaceUtil.getUniqueName(itemName, destinationFolder);
				report = wa.createReport(itemName, "", Calendar.getInstance(), Calendar.getInstance(), "", "", "", 0, "",fileInputStream, destinationFolder.getId());

				notifyUploadInSharedFolder(request, httpSession,wa,report.getId(),destinationFolder.getId(), isOverwrite);
				sendMessage(response, "File "+report.getName()+" imported correctly in "+destinationFolder.getPath());
			}
			else{ 	//CASE OVERWRITE
				FolderItem rep = overwriteItem(wa, itemName, fileInputStream, destinationFolder);

				if(rep!=null){

					notifyUploadInSharedFolder(request, httpSession,wa,rep.getId(),destinationFolder.getId(), isOverwrite);
					sendMessage(response, "File "+rep.getName()+" imported correctly in "+destinationFolder.getPath());
				}
				else
					sendError(response,"Internal error: Workspace Item Not Found");
			}
		} catch (WrongDestinationException e) {
			logger.error("Error creating elements", e);
			workspaceUploader.setStatusDescription("An error occurred in the upload. Wrong Destination");
			workspaceUploader.setUploadStatus(UPLOAD_STATUS.FAILED);
			sendError(response, "Internal error: Wrong Destination");
		} catch (WorkspaceFolderNotFoundException e) {
			logger.error("Error creating elements", e);
			workspaceUploader.setStatusDescription("An error occurred in the upload. Workspace Folder Not Found");
			workspaceUploader.setUploadStatus(UPLOAD_STATUS.FAILED);
			sendError(response, "Internal error: Workspace Folder Not Found");
		}finally{
			try {
//				StreamUtils.deleteTempFile(file);
				WsUtil.setErasableWorkspaceUploaderInSession(httpSession, workspaceUploader.getIdentifier());
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}

	}

	/**
	 * Creates the template.
	 *
	 * @param workspaceUploader the workspace uploader
	 * @param httpSession the http session
	 * @param wa the wa
	 * @param itemName the item name
	 * @param fileInputStream the file input stream
	 * @param destinationFolder the destination folder
	 * @param response the response
	 * @param isOverwrite the is overwrite
	 * @throws InsufficientPrivilegesException the insufficient privileges exception
	 * @throws ItemAlreadyExistException the item already exist exception
	 * @throws InternalErrorException the internal error exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void createTemplate(final HttpServletRequest request, WorkspaceUploaderItem workspaceUploader, HttpSession httpSession, Workspace wa, String itemName, InputStream fileInputStream, WorkspaceFolder destinationFolder, HttpServletResponse response, boolean isOverwrite) throws InsufficientPrivilegesException, ItemAlreadyExistException, InternalErrorException, IOException{

		try {

			ReportTemplate template = null;

			if(!isOverwrite){
				itemName = WorkspaceUtil.getUniqueName(itemName, destinationFolder);
				template = wa.createReportTemplate(itemName, "", Calendar.getInstance(), Calendar.getInstance(), "", "", 0, "", fileInputStream, destinationFolder.getId());

				notifyUploadInSharedFolder(request, httpSession,wa,template.getId(),destinationFolder.getId(), isOverwrite);
				sendMessage(response, "File "+template.getName()+" imported correctly in "+destinationFolder.getPath());

			}else{ 	//CASE OVERWRITE
				FolderItem rep = overwriteItem(wa, itemName, fileInputStream, destinationFolder);

				if(rep!=null){

					notifyUploadInSharedFolder(request, httpSession,wa,rep.getId(),destinationFolder.getId(), isOverwrite);
					sendMessage(response, "File "+rep.getName()+" imported correctly in "+destinationFolder.getPath());
				}
				else
					sendError(response,"Internal error: Workspace Item Not Found");
			}

		} catch (WrongDestinationException e) {
			logger.error("Error creating elements", e);
			workspaceUploader.setStatusDescription("An error occurred in the upload. Wrong Destination");
			workspaceUploader.setUploadStatus(UPLOAD_STATUS.FAILED);
			sendError(response, "Internal error: Wrong Destination");
		} catch (WorkspaceFolderNotFoundException e) {
			logger.error("Error creating elements", e);
			workspaceUploader.setStatusDescription("An error occurred in the upload. Workspace Folder Not Found");
			workspaceUploader.setUploadStatus(UPLOAD_STATUS.FAILED);
			sendError(response, "Internal error: Workspace Folder Not Found");
		}finally{
			try {
//				StreamUtils.deleteTempFile(fileInputStream);
				WsUtil.setErasableWorkspaceUploaderInSession(httpSession, workspaceUploader.getIdentifier());
			} catch (Exception e2) {
				// TODO: handle exception
			}
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

	/**
	 * Send message.
	 *
	 * @param response the response
	 * @param message the message
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected void sendMessage(HttpServletResponse response, String message) throws IOException{
		try {
			response.setStatus(HttpServletResponse.SC_ACCEPTED);
			message = message.replaceAll(":", "");
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

	/**
	 * Send warn message.
	 *
	 * @param response the response
	 * @param message the message
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
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

	/**
	 * Mark the current upload process to be canceled.
	 *
	 * @param session the session
	 * @param clientUploadKey the client upload key
	 * @return true, if successful
	 */
	public boolean cancelUpload(HttpSession session, String clientUploadKey) {
		logger.debug("UPLOAD-SERVLET (" + session.getId()+ ") cancelling Upload: "+clientUploadKey);
		AbstractUploadProgressListener listener = getCurrentListener(session, clientUploadKey);
		if (listener != null && !listener.isCanceled()) {
			logger.info("CancelUpload listener is "+listener.toString());
			listener.setException(new UploadCanceledException());
			return true;
		}else{
			logger.info("Skipping cancel upload: listener is null or is cancel");
			return false;
		}
	}

	/**
	 * Get the listener active in this session.
	 *
	 * @param session the session
	 * @param clientUploadKey the client upload key
	 * @return the listener active
	 */
	protected AbstractUploadProgressListener getCurrentListener(HttpSession session, String clientUploadKey) {
		if (isAppEngine()) {
			return MemoryUploadListener.current(session.getId(), clientUploadKey);
		} else {
			return UploadProgressListener.current(session, clientUploadKey);
		}
	}

	/**
	 * Just a method to detect whether the web container is running with
	 * appengine restrictions.
	 *
	 * @return true if the case of the application is running in appengine
	 */
	public boolean isAppEngine() {
		return appEngine;
	}

	/**
	 * Create a new listener for this session.
	 *
	 * @param session the session
	 * @param clientUploadKey the client upload key
	 * @return the appropriate listener
	 */
	protected AbstractUploadProgressListener createNewListener(HttpSession session, String clientUploadKey) {
		if (isAppEngine()) {
			return new MemoryUploadListener(session, clientUploadKey, 0, 100);
		} else {
			return new UploadProgressListener(session, clientUploadKey, 0, 100);
		}
	}


	/**
	 * Gets the content length.
	 *
	 * @param request the request
	 * @return the content length
	 */
	private long getContentLength(HttpServletRequest request) {
		long size = -1;
		try {
			size = Long.parseLong(request
					.getHeader(FileUploadBase.CONTENT_LENGTH));
		} catch (NumberFormatException e) {
		}
		return size;
	}

	/**
	 * Remove the listener active in this session.
	 *
	 * @param session the session
	 * @param clientUploadKey the client upload key
	 */
	protected void removeCurrentListener(HttpSession session, String clientUploadKey) {
		logger.debug("RemoveCurrentListener: "+clientUploadKey);
		AbstractUploadProgressListener listener = getCurrentListener(session, clientUploadKey);

		if (listener != null) {
			logger.debug("Removing listener: "+listener.getClientUploadKey());
			listener.remove();
		}else
			logger.warn("Listener "+clientUploadKey+ "is null");
	}
}
