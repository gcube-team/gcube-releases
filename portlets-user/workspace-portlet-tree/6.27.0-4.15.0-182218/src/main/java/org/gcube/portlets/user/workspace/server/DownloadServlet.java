/**
 *
 */
package org.gcube.portlets.user.workspace.server;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.storagehubwrapper.server.StorageHubWrapper;
import org.gcube.common.storagehubwrapper.server.tohl.Workspace;
import org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceFolder;
import org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem;
import org.gcube.common.storagehubwrapper.shared.tohl.items.FileItem;
import org.gcube.common.storagehubwrapper.shared.tohl.items.ImageFileItem;
import org.gcube.common.storagehubwrapper.shared.tohl.items.ItemStreamDescriptor;
import org.gcube.common.storagehubwrapper.shared.tohl.items.PDFFileItem;
import org.gcube.common.storagehubwrapper.shared.tohl.items.URLFileItem;
import org.gcube.common.storagehubwrapper.shared.tohl.items.URLItem;
import org.gcube.portlets.user.workspace.client.ConstantsExplorer;
import org.gcube.portlets.user.workspace.server.property.PortalUrlGroupGatewayProperty;
import org.gcube.portlets.user.workspace.server.util.AllowedMimeTypeToInline;
import org.gcube.portlets.user.workspace.server.util.MimeTypeUtility;
import org.gcube.portlets.user.workspace.server.util.WsUtil;
import org.gcube.portlets.user.workspace.shared.HandlerResultMessage;
import org.gcube.portlets.user.workspace.shared.SessionExpiredException;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * The Class DownloadServlet.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * Oct 18, 2018
 */
public class DownloadServlet extends HttpServlet{

	private static final long serialVersionUID = -8423345575690165644L;

	protected static Logger logger = LoggerFactory.getLogger(DownloadServlet.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() throws ServletException {
		super.init();
		logger.trace("Workspace DownloadServlet ready.");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		String itemId = req.getParameter("id");
		boolean viewContent =  req.getParameter("viewContent")==null?false:req.getParameter("viewContent").equals("true");
		boolean isValidItem =  req.getParameter(ConstantsExplorer.VALIDATEITEM)==null?false:req.getParameter(ConstantsExplorer.VALIDATEITEM).equals("true");
		boolean urlRedirectOnError = req.getParameter(ConstantsExplorer.REDIRECTONERROR)==null?false:req.getParameter(ConstantsExplorer.REDIRECTONERROR).equals("true");
		String contextID = req.getParameter(ConstantsExplorer.CURRENT_CONTEXT_ID);
		String versionID = req.getParameter(ConstantsExplorer.FILE_VERSION_ID);

		logger.info("Download Params " +
		"[id: "+itemId + ", " +
		"viewContent: "+viewContent+", " +
		ConstantsExplorer.VALIDATEITEM +": " +isValidItem+", " +
		"urlRedirectOnError:" +urlRedirectOnError+", " +
		"contextID: "+contextID+", " +
		"versionID: "+versionID+"]");

		if(itemId==null || itemId.isEmpty()){
			sendError(resp,HttpServletResponse.SC_INTERNAL_SERVER_ERROR +": Item id is null");
			return;
		}

		logger.debug("DOWNLOAD REQUEST FOR ITEM ID: "+itemId);
		//Workspace wa = null;
		StorageHubWrapper storageHubWrapper = null;
		try {

			if(WsUtil.isSessionExpired(req))
				throw new SessionExpiredException();

			GCubeUser gcubeUser = PortalContext.getConfiguration().getCurrentUser(req);
			storageHubWrapper = WsUtil.getStorageHubWrapper(req, contextID,gcubeUser);
			//wa = WsUtil.getWorkspace(req, contextID, gcubeUser);
		} catch (Exception e) {

			if (e instanceof SessionExpiredException){
				sendErrorForStatus(resp, HttpServletResponse.SC_UNAUTHORIZED +": Session expired", HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}

			handleError(urlRedirectOnError, req, resp, itemId, HttpServletResponse.SC_INTERNAL_SERVER_ERROR +": Error during workspace retrieving");
			return;
		}

		if (storageHubWrapper == null || storageHubWrapper.getWorkspace()==null) {
			handleError(urlRedirectOnError, req, resp, itemId, HttpServletResponse.SC_INTERNAL_SERVER_ERROR +": Error, no workspace in session");
			return;
		}

		WorkspaceItem item = null;
		Workspace wa = storageHubWrapper.getWorkspace();
		try {

			item = wa.getItem(itemId);
			if(isValidItem){ //ADDED 25/06/2013 - THIS CODE RETURN A SC_ACCEPT IS ITEM EXIST
				String message = HttpServletResponse.SC_ACCEPTED+ ": The resource is available";
				sendMessageResourceAvailable(resp, message);
				logger.trace("response return: "+message);
				return;
			}

		} catch (Exception e) {
			logger.error("Requested item "+itemId+" not found",e);
			handleError(urlRedirectOnError, req, resp, itemId, HttpServletResponse.SC_INTERNAL_SERVER_ERROR +": The file has been deleted by another user.");
			return;
		}

		switch (item.getType()) {
			case URL_ITEM: {
				try{
					logger.info("Downloading "+item.getType());
					String urlValue = "URL not found";
					if(item instanceof URLItem) {
						URLItem theURL = (URLItem) item;
						urlValue = theURL.getValue()==null?"URL not found":theURL.getValue().toString();
					}
					
					StringBuilder build = new StringBuilder();
					build.append("#URL downloaded from D4Science, source filename: "+item.getName());
					build.append("\n");
					build.append(urlValue);
					String fileContent = build.toString();
					logger.info("Writing file content: \n"+fileContent);
					ByteArrayInputStream is = new ByteArrayInputStream(fileContent.getBytes());
					String contentDisposition = viewContent?"inline":"attachment";
					String urlMimeType = "text/uri-list";
					String itemName = MimeTypeUtility.getNameWithExtension(item.getName(), urlMimeType);
					resp.setHeader("Content-Disposition", contentDisposition+"; filename=\"" + itemName + "\"" );
					resp.setContentType(urlMimeType);
					
					OutputStream out = resp.getOutputStream();
					IOUtils.copy(is, out);
					is.close();
					out.close();
					return;
				}catch (Exception e) {
					logger.error("Error during downloading the item "+itemId,e);
					handleError(urlRedirectOnError, req, resp, itemId, HttpServletResponse.SC_INTERNAL_SERVER_ERROR +": Error during data downloading: "+e.getMessage());
					return;
				}
			}
			case FOLDER:
			case SHARED_FOLDER:
			case VRE_FOLDER:
			case SMART_FOLDER: {

				WorkspaceFolder workspaceFolder =  (WorkspaceFolder) item;
				ItemStreamDescriptor descr;
				try {
					descr = wa.downloadFolder(workspaceFolder.getId(), workspaceFolder.getName(), null);
				}
				catch (Exception e) {
					logger.error("Error on downloading the folder with id "+itemId, e);
					String error = e.getMessage()!=null?e.getMessage():"The folder is not available for downloading";
					handleError(urlRedirectOnError, req, resp, itemId, HttpServletResponse.SC_INTERNAL_SERVER_ERROR +": "+error);
					return;
				}

				try{

					logger.info("Downloading the folder: "+workspaceFolder);
					String contentDisposition = viewContent?"inline":"attachment";
					String mimeType = "application/zip";
					String itemName = MimeTypeUtility.getNameWithExtension(item.getName(), mimeType);
					resp.setHeader("Content-Disposition", contentDisposition+"; filename=\"" + itemName + "\"" );
					resp.setContentType(mimeType);

					OutputStream out = resp.getOutputStream();
					InputStream is = descr.getStream();
					IOUtils.copy(is, out);

					is.close();
					out.close();
					return;
				} catch (Exception e) {
					logger.error("Error during item downloading "+itemId,e);
					handleError(urlRedirectOnError, req, resp, itemId, HttpServletResponse.SC_INTERNAL_SERVER_ERROR +": Error during folder data retrieving: "+e.getMessage());
					return;
				}

			}
			case FILE_ITEM:{

				FileItem workspaceFileItem =  (FileItem) item;
				ItemStreamDescriptor descr;
				try {
					logger.info("Downloading the file id: "+workspaceFileItem.getId()+" with name: "+workspaceFileItem.getName()+" and versionID: "+versionID);
					descr = wa.downloadFile(workspaceFileItem.getId(), workspaceFileItem.getName(), versionID, null);
				}
				catch (Exception e1) {
					logger.error("Error on downloading the file with id "+itemId, e1);
					String error = e1.getMessage()!=null?e1.getMessage():"The file is not available for downloading";
					handleError(urlRedirectOnError, req, resp, itemId, HttpServletResponse.SC_INTERNAL_SERVER_ERROR +": "+error);
					return;
				}

				switch (workspaceFileItem.getFileItemType()) {

					case PDF_DOCUMENT:{

						try{
							PDFFileItem pdfFile = (PDFFileItem) workspaceFileItem;
							logger.info("Downloading: "+pdfFile);
							String mimeType = pdfFile.getMimeType();
							logger.trace("EXTERNAL_FILE DOWNLOAD FOR "+pdfFile.getId());
							String contentDisposition = viewContent?"inline":"attachment";
							String itemName = MimeTypeUtility.getNameWithExtension(descr.getItemName(), mimeType);

							resp.setHeader("Content-Disposition", contentDisposition+"; filename=\"" + itemName + "\"" );
							resp.setContentType(mimeType);
							resp = setContentLength(resp, pdfFile.getSize());
							InputStream is = descr.getStream();
							OutputStream out = resp.getOutputStream();
							IOUtils.copy(is, out);

							is.close();
							out.close();

						} catch (Exception e) {
							logger.error("Error during external item retrieving "+itemId,e);
							handleError(urlRedirectOnError, req, resp, itemId, HttpServletResponse.SC_INTERNAL_SERVER_ERROR +": Error during data retrieving: "+e.getMessage());
							return;
						}
						return;

					}
					case IMAGE_DOCUMENT:{

						try{
							ImageFileItem imageFile = (ImageFileItem) workspaceFileItem;
							logger.info("Downloading: "+imageFile);
							String mimeType = imageFile.getMimeType();
							String itemName = MimeTypeUtility.getNameWithExtension(descr.getItemName(), mimeType);
							String contentDisposition = viewContent?"inline":"attachment";
							resp.setHeader("Content-Disposition", contentDisposition+"; filename=\"" + itemName + "\"" );
							resp.setContentType(mimeType);
							resp = setContentLength(resp, imageFile.getSize());

							InputStream is = descr.getStream();
							OutputStream out = resp.getOutputStream();
							IOUtils.copy(is, out);

							is.close();
							out.close();
							return;
						} catch (Exception e) {
							logger.error("Error during item retrieving "+itemId,e);
							handleError(urlRedirectOnError, req, resp, itemId, HttpServletResponse.SC_INTERNAL_SERVER_ERROR +": Error during data retrieving: "+e.getMessage());
							return;
						}
					}
					case URL_DOCUMENT:{
						try{
							URLFileItem externalUrl = (URLFileItem) workspaceFileItem;
							logger.info("Downloading: "+externalUrl);
							String urlMimeType = "text/uri-list";
							String itemName = MimeTypeUtility.getNameWithExtension(descr.getItemName(), urlMimeType);
							String contentDisposition = viewContent?"inline":"attachment";
							resp.setHeader("Content-Disposition", contentDisposition+"; filename=\"" + itemName + "\"" );
							resp.setContentType(urlMimeType);
							//resp = setContentLength(resp, externalUrl.getSize());

							//MODIFIED 22-05-2013 CLOSE STREAM
							OutputStream out = resp.getOutputStream();
							InputStream is = descr.getStream();
							IOUtils.copy(descr.getStream(), out);

							is.close();
							out.close();
							return;
						} catch (Exception e) {
							logger.error("Error during item retrieving "+itemId,e);
							handleError(urlRedirectOnError, req, resp, itemId, HttpServletResponse.SC_INTERNAL_SERVER_ERROR +": Error during data retrieving: "+e.getMessage());
							return;
						}
					}

					case GCUBE_ITEM:{
						try{
//							Document document = (Document)item;
							org.gcube.common.storagehubwrapper.shared.tohl.items.GCubeItem gcubeItem = (org.gcube.common.storagehubwrapper.shared.tohl.items.GCubeItem) item; //Cast GCubeItem
							logger.info("Downloading: "+gcubeItem);
							String mimeType = "text/plain";
							String contentDisposition = viewContent?"inline":"attachment";
							resp.setHeader("Content-Disposition", contentDisposition+"; filename=\"" + gcubeItem.getName() + ".txt\""  );
							resp.setContentType(mimeType);
							//MODIFIED 22-05-2013 CLOSE STREAM
							OutputStream out = resp.getOutputStream();
							InputStream is = descr.getStream();
							IOUtils.copy(is, out);
							is.close();

							out.close();

							return;
						} catch (Exception e) {
							logger.error("Error during item retrieving "+itemId,e);
							handleError(urlRedirectOnError, req, resp, itemId, HttpServletResponse.SC_INTERNAL_SERVER_ERROR +": Error during data retrieving: "+e.getMessage());
							return;
						}
					}
					default:{

						try{

							String itemName = MimeTypeUtility.getNameWithExtension(descr.getItemName(), workspaceFileItem.getMimeType());
							logger.info("Downloading default item: "+workspaceFileItem);

							//String contentDisposition = viewContent?"inline":"attachment";
							//Support #16430 
							//The 'inline' option may be badly managed by browser
							String contentDisposition = "attachment";
							List<String> allowedPrefixes = AllowedMimeTypeToInline.getAllowedMimeTypePrefixes();
							if(viewContent) {
								logger.info("Checking if the mime type "+workspaceFileItem.getMimeType()+" exists among Mime Type Prefixes");
								for (String prefix : allowedPrefixes) {
									 if(workspaceFileItem.getMimeType().startsWith(prefix)) {
										 logger.info("yes, the prefix "+prefix+" is matching the mimetype "+workspaceFileItem.getMimeType()+", so returning 'Content-Disposition' at 'inline'");
										 contentDisposition = "inline";
										 break;
									 }
								}
							}
							
							resp.setHeader("Content-Disposition", contentDisposition+"; filename=\"" + itemName + "\"" );
							if(workspaceFileItem.getSize()!=null && workspaceFileItem.getSize()>0)
								resp = setContentLength(resp, workspaceFileItem.getSize());

							//MODIFIED 22-05-2013 CLOSE STREAM
							OutputStream out = resp.getOutputStream();
							InputStream is = descr.getStream();
							IOUtils.copy(descr.getStream(), out);

							is.close();
							out.close();
							return;
						} catch (Exception e) {
							logger.error("Error during item retrieving "+itemId,e);
							handleError(urlRedirectOnError, req, resp, itemId, HttpServletResponse.SC_INTERNAL_SERVER_ERROR +": Error during data retrieving: "+e.getMessage());
							return;
						}
					}

				}

			}
		default:
			break;
		}

		handleError(urlRedirectOnError, req, resp, itemId,HttpServletResponse.SC_INTERNAL_SERVER_ERROR +": Error during data retrieving");
		return;
	}



	/**
	 * Gets the file name with version.
	 *  IF DONWLOADING A VERSIONED FILE ADDS THE VERSION NAME AS SUFFIX
	 *  DONE by StorageHub
	 * @param fileName the file name
	 * @param versionName the version name
	 * @return the file name with version
	 */
	protected String getFileNameWithVersion(String fileName, String versionName){

		String fileNameToDwnld = fileName;


		if(versionName!=null && !versionName.isEmpty())
			fileNameToDwnld = FilenameUtils.getBaseName(fileName)+"v"+versionName+FilenameUtils.getExtension(fileName);

		return fileNameToDwnld;
	}

	/**
	 * Method to manage HttpServletResponse content length also to big data.
	 *
	 * @param resp the resp
	 * @param length the length
	 * @return the http servlet response
	 */
	protected HttpServletResponse setContentLength(HttpServletResponse resp, long length){
		try{
		    if (length <= Integer.MAX_VALUE)
		    	resp.setContentLength((int)length);
		    else
		    	resp.addHeader("Content-Length", Long.toString(length));
		}catch(Exception e){
			//silent
		}
		return resp;
	}

	/**
	 * Handle error.
	 *
	 * @param urlRedirectOnError the url redirect on error
	 * @param req the req
	 * @param resp the resp
	 * @param itemId the item id
	 * @param message the message
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected void handleError(boolean urlRedirectOnError, HttpServletRequest req, HttpServletResponse resp, String itemId, String message) throws IOException{

		logger.warn("Handle error occurred: "+message);
		logger.trace("urlRedirectOnError is active: "+urlRedirectOnError);
		if(urlRedirectOnError){
			urlRedirect(req, resp, itemId, message);
		}else
			sendError(resp,message);

	}

	/**
	 * Send error.
	 *
	 * @param response the response
	 * @param message the message
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected void sendError(HttpServletResponse response, String message) throws IOException
	{
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		HandlerResultMessage resultMessage = HandlerResultMessage.errorResult(message);
		logger.trace("error message: "+resultMessage);
		logger.trace("writing response...");
		StringReader sr = new StringReader(resultMessage.toString());
		IOUtils.copy(sr, response.getOutputStream());

		logger.trace("response writed");
		response.flushBuffer();
	}


	/**
	 * Send error for status.
	 *
	 * @param response the response
	 * @param message the message
	 * @param status the status
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected void sendErrorForStatus(HttpServletResponse response, String message, int status) throws IOException
	{
		response.setStatus(status);
		HandlerResultMessage resultMessage = HandlerResultMessage.errorResult(message);
		logger.trace("error message: "+resultMessage);
		logger.trace("writing response...");
		StringReader sr = new StringReader(resultMessage.toString());
		IOUtils.copy(sr, response.getOutputStream());

		logger.trace("response writed");
		response.flushBuffer();
	}

	/**
	 * Send message.
	 *
	 * @param response the response
	 * @param message the message
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected void sendMessage(HttpServletResponse response, String message) throws IOException
	{
		response.setStatus(HttpServletResponse.SC_ACCEPTED);
		HandlerResultMessage resultMessage = HandlerResultMessage.okResult(message);
		response.getWriter().write(resultMessage.toString());
		response.flushBuffer();
	}

	/**
	 * Send message resource available.
	 *
	 * @param response the response
	 * @param message the message
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected void sendMessageResourceAvailable(HttpServletResponse response, String message) throws IOException
	{
		response.setStatus(HttpServletResponse.SC_ACCEPTED);
		HandlerResultMessage resultMessage = HandlerResultMessage.okResult(message);
		response.getWriter().write(resultMessage.toString());
		response.flushBuffer();
	}

	/**
	 * Send warn message.
	 *
	 * @param response the response
	 * @param message the message
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected void sendWarnMessage(HttpServletResponse response, String message) throws IOException
	{
		response.setStatus(HttpServletResponse.SC_ACCEPTED);
		HandlerResultMessage resultMessage = HandlerResultMessage.warnResult(message);
		response.getWriter().write(resultMessage.toString());
		response.flushBuffer();
	}

	/**
	 * Url redirect.
	 *
	 * @param req the req
	 * @param response the response
	 * @param fakePath the fake path
	 * @param errorMessage the error message
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected void urlRedirect(HttpServletRequest req, HttpServletResponse response, String fakePath, String errorMessage) throws IOException  {

			String requestUrl = getRequestURL(req) +fakePath;
			logger.trace("Url redirect on: "+requestUrl);
		    response.sendRedirect(response.encodeRedirectURL(requestUrl));
		    return;
	}
	
    /**
     * Creates the temp file.
     *
     * @param fileName the file name
     * @param extension the extension
     * @param data the data
     * @return the file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static File createTempFile(String fileName, String extension, byte[] data) throws IOException {
        // Since Java 1.7 Files and Path API simplify operations on files
    	java.nio.file.Path path = Files.createTempFile(fileName, extension);
        File file = path.toFile();
        // writing sample data
        Files.write(path, data);
        logger.info("Created the Temp File: "+file.getAbsolutePath());
        return file;
    }

	/**
	 * Gets the request url.
	 *
	 * @param req the req
	 * @return the request url
	 */
	public static String getRequestURL(HttpServletRequest req) {

	    String scheme = req.getScheme();             // http
	    String serverName = req.getServerName();     // hostname.com
	    int serverPort = req.getServerPort();        // 80
	    String contextPath = req.getContextPath();   // /mywebapp
//	    String servletPath = req.getServletPath();   // /servlet/MyServlet
//	    String pathInfo = req.getPathInfo();         // /a/b;c=123
//	    String queryString = req.getQueryString();          // d=789

	    // Reconstruct original requesting URL
	    StringBuffer url =  new StringBuffer();
	    url.append(scheme).append("://").append(serverName);

	    if (serverPort != 80 && serverPort != 443) {
	        url.append(":").append(serverPort);
	    }

	    logger.trace("server: "+url);
	    logger.trace("omitted contextPath: "+contextPath);
	    PortalUrlGroupGatewayProperty p = new PortalUrlGroupGatewayProperty();
		int lenght = p.getPath().length();
		String groupgatewaypath = "/";

		if(lenght>1){
			String lastChar = p.getPath().substring(lenght-1, lenght-1);
			groupgatewaypath+= lastChar.compareTo("/")!=0?p.getPath()+"/":p.getPath();
		}

		url.append(groupgatewaypath);
	    return url.toString();
	}
}
