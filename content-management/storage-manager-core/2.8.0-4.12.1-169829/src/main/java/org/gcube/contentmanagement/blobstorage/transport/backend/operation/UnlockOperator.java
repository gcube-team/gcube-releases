/**
 * 
 */
package org.gcube.contentmanagement.blobstorage.transport.backend.operation;

import java.io.FileNotFoundException;

import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.resource.OperationDefinition;
import org.gcube.contentmanagement.blobstorage.resource.OperationDefinition.OPERATION;
import org.gcube.contentmanagement.blobstorage.resource.OperationDefinition.REMOTE_RESOURCE;
import org.gcube.contentmanagement.blobstorage.service.operation.Monitor;
import org.gcube.contentmanagement.blobstorage.service.operation.Unlock;
import org.gcube.contentmanagement.blobstorage.transport.backend.MongoIOManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;

/**
 * @author Roberto Cirillo (ISTI-CNR) 2018
 *
 */
public class UnlockOperator extends Unlock {

	Logger logger= LoggerFactory.getLogger(UnlockOperator.class);
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
	public UnlockOperator(String[] server, String user, String pwd, String bucket, Monitor monitor, boolean isChunk,
			String backendType, String[] dbs) {
		super(server, user, pwd, bucket, monitor, isChunk, backendType, dbs);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.gcube.contentmanagement.blobstorage.service.operation.Unlock#execute(org.gcube.contentmanagement.blobstorage.transport.backend.MongoIO, org.gcube.contentmanagement.blobstorage.transport.backend.MongoIO)
	 */
	@Override
	public String execute(MongoIOManager mongoPrimaryInstance, MongoIOManager mongoSecondaryInstance, MyFile resource, String bucket, String key4unlock) throws Exception {
		String id=null;
		OperationDefinition op=resource.getOperationDefinition();
		REMOTE_RESOURCE remoteResourceIdentifier=resource.getOperation().getRemoteResource();
		logger.info("MongoClient unlock method: "+op.toString());
		if(((resource.getLocalPath() !=null) && (!resource.getLocalPath().isEmpty()))){
			 resource.setOperation(OPERATION.UPLOAD);
			 id=put(getUpload(), getResource(), isChunk(), false, false, true);
			 mongoPrimaryInstance.close();
			resource.setOperation(op);
		}
		String dir=((MyFile)resource).getRemoteDir();
		String name=((MyFile)resource).getName();
		String path=getBucket(); 
		if(logger.isDebugEnabled())
			logger.debug("DIR: "+dir+" name: "+name+" fullPath "+path+" bucket: "+bucket);
		GridFSDBFile f=mongoPrimaryInstance.retrieveRemoteDescriptor(path, remoteResourceIdentifier, true);
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
	     	        		mongoPrimaryInstance.updateCommonFields((GridFSFile)f, (MyFile)resource, OPERATION.UNLOCK);
	     	        		f.save();
	     	        	 }else{
	     	        		mongoPrimaryInstance.close();
	     	        		 throw new IllegalAccessError("bad key for unlock");
	     	        	 }
	     	         }else{
	     	        	mongoPrimaryInstance.updateCommonFields((GridFSFile)f, (MyFile)resource, OPERATION.UNLOCK);
     	        		f.save();
	     	         }
	        }else{
	        	mongoPrimaryInstance.close();
	        	throw new FileNotFoundException(path);
	        }
	     }else{
	    	 mongoPrimaryInstance.close(); 
	       	throw new FileNotFoundException(path);
	     }
		return id;
	}

}
