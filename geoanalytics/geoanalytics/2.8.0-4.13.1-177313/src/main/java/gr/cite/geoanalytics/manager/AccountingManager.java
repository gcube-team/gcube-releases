package gr.cite.geoanalytics.manager;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import gr.cite.geoanalytics.dataaccess.entities.accounting.Accounting;
import gr.cite.geoanalytics.dataaccess.entities.accounting.dao.AccountingDao;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.tenant.Tenant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountingManager {
	private static final Logger log = LoggerFactory.getLogger(AccountingManager.class);
	
	private TenantManager tenantManager;
	private AccountingDao accountingDao;
	private PrincipalManager principalManager;
	
	@Inject
	public void setPrincipalManager(PrincipalManager principalManager) {
		this.principalManager = principalManager;
	}
	
	@Inject
	public void setCustomerManager(TenantManager tenantManager) {
		this.tenantManager = tenantManager;
	}
	
	@Inject
	public void setAccountingDao(AccountingDao accountingDao) {
		this.accountingDao = accountingDao;
	}
	
	private void getAccountingDetails(Accounting accounting) {
		accounting.getCreator().getPrincipalData().getFullName();
		accounting.getTenant().getName();
		if(accounting.getPrincipal() != null) accounting.getPrincipal().getName();
	}
	
	private void getAccountingDetails(List<Accounting> acc) {
		for(Accounting a : acc)
			getAccountingDetails(a);
	}
	
	@Transactional(readOnly = true)
	public Accounting findAccountingById(String id, boolean loadDetails) throws Exception {
		Accounting a = accountingDao.read(UUID.fromString(id));
		if(loadDetails) getAccountingDetails(a);
		return a;
	}
	
	@Transactional(readOnly = true)
	public List<Accounting> allAccounting(boolean loadDetails) throws Exception {
		List<Accounting> res = accountingDao.getAll();
		if(loadDetails) getAccountingDetails(res);
		return res;
	}
	
	@Transactional(readOnly = true)
	public List<Accounting> validAccounting(boolean loadDetails) throws Exception {
		List<Accounting> res = accountingDao.validAccounting();
		if(loadDetails) getAccountingDetails(res);
		return res;
	}
	
	@Transactional(readOnly = true)
	public float aggregateAccountingOfCustomer(String customer, Date from, Date to) throws Exception {
		Tenant c = tenantManager.findByName(customer);
		if(c == null) throw new Exception("Customer " + customer + " not found");
		return accountingDao.aggregateByCustomer(c, from, to);
	}
	
	@Transactional(readOnly = true)
	public float aggregateAccountingOfUser(String tenant, String user, Date from, Date to) throws Exception {
		Tenant c = tenantManager.findByName(tenant);
		if(c == null) throw new Exception("Customer " + tenant + " not found");
		Principal principal = principalManager.findActivePrincipalByNameAndTenant(user, tenant);
		if(principal == null) throw new Exception("User " + user + " not found");
		return accountingDao.aggregateByUser(principal, from, to);
	}

	@Transactional
	public void updateAccounting(Accounting a, boolean create) throws Exception {
		if(create) {
			accountingDao.create(a);
		}else {
			Accounting ex = null;
			ex = findAccountingById(a.getId().toString(), false);
			
			if(ex == null) {
				log.error("Accounting " + a.getId() + " does not exist");
				throw new Exception("Accounting " + a.getId() + " does not exist");
			}
			
			ex.setDate(a.getDate());
			ex.setType(a.getType());
			ex.setUnits(a.getUnits());
			ex.setIsValid(a.getUnits() > 0.0f);
			if(ex.getReferenceData() != null) ex.setReferenceData(a.getReferenceData());
			
			accountingDao.update(ex);
		}
	}
	
	@Transactional(rollbackFor={Exception.class})
	public void deleteAccounting(List<String> accounting) throws Exception {
		for(String a : accounting) {
			Accounting acc = findAccountingById(a, false);
			if(acc == null) throw new Exception("Accounting " + a + " not found");
					
			accountingDao.delete(acc);
		}
	}
}
