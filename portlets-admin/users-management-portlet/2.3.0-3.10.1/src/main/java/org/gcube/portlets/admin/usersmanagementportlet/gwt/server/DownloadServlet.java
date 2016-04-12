package org.gcube.portlets.admin.usersmanagementportlet.gwt.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.UserModel;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class DownloadServlet extends HttpServlet {

	/** */
	private static final long serialVersionUID = -8423345575690165644L;
	
	/** Logger */
	private static Logger logger = Logger.getLogger(DownloadServlet.class);

	/**
	 *  
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try{
			response.setHeader( "Content-Disposition", "attachment; filename=users.csv" );
			response.setContentType("text/csv");
			
			PrintWriter out = response.getWriter();
			//out.write(createXMLWithEmails(request));
			out.write(createCSVFileWithUserInfo(request));
			out.close();
		} catch (Exception e) {
			logger.error("Error while trying to serve the file with the emails", e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Error during data retrieving: "+e.getMessage());
			return;
		}
		return;

	}
	
	/**
	 * Creates a string with an xml representation of the current registered users for the current VO/VRE
	 * For each user his/her username and email are used
	 * 
	 * @param request The http request
	 * @return a String with the XML information
	 * @throws Exception an exception
	 */
	private String createXMLWithEmails(HttpServletRequest request) throws Exception {
		HttpSession httpSession = request.getSession();
		String currentUser = httpSession.getAttribute("username").toString();
		ASLSession session = SessionManager.getInstance().getASLSession(httpSession.getId(), currentUser);
		GroupManager groupM = new LiferayGroupManager();
		UserManager userM = new LiferayUserManager();
		String usersInfo = null;
		HashMap<String,String> usersAndEmails = new HashMap<String, String>();

		List<UserModel> registeredUsers = new ArrayList<UserModel>();
		try {
			registeredUsers = userM.listUsersByGroup(groupM.getGroupId(session.getGroupName()));
		} catch (Exception e) {
			logger.error("Failed to get the users of the current VO. An exception was thrown", e);
			throw new Exception();
		}
		for (UserModel u : registeredUsers) {
			// this is the username
			String user = u.getScreenName();
			String email = u.getEmail();
			usersAndEmails.put(user, email);
		}
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			DOMImplementation impl = builder.getDOMImplementation();
			Document doc = impl.createDocument(null,null,null);
			Element root = doc.createElement("UsersInfo");
			doc.appendChild(root);
			Element users = doc.createElement("RegisteredUsers");
			users.setAttribute("VO-VRE", session.getScopeName().replaceAll("/", " "));
			root.appendChild(users);
			Iterator<String> iter = usersAndEmails.keySet().iterator();
			while(iter.hasNext()) {
				String username = iter.next();
				String email = usersAndEmails.get(username);
				Element userEl = doc.createElement("user");
				users.appendChild(userEl);
				Element usernameEl = doc.createElement("username");
				usernameEl.setTextContent(username);
				Element emailEl = doc.createElement("email");
				emailEl.setTextContent(email);
				userEl.appendChild(usernameEl);
				userEl.appendChild(emailEl);	
			}
			try {
				usersInfo = createStringFromDomTree(doc);
			} catch (TransformerException e) {
				logger.error("Failed to create the XML file");
				throw new Exception("Failed to create the XML file", e);
			}
		} catch (ParserConfigurationException e1) {
			logger.error("Failed to create the XML file");
			e1.printStackTrace();
			throw new Exception("Failed to create the XML file", e1);
		}
		
		return usersInfo;	
	}
	
	private String createCSVFileWithUserInfo(HttpServletRequest request) throws Exception {
		StringWriter stringWriter = new StringWriter();
		BufferedWriter bufferedWriter = new BufferedWriter(stringWriter);
		bufferedWriter.write("Username,Fullname,Email");
		bufferedWriter.newLine();
		
		HttpSession httpSession = request.getSession();
		String currentUser = httpSession.getAttribute("username").toString();
		ASLSession session = SessionManager.getInstance().getASLSession(httpSession.getId(), currentUser);
		GroupManager groupM = new LiferayGroupManager();
		UserManager userM = new LiferayUserManager();

		List<UserModel> registeredUsers = new ArrayList<UserModel>();
		try {
			registeredUsers = userM.listUsersByGroup(groupM.getGroupId(session.getGroupName()));
		} catch (Exception e) {
			logger.error("Failed to get the users of the current VO. An exception was thrown", e);
			throw new Exception();
		}
		for (UserModel u : registeredUsers) {
			// this is the username
			String user = u.getScreenName();
			String email = u.getEmail();
			String fullname = u.getFullname();
			bufferedWriter.write(user+","+fullname+","+email);
			bufferedWriter.newLine();
		}
		bufferedWriter.close();
		return stringWriter.toString();
	}
	
	/**
	 * This method converts a node of a tree to a string representation.
	 * 
	 * @param tree The node of a document that will be transformed
	 * @return A string representation of the node
	 * @throws TransformerException failed to transform the DOMTree to String
	 */
	private String createStringFromDomTree(Node tree) throws TransformerException {
		String nodeString = null;
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer();
		transformer.setOutputProperty("omit-xml-declaration", "yes");
		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult(sw);
		DOMSource source = new DOMSource(tree);
		transformer.transform( source, result );
		nodeString = sw.getBuffer().toString();
		
		return nodeString;
	}
}

