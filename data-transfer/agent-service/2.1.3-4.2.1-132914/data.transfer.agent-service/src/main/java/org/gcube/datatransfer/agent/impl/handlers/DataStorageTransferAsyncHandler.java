package org.gcube.datatransfer.agent.impl.handlers;


import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;
import org.apache.commons.net.io.Util;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.gcube.datatransfer.agent.impl.context.ServiceContext;
import org.gcube.datatransfer.agent.impl.jdo.TransferObject;
import org.gcube.datatransfer.common.outcome.TransferStatus;
import org.gcube.datatransfer.agent.impl.utils.TransferUtils;
import org.gcube.datatransfer.agent.stubs.datatransferagent.DestData;
import org.gcube.datatransfer.agent.stubs.datatransferagent.TransferType;


public class DataStorageTransferAsyncHandler  extends TransferHandler{


	public static int bufferSize = Util.DEFAULT_COPY_BUFFER_SIZE * 1000;
	private ExecutorService pool;

	protected String [] outputFiles = null;
	private long bytesTransferredForCurrent;

	public DataStorageTransferAsyncHandler (String[] inputFiles,
			String[] outputFiles,String transferId, TransferType type, DestData data, int startIndex, int endIndex) throws FileSystemException{
		this.inputFiles = inputFiles;
		this.timeout = data.getOutUri().getOptions().getTransferTimeout();
		this.outputFiles = outputFiles;
		this.transferId = transferId;
		this.transferType = type;
		this.destData = data;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}

	@Override
	public void run() {

		for (int i =startIndex; i<=endIndex ; i++){
			pool = Executors.newFixedThreadPool(1);
			long startTime = 0;
			TransferObject transferObj = null;

			try {
				transferObj =TransferUtils.createTransferObjectJDO(transferId,transferType);
				transferObj.setSourceURI(inputFiles[i]);
				transferObj.setDestURI(outputFiles[i]);

				FileObject inputFile = TransferUtils.prepareFileObject(inputFiles[i]);

				FileObject outputFile  = TransferUtils.prepareFileObject(outputFiles[i]);

				logger.debug("Copy file from URL "+ inputFile.getURL() + " to : " +outputFile.getURL());

				if (outputFile.exists() && !destData.getOutUri().getOptions().isOverwrite())
				{
					logger.error("the file cannot be copied cause a file with the same name already exists");
					throw new Exception ("the file cannot be copied cause a  file with the same name already exists");
				}

				startTime= System.currentTimeMillis();

				// parameters
				boolean terminate  = false;

				OutputStream destinationFileOut = outputFile.getContent().getOutputStream();
				InputStream sourceFileIn=inputFile.getContent().getInputStream();

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
//					logger.debug("File succesfully copied to "+ outputFile.getURL().toURI());
					logger.debug("File succesfully copied to "+ fixFileURL(outputFile.getURL()));
					transferObj.setSize(inputFile.getContent().getSize());
					transferObj.setBytesOfObjTransferred(bytesTransferredForCurrent);
					transferObj.setStatus(TransferStatus.DONE.name());
//					transferObj.setOutcome("File succesfully copied to "+ outputFile.getURL().toURI());
					transferObj.setOutcome("File succesfully copied to "+ fixFileURL(outputFile.getURL()));
				}else {
					String msg = "Transfer aborted because timeout has elapsed";
					logger.error(msg);
					throw new Exception(msg);
				}

			}catch (Exception e){
				e.printStackTrace();
				transferObj.setStatus(TransferStatus.FAILED.name());
				transferObj.setOutcome(e.toString());
				transferObj.setBytesOfObjTransferred((long) 0);
				errorHappened = true;
			}
			finally {
				long endTime = System.currentTimeMillis();
				transferObj.setTransferTime(endTime - startTime);
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
	
	public static URI fixFileURL(URL u) {
	    if (!"file".equals(u.getProtocol())) throw new IllegalArgumentException();
	    return new File(u.getFile()).toURI();
	}

}

