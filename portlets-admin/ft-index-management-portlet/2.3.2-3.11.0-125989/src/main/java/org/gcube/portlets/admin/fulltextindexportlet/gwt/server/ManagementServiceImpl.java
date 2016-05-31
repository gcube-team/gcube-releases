package org.gcube.portlets.admin.fulltextindexportlet.gwt.server;

import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.rr.ResourceRegistry;
import gr.uoa.di.madgik.rr.ResourceRegistryException;
import gr.uoa.di.madgik.rr.element.data.DataCollection;

import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.admin.fulltextindexportlet.gwt.client.interfaces.ManagementService;
import org.gcube.portlets.admin.fulltextindexportlet.gwt.server.util.GenericResourceManager;
import org.gcube.portlets.admin.fulltextindexportlet.gwt.server.util.XMLUtils;
import org.gcube.portlets.admin.fulltextindexportlet.gwt.shared.CollectionBean;
import org.gcube.portlets.admin.fulltextindexportlet.gwt.shared.FieldBean;
import org.gcube.portlets.admin.fulltextindexportlet.gwt.shared.FullTextIndexTypeBean;
import org.gcube.portlets.admin.fulltextindexportlet.gwt.shared.IndexBean;
import org.gcube.portlets.admin.fulltextindexportlet.gwt.shared.IndexTypeBean;
import org.gcube.portlets.admin.fulltextindexportlet.gwt.shared.MgmtPropertiesBean;
import org.gcube.portlets.admin.fulltextindexportlet.gwt.shared.RunningInstanceBean;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.rest.index.client.IndexClient;
import org.gcube.rest.index.client.exceptions.IndexException;
import org.gcube.rest.index.client.factory.IndexFactoryClient;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.client;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * A RemoteService implementation for the ManagementService following GWT RPC
 * design. Used in order to communicate with Management Resources and for
 * Management related queries towards DIS
 */
public class ManagementServiceImpl extends RemoteServiceServlet implements ManagementService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2262958144498861061L;

	private static final long RSTIMEOUT = 10;
	private static final int NORESULTS = 25;

	private static final String OPEN_SEARCH_TYPE = "opensearch";

	/** Logger */
	private static Logger logger = Logger.getLogger(ManagementServiceImpl.class);

	private Map<String, GenericResource> indexTypeResources;

	/** Class constructor */
	public ManagementServiceImpl() {
		try {
			ResourceRegistry.startBridging();
		} catch (ResourceRegistryException e) {
			logger.error("Error initializing rr bridging", e);
		}
	}

	/**
	 * Returns the current GCUBE scope
	 * @return the scope
	 */
	private String getScope() {
		HttpSession httpSession = this.getThreadLocalRequest().getSession();
		String username = httpSession.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE).toString();
		ASLSession session = SessionManager.getInstance().getASLSession(httpSession.getId(), username);
		return session.getScope();
	}


	public List<RunningInstanceBean> getRunningInstances() {
		ScopeProvider.instance.set(getScope());
		List<RunningInstanceBean> factoryEPRs = new ArrayList<RunningInstanceBean>();
		SimpleQuery query = queryFor(GCoreEndpoint.class);
		query.addCondition("$resource/Profile/ServiceClass/text() eq 'Index'")
		.addCondition("$resource/Profile/ServiceName/text() eq 'FullTextIndexNode'");

		DiscoveryClient<GCoreEndpoint> client = clientFor(GCoreEndpoint.class);
		List<GCoreEndpoint> resources = client.submit(query);
		for (GCoreEndpoint se : resources) {
			if (se != null && se.profile() != null && se.profile().endpointMap() != null) {
				String status = se.profile().deploymentData().status();
				if (!status.equalsIgnoreCase("ready")){
					continue;
				}
				String endpoint = se.profile().endpointMap().get("resteasy-servlet").uri().toString();
				RunningInstanceBean ribean = new RunningInstanceBean();
				ribean.setRunningInstanceEPR(endpoint);
				factoryEPRs.add(ribean);
			}
		}
		return factoryEPRs;
	}

	public List<String> query(String queryString, String indexID) {
		logger.debug("-------->   query");
		logger.debug("Quering using --> indexID and term : " + indexID + " -- " + queryString);
		try {
			IndexClient rClient = new IndexClient.Builder().scope(getScope()).indexID(indexID).build();
			String rsEPR = rClient.query(queryString, null);
			logger.info("FT index Query RSepr -> " + rsEPR);
			return getListFromRS(rsEPR);
		} catch (Exception e) {
			logger.error("Exception while quering index", e);
			return null;
		}			
	}

	public List<CollectionBean> getCollections() {
		logger.debug("--------> START of getCollections() in " + getScope());

		HashMap<String,CollectionBean> beanFromIDMap = new HashMap<String,CollectionBean>();
		String colID, colName;

		try {
			List<DataCollection> collections = DataCollection.getCollectionsOfScope(true, getScope());
			logger.debug("Size of collections is " + collections.size());
			for (DataCollection c : collections) {
				logger.debug("Available collection has ID & name --> " + c.getID() + " - " + c.getName() + " and type --> " + c.getCollectionType());
				if (c.getCollectionType() != null && c.getCollectionType().trim().equals(OPEN_SEARCH_TYPE)) {
					logger.debug("OpenSearch collection. Not adding it to the list");
				}
				else {
					colID = c.getID();
					colName = c.getName();
					logger.debug("Added collection -> " + colName + " (" + colID + ") to nameFromIDMap");
					CollectionBean collection = new CollectionBean();
					collection.setId(colID);
					collection.setName(colName);
					collection.setIsReal(true);
					beanFromIDMap.put(colID, collection);
				}
			}				
		} catch (ResourceRegistryException e) {
			logger.error("Failed to retrieve the collections", e);
			e.printStackTrace();
		}

		CollectionBean fakeCollections = new CollectionBean();
		fakeCollections.setName("Empty Managers");

		SimpleQuery query = queryFor(GenericResource.class);
		query.addCondition("$resource/Profile/SecondaryType/text() eq '" + "IndexResources" + "'");
		DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);
		List<GenericResource> indexResources = client.submit(query);

		XPath xpath = XPathFactory.newInstance().newXPath();

		String idxID = null;

		// For each ws-resource
		for (GenericResource w : indexResources) {
			try {
				Element customProperties = w.profile().body();
				// Get the Index ID
				String indexID = (String) xpath.evaluate("/doc/indexResource/indexID/text()", customProperties, XPathConstants.STRING);
				if (indexID != null) {
					idxID = indexID;
					logger.debug("Index ID --> " + idxID);
				}

				// Get the list of collections
				NodeList list = (NodeList)xpath.evaluate("/doc/indexResource/collections/text()", customProperties, XPathConstants.NODESET);
				if (list != null && list.getLength() > 0) {
					for(int i=0; i < list.getLength(); i++ ) {
						String collectionID = XMLUtils.createStringFromDomTree(list.item(i));
						logger.debug("Found index resource for collection -> " + collectionID + " Going to check if it is real");
						CollectionBean collection = (CollectionBean) beanFromIDMap.get(collectionID);
						if (collection == null) {
							logger.debug("Found fake index with CollectionID = " + collectionID);
						}
						else {
							logger.debug("Found index for CollectionID = " + collectionID);
							if (idxID != null) {
								IndexBean idx = new IndexBean();
								idx.setId(idxID);
								collection.addIndex(idx);
							}
						}
					}
				}
				else {
					IndexBean idx = new IndexBean();
					idx.setId(idxID);
					fakeCollections.addIndex(idx);
				}
			} catch (Exception e) {
				logger.error("Exception while parsing the values of the index resources", e);
			} 
		}

		//add to return list and sort:
		ArrayList<CollectionBean> returnList = new ArrayList<CollectionBean>();
		returnList.addAll(beanFromIDMap.values());
		Collections.sort(returnList);

		returnList.add(fakeCollections);
		for(int i = 0; i < returnList.size(); i++ ){
			((CollectionBean)returnList.get(i)).sort();
		}

		logger.debug("END of getCollections() -------->");
		return returnList;
	}

	public IndexBean[] getIndices(String collectionID) {
		ScopeProvider.instance.set(getScope());

		SimpleQuery query = queryFor(GenericResource.class);
		query.addCondition("$resource/Profile/SecondaryType/text() eq '" + "IndexResources" + "'")
		.addCondition("$resource/Profile/Body/indexResource/collections/text() eq '" + collectionID +  "'");

		DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);
		List<GenericResource> resources = client.submit(query);

		if (resources == null)
			return null;

		XPath xpath = XPathFactory.newInstance().newXPath();

		String indexID = "";
		IndexBean[] returnArray = new IndexBean[resources.size()];
		int i = 0;
		// For each ws-resource
		for (GenericResource w : resources) {
			try {
				Element wsResourceCustomProperties = w.profile().body();
				// Get the Index ID
				indexID = (String) xpath.evaluate("/doc/indexResource/indexID/text()", wsResourceCustomProperties, XPathConstants.STRING);
				if (indexID != null) {
					logger.debug("Index ID --> " + indexID);
				}
				IndexBean indexBean = new IndexBean();
				indexBean.setId(indexID);
				indexBean.setName(indexID + "_name");
				String hostname = (String) xpath.evaluate("/doc/indexResource/hostname/text()", wsResourceCustomProperties, XPathConstants.STRING);
				indexBean.setHost(hostname);
				returnArray[i] = indexBean;
			} catch (XPathExpressionException e) {
				logger.error("Exception while parsing the values of the index resources", e);
			}
			i++;
		}
		return returnArray;
	}

	public List<FullTextIndexTypeBean> getAvailableIndexTypeIDs() {
		ScopeProvider.instance.set(getScope());
		SimpleQuery query = queryFor(GenericResource.class);
		query.addCondition("contains($resource/Profile/Name/string(), 'IndexType_ft')")
		.setResult("$resource/Profile/Name/text()");

		DiscoveryClient<String> client = client();
		List<String> names = client.submit(query);
		List<FullTextIndexTypeBean> ret = new LinkedList<FullTextIndexTypeBean>();
		if (names != null && !names.isEmpty()) {
			for (String name : names) {
				String indexTypeID = name.substring("IndexType_".length());
				FullTextIndexTypeBean ibean = new FullTextIndexTypeBean();
				ibean.setIndexTypeID(indexTypeID);
				ret.add(ibean);
			}
		}
		return ret;
	}

	//TODO check this method with Alex
	public String getIndexTypeID(String indexID) {
		logger.debug("-------->   getIndexTypeID");
		//		ScopeProvider.instance.set(getScope());
		//		try {	
		//			StatefulQuery q = FullTextIndexNodeDSL.getSource().withIndexID(indexID).build();
		//			List<javax.xml.ws.EndpointReference> refs = q.fire();
		//			//Get a proxy
		//			try {
		//				FullTextIndexNodeCLProxyI proxyRandom = FullTextIndexNodeDSL.getFullTextIndexNodeProxyBuilder().at((W3CEndpointReference)refs.get(0)).build();
		//				GetIndexInformationResponse indexInformation = proxyRandom.getIndexInformation();
		//				String idxType = indexInformation.IndexID;
		//				logger.debug("getIndexTypeID -------->");
		//				return idxType;
		//			} catch (FullTextIndexNodeException e) {
		//				//Handle the exception
		//				throw e;
		//			}			
		//		} catch (Exception e) {
		//			logger.error("Exception while trying to get the indexTypeID", e);
		//			return null;
		//		}
		return null;
	}


	public MgmtPropertiesBean getResourceProperties(String indexID) {

		logger.debug("-------->   getResourceProperties");
		ScopeProvider.instance.set(getScope());
		try {
			SimpleQuery query = queryFor(GenericResource.class);
			query.addCondition("$resource/Profile/SecondaryType/text() eq '" + "IndexResources" + "'")
			.addCondition("$resource/Profile/Body/indexResource/indexID/text() eq '" + indexID +  "'");

			DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);
			List<GenericResource> resources = client.submit(query);

			if (resources.size() == 0) {
				throw new Exception("The index with the id [" + indexID + "] was not found in IS");
			}
			GenericResource se = resources.get(0);
			Element wsResourceCustomProperties = se.profile().body();
			XPath xpath = XPathFactory.newInstance().newXPath();
			String host = (String) xpath.evaluate("/doc/indexResource/hostname/text()", wsResourceCustomProperties, XPathConstants.STRING);
			String lastModified = (String) xpath.evaluate("/doc/indexResource/lastUpdated/text()", wsResourceCustomProperties, XPathConstants.STRING);;
			String created = (String) xpath.evaluate("/doc/indexResource/created/text()", wsResourceCustomProperties, XPathConstants.STRING);
			String clusterID = (String) xpath.evaluate("/doc/indexResource/clusterID/text()", wsResourceCustomProperties, XPathConstants.STRING);

			MgmtPropertiesBean properties = new MgmtPropertiesBean();
			properties.setCreated(created);
			properties.setModified(lastModified);
			properties.setClusterID(clusterID);
			properties.setHost(host);

			logger.debug("getResourceProperties -------->");
			return properties;

		} catch (Exception e) {
			logger.error("Exception while trying to read the index resource properties", e);
			return null;
		}
	}

	public String createIndex(String clusterID, String collectionID, String RIEPR) {
		try {
			IndexFactoryClient fclient = new IndexFactoryClient.Builder().scope(getScope()).endpoint(RIEPR).build();
			String resourceID = null;
			if (clusterID != null && !clusterID.isEmpty())
				resourceID = fclient.createResource(clusterID, getScope());
			else
				resourceID = fclient.createResource(null, getScope());
			return resourceID;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Boolean removeIndex(String indexID, String collectionID) {
		logger.debug("Destroying all FullTextIndexManagement resources with the ID: " + indexID + " for the collection with ID --> " + collectionID);
		boolean toReturn = true;
		try {
			IndexClient rClient = new IndexClient.Builder().scope(getScope()).indexID(indexID).build();

			if (collectionID != null && !collectionID.trim().isEmpty()) {
				Set<String> indicesOfCollection = rClient.indicesOfCollection(collectionID);
				Iterator<String> it = indicesOfCollection.iterator();
				while (it.hasNext()) {
					boolean isDeleted = rClient.deleteIndex(it.next());
					if (!isDeleted)
						toReturn = false;
				}
			}
			// This is the case for empty index nodes, where no collection is associated to them
			else {
				rClient.shutdown(false);
				rClient.destroy();
			}
		} catch (IndexException e) {
			logger.error("Exception while trying to remove the index resource for the selected collection", e);
			toReturn = false;
		}
		return toReturn;
	}

	public Map<String, IndexTypeBean> getAllIndexTypes() {
		ScopeProvider.instance.set(getScope());
		List<String> conditions = new LinkedList<String>();
		conditions.add("$resource/Profile/SecondaryType/text() eq 'FullTextIndexType'");

		Map<String, IndexTypeBean> retMap = new HashMap<String, IndexTypeBean>();
		indexTypeResources = new HashMap<String, GenericResource>();
		try {
			for (GenericResource gr : GenericResourceManager.retrieveGenericResource(conditions, getScope())) {
				indexTypeResources.put(gr.id(), gr);
				retMap.put(gr.id(), parseIndexType(gr));
			}
		} catch (Exception e) {
			logger.error("Failed to get the indexTypes of the Index generic resources. An exception was thrown", e);
		}
		return retMap;
	}

	public String saveIndexType(IndexTypeBean idxType) {
		ScopeProvider.instance.set(getScope());
		String resourceID = idxType.getResourceID();
		List<String> conditions = new LinkedList<String>();
		conditions.add("$resource/ID eq '" + resourceID + "'");
		try {
			List<GenericResource> resources = GenericResourceManager.retrieveGenericResource(conditions, getScope());
			if (resources != null && !resources.isEmpty()) {
				GenericResource gr = resources.get(0);
				gr.profile().name(idxType.getIndexTypeName());
				gr.profile().description(idxType.getIndexTypeDesc());
				gr.profile().newBody(createIndexType(idxType.getIndexTypeFields()));

				resourceID = GenericResourceManager.updateGenericResource(gr, getScope());
				indexTypeResources.put(resourceID, gr);
			}
		} catch (Exception e) {
			logger.error("Failed to update the generic resource with ID --> " + resourceID);
		}
		return resourceID;
	}

	private IndexTypeBean parseIndexType(GenericResource idxTypeResource) {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = dbFactory.newDocumentBuilder();
			Document idxTypeDoc = builder.parse(new InputSource(new StringReader(idxTypeResource.profile().bodyAsString())));
			XPath xpath = XPathFactory.newInstance().newXPath();
			Element elFieldList = (Element) xpath.evaluate("//field-list", idxTypeDoc.getDocumentElement(), XPathConstants.NODE);

			List<FieldBean> returnList = new LinkedList<FieldBean>();
			NodeList fieldList = elFieldList.getElementsByTagName("field");

			int fieldListLength = fieldList.getLength();

			for (int i = 0; i < fieldListLength; i++) {
				FieldBean fieldBean = new FieldBean();
				Node field = fieldList.item(i);
				NodeList fieldProperties = field.getChildNodes();
				String fieldName = field.getAttributes().getNamedItem("name").getNodeValue();
				//logger.debug("-----> " + fieldName);
				fieldBean.setName(fieldName);

				int propCount = fieldProperties.getLength();

				for (int j = 0; j<propCount; j++) {
					String propName = null;
					try {
						Node property = fieldProperties.item(j);

						if (property.getNodeType() == Node.ELEMENT_NODE) {
							Node textNode = property.getFirstChild();
							propName = property.getNodeName();

							if (propName.equalsIgnoreCase("index")) {
								fieldBean.setIndex("yes".equalsIgnoreCase(textNode.getNodeValue().trim()));
							} else if (propName.equalsIgnoreCase("store")) {
								fieldBean.setStore("yes".equalsIgnoreCase(textNode.getNodeValue().trim()));
							} else if (propName.equalsIgnoreCase("return")) {
								fieldBean.setReturned("yes".equalsIgnoreCase(textNode.getNodeValue().trim()));
							} else if (propName.equalsIgnoreCase("tokenize")) {
								fieldBean.setTokenize("yes".equalsIgnoreCase(textNode.getNodeValue().trim()));
							} else if (propName.equalsIgnoreCase("sort")) {
								fieldBean.setSort("yes".equalsIgnoreCase(textNode.getNodeValue().trim()));
							} else if (propName.equalsIgnoreCase("boost")) {
								fieldBean.setBoost(textNode.getNodeValue());
							}
						}
					} catch (IllegalArgumentException e) {
					} // illegal property -- ignore
				}
				returnList.add(fieldBean);
			}

			IndexTypeBean itb = new IndexTypeBean();
			itb.setIndexTypeName(idxTypeResource.profile().name());
			itb.setIndexTypeDesc(idxTypeResource.profile().description());
			itb.setResourceID(idxTypeResource.id());
			FieldBean[] fields = new FieldBean[returnList.size()];
			for (int i=0; i<returnList.size(); i++)
				fields[i] = returnList.get(i);
			itb.setIndexTypeFields(fields);
			return itb;
		} catch (Exception e) {
			logger.error("Failed to parse indexType from generic resource with name: " + idxTypeResource.profile().name(), e);
			return null;
		}
	}

	private String createIndexType(FieldBean[] fields) {
		StringBuilder indexType = new StringBuilder();
		indexType.append("<index-type name=\"default\"><field-list sort-xnear-stop-word-threshold=\"2E8\">");

		for (int i = 0; i < fields.length; i++) {
			FieldBean field = (FieldBean) fields[i];
			indexType.append("<field name=\"" + field.getName() + "\">");
			indexType.append("<index>" + (field.getIndex() ? "yes" : "no") + "</index>");
			indexType.append("<store>" + (field.getStore() ? "yes" : "no") + "</store>");
			indexType.append("<return>" + (field.getReturned() ? "yes" : "no") + "</return>");
			indexType.append("<tokenize>" + (field.getTokenize() ? "yes" : "no") + "</tokenize>");
			indexType.append("<sort>no</sort>");
			indexType.append("<boost>" + field.getBoost() + "</boost>");
			indexType.append("</field>");
		}
		indexType.append("</field-list></index-type>");

		return indexType.toString();
	}

	public void deleteIndexType(IndexTypeBean idxType) {
		String resourceID = idxType.getResourceID();
		List<String> conditions = new LinkedList<String>();
		conditions.add("$resource/ID eq '" + resourceID + "'");
		try {
			List<GenericResource> resources = GenericResourceManager.retrieveGenericResource(conditions, getScope());
			if (resources != null && !resources.isEmpty()) {
				GenericResource gr = resources.get(0);
				GenericResourceManager.deleteGenericResource(gr, getScope());
				indexTypeResources.remove(resourceID);
			}
		} catch (Exception e) {
			logger.error("Failed to delete the index generic resource");
		}
	}

	private List<String> getListFromRS(String epr) throws Exception {

		ForwardReader<Record> reader = new ForwardReader<Record>(new URI(epr));
		Record result = null;

		ArrayList<String> returnList = new ArrayList<String>();

		int i = 0;
		while(i < NORESULTS) 
		{
			int counter = 0;
			int maxFail = 5;
			//read the next record
			while (true) {

				try {
					result = reader.get(RSTIMEOUT, TimeUnit.SECONDS);
					break;
				} catch (Exception e) {
					if (counter++ <= maxFail) {
						logger.error("getListFromRS failed for the "
								+ counter
								+ ". time.", e);
					} else {
						logger.error(" getListFromRS giving up. FAILED!",	e);
						throw e;
					}
				}
			}

			//if there is nothing else to read
			if(result == null && (reader.getStatus()==Status.Dispose || (reader.getStatus()==Status.Close && reader.availableRecords()==0))) 
			{
				break;
			}

			if(result != null) {
				StringBuilder builder =  new StringBuilder();
				builder.append("<record>");

				//read the fields
				Field[] fields = result.getFields();
				for(Field f : fields) {
					String fieldName = f.getFieldDefinition().getName();
					String fieldContent = ((StringField)f).getPayload();
					builder.append("<" + fieldName + ">" 
							+ fieldContent + "</" + fieldName + ">");
				}

				builder.append("</record>");

				returnList.add(builder.toString());

				i++;
			}
		}

		try{
			reader.close();
		} catch (Exception e) {
			logger.warn("could not close reader: ", e);
		}

		System.out.println("query    --------> ");
		return returnList;
	}

	public String updateIndex(String indexID, String collectionID, String rsLocator) {
		try {
			IndexClient rClient = new IndexClient.Builder().scope(getScope()).indexID(indexID).build();
			rClient.feedLocator(rsLocator, (collectionID+new Date()).toLowerCase().replaceAll(" ", "_"), true, null);
			return "";
		} catch (IndexException e) {
			logger.debug("Got an exception from index cliend while trying to feed the index", e);
		}
		return null;
	}

}
