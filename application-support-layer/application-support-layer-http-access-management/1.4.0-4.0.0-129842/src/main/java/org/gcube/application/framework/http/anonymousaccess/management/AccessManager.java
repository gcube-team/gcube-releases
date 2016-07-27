package org.gcube.application.framework.http.anonymousaccess.management;

import java.io.IOException;
import java.io.StringReader;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.gcube.application.framework.core.genericresources.model.ISGenericResource;
//import org.gcube.application.framework.core.security.UsersManagementUtils;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.application.framework.core.util.ASLGroupModel;
import org.gcube.application.framework.core.util.GenericResource;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccessManager {
	
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(AccessManager.class);

	protected HashMap<String, String> accessMap ;
	protected static AccessManager accessManager = new AccessManager();
	protected HashMap<String, ArrayList<FunctionAccess>> scopesFunctionsRightsMap;

	protected AccessManager () {
		accessMap = new HashMap<String, String>();
		scopesFunctionsRightsMap = new HashMap<String, ArrayList<FunctionAccess>>();
	}

	public static AccessManager getInstance () {
		return accessManager;
	}

	public HashMap<String, ArrayList<FunctionAccess>> getFunctionsRightsMapForScope(String scope, String externalSessionId) {
		// get the access configuration file for the given scope
		Document oaDoc = getOpenAccessConfiguration(scope, externalSessionId);
		// Parse the document
		if (oaDoc != null) {
			ArrayList<FunctionAccess> faList = new ArrayList<FunctionAccess>();
			NodeList functionsElementList = oaDoc.getElementsByTagName("Functions");
			NodeList functionsElements = functionsElementList.item(0).getChildNodes();
			for (int j = 0; j < functionsElements.getLength(); j++) {
				if (functionsElements.item(j).getNodeType() == Node.ELEMENT_NODE) {
					String functionName = functionsElements.item(j).getNodeName();
					String allowAccess = functionsElements.item(j).getTextContent();
					FunctionAccess fa = new FunctionAccess();
					fa.setFunction(functionName);
					logger.debug("OpenAccess for: " + functionName + " -> " + allowAccess);
					if (allowAccess.equals("true")) {
						fa.allowOpenAccess();
					} else
						fa.restrictOpenAccess();
					faList.add(fa);
				}
			}
			scopesFunctionsRightsMap.put(scope, faList);
		}
		return scopesFunctionsRightsMap;
	}

	public ArrayList<String> getAllScopes() {
		ArrayList<String> allScopes = new ArrayList<String>();
		UsersManagementUtils um = new UsersManagementUtils();
		List<ASLGroupModel> groups = new ArrayList<ASLGroupModel>();
		try {
			groups = um.listGroups();
		} catch (GroupRetrievalFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < groups.size(); i++) {
			if (!um.getRootVO().equals(groups.get(i).getGroupId())) {
				String scope = null;
				scope = um.getScope(groups.get(i).getGroupId());
				logger.debug("Adding scope: " + scope);

				//TODO: remove that later - um library bug
				if (scope.startsWith("/"))
					allScopes.add(scope);
			}
		}
		return allScopes;
	}


	private Document getOpenAccessConfiguration(String scope, String externalSessionId) {
		ASLSession aslSession = SessionManager.getInstance().getASLSession(externalSessionId, "guest.d4science");
		logger.debug("The scope is: " + scope);
		try {
			aslSession.setScope(scope);
		} catch (Exception e) {
			logger.error("An exception was thrown while trying to set the scope to ASL session", e);
			return null;
		}
		GenericResource gr = new GenericResource(aslSession);
		try {
			List<ISGenericResource> genResources = gr.getGenericResourceByName(AccessConstants.openAccessConfiguration);
			if (genResources != null && genResources.size() != 0) {
				DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
				InputSource in = new InputSource(new StringReader(genResources.get(0).getBody()));
				try {
					Document doc = dfactory.newDocumentBuilder().parse(in);
					return doc;
				} catch (SAXException e) {
					logger.error("Exception:", e);
				} catch (IOException e) {
					logger.error("Exception:", e);
				} catch (ParserConfigurationException e) {
					logger.error("Exception:", e);
				}
			} else
				return null;
		} catch (RemoteException e) {
		}
		return null;
	}

}
