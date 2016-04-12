package org.apache.jackrabbit.j2ee.workspacemanager.get;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.SimpleCredentials;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.j2ee.ConfigRepository;
import org.apache.jackrabbit.j2ee.RepositoryAccessServlet;
import org.apache.jackrabbit.j2ee.workspacemanager.NodeManager;
import org.apache.jackrabbit.j2ee.workspacemanager.ServletParameter;
import org.apache.jackrabbit.j2ee.workspacemanager.SessionManager;
import org.gcube.common.homelibary.model.items.SearchItemDelegate;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Delete Aces for a resource.
 */

public class ExecuteQuery extends HttpServlet {
	private static Logger logger = LoggerFactory.getLogger(ExecuteQuery.class);



	public static final String PATH_SEPARATOR 				= "/";
	public static final String HOME_FOLDER 					= "Home";
	public static final String SHARED_FOLDER				= "Share";	
	public static final String USERS 						= "hl:users";

	private static final long serialVersionUID = 1L;

	public ExecuteQuery() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html; charset=UTF-8");

		PrintWriter out = response.getWriter();

		final String query = new String(request.getParameter(ServletParameter.QUERY).getBytes("iso-8859-1"), "UTF-8");	
		final String lang = request.getParameter(ServletParameter.LANG);
		final String login = request.getParameter(ServletParameter.LOGIN);
		final String limit = request.getParameter(ServletParameter.LIMIT);

		logger.info("Servlet ExecuteQuery called with parameters: [query: "+ query + " - lang: " + lang + " - login: " + login + " - limit: " + limit +"]");





		Repository rep = RepositoryAccessServlet
				.getRepository(getServletContext());	

		SessionImpl session = null;

		XStream xstream = null;
		String xmlConfig = null;
		List<SearchItemDelegate> children = null;
		try {
			xstream = new XStream(new DomDriver("UTF-8"));
			session = (SessionImpl) rep
					.login(new SimpleCredentials(request.getParameter(ConfigRepository.USER), request.getParameter(ConfigRepository.PASSWORD).toCharArray()));

			children = execute(session, query, lang, login, Integer.parseInt(limit));

			xmlConfig = xstream.toXML(children);
			response.setContentLength(xmlConfig.length());
			out.println(xmlConfig);

		} catch (Exception e) {
			logger.error("Error repository ex " + e);
			xmlConfig = xstream.toXML(e.toString());
			response.setContentLength(xmlConfig.length());
			out.println(xmlConfig);

		} finally {
			if (session != null)
				session.logout();

			out.close();
			out.flush();
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


}