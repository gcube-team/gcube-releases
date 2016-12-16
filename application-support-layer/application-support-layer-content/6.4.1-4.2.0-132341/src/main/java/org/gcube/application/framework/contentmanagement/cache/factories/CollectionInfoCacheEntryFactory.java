package org.gcube.application.framework.contentmanagement.cache.factories;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.gcube.application.framework.contentmanagement.model.CollectionInfo;
import org.gcube.application.framework.contentmanagement.util.XMLTokenReplacer;
import org.gcube.application.framework.core.util.QueryString;
import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.common.resources.gcore.ServiceInstance;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
//import org.gcube.common.core.contexts.GHNContext;
//import org.gcube.common.core.informationsystem.ISException;
//import org.gcube.common.core.informationsystem.client.AtomicCondition;
//import org.gcube.common.core.informationsystem.client.ISClient;
//import org.gcube.common.core.informationsystem.client.ISClient.ISMalformedQueryException;
//import org.gcube.common.core.informationsystem.client.ISClient.ISUnsupportedQueryException;
//import org.gcube.common.core.informationsystem.client.RPDocument;
//import org.gcube.common.core.informationsystem.client.XMLResult;
//import org.gcube.common.core.informationsystem.client.XMLResult.ISResultEvaluationException;
//import org.gcube.common.core.informationsystem.client.queries.GCUBEGenericQuery;
//import org.gcube.common.core.informationsystem.client.queries.GCUBEGenericResourceQuery;
//import org.gcube.common.core.informationsystem.client.queries.WSResourceQuery;
//import org.gcube.common.core.scope.GCUBEScope;
//import org.gcube.common.core.scope.GCUBEScope.MalformedScopeExpressionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.gcube.resources.discovery.icclient.ICFactory.*;


import net.sf.ehcache.constructs.blocking.CacheEntryFactory;

/**
 * 
 * @author Rena
 * @author Nikolas (refactored for FeatherWeight Aug 2013)
 *
 *
 *	This cache serves the collection hierarchy for each collection in the given scope
 */

public class CollectionInfoCacheEntryFactory implements CacheEntryFactory{

//	static ISClient client = null;
	static DiscoveryClient<ServiceInstance> client = null;

	/**
	 * The namespace declaration used in every query sent to the IS
	 */
	private static final String IS_NAMESPACE_DECL = "declare namespace is = 'http://gcube-system.org/namespaces/informationsystem/registry';\n";

	/**
	 * The name of the service that handles the Metadata Views
	 */
	public static final String VIEWMANAGER = "ViewManager";

	public static final String XP_ISUSER = "//child::*[local-name()='property' and child::*[local-name()='name']/text()='isUserCollection']/child::*[local-name()='value']/text()";

	/**
	 * The identifier of the factory resources of the View manager
	 */
	public static final String FACTORYID = "factory";

	public static final String XP_SCHEMANAME = "//child::*[local-name()='property' and child::*[local-name()='name']/text()='schemaName']/child::*[local-name()='value']/text()";
	public static final String XP_LANGUAGE = "//child::*[local-name()='property' and child::*[local-name()='name']/text()='language']/child::*[local-name()='value']/text()";
	public static final String XP_COLNAME = "//child::*[local-name()='property' and child::*[local-name()='name']/text()='name']/child::*[local-name()='value']/text()";
	public static final String XP_COLID = "//child::*[local-name()='id']/text()";
	public static final String XP_RELATEDCOLID = "//child::*[local-name()='collectionID']/text()";
	public static final String XP_SCHEMAURI = "//child::*[local-name()='property' and child::*[local-name()='name']/text()='schemaURI']/child::*[local-name()='value']/text()";
	
	
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(CollectionInfoCacheEntryFactory.class);

	public CollectionInfoCacheEntryFactory() {
		super();
		//Initialize static variable
		if(client == null)
		{
			try {
				client = clientFor(ServiceInstance.class); //client = GHNContext.getImplementation(ISClient.class);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Failed to get implemetation of ISClient", e);
			}
		}
	}


	/**
	 * @param key the name collection id and the scope
	 */
	public Object createEntry(Object key) throws Exception {
		QueryString query = (QueryString) key;

		String colId = query.get("colId");
		String scope = query.get("scope");


		return harvestCollectionProfile(colId, scope);
	}


	public CollectionInfo harvestCollectionProfile(String colId, String scope) {//throws MalformedScopeExpressionException, ISMalformedQueryException, ISUnsupportedQueryException, ISException, ISResultEvaluationException {

		ScopeProvider.instance.set(scope);
		
		// Get the collection name

//		WSResourceQuery wsquery = null;
		SimpleQuery query = null;
		
		List<ServiceInstance> queryResults = null;
		
		try {
//			wsquery = client.getQuery(WSResourceQuery.class);
			query = queryFor(ServiceInstance.class);
		}catch (Exception e){
			logger.error("Exception:", e);
		} 
//			catch (InstantiationException e1) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e1);
//		} catch (IllegalAccessException e2) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e2);
//		}
		
//		wsquery.addAtomicConditions(new AtomicCondition("//child::*[local-name()='ServiceName']", VIEWMANAGER));
		query.addCondition("$resource/Data/gcube:ServiceName/text() eq '" + VIEWMANAGER + "'");
		
		CollectionInfo colInfo = new CollectionInfo();
		colInfo.setId(colId);

//		logger.debug("===SOMetadataCollections===");
//		for (RPDocument rpd : client.execute(wsquery, GCUBEScope.getScope(scope))) {
//		for(ServiceInstance result : client.submit(query)) {
////			logger.trace("Found View with ID: " + rpd.getKey().getValue());
//			logger.trace("Found View with ID: " + result.properties().serviceId());
//			
//			//check if it is a factory resource
////			if(rpd.getKey().getValue().trim().equalsIgnoreCase(FACTORYID)) continue;
//			if(result.key().trim().equalsIgnoreCase(FACTORYID)){
//				logger.trace("Factory");
//				continue;
//			}
//			
//			logger.trace("Not factory");
//
//			//check if it is a user collection
//			String userCollection = "false";
//			
//			result.properties().
//			
//			for (String value : rpd.evaluate(XP_ISUSER)) {
//				userCollection = value.trim();
//			}
//			logger.trace("userCollection: " + userCollection);
//			if(userCollection.equalsIgnoreCase("false"))
//				continue;
//
//			logger.trace("User View");
//
//			String MColID = null;
//			String MColName = null;
//			String DColID = null;
//			String schema = null;
//			String language = null;
//			String schemaURI = null;
//
//			for (String ID : rpd.evaluate(XP_RELATEDCOLID)) {
//				DColID = ID;
//				// check if it belongs to our data collection
//				if (DColID.equals(colId)) {
//					for (String MID : rpd.evaluate(XP_COLID)) {
//						MColID = MID;
//					}
//					for (String Name : rpd.evaluate(XP_COLNAME)) {
//						MColName = Name;
//						//the name must be unresolved because XML chars 
//						//were detected inside collection names
//						MColName = XMLTokenReplacer.XMLUnresolve(MColName);
//					}
//
//					for (String sch : rpd.evaluate(XP_SCHEMANAME)) {
//						schema = sch;
//					}
//
//					for (String l : rpd.evaluate(XP_LANGUAGE)) {
//						language = l;
//					}
//
//					for (String uri : rpd.evaluate(XP_SCHEMAURI)) {
//						schemaURI = uri;
//					}
//				}
//
//				logger.debug(DColID + " " + MColID + "(" + schema + ":" + language + ")");
//
//
//				// TODO: Make a query to IS to take also the name of the collection!
//
//				colInfo.setMetadataCollection(schemaURI, MColID, language);
////				colInfo.getMetadataCollectionIDs().add(MColID);
////				colInfo.getSchemataNames().add(schema);
////				colInfo.getSchemataLanguages().add(language);
////				colInfo.getSchemataURIs().add(schemaURI);
////				colInfo.getSchemataNamespaces().add(schemaURI);			// WRONG
//				// TODO: get also the schemaNamespace and the schemaURI
//
//			}


//		}
		return colInfo;

	}

}
