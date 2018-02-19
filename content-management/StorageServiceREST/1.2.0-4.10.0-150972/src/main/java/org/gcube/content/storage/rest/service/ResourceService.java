package org.gcube.content.storage.rest.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.gcube.content.storage.rest.bean.Credentials;
import org.gcube.content.storage.rest.bean.Resource;
import org.gcube.content.storage.rest.utils.Costants;
import org.gcube.content.storage.rest.utils.Utils;
import org.gcube.mongodb.access.GCubeClient;
import org.gcube.mongodb.driver.MongoClientInstance;
import org.gcube.mongodb.driver.MongoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.MongoCursor;


/**
 * 
 * @author Roberto Cirillo (ISTI-CNR) 2017
 *
 */
public class ResourceService {

	static HashMap<Integer,Resource> resourceIdMap=getResourceIdMap();
	private String db;
	Credentials credentials;
	private String token;
	MongoClientInstance instance;
	MongoClientInstance sharedInstance;
	Credentials sharedCredentials;
	private static final Logger logger = LoggerFactory.getLogger(ResourceService.class);
	
	
	public ResourceService(String token) {
		this.token=token;
	}
	
	
	public MongoClientInstance loadContext(String token){
		logger.trace("loadContext method");
    	GCubeClient gcube=new GCubeClient(Costants.SERVICE_CLASS_DEFAULT, Costants.SERVICE_NAME_DEFAULT);
    // retrieve serviceEndpoint properties from dbName
    	this.db = gcube.retrieveDBEnabled(Utils.getTokenId(token));
    	credentials= gcube.getCredentials(db, Utils.getTokenId(token));
    	Utils.printCredentials(credentials);
    // set Mongo configuration properties object
    	MongoConfiguration configuration= new MongoConfiguration(credentials.getUser(), credentials.getPwd(), credentials.getDb(), credentials.getServers(), null);
    // get mongo instance	
    	instance=MongoClientInstance.getInstance(configuration);
    	return instance;
	}

	
	public List<String> getAllResources()
	{
		instance=loadContext(token);
		List<Document> resources = instance.find(credentials.getDb(), credentials.getCollection());
		if((resources != null) && (!resources.isEmpty())){
			List <String> jsons=new ArrayList<String>(resources.size());
			for (Document doc : resources){
				jsons.add(doc.toJson());
			}
			return jsons;
		}else return null;
		
	}

	
	public String getResource(String id)
	{
		instance=loadContext(token);
		Document doc=instance.findById(credentials.getDb(), credentials.getCollection(), id);
		return doc.toJson();
	}

	public String getResource(String name, String value)
	{
		instance=loadContext(token);
		Document doc=instance.findOne(credentials.getDb(), credentials.getCollection(), name, value);
		return doc.toJson();
	}

	
/**
 * Get all the value with the a given timestamp enclosed in the given range
 * 
 * Get all the X where:
 * 
 *  (timestamp - value)<= X <= (timestamp + value)
 * 
 * @param timestamp
 * @param range
 * @return 
 */
	public List<String> getRange(long timestamp, int range){
		instance=loadContext(token);
		List<Document> resources = instance.findValuesInRange(credentials.getDb(), credentials.getCollection(), timestamp, range);
		if((resources != null) && (!resources.isEmpty())){
			List <String> jsons=new ArrayList<String>(resources.size());
			for (Document doc : resources){
				jsons.add(doc.toJson());
			}
			return jsons;
		}else return null;

	}
	
	
	
	public String addResource(String resource)
	{
		logger.trace("add new resource ");
		instance=loadContext(token);
		Document doc=Document.parse(resource);
		if(doc.get("_id") == null){
			logger.debug("id present on POST operation");
			ObjectId id = new ObjectId();
			doc.append("_id", id);
			logger.debug("new id generated: "+id);
		}
		instance.insertOne(credentials.getDb(),credentials.getCollection(), doc);
		insertIntoSharedContext(doc);
		return resource;
	}

	public String updateResource(String id, String resource){
		logger.trace("updating resource with id: "+id);
		instance=loadContext(token);
		instance.update(credentials.getDb(),credentials.getCollection(), id, resource);
		updateIntoSharedContext(id, resource);
		return resource;
	}
	
	
	public void deleteResource(String id){
		logger.trace("deleting resource with id: "+id);
		instance=loadContext(token);
		instance.deleteById(credentials.getDb(),credentials.getCollection(), id);
		deleteIntoSharedContext(id);
	}

	public void deleteAllResources(){
		logger.trace("deleting all resources ");
		instance=loadContext(token);
		Iterator<Document>  it= instance.getIterator(credentials.getDb(),credentials.getCollection());
		 while(it.hasNext()){
			 Document doc=it.next();
			 ObjectId id=(ObjectId)doc.get("_id");
			 instance.deleteById(credentials.getDb(),credentials.getCollection(), id.toString());
			 deleteIntoSharedContext(id.toString());
		 }
	}

	
	public static HashMap<Integer, Resource> getResourceIdMap() {
		return resourceIdMap;
	}


	private void insertIntoSharedContext(Document doc) {
		MongoClientInstance instance=loadSharedContext();
		instance.insertOne(sharedCredentials.getDb(),sharedCredentials.getCollection(), doc);

	}
	
	private void updateIntoSharedContext(String id, String resource) {
		MongoClientInstance instance=loadSharedContext();
		instance.update(sharedCredentials.getDb(),sharedCredentials.getCollection(), id, resource);
	}
	
	private void deleteIntoSharedContext(String id) {
		MongoClientInstance instance=loadSharedContext();
		instance.deleteById(sharedCredentials.getDb(),sharedCredentials.getCollection(), id);
	}
	
	private void deleteIntoSharedContext() {
		MongoClientInstance instance=loadSharedContext();
		instance.deleteAll(sharedCredentials.getDb(),sharedCredentials.getCollection());
	}

	private MongoClientInstance loadSharedContext() {
	// only one instantiation	
		if(sharedInstance==null){
			logger.trace("loadSharedContext method");
	    	GCubeClient gcube=new GCubeClient(Costants.SERVICE_CLASS_DEFAULT, Costants.SERVICE_NAME_DEFAULT);
	    // retrieve serviceEndpoint properties from dbName
	    	String dbName = gcube.retrieveDBEnabled(Costants.ALL_IN_DB_NAME);
	    	sharedCredentials= gcube.getCredentials(dbName, Costants.COLLECTION_PROPERTY_NAME);
	    	Utils.printCredentials(sharedCredentials);
	    // set Mongo configuration properties object
	    	MongoConfiguration configuration= new MongoConfiguration(sharedCredentials.getUser(), sharedCredentials.getPwd(), sharedCredentials.getDb(), sharedCredentials.getServers(), null);
	    // get mongo instance	
	    	sharedInstance=MongoClientInstance.getInstance(configuration);
		}
    	return sharedInstance;
	}

}
