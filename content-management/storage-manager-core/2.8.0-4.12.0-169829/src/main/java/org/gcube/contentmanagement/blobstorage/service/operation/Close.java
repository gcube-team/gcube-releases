package org.gcube.contentmanagement.blobstorage.service.operation;

import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.service.impl.ServiceEngine;
import org.gcube.contentmanagement.blobstorage.transport.TransportManager;
import org.gcube.contentmanagement.blobstorage.transport.TransportManagerFactory;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Close extends Operation{
	
	/**
	 * Logger for this class
	 */
    final Logger logger=LoggerFactory.getLogger(GetSize.class);
	public String file_separator = ServiceEngine.FILE_SEPARATOR;//System.getProperty("file.separator");

	public Close(String[] server, String user, String pwd,  String bucket, Monitor monitor, boolean isChunk, String backendType, String[] dbs) {
		super(server, user, pwd, bucket, monitor, isChunk, backendType, dbs);
	}
	
	public String doIt(MyFile myFile) throws RemoteBackendException{
		TransportManagerFactory tmf= new TransportManagerFactory(server, user, password);
		TransportManager tm=tmf.getTransport(backendType, myFile.getGcubeMemoryType(), dbNames, myFile.getWriteConcern(), myFile.getReadPreference());
		try {
			tm.close();
		} catch (Exception e) {
			throw new RemoteBackendException(" Error in GetSize operation ", e.getCause());			}
		if (logger.isDebugEnabled()) {
			logger.debug(" PATH " + bucket);
		}
		return null;
	}

	@Override
	public String initOperation(MyFile file, String remotePath,
		String author, String[] server, String rootArea, boolean replaceOption) {
		return null;
	}


	@Override
	public String initOperation(MyFile resource, String RemotePath,
			String author, String[] server, String rootArea) {
		return null;
	}


}
