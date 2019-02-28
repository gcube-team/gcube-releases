package org.gcube.contentmanager.storageserver.store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class StorageStatusOperationManager {
	
	DBCollection ssCollection;
	Logger logger=LoggerFactory.getLogger(StorageStatusOperationManager.class);
	
	public StorageStatusOperationManager(DBCollection ssCollection){
		setSsCollection(ssCollection);
	}
	
	public void putSSRecord(String consumer, long volume, int count){
		logger.debug("put ss record method");
		BasicDBObject doc = new BasicDBObject("consumer", consumer)
        .append("volume", volume)
        .append("count", count);
		DBCollection collection=getSsCollection();
		logger.info("put ss record ["+ consumer+" "+volume+" "+count+" ] in collection: "+collection);
		collection.insert(doc);
	}

	public StorageStatusObject updateUser(StorageStatusObject ssRecord, String  lastOperation){
		String consumer= ssRecord.getConsumer();
		logger.debug("check counts and volume for user "+consumer);
		StorageStatusObject oldSsr=getSSRecord(consumer);
		if(oldSsr != null){
			logger.debug("user already present");
			int partialCount=oldSsr.getCount();
			logger.info(consumer+" count found on db "+partialCount);
			int count = countCalculation(ssRecord.getCount(), partialCount, lastOperation);
			ssRecord.setCount(count);
			
			long partialVolume=oldSsr.getVolume();
			logger.info(consumer+" volume found on db "+partialVolume);
			long volume = volumeCalculation(ssRecord.getVolume(), partialVolume, lastOperation);
			ssRecord.setVolume(volume);
      	    
			final BasicDBObject query = new BasicDBObject("consumer", consumer);
        // Creating BasicDBObjectBuilder object without arguments
			DBObject documentBuilder = BasicDBObjectBuilder.start().add("volume", volume).add("count", count).get();
       // get the dbobject from builder and Inserting document
			getSsCollection().update(query,new BasicDBObject("$set", documentBuilder), true, false);
		}else{
			logger.debug("user not present on db");
			putSSRecord(consumer, ssRecord.getVolume(), ssRecord.getCount());
		}
		return ssRecord;
	}

	public StorageStatusObject overwriteUser(String consumer, String count, String volume){
		StorageStatusObject oldSsr=getSSRecord(consumer);
		final BasicDBObject query = new BasicDBObject("consumer", consumer);
        // Creating BasicDBObjectBuilder object without arguments
			DBObject documentBuilder = BasicDBObjectBuilder.start()
			.add("volume", volume).add("count", count).get();
       // get the dbobject from builder and Inserting document
			getSsCollection().update(query,new BasicDBObject("$set", documentBuilder), true, false);
			StorageStatusObject newSsr=getSSRecord(consumer);
		return newSsr;
	}
	
	public StorageStatusObject getSSRecord(String consumer){
		BasicDBObject query = new BasicDBObject("consumer", consumer);
		DBCursor cursor=getSsCollection().find(query);
		DBObject obj=null;
		try{
			if(cursor.hasNext()){
				obj=cursor.next();
				
			}
		}finally{
			cursor.close();
		}
		if(obj!=null){
			String cons=null;
			if(obj.containsField("consumer")) cons=(String) obj.get("consumer");
			else logger.error("incomplete record found. consumer field is missing");
			long vol =0;
			if(obj.containsField("volume")) vol=(long) obj.get("volume");
			else logger.error("incomplete record found. volume field is missing");
			int count=0;
			if(obj.containsField("count")) count=(int) obj.get("count");
			else logger.error("incomplete record found. count field is missing");
			String id=(String)obj.get("id");
			return new StorageStatusObject(id, cons, vol, count, obj);
		}else{
			return null;
		}
	}
	
	private long volumeCalculation(long currentVolume, long partialVolume, String operation) {
		logger.info("accounting: operation "+operation+" total Volume "+partialVolume+" current volume "+currentVolume);
		if(operation.equalsIgnoreCase("UPLOAD") || operation.equalsIgnoreCase("COPY") || operation.equalsIgnoreCase("SOFT_COPY")){			
			partialVolume=partialVolume+currentVolume;
		}else if(operation.equalsIgnoreCase("DELETE")){
			partialVolume=partialVolume-currentVolume;
		}
		logger.info("new volume "+partialVolume);
		return partialVolume;
	}

	private int countCalculation(int current, int partial, String operation) {
//		int partial=Integer.parseInt(partialCount);
//		int current=Integer.parseInt(currentCount);
		logger.info("accounting: operation "+operation+" old count "+partial+" current count"+current);
		if(operation.equalsIgnoreCase("UPLOAD")|| operation.equalsIgnoreCase("COPY") || operation.equalsIgnoreCase("SOFT_COPY"))
			partial=partial+current;
		else if(operation.equalsIgnoreCase("DELETE"))
			partial=partial-current;
		logger.info("new count: "+partial);
		return partial;
	}

	public DBCollection getSsCollection() {
		return ssCollection;
	}
	
	public DBCursor getSsCollectionList() {
		return ssCollection.find();
	}

	public void setSsCollection(DBCollection ssCollection) {
		this.ssCollection = ssCollection;
	}
	
	

}
