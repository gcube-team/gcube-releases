package org.gcube.contentmanager.storageserver.store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class FolderStatusOperationManager {

	Logger logger=LoggerFactory.getLogger(FolderStatusOperationManager.class);
	
	DBCollection fsCollection;
	
	public FolderStatusOperationManager(DBCollection fsCollection){
		setFsCollection(fsCollection);
	}
	
	public FolderStatusObject putFSRecord(String folder, long volume, int count, String lastUpdate){
		
		BasicDBObject doc = new BasicDBObject("folder", folder)
        .append("volume", volume)
        .append("count", count)
        .append("lastUpdate", lastUpdate);
		getFsCollection().insert(doc);
		return new FolderStatusObject(folder, volume, count, lastUpdate, null);
	}

	public FolderStatusObject updateFolder(FolderStatusObject fsObject, String  lastOperation){
		if(fsObject!=null){
			String currentFolder=fsObject.getFolder();
			int currentCount=fsObject.getCount();
			long currentVolume=fsObject.getVolume();
			logger.debug("update Folder: "+currentFolder+" with partialVolume "+fsObject.getVolume()+" and count: "+fsObject.getCount());
			FolderStatusObject oldFsr=getFSRecord(currentFolder);
			if(oldFsr != null){
				logger.debug("this folder "+currentFolder+" is already present in the storage");
				int partialCount=oldFsr.getCount();
				int count = countCalculation(fsObject.getCount(), partialCount, lastOperation);
				fsObject.setCount(count);
				
				long partialVolume=oldFsr.getVolume();
				long volume = volumeCalculation(fsObject.getVolume(), partialVolume, lastOperation);
				fsObject.setVolume(volume);
	      	    
				final BasicDBObject query = new BasicDBObject("folder", fsObject.getFolder());
	        // Creating BasicDBObjectBuilder object without arguments
				DBObject documentBuilder = BasicDBObjectBuilder.start()
				.add("volume", volume).add("count", count).add("lastUpdate", fsObject.getLastUpdate()).get();
				
	       // get the dbobject from builder and Inserting document
				getFsCollection().update(query,new BasicDBObject("$set", documentBuilder), true, false);
				
			}else{
				logger.debug("folder "+currentFolder+" not present yet");
				putFSRecord(fsObject.getFolder(), currentVolume, currentCount, fsObject.getLastUpdate());
			}		
			String parentDir=currentFolder.substring(0, currentFolder.lastIndexOf("/"));
			logger.debug("recursive update from parent folder: "+parentDir);
			FolderStatusObject parentObject=new FolderStatusObject(parentDir, currentVolume, 1, fsObject.getLastUpdate(), fsObject.getOriginalFolder());
			if((parentDir !=null) && parentDir.contains("/"))
				parentObject=updateFolder(parentObject, lastOperation);
			return fsObject;
		}else{
			logger.error("invalid invocation update method: record is null");
			return null;
		}
	}

	
	
	public FolderStatusObject getFSRecord(String folder){
		BasicDBObject query = new BasicDBObject("folder", folder);
		DBCursor cursor=getFsCollection().find(query);
		DBObject obj=null;
		try{
			if(cursor.hasNext()){
				obj=cursor.next();
				
			}
		}finally{
			cursor.close();
		}
		if(obj!=null){
			String currentFolder=null;
			if(obj.containsField("folder")) currentFolder=(String) obj.get("folder");
			else logger.error("incomplete record found. folder field is missing");
			long vol =0;
			if(obj.containsField("volume")) vol=(long) obj.get("volume");
			else logger.error("incomplete record found. volume field is missing");
			int count=0;
			if(obj.containsField("count")) count=(int) obj.get("count");
			else logger.error("incomplete record found. count field is missing");
			String lastUpdate=null;
			if(obj.containsField("lastUpdate")) lastUpdate=(String) obj.get("lastUpdate");
			else logger.error("incomplete record found. lastUpdate field is missing");
			String originalFolder=null;
			if(obj.containsField("from")) originalFolder=(String) obj.get("from");
			else logger.debug(" originalFolder field is missing. This is correct if this isn't a move operation");
			String id=(String)obj.get("id");
			return new FolderStatusObject(id, currentFolder, vol, count, lastUpdate, originalFolder, obj);
		}else{
			return null;
		}
	}
	
	private long volumeCalculation(long currentVolume, long partialVolume, String operation) {
		logger.debug("folder accounting: operation "+operation+" total Volume "+partialVolume+" current volume "+currentVolume);
		if(operation.equalsIgnoreCase("UPLOAD") || operation.equalsIgnoreCase("COPY")){			
			partialVolume=partialVolume+currentVolume;
		}else if(operation.equalsIgnoreCase("DELETE")){
			partialVolume=partialVolume-currentVolume;
		}
		logger.debug("new volume "+partialVolume);
		return partialVolume;
	}

	private int countCalculation(int currentCount, int partialCount, String operation) {
		logger.debug("folder accounting operation "+operation+" partial count "+partialCount+" current count"+currentCount);
		if(operation.equalsIgnoreCase("UPLOAD")|| operation.equalsIgnoreCase("COPY"))
			partialCount=partialCount+currentCount;
		else if(operation.equalsIgnoreCase("DELETE"))
			partialCount=partialCount-currentCount;
		logger.debug("new count: "+partialCount);
		return partialCount;
	}

	public DBCollection getFsCollection() {
		return fsCollection;
	}

	public void setFsCollection(DBCollection fsCollection) {
		this.fsCollection = fsCollection;
	}
	
	
	
}
