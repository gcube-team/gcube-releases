package org.apache.jackrabbit.j2ee.workspacemanager.lock;

import java.io.IOException;
import java.io.PrintWriter;

import javax.jcr.Repository;
import javax.jcr.SimpleCredentials;
import javax.jcr.lock.LockManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.j2ee.ConfigRepository;
import org.apache.jackrabbit.j2ee.RepositoryAccessServlet;
import org.apache.jackrabbit.j2ee.workspacemanager.ServletParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Delete Aces for a resource.
 */

public class IsLocked extends HttpServlet {
	private static Logger logger = LoggerFactory.getLogger(IsLocked.class);


	private static final long serialVersionUID = 1L;

	public IsLocked() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {


		//		response.setContentType("text/plain");
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		final String id = request.getParameter(ServletParameter.ID);	
		final String login = request.getParameter(ConfigRepository.USER);	
		final String pass = request.getParameter(ConfigRepository.PASSWORD);

//		logger.info("Servlet IsLocked called with parameters: [id: "+ id + "]");

		Repository rep = RepositoryAccessServlet
				.getRepository(getServletContext());
		SessionImpl session = null;

		XStream xstream = null;
		String xmlConfig = null;
		boolean flag = false;

		try {
			xstream = new XStream(new DomDriver("UTF-8"));

			session = (SessionImpl) rep
					.login(new SimpleCredentials(login, pass.toCharArray()));

			LockManager lockManager = session.getWorkspace().getLockManager();
			String pathNode = session.getNodeByIdentifier(id).getPath();
			if (lockManager.isLocked(pathNode)){
				flag = true;
				logger.trace("Remove Lock from node: " + pathNode);

			}

			xmlConfig = xstream.toXML(flag);
			response.setContentLength(xmlConfig.length());
			out.println(xmlConfig);

		} catch (Exception e) {
			logger.error("Error repository ex " + e);
			xmlConfig = xstream.toXML(e.toString());
			response.setContentLength(xmlConfig.length());
			out.println(xmlConfig);

		} finally {
			if (session!=null)
				session.logout();

			out.close();
			out.flush();

		}
	}




}