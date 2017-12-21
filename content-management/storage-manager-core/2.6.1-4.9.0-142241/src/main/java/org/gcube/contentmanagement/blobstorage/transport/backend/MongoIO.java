package org.gcube.contentmanagement.blobstorage.transport.backend;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.bson.types.ObjectId;
import org.gcube.contentmanagement.blobstorage.resource.MemoryType;
import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.service.impl.ServiceEngine;
import org.gcube.contentmanagement.blobstorage.transport.backend.util.MongoInputStream;
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


public class MongoIO {
	
	private DB db;
	private String[] server;
	private MongoClient mongo;
	private String user;
	private String password;
	private Logger logger = LoggerFactory.getLogger(MongoIO.class);
	private GridFS gfs; 
	private MemoryType memoryType;
	protected static final String DEFAULT_META_COLLECTION="fs.files";
	protected static final String DEFAULT_DB_NAME="remotefs";
	protected static final String ROOT_PATH_PATCH_V1=ServiceEngine.FILE_SEPARATOR+"home"+ServiceEngine.FILE_SEPARATOR+"null"+ServiceEngine.FILE_SEPARATOR;
	protected static final String ROOT_PATH_PATCH_V2=ServiceEngine.FILE_SEPARATOR+"public"+ServiceEngine.FILE_SEPARATOR;
	protected static final String DEFAULT_CHUNKS_COLLECTION = "fs.chunks";
	protected static final  WriteConcern DEFAULT_WRITE_TYPE=WriteConcern.REPLICA_ACKNOWLEDGED;
	protected static ReadPreference DEFAULT_READ_PREFERENCE=ReadPreference.primaryPreferred();
	
	protected MongoIO(String[] server, String user, String password, MemoryType memoryType){
		setServer(server);
		setUser(user);
		setPassword(password);
		setMemoryType(memoryType);
	}
	
	
	protected DB getDB(String writeConcern, String readConcern){
		if(db==null){
			try{
			
					List<ServerAddress> srvList=new ArrayList<ServerAddress>();
					for(String srv : server){
							srvList.add(new ServerAddress(srv));
					}
					if(mongo==null){		
							logger.debug(" open mongo connection ");
							MongoClientOptions options=MongoClientOptions.builder().connectionsPerHost(30).connectTimeout(30000).build();
							if(((password != null) && (password.length() >0))  && ((user != null) && (user.length() > 0))){
								MongoCredential credential = MongoCredential.createCredential(user, DEFAULT_DB_NAME, password.toCharArray());
								mongo = new MongoClient(srvList, Arrays.asList(credential), options);
							}else{
								mongo = new MongoClient(srvList, options);
							}
							logger.debug("Istantiate MongoDB with options: "+mongo.getMongoClientOptions());
					}
					db = mongo.getDB(DEFAULT_DB_NAME);
					if((!(memoryType== MemoryType.VOLATILE)) && (srvList.size()>1)){
						if(writeConcern!=null){
							WriteConcern write=new WriteConcern(Integer.parseInt(writeConcern));
							db.setWriteConcern(write);
						}else{
							db.setWriteConcern(DEFAULT_WRITE_TYPE);
						}
						if(readConcern!=null){
							ReadPreference read=ReadPreference.valueOf(readConcern);
							db.setReadPreference(read);
						}else{
							db.setReadPreference(DEFAULT_READ_PREFERENCE);
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

/**
 * This method is used for connections without write and read preferences
 * @return
 */
	protected DB getDB(){
		if(db==null){
			try{
					List<ServerAddress> srvList=new ArrayList<ServerAddress>();
					for(String srv : server){
							srvList.add(new ServerAddress(srv));
					}
					if(mongo==null){		
							logger.debug(" open mongo connection ");
							MongoClientOptions options=MongoClientOptions.builder().connectionsPerHost(30).connectTimeout(30000).build();
							if(((password != null) && (password.length() >0))  && ((user != null) && (user.length() > 0))){
								MongoCredential credential = MongoCredential.createCredential(user, DEFAULT_DB_NAME, password.toCharArray());
								mongo = new MongoClient(srvList, Arrays.asList(credential), options);
							}else{
								mongo = new MongoClient(srvList, options);
							}
							logger.debug("Istantiate MongoDB with options: "+mongo.getMongoClientOptions());
					}
					db = mongo.getDB(DEFAULT_DB_NAME);
			} catch (Exception e) {
				close();
				logger.error("Problem to open the DB connection for gridfs file ");
				throw new RemoteBackendException("Problem to open the DB connection: "+ e.getMessage());
			}
			logger.info("new mongo connection pool opened");
		}
		return db;
	}

	
	protected DBCollection getMetaDataCollection() throws UnknownHostException{
		if(db==null)
			this.db=getDB(null, null);
		return db.getCollection(DEFAULT_META_COLLECTION);
	}
	
	protected DBCollection getMetaDataCollection(DB db) throws UnknownHostException{
		if(db==null){
			this.db=getDB(null, null);
			return this.db.getCollection(DEFAULT_META_COLLECTION);
	}else{
		return db.getCollection(DEFAULT_META_COLLECTION);
	}
	}
	
	protected DBCollection getCollection(DB db, String collectionName) throws UnknownHostException{
		if(db==null){
			this.db=getDB(null, null);
			return this.db.getCollection(collectionName);
		}else{
			return db.getCollection(collectionName);
		}
	}

	
	protected GridFSDBFile retrieveRemoteObject(String serverLocation, boolean retry){
		logger.info("MongoDB - retrieve object from pathServer: "+serverLocation);
		GridFSDBFile f=null;
		try{
			GridFS gfs = new GridFS(getDB(null, null)); 
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
				if(retry && (f==null)){
					int i=0;
					while((f== null) && (i <ServiceEngine.CONNECTION_RETRY_THRESHOLD)){
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
				}
			}else{
				logger.info("remote object is not a validID : "+serverLocation);
				f=gfs.findOne(serverLocation);
				if(retry && (f==null)){
					int i=0;
					while((f== null) && (i <ServiceEngine.CONNECTION_RETRY_THRESHOLD)){
						logger.info(" retry to search file "+serverLocation);
						Thread.sleep(500);
						f=gfs.findOne(serverLocation);
						i++;
					}
				}
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
	
	protected List<GridFSDBFile> retrieveRemoteObjects(BasicDBObject query) throws UnknownHostException {
		GridFS gfs=getGfs();
		return gfs.find(query);
	}
	
	protected GridFSDBFile retrieveLinkPayload(GridFSDBFile f) throws UnknownHostException {
		while((f.containsField("link")) && (f.get("link") != null )){
			String id=(String)f.get("link");
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
			if(serverLocation.contains(ROOT_PATH_PATCH_V1)){
				locationV1=path.replace(ROOT_PATH_PATCH_V1, ROOT_PATH_PATCH_V2);
				f=gfs.findOne(locationV1);
				if(f== null){
					String locationV1patch=locationV1.substring(1);
					f=gfs.findOne(locationV1patch);
				}
			}else if(serverLocation.contains(ROOT_PATH_PATCH_V2)){
				locationV1=path.replace(ROOT_PATH_PATCH_V2, ROOT_PATH_PATCH_V1);
				f=gfs.findOne(locationV1);
				if(f== null){
					String locationV1patch=ServiceEngine.FILE_SEPARATOR+locationV1;
					f=gfs.findOne(locationV1patch);
				}
			}
		return f;
	}
	
	protected List<GridFSDBFile> patchRemoteDirPathVersion1(String bucket, GridFS gfs,
			BasicDBObject query, List<GridFSDBFile> list) {
		List<GridFSDBFile> patchList=null;
	//Patch incompatibility version 1 - 2	
		if(bucket.contains(ROOT_PATH_PATCH_V1)){
			String locationV2=bucket.replace(ROOT_PATH_PATCH_V1, ROOT_PATH_PATCH_V2);
			BasicDBObject queryPatch = new BasicDBObject();
			queryPatch.put("dir", locationV2);
			patchList = gfs.find(queryPatch);
		}else if(bucket.contains(ROOT_PATH_PATCH_V2)){
			String locationV1=bucket.replace(ROOT_PATH_PATCH_V2, ROOT_PATH_PATCH_V1);
			BasicDBObject queryPatch = new BasicDBObject();
			queryPatch.put("dir", locationV1);
			patchList = gfs.find(queryPatch);
			String locationV1patch=ServiceEngine.FILE_SEPARATOR+locationV1;
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
	
	protected BasicDBObject findMetaCollectionObject(String source) throws UnknownHostException {
		DBCollection fileCollection=getDB(null, null).getCollection("fs.files");
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
	
	protected DBObject findCollectionObject(DBCollection collection, BasicDBObject query) throws UnknownHostException {
		
		DBObject obj=null;
		obj=collection.findOne(query);
		return obj;
	}


	protected DBCursor findCollectionObjects(DBCollection collection, BasicDBObject query) throws UnknownHostException {
		DBCursor cursor=collection.find(query);
		return cursor;
	}

	
	
	protected GridFSDBFile findGFSCollectionObject(ObjectId id){
		return getGfs().find(id);
	}
	
	protected DBObject executeQuery(DBCollection fileCollection, BasicDBObject query)
			throws UnknownHostException {
		if(fileCollection == null)
			fileCollection=getMetaDataCollection(getDB(null, null));
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
			if(count < ServiceEngine.CONNECTION_RETRY_THRESHOLD){
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

	protected GridFSInputFile createGFSFileObject(InputStream is, String writeConcern, String readPreference) throws UnknownHostException {
		GridFSInputFile f2;
		GridFS gfs = new GridFS(getDB(writeConcern, readPreference));
		f2 = gfs.createFile(is);
		return f2;
	}
	
	protected GridFSInputFile createGFSFileObject(String name, String writeConcern, String readPreference) throws IOException {
		GridFSInputFile f2;
		GridFS gfs = new GridFS(getDB(writeConcern, readPreference));
		f2 = gfs.createFile(name);
		return f2;
	}

	protected GridFSInputFile createGFSFileObject(File f, String writeConcern, String readPreference){
		GridFS gfs = new GridFS(getDB(writeConcern, readPreference));
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
	
	protected GridFSInputFile createGFSFileObject(byte[] b, String writeConcern, String readPreference){
		GridFS gfs = new GridFS(getDB(writeConcern, readPreference));
		GridFSInputFile f2;
		f2 = gfs.createFile(b);
		return f2;
	}

	protected GridFSInputFile createGFSFileObject(InputStream is) throws UnknownHostException {
		GridFSInputFile f2;
		GridFS gfs = new GridFS(getDB());
		f2 = gfs.createFile(is);
		return f2;
	}
	
	protected GridFSInputFile createGFSFileObject(String name) throws IOException {
		GridFSInputFile f2;
		GridFS gfs = new GridFS(getDB());
		f2 = gfs.createFile(name);
		return f2;
	}

	protected GridFSInputFile createGFSFileObject(File f){
		GridFS gfs = new GridFS(getDB());
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
	
	protected GridFSInputFile createGFSFileObject(byte[] b){
		GridFS gfs = new GridFS(getDB());
		GridFSInputFile f2;
		f2 = gfs.createFile(b);
		return f2;
	}

	
	/**
	 * Build a directory tree from leaf to root if not already present.
	 * @param meta metadata collection
	 * @param dir directory path
	 */
	protected void buildDirTree(DBCollection meta, String dir) {
		String[] dirTree=dir.split(ServiceEngine.FILE_SEPARATOR);
         StringBuffer strBuff=new StringBuffer();
         strBuff.append(ServiceEngine.FILE_SEPARATOR);
         for(int i=1;i<dirTree.length;i++){
        	 BasicDBObject query = new BasicDBObject();
     		 query.put("name", dirTree[i]);
     		 query.put("dir", strBuff.toString());
     		 query.put("type", "dir");
     		 DBObject f=meta.findOne(query);
     		 if(f==null){
     			BasicDBObject newDir=new BasicDBObject();
     			newDir.put("$set", new BasicDBObject().append("name", dirTree[i]).append("dir", strBuff.toString()).append("type", "dir"));
     			if(!(memoryType== MemoryType.VOLATILE))
     				meta.update(query, newDir, true, true, DEFAULT_WRITE_TYPE);
     			else
     				meta.update(query, newDir, true, true);
     			if(logger.isDebugEnabled())
     				logger.debug(" Create new  object with name: "+dirTree[i]+" dir: "+strBuff.toString()+" type= dir");
     		 }
        	 strBuff.append(dirTree[i]+ServiceEngine.FILE_SEPARATOR);
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
	
	protected void removeGFSFile(GridFSDBFile f, ObjectId idF){
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

	private GridFS getGfs(){
		if (gfs==null){
			gfs= new GridFS(getDB(null, null));
		}
		return gfs;
	}


	public MemoryType getMemoryType() {
		return memoryType;
	}


	public void setMemoryType(MemoryType memoryType) {
		this.memoryType = memoryType;
	}
	
	
	
}
