package org.gcube.informationsystem.resource_checker.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.resources.discovery.icclient.ICFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Retrieve the list of contexts in which resources must be found
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
@SuppressWarnings({"unchecked","rawtypes"})
public class RetrieveContextsList {

	private static final String VO_SCOPES_FILE_PATH = "/META-INF/plugin_resources/VoScopes.xml";
	private static Logger logger = LoggerFactory.getLogger(RetrieveContextsList.class);

	public static final List<String> getContexts() throws ParserConfigurationException, SAXException, IOException{

		List<String> contexts = new ArrayList<String>();

		// load vos from file
		loadVOs(contexts);

		// load vres
		loadVREs(contexts);

		return contexts;

	}

	private static void loadVREs(List<String> voContexts) throws SAXException, IOException, ParserConfigurationException {
		SimpleQuery queryVRE = ICFactory.queryFor(GenericResource.class);		
		queryVRE.addCondition("$resource/Profile/SecondaryType/text() = 'VRE'");		
		queryVRE.setResult("$resource/Profile/Body/Scope/string()");		
		String currentScope = ScopeProvider.instance.get();
		DiscoveryClient vREScopeClient = ICFactory.client();

		List<String> vres = new ArrayList<String>();

		for (String vo: voContexts){			
			ScopeProvider.instance.set(vo);			
			List<String> vresScopes = vREScopeClient.submit(queryVRE);			
			logger.debug("vres found " + vresScopes.size() + " in " + vo);			
			vres.addAll(vresScopes);		
		}

		voContexts.addAll(vres);
		ScopeProvider.instance.set(currentScope);

		logger.info("List of all contexts is " + voContexts);

	}

	public static void loadVOs(List<String> contexts) throws ParserConfigurationException, SAXException, IOException {

		String infrastructureRoot = "/" + ScopeProvider.instance.get().split("/")[1];
		logger.debug("Infrastructure root is " + infrastructureRoot); 
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(RetrieveContextsList.class.getResourceAsStream(VO_SCOPES_FILE_PATH));
		doc.getDocumentElement().normalize();
		NodeList nList = doc.getElementsByTagName("scope");
		for (int i = 0; i < nList.getLength(); i++){

			Node nNode = nList.item(i);
			String context = nNode.getTextContent();
			if(context.startsWith(infrastructureRoot))
				contexts.add(context);

		}

		logger.info("List of VOs is " + contexts);

	}

}
