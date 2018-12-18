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
 * Filename: ScopeManager.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.resourcemanagement.support.server.managers.scope;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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
 * Scopes and their maps are persisted in this structure.
 * 
 * @author Massimiliano Assante (ISTI-CNR)
 * @author Daniele Strollo 
 *
 */
public class ScopeManager {
	private static final Map<String, ScopeBean> SCOPES = new LinkedHashMap<String, ScopeBean>();
	private static String confFile = null;
	private static final String LOG_PREFIX = "[SCOPE-MGR]";

	public static ScopeBean getScope(final String scope) throws Exception {
		synchronized (SCOPES) {
			if (getAvailableScopes().containsKey(scope)) {
				return getAvailableScopes().get(scope);
			}
		}
		ServerConsole.warn(LOG_PREFIX, "Using DEFAULT scope manager");
		return new ScopeBean(scope);
	}

	public static void setScopeConfigFile(final String file) {
		confFile = file;
	}

	/**
	 * Refreshes the list of scopes and associated maps.
	 */

	public static Map<String, ScopeBean> getAvailableScopes()
			throws Exception {
		if (SCOPES.size() == 0) {
			update();
		}
		return SCOPES;
	}

	public static void clear() {
		SCOPES.clear();
	}

	public static void update()	throws Exception {
		if (confFile == null) {
			throw new NullPointerException("the scope file has not been defined");
		}		
		LinkedHashMap<String, ScopeBean> toCopy = readScopes(confFile);
		for (String key : toCopy.keySet()) {
			SCOPES.put(key, toCopy.get(key));
		}
	}
	/**
	 * 
	 * @param confFile
	 * @return
	 * @throws Exception
	 */
	public static LinkedHashMap<String, ScopeBean> readScopes(String confFile) throws Exception {
		if (confFile == null) {
			throw new NullPointerException("the scope file has not been defined");
		}
		LinkedHashMap<String, ScopeBean>  toReturn = new LinkedHashMap<String, ScopeBean>();
		String scopeXML = fileToString(confFile);

		Document scopeDocument = getDocumentGivenXML(scopeXML);
		NodeList voElements = scopeDocument.getElementsByTagName("vo");

		for (int i = 0; i < voElements.getLength(); i++) {
			NodeList voDetails = voElements.item(i).getChildNodes();
			String voString = voDetails.item(5).getFirstChild().getNodeValue();
			// String voName = voDetails.item(1).getFirstChild().getNodeValue();
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
	 * Given a scope, if it is a VO or an infrastructure, the corresponding
	 * map file is retrieved from the globus location and the contained xml
	 * representation is returned.
	 * @param searchvo
	 * @return
	 * @throws Exception
	 */
	public static String getMapXML(final ScopeBean searchvo) throws Exception {
		if (confFile == null) {
			throw new NullPointerException("the scope file has not been defined");
		}
		String scopeXML = fileToString(confFile);
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
				return fileToString(System.getenv("GLOBUS_LOCATION") + File.separator + "config" + File.separator + fileName);
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

	public static String fileToString(final String path) throws IOException {
		BufferedReader filebuf = null;
		String nextStr = null;
		StringBuilder ret = new StringBuilder();

		filebuf = new BufferedReader(new FileReader(path));
		nextStr = filebuf.readLine(); // legge una riga dal file
		while (nextStr != null) {
			ret.append(nextStr);
			nextStr = filebuf.readLine(); // legge la prossima riga
		}
		filebuf.close(); // chiude il file

		return ret.toString();
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
