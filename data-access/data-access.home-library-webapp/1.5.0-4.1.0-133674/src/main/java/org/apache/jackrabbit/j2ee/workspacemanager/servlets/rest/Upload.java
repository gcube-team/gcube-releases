package org.apache.jackrabbit.j2ee.workspacemanager.servlets.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.security.Principal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.security.Privilege;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.api.security.JackrabbitAccessControlManager;
import org.apache.jackrabbit.api.security.principal.PrincipalManager;
import org.apache.jackrabbit.j2ee.RepositoryAccessServlet;
import org.apache.jackrabbit.j2ee.accessmanager.AccessControlUtil;
import org.apache.jackrabbit.j2ee.workspacemanager.ItemDelegateWrapper;
import org.gcube.common.homelibary.model.servlet.ServletParameter;
import org.apache.jackrabbit.j2ee.workspacemanager.SessionManager;
import org.apache.jackrabbit.j2ee.workspacemanager.storage.GCUBEStorage;
import org.apache.jackrabbit.j2ee.workspacemanager.util.CustomPrivilege;
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
	
	private static final String PORTAL_LOGIN = "hl:portalLogin";
	private static final String OWNER_NODE = "hl:owner";

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
		
		String filenameWithExtension = request.getParameter(ServletParameter.FILENAME);

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

			boolean canWrite = false;
			try {   
				String login = sessionManager.getLogin(sessionId);
						
				Node node = session.getNode(parentPath);
				String owner = null;
				if (node.hasProperty(PORTAL_LOGIN))
					owner = node.getProperty(PORTAL_LOGIN).getString();
				else if (node.hasNode(OWNER_NODE))
					owner = node.getNode(OWNER_NODE).getProperty(PORTAL_LOGIN).getString();

				if (owner.equals(login))
					canWrite = true;
				else{
					PrincipalManager principalManager = AccessControlUtil.getPrincipalManager(session);

					Principal principal = principalManager.getPrincipal(login);
					logger.info("Check if " + principal.getName() + " can upload file on " + parentPath);

					JackrabbitAccessControlManager  jacm = (JackrabbitAccessControlManager) session.getAccessControlManager();
					Set<Principal> principals = new HashSet<Principal>();
					principals.add(principal);
					
					canWrite = jacm.hasPrivileges(parentPath, principals, new Privilege[] {
							jacm.privilegeFromName(CustomPrivilege.JCR_ADD_CHILD_NODES)
					});

					logger.info("Can " + principal.getName() + " upload file on node " + parentPath + "? " + canWrite);
					
				}
						
				if (canWrite){
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
				}
				else
					xmlConfig = xstream.toXML("No privilege to Upload files");
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
