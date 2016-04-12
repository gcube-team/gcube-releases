package org.apache.jackrabbit.j2ee.workspacemanager.post;

import java.io.IOException;
import java.io.PrintWriter;

import javax.jcr.Repository;
import javax.jcr.SimpleCredentials;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.j2ee.ConfigRepository;
import org.apache.jackrabbit.j2ee.RepositoryAccessServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Receive a delegate item
 */

public class RemoveItem extends HttpServlet {
	private static Logger logger = LoggerFactory.getLogger(RemoveItem.class);
	private static final long serialVersionUID = 1L;
	private static final String ABS_PATH = "absPath";


	public RemoveItem() {
		super();
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {


		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		final String absPath = request.getParameter(ABS_PATH);
		
		logger.info("Servlet RemoveItem called with parameters: [absPath: "+ absPath + "]");
		
		Repository rep = RepositoryAccessServlet
				.getRepository(getServletContext());

		SessionImpl session = null;

		XStream xstream = null;
		String xmlConfig = null;
		//		List<ItemDelegate> children = null;
		try {
			xstream = new XStream(new DomDriver("UTF-8"));
			session = (SessionImpl) rep
					.login(new SimpleCredentials(request.getParameter(ConfigRepository.USER), request.getParameter(ConfigRepository.PASSWORD).toCharArray()));


			// Boolean b = Boolean.valueOf(all);

			remove(session, absPath);

			xmlConfig = xstream.toXML("Item removed");
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
