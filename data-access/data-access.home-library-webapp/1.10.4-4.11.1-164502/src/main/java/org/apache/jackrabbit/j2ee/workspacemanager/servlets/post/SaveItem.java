package org.apache.jackrabbit.j2ee.workspacemanager.servlets.post;

import java.io.IOException;
import java.io.PrintWriter;

import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.lock.LockException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.j2ee.RepositoryAccessServlet;
import org.apache.jackrabbit.j2ee.workspacemanager.ItemDelegateWrapper;
import org.apache.jackrabbit.j2ee.workspacemanager.session.SessionManager;
import org.gcube.common.homelibary.model.servlet.ServletParameter;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;


public class SaveItem extends HttpServlet {

	private static Logger logger = LoggerFactory.getLogger(SaveItem.class);
	private static final long serialVersionUID = 1L;


	public SaveItem() {
		super();
	}


	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		String sessionId = request.getParameter(ServletParameter.UUID);
		logger.debug("SaveItem servlet called with session id " + sessionId);
		boolean createVersion = Boolean.valueOf(request.getParameter("flag"));

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
				//				logger.info(sessionId + " already exists, get it");
			}
			else{				 
				session = sessionManager.newSession(request);
				sessionId = session.toString();
				//				logger.info(sessionId + " does not exist, a new session has been created " + session.toString());
			}

			ItemDelegate item = null;
			try{

				//					System.out.println(request.getInputStream().available());
				item = (ItemDelegate) xstream.fromXML(request.getInputStream());
				//					System.out.println(item.toString());

				logger.info("Servlet SaveItem called with parameters: [itemName: "+ item.getName() + " - by: " + sessionManager.getLogin(request) +"]");

				ItemDelegateWrapper wrapper = new ItemDelegateWrapper(item, "");

				//				System.out.println("save item: " + item.toString());
				ItemDelegate new_item = wrapper.save(session, createVersion);

				xmlConfig = xstream.toXML(new_item);
				//response.setContentLength(xmlConfig.length());
				out.println(xmlConfig);
			} catch (LockException e) {
				logger.error("Error saving item with id: " + item.getId(), e);
				xmlConfig = xstream.toXML(e.toString());
				//response.setContentLength(xmlConfig.length());
				out.println(xmlConfig);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Error saving item with id: " + item.getId(), e);
				xmlConfig = xstream.toXML(e.toString());
				//	response.setContentLength(xmlConfig.length());
				out.println(xmlConfig);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error repository ex " + e.getMessage());
			xmlConfig = xstream.toXML(e.toString());
			//	response.setContentLength(xmlConfig.length());
			out.println(xmlConfig);

		} finally {
			if (!exist){
				sessionManager.releaseSession(sessionId);
				//				logger.info("Released session " + sessionId);
			}
			out.close();
			//	out.flush();
		}
	}


}
