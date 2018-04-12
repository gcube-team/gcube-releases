package org.gcube.portlets.user.geoexplorer.test;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.user.geoexplorer.server.SummaryMetadataISO19139View;
import org.gcube.portlets.user.geoexplorer.server.service.GeonetworkInstance;
import org.gcube.portlets.user.geoexplorer.server.util.MetadataConverter;
import org.gcube.portlets.user.geoexplorer.shared.MetadataItem;
import org.gcube.spatial.data.geonetwork.GeoNetworkReader;

public class GeoExplorerHttpCallertTest {


//	public static final String UUID = "26b5ceaa-772e-4a7b-a1d6-6aa0570fc0eb";

//	public static final String UUID = "7f700d7a-6952-42e2-905f-8650d2285b2a";

	public static final String UUID = "ed8f77bd-2423-4036-b34d-2f1cb5fqwer"; //(EEZ)

//	public static final String UUID = "1d954a4c-7c50-44a6-9a48-b51ce15b9705"; //sarda sarda

//	public static final String UUID = "56146ceb-aaeb-4d3e-af05-aefaf8a792cb"; //sarda sarda fabio

//	public static final String UUID = "88d3a519-36a7-478b-8f27-8a29695d9cf2"; //biodiversity on devVRE

	public MetadataItem meta;

	public GeoExplorerHttpCallertTest() throws Exception {

		String defaultScope="/gcube/devsec/devVRE";

//		String defaultScope="/gcube/devNext";

		ScopeProvider.instance.set(defaultScope);

		System.out.println("Get Metadata by UUID: " + UUID);

		GeonetworkInstance gn = new GeonetworkInstance(defaultScope);

		gn.readConfigurationAndInstance(true,null);

		System.out.println("-- GeonetworkInstance OK");

		GeoNetworkReader gReader = gn.getGeonetworkReader();
		System.out.println("-- GeoNetworkReader OK");

		// GNSearchRequest req = getRequest();
		// System.out.println("-- GNSearchRequest OK");

		try {

			meta = MetadataConverter.getMetadataItemFromMetadataUUID(gReader, UUID);
			System.out.println(meta);


			SummaryMetadataISO19139View sum = new SummaryMetadataISO19139View();

			System.out.println(sum.getBody(meta, "", true, false));



			// List<LayerItem> layerItems = MetadataConverter.getLayerItemsFromMetadata(gReader, resp);

			// printLayerItems(layerItems,20);

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error in layers csw loader ", e);
		}

	}

	public static void main(String[] args) throws Exception {

		new GeoExplorerHttpCallertTest();

	}

}
