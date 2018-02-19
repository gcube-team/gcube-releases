package gr.cite.regional.data.collection.dataaccess.services;

import gr.cite.regional.data.collection.dataaccess.entities.DataModel;
import gr.cite.regional.data.collection.dataccess.exceptions.ServiceException;

import java.util.List;

public interface DataModelService {
	public DataModel addDataModel(DataModel dataModel) throws ServiceException;
	public DataModel updateDataModel(DataModel dataModel);
	public DataModel getDataModel(Integer dataModelId);
	public List<DataModel> getAllDataModels() throws ServiceException;
	public void deleteDataModel(Integer dataModelId) throws ServiceException;
	public boolean isDataCollectionPeriodActiveByDataModelId(Integer id) throws ServiceException;
}
