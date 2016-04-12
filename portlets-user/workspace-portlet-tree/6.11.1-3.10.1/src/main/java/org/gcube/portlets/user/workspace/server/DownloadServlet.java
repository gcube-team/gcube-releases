/**
 * 
 */
package org.gcube.portlets.user.workspace.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
import org.gcube.portlets.user.workspace.client.ConstantsExplorer;
import org.gcube.portlets.user.workspace.server.property.PortalUrlGroupGatewayProperty;
import org.gcube.portlets.user.workspace.server.util.WsUtil;
import org.gcube.portlets.user.workspace.shared.HandlerResultMessage;
import org.gcube.portlets.user.workspace.shared.SessionExpiredException;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 */
public class DownloadServlet extends HttpServlet{

	private static final long serialVersionUID = -8423345575690165644L;

	protected static Logger logger = Logger.getLogger(DownloadServlet.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() throws ServletException {
		super.init();
		logger.trace("Workspace DownloadServlet ready.");
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		String itemId = req.getParameter("id");
		boolean viewContent =  (req.getParameter("viewContent")==null)?false:req.getParameter("viewContent").equals("true");
		boolean isValidItem =  (req.getParameter(ConstantsExplorer.VALIDATEITEM)==null)?false:req.getParameter(ConstantsExplorer.VALIDATEITEM).equals("true");
		boolean urlRedirectOnError = (req.getParameter(ConstantsExplorer.REDIRECTONERROR)==null)?false:req.getParameter(ConstantsExplorer.REDIRECTONERROR).equals("true");

		logger.trace("Input Params [id: "+itemId + ", viewContent: "+viewContent+", "+ConstantsExplorer.VALIDATEITEM +": " +isValidItem+", urlRedirectOnError:" +urlRedirectOnError+"]");
		if(itemId==null || itemId.isEmpty()){
			sendError(resp,HttpServletResponse.SC_INTERNAL_SERVER_ERROR +": Item id is null");
			return;
		}
		
		logger.trace("FILE DOWNLOAD REQUEST "+itemId);
		Workspace wa = null;
		try {
			//ADDED 13-01-2014 SESSION VALIDATION
			HttpSession session = req.getSession();
			if(WsUtil.isSessionExpired(session))
				throw new SessionExpiredException();

			wa = WsUtil.getWorkspace(session);
		} catch (Exception e) {
			
			if (e instanceof SessionExpiredException){
				sendErrorForStatus(resp, HttpServletResponse.SC_UNAUTHORIZED +": Session expired", HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}
			
			handleError(urlRedirectOnError, req, resp, itemId, HttpServletResponse.SC_INTERNAL_SERVER_ERROR +": Error during workspace retrieving");
			return;
		}

		if (wa == null) {
			handleError(urlRedirectOnError, req, resp, itemId, HttpServletResponse.SC_INTERNAL_SERVER_ERROR +": Error, no workspace in session");
			return;
		}

		WorkspaceItem item;
		try {
			
			item = wa.getItem(itemId);
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
				logger.error("Requested item "+itemId+" has thrown an internal error exception",e);
			}

		} catch (ItemNotFoundException e) {
			logger.error("Requested item "+itemId+" not found",e);
			handleError(urlRedirectOnError, req, resp, itemId, HttpServletResponse.SC_INTERNAL_SERVER_ERROR +": The file has been deleted by another user.");
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
					logger.error("Error during folder compression "+itemId,e);
					handleError(urlRedirectOnError, req, resp, itemId, HttpServletResponse.SC_INTERNAL_SERVER_ERROR +": Error during folder compression: "+e.getMessage());
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
							String contentDisposition = (viewContent)?"inline":"attachment";
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
							logger.error("Error during external item sending "+itemId,e);
							handleError(urlRedirectOnError, req, resp, itemId, HttpServletResponse.SC_INTERNAL_SERVER_ERROR +": Error during data retrieving: "+e.getMessage());
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
							String contentDisposition = (viewContent)?"inline":"attachment";
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
							logger.error("Error during external item sending "+itemId,e);
							handleError(urlRedirectOnError, req, resp, itemId, HttpServletResponse.SC_INTERNAL_SERVER_ERROR +": Error during data retrieving: "+e.getMessage());
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
							String contentDisposition = (viewContent)?"inline":"attachment";
							resp.setHeader("Content-Disposition", contentDisposition+"; filename=\"" + item.getName() + "\"" );
							resp.setContentType(mimeType);
							resp = setContentLength(resp, externalFile.getLength());
							is = externalFile.getData();
							out = resp.getOutputStream();
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
					case EXTERNAL_IMAGE:{
						
						try{
							ExternalImage externalImage = (ExternalImage)folderItem;

							String mimeType = externalImage.getMimeType();
							String itemName = MimeTypeUtil.getNameWithExtension(item.getName(), mimeType);

							String contentDisposition = (viewContent)?"inline":"attachment";
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
							logger.error("Error during item retrieving "+itemId,e);
							handleError(urlRedirectOnError, req, resp, itemId, HttpServletResponse.SC_INTERNAL_SERVER_ERROR +": Error during data retrieving: "+e.getMessage());
							return;
						}
					}
					case EXTERNAL_URL:{
						try{
							ExternalUrl externalUrl = (ExternalUrl)folderItem;
							
							String itemName = MimeTypeUtil.getNameWithExtension(externalUrl.getName(), "text/uri-list");
							String contentDisposition = (viewContent)?"inline":"attachment";
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
							logger.error("Error during item retrieving "+itemId,e);
							handleError(urlRedirectOnError, req, resp, itemId, HttpServletResponse.SC_INTERNAL_SERVER_ERROR +": Error during data retrieving: "+e.getMessage());
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
							handleError(urlRedirectOnError, req, resp, itemId, HttpServletResponse.SC_INTERNAL_SERVER_ERROR +": Error during data retrieving: "+e.getMessage());
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

							String contentDisposition = (viewContent)?"inline":"attachment";
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
							logger.error("Error during item retrieving "+itemId,e);
							handleError(urlRedirectOnError, req, resp, itemId, HttpServletResponse.SC_INTERNAL_SERVER_ERROR +": Error during data retrieving: "+e.getMessage());
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
							logger.error("Error during item retrieving "+itemId,e);
							handleError(urlRedirectOnError, req, resp, itemId, HttpServletResponse.SC_INTERNAL_SERVER_ERROR +": Error during data retrieving: "+e.getMessage());
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
							logger.error("Error during item retrieving "+itemId,e);
							handleError(urlRedirectOnError, req, resp, itemId, HttpServletResponse.SC_INTERNAL_SERVER_ERROR +": Error during data retrieving: "+e.getMessage());
							return;
						}
					}
				}

			}
		}
		
		handleError(urlRedirectOnError, req, resp, itemId,HttpServletResponse.SC_INTERNAL_SERVER_ERROR +": Error during data retrieving");
		return;
	}
	
	/**
	 * Method to manage HttpServletResponse content length also to big data
	 * @param resp
	 * @param length
	 * @return
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
	
	protected void handleError(boolean urlRedirectOnError, HttpServletRequest req, HttpServletResponse resp, String itemId, String message) throws IOException{
		
		logger.warn("Handle error occurred: "+message);
		logger.trace("urlRedirectOnError is active: "+urlRedirectOnError);
		if(urlRedirectOnError){
			urlRedirect(req, resp, itemId);
		}else
			sendError(resp,message);
		
	}
	
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
	
	protected void sendMessage(HttpServletResponse response, String message) throws IOException
	{
		response.setStatus(HttpServletResponse.SC_ACCEPTED);
		HandlerResultMessage resultMessage = HandlerResultMessage.okResult(message);
		response.getWriter().write(resultMessage.toString());
		response.flushBuffer();
	}
	
	protected void sendMessageResourceAvailable(HttpServletResponse response, String message) throws IOException 
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
	
	protected void urlRedirect(HttpServletRequest req, HttpServletResponse response, String fakePath) throws IOException  {

			String requestUrl = getRequestURL(req) +fakePath;
			logger.trace("Url redirect on: "+requestUrl);
//			System.out.println("Url redirect on: "+requestUrl);
		    response.sendRedirect(response.encodeRedirectURL(requestUrl));
		    return;
	}
	
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

	    if ((serverPort != 80) && (serverPort != 443)) {
	        url.append(":").append(serverPort);
	    }

	    logger.trace("server: "+url);
	    logger.trace("omitted contextPath: "+contextPath);
//	    logger.trace("servletPath: "+servletPath);
//	    url.append(contextPath).append(servletPath);
	    
//	    if (pathInfo != null) {
//	        url.append(pathInfo);
//	    }
//	    if (queryString != null) {
//	        url.append("?").append(queryString);
//	    }
	    
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
	
	/*
	public static void main(String[] args) {
		
		InputStream is = null;
		
		logger.trace("start");
	
//		is = GCUBEStorage.getRemoteFile("/Home/test.user/Workspace3d660604-03ef-49eb-89c3-4c73f8a47914");
		
		try{
			
			Workspace ws = HomeLibrary.getHomeManagerFactory().getHomeManager().getHome("francesco.mangiacrapa").getWorkspace();
//			
//			ExternalFile f = (ExternalFile) ws.getItem("907ce8ef-5c0b-4601-83ac-215d1f432f6b");
			
			WorkspaceItem wsItem = ws.getItem("907ce8ef-5c0b-4601-83ac-215d1f432f6b");
			
			
			logger.trace("metadata info recovered from HL: [ID: "+wsItem.getId() +", name: "+wsItem.getName()+"]");

			FileOutputStream out = new FileOutputStream(new File("/tmp/bla"));
//			byte[] buffer = new byte[1024];
//			int len;
//			while ((len = is.read(buffer)) != -1) {
//			    out.write(buffer, 0, len);
//			}
			
			logger.trace("cast as external file");
			ExternalFile f = (ExternalFile) wsItem;
			
			is = f.getData();
			
			IOUtils.copy(is, out);
			is.close();
			
			out.close();
			
//			logger.trace("Sleeping");
//			Thread.sleep(20000);
//			logger.trace("Alive");
			
			logger.trace("end");
		}catch (Exception e) {
			e.printStackTrace();
		}
	}*/
}
