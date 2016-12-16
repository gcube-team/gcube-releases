package org.gcube.datatransfer.agent.impl.handlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.provider.DecryptSmpUrl;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.blobstorage.resource.StorageObject;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
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
public class StorageManagerAsyncHandlerOld extends TransferHandler{

	IClient client = null;


	public StorageManagerAsyncHandlerOld (String [] inputFiles, 
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
			long startTime = 0;
			File tmpFile = null;
			TransferObject transferObj = null;
			InputStream streamIn = null;
			FileOutputStream streamOut = null;
			
			try {
				transferObj =TransferUtils.createTransferObjectJDO(transferId,transferType);
				transferObj.setSourceURI(inputFiles[i]);


				FileObject inputFile = TransferUtils.prepareFileObject(inputFiles[i]);
								
				logger.debug("inputFile.getURL()= "+ inputFile.getURL());		
				URLConnection connection = inputFile.getURL().openConnection();
				connection.setConnectTimeout((int)timeout);
				
				streamIn = connection.getInputStream();
				
				try {
					tmpFile = new File("/tmp/data-transfer-tmp");
					//tmpFile = File.createFile("data-transfer");
				} catch (Exception e1) {
					e1.printStackTrace();
					throw e1;
				}

				streamOut = null;
				try {
					streamOut = new FileOutputStream(tmpFile);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}

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
				
				if(outPath.endsWith("/"))outPath=outPath.substring(0, outPath.length()-1);
				String absoluteOutputFile = outPath+File.separator+outputFile;


				startTime= System.currentTimeMillis();

				if(streamIn==null){
				logger.debug("streamIn=null");
				}
				if(streamOut==null){
					logger.debug("streamOut=null");
				}
				
				IOUtils.copy(streamIn, streamOut);
						
				client.put(true).LFile(tmpFile.getAbsolutePath()).RFile(absoluteOutputFile);	
				String outURL = client.getUrl().RFile(absoluteOutputFile);
				logger.debug("localFile size="+tmpFile.length());
				//logger.debug("absoluteOutputFile="+absoluteOutputFile);
				
				transferObj.setDestURI(outURL);
				transferObj.setSize(tmpFile.getTotalSpace());
				transferObj.setStatus(TransferStatus.DONE.name());
				
				transferObj.setOutcome("File succesfully copied to "+ outURL);

				logger.debug("File succesfully copied to "+ outURL);
				
				streamIn.close();
				streamOut.close();
				
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
}


