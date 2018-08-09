/**
 * 
 */
package org.gcube.contentmanagement.blobstorage.transport.backend.operation;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.bson.types.ObjectId;
import org.gcube.contentmanagement.blobstorage.resource.OperationDefinition;
import org.gcube.contentmanagement.blobstorage.resource.OperationDefinition.REMOTE_RESOURCE;
import org.gcube.contentmanagement.blobstorage.service.operation.Download;
import org.gcube.contentmanagement.blobstorage.service.operation.Monitor;
import org.gcube.contentmanagement.blobstorage.transport.backend.MongoOperationManager;
import org.gcube.contentmanagement.blobstorage.transport.backend.MongoIOManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.gridfs.GridFSDBFile;

/**
 * @author Roberto Cirillo (ISTI-CNR) 2018
 *
 */
public class DownloadOperator extends Download {
	
	final Logger logger=LoggerFactory.getLogger(DownloadOperator.class);
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
	public DownloadOperator(String[] server, String user, String pwd, String bucket, Monitor monitor, boolean isChunk,
			String backendType, String[] dbs) {
		super(server, user, pwd, bucket, monitor, isChunk, backendType, dbs);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.gcube.contentmanagement.blobstorage.service.operation.Download#execute(org.gcube.contentmanagement.blobstorage.transport.backend.MongoIO)
	 */
	@Override
	public ObjectId execute(MongoIOManager mongoPrimaryInstance, MongoIOManager mongoSecondaryInstance) throws IOException  {
		OperationDefinition op=resource.getOperationDefinition();
		logger.info("MongoClient get method: "+op.toString());
		mongoPrimaryInstance.getConnectionDB( MongoOperationManager.getPrimaryCollectionName(), true);// getDB(resource);
//		GridFS gfs=mongoPrimaryInstance.getGfs(getPrimaryCollectionName(), true);
	//if the operation is required by id we avoid to check if the object is available by path	
		REMOTE_RESOURCE remoteResourceIdentifier=resource.getOperation().getRemoteResource();
		logger.info("operation required by "+remoteResourceIdentifier);
		GridFSDBFile f = mongoPrimaryInstance.retrieveRemoteDescriptor(getBucket(), remoteResourceIdentifier, false);	//previous value was true	
		ObjectId id=null;
		if(f!=null){
			id = mongoPrimaryInstance.getRemoteObject(resource, f);
	//check if the file is present on another db in the same backend		
		}else if(mongoSecondaryInstance!=null){
//			DB secondaryDb =mongoSecondaryInstance.getConnectionDB(resource.getWriteConcern(), resource.getReadPreference(), getSecondaryCollectionName(), true);// getDB(resource);
//			GridFS secondaryGfs = mongoSecondaryInstance.getGfs(getSecondaryCollectionName(), true);  
			GridFSDBFile secondaryF = mongoSecondaryInstance.retrieveRemoteDescriptor(getRemotePath(), remoteResourceIdentifier, true);		
			if(secondaryF !=null){
				id = mongoSecondaryInstance.getRemoteObject( resource, secondaryF);
			}
		}else{
			mongoPrimaryInstance.close();
			throw new FileNotFoundException("REMOTE FILE NOT FOUND: WRONG PATH OR WRONG OBJECT ID");
		}
		return id;

	}

}
