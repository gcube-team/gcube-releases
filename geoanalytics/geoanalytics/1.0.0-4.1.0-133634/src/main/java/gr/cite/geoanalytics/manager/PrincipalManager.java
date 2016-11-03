package gr.cite.geoanalytics.manager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.management.RuntimeErrorException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gr.cite.gaap.datatransferobjects.PrincipalProjectInfo;
import gr.cite.gaap.datatransferobjects.PrincipalTenantPair;
import gr.cite.gaap.datatransferobjects.UserinfoObject;
import gr.cite.gaap.datatransferobjects.user.RoleMessengerUpdate;
import gr.cite.geoanalytics.dataaccess.dao.UUIDGenerator;
import gr.cite.geoanalytics.dataaccess.entities.ActiveStatus;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.principal.PrincipalClass;
import gr.cite.geoanalytics.dataaccess.entities.principal.PrincipalData;
import gr.cite.geoanalytics.dataaccess.entities.principal.PrincipalMembership;
import gr.cite.geoanalytics.dataaccess.entities.security.principal.dao.PrincipalDao;
import gr.cite.geoanalytics.dataaccess.entities.security.principal.dao.PrincipalDataDao;
import gr.cite.geoanalytics.dataaccess.entities.security.principal.dao.PrincipalMembershipDao;
import gr.cite.geoanalytics.dataaccess.entities.security.principal.dao.PrincipalProjectDao;
import gr.cite.geoanalytics.dataaccess.entities.security.principal.dao.PrincipalProjectInfoDao;
import gr.cite.geoanalytics.dataaccess.entities.tenant.Tenant;
import gr.cite.geoanalytics.dataaccess.entities.tenant.TenantActivation;
import gr.cite.geoanalytics.dataaccess.entities.tenant.dao.TenantActivationDao;
import gr.cite.geoanalytics.dataaccess.entities.tenant.dao.TenantDao;

@Service
public class PrincipalManager {

	private PrincipalDao principalDao;
	private TenantDao tenantDao;
	private TenantActivationDao tenantActivationDao;
	private PrincipalDataDao principalDataDao;
	private PrincipalProjectDao principalProjectDao;
	private PrincipalMembershipDao principalMembershipDao;
	
	@Inject
	public void setPrincipalMembershipDao(PrincipalMembershipDao principalMembershipDao) {
		this.principalMembershipDao = principalMembershipDao;
	}

	@Inject
	public void setPrincipalProjectDao(PrincipalProjectDao principalProjectDao) {
		this.principalProjectDao = principalProjectDao;
	}

	@Inject
	public void setPrincipalDataDao(PrincipalDataDao pricipalDataDao) {
		this.principalDataDao = pricipalDataDao;
	}
	
	@Inject
	public void setTenantActivationDao(TenantActivationDao tenantActivationDao) {
		this.tenantActivationDao = tenantActivationDao;
	}
	
	@Inject
	public void setTenantDao(TenantDao tenantDao) {
		this.tenantDao = tenantDao;
	}
	
	@Inject
	public void setPrincipalDao(PrincipalDao principalDao) {
		this.principalDao = principalDao;
	}
	
	@Transactional(readOnly=true)
	public List<Principal> getPrincipals(){
		return this.principalDao.getAll();
	}
	
	@Transactional(readOnly=true) 
	public Principal getPrincipalById(UUID principalId){
		return this.principalDao.read(principalId);
	}
	
	@Transactional(rollbackFor=Exception.class)
	public Principal createPrincipal(Principal principal){
		return this.principalDao.create(principal);
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void deletePrincipal(Principal principal){
		this.principalDao.delete(principal);
	}
	
	@Transactional(rollbackFor=Exception.class)
	public Principal updatePrincipal(Principal principal){
		return this.principalDao.update(principal);
	}
	
	@Transactional(readOnly=true)
	public Principal getPrincipal(UUID principalId){
		return this.principalDao.read(principalId);
	}
	
	@Transactional(readOnly=true)
	public long countPrincipals(){
		return this.principalDao.count();
	}
	
	@Transactional(readOnly=true)
	public List<Principal> getActivePrincipals(){
		return this.principalDao.findActivePrincipals();
	}
	
	@Transactional(readOnly=true)
	public Principal getActivePrincipalByName(String name){
		return this.principalDao.findActivePrincipalByName(name);
	}
	
	@Transactional(readOnly=true)
	public Principal getPrincipalByNameAndActivity(String name, ActiveStatus activeStatus){
		return this.principalDao.findPrincipalByNameAndActivityStatus(name, activeStatus);
	}
	
	@Transactional(readOnly=true)
	public Principal getSystemPrincipal(){
		return this.principalDao.systemPrincipal();
	}
	
	@Transactional(readOnly=true)
	public List<Principal> getPrincipalsByClassTypeAndActivity(PrincipalClass principalType, ActiveStatus activeStatus){
		return this.principalDao.findByClass(principalType);
	}
	
	@Transactional(readOnly=true)
	public List<Principal> getActivePrincipalsByClassType(PrincipalClass principalType){
		return this.principalDao.findByClass(principalType);
	}
	
	@Transactional(readOnly=true)
	public Principal findPrincipalByNameTenantAndActiveStatus(String principalName, String tenantName, ActiveStatus activeStatus){
		return this.principalDao.findPrincipalByNameTenantAndActiveStatus(principalName, tenantName, activeStatus);
	}
	
	@Transactional(readOnly=true)
	public Principal findActivePrincipalByNameAndTenant(String principalName, String tenantName){
		return this.principalDao.findActivePrincipalByNameAndTenant(principalName, tenantName);
	}
	
	@Transactional(readOnly=true)
	public Principal findPrincipalByNameAndTenant(String principalName, String tenantName){
		return this.principalDao.findPrincipalByNameAndTenant(principalName, tenantName);
	}
	
	@Transactional(readOnly=true)
	public Boolean isActiveStatusByActiveStatusAndName(String principalName, ActiveStatus activeStatus){
		Boolean status;
		Principal principal = principalDao.findPrincipalByNameAndActivityStatus(principalName, activeStatus);
		if (principal == null){
			status = false;
		}else{
			status = true;
		}
		return status;
		
	}
	
	@Transactional(rollbackFor=Exception.class)
	public Principal create(Principal principal, PrincipalData principalData){
		this.principalDataDao.create(principalData);
		return this.principalDao.create(principal);
	}
	
	@Transactional(rollbackFor=Exception.class)
	public Principal setActivityStatus(Principal detachedPrincipal, ActiveStatus activeStatus){
		Principal principal = this.principalDao.read(detachedPrincipal.getId());
		principal.setIsActive(activeStatus);
		principal = this.principalDao.create(principal);
		return principal;
	}
	
	@Transactional(readOnly=true)
	public List<String> listActivePrincipalNames(){
		return this.listActivePrincipalNames();
	}
	
	@Transactional(readOnly = true)
	public List<String> listPrincipalNamesByTenantName(String tenantName, boolean activeTenant) throws Exception {
		List<String> principalNames = new ArrayList<String>();
		List<Tenant> tenants = tenantDao.findByName(tenantName);
		for(Tenant tenant : tenants) {
			if(activeTenant) {
				List<TenantActivation> tenantActivations = tenantActivationDao.findActive(tenant);
				if(tenantActivations != null && !tenantActivations.isEmpty())
					principalNames.addAll(principalDao.listPrincipalNamesByTenant(tenant));
			}else principalNames.addAll(principalDao.listPrincipalNamesByTenant(tenant));
		}
		return principalNames;
	}
	
	@Transactional(readOnly=true)
	public List<String> listPrincipalNames(){
		return this.principalDao.listPrincipalNames();
	}
	
	@Transactional(readOnly = true)
	public List<String> searchPrincipalNamesOfTenant(List<String> userNames, String tenantNames, boolean activeCustomer) throws Exception {
		List<String> principalNames = new ArrayList<String>();
		List<Tenant> tenants = tenantDao.findByName(tenantNames);
		for(Tenant tenant : tenants) {
			if(activeCustomer) {
				List<TenantActivation> cas = tenantActivationDao.findActive(tenant);
				if(cas != null && !cas.isEmpty())
					principalNames.addAll(principalDao.searchByPrincipalNamesAndTenant(userNames, tenant));
			}else principalNames.addAll(principalDao.searchByPrincipalNamesAndTenant(userNames, tenant));
		}
		return principalNames;
	}

	@Transactional(rollbackFor=Exception.class)
	public void update(Principal principal) {
		Principal principalFormer = this.getPrincipal(principal.getId());
		if (principal.getPrincipalData() == null){
			throw new RuntimeErrorException(null, "PrincipalData is null");
		}
		PrincipalData principalDataFormer = this.principalDataDao.read(principal.getPrincipalData().getId());
		if (principalDataFormer == null || principalFormer == null){
			throw new RuntimeErrorException(null ,"Either principal or principalData are null");
		}
		principalDataFormer.setCredential(principal.getPrincipalData().getCredential());
		principalDataFormer.setEmail(principal.getPrincipalData().getEmail());
		principalDataFormer.setExpirationDate(principal.getPrincipalData().getExpirationDate());
		principalDataFormer.setFullName(principal.getPrincipalData().getFullName());
		principalDataFormer.setInitials(principal.getPrincipalData().getInitials());
		
		principalFormer.setCreationDate(principalFormer.getCreationDate());
		principalFormer.setCreator(principal.getCreator());
		principalFormer.setName(principalFormer.getName());
		principalFormer.setIsActive(principal.getIsActive());
		
		principalFormer.setPrincipalData(principalDataFormer);
		
		this.principalDataDao.update(principalDataFormer);
		this.principalDao.update(principalFormer);
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void deleteBySystemNameAndCustomer(List<PrincipalTenantPair> principalTenantsPairs) throws Exception {
		for(PrincipalTenantPair principalTenantPair : principalTenantsPairs) {
			Principal principal;
			if(principalTenantPair.getTenant() != null) {
				principal = this.findPrincipalByNameAndTenant(principalTenantPair.getPrincipal(), principalTenantPair.getTenant()); 
				if(principal == null) throw new Exception("Principal " + principalTenantPair.getPrincipal() + " of tenant " + principalTenantPair.getTenant() + " was not found");
			}else {
				principal = this.getPrincipalByNameAndActivity(principalTenantPair.getPrincipal(), null);
				if(principal == null) throw new Exception("Principal " + principalTenantPair.getPrincipal() + " was not found");
				if(principal.getTenant() != null) throw new Exception("Invalid Principal identity");
			}
			principalDao.delete(principal);
		}
	}
	
	@SuppressWarnings("deprecation")
	@Transactional(rollbackFor=Exception.class)
	public Principal createPrincipalIfNotExists(UserinfoObject psm, Tenant tenant) throws Exception {
		if(psm == null || psm.getFullname() == null){
			return null;
		}
		Principal principal = principalDao.findPrincipalByNameAndActivityStatus(psm.getFullname(), null);
		
		try{
			if(principal == null){
				principal = new Principal();
				PrincipalData principalDataData = new PrincipalData();
				principalDataData.setFullName(psm.getFullname());
				principalDataData.setInitials(psm.calculateInitials());
				principalDataData.setEmail(psm.getEmail());
				principal.setCreationDate(Calendar.getInstance().getTime());
				principal.setLastUpdate(Calendar.getInstance().getTime());
				principalDataData.setExpirationDate(new Date(3000-1900,12,31));
				principal.setName(psm.getFullname());
				principal.setCreator(principal);
				principal.setClassId(PrincipalClass.ITEM.classCode());
				principal.setIsActive(ActiveStatus.ACTIVE);
				
				principal.setPrincipalData(principalDataData);
				
				Principal sysUser = this.getSystemPrincipal();
				PrincipalMembership pm = null;
				
				List<Principal> roles = this.principalMembershipDao.findRolesByPrincipal(sysUser);
				for(Principal role: roles){
					if(role.getName() != null && role.getName().equals("User")){
						pm = new PrincipalMembership();
						pm.setGroup(role);
						pm.setMember(principal);
						pm.setId(UUID.randomUUID());
						pm.setCreationDate(Calendar.getInstance().getTime());
						pm.setLastUpdate(Calendar.getInstance().getTime());
						principal.getGroupsPrincipal().add(pm);
						principalDataData.setCredential(sysUser.getPrincipalData().getCredential());
						break;
					}
				}
				
				principal = this.create(principal, principalDataData);
				
				if(pm != null){
					this.principalDao.update(principal);
				}
				
//				Principal role = principalDao.findPrincipalByNameAndActivityStatus("User", ActiveStatus.ACTIVE);
//					
//					PrincipalMembership principalMembership = new PrincipalMembership();
//					principalMembership.setMember(principal);
//					principalMembership.setGroup(role);
//				
//				principalDao.update(principal);
				
				if(tenant == null){
					tenant = new Tenant();
					tenant.setCreator(principal);
					tenant.setName(psm.getTenant());
					tenant.setCode("");
					tenantDao.create(tenant);
					
				}
			}
			if(tenant == null){
				tenant = new Tenant();
				tenant.setCreator(principal);
				tenant.setName(psm.getTenant());
				tenant.setCode("");
				if(principal.getTenant() == null){
					principal.setTenant(tenant);
				}
				tenantDao.create(tenant);	
			}
			if(principal != null && principal.getTenant() == null && tenant != null){
				principal.setTenant(tenant);
				principalDao.update(principal);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return principal;
	}
	
	@Transactional(rollbackFor=Exception.class)
	public List<PrincipalProjectInfoDao> collectInfoOfActivePrincipals(String creatorName) throws Exception {
		List<PrincipalProjectInfoDao> principalsInfo = new ArrayList<PrincipalProjectInfoDao>();
		
//		long startTime = System.nanoTime();
//		List<Principal> principals = this.getActivePrincipalsByClassType(PrincipalClass.ITEM);
//		long endTime = System.nanoTime();
//		System.out.println("Total duration: "+ (endTime - startTime));
//		
//		long startTimeXML = System.nanoTime();
//		for(Principal p : principals){
//			if(p.getId().toString().equals(UUIDGenerator.systemUserUUID().toString()) || 
//					p.getId().toString().equals(creatorUUID) || p.getName().equals("")) {
//				continue;
//			}
//			PrincipalProjectInfo ppi = new PrincipalProjectInfo();
//			ppi.setName(p.getName());
//			if(p.getPrincipalData() != null){
//				ppi.setEmail(p.getPrincipalData().getEmail());
//			}else{
//				ppi.setEmail("");
//			}
//			
//			ppi.setNumOfProjects(p.getProjectsParticipant().size());
//			principalsInfo.add(ppi);
//		}
//		long endTimeXML = System.nanoTime();
		principalsInfo = principalDao.findByClassReturnsPrincipalProjectInfo(PrincipalClass.ITEM, ActiveStatus.ACTIVE);
//		System.out.println(principalDao.test(PrincipalClass.ITEM, ActiveStatus.ACTIVE));
		
//		System.out.println("Total duration: "+ (endTimeXML - startTimeXML));
		String adminName = this.getSystemPrincipal().getName();
		List<PrincipalProjectInfoDao> ret = principalsInfo
				.stream()
				.filter(p -> (!p.getName().equals("")) && (!p.getName().equals(adminName)) && (!p.getName().equals(creatorName)))
				.collect(Collectors.toList());
		
		return ret;
	}
}
