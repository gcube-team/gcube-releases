package gr.cite.regional.data.collection.dataaccess.services;

import gr.cite.regional.data.collection.dataaccess.entities.DataSubmission;
import gr.cite.regional.data.collection.dataccess.exceptions.ServiceException;

import java.util.List;
import java.util.Set;

public interface DataSubmissionService {
	public DataSubmission addDataSubmission(DataSubmission dataSubmission) throws ServiceException;
	public DataSubmission updateDataSubmission(DataSubmission dataSubmission) throws ServiceException;
	public DataSubmission updateDataSubmissionAndData(DataSubmission dataSubmission) throws ServiceException;
	public DataSubmission updateDataSubmission(DataSubmission dataSubmission, boolean replace) throws ServiceException;
	public DataSubmission getDataSubmission(Integer id) throws ServiceException;
	List<DataSubmission> getDataSubmissionsByDataCollectionId(Integer id) throws ServiceException;
	List<DataSubmission> getDataSubmissionsByDataCollectionId(Integer id, boolean loadCDTs) throws ServiceException;
	public List<DataSubmission> getDataSubmissionsByDataCollectionAndOwner(Integer dataCollectionId, Integer ownerId) throws ServiceException;
	public List<DataSubmission> getAllDataSubmissions() throws ServiceException;
	public void deleteDataSubmission(Integer id) throws ServiceException;
	public DataSubmission loadDetails(DataSubmission ds);
}
