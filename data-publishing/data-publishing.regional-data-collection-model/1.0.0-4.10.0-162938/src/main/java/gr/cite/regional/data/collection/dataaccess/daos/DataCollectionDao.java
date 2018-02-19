package gr.cite.regional.data.collection.dataaccess.daos;

import gr.cite.regional.data.collection.dataaccess.entities.DataCollection;
import gr.cite.regional.data.collection.dataaccess.entities.Domain;

import java.util.List;

public interface DataCollectionDao extends Dao<DataCollection, Integer> {
	List<DataCollection> getDataCollectionsByDomain(Domain domain);
	List<DataCollection> getDataCollectionByLabel(String label);
}
