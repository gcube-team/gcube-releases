package org.gcube.common.geoserverinterface.test;


import static org.gcube.datatransfer.agent.library.proxies.Proxies.transferAgent;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.gcube.common.geoserverinterface.GeoCaller;
import org.gcube.common.geoserverinterface.GeonetworkCommonResourceInterface.GeonetworkCategory;
import org.gcube.common.geoserverinterface.GeonetworkCommonResourceInterface.GeoserverMethodResearch;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datatransfer.agent.library.AgentLibrary;
import org.gcube.datatransfer.agent.library.exceptions.ConfigurationException;
import org.gcube.datatransfer.agent.library.exceptions.GetTransferOutcomesException;
import org.gcube.datatransfer.agent.library.exceptions.TransferException;
import org.gcube.datatransfer.common.agent.Types.MonitorTransferReportMessage;
import org.gcube.datatransfer.common.agent.Types.storageType;
import org.gcube.datatransfer.common.options.TransferOptions;
import org.gcube.datatransfer.common.outcome.FileTransferOutcome;
import org.gcube.datatransfer.common.outcome.TransferStatus;

public class TestGeoTiff {



	private static final String TRANSFER_STATE_DONE = "DONE";
	private static final Object GEOTIFF_TYPE = "GeoTIFF";
	static String geonetworkUrl = "http://geoserver-dev.d4science-ii.research-infrastructures.eu/geonetwork";
	static String geonetworkUsername = "admin";
	static String geonetworkPassword = "admin";

	static String geoserverUrl = "http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver";
	static String geoserverUsername = "admin";
	static String geoserverPassword = "gcube@geo2010";

	public static void main(String[] args) {
		
		addErdasImgTest();

//		addGeoTiffTest();
		
//		addGeoTiffTest2();

//		dataTransferTest();

	}


	/**
	 * 
	 */
	private static void addErdasImgTest() {
		try {
			GeoCaller geoCaller = new GeoCaller(geonetworkUrl, geonetworkUsername, geonetworkPassword, geoserverUrl, geoserverUsername, geoserverPassword, GeoserverMethodResearch.MOSTUNLOAD);
			String workspace = "aquamaps";
			String fileTiffUrl = "http://dedalo.i3m.upv.es/enm2-results/684d9c8d-05f0-4c4a-95f6-12b57fdb0578/results/p_edulis_map.img";
			String description = "p_edulis_img";
			String scope = "/gcube/devsec/";
			String layerName = "p_edulis_img";
			String layerTitle = "p_edulis_img title";
			String abstr = "p_edulis_img abstr";
			geoCaller.addGeoLayer(fileTiffUrl, layerName, layerTitle, workspace, GeonetworkCategory.DATASETS, description, abstr, scope);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * 
	 */
	private static void addGeoTiffTest() {
		try {
			GeoCaller geoCaller = new GeoCaller(geonetworkUrl, geonetworkUsername, geonetworkPassword, geoserverUrl, geoserverUsername, geoserverPassword, GeoserverMethodResearch.MOSTUNLOAD);
			String workspace = "aquamaps";
			String geoTiffUrl = "https://www.dropbox.com/s/ec68ssrkbm759ba/albers27.tif";
//			String geoTiffUrl = "https://dl.dropbox.com/u/24368142/cea.tif";
//			String geoTiffUrl = "https://dl.dropbox.com/u/12809149/p_edulis_map.tiff";
			String description = "albers test geotiff 0";
			String scope = "/gcube/devsec/";
			String layerName = "albers";
			String layerTitle = "albers title";
			String abstr = "albers abstr";
			geoCaller.addGeoTiff(geoTiffUrl, layerName, layerTitle, workspace, GeonetworkCategory.DATASETS, description, abstr, scope);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * 
	 */
	private static void addGeoTiffTest2() {
		try {
			GeoCaller geoCaller = new GeoCaller(geonetworkUrl, geonetworkUsername, geonetworkPassword, geoserverUrl, geoserverUsername, geoserverPassword, GeoserverMethodResearch.MOSTUNLOAD);
			boolean b = geoCaller.addPreExistentGeoTiff("p_edulis_map.tiff", "newEdulis1", "newEdulis1", "aquamaps", GeonetworkCategory.DATASETS, "descr", "");
			System.out.println("b="+b);
//			geoCaller.addCoverageStore("myGeoTiff.tif", "myGeoTiffLayerName", "myGeoTiffLayer Title", "aquamaps", "descr", "abstr", "/gcube/devsec/");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	private static void dataTransferTest() {
		try {
			System.out.println("STARTED...");
			
			ScopeProvider.instance.set("/gcube/devsec/");

			AgentLibrary library = transferAgent().at("geoserver-dev.d4science-ii.research-infrastructures.eu", 9000).build();

			ArrayList<URI> inputs = new ArrayList<URI>();
			inputs.add(new URI("http://img821.imageshack.us/img821/6658/gisviewerdiagram.png"));
			inputs.add(new URI("http://img11.imageshack.us/img11/9008/geoexplorerdiagram.png"));
			inputs.add(new URI("https://www.dropbox.com/s/ec68ssrkbm759ba/albers27.tif"));

			String outPath = "./";

			TransferOptions options = new TransferOptions();
			options.setOverwriteFile(true);
			options.setType(storageType.LocalGHN);
			options.setUnzipFile(false);

			String transferId = library.startTransfer(inputs, outPath, options);
			//ArrayList<FileTransferOutcome> outcomes = library.startTransferSync(input, outPath, options);
			System.out.println("Transfer started "+transferId);
			
			MonitorTransferReportMessage message = null;
			TransferStatus ts = null;
			
			do {
				try {
					message = library.monitorTransferWithProgress(transferId);
					ts = TransferStatus.valueOf(message.getTransferStatus());
					System.out.println("Status: "+message.getTransferStatus()
							+"\tTotBytes: "+message.getTotalBytes()
							+"\tTransferedBytes: "+message.getBytesTransferred()
							+"\tTotalTransfers: "+message.getTotalTransfers()
							+"\tTransfersCompleted: "+message.getTransferCompleted());
				} catch (Exception e) {
					e.printStackTrace();
				}
				Thread.sleep(500);
			} while (!ts.hasCompleted());
			
//			String transferState="";
//			while (!transferState.contentEquals(TRANSFER_STATE_DONE)) {
//				try {
//					
//					transferState = library.monitorTransfer(transferId);
//					System.out.print(".");
//					
//				} catch (MonitorTransferException e) {
//					e.printStackTrace();
//				}
//				Thread.sleep(500);
//			}
			System.out.println("done!");
			ArrayList<FileTransferOutcome> outcomes = library.getTransferOutcomes(transferId, FileTransferOutcome.class);

			for (FileTransferOutcome outcome : outcomes)
				System.out.println("file: "+outcome.getDest()+"; "+ (outcome.isSuccess() ? "SUCCESS" : "FAILURE"));
			
			
		} catch (TransferException e) {
			System.out.println("TRANSFER EXCEPTION");
			e.printStackTrace();
		} catch (ConfigurationException e) {
			System.out.println("CONFIGURATION EXCEPTION");
			e.printStackTrace();
		} catch (URISyntaxException e) {
			System.out.println("URI SYNTAX EXCEPTION");
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (GetTransferOutcomesException e) {
			e.printStackTrace();
		}
	}

}
