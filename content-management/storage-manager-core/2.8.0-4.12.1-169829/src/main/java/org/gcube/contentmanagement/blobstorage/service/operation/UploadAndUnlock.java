package org.gcube.contentmanagement.blobstorage.service.operation;

import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.service.directoryOperation.BucketCoding;
import org.gcube.contentmanagement.blobstorage.transport.TransportManager;
import org.gcube.contentmanagement.blobstorage.transport.TransportManagerFactory;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.gcube.contentmanagement.blobstorage.transport.backend.operation.UploadOperator;
/**
 * @deprecated
 * @author Roberto Cirillo (ISTI - CNR)
 *
 */
public class UploadAndUnlock extends Operation {

//	private String keyUnlock;
	
	public UploadAndUnlock(String[] server, String user, String pwd,  String bucket, Monitor monitor, boolean isChunk, String backendType, String[] dbs) {
		// TODO Auto-generated constructor stub
			super(server,user, pwd, bucket, monitor, isChunk, backendType, dbs);
		}
	
	@Override
	public String doIt(MyFile myFile) throws RemoteBackendException {
		if (logger.isDebugEnabled()) {
			logger.debug(" UPLOAD " + myFile.getLocalPath()
					+ " author: " + myFile.getOwner());
		}
		Upload upload= new UploadOperator(getServer(), getUser(), getPassword(), getBucket(), getMonitor(), isChunk(), getBackendType(), getDbNames());
		String objectId=null;
		try {
			//inserire parametro per il lock 
			objectId=put(upload, myFile, isChunk(), false, false, true);
		} catch (Exception e) {
			TransportManagerFactory tmf=new TransportManagerFactory(server, user, password);
			TransportManager tm=tmf.getTransport(backendType, myFile.getGcubeMemoryType(), dbNames, myFile.getWriteConcern(), myFile.getReadPreference());
			tm.close();
			throw new RemoteBackendException(" Error in uploadAndUnlock operation ", e.getCause());		}
		return objectId;

	}

	@Override
	public String initOperation(MyFile file, String remotePath,
			String author, String[] server, String rootArea,
			boolean replaceOption) {
		// set replace option
//		this.replaceOption=replaceOption;
		// the name of bucket is formed: path_____fileName_____author				
		String bucketName=new BucketCoding().bucketFileCoding(remotePath, rootArea);
		return bucket=bucketName;

	}

	@Override
	public String initOperation(MyFile resource, String RemotePath,
			String author, String[] server, String rootArea) {
		// TODO Auto-generated method stub
		return null;
	}

}
