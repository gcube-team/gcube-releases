package org.gcube.datatransfer.agent.impl.handlers;


import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.gcube.datatransfer.agent.impl.jdo.TransferObject;
import org.gcube.datatransfer.common.outcome.TransferStatus;
import org.gcube.datatransfer.agent.impl.utils.TransferUtils;
import org.gcube.datatransfer.agent.stubs.datatransferagent.DestData;
import org.gcube.datatransfer.agent.stubs.datatransferagent.TransferType;


/**
 * 
 * @author Andrea Manzi(CERN)
 *
 */
public class DataStorageTransferAsyncHandlerOld  extends TransferHandler{


	StandardFileSystemManager fsManager = new StandardFileSystemManager();


	protected String [] outputFiles = null;


	public DataStorageTransferAsyncHandlerOld (String[] inputFiles,
			String[] outputFiles,String transferId, TransferType type, DestData data, int startIndex, int endIndex) throws FileSystemException{
		this.inputFiles = inputFiles;
		this.timeout = data.getOutUri().getOptions().getTransferTimeout();
		this.outputFiles = outputFiles;
		this.transferId = transferId;
		this.transferType = type;
		this.destData = data;
		this.startIndex = startIndex;
		this.endIndex = endIndex;

		fsManager.init();

	}

	@Override
	public void run() {

		for (int i =startIndex; i<=endIndex ; i++){
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
				
				outputFile.copyFrom(inputFile, Selectors.SELECT_SELF);

				logger.debug("File succesfully copied to "+ outputFile.getURL().toURI());
				transferObj.setSize(inputFile.getContent().getSize());
				transferObj.setStatus(TransferStatus.DONE.name());
				transferObj.setOutcome("File succesfully copied to "+ outputFile.getURL().toURI());

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

}

