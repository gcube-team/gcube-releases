package org.apache.jackrabbit.j2ee.workspacemanager.servlets.ftp;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.j2ee.ConfigRepository;
import org.apache.jackrabbit.j2ee.RepositoryAccessServlet;
import org.apache.jackrabbit.j2ee.workspacemanager.ItemDelegateWrapper;
import org.apache.jackrabbit.j2ee.workspacemanager.ServletParameter;
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
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;


public class Upload extends HttpServlet {

	private static Logger logger = LoggerFactory.getLogger(Upload.class);
	private static final long serialVersionUID = 1L;


	public Upload() {
		super();
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		//		response.setContentType("text/plain");
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		String login = request.getParameter(ServletParameter.LOGIN);
		String sessionId = request.getParameter(ServletParameter.UUID);
		final String user = request.getParameter(ConfigRepository.USER);
		final char[] pass = request.getParameter(ConfigRepository.PASSWORD).toCharArray();

		//		portalLogin = request.getParameter(ServletParameter.PORTAL_LOGIN);
		String scope = request.getParameter(ServletParameter.SCOPE);	
		String filenameWithExtension = request.getParameter(ServletParameter.FILENAME);
		String serviceName = request.getParameter(ServletParameter.SERVICE_NAME);

		String name = request.getParameter(ServletParameter.NAME);
		String description = request.getParameter(ServletParameter.DESCRIPTION);
		String parentPath = request.getParameter(ServletParameter.PARENT_PATH);


		Repository rep = RepositoryAccessServlet.getRepository(getServletContext());

		SessionImpl session = null;
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
				session = sessionManager.newSession(login, user, pass);
				sessionId = session.toString();
				//				logger.info(sessionId + " does not exist, a new session has been created " + session.toString());
			}

			try {   
				String remotePath = parentPath + name;
				Node parent = session.getNode(parentPath);

				// opens input stream of the request for reading data
				InputStream inputStream = request.getInputStream();

				MetaInfo metadata = null;
				if (inputStream!=null){
					GCUBEStorage storage = new GCUBEStorage(login, scope, serviceName);
					metadata = Util.getMetadataInfo(inputStream, storage, remotePath, filenameWithExtension);
					if (metadata.getStorageId()==null)
						throw new Exception("Inpustream not saved in storage.");
				}
				//				long size;
				//				String url = null;
				//
				//				try {
				//					GCUBEStorage storage = new GCUBEStorage(login, scope, serviceName);
				//					long start1 = System.currentTimeMillis();
				//					url = storage.putStream(inputStream,remotePath);
				//					logger.info("**** " + remotePath + " save in storage in "+(System.currentTimeMillis()-start1)+ " millis");
				//					logger.info("saved to " + remotePath + " - GCUBEStorage URL : " + url);						
				//					size =	storage.getRemoteFileSize(remotePath);
				//				} catch (RemoteBackendException e) {
				//					logger.error(remotePath + " remote path not present" + e);
				//					throw new RemoteBackendException(e.getMessage());
				//				}


				ItemDelegate delegate = new ItemDelegate();
				delegate.setName(name);
				delegate.setTitle(name);
				delegate.setDescription(description);
				delegate.setParentId(parent.getIdentifier());
				delegate.setOwner(login);
				delegate.setPrimaryType(PrimaryNodeType.NT_WORKSPACE_IMAGE);
				delegate.setLastAction(WorkspaceItemAction.CREATED);

				Map<NodeProperty, String> content = new HashMap<NodeProperty, String>();
				content.put(NodeProperty.CONTENT, ContentType.IMAGE.toString());
				content.put(NodeProperty.FOLDER_ITEM_TYPE, FolderItemType.EXTERNAL_IMAGE.toString());
				content.put(NodeProperty.PORTAL_LOGIN, login);

				//set metadata
				content.put(NodeProperty.MIME_TYPE, metadata.getMimeType());
				Long size = Long.valueOf(String.valueOf(metadata.getSize()));
				content.put(NodeProperty.SIZE, new XStream().toXML(size));
				content.put(NodeProperty.REMOTE_STORAGE_PATH, remotePath);		
				delegate.setContent(content);

				ItemDelegateWrapper wrapper = new ItemDelegateWrapper(delegate, login);
				ItemDelegate new_item = wrapper.save(session);

				xmlConfig = xstream.toXML(new_item.getPath());
				//	System.out.println(xmlConfig);
				response.setContentLength(xmlConfig.length());
				out.println(xmlConfig);
//				logger.info("**** " + remotePath + " save in storage and jackrabbit in "+(System.currentTimeMillis()-start)+ " millis");
			} catch (Exception e) {
				logger.error("Error saving inpustream for file: " + filenameWithExtension, e);
				xmlConfig = xstream.toXML(e.toString());
				response.setContentLength(xmlConfig.length());
				out.println(xmlConfig);
			}


		} finally {
			if (!exist){
				sessionManager.releaseSession(sessionId);
				//				logger.info("Released session " + sessionId);
			}
			out.close();
			out.flush();
		}
	}




}
