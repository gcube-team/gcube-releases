package org.gcube.spatial.data.geonetwork.test;

import org.gcube.spatial.data.geonetwork.GeoNetwork;
import org.gcube.spatial.data.geonetwork.GeoNetworkAdministration;
import org.gcube.spatial.data.geonetwork.LoginLevel;

public class DeleteMeta {

	public static void main(String[] args) throws Exception {
		TokenSetter.set("/d4science.research-infrastructures.eu/gCubeApps/ProtectedAreaImpactMaps");
		GeoNetworkAdministration admin=GeoNetwork.get();
		admin.login(LoginLevel.ADMIN);
		for(long l=48986;l<=49005;l++){
			
			admin.deleteMetadata(l);
		}
		

	}

}
