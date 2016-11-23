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
	 * Save inpustream into storage and get metadata
	 * @param stream
	 * @param storage
	 * @param remotePath
	 * @param filenameWithExtension
	 * @return
	 * @throws Exception 
	 * @throws InternalErrorException
	 */
	public static MetaInfo getMetadataInfo(final InputStream stream, final GCUBEStorage storage, final String remotePath, final String filenameWithExtension) throws Exception{
//		System.out.println("***** REMOTE PATH " + remotePath);
		final MultipleOutputStream mos = new MultipleOutputStream(stream);
		final MetaInfo metadataInfo = new MetaInfo();

		//save to storage
		Thread t1 = new Thread(){
			public void run(){
//							System.out.println("*** START STEP A - Save file to Storage in remotepath " + remotePath);
							
				logger.debug("Save file to Storage in remotepath " + remotePath);
				long start = System.currentTimeMillis();
				try(InputStream is = mos.getS1()){
					saveToStorage(is, storage, remotePath, metadataInfo, filenameWithExtension);
				} catch (IOException e) {
					throw new RemoteBackendException(e.getMessage());
				} 
				
//				System.out.println("**** END STEP A - Save file to Storage in remotepath " + remotePath + " in milliseconds: "+ (System.currentTimeMillis()-start));

			}
		};

		//get mimetype utility
		Thread t2 = new Thread(){
			public void run(){
//									System.out.println("*** START STEP 1B - Detect Mimetype for file " + filenameWithExtension);
				logger.debug("Mimetype detect for file " + filenameWithExtension);
				long start = System.currentTimeMillis();

				try(InputStream is = mos.getS2()){
					String mimetype = MimeTypeUtil.getMimeType(filenameWithExtension, is);	
					logger.debug(filenameWithExtension + " mimetyepe: " + mimetype);
					//					System.out.println("MIMETYPE " + mimetype);
					metadataInfo.setMimeType(mimetype);
				} catch (IOException e) {
					throw new RemoteBackendException(e.getMessage());
				}
//				System.out.println("**** C - Mimetype detected for file " + filenameWithExtension + " in milliseconds: "+ (System.currentTimeMillis()-start));

				logger.info("Mimetype detected for file " + filenameWithExtension + " in milliseconds: "+ (System.currentTimeMillis()-start));
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

		updateMimeTypeIntoStorage(storage, metadataInfo, remotePath, filenameWithExtension);

		return metadataInfo;
	}



	public static MetaInfo getMetadataInfo(InputStream stream, GCUBEStorage storage, String remotePath,
			String filenameWithExtension, String mimeType, long size) throws Exception {
		if (mimeType!=null && !mimeType.isEmpty()){

			final MetaInfo metadataInfo = new MetaInfo(); 
			metadataInfo.setMimeType(mimeType);
			metadataInfo.setSize((int) size);
			logger.debug("Save file to Storage in remotepath " + remotePath);
			//			System.out.println("***** REMOTE PATH " + remotePath);

			saveToStorage(stream, storage, remotePath, metadataInfo, filenameWithExtension);

			logger.info("Set Mimetype for file " + filenameWithExtension + " to " + mimeType);	

			if (mimeType==null || mimeType.isEmpty())
				updateMimeTypeIntoStorage(storage, metadataInfo, remotePath, filenameWithExtension);

			return metadataInfo;
		}else
			return getMetadataInfo(stream, storage, remotePath, filenameWithExtension);
	}

	private static void saveToStorage(InputStream is, GCUBEStorage storage, String remotePath, MetaInfo metadataInfo, String filenameWithExtension) throws IOException {
			long start = System.currentTimeMillis();
		String url = null;
		int availableSize = is.available();
		try {
			if (metadataInfo.getMimeType()!=null)
				url = storage.putStream(is,remotePath, metadataInfo.getMimeType());
			else				
				url = storage.putStream(is,remotePath);
			
			metadataInfo.setStorageId(url);
			metadataInfo.setRemotePath(remotePath);
			logger.info(filenameWithExtension + " saved to " + remotePath + " - GCUBEStorage URL : " + url);
//				System.out.println("**** A - File " + remotePath + " saved into Storage in milliseconds: "+ (System.currentTimeMillis()-start));

					long start00 = System.currentTimeMillis();
			if (metadataInfo.getSize()<=0){
				long size = storage.getRemoteFileSize(remotePath);
				if (size < availableSize)
					logger.error("size < available for file " + remotePath);
				else
					metadataInfo.setSize((int) size);			
			}
//						System.out.println("**** B - Getting size from Storage for file " + remotePath + " in milliseconds: "+ (System.currentTimeMillis()-start00));

		} catch (RemoteBackendException e) {
			logger.error(remotePath + " remote path not present " + e);
			throw new RemoteBackendException(e.getMessage());
		}

	}


	/**
	 * Update mimetype into storage
	 * @param storage
	 * @param metadataInfo
	 * @param remotePath
	 * @param filenameWithExtension
	 */
	private static void updateMimeTypeIntoStorage(GCUBEStorage storage, MetaInfo metadataInfo, String remotePath, String filenameWithExtension) {
		long start = System.currentTimeMillis();
		//update mimetype metadata in storage
		if (metadataInfo.getStorageId()!=null && metadataInfo.getMimeType()!=null) {
			logger.info("Update mimetype metadata for remotepath: " + remotePath + " to "+ metadataInfo.getMimeType());
			storage.setMetaInfo("mimetype", metadataInfo.getMimeType(), remotePath);
		}
		//		System.out.println("**** D - update Mimetype into storage for file " + filenameWithExtension + " in milliseconds: "+ (System.currentTimeMillis()-start));
		logger.info("UPDATE Mimetype into storage for file " + filenameWithExtension + " in milliseconds: "+ (System.currentTimeMillis()-start));

	}

}
