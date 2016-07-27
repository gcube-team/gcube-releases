package org.apache.jackrabbit.j2ee.workspacemanager.servlets.post;

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


public class MoveToTrashIds extends HttpServlet {

	private static Logger logger = LoggerFactory.getLogger(MoveToTrashIds.class);
	private static final long serialVersionUID = 1L;

	public static final String PATH_SEPARATOR 				= "/";
	public static final String HOME_FOLDER 					= "Home";
	public static final String SHARED_FOLDER				= "Share";	
	public static final String HL_NAMESPACE					= "hl:";
	public static final String JCR_NAMESPACE				= "jcr:";
	public static final String REP_NAMESPACE				= "rep:";


	public static final String NT_WORKSPACE_FOLDER 				= "nthl:workspaceItem";
	public static final String NT_WORKSPACE_SHARED_FOLDER		= "nthl:workspaceSharedItem";

	public MoveToTrashIds() {
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

			List<String> itemIds = null;
			String trashId = null;
			try{
				trashId = request.getParameter(ServletParameter.TRASH_ID);
				
				logger.info("Servlet MoveToTrashIds called with parameters: [trashId: "+ trashId + " - by: " + login +"]");
				
				itemIds = (List<String>) xstream.fromXML(request.getInputStream());
				logger.info("Remove "+ itemIds.size() + " items");
				Map<String, String> error = moveToTrashIds(session, itemIds, trashId, login);
				xmlConfig = xstream.toXML(error);
//				response.setContentLength(xmlConfig.length());
				out.println(xmlConfig);
			} catch (Exception e) {
				logger.error("Error removing items", e);
				xmlConfig = xstream.toXML(e.toString());
//				response.setContentLength(xmlConfig.length());
				out.println(xmlConfig);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error repository ex " + e.getMessage());
			xmlConfig = xstream.toXML(e.toString());
//			response.setContentLength(xmlConfig.length());
			out.println(xmlConfig);

		} finally {
			if (!exist){
				sessionManager.releaseSession(sessionId);
				//				logger.info("Released session " + sessionId);
			}
			out.close();
//			out.flush();
		}
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

}
