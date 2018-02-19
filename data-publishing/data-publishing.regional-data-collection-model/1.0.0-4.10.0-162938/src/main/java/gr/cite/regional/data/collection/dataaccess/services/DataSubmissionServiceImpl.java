package gr.cite.regional.data.collection.dataaccess.services;

import gr.cite.regional.data.collection.dataaccess.daos.DataSubmissionDao;
import gr.cite.regional.data.collection.dataaccess.entities.Cdt;
import gr.cite.regional.data.collection.dataaccess.entities.DataCollection;
import gr.cite.regional.data.collection.dataaccess.entities.DataSubmission;
import gr.cite.regional.data.collection.dataaccess.entities.Domain;
import gr.cite.regional.data.collection.dataaccess.entities.UserReference;
import gr.cite.regional.data.collection.dataccess.exceptions.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class DataSubmissionServiceImpl implements DataSubmissionService {
	private CdtService cdtService;
	private DataSubmissionDao dataSubmissionDao;
	private DataCollectionService dataCollectionService;
	private DomainService domainService;
	private UserReferenceService userReferenceService;
	
	@Autowired
	public DataSubmissionServiceImpl(CdtService cdtService, DataCollectionService dataCollectionService, DomainService domainService, UserReferenceService userReferenceService, DataSubmissionDao dataSubmissionDao) {
		this.cdtService = cdtService;
		this.dataSubmissionDao = dataSubmissionDao;
		this.dataCollectionService = dataCollectionService;
		this.domainService = domainService;
		this.userReferenceService = userReferenceService;
	}
	
	@Override
	@Transactional(rollbackOn = ServiceException.class)
	public DataSubmission addDataSubmission(DataSubmission dataSubmission) throws ServiceException {
		setManagedReferences(dataSubmission);
		
		DataSubmission newDataSubmission = this.dataSubmissionDao.create(dataSubmission);
		
		dataSubmission.getData().forEach(cdt -> cdt.setDataSubmission(newDataSubmission));
		this.cdtService.addCdts(newDataSubmission.getData());
		
		return newDataSubmission;
	}
	
	private void setManagedReferences(DataSubmission dataSubmission) {
		DataCollection dataCollection = this.dataCollectionService.getDataCollection(dataSubmission.getDataCollection().getId());
		Domain domain = this.domainService.getDomain(dataSubmission.getDomain().getId());
		UserReference owner = this.userReferenceService.getUserReference(dataSubmission.getOwner().getId());
		
		dataSubmission.setDataCollection(dataCollection);
		dataSubmission.setDomain(domain);
		dataSubmission.setOwner(owner);
	}
	
	@Override
	@Transactional(rollbackOn = ServiceException.class)
	public DataSubmission updateDataSubmission(DataSubmission dataSubmission) throws ServiceException {
		return updateDataSubmission(dataSubmission, false);
	}
	
	@Override
	@Transactional(rollbackOn = ServiceException.class)
	public DataSubmission updateDataSubmissionAndData(DataSubmission dataSubmission) throws ServiceException {
		return updateDataSubmission(dataSubmission, true);
	}
	
	@Override
	@Transactional(rollbackOn = ServiceException.class)
	public DataSubmission updateDataSubmission(DataSubmission dataSubmission, boolean updateData) throws ServiceException {
		DataSubmission updatedDataSubmission;
		DataSubmission currentDataSubmission = this.dataSubmissionDao.read(dataSubmission.getId());
		if (currentDataSubmission == null) {
			throw new NoSuchElementException("The requested Data Submission [" + dataSubmission.getId() + "] does not exist");
		}
		
		replaceModifiedFields(dataSubmission, currentDataSubmission);
		try {
			updatedDataSubmission = this.dataSubmissionDao.update(currentDataSubmission);
		} catch (Exception e) {
			throw new ServiceException("Data Submission [" + dataSubmission.getId() + "] update failed", e);
		}
		
		if (updateData && dataSubmission.getData() != null) {
			try {
				updatedDataSubmission.setData(dataSubmission.getData());
				updateDataSubmissionData(updatedDataSubmission);
			} catch (Exception e) {
				throw new ServiceException("Data Submission [" + dataSubmission.getId() + "] data update failed", e);
			}
		}
		
		return updatedDataSubmission;
	}
	
	private void replaceModifiedFields(DataSubmission dataSubmission, DataSubmission currentDataSubmission) {
		if (dataSubmission.getAttributes() != null) {
			currentDataSubmission.setAttributes(dataSubmission.getAttributes());
		}
		if (dataSubmission.getComment() != null) {
			currentDataSubmission.setComment(dataSubmission.getComment());
		}
		if (dataSubmission.getCompletionTimestamp() != null) {
			currentDataSubmission.setCompletionTimestamp(dataSubmission.getCompletionTimestamp());
		}
		if (dataSubmission.getStatus() != null) {
			currentDataSubmission.setStatus(dataSubmission.getStatus());
		}
		if (dataSubmission.getSubmissionTimestamp() != null) {
			currentDataSubmission.setSubmissionTimestamp(dataSubmission.getSubmissionTimestamp());
		}
	}
	
	private void replaceAllFields(DataSubmission dataSubmission, DataSubmission currentDataSubmission) {
		currentDataSubmission.setAttributes(dataSubmission.getAttributes());
		currentDataSubmission.setComment(dataSubmission.getComment());
		currentDataSubmission.setCompletionTimestamp(dataSubmission.getCompletionTimestamp());
		currentDataSubmission.setStatus(dataSubmission.getStatus());
		currentDataSubmission.setSubmissionTimestamp(dataSubmission.getSubmissionTimestamp());
	}
	
	@Transactional(rollbackOn = ServiceException.class)
	private void updateDataSubmissionData(DataSubmission dataSubmission) throws ServiceException {
		this.cdtService.deactivateCdtsOfDataSubmission(dataSubmission);
		dataSubmission.getData().forEach(cdt -> cdt.setDataSubmission(dataSubmission));
		this.cdtService.addCdts(dataSubmission.getData());
	}
	
	@Override
	@Transactional(rollbackOn = ServiceException.class)
	public DataSubmission getDataSubmission(Integer id) throws ServiceException {
		DataSubmission dataSubmission = this.dataSubmissionDao.read(id);
		
		if (dataSubmission == null) throw new NoSuchElementException("DataSubmission [" + id + "] does not exist");
		
		List<Cdt> data = this.cdtService.getCdtByDataSubmission(dataSubmission);
		dataSubmission.setData(data);
		
		return dataSubmission;
	}
	
	@Override
	public List<DataSubmission> getDataSubmissionsByDataCollectionId(Integer id) throws ServiceException {
		try {
			return this.getDataSubmissionsByDataCollectionId(id, false);
		} catch (Exception e) {
			throw new ServiceException("Error on Data Submission [" + id + "] retrieval", e);
		}
	}

	@Override
	public List<DataSubmission> getDataSubmissionsByDataCollectionId(Integer id, boolean loadCDTs) throws ServiceException {
		try {
			List<DataSubmission> dataSubmissions = this.dataSubmissionDao.getDataSubmissionsByDataCollection(id);
			if (loadCDTs) {
				for (DataSubmission dataSubmission : dataSubmissions) {
					List<Cdt> data = this.cdtService.getCdtByDataSubmission(dataSubmission);
					dataSubmission.setData(data);
				}
			}
			return  dataSubmissions;
		} catch (Exception e) {
			throw new ServiceException("Error on Data Submission [" + id + "] retrieval", e);
		}
	}

	@Override
	public List<DataSubmission> getDataSubmissionsByDataCollectionAndOwner(Integer dataCollectionId, Integer ownerId) throws ServiceException {
		/*DataCollection dataCollection = this.dataCollectionService.getDataCollection(dataCollectionId);
		UserReference owner = this.userReferenceService.getUserReference(ownerId);*/
		try {
			return this.dataSubmissionDao.getDataSubmissionsByDataCollectionAndOwner(dataCollectionId, ownerId);
		} catch (Exception e) {
			throw new ServiceException("Error on Data Submission with Data Collection [" + dataCollectionId + "] and Owner [" + ownerId + "] retrieval", e);
		}
	}
	
	@Override
	public List<DataSubmission> getAllDataSubmissions() throws ServiceException {
		try {
			return this.dataSubmissionDao.getAll();
		} catch (Exception e) {
			throw new ServiceException("Error on Data Submissions retrieval", e);
		}
	}
	
	@Override
	public void deleteDataSubmission(Integer id) throws ServiceException {
		throw new RuntimeException("DataSubmission deletion is not yet implemented");
	}
	
	@Override
	public DataSubmission loadDetails(DataSubmission ds) {
		return this.dataSubmissionDao.loadDetails(ds);
	}
}
