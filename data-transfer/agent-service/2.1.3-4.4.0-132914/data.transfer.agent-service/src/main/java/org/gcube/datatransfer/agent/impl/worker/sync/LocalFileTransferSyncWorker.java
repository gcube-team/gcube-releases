package org.gcube.datatransfer.agent.impl.worker.sync;

import gr.uoa.di.madgik.grs.writer.GRS2WriterException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.VFS;

import org.gcube.datatransfer.agent.impl.context.ServiceContext;
import org.gcube.datatransfer.agent.impl.grs.GRSOutComeWriter;
import org.gcube.datatransfer.agent.impl.utils.TransferUtils;
import org.gcube.datatransfer.agent.impl.worker.SyncWorker;
import org.gcube.datatransfer.agent.stubs.datatransferagent.DestData;
import org.gcube.datatransfer.agent.stubs.datatransferagent.PostProcessType;
import org.gcube.datatransfer.agent.stubs.datatransferagent.SourceData;

/**
 * 
 * @author andrea
 *
 */
public class LocalFileTransferSyncWorker extends SyncWorker{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	public LocalFileTransferSyncWorker(String tranferID,SourceData source, DestData dest) throws GRS2WriterException, FileSystemException {
		this.transferId = tranferID;
		this.sourceParameters = source;
		this.destParameters = dest;
		outcomeWriter = new GRSOutComeWriter(source.getInputURIs().length,false);
			
	}

	@Override
	public Object call() throws Exception {

		String [] urlInputs= sourceParameters.getInputURIs();
		String outPath = destParameters.getOutUri().getOutUris()[0];
		int timeout = (int)destParameters.getOutUri().getOptions().getTransferTimeout();
		
		InputStream streamIn = null;

		for (String urlString : urlInputs) {
			Exception exception = null;
			FileObject absoluteOutputFile  = null;
			
			long startTime = 0;
		    long endTime;
		
			try {
	
				FileObject file = VFS.getManager().resolveFile(urlString);
				URLConnection connection = file.getURL().openConnection();
				connection.setConnectTimeout(timeout);
				streamIn = 	connection.getInputStream();
				
				//getting outfile info	
				String outputFile = urlString.substring(urlString.lastIndexOf(File.separator)+1);
				
				String relativeOutputFile = outPath+File.separator+outputFile;
				
				absoluteOutputFile = ServiceContext.getContext().
						getLocalFSManager().resolveFile(relativeOutputFile);
				
				FileObject absolutePath =ServiceContext.getContext().
						getLocalFSManager().resolveFile(outPath);
				
				absolutePath.createFolder();
				
				if (absoluteOutputFile.exists() && !destParameters.getOutUri().getOptions().isOverwrite())
				{
					logger.error("the file represented by the URL " + urlString + " cannot be copied cause a file with the same name already exists");
					throw new Exception ("the file represented by the URL " + urlString + " cannot be copied cause a local file with the same name already exists");
				}
			
				OutputStream streamOut = null;
				try {
					streamOut = new FileOutputStream(absoluteOutputFile.getName().getPath());
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
					throw e1;
				}
				
				logger.debug("Copying file from URL "+ urlString  + "to : "+ absoluteOutputFile.getName().getPath()); 
				startTime= System.currentTimeMillis();
				
				IOUtils.copy(streamIn, streamOut);
				streamIn.close();
				streamOut.close();
				
				
				//check the postprocess Options
				PostProcessType [] postProcesses = destParameters.getOutUri().getOptions().getPostProcess();
				
				if (postProcesses != null)
					for (PostProcessType process : postProcesses)
						TransferUtils.applyPostProcess(process,absoluteOutputFile, absolutePath,destParameters.getOutUri().getOptions().getConversionType());		

				
				logger.debug("File succesfully copied to "+ absoluteOutputFile.getName().getPath());
			
			}
			
			catch (Exception e){
				e.printStackTrace();
				exception = e;
			}
			endTime = System.currentTimeMillis();
			long transferTime = endTime - startTime;
				
			if (exception == null)
				outcomeWriter.putField(urlString,absoluteOutputFile.getName().getPath(),transferTime,new Long(0),new Long(0));
			else 
				outcomeWriter.putField(urlString,absoluteOutputFile.getName().getPath(),transferTime,new Long(0),new Long(0),exception);
		}
		outcomeWriter.close();
		return true;
	}

	@Override
	public String getOutcomeLocator() throws GRS2WriterException {
		return outcomeWriter.writer.getLocator().toString();
	}

}
