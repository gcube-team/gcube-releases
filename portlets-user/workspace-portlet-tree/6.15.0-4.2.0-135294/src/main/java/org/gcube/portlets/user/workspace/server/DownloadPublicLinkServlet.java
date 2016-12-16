/**
 * 
 */
package org.gcube.portlets.user.workspace.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
import org.gcube.portlets.user.workspace.server.property.PortalUrlGroupGatewayProperty;
import org.gcube.portlets.user.workspace.shared.HandlerResultMessage;


/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jul 1, 2013
 *
 */
public class DownloadPublicLinkServlet extends HttpServlet{

	private static final long serialVersionUID = -8423345575690165644L;

	protected static Logger logger =  Logger.getLogger(DownloadPublicLinkServlet.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() throws ServletException {
		super.init();
		logger.trace("Workspace DownloadPublicLinkServlet ready.");
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		String smp = req.getParameter("smp");
		boolean viewContent =  (req.getParameter("viewContent")==null)?false:req.getParameter("viewContent").equals("true");
		
		logger.trace("Input Params [smp: "+smp + ", viewContent: "+viewContent+"]");
		
		if(smp==null || smp.isEmpty()){
			sendError(resp,HttpServletResponse.SC_INTERNAL_SERVER_ERROR +": Item id is null");
			return;
		}
		
		logger.trace("PUBLIC FILE DOWNLOAD REQUEST "+smp);
		
		//COMMENTED 26/03/2013
//							String itemName = MimeTypeUtil.getNameWithExtension(item.getName(), mimeType);

//		String contentDisposition = (viewContent)?"inline":"attachment";
//		//COMMENTED 26/03/2013
////		resp.setHeader( "Content-Disposition", contentDisposition+"; filename=\"" + itemName + "\"" );
//		resp.setHeader( "Content-Disposition", contentDisposition+"; filename=\"" + item.getName() + "\"" );
//		
//		resp.setContentType(mimeType);
//
//		resp.setContentLength((int) externalFile.getLength());
//		
//		
//		//MODIFIED 22-05-2013 CLOSE STREAM
////							IOUtils.copy(externalFile.getData(), resp.getOutputStream());
//		is = externalFile.getData();
//		out = resp.getOutputStream();
//		IOUtils.copy(is, out);
//				
//				is.close();
//				out.close();

	}
	
//	protected void handleError(boolean urlRedirectOnError, HttpServletRequest req, HttpServletResponse resp, String itemId, String message) throws IOException{
//		
//		logger.warn("Handle error occurred: "+message);
//		if(urlRedirectOnError){
//			urlRedirect(req, resp, itemId);
//		}else
//			sendError(resp,message);
//		
//	}
	
	protected void sendError(HttpServletResponse response, String message) throws IOException
	{	
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		HandlerResultMessage resultMessage = HandlerResultMessage.errorResult(message);
		response.getWriter().write(resultMessage.toString());
		response.flushBuffer();
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
	    logger.trace("contextPath: "+contextPath);
//	    logger.trace("servletPath: "+servletPath);
//	    url.append(contextPath).append(servletPath);
	    
	    
	    url.append(contextPath);

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
	}
}
