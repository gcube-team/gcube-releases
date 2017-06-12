package org.apache.jackrabbit.j2ee.workspacemanager.servlets.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

import javax.jcr.Repository;
import javax.jcr.Session;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.jackrabbit.j2ee.RepositoryAccessServlet;
import org.apache.jackrabbit.j2ee.workspacemanager.session.SessionManager;
import org.gcube.common.homelibary.model.servlet.ServletParameter;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.homelibrary.util.WorkspaceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;


public class Upload extends HttpServlet {

	private static Logger logger = LoggerFactory.getLogger(Upload.class);
	private static final long serialVersionUID = 1L;
	private static final String DATA = "data";
	private String name;
	private String description;
	private String parentPath;
	private String mimetype;
	private String size;
//	private String filenameWithExtension;

	//	private static final String SEPARATOR = "/";
	//
	//	private static final String PORTAL_LOGIN = "hl:portalLogin";
	//	private static final String OWNER_NODE = "hl:owner";

	public Upload() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request,response);

	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		long start = System.currentTimeMillis();
		//		response.setContentType("text/plain");
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		String sessionId = request.getParameter(ServletParameter.UUID);

//		filenameWithExtension = request.getParameter(ServletParameter.FILENAME);

		name = request.getParameter(ServletParameter.NAME);
		description = request.getParameter(ServletParameter.DESCRIPTION);
		parentPath = request.getParameter(ServletParameter.PARENT_PATH);

		mimetype = request.getParameter(ServletParameter.MIMETYPE);
		size = request.getParameter(ServletParameter.SIZE);

		Repository rep = RepositoryAccessServlet.getRepository(getServletContext());

		Session session = null;
		XStream xstream = null;
		String xmlConfig = null;
		SessionManager sessionManager = null;
		boolean exist = false;
		try {
			xstream = new XStream(new DomDriver("UTF-8"));

			sessionManager = SessionManager.getInstance(rep);
			exist = sessionManager.sessionExists(sessionId); 
			if (exist){				
				session = sessionManager.getSession(sessionId);
				//				logger.info(sessionId + " already exists, get it");
			}
			else{				 
				session = sessionManager.newSession(request);
				sessionId = session.toString();
				//				logger.info(sessionId + " does not exist, a new session has been created " + session.toString());
			}


			try {   


				InputStream inputStream = request.getInputStream();
				logger.info("Get Inpustream: " + inputStream.available());
				try{
					List<FileItem> multiparts = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
					for (FileItem item : multiparts) {
						if (!item.isFormField()){
							if (item.getFieldName().equals(DATA))
								inputStream = item.getInputStream();
						} else {
							String field = item.getFieldName();
							String value = item.getString();

							switch (field) {
//							case ServletParameter.FILENAME:
//								filenameWithExtension  = value;
//								break;
							case ServletParameter.NAME:
								name  = value;
								break;
							case ServletParameter.DESCRIPTION:
								description  = value;
								break;
							case ServletParameter.PARENT_PATH:
								parentPath  = value;
								break;
							case ServletParameter.MIMETYPE:
								mimetype  = value;
								break;
							case ServletParameter.SIZE:
								size  = value;
								break;

							default:
								break;
							}
						}
					}
				}catch ( Exception e){
					logger.info("No multipart boundary was found");
				}

				logger.info("Called Rest Servlet Upload with parameters:");
				logger.info("Session Id: " +  sessionId);
//				logger.info("filenameWithExtension: " +  filenameWithExtension);
				logger.info("name: " +  name);
				logger.info("description: " +  description);
				logger.info("parentPath: " +  parentPath);
				logger.info("mimetype: " +  mimetype);
				logger.info("size: " +  size);


				Workspace workspace = HomeLibrary.getHomeManagerFactory().getHomeManager().getHome().getWorkspace();
				parentPath = Utils.cleanPath(workspace, parentPath);
				WorkspaceFolder destinationFolder = (WorkspaceFolder) workspace.getItemByPath(parentPath);


				FolderItem folderItem;
				if (mimetype!=null && size!=null)
					folderItem = WorkspaceUtil.createExternalFile(destinationFolder, name, description, inputStream, new HashMap<String, String>(), mimetype, Long.parseLong(size));
				else if (mimetype!=null)
					folderItem = WorkspaceUtil.createExternalFile(destinationFolder, name, description, mimetype, inputStream);
				else
					folderItem = WorkspaceUtil.createExternalFile(destinationFolder, name, description, inputStream);

				xmlConfig = xstream.toXML(folderItem.getPath());
				response.setContentLength(xmlConfig.length());
				out.println(xmlConfig);

			} catch (Exception e) {
				logger.error("Error saving inpustream for file: " + name, e);
				xmlConfig = xstream.toXML(e.toString());
				response.setContentLength(xmlConfig.length());
				out.println(xmlConfig);
			}


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (!exist){
				sessionManager.releaseSession(sessionId);
				//				logger.info("Released session " + sessionId);
			}
			out.close();
			out.flush();
			logger.info("**** " + name + " save in storage and jackrabbit in "+(System.currentTimeMillis()-start)+ " millis");
		}
	}





}
