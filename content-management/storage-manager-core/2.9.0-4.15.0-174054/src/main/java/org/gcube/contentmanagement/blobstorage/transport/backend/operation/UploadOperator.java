/**
 * 
 */
package org.gcube.contentmanagement.blobstorage.transport.backend.operation;

import java.io.IOException;
import org.bson.types.ObjectId;
import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.resource.OperationDefinition;
import org.gcube.contentmanagement.blobstorage.resource.OperationDefinition.REMOTE_RESOURCE;
import org.gcube.contentmanagement.blobstorage.service.operation.Monitor;
import org.gcube.contentmanagement.blobstorage.service.operation.Upload;
import org.gcube.contentmanagement.blobstorage.transport.backend.MongoIOManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.gridfs.GridFSDBFile;

/**
 * @author Roberto Cirillo (ISTI-CNR) 2018
 *
 */
public class UploadOperator extends Upload {

	Logger logger= LoggerFactory.getLogger(UploadOperator.class);
	/**
	 * @param server
	 * @param user
	 * @param pwd
	 * @param bucket
	 * @param monitor
	 * @param isChunk
	 * @param bck
	 * @param dbs
	 */
	public UploadOperator(String[] server, String user, String pwd, String bucket, Monitor monitor, boolean isChunk,
			String bck, String[] dbs) {
		super(server, user, pwd, bucket, monitor, isChunk, bck, dbs);
		// TODO Auto-generated constructor stub
	}

	
	/* (non-Javadoc)
	 * @see org.gcube.contentmanagement.blobstorage.service.operation.Upload#execute(org.gcube.contentmanagement.blobstorage.transport.backend.MongoIO, org.gcube.contentmanagement.blobstorage.transport.backend.MongoIO)
	 */
	@Override
	public String execute(MongoIOManager mongoPrimaryInstance, MongoIOManager mongoSecondaryInstance, MyFile resource, String bucket, boolean replace) throws IOException {
		OperationDefinition op=((MyFile)resource).getOperationDefinition();
		REMOTE_RESOURCE remoteResourceIdentifier=((MyFile)resource).getOperation().getRemoteResource();
		logger.info("MongoClient put method: "+op.toString());
		String dir=((MyFile)resource).getRemoteDir();
		String name=((MyFile)resource).getName();		
		Object id=null;
		ObjectId oldId=null;
	// id of the remote file if present		
		GridFSDBFile fold = mongoPrimaryInstance.retrieveRemoteDescriptor(bucket, remoteResourceIdentifier, false);			
		if(fold != null){
	// if a file is present				
			logger.info("a file is already present at: "+getBucket());
	// keep old id		
			oldId=(ObjectId) fold.getId();
			logger.info("get old id: "+oldId);
	// remove old file			
			oldId = mongoPrimaryInstance.removeFile(resource, bucket, replace, oldId, fold);
	//ADDED 03112015			
			if(!isReplaceOption()){
				return oldId.toString();
			}
	// END ADDED		
	    }
	// create new file
		logger.info("create new file "+bucket);
		if((remoteResourceIdentifier != null) && ((remoteResourceIdentifier.equals(REMOTE_RESOURCE.ID))) && (ObjectId.isValid(getBucket()))){
			id = mongoPrimaryInstance.createNewFile(resource, null, dir, name, new ObjectId(getBucket()));
		}else{
			id = mongoPrimaryInstance.createNewFile(resource, getBucket(), dir , name, oldId);
		}
		return id.toString();  
	}

	public String executeSafeMode(MongoIOManager mongoPrimaryInstance, MongoIOManager mongoSecondaryInstance) throws IOException {
		OperationDefinition op=((MyFile)resource).getOperationDefinition();
		REMOTE_RESOURCE remoteResourceIdentifier=((MyFile)resource).getOperation().getRemoteResource();
		logger.info("MongoClient put method: "+op.toString());
		String dir=((MyFile)resource).getRemoteDir();
		String name=((MyFile)resource).getName();		
		ObjectId id=null;
		ObjectId oldId=null;
// id of the remote file if present		
		GridFSDBFile fold = mongoPrimaryInstance.retrieveRemoteDescriptor(getBucket(), remoteResourceIdentifier, false);			
		if(fold != null){
// if a file is present				
			logger.info("a file is already present at: "+getBucket());
// keep old id		
			oldId=(ObjectId) fold.getId();
			logger.info("get old id: "+oldId);
// create new file		
     		id = mongoPrimaryInstance.createNewFile(resource, getBucket(), dir, name, null);
// remove old file			
			oldId = mongoPrimaryInstance.removeFile(resource, getBucket(), isReplaceOption(), oldId, fold);
//			oldId = removeOldMetadataFile(oldId);
// update the id to the new file
			id=mongoPrimaryInstance.updateId(id, oldId);
			
	    }else{
// create new file		
			id = mongoPrimaryInstance.createNewFile(resource, getBucket(), dir, name, oldId);
	    }
        return id.toString();
	}
	
}
