package gr.cite.regional.data.collection.dataaccess.daos;

import gr.cite.regional.data.collection.dataaccess.entities.DataCollection;
import gr.cite.regional.data.collection.dataaccess.entities.DataSubmission;
import gr.cite.regional.data.collection.dataaccess.entities.UserReference;

import java.util.List;

public interface DataSubmissionDao extends Dao<DataSubmission, Integer> {
	List<DataSubmission> getDataSubmissionsByDataCollection(Integer dataCollectionId);
	List<DataSubmission> getDataSubmissionsByDataCollectionAndOwner(Integer dataCollectionId, Integer ownerId);
}
