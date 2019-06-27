package org.gcube.contentmanagement.blobstorage.transport.backend;


import org.bson.types.ObjectId;

import java.io.FileNotFoundException;
import java.io.IOException;
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
import org.gcube.contentmanagement.blobstorage.service.operation.*;
import org.gcube.contentmanagement.blobstorage.transport.TransportManager;
import org.gcube.contentmanagement.blobstorage.transport.backend.MongoIOManager;
import org.gcube.contentmanagement.blobstorage.transport.backend.util.Costants;
import org.gcube.contentmanagement.blobstorage.resource.StorageObject;
import org.gcube.contentmanagement.blobstorage.resource.OperationDefinition.REMOTE_RESOURCE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.MongoException;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

/**
 * MongoDB transport layer
 * @author Roberto Cirillo (ISTI - CNR)
 *
 */
public class MongoOperationManager extends TransportManager{
	/**
	 * Logger for this class
	 */
	final Logger logger = LoggerFactory.getLogger(MongoOperationManager.class);
//	private MongoClient mongo;
	private MongoIOManager mongoPrimaryInstance;
	private MongoIOManager mongoSecondaryInstance;
	private MemoryType memoryType;
	protected static String[] dbNames;

	
	public MongoOperationManager(String[] server, String user, String password, MemoryType memoryType, String[] dbNames,String writeConcern, String readConcern){
		initBackend(server,user,password, memoryType,dbNames, writeConcern, readConcern);
	}

	
	@Override
	public void initBackend(String[] server, String user, String pass, MemoryType memoryType , String[] dbNames, String writeConcern, String readConcern) {
		try {
			this.memoryType=memoryType;
			MongoOperationManager.dbNames=dbNames;
			logger.debug("check mongo configuration");
			if (dbNames!=null){
				if(dbNames.length==1){
					logger.info("found one mongo db to connect");
					mongoPrimaryInstance= getMongoInstance(server, user, pass, memoryType, dbNames[0], writeConcern, readConcern);
				}else if (dbNames.length== 0){
					
					logger.warn("primary db not discovered correctly. Backend will be instantiated with default value");
					mongoPrimaryInstance= getMongoInstance(server, user, pass, memoryType, null, writeConcern, readConcern);
				} else if (dbNames.length== 2){
					logger.info("found two mongo db to connect");
					mongoPrimaryInstance= getMongoInstance(server, user, pass, memoryType, dbNames[0], writeConcern, readConcern);
					mongoSecondaryInstance=getMongoInstance(server, user, pass, memoryType, dbNames[1], writeConcern, readConcern);
				}else{
					throw new RuntimeException("Found more than 2 collection on the ServiceEndopint. This case is not managed");
				}
			}else{
				logger.debug("primary db not discovered. Backend will be instantiated with default value");
				mongoPrimaryInstance= getMongoInstance(server, user, pass, memoryType, null, writeConcern, readConcern);
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		}
		
	}
	
	private MongoIOManager getMongoInstance(String[] server, String user, String password, MemoryType memoryType, String dbName, String writeConcern, String readPreference)
			throws UnknownHostException {
		MongoIOManager mongoInstance=new MongoIOManager(server, user, password, memoryType, dbName, writeConcern, readPreference);//MongoIO.getInstance(server, user, password);
		mongoInstance.clean();
		DBCollection coll =mongoInstance.getMetaDataCollection();// io.getDB().getCollection("fs.files");
		coll.createIndex(new BasicDBObject("filename", 1));  // create index on "filename", ascending
		coll.createIndex(new BasicDBObject("dir", 1));  // create index on "filename", ascending
		coll.createIndex(new BasicDBObject("owner", 1));  // create index on "owner", ascending
		return mongoInstance;
	}
	

	
	/**
	 * @param serverLocation can be a path remote on the cluster or a object id 
	 * @throws IOException 
	 */
	@Override
	public ObjectId get(Download download) throws IOException {
		return download.execute(mongoPrimaryInstance, mongoSecondaryInstance);
	}


/**
 * return the key that permits the object's unlock
 * @throws IOException 
 */	
	@Override
	public String lock(Lock lock) throws Exception {
		return lock.execute(mongoPrimaryInstance, mongoSecondaryInstance, lock.getResource(), lock.getBucket());
	}

	
	
	@Override
	public String put(Upload upload) throws IOException {
		return upload.execute(mongoPrimaryInstance, mongoSecondaryInstance, upload.getResource(), upload.getBucket(), upload.isReplaceOption());
	}

	public void close() {
		mongoPrimaryInstance.close();
//		mongoSecondaryInstance.close();
	}
	
	/**
	 * Unlock the object specified, this method accept the key field for the unlock operation
	 * @throws FileNotFoundException 
	 * @throws UnknownHostException 
	 */
	@Override
	public String unlock(Unlock unlock) throws Exception {
		return unlock.execute(mongoPrimaryInstance, mongoSecondaryInstance,unlock.getResource(), unlock.getBucket(), unlock.getKeyUnlock());
	}
	
	@Override
	public Map<String, StorageObject> getValues(MyFile resource, String bucket, Class<? extends Object> type){
		Map<String, StorageObject> map=null;
		try{
			OperationDefinition op=resource.getOperationDefinition();
			logger.info("MongoClient getValues method: "+op.toString());
//			DB db=mongoPrimaryInstance.getConnectionDB(resource.getWriteConcern(), resource.getReadPreference(), getPrimaryCollectionName(), true);
			GridFS gfs = mongoPrimaryInstance.getGfs(getPrimaryCollectionName(), true);  
			if(logger.isDebugEnabled()){
				logger.debug("Mongo get values of dir: "+bucket);
			}
			
			BasicDBObject query = new BasicDBObject();
			query.put("dir", bucket);
			List<GridFSDBFile> list = gfs.find(query);
	// Patch for incompatibility v 1-2
			list=mongoPrimaryInstance.patchRemoteDirPathVersion1(bucket, gfs, query, list);
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
		logger.info("Check file: "+bucket+ " for removing operation");
		GridFSDBFile f=mongoPrimaryInstance.retrieveRemoteDescriptor(bucket, null, true);
		if(f!=null){
			mongoPrimaryInstance.checkAndRemove(f, resource);
		}else{
			if(logger.isDebugEnabled())
				logger.debug("File Not Found. Try to delete by ObjectID");
			if(bucket.length()>23){
				ObjectId id=new ObjectId(bucket);
				GridFSDBFile fID=mongoPrimaryInstance.findGFSCollectionObject(id);
				if(fID != null){
					mongoPrimaryInstance.checkAndRemove(fID, resource);
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
		patchCompatibilityOldLibraryVersion(remoteDir, dirs);
	// end patch	
//		DB db=mongoPrimaryInstance.getConnectionDB(resource.getWriteConcern(), resource.getReadPreference(),getPrimaryCollectionName(), true);
		GridFS gfs =mongoPrimaryInstance.getGfs(getPrimaryCollectionName(), true);//new GridFS(db);  
		for(String directory : dirs){
			if(logger.isDebugEnabled())
				logger.debug("Mongo start operation delete bucket: "+directory);
	// remove subfolders
			if(logger.isDebugEnabled())
				logger.debug("remove subfolders of folder: "+directory);
			BasicDBObject query = new BasicDBObject();
			String regex=directory+"*";
			query.put("dir", java.util.regex.Pattern.compile(regex));
			mongoPrimaryInstance.removeObject(gfs, query,resource);
			query=new BasicDBObject();
			String[] dir=directory.split(Costants.FILE_SEPARATOR);
			StringBuffer parentDir=new StringBuffer();
			for(int i=0;i<dir.length-1;i++){
				parentDir.append(dir[i]+Costants.FILE_SEPARATOR);
			}
			String name=dir[dir.length-1];
			query.put("dir", parentDir.toString());
			query.put("name", name);
			if(logger.isDebugEnabled())
				logger.debug("now remove the folder: "+name+" from folder "+parentDir);
			mongoPrimaryInstance.removeObject(gfs, query, resource);
			if(logger.isDebugEnabled())
				logger.debug("Mongo end operation delete bucket: "+directory);
		}
		close();
		
	}


	private void patchCompatibilityOldLibraryVersion(String remoteDir, ArrayList<String> dirs) {
		if((remoteDir.contains(Costants.ROOT_PATH_PATCH_V1)) || (remoteDir.contains(Costants.ROOT_PATH_PATCH_V2))){
			if(remoteDir.contains(Costants.ROOT_PATH_PATCH_V1)){
				String remoteDirV1=remoteDir.replace(Costants.ROOT_PATH_PATCH_V1, Costants.ROOT_PATH_PATCH_V2);
				dirs.add(remoteDirV1);
			}else{
				String remoteDirV2= remoteDir.replace(Costants.ROOT_PATH_PATCH_V2, Costants.ROOT_PATH_PATCH_V1);
				dirs.add(remoteDirV2);
				String remoteDirV2patch=Costants.FILE_SEPARATOR+remoteDirV2;
				dirs.add(remoteDirV2patch);
			}
		}
	}

	@Override
	public long getSize(String remotePath){
		long length=-1;
		if(logger.isDebugEnabled())
			logger.debug("MongoDB - get Size for pathServer: "+remotePath);
		GridFSDBFile f = mongoPrimaryInstance.retrieveRemoteDescriptor(remotePath, null, true);
		if(f!=null){
			length=f.getLength();
		}
		close();
		return length;
	}
	
	@Override
	public boolean exist(String remotePath){
		boolean isPresent=false;
		if(logger.isDebugEnabled())
			logger.debug("MongoDB - get Size for pathServer: "+remotePath);
		GridFSDBFile f = mongoPrimaryInstance.retrieveRemoteDescriptor(remotePath, null, true);
		if(f!=null){
			isPresent=true;
		}
		close();
		return isPresent;
	}

	@Override
	public long getTTL(String remotePath) throws UnknownHostException{
		long timestamp=-1;
		long currentTTL=-1;
		long remainsTTL=-1;
		if(logger.isDebugEnabled())
			logger.debug("MongoDB - pathServer: "+remotePath);
		GridFSDBFile f=mongoPrimaryInstance.retrieveRemoteDescriptor(remotePath, null, true);
		if(f!=null){
			timestamp=(Long)f.get("timestamp");
			if(timestamp > 0){
				currentTTL=System.currentTimeMillis() - timestamp;
				remainsTTL=Costants.TTL- currentTTL;
			}
			
		}
		close();
		return remainsTTL;
	}

	@Override
	public long renewTTL(MyFile resource) throws UnknownHostException, IllegalAccessException{
		long ttl=-1;
		MyFile file=(MyFile)resource;
		REMOTE_RESOURCE remoteResourceIdentifier=file.getOperation().getRemoteResource();
		String key=file.getLockedKey();
		String remotePath=file.getRemotePath();
		GridFSDBFile f=mongoPrimaryInstance.retrieveRemoteDescriptor(remotePath, remoteResourceIdentifier, true);
		if(f!=null){
			  String lock=(String)f.get("lock");
  	         //check if the od file is locked		  
  	         if((lock !=null) && (!lock.isEmpty())){
  	        	 String lck=(String)f.get("lock");
  	        	 if(lck.equalsIgnoreCase(key)){
  	        		if((f.containsField("countRenew")) && (f.get("countRenew") != null)){ 
  	        			int count=(Integer)f.get("countRenew");
  	        			if(count < Costants.TTL_RENEW){
  	        				f.put("countRenew", count+1);
  	        			}else{
  	        				close();
// number max of ttl renew operation reached. the operation is blocked
  	        				throw new IllegalAccessException("The number max of TTL renew reached. The number max is: "+Costants.TTL_RENEW);
  	        			}
  	        		}else{
//  first renew operation	        			
  	        			f.put("countRenew", 1);
  	        		}
  	        		f.put("timestamp", System.currentTimeMillis());
  	        		f.save();
  	        		ttl=Costants.TTL;
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
	public String link(Link link) throws UnknownHostException{
		return link.execute(mongoPrimaryInstance, mongoSecondaryInstance, link.getResource(), link.getSourcePath(), link.getDestinationPath());
	}

	
	@Override
	public String copy(Copy copy) throws UnknownHostException{
		logger.info("CopyFile operation from "+copy.getSourcePath()+" to "+ copy.getDestinationPath());
		return copy.execute(mongoPrimaryInstance, copy.getResource(), copy.getSourcePath(), copy.getDestinationPath());
	}

	
	@Override
	public String move(Move move) throws UnknownHostException{
		logger.info("MoveFile operation from "+move.getSourcePath()+" to "+ move.getDestinationPath());
		return move.execute(mongoPrimaryInstance, memoryType, move.getResource(), move.getSourcePath(), move.getDestinationPath());
	}
	
	


	@Override
	public String getName() {
		return Costants.DEFAULT_TRANSPORT_MANAGER;
	}



	@Override
	public List<String> copyDir(CopyDir copy) throws UnknownHostException {
		return copy.execute(mongoPrimaryInstance, copy.getResource(), copy.getSourcePath(), copy.getDestinationPath());
	}
		

	@Override
	public List<String> moveDir(MoveDir move) throws UnknownHostException {
		return move.execute(mongoPrimaryInstance, move.getResource(), move.getSourcePath(), move.getDestinationPath(), memoryType);
	}
	

	@Override
	public String getFileProperty(String remotePath, String property){
		GridFSDBFile f = mongoPrimaryInstance.retrieveRemoteDescriptor(remotePath, null, true);
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
	public void setFileProperty(String remotePath, String propertyField, String propertyValue){
		logger.trace("setting field "+propertyField+" with value: "+propertyValue);
		try {
			updateMetaObject(remotePath, propertyField, propertyValue);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			throw new RemoteBackendException("UnknownHostException:  "+e.getMessage());
		}
	}

	/**
	 * This method perform a query to mongodb in order to add a new property to the metadata object
	 * @param remoteIdentifier: objectID or remote path of the remote object
	 * @param propertyField: new field name
	 * @param propertyValue value of the new field
	 * @return
	 * @throws UnknownHostException
	 */
	private void updateMetaObject(String remoteIdentifier, String propertyField, String propertyValue)
			throws UnknownHostException {
		BasicDBObject remoteMetaCollectionObject;
		logger.debug("find object...");
		remoteMetaCollectionObject = mongoPrimaryInstance.findMetaCollectionObject(remoteIdentifier);
		if(remoteMetaCollectionObject!=null){
			logger.debug("object found");
			remoteMetaCollectionObject.put(propertyField, propertyValue);
			logger.info("set query field: "+propertyField+" with value: "+propertyValue);
			BasicDBObject updateQuery= new BasicDBObject();
			updateQuery.put("$set", remoteMetaCollectionObject);
			// retrieve original object		
			BasicDBObject querySourceObject = getQuery(remoteIdentifier);
			//getCollection
			logger.debug("get Collection ");
			DBCollection metaCollectionInstance=mongoPrimaryInstance.getMetaDataCollection(mongoPrimaryInstance.getConnectionDB(getPrimaryCollectionName(), false));
			//update field
			logger.debug("update Collection ");
			if (!(memoryType== MemoryType.VOLATILE))
				metaCollectionInstance.update(querySourceObject, updateQuery, false, true, Costants.DEFAULT_WRITE_TYPE);
			else
				metaCollectionInstance.update(querySourceObject, updateQuery, false, true);
			logger.info("update completed");
			close();
		}else{
			logger.debug("object not found");
			close();
			throw new RemoteBackendException("remote file not found at path: "+remoteIdentifier);
		}
	}

	/**
	 * 
	 * @param remoteIdentifier objectID or remote path of the remote object
	 * @return the BasicDBObject of the remote object
	 */
	private BasicDBObject getQuery(String remoteIdentifier) {
		BasicDBObject  querySourceObject = new BasicDBObject();
		logger.debug("check identifier object: "+remoteIdentifier);
		if(ObjectId.isValid(remoteIdentifier)){
			logger.debug("object is a valid id");
			querySourceObject.put( "_id" , new ObjectId(remoteIdentifier));
		}else{
			logger.debug("object is a remotepath");
			querySourceObject.put( "filename" , remoteIdentifier);
		}
		return querySourceObject;
	}

	@Override
	public long getFolderTotalItems(String folderPath){
		logger.debug("getFolderTotalItems for folder "+folderPath);
		long totalItems=0;
		try{
			List<GridFSDBFile> list= mongoPrimaryInstance.getFilesOnFolder(folderPath);
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
			List<GridFSDBFile> list= mongoPrimaryInstance.getFilesOnFolder(folderPath);
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
			List<GridFSDBFile> list= mongoPrimaryInstance.getOwnedFiles(user);
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
			List<GridFSDBFile> list= mongoPrimaryInstance.getOwnedFiles(user);
			logger.info("getUserTotalItems found "+list.size()+" objects for user "+user);
			count=getCount(list);
		}catch(Exception e ){
			close();
			throw new RemoteBackendException(e.getMessage());
		}
		return ""+count;
	}
	

	
	@Override
	public String getId(String path, boolean forceCreation){
		ObjectId id=null;
		if(logger.isDebugEnabled())
			logger.debug("MongoDB - pathServer: "+path);
		GridFSDBFile f = mongoPrimaryInstance.retrieveRemoteDescriptor(path, null, true);
		if(f!=null){
			id=(ObjectId)f.getId();
		}else if(forceCreation){
			logger.warn("The remote file doesn't exist. An empty file will be created");
	// if the file doesn't exist. An empty file will be created
			id = forceCreation(path, id);
		}else{
			close();
			throw new RemoteBackendException("the file "+path+" is not present on storage. The uri is not created ");
		}
		close();
		return id.toString();
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

	
	private ObjectId forceCreation(String path, ObjectId id) {
		if(!ObjectId.isValid(path)){
			byte[] data=new byte[1];
			GridFSInputFile f2 = null;
			if (path.startsWith("/VOLATILE")){
				f2=mongoPrimaryInstance.createGFSFileObject(data);//gfs.createFile(data);
			}else{
				f2=mongoPrimaryInstance.createGFSFileObject(data, null, null);//gfs.createFile(data);
			}
			
			int indexName=path.lastIndexOf(Costants.FILE_SEPARATOR);
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
		return id;
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
		GridFSDBFile f = mongoPrimaryInstance.retrieveRemoteDescriptor(remoteIdentifier, null, true);
		if(f!=null){
			fieldValue=f.get(fieldName).toString();
		}
		close();
		return fieldValue;
	}
	
	public static String getPrimaryCollectionName(){
		if ((dbNames != null) && (dbNames.length>0))
			return dbNames[0];
		else 
			return null;
	}
	
	protected static String getSecondaryCollectionName(){
		if ((dbNames != null) && (dbNames.length>1))
			return dbNames[1];
		else 
			return null;
	}


	/**
	 * Create a new file with the same remotepath and the suffix -dpl
	 */
	@Override
	public String duplicateFile(DuplicateFile duplicate) {
		return duplicate.execute(mongoPrimaryInstance);
	}
	

	@Override
	public String softCopy(SoftCopy copy) throws UnknownHostException{
		return copy.execute(mongoPrimaryInstance, copy.getResource(), copy.getSourcePath(), copy.getDestinationPath());
	}
	
}
