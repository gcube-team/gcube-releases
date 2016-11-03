package gr.cite.geoanalytics.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import gr.cite.gaap.datatransferobjects.TenantListInfo;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
import gr.cite.geoanalytics.dataaccess.entities.tenant.Tenant;
import gr.cite.geoanalytics.dataaccess.entities.tenant.TenantActivation;
import gr.cite.geoanalytics.dataaccess.entities.tenant.dao.TenantActivationDao;
import gr.cite.geoanalytics.dataaccess.entities.tenant.dao.TenantDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TenantManager {
	private static final Logger log = LoggerFactory.getLogger(TenantManager.class);
	
	private TenantDao tenantDao = null;
	private TenantActivationDao tenantActivationDao = null;
	
	@Inject
	public void setTenantDao(TenantDao tenantDao) {
		this.tenantDao = tenantDao;
	}
	
	@Inject
	public void setTenantActivationDao(TenantActivationDao tenantActivationDao) {
		this.tenantActivationDao = tenantActivationDao;
	}
	
	private void getTenantActivationDetails(TenantActivation tenantActivation) {
		if(tenantActivation.getActivationConfig() != null) tenantActivation.getActivationConfig().length();
		tenantActivation.getCreator().getName();
		if(tenantActivation.getCreator().getTenant() != null) tenantActivation.getCreator().getTenant().getName();
		if(tenantActivation.getTenant() != null) tenantActivation.getTenant().getName();
		if(tenantActivation.getShape() != null) tenantActivation.getShape().getName();
	}
	
	@Transactional(readOnly = true)
	public Tenant findById(String id) throws Exception {
		return tenantDao.read(UUID.fromString(id));
	}
	
	@Transactional(readOnly = true)
	public Tenant findByName(String name) throws Exception {
		List<Tenant> res =  tenantDao.findByName(name);
//		if(res != null && res.size() > 1) throw new Exception("More than one customers with name \"" + name + "\" were found");
		if(res == null || res.isEmpty()) return null;
		return res.get(0);
	}
	
	@Transactional(readOnly = true)
	public List<Tenant> all() throws Exception {
		return tenantDao.getAll();
	}
	
	@Transactional(readOnly = true)
	public List<String> listTenants() throws Exception {
		return tenantDao.listNames();
	}
	
	@Transactional(readOnly = true)
	public List<String> listActiveTenants() throws Exception {
		List<String> res = new ArrayList<String>();
		for(Tenant tenant : all()) {
			List<TenantActivation> cas = tenantActivationDao.findActive(tenant);
			if(cas != null && !cas.isEmpty())
				res.add(tenant.getName());
		}
		return res;
	}
	
	@Transactional(readOnly = true)
	public List<String> listByCode(String code) throws Exception {
		return tenantDao.listNamesByCode(code);
	}
	
	@Transactional(readOnly = true)
	public List<Tenant> getByCode(String code) throws Exception {
		return tenantDao.findByCode(code);
	}
	
	
	
	@Transactional(readOnly = true)
	public boolean isActiveForAll(Tenant tenant) throws Exception {
		List<TenantActivation> tenantActivations = tenantActivationDao.findActive(tenant);
		if(tenantActivations == null || tenantActivations.isEmpty()) return false;
		for(TenantActivation ca : tenantActivations)
			if(ca.getShape() == null) return true;
		return false;
	}
	
	@Transactional(readOnly = true)
	public List<Shape> getGrantedShapes(Tenant tenant) throws Exception {
		List<TenantActivation> tenantActivations = tenantActivationDao.findActive(tenant);
		List<Shape> shapes = new ArrayList<Shape>();
		if(tenantActivations == null || tenantActivations.isEmpty()) return shapes;
		for(TenantActivation tenantActivation : tenantActivations) {
			if(tenantActivation.getShape() != null)
				shapes.add(tenantActivation.getShape());
		}
		return shapes;
	}
	
	@Transactional(readOnly = true)
	public List<TenantActivation> getActivations(Tenant tenant, boolean loadDetails) throws Exception {
		List<TenantActivation> tenantActivations =  tenantActivationDao.findAll(tenant);
		if(loadDetails) {
			for(TenantActivation tenantActivation : tenantActivations)
				getTenantActivationDetails(tenantActivation);
		}
		return tenantActivations;
	}
	
	@Transactional
	public void addActivationsToTenant(Tenant tenant, List<TenantActivation> tenantActivations) throws Exception {
		for(TenantActivation tenantActication : tenantActivations) {
			if(tenantActication.getEnd().getTime() < new Date().getTime()) {
				log.error("Activation addition to customer " + tenant.getName() + " failed because end date has already elapsed");
				throw new Exception("End date is already due");
			}
			if(tenantActication.getStart().getTime() > tenantActication.getEnd().getTime()) {
				log.error("Activation addition to customer " + tenant.getName() + " failed because start date is greater than end date");
				throw new Exception("Start date greater than end date");
			}
			tenantActication.setIsActive(true);
			tenantActivationDao.create(tenantActication);
		}
	}
	
	@Transactional
	public void updateActivation(TenantActivation tenantActivation) throws Exception {
		if(tenantActivation.getId() == null) throw new Exception("Null customer activation identifier");
		TenantActivation ex = tenantActivationDao.read(tenantActivation.getId());
		if(ex == null) throw new Exception("Tenant activation " + tenantActivation.getId() + " not found");
		
		ex.setActivationConfig(tenantActivation.getActivationConfig());
		if(tenantActivation.getEnd().getTime() < new Date().getTime())
			ex.setIsActive(false);
		else
			ex.setIsActive(tenantActivation.getIsActive());
		if(tenantActivation.getStart().getTime() > tenantActivation.getEnd().getTime())
		{
			log.error("Activation update failed because start date is greater than end date");
			throw new Exception("Start date greater than end date");
		}
		ex.setStart(tenantActivation.getStart());
		ex.setEnd(tenantActivation.getEnd());
		
		if(tenantActivation.getShape() != null)
			ex.setShape(tenantActivation.getShape());
	}
	
	@Transactional(readOnly = true)
	public List<TenantListInfo> searchTenants(List<String> terms, boolean active, Date start, Date end) throws Exception {
		boolean searchWithinPeriod = start != null && end != null;
		List<String> activeSet = listActiveTenants();
		List<Tenant> tenants = tenantDao.searchByName(terms);
		
		List<TenantListInfo> res = new ArrayList<TenantListInfo>();
		for(Tenant tenant : tenants) {
			boolean cIsActive = false;
			for(String ac : activeSet) {
				if(ac.equals(tenant.getName())) {
					cIsActive = true;
					break;
				}
			}
			
			if(!active || cIsActive)  {
				if(searchWithinPeriod) {
					if(!tenantActivationDao.findWithinActive(tenant, start, end).isEmpty())
						res.add(new TenantListInfo(tenant.getName(), tenant.getEmail(), tenant.getCode(), cIsActive));
				}
				else res.add(new TenantListInfo(tenant.getName(), tenant.getEmail(), tenant.getCode(), cIsActive));
			}
		}
		return res;
	}
	
	@Transactional
	public void update(Tenant tenant, String originalName, boolean create) throws Exception {
		if(create) {
			Tenant ex = null;
			if(tenant.getId() != null) ex = findById(tenant.getId().toString());
			else ex = findByName(tenant.getName());
			if(ex != null)
			{
				log.error("Tenant " + tenant.getName() + " already exists");
				throw new Exception("Tenant " + tenant.getName() + " already exists");
			}
			
			tenantDao.create(tenant);
		}else {
			Tenant ex = null;
			if(tenant.getId() != null) ex = findById(tenant.getId().toString());
			else ex = findByName(originalName);
			
			if(ex == null)
			{
				log.error("Tenant " + originalName + " does not exist");
				throw new Exception("Tenant " + originalName + " does not exist");
			}
			tenant.setId(ex.getId());
			tenant.setCreationDate(ex.getCreationDate());
			tenant.setCreator(ex.getCreator());
			
			tenantDao.update(tenant);
		}
	}
	
	@Transactional(rollbackFor={Exception.class})
	public void deleteTenants(List<String> tenants) throws Exception {
		for(String tenant : tenants) {
			Tenant cus = findByName(tenant);
			for(TenantActivation tenantActivations : getActivations(cus, false)) //First delete all activation entries of the customer
				tenantActivationDao.delete(tenantActivations);
			if(tenant == null) throw new Exception("Tenant " + tenant + " was not found");
			tenantDao.delete(cus);
		}
	}
	
	@Transactional(rollbackFor={Exception.class})
	public void deleteActivations(List<String> tenantActivationsId) throws Exception {
		for(String tenantActivationId : tenantActivationsId) {
			TenantActivation tenantActivation = tenantActivationDao.read(UUID.fromString(tenantActivationId));
			if(tenantActivation == null) throw new Exception("Tenant activation " + tenantActivationId + " was not found");
			tenantActivationDao.delete(tenantActivation);
		}
	}
}
