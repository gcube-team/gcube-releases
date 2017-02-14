package org.apache.jackrabbit.j2ee.usermanager;

import java.io.IOException;
import java.io.PrintWriter;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.SimpleCredentials;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.j2ee.ConfigRepository;
import org.apache.jackrabbit.j2ee.RepositoryAccessServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

public class UpdateGroupServlet extends HttpServlet {

	public static final String MEMBER							= "member";
	public static final String MEMBER_DELETE					= "memberToDelete";
	public static final String GROUP_NAME						= "groupName";

	private static final long serialVersionUID = 1L;
	
	private Logger logger = LoggerFactory.getLogger(UpdateGroupServlet.class);

	public UpdateGroupServlet() {
		super();
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		logger.info("Servlet UpdateGroupServlet called ......");

		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();

		Repository rep = RepositoryAccessServlet.getRepository(getServletContext());
		SessionImpl session = null;
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

			final String groupName = request.getParameter(GROUP_NAME);
			final String[] membersToAdd = request.getParameterValues(MEMBER);
			final String[] membersToDelete = request.getParameterValues(MEMBER_DELETE);

			final UserManager userManager = session.getUserManager();

			Authorizable authorizable = userManager.getAuthorizable(groupName);

			if (authorizable.isGroup()) {
				Group group = (Group) authorizable;
				
				if (membersToAdd != null) {
					for (String member : membersToAdd) {
						Authorizable memberAuthorizable = getAuthorizable(member,userManager);
						if (memberAuthorizable != null) {
							group.addMember(memberAuthorizable);
						}
					}
				}

				if (membersToDelete != null) {
					for (String member : membersToDelete) {
						Authorizable memberAuthorizable = getAuthorizable(member,userManager);
						if (memberAuthorizable != null) {
							group.removeMember(memberAuthorizable);
						}
					}
				}
			}

			session.save();
			
			modified = true;
			xmlConfig = xstream.toXML(modified);
			response.setContentLength(xmlConfig.length()); 
			out.println(xmlConfig);

		} catch (RepositoryException e) {
			modified = false;
			
			xmlConfig = xstream.toXML(modified);
			response.setContentLength(xmlConfig.length()); 
			out.println(xmlConfig);	

		} finally {
			if(session != null)
				session.logout();

			out.close();
			out.flush();
		}	
	}


	/**
	 * Gets the member, assuming its a principal name, failing that it assumes it a path to the resource.
	 * @param member the token pointing to the member, either a name or a uri
	 * @param userManager the user manager for this request.
	 * @return the authorizable, or null if no authorizable was found.
	 */
	private Authorizable getAuthorizable( String member, UserManager userManager) {
		Authorizable memberAuthorizable = null;
		try {
			memberAuthorizable = userManager.getAuthorizable(member);
		} catch (RepositoryException e) {
			// if we can't find the members then it may be resolvable as a resource.
		}
		return memberAuthorizable;
	}

}