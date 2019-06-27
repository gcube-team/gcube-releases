/**
 *
 */
package org.gcube.portlets.user.workspace.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.gcube.portlets.user.workspace.server.property.PortalUrlGroupGatewayProperty;
import org.gcube.portlets.user.workspace.shared.HandlerResultMessage;



/**
 * The Class DownloadPublicLinkServlet.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * May 16, 2017
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

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		String smp = req.getParameter("smp");
		boolean viewContent =  req.getParameter("viewContent")==null?false:req.getParameter("viewContent").equals("true");

		logger.trace("Input Params [smp: "+smp + ", viewContent: "+viewContent+"]");

		if(smp==null || smp.isEmpty()){
			sendError(resp,HttpServletResponse.SC_INTERNAL_SERVER_ERROR +": Item id is null");
			return;
		}

		logger.trace("PUBLIC FILE DOWNLOAD REQUEST "+smp);
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
		response.getWriter().write(resultMessage.toString());
		response.flushBuffer();
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
	    logger.trace("contextPath: "+contextPath);

	    url.append(contextPath);
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

//	/**
//	 * The main method.
//	 *
//	 * @param args the arguments
//	 */
//	public static void main(String[] args) {
//
//		InputStream is = null;
//		logger.trace("start");
//		try{
//
//			Workspace ws = HomeLibrary.getHomeManagerFactory().getHomeManager().getHome("francesco.mangiacrapa").getWorkspace();
//			WorkspaceItem wsItem = ws.getItem("907ce8ef-5c0b-4601-83ac-215d1f432f6b");
//			logger.trace("metadata info recovered from HL: [ID: "+wsItem.getId() +", name: "+wsItem.getName()+"]");
//			FileOutputStream out = new FileOutputStream(new File("/tmp/bla"));
//			logger.trace("cast as external file");
//			ExternalFile f = (ExternalFile) wsItem;
//			is = f.getData();
//			IOUtils.copy(is, out);
//			is.close();
//			out.close();
//			logger.trace("end");
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
}
