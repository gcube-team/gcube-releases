package gr.cite.regional.data.collection.dataaccess.services;

import gr.cite.regional.data.collection.dataaccess.entities.Cdt;
import gr.cite.regional.data.collection.dataaccess.entities.DataCollection;
import gr.cite.regional.data.collection.dataaccess.entities.DataSubmission;
import gr.cite.regional.data.collection.dataccess.exceptions.ServiceException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface CdtService {
	public void createCdtTable(DataCollection dataCollection) throws ServiceException;
	public Cdt addCdt(Cdt cdt) throws ServiceException;
	public List<Cdt> addCdts(List<Cdt> cdts) throws ServiceException;
	public Cdt updateCdt(Cdt cdt) throws ServiceException;
	public List<Cdt> updateCdts(List<Cdt> cdts) throws ServiceException;
	public void deactivateCdtsOfDataSubmission(DataSubmission dataSubmission) throws ServiceException;
	public Cdt addCdts(Set<Cdt> cdt) throws ServiceException;
	public Cdt getCdt(UUID id, Integer dataCollectionid) throws ServiceException;
	public List<Cdt> getCdtByDataSubmission(DataSubmission dataSubmission) throws ServiceException;
	public void deleteCdtsOfDataCollection(Integer dataCollectionId);
}
