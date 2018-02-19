package org.gcube.mongodb.driver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.gcube.content.storage.rest.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ReadPreference;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

/**
 * Create a MongoClient by Multiton pattern way 
 * 
 * @author Roberto Cirillo (ISTI-CNR)
 *
 */
public class MongoClientInstance {
	
	private static final HashMap <String, MongoClientInstance> instances = new HashMap<String, MongoClientInstance>();
	private static final Logger logger = LoggerFactory.getLogger(MongoClientInstance.class);
	private static final HashMap<String, MongoConfiguration> configuration=new HashMap<String, MongoConfiguration>();
	private static final HashMap<String, MongoClient> mongo=new HashMap<String, MongoClient>();
	  
	  private MongoClientInstance(MongoConfiguration configuration){
		  MongoClientInstance.configuration.put(configuration.getDb(), configuration);
		 logger.debug("Try to connect to "+configuration.getServers().get(0));
	     if(((configuration.getPwd() != null) && (configuration.getPwd().length() >0))  && ((configuration.getUser() != null) && (configuration.getUser().length() > 0))){
	    	logger.debug("Mongo configuration with authentication enabled for user: "+configuration.getUser());
			MongoCredential credential = MongoCredential.createCredential(configuration.getUser(), configuration.getDb(), configuration.getPwd().toCharArray());
			logger.debug("Try to connect to "+configuration.getServers().get(0));
		    if(configuration.getOptions() != null){
		    	mongo.put(configuration.getDb(), new MongoClient(configuration.getServers(), Arrays.asList(credential), configuration.getOptions()));
		    }else{
		    	mongo.put(configuration.getDb(), new MongoClient(configuration.getServers(), Arrays.asList(credential), MongoClientOptions.builder().connectionsPerHost(30).connectTimeout(30000).readPreference(ReadPreference.primaryPreferred()).build()));
		    }
		    
	    }else{
	    	logger.debug("Mongo Coonfiguration without authentication");
	    	
	    	if(configuration.getOptions() != null)
	    		mongo.put(configuration.getDb(), new MongoClient(configuration.getServers(),  configuration.getOptions()));
		    else{
		    	mongo.put(configuration.getDb(), new MongoClient(configuration.getServers()));
		    }
	    }
	    logger.debug("mongo instantiated");
	  }
	  
	  public static MongoClientInstance getInstance(MongoConfiguration configuration){
		  MongoClientInstance instance=instances.get(configuration.getDb());	  
		  if (instance == null){
			  logger.debug("Going to create a new MongoInstance object");
			  synchronized(MongoClientInstance.class){
				  if(instance==null){
					  instance = new MongoClientInstance(configuration);
					  instances.put(configuration.getDb(), instance);
				  }
			  }
		  }
	    return instance;
	  }
	  
	  public static MongoConfiguration getConfiguration(String dbName){
	    return configuration.get(dbName);
	  }
	  
	  public MongoDatabase getDB(String databaseName){
		  return getMongoClient(databaseName).getDatabase(databaseName);
	  }
	  
	  public MongoCollection<Document> getCollection(String databaseName, String collectionName){
		  return mongo.get(databaseName).getDatabase(databaseName).getCollection(collectionName);
	  }
	  
	  public MongoClient getMongoClient(String db){
		  logger.debug("find mongo instance for db: "+db);
		  MongoClient mongos=mongo.get(db);
		  if(mongos==null)
			  throw new RuntimeException("Mongo not istantiated correctly");
		  return mongo.get(db);
	  }
 
	  public Document findOne(String db, String collection, String name, Object value){
		BasicDBObject query = new BasicDBObject();
		query.put( name , value);
		Document doc=this.getMongoClient(configuration.get(db).getDb()).getDatabase(configuration.get(db).getDb()).getCollection(collection).find(query).first();
	  	return doc;
	  }
	  
	  public Document findOne(String db, String collection, Document doc){
			Document docId=this.getMongoClient(configuration.get(db).getDb()).getDatabase(configuration.get(db).getDb()).getCollection(collection).find(doc).first();
		  	return docId;
		  }
	  
	  public List<Document> find(String db, String collection, String name, Object value){
			BasicDBObject query = new BasicDBObject();
			query.put( name , value);
			MongoCursor<Document> cursor =this.getMongoClient(configuration.get(db).getDb()).getDatabase(configuration.get(db).getDb()).getCollection(collection).find(query).sort(new BasicDBObject("creationTime", 1)).iterator();
			return fillist( cursor);
		  }
	  
	  public void findPrint(String db,  String collection, String name, Object value){
		  Block<Document> block = new Block<Document>() {
			     @Override
			     public void apply(final Document document) {
			         System.out.println(document.toJson());
			     }
			};
		  BasicDBObject query = new BasicDBObject();
			query.put( name , value);
		  this.getMongoClient(configuration.get(db).getDb()).getDatabase(configuration.get(db).getDb()).getCollection(collection).find(query).forEach(block);
		  
	  }
	  
	  public List<Document> findValuesInRange(String db, String collection, long ts, int range){
		  logger.debug("find documents for the following collection: "+collection+ " with ts: "+ts+" and range: "+range);
		  long lowerBound= ts -range;
		  long upperBound=ts +range;
		  logger.debug("check record between "+lowerBound+ " and "+upperBound);
		  BasicDBObject query = new BasicDBObject();
		  query.put("timestamp",  new BasicDBObject("$gte", lowerBound).append("$lt", upperBound));
		  List <Document> list=null;
		  MongoCursor<Document> cursor =this.getMongoClient(configuration.get(db).getDb()).getDatabase(configuration.get(db).getDb()).getCollection(collection).find(query).sort(new BasicDBObject("timestamp", 1)).iterator();
		  return fillist( cursor);
	  }
	  
//	  public List<Document> findAvg(String db, String collectionName, String groupedBy, String averageField, long min, long max){
//		  Document match = new Document();
//		// set the $match operation  
//		  match.put("timestamp", new BasicDBObject("$gte", min));
//		  match.put("timestamp", new BasicDBObject("$lte", max));
//
//		  
//		// build the $projection operation
//		  DBObject fields = new BasicDBObject("AppId", 1);
//		  fields.put(averageField, 1);
//		  fields.put("_id", 0);
//		  DBObject project = new BasicDBObject("$project", fields );
//
//		 // Now the $group operation
//		  DBObject groupFields = new BasicDBObject( "_id", "$AppID");
//		  groupFields.put("average", new BasicDBObject( "$avg", "$"+averageField));
//		  DBObject group = new BasicDBObject("$group", groupFields);
//
//		  // run aggregation
//		List<? extends Bson> list = (List<? extends Bson>) Arrays.asList(match, project, group);
//		AggregateIterable<Document> output =this.getMongoClient(configuration.get(db).getDb()).getDatabase(configuration.get(db).getDb()).getCollection(collectionName).aggregate(list);
//		  if(output!= null){
//			  List<Document> docs= new ArrayList<Document>();
//			  for (Document dbObject : output)
//			    {
//			        docs.add(dbObject);
//			    }	
//			  return docs;
//		  }
//		  return null;
//	  }
	  
	  public List<Document> findAll(String db, String collection, Document execQuery){
		  MongoCursor<Document> cursor = this.getMongoClient(configuration.get(db).getDb()).getDatabase(configuration.get(db).getDb()).getCollection(collection).find(execQuery).iterator();
		  return fillist( cursor);
	  }

	private List<Document> fillist(MongoCursor<Document> cursor) {
		List<Document> list=new ArrayList<Document>();
		try {
		      while (cursor.hasNext()) {
		          list.add(cursor.next());//.toJson());
		          
		      }
		  } finally {
		      cursor.close();
		  }
		  return list;
	}
	  
	  public List<String> find(String db, String collection, Document execQuery){
		  List<String> list=new ArrayList<String>();
		  MongoCursor<Document> cursor = this.getMongoClient(configuration.get(db).getDb()).getDatabase(configuration.get(db).getDb()).getCollection(collection).find(execQuery).iterator();
		  try {
		      while (cursor.hasNext()) {
		          list.add(cursor.next().toJson());//);
		          
		      }
		  } finally {
		      cursor.close();
		  }
		  return list;
	  }
	  
	  public Document findById(String db,  String collection, String id){
		  BasicDBObject query = new BasicDBObject();
		  query.put( "_id" , new ObjectId(id) );
		  Document doc=this.getMongoClient(configuration.get(db).getDb()).getDatabase(configuration.get(db).getDb()).getCollection(collection).find(query).first();
		  return doc;
	  }
	  
	  public List<Document> find(String db, String collection){
		  logger.debug("find all documents for the following collection: "+collection);
		  List <Document> list=null;
		  Utils.printConfiguration(configuration.get(db));
		  Iterator<Document> it=this.getMongoClient(configuration.get(db).getDb()).getDatabase(configuration.get(db).getDb()).getCollection(collection).find().iterator();
		  while(it.hasNext()){
			  if(list==null)
				  list=new ArrayList<Document>();
			  Document d=(Document)it.next();
			  list.add(d);
		  }
		  return list;
	  }
	  
	  
	  public  Iterator<Document> getIterator(String db, String collection){
		  logger.debug("find all documents for the following collection: "+collection);
		  List <Document> list=null;
		  Utils.printConfiguration(configuration.get(db));
		  Iterator<Document> it=this.getMongoClient(configuration.get(db).getDb()).getDatabase(configuration.get(db).getDb()).getCollection(collection).find().iterator();
		  return it;
	  }
	  
	  
	  public void insertOne(String db, String collection, Document doc){
		  this.getMongoClient(configuration.get(db).getDb()).getDatabase(configuration.get(db).getDb()).getCollection(collection).insertOne(doc);
	  }
	  
	  public void insertOne(String db, String collection, String json ){
		  this.getMongoClient(configuration.get(db).getDb()).getDatabase(configuration.get(db).getDb()).getCollection(collection).insertOne(Document.parse(json));
	  }
	  
	  public void insertMany(String db, String collection, List<Document> docs){
		  this.getMongoClient(configuration.get(db).getDb()).getDatabase(configuration.get(db).getDb()).getCollection(collection).insertMany(docs);
	  }
	  
	  public void update(String db, String collection, String id, String json){
		  logger.debug("updating object with id "+id+" \n the new object is: "+json);
		  BasicDBObject searchQuery = new BasicDBObject();
		  searchQuery.append("_id", new ObjectId(id));
		  Document update=Document.parse(json);
		  this.getMongoClient(configuration.get(db).getDb()).getDatabase(configuration.get(db).getDb()).getCollection(collection).replaceOne(searchQuery, update);
	  }
	  
	  public void update(String db, String collection, String id, Document json){
		  logger.debug("updating object with id "+id+" \n the new object is: "+json);
		  Document filter=findById(db, collection, id);
		  logger.debug("old document found is:"+filter.toString());
		  logger.debug("new document is "+json);
		  this.getMongoClient(configuration.get(db).getDb()).getDatabase(configuration.get(db).getDb()).getCollection(collection).replaceOne(filter, json);
	  }
	  
	  public void deleteOne(String db, String collection, Document doc){
		  this.getMongoClient(configuration.get(db).getDb()).getDatabase(configuration.get(db).getDb()).getCollection(collection).deleteOne(doc);
	  }
	  
	  public void deleteMany(String db, String collection, Document doc){
		  this.getMongoClient(configuration.get(db).getDb()).getDatabase(configuration.get(db).getDb()).getCollection(collection).deleteMany(doc);
	  }
	  
	  public void deleteOne(String db, String collection, String json ){
		  this.getMongoClient(configuration.get(db).getDb()).getDatabase(configuration.get(db).getDb()).getCollection(collection).deleteOne(Document.parse(json));
	  }
	  
	  public void deleteById(String db, String collection, String id ){
		  BasicDBObject query = new BasicDBObject();
		  query.put( "_id" , new ObjectId(id) );
		  this.getMongoClient(configuration.get(db).getDb()).getDatabase(configuration.get(db).getDb()).getCollection(collection).deleteOne(query);
	  }
	  
	  public void deleteMany(String db, String collection, String json ){
		  this.getMongoClient(configuration.get(db).getDb()).getDatabase(configuration.get(db).getDb()).getCollection(collection).deleteMany(Document.parse(json));
	  }
	  
	  public void deleteAll(String db, String collection){
		  this.getMongoClient(configuration.get(db).getDb()).getDatabase(configuration.get(db).getDb()).getCollection(collection).drop();
	  }

	  
	  public List<Document> convertToDocuments(List<String> jsons){
		  List<Document> list= new ArrayList<Document>(jsons.size());
		  for (String json : jsons)
			  list.add(Document.parse(json));
		  return list;
	  }
	  
}
