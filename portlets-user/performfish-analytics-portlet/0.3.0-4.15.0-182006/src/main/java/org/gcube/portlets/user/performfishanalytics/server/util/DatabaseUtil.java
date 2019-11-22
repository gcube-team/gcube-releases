/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.server.util;

import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletContext;

import org.gcube.portlets.user.performfishanalytics.server.persistence.GenericPersistenceDaoBuilder;
import org.gcube.portlets.user.performfishanalytics.server.util.database.FillDatabasePerBatchType;
import org.gcube.portlets.user.performfishanalytics.shared.Population;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class DatabaseUtil.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 22, 2019
 */
public class DatabaseUtil {

	protected static Logger log = LoggerFactory.getLogger(DatabaseUtil.class);

	/**
	 *
	 */
	public DatabaseUtil() {
	}


	/**
	 * Fill database if empty.
	 *
	 * @param dbFactory the db factory
	 * @param context the context
	 * @throws Exception the exception
	 */
	public void fillDatabaseIfEmpty(EntityManagerFactory dbFactory, ServletContext context) throws Exception{
		GenericPersistenceDaoBuilder<Population> builderPopulation =
			new GenericPersistenceDaoBuilder<Population>(
				dbFactory, Population.class.getSimpleName());
		List<Population> listPopulation = builderPopulation.getPersistenceEntity().getList();

		if(listPopulation.size()==0){
			log.info("The DB is empty filling it:");
			FillDatabasePerBatchType.fillDatabase(dbFactory, context);
			log.info("The DB was filled, printing it:");
			FillDatabasePerBatchType.printDatabaseData(dbFactory);
		}else{
			log.info("The DB is filled, no action performed");
		}

	}
}
