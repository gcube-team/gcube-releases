package gr.cite.geoanalytics.manager;

import java.io.StringWriter;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import gr.cite.geoanalytics.dataaccess.entities.auditing.Auditing;
import gr.cite.geoanalytics.dataaccess.entities.auditing.AuditingData;
import gr.cite.geoanalytics.dataaccess.entities.auditing.dao.AuditingDao;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.auditing.Auditing.AuditingType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditingManager {
	private static final Logger log = LoggerFactory.getLogger(AuditingManager.class);
	
	private AuditingDao auditingDao;
	private PrincipalManager principalManager;
	
	private JAXBContext auditingCtx = null;
	
	public AuditingManager() throws Exception {
		this.auditingCtx = JAXBContext.newInstance(AuditingData.class);
	}
	
	@Inject
	public void setPrincipalManager(PrincipalManager principalManager) {
		this.principalManager = principalManager;
	}
	
	@Inject
	public void setAuditingDao(AuditingDao auditingDao) {
		this.auditingDao = auditingDao;
	}
	
	private void getAuditingDetails(Auditing a) {
		a.getCreator().getPrincipalData().getFullName();
		if(a.getTenant() != null) a.getTenant().getName();
		if(a.getPrincipal() != null) a.getPrincipal().getName();
	}
	
	private void getAuditingDetails(List<Auditing> acc) {
		for(Auditing a : acc)
			getAuditingDetails(a);
	}
	
	@Transactional(readOnly = true)
	public Auditing findAuditingById(String id, boolean loadDetails) throws Exception {
		Auditing a = auditingDao.read(UUID.fromString(id));
		if(loadDetails) getAuditingDetails(a);
		return a;
	}
	
	private String createAuditData(AuditingType type, String data, String address) {
		if(type != AuditingType.LastUnsuccessfulUserLogin &&
				type != AuditingType.LastUserPasswordRequest)
			return data;
		String[] parts = data.split(":");
		
		Set<String> addrs = new HashSet<String>();
		for(int i=1; i<parts.length; i++)
			addrs.add(parts[i]);
		addrs.add(address);
		
		StringBuilder d = new StringBuilder();
		d.append(parts[0]);
		for(String addr : addrs)
		{
			d.append(":");
			d.append(addr);
		}
		return d.toString();
	}
	
	private void auditByType(AuditingType type, String data, UUID entityId, String entityType, String address, Principal principal) throws Exception {
		Auditing auditingEntry = auditingDao.findByTypeAndUser(type, principal);
		boolean create = false;
		
		if(auditingEntry == null) {
			create = true;
			auditingEntry = new Auditing();
			auditingEntry.setCreator(principalManager.getSystemPrincipal());
			auditingEntry.setTenant(principal.getTenant());
			auditingEntry.setType(type);
			auditingEntry.setPrincipal(principal);
		}
		Date now = new Date();
		auditingEntry.setDate(now);
		
		AuditingData ad = new AuditingData();
		ad.setData(createAuditData(type, data, address));
		ad.setEntityId(entityId);
		ad.setEntityType(entityType);
		ad.setTimestamp(now.getTime());
		ad.setType(type);
		
		Marshaller m = auditingCtx.createMarshaller();
		StringWriter sw = new StringWriter();
		m.marshal(ad, sw);
		
		auditingEntry.setData(sw.toString());
		
		if(create) auditingDao.create(auditingEntry);
		else auditingDao.update(auditingEntry);
		
	}
	
	@Transactional
	public void auditLastAction(String action, UUID entityId, String entityType, Principal principal) throws Exception {
		auditByType(AuditingType.LastUserAction, action, entityId, entityType, null, principal);
	}
	
	@Transactional
	public void auditLogin(Principal principal) throws Exception {
		auditByType(AuditingType.LastUserLogin, null, null, null, null, principal);
	}
	
	@Transactional
	public void auditUnsuccessfulLogin(Principal principal, int times, String address) throws Exception {
		auditByType(AuditingType.LastUnsuccessfulUserLogin, new Integer(times).toString(), null, null, address, principal); 
	}
	
	@Transactional
	public void auditPasswordRequest(Principal principal, int times, String address) throws Exception {
		auditByType(AuditingType.LastUserPasswordRequest, new Integer(times).toString(), null, null, address, principal);
	}
	
	@Transactional(readOnly = true)
	public List<Auditing> allAuditing(boolean loadDetails) throws Exception {
		List<Auditing> res = auditingDao.getAll();
		if(loadDetails) getAuditingDetails(res);
		return res;
	}
	
	@Transactional
	public void updateAuditing(Auditing a, boolean create) throws Exception {
		if(create) {
			auditingDao.create(a);
		}else {
			Auditing ex = null;
			ex = findAuditingById(a.getId().toString(), false);
			
			if(ex == null) {
				log.error("Auditing" + a.getId() + " does not exist");
				throw new Exception("Auditing" + a.getId() + " does not exist");
			}
			
			ex.setDate(a.getDate());
			ex.setType(a.getType());
			if(ex.getData() != null) ex.setData(a.getData());
			
			auditingDao.update(ex);
		}
	}
	
	@Transactional(rollbackFor={Exception.class})
	public void deleteAuditing(List<String> auditing) throws Exception {
		for(String a : auditing) {
			Auditing acc = findAuditingById(a, false);
			if(acc == null) throw new Exception("Auditing " + a + " not found");
					
			auditingDao.delete(acc);
		}
	}
}
