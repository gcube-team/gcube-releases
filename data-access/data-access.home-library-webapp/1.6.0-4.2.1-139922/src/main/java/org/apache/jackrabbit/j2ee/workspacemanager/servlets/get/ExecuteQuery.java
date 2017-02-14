package org.apache.jackrabbit.j2ee.workspacemanager.servlets.get;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.j2ee.RepositoryAccessServlet;
import org.apache.jackrabbit.j2ee.workspacemanager.NodeManager;
import org.gcube.common.homelibary.model.servlet.ServletParameter;
import org.apache.jackrabbit.j2ee.workspacemanager.servlets.Utils;
import org.apache.jackrabbit.j2ee.workspacemanager.session.SessionManager;
import org.gcube.common.homelibary.model.items.SearchItemDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class ExecuteQuery extends HttpServlet {

	private static Logger logger = LoggerFactory.getLogger(ExecuteQuery.class);
	private static final long serialVersionUID = 1L;

	public ExecuteQuery() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		//		response.setContentType("text/plain");
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		String sessionId = request.getParameter(ServletParameter.UUID);

		String query = new String(request.getParameter(ServletParameter.QUERY).getBytes("iso-8859-1"), "UTF-8");	
		String lang = request.getParameter(ServletParameter.LANG);
		String limit = request.getParameter(ServletParameter.LIMIT);

		logger.info("Servlet ExecuteQuery called with parameters: [query: "+ query + " - lang: " + lang + " - limit: " + limit +"]");

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
			} else {
				session = sessionManager.newSession(request);
				sessionId = session.toString();
			}

			List<SearchItemDelegate> children = execute(session, query, lang, sessionManager.getLogin(request), Integer.parseInt(limit));
			xmlConfig = xstream.toXML(children);
			out.println(xmlConfig);

		} catch (Exception e) {
			if (!exist)
				sessionManager.releaseSession(sessionId);
			throw new ServletException(e);

		} finally {
			if (!exist)
				sessionManager.releaseSession(sessionId);

			out.close();
			out.flush();
		}
	}


	private List<SearchItemDelegate> execute(Session session, String query, String lang, String login, int limit) throws Exception {

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


			QueryResult result = q.execute();

			NodeIterator iterator = result.getNodes();
			list = new LinkedList<SearchItemDelegate>();
			int i = 0;
			if (limit<1)
				limit = 100;
			while (iterator.hasNext()  && i < limit) {

				Node node = iterator.nextNode();

				String itemName = Utils.isValidSearchResult(node, login);
				if (itemName == null) {
					logger.trace("Search result is not valid :" + node.getPath());
					continue;
				}

				SearchItemDelegate item = null;
				NodeManager wrap = new NodeManager(node, login);
				try {
					item = wrap.getSearchItem(itemName);
					if (!list.contains(item)){		
						list.add(item);
						i++;
					}

				} catch (Exception e) {
					throw new RepositoryException("Error adding item: " + e.getMessage());
				}

			}
		}catch (Exception e) {
			throw new RepositoryException("Error executing query  " + query +" : " + e.getMessage());
		}
		return list;

	}


}
