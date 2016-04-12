package org.apache.jackrabbit.j2ee.workspacemanager.get;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.lock.Lock;
import javax.jcr.lock.LockManager;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.j2ee.ConfigRepository;
import org.apache.jackrabbit.j2ee.RepositoryAccessServlet;
import org.apache.jackrabbit.j2ee.workspacemanager.ItemDelegateWrapper;
import org.apache.jackrabbit.j2ee.workspacemanager.NodeManager;
import org.apache.jackrabbit.j2ee.workspacemanager.ServletParameter;
import org.apache.jackrabbit.j2ee.workspacemanager.SessionManager;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.SearchItemDelegate;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibary.model.items.type.PrimaryNodeType;
import org.gcube.common.homelibrary.model.exceptions.InternalErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class GetServlets extends HttpServlet {

	private static Logger logger = LoggerFactory.getLogger(GetServlets.class);
	private static final long serialVersionUID = 1L;

	public static final String PATH_SEPARATOR 				= "/";
	public static final String HOME_FOLDER 					= "Home";
	public static final String SHARED_FOLDER				= "Share";	
	public static final String HL_NAMESPACE					= "hl:";
	public static final String JCR_NAMESPACE				= "jcr:";
	public static final String REP_NAMESPACE				= "rep:";

	public GetServlets() {
		super();
	}

	protected void doGet(HttpServletRequest request,
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
			} else {
				session = sessionManager.newSession(login, user, pass);
				sessionId = session.toString();
			}

			StringBuffer requestURL = request.getRequestURL();
			String servlet = (requestURL.toString().substring(requestURL.lastIndexOf("/")+1, requestURL.length()));

			switch (servlet) {
			case "ExecuteQuery":
				String query = null;
				String lang = null;
				String limit = null;
				try{
					query = new String(request.getParameter(ServletParameter.QUERY).getBytes("iso-8859-1"), "UTF-8");	
					lang = request.getParameter(ServletParameter.LANG);
					limit = request.getParameter(ServletParameter.LIMIT);

					logger.info("Servlet ExecuteQuery called with parameters: [query: "+ query + " - lang: " + lang + " - login: " + login + " - limit: " + limit +"]");
					List<SearchItemDelegate> children = execute(session, query, lang, login, Integer.parseInt(limit));
					xmlConfig = xstream.toXML(children);
					response.setContentLength(xmlConfig.length());
					out.println(xmlConfig);
				} catch (Exception e) {
					logger.error("Error executing query: " + query + "- lang: " + lang + " - limit: " + limit, e);
					xmlConfig = xstream.toXML(e.toString());
					response.setContentLength(xmlConfig.length());
					out.println(xmlConfig);
				}
				break;
			case "GetChildrenById":
				String itemId1 = null;
				Boolean showHidden = false;
				try{
					itemId1 = request.getParameter(ServletParameter.ID);	
					showHidden = Boolean.parseBoolean(request.getParameter(ServletParameter.SHOW_HIDDEN));
					//					logger.info("Servlet GetChildrenById called with parameters: [id: "+ itemId1 + "]");
					List<ItemDelegate> children = getChildren(session, itemId1, login, showHidden);
					xmlConfig = xstream.toXML(children);
					response.setContentLength(xmlConfig.length());
					out.println(xmlConfig);
				} catch (Exception e) {
					logger.error("Error getting children by id: " + itemId1, e);
					xmlConfig = xstream.toXML(e.toString());
					response.setContentLength(xmlConfig.length());
					out.println(xmlConfig);
				}
				break;

			case "GetParentsById":
				String myId = null;
				try{
					myId = request.getParameter(ServletParameter.ID);	
					//					logger.info("Servlet GetChildrenById called with parameters: [id: "+ itemId1 + "]");
					List<ItemDelegate> children = getParentsById(session, myId, login);
					xmlConfig = xstream.toXML(children);
					response.setContentLength(xmlConfig.length());
					out.println(xmlConfig);
				} catch (Exception e) {
					logger.error("Error getting parents items by id: " + myId, e);
					xmlConfig = xstream.toXML(e.toString());
					response.setContentLength(xmlConfig.length());
					out.println(xmlConfig);
				}
				break;

			case "GetHiddenItemsById":
				String myItem = null;
				try{
					myItem = request.getParameter(ServletParameter.ID);	
					//					logger.info("Servlet GetChildrenById called with parameters: [id: "+ itemId1 + "]");
					List<ItemDelegate> children = getHiddenItems(session, myItem, login);
					xmlConfig = xstream.toXML(children);
					response.setContentLength(xmlConfig.length());
					out.println(xmlConfig);
				} catch (Exception e) {
					logger.error("Error getting hidden items by id: " + myItem, e);
					xmlConfig = xstream.toXML(e.toString());
					response.setContentLength(xmlConfig.length());
					out.println(xmlConfig);
				}
				break;

			case "GetItemById":
				String itemId11 = null;
				try{
					itemId11 = request.getParameter(ServletParameter.ID);	
					//					logger.info("Servlet GetItemById called with parameters: [id: "+ itemId11 + "]");
					ItemDelegate item = getDelegateItemById(session, itemId11, login);
					xmlConfig = xstream.toXML(item);
					response.setContentLength(xmlConfig.length());
					out.println(xmlConfig);
				} catch (Exception e) {
					logger.error("Error getting item by id: " + itemId11, e);
					xmlConfig = xstream.toXML(e.toString());
					response.setContentLength(xmlConfig.length());
					out.println(xmlConfig);
				}
				break;

			case "GetItemByPath":
				String path = null;
				try{
					path = new String(request.getParameter(ServletParameter.PATH).getBytes("iso-8859-1"), "UTF-8");
					logger.info("Servlet GetItemByPath called with parameters: [path: "+ path + "]");
					ItemDelegate item = getDelegateItemByPath(session, path, login);
					xmlConfig = xstream.toXML(item);
					response.setContentLength(xmlConfig.length());
					out.println(xmlConfig);
				} catch (PathNotFoundException e) {
					logger.error("PathNotFoundException: " + path);
					xmlConfig = xstream.toXML(path + " Path Not Found");
					response.setContentLength(xmlConfig.length());
					out.println(xmlConfig);
				}
				break;

			case "SearchItems":
				List<ItemDelegate> children = null;
				String query1 = null;
				String lang1 = null;
				try{
					query1 = request.getParameter(ServletParameter.QUERY);	
					lang1 = request.getParameter(ServletParameter.LANG);
					logger.info("Servlet SearchItems called with parameters: [query: "+ query1 + " - lang: " + lang1 + "]");
					children = execute(session, query1, lang1, login);
					xmlConfig = xstream.toXML(children);
					response.setContentLength(xmlConfig.length());
					out.println(xmlConfig);
				} catch (Exception e) {
					logger.error("Error searching items by query: " + query1 +  " - lang: " + lang1, e);
					xmlConfig = xstream.toXML(e.toString());
					response.setContentLength(xmlConfig.length());
					out.println(xmlConfig);
				}
				break;

			case "Clone":
				String srcAbsPath = null;
				String destAbsPath = null;
				String removeExisting = null;
				try{
					srcAbsPath = new String(request.getParameter(ServletParameter.SRC_ABS_PATH).getBytes("iso-8859-1"), "UTF-8");	
					destAbsPath = new String(request.getParameter(ServletParameter.DEST_ABS_PATH).getBytes("iso-8859-1"), "UTF-8");
					removeExisting = request.getParameter(ServletParameter.REMOVE_EXISTING);	
					logger.info("Servlet Clone called with parameters: [srcAbsPath: "+ srcAbsPath + " - destAbsPath: " + destAbsPath + " - removeExisting: " + removeExisting +"]");
					ItemDelegate new_item = cloneItem(session, srcAbsPath, destAbsPath, Boolean.valueOf(removeExisting), login);
					xmlConfig = xstream.toXML(new_item);
					response.setContentLength(xmlConfig.length());
					out.println(xmlConfig);
				} catch (Exception e) {
					logger.error("Error cloning item from path: " + srcAbsPath +  " - to: " + destAbsPath + " - removeExisting? "+ removeExisting, e);
					xmlConfig = xstream.toXML(e.toString());
					response.setContentLength(xmlConfig.length());
					out.println(xmlConfig);
				}
				break;

			case "Copy":
				String srcAbsPath1 = null;
				String destAbsPath1 = null;
				try{
					srcAbsPath1 = new String(request.getParameter(ServletParameter.SRC_ABS_PATH).getBytes("iso-8859-1"), "UTF-8");
					destAbsPath1 = new String(request.getParameter(ServletParameter.DEST_ABS_PATH).getBytes("iso-8859-1"), "UTF-8");
					logger.info("Servlet Copy called with parameters: [srcAbsPath: "+ srcAbsPath1 + " - destAbsPath: " + destAbsPath1 +"]");
					ItemDelegate new_item = copyItem(session, srcAbsPath1, destAbsPath1, login);
					xmlConfig = xstream.toXML(new_item);
					response.setContentLength(xmlConfig.length());
					out.println(xmlConfig);
				} catch (Exception e) {
					logger.error("Error copying item from path: " + srcAbsPath1 +  " - to: " + destAbsPath1, e);
					xmlConfig = xstream.toXML(e.toString());
					response.setContentLength(xmlConfig.length());
					out.println(xmlConfig);
				}
				break;

			case "CreateReference":
				String srcID = null;
				String destID = null;
				try{
					srcID = request.getParameter(ServletParameter.SRC_ID);
					destID = request.getParameter(ServletParameter.DEST_ID);
					logger.info("Servlet CreateReference called with parameters: [srcID: "+ srcID + " - destID: " + destID +"]");
					ItemDelegate new_item = createReference(session, srcID, destID, login);
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

			case "Move":
				String srcAbsPath11 = null;
				String destAbsPath11 = null;
				try{
					srcAbsPath11 = new String(request.getParameter(ServletParameter.SRC_ABS_PATH).getBytes("iso-8859-1"), "UTF-8");
					destAbsPath11 = new String(request.getParameter(ServletParameter.DEST_ABS_PATH).getBytes("iso-8859-1"), "UTF-8");
					logger.info("Servlet Move called with parameters: [srcAbsPath11: "+ srcAbsPath11 + " - destAbsPath11: " + destAbsPath11 +"]");
					ItemDelegate new_item = moveItem(session, srcAbsPath11, destAbsPath11, login);
					xmlConfig = xstream.toXML(new_item);
					response.setContentLength(xmlConfig.length());
					out.println(xmlConfig);
				} catch (Exception e) {
					logger.error("Error moving item from path: " + srcAbsPath11 +  " - to: " + destAbsPath11, e);
					xmlConfig = xstream.toXML(e.toString());
					response.setContentLength(xmlConfig.length());
					out.println(xmlConfig);
				}
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
					xmlConfig = xstream.toXML("Item removed");
					response.setContentLength(xmlConfig.length());
					out.println(xmlConfig);
				} catch (Exception e) {
					logger.error("Error removing item by path: " + absPath, e);
					xmlConfig = xstream.toXML(e.toString());
					response.setContentLength(xmlConfig.length());
					out.println(xmlConfig);
				}
				break;

			case "SaveItem":
				ItemDelegate item = null;
				try{
					item = (ItemDelegate) xstream.fromXML(request.getInputStream());
					logger.info("Servlet saveItem called on item: "+ item.getName());
					ItemDelegateWrapper wrapper = new ItemDelegateWrapper(item, "");
					ItemDelegate new_item = wrapper.save(session);

					xmlConfig = xstream.toXML(new_item);
					response.setContentLength(xmlConfig.length());
					out.println(xmlConfig);
				} catch (Exception e) {
					logger.error("Error saving item with id: " + item.getId(), e);
					xmlConfig = xstream.toXML(e.toString());
					response.setContentLength(xmlConfig.length());
					out.println(xmlConfig);
				}
				break;

			case "LockSession":
				String uuid = null;
				String id = null;
				try{
					uuid = request.getParameter(ServletParameter.UUID);	
					id = request.getParameter(ServletParameter.ID);	
					//					logger.info("Servlet LockSession called with parameters: [uuid: "+ uuid + " - id: " + id +"]");
					LockManager lockManager = session.getWorkspace().getLockManager();
					Lock lock = lockManager.lock(session.getNodeByIdentifier(id).getPath(), true, true, 0, "");
					logger.trace("LOCK on Node: " + lock.getNode().getPath());
					session.save();
					xmlConfig = xstream.toXML("Node id " + id + " locked in session " + uuid);
					response.setContentLength(xmlConfig.length());
					out.println(xmlConfig);
				} catch (Exception e) {
					logger.error("Error locking item with id: " + id + " in session " + uuid, e);
					xmlConfig = xstream.toXML(e.toString());
					response.setContentLength(xmlConfig.length());
					out.println(xmlConfig);
				}
				break;


			case "UnlockSession":
				String uuid1 = null;
				String id1 = null;
				try{
					uuid1 = request.getParameter(ServletParameter.UUID);	
					id1 = request.getParameter(ServletParameter.ID);	
					//					logger.info("Servlet LockSession called with parameters: [uuid: "+ uuid1 + " - id: " + id1 +"]");
					LockManager lockManager = session.getWorkspace().getLockManager();
					String pathNode = session.getNodeByIdentifier(id1).getPath();
					if (lockManager.isLocked(pathNode)){
						lockManager.unlock(pathNode);
						logger.trace("Remove Lock from node: " + pathNode);
					}
					xmlConfig = xstream.toXML("Node id " + id1 + " unlocked in session " + uuid1);
					response.setContentLength(xmlConfig.length());
					out.println(xmlConfig);
				} catch (Exception e) {
					logger.error("Error unlocking item with id: " + id1 + " in session " + uuid1, e);
					xmlConfig = xstream.toXML(e.toString());
					response.setContentLength(xmlConfig.length());
					out.println(xmlConfig);
				}
				break;

			case "IsLocked":
				String id11 = null;
				boolean flag = false;
				try{
					id11 = request.getParameter(ServletParameter.ID);	
					//					logger.info("Servlet IsLocked called with parameters: [ id: " + id11 +"]");
					LockManager lockManager = session.getWorkspace().getLockManager();
					String pathNode = session.getNodeByIdentifier(id11).getPath();
					if (lockManager.isLocked(pathNode)){
						flag = true;
						logger.trace(pathNode + " is locked");

					}
					xmlConfig = xstream.toXML(flag);
					response.setContentLength(xmlConfig.length());
					out.println(xmlConfig);
				} catch (Exception e) {
					logger.error("Error in islock servlet ", e);
					xmlConfig = xstream.toXML(e.toString());
					response.setContentLength(xmlConfig.length());
					out.println(xmlConfig);
				}
				break;


			case "ActiveSessions":
				//				logger.info("Servlet ActiveSessions called");
				List<String> ids = new ArrayList<String>();
				try{
					Set<String> list = sessionManager.getSessionIds();
					for (String sessionId1: list)
						ids.add(sessionId1);
					xmlConfig = xstream.toXML(ids);
					response.setContentLength(xmlConfig.length());
					out.println(xmlConfig);
				} catch (Exception e) {
					logger.error("Error ActiveSessions", e);
					xmlConfig = xstream.toXML(e.toString());
					response.setContentLength(xmlConfig.length());
					out.println(xmlConfig);
				}
				break;

			default:
				break;
			}


		} catch (Exception e) {
			logger.error("Error repository ex " + e);
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


	private List<ItemDelegate> getParentsById(SessionImpl session, String identifier,
			String login) throws Exception {

		Node node = session.getNodeByIdentifier(identifier);

		List<ItemDelegate> parents = new ArrayList<ItemDelegate>();

		while(!isRoot(node)) {
			ItemDelegate item = null;
			NodeManager wrap = new NodeManager(node, login);
			try {
				item = wrap.getItemDelegate();
				
				parents.add(item);
				
				if ((login!=null) && (item.getPrimaryType().equals(PrimaryNodeType.NT_WORKSPACE_SHARED_FOLDER))){
					@SuppressWarnings("unchecked")
					Map<String, String> users =  (Map<String, String>) new XStream().fromXML(item.getProperties().get(NodeProperty.USERS));
					String[] user = users.get(login).split("/");
					String parentId = user[0];
					node = session.getNodeByIdentifier(parentId);
				}else
					node = node.getParent();
				
			} catch (Exception e) {
				logger.error("Error getting parents by id " + e);
			}
			
		}
		return parents;
	}

	/**
	 * Check if the node is root in Jackrabbit
	 * @param node
	 * @return true if the node is root
	 * @throws InternalErrorException
	 * @throws ItemNotFoundException
	 * @throws RepositoryException
	 */
	public boolean isRoot(Node node) throws InternalErrorException, RepositoryException { 
		Node parent = null;
		try{
			parent = node.getParent();
		}catch (ItemNotFoundException e){
			return true;
		}
		return (parent == null);
	}

	/**
	 * Get children by id
	 * @param session
	 * @param identifier
	 * @param login
	 * @return
	 * @throws Exception
	 */
	private List<ItemDelegate> getHiddenItems(SessionImpl session, String identifier, String login) throws Exception {

		Node folderNode = session.getNodeByIdentifier(identifier);

		NodeIterator iterator = folderNode.getNodes();
		List<ItemDelegate> children = new ArrayList<ItemDelegate>();
		while(iterator.hasNext()) {

			Node node = iterator.nextNode();
			Boolean isHidden = false;

			if (node.hasProperty(NodeProperty.HIDDEN.toString()))
				isHidden = node.getProperty(NodeProperty.HIDDEN.toString()).getBoolean();

			if (!isHidden)
				continue;

			ItemDelegate item = null;
			NodeManager wrap = new NodeManager(node, login);
			try {
				item = wrap.getItemDelegate();
				children.add(item);
			} catch (Exception e) {
				e.printStackTrace();
			}


		}
		return children;
	}



	/**
	 * Get children by id
	 * @param session
	 * @param identifier
	 * @param login
	 * @return
	 * @throws Exception
	 */
	private List<ItemDelegate> getChildren(SessionImpl session, String identifier, String login, Boolean showHidden) throws Exception {

		Node folderNode = session.getNodeByIdentifier(identifier);


		NodeIterator iterator = folderNode.getNodes();
		List<ItemDelegate> children = new ArrayList<ItemDelegate>();
		while(iterator.hasNext()) {

			Node node = iterator.nextNode();
			Boolean isHidden = false;

			if (node.hasProperty(NodeProperty.HIDDEN.toString()))
				isHidden = node.getProperty(NodeProperty.HIDDEN.toString()).getBoolean();

			String path = null;
			try {
				path = node.getPath();
				//				String title = node.getName();
				String name = path.substring(path.lastIndexOf('/') + 1);
				if ((isHidden && !showHidden) || (name.equals("Trash") || (name.equals("MySpecialFolders") ||(name.startsWith("rep:")) || (name.startsWith("hl:")) || (name.startsWith(".")))))
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
	private ItemDelegate createReference(SessionImpl session, String srcID, String destID, String login) {
		ItemDelegate item = null;
		Node srcNode = null;
		Node destNode = null;
		try{
			srcNode = session.getNodeByIdentifier(srcID);
			srcNode.addMixin(JcrConstants.MIX_REFERENCEABLE);

			destNode = session.getNodeByIdentifier(destID);

			Node link = destNode.addNode(srcNode.getName(), srcNode.getPrimaryNodeType().getName());
			link.setProperty(NodeProperty.REFERENCE.toString(), srcNode);
			session.save();

			logger.info("References to " + srcNode.getPath() + ":");
			for (Property reference : JcrUtils.getReferences(srcNode)) {
				logger.info("- " + reference.getPath());
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
	private ItemDelegate moveItem(SessionImpl session, String srcAbsPath, String destAbsPath, String login) throws RepositoryException, InternalErrorException {
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

	/**
	 * Execute a query
	 * @param session
	 * @param query
	 * @param limit 
	 * @param login2 
	 * @return
	 * @throws Exception
	 */
	private List<SearchItemDelegate> execute(SessionImpl session, String query, String lang, String login, int limit) throws Exception {

		List<SearchItemDelegate> list = null;

		QueryManager queryManager = session.getWorkspace().getQueryManager();	
		try{
			javax.jcr.query.Query q = null;

			switch (lang) {
			case "JCR-SQL2":
				q = queryManager.createQuery(query, javax.jcr.query.Query.JCR_SQL2);
				break;
			case "xpath":
				q = queryManager.createQuery(query, javax.jcr.query.Query.XPATH);
				break;
			case "sql":
				q = queryManager.createQuery(query, javax.jcr.query.Query.SQL);
				break;
			case "JCR_JQOM":
				q = queryManager.createQuery(query, javax.jcr.query.Query.JCR_JQOM);
				break;
			default:
				logger.error("lang unknown");
				break;
			}


			//			q.setLimit(limit);
			QueryResult result = q.execute();

			NodeIterator iterator = result.getNodes();
			//			int count = 0;
			list = new LinkedList<SearchItemDelegate>();
			while (iterator.hasNext()) {
				//				if (limit!=0){
				//					if (count<limit){
				Node node = iterator.nextNode();

				String itemName = isValidSearchResult(node, login);
				if (itemName == null) {
					logger.trace("Search result is not valid :" + node.getPath());
					continue;
				}

				SearchItemDelegate item = null;
				NodeManager wrap = new NodeManager(node, login);
				try {
					item = wrap.getSearchItem(itemName);
					if (!list.contains(item))
						list.add(item);
					//							count++;
				} catch (Exception e) {
					e.printStackTrace();
				}
				//					}
				//				}

			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return list;

	}




}
