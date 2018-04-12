package org.gcube.common.geoserverinterface.test;

import java.util.ArrayList;
import java.util.UUID;

import org.gcube.common.geoserverinterface.GeonetworkCaller;
import org.gcube.common.geoserverinterface.GeonetworkCommonResourceInterface.GeonetworkCategory;
import org.gcube.common.geoserverinterface.bean.CswRecord;
import org.gcube.common.geoserverinterface.bean.MetadataInfo;


public class TestGeonetwork {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String geonetworkUrl = "http://localhost:8080/geonetwork";
		String geonetworkUsername = "admin";
		String geonetworkPassword = "admin";
		String geoserverUsername = "admin";
		String geoserverPassword = "gcube@geo2010";

		GeonetworkCaller geonetwork = new GeonetworkCaller(geonetworkUrl, geonetworkUsername, geonetworkPassword, geoserverUsername, geoserverPassword);

		try {
			System.out.println(geonetwork.getHarvestings()); // Ritorna la lista di Harvesting in un file XML
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 722 -> Geoserver Localhost
		// 781 -> Geoserver node25.d
		// 937 -> Localhost WFS
		// 1031 -> Node25.d WFS

		/**
		 * updateHarvesting by id TEST
		 */
		// try {
		// System.out.println(geonetwork.updateHarvesting("1031"));
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } //Update Harvesting by Id

		// try {
		// System.out.println("Sleep... 7 sec");
		// Thread.sleep(7000);
		// System.out.println("Alive...");
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }

		System.out.println("\n\n");

		// Localhost insert Group example
//		String geoServerWmsUrl = "http://localhost:8080/geoserver/wms";
//		String geoserverLayerTitle = "tasmania_Group";
//		String geoserverLayerName = null;
//		String groupDescription = "descrizione gruppo";
//		UUID fd = UUID.randomUUID();
//		String fileIdentifier = fd.toString();
		
		
		// Localhost insert Layer example
		String geoServerWmsUrl = "http://localhost:8080/geoserver/wms";
		String workspace = "topp";
		String geoserverLayerTitle = "USA Population";
		String geoserverLayerName = "states";
		String groupDescription = "descrizione layer";
		UUID fd = UUID.randomUUID();
		String fileIdentifier = fd.toString();
		

		// geonetwork.getOrderedListOfGeoserver(GeoserverMethodResearch.RANDOM,
		// GeoserverType.WMS);

		// Node25.d //TRUE MARBLE
		// String geoServerWmsUrl =
		// "http://node25.d.d4science.research-infrastructures.eu:8080/geoserver/wms";
		// String geoserverLayerTitle = "TrueMarble.16km.2700x1350";
		// String groupDescription = "descrizione gruppo true marble";
		// String fileIdentifier = "123456";

		// Node25.d //ICE
		// String geoServerWmsUrl =
		// "http://node25.d.d4science.research-infrastructures.eu:8080/geoserver/wms";
		// String geoserverLayerTitle = "ice";
		// String groupDescription = "descrizione gruppo ice";
		// String fileIdentifier = "1234567";

		/**
		 * Insert Metadata
		 */
//		 System.out.println("\n\n\n Insert Metadata \n\n");
//		 try {
//			 System.out.println(geonetwork.insertMetadata(fileIdentifier, workspace, geoserverLayerTitle, geoserverLayerName, groupDescription, GeonetworkCategory.DATASETS, geoServerWmsUrl));
//		 } catch (Exception e) {
//		
//		 e.printStackTrace();
//		 }
		
		/**
		 * Insert Metadata with MethadataInfo
		 */
		
		MetadataInfo metadataInfo = new MetadataInfo();
		metadataInfo.setUrl(geoServerWmsUrl);
		metadataInfo.setWorkspace(workspace);
		metadataInfo.setName(geoserverLayerName);
		metadataInfo.setTitle(geoserverLayerTitle);
		metadataInfo.setDescription(groupDescription);
//		metadataInfo.setFileIdentifier(fileIdentifier);
		
		 System.out.println("\n\n\n Insert Metadata \n\n");
		 try {
			 System.out.println(geonetwork.insertMetadata(metadataInfo));
		 } catch (Exception e) {
		
		 e.printStackTrace();
		 }

//		 System.out.println("\n\n\n Insert Metadata \n\n");
//		 try {
//		 System.out.println(geonetwork.insertMetadataByCswTransaction(fileIdentifier,workspace, geoserverLayerTitle, geoserverLayerName, groupDescription, geoServerWmsUrl));
//		 } catch (Exception e) {
//		
//		 e.printStackTrace();
//		 }

		/**
		 * Get GeonetetworkLayerInfo By Search Service
		 */
//		 System.out.println("\n\n\nGet GeonetInfo By Search Service \n\n");
//		 try {
//		 System.out.println(geonetwork.getGeonetInfoBySearchService(geoserverLayerTitle,GeonetworkCategory.DATASETS,
//		 true));
//		 } catch (Exception e) {
//		 e.printStackTrace();
//		 }

		/**
		 * Get Metadata By Id
		 */
		// System.out.println("\n\n\nGet Metadata By Id \n\n");
		// try {
		// System.out.println(geonetwork.getMetadataById("29014"));
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		/**
		 * Get Metadata By UUID
		 */
		// System.out.println("\n\n\nGet Metadata By UUID \n\n");
		// try {
		// System.out.println(geonetwork.getMetadataByFileIdentifier(fileIdentifier));
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		/**
		 * Get Harvestings List
		 */
		// System.out.println("\n\n\nGet Harvestings List \n\n");
		// try {
		// System.out.println(geonetwork.getListHarvestings());
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		/**
		 * Get Harvesting by Id
		 */
		// System.out.println("\n\n\nGet Harvesting by Id \n\n");
		// try {
		// System.out.println(geonetwork.getHarvestingById("722"));
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		/**
		 * Search By Title
		 */
		// System.out.println("\n\n\nSearch By Title \n\n");
		// try {
		// System.out.println(geonetwork.searchLayerByTitleIsEqualTo(geoserverLayerTitle));
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		//
		
		
		/**
		 * Search By AnyText
		 */
//		 System.out.println("\n\n\nSearch By AnyText \n\n");
//		 try {
//		 System.out.println(geonetwork.searchLayerByAnyText("USA POPULATION", 5));
//		 } catch (Exception e) {
//		 e.printStackTrace();
//		 }
		
		/**
		 * Get Csw Record by Search
		 */
		 System.out.println("\n\n\nSearch By AnyText \n\n");
		 try {
			ArrayList<CswRecord> arrayCsw = new ArrayList<CswRecord>();
			arrayCsw = geonetwork.getCswRecordsBySearch(geoserverLayerName, 5);
			if(arrayCsw != null && arrayCsw.size() == 0)
				System.out.println("Object not found");
			
			int i = 0;
			for(CswRecord cso : arrayCsw){
				System.out.println("\nCSW Object "+ ++i +"...");
				System.out.println("Identifier: " + cso.getIdentifier());
				System.out.println("Title: " +cso.getTitle());
				for(String uri : cso.getURI())
					System.out.println("URI: " +uri);
			}
				
			
		 } catch (Exception e) {
		 e.printStackTrace();
		 }
		
		/**
		 * Delete Metadata by Id
		 */
		// System.out.println("\n\n\nDelete Metadata by Id \n\n");
		// try {
		// System.out.println(geonetwork.deleteMetadataById("29014"));
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		/**
		 * Logout Geonetwork
		 */
		// System.out.println("\n\n\nLogout Geonetwork \n\n");
		// System.out.println(geonetwork.logoutGeonetwork());

	}

}
