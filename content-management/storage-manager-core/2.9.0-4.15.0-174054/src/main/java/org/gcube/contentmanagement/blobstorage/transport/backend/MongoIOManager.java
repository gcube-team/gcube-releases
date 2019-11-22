package org.gcube.contentmanagement.blobstorage.transport.backend;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.bson.types.ObjectId;
import org.gcube.contentmanagement.blobstorage.resource.MemoryType;
import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.resource.OperationDefinition;
import org.gcube.contentmanagement.blobstorage.resource.OperationDefinition.OPERATION;
import org.gcube.contentmanagement.blobstorage.resource.OperationDefinition.REMOTE_RESOURCE;
import org.gcube.contentmanagement.blobstorage.service.impl.ServiceEngine;
import org.gcube.contentmanagement.blobstorage.service.operation.Operation;
import org.gcube.contentmanagement.blobstorage.transport.backend.util.Costants;
import org.gcube.contentmanagement.blobstorage.transport.backend.util.DateUtils;
import org.gcube.contentmanagement.blobstorage.transport.backend.util.MongoInputStream;
import org.gcube.contentmanagement.blobstorage.transport.backend.util.MongoOutputStream;
import org.gcube.contentmanagement.blobstorage.transport.backend.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;


public class MongoIOManager {
	
	private DB db;
	private String[] server;
	private MongoClient mongo;
	private String user;
	private String password;
	private Logger logger = LoggerFactory.getLogger(MongoIOManager.class);
	private GridFS gfs; 
	private MemoryType memoryType;
	private String dbName;
	protected String writeConcern;
	protected String readPreference;
	
	protected MongoIOManager(String[] server, String user, String password, MemoryType memoryType, String dbName, String writeConcern, String readPreference){
		setServer(server);
		setUser(user);
		setPassword(password);
		setMemoryType(memoryType);
		setDbName(dbName);
		setWriteConcern(writeConcern);
		setReadPreference(readPreference);
	}
	
	
	public DB getConnectionDB(String dbName, boolean readwritePreferences){
		if(db==null){
			try{
			
					List<ServerAddress> srvList=new ArrayList<ServerAddress>();
					for(String srv : server){
							srvList.add(new ServerAddress(srv));
					}
					if(mongo==null){		
							logger.debug(" open mongo connection ");
							MongoClientOptions options=null;
							if ((!Utils.isVarEnv(Costants.NO_SSL_VARIABLE_NAME)) && (Costants.DEFAULT_CONNECTION_MODE.equalsIgnoreCase("SSL"))){
//			 					for enable SSL use the following instructions	
//								System.setProperty("javax.net.ssl.trustStore", "/usr/local/lib/jvm/jdk1.8.0_151/jre/lib/security/cacerts");
//								System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
								options=MongoClientOptions.builder().sslEnabled(true).sslInvalidHostNameAllowed(true).connectionsPerHost(Costants.CONNECTION_PER_HOST).connectTimeout(Costants.CONNECT_TIMEOUT).build();
							}else{
								if((Costants.DEFAULT_CONNECTION_MODE.equalsIgnoreCase("NO-SSL")) || (Utils.checkVarEnv(Costants.NO_SSL_VARIABLE_NAME).equalsIgnoreCase("TRUE"))){
//									for disable ssl use the following instruction		
									options=MongoClientOptions.builder().connectionsPerHost(Costants.CONNECTION_PER_HOST).connectTimeout(Costants.CONNECT_TIMEOUT).build();
								}else{
									options=MongoClientOptions.builder().sslEnabled(true).sslInvalidHostNameAllowed(true).connectionsPerHost(Costants.CONNECTION_PER_HOST).connectTimeout(Costants.CONNECT_TIMEOUT).build();
								}
							}
							if(((password != null) && (password.length() >0))  && ((user != null) && (user.length() > 0))){
								MongoCredential credential = MongoCredential.createCredential(user, dbName, password.toCharArray());
								mongo = new MongoClient(srvList, Arrays.asList(credential), options);
							}else{
								mongo = new MongoClient(srvList, options);
							}
							logger.debug("Istantiate MongoDB with options: "+mongo.getMongoClientOptions());
					}
					db = mongo.getDB(dbName);
					if((readwritePreferences) && (!(memoryType== MemoryType.VOLATILE)) && (srvList.size()>1)){
						if(writeConcern!=null){
							WriteConcern write=new WriteConcern(Integer.parseInt(writeConcern));
							db.setWriteConcern(write);
						}else{
							db.setWriteConcern(Costants.DEFAULT_WRITE_TYPE);
						}
						if(readPreference!=null){
							ReadPreference read=ReadPreference.valueOf(readPreference);
							db.setReadPreference(read);
						}else{
							db.setReadPreference(Costants.DEFAULT_READ_PREFERENCE);
						}
					}
			} catch (Exception e) {
				close();
				logger.error("Problem to open the DB connection for gridfs file ");
				throw new RemoteBackendException("Problem to open the DB connection: "+ e.getMessage());
			}
			logger.info("new mongo connection pool opened");
			
		}
		return db;
	}
//PATCHED METHODS
	protected ObjectId getRemoteObject(GridFS gfs, MyFile resource, GridFSDBFile f) throws IOException, IllegalAccessError {
		ObjectId id;
		id=(ObjectId)f.getId();
		String lock=(String)f.get("lock");
		if((lock==null || lock.isEmpty()) || (isTTLUnlocked(f))){
			if((f.containsField("lock")) && (f.get("lock") != null)){
				f.put("lock", null);
				f.save();
			}
			download(gfs, resource, f, false);
		}else{
			checkTTL(f);
		}
		return id;
	}
	
	public ObjectId getRemoteObject(MyFile resource, GridFSDBFile f) throws IOException, IllegalAccessError {
		ObjectId id;
		id=(ObjectId)f.getId();
		String lock=(String)f.get("lock");
		if((lock==null || lock.isEmpty()) || (isTTLUnlocked(f))){
			if((f.containsField("lock")) && (f.get("lock") != null)){
				f.put("lock", null);
				f.save();
			}
			download(resource, f, false);
		}else{
			checkTTL(f);
		}
		return id;
	}
	/**
	 *  Unused feature
	 * @param f
	 * @return
	 */
	@Deprecated
	public boolean isTTLUnlocked(GridFSDBFile f) {
		if(f.get("timestamp")==null)
			return true;
		long timestamp=(Long)f.get("timestamp");
		logger.debug("timestamp found: "+timestamp);
		if(timestamp != 0){
			long currentTTL=System.currentTimeMillis() - timestamp;
			logger.debug("currentTTL: "+currentTTL+" TTL stabilito: "+Costants.TTL);
			if(Costants.TTL < currentTTL){
				f.put("timestamp", null);
				return true;
			}else{
				return false;
			}
		}else
			return true;
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
		while((f !=null ) && (f.containsField(Costants.LINK_IDENTIFIER)) && (f.get(Costants.LINK_IDENTIFIER) != null)){
			BasicDBObject query = new BasicDBObject();
			query.put( "_id" , new ObjectId((String)f.get(Costants.LINK_IDENTIFIER)) );
//			query.put( "_id" , f.get(Costants.LINK_IDENTIFIER) );
			f=gfs.findOne( query );
		}
		updateCommonFields(f, resource, OPERATION.DOWNLOAD);
		f.save();
		if((resource.getLocalPath()!=null) && (!resource.getLocalPath().isEmpty())){
			readByPath(resource, f, isLock, 0);
			close();
		}else if(resource.getOutputStream()!=null){
			readByOutputStream(resource, f, isLock, 0);
			close();
		}
		if((resource!=null) && (resource.getType()!=null) && resource.getType().equalsIgnoreCase("input")){
			readByInputStream(resource, f, isLock, 0);
		}
	}
	
	
	/**
	 * @param resource
	 * @param f
	 * @param isLock indicates if the file must be locked
	 * @throws IOException 
	 */
	private void download( MyFile resource, GridFSDBFile f, boolean isLock) throws IOException {
		OperationDefinition op=resource.getOperationDefinition();
		logger.info("MongoClient download method: "+op.toString());
// if contains the field link it means that is a link hence I follow ne or more links		
		while((f !=null ) && (f.containsField(Costants.LINK_IDENTIFIER)) && (f.get(Costants.LINK_IDENTIFIER) != null)){
			BasicDBObject query = new BasicDBObject();
			query.put( "_id" , new ObjectId((String)f.get(Costants.LINK_IDENTIFIER)) );
//			query.put( "_id" , f.get(Costants.LINK_IDENTIFIER) );
			f=getGfs().findOne( query );
		}
		updateCommonFields(f, resource, OPERATION.DOWNLOAD);
		f.save();
		if((resource.getLocalPath()!=null) && (!resource.getLocalPath().isEmpty())){
			readByPath(resource, f, isLock, 0);
			close();
		}else if(resource.getOutputStream()!=null){
			readByOutputStream(resource, f, isLock, 0);
			close();
		}
		if((resource!=null) && (resource.getType()!=null) && resource.getType().equalsIgnoreCase("input")){
			readByInputStream(resource, f, isLock, 0);
		}
	}

	public void updateCommonFields(DBObject f, MyFile resource, OPERATION op) {
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


	public ObjectId removeFile(Object resource, String key, boolean replace,
			ObjectId oldId, GridFSDBFile fold) throws IllegalAccessError,
			UnknownHostException {
		logger.info("removing object with id: "+resource);
		//remove old object			
					String oldir=(String)fold.get("dir");
			        if(logger.isDebugEnabled()){
			      	  logger.debug("old dir  found "+oldir);
			        }
			        logger.info("remove old object if replace is true and the file is not locked");
 /* 20180409 removed if cause new StorageObject could not have the dir set */      
//			        if((oldir !=null) &&(oldir.equalsIgnoreCase(((MyFile)resource).getRemoteDir()))){
			  // if the file contains a link the replace is not allowed
			         	  if((!replace)){
			         		  return oldId;
			         	  }else if((fold.containsField(Costants.COUNT_IDENTIFIER)) && (fold.get(Costants.COUNT_IDENTIFIER)!=null)){
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
//			        }else if(oldir == null){
//			        	if((!replace) && (oldId!= null)){
//			        		return oldId;
//			        	}
//			        }
		return oldId;
	}
	
	
	public ObjectId checkAndRemove(GridFSDBFile f, MyFile resource){
		String idToRemove=f.getId().toString();
		logger.info("check and remove object with id "+idToRemove+" and path: "+f.get("filename"));
		ObjectId idFile=null;
		if(logger.isDebugEnabled())
			logger.debug("fileFound\t remove file");
		updateCommonFields(f, resource, OPERATION.REMOVE);
// check if the file is linked		
		if((f!=null) && (f.containsField(Costants.COUNT_IDENTIFIER)) && (f.get(Costants.COUNT_IDENTIFIER) != null)){
		// this field is only added for reporting tool: storage-manager-trigger	
			String filename=(String)f.get("filename");
			f.put("onScope", filename);
// remove metadata: dir, filename, name 	
			f.put("dir", null);
			f.put("filename", null);
			f.put("name", null);
			f.put("onDeleting", "true");
			f.save();
	// check if the file is a link 
		}else if((f.containsField(Costants.LINK_IDENTIFIER)) && (f.get(Costants.LINK_IDENTIFIER) != null )){
			while((f!=null) && (f.containsField(Costants.LINK_IDENTIFIER)) && (f.get(Costants.LINK_IDENTIFIER) != null )){
				// remove f and decrement linkCount field on linked object
				String id=(String)f.get(Costants.LINK_IDENTIFIER);
				GridFSDBFile fLink=findGFSCollectionObject(new ObjectId(id));
			    int linkCount=(Integer)fLink.get(Costants.COUNT_IDENTIFIER);
			    linkCount--;
			    if(linkCount == 0){
		// if the name the filename and dir are null, then I delete also the link object	    	
			    	if((fLink.get("name")==null ) && (fLink.get("filename")==null ) && (fLink.get("dir")==null )){
		    			ObjectId idF=(ObjectId) f.getId();
		    			idFile=idF;
		    // this field is an advice for oplog collection reader			 
						 removeGFSFile(f, idF);
			    		if((fLink.containsField(Costants.LINK_IDENTIFIER)) && (fLink.get(Costants.LINK_IDENTIFIER) != null )){
			    	//the link is another link		
							id=(String)fLink.get(Costants.LINK_IDENTIFIER);
							f=findGFSCollectionObject(new ObjectId(id));
			    		}else{
			    	// the link is not another link		
			    			f=null;
			    		}
			    		ObjectId idLink=(ObjectId)fLink.getId();
			    		idFile=idLink;
			    		removeGFSFile(fLink, idLink);
			    	}else{
			    		fLink.put(Costants.COUNT_IDENTIFIER, null);
			    		fLink.save();
						ObjectId oId=(ObjectId) f.getId();
						idFile=oId;
						removeGFSFile(f, oId);
						f=null;
			    	}
			    }else{
			    	fLink.put(Costants.COUNT_IDENTIFIER, linkCount);
			    	fLink.save();
					ObjectId oId=(ObjectId) f.getId();
					idFile=oId;
     				removeGFSFile(f, oId);
     				f=null;
			    }
			}
		}else{
			logger.info("removing file with id: "+idToRemove);
			 idFile=new ObjectId(idToRemove);
			 removeGFSFile(f, new ObjectId(idToRemove));
		}
		return idFile;
	}


	
	/**
	 * @param f mongo gridfs file identity
	 * @throws IllegalAccessError
	 */
	public void checkTTL(GridFSDBFile f) throws IllegalAccessError {
		if((f.containsField("timestamp")) && (f.get("timestamp")!= null)){
			long timestamp=(Long)f.get("timestamp");
			long currentTTL=System.currentTimeMillis() - timestamp;
			close();
			throw new IllegalAccessError("the file is locked currentTTL: "+currentTTL+"TTL bound "+Costants.TTL);
		}else{
			checkTTL(f);
		}
	}

	public ObjectId createNewFile(Object resource, String bucket, String dir,
			String name, ObjectId oldId) throws UnknownHostException {
			ObjectId id;
		// create new dir
			if((dir !=null && !dir.isEmpty()) && (bucket !=null && !bucket.isEmpty())){
				buildDirTree(getMetaDataCollection(null), dir);
			}
		//create new file with specified id
		    GridFSInputFile f2 = writePayload(resource, 0, bucket, name, dir, oldId);
		    id=(ObjectId)f2.getId();
		    logger.info("new file created with id: "+id);
		return id;
	}



	protected GridFSInputFile writePayload(Object resource, int count, String bucket, String name, String dir, ObjectId idFile){
		GridFSInputFile f2=null;
	//maybe this close is not needed	
//		clean();
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
			if(count < Costants.CONNECTION_RETRY_THRESHOLD){
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

	
	protected GridFSInputFile writeByLocalFilePath(Object resource,
			String bucket, String name, String dir, ObjectId idFile)
			throws IOException {
		GridFSInputFile f2;
		if(!(memoryType== MemoryType.VOLATILE))
			f2 = createGFSFileObject(new File(((MyFile)resource).getLocalPath()), ((MyFile)resource).getWriteConcern(), ((MyFile)resource).getReadPreference());
		else
			f2 = createGFSFileObject(new File(((MyFile)resource).getLocalPath()));
		fillInputFile(resource, bucket, name, dir, f2, idFile);
		saveGFSFileObject(f2);
		return f2;
	}

	protected GridFSInputFile writeByOutputStream(Object resource,
			String bucket, String name, String dir, ObjectId idFile) throws IOException {
		GridFSInputFile f2;
		if(!(memoryType== MemoryType.VOLATILE))
			f2 = createGFSFileObject(((MyFile)resource).getName(), ((MyFile)resource).getWriteConcern(), ((MyFile)resource).getReadPreference());
		else
			f2 = createGFSFileObject(((MyFile)resource).getName());
		fillInputFile(resource, bucket, name, dir, f2, idFile);
		((MyFile)resource).setOutputStream(new MongoOutputStream(mongo, f2.getOutputStream()));
		return f2;
	}

	protected GridFSInputFile writeByInputStream(Object resource,
			String bucket, String name, String dir, ObjectId idFile)
			throws IOException {
		GridFSInputFile f2;
		if(!(memoryType== MemoryType.VOLATILE))
			f2 = createGFSFileObject(((MyFile)resource).getInputStream(), ((MyFile)resource).getWriteConcern(),((MyFile)resource).getReadPreference());
		else
			f2 = createGFSFileObject(((MyFile)resource).getInputStream());
		fillInputFile(resource, bucket, name, dir, f2, idFile);
		saveGFSFileObject(f2);
		((MyFile)resource).getInputStream().close();
		((MyFile)resource).setInputStream(null);
		return f2;
	}



	
	
	protected void fillInputFile(Object resource, String bucket, String name,	String dir, GridFSInputFile f2, ObjectId id) {
		if(id != null)
			f2.put("_id", new ObjectId(id.toString()));
		if((bucket != null) &&(bucket.contains("/")))
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
	 * @param gfs
	 * @param query
	 * @throws UnknownHostException 
	 */
	protected void removeObject(GridFS gfs, BasicDBObject query, MyFile resource){
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


	public void setGenericProperties(MyFile resource, String destination,
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

	public BasicDBObject setGenericMoveProperties(MyFile resource, String filename, String dir,
			String name, BasicDBObject f) {
		f.append("filename", filename).append("type", "file").append("name", name).append("dir", dir);
		return f;
	}

	
	public ObjectId updateId(ObjectId oldId, ObjectId newId) throws UnknownHostException {
		logger.info("retrieve object with id: "+oldId);
		// update chunks		
		updateChunksCollection(oldId, newId);
				// update fs files collection
		replaceObjectIDOnMetaCollection(oldId, newId);

		return newId;
	}


	protected void replaceObjectIDOnMetaCollection(ObjectId oldId, ObjectId newId)
			throws UnknownHostException {
		BasicDBObject oldIdQuery= new BasicDBObject();
		oldIdQuery.put("_id", oldId);
		String collectionName= Costants.DEFAULT_META_COLLECTION;
		DBCollection dbc=getCollection(null, collectionName);
		DBObject obj=findCollectionObject(dbc, oldIdQuery);// or multiple objects?
		obj.put("_id", newId);
//		dbc.dropIndex("_id");
		if (!(memoryType== MemoryType.VOLATILE)){
			dbc.remove(oldIdQuery, Costants.DEFAULT_WRITE_TYPE);
			dbc.insert(obj, Costants.DEFAULT_WRITE_TYPE);
		}else{
			dbc.remove(oldIdQuery);
			dbc.insert(obj);
		}
	}

	public void updateChunksCollection(ObjectId oldId, ObjectId newId)
			throws UnknownHostException {
		DBCollection dbc;
		// update fs.chunks collection		
		logger.info("update chunks collection. Change file_id from "+oldId+" to "+newId);
		BasicDBObject searchQuery=new BasicDBObject().append("files_id", oldId);
//		searchQuery.put("files_id", oldId);
		BasicDBObject queryNewFileId=new BasicDBObject().append("$set",new BasicDBObject().append("files_id", newId));
		String chunksCollectionName=Costants.DEFAULT_CHUNKS_COLLECTION;
		dbc=getCollection(null, chunksCollectionName);
//		if (!(memoryType== MemoryType.VOLATILE))
//			dbc.updateMulti(searchQuery, queryNewFileId);//(searchQuery, queryNewFileId, true, true, MongoIOManager.DEFAULT_WRITE_TYPE);
//		else
			dbc.update(searchQuery, queryNewFileId, true, true);
	}
	

	
	
	
	
// END PATCHED METHODS
	
	protected DBCollection getMetaDataCollection() throws UnknownHostException{
		if(db==null)
			this.db=getConnectionDB( dbName, true);
		return db.getCollection(Costants.DEFAULT_META_COLLECTION);
	}
	
	public DBCollection getMetaDataCollection(DB db) throws UnknownHostException{
		if(db==null){
			this.db=getConnectionDB(dbName, true);
			return this.db.getCollection(Costants.DEFAULT_META_COLLECTION);
	}else{
		return db.getCollection(Costants.DEFAULT_META_COLLECTION);
	}
	}
	
	protected DBCollection getCollection(DB db, String collectionName) throws UnknownHostException{
		if(db==null){
			this.db=getConnectionDB(dbName, false);
			return this.db.getCollection(collectionName);
		}else{
			return db.getCollection(collectionName);
		}
	}

	/**
	 * 
	 * @param serverLocation serverpath or objectid that identifies the resource
	 * @param retry if true a retry mechanism is performed
	 * @return resource descriptor
	 */
	public GridFSDBFile retrieveRemoteDescriptor(String serverLocation, REMOTE_RESOURCE remoteResourceIdentifier, boolean retry){
		logger.info("MongoDB - retrieve object from pathServer: "+serverLocation);
		GridFSDBFile f=null;
		try{
			GridFS gfs = new GridFS(getConnectionDB( dbName, true)); 
			//check if the variable remotePath is a valid object id	
			if(ObjectId.isValid(serverLocation)){
				try{
					BasicDBObject query = new BasicDBObject();
					query.put( "_id" , new ObjectId(serverLocation) );
					f=gfs.findOne( query );
				}catch(Exception e){
					logger.warn("the file "+serverLocation+" is not a valid objectId "+e.getMessage());
					f=null;
				}
				if((retry && (f==null))){
					int i=0;
					while((f== null) && (i <Costants.CONNECTION_RETRY_THRESHOLD)){
						logger.info(" retry to search file "+serverLocation);
						Thread.sleep(500);
						try{
							BasicDBObject query = new BasicDBObject();
							query.put( "_id" , new ObjectId(serverLocation) );
							f=gfs.findOne( query );
							i++;
						}catch(Exception e){
							logger.warn("the file "+serverLocation+" is not a valid objectId "+e.getMessage());
							f=null;
						}
					}
		// if the remote identifier is not a specified as ID, try to check if it is a valid remote path 
		// in this case the remote identifier is a valid objectID but it indicates a path
				}else if ((remoteResourceIdentifier != null) && (!(remoteResourceIdentifier.equals(REMOTE_RESOURCE.ID))) && (f==null)){
					f=gfs.findOne(serverLocation);
					f = retryAsAPath(serverLocation, true, f, gfs);
				}
			}else{
				logger.info("remote object is not a validID : "+serverLocation);
				f=gfs.findOne(serverLocation);
				f = retryAsAPath(serverLocation, retry, f, gfs);
			}
			if(f==null){
				f=patchRemoteFilePathVersion1(serverLocation, gfs);
			}
			if(f != null)
				logger.info("object found "+f.get("name"));
			else{
				logger.info("object not found ");
			}
		}catch(Exception e){
			logger.error("problem retrieving remote object: "+serverLocation+" "+e.getMessage());
			close();
			throw new RemoteBackendException(e.getMessage());
		}
		return f;
	}


	private GridFSDBFile retryAsAPath(String serverLocation, boolean retry, GridFSDBFile f, GridFS gfs)
			throws InterruptedException {
		if(retry && (f==null)){
			int i=0;
			while((f== null) && (i <Costants.CONNECTION_RETRY_THRESHOLD)){
				logger.info(" retry to search file as a path"+serverLocation);
				Thread.sleep(500);
				f=gfs.findOne(serverLocation);
				i++;
			}
		}
		return f;
	}
	
	protected List<GridFSDBFile> retrieveRemoteObjects(BasicDBObject query) throws UnknownHostException {
		GridFS gfs=getGfs();
		return gfs.find(query);
	}
	
	public GridFSDBFile retrieveLinkPayload(GridFSDBFile f) throws UnknownHostException {
		while((f.containsField(Costants.LINK_IDENTIFIER)) && (f.get(Costants.LINK_IDENTIFIER) != null )){
			String id=(String)f.get(Costants.LINK_IDENTIFIER);
			f=getGfs().find(new ObjectId(id));
		}
		return f;
	}
	
	private GridFSDBFile patchRemoteFilePathVersion1(String serverLocation,
			GridFS gfs) {
		GridFSDBFile f=null;
		String path=serverLocation;
		//check if the file is stored by sm v.<2 (patch)
		    String locationV1=null;
			if(serverLocation.contains(Costants.ROOT_PATH_PATCH_V1)){
				locationV1=path.replace(Costants.ROOT_PATH_PATCH_V1, Costants.ROOT_PATH_PATCH_V2);
				f=gfs.findOne(locationV1);
				if(f== null){
					String locationV1patch=locationV1.substring(1);
					f=gfs.findOne(locationV1patch);
				}
			}else if(serverLocation.contains(Costants.ROOT_PATH_PATCH_V2)){
				locationV1=path.replace(Costants.ROOT_PATH_PATCH_V2, Costants.ROOT_PATH_PATCH_V1);
				f=gfs.findOne(locationV1);
				if(f== null){
					String locationV1patch=Costants.FILE_SEPARATOR+locationV1;
					f=gfs.findOne(locationV1patch);
				}
			}
		return f;
	}
	
	protected List<GridFSDBFile> patchRemoteDirPathVersion1(String bucket, GridFS gfs,
			BasicDBObject query, List<GridFSDBFile> list) {
		List<GridFSDBFile> patchList=null;
	//Patch incompatibility version 1 - 2	
		if(bucket.contains(Costants.ROOT_PATH_PATCH_V1)){
			String locationV2=bucket.replace(Costants.ROOT_PATH_PATCH_V1, Costants.ROOT_PATH_PATCH_V2);
			BasicDBObject queryPatch = new BasicDBObject();
			queryPatch.put("dir", locationV2);
			patchList = gfs.find(queryPatch);
		}else if(bucket.contains(Costants.ROOT_PATH_PATCH_V2)){
			String locationV1=bucket.replace(Costants.ROOT_PATH_PATCH_V2, Costants.ROOT_PATH_PATCH_V1);
			BasicDBObject queryPatch = new BasicDBObject();
			queryPatch.put("dir", locationV1);
			patchList = gfs.find(queryPatch);
			String locationV1patch=Costants.FILE_SEPARATOR+locationV1;
			queryPatch=new BasicDBObject();
			queryPatch.put("dir", locationV1patch);
			List<GridFSDBFile> patchList2=gfs.find(queryPatch);
			if((patchList2 != null) && (!patchList2.isEmpty())){
				if(patchList != null){
					patchList.addAll(patchList2);
				}else{
					patchList=patchList2;
				}
			}
		}
		if ((patchList != null) && (!patchList.isEmpty())){
			list.addAll(patchList);
		}
	// END Patch	
		return list;
	}
	
	public BasicDBObject findMetaCollectionObject(String source) throws UnknownHostException {
		DBCollection fileCollection=getConnectionDB(dbName, false).getCollection(Costants.DEFAULT_META_COLLECTION);
		BasicDBObject query = new BasicDBObject();
		BasicDBObject obj=null;
		query.put( "filename" ,source);
		DBCursor cursor=fileCollection.find(query);
		if(cursor != null && !cursor.hasNext()){
			query = new BasicDBObject();
			query.put( "_id" ,new ObjectId(source));
			cursor=fileCollection.find(query);
		}
		if(cursor.hasNext()){
			obj=(BasicDBObject) cursor.next();
			String path=(String)obj.get("filename");
			logger.debug("path found "+path);
		}
		return obj;
	}
	
	public DBObject findCollectionObject(DBCollection collection, BasicDBObject query) throws UnknownHostException {
		
		DBObject obj=null;
		obj=collection.findOne(query);
		return obj;
	}


	public DBCursor findCollectionObjects(DBCollection collection, BasicDBObject query) throws UnknownHostException {
		DBCursor cursor=collection.find(query);
		return cursor;
	}

	
	
	protected GridFSDBFile findGFSCollectionObject(ObjectId id){
		return getGfs().find(id);
	}
	
	public DBObject executeQuery(DBCollection fileCollection, BasicDBObject query)
			throws UnknownHostException {
		if(fileCollection == null)
			fileCollection=getMetaDataCollection(getConnectionDB( dbName, false));
		DBCursor cursor=fileCollection.find(query);
		if(cursor.hasNext())
			return cursor.next();
		return null;
	}
	
	/**
	 * @param resource
	 * @param f
	 * @param isLock 
	 * @return
	 */
	protected String readByInputStream(MyFile resource, GridFSDBFile f, boolean isLock, int count) {
		String key=null;
		resource.setInputStream(new MongoInputStream(mongo, f.getInputStream()));
		return key;
	}

	/**
	 * @param resource
	 * @param f
	 * @param isLock 
	 * @return
	 * @throws IOException
	 */
	protected String readByOutputStream(MyFile resource, GridFSDBFile f, boolean isLock, int count)
			throws IOException {
		String key=null;
		f.writeTo(resource.getOutputStream());
		resource.setOutputStream(null);
		f.save();
		return key;
	}

	/**
	 * This method write a new file on the remote server. It contains a failover system
	 * 
	 * @param resource
	 * @param f
	 * @param isLock 
	 * @return
	 * @throws IOException
	 */
	protected String readByPath(MyFile resource, GridFSDBFile f, boolean isLock, int count)
			throws IOException {
		String key=null;
		try{
			File file=new File(resource.getLocalPath());
			f.writeTo(file);
			resource.setLocalPath(null);
		}catch(IOException e){
			logger.error("Connection error. "+e.getMessage());
			if(count < Costants.CONNECTION_RETRY_THRESHOLD){
				count++;
				logger.info(" Retry : #"+count);
				readByPath(resource,f,isLock,count);
			}else{
				close();
				logger.error("max number of retry completed ");
				throw new RuntimeException(e);
			}
		}
		return key;
	}

	public GridFSInputFile createGFSFileObject(InputStream is, String writeConcern, String readPreference) throws UnknownHostException {
		GridFSInputFile f2;
		GridFS gfs = new GridFS(getConnectionDB( dbName, true));
		f2 = gfs.createFile(is);
		return f2;
	}
	
	protected GridFSInputFile createGFSFileObject(String name, String writeConcern, String readPreference) throws IOException {
		GridFSInputFile f2;
		GridFS gfs = new GridFS(getConnectionDB(dbName, true));
		f2 = gfs.createFile(name);
		return f2;
	}

	protected GridFSInputFile createGFSFileObject(File f, String writeConcern, String readPreference){
		GridFS gfs = new GridFS(getConnectionDB(dbName, true));
		GridFSInputFile f2=null;;
		try {
			f2 = gfs.createFile(f);
		} catch (IOException e) {
			logger.error("problem in creation remote file "+f.getAbsolutePath());
			close();
			throw new RemoteBackendException(e.getMessage());
		}
		return f2;
	}
	
	public GridFSInputFile createGFSFileObject(byte[] b, String writeConcern, String readPreference){
		GridFS gfs = new GridFS(getConnectionDB(dbName, true));
		GridFSInputFile f2;
		f2 = gfs.createFile(b);
		return f2;
	}

	protected GridFSInputFile createGFSFileObject(InputStream is) throws UnknownHostException {
		GridFSInputFile f2;
//		GridFS gfs = new GridFS(getDB());
		GridFS gfs = new GridFS(getConnectionDB(null, false));
		f2 = gfs.createFile(is);
		return f2;
	}
	
	protected GridFSInputFile createGFSFileObject(String name) throws IOException {
		GridFSInputFile f2;
		GridFS gfs = new GridFS(getConnectionDB(null, false));
		f2 = gfs.createFile(name);
		return f2;
	}

	protected GridFSInputFile createGFSFileObject(File f){
		GridFS gfs = new GridFS(getConnectionDB(null, false));
		GridFSInputFile f2=null;;
		try {
			f2 = gfs.createFile(f);
		} catch (IOException e) {
			logger.error("problem in creation remote file "+f.getAbsolutePath());
			close();
			throw new RemoteBackendException(e.getMessage());
		}
		return f2;
	}
	
	public GridFSInputFile createGFSFileObject(byte[] b){
		GridFS gfs = new GridFS(getConnectionDB(null, false));
		GridFSInputFile f2;
		f2 = gfs.createFile(b);
		return f2;
	}

	
	protected List<GridFSDBFile> getFilesOnFolder( String folderPath) {
		GridFS gfs = new GridFS(getConnectionDB(dbName, false));
		BsonOperator bson=new BsonOperator(gfs);
		List<GridFSDBFile> list=bson.getFilesOnFolder(folderPath);
		close();
		return list;		
	}
	
	protected List<GridFSDBFile> getOwnedFiles(String username){
		GridFS gfs = new GridFS(getConnectionDB(dbName, false));
		BsonOperator bson=new BsonOperator(gfs);
		List<GridFSDBFile> list=bson.getOwnedFiles(username);
		close();
		return list;		
	}
	
	/**
	 * Build a directory tree from leaf to root if not already present.
	 * @param meta metadata collection
	 * @param dir directory path
	 */
	public void buildDirTree(DBCollection meta, String dir) {
		String[] dirTree=dir.split(Costants.FILE_SEPARATOR);
         StringBuffer strBuff=new StringBuffer();
         strBuff.append(Costants.FILE_SEPARATOR);
         for(int i=1;i<dirTree.length;i++){
        	 BasicDBObject query = new BasicDBObject();
     		 query.put("name", dirTree[i]);
     		 query.put("dir", strBuff.toString());
     		 query.put("type", "dir");
     		 DBObject f=meta.findOne(query);
     		 if(f==null){
     			BasicDBObject newDir=new BasicDBObject();
     			newDir.put("$set", new BasicDBObject().append("name", dirTree[i]).append("dir", strBuff.toString()).append("type", "dir"));
     			if(!(memoryType== MemoryType.VOLATILE) && Costants.DEFAULT_READWRITE_PREFERENCE)
     				meta.update(query, newDir, true, true, Costants.DEFAULT_WRITE_TYPE);
     			else
     				meta.update(query, newDir, true, true);
     			if(logger.isDebugEnabled())
     				logger.debug(" Create new  object with name: "+dirTree[i]+" dir: "+strBuff.toString()+" type= dir");
     		 }
        	 strBuff.append(dirTree[i]+Costants.FILE_SEPARATOR);
         }
	}

	protected String[] getServer() {
		return server;
	}

	public void setServer(String[] server) {
		this.server = server;
	}

	public MongoClient getMongo() {
		return mongo;
	}

	public void setMongo(MongoClient mongo) {
		this.mongo = mongo;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void printObject(DBObject obj) {
		Set <String> keys=obj.keySet();
		for(String key : keys){
			logger.debug(" "+key+" "+obj.get(key));
		}
	}

	protected void saveGFSFileObject(GridFSInputFile f2) {
		f2.save();
		
	}

	/**
	 * the old close method
	 */
	protected void clean() {
		if(mongo!=null)
			mongo.close();
		mongo=null;
		if(db!=null)
			db=null;
	}
	
	/**
	 * For mongo java driver version 2.14. 
	 * MongoClient Java instance  will maintain an internal pool of connections (default size of 10)  
	 * it's not need close mongo every action. I can use it in every request.
	 */
	
	public void close() {
		if(mongo!=null)
			mongo.close();
		logger.info("Mongo has been closed");
		mongo=null;
		gfs=null;
		db=null;
	}
	
	public void removeGFSFile(GridFSDBFile f, ObjectId idF){
	// this field is an advice for oplog collection reader	
		f.put("onDeleting", "true");
		f.save();
		getGfs().remove(idF);
	}
	
	protected void replaceGFSFile(GridFSDBFile f, ObjectId idToRemove){
		// this field is an advice for oplog collection reader	
			f.put("onDeleting", "true");
			f.save();
			getGfs().remove(idToRemove);
		}

	
	public GridFS getGfs(String dbName, boolean readwritePreferences){
		if (gfs==null){
			if(db==null){
				gfs= new GridFS(getConnectionDB(dbName, readwritePreferences));
			}else{
				gfs= new GridFS(db);
			}
		}
		return gfs;
	}
	
	public GridFS getGfs(boolean readwritePreferences){
		return getGfs(dbName, readwritePreferences);
	}

	public GridFS getGfs(){
		return getGfs(Costants.DEFAULT_READWRITE_PREFERENCE);
	}


	public MemoryType getMemoryType() {
		return memoryType;
	}


	public void setMemoryType(MemoryType memoryType) {
		this.memoryType = memoryType;
	}


	public String getDbName() {
		return dbName;
	}


	public void setDbName(String dbName) {
		if ((dbName == null) || (dbName.isEmpty()))
			this.dbName =Costants.DEFAULT_DB_NAME;
		else
			this.dbName = dbName;
	}


	public String getWriteConcern() {
		return writeConcern;
	}


	public void setWriteConcern(String writeConcern) {
		this.writeConcern = writeConcern;
	}


	public String getReadPreference() {
		return readPreference;
	}


	public void setReadPreference(String readPreference) {
		this.readPreference = readPreference;
	}
	
	
	
}
