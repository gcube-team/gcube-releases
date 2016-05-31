package org.gcube.spatial.data.geonetwork.test;

import org.gcube.spatial.data.geonetwork.GeoNetwork;
import org.gcube.spatial.data.geonetwork.GeoNetworkReader;
import org.gcube.spatial.data.geonetwork.LoginLevel;
import org.gcube.spatial.data.geonetwork.configuration.ConfigurationManager;
import org.geotoolkit.xml.XML;

public class Wiki {

		
	public static void main(String[] args) throws Exception{
		ConfigurationManager.setConfiguration(MyConfiguration.class);
		String uuid="86a7ac79-866a-49c6-b5d5-602fc2d87ddd";
		GeoNetworkReader reader=GeoNetwork.get();
		reader.login(LoginLevel.DEFAULT);
		System.out.println(reader.getByIdAsRawString(uuid));
		System.out.println("\n\n*********************************************************************************\n\n");
		System.out.println(XML.marshal(reader.getById(uuid)));
	}
}
