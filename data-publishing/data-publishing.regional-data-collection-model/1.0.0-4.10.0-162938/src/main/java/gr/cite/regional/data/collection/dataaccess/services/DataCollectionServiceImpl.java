package gr.cite.regional.data.collection.dataaccess.services;

import gr.cite.regional.data.collection.dataaccess.daos.DataCollectionDao;
import gr.cite.regional.data.collection.dataaccess.daos.DataSubmissionDao;
import gr.cite.regional.data.collection.dataaccess.dsd.DsdProcessing;
import gr.cite.regional.data.collection.dataaccess.entities.DataCollection;
import gr.cite.regional.data.collection.dataaccess.entities.DataModel;
import gr.cite.regional.data.collection.dataaccess.entities.Domain;
import gr.cite.regional.data.collection.dataccess.exceptions.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class DataCollectionServiceImpl implements DataCollectionService {
	private static final Logger logger = LogManager.getLogger(DataCollectionServiceImpl.class);

	private DataCollectionDao dataCollectionDao;
	private DomainService domainService;
	private DataModelService dataModelService;
	private CdtService cdtService;
	private DataSubmissionDao dataSubmissionDao;
	private DsdProcessing dsdProcessing;
	
	@Autowired
	public DataCollectionServiceImpl(DataCollectionDao dataCollectionDao, DomainService domainService,
			DataModelService dataModelService, CdtService cdtService, DataSubmissionDao dataSubmissionDao,
				DsdProcessing dsdProcessing) {
		this.dataCollectionDao = dataCollectionDao;
		this.domainService = domainService;
		this.dataModelService = dataModelService;
		this.cdtService = cdtService;
		this.dataSubmissionDao = dataSubmissionDao;
		this.dsdProcessing = dsdProcessing;
	}
	
	@Override
	@Transactional(rollbackOn = ServiceException.class)
	public DataCollection addDataCollection(DataCollection dataCollection) throws ServiceException {
		try {
			setManagedReferences(dataCollection);
			this.dataCollectionDao.create(dataCollection);
			
			//dataCollection.getDataModel().getDefinition()
			this.cdtService.createCdtTable(dataCollection);
			
			return dataCollection;
		} catch (Exception e) {
			throw new ServiceException("Persistence error on Data Collection insertion", e);
		}
	}
	
	private void setManagedReferences(DataCollection dataCollection) throws ServiceException {
		Domain domain = this.domainService.getOrCreateDomainByLabel(dataCollection.getDomain().getLabel());
		DataModel dataModel = this.dataModelService.getDataModel(dataCollection.getDataModel().getId());
		
		dataCollection.setDomain(domain);
		dataCollection.setDataModel(dataModel);
	}
	
	@Override
	@Transactional(rollbackOn = ServiceException.class)
	public DataCollection updateDataCollection(DataCollection dataCollection) throws ServiceException {
		try {
			DataCollection currentDataCollection = this.dataCollectionDao.read(dataCollection.getId());
			replaceModifiedFields(dataCollection, currentDataCollection);
			
			return this.dataCollectionDao.update(currentDataCollection);
		} catch (Exception e) {
			throw new ServiceException("Persistence error on Data Collection update", e);
		}
	}
	
	private void replaceModifiedFields(DataCollection dataCollection, DataCollection currentDataCollection) {
		if (dataCollection.getLabel() != null) {
			currentDataCollection.setLabel(dataCollection.getLabel() );
		}
		if (dataCollection.getStatus() != null) {
			currentDataCollection.setStatus(dataCollection.getStatus());
		}
		if (dataCollection.getStartDate() != null) {
			currentDataCollection.setStartDate(dataCollection.getStartDate());
		}
		if (dataCollection.getEndDate() != null) {
			currentDataCollection.setEndDate(dataCollection.getEndDate());
		}
		if (dataCollection.getAttributes() != null) {
			currentDataCollection.setAttributes(dataCollection.getAttributes());
		}
		if (dataCollection.getDataModel() != null) {
			currentDataCollection.setDataModel(dataCollection.getDataModel());
		}
	}
	
	@Override
	@Transactional
	public DataCollection getDataCollection(Integer id) {
		return this.dataCollectionDao.read(id);
	}
	
	@Override
	@Transactional
	public List<DataCollection> getDataCollectionsByDomain(String domainLabel) throws ServiceException {
		Domain domain = this.domainService.getOrCreateDomainByLabel(domainLabel);
		
		try {
			return this.dataCollectionDao.getDataCollectionsByDomain(domain);
		} catch (Exception e) {
			throw new ServiceException("Error on retrieving Data Collection by Domain [" + domain.getLabel() + "]", e);
		}
		
		
	}
	
	@Override
	@Transactional
	public List<DataCollection> getDataCollectionByLabel(String label) throws ServiceException {
		try {
			return this.dataCollectionDao.getDataCollectionByLabel(label);
		} catch (Exception e) {
			throw new ServiceException("Persistence error on Data Collection insertion", e);
		}
	}
	
	/*@Override
	@Transactional
	public Set<DataSubmission> getDataCollectionDataSubmissions(Integer dataCollectionId) throws ServiceException {
		DataCollection dataCollection;
		try {
			dataCollection = this.dataCollectionDao.read(dataCollectionId);
			if (dataCollection == null) {
				throw new NoSuchElementException("DataCollection [" + dataCollectionId + "] does not exist");
			}
		} catch (Exception e) {
			throw new ServiceException("Error on DataCollection [" + dataCollectionId + "] DataSubmissions retrieval", e);
		}
		
		dataCollection.getDataSubmissions().forEach(this.dataSubmissionDao::loadDetails);
		
		return dataCollection.getDataSubmissions();
	}*/
	
	@Override
	@Transactional
	public List<DataCollection> getAllDataCollections() {
		return this.dataCollectionDao.getAll();
	}
	
	@Override
	@Transactional(rollbackOn = ServiceException.class)
	public void deleteDataCollection(Integer id) throws ServiceException {
		try {
			DataCollection dataCollection = this.dataCollectionDao.read(id);
			this.dataCollectionDao.delete(dataCollection);
			
			this.cdtService.deleteCdtsOfDataCollection(id);
		} catch (Exception e) {
			throw new ServiceException("Persistence error on Data Collection deletion", e);
		}
	}
}
