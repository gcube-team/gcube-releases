package org.gcube.datatransfer.agent.impl.handlers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamException;
import org.apache.commons.net.io.CopyStreamListener;
import org.apache.commons.net.io.Util;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.provider.DecryptSmpUrl;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.gcube.datatransfer.agent.impl.context.ServiceContext;
import org.gcube.datatransfer.agent.impl.jdo.TransferObject;
import org.gcube.datatransfer.common.outcome.TransferStatus;
import org.gcube.datatransfer.agent.impl.utils.TransferUtils;
import org.gcube.datatransfer.agent.stubs.datatransferagent.DestData;
import org.gcube.datatransfer.agent.stubs.datatransferagent.PostProcessType;
import org.gcube.datatransfer.agent.stubs.datatransferagent.TransferType;

public class LocalFileTransferAsyncHandler extends TransferHandler {

	public static int bufferSize = Util.DEFAULT_COPY_BUFFER_SIZE * 1000;
	private long bytesTransferredForCurrent;

	private ExecutorService pool;

	public LocalFileTransferAsyncHandler(String[] inputFiles, 
			String outPath, String transferId, TransferType type,
			DestData data, int startIndex, int endIndex) {
		this.inputFiles = inputFiles;
		this.timeout = data.getOutUri().getOptions().getTransferTimeout();
		this.outPath = outPath;
		this.transferId = transferId;
		this.transferType = type;
		this.destData = data;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}

	@Override
	public void run() {
		for (int i = startIndex; i <= endIndex; i++) {
			pool = Executors.newFixedThreadPool(1);
			long startTime = 0;
			TransferObject transferObj = null;

			try {

				transferObj = TransferUtils.createTransferObjectJDO(transferId,
						transferType);
				transferObj.setSourceURI(inputFiles[i]);

				FileObject inputFile = TransferUtils
						.prepareFileObject(inputFiles[i]);
				transferObj.setSize(inputFile.getContent().getSize());
				
				if(!checkAvailableSize(inputFile.getContent().getSize())){
					String msg = "There is no available space in the destination!!";
					logger.error(msg);
					throw new Exception(msg);
				}
				InputStream sourceFileIn = inputFile.getContent()
						.getInputStream();
				
				//getting outfile info	
				String outputFile;
				if(inputFile.getURL().toString().startsWith("smp")){			
					String str=inputFiles[i];
					String[] parts = str.split("\\?");
					if(sourceFileIn==null){
						String rpath=parts[0].replaceFirst("smp:/", "");
						logger.debug("rpath="+rpath);
						
						DecryptSmpUrl.decrypt(parts[1]);
						GCUBEScope scope = GCUBEScope.getScope(DecryptSmpUrl.scopeType);		
						//new client (there is need to set the scope)
						ScopeProvider.instance.set(scope.toString());
						IClient clientNew = new StorageClient(DecryptSmpUrl.serviceClass, DecryptSmpUrl.serviceName, DecryptSmpUrl.owner, AccessType.valueOf(DecryptSmpUrl.accessType.toUpperCase())).getClient();
						
						sourceFileIn=clientNew.get().RFileAsInputStream(rpath);
					}
					String[] partsOfMain=parts[0].split("/");
					outputFile = partsOfMain[partsOfMain.length-1];
				}
				else outputFile = inputFile.getName().getBaseName();
				
				if(outPath.endsWith("/"))outPath=outPath.substring(0, outPath.length()-1);
				
				String relativeOutputFile = outPath + File.separator
						+ outputFile;

				logger.debug("Relative Output file " + relativeOutputFile);

				FileObject absoluteOutputFile = ServiceContext.getContext()
						.getLocalFSManager().resolveFile(relativeOutputFile);

				FileObject absolutePath = ServiceContext.getContext()
						.getLocalFSManager().resolveFile(outPath);

				absolutePath.createFolder();

				if (absoluteOutputFile.exists()
						&& !destData.getOutUri().getOptions().isOverwrite()) {
					String msg= "the file represented by the URL "
							+ inputFile.getURL().toString()
							+ " cannot be copied cause a file with the same name already exists";
					logger.error(msg);
					throw new Exception(msg);
				}

				transferObj.setDestURI(absoluteOutputFile.getName().getPath());

				
				logger.debug("Copying file from URL " + inputFile.getURL()
						+ " to : " + absoluteOutputFile.getName().getPath());

				startTime = System.currentTimeMillis();
				// parameters
				boolean terminate  = false;
				
				OutputStream destinationFileOut = absoluteOutputFile
						.getContent().getOutputStream();

				bytesTransferredForCurrent=0;
				CopyStreamHandler handler = new CopyStreamHandler(sourceFileIn, destinationFileOut,
						inputFile.getContent().getSize(), listener);

				try {
					pool.execute(handler);
					pool.shutdown();
				} catch (Exception e) {
					pool.shutdownNow();
					e.printStackTrace();
					absoluteOutputFile.delete();
					throw new Exception("Error while executing the transfer");
				}

				// waiting for transfer to complete
				terminate = pool.awaitTermination(this.timeout,	TimeUnit.MILLISECONDS);
				sourceFileIn.close();
				destinationFileOut.close();
				
				if (terminate) {
					// check the postprocess Options
					PostProcessType[] postProcesses = destData.getOutUri()
							.getOptions().getPostProcess();

					if (postProcesses != null)
						for (PostProcessType process : postProcesses)
							TransferUtils.applyPostProcess(process,
									absoluteOutputFile, absolutePath, destData
									.getOutUri().getOptions()
									.getConversionType());

					transferObj.setStatus(TransferStatus.DONE.name());
					transferObj.setBytesOfObjTransferred(bytesTransferredForCurrent);
					transferObj.setOutcome("File succesfully copied to "
							+ absoluteOutputFile);
					logger.debug("File succesfully copied to "
							+ absoluteOutputFile);
					ServiceContext.getContext().getDbManager()
					.addTransferObjectCompleted(transferId);
				} else {
					String msg = "Transfer aborted because timeout has elapsed";
					logger.error(msg);
					absoluteOutputFile.delete();
					throw new Exception(msg);
				}

			}
			catch (Exception e) {
				e.printStackTrace();
				transferObj.setStatus(TransferStatus.FAILED.name());
				transferObj.setOutcome(e.toString());
				transferObj.setBytesOfObjTransferred((long) 0);
				errorHappened = true;
			} finally {
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

	public boolean checkAvailableSize(long size){
		File root = new File("/");
		long total_usable_size=root.getUsableSpace();
		logger.debug("Total available space in the destination is " + total_usable_size);
		if(total_usable_size<=size)return false;		
		return true;		
	}

	public static long copyLarge(InputStream input, OutputStream output)
			throws IOException {
		byte[] buffer = new byte[bufferSize];
		long count = 0;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

	public static void copyWithHttpClient(String inputURI, String outPutUri) throws Exception{
		HttpClient client = new HttpClient();
		GetMethod get = new GetMethod(inputURI);

		int status = client.executeMethod(get);

		InputStream is = get.getResponseBodyAsStream();

		FileOutputStream fos = new FileOutputStream(outPutUri);

		copyLarge(is, fos);
		fos.close();

	}
}


