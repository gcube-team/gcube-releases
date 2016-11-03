package org.gcube.datatransfer.agent.impl.handlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;
import org.apache.commons.net.io.Util;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.provider.DecryptSmpUrl;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.blobstorage.resource.StorageObject;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.gcube.datatransfer.agent.impl.context.ServiceContext;
import org.gcube.datatransfer.agent.impl.jdo.TransferObject;
import org.gcube.datatransfer.agent.impl.utils.TransferUtils;
import org.gcube.datatransfer.common.outcome.TransferStatus;
import org.gcube.datatransfer.agent.stubs.datatransferagent.DestData;
import org.gcube.datatransfer.agent.stubs.datatransferagent.TransferType;

/**
 * 
 * 
 * @author Andrea Manzi(CERN)
 *
 */
public class StorageManagerAsyncHandler extends TransferHandler{

	IClient client = null;
	public static int bufferSize = Util.DEFAULT_COPY_BUFFER_SIZE * 1000;
	private long bytesTransferredForCurrent;

	private ExecutorService pool;

	public StorageManagerAsyncHandler (String [] inputFiles, 
			String outPath,String transferId, TransferType type,IClient client, DestData data, int startIndex, int endIndex){
		this.inputFiles = inputFiles;
		this.timeout = data.getOutUri().getOptions().getTransferTimeout();;
		this.outPath = outPath;
		this.transferId = transferId;
		this.transferType = type;
		this.destData = data;
		this.client = client;
		this.startIndex = startIndex;
		this.endIndex = endIndex;

	}


	@Override
	public void run() {

		for (int i =startIndex; i<=endIndex ; i++){
			pool = Executors.newFixedThreadPool(1);
			long startTime = 0;
			TransferObject transferObj = null;
			InputStream streamIn = null;
			OutputStream streamOut = null;
			
			try {
				transferObj =TransferUtils.createTransferObjectJDO(transferId,transferType);
				transferObj.setSourceURI(inputFiles[i]);


				FileObject inputFile = TransferUtils.prepareFileObject(inputFiles[i]);
								
				logger.debug("inputFile.getURL()= "+ inputFile.getURL());		
				URLConnection connection = inputFile.getURL().openConnection();
				connection.setConnectTimeout((int)timeout);
				
				streamIn = connection.getInputStream();
				
				//getting outfile info	
				String outputFile;
				if(inputFile.getURL().toString().startsWith("smp")){			
					String str=inputFiles[i];
					String[] parts = str.split("\\?");
					if(streamIn==null){
						String rpath=parts[0].replaceFirst("smp:/", "");
						logger.debug("rpath="+rpath);
						
						DecryptSmpUrl.decrypt(parts[1]);
						GCUBEScope scope = GCUBEScope.getScope(DecryptSmpUrl.scopeType);		
						//new client (there is need to set the scope)
						ScopeProvider.instance.set(scope.toString());
						IClient clientNew = new StorageClient(DecryptSmpUrl.serviceClass, DecryptSmpUrl.serviceName, DecryptSmpUrl.owner, AccessType.valueOf(DecryptSmpUrl.accessType.toUpperCase())).getClient();
						
						streamIn=clientNew.get().RFileAsInputStream(rpath);
					}
					String[] partsOfMain=parts[0].split("/");
					outputFile = partsOfMain[partsOfMain.length-1];
				}
				else outputFile = inputFile.getName().getBaseName();
				
				outputFile =outputFile.replaceAll(" ","%20");
				if(outPath.endsWith("/"))outPath=outPath.substring(0, outPath.length()-1);
				
				String absoluteOutputFile = outPath+File.separator+outputFile;

				//get the outputstream 
				streamOut= client.put(true).RFileAsOutputStream(absoluteOutputFile);	
				String outURL = client.getUrl().RFile(absoluteOutputFile);
				transferObj.setDestURI(outURL);
				
				if(streamIn==null)logger.debug("streamIn=null");
				if(streamOut==null)logger.debug("streamOut=null");
				
				logger.debug("Copy file from URL "+ inputFile.getURL() + " to : " +outURL);

				startTime= System.currentTimeMillis();
				/*if (outputFile.exists() && !destData.getOutUri().getOptions().isOverwrite())
				{
					logger.error("the file cannot be copied cause a file with the same name already exists");
					throw new Exception ("the file cannot be copied cause a  file with the same name already exists");
				}*/

				// parameters
				boolean terminate  = false;

				OutputStream destinationFileOut = streamOut;
				InputStream sourceFileIn=streamIn;

				bytesTransferredForCurrent=0;
				CopyStreamHandler handler = new CopyStreamHandler(sourceFileIn, destinationFileOut,
						inputFile.getContent().getSize(),listener);

				try {
					pool.execute(handler);
					pool.shutdown();
				} catch (Exception e) {
					pool.shutdownNow();
					e.printStackTrace();
					throw new Exception("Error while executing the transfer");
				}

				// waiting for transfer to complete
				terminate = pool.awaitTermination(this.timeout,	TimeUnit.MILLISECONDS);
				
				sourceFileIn.close();
				destinationFileOut.close();
				
				if(terminate){
					logger.debug("File succesfully copied to "+ outURL);
					transferObj.setSize(inputFile.getContent().getSize());
					transferObj.setBytesOfObjTransferred(bytesTransferredForCurrent);
					transferObj.setStatus(TransferStatus.DONE.name());
					transferObj.setOutcome("File succesfully copied to "+ outURL);
				}else {
					String msg = "Transfer aborted because timeout has elapsed";
					logger.error(msg);
					throw new Exception(msg);
				}				
				
			}catch (Exception e){
				transferObj.setStatus(TransferStatus.FAILED.name());
				transferObj.setOutcome(e.toString());
				e.printStackTrace();
				errorHappened = true;
			}
			finally {
				long endTime = System.currentTimeMillis();
				transferObj.setTransferTime(endTime - startTime);
				//tmpFile.delete();
				transferObjs.add(transferObj);
			}
		}
	}
	CopyStreamListener listener = new CopyStreamListener() {
		@Override
		public void bytesTransferred(long arg0, int arg1, long arg2) {
			try {
				//for the transfer
				ServiceContext.getContext().getDbManager()
				.updateTransferObjectInfo(transferId, arg1);
				//only for the current object
				bytesTransferredForCurrent=bytesTransferredForCurrent+arg1;
			} catch (Exception e) {
				logger.error("Error updating DB");
			}
		}
		@Override
		public void bytesTransferred(CopyStreamEvent arg0) {
		}
	};
}


