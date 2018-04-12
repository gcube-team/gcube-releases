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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gr.cite.gaap.datatransferobjects.PrincipalTenantPair;
import gr.cite.gaap.datatransferobjects.ProjectGroupInfo;
import gr.cite.gaap.datatransferobjects.UserinfoObject;
import gr.cite.geoanalytics.dataaccess.entities.ActiveStatus;
import gr.cite.geoanalytics.dataaccess.entities.principal.AccessControl;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.principal.PrincipalClass;
import gr.cite.geoanalytics.dataaccess.entities.principal.PrincipalData;
import gr.cite.geoanalytics.dataaccess.entities.principal.PrincipalMembership;
import gr.cite.geoanalytics.dataaccess.entities.principal.PrincipalProject;
import gr.cite.geoanalytics.dataaccess.entities.security.accesscontrol.dao.AccessControlDao;
import gr.cite.geoanalytics.dataaccess.entities.security.principal.dao.PrincipalDao;
import gr.cite.geoanalytics.dataaccess.entities.security.principal.dao.PrincipalDataDao;
import gr.cite.geoanalytics.dataaccess.entities.security.principal.dao.PrincipalMembershipDao;
import gr.cite.geoanalytics.dataaccess.entities.security.principal.dao.PrincipalProjectDao;
import gr.cite.geoanalytics.dataaccess.entities.security.principal.dao.PrincipalProjectInfoDao;
import gr.cite.geoanalytics.dataaccess.entities.tenant.Tenant;
import gr.cite.geoanalytics.dataaccess.entities.tenant.TenantActivation;
import gr.cite.geoanalytics.dataaccess.entities.tenant.dao.TenantActivationDao;
import gr.cite.geoanalytics.dataaccess.entities.tenant.dao.TenantDao;
import gr.cite.geoanalytics.security.SecurityContextAccessor;

@Service
public class PrincipalManager {

	private PrincipalDao principalDao;
	private TenantDao tenantDao;
	@Autowired private TenantManager tenantManager;
	@Autowired private SecurityContextAccessor securityContextAccessor;;
	private TenantActivationDao tenantActivationDao;
	private PrincipalDataDao principalDataDao;
	private PrincipalProjectDao principalProjectDao;
	private PrincipalMembershipDao principalMembershipDao;
	private AccessControlDao accessControlDao;

	@Inject
	public void setAccessControlDao(AccessControlDao accessControlDao) {
		this.accessControlDao = accessControlDao;
	}

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
	public void deletePrincipalByID(UUID id){
		this.principalDao.delete(this.principalDao.read(id));
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void deleteProjectGroupByID(UUID id) throws Exception{
		log.info("Deleting accesscontrol of projectGroup: " + id);
		this.accessControlDao.deleteByEntityId(id);
		log.info("Deleting projectGroup: " + id);
		this.principalDao.delete(this.principalDao.read(id));
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
	public List<UUID> listPrincipalUUIDsOfProjectGroupByGroupID(UUID groupID) {
		return this.principalMembershipDao.listPrincipalUUIDsOfProjectGroupByGroupID(groupID);
	}
	
	@Transactional(readOnly=true)
	public List<Principal> getActivePrincipalsByClassType(PrincipalClass principalType){
		return this.principalDao.findByClass(principalType);
	}
	
	@Transactional(readOnly=true)
	public List<Principal> getActivePrincipalsByClassTypeAndTenant(PrincipalClass principalType, ActiveStatus activeStatus, String tenantName){
		return this.principalDao.findByClass(principalType, activeStatus, tenantName);
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
		principal.setIsActive(activeStatus.code());
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
	
	@Transactional(rollbackFor=Exception.class)
	public void createProjectGroup(UserinfoObject uo, Principal creator, Tenant tenant) throws Exception {
		Principal principal = new Principal();
		principal.setClassId(PrincipalClass.PROJECT_GROUP.classCode());
		principal.setCreator(creator);
		principal.setIsActive(ActiveStatus.ACTIVE.code());
		principal.setName(uo.getGroupName());
		principal.setTenant(tenant);
		
		principal = principalDao.create(principal);
	}
	
	@SuppressWarnings("deprecation")
	@Transactional(rollbackFor=Exception.class)
	public Principal createPrincipalIfNotExists(
			UUID vreId, Tenant tenant, String principalName, String principalEmail, String principalInitials) throws Exception {

		Principal principal = new Principal();
		PrincipalData principalDataData = new PrincipalData();
		principalDataData.setFullName(principalName);
		principalDataData.setInitials(principalInitials);
		principalDataData.setEmail(principalEmail);
		principal.setCreationDate(Calendar.getInstance().getTime());
		principal.setLastUpdate(Calendar.getInstance().getTime());
		principalDataData.setExpirationDate(new Date(3000-1900,12,31));
		principalDataData.setVreUsrId(vreId);
		principal.setName(principalName);
		principal.setCreator(principal);
		principal.setClassId(PrincipalClass.ITEM.classCode());
		principal.setIsActive(ActiveStatus.ACTIVE.code());

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


		principal.setTenant(tenant);
		principalDao.update(principal);

		return principal;
	}
	
	@Transactional(rollbackFor=Exception.class)
	public List<PrincipalProjectInfoDao> collectInfoOfActivePrincipals(String creatorName, UserinfoObject uio) throws Exception {
		Tenant tenant = securityContextAccessor.getTenant();
		
		List<PrincipalProjectInfoDao> principalsInfo = new ArrayList<PrincipalProjectInfoDao>();
		
		principalsInfo = principalDao.findByClassReturnsPrincipalProjectInfo(PrincipalClass.ITEM, ActiveStatus.ACTIVE, tenant);
		
		String adminName = this.getSystemPrincipal().getName();
		List<PrincipalProjectInfoDao> ret = principalsInfo
				.stream()
				.filter(p -> (!p.getName().equals("")) && (!p.getName().equals(adminName)))
				.collect(Collectors.toList());
		
		if(uio.isEditMode()){
			Principal principal = principalDao.findProjectCreatorByProjectId(uio.getProjectId());
			
			ret.removeIf(p -> p.getId().equals(principal.getId()));
		} else {
			ret.removeIf(p -> {
				try {
					return p.getId().equals(securityContextAccessor.getPrincipal().getId());
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				return false;
			});
		}
		
		return ret;
	}
	
	@Transactional(rollbackFor=Exception.class)
	public List<ProjectGroupInfo> collectInfoOfActiveProjectGroups(Tenant tenant, Principal principal) throws Exception {
		List<Principal> projectGroups = new ArrayList<Principal>();
		projectGroups = principalDao.findByClassAndCreatorAndTenantAndStatus(
				PrincipalClass.PROJECT_GROUP, ActiveStatus.ACTIVE, tenant.getId(), principal);
		
		List<ProjectGroupInfo> projectGroupsNames = new ArrayList<ProjectGroupInfo>();
		projectGroups.forEach(p -> {
			projectGroupsNames.add(new ProjectGroupInfo(p.getName(), p.getCreator().getName(), p.getGroupsPrincipal().size(), p.getId()));
		});
		
		return projectGroupsNames;
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void assignPrincipalsToProjectGroup(List<UUID> principalUUIDs, UUID projectGroupID){
		Principal projectGroup = principalDao.read(projectGroupID);
		
		if(projectGroup == null){
			throw new RuntimeException("No project group with this name exists in the database");
		}
		
		
		Set<PrincipalMembership> initialSetOfPrincipalMemberships = new HashSet<PrincipalMembership>(projectGroup.getGroupsPrincipal());
		Set<PrincipalMembership> membershipsToBeDeleted = new HashSet<PrincipalMembership>();
		Set<PrincipalMembership> newMemberships = new HashSet<PrincipalMembership>();
		Set<PrincipalMembership> membershipsAlreadyInGroup = new HashSet<PrincipalMembership>();
		
		Set<Principal> initialSetOfPrincipals = new HashSet<Principal>();
		for(PrincipalMembership pmInit : initialSetOfPrincipalMemberships){
			initialSetOfPrincipals.add(pmInit.getMember());
		}
		
		principalUUIDs.forEach(pID -> {
			Principal member = this.getPrincipalById(pID);
			
			if(initialSetOfPrincipals.contains(member)){
				//update last update
				PrincipalMembership pm = principalMembershipDao.findPrincipalMembershipByPrincipalAndGroup(member, projectGroup);
				pm.setCreationDate(pm.getCreationDate());
				pm.setLastUpdate(Calendar.getInstance().getTime());
				principalMembershipDao.update(pm);
				membershipsAlreadyInGroup.add(pm);
			}else {
				PrincipalMembership pm = new PrincipalMembership();
				pm.setGroup(projectGroup);
				pm.setMember(member);
				principalMembershipDao.create(pm);
				newMemberships.add(pm);
			}
		});
		
		membershipsToBeDeleted.addAll(initialSetOfPrincipalMemberships);
		membershipsToBeDeleted.removeAll(newMemberships);
		membershipsToBeDeleted.removeAll(membershipsAlreadyInGroup);
		
		projectGroup.getGroupsPrincipal().removeAll(membershipsToBeDeleted);
		projectGroup.getGroupsPrincipal().addAll(newMemberships);
		
		principalDao.update(projectGroup);
	}
	
	@Transactional(rollbackFor=Exception.class)
	public List<PrincipalProjectInfoDao> collectInfoOfActivePrincipalsAndNumOfMembersByTenant(
			Tenant tenant, Principal principal, boolean includeRights)
					throws Exception {
		List<PrincipalProjectInfoDao> principalsInfo = new ArrayList<PrincipalProjectInfoDao>();
		principalsInfo = principalDao.retrieveProjectGroupByTenant(PrincipalClass.PROJECT_GROUP, ActiveStatus.ACTIVE, tenant, principal);
		
		principalsInfo.forEach(p -> {
			Principal pr = principalDao.read(p.getId());
			if(includeRights){
				AccessControl ac = accessControlDao.findByPrincipal(pr);
				p.setRead(ac.getReadRight());
				p.setEdit(ac.getEditRight());
				p.setDelete(ac.getDeleteRight());
			} else {
				short o = 0;
				p.setRead(o);
				p.setEdit(o);
				p.setDelete(o);
			}
		});
		
		return principalsInfo;
	}
	
	public List<String> collectActiveGroupsAndNumOfMembersByTenant(String tenantName) throws Exception {
		List<Principal> principalsInfo = new ArrayList<Principal>();
		
		principalsInfo = getActivePrincipalsByClassTypeAndTenant(PrincipalClass.GROUP, ActiveStatus.ACTIVE, tenantName);
		
		List<String> ret = principalsInfo
				.stream()
				.map(Principal::getName)
				.collect(Collectors.toList());
		
		return ret;
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void deletePrincipalByNameAndTenantName(String principalName, String tenantName){
		Principal principal = this.findActivePrincipalByNameAndTenant(principalName, tenantName);
		principalDao.delete(principal);
	}
	
	@Transactional(rollbackFor=Exception.class)
	public List<UUID> findActivePrincipalUUIDsByNameListTenantAndActiveStatus(List<String> principalNames, String tenantName)
	{
		List<UUID> principalIDs = new ArrayList<UUID>();
		
		List<Principal> principals = findActivePrincipalByNameListTenantAndActiveStatus( principalNames, tenantName);
		if(!principals.isEmpty()){
			principalIDs = principals.stream().map(pr -> pr.getId()).collect(Collectors.toList());
		}
		
		return principalIDs;
	}
	
	@Transactional(rollbackFor=Exception.class)
	public List<Principal> findActivePrincipalByNameListTenantAndActiveStatus(List<String> principalNames, String tenantName)
	{
		return principalDao.findPrincipalByNameListTenantAndActiveStatus( principalNames, tenantName, ActiveStatus.ACTIVE);
	}
	
	@Transactional(rollbackFor=Exception.class)
	public List<Principal> findPrincipalByNameListTenantAndActiveStatus(List<String> principalNames, String tenantName,
			ActiveStatus activeStatus){
		return principalDao.findPrincipalByNameListTenantAndActiveStatus( principalNames, tenantName,activeStatus);
	}
	
	@Transactional(readOnly=true)
	public List<PrincipalProject> getByPrincipalUUIDsAndProjectId(List<UUID> principalIDs, UUID projectID) {
		return principalProjectDao.getByPrincipalUUIDsAndProjectId(principalIDs, projectID);
	}
	
	@Transactional(readOnly=true)
	public Principal findByVreIdAndTenant(UUID vreId, Tenant tenant){
		Principal principal = null;
		try {
			principal = principalDao.findByVreIdAndTenant(vreId, tenant);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return principal;
	}
	
	@Transactional(rollbackFor=Exception.class)
	public Principal findByVreIdAndTenantOrByNameAndTenant(UUID vreId, Tenant tenant, String principalName) {
		Principal principal = null;
		
		try {
			principal = principalDao.findByVreIdAndTenant(vreId, tenant);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		if(principal == null) {
			try {
				principal = principalDao.findActivePrincipalByNameAndTenant(principalName, tenant.getName());
				
				PrincipalData pd = principal.getPrincipalData();
				pd.setVreUsrId(vreId);
				principalDataDao.update(pd);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		return principal;
	}
	
	@Transactional(rollbackFor=Exception.class)
	public Principal findByVreIdAndTenantOrByNameAndTenant(UUID vreId, UUID tenantId, String principalName) {
		Tenant tenant = tenantDao.read(tenantId);
		
		return this.findByVreIdAndTenantOrByNameAndTenant(vreId, tenant, principalName);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public synchronized Principal findByVreIdAndTenantOrByNameAndTenantOrCreateOrUpdate(
			UUID vreId, String tenantName, String principalName, String principalEmail, String principalInitials) {
		
		Tenant tenant = tenantManager.createIfNotExists(tenantName);
		//Find existing
		Principal principal = null;
		log.debug("Retrieving principal by vreUsrId: " + vreId + " and tenant id: " + tenant.getId());
		principal = this.findByVreIdAndTenantOrByNameAndTenant(vreId, tenant, principalName);
		
		//Create or Update
		if(principal == null) {
			//Create
			log.info("Failed to retrieve principal by vreUsrId: " + vreId + " and tenant id: " + tenant.getId());
			log.info("Creating new principal with name :" + principalName + " with vreUsrId: " + vreId + " and tenant id: " + tenant.getId());
			try {
				principal = createPrincipalIfNotExists(vreId, tenant, principalName, principalEmail, principalInitials);
			} catch (Exception e) {
				log.error("Failed at creating new principal with name :" + principalName + " with vreUsrId: " + vreId + " and tenant id: " + tenant.getId());
				e.printStackTrace();
			}
		} else if(principal != null && principalProperiesHaveChanges(principalName, principalEmail, principalInitials, principal)){
			//Update
			log.info("The principal properties have changed. Updating principal with UUID: " + principal.getId());
			
			Date now = new Date();
			principal.setLastUpdate(now);
			principal.setName(principalName);
			principalDao.update(principal);
			
			PrincipalData pd = principal.getPrincipalData();
			pd.setLastUpdate(now);
			pd.setEmail(principalEmail);
			pd.setInitials(principalInitials);
			pd.setFullName(principalName);
			principalDataDao.update(pd);
		}
		
		return principal;
	}
	
	public synchronized Principal findByVreIdAndTenantId(UUID vreUsrID, UUID tenantID) {
		return principalDao.findByVreIdAndTenantID(vreUsrID, tenantID);
	}
	
	public boolean principalProperiesHaveChanges(String principalName, String principalEmail,
			String principalInitials, Principal principal) {
		
		if(!principalName.equals(principal.getName())) {
			return true;
		}
		if(!principalEmail.equals(principal.getPrincipalData().getEmail())) {
			return true;
		}
		if(!principalInitials.equals(principal.getPrincipalData().getInitials())) {
			return true;
		}
		
		return false;
	}
	
	private static Logger log = LoggerFactory.getLogger(PrincipalManager.class); 
}
