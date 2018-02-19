package gr.cite.regional.data.collection.dataaccess.daos;

import gr.cite.regional.data.collection.dataaccess.entities.DataCollection;
import gr.cite.regional.data.collection.dataaccess.entities.Domain;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class DataCollectionDaoImpl extends JpaDao<DataCollection, Integer> implements DataCollectionDao {
	private static final Logger logger = LogManager.getLogger(DataCollectionDaoImpl.class);

	@Override
	public DataCollection loadDetails(DataCollection t) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<DataCollection> getDataCollectionsByDomain(Domain domain) {
		return this.entityManager.createQuery(
				"FROM " + DataCollection.class.getSimpleName()
						+ " where domain = :domain", DataCollection.class)
				.setParameter("domain", domain).getResultList();
	}
	
	@Override
	public List<DataCollection> getDataCollectionByLabel(String label) {
		List<DataCollection> dataCollections = this.entityManager.createQuery(
				"FROM " + DataCollection.class.getSimpleName()
					+ " where label = :label", DataCollection.class)
				.setParameter("label", label).getResultList();
		
		if (logger.isDebugEnabled() && dataCollections != null) {
			for (DataCollection dataCollection: dataCollections)
				logger.debug("DataCollection (" + dataCollection.getLabel() + ")");
		}
		
		return dataCollections;
	}

}
