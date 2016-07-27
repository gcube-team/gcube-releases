package org.apache.jackrabbit.j2ee.workspacemanager.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.jackrabbit.j2ee.workspacemanager.storage.GCUBEStorage;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Util {

	private static Logger logger = LoggerFactory.getLogger(GCUBEStorage.class);
	/**
	 * Get metadata info and save to storage using the same inpustream
	 * @param stream
	 * @param storage
	 * @param name
	 * @param remotePath
	 * @param filenameWithExtension 
	 * @return
	 * @throws Exception 
	 * @throws InternalErrorException
	 */
	public static MetaInfo getMetadataInfo(InputStream stream, final GCUBEStorage storage, final String remotePath, final String filenameWithExtension) throws Exception{

		final MultipleOutputStream mos = new MultipleOutputStream(stream);
		final MetaInfo metadataInfo = new MetaInfo();

		//save to storage
		Thread t1 = new Thread(){
			public void run(){
				long start = System.currentTimeMillis();

				try(InputStream is = mos.getS1()){
					String url = null;
					try {
						url = storage.putStream(is,remotePath);
						metadataInfo.setStorageId(url);
						logger.info("saved to " + remotePath + " - GCUBEStorage URL : " + url);						
						long size =	storage.getRemoteFileSize(remotePath);
					//	System.out.println("SIZE ***************** "+ size);
						metadataInfo.setSize((int) size);
					} catch (RemoteBackendException e) {
						logger.error(remotePath + " remote path not present" + e);
						throw new RemoteBackendException(e.getMessage());
					}

				} catch (IOException e) {
					throw new RemoteBackendException(e.getMessage());
				} 
				logger.info("File "+ remotePath + " uploaded from " + Thread.currentThread().getName()+" in "+(System.currentTimeMillis()-start)+ " millis");
			}
		};

		//get mimetype utility
		Thread t2 = new Thread(){
			public void run(){
				long start = System.currentTimeMillis();
				logger.info("Mymetype detect for file " + filenameWithExtension);

				try(InputStream is = mos.getS2()){
					String mimetype = MimeTypeUtil.getMimeType(filenameWithExtension, is);		
					metadataInfo.setMimeType(mimetype);
					logger.info("Mymetype " + mimetype);
				} catch (IOException e) {
					throw new RemoteBackendException(e.getMessage());
				}
				
				logger.info("MimeType detect from " + Thread.currentThread().getName()+" in "+(System.currentTimeMillis()-start) + " millis");
			}
		};

		t1.start();
		t2.start();
		
		try {
			mos.startWriting();
			t1.join();
			t2.join();
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}

		//update mimetype metadata in storage
		if (metadataInfo.getStorageId()!=null && metadataInfo.getMimeType()!=null) {
			logger.info("Update mimetype metadata for remotepath: " + remotePath + " to "+ metadataInfo.getMimeType());
			storage.setMetaInfo("mimetype", metadataInfo.getMimeType(), remotePath);
		}
		return metadataInfo;
	}


}
