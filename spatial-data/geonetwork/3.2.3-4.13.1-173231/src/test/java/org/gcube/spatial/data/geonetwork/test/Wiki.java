package org.gcube.spatial.data.geonetwork.test;

import org.gcube.spatial.data.geonetwork.GeoNetwork;
import org.gcube.spatial.data.geonetwork.GeoNetworkAdministration;
import org.gcube.spatial.data.geonetwork.LoginLevel;
import org.geotoolkit.xml.XML;

public class Wiki {

		
	public static void main(String[] args) throws Exception{
//		ConfigurationManager.setConfiguration(MyConfiguration.class);
//		String uuid="86a7ac79-866a-49c6-b5d5-602fc2d87ddd";
		TokenSetter.set("/d4science.research-infrastructures.eu");
		GeoNetworkAdministration reader=GeoNetwork.get();
		long id=93778;
		reader.login(LoginLevel.ADMIN);		
		System.out.println("\n\n*********************************************************************************\n\n");
		System.out.println(XML.marshal(reader.getById(id)));
		
		
		
		
	}
}
