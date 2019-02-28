/****************************************************************************
 *  This software is part of the gCube Project.
 *  Site: http://www.gcube-system.org/
 ****************************************************************************
 * The gCube/gCore software is licensed as Free Open Source software
 * conveying to the EUPL (http://ec.europa.eu/idabc/eupl).
 * The software and documentation is provided by its authors/distributors
 * "as is" and no expressed or
 * implied warranty is given for its use, quality or fitness for a
 * particular case.
 ****************************************************************************
 * Filename: ContextManager.java
 ****************************************************************************
 * @author <a href="mailto:massimiliano.assante@isti.cnr.it">Massimiliano Assante</a>
 ***************************************************************************/

package org.gcube.resourcemanagement.support.server.managers.context;

import static org.gcube.common.authorization.client.Constants.authorizationService;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.resourcemanagement.support.server.utils.ServerConsole;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Context and their maps are persisted in this structure.
 * 
 * @author Massimiliano Assante (ISTI-CNR)
 *
 */
public class ContextManager {
	private static final Map<String, ScopeBean> CONTEXTS = new LinkedHashMap<String, ScopeBean>();
	private static String confFile = null;
	private static final String LOG_PREFIX = "[SCOPE-MGR]";
	private static final String GEN_RESOURCE_TYPE = "INFRASTRUCTURE";
	private static final String GEN_RESOURCE_NAME = "VirtualOrganisation";

	public static ScopeBean getContext(final String context) throws Exception {
		synchronized (CONTEXTS) {
			if (getAvailableContexts().containsKey(context)) {
				return getAvailableContexts().get(context);
			}
		}
		ServerConsole.warn(LOG_PREFIX, "Using DEFAULT context manager");
		return new ScopeBean(context);
	}
	/**
	 * Refreshes the list of scopes and associated maps.
	 */
	public static Map<String, ScopeBean> getAvailableContexts()
			throws Exception {
		if (CONTEXTS.size() == 0) {
			update();
		}
		return CONTEXTS;
	}

	public static void clear() {
		CONTEXTS.clear();
	}

	public static void update()	throws Exception {
		if (confFile == null) {
			throw new NullPointerException("the context file has not been defined");
		}		
		LinkedHashMap<String, ScopeBean> toCopy = readContexts();
		for (String key : toCopy.keySet()) {
			CONTEXTS.put(key, toCopy.get(key));
		}
	}
	/**
	 * 
	 * @param confFile
	 * @return
	 * @throws Exception
	 */
	public static LinkedHashMap<String, ScopeBean> readContexts() throws Exception {
		LinkedHashMap<String, ScopeBean>  toReturn = new LinkedHashMap<String, ScopeBean>();
		String scopeXML = readInfraVoFromIS();

		Document scopeDocument = getDocumentGivenXML(scopeXML);
		NodeList voElements = scopeDocument.getElementsByTagName("vo");
		System.out.println("voElements="+ voElements.getLength());
		for (int i = 0; i < voElements.getLength(); i++) {
			NodeList voDetails = voElements.item(i).getChildNodes();
			String voString = voDetails.item(2).getFirstChild().getNodeValue();
			ScopeBean vo = new ScopeBean(voString);

			toReturn.put(vo.toString(), vo);
			try {
				for (String vre : getVREFromVO(vo)) {
					// This operation overrides the vo map
					toReturn.put(vre.toString(), new ScopeBean(vo.toString()+"/"+vre));
				}
			} catch (Exception e) {
				ServerConsole.error("Exception raised while loading VREs for VO : " + vo, e);
			}
		}
		return toReturn;

	}
	/**
	 * query the IS to get the VRE list given a VO
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	protected static List<String> getVREFromVO(final ScopeBean vo) 	throws Exception {
		ServerConsole.info(LOG_PREFIX, "Starting Retrieving VREs for VO : " + vo);
		List<String> toReturn = new ArrayList<String>();

		ScopeProvider.instance.set(vo.toString());	
		SimpleQuery query = queryFor(GenericResource.class);
		query.addCondition("$resource/Profile/SecondaryType/text() eq 'VRE'");

		DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);

		List<GenericResource> gRes = client.submit(query);

		for (GenericResource res : gRes) {
			ServerConsole.info(LOG_PREFIX, "Found: " + res.profile().name());
			toReturn.add(res.profile().name());
		}
		return toReturn;
	}

	/**
	 * Given a context, if it is a VO or an infrastructure, the corresponding
	 * map file is retrieved from the globus location and the contained xml
	 * representation is returned.
	 * @param searchvo
	 * @return
	 * @throws Exception
	 */
	public static String getMapXML(final ScopeBean searchvo) throws Exception {
		if (confFile == null) {
			throw new NullPointerException("the context file has not been defined");
		}
		String scopeXML = readInfraVoFromIS();
		ServerConsole.info(LOG_PREFIX, "Starting retrieving scopes..");
		Document scopeDocument = getDocumentGivenXML(scopeXML);
		NodeList voElements = scopeDocument.getElementsByTagName("vo");
		for (int i = 0; i < voElements.getLength(); i++) {
			NodeList voDetails = voElements.item(i).getChildNodes();
			String voString = voDetails.item(5).getFirstChild().getNodeValue();
			String fileName = voDetails.item(3).getFirstChild().getNodeValue();
			// String voName = voDetails.item(1).getFirstChild().getNodeValue();
			ScopeBean vo = new ScopeBean(voString);

			if (vo.equals(searchvo)) {
				return readInfraVoFromIS();
			}
		}
		ServerConsole.error(LOG_PREFIX, "*** maps for " + searchvo + " not found");
		return null;
	}

	//	private static ServiceMap loadServiceMap(final VO vo, final String fileName) throws Exception {
	//		ServiceMap map = new ServiceMap();
	//		String filePath = System.getenv("GLOBUS_LOCATION") + File.separator + "config" + File.separator + fileName;
	//		ServerConsole.info(LOG_PREFIX, "--- Loading " + vo.getName() + " from: " + filePath);
	//		map.load(new FileReader(filePath));
	//		return map;
	//	}

	private static String readInfraVoFromIS() throws Exception {
		String context = "";
		String token = SecurityTokenProvider.instance.get();
		if (token == null || token.compareTo("") == 0) {
			ServerConsole.warn(LOG_PREFIX, "SecurityTokenProvider returned empty token, trying with ScopeProvider ...");
			context = ScopeProvider.instance.get();
			if (context == null || context.compareTo("") == 0) {
				ServerConsole.error(LOG_PREFIX, "ScopeProvider returned empty context, exiting ...");
				return null;
			}
		} else { //there is a token
			ServerConsole.debug(LOG_PREFIX, "SecurityTokenProvider token found");
			AuthorizationEntry entry = authorizationService().get(token);
			context = entry.getContext();
		}			
		String[] splits = context.split("/");
		if (splits.length < 2)
			throw new IllegalArgumentException("The context found is malformed: " + context);
		String infraScope = "/"+splits[1];
		
		ScopeProvider.instance.set(infraScope);
		
		SimpleQuery query = queryFor(GenericResource.class);
		query.addCondition("$resource/Profile/SecondaryType/text() eq '"+ GEN_RESOURCE_TYPE +"'");
		query.addCondition("$resource/Profile/Name/text() eq '"+ GEN_RESOURCE_NAME +"'");


		DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);
		try {
			List<GenericResource> list = client.submit(query);
			if (list.size() > 1) {
				System.out.println("Too many Generic Resources having name " + GEN_RESOURCE_NAME +" in this context having Type " + GEN_RESOURCE_TYPE);
			}
			else if (list.size() == 0){
				System.out.println("There is no Generic Resources having name " + GEN_RESOURCE_NAME +" in this context having Type " + GEN_RESOURCE_TYPE);
			}
			else {
				for (GenericResource res : list) {
					TransformerFactory transformerFactory = TransformerFactory.newInstance();
		            Transformer transformer = transformerFactory.newTransformer();
		            DOMSource source = new DOMSource(res.profile().body());
		            StreamResult result = new StreamResult(new StringWriter());
		            transformer.transform(source, result);
		            return result.getWriter().toString();
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		//set back the context
		ScopeProvider.instance.set(context);
		return "";
	}

	public static Document getDocumentGivenXML(final String result) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		DocumentBuilder db;
		Document document = null;
		try {
			db = dbf.newDocumentBuilder();
			document = db.parse(new ByteArrayInputStream(result.getBytes()));
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return document;
	}

}
