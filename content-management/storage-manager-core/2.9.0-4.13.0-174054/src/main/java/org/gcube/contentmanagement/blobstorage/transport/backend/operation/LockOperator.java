/**
 * 
 */
package org.gcube.contentmanagement.blobstorage.transport.backend.operation;

import java.io.FileNotFoundException;
import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.resource.OperationDefinition;
import org.gcube.contentmanagement.blobstorage.resource.OperationDefinition.OPERATION;
import org.gcube.contentmanagement.blobstorage.resource.OperationDefinition.REMOTE_RESOURCE;
import org.gcube.contentmanagement.blobstorage.service.operation.Download;
import org.gcube.contentmanagement.blobstorage.service.operation.Lock;
import org.gcube.contentmanagement.blobstorage.service.operation.Monitor;
import org.gcube.contentmanagement.blobstorage.transport.backend.MongoIOManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.gridfs.GridFSDBFile;

/**
 * @author Roberto Cirillo (ISTI-CNR) 2018
 *
 */
public class LockOperator extends Lock {
	
	final Logger logger=LoggerFactory.getLogger(LockOperator.class);
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
	public LockOperator(String[] server, String user, String pwd, String bucket, Monitor monitor, boolean isChunk,
			String backendType, String[] dbs) {
		super(server, user, pwd, bucket, monitor, isChunk, backendType, dbs);
		// TODO Auto-generated constructor stub
	}

	

	/* (non-Javadoc)
	 * @see org.gcube.contentmanagement.blobstorage.service.operation.Lock#execute(org.gcube.contentmanagement.blobstorage.transport.backend.MongoIO, org.gcube.contentmanagement.blobstorage.transport.backend.MongoIO)
	 */
	@Override
	public String execute(MongoIOManager mongoPrimaryInstance, MongoIOManager mongoSecondaryInstance, MyFile resource, String serverLocation) throws  Exception {
		OperationDefinition op=resource.getOperationDefinition();
		REMOTE_RESOURCE remoteResourceIdentifier=resource.getOperation().getRemoteResource();
//		if((resource.getLocalPath()!= null) && (!resource.getLocalPath().isEmpty())){
//			resource.setOperation(OPERATION.DOWNLOAD);
//			Download download= new DownloadOperator(getServer(), getUser(), getPassword(), getBucket(), getMonitor(), isChunk(), getBackendType(), getDbNames());
//			setDownload(download);
//			get(getDownload(), resource, true);
//			resource.setOperation(op);
//			mongoPrimaryInstance.close();
//			mongoPrimaryInstance=null;
//		}
		logger.info("MongoClient lock method: "+op.toString());
		String key=null;
		if(logger.isDebugEnabled())
			logger.debug("MongoDB - pathServer: "+resource.getAbsoluteRemotePath());
		GridFSDBFile f=mongoPrimaryInstance.retrieveRemoteDescriptor(resource.getAbsoluteRemotePath(), remoteResourceIdentifier, true);
		if(f!=null){
	//timestamp is used for compare to ttl of a file lock.		
			String lock=(String)f.get("lock");
			if((lock==null || lock.isEmpty()) || (mongoPrimaryInstance.isTTLUnlocked(f))){
				key=f.getId()+""+System.currentTimeMillis();
				f.put("lock", key);
				f.put("timestamp", System.currentTimeMillis());
				mongoPrimaryInstance.updateCommonFields(f, resource, OPERATION.LOCK);
				f.save();
			}else{
				mongoPrimaryInstance.checkTTL(f);
			}

		}else{
			mongoPrimaryInstance.close();
			throw new FileNotFoundException("REMOTE FILE NOT FOUND: WRONG PATH OR WRONG OBJECT ID");
		}
		return key;
	}

}
