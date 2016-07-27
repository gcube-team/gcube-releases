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
 * Filename: ISClientRequester.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.resourcemanagement.support.server.gcube;

import static org.gcube.resources.discovery.icclient.ICFactory.client;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.gcube.common.resources.gcore.utils.XPathHelper;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.resourcemanagement.support.client.views.ResourceTypeDecorator;
import org.gcube.resourcemanagement.support.server.gcube.queries.QueryLoader;
import org.gcube.resourcemanagement.support.server.gcube.queries.QueryLocation;
import org.gcube.resourcemanagement.support.server.managers.scope.ScopeManager;
import org.gcube.resourcemanagement.support.server.utils.ServerConsole;
import org.gcube.resourcemanagement.support.shared.plugins.GenericResourcePlugin;
import org.gcube.resourcemanagement.support.shared.plugins.TMPluginFormField;
import org.gcube.resourcemanagement.support.shared.types.Tuple;
import org.gcube.resourcemanagement.support.shared.types.datamodel.CompleteResourceProfile;
import org.gcube.resourcemanagement.support.shared.types.datamodel.ResourceDescriptor;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.Query;
import org.gcube.resources.discovery.client.queries.impl.QueryBox;
import org.gcube.resources.discovery.client.queries.impl.QueryTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * All the requests to the IS are implemented here.
 * @author Massimiliano Assante (ISTI-CNR)
 * @author Daniele Strollo 
 */
public class ISClientRequester {

	private static final ISQueryCache CACHE = new ISQueryCache();
	private static final String LOG_PREFIX = "[ISCLIENT-REQS]";

	public static void emptyCache() {
		CACHE.empty();
	}

	private static final ArrayList<String> getResourceTypes(final CacheManager status, final ScopeBean queryScope) throws Exception {

		List<String> resultz  = null;

		//set the scope
		String currScope = ScopeProvider.instance.get();
		ScopeProvider.instance.set(queryScope.toString());

		Query isQuery = new QueryBox(QueryLoader.getQuery(QueryLocation.GET_TREE_TYPES));
		DiscoveryClient<String> client = client();		 

		// Handles the cache
		ISQueryCacheKeyT cacheKey = new ISQueryCacheKeyT(queryScope.toString(),
				isQuery.expression(),
				"getResourcesTypes");

		if (status.isUsingCache() && CACHE.contains(cacheKey) && CACHE.get(cacheKey) != null) {
			resultz = CACHE.get(cacheKey);
		} else {
			try {

				resultz = client.submit(isQuery);
				if (status.isUsingCache()) {
					CACHE.insert(cacheKey, resultz);
				}
			} catch (Exception e) {
				throw new Exception(e.getMessage());
			}
		}
		if (resultz == null || resultz.size() == 0) {
			return null;
		}

		String type = null;
		ArrayList<String> types = new ArrayList<String>();
		try {			
			for (String elem : resultz) {
				DocumentBuilder docBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Node node = docBuilder.parse(new InputSource(new StringReader(elem))).getDocumentElement();
				XPathHelper helper = new XPathHelper(node);
				type = helper.evaluate("/type/text()").get(0);
				if (type != null && type.trim().length() > 0) {
					types.add(type.trim());
				}
			}
			//****  	CHANGES TO KNOW IF VIEWS AND GCUBECollection are present
			types.remove("MetadataCollection");

			isQuery = new QueryBox("declare namespace vm = 'http://gcube-system.org/namespaces/contentmanagement/viewmanager';"+
					"declare namespace gc = 'http://gcube-system.org/namespaces/common/core/porttypes/GCUBEProvider';"+
					"declare namespace is = 'http://gcube-system.org/namespaces/informationsystem/registry';" +
					"for $data in collection(\"/db/Properties\")//Document, $view in $data/Data where $view/gc:ServiceName/string() eq \"ViewManager\" " +
					" and $view/gc:ServiceClass/string() eq \"ContentManagement\" and count($view//vm:View)>0 return <a>true</a>");

			if (client.submit(isQuery).size() > 0)
				types.add("VIEW");

			isQuery = new QueryBox("declare namespace gc = 'http://gcube-system.org/namespaces/common/core/porttypes/GCUBEProvider';"+
					"declare namespace is = 'http://gcube-system.org/namespaces/informationsystem/registry';" +
					"for $data in collection(\"/db/Profiles/GenericResource\")//Document/Data/is:Profile/Resource where $data/Profile/SecondaryType eq \"GCUBECollection\" " +
					" return <a>true</a>");

			if (client.submit(isQuery).size() > 0)
				types.add("Collection");

		} catch (IndexOutOfBoundsException e) {
			// ignore exception
		}
		ScopeProvider.instance.set(currScope);		
		return types;
	}

	/**
	 * 
	 * @param status
	 * @param queryScope
	 * @param resourceType
	 * @return
	 * @throws Exception
	 */
	private static final ArrayList<String> getResourceSubTypes(final CacheManager status, final ScopeBean queryScope, final String resourceType) throws Exception {

		ArrayList<String> subtypes = new ArrayList<String>();
		if (resourceType.equals("Collection")) {
			subtypes.add("System");
			subtypes.add("User");	
			return subtypes;
		}
		if (resourceType.equals("VIEW")) {
			subtypes.add("Not supported");
			return subtypes;
		}		


		List<String> resultz  = null;

		//set the scope
		String currScope = ScopeProvider.instance.get();
		ScopeProvider.instance.set(queryScope.toString());

		QueryTemplate isQuery = new QueryTemplate(QueryLoader.getQuery(QueryLocation.GET_TREE_SUBTYPES)); 
		DiscoveryClient<String> client = client();		 

		isQuery.addParameter("RES_TYPE", resourceType);
		isQuery.addParameter("SUBTYPE", "<subtype>{$subtype}</subtype>");

		// Handles the cache
		ISQueryCacheKeyT cacheKey = new ISQueryCacheKeyT(queryScope.toString(),
				isQuery.expression(),
				"getResourcesSubTypes");

		if (status.isUsingCache() && CACHE.contains(cacheKey) && CACHE.get(cacheKey) != null) {
			resultz = CACHE.get(cacheKey);
		} else {
			try {
				resultz = client.submit(isQuery);
				if (status.isUsingCache()) {
					CACHE.insert(cacheKey, resultz);
				}
			} catch (Exception e) {
				throw new Exception(e.getMessage());
			}
		}
		if (resultz == null || resultz.size() == 0) 
			return null;

		for (String elem : resultz) {
			try {
				DocumentBuilder docBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Node node = docBuilder.parse(new InputSource(new StringReader(elem))).getDocumentElement();
				XPathHelper helper = new XPathHelper(node);
				String subtype = helper.evaluate("/subtype/text()").get(0);
				if (subtype != null && subtype.trim().length() > 0) {
					subtypes.add(subtype.trim());
				}

			} catch (IndexOutOfBoundsException e) {
				// ignore exception
			}
		}
		ScopeProvider.instance.set(currScope);	
		return subtypes;
	}


	/**
	 * For all the resource in the scope retrieves their
	 * (type, subtype) values.
	 * The result is a list of couples of that form.
	 * @return a list of string tuples (type, subtype)
	 * @throws Exception
	 */
	public static final HashMap<String, ArrayList<String>> getResourcesTree(final CacheManager status, final ScopeBean queryScope) throws Exception {
		HashMap<String, ArrayList<String>> retval = new HashMap<String, ArrayList<String>>();

		// Loads the Resources
		ArrayList<String> types = getResourceTypes(status, queryScope);
		ArrayList<String> subtypes = null;

		for (String type : types) {
			try {
				subtypes = getResourceSubTypes(status, queryScope, type);
				if (subtypes != null && subtypes.size() > 0) {
					retval.put(type, subtypes);
				}
			} catch (Exception e) {
				ServerConsole.error(LOG_PREFIX, e);
			}
		}

		// Loads the WSResources
		// This types is statically handled since it is a particular case of resource.


		List<String> resultz  = null;

		//set the scope
		String currScope = ScopeProvider.instance.get();
		ScopeProvider.instance.set(queryScope.toString());

		Query isQuery = new QueryBox(QueryLoader.getQuery(QueryLocation.GET_WSRES_TYPES));
		DiscoveryClient<String> client = client();		 

		resultz = client.submit(isQuery);
		if (resultz == null || resultz.size() == 0) {
			return retval;
		}
		subtypes = new ArrayList<String>();
		for (String elem : resultz) {
			subtypes.add(elem.toString());
		}
		retval.put("WSResource", subtypes);
		ScopeProvider.instance.set(currScope);
		return retval;
	}

	public static final List<String> getRelatedResources(final CacheManager status,	final String type, final String id,	final ScopeBean queryScope) throws Exception {
		QueryLocation queryPath = null;
		try {
			queryPath = QueryLocation.valueOf("LIST_RELATED_" + type);
		} catch (Exception e) {
			ServerConsole.error(LOG_PREFIX, "Getting the resource query.", e);
			throw new Exception(e.getMessage());
		}

		//set the scope
		String currScope = ScopeProvider.instance.get();
		ScopeProvider.instance.set(queryScope.toString());

		QueryTemplate isQuery = new QueryTemplate(QueryLoader.getQuery(queryPath)); 
		DiscoveryClient<String> client = client();		 

		isQuery.addParameter("RES_ID", id.trim());
		//add the return statement
		isQuery.addParameter("RESOURCE", QueryLoader.getQuery(QueryLocation.valueOf("LIST_RELATED_RETURN_" + type)));

		ISQueryCacheKeyT cacheKey = new ISQueryCacheKeyT(queryScope.toString(),	isQuery.expression(), "getResourceRelated");

		List<String> resultz = null;
		// Handle cache
		if (status.isUsingCache() && CACHE.contains(cacheKey) && CACHE.get(cacheKey) != null) {
			resultz = CACHE.get(cacheKey);
		} else {
			resultz = client.submit(isQuery);
			if (status.isUsingCache()) {
				CACHE.insert(cacheKey, resultz);
			}
		}
		if (resultz == null || resultz.size() == 0) {
			ServerConsole.debug(LOG_PREFIX, "[getResourcesRelated] Got No Results");
			return null;
		}
		// ENDOF Handle cache

		List<String> retval = new ArrayList<String>();

		for (String elem : resultz) {
			// Removes the resources with no ID or empty
			try {
				String toAdd = elem.toString();
				if (toAdd != null) {
					toAdd = toAdd.replace("<Resources>", "");
					toAdd = toAdd.replace("</Resources>", "");
				}
				retval.add(toAdd);
			} catch (Exception e) {
				ServerConsole.debug(LOG_PREFIX, "[getResourcesRelated] found and invalid resource");
			}
		}
		ScopeProvider.instance.set(currScope);
		ServerConsole.trace(LOG_PREFIX, "Retrieved (" + retval.size() + ") ServiceDetails for type: " + type);
		return retval;
	}
	/**
	 * 
	 * @param status
	 * @param queryScope
	 * @param type
	 * @param subType
	 * @return
	 * @throws Exception
	 */
	public static final List<String> getResourcesByType(final CacheManager status, final ScopeBean queryScope,	final String type, final String subType) throws Exception {

		QueryLocation queryPath = null;
		QueryLocation returnPath = null;
		try {
			if (type.equals(ResourceTypeDecorator.WSResource.name())) {
				queryPath = QueryLocation.GET_WSRES_DETAILS_BYSUBTYPE;
				returnPath = QueryLocation.valueOf("RETURN_" + QueryLocation.GET_WSRES_DETAILS_BYSUBTYPE);
			} else {
				queryPath = QueryLocation.valueOf("LIST_" + type);
				returnPath = QueryLocation.valueOf("RETURN_" + type);

			}
		} catch (Exception e) {
			ServerConsole.error(LOG_PREFIX, "Getting the resource query.", e);
			throw new Exception(e.getMessage());
		}

		//set the scope
		String currScope = ScopeProvider.instance.get();
		ScopeProvider.instance.set(queryScope.toString());

		QueryTemplate isQuery = new QueryTemplate(QueryLoader.getQuery(queryPath)); 
		DiscoveryClient<String> client = client();		 

		if (type.equals(ResourceTypeDecorator.WSResource.name())) {
			if (subType != null && subType.length() > 0) {
				isQuery.addParameter("RES_SUBTYPE", subType.trim());
			}
		} else {
			if (subType != null && subType.length() > 0) {
				if (subType.equalsIgnoreCase("User") || subType.equalsIgnoreCase("System")) {
					isQuery.addParameter("RES_SUBTYPE", " and $profiles/Profile/Body/CollectionInfo/user/text() = \"" + ((subType.trim().equals("User")) ? "true" : "false") + "\"");
				}
				else 
					isQuery.addParameter("RES_SUBTYPE", "where $subtype = \"" + subType.trim() + "\"");
			}
		}
		String retParam = type.equals(ResourceTypeDecorator.WSResource.name()) ? "WSRESOURCE" : "RESOURCE";
		//add the return statement
		isQuery.addParameter(retParam, QueryLoader.getQuery(returnPath));

		ISQueryCacheKeyT cacheKey = new ISQueryCacheKeyT(queryScope.toString(),	isQuery.expression(), "getResourcesTypes");

		List<String> resultz = null;

		if (status.isUsingCache() && CACHE.contains(cacheKey) && CACHE.get(cacheKey) != null) {
			resultz = CACHE.get(cacheKey);
		} else {
			resultz = client.submit(isQuery);
			if (status.isUsingCache()) {
				CACHE.insert(cacheKey, resultz);
			}
		}
		if (resultz == null || resultz.size() == 0) {
			return null;
		}
		List<String> retval = new Vector<String>();

		for (String elem : resultz) {
			// Removes the resources with no ID or empty
			DocumentBuilder docBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Node node = docBuilder.parse(new InputSource(new StringReader(elem))).getDocumentElement();
			XPathHelper helper = new XPathHelper(node);
			try {
				if (helper.evaluate("//ID").get(0) != null && helper.evaluate("//ID").get(0).trim().length() > 0) {
					retval.add(elem.toString());
					//ServerConsole.debug("", elem.toString());// Print the result 
				} else {
					ServerConsole.debug(LOG_PREFIX, "*** Found an invalid element with no ID");
				}

			} catch (Exception e) {
				ServerConsole.debug(LOG_PREFIX, "[getResourcesByType] found a resource with empty ID");
			}
		}
		//ServerConsole.trace(LOG_PREFIX, "Retrieved (" + retval.size() + ") ServiceDetails for type: " + type);
		ScopeProvider.instance.set(currScope);
		return retval;
	}
	/**
	 * @param queryScope
	 * @param type
	 * @param subType
	 * @param additionalMaps
	 * @return
	 * @throws Exception
	 */
	public static final List<ResourceDescriptor> getResourceModels(final ScopeBean queryScope,	final String type, final String subType, final List<Tuple<String>> additionalMaps)	throws Exception {

		//set the scope
		String currScope = ScopeProvider.instance.get();
		ScopeProvider.instance.set(queryScope.toString());

		QueryTemplate isQuery = null;
		DiscoveryClient<String> client = client();	

		if (subType != null && subType.trim().length() > 0) {
			isQuery = new QueryTemplate(QueryLoader.getQuery(QueryLocation.GET_RES_DETAILS_BYSUBTYPE)); 
			isQuery.addParameter("RES_TYPE", type.trim());
			isQuery.addParameter("RES_SUBTYPE", subType.trim());
			//add the return statement
			isQuery.addParameter("RESOURCE", QueryLoader.getQuery(QueryLocation.RETURN_GET_RES_DETAILS_BYSUBTYPE));
		} else {
			isQuery = new QueryTemplate(QueryLoader.getQuery(QueryLocation.GET_RES_DETAILS_BYTYPE)); 
			isQuery.addParameter("RES_TYPE", type);
			isQuery.addParameter("RESOURCE", QueryLoader.getQuery(QueryLocation.RETURN_GET_RES_DETAILS_BYTYPE));
		}
		List<String> resultz = client.submit(isQuery);

		List<ResourceDescriptor> retval = new Vector<ResourceDescriptor>();
		ResourceDescriptor toAdd = null;
		for (String elem : resultz) {
			// Removes the resources with no ID or empty
			try {
				DocumentBuilder docBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Node node = docBuilder.parse(new InputSource(new StringReader(elem))).getDocumentElement();
				XPathHelper helper = new XPathHelper(node);

				if (helper.evaluate("//ID").get(0) != null) {
					toAdd = new ResourceDescriptor(
							helper.evaluate("//Type/text()").get(0),
							helper.evaluate("//SubType/text()").get(0),
							helper.evaluate("//ID/text()").get(0),
							helper.evaluate("//Name/text()").get(0));

					// Additional mappings can be defined by the requester.
					// e.g. new Tuple("description", "//Profile/Description/text()");
					if (additionalMaps != null && additionalMaps.size() > 0) {
						for (Tuple<String> map : additionalMaps) {
							try {
								toAdd.addProperty(map.get(0),
										helper.evaluate(map.get(1)).get(0));
							} catch (final Exception e) {
								toAdd.addProperty(map.get(0),
										"");
							}
						}
					}

					retval.add(toAdd);
				}
			} catch (Exception e) {
				ServerConsole.debug(LOG_PREFIX, "[getResourcesByType] found a resource with empty ID");
			}
		}
		//ServerConsole.trace(LOG_PREFIX, "Retrieved (" + retval.size() + ") ServiceDetails for type: " + type);
		ScopeProvider.instance.set(currScope);
		return retval;
	}
	/**
	 * 
	 * @param queryScope
	 * @return
	 * @throws Exception
	 */
	public static final List<String> getWSResources(final ScopeBean queryScope) throws Exception {


		List<String> resultz  = null;

		//set the scope
		String currScope = ScopeProvider.instance.get();
		ScopeProvider.instance.set(queryScope.toString());

		Query isQuery = new QueryBox(QueryLoader.getQuery(QueryLocation.GET_WSRES_DETAILS_BYTYPE));
		DiscoveryClient<String> client = client();		 

		List<String> retval = new Vector<String>();
		resultz = client.submit(isQuery);

		for (String elem : resultz) {
			retval.add(elem.toString());
		}
		//ServerConsole.trace(LOG_PREFIX, "Retrieved (" + retval.size() + ") ServiceDetails for type: " + ResourceTypeDecorator.WSResource.name());
		ScopeProvider.instance.set(currScope);
		return retval;
	}
	/**
	 * 
	 * @param xmlFilePath
	 * @param queryScope
	 * @param resType
	 * @param resID
	 * @return
	 * @throws Exception
	 */
	public static final CompleteResourceProfile getResourceByID(String xmlFilePath, ScopeBean queryScope, String resType, String resID) throws Exception {

		//set the scope
		String currScope = ScopeProvider.instance.get();
		ScopeProvider.instance.set(queryScope.toString());

		QueryTemplate isQuery = null;
		DiscoveryClient<String> client = client();	


		if (resType == null) {
			isQuery = new QueryTemplate(QueryLoader.getQuery(QueryLocation.GET_RESOURCE_BYID)); 
			isQuery.addParameter("RES_ID", resID);
		} else if (resType.equalsIgnoreCase(ResourceTypeDecorator.WSResource.name())) {
			isQuery = new QueryTemplate(QueryLoader.getQuery(QueryLocation.GET_WSRESOURCE_BYID)); 
			isQuery.addParameter("RES_ID", resID);
		} else {
			isQuery = new QueryTemplate(QueryLoader.getQuery(QueryLocation.GET_RESOURCE_BYID)); 
			isQuery.addParameter("RES_ID", resID);
			isQuery.addParameter("RES_TYPE", resType);			
		}
		List<String> results = client.submit(isQuery);


		//ServerConsole.trace(LOG_PREFIX, "Retrieved (" + retval.size() + ") Resource for ID: " + resID);

		if (results != null && results.size() > 0) {
			String type = null;
			if (resType == null) {
				try {
					DocumentBuilder docBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();
					Node node = docBuilder.parse(new InputSource(new StringReader(results.get(0)))).getDocumentElement();
					XPathHelper helper = new XPathHelper(node);
					type = helper.evaluate("/Resource/Type/text()").get(0);
				} catch (Exception e) {
					ServerConsole.error(LOG_PREFIX, e);
				}
			} else {
				type = resType;
			}
			String xmlRepresentation = results.get(0).toString();
			String htmlRepresentation = XML2HTML(xmlRepresentation, xmlFilePath);
			ScopeProvider.instance.set(currScope);
			return new CompleteResourceProfile(resID, ResourceTypeDecorator.valueOf(type), getResourceName(type, resID, results.get(0)), xmlRepresentation, htmlRepresentation);
		}
		ScopeProvider.instance.set(currScope);
		return null;
	}

	public static Map<String, GenericResourcePlugin> getGenericResourcePlugins(final ScopeBean scope) throws Exception {
		//set the scope
		ScopeProvider.instance.set(scope.toString());

		Query isQuery = new QueryBox(QueryLoader.getQuery(QueryLocation.GET_GENERIC_RESOURCE_PLUGINS));
		DiscoveryClient<String> client = client();		 
		List<String> resultz= client.submit(isQuery);

		Map<String, GenericResourcePlugin> retval = new HashMap<String, GenericResourcePlugin>();
		gonext: for (String plugin : resultz) {

			try {
				DocumentBuilder docBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Node node = docBuilder.parse(new InputSource(new StringReader(plugin))).getDocumentElement();
				XPathHelper helper = new XPathHelper(node);
				for (String entry : helper.evaluate("/CMPlugins/Plugin/Entry")) {

					Document doc = ScopeManager.getDocumentGivenXML(entry);
					String name = doc.getElementsByTagName("name").item(0).getFirstChild().getNodeValue();
					ServerConsole.trace("[LOAD-PLUGIN] found: *** name " + name);
					String pluginType = doc.getElementsByTagName("Type").item(0).getFirstChild().getNodeValue();
					ServerConsole.trace("[LOAD-PLUGIN] found: *** type " + pluginType);
					String description = doc.getElementsByTagName("description").item(0).getFirstChild().getNodeValue();
					ServerConsole.trace("[LOAD-PLUGIN] found: *** description " + description);
					String namespace = null;
					try {
						namespace =  doc.getElementsByTagName("namespace").item(0).getFirstChild().getNodeValue();
						ServerConsole.debug("[LOAD-PLUGIN] found: *** namespace " + namespace);
					} catch (Exception e) {
						ServerConsole.warn("[LOAD-PLUGIN] namespace not found");
					}

					GenericResourcePlugin toAdd = new GenericResourcePlugin(name, namespace, description, pluginType);

					NodeList params = doc.getElementsByTagName("param");
					for (int i = 0; i < params.getLength(); i++) {

						NodeList paramTree = params.item(i).getChildNodes();
						String paramName = null;
						String paramDefinition = null;
						for (int j = 0; j < paramTree.getLength(); j++) {
							if (paramTree.item(j).getNodeName().equals("param-name")) {
								paramName = paramTree.item(j).getFirstChild().getNodeValue();
							}

							if (paramTree.item(j).getNodeName().equals("param-definition")) {
								paramDefinition = paramTree.item(j).getFirstChild().getNodeValue();
							}
						}

						ServerConsole.trace("[LOAD-PLUGIN] found: param " + paramName);

						GenericResourcePlugin.Field paramField = new GenericResourcePlugin.Field(paramName, GenericResourcePlugin.FieldType.string);
						if (paramDefinition != null) {
							StringTokenizer parser = new StringTokenizer(paramDefinition, ";");
							while (parser.hasMoreTokens()) {
								try {
									String currElem = parser.nextToken();
									String key = currElem.substring(0, currElem.indexOf("="));
									String value = currElem.substring(currElem.indexOf("=") + 1, currElem.length()).trim();
									if (key.equals("type")) {
										paramField.setType(GenericResourcePlugin.FieldType.valueOf(value));
									}
									if (key.equals("opt")) {
										paramField.setIsRequired(!Boolean.parseBoolean(value));
									}
									if (key.equals("label")) {
										if (value.startsWith("'")) {
											value = value.substring(1, value.length());
										}
										if (value.endsWith("'")) {
											value = value.substring(0, value.length() - 1);
										}
										paramField.setLabel(value);
									}
									if (key.equals("default")) {
										if (value.startsWith("'")) {
											value = value.substring(1, value.length());
										}
										if (value.endsWith("'")) {
											value = value.substring(0, value.length() - 1);
										}
										paramField.setDefaultValue(value);
									}
								} catch (Exception e) {
									// parsing error - not well formed string
								}
							}
						}
						toAdd.addParam(paramField);
					}

					retval.put(name + "::" + pluginType, toAdd);
				}
			} catch (RuntimeException e) {
				continue gonext;
			}


		}

		return retval;
	}
	/**
	 * get the plugins for tree manager
	 * @param scope
	 * @return a map containing the plugin name as key and a List of formfield
	 * @throws Exception
	 */
	public static HashMap<String, ArrayList<TMPluginFormField>> getGenericResourceTreeManagerPlugins(final ScopeBean scope) throws Exception {
		
		ScopeProvider.instance.set(scope.toString());

		Query isQuery = new QueryBox(QueryLoader.getQuery(QueryLocation.GET_GENERIC_RESOURCE_TREE_MANAGER_PLUGINS));
		DiscoveryClient<String> client = client();		 
		List<String> resultz= client.submit(isQuery);

		HashMap<String, ArrayList<TMPluginFormField>> retval = new HashMap<String, ArrayList<TMPluginFormField>>();
		gonext: for (String plugin : resultz) {
			try {				
				DocumentBuilder docBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Node node = docBuilder.parse(new InputSource(new StringReader(plugin))).getDocumentElement();
				XPathHelper helper = new XPathHelper(node);
				for (String entry : helper.evaluate("/TMPlugins/Plugin/Entry")) {
					String requestName = null;
					boolean foundRequest = false;
					Document doc = ScopeManager.getDocumentGivenXML(entry);
					String name = doc.getElementsByTagName("name").item(0).getFirstChild().getNodeValue();
					ServerConsole.trace("[LOAD-TMPLUGIN] found: *** name " + name);
					String pluginType = doc.getElementsByTagName("Type").item(0).getFirstChild().getNodeValue();
					ServerConsole.trace("[LOAD-TMPLUGIN] found: *** type " + pluginType);
					String description = doc.getElementsByTagName("description").item(0).getFirstChild().getNodeValue();
					ServerConsole.trace("[LOAD-TMPLUGIN] found: *** description " + description);
					String namespace = null;
					try {
						namespace =  doc.getElementsByTagName("namespace").item(0).getFirstChild().getNodeValue();
						ServerConsole.trace("[LOAD-TMPLUGIN] found: *** namespace " + namespace);
					} catch (Exception e) {
						ServerConsole.warn("[LOAD-TMPLUGIN] namespace not found");
					}

					NodeList params = doc.getElementsByTagName("param");
					ArrayList<TMPluginFormField> formFields = new ArrayList<TMPluginFormField>();

					for (int i = 0; i < params.getLength(); i++) {

						NodeList paramTree = params.item(i).getChildNodes();
						String paramName = null;
						String xmlToParse = null;
						boolean foundSample = false;
						for (int j = 0; j < paramTree.getLength(); j++) {							
							if (paramTree.item(j).getNodeName().equals("param-name")) {
								paramName = paramTree.item(j).getFirstChild().getNodeValue();

								if (paramName.compareTo("requestSample") == 0) {
									foundSample = true;
									foundRequest = true;
								}							
							}
							if (paramTree.item(j).getNodeName().equals("param-definition") && foundSample) {
								xmlToParse = paramTree.item(j).getFirstChild().getNodeValue();
								xmlToParse = xmlToParse.replaceAll("&lt;", "<");
								xmlToParse = xmlToParse.replaceAll("&gt;", "<");
								foundSample = false;		

								requestName = getRequestName(xmlToParse);

								formFields = getPluginFormFromXml(xmlToParse);
							}
						}
						//						if (params.getLength()>1)
						retval.put(name + ":" + requestName, formFields);						

					}
					if (foundRequest == false){				
						retval.put(name, formFields);
					}

				}
			} catch (RuntimeException e) {
				continue gonext;
			}
		}
		return retval;
	}


	/**
	 * get Request Name
	 * 
	 * 
	 * @param xmlToParse
	 * @return the list 
	 */
	private static String getRequestName(String xmlToParse) {
		Document doc = ScopeManager.getDocumentGivenXML(xmlToParse);

		return doc.getDocumentElement().getNodeName();

	}

	/**
	 * parses the following and return the list to generate the form automatically
	 * 
	 * sample
	 * <speciesRequest>
	 * 	<name>Parachela collection</name>
	 * 	<description>Parachela collection from Itis</description>
	 * 	<scientificNames repeatable="true">Parachela</scientificNames>
	 * 	<datasources repeatable="true">ITIS</datasources>
	 * 	<strictMatch>true</strictMatch>
	 * 	<refreshPeriod>5</refreshPeriod>
	 * 	<timeUnit>MINUTES</timeUnit>
	 * </speciesRequest>
	 * 
	 * @param xmlToParse
	 * @return the list 
	 */
	private static ArrayList<TMPluginFormField> getPluginFormFromXml(String xmlToParse) {
		ArrayList<TMPluginFormField> toReturn = new ArrayList<TMPluginFormField>();
		Document doc = ScopeManager.getDocumentGivenXML(xmlToParse);
		//		Node root = doc.getElementsByTagName("request").item(0);

		Node root = doc.getDocumentElement();
		NodeList children = root.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			String label = children.item(i).getNodeName();
			String defaultValue =  children.item(i).getFirstChild().getNodeValue();
			boolean repeatable = false;
			boolean required = false;
			if (children.item(i).hasAttributes()) {
				NamedNodeMap attributes = children.item(i).getAttributes();
				if (children.item(i).getAttributes().getNamedItem("repeatable") != null)
					repeatable = attributes.getNamedItem("repeatable").getNodeValue().equalsIgnoreCase("true");	
				if (children.item(i).getAttributes().getNamedItem("required") != null)
					required = attributes.getNamedItem("required").getNodeValue().equalsIgnoreCase("true");	
			}
			toReturn.add(new TMPluginFormField(label, defaultValue, required, repeatable));
		}
		return toReturn;
	}

	/**
	 * From the ID of a resource retrieves its name. Notice that resource name
	 * is retrieved according to their type.
	 * @param type the type of the resource
	 * @param ID the identifier of the resource
	 * @param node the XML node from which retrieve the information
	 * @return
	 * @throws IOException 
	 * @throws SAXException 
	 */
	private static String getResourceName(String type, String ID, String node) throws Exception {
		DocumentBuilder docBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Node xnode = docBuilder.parse(new InputSource(new StringReader(node))).getDocumentElement();
		XPathHelper helper = new XPathHelper(xnode);

		if (type.equalsIgnoreCase(ResourceTypeDecorator.GHN.name())) {
			try {
				return helper.evaluate("/Resource/Profile/GHNDescription/Name/text()").get(0);
			} catch (Exception e) {
				return ID;
			}
		}
		if (type.equalsIgnoreCase(ResourceTypeDecorator.Collection.name())) {
			try {
				return helper.evaluate("/Resource/Profile/Name/text()").get(0);
			} catch (Exception e) {
				return ID;
			}
		}
		if (type.equalsIgnoreCase(ResourceTypeDecorator.Service.name())) {
			try {
				return helper.evaluate("/Resource/Profile/Name/text()").get(0);
			} catch (Exception e) {
				return ID;
			}
		}
		if (type.equalsIgnoreCase(ResourceTypeDecorator.RunningInstance.name())) {
			try {
				return helper.evaluate("/Resource/Profile/ServiceName/text()").get(0);
			} catch (Exception e) {
				return ID;
			}
		}
		if (type.equalsIgnoreCase(ResourceTypeDecorator.VIEW.name())) {
			try {
				return helper.evaluate("/Resource/Profile/Name/text()").get(0);
			} catch (Exception e) {
				return ID;
			}
		}
		if (type.equalsIgnoreCase(ResourceTypeDecorator.RuntimeResource.name())) {
			try {
				return helper.evaluate("/Resource/Profile/Name/text()").get(0);
			} catch (Exception e) {
				return ID;
			}
		}
		if (type.equalsIgnoreCase(ResourceTypeDecorator.GenericResource.name())) {
			try {
				return helper.evaluate("/Resource/Profile/Name/text()").get(0);
			} catch (Exception e) {
				return ID;
			}
		}
		if (type.equalsIgnoreCase(ResourceTypeDecorator.WSResource.name())) {
			try {
				return helper.evaluate("/Document/Data/child::*[local-name()='ServiceName']/text()").get(0);
			} catch (Exception e) {
				return ID;
			}
		}
		return null;
	}
	/**
	 * transform an xml in a readable xml by using HTML
	 * @param xml
	 * @param xslt
	 * @return
	 * @throws Exception
	 */
	public static String XML2HTML(final String xml, final String xslt) throws Exception {
		TransformerFactory tf = TransformerFactory.newInstance();

		InputStream stream = new FileInputStream(xslt);
		BufferedReader in = new BufferedReader(new InputStreamReader(stream));
		StringBuilder retval = new StringBuilder();
		String currLine = null;

		while ((currLine = in.readLine()) != null) {
			// a comment
			if (currLine.trim().length() > 0 && currLine.trim().startsWith("#")) {
				continue;
			}
			if (currLine.trim().length() == 0) { continue; }
			retval.append(currLine + System.getProperty("line.separator"));
		}
		in.close();


		StreamSource source = new StreamSource(new ByteArrayInputStream(retval.toString().getBytes()));
		Templates compiledXSLT = tf.newTemplates(source);
		Transformer t = compiledXSLT.newTransformer();
		t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "true");
		StringWriter w = new StringWriter();
		t.transform(new StreamSource(new StringReader(xml)), new StreamResult(w));
		return w.toString();
	}

}
