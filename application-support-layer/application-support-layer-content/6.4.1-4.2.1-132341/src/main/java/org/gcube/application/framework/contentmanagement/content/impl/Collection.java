package org.gcube.application.framework.contentmanagement.content.impl;

import gr.uoa.di.madgik.rr.ResourceRegistryException;
import gr.uoa.di.madgik.rr.element.data.DataCollection;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.gcube.application.framework.contentmanagement.util.DocumentInfos;
import org.gcube.application.framework.core.session.ASLSession;
//import org.gcube.common.clients.fw.queries.StatefulQuery;
//import org.gcube.common.core.faults.GCUBEFault;
//import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.resources.gcore.ServiceInstance;
import org.gcube.common.scope.api.ScopeProvider;
//import org.gcube.data.streams.Stream;
//import org.gcube.data.tml.proxies.TReader;
//import org.gcube.data.tml.proxies.TServiceFactory;
//import org.gcube.data.trees.data.Tree;
//import org.gcube.data.trees.patterns.Patterns;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.impl.XQuery;
//import org.gcube.resources.discovery.client.queries.impl.XQuery;
import org.gcube.resources.discovery.icclient.ICFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class Collection {
	
//	static ISClient client;
	
	/**
	 * the D4Science session
	 */
	protected ASLSession session;
	protected String colID;
	
	protected static AtomicInteger cmsId = new AtomicInteger(0);
	protected static AtomicInteger colMSId = new AtomicInteger(0);

	/**
	 * 
	 * @param ses --
	 * @param collectionId --
	 */
	public Collection (ASLSession ses, String collectionId) {
		session = ses;
		colID = collectionId;
	}
	
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(Collection.class);
	
	/**
	 * This method retrieves the ID of a collection with a specific name
	 * 
	 * @param name: the name of the collection
	 * @param scope:  --
	 * @return: the collection's ID
	 */
	public static String getCollectionByName(String name, String scope) {	
//		try {
//			List<org.gcube.contentmanagement.gcubedocumentlibrary.util.Collection> collections = Collections.findByName(ses.getScope(), name);
//			if (collections != null && collections.size() > 0) {
//				if (collections.size() > 1)
//					logger.info("More than one collections detected with the same name. The first one will be used");
//				logger.info("Returning the ID of the collection with name --> " + name + ". The ID is --> " + collections.get(0).getId());
//				return collections.get(0).getId();
//			}
//		} catch (Exception e) {
//			logger.info("An exception was thrown while trying to find the collection with name: " + name);
//			return null;
//		}
//		return null;

		//TODO: replace it since there is a new api for this
		//Changed to comments 7 Apr 2016
//		ScopeProvider.instance.set(scope);
//		StatefulQuery query = TServiceFactory.readSource().withName(name).build();
//		TReader treader = TServiceFactory.reader().matching(query).build();
//		Stream<Tree> treesReader = treader.get(Patterns.tree());
//		
//		if(treesReader.hasNext() == false) {
//			logger.info("Couldn't find collection with name: " + name);
//			return null;
//		}
//		else {
//			Tree t = treesReader.next();
//			
//			String[] str = t.uri().toString().split("/");
//			return str[2];
//		}
		return "";
	}
	
	/**
	 * 
	 * @param collectionName --
	 * @param ses --
	 * @param userCollection --
	 * @return --
	 */
	public static String createCollection (String collectionName, ASLSession ses, Boolean userCollection)  {
		return null;
	}
	
	
	/**
	 * 
	 * @param collectionID --
	 * @param ses --
	 * @return --
	 */
	public static boolean deleteCollection (String collectionID, ASLSession ses) {
		/*try {
			GCubeCollections.deleteGCubeCollection(collectionID, true, ses.getScope());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception:", e);
		}*/
		
		return true;
	}
	
	/**
	 * 
	 * @param content --
	 * @param objectID --
	 * @param mimeType --
	 * @return --
	 */
	public DigitalObject addMember (InputStream content, String objectID, String mimeType) {
		DigitalObject newDO;
		try {
			newDO = DigitalObject.createNewDigitalObject(colID, content, objectID, session, mimeType);
			return newDO;
		}
		catch (IOException e) {
			logger.info("Couldn't create a new instance of DigitalObjectTM!");
			return null;
		}
	}

	/**
	 * 
	 * @param oid --
	 * @param collectionId --
	 * @param ses --
	 */
	public void removeMember (String oid, String collectionId, ASLSession ses) {
		return;
	}
	
	/**
	 * 
	 * @param collectionId --
	 * @param scope --
	 * @return --
	 */
	public String[] getMemberIDs (String collectionId, String scope) {
		/*DocumentReader reader = null;
		try {
			reader = new DocumentReader(colID, session.getScope());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception:", e);
		}
		
		String[] ids = null;
		ArrayList<String> idsList = new ArrayList<String>();
		try {
			RemoteIterator<GCubeDocument> docs = reader.get(document().with(CREATION_TIME));
			while (docs.hasNext()) {
				idsList.add(docs.next().id());
			}
			
			ids = new String[idsList.size()];
			for (int i = 0; i < idsList.size(); i++) {
				ids[i] = idsList.get(i);
			}
			
		} catch (DiscoveryException e) {
			// TODO Auto-generated catch block
			logger.error("Exception:", e);
		} catch (GCUBEException e) {
			// TODO Auto-generated catch block
			logger.error("Exception:", e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception:", e);
		}*/
		
		//TODO: replace it since there is a new api for this
		//Changed to comments 7 Apr 2016
//		ScopeProvider.instance.set(scope);
//		StatefulQuery query = TServiceFactory.readSource().withId(colID).build();
//		TReader treader = TServiceFactory.reader().matching(query).build();
//		Stream<Tree> treesReader = treader.get(Patterns.tree());
//		ArrayList<String> idsList = new ArrayList<String>();
//		while(treesReader.hasNext())
//			idsList.add(treesReader.next().sourceId());
//		return (String[]) idsList.toArray();
		return null;
	}
	
	/**
	 * 
	 * @param scope --
	 * @return --
	 */
	public ArrayList<DocumentInfos> getDocumentInfos(String scope) {
		/*DocumentReader reader = null;
		try {
			reader = new DocumentReader(colID, session.getScope());
		} catch (Exception e) {
			throw new ContentReaderCreationException(e);
		}
		
		ArrayList<DocumentInfos> documentInfos = new ArrayList<DocumentInfos>();
		try {
			RemoteIterator<GCubeDocument> docs = reader.get(document().allexcept(BYTESTREAM));
			while (docs.hasNext()) {
				DocumentInfos docInfo = new DocumentInfos();
				GCubeDocument doc = docs.next();
				docInfo.setDocumentId(doc.id());
				docInfo.setName(doc.name());
				documentInfos.add(docInfo);
			}
			
			
		} catch (DiscoveryException e) {
			throw new DocumentRetrievalException(e);
		} catch (GCUBEException e) {
			throw new DocumentRetrievalException(e);
		} catch (Exception e) {
			throw new DocumentRetrievalException(e);
		}
		return documentInfos;*/
		
		//TODO: replace it since there is a new api for this
				//Changed to comments 7 Apr 2016
//		ScopeProvider.instance.set(scope);
//		StatefulQuery query = TServiceFactory.readSource().withId(colID).build();
//		TReader treader = TServiceFactory.reader().matching(query).build();
//		Stream<Tree> treesReader = treader.get(Patterns.tree());
//		
//		ArrayList<DocumentInfos> documentInfos = new ArrayList<DocumentInfos>();
//		while(treesReader.hasNext()) {
//			DocumentInfos docInfo = new DocumentInfos();
//			Tree tree = treesReader.next();
//			docInfo.setDocumentId(tree.sourceId());
//			documentInfos.add(docInfo);
//		}
//		return documentInfos;
		return new ArrayList<DocumentInfos>();
	}
	
	/**
	 * 
	 * @param collectionID --
	 * @return --
	 */
	public static String getCollectionName(String collectionID) {
		XQuery _query = ICFactory.queryFor(ServiceInstance.class);
		_query.addNamespace("tm",URI.create("http://gcube-system.org/namespaces/data/tm"))
			  .addCondition("$resource/Data/gcube:ServiceClass/text() eq 'DataAccess'")
              .addCondition("$resource/Data/gcube:ServiceName/text() eq 'tree-manager-service'")
              .addCondition("$resource/Data/tm:SourceId/text() eq '" + collectionID + "'")
              .setResult("$resource/Data/tm:Name/text()");
		
		DiscoveryClient<String> client = ICFactory.client();
		Set<String> props = new HashSet<String>(client.submit(_query));
		return props.iterator().next();
	}
	
	/**
	 * 
	 * @param id --
	 * @return --
	 * @throws ResourceRegistryException --
	 */
	public static String getCollectionNameByID(String id) throws ResourceRegistryException {
		DataCollection colData = new DataCollection();
		colData.setID(id);
		colData.load(true);
		String colName = colData.getName();
		return colName;
	}
	/**
	 * 
	 * @param collectionID --
	 * @param session --
	 * @return --
	 */
	public static String getCollectionName(String collectionID, String session) {
		
		ScopeProvider.instance.set(session);
		
		XQuery _query = ICFactory.queryFor(ServiceInstance.class);
		_query.addNamespace("tm",URI.create("http://gcube-system.org/namespaces/data/tm"))
			  .addCondition("$resource/Data/gcube:ServiceClass/text() eq 'DataAccess'")
              .addCondition("$resource/Data/gcube:ServiceName/text() eq 'tree-manager-service'")
              .addCondition("$resource/Data/tm:SourceId/text() eq '" + collectionID + "'")
              .setResult("$resource/Data/tm:Name/text()");
		
		DiscoveryClient<String> client = ICFactory.client();
		Set<String> props = new HashSet<String>(client.submit(_query));
		return props.iterator().next();
	}

}
