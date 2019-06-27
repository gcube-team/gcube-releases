package org.gcube.portlets.user.geoexplorer.test;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.gcube.portlets.user.geoexplorer.client.Constants;
import org.gcube.portlets.user.geoexplorer.server.service.dao.DaoManager;
import org.gcube.portlets.user.geoexplorer.shared.GeonetworkMetadata;

public class EclipseLinkTest {


	public static Logger logger = Logger.getLogger(EclipseLinkTest.class);


	public static <T> void main(String[] args) throws Exception {

		
		EntityManagerFactory factory = DaoManager.getEntityManagerFactoryForGeoParameters(Constants.defaultScope, null);
		
		EntityManager entity = factory.createEntityManager();
		
		
		Query query = entity.createQuery("select t from GeonetworkMetadata t");
		

		List<GeonetworkMetadata> listMeta = (List<GeonetworkMetadata> ) query.getResultList();
		
		for (GeonetworkMetadata meta : listMeta) {
			
			System.out.println(meta);
		}
		
		
	}
	
	

}
