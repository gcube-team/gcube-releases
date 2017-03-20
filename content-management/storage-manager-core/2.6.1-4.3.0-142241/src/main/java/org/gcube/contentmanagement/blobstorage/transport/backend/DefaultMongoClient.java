package org.gcube.contentmanagement.blobstorage.transport.backend;


import org.bson.types.ObjectId;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.gcube.contentmanagement.blobstorage.resource.MemoryType;
import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.resource.OperationDefinition;
import org.gcube.contentmanagement.blobstorage.service.impl.ServiceEngine;
import org.gcube.contentmanagement.blobstorage.transport.TransportManager;
import org.gcube.contentmanagement.blobstorage.transport.backend.MongoIO;
import org.gcube.contentmanagement.blobstorage.transport.backend.util.DateUtils;
import org.gcube.contentmanagement.blobstorage.transport.backend.util.MongoOutputStream;
import org.gcube.contentmanagement.blobstorage.resource.StorageObject;
import org.gcube.contentmanagement.blobstorage.resource.OperationDefinition.OPERATION;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;
import com.mongodb.gridfs.GridFSInputFile;

/**
 * MongoDB transport layer
 * @author Roberto Cirillo (ISTI - CNR)
 *
 */
public class DefaultMongoClient extends TransportManager{
	/**
	 * Logger for this class
	 */
	final Logger logger = LoggerFactory.getLogger(DefaultMongoClient.class);
	private MongoClient mongo;
	private MongoIO io;
	private MemoryType memoryType;

	
	public DefaultMongoClient(String[] server, String user, String password, MemoryType memoryType){
		try {
			this.memoryType=memoryType;
			io=new MongoIO(server, user, password, memoryType);//MongoIO.getInstance(server, user, password);
			io.clean();
			DBCollection coll =io.getMetaDataCollection();// io.getDB().getCollection("fs.files");
			coll.createIndex(new BasicDBObject("filename", 1));  // create index on "filename", ascending
			coll.createIndex(new BasicDBObject("dir", 1));  // create index on "filename", ascending
			coll.createIndex(new BasicDBObject("owner", 1));  // create index on "owner", ascending
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void initBackend(String[] server, String user, String pass, MemoryType memoryType) {
		try {
			io=new MongoIO(server, user, pass, memoryType);//MongoIO.getInstance(server, user, password);
			io.clean();
			DBCollection coll =io.getMetaDataCollection();// io.getDB().getCollection("fs.files");
			coll.createIndex(new BasicDBObject("filename", 1));  // create index on "filename", ascending
			coll.createIndex(new BasicDBObject("dir", 1));  // create index on "filename", ascending
			coll.createIndex(new BasicDBObject("owner", 1));  // create index on "owner", ascending
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * @param serverLocation can be a path remote on the cluster or a object id 
	 * @throws IOException 
	 */
	@Override
	public Object get(MyFile resource, String serverLocation, Class<? extends Object> type) throws IOException {
		OperationDefinition op=resource.getOperationDefinition();
		logger.info("MongoClient get method: "+op.toString());
		DB db = getDB(resource);
		GridFS gfs = new GridFS(db);  
		GridFSDBFile f = io.retrieveRemoteObject(serverLocation, true);		
		Object id=null;
		if(f!=null){
			id=f.getId();
			String lock=(String)f.get("lock");
			if((lock==null || lock.isEmpty()) || (isTTLUnlocked(f))){
				if((f.containsField("lock")) && (f.get("lock") != null)){
					f.put("lock", null);
					f.save();
				}
				download(gfs,resource, f, false);
			}else{
				checkTTL(f);
			}
		}else{
			close();
			throw new FileNotFoundException("REMOTE FILE NOT FOUND: WRONG PATH OR WRONG OBJECT ID");
		}
		return id;
	}


/**
 * return the key that permits the object's unlock
 * @throws IOException 
 */	
	@Override
	public String lock(MyFile resource, String serverLocation, Class <? extends Object> type) throws IOException {
		OperationDefinition op=resource.getOperationDefinition();
		if((resource.getLocalPath()!= null) && (!resource.getLocalPath().isEmpty())){
			resource.setOperation(OPERATION.DOWNLOAD);
			get(resource, serverLocation, type);
			resource.setOperation(op);
			close();
			mongo=null;
		}
		logger.info("MongoClient lock method: "+op.toString());
		String key=null;
		if(logger.isDebugEnabled())
			logger.debug("MongoDB - pathServer: "+serverLocation);
		GridFSDBFile f=io.retrieveRemoteObject(serverLocation, true);
		if(f!=null){
	//timestamp is used for compare to ttl of a file lock.		
			String lock=(String)f.get("lock");
			if((lock==null || lock.isEmpty()) || (isTTLUnlocked(f))){
				key=f.getId()+""+System.currentTimeMillis();
				f.put("lock", key);
				f.put("timestamp", System.currentTimeMillis());
				updateCommonFields(f, resource, OPERATION.LOCK);
				f.save();
			}else{
				checkTTL(f);
			}

		}else{
			close();
			throw new FileNotFoundException("REMOTE FILE NOT FOUND: WRONG PATH OR WRONG OBJECT ID");
		}
		return key;
	}

	
	@Override
	public String put(Object resource, String bucket, String key, boolean replace) throws UnknownHostException{
		OperationDefinition op=((MyFile)resource).getOperationDefinition();
		logger.info("MongoClient put method: "+op.toString());
		String dir=((MyFile)resource).getRemoteDir();
		String name=((MyFile)resource).getName();		
		Object id=null;
		ObjectId oldId=null;
	// id of the remote file if present		
		GridFSDBFile fold = io.retrieveRemoteObject(bucket, false);			
		if(fold != null){
	// if a file is present				
			logger.info("a file is already present at: "+bucket);
	// keep old id		
			oldId=(ObjectId) fold.getId();
			logger.info("get old id: "+oldId);
	// remove old file			
			oldId = removeFile(resource, key, replace, oldId, fold);
	//ADDED 03112015			
			if(!replace){
				return oldId.toString();
			}
	// END ADDED		
	    }
	// create new file
		logger.info("create new file "+bucket);
		id = createNewFile(resource, bucket, dir, name, oldId);
		return id.toString();        
	}
	
	
	public String safePut(Object resource, String bucket, String key, boolean replace) throws UnknownHostException{
		OperationDefinition op=((MyFile)resource).getOperationDefinition();
		logger.info("MongoClient put method: "+op.toString());
		String dir=((MyFile)resource).getRemoteDir();
		String name=((MyFile)resource).getName();		
		ObjectId id=null;
		ObjectId oldId=null;
// id of the remote file if present		
		GridFSDBFile fold = io.retrieveRemoteObject(bucket, false);			
		if(fold != null){
// if a file is present				
			logger.info("a file is already present at: "+bucket);
// keep old id		
			oldId=(ObjectId) fold.getId();
			logger.info("get old id: "+oldId);
// create new file		
     		id = createNewFile(resource, bucket, dir, name, null);
// remove old file			
			oldId = removeFile(resource, key, replace, oldId, fold);
//			oldId = removeOldMetadataFile(oldId);
// update the id to the new file
			id=updateId(id, oldId);
			
	    }else{
// create new file		
			id = createNewFile(resource, bucket, dir, name, oldId);
	    }
        return id.toString();
	}

	
	private ObjectId updateId(ObjectId oldId, ObjectId newId) throws UnknownHostException {
		logger.info("retrieve object with id: "+oldId);
		// update chunks		
		updateChunksCollection(oldId, newId);
				// update fs files collection
		replaceObjectIDOnMetaCollection(oldId, newId);

		return newId;
	}


	private void replaceObjectIDOnMetaCollection(ObjectId oldId, ObjectId newId)
			throws UnknownHostException {
		BasicDBObject oldIdQuery= new BasicDBObject();
		oldIdQuery.put("_id", oldId);
		String collectionName= MongoIO.DEFAULT_META_COLLECTION;
		DBCollection dbc=io.getCollection(null, collectionName);
		DBObject obj=io.findCollectionObject(dbc, oldIdQuery);// or multiple objects?
		obj.put("_id", newId);
//		dbc.dropIndex("_id");
		if (!(memoryType== MemoryType.VOLATILE)){
			dbc.remove(oldIdQuery, MongoIO.DEFAULT_WRITE_TYPE);
			dbc.insert(obj, MongoIO.DEFAULT_WRITE_TYPE);
		}else{
			dbc.remove(oldIdQuery);
			dbc.insert(obj);
		}
	}

	private void updateChunksCollection(ObjectId oldId, ObjectId newId)
			throws UnknownHostException {
		DBCollection dbc;
		// update fs.chunks collection		
		logger.info("update chunks collection. Change file_id from "+oldId+" to "+newId);
		BasicDBObject queryOldFileId=new BasicDBObject();
		queryOldFileId.put("files_id", oldId);
		BasicDBObject queryNewFileId=new BasicDBObject();
		queryNewFileId.put("files_id", newId);
		String chunksCollectionName=MongoIO.DEFAULT_CHUNKS_COLLECTION;
		dbc=io.getCollection(null, chunksCollectionName);
		if (!(memoryType== MemoryType.VOLATILE))
			dbc.update(queryOldFileId, queryNewFileId, true, true, MongoIO.DEFAULT_WRITE_TYPE);
		else
			dbc.update(queryOldFileId, queryNewFileId, true, true);
	}

	public void close() {
		io.close();
	}
	
	/**
	 * Unlock the object specified, this method accept the key field for the unlock operation
	 * @throws FileNotFoundException 
	 * @throws UnknownHostException 
	 */
	@Override
	public String unlock(Object resource, String bucket, String key, String key4unlock) throws FileNotFoundException, UnknownHostException{
		String id=null;
		OperationDefinition op=((MyFile)resource).getOperationDefinition();
		logger.info("MongoClient unlock method: "+op.toString());
		if((((MyFile)resource).getLocalPath() !=null) && (!((MyFile)resource).getLocalPath().isEmpty())){
			((MyFile)resource).setOperation(OPERATION.UPLOAD);
			 id=put(resource, bucket, key4unlock, true);
			 close();
			 mongo=null;
			((MyFile)resource).setOperation(op);
		}
		String dir=((MyFile)resource).getRemoteDir();
		String name=((MyFile)resource).getName();
		String path=bucket; 
		if(logger.isDebugEnabled())
			logger.debug("DIR: "+dir+" name: "+name+" fullPath "+path+" bucket: "+bucket);
		GridFSDBFile f=io.retrieveRemoteObject(path, false);
		if(f != null){
			String oldir=(String)f.get("dir");
	        if(logger.isDebugEnabled())
	      	  logger.debug("old dir  found "+oldir);
	        if((oldir.equalsIgnoreCase(((MyFile)resource).getRemoteDir())) || ((MyFile)resource).getRemoteDir()==null){
	         		  String lock=(String)f.get("lock");
	     	  //check if the od file is locked		  
	     	         if((lock !=null) && (!lock.isEmpty())){
	     	        	 String lck=(String)f.get("lock");
	     	        	 if(lck.equalsIgnoreCase(key4unlock)){
	     	        		f.put("lock", null);
	     	        		f.put("timestamp", null);
	     	        		updateCommonFields((GridFSFile)f, (MyFile)resource, OPERATION.UNLOCK);
	     	        		f.save();
	     	        	 }else{
	     	        		 close();
	     	        		 throw new IllegalAccessError("bad key for unlock");
	     	        	 }
	     	         }else{
	     	        	updateCommonFields((GridFSFile)f, (MyFile)resource, OPERATION.UNLOCK);
     	        		f.save();
	     	         }
	        }else{
	        	close();
	        	throw new FileNotFoundException(path);
	        }
	     }else{
	    	close(); 
	       	throw new FileNotFoundException(path);
	     }
		return id;
    }
	
	
	@Override
	public Map<String, StorageObject> getValues(MyFile resource, String bucket, Class<? extends Object> type){
		Map<String, StorageObject> map=null;
		try{
			OperationDefinition op=resource.getOperationDefinition();
			logger.info("MongoClient getValues method: "+op.toString());
			DB db=io.getDB(resource.getWriteConcern(), resource.getReadPreference());
			GridFS gfs = new GridFS(db);  
			if(logger.isDebugEnabled()){
				logger.debug("Mongo get values of dir: "+bucket);
			}
			
			BasicDBObject query = new BasicDBObject();
			query.put("dir", bucket);
			List<GridFSDBFile> list = gfs.find(query);
	// Patch for incompatibility v 1-2
			list=io.patchRemoteDirPathVersion1(bucket, gfs, query, list);
	//end		
			logger.info("find all object (files/dirs) in the directory "+bucket);
			for(Iterator<GridFSDBFile> it=list.iterator(); it.hasNext();){
				GridFSDBFile f=(GridFSDBFile)it.next();
					if(map==null){
						map=new HashMap<String, StorageObject>();
					}
					StorageObject s_obj=null;
			// = null if the object is not contained in a subDirectory
					if((f.get("type")==null) || (f.get("type").toString().equalsIgnoreCase("file"))){
						if(logger.isDebugEnabled())
							logger.debug("found object: "+f.get("name")+"    type:  "+f.get("type"));
						s_obj=new StorageObject(f.get("name").toString(), "file");
						String owner=(String)f.get("owner");
						if(owner !=null)
							s_obj.setOwner(owner);
						String creationTime=(String)f.get("creationTime");
						if(creationTime!=null)
							s_obj.setCreationTime(creationTime);
						s_obj.setId(f.getId().toString());
					}else{
						if(logger.isDebugEnabled())
							logger.debug("found directory: "+f.get("name")+"    type:  "+f.get("type"));
				// check if a empty dir, if it is a empty dir then I remove it
						BasicDBObject queryDir = new BasicDBObject();
						queryDir.put("dir", f.get("dir").toString()+f.get("name").toString());
						List<GridFSDBFile> listDir = gfs.find(queryDir);
						if((listDir != null) && (listDir.size() > 0))
							s_obj=new StorageObject(f.get("name").toString(), "dir");
						else{
							// then the dir not contains subDirectory
							//check if it contains subfiles
							BasicDBObject queryFile = new BasicDBObject();
							queryFile.put("filename", java.util.regex.Pattern.compile(f.get("dir").toString()+"*"));
							logger.info("find all files in the directory "+f.get("name"));
							List<GridFSDBFile> listFile = gfs.find(queryFile);
							logger.info("search completed");
							if((listFile != null) && (listFile.size() > 0)){
						// then it contains subFile. Insert it in the result map		
								s_obj=new StorageObject(f.get("name").toString(), "dir");
							}else s_obj=null;
						}
					}
					if(s_obj !=null)
						map.put(f.get("name").toString(), s_obj);
			}
			logger.info("search completed");
		}catch(Exception e ){
			close();
			throw new RemoteBackendException("problem to retrieve objects in the folder: "+bucket+" exception message: "+e.getMessage());
		}
		close();
		return map;
	}


	@Override
	public void removeRemoteFile(String bucket, MyFile resource) throws UnknownHostException{
		if(logger.isDebugEnabled())
			logger.debug("Mongo delete bucket: "+bucket);
		GridFSDBFile f=io.retrieveRemoteObject(bucket, true);
		if(f!=null){
			checkAndRemove(f, resource);
		}else{
			if(logger.isDebugEnabled())
				logger.debug("File Not Found. Try to delete by ObjectID");
			if(bucket.length()>23){
				ObjectId id=new ObjectId(bucket);
				GridFSDBFile fID=io.findGFSCollectionObject(id);
				if(fID != null){
					checkAndRemove(fID, resource);
					if(logger.isInfoEnabled())
						logger.info("object deleted by ID");
				}
			}
		}
		close();
	}


	@Override
	public void removeDir(String remoteDir, MyFile resource){
		ArrayList<String> dirs=new ArrayList<String>();
		dirs.add(remoteDir);
	// patch for incompatibility v 1-2	
		if((remoteDir.contains(MongoIO.ROOT_PATH_PATCH_V1)) || (remoteDir.contains(MongoIO.ROOT_PATH_PATCH_V2))){
			if(remoteDir.contains(MongoIO.ROOT_PATH_PATCH_V1)){
				String remoteDirV1=remoteDir.replace(MongoIO.ROOT_PATH_PATCH_V1, MongoIO.ROOT_PATH_PATCH_V2);
				dirs.add(remoteDirV1);
			}else{
				String remoteDirV2= remoteDir.replace(MongoIO.ROOT_PATH_PATCH_V2, MongoIO.ROOT_PATH_PATCH_V1);
				dirs.add(remoteDirV2);
				String remoteDirV2patch=ServiceEngine.FILE_SEPARATOR+remoteDirV2;
				dirs.add(remoteDirV2patch);
			}
		}
	// end patch	
		DB db=io.getDB(resource.getWriteConcern(), resource.getReadPreference());
		GridFS gfs = new GridFS(db);  
		for(String directory : dirs){
			if(logger.isDebugEnabled())
				logger.debug("Mongo start operation delete bucket: "+directory);
	// remove subfolders
			if(logger.isDebugEnabled())
				logger.debug("remove subfolders of folder: "+directory);
			BasicDBObject query = new BasicDBObject();
			String regex=directory+"*";
			query.put("dir", java.util.regex.Pattern.compile(regex));
			removeObject(gfs, query,resource);
			query=new BasicDBObject();
			String[] dir=directory.split(ServiceEngine.FILE_SEPARATOR);
			StringBuffer parentDir=new StringBuffer();
			for(int i=0;i<dir.length-1;i++){
				parentDir.append(dir[i]+ServiceEngine.FILE_SEPARATOR);
			}
			String name=dir[dir.length-1];
			query.put("dir", parentDir.toString());
			query.put("name", name);
			if(logger.isDebugEnabled())
				logger.debug("now remove the folder: "+name+" from folder "+parentDir);
			removeObject(gfs, query, resource);
			if(logger.isDebugEnabled())
				logger.debug("Mongo end operation delete bucket: "+directory);
		}
		close();
		
	}

	@Override
	public long getSize(String remotePath){
		long length=-1;
		if(logger.isDebugEnabled())
			logger.debug("MongoDB - get Size for pathServer: "+remotePath);
		GridFSDBFile f = io.retrieveRemoteObject(remotePath, true);
		if(f!=null){
			length=f.getLength();
		}
		close();
		return length;
	}

	@Override
	public long getTTL(String remotePath) throws UnknownHostException{
		long timestamp=-1;
		long currentTTL=-1;
		long remainsTTL=-1;
		if(logger.isDebugEnabled())
			logger.debug("MongoDB - pathServer: "+remotePath);
		GridFSDBFile f=io.retrieveRemoteObject(remotePath, true);
		if(f!=null){
			timestamp=(Long)f.get("timestamp");
			if(timestamp > 0){
				currentTTL=System.currentTimeMillis() - timestamp;
				remainsTTL=ServiceEngine.TTL- currentTTL;
			}
			
		}
		close();
		return remainsTTL;
	}

	@Override
	public long renewTTL(MyFile resource) throws UnknownHostException, IllegalAccessException{
		long ttl=-1;
		MyFile file=(MyFile)resource;
		String key=file.getLockedKey();
		String remotePath=file.getRemotePath();
		GridFSDBFile f=io.retrieveRemoteObject(remotePath, true);
		if(f!=null){
			  String lock=(String)f.get("lock");
  	         //check if the od file is locked		  
  	         if((lock !=null) && (!lock.isEmpty())){
  	        	 String lck=(String)f.get("lock");
  	        	 if(lck.equalsIgnoreCase(key)){
  	        		if((f.containsField("countRenew")) && (f.get("countRenew") != null)){ 
  	        			int count=(Integer)f.get("countRenew");
  	        			if(count < ServiceEngine.TTL_RENEW){
  	        				f.put("countRenew", count+1);
  	        			}else{
  	        				close();
// number max of ttl renew operation reached. the operation is blocked
  	        				throw new IllegalAccessException("The number max of TTL renew reached. The number max is: "+ServiceEngine.TTL_RENEW);
  	        			}
  	        		}else{
//  first renew operation	        			
  	        			f.put("countRenew", 1);
  	        		}
  	        		f.put("timestamp", System.currentTimeMillis());
  	        		f.save();
  	        		ttl=ServiceEngine.TTL;
  	        	 }else{
  	        		close();
  	        		 throw new IllegalAccessError("bad key for unlock");
  	        	 }
  	         }

		}
		close();
		return ttl;
	}

	
	/**
	 * link operation
	 * 
	 */
	@Override
	public String link(MyFile resource, String sourcePath, String destinationPath) throws UnknownHostException{
		boolean replace=true;
		String source=sourcePath;
		String destination=destinationPath;
		String dir=((MyFile)resource).getRemoteDir();
		String name=((MyFile)resource).getName();
		String destinationId=null;
		String sourceId=null;
		logger.debug("link operation on Mongo backend, parameters: source path: "+source+" destination path: "+destination);
		if((source != null) && (!source.isEmpty()) && (destination != null) && (!destination.isEmpty())){
			GridFSDBFile f = io.retrieveRemoteObject(source, false);
			if(f != null){
				int count=1;
				if((f.containsField("linkCount")) && ((f.get("linkCount") != null))){
					count=(Integer)f.get("linkCount");
					count++;
				}
				f.put("linkCount", count);
				updateCommonFields(f, resource, OPERATION.LINK);
				sourceId=f.getId().toString();
				f.save();
		    }else{
		    	close();
		    	throw new IllegalArgumentException(" source remote file not found at: "+source);
		    }
	// check if the destination file exists
//			GridFSDBFile fold = gfs.findOne(destinationPath);
			GridFSDBFile fold = io.retrieveRemoteObject(destinationPath, true);
			if(fold != null){
				String oldir=(String)fold.get("dir");
		        if(logger.isDebugEnabled())
		      	  logger.debug("old dir  found "+oldir);
		        if((oldir.equalsIgnoreCase(((MyFile)resource).getRemoteDir()))){
		         	  ObjectId oldId=(ObjectId) fold.getId();
		         	  if(!replace){
		         		  return oldId.toString();
		         	  }else{
		         		  if(logger.isDebugEnabled())
		         			  logger.debug("remove id: "+oldId);
		         		  String lock=(String)fold.get("lock");
		         //check if the od file is locked		  
		         		  if((lock !=null) && (!lock.isEmpty()) && (!isTTLUnlocked(fold))){
		         			 close();
		         			  throw new IllegalAccessError("The file is locked");
		         		  }else{
		         //remove old file			  
		         	  		  io.removeGFSFile(fold, oldId);
		         		  }
		         	  }
		        }
		    }
	// create destination file
		    GridFSInputFile destinationFile=null;    
			//create new file
		    byte[] data=new byte[1];
		    if (resource.getGcubeMemoryType()== MemoryType.VOLATILE){
		    	destinationFile = io.createGFSFileObject(data);//gfs.createFile(data);
		    }else{
		    	destinationFile = io.createGFSFileObject(data, resource.getWriteConcern(), resource.getReadPreference());//gfs.createFile(data);
		    }
			if(logger.isDebugEnabled())
			   	logger.debug("Directory: "+dir);
			setGenericProperties(resource, destinationPath, dir,
					destinationFile, name);
			destinationFile.put("link", sourceId);
			destinationId=destinationFile.getId().toString();
			if(logger.isDebugEnabled())
				logger.debug("ObjectId: "+destinationId);
			io.buildDirTree(io.getMetaDataCollection(null), dir);
			destinationFile.save();
			close();
		}else{
			close();
			throw new IllegalArgumentException(" invalid argument: source: "+source+" dest: "+destination+" the values must be not null and not empty");
		}
		return destinationId.toString();
	}

	
	@Override
	public String copy(MyFile resource, String sourcePath, String destinationPath) throws UnknownHostException{
		String source=sourcePath;
		String destination=destinationPath;
		String dir=((MyFile)resource).getRemoteDir();
		String name=((MyFile)resource).getName();
		String destinationId=null;
		logger.debug("copy operation on Mongo backend, parameters: source path: "+source+" destination path: "+destination);
		if((source != null) && (!source.isEmpty()) && (destination != null) && (!destination.isEmpty())){
			GridFSDBFile f = io.retrieveRemoteObject(source, true);
			if(f != null){
// if it is a copy of an hardLink, then I'm going to retrieve and copy the payload associated to the link			
				f = io.retrieveLinkPayload(f);
				InputStream is= f.getInputStream();
		// check if the destination is a dir or a file and if the destination exist
				GridFSDBFile dest = io.retrieveRemoteObject(destination, false);//gfs.findOne(destination);
				GridFSInputFile destinationFile=io.createGFSFileObject(is, resource.getWriteConcern(), resource.getReadPreference());//gfs.createFile(is);
				if(dest != null){
		//overwrite the file
					checkAndRemove(dest, resource);
					setGenericProperties(resource, destination, dir,
							destinationFile, destination.substring(destination.lastIndexOf(ServiceEngine.FILE_SEPARATOR)+1));
					io.buildDirTree(io.getMetaDataCollection(null), dir);
				}else{
					BasicDBObject query = new BasicDBObject();
					query.put( "dir" , destination );
					List<GridFSDBFile> folder =io.retrieveRemoteObjects(query);
//		if the destination is a folder			
					if((folder != null) && (folder.size() > 0)){
						destination=appendFileSeparator(destination);
						setGenericProperties(resource, destination+name, destination, destinationFile, name);
						io.buildDirTree(io.getMetaDataCollection(null), destination);
					}else{
//	if the last char of dest path is a separator then the destination is a dir otherwise is a file
						if(destination.lastIndexOf(ServiceEngine.FILE_SEPARATOR) == destination.length()-1){
							setGenericProperties(resource, destination+name, destination, destinationFile, name);
//							buildDirTree(gfs, destination);
							io.buildDirTree(io.getMetaDataCollection(null), destination);
						}else{
							String newName=destination.substring(destination.lastIndexOf(ServiceEngine.FILE_SEPARATOR)+1);
							setGenericProperties(resource, destination, dir, destinationFile, newName);
//							buildDirTree(gfs, dir);
							io.buildDirTree(io.getMetaDataCollection(null), dir);
						}
					}
				}
				destinationId=destinationFile.getId().toString();
				destinationFile.save();
				if(logger.isDebugEnabled())
					logger.debug("ObjectId: "+destinationId);
				close();
			}else{
				close();
				throw new RemoteBackendException(" the source path is wrong. There isn't a file at "+source);
			}
		}
		return destinationId.toString();
	}
	
	
	@Override
	public String move(MyFile resource, String sourcePath, String destinationPath) throws UnknownHostException{
		String source=sourcePath;
		String destination=destinationPath;
		resource.setLocalPath(sourcePath);
		String dir=((MyFile)resource).getRemoteDir();
		String name=((MyFile)resource).getName();
		String destinationId=null;
		String sourceId=null;
		logger.info("move operation on Mongo backend, parameters: source path: "+source+" destination path: "+destination);
		logger.debug("MOVE OPERATION operation defined: "+resource.getOperationDefinition().getOperation());
		if((source != null) && (!source.isEmpty()) && (destination != null) && (!destination.isEmpty())){
			BasicDBObject sourcePathMetaCollection = io.findMetaCollectionObject(source);
//check if the file exist in the destination path, if it exist then it will be deleted		
			if(sourcePathMetaCollection != null){
				sourceId=sourcePathMetaCollection.get("_id").toString();
				sourcePathMetaCollection=setCommonFields(sourcePathMetaCollection, resource, OPERATION.MOVE);
//				updateCommonFields(sourcePathMetaCollection, resource);
				BasicDBObject queryDestPath = new BasicDBObject();
				queryDestPath.put( "filename" , destinationPath);
				DBCollection metaCollectionInstance=null;
				if(!(memoryType== MemoryType.VOLATILE))
					metaCollectionInstance=io.getMetaDataCollection(io.getDB(resource.getWriteConcern(), resource.getReadPreference()));
				else
					metaCollectionInstance=io.getMetaDataCollection(io.getDB());
				
				DBObject destPathMetaCollection= io.executeQuery(metaCollectionInstance, queryDestPath);
	// retrieve original object		
				BasicDBObject  querySourcePath = new BasicDBObject();
				querySourcePath.put( "filename" , sourcePath);
	   //update common fields		
				BasicDBObject updateQuery= new BasicDBObject();
				updateQuery.put("$set", sourcePathMetaCollection);
				if(!(memoryType== MemoryType.VOLATILE))
					metaCollectionInstance.update(querySourcePath, updateQuery, false, true, MongoIO.DEFAULT_WRITE_TYPE);
				else
					metaCollectionInstance.update(querySourcePath, updateQuery, false, true);
				if(destPathMetaCollection != null)
					destinationId=destPathMetaCollection.get("_id").toString();
				if((destPathMetaCollection!=null) && (destinationId != null) && (!destinationId.equals(sourceId))){
					io.printObject(destPathMetaCollection);
				// if exist, keep id (it need a replace)
					destinationId=destPathMetaCollection.get("_id").toString();
					logger.info("file in destination path already present with id : "+destinationId);		
		//remove old one	
					GridFS gfs = new GridFS(io.getDB(resource.getWriteConcern(), resource.getReadPreference()));
					GridFSDBFile fNewFSPath = gfs.findOne(queryDestPath);
					checkAndRemove(fNewFSPath, resource);
		// print			
					logger.debug("Changing filename metadata from:"+sourcePathMetaCollection.get("filename")+"\n  to: "+destinationPath);
					logger.debug("original objects:\n  ");
					logger.debug("source object: ");
					io.printObject(sourcePathMetaCollection);
					logger.info("destination object: ");
					io.printObject(destPathMetaCollection);
		// update fields	
					io.buildDirTree(io.getMetaDataCollection(io.getDB(resource.getWriteConcern(), resource.getReadPreference())), dir);
					sourcePathMetaCollection= new BasicDBObject();
					sourcePathMetaCollection.put("$set", new BasicDBObject().append("dir", dir).append("filename", destinationPath).append("name", name).append("owner", ((MyFile)resource).getOwner()));
					logger.info("new object merged ");
					io.printObject(sourcePathMetaCollection);
			//applies the update	
					if(!(memoryType== MemoryType.VOLATILE))
						metaCollectionInstance.update(querySourcePath, sourcePathMetaCollection, false, true, MongoIO.DEFAULT_WRITE_TYPE);
					else
						metaCollectionInstance.update(querySourcePath, sourcePathMetaCollection, false, true);
					logger.info("update metadata done ");
					logger.info("check update ");
					DBObject newDestPathMetaCollection= io.executeQuery(metaCollectionInstance, queryDestPath);
					io.printObject(newDestPathMetaCollection);
				}else if((destinationId!= null) && (destinationId.equals(sourceId))){
					logger.warn("the destination id and the source id are the same id. skip operation. ");
				}else{
					queryDestPath = new BasicDBObject();
					queryDestPath.put( "dir" , destination );
					DBObject folder = metaCollectionInstance.findOne(queryDestPath);//= gfs.find(query);
//		if the destination is an existing folder			
					if((folder != null)){
						destination=appendFileSeparator(destination);
						sourcePathMetaCollection=setGenericMoveProperties(resource, destination+name, destination, name, sourcePathMetaCollection);
						destinationId=sourcePathMetaCollection.get("_id").toString();
						io.buildDirTree(metaCollectionInstance, destination);
						
					}else{
//	if the last char of dest path is a separator then the destination is a dir otherwise is a file
// then if it is a new folder						
						if(destination.lastIndexOf(ServiceEngine.FILE_SEPARATOR) == destination.length()-1){
							sourcePathMetaCollection=setGenericMoveProperties(resource, destination+name, destination, name, sourcePathMetaCollection);
							destinationId=sourcePathMetaCollection.get("_id").toString();
							io.buildDirTree(metaCollectionInstance, destination);
							
						}else{
							String newName=destination.substring(destination.lastIndexOf(ServiceEngine.FILE_SEPARATOR)+1);
							sourcePathMetaCollection=setGenericMoveProperties(resource, destination, dir, newName, sourcePathMetaCollection);
							destinationId=sourcePathMetaCollection.get("_id").toString();
							io.buildDirTree(metaCollectionInstance, dir);
						}
						queryDestPath = new BasicDBObject();
						queryDestPath.put( "filename" , sourcePath);
					//update common fields		
						updateQuery= new BasicDBObject();
						updateQuery.put("$set", sourcePathMetaCollection);
						if(!(memoryType== MemoryType.VOLATILE))
								metaCollectionInstance.update(queryDestPath, updateQuery, true, true, MongoIO.DEFAULT_WRITE_TYPE);
						else
							metaCollectionInstance.update(queryDestPath, updateQuery, true, true);
					}
				}
				close();
				return destinationId;
			}else{
				close();
				throw new RemoteBackendException(" the source path is wrong. There isn't a file at this path: "+source);
			}
		}else{
			close();
			throw new IllegalArgumentException("parameters not completed, source: "+source+", destination: "+destination);
		}
	}
	
	
	private ObjectId removeFile(Object resource, String key, boolean replace,
			ObjectId oldId, GridFSDBFile fold) throws IllegalAccessError,
			UnknownHostException {
		//remove old object			
					String oldir=(String)fold.get("dir");
			        if(logger.isDebugEnabled())
			      	  logger.debug("old dir  found "+oldir);
			        logger.info("remove old object if replace is true and the file is not locked");
			        if((oldir !=null) &&(oldir.equalsIgnoreCase(((MyFile)resource).getRemoteDir()))){
		//	         	  ObjectId oldId=(ObjectId) fold.getId();
			  // if the file contains a link the replace is not allowed
			         	  if((!replace)){
			         		  return oldId;
			         	  }else if((fold.containsField("countLink")) && (fold.get("countLink")!=null)){
			         		  close();
			         		  throw new RemoteBackendException("The file cannot be replaced because is linked from another remote file");
			         	  }else{
			         		  if(logger.isDebugEnabled())
			         			  logger.debug("remove id: "+oldId);
			         		  String lock=(String)fold.get("lock");
			         //check if the od file is locked		  
			         		  if((lock !=null) && (!lock.isEmpty()) && (!isTTLUnlocked(fold) && (!lock.equalsIgnoreCase(key)))){
			         			  close();
				         			  throw new IllegalAccessError("The file is locked");
			         		  }else{
			         			 oldId=checkAndRemove(fold, (MyFile)resource);
			         		  }
			         	  }
			        }else if(oldir == null){
			        	if((!replace) && (oldId!= null)){
			        		return oldId;
			        	}
			        }
		return oldId;
	}

	private ObjectId createNewFile(Object resource, String bucket, String dir,
			String name, ObjectId oldId) throws UnknownHostException {
			ObjectId id;
		// create new dir
		    io.buildDirTree(io.getMetaDataCollection(null), dir);
		//create new file with specified id
		    GridFSInputFile f2 = writePayload(resource, 0, bucket, name, dir, oldId);
		    id=(ObjectId)f2.getId();
		    logger.info("new file created with id: "+id);
		return id;
	}

	
	private ObjectId checkAndRemove(GridFSDBFile f, MyFile resource){
		String idToRemove=f.getId().toString();
		logger.info("check and remove object with id "+idToRemove+" and path: "+f.get("filename"));
		ObjectId idFile=null;
		if(logger.isDebugEnabled())
			logger.debug("fileFound\t remove file");
		updateCommonFields(f, resource, OPERATION.REMOVE);
// check if the file is linked		
		if((f!=null) && (f.containsField("linkCount")) && (f.get("linkCount") != null)){
		// this field is only added for reporting tool: storage-manager-trigger	
			String filename=(String)f.get("filename");
			f.put("onScope", filename);
// remove metadata: dir, filename, name 	
			f.put("dir", null);
			f.put("filename", null);
			f.put("name", null);
			f.put("onDeleting", "true");
			f.save();
		}else if((f.containsField("link")) && (f.get("link") != null )){
			while((f!=null) && (f.containsField("link")) && (f.get("link") != null )){
				// remove f and decrement linkCount field on linked object
				String id=(String)f.get("link");
				GridFSDBFile fLink=io.findGFSCollectionObject(new ObjectId(id));
			    int linkCount=(Integer)fLink.get("linkCount");
			    linkCount--;
			    if(linkCount == 0){
		// if the name the filename and dir are null, then I delete also the link object	    	
			    	if((fLink.get("name")==null ) && (fLink.get("filename")==null ) && (fLink.get("dir")==null )){
		    			ObjectId idF=(ObjectId) f.getId();
		    			idFile=idF;
		    // this field is an advice for oplog collection reader			 
						 io.removeGFSFile(f, idF);
			    		if((fLink.containsField("link")) && (fLink.get("link") != null )){
			    	//the link is another link		
							id=(String)fLink.get("link");
							f=io.findGFSCollectionObject(new ObjectId(id));
			    		}else{
			    	// the link is not another link		
			    			f=null;
			    		}
			    		ObjectId idLink=(ObjectId)fLink.getId();
			    		idFile=idLink;
			    		io.removeGFSFile(fLink, idLink);
			    	}else{
			    		fLink.put("linkCount", null);
			    		fLink.save();
						ObjectId oId=(ObjectId) f.getId();
						idFile=oId;
						io.removeGFSFile(f, oId);
						f=null;
			    	}
			    }else{
			    	fLink.put("linkCount", linkCount);
			    	fLink.save();
					ObjectId oId=(ObjectId) f.getId();
     				io.removeGFSFile(f, oId);
     				f=null;
			    }
			}
		}else{
			logger.debug("");
			 idFile=new ObjectId(idToRemove);
			 io.removeGFSFile(f, new ObjectId(idToRemove));
		}
		return idFile;
	}




	private GridFSInputFile writePayload(Object resource, int count, String bucket, String name, String dir, ObjectId idFile){
		GridFSInputFile f2=null;
		io.clean();
		try{
			if(((MyFile)resource).getInputStream()!= null){
			//upload with client inputStream	
				f2 = writeByInputStream(resource, bucket, name, dir,idFile);
				f2.save();
			}else if(((((MyFile)resource).getType() != null) && (((MyFile)resource).getType().equals("output")))){
	// upload with outputstream		
				f2 = writeByOutputStream(resource, bucket, name, dir, idFile);
			}else{
	// upload by local file path			
				f2 = writeByLocalFilePath(resource, bucket, name, dir, idFile);
				f2.save();
			}
			if(logger.isDebugEnabled())
			   	logger.debug("Directory: "+dir);
			Object id=f2.getId();
			if(logger.isDebugEnabled())
				logger.debug("ObjectId: "+id);
			
		// if it is an outputstream	don't close	
			if(!((((MyFile)resource).getType() != null) && (((MyFile)resource).getType().equals("output")))){
				close();
			}
		}catch(IOException e1){
			logger.error("Connection error. "+e1.getMessage());
			if(count < ServiceEngine.CONNECTION_RETRY_THRESHOLD){
				count++;
				logger.info(" Retry : #"+count);
				writePayload(resource, count, bucket, name, dir, idFile);
			}else{
				logger.error("max number of retry completed ");
				close();
				throw new RemoteBackendException(e1);
			}

		}
		return f2;
	}

	
	private GridFSInputFile writeByLocalFilePath(Object resource,
			String bucket, String name, String dir, ObjectId idFile)
			throws IOException {
		GridFSInputFile f2;
		if(!(memoryType== MemoryType.VOLATILE))
			f2 = io.createGFSFileObject(new File(((MyFile)resource).getLocalPath()), ((MyFile)resource).getWriteConcern(), ((MyFile)resource).getReadPreference());
		else
			f2 = io.createGFSFileObject(new File(((MyFile)resource).getLocalPath()));
		fillInputFile(resource, bucket, name, dir, f2, idFile);
		io.saveGFSFileObject(f2);
		return f2;
	}

	private GridFSInputFile writeByOutputStream(Object resource,
			String bucket, String name, String dir, ObjectId idFile) throws IOException {
		GridFSInputFile f2;
		if(!(memoryType== MemoryType.VOLATILE))
			f2 = io.createGFSFileObject(((MyFile)resource).getName(), ((MyFile)resource).getWriteConcern(), ((MyFile)resource).getReadPreference());
		else
			f2 = io.createGFSFileObject(((MyFile)resource).getName());
		fillInputFile(resource, bucket, name, dir, f2, idFile);
		((MyFile)resource).setOutputStream(new MongoOutputStream(mongo, f2.getOutputStream()));
		return f2;
	}

	private GridFSInputFile writeByInputStream(Object resource,
			String bucket, String name, String dir, ObjectId idFile)
			throws IOException {
		GridFSInputFile f2;
		if(!(memoryType== MemoryType.VOLATILE))
			f2 = io.createGFSFileObject(((MyFile)resource).getInputStream(), ((MyFile)resource).getWriteConcern(),((MyFile)resource).getReadPreference());
		else
			f2 = io.createGFSFileObject(((MyFile)resource).getInputStream());
		fillInputFile(resource, bucket, name, dir, f2, idFile);
		io.saveGFSFileObject(f2);
		((MyFile)resource).getInputStream().close();
		((MyFile)resource).setInputStream(null);
		return f2;
	}



	
	
	private void fillInputFile(Object resource, String bucket, String name,	String dir, GridFSInputFile f2, ObjectId id) {
		if(id != null)
			f2.put("_id", new ObjectId(id.toString()));
		if(bucket.contains("/"))
			f2.put("filename", bucket);  
		f2.put("type", "file");
		if(name!= null)
			f2.put("name", name);
		if(dir!=null)
			f2.put("dir", dir);
		if(((MyFile)resource).getOwner() !=null)
			f2.put("owner", ((MyFile)resource).getOwner());
		String mime= ((MyFile)resource).getMimeType();
		if( mime !=null){
			f2.put("mimetype", mime);
		}
		f2.put("creationTime", DateUtils.now("dd MM yyyy 'at' hh:mm:ss z"));
		updateCommonFields(f2, (MyFile)resource, null);
	}

	
	/**
	 * @param resource
	 * @param f
	 * @param isLock indicates if the file must be locked
	 * @throws IOException 
	 */
	private void download(GridFS gfs, MyFile resource, GridFSDBFile f, boolean isLock) throws IOException {
		OperationDefinition op=resource.getOperationDefinition();
		logger.info("MongoClient download method: "+op.toString());
// if contains the field link it means that is a link hence I follow ne or more links		
		while((f !=null ) && (f.containsField("link")) && (f.get("link") != null)){
			BasicDBObject query = new BasicDBObject();
			query.put( "_id" , new ObjectId((String)f.get("link")) );
//			query.put( "_id" , f.get("link") );
			f=gfs.findOne( query );
		}
		updateCommonFields(f, resource, OPERATION.DOWNLOAD);
		f.save();
		if((resource.getLocalPath()!=null) && (!resource.getLocalPath().isEmpty())){
			io.readByPath(resource, f, isLock, 0);
			close();
		}else if(resource.getOutputStream()!=null){
			io.readByOutputStream(resource, f, isLock, 0);
			close();
		}
		if((resource!=null) && (resource.getType()!=null) && resource.getType().equalsIgnoreCase("input")){
			io.readByInputStream(resource, f, isLock, 0);
		}
	}

	
	
	/**
	 * @param gfs
	 * @param query
	 * @throws UnknownHostException 
	 */
	private void removeObject(GridFS gfs, BasicDBObject query, MyFile resource){
		List<GridFSDBFile> list = gfs.find(query);
		for(Iterator<GridFSDBFile> it=list.iterator(); it.hasNext();){
			GridFSDBFile f=(GridFSDBFile)it.next();
			if(f!=null){
				checkAndRemove(f, resource);
			}else{
				if(logger.isDebugEnabled())
					logger.debug("File Not Found");
			}
		}
	}


	private void setGenericProperties(MyFile resource, String destination,
			String dir, GridFSInputFile destinationFile, String name) {
		updateCommonFields(destinationFile, resource, null);
		destinationFile.put("filename", destination);  
		destinationFile.put("type", "file");
		destinationFile.put("name", name);
		destinationFile.put("dir", dir);
		destinationFile.put("owner", ((MyFile)resource).getOwner());
		destinationFile.put("mimetype", ((MyFile)resource).getMimeType());
		destinationFile.put("creationTime", DateUtils.now("dd MM yyyy 'at' hh:mm:ss z"));
	}

	private BasicDBObject setGenericMoveProperties(MyFile resource, String filename, String dir,
			String name, BasicDBObject f) {
		f.append("filename", filename).append("type", "file").append("name", name).append("dir", dir);
		return f;
	}

	@Override
	public String getName() {
		return TransportManager.DEFAULT_TRANSPORT_MANAGER;
	}

	@Override
	public List<String> copyDir(MyFile resource, String sourcePath, String destinationPath)
			throws UnknownHostException {
		String source=sourcePath;
		source = appendFileSeparator(source);
		String destination=destinationPath;
		destination = appendFileSeparator(destination);
		String parentFolder=extractParent(source);
		String destinationId=null;
		List<String> idList=null;
		logger.debug("copyDir operation on Mongo backend, parameters: source path: "+source+" destination path: "+destination);
		if((source != null) && (!source.isEmpty()) && (destination != null) && (!destination.isEmpty())){
			DB db = getDB(resource);
			GridFS gfs = new GridFS(db);
//// create query for dir field
			BasicDBObject query = new BasicDBObject();
			query.put( "dir" , new BasicDBObject("$regex", source+"*"));
			List<GridFSDBFile> folder = gfs.find(query);
			if(folder!=null){
				idList=new ArrayList<String>(folder.size());
				for(GridFSDBFile f : folder){
					if(f.get("type").equals("file")){
						String oldFilename=(String)f.get("filename");
						String oldDir=(String)f.get("dir");
						f=io.retrieveLinkPayload(f);
						InputStream is= f.getInputStream();
						int relativePathIndex=source.length();
						String relativeDirTree=parentFolder+ServiceEngine.FILE_SEPARATOR+oldDir.substring(relativePathIndex);
						String relativePath=parentFolder+ServiceEngine.FILE_SEPARATOR+oldFilename.substring(relativePathIndex);
						String filename=destination+relativePath;
						String dir=destination+relativeDirTree;
						GridFSInputFile destinationFile=gfs.createFile(is);
						destinationFile.put("filename", filename);  
						destinationFile.put("type", "file");
						destinationFile.put("dir", dir);
						updateCommonFields(destinationFile, resource, OPERATION.COPY_DIR);
						idList.add(destinationFile.getId().toString());
						if(logger.isDebugEnabled())
							logger.debug("ObjectId: "+destinationId);
						io.buildDirTree(io.getMetaDataCollection(db), dir);
						destinationFile.save();
					}
				}
			}
			close();
		}
		return idList;
	}


	private DB getDB(MyFile resource) {
		DB db=null;
		if(resource.getGcubeMemoryType()==MemoryType.VOLATILE){
			db=io.getDB();
		}else{
			db=io.getDB(resource.getWriteConcern(), resource.getReadPreference());
		}
		return db;
	}

	@Override
	public List<String> moveDir(MyFile resource, String sourcePath, String destinationPath)
			throws UnknownHostException {
		String source=sourcePath;
		source = appendFileSeparator(source);
		String parentFolder=extractParent(source);
		String destination=destinationPath;
		destination = appendFileSeparator(destination);
		List<String> idList=null;
		logger.debug("moveDir operation on Mongo backend, parameters: source path: "+source+" destination path: "+destination);
		if((source != null) && (!source.isEmpty()) && (destination != null) && (!destination.isEmpty())){
			DB db=io.getDB(resource.getWriteConcern(), resource.getReadPreference());
//			GridFS meta = new GridFS(db); 
			DBCollection meta=io.getMetaDataCollection(db);
// create query for dir field
			BasicDBObject query = new BasicDBObject();
			query.put( "dir" , new BasicDBObject("$regex", source+"*"));
			DBCursor folderCursor = meta.find(query);
			if((folderCursor !=null)){
				idList=new ArrayList<String>();
				while(folderCursor.hasNext()){//GridFSDBFile f : folder){
					DBObject f=folderCursor.next();
					if(f.get("type").equals("file")){
						String oldFilename=(String)f.get("filename");
						String oldDir=(String)f.get("dir");
						int relativePathIndex=source.length();
						String relativeDirTree=parentFolder+ServiceEngine.FILE_SEPARATOR+oldDir.substring(relativePathIndex);
						String relativePath=parentFolder+ServiceEngine.FILE_SEPARATOR+oldFilename.substring(relativePathIndex);
						String filename=destination+relativePath;
						String dir=destination+relativeDirTree;
						f.put("filename", filename);
						f.put("dir", dir);
						updateCommonFields(f, resource, OPERATION.MOVE_DIR);
						String id=f.get("_id").toString();
						idList.add(id);
						query = new BasicDBObject();
						query.put( "_id" , new ObjectId(id));
						if(!(memoryType== MemoryType.VOLATILE))
							meta.update(query, f, true, false, MongoIO.DEFAULT_WRITE_TYPE);
						else
							meta.update(query, f, true, false);
//						meta.update(query, f, true, true);
						io.buildDirTree(meta, dir);
					}
				}
			}
		}else{
			close();
			throw new IllegalArgumentException("parameters not completed, source: "+source+", destination: "+destination);
		}
		close();
		return idList;
	}
	

	private String extractParent(String source) {
		source=source.substring(0, source.length()-1);
		String parent=source.substring(source.lastIndexOf(ServiceEngine.FILE_SEPARATOR)+1);
		logger.debug("parent folder extracted: "+parent);
		return parent;
	}

	private String appendFileSeparator(String source) {
		if(source.lastIndexOf(ServiceEngine.FILE_SEPARATOR) != (source.length()-1))
			source=source+ServiceEngine.FILE_SEPARATOR;
		return source;
	}

	@Override
	public String getFileProperty(String remotePath, String property){
		GridFSDBFile f = io.retrieveRemoteObject(remotePath, false);
		if(f!=null){
			String value=(String)f.get(property);
			close();
			return value;
		}else{
			close();
			throw new RemoteBackendException("remote file not found at path: "+remotePath);
		}
	}
	
	@Override
	public String setFileProperty(String remotePath, String propertyField, String propertyValue){
		logger.trace("setting field "+propertyField+" with value: "+propertyValue);
		BasicDBObject remoteMetaCollectionObject;
//		if(!propertyField.equalsIgnoreCase("mimetype") && !propertyField.equalsIgnoreCase("owner")){
//			logger.warn("It is not allowed to set the field "+propertyValue);
//			logger.warn("The only field that could be set is mimetype");
//			throw new RemoteBackendException("It is not allowed to set the field "+propertyValue);
//		}else{
			try {
				logger.debug("find object...");
				remoteMetaCollectionObject = io.findMetaCollectionObject(remotePath);
				if(remoteMetaCollectionObject!=null){
					logger.debug("object found");
					remoteMetaCollectionObject.put(propertyField, propertyValue);
					logger.info("set query field: "+propertyField+" with value: "+propertyValue);
					BasicDBObject updateQuery= new BasicDBObject();
					updateQuery.put("$set", remoteMetaCollectionObject);
					// retrieve original object		
					BasicDBObject  querySourceObject = new BasicDBObject();
					logger.debug("check identifier object: "+remotePath);
					if(ObjectId.isValid(remotePath)){
						logger.debug("object is a valid id");
						querySourceObject.put( "_id" , new ObjectId(remotePath));
					}else{
						logger.debug("object is a remotepath");
						querySourceObject.put( "filename" , remotePath);
					}
					//getCollection
					logger.debug("get Collection ");
					DBCollection metaCollectionInstance=io.getMetaDataCollection(io.getDB(null,null));
					//update field
					logger.debug("update Collection ");
					if (!(memoryType== MemoryType.VOLATILE))
						metaCollectionInstance.update(querySourceObject, updateQuery, false, true, MongoIO.DEFAULT_WRITE_TYPE);
					else
						metaCollectionInstance.update(querySourceObject, updateQuery, false, true);
					logger.info("update completed");
					close();
					return null;
				}else{
					logger.debug("object not found");
					close();
					throw new RemoteBackendException("remote file not found at path: "+remotePath);
				}
			} catch (UnknownHostException e) {
				e.printStackTrace();
				throw new RemoteBackendException("UnknownHostException:  "+e.getMessage());
			}
//		}
	}

	@Override
	public long getFolderTotalItems(String folderPath){
		logger.debug("getFolderTotalItems for folder "+folderPath);
		long totalItems=0;
		try{
			List<GridFSDBFile> list= retrieveRemoteFileObject(folderPath);
			totalItems=getCount(list);
			logger.info("getFolderTotalItems found "+list.size()+" objects for folder "+folderPath);
		}catch(Exception e ){
			close();
			throw new RemoteBackendException(e.getMessage());
		}
		return totalItems;
	}

	@Override
	public long getFolderTotalVolume(String folderPath){
		logger.debug("getFolderTotalVolume for folder "+folderPath);
		long totalVolume=0;
		try{
			List<GridFSDBFile> list= retrieveRemoteFileObject(folderPath);
			totalVolume=getVolume(list);
			logger.info("getFolderTotalVolume  "+totalVolume+" for folder "+folderPath);
		}catch(Exception e ){
			close();
			throw new RemoteBackendException(e.getMessage());
		}
		return totalVolume;
	}
	
	@Override
	public String getUserTotalVolume(String user){
		logger.debug("getUserTotalVolume for folder "+user);
		long volume=0;
		try{
			List<GridFSDBFile> list= retrieveUsersFileObject(user);
			volume=getVolume(list);
			logger.info("getUserTotalVolume found "+volume+" for user "+user);
		}catch(Exception e ){
			close();
			throw new RemoteBackendException(e.getMessage());
		}
		return ""+volume;
	}

	@Override
	public String getUserTotalItems(String user){
		logger.debug("getUserTotalItems for folder "+user);
		long count=0;
		try{
			List<GridFSDBFile> list= retrieveUsersFileObject(user);
			logger.info("getUserTotalItems found "+list.size()+" objects for user "+user);
			count=getCount(list);
		}catch(Exception e ){
			close();
			throw new RemoteBackendException(e.getMessage());
		}
		return ""+count;
	}
	
	private List<GridFSDBFile> retrieveRemoteFileObject( String folderPath) {
		GridFS gfs = new GridFS(io.getDB(null, null));
		BasicDBObject queryFile = new BasicDBObject();
		queryFile.put("dir", java.util.regex.Pattern.compile(folderPath+"*"));
		List<GridFSDBFile> list=gfs.find(queryFile);
		logger.info("retrieveRemoteFileObject found "+list.size()+" objects ");
		close();
		return list;		
	}
	
	private List<GridFSDBFile> retrieveUsersFileObject(String username){
		GridFS gfs = new GridFS(io.getDB(null, null));
		BasicDBObject queryFile = new BasicDBObject();
		queryFile.put("owner", username);
		List<GridFSDBFile> list=gfs.find(queryFile);
		logger.info("retrieveUsersFileObjectfound "+list.size()+" objects ");
		close();
		return list;		
	}
	
	
	private long getCount(List<GridFSDBFile> list){
		return list.size();
	}

	private long getVolume(List<GridFSDBFile> list){
		long partialVolume=0;
		for(GridFSDBFile f : list){
			long fileVolume=f.getLength();
			partialVolume=partialVolume+fileVolume;
		}
		return partialVolume;
	}

	@Override
	public String getId(String path, boolean forceCreation){
		ObjectId id=null;
		if(logger.isDebugEnabled())
			logger.debug("MongoDB - pathServer: "+path);
		GridFSDBFile f = io.retrieveRemoteObject(path, true);
		if(f!=null){
			id=(ObjectId)f.getId();
		}else if(forceCreation){
			logger.warn("The remote file doesn't exist. An empty file will be created");
	// if the file doesn't exist. An empty file will be created
			if(!ObjectId.isValid(path)){
				byte[] data=new byte[1];
				GridFSInputFile f2 = null;
				if (path.startsWith("/VOLATILE")){
					f2=io.createGFSFileObject(data);//gfs.createFile(data);
				}else{
					f2=io.createGFSFileObject(data, null, null);//gfs.createFile(data);
				}
				
				int indexName=path.lastIndexOf(ServiceEngine.FILE_SEPARATOR);
				String name=path.substring(indexName+1);
				String dir=path.substring(0, indexName+1);
				f2.put("filename", path);
				f2.put("name", name);
				f2.put("dir", dir);
				id=(ObjectId)f2.getId();
				f2.save();
				close();
			}else{
				logger.error("Cannot force creation of smp uri without a remote path. The input parameter is not a remotePath valid: "+path);
				close();
				throw new RemoteBackendException("The uri is not created. Cannot force creation of smp uri without a remote path. The input parameter is not a remotePath:  "+path);
			}
		}else{
			close();
			throw new RemoteBackendException("the file "+path+" is not present on storage. The uri is not created ");
		}
		close();
		return id.toString();
	}
	
	@Override
	public boolean isValidId(String id){
		return ObjectId.isValid(id);
	}
	
	@Override
	public String getRemotePath(String bucket) throws UnknownHostException{
		if(!ObjectId.isValid(bucket))
			throw new RuntimeException("The following id is not valid: "+bucket);
		String path=null;
		path=getField(bucket, "filename");
		return path;
	}
	
	@Override
	public String getField(String remoteIdentifier, String fieldName) throws UnknownHostException {
		String fieldValue=null;
		if(logger.isDebugEnabled())
			logger.debug("MongoDB - pathServer: "+remoteIdentifier);
		GridFSDBFile f = io.retrieveRemoteObject(remoteIdentifier, true);
		if(f!=null){
			fieldValue=f.get(fieldName).toString();
		}
		close();
		return fieldValue;
	}
	
	private BasicDBObject setCommonFields(BasicDBObject f, MyFile resource, OPERATION op) {
		String owner=resource.getOwner();
		if(op == null){
			op=resource.getOperationDefinition().getOperation();
		}
		logger.info("set last operation: "+op);
		String from=null;
		if(op.toString().equalsIgnoreCase(OPERATION.MOVE.toString())){
			from=resource.getLocalPath();
		}
		String address=null;
		try {
			address=InetAddress.getLocalHost().getCanonicalHostName().toString();
			f.put("callerIP", address);
			
		} catch (UnknownHostException e) {	}
		if(from == null)
			f.append("lastAccess", DateUtils.now("dd MM yyyy 'at' hh:mm:ss z")).append("lastUser", owner).append("lastOperation", op.toString()).append("callerIP", address);
		else
			f.append("lastAccess", DateUtils.now("dd MM yyyy 'at' hh:mm:ss z")).append("lastUser", owner).append("lastOperation", op.toString()).append("callerIP", address).append("from", from);
			return f;
	}
	
	private void updateCommonFields(DBObject f, MyFile resource, OPERATION op) {
		f.put("lastAccess", DateUtils.now("dd MM yyyy 'at' hh:mm:ss z"));
		String owner=resource.getOwner();
		f.put("lastUser", owner);
		if(op == null){
			op=resource.getOperationDefinition().getOperation();
		}
		logger.info("set last operation: "+op);
		f.put("lastOperation", op.toString());
		if(op.toString().equalsIgnoreCase(OPERATION.MOVE.toString())){
			f.put("from", resource.getLocalPath());
		}
		String address=null;
		try {
			address=InetAddress.getLocalHost().getCanonicalHostName().toString();
			f.put("callerIP", address);
		} catch (UnknownHostException e) {	}
	}




	/**
	 * @param f mongo gridfs file identity
	 * @throws IllegalAccessError
	 */
	private void checkTTL(GridFSDBFile f) throws IllegalAccessError {
		if((f.containsField("timestamp")) && (f.get("timestamp")!= null)){
			long timestamp=(Long)f.get("timestamp");
			long currentTTL=System.currentTimeMillis() - timestamp;
			close();
			throw new IllegalAccessError("the file is locked currentTTL: "+currentTTL+"TTL bound "+ServiceEngine.TTL);
		}else{
			checkTTL(f);
		}
	}

	/**
	 * @param f mongo gridfs file identity
	 * @return
	 */
	private boolean isTTLUnlocked(GridFSDBFile f) {
		if(f.get("timestamp")==null)
			return true;
		long timestamp=(Long)f.get("timestamp");
		logger.debug("timestamp found: "+timestamp);
		if(timestamp != 0){
			long currentTTL=System.currentTimeMillis() - timestamp;
			logger.debug("currentTTL: "+currentTTL+" TTL stabilito: "+ServiceEngine.TTL);
			if(ServiceEngine.TTL < currentTTL){
				f.put("timestamp", null);
				return true;
			}else{
				return false;
			}
		}else
			return true;
	}

}
