package org.gcube.datatransfer.agent.impl.worker.sync;

import gr.uoa.di.madgik.grs.writer.GRS2WriterException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.VFS;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.gcube.datatransfer.agent.impl.grs.GRSOutComeWriter;
import org.gcube.datatransfer.agent.impl.worker.SyncWorker;
import org.gcube.datatransfer.agent.impl.worker.async.StorageManagerASyncWorker;
import org.gcube.datatransfer.agent.stubs.datatransferagent.DestData;
import org.gcube.datatransfer.agent.stubs.datatransferagent.SourceData;
import org.gcube.datatransfer.agent.stubs.datatransferagent.StorageManagerDetails;

/**
 * 
 * @author andrea
 *
 */
public class StorageManagerSyncWorker extends SyncWorker{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private IClient client;
	

	public StorageManagerSyncWorker(String id,SourceData source, DestData dest) throws Exception {
		this.sourceParameters = source;
		this.destParameters = dest;
		this.transferId = id;
		

		StorageManagerDetails details = dest.getOutUri().getOptions().getStorageManagerDetails();

		if (details == null)
			throw new Exception("Missing storage Manager configuration details");
		//to add parameter to dest Data
		client = new StorageClient(
				details.getServiceClass(), 
				details.getServiceName(),
				details.getOwner(),
				AccessType.valueOf(details.getAccessType().getValue()),
				source.getScope()).getClient();
		outcomeWriter = new GRSOutComeWriter(source.getInputURIs().length,false);
	}

	@Override
	public Object call() throws Exception {

		String [] urlInputs= sourceParameters.getInputURIs();
		String outPath = destParameters.getOutUri().getOutUris()[0];
		int timeout = (int)destParameters.getOutUri().getOptions().getTransferTimeout();
		
		
		String outURL = null;

		File tmpFile;
		try {
			tmpFile = File.createTempFile("data-transfer", ".tmp");
		} catch (IOException e1) {
			e1.printStackTrace();
			return e1;
		}
		FileOutputStream streamOut = null;
		InputStream streamIn = null;
		

		for (String urlString : urlInputs) {
			
			Exception exception = null;
			
			long startTime = 0;
		    long endTime =0;

			try {
		
				FileObject file = VFS.getManager().resolveFile(urlString);
				URLConnection connection = file.getURL().openConnection();
				connection.setConnectTimeout(timeout);
				streamIn = 	connection.getInputStream();
				
			
				try {
					streamOut = new FileOutputStream(tmpFile);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
					return e1;
				}			
				
				String outputFile = urlString.substring(urlString.lastIndexOf(File.separator)+1);
				String absoluteOutputFile = outPath+File.separator+outputFile;

				startTime= System.currentTimeMillis();
				
				IOUtils.copy(streamIn, streamOut);
				streamIn.close();
				streamOut.close();
			
				client.put(true).LFile(tmpFile.getAbsolutePath()).RFile(absoluteOutputFile);	
				outURL = client.getUrl().RFile(absoluteOutputFile);
			}
			catch (Exception e){
				e.printStackTrace();
				exception = e;
			}
			endTime = System.currentTimeMillis();
			long transferTime = endTime - startTime;
			
			if (exception == null)
				outcomeWriter.putField(urlString,outURL,transferTime,new Long(0),new Long(0));
			else 
				outcomeWriter.putField(urlString,outURL,transferTime,new Long(0),new Long(0),exception);
		}
		tmpFile.delete();
		outcomeWriter.close();
		return true;
	}


	@Override
	public String getOutcomeLocator() throws GRS2WriterException {
		return outcomeWriter.writer.getLocator().toString();
	}

}
