package org.apache.jackrabbit.j2ee.workspacemanager.accounting.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.SimpleCredentials;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.j2ee.RepositoryAccessServlet;
import org.apache.jackrabbit.j2ee.workspacemanager.accounting.AccoutingNodeWrapper;
import org.gcube.common.homelibary.model.items.accounting.AccountingDelegate;
import org.gcube.common.homelibary.model.servlet.ServletParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

/**
 * Delete Aces for a resource.
 */

public class GetAccountingById extends HttpServlet {
	private static Logger logger = LoggerFactory.getLogger(GetAccountingById.class);

	private static final long serialVersionUID = 1L;

	public GetAccountingById() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {


		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		final String id = request.getParameter(ServletParameter.ID);	

		logger.debug("Servlet GetAccountingById called with parameters: [id: "+ id + "]");
		
		Repository rep = RepositoryAccessServlet
				.getRepository(getServletContext());
		SessionImpl session = null;

		XStream xstream = null;
		String xmlConfig = null;
		List<AccountingDelegate> children = null;
		String user = request.getSession()
				.getServletContext()
				.getInitParameter("user");
		char[] pass = request.getSession()
				.getServletContext()
				.getInitParameter("pass").toCharArray();
		try {

			session = (SessionImpl) rep
					.login(new SimpleCredentials(user, pass));
			xstream = new XStream();

			children = getAccouting(session, id);

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

	private List<AccountingDelegate> getAccouting(SessionImpl session, String identifier) throws Exception {

		List<AccountingDelegate> children = new ArrayList<AccountingDelegate>();
		
		Node node = session.getNodeByIdentifier(identifier);
		Node accountingNode = node.getNode("hl:accounting");
		for(NodeIterator iterator = accountingNode.getNodes();iterator.hasNext();) {
			Node entryNode = (Node)iterator.next();
			
			AccountingDelegate item = null;
			AccoutingNodeWrapper wrap = new AccoutingNodeWrapper(entryNode);
			try {
				item = wrap.getAccountingDelegate();
//				wrap.setProperties(item);
				children.add(item);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		}
		return children;
	}



}