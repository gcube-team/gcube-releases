package org.gcube.portlets.user.geoexplorer.test;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.user.geoexplorer.client.beans.LayerItem;
import org.gcube.portlets.user.geoexplorer.server.SummaryMetadataISO19139View;
import org.gcube.portlets.user.geoexplorer.server.service.GeonetworkInstance;
import org.gcube.portlets.user.geoexplorer.server.util.MetadataConverter;
import org.gcube.portlets.user.geoexplorer.shared.MetadataItem;
import org.gcube.spatial.data.geonetwork.GeoNetworkReader;

public class GeoExplorerMetadataConverter {


//	public static final String UUID = "26b5ceaa-772e-4a7b-a1d6-6aa0570fc0eb";

//	public static final String UUID = "ed8f77bd-2423-4036-b34d-2f1cb5fcaffc"; //(EEZ)

//	public static final String UUID = "3dc36cff-d4f1-4024-844c-1a16e51c5416"; //(Large Marine Ecosystems of the World)

//	public static final String UUID = "7f700d7a-6952-42e2-905f-8650d2285b2a";

	//public static final String UUID = "1d954a4c-7c50-44a6-9a48-b51ce15b9705"; //sarda sarda

	public static final String UUID = "8a878105-ef06-4b1f-843f-120fc525b22b"; //sarda sarda


//	public static final String UUID = "56146ceb-aaeb-4d3e-af05-aefaf8a792cb"; //sarda sarda fabio

//	public static final String UUID = "88d3a519-36a7-478b-8f27-8a29695d9cf2"; //biodiversity on devVRE


//	public static final String UUID = "3dc36cff-d4f1-4024-844c-1a16e51c5416";

//	public static final String UUID = "a42c2a8a-62ad-4b11-8cf5-13864de1a569"; //TRUE MARBLE

//	public static final String UUID = "e3012486-5ca9-43a6-bbdb-61849a70c511"; //POZZI

//	public static final String UUID = "efe91762-f8d0-41cb-beba-1a93dbbd2976"; //TEMPERATURE


	public MetadataItem meta;

	public GeoExplorerMetadataConverter() throws Exception {

		String defaultScope="/d4science.research-infrastructures.eu/gCubeApps/SIASPA";

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
//			 Object resp;
			LayerItem li = MetadataConverter.getLayerItemFromMetadataUUID(gReader, UUID);
			System.out.println(li);
			// printLayerItems(layerItems,20);

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error in layers csw loader ", e);
		}

	}

	public static void main(String[] args) throws Exception {

		new GeoExplorerMetadataConverter();

	}

}
