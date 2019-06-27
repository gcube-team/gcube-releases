/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.server;

import javax.persistence.EntityManagerFactory;

import org.gcube.portlets.user.performfishanalytics.server.database.EntityManagerFactoryCreator;
import org.gcube.portlets.user.performfishanalytics.server.util.DatabaseUtil;


/**
 * The Class TestFillDatabase.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Feb 12, 2019
 */
public class TestFillDatabase {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws Exception the exception
	 */
	public static void main(String[] args) throws Exception {
		EntityManagerFactoryCreator mng = EntityManagerFactoryCreator.instanceLocalMode();
		EntityManagerFactory entityManagerFactory = mng.getEntityManagerFactory();

		new DatabaseUtil().fillDatabaseIfEmpty(entityManagerFactory, null);

	}
}
