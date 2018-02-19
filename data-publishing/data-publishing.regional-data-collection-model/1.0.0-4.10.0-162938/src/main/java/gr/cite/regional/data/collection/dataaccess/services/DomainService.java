package gr.cite.regional.data.collection.dataaccess.services;

import gr.cite.regional.data.collection.dataaccess.entities.Domain;
import gr.cite.regional.data.collection.dataccess.exceptions.ServiceException;

import java.util.List;

public interface DomainService {
	public Domain addDomain(Domain domain) throws ServiceException;
	public Domain updateDomain(Domain domain);
	public List<Domain> getDomains();
	public Domain getDomain(Integer domainId);
	public Domain getOrCreateDomainByLabel(String domainLabel) throws ServiceException;
	public Domain deleteDomain(Integer domainId);
}
