/**
 *
 */
package org.gcube.portlets.user.workspaceexplorerapp.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalImage;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalUrl;
import org.gcube.common.homelibrary.home.workspace.folder.items.GCubeItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.Query;
import org.gcube.common.homelibrary.home.workspace.folder.items.Report;
import org.gcube.common.homelibrary.home.workspace.folder.items.ReportTemplate;
import org.gcube.common.homelibrary.home.workspace.folder.items.ts.TimeSeries;
import org.gcube.common.homelibrary.util.Extensions;
import org.gcube.common.homelibrary.util.MimeTypeUtil;
import org.gcube.common.homelibrary.util.zip.ZipUtil;
import org.gcube.portlets.user.workspaceexplorerapp.client.WorkspaceExplorerAppConstants;
import org.gcube.portlets.user.workspaceexplorerapp.shared.HandlerResultMessage;

/**
 * The Class DownloadServlet.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 */
public class DownloadWorkspaceExplorerServlet extends HttpServlet{

	/**
	 *
	 */
	private static final String ERROR404_HTML = "error404.html";

	private static final long serialVersionUID = -8423345575690165644L;

	protected static Logger logger = Logger.getLogger(DownloadWorkspaceExplorerServlet.class);
	public static final String ERROR_ITEM_DOES_NOT_EXIST = "Item does not exist. It may have been deleted by another user";

	public static final String REDIRECTONERROR = "redirectonerror";

	private final String VALIDATEITEM = WorkspaceExplorerAppConstants.VALIDATEITEM;
	private final String IDS = WorkspaceExplorerAppConstants.IDS;
	private final String IDS_SEPARATOR = WorkspaceExplorerAppConstants.IDS_SEPARATOR;
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

		String itemIds = req.getParameter(IDS);
		boolean viewContent =  req.getParameter("viewContent")==null?false:req.getParameter("viewContent").equals("true");
		boolean isValidItem =  req.getParameter(VALIDATEITEM)==null?false:req.getParameter(VALIDATEITEM).equals("true");
		boolean urlRedirectOnError = req.getParameter(REDIRECTONERROR)==null?false:req.getParameter(REDIRECTONERROR).equals("true");

		logger.trace("Input Params [ids: "+itemIds + ", viewContent: "+viewContent+", "+VALIDATEITEM +": " +isValidItem+", urlRedirectOnError:" +urlRedirectOnError+"]");
		if(itemIds==null || itemIds.isEmpty()){
			sendError(resp,HttpServletResponse.SC_INTERNAL_SERVER_ERROR +": Item id is null");
			return;
		}

		logger.trace("FILE DOWNLOAD REQUEST "+itemIds);
		List<String> ids = toList(itemIds, IDS_SEPARATOR);
		Workspace wa = null;
		try {

			wa = WsUtil.getWorkspace(req.getSession());
		} catch (Exception e) {

			/*if (e instanceof SessionExpiredException){
				sendErrorForStatus(resp, HttpServletResponse.SC_UNAUTHORIZED +": Session expired", HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}*/

			handleError(urlRedirectOnError, req, resp, itemIds, HttpServletResponse.SC_INTERNAL_SERVER_ERROR +": Error during workspace retrieving");
			return;
		}

		if (wa == null) {
			handleError(urlRedirectOnError, req, resp, itemIds, HttpServletResponse.SC_INTERNAL_SERVER_ERROR +": Error, no workspace in session");
			return;
		}

		WorkspaceItem item;
		try {

			//MULTIPLE DOWNLOAD - CREATE A ZIP FOR SUCH WORKSPACE IDs AND RETURN
			if(ids.size()>1){
				List<WorkspaceItem> listWI = toWorkspaceItems(wa, ids);
				try {
					File tmpZip = ZipUtil.zipWorkspaceItems(listWI, null);
					resp.setHeader( "Content-Disposition", "attachment; filename=\"gCube Workspace Files - " + new Date() +".zip\"" );
					resp.setContentType("application/zip");
					resp = setContentLength(resp, tmpZip.length());
					OutputStream out = resp.getOutputStream();

					FileInputStream fileTmpZip = new FileInputStream(tmpZip);
					IOUtils.copy(fileTmpZip, resp.getOutputStream());
					fileTmpZip.close();

					out.close();
					tmpZip.delete();
					return;
				}
				catch (InternalErrorException e) {
					logger.error("Error during folder compression "+itemIds,e);
					handleError(urlRedirectOnError, req, resp, itemIds, HttpServletResponse.SC_INTERNAL_SERVER_ERROR +": Error during folder compression: "+e.getMessage());
					return;
				}

			}

			//SINGLE DONWLOAD - DOWNLOAD WORKSPACE ITEM
			item = wa.getItem(ids.get(0));
			if(isValidItem){ //ADDED 25/06/2013 - THIS CODE RETURN A SC_ACCEPT IS ITEM EXIST
				String message = HttpServletResponse.SC_ACCEPTED+ ": The resource is available";
				sendMessageResourceAvailable(resp, message);
				logger.trace("response return: "+message);
				return;
			}

			try{
				//ACCOUNTING
				item.markAsRead(true);
			} catch (InternalErrorException e) {
				logger.error("Requested item "+itemIds+" has thrown an internal error exception",e);
			}

		} catch (ItemNotFoundException e) {
			logger.error("Requested item "+itemIds+" not found",e);
			handleError(urlRedirectOnError, req, resp, itemIds, HttpServletResponse.SC_INTERNAL_SERVER_ERROR +": The file has been deleted by another user.");
			return;
		}

		switch (item.getType()) {

			case SHARED_FOLDER:
			case FOLDER:{
				try {
					File tmpZip = ZipUtil.zipFolder((WorkspaceFolder) item);
					resp.setHeader( "Content-Disposition", "attachment; filename=\"" + item.getName() + ".zip\"" );
					resp.setContentType("application/zip");
					resp = setContentLength(resp, tmpZip.length());
					OutputStream out = resp.getOutputStream();

					FileInputStream fileTmpZip = new FileInputStream(tmpZip);
					IOUtils.copy(fileTmpZip, resp.getOutputStream());
					fileTmpZip.close();

					out.close();
					tmpZip.delete();
					return;

				} catch (Exception e) {
					logger.error("Error during folder compression "+itemIds,e);
					handleError(urlRedirectOnError, req, resp, itemIds, HttpServletResponse.SC_INTERNAL_SERVER_ERROR +": Error during folder compression: "+e.getMessage());
					return;
				}
			}
			case FOLDER_ITEM:{
				FolderItem folderItem = (FolderItem) item;

				switch (folderItem.getFolderItemType()) {

					case REPORT_TEMPLATE:{
						try{
							ReportTemplate reportTemplate = (ReportTemplate)folderItem;
							String extension = FilenameUtils.getExtension(item.getName());
							String itemName = item.getName();
							logger.trace("case REPORT_TEMPLATE extension is" +extension);

							if(extension.compareToIgnoreCase(Extensions.REPORT_TEMPLATE.getName())!=0) //ADD EXTENSION?
								itemName =  "." + Extensions.REPORT_TEMPLATE.getName();

							logger.trace("case REPORT_TEMPLATE itemName is" +extension);
							String contentDisposition = viewContent?"inline":"attachment";
							resp.setHeader( "Content-Disposition", contentDisposition+"; filename=\"" + itemName + "\"" );

							resp.setContentType("application/zip");
							resp = setContentLength(resp, reportTemplate.getLength());
							OutputStream out = resp.getOutputStream();

							//MODIFIED 22-05-2013 CLOSE STREAM
							InputStream is = reportTemplate.getData();
							IOUtils.copy(is, resp.getOutputStream());
							is.close();

							out.close();
						} catch (Exception e) {
							logger.error("Error during external item sending "+itemIds,e);
							handleError(urlRedirectOnError, req, resp, itemIds, HttpServletResponse.SC_INTERNAL_SERVER_ERROR +": Error during data retrieving: "+e.getMessage());
							return;
						}
						return;

					}
					case REPORT:{
						try{
							Report report = (Report)folderItem;
							String extension = FilenameUtils.getExtension(item.getName());
							String itemName = item.getName();
							logger.trace("case REPORT extension is" +extension);

							if(extension.compareToIgnoreCase(Extensions.REPORT.getName())!=0) //ADD EXTENSION?
									itemName =  "." + Extensions.REPORT.getName();

							logger.trace("case REPORT itemName is" +extension);
							String contentDisposition = viewContent?"inline":"attachment";
							resp.setHeader( "Content-Disposition", contentDisposition+"; filename=\"" + itemName + "\"" );

							resp.setContentType("application/zip");
							resp = setContentLength(resp, report.getLength());
							OutputStream out = resp.getOutputStream();

							//MODIFIED 22-05-2013 CLOSE STREAM
							InputStream is = report.getData();
							IOUtils.copy(is, resp.getOutputStream());
							is.close();

							out.close();
						} catch (Exception e) {
							logger.error("Error during external item sending "+itemIds,e);
							handleError(urlRedirectOnError, req, resp, itemIds, HttpServletResponse.SC_INTERNAL_SERVER_ERROR +": Error during data retrieving: "+e.getMessage());
							return;
						}
						return;

					}
					case EXTERNAL_PDF_FILE:
					case EXTERNAL_FILE:{

						InputStream is = null;
						OutputStream out = null;
						try{
							ExternalFile externalFile = (ExternalFile)folderItem;

							String mimeType = externalFile.getMimeType();

							logger.trace("EXTERNAL_FILE DOWNLOAD FOR "+externalFile.getId());
							String contentDisposition = viewContent?"inline":"attachment";
							resp.setHeader("Content-Disposition", contentDisposition+"; filename=\"" + item.getName() + "\"" );
							resp.setContentType(mimeType);
							resp = setContentLength(resp, externalFile.getLength());
							is = externalFile.getData();
							out = resp.getOutputStream();
							IOUtils.copy(is, out);

							is.close();
							out.close();

						} catch (Exception e) {
							logger.error("Error during external item retrieving "+itemIds,e);
							handleError(urlRedirectOnError, req, resp, itemIds, HttpServletResponse.SC_INTERNAL_SERVER_ERROR +": Error during data retrieving: "+e.getMessage());
							return;
						}
						return;

					}
					case EXTERNAL_IMAGE:{

						try{
							ExternalImage externalImage = (ExternalImage)folderItem;

							String mimeType = externalImage.getMimeType();
							String itemName = MimeTypeUtil.getNameWithExtension(item.getName(), mimeType);

							String contentDisposition = viewContent?"inline":"attachment";
							resp.setHeader( "Content-Disposition", contentDisposition+"; filename=\"" + itemName + "\"" );
							resp.setContentType(externalImage.getMimeType());
							resp = setContentLength(resp, externalImage.getLength());
							OutputStream out = resp.getOutputStream();
							InputStream is = externalImage.getData();
							IOUtils.copy(is, out);
							is.close();

							out.close();
							return;
						} catch (Exception e) {
							logger.error("Error during item retrieving "+itemIds,e);
							handleError(urlRedirectOnError, req, resp, itemIds, HttpServletResponse.SC_INTERNAL_SERVER_ERROR +": Error during data retrieving: "+e.getMessage());
							return;
						}
					}
					case EXTERNAL_URL:{
						try{
							ExternalUrl externalUrl = (ExternalUrl)folderItem;

							String itemName = MimeTypeUtil.getNameWithExtension(externalUrl.getName(), "text/uri-list");
							String contentDisposition = viewContent?"inline":"attachment";
							resp.setHeader("Content-Disposition", contentDisposition+"; filename=\"" + itemName + "\"" );
							resp.setContentType("text/uri-list");
							resp = setContentLength(resp, externalUrl.getLength());

							//MODIFIED 22-05-2013 CLOSE STREAM
							StringReader sr = new StringReader(externalUrl.getUrl());
							OutputStream out = resp.getOutputStream();
							IOUtils.copy(sr, out);

							sr.close();
							out.close();
							return;
						} catch (Exception e) {
							logger.error("Error during item retrieving "+itemIds,e);
							handleError(urlRedirectOnError, req, resp, itemIds, HttpServletResponse.SC_INTERNAL_SERVER_ERROR +": Error during data retrieving: "+e.getMessage());
							return;
						}
					}
					case QUERY:{

						Query query = (Query)folderItem;
						resp.setContentType("text/plain");
						try {
							resp = setContentLength(resp, query.getLength());
						} catch (Exception e) {
							logger.error("Error getting item lenght "+query,e);
							handleError(urlRedirectOnError, req, resp, itemIds, HttpServletResponse.SC_INTERNAL_SERVER_ERROR +": Error during data retrieving: "+e.getMessage());
							return;
						}

						//MODIFIED 22-05-2013 CLOSE STREAM
						OutputStream out = resp.getOutputStream();
						StringReader sr = new StringReader(query.getQuery());
						IOUtils.copy(sr, out);
						sr.close();

						out.close();
						return;

					}
					case TIME_SERIES:{
						try{
							TimeSeries ts = (TimeSeries)folderItem;
							File tmpZip = ZipUtil.zipTimeSeries(ts);

							String contentDisposition = viewContent?"inline":"attachment";
							resp.setHeader( "Content-Disposition", contentDisposition+"; filename=\"" + item.getName() + ".zip\"" );
							resp.setContentType("application/zip");
							resp = setContentLength(resp, tmpZip.length());

							//MODIFIED 22-05-2013 CLOSE STREAM
							OutputStream out = resp.getOutputStream();
							FileInputStream fileTmpZip = new FileInputStream(tmpZip);
							IOUtils.copy(fileTmpZip, out);
							fileTmpZip.close();

							out.close();
							tmpZip.delete();
							return;
						} catch (Exception e) {
							logger.error("Error during item retrieving "+itemIds,e);
							handleError(urlRedirectOnError, req, resp, itemIds, HttpServletResponse.SC_INTERNAL_SERVER_ERROR +": Error during data retrieving: "+e.getMessage());
							return;
						}
					}
					case IMAGE_DOCUMENT:
					case PDF_DOCUMENT:
					case URL_DOCUMENT:
					case DOCUMENT:{
						try{
//							Document document = (Document)item;
							GCubeItem document = (GCubeItem) item; //Cast GCubeItem

							if (!viewContent){
								File tmpZip = ZipUtil.zipDocument(document);

								resp.setHeader( "Content-Disposition", "attachment; filename=\"" + item.getName() + ".zip\"" );
								resp.setContentType("application/zip");
								resp = setContentLength(resp, tmpZip.length());

								//MODIFIED 22-05-2013 CLOSE STREAM
								OutputStream out = resp.getOutputStream();
								FileInputStream fileTmpZip = new FileInputStream(tmpZip);
								IOUtils.copy(fileTmpZip, out);
								fileTmpZip.close();

								out.close();
								tmpZip.delete();
							}
							else{
								String mimeType = document.getMimeType();
								String itemName = MimeTypeUtil.getNameWithExtension(item.getName(), mimeType);
								resp.setHeader( "Content-Disposition", "inline; filename=\"" + itemName + "\"" );
								resp.setContentType(document.getMimeType());
								resp = setContentLength(resp, document.getLength());
								//MODIFIED 22-05-2013 CLOSE STREAM
								OutputStream out = resp.getOutputStream();
								InputStream is = document.getData();
								IOUtils.copy(is, out);
								is.close();

								out.close();
							}
							return;
						} catch (Exception e) {
							logger.error("Error during item retrieving "+itemIds,e);
							handleError(urlRedirectOnError, req, resp, itemIds, HttpServletResponse.SC_INTERNAL_SERVER_ERROR +": Error during data retrieving: "+e.getMessage());
							return;
						}
					}

					case METADATA:{
						try{
//							Metadata document = (Metadata)item;
							GCubeItem metadata = (GCubeItem) item; //Cast GCubeItem

							resp.setContentType("text/html");
							resp = setContentLength(resp, metadata.getLength());

							//MODIFIED 22-05-2013 CLOSE STREAM
							OutputStream out = resp.getOutputStream();
							InputStream is = metadata.getData();
							IOUtils.copy(is, out);
							is.close();
							out.close();

							return;
						} catch (Exception e) {
							logger.error("Error during item retrieving "+itemIds,e);
							handleError(urlRedirectOnError, req, resp, itemIds, HttpServletResponse.SC_INTERNAL_SERVER_ERROR +": Error during data retrieving: "+e.getMessage());
							return;
						}
					}
				}

			}
		}

		handleError(urlRedirectOnError, req, resp, itemIds,HttpServletResponse.SC_INTERNAL_SERVER_ERROR +": Error during data retrieving");
		return;
	}


	/**
	 * To list.
	 *
	 * @param ids the ids
	 * @param separator the separator
	 * @return the list
	 */
	private static List<String> toList(String ids, String separator){

		String[] toArray = ids.split(separator);
		List<String> lstIds = new ArrayList<String>(toArray.length);
		for (String id : toArray) {
			if(id!=null && !id.isEmpty())
				lstIds.add(id);
		}
		logger.trace("ids to list: "+lstIds);
		return lstIds;
	}


	/**
	 * To workspace items.
	 *
	 * @param ws the ws
	 * @param workspaceItemIds the workspace item ids
	 * @return the list
	 */
	private List<WorkspaceItem> toWorkspaceItems(Workspace ws, List<String> workspaceItemIds){

		if(workspaceItemIds==null)
			return null;

		List<WorkspaceItem> listWI = new ArrayList<WorkspaceItem>(workspaceItemIds.size());

		for (String wsId : workspaceItemIds) {
			try{
				listWI.add(ws.getItem(wsId));
			}catch(Exception e){
				logger.warn("Error on getting item id: "+wsId +", skipping item");
			}
		}
		return listWI;
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
			urlRedirect(req, resp, ERROR404_HTML);
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
	 * @param errorPage the error page
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected void urlRedirect(HttpServletRequest req, HttpServletResponse response, String errorPage) throws IOException  {

			String requestUrl = getRedirectURL(req) +errorPage;
			logger.trace("Url redirect on: "+requestUrl);
//			System.out.println("Url redirect on: "+requestUrl);
		    response.sendRedirect(response.encodeRedirectURL(requestUrl));
		    return;
	}

	/**
	 * Gets the redirect url.
	 *
	 * @param req the req
	 * @return the redirect url
	 */
	public static String getRedirectURL(HttpServletRequest req) {

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

	    url.append("/").append(contextPath);
	    logger.trace("returning url: "+url);
	    return url.toString();
	}
}
