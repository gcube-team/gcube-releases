package gr.cite.regional.data.collection.dataaccess.services;

import gr.cite.regional.data.collection.dataaccess.daos.DataModelDao;
import gr.cite.regional.data.collection.dataaccess.entities.DataModel;
import gr.cite.regional.data.collection.dataaccess.entities.Domain;
import gr.cite.regional.data.collection.dataccess.exceptions.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class DataModelServiceImpl implements DataModelService {
	private static final Logger logger = LogManager.getLogger(DataModelServiceImpl.class);
	
	private DataModelDao dataModelDao;
	private DomainService domainService;
	
	@Autowired
	public DataModelServiceImpl(DataModelDao dataModelDao, DomainService domainService) {
		this.dataModelDao = dataModelDao;
		this.domainService = domainService;
	}
	
	@Override
	@Transactional(rollbackOn = ServiceException.class)
	public DataModel addDataModel(DataModel dataModel) throws ServiceException {
		try {
			setManagedReferences(dataModel);
			return this.dataModelDao.create(dataModel);
		} catch (Exception e) {
			throw new ServiceException("Error on DataModel insertion", e);
		}
	}
	
	private void setManagedReferences(DataModel dataModel) throws ServiceException {
		Domain domain = this.domainService.getOrCreateDomainByLabel(dataModel.getDomain().getLabel());
		dataModel.setDomain(domain);
	}
	
	@Override
	@Transactional
	public DataModel updateDataModel(DataModel dataModel) {
		logger.debug("Service that updates data model");
		
		DataModel current = this.dataModelDao.read(dataModel.getId());
		if (current  == null) throw new NoSuchElementException("DataModel [" + dataModel.getId() + "] does not exist");
		
		replaceModifiedFields(dataModel, current);
		
		return this.dataModelDao.update(current);
	}
	
	private void replaceModifiedFields(DataModel dataModel, DataModel currentDataModel) {
		if (dataModel.getLabel() != null) {
			currentDataModel.setLabel(dataModel.getLabel());
		}
		if (dataModel.getDefinition() != null) {
			currentDataModel.setDefinition(dataModel.getDefinition());
		}
		if (dataModel.getPrevious() != null) {
			currentDataModel.setPrevious(dataModel.getPrevious());
		}
		if (dataModel.getUri() != null) {
			currentDataModel.setUri(dataModel.getUri());
		}
		if (dataModel.getVersion() != null) {
			currentDataModel.setVersion(dataModel.getVersion());
		}
		if (dataModel.getProperties() != null) {
			currentDataModel.setProperties(dataModel.getProperties());
		}
	}
	
	@Override
	@Transactional
	public DataModel getDataModel(Integer dataModelId) {
		logger.debug("Service that gets data model by id");
		
		DataModel dataModel = this.dataModelDao.read(dataModelId);
		if (dataModel == null) throw new NoSuchElementException("DataModel [" + dataModelId + "] does not exist");
		return dataModel;
	}
	
	@Override
	@Transactional
	public List<DataModel> getAllDataModels() throws ServiceException {
		logger.debug("Service that gets all data models");
		try {
			return this.dataModelDao.getAll();
		} catch (Exception e) {
			throw new ServiceException("An error occurred when retrieving data models", e);
		}
	}
	
	@Override
	@Transactional
	public void deleteDataModel(Integer dataModelId) throws ServiceException {
		logger.debug("Service that deletes data model");
		
		DataModel dataModel = this.dataModelDao.read(dataModelId);
		
		if (dataModel == null) throw new NoSuchElementException("DataModel [" + dataModelId + "] does not exist");
		if (dataModel.getDataCollections().size() > 0) throw new ServiceException("A data model cannot be deleted when the data collection period is active");
		
		try {
			this.dataModelDao.delete(dataModel);
		} catch (Exception e) {
			throw new ServiceException("An error occurred while deleting the data model " + dataModel.getLabel(), e);
		}
	}

	@Override
	@Transactional(rollbackOn = ServiceException.class)
	public boolean isDataCollectionPeriodActiveByDataModelId(Integer id) throws ServiceException {
		return !this.dataModelDao.read(id).getDataCollections().isEmpty();
	}
}