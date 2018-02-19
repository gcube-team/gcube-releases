package gr.cite.regional.data.collection.dataaccess.services;

import gr.cite.regional.data.collection.dataaccess.daos.DomainDao;
import gr.cite.regional.data.collection.dataaccess.entities.Domain;
import gr.cite.regional.data.collection.dataccess.exceptions.ServiceException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class DomainServiceImpl implements DomainService {
	private static final Logger logger = LogManager.getLogger(DomainServiceImpl.class);
	private DomainDao domainDao;
	
	@Autowired
	public DomainServiceImpl(DomainDao domainDao) {
		this.domainDao = domainDao;
	}
	
	@Override
	@Transactional(rollbackOn = ServiceException.class)
	public Domain addDomain(Domain domain) throws ServiceException {
		try {
			return this.domainDao.create(domain);
		} catch (Exception e) {
			throw new ServiceException("Error on creating domain [" + domain.getLabel() + "]", e);
		}
	}
	
	@Override
	public Domain updateDomain(Domain domain) {
		return null;
	}
	
	@Override
	@Transactional
	public Domain getDomain(Integer domainId) {
		return this.domainDao.read(domainId);
	}
	
	@Override
	@Transactional
	public List<Domain> getDomains() {
		return this.domainDao.getAll();
	}
	
	@Override
	@Transactional(rollbackOn = ServiceException.class)
	public Domain getOrCreateDomainByLabel(String domainLabel) throws ServiceException {
		logger.debug("Service that gets domain by label");
		
		List<Domain> domains;
		try {
			domains = this.domainDao.getDomainByLabel(domainLabel);
		} catch (Exception e) {
			throw new ServiceException("Error on retrieving domain [" + domainLabel + "]", e);
		}
		
		if (domains == null || domains.isEmpty()) {
			Domain newDomain = new Domain();
			newDomain.setLabel(domainLabel);
			
			return addDomain(newDomain);
		}
		
		if (domains.size() > 1) {
			throw new ServiceException("Retrieved more than one domains with the same label");
		}
		
		return domains.get(0);
	}
	
	@Override
	public Domain deleteDomain(Integer domainId) {
		return null;
	}
	
}
