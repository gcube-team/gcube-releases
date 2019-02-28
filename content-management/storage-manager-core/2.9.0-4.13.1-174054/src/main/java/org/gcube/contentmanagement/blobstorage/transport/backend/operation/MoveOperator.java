/**
 * 
 */
package org.gcube.contentmanagement.blobstorage.transport.backend.operation;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.gcube.contentmanagement.blobstorage.resource.MemoryType;
import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.resource.OperationDefinition.OPERATION;
import org.gcube.contentmanagement.blobstorage.service.impl.ServiceEngine;
import org.gcube.contentmanagement.blobstorage.service.operation.Monitor;
import org.gcube.contentmanagement.blobstorage.service.operation.Move;
import org.gcube.contentmanagement.blobstorage.transport.backend.MongoOperationManager;
import org.gcube.contentmanagement.blobstorage.transport.backend.MongoIOManager;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.gcube.contentmanagement.blobstorage.transport.backend.util.Costants;
import org.gcube.contentmanagement.blobstorage.transport.backend.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;

/**
 * @author Roberto Cirillo (ISTI-CNR) 2018
 *
 */
public class MoveOperator extends Move {

	Logger logger=LoggerFactory.getLogger(MoveOperator.class);
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
	public MoveOperator(String[] server, String user, String pwd, String bucket, Monitor monitor, boolean isChunk,
			String backendType, String[] dbs) {
		super(server, user, pwd, bucket, monitor, isChunk, backendType, dbs);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.gcube.contentmanagement.blobstorage.service.operation.Move#execute(org.gcube.contentmanagement.blobstorage.transport.backend.MongoIO)
	 */
	@Override
//	public String execute(MongoIO mongoPrimaryInstance, MemoryType memoryType) throws UnknownHostException {
	public String execute(MongoIOManager mongoPrimaryInstance,  MemoryType memoryType, MyFile resource, String sourcePath, String destinationPath) throws UnknownHostException {
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
			BasicDBObject sourcePathMetaCollection = mongoPrimaryInstance.findMetaCollectionObject(source);
//check if the file exist in the destination path, if it exist then it will be deleted		
			if(sourcePathMetaCollection != null){
				sourceId=sourcePathMetaCollection.get("_id").toString();
				sourcePathMetaCollection=setCommonFields(sourcePathMetaCollection, resource, OPERATION.MOVE);
//				updateCommonFields(sourcePathMetaCollection, resource);
				BasicDBObject queryDestPath = new BasicDBObject();
				queryDestPath.put( "filename" , destinationPath);
				DBCollection metaCollectionInstance=null;
				if(!(memoryType== MemoryType.VOLATILE))
					metaCollectionInstance=mongoPrimaryInstance.getMetaDataCollection(mongoPrimaryInstance.getConnectionDB(MongoOperationManager.getPrimaryCollectionName(), true));
				else
					metaCollectionInstance=mongoPrimaryInstance.getMetaDataCollection(mongoPrimaryInstance.getConnectionDB(MongoOperationManager.getPrimaryCollectionName(), false));
				
				DBObject destPathMetaCollection= mongoPrimaryInstance.executeQuery(metaCollectionInstance, queryDestPath);
	// retrieve original object		
				BasicDBObject  querySourcePath = new BasicDBObject();
				querySourcePath.put( "filename" , sourcePath);
	   //update common fields		
				BasicDBObject updateQuery= new BasicDBObject();
				updateQuery.put("$set", sourcePathMetaCollection);
				if(!(memoryType== MemoryType.VOLATILE))
					metaCollectionInstance.update(querySourcePath, updateQuery, false, true, Costants.DEFAULT_WRITE_TYPE);
				else
					metaCollectionInstance.update(querySourcePath, updateQuery, false, true);
				if(destPathMetaCollection != null)
					destinationId=destPathMetaCollection.get("_id").toString();
				if((destPathMetaCollection!=null) && (destinationId != null) && (!destinationId.equals(sourceId))){
					mongoPrimaryInstance.printObject(destPathMetaCollection);
				// if exist, keep id (it need a replace)
					destinationId=destPathMetaCollection.get("_id").toString();
					logger.info("file in destination path already present with id : "+destinationId);		
		//remove old one	
//					GridFS gfs = new GridFS(mongoPrimaryInstance.getConnectionDB(resource.getWriteConcern(), resource.getReadPreference(), getPrimaryCollectionName(), true));
					GridFS gfs = mongoPrimaryInstance.getGfs(MongoOperationManager.getPrimaryCollectionName(), true);
					GridFSDBFile fNewFSPath = gfs.findOne(queryDestPath);
					mongoPrimaryInstance.checkAndRemove(fNewFSPath, resource);
		// print			
					logger.debug("Changing filename metadata from:"+sourcePathMetaCollection.get("filename")+"\n  to: "+destinationPath);
					logger.debug("original objects:\n  ");
					logger.debug("source object: ");
					mongoPrimaryInstance.printObject(sourcePathMetaCollection);
					logger.info("destination object: ");
					mongoPrimaryInstance.printObject(destPathMetaCollection);
		// update fields	
					mongoPrimaryInstance.buildDirTree(mongoPrimaryInstance.getMetaDataCollection(mongoPrimaryInstance.getConnectionDB( MongoOperationManager.getPrimaryCollectionName(), true)), dir);
					sourcePathMetaCollection= new BasicDBObject();
					sourcePathMetaCollection.put("$set", new BasicDBObject().append("dir", dir).append("filename", destinationPath).append("name", name).append("owner", ((MyFile)resource).getOwner()));
					logger.info("new object merged ");
					mongoPrimaryInstance.printObject(sourcePathMetaCollection);
			//applies the update	
					if(!(memoryType== MemoryType.VOLATILE))
						metaCollectionInstance.update(querySourcePath, sourcePathMetaCollection, false, true, Costants.DEFAULT_WRITE_TYPE);
					else
						metaCollectionInstance.update(querySourcePath, sourcePathMetaCollection, false, true);
					logger.info("update metadata done ");
					logger.info("check update ");
					DBObject newDestPathMetaCollection= mongoPrimaryInstance.executeQuery(metaCollectionInstance, queryDestPath);
					mongoPrimaryInstance.printObject(newDestPathMetaCollection);
				}else if((destinationId!= null) && (destinationId.equals(sourceId))){
					logger.warn("the destination id and the source id are the same id. skip operation. ");
				}else{
					queryDestPath = new BasicDBObject();
					queryDestPath.put( "dir" , destination );
					DBObject folder = metaCollectionInstance.findOne(queryDestPath);//= gfs.find(query);
//		if the destination is an existing folder			
					if((folder != null)){
						destination=appendFileSeparator(destination);
						sourcePathMetaCollection=mongoPrimaryInstance.setGenericMoveProperties(resource, destination+name, destination, name, sourcePathMetaCollection);
						destinationId=sourcePathMetaCollection.get("_id").toString();
						mongoPrimaryInstance.buildDirTree(metaCollectionInstance, destination);
						
					}else{
//	if the last char of dest path is a separator then the destination is a dir otherwise is a file
// then if it is a new folder						
						if(destination.lastIndexOf(Costants.FILE_SEPARATOR) == destination.length()-1){
							sourcePathMetaCollection=mongoPrimaryInstance.setGenericMoveProperties(resource, destination+name, destination, name, sourcePathMetaCollection);
							destinationId=sourcePathMetaCollection.get("_id").toString();
							mongoPrimaryInstance.buildDirTree(metaCollectionInstance, destination);
							
						}else{
							String newName=destination.substring(destination.lastIndexOf(Costants.FILE_SEPARATOR)+1);
							sourcePathMetaCollection=mongoPrimaryInstance.setGenericMoveProperties(resource, destination, dir, newName, sourcePathMetaCollection);
							destinationId=sourcePathMetaCollection.get("_id").toString();
							mongoPrimaryInstance.buildDirTree(metaCollectionInstance, dir);
						}
						queryDestPath = new BasicDBObject();
						queryDestPath.put( "filename" , sourcePath);
					//update common fields		
						updateQuery= new BasicDBObject();
						updateQuery.put("$set", sourcePathMetaCollection);
						if(!(memoryType== MemoryType.VOLATILE))
								metaCollectionInstance.update(queryDestPath, updateQuery, true, true, Costants.DEFAULT_WRITE_TYPE);
						else
							metaCollectionInstance.update(queryDestPath, updateQuery, true, true);
					}
				}
				mongoPrimaryInstance.close();
				return destinationId;
			}else{
				mongoPrimaryInstance.close();
				throw new RemoteBackendException(" the source path is wrong. There isn't a file at this path: "+source);
			}
		}else{
			mongoPrimaryInstance.close();
			throw new IllegalArgumentException("parameters not completed, source: "+source+", destination: "+destination);
		}

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

}
