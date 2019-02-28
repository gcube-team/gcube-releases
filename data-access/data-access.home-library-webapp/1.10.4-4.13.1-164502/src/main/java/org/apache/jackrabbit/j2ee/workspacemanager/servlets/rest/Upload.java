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
import org.apache.commons.fileupload.ProgressListener;
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

/**
 * Upload servlet
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class Upload extends HttpServlet {

	private static Logger logger = LoggerFactory.getLogger(Upload.class);
	private static final long serialVersionUID = 1L;
	private static final String DATA = "data";
	private final static int THRESHOLD_BEFORE_DISK_IS_USED_MULTIPART = 0xfa00000; // 250MB
	private final static long MAX_ALLOWED_REQUEST_MULTIPART = 0xa00000000l; // 40GB
	private static DiskFileItemFactory factory;

	public Upload() {
		super();
	}

	@Override
	public void init() throws ServletException {
		super.init();
		factory = new DiskFileItemFactory();
		factory.setSizeThreshold(THRESHOLD_BEFORE_DISK_IS_USED_MULTIPART); // write on disk if ...
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request,response);

	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		long start = System.currentTimeMillis();
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		String sessionId = request.getParameter(ServletParameter.UUID);
		String name = request.getParameter(ServletParameter.NAME);
		String description = request.getParameter(ServletParameter.DESCRIPTION);
		String parentPath = request.getParameter(ServletParameter.PARENT_PATH);
		String mimetype = request.getParameter(ServletParameter.MIMETYPE);
		String size = request.getParameter(ServletParameter.SIZE);

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
			}
			else{				 
				session = sessionManager.newSession(request);
				sessionId = session.toString();
			}

			try {   

				InputStream inputStream = null;

				// check if it is a multipart request or not
				if(!ServletFileUpload.isMultipartContent(request)){
					inputStream = request.getInputStream();
				}
				else{
					try{

						ServletFileUpload upload = new ServletFileUpload(factory);
						upload.setSizeMax(MAX_ALLOWED_REQUEST_MULTIPART);

						// progress upload for file
						upload.setProgressListener(new ProgressListener(){
							private long megaBytes = -1;
							public void update(long pBytesRead, long pContentLength, int pItems) {
								long mBytes = pBytesRead / 1000000;
								if (megaBytes == mBytes) {
									return;
								}
								megaBytes = mBytes;
								logger.debug("We are currently reading item " + pItems);
								if (pContentLength == -1) {
									logger.debug("So far, " + pBytesRead + " bytes have been read.");
								} else {
									logger.debug("So far, " + pBytesRead + " of " + pContentLength
											+ " bytes have been read.");
								}
							}
						});

						List<FileItem> multiparts = upload.parseRequest(request);

						for (FileItem item : multiparts) {
							if (!item.isFormField()){ // i.e. is not a file
								if (item.getFieldName().equals(DATA)){
									inputStream = item.getInputStream();
									if(size == null || size.isEmpty()){
										size = String.valueOf(item.getSize());
										logger.debug("File size read from the inputstream is " + size);
									}
								}
							}else{
								String field = item.getFieldName();
								String value = item.getString();

								switch (field) {
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
					}catch (Exception e){
						logger.error("Exception while handling multipart request ", e);
						xmlConfig = xstream.toXML(e.toString());
						response.setContentLength(xmlConfig.length());
						out.println(xmlConfig);
						return;
					}
				}

				logger.info("Called Rest Servlet Upload with parameters:");
				logger.info("Session Id: " +  sessionId);
				logger.info("name: " +  name);
				logger.info("description: " +  description);
				logger.info("parentPath: " +  parentPath);
				logger.info("mimetype: " +  mimetype);
				logger.info("size: " +  size);

				Workspace workspace = HomeLibrary.getHomeManagerFactory().getHomeManager().getHome().getWorkspace();
				parentPath = Utils.cleanPath(workspace, parentPath);
				WorkspaceFolder destinationFolder = (WorkspaceFolder) workspace.getItemByPath(parentPath);
				FolderItem folderItem = (FolderItem)destinationFolder.find(name);

				if(folderItem != null){
					logger.info("A file named " + name + " already exists into " + destinationFolder.getPath() + ". Updating its content ...");
					folderItem.updateItem(inputStream);
				}else{
					if (mimetype!=null && size!=null)
						folderItem = WorkspaceUtil.createExternalFile(destinationFolder, name, description, inputStream, new HashMap<String, String>(), mimetype, Long.parseLong(size));
					else if (mimetype!=null)
						folderItem = WorkspaceUtil.createExternalFile(destinationFolder, name, description, mimetype, inputStream);
					else
						folderItem = WorkspaceUtil.createExternalFile(destinationFolder, name, description, inputStream);
				}

				xmlConfig = xstream.toXML(folderItem.getPath());
				response.setContentLength(xmlConfig.length());
				out.println(xmlConfig);

				logger.info("**** " + name + " save in storage and jackrabbit in "+(System.currentTimeMillis()-start)+ " millis");

			} catch (Exception e) {
				logger.error("Error saving inpustream for file: " + name, e);
				xmlConfig = xstream.toXML(e.toString());
				response.setContentLength(xmlConfig.length());
				out.println(xmlConfig);
			}
		} catch (Exception e) {
			logger.error("An error occurred while uploading file", e);
			xmlConfig = xstream.toXML(e.toString());
			response.setContentLength(xmlConfig.length());
			out.println(xmlConfig);
		} finally {
			if (!exist){
				sessionManager.releaseSession(sessionId);
			}
			out.close();
			out.flush();
		}
	}

}
