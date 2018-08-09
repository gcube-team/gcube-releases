/**
 * 
 */
package org.gcube.contentmanagement.blobstorage.transport.backend.operation;

import java.io.InputStream;
import java.net.UnknownHostException;

import org.bson.types.ObjectId;
import org.gcube.contentmanagement.blobstorage.resource.MemoryType;
import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.resource.OperationDefinition.LOCAL_RESOURCE;
import org.gcube.contentmanagement.blobstorage.resource.OperationDefinition.OPERATION;
import org.gcube.contentmanagement.blobstorage.resource.OperationDefinition.REMOTE_RESOURCE;
import org.gcube.contentmanagement.blobstorage.service.operation.Monitor;
import org.gcube.contentmanagement.blobstorage.service.operation.Operation;
import org.gcube.contentmanagement.blobstorage.service.operation.SoftCopy;
import org.gcube.contentmanagement.blobstorage.transport.backend.MongoIOManager;
import org.gcube.contentmanagement.blobstorage.transport.backend.MongoOperationManager;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.gcube.contentmanagement.blobstorage.transport.backend.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;

/**
 * @author Roberto Cirillo (ISTI-CNR) 2018
 *
 */
public class SoftCopyOperator extends SoftCopy {

	Logger logger=LoggerFactory.getLogger(SoftCopyOperator.class);
	private MemoryType memoryType;
	private MongoIOManager mongoPrimaryInstance;
	private MyFile resource;
	/**
	 * @param server
	 * @param user
	 * @param pwd
	 * @param bucket
	 * @param monitor
	 * @param isChunk
	 * @param backendType
	 * @param dbs
	 */
	public SoftCopyOperator(String[] server, String user, String pwd, String bucket, Monitor monitor, boolean isChunk,
			String backendType, String[] dbs) {
		super(server, user, pwd, bucket, monitor, isChunk, backendType, dbs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String execute(MongoIOManager mongoPrimaryInstance, MyFile resource, String sourcePath, String destinationPath)
			throws UnknownHostException {
		REMOTE_RESOURCE remoteResourceIdentifier=resource.getOperation().getRemoteResource();
		LOCAL_RESOURCE localResourceIdentifier=resource.getOperation().getLocalResource();
		String source=null;
		if(localResourceIdentifier.equals(LOCAL_RESOURCE.ID))
			source=resource.getId();
		else
			source=sourcePath;
		String destination=null;
		if(remoteResourceIdentifier.equals(REMOTE_RESOURCE.ID))
			destination=resource.getId();
		else
			destination=destinationPath;
		if(resource!=null){
			String dir=((MyFile)resource).getRemoteDir();
			String name=((MyFile)resource).getName();
			setMemoryType(((MyFile)resource).getGcubeMemoryType());
		}
		setMongoPrimaryInstance(mongoPrimaryInstance);
		ObjectId mapId=null;
		GridFSDBFile destObject=null;
		logger.debug("softCopy operation on Mongo backend, parameters: source path: "+source+" destination path: "+destination);
		if((source != null) && (!source.isEmpty())){
			GridFSDBFile sourceObject = mongoPrimaryInstance.retrieveRemoteDescriptor(source, remoteResourceIdentifier, true);
			if(sourceObject != null){
//				GridFSDBFile originalObject=sourceObject;
	// if it contains a link field, then I'm going to retrieve the related payload 
				sourceObject = mongoPrimaryInstance.retrieveLinkPayload(sourceObject);
				ObjectId sourceId=(ObjectId)sourceObject.getId();
				InputStream is= sourceObject.getInputStream();
				resource.setInputStream(is);
				GridFSDBFile dest = null;
				if((destination == null) || (destination.isEmpty())){
					// if the destination param is null, the destination object will be filled with values extracted from sourceObject
					if(sourceId==null) throw new RemoteBackendException("source object not found: "+source);
					destination = fillGenericDestinationFields(resource, sourceId);
					logger.warn("SoftCopy without destination parameter. The operation will be executed with the following destination path "+destination);
				}else{
					// check if the destination is a dir or a file and if the destination exist
					dest = mongoPrimaryInstance.retrieveRemoteDescriptor(destination, remoteResourceIdentifier,  false);//gfs.findOne(destination);
				}
					// check if the destination is a dir or a file and if the destination exist
//					GridFSDBFile dest = mongoPrimaryInstance.retrieveRemoteDescriptor(destination, remoteResourceIdentifier,  false);//gfs.findOne(destination);
//										GridFSInputFile destinationFile=mongoPrimaryInstance.createGFSFileObject(is, resource.getWriteConcern(), resource.getReadPreference());//gfs.createFile(is);
				ObjectId removedId=null;
	// if the destination location is not empty			
				if (dest != null){
	// remove the destination file. The third parameter to true replace the file otherwise the remote id is returned
					if(resource.isReplace()){
						removedId = mongoPrimaryInstance.removeFile(resource, null, resource.isReplace(), null, dest);
					}else{
						return dest.getId().toString();
					}
				}
			//  get metacollection instance 	
				DBCollection metaCollectionInstance = getMetaCollection();
				String md5=sourceObject.getMD5();
				// check if the payload is already present on backend		
				ObjectId md5Id=getDuplicatesMap(md5);
			// check if the source object is already a map	
				if(isMap(sourceObject)){
					logger.debug("the sourceObject with the following id: "+mapId+" is already a map");
					mapId=sourceId;
			// then it's needed to add only the destObject to the map	
			//first: create link object to destination place		
					DBObject newObject=createNewLinkObject(resource, sourceObject, destination, metaCollectionInstance, md5, mapId, removedId);
					destObject = mongoPrimaryInstance.retrieveRemoteDescriptor(destination, remoteResourceIdentifier, true);
			// second: add the new object to the map		
					mapId = addToDuplicateMap(metaCollectionInstance, mapId, destObject);
//			if the payload is already present on backend			
				}else if(md5Id!=null){
						mapId=md5Id;
						logger.debug("retrieved md5 on backend with the following id: "+mapId);
						mapId = addToDuplicateMap(metaCollectionInstance, mapId, sourceObject);
						DBObject newObject=createNewLinkObject(resource, sourceObject, destination, metaCollectionInstance, md5, mapId, removedId);
						destObject = mongoPrimaryInstance.retrieveRemoteDescriptor(destination, remoteResourceIdentifier, true);
						mapId = addToDuplicateMap(metaCollectionInstance, mapId, destObject);
				}else{
		// no map present no md5 present			
					mapId = createNewDuplicatesMap(metaCollectionInstance, resource, sourceObject, destination, sourceId);
					mapId = addToDuplicateMap(metaCollectionInstance, mapId, sourceObject);
					DBObject newObject=createNewLinkObject(resource, sourceObject,destination, metaCollectionInstance, md5, mapId, removedId);
					destObject = mongoPrimaryInstance.retrieveRemoteDescriptor(destination, remoteResourceIdentifier, true);
					mapId = addToDuplicateMap(metaCollectionInstance, mapId, destObject);
				}
				if(logger.isDebugEnabled())
					logger.debug("mapId created/updated: "+mapId);
				mongoPrimaryInstance.close();
			}else{
				mongoPrimaryInstance.close();
				throw new RemoteBackendException(" the source path is wrong. There isn't a file at "+source);
			}
		}else throw new RemoteBackendException("Invalid arguments: source "+source+" destination "+destination);
//		return mapId.toString();
		return destObject.getId().toString();
	}

	private String fillGenericDestinationFields(MyFile resource, ObjectId souceId) {
		String destination;				
		destination=resource.getRootPath()+souceId;
		resource.setName(souceId.toString());
		resource.setRemoteDir(resource.getRootPath());
		return destination;
	}
	
	/**
	 * 
	 * @param resource 
	 * @param bucket destinationPath
	 * @param dir destination directory
	 * @param name name of the new file
	 * @param oldId id of the file was present in the destination place
	 * @return id of the new map
	 * @throws UnknownHostException
	 */
	private ObjectId createNewDuplicatesMap(DBCollection metaCollectionInstance, Object resource, GridFSDBFile sourceObject,  String bucket, ObjectId sourceId) throws UnknownHostException {
			ObjectId id = null;
			String dir= ((MyFile)resource).getRemoteDir();
		// create new dir (is it really needed in case of map object?)
			if((dir !=null && !dir.isEmpty()) && (bucket !=null && !bucket.isEmpty())){
				getMongoPrimaryInstance().buildDirTree(getMongoPrimaryInstance().getMetaDataCollection(null), dir);
			}
		// create new map object
			id= createNewObjectMap(metaCollectionInstance, (MyFile)resource, sourceObject, sourceId);
		return id;
	}
	
	private ObjectId createNewObjectMap(DBCollection metaCollectionInstance, MyFile resource, GridFSDBFile source, ObjectId sourceId) throws UnknownHostException {
		String md5=source.getMD5();
	// set type of object	
		DBObject document=new BasicDBObject("type", "map");
		// initialize count field to 0
		document.put("count", 0);
		ObjectId id=new ObjectId();
		document.put("_id", id);
		logger.debug("generated id for new map"+id);
		document=fillCommonfields(document, resource, source, metaCollectionInstance,  md5);
	// update chunks collection
		getMongoPrimaryInstance().updateChunksCollection(sourceId, id);
	return id;
}
	
	private DBObject createNewLinkObject(MyFile resource, GridFSDBFile sourceObject, String destination, DBCollection metaCollectionInstance, String md5, ObjectId mapId, ObjectId newId){
		DBObject document=new BasicDBObject("type", "file");
		document.put("filename", destination);
		document.put("name", resource.getName());
		document.put("dir", resource.getRemoteDir());
		document.put("owner", resource.getOwner());
		document.put(Operation.LINK_IDENTIFIER, mapId.toString());
		ObjectId id=null;
		if(newId == null){
			id=new ObjectId();
			logger.debug("generated id for new object link"+id);
		}else{
			id=newId;
			logger.debug("restored id for new object link"+id);
		}
		document.put("_id", id);
		
		return fillCommonfields(document, resource, sourceObject, metaCollectionInstance,  md5);
	}

	private DBObject fillCommonfields(DBObject document, MyFile resource, GridFSDBFile sourceObject, DBCollection metaCollectionInstance, String md5) {
		document.put("mimetype", ((MyFile)resource).getMimeType());
		document.put("creationTime", DateUtils.now("dd MM yyyy 'at' hh:mm:ss z"));
		document.put("md5", md5);
		document.put("length", sourceObject.getLength());
		// set chunkSize inherited from original object	
		document.put("chunkSize", sourceObject.getChunkSize());
		metaCollectionInstance.insert(document);
		metaCollectionInstance.save(document);
		return document;
	}

	private DBCollection getMetaCollection() throws UnknownHostException {
		DBCollection metaCollectionInstance=null;
		if(!(getMemoryType() == MemoryType.VOLATILE))
			metaCollectionInstance=mongoPrimaryInstance.getMetaDataCollection(mongoPrimaryInstance.getConnectionDB(MongoOperationManager.getPrimaryCollectionName(), true));
		else
			metaCollectionInstance=mongoPrimaryInstance.getMetaDataCollection(mongoPrimaryInstance.getConnectionDB(MongoOperationManager.getPrimaryCollectionName(), false));
		return metaCollectionInstance;
	}
	
	private ObjectId addToDuplicateMap(DBCollection metaCollectionInstance, ObjectId mapId, GridFSDBFile f) throws UnknownHostException {
		f.put(Operation.LINK_IDENTIFIER, mapId.toString());
		mongoPrimaryInstance.updateCommonFields(f, getResource(), OPERATION.SOFT_COPY);
		f.save();
		incrementCountField(metaCollectionInstance, mapId);
		return mapId;
	}

	private void incrementCountField(DBCollection metaCollectionInstance, ObjectId mapId) throws UnknownHostException {
		logger.info("increment count field on"+mapId+ " object map");
		BasicDBObject searchQuery= new BasicDBObject();
		searchQuery.put("_id" ,mapId);
		DBObject mapObject=mongoPrimaryInstance.findCollectionObject(metaCollectionInstance, searchQuery);
//		BasicDBObject updateObject= new BasicDBObject().append("$inc",new BasicDBObject().append("count", 1));;
		int count=(int)mapObject.get("count");
		count++;
		mapObject.put("count", count);
//		metaCollectionInstance.update(mapObject, updateObject);
		metaCollectionInstance.save(mapObject);
	}

	private ObjectId getDuplicatesMap(String md5){
		ObjectId id= checkMd5(md5);
		return id;
	}
	
	/**
	 * @param sourceObject
	 * @return
	 */
	private boolean isMap(GridFSDBFile sourceObject) {
		String type=sourceObject.get("type").toString();
		if(type.equals("map"))
			return true;
		return false;
	}




	/**
	 * Check if the backend already has the payload
	 * @param md5 string of the file
	 * @return the ObjectID of the md5 file found on the backend, else null
	 */
	private ObjectId checkMd5(String md5) {
		// TODO Auto-generated method stub
		return null;
	}

	public MemoryType getMemoryType() {
		return memoryType;
	}

	public void setMemoryType(MemoryType memoryType) {
		this.memoryType = memoryType;
	}

	public MongoIOManager getMongoPrimaryInstance() {
		return mongoPrimaryInstance;
	}

	public void setMongoPrimaryInstance(MongoIOManager mongoPrimaryInstance) {
		this.mongoPrimaryInstance = mongoPrimaryInstance;
	}

	public MyFile getResource() {
		return resource;
	}

	public void setResource(MyFile resource) {
		this.resource = resource;
	}

	
	
}
