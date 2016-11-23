package org.gcube.application.framework.contentmanagement.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.gcube.application.framework.contentmanagement.model.CollectionInfo;
import org.gcube.application.framework.core.util.SearchField;
//import org.gcube.common.core.contexts.GHNContext;
//import org.gcube.common.core.informationsystem.ISException;
//import org.gcube.common.core.informationsystem.client.AtomicCondition;
//import org.gcube.common.core.informationsystem.client.ISClient;
//import org.gcube.common.core.informationsystem.client.XMLResult;
//import org.gcube.common.core.informationsystem.client.queries.GCUBEGenericQuery;
//import org.gcube.common.core.informationsystem.client.queries.GCUBEMCollectionQuery;
//import org.gcube.common.core.resources.GCUBEMCollection;
//import org.gcube.common.core.scope.GCUBEScope;
//import org.gcube.common.informationsystem.client.eximpl.ExistQuery;
import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.gcube.resources.discovery.icclient.ICFactory.*;

/**
 * @author Valia Tsagkalidou (NKUA)
 * @author Rena Tsantouli
 */
public class FindInfo {
	
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(FindInfo.class);

	public static final String ALL = "ALL";
	public static final String NAME = "NAME";
	public static final String DESCRIPTION = "DESCRIPTION";
	
	/**
	 * @param colID  --
	 * @param collections  --
	 * @return  --
	 */
	public static CollectionInfo findCollectionInfo(String colID, List<CollectionInfo>[] collections)
	{
		if (collections != null) {
			for(int counter=0; counter < collections.length;counter++)
			{
				for(int c2=0; c2 < collections[counter].size(); c2++)
				{
					logger.info("compare: " + collections[counter].get(c2).getId() + " with: " + colID);
					if(collections[counter].get(c2).getId().equals(colID))
					{
						logger.info("equal!!");
						return collections[counter].get(c2);
					}
				}
			}
		}
		else {
			logger.info("No collections!!");
			return null;
		}
		logger.info("Null!!");
		return null;
	}
	
	/**
	 * @param metadataColID  --
	 * @param collections  --
	 * @return  --
	 */
	public static CollectionInfo findCollectionInfoFromMetadata(String metadataColID, List<CollectionInfo>[] collections)
	{
		for(int counter=0; counter < collections.length;counter++)
		{
			for(int c2=1; c2 < collections[counter].size(); c2++)
			{
				for(int c3=0; c3 < collections[counter].get(c2).getMetadataSize(); c3++)
				{
					if(collections[counter].get(c2).getMetadataID(c3).equals(metadataColID))
					{
						return collections[counter].get(c2);
					}
				}
			}
		}
		return null;
	}
	
	
	
	/**
	 * 
	 * @param MCISID metadata collection id
	 * @param scope gcube scope
	 * @return String oid
	 * @throws Exception  --
	 */
	// TO BE REMOVED WITH NEW CMS
	public static String getMCOIDFromISID(String MCISID, String scope) throws Exception {
	  /*  String queryExpr = ExistQuery.NS + "for $result in collection(\"/db/Profiles/MetadataCollection\")//Document/Data/is:Profile/Resource where ($result/Resource/ID/text() eq '" + MCISID + "' return $result/OID/text()";
	
	    ISClient client = GHNContext.getImplementation(ISClient.class);
	    GCUBEGenericQuery query = client.getQuery(GCUBEGenericQuery.class);
	    query.setExpression(queryExpr);
	    List<XMLResult> result = client.execute(query, scope);
	    return result.get(0).evaluate("/Resource/Profile/OID/text()").get(0); */
		
		ScopeProvider.instance.set(scope);
		DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);
		SimpleQuery query = queryFor(GenericResource.class);
		query.addCondition("$resource/ID/text() eq '" + MCISID + "'");
		logger.info("The query expression is: << "+ query.expression() + " >>");
		List<GenericResource> results = null;
		try{
			results = client.submit(query);
		}
		catch (Exception e) {
			logger.debug(e.toString());
		}
		
//		ISClient client = GHNContext.getImplementation(ISClient.class);
//		GCUBEMCollectionQuery mcolQuery = client.getQuery(GCUBEMCollectionQuery.class);
//		AtomicCondition mcIDAtomicCondition = new AtomicCondition("/ID", MCISID);
//		mcolQuery.addAtomicConditions(mcIDAtomicCondition);
//		logger.info("The query expression is: << "+ mcolQuery.getExpression() + " >>");
//		List<GCUBEMCollection> results = client.execute(mcolQuery,scope);
		if (results != null && results.size() > 0)
			return results.get(0).id();
//			return results.get(0).profile().
		else{
			logger.debug("getMCOIDFromISID() -> Query to IS returned no results !");
			return null;
		}
	}
	
	
	/*public static String getRandomMetadataContentOfView(String metadataViewID, String scope) {
		logger.info("Getting random metadata of view: " + metadataViewID);
		MetadataView mView = new MetadataView(GCUBEScope.getScope(scope));
		mView.setId(metadataViewID);
		List<MetadataView> similars = null;
		try {
			similars = mView.findSimilar();
		} catch (ISException e1) {
			// TODO Auto-generated catch block
			logger.error("Exception:", e1);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			logger.error("Exception:", e1);
		}
		if (similars != null && !similars.isEmpty()) {
			logger.info("Got similars");
			MetadataView mv = similars.get(0);
			try {
				ViewReader vReader = mv.reader();
				
				// TODO: A projection might be needed for specifying the schema URI!
				RemoteIterator<GCubeDocument> metaPayloads = vReader.get(metadata().with(BYTESTREAM));
				if (metaPayloads.hasNext()) {
					//byte[] bArray = metaPayloads.next().bytestream();
					MetadataElements meta = metaPayloads.next().metadata();
					Iterator<GCubeMetadata> iter = meta.iterator();
					while (iter.hasNext()) {
						logger.info("Found metadata!");
						return new String(iter.next().bytestream());
					}
					//return new String(bArray);
				}
				else 
					logger.info("Meta Payloads is EMPTY");
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				logger.error("Exception:", e);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception:", e);
			}
		}
		else 
			logger.info("The similars is NULL! - not View Find");
		return null;
	}*/
	

	
	/**
	 * @param term  --
	 * @param whereToSearch --
	 * @param collections --
	 * @return --
	 */
	public static List<CollectionInfo> searchCollectionInfo(String term, String whereToSearch, List<CollectionInfo>[] collections)
	{
		term = term.trim().toLowerCase();
		if(term.startsWith("*"))
		{
			term = term.substring(1);
		}
		else
		{
			term = "(\\s|\\p{Punct})" + term;
		}
		if(term.endsWith("*"))
		{
			term = term.substring(0,term.length()-1);
		}
		else
		{
			term = term + "(\\s|\\p{Punct})";
		}
		term = term.replaceAll("\\x2A", ".*");
		term = term.replaceAll("\\x3F", ".");
	    Pattern pattern = Pattern.compile(term);
	    
		boolean name = false, descr = false;
		if(whereToSearch.equals(FindInfo.ALL))
		{
			name = true;
			descr = true;
		}
		else if(whereToSearch.equals(FindInfo.NAME))
		{
			name = true;
		}
		else if(whereToSearch.equals(FindInfo.DESCRIPTION))
		{
			descr = true;
		}
		List<CollectionInfo> res = new ArrayList<CollectionInfo>();
		for(int counter=0; counter < collections.length;counter++)
		{
			for(int c2=0; c2 < collections[counter].size(); c2++)
			{
				if(name)
				{
					if(pattern.matcher(" " + collections[counter].get(c2).getName().toLowerCase() + " ").find())
					{
						res.add(collections[counter].get(c2));
						continue;
					}
				}
				if(descr)
				{
					if(pattern.matcher(" " + collections[counter].get(c2).getDescription().toLowerCase() + " ").find())
					{
						res.add(collections[counter].get(c2));
						continue;
					}
				}
			}
		}
		return res;
	}
	
	/**
	 * @param schemaName --
	 * @param collection --
	 * @return --
	 */
	public static int findCollectionSchema(String schemaName, CollectionInfo collection)
	{
		for(int counter =0; counter < collection.getMetadataSize(); counter++)
		{
			if(collection.getSchema(counter).equals(schemaName))
			{
				return counter;
			}
		}
		return -1;
	}
	
	public static int findCollectionSchema(String schemaName, String schemaLanguage, CollectionInfo collection) {
		for (int counter = 0; counter < collection.getMetadataSize(); counter++) {
			if (collection.getSchema(counter).equals(schemaName)) 
			{
				if (collection.getLanguage(counter).equals(schemaLanguage))
					return counter;
			}
		}
		return -1;
	}
	
	/**
	 * @param name --
	 * @param schemaName --
	 * @param SchemaHashMap --
	 * @return --
	 */
	public static int findCriterion(String name, String schemaName, HashMap<String, List<SearchField>> SchemaHashMap)
	{
		List<SearchField> schemaCriteria = SchemaHashMap.get(schemaName);
		for(int counter = 0; counter < schemaCriteria.size(); counter++)
		{// finding details regarding this criterion
			if(schemaCriteria.get(counter).name.equals(name))
			{
				return counter;
			}
		}
		return -1;
	}
	

	/**
	 * @param collections the available collections in a hierarchical structure
	 * @return a HashMap: contains pairs of (content/metadata collection ID, collection name)
	 */
	public static HashMap<String, String> getCollectionNames(List<CollectionInfo>[] collections)
	{		
		HashMap<String, String> collectionMap = new HashMap<String, String>();
		collectionMap = new HashMap<String, String>();
		if(collections == null)
			return collectionMap;
		for(int i=0; i< collections.length; i++)
		{
			for(int k=0; k < collections[i].size(); k++)
			{
				for(int j=0; j< collections[i].get(k).getMetadataSize(); j++)
				{
					collectionMap.put(collections[i].get(k).getMetadataID(j), collections[i].get(k).getName());
				}
				collectionMap.put(collections[i].get(k).getId(), collections[i].get(k).getName());
			}
		}
		return collectionMap;
	}
	
	
}
