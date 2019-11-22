/**
 * 
 */
package org.gcube.contentmanagement.blobstorage.transport.backend.operation;

import java.io.IOException;

import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.resource.OperationDefinition.REMOTE_RESOURCE;
import org.gcube.contentmanagement.blobstorage.service.operation.DuplicateFile;
import org.gcube.contentmanagement.blobstorage.service.operation.Monitor;
import org.gcube.contentmanagement.blobstorage.transport.backend.MongoIOManager;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.gcube.contentmanagement.blobstorage.transport.backend.util.Costants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

/**
 * @author Roberto Cirillo (ISTI-CNR) 2018
 *
 */
public class DuplicateOperator extends DuplicateFile {

	Logger logger=LoggerFactory.getLogger(DuplicateOperator.class);
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
	public DuplicateOperator(String[] server, String user, String pwd, String bucket, Monitor monitor, boolean isChunk,
			String backendType, String[] dbs) {
		super(server, user, pwd, bucket, monitor, isChunk, backendType, dbs);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.gcube.contentmanagement.blobstorage.service.operation.DuplicateFile#execute(org.gcube.contentmanagement.blobstorage.transport.backend.MongoIO)
	 */
	@Override
	public String execute(MongoIOManager mongoPrimaryInstance){
		String destination=((MyFile)getResource()).getRemotePath()+Costants.DUPLICATE_SUFFIX;
		String dir=((MyFile)getResource()).getRemoteDir();
//		String name=((MyFile)getResource()).getName();
		if((getBucket() != null) && (!getBucket().isEmpty())){
			REMOTE_RESOURCE remoteResourceIdentifier=resource.getOperation().getRemoteResource();
			GridFSDBFile f = mongoPrimaryInstance.retrieveRemoteDescriptor(getBucket(), remoteResourceIdentifier, true);
			GridFSInputFile destinationFile=null;
			try {
//				GridFSInputFile f2 = mongoPrimaryInstance.createGFSFileObject(f.getFilename());
				destinationFile=mongoPrimaryInstance.createGFSFileObject(f.getInputStream(), resource.getWriteConcern(), resource.getReadPreference());//gfs.createFile(is);
				mongoPrimaryInstance.setGenericProperties(getResource(), destination, dir,
						destinationFile, destination.substring(destination.lastIndexOf(Costants.FILE_SEPARATOR)+1));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String destinationId=destinationFile.getId().toString();
			destinationFile.save();
			if(logger.isDebugEnabled())
				logger.debug("ObjectId: "+destinationId);
			mongoPrimaryInstance.close();
			return destinationId;
		} throw new RemoteBackendException("argument cannot be null for duplicate operation");
	}

}
