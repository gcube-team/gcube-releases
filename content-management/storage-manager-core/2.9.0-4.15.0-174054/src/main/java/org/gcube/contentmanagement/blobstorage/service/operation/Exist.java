/**
 * 
 */
package org.gcube.contentmanagement.blobstorage.service.operation;

import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.bson.types.ObjectId;
import org.gcube.contentmanagement.blobstorage.service.directoryOperation.BucketCoding;
import org.gcube.contentmanagement.blobstorage.transport.TransportManager;
import org.gcube.contentmanagement.blobstorage.transport.TransportManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Implements a Exist operation: check if a given object exist
 * @author Roberto Cirillo (ISTI - CNR) 2018
 *
 */

public class Exist extends Operation{

		/**
		 * Logger for this class
		 */
	    final Logger logger=LoggerFactory.getLogger(Exist.class);

		public Exist(String[] server, String user, String pwd,  String bucket, Monitor monitor, boolean isChunk, String backendType, String[] dbs) {
			super(server, user, pwd, bucket, monitor, isChunk, backendType, dbs);
		}
		
		public String doIt(MyFile myFile) throws RemoteBackendException{
			TransportManagerFactory tmf= new TransportManagerFactory(server, user, password);
			TransportManager tm=tmf.getTransport(backendType, myFile.getGcubeMemoryType(), dbNames, myFile.getWriteConcern(), myFile.getReadPreference());
			boolean isPresent=false;
			try {
				isPresent = tm.exist(bucket);
			} catch (Exception e) {
				tm.close();
				throw new RemoteBackendException(" Error in Exist operation ", e.getCause());			}
			if (logger.isDebugEnabled()) {
				logger.debug(" PATH " + bucket);
			}
			return isPresent+"";
		}

		@Override
		public String initOperation(MyFile file, String remotePath,
			String author, String[] server, String rootArea, boolean replaceOption) {
//			String[] dirs= remotePath.split(file_separator);
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
			throw new IllegalArgumentException("Input/Output stream is not compatible with Exist operation");
		}

	
}
