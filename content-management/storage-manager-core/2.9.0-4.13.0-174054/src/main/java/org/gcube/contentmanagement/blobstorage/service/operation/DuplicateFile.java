/**
 * 
 */
package org.gcube.contentmanagement.blobstorage.service.operation;

import org.bson.types.ObjectId;
import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.service.directoryOperation.BucketCoding;
import org.gcube.contentmanagement.blobstorage.transport.TransportManager;
import org.gcube.contentmanagement.blobstorage.transport.TransportManagerFactory;
import org.gcube.contentmanagement.blobstorage.transport.backend.MongoIOManager;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Roberto Cirillo (ISTI-CNR) 2018
 *
 */
public abstract class DuplicateFile extends Operation {

	/**
	 * Logger for this class
	 */
    final Logger logger=LoggerFactory.getLogger(DuplicateFile.class);
	protected String sourcePath;
	protected MyFile resource;

	public DuplicateFile(String[] server, String user, String pwd,  String bucket, Monitor monitor, boolean isChunk, String backendType, String[] dbs) {
		super(server, user, pwd, bucket, monitor, isChunk, backendType, dbs);
	}
	
	public String doIt(MyFile myFile) throws RemoteBackendException{
		TransportManagerFactory tmf= new TransportManagerFactory(server, user, password);
		TransportManager tm=tmf.getTransport(backendType, myFile.getGcubeMemoryType(), dbNames, myFile.getWriteConcern(), myFile.getReadPreference());
		String id=null;
		try {
//			id = tm.duplicateFile(myFile, bucket);
			id = tm.duplicateFile(this);
		} catch (Exception e) {
			tm.close();
			throw new RemoteBackendException(" Error in GetSize operation ", e.getCause());			}
		if (logger.isDebugEnabled()) {
			logger.debug(" PATH " + bucket);
		}
		return id;
	}

	@Override
	public String initOperation(MyFile file, String remotePath,	String author, String[] server, String rootArea, boolean replaceOption) {
		if(remotePath != null){
			boolean isId=ObjectId.isValid(remotePath);
			setResource(file);
			if(!isId){
//				String[] dirs= remotePath.split(file_separator);
				if(logger.isDebugEnabled())
					logger.debug("remotePath: "+remotePath);
				String buck=null;
				buck = new BucketCoding().bucketFileCoding(remotePath, rootArea);
				return bucket=buck;
			}else{
				return bucket=remotePath;
			}
		}else throw new RemoteBackendException("argument cannot be null");
	}


	@Override
	public String initOperation(MyFile resource, String RemotePath,
			String author, String[] server, String rootArea) {
		throw new IllegalArgumentException("Input/Output stream is not compatible with getSize operation");
	}
	
	public abstract String execute(MongoIOManager mongoPrimaryInstance);

	public String getSourcePath() {
		return sourcePath;
	}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	public MyFile getResource() {
		return resource;
	}

	public void setResource(MyFile resource) {
		this.resource = resource;
	}

	
}
