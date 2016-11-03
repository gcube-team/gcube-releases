/**
 *
 */
package org.gcube.portlets.user.workspace.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.gcube.portlets.user.workspace.client.ConstantsExplorer;
import org.gcube.portlets.user.workspace.server.property.PortalUrlGroupGatewayProperty;
import org.gcube.portlets.user.workspace.server.util.WsUtil;
import org.gcube.portlets.user.workspace.shared.HandlerResultMessage;
import org.gcube.portlets.user.workspace.shared.SessionExpiredException;

/**
 * @author M. Assante, CNR-ISTI
 */
@SuppressWarnings("serial")
public class DownloadFolderServlet extends HttpServlet{


	protected static Logger logger = Logger.getLogger(DownloadFolderServlet.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() throws ServletException {
		super.init();
		logger.trace("Workspace DownloadFolderServlet ready.");
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		boolean urlRedirectOnError = req.getParameter(ConstantsExplorer.REDIRECTONERROR)==null?false:req.getParameter(ConstantsExplorer.REDIRECTONERROR).equals("true");
		
		try {
			HttpSession session = req.getSession();
			if(WsUtil.isSessionExpired(session))
				throw new SessionExpiredException();
		} catch (Exception e) {

			if (e instanceof SessionExpiredException){
				sendErrorForStatus(resp, HttpServletResponse.SC_UNAUTHORIZED +": Session expired", HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}
		}
		
		String fileRelativePath = req.getParameter("filepath");
		String tmpDir = System.getProperty("java.io.tmpdir");
		String pathToFile = tmpDir + File.separator + fileRelativePath;
		try {
			logger.debug("looking for File in " + pathToFile);
			File tmpZip = new File(pathToFile);
			logger.debug("File instanciated " + pathToFile);
			
			resp.setHeader( "Content-Disposition", "attachment; filename=\"" + tmpZip.getName() + ".zip\"" );
			resp.setContentType("application/zip");
			resp = setContentLength(resp, tmpZip.length());
			OutputStream out = resp.getOutputStream();

			FileInputStream fileTmpZip = new FileInputStream(tmpZip);
			IOUtils.copy(fileTmpZip, resp.getOutputStream());
			fileTmpZip.close();

			out.close();
			tmpZip.deleteOnExit();
			return;

		} catch (Exception e) {
			logger.error("Error during folder compression "+pathToFile,e);
			handleError(urlRedirectOnError, req, resp, fileRelativePath, HttpServletResponse.SC_INTERNAL_SERVER_ERROR +": Error during folder compression: "+e.getMessage());
			return;
		}


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

		logger.trace("response written");
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
