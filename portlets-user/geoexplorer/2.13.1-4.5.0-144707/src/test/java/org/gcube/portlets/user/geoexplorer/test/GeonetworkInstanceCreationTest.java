package org.gcube.portlets.user.geoexplorer.test;

import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.gcube.portlets.user.geoexplorer.server.service.dao.DaoManager;
import org.gcube.portlets.user.geoexplorer.server.service.dao.GeoParametersPersistence;
import org.gcube.portlets.user.geoexplorer.shared.GeoResourceParameters;
import org.gcube.portlets.user.geoexplorer.shared.GeoResourceParameters.RESOURCETYPE;

public class GeonetworkInstanceCreationTest {

//	public static final String TEXT_TO_SEARCH = "sarda sarda";
	
//	public static final String TEXT_TO_SEARCH = "fao";
	
	public static final String TEXT_TO_SEARCH = "";

	public static Logger logger = Logger.getLogger(GeonetworkInstanceCreationTest.class);

	private static HttpSession session;

	public static <T> void main(String[] args) throws Exception {

		
		String defaultScope="/gcube/devsec/devVRE";
		
		EntityManagerFactory factory = DaoManager.getEntityManagerFactoryForGeoParameters(defaultScope,null);
		GeoParametersPersistence geoPersistence = new GeoParametersPersistence(factory);
		
//		geoPersistence.removeAll();
		
		
		for (int i = 0; i < 5; i++) {
			
			String geoUrl = "http://geonetwork";
			String geoNetworkUser = ""+i;
			String geoNetworkPwd = ""+i;
			String geoScope = "/d4science.research-infrastructures.eu/gCubeApps/EcologicalModelling";

			GeoResourceParameters geo = new GeoResourceParameters(geoScope, geoUrl, geoNetworkUser, geoNetworkPwd, GeoResourceParameters.RESOURCETYPE.GEONETWORK);
	
			geoPersistence.insert(geo);
			
			geoUrl = "http://geoserver";
			
			geo = new GeoResourceParameters(geoScope, geoUrl, geoNetworkUser, geoNetworkPwd, GeoResourceParameters.RESOURCETYPE.GEOSERVER);
			
			geoPersistence.insert(geo);
			
			Thread.sleep(1000);
			System.out.println("sleeping "+i);
		}
		
		for (GeoResourceParameters gs : geoPersistence.getList()) {
			System.out.println(gs);
		}
//		
//		int items = geoPersistence.countItems();
//		
//		if(items>)
		
		System.out.println("Last RESOURCETYPE.GEONETWORK: "+geoPersistence.getLastResourceType(RESOURCETYPE.GEONETWORK));
		System.out.println("Last RESOURCETYPE.GEOSERVER: "+geoPersistence.getLastResourceType(RESOURCETYPE.GEOSERVER));



	}
}
