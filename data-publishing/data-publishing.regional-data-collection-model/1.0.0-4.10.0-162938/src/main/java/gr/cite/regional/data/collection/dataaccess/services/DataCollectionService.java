package gr.cite.regional.data.collection.dataaccess.services;

import gr.cite.regional.data.collection.dataaccess.entities.DataCollection;
import gr.cite.regional.data.collection.dataaccess.entities.DataSubmission;
import gr.cite.regional.data.collection.dataaccess.entities.Domain;
import gr.cite.regional.data.collection.dataccess.exceptions.ServiceException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchOperationException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTaskException;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface DataCollectionService {
	public DataCollection addDataCollection(DataCollection dataCollection) throws ServiceException;
	public DataCollection updateDataCollection(DataCollection dataCollection) throws ServiceException;
	public DataCollection getDataCollection(Integer id);
	public List<DataCollection> getDataCollectionsByDomain(String domainLabel) throws ServiceException;
	public List<DataCollection> getDataCollectionByLabel(String label) throws ServiceException;
	public List<DataCollection> getAllDataCollections();
	public void deleteDataCollection(Integer id) throws ServiceException;

}
