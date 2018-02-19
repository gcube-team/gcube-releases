package gr.cite.regional.data.collection.dataaccess.daos;

import gr.cite.regional.data.collection.dataaccess.entities.DataCollection;
import gr.cite.regional.data.collection.dataaccess.entities.DataSubmission;
import gr.cite.regional.data.collection.dataaccess.entities.UserReference;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class DataSubmissionDaoImpl extends JpaDao<DataSubmission, Integer> implements DataSubmissionDao {
	private static final Logger logger = LogManager.getLogger(DataSubmissionDaoImpl.class);

	@Override
	public DataSubmission loadDetails(DataSubmission t) {
		t.getAttributes();
		t.getComment();
		t.getCompletionTimestamp();
		t.getId();
		t.getOwner();
		t.getDomain();
		t.getStatus();
		t.getSubmissionTimestamp();
		return t;
	}
	
	@Override
	public List<DataSubmission> getDataSubmissionsByDataCollection(Integer dataCollectionId) {
		DataCollection dataCollection = new DataCollection();
		dataCollection.setId(dataCollectionId);
		
		return this.entityManager.createQuery(
				"FROM " + DataSubmission.class.getSimpleName() + " WHERE dataCollection=:dataCollection", DataSubmission.class)
				.setParameter("dataCollection", dataCollection).getResultList();
	}
	
	@Override
	public List<DataSubmission> getDataSubmissionsByDataCollectionAndOwner(Integer dataCollectionId, Integer ownerId) {
		DataCollection dataCollection = new DataCollection();
		dataCollection.setId(dataCollectionId);
		UserReference owner = new UserReference();
		owner.setId(ownerId);
		
		return this.entityManager.createQuery(
				"SELECT dataSubmission " +
				"FROM " + DataSubmission.class.getSimpleName() + " AS dataSubmission " +
				"WHERE dataCollection=:dataCollection AND owner=:owner " +
				"ORDER BY dataSubmission.submissionTimestamp", DataSubmission.class)
				.setParameter("dataCollection", dataCollection).setParameter("owner", owner)
				.getResultList();
	}
}
