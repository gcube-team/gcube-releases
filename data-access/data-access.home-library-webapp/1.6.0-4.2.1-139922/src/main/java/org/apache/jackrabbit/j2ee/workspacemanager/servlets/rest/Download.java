package org.apache.jackrabbit.j2ee.workspacemanager.servlets.rest;

import java.io.File;
import java.io.FileInputStream;
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
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
import org.gcube.common.homelibrary.util.zip.ZipUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class Download extends HttpServlet {

	private static Logger logger = LoggerFactory.getLogger(Download.class);
	private static final long serialVersionUID = 1L;
	private static final int BYTES_DOWNLOAD = 4096;

	public Download() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {


		String absPath = request.getParameter(ServletParameter.ABS_PATH);

		Repository rep = RepositoryAccessServlet.getRepository(getServletContext());
		Session session = null;
		XStream xstream = null;
		String sessionId = null;
//		String xmlConfig;
		
		SessionManager sessionManager = null;
		boolean exist = false;
		try {
			xstream = new XStream(new DomDriver("UTF-8"));

			sessionManager = SessionManager.getInstance(rep);
			session = sessionManager.newSession(request);
			sessionId = session.toString();


			String login = sessionManager.getLogin(sessionId);

//			PrincipalManager principalManager = AccessControlUtil.getPrincipalManager(session);
//
//			Principal principal = principalManager.getPrincipal(login);
//
//			JackrabbitAccessControlManager  jacm = (JackrabbitAccessControlManager) session.getAccessControlManager();
//			Set<Principal> principals = new HashSet<Principal>();
//			principals.add(principal);
//
//			boolean canRead = jacm.hasPrivileges(absPath, principals, new Privilege[] {
//					jacm.privilegeFromName(CustomPrivilege.JCR_READ)
//			});
//
//			logger.info("Can " + principal.getName() + " read node " + absPath + "? " + canRead);		
//
//			if (canRead)				
				download(request, response, xstream, session, absPath, login);
//			else
//				xmlConfig = xstream.toXML("No privilege to read the file/folder " + absPath);

		} catch (Exception e) {
			logger.error("Error repository ex " + e);

		} finally {
			if (!exist)
				sessionManager.releaseSession(sessionId);		

		}
	}

	private void download(HttpServletRequest request,
			HttpServletResponse response, XStream xstream,
			Session session, String absPath, String login) throws Exception{

		logger.info("Servlet Download called with parameters: [absPath: "+ absPath + "]");

		Workspace workspace = HomeLibrary.getHomeManagerFactory().getHomeManager().getHome().getWorkspace();
		absPath = Utils.cleanPath(workspace, absPath);
		WorkspaceItem item = workspace.getItemByPath(absPath);
		String name = item.getName();
		File file = null; 
		InputStream inStream = null;
		OutputStream outStream = null;

		String mimeType;
		int lenght;
		try{
			if (item.isFolder()){

				WorkspaceFolder folder = (WorkspaceFolder) item;
				name = name + ".zip";

				logger.info("Download zip folder " + name);

				file = ZipUtil.zipFolder(folder);
				inStream = new FileInputStream(file);

				mimeType = "application/zip";
				lenght = (int) file.length();

			}else{
				logger.info("Download file " + name);

				ExternalFile myfile = (ExternalFile) item;
				inStream = myfile.getData();

				if (inStream.available()==0 )
					logger.error("No inpustream for item " + item.getName());

				mimeType = myfile.getMimeType();
				lenght = (int) myfile.getLength();

			}

			response.setHeader("Content-Disposition",
					String.format("attachment; filename=\"%s\"", name));
			response.setContentType(mimeType);
			response.setContentLength(lenght);

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

