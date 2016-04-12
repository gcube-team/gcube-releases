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
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Delete Aces for a resource.
 */

public class SearchItems extends HttpServlet {
	private static Logger logger = LoggerFactory.getLogger(SearchItems.class);

	public static final String PATH_SEPARATOR 				= "/";
	public static final String HOME_FOLDER 					= "Home";
	public static final String SHARED_FOLDER				= "Share";	
	public static final String USERS 						= "hl:users";

	private static final long serialVersionUID = 1L;

	public SearchItems() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html; charset=UTF-8");

		PrintWriter out = response.getWriter();

		final String query = request.getParameter(ServletParameter.QUERY);	
		final String lang = request.getParameter(ServletParameter.LANG);
		final String login = request.getParameter(ServletParameter.LOGIN);

		logger.info("Servlet SearchItems called with parameters: [query: "+ query + " - lang: " + lang + " - login: " + login +"]");


		Repository rep = RepositoryAccessServlet
				.getRepository(getServletContext());
		SessionImpl session = null;

		XStream xstream = null;
		String xmlConfig = null;
		List<ItemDelegate> children = null;
		try {
			xstream = new XStream(new DomDriver("UTF-8"));
			session = (SessionImpl) rep
					.login(new SimpleCredentials(request.getParameter(ConfigRepository.USER), request.getParameter(ConfigRepository.PASSWORD).toCharArray()));

			children = execute(session, query, lang, login);

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

	
}