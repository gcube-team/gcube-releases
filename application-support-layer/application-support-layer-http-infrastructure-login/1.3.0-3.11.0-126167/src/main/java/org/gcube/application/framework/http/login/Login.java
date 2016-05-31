package org.gcube.application.framework.http.login;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gcube.application.framework.core.security.LDAPAuthenticationModule;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Servlet implementation class Login
 */
public class Login extends HttpServlet {
	
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(Login.class);
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Login() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String auth = request.getHeader("Authorization");

		HttpSession session = request.getSession(true);

		logger.debug("The request session id is: " + session.getId());
		
		if (session.getAttribute("logon.isDone") == null) {
			// Do we allow that user?
			String username = allowUserGet(auth);
			if (username == null) {
				response.setHeader("WWW-Authenticate", "BASIC realm=\"D4ScienceUsers\"");
				response.sendError(response.SC_UNAUTHORIZED);			
				return;
			}
			else {
				// Valid login. Make a note in the session object.
				logger.debug("The session id is: " + session.getId());
				session.setAttribute("logon.isDone", username);	//just a marker object
				String target = (String) session.getAttribute("target");

				try {
					response.setStatus(200);
					response.sendRedirect(response.encodeRedirectURL(request.getRequestURI()));
					return;
				} catch (Exception ignored) {
					logger.debug("Redirect failed");
					logger.debug(target);
				}
			}
		}
		else {
			//user already logged in
			logger.debug("No authentication needed");
			return;
		}
	}


	// This method checks the user information sent in the Authorization
	// header against the ldap users
	protected String allowUserGet (String auth) throws IOException {

		if (auth == null) 
			return null; 	//no auth
		if (!auth.toUpperCase().startsWith("BASIC "))
			return null;	// we only do BASIC

		// Get encoded user and password, comes after "BASIC "
		String userpassEncoded = auth.substring(6);

		// Decode it, using any base 64 decoder
		sun.misc.BASE64Decoder dec = new sun.misc.BASE64Decoder();
		String userpassDecoded = new String(dec.decodeBuffer(userpassEncoded));

		String[] token = userpassDecoded.split(":");
		if (token.length < 2)
			return null;
		String user1 = token[0];
		userpassDecoded = token[1];
		boolean authenticated = false;

		// Check our user list to see if that user and password are "allowed"
		LDAPAuthenticationModule authModule = new LDAPAuthenticationModule();
		try {
			authenticated = authModule.checkAuthentication(user1, userpassDecoded);
		} catch (Throwable all) {
			logger.error("Exception:", all);
		}
		if (authenticated) {
			logger.debug("The user "+user1+" exists and is now authenticated");
			return user1;
		}
		else {
			logger.debug("The user doesn't exist!");
			return null;
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String auth = request.getParameter("password");
		String username = request.getParameter("username");
		if (username == null || auth == null) {
			response.sendError(404);
			return;
		}

		HttpSession session = request.getSession(true);

		logger.debug("The request session id is: " + session.getId());
		
		if (session.getAttribute("logon.isDone") == null) {
			// Do we allow that user?
			if (!allowUserPost(auth, username)) {
				response.sendError(401);			
				//return;
			}
			else {
				// Valid login. Make a note in the session object.
				logger.debug("The session id is: " + session.getId());
				session.setAttribute("logon.isDone", username);	//just a marker object

				PrintWriter out = response.getWriter();
				response.setContentType("text/xml");
				DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = null;
				try {
					docBuilder = dbfac.newDocumentBuilder();
				} catch (ParserConfigurationException e) {
					logger.error("Exception:", e);
				} 

				Document doc = docBuilder.newDocument();
				Element root = doc.createElement("SessionID");
				doc.appendChild(root);
				Element sessionEl = doc.createElement("jsessionid");
				sessionEl.setTextContent(session.getId());
				root.appendChild(sessionEl);

				String xmlToStr = "";
				StringWriter writer = new StringWriter();
				try {
					DOMSource domSource = new DOMSource(doc);
					StreamResult result = new StreamResult(writer);
					TransformerFactory tf = TransformerFactory.newInstance();
					Transformer transformer = tf.newTransformer();
					transformer.transform(domSource, result);		
				} catch (Exception e) {
					logger.error("Exception:", e);
				}
				xmlToStr = writer.toString();
				logger.debug(xmlToStr);
				out.write(xmlToStr);

				out.close();
			}
		}
		else {
			//user already logged in
			logger.debug("No authentication needed");
			return;
		}
	}

	protected boolean allowUserPost (String auth, String user) throws IOException {
		logger.debug("username is: " + user);

		if (auth == null) 
			return false; 	//no auth

		String user1 = user;
		String userpassDecoded = auth;
		boolean authenticated = false;

		// Check our user list to see if that user and password are "allowed"
		LDAPAuthenticationModule authModule = new LDAPAuthenticationModule();
		try {
			authenticated = authModule.checkAuthentication(user1, userpassDecoded);
		} catch (Throwable all) {
			logger.error("Exception:", all);
		}
		if (authenticated) {
			logger.debug("The user exists");
			return true;
		}
		else {
			logger.debug("The user doesn't exist!");
			return false;
		}
	}

}
