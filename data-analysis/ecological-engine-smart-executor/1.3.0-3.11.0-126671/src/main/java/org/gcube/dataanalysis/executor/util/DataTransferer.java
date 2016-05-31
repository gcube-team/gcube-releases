package org.gcube.dataanalysis.executor.util;

import static org.gcube.datatransfer.agent.library.proxies.Proxies.transferAgent;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.MemoryType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.datatransfer.agent.library.AgentLibrary;
import org.gcube.datatransfer.agent.library.exceptions.MonitorTransferException;
import org.gcube.datatransfer.common.agent.Types.storageType;
import org.gcube.datatransfer.common.options.TransferOptions;
import org.gcube.datatransfer.common.outcome.FileTransferOutcome;
import org.gcube.datatransfer.common.outcome.TransferStatus;

public class DataTransferer {

	public static void main(String[] args) throws Exception {
		//String scope = "/d4science.research-infrastructures.eu/gCubeApps";
		
		String scope = "/gcube/devsec/devVRE";
		ScopeProvider.instance.set(scope);
		//String transferGHN = "dewn04.madgik.di.uoa.gr";
		String transferGHN = "access.d4science.org";
		int transferPort = 8080;
		AgentLibrary library = transferAgent().at(transferGHN, transferPort).build();

		ArrayList<URI> input = new ArrayList<URI>();
		/*
		 * File localfile = new File("C:/Users/coro/Dropbox/Public/wind1.tif"); String file = "wind1.tif"; String localfolder = "C:/Users/coro/Dropbox/Public/"; String storagesmpurl = StorageUtils.uploadFilesOnStorage("/gcube/devsec", "gianpaolo.coro",localfolder,file);
		 * 
		 * System.out.println("URI from storage: "+storagesmpurl);
		 * 
		 * String urlStorage = "http://dev.d4science.org/uri-resolver/smp?smp-uri="+storagesmpurl+"&fileName="+file;
		 */
		// String urlStorage = "http://dev.d4science.org/smp?smp-uri=smp://data.gcube.org/gzAv/RparhTHO4yhbF9ItALcRlSJRIiBGmbP5+HKCzc=&fileName=wind1.tif";
		//String urlStorage = "http://data.gcube.org/gzAv/RparhTHO4yhbF9ItALcRlSJRIiBGmbP5+HKCzc=";
		String urlStorage ="http://goo.gl/Vq8QVY";

		System.out.println("URL for storage: " + urlStorage);

		URI uri = new URI("http://dl.dropboxusercontent.com/u/12809149/wind1.tif");
		// URI uri = new URI(urlStorage);

		// http://dev.d4science.org/uri-resolver/smp?smp-uri=smp://data.gcube.org/gzAv/RparhTHO4yhbF9ItALcRlSJRIiBGmbP5+HKCzc=&fileName=wind1.tif&contentType=tiff

		// URI uri = new URI(storageurl); //localfile.toURI();
		// URI uri = new URI("file:///C:Users/coro/Dropbox/Public/wind1.tif");
		input.add(uri);

		//String outPath = "/tmp";
		String outPath = "/var/www/html/test/";
		String fileToTransfer = "C:\\Users\\coro\\Dropbox\\Public\\3_Aquamaps.jpg";
		//fileToTransfer = "C:\\Users\\coro\\Dropbox\\Public\\3_Aquamaps.jpg";
//		fileToTransfer = "C:/Users/coro/Desktop/DATABASE e NOTE/Experiments/WEB_APP_PUBLISHER/gcube/images/Resource Model.png";
		transferFileToService(scope, "gianpaolo.coro", transferGHN, transferPort,fileToTransfer , outPath);
		
		
	}

	// returns the number of transferred bytes
	public static boolean transferFileToService(String scope, String username, String service, int port, String fileAbsolutePath, String remoteFolder) throws Exception {
		AnalysisLogger.getLogger().debug("Transferring file " + fileAbsolutePath + " to " + service + ":" + port);
		ScopeProvider.instance.set(scope);

		AgentLibrary library = transferAgent().at(service, port).build();
		ArrayList<URI> input = new ArrayList<URI>();
		File localFile = new File(fileAbsolutePath);
		if (!localFile.exists())
			throw new Exception("Local file does not exist: " + localFile);

		String localfolder = localFile.getParent();
		String file = localFile.getName();
		AnalysisLogger.getLogger().debug("Uploading file " + file + " onto storage");
		ScopeProvider.instance.set(scope);
		AnalysisLogger.getLogger().info("Loading file on scope: " + scope);
		
		String storagesmpurl = StorageUtils.uploadFilesOnStorage(scope, username, localfolder, "/",file,true);
		//urls for testing
		//storagesmpurl = "http://dev.d4science.org/smp?smp-uri="+storagesmpurl+"&fileName="+file;
		//	String storagesmpurl = "smp://data.gcube.org/sHtVhK4clGtbcWCliQud+5b4PfGx5BW+GmbP5+HKCzc=";
		//	String storagesmpurl = "http://goo.gl/r6ggMA";
		//	String storagesmpurl = "http://dl.dropboxusercontent.com/u/12809149/3_Aquamaps.jpg";
		
		AnalysisLogger.getLogger().debug("SMP url generated: " + storagesmpurl);
		URI uri = new URI(storagesmpurl);
		input.add(uri);

		TransferOptions options = new TransferOptions();

		options = new TransferOptions();
		options.setOverwriteFile(false);
		options.setType(storageType.DataStorage);
		options.setUnzipFile(false);
		options.setTransferTimeout(3, TimeUnit.HOURS);
		AnalysisLogger.getLogger().debug("Transferring...");

		//old code for sync transfer
//		ArrayList<FileTransferOutcome> outcomes = library.startTransferSync(input, remoteFolder, options);
		
		
		ArrayList<URI> outputURI = new ArrayList<URI>();
//		outputURI.add(new URI("file://"+remoteFolder.replace(" ", "_")+file.replace(" ", "_")));
		outputURI.add(new URI("file://"+remoteFolder.replace(" ", "%20")+file.replace(" ", "%20")));
		
		AnalysisLogger.getLogger().debug("Remote file name will be: " + outputURI.get(0));
		
		String transferId = library.startTransfer(input, outputURI, options);

		TransferStatus transferStatus = null;

		do {
			try {

				Thread.sleep(1000);
				transferStatus = TransferStatus.valueOf(library.monitorTransfer(transferId));

			} catch (MonitorTransferException e) {
				e.printStackTrace();
			}

		} while (!transferStatus.hasCompleted());

		ArrayList<FileTransferOutcome> outcomes = library.getTransferOutcomes(transferId, FileTransferOutcome.class);

		AnalysisLogger.getLogger().debug("Transferring complete");
		boolean success = false;
		String outcomeString = "";
		for (FileTransferOutcome outcome : outcomes) {
			AnalysisLogger.getLogger().debug("Outcome " + outcome);
			outcomeString = outcome.toString();
			AnalysisLogger.getLogger().debug("Transferred file name " + outcome.fileName());
			AnalysisLogger.getLogger().debug("Transferring success " + outcome.isSuccess());
			AnalysisLogger.getLogger().debug("Transferred bytes " + outcome.getTotal_size());
			AnalysisLogger.getLogger().debug("Transfer time " + outcome.getTransferTime());
			success = outcome.isSuccess();
		}

		if (!success)
			throw new Exception("No Bytes were transferred to the Thredds server: "+outcomeString);

		return success;
	}

}
