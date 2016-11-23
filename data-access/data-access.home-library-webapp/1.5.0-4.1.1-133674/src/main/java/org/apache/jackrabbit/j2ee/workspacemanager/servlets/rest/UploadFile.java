package org.apache.jackrabbit.j2ee.workspacemanager.servlets.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.jackrabbit.j2ee.RepositoryAccessServlet;
import org.apache.jackrabbit.j2ee.workspacemanager.ItemDelegateWrapper;
import org.gcube.common.homelibary.model.servlet.ServletParameter;
import org.apache.jackrabbit.j2ee.workspacemanager.SessionManager;
import org.apache.jackrabbit.j2ee.workspacemanager.storage.GCUBEStorage;
import org.apache.jackrabbit.j2ee.workspacemanager.util.MetaInfo;
import org.apache.jackrabbit.j2ee.workspacemanager.util.Util;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.type.ContentType;
import org.gcube.common.homelibary.model.items.type.FolderItemType;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibary.model.items.type.PrimaryNodeType;
import org.gcube.common.homelibary.model.util.WorkspaceItemAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;


public class UploadFile extends HttpServlet {

	private static Logger logger = LoggerFactory.getLogger(UploadFile.class);
	private static final long serialVersionUID = 1L;
	
	public UploadFile() {
		super();
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		String sessionId = request.getParameter(ServletParameter.UUID);
			
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

			uploadFile(request, response, out, xstream, session);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error repository ex " + e.getMessage());
			xmlConfig = xstream.toXML(e.toString());
			response.setContentLength(xmlConfig.length());
			out.println(xmlConfig);

		} finally {
			if (!exist){
				sessionManager.releaseSession(sessionId);
				//				logger.info("Released session " + sessionId);
			}
			out.close();
			out.flush();
		}
	}




	/**
	 * Upload a file
	 * @param request
	 * @param response
	 * @param out
	 * @param xstream
	 * @param session
	 */
	private void uploadFile(HttpServletRequest request, HttpServletResponse response, PrintWriter out, XStream xstream, Session session) {

		String xmlConfig;
		String portalLogin;
		String scope;

		String filenameWithExtension = null;
		String serviceName;

		String name;
		String description;
		String parentPath;
		
		String mimetype;
		String size;

		try {   

			portalLogin = request.getParameter(ServletParameter.PORTAL_LOGIN);
			scope = request.getParameter(ServletParameter.SCOPE);	
			filenameWithExtension = request.getParameter(ServletParameter.FILENAME);
			serviceName = request.getParameter(ServletParameter.SERVICE_NAME);

			name = request.getParameter(ServletParameter.NAME);
			description = request.getParameter(ServletParameter.DESCRIPTION);
			parentPath = request.getParameter(ServletParameter.PARENT_PATH);
			
			mimetype = request.getParameter(ServletParameter.MIMETYPE);
			size = request.getParameter(ServletParameter.SIZE);

			boolean isMultipart = ServletFileUpload.isMultipartContent(request);
			if (isMultipart){

				// Create a new file upload handler
				ServletFileUpload upload = new ServletFileUpload();

				// Parse the request
				FileItemIterator iter = upload.getItemIterator(request);
				while (iter.hasNext()) {
					FileItemStream item = iter.next();
					String filename = item.getFieldName();
					InputStream inputStream = item.openStream();
					if (!item.isFormField()) {

						logger.info("** Upload File: field " + filename + " with file name "
								+ item.getName() + " detected.");

						//		Tomcat 7!!!!
						//			Part filePart = request.getPart("fileUpload"); // Retrieves <input type="file" name="file">
						//			InputStream inputStream = filePart.getInputStream();

						String remotePath = parentPath + name;
						Node parent = session.getNode(parentPath);

						MetaInfo metadata = null;
						if (inputStream!=null){
							GCUBEStorage storage = new GCUBEStorage(portalLogin);
							metadata = Util.getMetadataInfo(inputStream, storage, remotePath, filenameWithExtension, mimetype, Long.parseLong(size));
							if (metadata.getStorageId()==null)
								throw new Exception("Inpustream not saved in storage.");
						}


						ItemDelegate delegate = new ItemDelegate();
						delegate.setName(name);
						delegate.setTitle(name);
						delegate.setDescription(description);
						delegate.setParentId(parent.getIdentifier());
						delegate.setOwner(portalLogin);
						delegate.setPrimaryType(PrimaryNodeType.NT_WORKSPACE_IMAGE);
						delegate.setLastAction(WorkspaceItemAction.CREATED);

						Map<NodeProperty, String> content = new HashMap<NodeProperty, String>();
						content.put(NodeProperty.CONTENT, ContentType.IMAGE.toString());
						content.put(NodeProperty.FOLDER_ITEM_TYPE, FolderItemType.EXTERNAL_IMAGE.toString());
						content.put(NodeProperty.PORTAL_LOGIN, portalLogin);

						//set metadata
						content.put(NodeProperty.MIME_TYPE, metadata.getMimeType());
						Long l = Long.valueOf(String.valueOf(metadata.getSize()));
						content.put(NodeProperty.SIZE, new XStream().toXML(l));
						content.put(NodeProperty.REMOTE_STORAGE_PATH, remotePath);		
						delegate.setContent(content);

						ItemDelegateWrapper wrapper = new ItemDelegateWrapper(delegate, portalLogin);
						ItemDelegate new_item = wrapper.save(session);

						xmlConfig = xstream.toXML(new_item.getPath());
						//	System.out.println(xmlConfig);
						response.setContentLength(xmlConfig.length());
						out.println(xmlConfig);
					}
				}
			}
		} catch (Exception e) {
			logger.error("Error saving inpustream for file: " + filenameWithExtension, e);
			xmlConfig = xstream.toXML(e.toString());
			response.setContentLength(xmlConfig.length());
			out.println(xmlConfig);
		}

	}

}
