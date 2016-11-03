package org.apache.jackrabbit.j2ee.workspacemanager.servlets.ftp;

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


public class Upload extends HttpServlet {

	private static Logger logger = LoggerFactory.getLogger(Upload.class);
	private static final long serialVersionUID = 1L;
	private static final String SEPARATOR = "/";

	public Upload() {
		super();
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		long start = System.currentTimeMillis();
		//		response.setContentType("text/plain");
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		String sessionId = request.getParameter(ServletParameter.UUID);

		String scope = request.getParameter(ServletParameter.SCOPE);	
		String filenameWithExtension = request.getParameter(ServletParameter.FILENAME);
		String serviceName = request.getParameter(ServletParameter.SERVICE_NAME);

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
				//				logger.info(sessionId + " already exists, get it");
			}
			else{				 
				session = sessionManager.newSession(request);
				sessionId = session.toString();
				//				logger.info(sessionId + " does not exist, a new session has been created " + session.toString());
			}

			try {   
				String remotePath = parentPath + SEPARATOR + name;
				Node parent = session.getNode(parentPath);
				
				long start00 = System.currentTimeMillis();
				// opens input stream of the request for reading data
				InputStream inputStream = request.getInputStream();
				
				logger.info("**** " + filenameWithExtension + " get Inpustream in "+(System.currentTimeMillis()-start00)+ " millis");

				MetaInfo metadata = null;
				if (inputStream!=null){
					GCUBEStorage storage = new GCUBEStorage(sessionManager.getLogin(request));

					long mysize = 0;
					if(size!=null)
						try {
							mysize = Long.parseLong(size);
						} catch (Exception e) {
							logger.error("size cannot be cast to long  " + e.getMessage());
						}
					
					metadata = Util.getMetadataInfo(inputStream, storage, remotePath, filenameWithExtension, mimetype, mysize);
					if (metadata.getStorageId()==null)
						throw new Exception("Inpustream not saved in storage.");
				}

				long start01 = System.currentTimeMillis();
				ItemDelegate delegate = new ItemDelegate();
				delegate.setName(name);
				delegate.setTitle(name);
				delegate.setDescription(description);
				delegate.setParentId(parent.getIdentifier());
				delegate.setOwner(sessionManager.getLogin(request));
				delegate.setPrimaryType(PrimaryNodeType.NT_WORKSPACE_IMAGE);
				delegate.setLastAction(WorkspaceItemAction.CREATED);

				Map<NodeProperty, String> content = new HashMap<NodeProperty, String>();
				content.put(NodeProperty.CONTENT, ContentType.IMAGE.toString());
				content.put(NodeProperty.FOLDER_ITEM_TYPE, FolderItemType.EXTERNAL_IMAGE.toString());
				content.put(NodeProperty.PORTAL_LOGIN, sessionManager.getLogin(request));

				//set metadata
				content.put(NodeProperty.MIME_TYPE, metadata.getMimeType());
				content.put(NodeProperty.SIZE, new XStream().toXML(Long.valueOf(String.valueOf(metadata.getSize()))));
				content.put(NodeProperty.REMOTE_STORAGE_PATH, remotePath);		
				delegate.setContent(content);

				ItemDelegateWrapper wrapper = new ItemDelegateWrapper(delegate, sessionManager.getLogin(request));
				ItemDelegate new_item = wrapper.save(session);

				
				logger.info("**** " + filenameWithExtension + " create obj in Jackrabbit in "+(System.currentTimeMillis()-start01)+ " millis");
				
				xmlConfig = xstream.toXML(new_item.getPath());
				//	System.out.println(xmlConfig);
				response.setContentLength(xmlConfig.length());
				out.println(xmlConfig);
								
			} catch (Exception e) {
				logger.error("Error saving inpustream for file: " + filenameWithExtension, e);
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
			logger.info("**** " + filenameWithExtension + " save in storage and jackrabbit in "+(System.currentTimeMillis()-start)+ " millis");
		}
	}




}
