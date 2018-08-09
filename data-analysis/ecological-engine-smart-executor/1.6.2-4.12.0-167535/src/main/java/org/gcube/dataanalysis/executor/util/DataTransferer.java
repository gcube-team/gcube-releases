package org.gcube.dataanalysis.executor.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.gcube.data.transfer.library.DataTransferClient;
import org.gcube.data.transfer.library.TransferResult;
import org.gcube.data.transfer.model.Destination;
import org.gcube.data.transfer.model.PluginInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DataTransferer {

	private static final Logger logger = LoggerFactory.getLogger(DataTransferer.class); 	

	public static TransferResult transferFileToService(String scope, String username, String host, int port, String fileAbsolutePath, String remoteFolder) throws Exception {
		return transferFileToService(scope, username, host, port, fileAbsolutePath, remoteFolder, null);
	}
	
	public static TransferResult transferFileToService(String scope, String username, String host, int port, String fileAbsolutePath, String remoteFolder, String destinationId) throws Exception {
		return transferFileToService(scope, username, host, port, fileAbsolutePath, remoteFolder, destinationId, false, null );
	}
	
	// returns the number of transferred bytes
	public static TransferResult transferFileToService(String scope, String username, String host, int port, String fileAbsolutePath, String remoteFolder, String destinationId, boolean decompress, String decompressedFilefolderDestination) throws Exception {
		logger.debug("Transferring file {} to {}:{} " , fileAbsolutePath, host, port );

		DataTransferClient client=DataTransferClient.getInstanceByEndpoint("http://"+host+":"+port);

		File localFile = new File(fileAbsolutePath);
		if (!localFile.exists())
			throw new Exception("Local file does not exist: " + localFile);

		//String localfolder = localFile.getParent();
		String file = localFile.getName();
		
		
		
		Destination dest=new Destination(file);
		dest.setSubFolder(remoteFolder);
		
		if(destinationId!=null)
			dest.setPersistenceId(destinationId);
		
		TransferResult transferResult = null;
		if(decompress){
			Map<String,String> params=new HashMap<>();
			params.put("DELETE_ARCHIVE", Boolean.TRUE.toString());
			if (decompressedFilefolderDestination!=null)
				params.put("DESTINATION", decompressedFilefolderDestination);
			transferResult = client.localFile(localFile,dest, new PluginInvocation("DECOMPRESS",params));
		} else
			transferResult = client.localFile(localFile,dest);
		
		logger.debug("Transferring...");
		
		return transferResult;
	}

}
