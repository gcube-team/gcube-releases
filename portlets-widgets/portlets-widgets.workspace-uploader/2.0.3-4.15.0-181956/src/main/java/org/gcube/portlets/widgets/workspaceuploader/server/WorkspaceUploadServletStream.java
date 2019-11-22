/**
 *
 */
package org.gcube.portlets.widgets.workspaceuploader.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
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
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.Validate;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.storagehub.model.items.FolderItem;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehubwrapper.server.StorageHubWrapper;
import org.gcube.common.storagehubwrapper.server.tohl.Workspace;
import org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceFolder;
import org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem;
import org.gcube.common.storagehubwrapper.shared.tohl.exceptions.InsufficientPrivilegesException;
import org.gcube.common.storagehubwrapper.shared.tohl.exceptions.InternalErrorException;
import org.gcube.common.storagehubwrapper.shared.tohl.exceptions.ItemAlreadyExistException;
import org.gcube.common.storagehubwrapper.shared.tohl.exceptions.ItemNotFoundException;
import org.gcube.common.storagehubwrapper.shared.tohl.exceptions.WrongItemTypeException;
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
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
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

	public static final String VRE_ID_ATTR_NAME = "gcube-vreid";
	public static final String CURR_GROUP_ID = ConstantsWorkspaceUploader.CURR_GROUP_ID;

	//public static final String CURR_USER_ID = ConstantsWorkspaceUploader.CURR_USER_ID;

	public static final String UPLOAD_TYPE = ConstantsWorkspaceUploader.UPLOAD_TYPE;

	public static final String ID_FOLDER = ConstantsWorkspaceUploader.ID_FOLDER;

	public static final String UPLOAD_FORM_ELEMENT = ConstantsWorkspaceUploader.UPLOAD_FORM_ELEMENT;

	public static final String CLIENT_UPLOAD_KEYS = ConstantsWorkspaceUploader.CLIENT_UPLOAD_KEYS;

	public static final String JSON_CLIENT_KEYS = ConstantsWorkspaceUploader.JSON_CLIENT_KEYS;

	public static final String IS_OVERWRITE = ConstantsWorkspaceUploader.IS_OVERWRITE;

	public static final String CANCEL_UPLOAD = ConstantsWorkspaceUploader.CANCEL_UPLOAD;

	public static final String FILE = "File";

	public static final String ARCHIVE = "Archive";

	public static final Map<String, String> SUPPORTED_UNPACKING_ARCHIVE = new HashMap<String, String>() {{
	    put("zip","application/zip"); //.zip
	    put("7z","application/x-7z-compressed"); //.7z
	    put("tar","application/x-tar"); //tar
	    put("java archive","application/java-archive");//.jar
	    put("jar","application/x-java-archive");
	    put("gtar","application/x-gtar");
	}};

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
		logger.info("POST on UploadServlet");
		boolean requestIsNull = request==null;
		logger.trace("[1] HttpServletRequest is: null? "+requestIsNull+", URI: "+request.getRequestURI() +", ServerName: "+request.getServerName());

		if (!ServletFileUpload.isMultipartContent(request)) {
			logger.error("ERROR: multipart request not found");
			sendError(response, "ERROR: multipart request not found");
			return;
		}

		try {

			logger.info("UPLOAD-SERVLET starting");
		    HttpSession session = request.getSession();
		    logger.info("UPLOAD-SERVLET session: "+session);
		    logger.debug("UPLOAD-SERVLET (" + session.getId() + ") new upload request received.");


		    if(WsUtil.isSessionExpired(request)){
		    	logger.error("SESSION_EXPIRED: session is expired");
		    	sendSessionExpired(response, "SESSION_EXPIRED: session is expired");
		    	return;
		    }

			String destinationId = null;
			String uploadType = null;
			boolean isOverwrite = true; //CREATE A NEW VERSION OF FILE IS BEHAVIOUR BY DEFAULT
	//		String clientUploadKey = null;
			FileItemStream uploadItem = null;
			ArrayList<String> listClientUploadKeys = null;
			GCubeUser user = PortalContext.getConfiguration().getCurrentUser(request);
			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload servletFileUpload = new ServletFileUpload(factory);

			/**
			 * An iterator to instances of <code>FileItemStream</code>
			 * parsed from the request, in the order that they were
			 *transmitted.
			 */
			FileItemIterator fileItemIterator = servletFileUpload.getItemIterator(request);

			int uploadItemsCnt = 0;
			String scopeGroupId = "";
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
					try{
						isOverwrite = Boolean.parseBoolean(Streams.asString(item.openStream()));
						logger.debug("IS_OVERWRITE OK "+ isOverwrite);
					}catch(Exception e){
						//Silent exception;
					}
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

				if (item.isFormField() && CURR_GROUP_ID.equals(item.getFieldName())){
					scopeGroupId = Streams.asString(item.openStream());
					logger.debug("currentGroupId passed as parameter = " + scopeGroupId);
					logger.debug("currentGroupId into PortalContext scope= " + PortalContext.getConfiguration().getCurrentScope(scopeGroupId));
				}

//				if (item.isFormField() && CURR_USER_ID.equals(item.getFieldName())){
//					currUserId = Streams.asString(item.openStream());
//					logger.debug("currUserId passed as parameter = " + currUserId);
//					logger.debug("currUserinto PortalContext = " + PortalContext.getConfiguration().getCurrentUser(request));
//				}

				//MUST BE THE LAST PARAMETER TRASMITTED
				if (UPLOAD_FORM_ELEMENT.equals(item.getFieldName())){
					uploadItem = item;
					logger.debug("UPLOAD_FORM_ELEMENT OK "+uploadItem.getName() + " scopeGroupId="+scopeGroupId);
//					break;
					uploadData(user, scopeGroupId, request, response, uploadItem, destinationId, uploadType, listClientUploadKeys.get(uploadItemsCnt), isOverwrite);
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

	public File coyStreamToFile(InputStream in, String fileExtension) throws IOException{
		File tempFile = File.createTempFile(UUID.randomUUID().toString(), fileExtension);
		tempFile.deleteOnExit();
		FileOutputStream out = new FileOutputStream(tempFile);
		IOUtils.copy(in, out);
		return tempFile;
	}

//	public static InputStream clone(final InputStream inputStream) {
//        try {
//            inputStream.mark(0);
//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//            byte[] buffer = new byte[1024];
//            int readLength = 0;
//            while ((readLength = inputStream.read(buffer)) != -1) {
//                outputStream.write(buffer, 0, readLength);
//            }
//            inputStream.reset();
//            outputStream.flush();
//            return new ByteArrayInputStream(outputStream.toByteArray());
//        }
//        catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return null;
//    }



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
	 * @param user the user
	 * @param scopeGroupId the scope group id
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
	private void uploadData(GCubeUser user, String scopeGroupId, HttpServletRequest request, final HttpServletResponse response, final FileItemStream uploadItem, String destinationId,String uploadType,String clientUploadKey, boolean isOverwrite) throws ServletException, IOException{

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
			workspaceUploader = WsUtil.getWorkspaceUploaderInSession(request, clientUploadKey);
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

		StorageHubWrapper storageHubWrapper;
		try {
			storageHubWrapper = WsUtil.getStorageHubWrapper(request, scopeGroupId, user);
		} catch (Exception e) {
			logger.error("Error during workspace retrieving", e);
			saveWorkspaceUploaderStatus(workspaceUploader, UPLOAD_STATUS.FAILED, "An error occurred during upload: "+fileName+". Error processing request in upload servlet", request.getSession());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing request in upload servlet");
			return;
		}

		if (storageHubWrapper == null) {
			logger.error("Now workspace found in session");
			saveWorkspaceUploaderStatus(workspaceUploader, UPLOAD_STATUS.FAILED, "An error occurred during upload: "+fileName+". No workspace in session", request.getSession());
			sendError(response, "Internal error: No workspace retrieved");
			return;
		}

		WorkspaceItem destinationItem = null;
		Workspace workspace = null;
		try {
			logger.debug("getWorkspaceItem destination id: "+destinationId+" from HL");
			workspace = storageHubWrapper.getWorkspace();
			destinationItem = workspace.getItem(destinationId);
		} catch (ItemNotFoundException | InternalErrorException e) {
			logger.error("Error, no destination folder found", e);
			saveWorkspaceUploaderStatus(workspaceUploader, UPLOAD_STATUS.FAILED, "An error occurred during upload: "+fileName+". No destination folder found", request.getSession());
			sendError(response, "Internal error: No destination folder found");
			return;
		} catch (Exception e) {
			logger.error("Error, no destination folder found", e);
			saveWorkspaceUploaderStatus(workspaceUploader, UPLOAD_STATUS.FAILED, "An error occurred during upload: "+fileName, request.getSession());
			sendError(response, "Internal error: No destination folder found");
			return;
		}

		if (!destinationItem.isFolder())  {
			logger.error("Error processing request in upload servlet: The destination is not a folder");
			saveWorkspaceUploaderStatus(workspaceUploader, UPLOAD_STATUS.FAILED, "An error occurred during upload: "+fileName+". The destination is not a folder", request.getSession());
			sendError(response, "Internal error: Wrong destination item. The destination is not a folder");
			return;
		}

		final WorkspaceFolder destinationFolder = (WorkspaceFolder) destinationItem;

		try {
			
			//CHECKING IF THE USER CAN WRITE IN THE FOLDER
			boolean canWrite = workspace.canUserWriteIntoFolder(destinationFolder.getId());
			if(!canWrite) {
				String notAuthorizedError = "The user cannot write in the folder with id: "+destinationFolder.getId();
				String folderName = destinationFolder.getName();
				workspaceUploader.setUploadStatus(UPLOAD_STATUS.FAILED);
				workspaceUploader.setStatusDescription("You have not permission to upload in the folder: "+folderName);
				logger.info(notAuthorizedError);
				try {
					WsUtil.putWorkspaceUploaderInSession(request.getSession(), workspaceUploader);
				} catch (Exception e1) {
					logger.error("Error during WorkspaceUploaderItem session update: ",e1);
				}finally {
					try {
						WsUtil.setErasableWorkspaceUploaderInSession(request, workspaceUploader.getIdentifier());
					} catch (Exception e2) {
						logger.error("Error during setErasableWorkspaceUploaderInSession session update: ",e2);
					}
				}
				return;
			}
			
			//Removing path from fileName
			String itemName = fileName;
			//Getting extension
			String extension = FilenameUtils.getExtension(itemName);
			logger.debug("extension: "+extension);
			//Getting base name
			String baseName = FilenameUtils.getBaseName(itemName); //Using base name in order to avoid Task #12470
			
			//Task #17152
			extension = extension.isEmpty()?"":"."+extension;
			
			itemName = baseName+extension;
			logger.debug("purged item name is: "+itemName);
			//SIZE
			Long size = getContentLength(request);
			logger.debug("size: " + size + " bytes");

			//CONFIRM DESTINATION FOLDER
			workspaceUploader.getFile().setParentId(destinationFolder.getId());

			//Instancing the progress listener
			final AbstractUploadProgressListener uploadProgressListener = createNewListener(request.getSession(), clientUploadKey);
			final UploadProgressInputStream inputStream = new UploadProgressInputStream(uploadItem.openStream(), size);
			inputStream.addListener(uploadProgressListener);
			workspaceUploader.setUploadProgress(uploadProgressListener.getUploadProgress());

			//USING isOverwrite to check if the file already exists
			try {
				isOverwrite = storageHubWrapper.getWorkspace().exists(itemName, destinationFolder.getId());
				workspaceUploader.setIsOverwrite(isOverwrite);
				logger.info("The file: "+itemName+ " exists in the folder? "+isOverwrite);
				saveWorkspaceUploaderInSession(workspaceUploader, request.getSession());
			}catch (ItemNotFoundException | WrongItemTypeException e) {
				logger.warn("Error on checking if the file: "+itemName+" exists in the folder id: "+ destinationFolder.getId());
			}

			saveWorkspaceUploaderInSession(workspaceUploader, request.getSession());

			String contentType = uploadItem.getContentType();
			logger.debug("Stream content type: "+contentType);


			if(uploadType.compareTo(ARCHIVE)==0){
				//UPLOAD ARCHIVE

				logger.debug("Uploding archive....");
				boolean isSupportedArchive = false;
				for (String archive_extension : SUPPORTED_UNPACKING_ARCHIVE.keySet()) {

					//is content-type supported?
					if(SUPPORTED_UNPACKING_ARCHIVE.get(archive_extension).compareTo(uploadItem.getContentType())==0){
						isSupportedArchive = true;
						break;
					}
				}

				if (isSupportedArchive){ //Uploading Archive OK

					logger.debug("Supported archive " +uploadItem.getContentType() +" for unpacking");
					//USING the baseName instead of itemName in oder to avoid adding extension as suffix
					workspaceUploader = WorkspaceUploaderMng.uploadArchive(storageHubWrapper, user, scopeGroupId, workspaceUploader, request, baseName, inputStream, destinationFolder, size);

					if(workspaceUploader==null)
						throw new Exception("Error when creating uploader, it is null!");

					/*
					 * Incident #10095. Commented OK as response
					 * in order to avoid downloading of .dms file by Safari
					 * from MAC
					 */
					//sendMessage(response, workspaceUploader.getIdentifier());

				}else{
					logger.warn("Unsuppoterd archive for unpacking: " +uploadItem.getContentType());
					saveWorkspaceUploaderStatus(workspaceUploader, UPLOAD_STATUS.ABORTED, "Unpacking not supported for: "+uploadItem.getContentType()+" Supported archive are: "+SUPPORTED_UNPACKING_ARCHIVE.keySet().toString(), request.getSession());
					sendError(response, "Internal error: Unpacking not supported for "+uploadItem.getContentType());
					return;
				}

			}else{
				//UPLOAD FILE. IT IS DEFAULT CASE

				logger.debug("Uploding file....");
				workspaceUploader = WorkspaceUploaderMng.uploadFile(storageHubWrapper, user, scopeGroupId, request, workspaceUploader, request.getSession(), itemName, inputStream, destinationFolder, contentType, isOverwrite, size);

				if(workspaceUploader==null)
					throw new Exception("Error when creating uploader, it is null!");

				/*
				 * Incident #10095. Commented OK as response
				 * in order to avoid downloading of .dms file by Safari
				 * from MAC
				 */
				//sendMessage(response, workspaceUploader.getIdentifier());
			}

		} catch (InsufficientPrivilegesException e) {
			logger.error("Error creating elements", e);
			saveWorkspaceUploaderStatus(workspaceUploader, UPLOAD_STATUS.FAILED, "Uploading error. Insufficient privileges", request.getSession());
			sendError(response, "Internal error: Insufficient privileges");
			return;
		} catch (InternalErrorException e) {
			logger.error("Error creating elements", e);
			saveWorkspaceUploaderStatus(workspaceUploader, UPLOAD_STATUS.FAILED, "Uploading error:"+e.getMessage(), request.getSession());
			sendError(response, "Internal error: "+e.getMessage());
			return;
		} catch (ItemAlreadyExistException e) {
			logger.error("Error creating elements", e);
			saveWorkspaceUploaderStatus(workspaceUploader, UPLOAD_STATUS.FAILED, "Uploading error. An item with that name already exists", request.getSession());
			sendError(response, "Internal error: An item with that name already exists");
			return;
		} catch (IOException e){
			logger.error("Error creating elements, is it cancel?", e);
			saveWorkspaceUploaderStatus(workspaceUploader, UPLOAD_STATUS.FAILED, "Uploading error", request.getSession());
			sendError(response, "Internal error: An item with that name already exists");
			return;
//			sendError(response, "Internal error: An item with that name already exists");
//			return;
		}catch (Exception e) {
			logger.error("Error creating elements", e);
			saveWorkspaceUploaderStatus(workspaceUploader, UPLOAD_STATUS.FAILED, "Uploading error. "+e.getMessage(), request.getSession());
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
		WorkspaceUploadFile wsUploadFile = new WorkspaceUploadFile(folderParentId, null, fileName, null);
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
	 * @param storageWrapper the storage wrapper
	 * @param currUser the curr user
	 * @param scopeGroupId the scope group id
	 * @param request the request
	 * @param httpSession the http session
	 * @param workspace the workspace
	 * @param itemId the item id
	 * @param destinationFolderId the destination folder id
	 * @param isOverwrite the is overwrite
	 */
	public static void notifyUploadInSharedFolder(final StorageHubWrapper storageWrapper, final GCubeUser currUser, final String scopeGroupId, final HttpServletRequest request, final HttpSession httpSession, final String itemId, final String destinationFolderId, final boolean isOverwrite){
		logger.trace("[2] HttpServletRequest is: URI: "+request.getRequestURI() +", ServerName: "+request.getServerName());
		final NotificationsWorkspaceUploaderProducer np = new NotificationsWorkspaceUploaderProducer(scopeGroupId, httpSession,request);
		new Thread(){
			public void run() {
				//Item sourceItem;
				try {
					Item sourceItem = storageWrapper.getStorageHubClientService().getItem(itemId);
					String sourceSharedId = null;
					try{
						sourceSharedId = storageWrapper.getStorageHubClientService().getIdSharedFolder(itemId);
					}catch(Exception e){
						//silent
					}
					Item folderDestinationItem = storageWrapper.getStorageHubClientService().getItem(destinationFolderId);
					FolderItem folderDestination = null;
					if(folderDestinationItem instanceof FolderItem){
						folderDestination = (FolderItem) folderDestinationItem;
					}

					Validate.notNull(folderDestination, "The folder destionation is null");

					logger.trace("[3] HttpServletRequest is: URI: "+request.getRequestURI() +", ServerName: "+request.getServerName());
					NotificationsWorkspaceUploader.checkSendNotifyChangedItemToShare(storageWrapper, request, currUser, scopeGroupId, np, httpSession, sourceItem, sourceSharedId, folderDestination);

				} catch (Exception e) {
					logger.error("Error in notifyUploadInSharedFolder", e);
				}
			};

		}.start();
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
	 * Send session expired.
	 *
	 * @param response the response
	 * @param message the message
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected void sendSessionExpired(HttpServletResponse response, String message) throws IOException{
		try {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			HandlerResultMessage resultMessage = HandlerResultMessage.sessionExpiredResult(message);
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
