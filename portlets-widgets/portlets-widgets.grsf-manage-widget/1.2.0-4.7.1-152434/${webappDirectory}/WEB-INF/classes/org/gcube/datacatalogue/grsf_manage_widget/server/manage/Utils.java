package org.gcube.datacatalogue.grsf_manage_widget.server.manage;

import static org.gcube.resources.discovery.icclient.ICFactory.client;

import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.gcube.common.resources.gcore.utils.XPathHelper;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datacatalogue.ckanutillibrary.shared.ex.ApplicationProfileNotFoundException;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.Query;
import org.gcube.resources.discovery.client.queries.impl.QueryBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Look up from the IS other information that the widget should show
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class Utils {

	public static final String GENERIC_RESOURCE_NAME_MAP_KEY_NAMESPACES_STOCK = "GRSF Stock";
	public static final String GENERIC_RESOURCE_NAME_MAP_KEY_NAMESPACES_FISHERY = "GRSF Fishery";
	private static final String GENERIC_RESOURCE_NAME = "GRSFManageEntries";
	private static final String GENERIC_RESOURCE_SECONDARY_TYPE = "ApplicationProfile";
	private static final Logger logger = LoggerFactory.getLogger(Utils.class);

	public static Set<String> getLookedUpExtrasKeys() {
		Set<String> lookedUpExtrasKeys = new HashSet<String>();
		String scope = ScopeProvider.instance.get();
		logger.debug("Trying to fetch applicationProfile profile from the infrastructure for " + GENERIC_RESOURCE_NAME + " scope: " +  scope);

		try {
			Query q = new QueryBox("for $profile in collection('/db/Profiles/GenericResource')//Resource " +
					"where $profile/Profile/SecondaryType/string() eq '"+ GENERIC_RESOURCE_SECONDARY_TYPE + "' and  $profile/Profile/Name/string() " +
					" eq '" + GENERIC_RESOURCE_NAME + "'" +
					"return $profile");

			DiscoveryClient<String> client = client();
			List<String> appProfile = client.submit(q);

			if (appProfile == null || appProfile.size() == 0) 
				throw new ApplicationProfileNotFoundException("Your applicationProfile is not registered in the infrastructure");
			else {

				String elem = appProfile.get(0);
				DocumentBuilder docBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Node node = docBuilder.parse(new InputSource(new StringReader(elem))).getDocumentElement();
				XPathHelper helper = new XPathHelper(node);

				List<String> currValue = null;
				currValue = helper.evaluate("/Resource/Profile/Body/text()");

				if (currValue != null && currValue.size() > 0) {
					String body = currValue.get(0);
					String[] splittedSet = body.split(",");
					if(splittedSet != null && splittedSet.length > 0)
						for (String entry : splittedSet) {
							String trimmed = entry.trim();
							if(trimmed.isEmpty())
								continue;
							lookedUpExtrasKeys.add(trimmed);
						}
				} 
			}

			logger.info("Extras entries are " + lookedUpExtrasKeys);
			return lookedUpExtrasKeys;
		} catch (Exception e) {
			logger.error("Error while trying to fetch applicationProfile profile from the infrastructure", e);
			return null;
		} 
	}

	/**
	 * Return a map for converting a key to a namespace:key format by reading a generic resource.
	 * @param httpSession 
	 * @return a map
	 */
	public static Map<String, String> getFieldToFieldNameSpaceMapping(HttpSession httpSession, String resourceName){
		
		// check if this information is available in session
		String sessionKey = ScopeProvider.instance.get() + resourceName;
		if(httpSession.getAttribute(sessionKey) != null)
			return (Map<String, String>) httpSession.getAttribute(sessionKey);
		
		Map<String, String> namespacesMap = new HashMap<String, String>(); // e.g. fishery_identity:Short Title -> Short Title
		try {
			Query q = new QueryBox("for $profile in collection('/db/Profiles/GenericResource')//Resource " +
					"where $profile/Profile/SecondaryType/string() eq '"+ "ApplicationProfile" + "' and  $profile/Profile/Name/string() " +
					" eq '" + resourceName + "'" +
					"return $profile");

			DiscoveryClient<String> client = client();
			List<String> appProfile = client.submit(q);

			if (appProfile == null || appProfile.size() == 0) 
				throw new Exception("Your applicationProfile is not registered in the infrastructure");
			else {

				String elem = appProfile.get(0);
				DocumentBuilder docBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Node node = docBuilder.parse(new InputSource(new StringReader(elem))).getDocumentElement();
				XPathHelper helper = new XPathHelper(node);

				NodeList nodeListKeys = helper.evaluateForNodes("//originalKey");
				NodeList nodeListModifiedKeys = helper.evaluateForNodes("//modifiedKey");
				int sizeKeys = nodeListKeys != null ? nodeListKeys.getLength() : 0;
				int sizeKeysModifed = nodeListModifiedKeys != null ? nodeListModifiedKeys.getLength() : 0;
				if(sizeKeys != sizeKeysModifed)
					throw new Exception("Malformed XML");
				logger.debug("Size is " + sizeKeys);
				for (int i = 0; i < sizeKeys; i++) {
					namespacesMap.put(nodeListModifiedKeys.item(i).getTextContent(), nodeListKeys.item(i).getTextContent());
				}
			}
			logger.debug("Map is " + namespacesMap);
			httpSession.setAttribute(sessionKey, namespacesMap);
			return namespacesMap;
		} catch (Exception e) {
			logger.error("Error while trying to fetch applicationProfile profile from the infrastructure", e);
			return null;
		}
	}

	/**
	 * Replace the extras' keys if needed
	 * @param customFields
	 * @param namespaces
	 * @return
	 */
	public static Map<String, String> replaceFieldsKey(Map<String, String> customFields,
			Map<String, String> namespaces) {

		Map<String, String> toReturn = new HashMap<String, String>();

		Iterator<Entry<String, String>> iterator = customFields.entrySet().iterator();

		while (iterator.hasNext()) {
			Map.Entry<java.lang.String, java.lang.String> entry = (Map.Entry<java.lang.String, java.lang.String>) iterator
					.next();
			if(namespaces.containsKey(entry.getKey()))
				toReturn.put(namespaces.get(entry.getKey()), entry.getValue());
			else
				toReturn.put(entry.getKey(), entry.getValue());
		}

		return toReturn;
	}

}
