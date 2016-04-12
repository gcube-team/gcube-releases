package org.gcube.application.framework.http.login;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

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


import org.gcube.application.framework.core.util.ASLGroupModel;
import org.gcube.application.framework.http.anonymousaccess.management.UsersManagementUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet implementation class ListInfrastructureScopes
 */
public class ListInfrastructureScopes extends HttpServlet {
	
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(ListInfrastructureScopes.class);
	
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ListInfrastructureScopes() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Get the session
		HttpSession session = request.getSession(true);
		// Does the session indicate this user already logged in?
		String username = (String)session.getAttribute("logon.isDone");
		
		if (username == null) {	
			// Always show available vres
			username = "guest.d4science";
		}
		
		logger.debug("Username: "+username);
		
		UsersManagementUtils um = new UsersManagementUtils();

		ArrayList<String> userVREs = new ArrayList<String>();
		ArrayList<String> userVOs = new ArrayList<String>();

		List<ASLGroupModel> groupModels = null;
		// change different case for anonymous access - make it the same as the others
		if (!username.equals("guest.d4science")) {
			String userId = null;
			userId = um.getUserId(username);
			logger.debug("userId: "+userId);
			groupModels = um.listGroupsByUser(userId);
		} else {
			groupModels = um.listGroups();
		}

		String fullDetails = request.getParameter("details");

		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = dbfac.newDocumentBuilder();
		} catch (ParserConfigurationException exep) {
			logger.error("Exception:", exep);
		}

		Document doc = docBuilder.newDocument();
		Element root = doc.createElement("VOs-VREs");
		doc.appendChild(root);

		if (fullDetails != null && fullDetails.equals("true")) {


			for (int i = 0; i < groupModels.size(); i++) {
				String groupId = Long.toString(groupModels.get(i).getGroupId());
				String groupName = groupModels.get(i).getGroupName();
				String groupType = null;
				if (um.isVO(groupId)) {
					groupType = "VO";
				}
				else if (um.isRootVO(groupId))
					groupType = "ROOT-VO";
				else {
					groupType = "VRE";
				}

				String groupDescription = groupModels.get(i).getDescription();
				String scope = null;
				scope = um.getScope(groupId);
				String parentId = null;

				parentId = um.getGroupParentId(groupId);
				String scopeParent = null;

				scopeParent = um.getScope(parentId);

				Element itemEl = doc.createElement("Item");
				itemEl.setAttribute("scope", scope);
				Element nameEl = doc.createElement("Name");
				nameEl.setTextContent(groupName);
				Element typeEl = doc.createElement("Type");
				typeEl.setTextContent(groupType);
				Element parentItemEl = doc.createElement("ParentItem");
				parentItemEl.setTextContent(scopeParent);
				Element descriptionEl = doc.createElement("Description");
				descriptionEl.setTextContent(groupDescription);
				itemEl.appendChild(nameEl);
				itemEl.appendChild(typeEl);
				itemEl.appendChild(parentItemEl);
				itemEl.appendChild(descriptionEl);

				root.appendChild(itemEl);
			}
		}

		else {
			for (int i = 0; i < groupModels.size(); i++) {
				String groupId = Long.toString(groupModels.get(i).getGroupId());
				if (um.isVO(groupId) || um.isRootVO(groupId)) {
					String scope = um.getScope(groupId);
					userVOs.add(scope);
				}
				else {
					String scope = um.getScope(groupId);
					userVREs.add(scope);
				}
			}

			for (int i = 0; i < userVREs.size(); i++) {
				Element vreEl = doc.createElement("VRE");
				vreEl.setTextContent(userVREs.get(i));
				root.appendChild(vreEl);
			}

			for (int i = 0; i < userVOs.size(); i++) {
				Element voEl = doc.createElement("VO");
				voEl.setTextContent(userVOs.get(i));
				root.appendChild(voEl);
			}
		}


		response.setContentType("text/xml");
		PrintWriter out = response.getWriter();
		String xmlToStr = "";
		StringWriter writer = new StringWriter();

		try {
			DOMSource domSource = new DOMSource(doc);
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(domSource, result);
		} catch (Exception exep) {
			logger.error("Exception:", exep);
		}

		xmlToStr = writer.toString();
		logger.info(xmlToStr);
		out.write(xmlToStr);
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
