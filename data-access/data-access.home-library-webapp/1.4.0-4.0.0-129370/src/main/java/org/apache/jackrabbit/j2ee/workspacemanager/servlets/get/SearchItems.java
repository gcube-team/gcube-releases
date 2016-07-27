package org.apache.jackrabbit.j2ee.workspacemanager.servlets.get;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
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

import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.j2ee.ConfigRepository;
import org.apache.jackrabbit.j2ee.RepositoryAccessServlet;
import org.apache.jackrabbit.j2ee.workspacemanager.ItemDelegateWrapper;
import org.apache.jackrabbit.j2ee.workspacemanager.NodeManager;
import org.apache.jackrabbit.j2ee.workspacemanager.ServletParameter;
import org.apache.jackrabbit.j2ee.workspacemanager.SessionManager;
import org.apache.jackrabbit.j2ee.workspacemanager.servlets.Utils;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.SearchItemDelegate;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibary.model.items.type.PrimaryNodeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class SearchItems extends HttpServlet {

	private static Logger logger = LoggerFactory.getLogger(SearchItems.class);
	private static final long serialVersionUID = 1L;


	public SearchItems() {
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

				List<ItemDelegate> children = null;
				String query = null;
				String lang = null;
				try{
					query = request.getParameter(ServletParameter.QUERY);	
					lang = request.getParameter(ServletParameter.LANG);
					logger.info("Servlet SearchItems called with parameters: [query: "+ query + " - lang: " + lang + "]");
					children = execute(session, query, lang, login);
					xmlConfig = xstream.toXML(children);
//					response.setContentLength(xmlConfig.length());
					out.println(xmlConfig);
				} catch (Exception e) {
					logger.error("Error searching items by query: " + query +  " - lang: " + lang, e);
					xmlConfig = xstream.toXML(e.toString());
					//		response.setContentLength(xmlConfig.length());
					out.println(xmlConfig);
				}

		} catch (Exception e) {
			logger.error("Error repository ex " + e);
			xmlConfig = xstream.toXML(e.toString());
			//	response.setContentLength(xmlConfig.length());
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

				String itemName = Utils.isValidSearchResult(node, login);
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



}
