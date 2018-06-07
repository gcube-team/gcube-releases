package org.gcube.contentmanagement.blobstorage.service.operation;

import org.bson.types.ObjectId;
import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.service.directoryOperation.BucketCoding;
import org.gcube.contentmanagement.blobstorage.service.directoryOperation.DirectoryBucket;
import org.gcube.contentmanagement.blobstorage.service.impl.ServiceEngine;
import org.gcube.contentmanagement.blobstorage.transport.TransportManager;
import org.gcube.contentmanagement.blobstorage.transport.TransportManagerFactory;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Implements a getSize operation from the remote system: return the dimension of a file in the remote system
 * @author Roberto Cirillo (ISTI - CNR)
 *
 */

public class GetSize extends Operation{

		/**
		 * Logger for this class
		 */
	    final Logger logger=LoggerFactory.getLogger(GetSize.class);
		public String file_separator = ServiceEngine.FILE_SEPARATOR;//System.getProperty("file.separator");

		public GetSize(String[] server, String user, String pwd,  String bucket, Monitor monitor, boolean isChunk, String backendType) {
			super(server, user, pwd, bucket, monitor, isChunk, backendType);
		}
		
		public String doIt(MyFile myFile) throws RemoteBackendException{
			TransportManagerFactory tmf= new TransportManagerFactory(server, user, password);
			TransportManager tm=tmf.getTransport(backendType, myFile.getGcubeMemoryType());
			long dim=0;
			try {
				dim = tm.getSize(bucket);
			} catch (Exception e) {
				tm.close();
				throw new RemoteBackendException(" Error in GetSize operation ", e.getCause());			}
			if (logger.isDebugEnabled()) {
				logger.debug(" PATH " + bucket);
			}
			return ""+dim;
		}

		@Override
		public String initOperation(MyFile file, String remotePath,
			String author, String[] server, String rootArea, boolean replaceOption) {
			String[] dirs= remotePath.split(file_separator);
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
			throw new IllegalArgumentException("Input/Output stream is not compatible with getSize operation");
		}

	}

