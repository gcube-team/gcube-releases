package org.gcube.contentmanagement.timeseries.geotools.tools;

import java.util.List;

import org.gcube.common.geoserverinterface.GeonetworkCaller;
import org.gcube.common.geoserverinterface.GeonetworkCommonResourceInterface.GeonetworkCategory;

public class GeoNetworkTotalCleaner {

	static String GeoNetworkUserName = "admin";
	static String GeoNetworkPwd = "admin";
	static String GeoServerUserName = "admin";
	static String GeoServerPwd = "gcube@geo2010";
	static String Workspace = "aquamaps:";
	static String GroupNames = "WMS*";
	static String GeoNetworkUrl = "http://geoserver.d4science-ii.research-infrastructures.eu/geonetwork";
	
	public static void cleanLayers() throws Exception {
		System.out.println("INIT - clean layers");
		long t0 = System.currentTimeMillis();
		
		GeonetworkCaller geonetwork = new GeonetworkCaller(GeoNetworkUrl, GeoNetworkUserName, GeoNetworkPwd,GeoServerUserName,GeoServerPwd);
		List<String> ids = geonetwork.getGeonetworkGetMethods().searchID(Workspace, GeonetworkCategory.ANY, 0.5f);
		System.out.println("DELETING "+ids.size()+" metaLayers");
		int n = ids.size();
		int i=0;
		for (String id:ids){
			try{
				System.out.println(""+((float)i*100f/(float)n));
				geonetwork.deleteMetadataById(id);
			}catch(Exception e){
				e.printStackTrace();
			}
			i++;
		}
		
		ids = geonetwork.getGeonetworkGetMethods().searchID(GroupNames, GeonetworkCategory.ANY, 0.3f);
		System.out.println("DELETING "+ids.size()+" metaLayers");
		n = ids.size();
		i=0;
		for (String id:ids){
			try{
				System.out.println(""+(i/n));
				geonetwork.deleteMetadataById(id);
			}catch(Throwable e){
				e.printStackTrace();
			}
			i++;
		}
		
		/*
		CswLayersResult result = geonetwork.getLayersFromCsw(Workspace, 1, 10, false, false, FILTER_TYPE.NO_FILTER, null);
		List<LayerCsw> layers = result.getLayers();
		for (LayerCsw csw:layers){
			System.out.println("TITLE: "+csw.getTitle());
			System.out.println("UUID: "+csw.getUuid());
			System.out.println("NAME: "+csw.getName());
			
			if (csw.getTitle().equals("species_and_space")){
				String id = geonetwork.getGeonetworkGetMethods().searchService(Workspace, GeonetworkCategory.ANY, false);
				System.out.println("\tID : "+id);
				/*
				System.out.println("\tDELETING ");
				String del = geonetwork.deleteMetadataById(csw.getUuid());
				System.out.println("\tDELETED "+del);
				*/
//				System.exit(0);
//			}
//				String id = geonetwork.getMetadataByFileIdentifier(csw.getUuid());
//				System.out.println("ID : "+id);
				/*
				String layer = csw.getName().substring(csw.getName().lastIndexOf(':'));
				ArrayList<CswRecord> records = geonetwork.getCswRecordsBySearch(layer, 0);		
				int s = records.size();
				for (int i=0;i<s;i++){ 
					CswRecord record = records.get(i);
					System.out.println("\t->ID: "+record.getIdentifier());
					System.out.println("\t->TITLE: "+record.getTitle());
				}
			}
			*/
//		}
		/*
		ArrayList<CswRecord> records = geonetwork.getCswRecordsBySearch(Workspace, 0);
		
		int s = records.size();
		for (int i=0;i<s;i++){ 
			CswRecord record = records.get(i);
			System.out.println("ID: "+record.getIdentifier());
			System.out.println("TITLE: "+record.getTitle());
		}
		
//		geonetwork.deleteMetadataById(id);
		
		
		long t1 = System.currentTimeMillis();
		System.out.println("finished in : " + (t1 - t0) + " ms");
		*/
	
	
	}

	
	public static void main(String[] args) throws Exception {
		cleanLayers();
	}

}
