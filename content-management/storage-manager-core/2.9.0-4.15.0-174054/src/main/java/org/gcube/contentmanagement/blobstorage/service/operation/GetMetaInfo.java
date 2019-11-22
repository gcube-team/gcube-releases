package org.gcube.contentmanagement.blobstorage.service.operation;

import org.bson.types.ObjectId;
import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.service.directoryOperation.BucketCoding;
import org.gcube.contentmanagement.blobstorage.transport.TransportManager;
import org.gcube.contentmanagement.blobstorage.transport.TransportManagerFactory;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetMetaInfo extends Operation {

	/**
	 * Logger for this class
	 */
    final Logger logger=LoggerFactory.getLogger(GetSize.class);

	public GetMetaInfo(String[] server, String user, String pwd,  String bucket, Monitor monitor, boolean isChunk, String backendType, String[] dbs) {
		super(server, user, pwd, bucket, monitor, isChunk, backendType, dbs);
	}
	
	public String doIt(MyFile myFile) throws RemoteBackendException{
		TransportManagerFactory tmf= new TransportManagerFactory(server, user, password);
		TransportManager tm=tmf.getTransport(backendType, myFile.getGcubeMemoryType(), dbNames, myFile.getWriteConcern(), myFile.getReadPreference());
		String value=null;
		try {
			value=tm.getFileProperty(bucket, myFile.getGenericPropertyField());
		} catch (Exception e) {
			tm.close();
			throw new RemoteBackendException(" Error in GetMetaFile operation ", e.getCause());			}
		if (logger.isDebugEnabled()) {
			logger.debug(" PATH " + bucket);
		}
		
		return value;
	}

	@Override
	public String initOperation(MyFile file, String remotePath,
		String author, String[] server, String rootArea, boolean replaceOption) {
//		String[] dirs= remotePath.split(file_separator);
		if(logger.isDebugEnabled())
			logger.debug("remotePath: "+remotePath);
		String buck=null;
		boolean isId=ObjectId.isValid(remotePath);
		if(!isId){
			buck = new BucketCoding().bucketFileCoding(remotePath, rootArea);
			return bucket=buck;
		}else{
			return bucket=remotePath;
		}
	}


	@Override
	public String initOperation(MyFile resource, String RemotePath,
			String author, String[] server, String rootArea) {
		throw new IllegalArgumentException("method not compatible with getMetaInfo operation");
	}

}
