package org.gcube.portlets.user.geoexplorer.test;

import org.apache.log4j.Logger;
import org.gcube.portlets.user.geoexplorer.server.GeoExplorerServiceImpl;

public class MetadataISOViewerTest {

	public static final String UUID = "26b5ceaa-772e-4a7b-a1d6-6aa0570fc0eb";

	public static Logger logger = Logger.getLogger(MetadataISOViewerTest.class);

	public static <T> void main(String[] args) throws Exception {

		String sessionTestId = "123";
		String scope = "";
		System.out.println(GeoExplorerServiceImpl.getMetadataItemByUUID(UUID, null, scope));
	}

}
