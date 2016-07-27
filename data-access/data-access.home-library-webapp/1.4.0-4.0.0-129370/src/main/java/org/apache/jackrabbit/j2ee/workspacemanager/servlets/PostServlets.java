package org.apache.jackrabbit.j2ee.workspacemanager.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.ReferentialIntegrityException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.version.VersionException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.core.NodeImpl;
import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.j2ee.ConfigRepository;
import org.apache.jackrabbit.j2ee.RepositoryAccessServlet;
import org.apache.jackrabbit.j2ee.workspacemanager.ItemDelegateWrapper;
import org.apache.jackrabbit.j2ee.workspacemanager.NodeManager;
import org.apache.jackrabbit.j2ee.workspacemanager.ServletParameter;
import org.apache.jackrabbit.j2ee.workspacemanager.SessionManager;
import org.apache.jackrabbit.j2ee.workspacemanager.accounting.AccountingDelegateWrapper;
import org.apache.jackrabbit.j2ee.workspacemanager.storage.GCUBEStorage;
import org.apache.jackrabbit.j2ee.workspacemanager.util.MetaInfo;
import org.apache.jackrabbit.j2ee.workspacemanager.util.Util;
import org.apache.jackrabbit.spi.NodeInfo;
import org.apache.jackrabbit.spi.RepositoryService;
import org.apache.jackrabbit.spi.commons.NodeInfoImpl;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.accounting.AccountingDelegate;
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


public class PostServlets extends HttpServlet {

	private static Logger logger = LoggerFactory.getLogger(PostServlets.class);
	private static final long serialVersionUID = 1L;

	public static final String PATH_SEPARATOR 				= "/";
	public static final String HOME_FOLDER 					= "Home";
	public static final String SHARED_FOLDER				= "Share";	
	public static final String HL_NAMESPACE					= "hl:";
	public static final String JCR_NAMESPACE				= "jcr:";
	public static final String REP_NAMESPACE				= "rep:";


	public static final String NT_WORKSPACE_FOLDER 				= "nthl:workspaceItem";
	public static final String NT_WORKSPACE_SHARED_FOLDER		= "nthl:workspaceSharedItem";

	public PostServlets() {
		super();
	}

	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		//		response.setContentType("text/plain");
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		String login = request.getParameter(ServletParameter.LOGIN);
		String sessionId = request.getParameter(ServletParameter.UUID);
		final String user = request.getParameter(ConfigRepository.USER);
		final char[] pass = request.getParameter(ConfigRepository.PASSWORD).toCharArray();

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


			StringBuffer requestURL = request.getRequestURL();

			String servlet = (requestURL.toString().substring(requestURL.lastIndexOf("/")+1, requestURL.length()));
			//			logger.info("Servlet DispatcherServlet called with parameters: [servlet: "+ servlet + "]");

			switch (servlet) {

			case "Upload":
				upload(request, response, out, xstream, session);
				break;

			case "UploadFile":
				uploadFile(request, response, out, xstream, session);
				break;

			case "CreateFolder":
				createFolder(request, response, out, xstream, session);
				break;

			case "Delete":
				delete(request, response, out, xstream, session);
				break;

			case "CopyContent":
				String srcAbsPath111 = null;
				String destAbsPath111 = null;
				try{
					srcAbsPath111 = new String(request.getParameter(ServletParameter.SRC_ABS_PATH).getBytes("iso-8859-1"), "UTF-8");
					destAbsPath111 = new String(request.getParameter(ServletParameter.DEST_ABS_PATH).getBytes("iso-8859-1"), "UTF-8");
					logger.info("Servlet CopyContent called with parameters: [srcAbsPath11: "+ srcAbsPath111 + " - destAbsPath11: " + destAbsPath111 +"]");
					ItemDelegate new_item = copyContetItem(session, srcAbsPath111, destAbsPath111, login);
					xmlConfig = xstream.toXML(new_item);
					response.setContentLength(xmlConfig.length());
					out.println(xmlConfig);
				} catch (Exception e) {
					logger.error("Error moving item from path: " + srcAbsPath111 +  " - to: " + destAbsPath111, e);
					xmlConfig = xstream.toXML(e.toString());
					response.setContentLength(xmlConfig.length());
					out.println(xmlConfig);
				}
				break;

			case "RemoveItem":
				String absPath = null;
				try{
					absPath = request.getParameter(ServletParameter.ABS_PATH);
					logger.info("Servlet RemoveItem called with parameters: [absPath: "+ absPath +"]");	
					remove(session, absPath);
					xmlConfig = xstream.toXML(true);
					response.setContentLength(xmlConfig.length());
					out.println(xmlConfig);
				} catch (Exception e) {
					logger.error("Error removing item by path: " + absPath, e);
					xmlConfig = xstream.toXML(false);
					response.setContentLength(xmlConfig.length());
					out.println(xmlConfig);
				}
				break;

			case "SaveItem":
				ItemDelegate item = null;
				try{

					//					System.out.println(request.getInputStream().available());
					item = (ItemDelegate) xstream.fromXML(request.getInputStream());
					//					System.out.println(item.toString());
					logger.info("Servlet saveItem called on item: "+ item.getName());
					ItemDelegateWrapper wrapper = new ItemDelegateWrapper(item, "");
					ItemDelegate new_item = wrapper.save(session);

					xmlConfig = xstream.toXML(new_item);
					response.setContentLength(xmlConfig.length());
					out.println(xmlConfig);
				} catch (LockException e) {
					logger.error("Error saving item with id: " + item.getId(), e);
					xmlConfig = xstream.toXML(e.toString());
					response.setContentLength(xmlConfig.length());
					out.println(xmlConfig);
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("Error saving item with id: " + item.getId(), e);
					xmlConfig = xstream.toXML(e.toString());
					response.setContentLength(xmlConfig.length());
					out.println(xmlConfig);
				}
				break;

			case "AddNode":
				String parentId = null;
				String id = null;
				try{
					parentId = request.getParameter(ServletParameter.PARENT_ID);
					id = request.getParameter(ServletParameter.ID);
					logger.info("Servlet AddNode called with id: "+ id + " to parentId " + parentId);
					ItemDelegate new_item = addNodeToParent(session, parentId, id);

					xmlConfig = xstream.toXML(new_item);
					response.setContentLength(xmlConfig.length());
					out.println(xmlConfig);
				} catch (Exception e) {
					logger.error("Error adding item with id: " + id + " to parentId " + parentId, e);
					xmlConfig = xstream.toXML(e.toString());
					response.setContentLength(xmlConfig.length());
					out.println(xmlConfig);
				}
				break;

			case "CreateReference":
				String srcID = null;
				String destID = null;
				String name = null;
				try{
					srcID = request.getParameter(ServletParameter.SRC_ID);
					destID = request.getParameter(ServletParameter.DEST_ID);
					name = request.getParameter(ServletParameter.NAME);
					logger.info("Servlet CreateReference called with parameters: [srcID: "+ srcID + " - destID: " + destID +"]");
					ItemDelegate new_item = createReference(session, srcID, destID, name, login);
					xmlConfig = xstream.toXML(new_item);
					response.setContentLength(xmlConfig.length());
					out.println(xmlConfig);
				} catch (Exception e) {
					logger.error("Error creating reference of node id: " + srcID +  " - to destination id: " + destID, e);
					xmlConfig = xstream.toXML(e.toString());
					response.setContentLength(xmlConfig.length());
					out.println(xmlConfig);
				}
				break;

			case "MoveToTrashIds":
				List<String> itemIds = null;
				String trashId = null;
				try{
					trashId = request.getParameter(ServletParameter.TRASH_ID);
					itemIds = (List<String>) xstream.fromXML(request.getInputStream());
					logger.info("Remove "+ itemIds.size() + " items");
					Map<String, String> error = moveToTrashIds(session, itemIds, trashId, login);
					xmlConfig = xstream.toXML(error);
					response.setContentLength(xmlConfig.length());
					out.println(xmlConfig);
				} catch (Exception e) {
					logger.error("Error removing items", e);
					xmlConfig = xstream.toXML(e.toString());
					response.setContentLength(xmlConfig.length());
					out.println(xmlConfig);
				}
				break;

			case "SaveAccountingItem":
				try{
					AccountingDelegate item1 = (AccountingDelegate) xstream.fromXML(request.getInputStream());
					logger.info("Servlet SaveAccountingItem called for entry " + item1.getEntryType().toString());

					AccountingDelegateWrapper wrapper = new AccountingDelegateWrapper(item1, "");
					AccountingDelegate new_item = wrapper.save(session);

					xmlConfig = xstream.toXML(new_item);
					response.setContentLength(xmlConfig.length());
					out.println(xmlConfig);

				} catch (Exception e) {
					logger.error("Error repository ex " + e);
					xmlConfig = xstream.toXML(e.toString());
					response.setContentLength(xmlConfig.length());
					out.println(xmlConfig);

				}

				break;

			default:
				break;
			}


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



	private void delete(HttpServletRequest request,
			HttpServletResponse response, PrintWriter out, XStream xstream,
			SessionImpl session) {
		String xmlConfig;
		String portalLogin;

		String scope;
		String serviceName;
		String path = null;
		Boolean flag = false;
		try {   
			scope = request.getParameter(ServletParameter.SCOPE);	
			serviceName = request.getParameter(ServletParameter.SERVICE_NAME);
			portalLogin = request.getParameter(ServletParameter.PORTAL_LOGIN);
			path = request.getParameter(ServletParameter.ABS_PATH);

			Node node = session.getNode(path);

			//	System.out.println(node.getPrimaryNodeType());
			Boolean isFolder = (node.getPrimaryNodeType().getName().equals(NT_WORKSPACE_FOLDER) || node.getPrimaryNodeType().getName().equals(NT_WORKSPACE_SHARED_FOLDER));


			session.removeItem(path);
			session.save();

			GCUBEStorage storage = new GCUBEStorage(portalLogin, scope, serviceName);
			if (isFolder)
				storage.removeRemoteFolder(path);
			else
				storage.removeRemoteFile(path);


			flag = true;

			xmlConfig = xstream.toXML(flag);
			response.setContentLength(xmlConfig.length());
			out.println(xmlConfig);

		} catch (Exception e) {
			logger.error("Error deleting item: " + path, e);
			xmlConfig = xstream.toXML(e.toString());
			response.setContentLength(xmlConfig.length());
			out.println(xmlConfig);
		}

	}

	private void createFolder(HttpServletRequest request,
			HttpServletResponse response, PrintWriter out, XStream xstream,
			SessionImpl session) {

		String xmlConfig;
		String portalLogin;
		String filenameWithExtension = null;

		String name;
		String description;
		String parentPath;

		try {   

			portalLogin = request.getParameter(ServletParameter.PORTAL_LOGIN);

			name = request.getParameter(ServletParameter.NAME);
			description = request.getParameter(ServletParameter.DESCRIPTION);
			parentPath = request.getParameter(ServletParameter.PARENT_PATH);


			Node parent = session.getNode(parentPath);

			ItemDelegate delegate = new ItemDelegate();
			delegate.setName(name);
			delegate.setTitle(name);
			delegate.setDescription(description);
			delegate.setParentId(parent.getIdentifier());
			delegate.setOwner(portalLogin);
			delegate.setPrimaryType(PrimaryNodeType.NT_WORKSPACE_FOLDER);
			delegate.setLastAction(WorkspaceItemAction.CREATED);

			ItemDelegateWrapper wrapper = new ItemDelegateWrapper(delegate, portalLogin);
			ItemDelegate new_item = wrapper.save(session);

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

	}


	/**
	 * Upload a file
	 * @param request
	 * @param response
	 * @param out
	 * @param xstream
	 * @param session
	 */
	private void uploadFile(HttpServletRequest request, HttpServletResponse response, PrintWriter out, XStream xstream, SessionImpl session) {

		String xmlConfig;
		String portalLogin;
		String scope;

		String filenameWithExtension = null;
		String serviceName;

		String name;
		String description;
		String parentPath;

		try {   

			portalLogin = request.getParameter(ServletParameter.PORTAL_LOGIN);
			scope = request.getParameter(ServletParameter.SCOPE);	
			filenameWithExtension = request.getParameter(ServletParameter.FILENAME);
			serviceName = request.getParameter(ServletParameter.SERVICE_NAME);

			name = request.getParameter(ServletParameter.NAME);
			description = request.getParameter(ServletParameter.DESCRIPTION);
			parentPath = request.getParameter(ServletParameter.PARENT_PATH);

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
							GCUBEStorage storage = new GCUBEStorage(portalLogin, scope, serviceName);
							metadata = Util.getMetadataInfo(inputStream, storage, remotePath, filenameWithExtension);
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

	/**
	 * Upload file as stream
	 * @param request
	 * @param response
	 * @param out
	 * @param xstream
	 * @param session
	 */
	private void upload(HttpServletRequest request, HttpServletResponse response, PrintWriter out, XStream xstream, SessionImpl session) {
		long start = System.currentTimeMillis();
		String xmlConfig;
		String portalLogin;
		String scope;

		String filenameWithExtension = null;
		String serviceName;

		String name;
		String description;
		String parentPath;

		try {   

			portalLogin = request.getParameter(ServletParameter.PORTAL_LOGIN);
			scope = request.getParameter(ServletParameter.SCOPE);	
			filenameWithExtension = request.getParameter(ServletParameter.FILENAME);
			serviceName = request.getParameter(ServletParameter.SERVICE_NAME);

			name = request.getParameter(ServletParameter.NAME);
			description = request.getParameter(ServletParameter.DESCRIPTION);
			parentPath = request.getParameter(ServletParameter.PARENT_PATH);


			String remotePath = parentPath + name;

			Node parent = session.getNode(parentPath);

			// opens input stream of the request for reading data
			InputStream inputStream = request.getInputStream();

			//			MetaInfo metadata = null;
			//			if (inputStream!=null){
			//				GCUBEStorage storage = new GCUBEStorage(portalLogin, scope, serviceName);
			//				metadata = Util.getMetadataInfo(inputStream, storage, remotePath, filenameWithExtension);
			//				if (metadata.getStorageId()==null)
			//					throw new Exception("Inpustream not saved in storage.");
			//			}
			long size;
			String url = null;


			try {
				GCUBEStorage storage = new GCUBEStorage(portalLogin, scope, serviceName);
				long start1 = System.currentTimeMillis();
				url = storage.putStream(inputStream,remotePath);
				logger.info(remotePath + " save in storage in "+(System.currentTimeMillis()-start1)+ " millis");
				logger.info("saved to " + remotePath + " - GCUBEStorage URL : " + url);						
				size =	storage.getRemoteFileSize(remotePath);
			} catch (RemoteBackendException e) {
				logger.error(remotePath + " remote path not present" + e);
				throw new RemoteBackendException(e.getMessage());
			}


		ItemDelegate delegate = new ItemDelegate();
		//				delegate.setId(null);
		delegate.setName(name);
		delegate.setTitle(name);
		delegate.setDescription(description);
		//				delegate.setLastModifiedBy(null);
		delegate.setParentId(parent.getIdentifier());
		//				delegate.setParentPath(null);
		//				delegate.setLastModificationTime(null);
		//				delegate.setCreationTime(null);
		//				delegate.setProperties(null);
		delegate.setOwner(portalLogin);
		delegate.setPrimaryType(PrimaryNodeType.NT_WORKSPACE_IMAGE);
		delegate.setLastAction(WorkspaceItemAction.CREATED);

		Map<NodeProperty, String> content = new HashMap<NodeProperty, String>();
		content.put(NodeProperty.CONTENT, ContentType.IMAGE.toString());
		content.put(NodeProperty.FOLDER_ITEM_TYPE, FolderItemType.EXTERNAL_IMAGE.toString());
		content.put(NodeProperty.PORTAL_LOGIN, portalLogin);

		//set metadata
		content.put(NodeProperty.MIME_TYPE, "image/jpeg");
		//			Long l = Long.valueOf(String.valueOf(metadata.getSize()));
		content.put(NodeProperty.SIZE, new XStream().toXML(size));
		content.put(NodeProperty.REMOTE_STORAGE_PATH, remotePath);		
		delegate.setContent(content);

		ItemDelegateWrapper wrapper = new ItemDelegateWrapper(delegate, portalLogin);
		ItemDelegate new_item = wrapper.save(session);

		xmlConfig = xstream.toXML(new_item.getPath());
		//	System.out.println(xmlConfig);
		response.setContentLength(xmlConfig.length());
		out.println(xmlConfig);
		logger.info(remotePath + " save in storage and jackrabbit in "+(System.currentTimeMillis()-start)+ " millis");
	} catch (Exception e) {
		logger.error("Error saving inpustream for file: " + filenameWithExtension, e);
		xmlConfig = xstream.toXML(e.toString());
		response.setContentLength(xmlConfig.length());
		out.println(xmlConfig);
	}

}


/**
 * Add node id to parent
 * @param session
 * @param parentId
 * @param id
 * @return
 */
private ItemDelegate addNodeToParent(SessionImpl session, String parentId,
		String id) {
	ItemDelegate item = null;
	try{
		Node parent = session.getNodeByIdentifier(parentId);
		Node node = parent.addNode(id);
		NodeManager wrap = new NodeManager(node, "");

		item = wrap.getItemDelegate();

	}catch (Exception e) {
		logger.error("impossible to add item " + id + " to parentId " + parentId, e.getMessage());
	}
	return item;
}

private Map<String,String> moveToTrashIds(SessionImpl session, List<String> itemIds, String trashId, String login) throws ItemNotFoundException, RepositoryException {
	Node trash = session.getNodeByIdentifier(trashId);
	Map<String,String> errors = new HashMap<String, String>();
	for(String id: itemIds){
		Node node = null;
		try{
			node = session.getNodeByIdentifier(id);

			Node trashItem = trash.addNode(id, PrimaryNodeType.NT_TRASH_ITEM);
			trashItem.setProperty(NodeProperty.LAST_ACTION.toString(), WorkspaceItemAction.CREATED.toString());
			trashItem.setProperty(NodeProperty.PORTAL_LOGIN.toString(), login);
			trashItem.setProperty(NodeProperty.TITLE.toString(), id);
			trashItem.setProperty(NodeProperty.DESCRIPTION.toString(), "trash item of node " + node.getPath());

			trashItem.setProperty(NodeProperty.TRASH_ITEM_NAME.toString(), node.getName());
			trashItem.setProperty(NodeProperty.DELETE_DATE.toString(), Calendar.getInstance());
			trashItem.setProperty(NodeProperty.DELETE_BY.toString(), login);
			trashItem.setProperty(NodeProperty.DELETED_FROM.toString(), node.getParent().getPath());		
			trashItem.setProperty(NodeProperty.ORIGINAL_PARENT_ID.toString(), node.getParent().getIdentifier());

			try{
				Node contentNode = node.getNode(NodeProperty.CONTENT.toString());	
				String mimeType = contentNode.getProperty(NodeProperty.MIME_TYPE.toString()).getString();
				//				long size = contentNode.getProperty(NodeProperty.SIZE.toString()).getLong();
				trashItem.setProperty(NodeProperty.TRASH_ITEM_MIME_TYPE.toString(), mimeType);	
				//				node.setProperty(NodeProperty.LENGTH.toString(), size);
				trashItem.setProperty(NodeProperty.IS_FOLDER.toString(), false);
			}catch (Exception e) {
				logger.error("mimetype and lenght not in node " + node.getPath() + e);
				trashItem.setProperty(NodeProperty.IS_FOLDER.toString(), true);
			}

			session.save();
			logger.info("Move item: " + node.getPath() + " to " + trashItem.getPath()+ "/"+ node.getName());
			session.move(node.getPath(), trashItem.getPath()+ "/"+ node.getName());
			session.save();

			Node myTrash = session.getNode(trashItem.getPath()+ "/"+ node.getName());
			logger.info("Update remote path of " + myTrash.getPath());

			updateRemotePath(myTrash);


		}catch (Exception e) {
			errors.put(id, e.toString());
			logger.error("impossible to move item " + node.getPath() + " to trash. " + e);
		}
	}
	return errors;


}

/**
 * Update remote path of trashed items
 * @param item
 * @throws InternalErrorException
 * @throws AccessDeniedException
 * @throws ValueFormatException
 * @throws VersionException
 * @throws LockException
 * @throws ConstraintViolationException
 * @throws PathNotFoundException
 * @throws ItemExistsException
 * @throws ReferentialIntegrityException
 * @throws InvalidItemStateException
 * @throws NoSuchNodeTypeException
 * @throws RepositoryException
 */
private void updateRemotePath(Node item) throws AccessDeniedException, ValueFormatException, VersionException, LockException, ConstraintViolationException, PathNotFoundException, ItemExistsException, ReferentialIntegrityException, InvalidItemStateException, NoSuchNodeTypeException, RepositoryException {
	if (item.hasNode(NodeProperty.CONTENT.toString())){
		Node contentNode = item.getNode(NodeProperty.CONTENT.toString());	
		if (contentNode.hasProperty(NodeProperty.REMOTE_STORAGE_PATH.toString())){
			contentNode.setProperty(NodeProperty.REMOTE_STORAGE_PATH.toString(), item.getPath());
			//				System.out.println("Update path from " + contentNode.getProperty(NodeProperty.REMOTE_STORAGE_PATH.toString()).getString() + " to  " + item.getPath());
			item.getSession().save();
		}
	}else{
		NodeIterator iterator = item.getNodes();
		while(iterator.hasNext()){
			Node child = iterator.nextNode();
			updateRemotePath(child);
		}
	}
}


/**
 * Get children by id
 * @param session
 * @param identifier
 * @param login
 * @return
 * @throws Exception
 */
private List<ItemDelegate> getChildren(SessionImpl session, String identifier, String login) throws Exception {

	Node folderNode = session.getNodeByIdentifier(identifier);
	NodeIterator iterator = folderNode.getNodes();
	List<ItemDelegate> children = new ArrayList<ItemDelegate>();
	while(iterator.hasNext()) {
		//
		Node node = iterator.nextNode();
		String path = null;
		try {
			path = node.getPath();
			//				String title = node.getName();
			String name = path.substring(path.lastIndexOf('/') + 1);
			if ((name.equals("Trash") || (name.equals("MySpecialFolders") ||(name.startsWith("rep:")) || (name.startsWith("hl:")) || (name.startsWith(".")))))
				continue;
		} catch (RepositoryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//			String name = path.substring(path.lastIndexOf('/') + 1);

		ItemDelegate item = null;
		NodeManager wrap = new NodeManager(node, login);
		try {
			item = wrap.getItemDelegate();
			//				wrap.setProperties(item);
			children.add(item);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
	return children;
}


/**
 * Get delegate item by id
 * @param session
 * @param identifier
 * @param login
 * @return
 * @throws Exception
 */
private ItemDelegate getDelegateItemById(SessionImpl session,
		String identifier, String login) throws Exception {

	Node node = session.getNodeByIdentifier(identifier);

	//		logger.info(identifier + " -> "+ node.getPath());
	NodeManager wrap = new NodeManager(node, login);
	ItemDelegate item = null;
	try {
		item = wrap.getItemDelegate();
	} catch (Exception e) {
		e.printStackTrace();
	}
	return item;
}


/**
 * Get Item by path
 * @param session
 * @param absPath
 * @param login
 * @return
 * @throws Exception
 */
private ItemDelegate getDelegateItemByPath(SessionImpl session, String absPath, String login) throws Exception {

	Node node = session.getNode(absPath);
	NodeManager wrap = new NodeManager(node, login);

	return wrap.getItemDelegate();

}


/**
 * Execute a query
 * @param session
 * @param query
 * @param login2 
 * @return
 * @throws Exception
 */
private List<ItemDelegate> execute(SessionImpl session, String query, String lang, String login) throws Exception {

	List<ItemDelegate> list = null;

	QueryManager queryManager = session.getWorkspace().getQueryManager();	
	try{
		javax.jcr.query.Query q = null;

		if(lang.equals("JCR-SQL2"))
			q = queryManager.createQuery(query, javax.jcr.query.Query.JCR_SQL2);
		else if (lang.equals("xpath"))
			q = queryManager.createQuery(query, javax.jcr.query.Query.XPATH);
		else if (lang.equals("sql"))
			q = queryManager.createQuery(query, javax.jcr.query.Query.SQL);
		else if (lang.equals("JCR_JQOM"))
			q = queryManager.createQuery(query, javax.jcr.query.Query.JCR_JQOM);
		else
			logger.error("lang unknown");

		QueryResult result = q.execute();

		NodeIterator iterator = result.getNodes();

		list = new LinkedList<ItemDelegate>();
		while (iterator != null && iterator.hasNext()) {

			Node node = iterator.nextNode();

			String itemName = isValidSearchResult(node, login);
			if (itemName == null) {
				logger.trace("Search result is not valid :" + node.getPath());
				continue;
			}

			NodeManager wrap = new NodeManager(node, login);
			try {
				ItemDelegate item = wrap.getItemDelegate();
				list.add(item);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}catch (Exception e) {
		e.printStackTrace();
	}
	return list;

}



public String isValidSearchResult(Node node, String login) {

	String sharePath = PATH_SEPARATOR + SHARED_FOLDER;
	String userPath = PATH_SEPARATOR + HOME_FOLDER + PATH_SEPARATOR + login;

	try {
		String nodePath = node.getPath();
		if (nodePath.startsWith(userPath)){
			//				System.out.println("*** userPath");
			return node.getProperty(NodeProperty.TITLE.toString()).getString();
		}

		if (nodePath.startsWith(sharePath)) {
			//				System.out.println("*** sharePath");
			Node sharedNode = (Node) node.getAncestor(2);

			if (node.getPath().equals(sharedNode.getPath())) {
				Node usersNode = sharedNode.getNode(NodeProperty.USERS.toString());
				String prop = (usersNode.getProperty(login)).getValue().getString();
				String[] value = prop.split(PATH_SEPARATOR);
				//					System.out.println("prop " + value[1]);
				return value[1];
			}				
			else 
				return node.getName();

		}	
		return null;
	} catch (RepositoryException e) {
		return null;
	}
}

/**
 * Clone item
 * @param session
 * @param srcAbsPath
 * @param destAbsPath
 * @param removeExisting
 * @param login
 * @return
 */
private ItemDelegate cloneItem(SessionImpl session, String srcAbsPath, String destAbsPath, boolean removeExisting, String login){
	Node node;
	ItemDelegate item = null;
	try{
		session.getWorkspace().clone(session.getWorkspace().getName(), srcAbsPath, destAbsPath, removeExisting);
		node = session.getNode(destAbsPath);
		NodeManager wrap = new NodeManager(node, login);
		item = wrap.getItemDelegate();

	} catch (Exception e) {
		e.printStackTrace();
	}
	return item;
}

/**
 * Copy item 
 * @param session
 * @param srcAbsPath
 * @param destAbsPath
 * @param login
 * @return
 */
private ItemDelegate copyItem(SessionImpl session, String srcAbsPath, String destAbsPath, String login){
	Node node;
	ItemDelegate item = null;
	try{
		session.getWorkspace().copy(srcAbsPath, destAbsPath);
		node = session.getNode(destAbsPath);
		NodeManager wrap = new NodeManager(node, login);
		item = wrap.getItemDelegate();

	} catch (Exception e) {
		e.printStackTrace();
	}
	return item;
}

/**
 * Create Reference
 * @param session
 * @param srcID
 * @param destID
 * @param login
 * @return
 */
private ItemDelegate createReference(SessionImpl session, String srcID, String destID, String name, String login) {
	ItemDelegate item = null;
	Node srcNode = null;
	Node destNode = null;
	try{
		srcNode = session.getNodeByIdentifier(srcID);
		srcNode.addMixin(JcrConstants.MIX_REFERENCEABLE);

		destNode = session.getNodeByIdentifier(destID);

		Node link = destNode.addNode(name, PrimaryNodeType.NT_WORKSPACE_REFERENCE);
		link.setProperty(NodeProperty.REFERENCE.toString(), srcNode);
		link.setProperty(NodeProperty.PORTAL_LOGIN.toString(), login);
		link.setProperty(NodeProperty.TITLE.toString(), srcNode.getName());
		link.setProperty(NodeProperty.LAST_ACTION.toString(), WorkspaceItemAction.CREATED.toString());
		session.save();	

		logger.info("References to " + srcNode.getPath() + ":");
		for (Property reference : JcrUtils.getReferences(srcNode)) {
			logger.info("- " + reference.getPath().replace(NodeProperty.REFERENCE.toString(), ""));
		}

		NodeManager wrap = new NodeManager(link, login);
		item = wrap.getItemDelegate();

	} catch (Exception e) {
		e.printStackTrace();
	}
	return item;
}


/**
 * Moves the node at srcAbsPath (and its entire subtree) to the new location at destAbsPath. 
 * @param session is coming form the servlet MOVE
 * @param srcAbsPath is an absolute path to the original location 
 * @param destAbsPath is an absolute path to the parent node of the new location, appended with the new name desired for the moved node
 * @return the item moved
 * @throws RepositoryException
 * @throws InternalErrorException 
 */
private ItemDelegate moveItem(SessionImpl session, String srcAbsPath, String destAbsPath, String login) throws RepositoryException {
	session.move(srcAbsPath, destAbsPath);
	session.save();

	NodeManager myNode = null;
	try {
		myNode = new NodeManager(session.getNode(destAbsPath), login);
	} catch (Exception e) {
		e.printStackTrace();
	}
	return myNode.getItemDelegate();
}


/**
 * Copy content item
 * @param session
 * @param srcId
 * @param destId
 * @param login
 * @return
 * @throws Exception
 */
private ItemDelegate copyContetItem(SessionImpl session, String srcId, String destId, String login) throws Exception{

	Node nodeFolder = session.getNodeByIdentifier(destId);
	Node sharedNode = session.getNodeByIdentifier(srcId);
	ItemDelegate item = null;
	for (NodeIterator iterator = sharedNode.getNodes(); iterator.hasNext();) {
		Node child = (Node) iterator.next();

		if (!child.getName().startsWith(HL_NAMESPACE) 
				&& !child.getName().startsWith(JCR_NAMESPACE)
				&& !child.getName().startsWith(REP_NAMESPACE)) {			
			session.getWorkspace().copy(child.getPath(), nodeFolder.getPath() 
					+ "/" + child.getName());
		}
	}

	NodeManager wrap = new NodeManager(nodeFolder, login);
	item = wrap.getItemDelegate();

	return item;
}


/**
 * Remove a node
 * @param session
 */
private void remove(SessionImpl session, String absPath) {
	try{
		session.removeItem(absPath);
		session.save();
	}catch (Exception e) {
		logger.error("impossible to remove item delegate: " + e);
	}
}
}
