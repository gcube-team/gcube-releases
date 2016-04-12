package org.gcube.portlets.user.shareupdates.server;

import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Massimiliano Assante ISTI-CNR
 *
 */
public class UploadToStorageThread implements Runnable {
	private static Logger _log = LoggerFactory.getLogger(UploadToStorageThread.class);

	/**
	 * remote path (with file name) in which you want to put the file
	 */
	private String remoteFilePath;
	/**
	 * the name of the file you are putting
	 */
	private String fileName;
	/**
	 * the path (with name) of the file you are putting
	 */
	private String fileabsolutePathOnServer;

	private IClient sClient;

	// type of file
	private String mimeType;
	/**
	 * 
	 * @param sClient the instance of the storage client
	 * @param mimeType 
	 * @param fileToUpload the absolute path of the file
	 */
	public UploadToStorageThread(IClient sClient, String fileName, String fileabsolutePathOnServer, String remoteFilePath, String mimeType) {
		super();
		this.sClient = sClient;
		this.remoteFilePath = remoteFilePath;
		this.fileName = fileName;
		this.fileabsolutePathOnServer = fileabsolutePathOnServer;
		this.mimeType = mimeType;
	}

	@Override
	public void run() {
		String theID = sClient.put(true, mimeType).LFile(fileabsolutePathOnServer).RFile(remoteFilePath);
		_log.debug("Uploaded " + fileName + " - Returned Storage id=" + theID);
	}

}
