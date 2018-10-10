/**
 * 
 */
package org.gcube.contentmanagement.blobstorage.transport.backend.operation;

import java.io.InputStream;
import java.net.UnknownHostException;

import org.bson.types.ObjectId;
import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.resource.OperationDefinition;
import org.gcube.contentmanagement.blobstorage.resource.OperationDefinition.REMOTE_RESOURCE;
import org.gcube.contentmanagement.blobstorage.service.operation.Copy;
import org.gcube.contentmanagement.blobstorage.service.operation.Monitor;
import org.gcube.contentmanagement.blobstorage.transport.backend.MongoIOManager;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.gridfs.GridFSDBFile;
/**
 * @author Roberto Cirillo (ISTI-CNR) 2018
 *
 */
public class CopyOperator extends Copy {

	
	final Logger logger=LoggerFactory.getLogger(CopyOperator.class);
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
	public CopyOperator(String[] server, String user, String pwd, String bucket, Monitor monitor, boolean isChunk,
			String backendType, String[] dbs) {
		super(server, user, pwd, bucket, monitor, isChunk, backendType, dbs);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.gcube.contentmanagement.blobstorage.service.operation.Copy#execute()
	 */
	@Override
//	public String execute(MongoIO mongoPrimaryInstance) throws UnknownHostException {
	public String execute(MongoIOManager mongoPrimaryInstance, MyFile resource, String sourcePath, String destinationPath) throws UnknownHostException {
		String source=sourcePath;
		String destination=destinationPath;
		String dir=((MyFile)resource).getRemoteDir();
		String originalDir=((MyFile)resource).getLocalDir();
		logger.debug("from directory: "+originalDir+ "to directory: "+dir);
		String name=((MyFile)resource).getName();
		REMOTE_RESOURCE remoteResourceIdentifier=resource.getOperation().getRemoteResource();
		ObjectId destinationId=null;
		logger.debug("copy operation on Mongo backend, parameters: source path: "+source+" destination path: "+destination);
		if((source != null) && (!source.isEmpty()) && (destination != null) && (!destination.isEmpty())){
			GridFSDBFile f = mongoPrimaryInstance.retrieveRemoteDescriptor(source, remoteResourceIdentifier, true);
			if(f != null){
// if it is a copy of an hardLink, then I'm going to retrieve and copy the payload associated to the link			
				f = mongoPrimaryInstance.retrieveLinkPayload(f);
				InputStream is= f.getInputStream();
				resource.setInputStream(is);
		// check if the destination is a dir or a file and if the destination exist
				GridFSDBFile dest = mongoPrimaryInstance.retrieveRemoteDescriptor(destination, remoteResourceIdentifier,  false);//gfs.findOne(destination);
//				GridFSInputFile destinationFile=mongoPrimaryInstance.createGFSFileObject(is, resource.getWriteConcern(), resource.getReadPreference());//gfs.createFile(is);
				ObjectId removedId=null;
				if (dest != null){
		//overwrite the file
	//				removedId=mongoPrimaryInstance.checkAndRemove(f, resource);
		// the third parameter to true replace the file			
					removedId = mongoPrimaryInstance.removeFile(resource, null, resource.isReplace(), null, dest);
					if((remoteResourceIdentifier != null) && ((remoteResourceIdentifier.equals(REMOTE_RESOURCE.ID))) && (removedId != null)){
						destinationId = mongoPrimaryInstance.createNewFile(resource, null, dir, name, removedId);
					}else{
						destinationId = mongoPrimaryInstance.createNewFile(resource, destination, dir , name, removedId);
					}
					if(logger.isDebugEnabled())
						logger.debug("ObjectId: "+destinationId);
					mongoPrimaryInstance.close();
				}else{
					destinationId = mongoPrimaryInstance.createNewFile(resource, destination, dir , name, null);
					mongoPrimaryInstance.close();
				}
			}else{
				mongoPrimaryInstance.close();
				throw new RemoteBackendException(" the source path is wrong. There isn't a file at "+source);
			}
		} else throw new RemoteBackendException("Invalid arguments: source "+source+" destination "+destination);
		return destinationId.toString();
		
	}

	
	public String safePut(MongoIOManager mongoPrimaryInstance, Object resource, String bucket, String key, boolean replace) throws UnknownHostException{
		OperationDefinition op=((MyFile)resource).getOperationDefinition();
		REMOTE_RESOURCE remoteResourceIdentifier=((MyFile)resource).getOperation().getRemoteResource();
		logger.info("MongoClient put method: "+op.toString());
		String dir=((MyFile)resource).getRemoteDir();
		String name=((MyFile)resource).getName();		
		ObjectId id=null;
		ObjectId oldId=null;
// id of the remote file if present		
		GridFSDBFile fold = mongoPrimaryInstance.retrieveRemoteDescriptor(bucket, remoteResourceIdentifier, false);			
		if(fold != null){
// if a file is present				
			logger.info("a file is already present at: "+bucket);
// keep old id		
			oldId=(ObjectId) fold.getId();
			logger.info("get old id: "+oldId);
// create new file		
     		id = mongoPrimaryInstance.createNewFile(resource, bucket, dir, name, null);
// remove old file			
			oldId = mongoPrimaryInstance.removeFile(resource, key, replace, oldId, fold);
//			oldId = removeOldMetadataFile(oldId);
// update the id to the new file
			id=mongoPrimaryInstance.updateId(id, oldId);
			
	    }else{
// create new file		
			id = mongoPrimaryInstance.createNewFile(resource, bucket, dir, name, oldId);
	    }
        return id.toString();
	}

}
