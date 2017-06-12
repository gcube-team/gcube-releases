package org.apache.jackrabbit.j2ee.workspacemanager.versioning;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.jcr.Repository;
import javax.jcr.Session;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.j2ee.RepositoryAccessServlet;
import org.apache.jackrabbit.j2ee.workspacemanager.session.SessionManager;
import org.gcube.common.homelibary.model.servlet.ServletParameter;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class DownloadVersion extends HttpServlet {

	private static Logger logger = LoggerFactory.getLogger(DownloadVersion.class);
	private static final long serialVersionUID = 1L;
	private static final int BYTES_DOWNLOAD = 4096;

	public DownloadVersion() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {


		String sessionId = request.getParameter(ServletParameter.UUID);

		String id = request.getParameter(ServletParameter.ID);	
		String version = request.getParameter(ServletParameter.VERSION);	

		logger.info("Download Version: sessionID " + sessionId + " - id " + id + " - version " + version );
		Repository rep = RepositoryAccessServlet.getRepository(getServletContext());
		Session session = null;
		XStream xstream = null;


		SessionManager sessionManager = null;
		boolean exist = false;
		try {
			xstream = new XStream(new DomDriver("UTF-8"));

			sessionManager = SessionManager.getInstance(rep);
			session = sessionManager.newSession(request);
			sessionId = session.toString();

			String login = sessionManager.getLogin(request);

			download(request, response, xstream, session, id, version, login);

		} catch (Exception e) {
			logger.error("Error repository ex " + e);

		} finally {
			if (!exist)
				sessionManager.releaseSession(sessionId);		

		}
	}

	
	public void download(HttpServletRequest request,
			HttpServletResponse response, XStream xstream,
			Session session, String id, String version, String login) throws Exception{

		logger.info("Servlet Download Version called with parameters: [id: "+ id + " - version: " +version + " - login: "+ login+ "]");

		Workspace workspace = HomeLibrary.getHomeManagerFactory().getHomeManager().getHome(login).getWorkspace();
		WorkspaceItem item = workspace.getItem(id);
		long lenght = 0;
		String name = null;
		String mimeType = null;
		if (!item.isFolder()){
			ExternalFile file =  (ExternalFile) item;
			name = file.getName();
			lenght = file.getLength();
			mimeType = file.getMimeType();
		}

		
		OutputStream outStream = null;
		
		JCRVersioning versioning = new JCRVersioning(session, login);
		InputStream inStream = versioning.downloadVersion(id, version);
	
		try{

			response.setHeader("Content-Disposition",
					String.format("attachment; filename=\"%s\"", name));
			response.setContentType(mimeType);
			response.setContentLength((int) lenght);

			int read= -1;
			byte[] buffer = new byte[BYTES_DOWNLOAD];
			outStream = response.getOutputStream();

			while((read = inStream.read(buffer))!= -1){
				outStream.write(buffer, 0, read);
			}
			outStream.flush();


		}finally
		{
			if (outStream != null)
				outStream.close();
			if (inStream != null)
				inStream.close();
		}
	}
	

}

