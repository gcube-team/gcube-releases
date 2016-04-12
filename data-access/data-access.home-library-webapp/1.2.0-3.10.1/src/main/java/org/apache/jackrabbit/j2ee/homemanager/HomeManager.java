package org.apache.jackrabbit.j2ee.homemanager;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.SimpleCredentials;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.j2ee.ConfigRepository;
import org.apache.jackrabbit.j2ee.RepositoryAccessServlet;
import org.apache.jackrabbit.j2ee.accessmanager.ModifyAceServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

public class HomeManager extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String PATH							= "path";
	
	private Logger logger = LoggerFactory.getLogger(HomeManager.class);

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		logger.info("Home Manager servlet called...");
		
		String message = "";
		
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		
		Repository rep = RepositoryAccessServlet.getRepository(getServletContext());
		SessionImpl session = null;


		try {
			
			session = (SessionImpl) rep
					.login(new SimpleCredentials(request.getParameter(ConfigRepository.USER), request.getParameter(ConfigRepository.PASSWORD).toCharArray()));
		
			
			final String absPath = request.getParameter(PATH);
			final List<Node> childrenNodes = new ArrayList<Node>();
			
//			System.out.println("session.getPath(): " + session.getRootNode().getPath());
//			System.out.println("session.getRootNode().getNode("+ absPath + "): ");
//			System.out.println(session.getRootNode().getNode(absPath));
			NodeIterator children = session.getRootNode().getNode(absPath).getNodes();
			
//			System.out.println("Size: " + children.getPosition());
			while (children.hasNext()){
				Node child = children.nextNode();
//				System.out.println(child.getPath());
				childrenNodes.add(child);
			}
			
			XStream xstream = new XStream();
			String xmlConfig = xstream.toXML(childrenNodes);

			out.println(xmlConfig);
			
		} catch (RepositoryException e) {
			message = e.getMessage();
			response.setContentLength(message.length()); 
			out.println(message);	

		} finally {
			if(session != null)
				session.logout();

			out.close();
			out.flush();
		}	
		
	}

}
