/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.server;


import javax.persistence.EntityManagerFactory;

import org.gcube.portlets.user.performfishanalytics.server.database.EntityManagerFactoryCreator;
import org.gcube.portlets.user.performfishanalytics.server.persistence.GenericPersistenceDaoBuilder;
import org.gcube.portlets.user.performfishanalytics.shared.Population;
import org.gcube.portlets.user.performfishanalytics.shared.exceptions.DatabaseServiceException;


/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 11, 2019
 */
public class TestEntityManagerFactory {

	EntityManagerFactory entityManagerFactory;
	//EntityManager em;

	//@Before
	public void instanceFactory() throws Exception{
		EntityManagerFactoryCreator mng = EntityManagerFactoryCreator.instanceLocalMode();
		entityManagerFactory = mng.getEntityManagerFactory();
		//em = entityManagerFactory.createEntityManager();
	}

	//@Test
	public void init() throws DatabaseServiceException{

		GenericPersistenceDaoBuilder<Population> builderPopulation = new GenericPersistenceDaoBuilder<Population>(entityManagerFactory, "Population");
		for (int i = 0; i < 10; i++) {
			builderPopulation.getPersistenceEntity().insert(new Population("id"+i, "name"+i, "level"+i, "description"+i, null));
		}

		System.out.println("Init done");

	}
}
