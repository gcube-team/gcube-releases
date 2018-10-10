package org.gcube.portlets.user.reportgenerator.server.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalImage;
import org.gcube.common.homelibrary.util.MimeTypeUtil;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;

/**
 * Servlet implementation class DownloadImageServlet
 */
public class DownloadImageServlet extends HttpServlet {
	protected static Logger _log = Logger.getLogger(DownloadImageServlet.class);
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DownloadImageServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String itemId = req.getParameter("id");
		if(itemId==null || itemId.isEmpty()){
			sendError(resp,HttpServletResponse.SC_INTERNAL_SERVER_ERROR +": Item id is null");
			return;
		}
		Workspace wa;
		WorkspaceItem item;
		try{
			wa = getWorkspaceArea(req.getSession());
			item = wa.getItem(itemId);
			ExternalImage externalImage = (ExternalImage) item;

			String mimeType = externalImage.getMimeType();
			String itemName = MimeTypeUtil.getNameWithExtension(item.getName(), mimeType);

			resp.setHeader( "Content-Disposition", "inline; filename=\"" + itemName + "\"" );
			resp.setContentType(externalImage.getMimeType());

			resp.setContentLength((int) externalImage.getLength());

			OutputStream out = resp.getOutputStream();
			InputStream is = externalImage.getData();

			IOUtils.copy(is, out);
			is.close();

			out.close();
			return;
		} catch (Exception e) {
			_log.error("Error during item retrieving "+itemId,e);
			sendError(resp,HttpServletResponse.SC_INTERNAL_SERVER_ERROR +": Error during image retrieving: "+e.getMessage());
			return;
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {	}

	/**
	 * 
	 * @return
	 * @throws WorkspaceFolderNotFoundException
	 * @throws InternalErrorException
	 * @throws HomeNotFoundException
	 */
	protected Workspace getWorkspaceArea(HttpSession session) throws WorkspaceFolderNotFoundException, InternalErrorException, HomeNotFoundException {
		String user = (String) session.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);
		String currScope = ScopeProvider.instance.get();
		if (currScope == null) {
			String scope2Set = "/"+PortalContext.getConfiguration().getInfrastructureName();
			ScopeProvider.instance.set(scope2Set);
			_log.warn("Found scope null, setting infrastructure scope="+scope2Set);
		}
		if (user == null) {
			user = ReportServiceImpl.TEST_USER;
			ScopeProvider.instance.set(ReportServiceImpl.TEST_SCOPE);
			_log.warn("User is null in session, setting dev user = " + user);
		}
		Workspace toReturn = HomeLibrary.getUserWorkspace(user);	
		
		return toReturn;
	}


	protected void sendError(HttpServletResponse response, String resultMessage) throws IOException	{	
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		_log.trace("error message: "+resultMessage);
		_log.trace("writing response...");
		StringReader sr = new StringReader(resultMessage);
		IOUtils.copy(sr, response.getOutputStream());
		_log.trace("response wrote");
		response.flushBuffer();
	}

}
