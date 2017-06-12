package org.gcube.datatransfer.agent.impl.handlers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;
import org.apache.commons.net.io.Util;
import org.gcube.datatransfer.agent.impl.context.ServiceContext;
import org.gcube.datatransfer.agent.impl.jdo.TransferObject;
import org.gcube.datatransfer.agent.impl.utils.TransferUtils;
import org.gcube.datatransfer.common.outcome.TransferStatus;
import org.gcube.datatransfer.agent.stubs.datatransferagent.DestData;
import org.gcube.datatransfer.agent.stubs.datatransferagent.PostProcessType;
import org.gcube.datatransfer.agent.stubs.datatransferagent.TransferType;


/**
 * 
 * @author Andrea Manzi(CERN)
 *
 */
public class LocalFileTransferHandlerWithoutVFS extends TransferHandler{


	public static int bufferSize = Util.DEFAULT_COPY_BUFFER_SIZE*1000;

	public LocalFileTransferHandlerWithoutVFS (String[] inputFiles, 
			String outPath,String transferId, TransferType type, DestData data, int startIndex, int endIndex){
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

		for (int i =startIndex; i<=endIndex ; i++){
			long startTime = 0;
			TransferObject transferObj = null;

			try {

				transferObj =TransferUtils.createTransferObjectJDO(transferId,transferType);
				transferObj.setSourceURI(inputFiles[i]);

				//creating URL
				URL url = new URL (inputFiles[i]);

				URLConnection connection = url.openConnection();

				transferObj.setSize(new Long(0));

				//getting outfile info
				String outputFile;
				if(inputFiles[i].startsWith("smp")){
					String str=inputFiles[i];
					String[] parts = str.split("\\?");
					String[] partsOfMain=parts[0].split("/");
					outputFile = partsOfMain[partsOfMain.length-1];
				}
				else outputFile = inputFiles[i].substring(inputFiles[i].lastIndexOf("/")+1,inputFiles[i].length());

				if(outPath.endsWith("/"))outPath=outPath.substring(0, outPath.length()-1);
				String relativeOutputFile = outPath+File.separator+outputFile;

				logger.debug("Relative Output file "+relativeOutputFile);

				String absoluteOutputFile = ServiceContext.getContext().getVfsRoot()+File.separator+relativeOutputFile;


				String absolutePath =ServiceContext.getContext().getVfsRoot()+File.separator+outPath;


				new File(absolutePath).mkdirs();

				if (new File(absoluteOutputFile).exists() && !destData.getOutUri().getOptions().isOverwrite())
				{
					logger.error("the file represented by the URL " + inputFiles[i] + " cannot be copied cause a file with the same name already exists");
					throw new Exception ("the file represented by the URL " + inputFiles[i]+ " cannot be copied cause a local file with the same name already exists");
				}

				transferObj.setDestURI(absoluteOutputFile);

				logger.debug("Copying file from URL "+ inputFiles[i] + " to : " +absoluteOutputFile);

				startTime= System.currentTimeMillis();

				//absoluteOutputFile.copyFrom(inputFile, Selectors.SELECT_SELF);

				InputStream sourceFileIn = connection.getInputStream();


				try {
					OutputStream destinationFileOut = new FileOutputStream(new File(absoluteOutputFile));
					try {
						Util.copyStream(sourceFileIn, destinationFileOut, bufferSize, connection.getContentLength(), listener);
					} finally {
						destinationFileOut.close();
					}
				} finally {
					sourceFileIn.close();
				}

				//check the postprocess Options
				PostProcessType [] postProcesses = destData.getOutUri().getOptions().getPostProcess();

				if (postProcesses != null)
					for (PostProcessType process : postProcesses)
						TransferUtils.applyPostProcess(process,absoluteOutputFile, absolutePath,destData.getOutUri().getOptions().getConversionType());		

				transferObj.setStatus(TransferStatus.DONE.name());
				transferObj.setOutcome("File succesfully copied to "+ absoluteOutputFile);
				logger.debug("File succesfully copied to "+ absoluteOutputFile);
				ServiceContext.getContext().getDbManager().addTransferObjectCompleted(transferId);	
			}

			catch (Exception e){
				e.printStackTrace();
				transferObj.setStatus(TransferStatus.FAILED.name());
				transferObj.setOutcome(e.toString());
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
				ServiceContext.getContext().getDbManager().updateTransferObjectInfo(transferId, arg1);
			} catch (Exception e) {
				logger.error("Error updating DB");
			}

		}

		@Override
		public void bytesTransferred(CopyStreamEvent arg0) {
		}
	};


}
