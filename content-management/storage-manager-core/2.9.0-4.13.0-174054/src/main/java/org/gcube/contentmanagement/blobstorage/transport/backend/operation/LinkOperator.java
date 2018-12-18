/**
 * 
 */
package org.gcube.contentmanagement.blobstorage.transport.backend.operation;

import java.net.UnknownHostException;

import org.bson.types.ObjectId;
import org.gcube.contentmanagement.blobstorage.resource.MemoryType;
import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.resource.OperationDefinition.OPERATION;
import org.gcube.contentmanagement.blobstorage.resource.OperationDefinition.REMOTE_RESOURCE;
import org.gcube.contentmanagement.blobstorage.service.operation.Link;
import org.gcube.contentmanagement.blobstorage.service.operation.Monitor;
import org.gcube.contentmanagement.blobstorage.service.operation.Operation;
import org.gcube.contentmanagement.blobstorage.transport.backend.MongoIOManager;
import org.gcube.contentmanagement.blobstorage.transport.backend.util.Costants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

/**
 * @author Roberto Cirillo (ISTI-CNR) 2018
 *
 */
public class LinkOperator extends Link {

	
	Logger logger=LoggerFactory.getLogger(LinkOperator.class);
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
	public LinkOperator(String[] server, String user, String pwd, String bucket, Monitor monitor, boolean isChunk,
			String backendType, String[] dbs) {
		super(server, user, pwd, bucket, monitor, isChunk, backendType, dbs);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.gcube.contentmanagement.blobstorage.service.operation.Link#execute(org.gcube.contentmanagement.blobstorage.transport.backend.MongoIO, org.gcube.contentmanagement.blobstorage.transport.backend.MongoIO, org.gcube.contentmanagement.blobstorage.resource.MyFile, java.lang.String)
	 */
	@Override
	public String execute(MongoIOManager mongoPrimaryInstance, MongoIOManager mongoSecondaryInstance, MyFile resource, String sourcePath, String destinationPath) throws UnknownHostException {
		boolean replace=true;
		String source=sourcePath;
		String destination=destinationPath;
		String dir=resource.getRemoteDir();
		String name=resource.getName();
		REMOTE_RESOURCE remoteResourceIdentifier=resource.getOperation().getRemoteResource();
		String destinationId=null;
		String sourceId=null;
		logger.debug("link operation on Mongo backend, parameters: source path: "+source+" destination path: "+destination);
		if((source != null) && (!source.isEmpty()) && (destination != null) && (!destination.isEmpty())){
			GridFSDBFile f = mongoPrimaryInstance.retrieveRemoteDescriptor(source, remoteResourceIdentifier, false);
			if(f != null){
				int count=1;
				if((f.containsField(Costants.COUNT_IDENTIFIER)) && ((f.get(Costants.COUNT_IDENTIFIER) != null))){
					count=(Integer)f.get(Costants.COUNT_IDENTIFIER);
					count++;
				}
				f.put(Costants.COUNT_IDENTIFIER, count);
				mongoPrimaryInstance.updateCommonFields(f, resource, OPERATION.LINK);
				sourceId=f.getId().toString();
				f.save();
		    }else{
		    	mongoPrimaryInstance.close();
		    	throw new IllegalArgumentException(" source remote file not found at: "+source);
		    }
	// check if the destination file exists
//			GridFSDBFile fold = gfs.findOne(destinationPath);
			GridFSDBFile fold = mongoPrimaryInstance.retrieveRemoteDescriptor(destinationPath, remoteResourceIdentifier, false);
			if(fold != null){
				String oldir=(String)fold.get("dir");
		        if(logger.isDebugEnabled())
		      	  logger.debug("old dir  found "+oldir);
		        if((oldir.equalsIgnoreCase(((MyFile)resource).getRemoteDir()))){
		         	  ObjectId oldId=(ObjectId) fold.getId();
		         	  if(!replace){
		         		  return oldId.toString();
		         	  }else{
		         		  if(logger.isDebugEnabled())
		         			  logger.debug("remove id: "+oldId);
		         		  String lock=(String)fold.get("lock");
		         //check if the od file is locked		  
		         		  if((lock !=null) && (!lock.isEmpty()) && (!mongoPrimaryInstance.isTTLUnlocked(fold))){
		         			 mongoPrimaryInstance.close();
		         			  throw new IllegalAccessError("The file is locked");
		         		  }else{
		         //remove old file			  
		         	  		  mongoPrimaryInstance.removeGFSFile(fold, oldId);
		         		  }
		         	  }
		        }
		    }
	// create destination file
		    GridFSInputFile destinationFile=null;    
			//create new file
		    byte[] data=new byte[1];
		    if (resource.getGcubeMemoryType()== MemoryType.VOLATILE){
		    	destinationFile = mongoPrimaryInstance.createGFSFileObject(data);//gfs.createFile(data);
		    }else{
		    	destinationFile = mongoPrimaryInstance.createGFSFileObject(data, resource.getWriteConcern(), resource.getReadPreference());//gfs.createFile(data);
		    }
			if(logger.isDebugEnabled())
			   	logger.debug("Directory: "+dir);
			mongoPrimaryInstance.setGenericProperties(resource, destinationPath, dir,
					destinationFile, name);
			destinationFile.put(Costants.LINK_IDENTIFIER, sourceId);
			destinationId=destinationFile.getId().toString();
			if(logger.isDebugEnabled())
				logger.debug("ObjectId: "+destinationId);
			mongoPrimaryInstance.buildDirTree(mongoPrimaryInstance.getMetaDataCollection(null), dir);
			destinationFile.save();
			mongoPrimaryInstance.close();
		}else{
			mongoPrimaryInstance.close();
			throw new IllegalArgumentException(" invalid argument: source: "+source+" dest: "+destination+" the values must be not null and not empty");
		}
		return destinationId.toString();
	}

}
