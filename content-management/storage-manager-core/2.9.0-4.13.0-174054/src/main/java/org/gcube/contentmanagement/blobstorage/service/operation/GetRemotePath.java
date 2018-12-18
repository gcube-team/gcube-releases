package org.gcube.contentmanagement.blobstorage.service.operation;

import org.bson.types.ObjectId;
import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.transport.TransportManager;
import org.gcube.contentmanagement.blobstorage.transport.TransportManagerFactory;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetRemotePath extends Operation{

	/**
	 * Logger for this class
	 */
    final Logger logger=LoggerFactory.getLogger(GetSize.class);
	private String rootPath;

	public GetRemotePath(String[] server, String user, String pwd,  String bucket, Monitor monitor, boolean isChunk, String backendType, String[] dbs) {
		super(server, user, pwd, bucket, monitor, isChunk, backendType,dbs);
	}
	
	public String doIt(MyFile myFile) throws RemoteBackendException{
		TransportManagerFactory tmf= new TransportManagerFactory(server, user, password);
		TransportManager tm=tmf.getTransport(backendType, myFile.getGcubeMemoryType(), dbNames, myFile.getWriteConcern(), myFile.getReadPreference());
		String path=null;
		try {
			path = tm.getRemotePath(bucket);
		} catch (Exception e) {
			tm.close();
			throw new RemoteBackendException(" Error in GetSize operation ", e.getCause());			}
		if (logger.isDebugEnabled()) {
			logger.debug(" PATH " + bucket);
		}
		logger.debug("\t path "+path+"\n\t rootPath: "+rootPath);
		int rootLength=rootPath.length();
		if((path.length() >= rootLength)){
			path=path.substring(rootLength-1);
			System.out.println("new relative path "+ path);
			return path;
		}else{
			throw new RuntimeException("expected rootPath or expected relative path are malformed: rootPath: "+rootPath+ " relativePath: "+path);
		}
	}

	@Override
	public String initOperation(MyFile file, String remotePath,
		String author, String[] server, String rootArea, boolean replaceOption) {
		rootPath=file.getRootPath();
		logger.trace("rootArea is "+file.getRootPath()+ " absoluteremotepath is "+file.getAbsoluteRemotePath());
		if(logger.isDebugEnabled())
			logger.debug("remotePath: "+remotePath);
		boolean isId=ObjectId.isValid(remotePath);
		if(!isId){
			throw new RuntimeException("the getRemotePath method have an invalid id"+ remotePath);
		}else{
			return bucket=remotePath;
		}
	}


	@Override
	public String initOperation(MyFile resource, String RemotePath,
			String author, String[] server, String rootArea) {
		throw new IllegalArgumentException("Input/Output stream is not compatible with getSize operation");
	}

	
	
}
