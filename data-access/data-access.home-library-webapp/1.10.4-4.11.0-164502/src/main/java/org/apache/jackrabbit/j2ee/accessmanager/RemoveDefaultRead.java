package org.apache.jackrabbit.j2ee.accessmanager;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.security.Principal;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.SimpleCredentials;
import javax.jcr.security.AccessControlEntry;
import javax.jcr.security.AccessControlList;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.AccessControlPolicy;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.api.security.principal.PrincipalManager;
import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.j2ee.RepositoryAccessServlet;
import org.gcube.common.homelibary.model.servlet.ServletParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

public class RemoveDefaultRead extends HttpServlet {

	public static final String ABS_PATH				= "absPath";

	private Logger logger = LoggerFactory.getLogger(RemoveDefaultRead.class);
	private static final long serialVersionUID = 1L;

	public RemoveDefaultRead() {
		super();
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		logger.debug("Servlet RemoveDefaultRead Ace called ......");
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();

		SessionImpl session = null;
		Repository rep = RepositoryAccessServlet.getRepository(getServletContext());

		boolean modified = false;
		XStream xstream = null;
		String xmlConfig = null;

		String adminId = request.getSession()
				.getServletContext()
				.getInitParameter("user");
		char[] adminPass = request.getSession()
				.getServletContext()
				.getInitParameter("pass").toCharArray();
		try {

			session = (SessionImpl) rep
					.login(new SimpleCredentials(adminId, adminPass));
			xstream = new XStream();

			String resourcePath = request.getParameter(ServletParameter.ABS_PATH);
//			String resourcePath = new String(request.getParameter(ABS_PATH).getBytes("iso-8859-1"), "UTF-8");


			AccessControlManager acMgr = session.getAccessControlManager();

			//			String rootPath = session.getRootNode().getPath();
//			System.out.println("abs PATH " + resourcePath);
			AccessControlPolicy[] acls = acMgr.getPolicies( resourcePath );
//			System.out.println("acls size " + acls.length);
			if ( acls.length > 0 ) {
				PrincipalManager pMgr = session.getPrincipalManager();

				Principal everyone = pMgr.getEveryone();

//				Privilege[] privs =
//						new Privilege[] { acMgr.privilegeFromName( Privilege.JCR_READ )};
				AccessControlList acList = (AccessControlList) acls[0];
				AccessControlEntry[] acEntries = acList.getAccessControlEntries();
				for ( AccessControlEntry acEntry : acEntries ) {
//					System.out.println("getPrincipal " + acEntry.getPrincipal().getName());
					if ( acEntry.getPrincipal().equals(everyone) ) {
//						System.out.println("remove acl for everyone");
//						System.out.println(acEntry.getPrivileges().toString());
						acList.removeAccessControlEntry(acEntry);
						session.save();
//						System.out.println("save.");
						modified = true;
					}
				}
//				acList.addAccessControlEntry( everyone, privs );
//				acMgr.setPolicy( resourcePath, acList );
				session.save();
			}

			xmlConfig = xstream.toXML(modified);
			response.setContentLength(xmlConfig.length()); 
			out.println(xmlConfig);


		} catch (RepositoryException e) {

			modified = false;


			//			xmlConfig = xstream.toXML(modified);

			xmlConfig = xstream.toXML(e);

			response.setContentLength(xmlConfig.length()); 
			out.println(xmlConfig);		
			out.println(xmlConfig);		

		} finally {
			if(session != null)
				session.logout();

			out.close();
			out.flush();
		}	

	}




}