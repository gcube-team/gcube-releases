package org.gcube.portlets.user.geoexplorer.test;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.user.geoexplorer.server.SummaryMetadataISO19139View;
import org.gcube.portlets.user.geoexplorer.server.service.GeonetworkInstance;
import org.gcube.portlets.user.geoexplorer.server.util.MetadataConverter;
import org.gcube.portlets.user.geoexplorer.shared.MetadataItem;
import org.gcube.spatial.data.geonetwork.GeoNetworkReader;

public class PoolGeoExplorerMetadataConverter {


//	public static final String UUID = "26b5ceaa-772e-4a7b-a1d6-6aa0570fc0eb";

//	public static final String UUID = "ed8f77bd-2423-4036-b34d-2f1cb5fcaffc"; //(EEZ)

//	public static final String UUID = "3dc36cff-d4f1-4024-844c-1a16e51c5416"; //(Large Marine Ecosystems of the World)

//	public static final String UUID = "7f700d7a-6952-42e2-905f-8650d2285b2a";

//	public static final String UUID = "1d954a4c-7c50-44a6-9a48-b51ce15b9705"; //sarda sarda

//	public static final String UUID = "56146ceb-aaeb-4d3e-af05-aefaf8a792cb"; //sarda sarda fabio

//	public static final String UUID = "88d3a519-36a7-478b-8f27-8a29695d9cf2"; //biodiversity on devVRE


//	public static final String UUID = "3dc36cff-d4f1-4024-844c-1a16e51c5416";

	public String UUID = "a42c2a8a-62ad-4b11-8cf5-13864de1a569"; //TRUE MARBLE
	public static String defaultScope="/gcube/devsec/devVRE";
	public static GeonetworkInstance gn = new GeonetworkInstance(defaultScope);
//	public MetadataItem meta;

	public PoolGeoExplorerMetadataConverter(int i) throws Exception {



//		String defaultScope="/gcube/devNext";

		ScopeProvider.instance.set(defaultScope);

		System.out.println("Thread "+i+") Get Metadata by UUID: " + UUID);

//		GeonetworkInstance gn = new GeonetworkInstance(defaultScope);



//		System.out.println("-- GeonetworkInstance OK");
		GeoNetworkReader gReader = gn.getGeonetworkReader();
		System.out.println("-- GeoNetworkReader OK");

		// GNSearchRequest req = getRequest();
		// System.out.println("-- GNSearchRequest OK");

		try {

//			MetadataConverter.getLayerItemFromMetadataUUID(gReader, UUID);
			MetadataItem meta = MetadataConverter.getMetadataItemFromMetadataUUID(gReader, UUID);
			System.out.println(meta);

			SummaryMetadataISO19139View sum = new SummaryMetadataISO19139View();
			String wmsRequest = null;

			sum.getBody(meta, wmsRequest, true, false);

			System.out.println("get body for UUID: "+meta.getUuid() +" is OK");

			UUID = "3dc36cff-d4f1-4024-844c-1a16e51c5416";

			MetadataConverter.getLayerItemFromMetadataUUID(gReader, UUID);
			meta = MetadataConverter.getMetadataItemFromMetadataUUID(gReader, UUID);
			System.out.println(meta);

//			System.out.println(meta);

			System.out.println(i +" sleeping.. ");
			Thread.sleep(50);
			System.out.println(i +" alive.. ");

			sum = new SummaryMetadataISO19139View();

			sum.getBody(meta, wmsRequest, true, false);

			System.out.println("get body for UUID: "+meta.getUuid() +" is OK");


			UUID = "ed8f77bd-2423-4036-b34d-2f1cb5fcaffc";
			MetadataConverter.getLayerItemFromMetadataUUID(gReader, UUID);
			meta = MetadataConverter.getMetadataItemFromMetadataUUID(gReader, UUID);
			System.out.println(meta);

			sum = new SummaryMetadataISO19139View();

			sum.getBody(meta, wmsRequest, true, false);

			System.out.println("get body for UUID: "+meta.getUuid() +" is OK");

			// List<LayerItem> layerItems = MetadataConverter.getLayerItemsFromMetadata(gReader, resp);

			// printLayerItems(layerItems,20);

			System.out.println(i +" termined! ");

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error in layers csw loader ", e);
		}

	}

	public static void main(String[] args) throws Exception {

//		for (int i = 0; i < 100; i++) {
//
//			final int index = i;
//			new Thread(){
//
//				public void run() {
//
//					try {
//						new PoolGeoExplorerMetadataConverter(index);
//
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//
//				};
//			}.start();
//		}
//
		gn.readConfigurationAndInstance(true,null);

		for (int i = 0; i < 100; i++) {
			final int index = i;
			new PoolGeoExplorerMetadataConverter(index);
//			final int index = i;
//			new Thread(){
//
//				public void run() {
//
//					try {
//						new PoolGeoExplorerMetadataConverter(index);
//
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//
//				};
//			}.start();
		}


	}

}
