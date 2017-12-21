package gr.cite.geoanalytics.manager.admin;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import gr.cite.clustermanager.actuators.layers.DataMonitor;
//import gr.cite.commons.util.datarepository.DataRepository;
import gr.cite.gaap.datatransferobjects.AccountLockInfo;
import gr.cite.gaap.datatransferobjects.DataUpdateInfo;
import gr.cite.gaap.datatransferobjects.IllegalAccessAuditingInfo;
import gr.cite.gaap.datatransferobjects.PrincipalLastActionInfo;
import gr.cite.gaap.datatransferobjects.UserLastLoginInfo;
import gr.cite.gaap.datatransferobjects.UserLastPasswordRequestInfo;
import gr.cite.gaap.datatransferobjects.UserLastUnsuccessfulLoginInfo;
import gr.cite.gaap.servicelayer.ConfigurationManager;
import gr.cite.gaap.servicelayer.GeospatialBackendClustered;
import gr.cite.geoanalytics.manager.PrincipalManager;
import gr.cite.geoanalytics.manager.TenantManager;
import gr.cite.geoanalytics.dataaccess.entities.ActiveStatus;
import gr.cite.geoanalytics.dataaccess.entities.Entity;
import gr.cite.geoanalytics.dataaccess.entities.auditing.Auditing;
import gr.cite.geoanalytics.dataaccess.entities.auditing.Auditing.AuditingType;
import gr.cite.geoanalytics.dataaccess.entities.auditing.AuditingData;
import gr.cite.geoanalytics.dataaccess.entities.auditing.dao.AuditingDao;
import gr.cite.geoanalytics.dataaccess.entities.document.dao.DocumentDao;
import gr.cite.geoanalytics.dataaccess.entities.geocode.dao.GeocodeDao;
import gr.cite.geoanalytics.dataaccess.entities.geocode.dao.GeocodeSystemDao;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.project.dao.ProjectDao;
import gr.cite.geoanalytics.dataaccess.entities.security.principal.dao.PrincipalDao;
import gr.cite.geoanalytics.dataaccess.entities.tenant.dao.TenantDao;
import gr.cite.geoanalytics.dataaccess.entities.user.UserRights;
import gr.cite.geoanalytics.dataaccess.entities.workflow.dao.WorkflowDao;
import gr.cite.geoanalytics.dataaccess.entities.workflow.dao.WorkflowTaskDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdministrationManager {
	
	private ConfigurationManager cfgMan;
	private TenantManager customerMan;
	private PrincipalManager principalManager;

	private GeospatialBackendClustered geospatialBackendClustered;
	private TenantDao tenantDao;
	private DocumentDao documentDao;
	private GeocodeSystemDao geocodeSystemDao;
	private GeocodeDao geocodeDao;
	private ProjectDao projectDao;
	private WorkflowDao workflowDao;
	private WorkflowTaskDao workflowTaskDao;
	private AuditingDao auditingDao;
	private PrincipalDao principalDao;
	
//	private DataRepository repository;
	
	@Resource(name="sessionRegistry")
	SessionRegistry sessionRegistry;
	
	public enum EntryType
	{
		SystemStatus,

		OnlineUserCount,
		ActiveUserCount,
		LockedUserCount,
		AllUserCount,
		ShapeCount,
		DocumentCount,
		DocumentSize,
		ProjectCount,
		WorkflowCount,
		WorkflowTaskCount,
		TaxonomyCount,
		GeocodeCount,
		AllCustomerCount,
		ActiveCustomerCount,
		
		LastDataUpdate,
		LastUserAction,
		LastUserActions,
		LastUserLogin,
		LastUserLogins,
		LastUserUnsuccessfulLogin,
		LastUserUnsuccessfulLogins,
		AccountLock,
		IllegalRequestAttempt,
		IllegalLayerAccessAttempt,
		IllegalLayerZoomAttempt,
		DOSAttackAttempt,
		IllegalRequestAttemptCount,
		IllegalLayerAccessAttemptCount,
		IllegalLayerZoomAttemptCount,
		DOSAttackAttemptCount,
		
		RepositorySize,
		RepositoryLastSweep,
		RepositoryLastSweepSizeReduction
	}
	
	private static final long maxAcceptableRateDefault = 20;
	private static final long accountLockCheckPeriodDefault = 40;
	private static final TimeUnit accountLockCheckPeriodUnitDefault = TimeUnit.SECONDS; 
	private static final long accountLockPeriodDefault = 30;
	private static final TimeUnit accountLockPeriodUnitDefault = TimeUnit.MINUTES; 
	
	
	private long maxAcceptableRate = maxAcceptableRateDefault;
	private long accountLockCheckPeriod = accountLockCheckPeriodDefault;
	private TimeUnit accountLockCheckPeriodUnit = accountLockCheckPeriodUnitDefault;
	private long accountLockPeriod = accountLockPeriodDefault;
	private TimeUnit accountLockPeriodUnit = accountLockPeriodUnitDefault;
	
	@Autowired(required=false) //DEPWARN spring dependency
	public void setRateLimitConfig(long maxAcceptableRate, long accountLockCheckPeriod, TimeUnit accountLockCheckPeriodUnit,
			long accountLockPeriod, TimeUnit accountLockPeriodUnit)
	{
		this.accountLockCheckPeriod = accountLockCheckPeriod;
		this.accountLockCheckPeriodUnit = accountLockCheckPeriodUnit;
		this.accountLockPeriod = accountLockPeriod;
		this.accountLockPeriodUnit = accountLockPeriodUnit;
	}
	
	public AdministrationManager() { }
	
	@Inject
	public AdministrationManager(ConfigurationManager cfgMan, TenantManager customerMan/*, DataRepository dataRepository*/)
	{
		this.cfgMan = cfgMan;
		this.customerMan = customerMan;
//		this.repository = dataRepository;
	}
	
	@Inject
	public void setPrincipalDao(PrincipalDao principalDao) {
		this.principalDao = principalDao;
	}
	
	@Inject
	public void setGeospatialBackendClustered(GeospatialBackendClustered geospatialBackendClustered) {
		this.geospatialBackendClustered = geospatialBackendClustered;
	}
	
	@Inject
	public void setCustomerDao(TenantDao tenantDao) {
		this.tenantDao = tenantDao;
	}
	
	@Inject
	public void setProjectDao(ProjectDao projectDao) {
		this.projectDao = projectDao;
	}

	@Inject
	public void setDocumentDao(DocumentDao documentDao) {
		this.documentDao = documentDao;
	}
	
	@Inject
	public void setGeocodeSystemDao(GeocodeSystemDao geocodeSystemDao) {
		this.geocodeSystemDao = geocodeSystemDao;
	}
	
	@Inject
	public void setGeocodeDao(GeocodeDao geocodeDao) {
		this.geocodeDao = geocodeDao;
	}
	
	@Inject
	public void setWorkflowDao(WorkflowDao workflowDao) {
		this.workflowDao = workflowDao;
	}
	
	@Inject
	public void setWorkflowTaskDao(WorkflowTaskDao workflowTaskDao) {
		this.workflowTaskDao = workflowTaskDao;
	}
	
	@Inject
	public void setAuditingDao(AuditingDao auditingDao) {
		this.auditingDao = auditingDao;
	}
	
	@Inject
	public void setPrincipalManager(PrincipalManager principalManager) {
		this.principalManager = principalManager;
	}
	
	
	@Transactional(readOnly = true)
	public Map<String, Object> getAllInfo() throws Exception {
		Map<String, Object> info = new HashMap<String, Object>();
		
		info.put(EntryType.SystemStatus.toString(), cfgMan.isSystemOnline());
		info.put(EntryType.LastUserAction.toString(), getLastUserAction());
		info.put(EntryType.LastUserActions.toString(), getLastUserActions());
		info.put(EntryType.LastUserLogin.toString(), getLastUserLogin());
		info.put(EntryType.LastUserUnsuccessfulLogin.toString(), getLastUnsuccessfulUserLogin());
		info.put(EntryType.LastDataUpdate.toString(), getLastDataUpdate());
		info.put(EntryType.ActiveUserCount.toString(), getActiveUserCount());
		info.put(EntryType.AllUserCount.toString(), getAllUserCount());
		info.put(EntryType.AllCustomerCount.toString(), getAllCustomerCount());
		info.put(EntryType.ActiveCustomerCount.toString(), getActiveCustomerCount());
		info.put(EntryType.OnlineUserCount.toString(), getOnlineUserCount());
		info.put(EntryType.LockedUserCount.toString(), getLockedUserCount());
		info.put(EntryType.ShapeCount.toString(), getShapeCount());
		info.put(EntryType.DocumentCount.toString(), getDocumentCount());
		info.put(EntryType.DocumentSize.toString(), getDocumentSize());
		info.put(EntryType.ProjectCount.toString(), getProjectCount());
		info.put(EntryType.TaxonomyCount.toString(), getTaxonomyCount());
		info.put(EntryType.GeocodeCount.toString(), getGeocodeCount());
		info.put(EntryType.WorkflowCount.toString(), getWorkflowCount());
		info.put(EntryType.WorkflowTaskCount.toString(), getWorkflowTaskCount());
		info.put(EntryType.IllegalRequestAttemptCount.toString(), getIllegalRequestAttemptCount());
		info.put(EntryType.IllegalLayerAccessAttemptCount.toString(), getLayerIllegalAccessAttemptCount());
		info.put(EntryType.IllegalLayerZoomAttemptCount.toString(), getLayerZoomIllegalAccessAttemptCount());
		//info.put(EntryType.DOSAttackAttemptCount, getDOSAttackAttemptCount()); //TODO
//		info.put(EntryType.RepositorySize.toString(), getRepositorySize());
//		info.put(EntryType.RepositoryLastSweep.toString(), getRepositoryLastSweep());
//		info.put(EntryType.RepositoryLastSweepSizeReduction.toString(), getRepositoryLastSweepSizeReduction());
		return info;
	}
	
	@Transactional(readOnly = true)
	public long getActiveUserCount() throws Exception {
		return principalDao.findActivePrincipals().size() -1; //-1 for system user
	}
	
	@Transactional(readOnly = true)
	public long getAllUserCount() throws Exception {
		return principalDao.count() -1; //-1 for system user
	}
	
	public long getOnlineUserCount() throws Exception
	{
		if(sessionRegistry == null) return -1;
		return sessionRegistry.getAllPrincipals().size();
	}
	
	@Transactional(readOnly = true)
	public long getLockedUserCount() throws Exception {
		long cnt = 0;
		List<Principal> principals = principalDao.getAll();
		for(Principal principal : principals) {
			JAXBContext rightsCtx = JAXBContext.newInstance(UserRights.class);
			Unmarshaller um = rightsCtx.createUnmarshaller();
			//TODO
			//UserRights rights = (UserRights)um.unmarshal(new StringReader(principal.getRights()));
			//if(rights.isLocked()) cnt++;
		}
		return cnt;
	}
	
	@Transactional(readOnly = true)
	public long getShapeCount() throws Exception
	{
		//TODO: find a way to count all distinct shapes from Gos
		//for now. it will return shape count (sum) of all GOS endpoints
		long count = geospatialBackendClustered.getDataMonitor().getAllGosEndpoints().parallelStream().map(gosDef -> {
			//add a function totalshapes() on ShapeManagement class
//			return geospatialBackendClustered.getShapeManagement().countTotalShapes(gosEndpoint);
			return 0;
		}).mapToLong(i -> i).sum();
		return count;
	}
	
	@Transactional(readOnly = true)
	public long getDocumentCount() throws Exception
	{
		return documentDao.count();
	}
	
	@Transactional(readOnly = true)
	public long getDocumentSize() throws Exception
	{
		return documentDao.totalSize();
	}
	
//	public long getRepositorySize()
//	{
//		return repository.getTotalSize();
//	}
//	
//	public Long getRepositoryLastSweep()
//	{
//		return repository.getLastSweep();
//	}
//	
//	public Long getRepositoryLastSweepSizeReduction()
//	{
//		return repository.getSweepSizeReduction();
//	}
	
	@Transactional(readOnly = true)
	public long getProjectCount() throws Exception
	{
		return projectDao.count();
	}
	
	@Transactional(readOnly = true)
	public long getWorkflowCount() throws Exception
	{
		return workflowDao.count();
	}
	
	@Transactional(readOnly = true)
	public long getWorkflowTaskCount() throws Exception
	{
		return workflowTaskDao.count();
	}
	
	@Transactional(readOnly = true)
	public long getTaxonomyCount() throws Exception
	{
		return geocodeSystemDao.count();
	}
	
	@Transactional(readOnly = true)
	public long getGeocodeCount() throws Exception
	{
		return geocodeDao.count();
	}
	
	@Transactional(readOnly = true)
	public long getAllCustomerCount() throws Exception
	{
		return tenantDao.count();
	}
	
	@Transactional(readOnly = true)
	public long getActiveCustomerCount() throws Exception {
		return customerMan.listActiveTenants().size();
	}
	
	private String marshal(AuditingData data) throws Exception {
		JAXBContext ctx = JAXBContext.newInstance(AuditingData.class);
		StringWriter sw = new StringWriter();
		Marshaller m = ctx.createMarshaller();
		m.marshal(data, sw);
		return sw.toString();
	}
	
	private AuditingData unmarshal(String data) throws Exception {
		JAXBContext ctx = JAXBContext.newInstance(AuditingData.class);
		Unmarshaller um = ctx.createUnmarshaller();
		return (AuditingData)um.unmarshal(new StringReader(data));
	}
	
	private Auditing getAuditingByTypeAndUser(AuditingType type, Principal principal) throws Exception {
		AuditingDao dao = auditingDao;
		Auditing auditing = null;
		if(principal != null) auditing = dao.findByTypeAndUser(type, principal);
		else {
//			List<Auditing> l = dao.findByType(type);
//			if(l != null && !l.isEmpty()) lastUserAction = l.get(0);
			return null;
		}
		
		return auditing;
	}
	
	@Transactional(readOnly = true)
	public PrincipalLastActionInfo getLastUserActionForUser(Principal principal) throws Exception {
		Auditing lastUserAction = getAuditingByTypeAndUser(AuditingType.LastUserAction, principal);
		if(lastUserAction == null) return null;
		AuditingData data = unmarshal(lastUserAction.getData());
		return new PrincipalLastActionInfo(principal.getName(), data.getEntityType(), data.getData(), data.getTimestamp());
	}
	
	@Transactional(readOnly = true)
	public UserLastLoginInfo getLastLoginForUser(Principal principal) throws Exception {
		Auditing lastUserLogin = getAuditingByTypeAndUser(AuditingType.LastUserLogin, principal);
		if(lastUserLogin == null) return null;
		AuditingData data = unmarshal(lastUserLogin.getData());
		return new UserLastLoginInfo(principal.getName(), data.getTimestamp());
	}
	
	@Transactional(readOnly = true)
	public UserLastUnsuccessfulLoginInfo getLastUnsuccessfulLoginForUser(Principal principal) throws Exception {
		Auditing lastUserLogin = getAuditingByTypeAndUser(AuditingType.LastUnsuccessfulUserLogin, principal);
		if(lastUserLogin == null) return null;
		AuditingData data = unmarshal(lastUserLogin.getData());
		return new UserLastUnsuccessfulLoginInfo(principal.getName(), data.getTimestamp(), Integer.parseInt(data.getData().split(":")[0]));
	}
	
	private Set<String> getAddressesForUser(AuditingType type, Principal principal) throws Exception {
		Set<String> addresses = new HashSet<String>();
		Auditing lastUserLogin = getAuditingByTypeAndUser(AuditingType.LastUnsuccessfulUserLogin, principal);
		if(lastUserLogin == null) return addresses;
		AuditingData data = unmarshal(lastUserLogin.getData());
		String[] parts = data.getData().split(":");
		for(int i=1; i<parts.length; i++)
			addresses.add(parts[i]);
		return addresses;
	}
	
	@Transactional(readOnly = true)
	public UserLastPasswordRequestInfo getLastPasswordRequestForUser(Principal principal) throws Exception {
		Auditing lastPasswordRequest = getAuditingByTypeAndUser(AuditingType.LastUserPasswordRequest, principal);
		if(lastPasswordRequest == null) return null;
		AuditingData data = unmarshal(lastPasswordRequest.getData());
		return new UserLastPasswordRequestInfo(principal.getName(), data.getTimestamp(), Integer.parseInt(data.getData()));
	}
	
	@Transactional(readOnly = true)
	public PrincipalLastActionInfo getLastUserAction() throws Exception {
		TreeMap<String, PrincipalLastActionInfo> actions = getLastUserActions();
		if(actions == null || actions.isEmpty()) return null;
		
		return new PrincipalLastActionInfo(actions.firstEntry().getKey(), actions.firstEntry().getValue().getEntityType(), 
				actions.firstEntry().getValue().getAction(), actions.firstEntry().getValue().getTimestamp());
	}
	
	@Transactional(readOnly = true)
	public DataUpdateInfo getLastDataUpdate() throws Exception {
		Auditing update = auditingDao.findMostRecentByType(AuditingType.LastDataUpdate);
		if(update != null) {
			AuditingData data = unmarshal(update.getData());
			//TODO updater
			return new DataUpdateInfo(data.getEntityId() != null ? data.getEntityId().toString() : null, data.getEntityType(), data.getTimestamp());
		}
		return null;
	}
	
	@Transactional(readOnly = true)
	public TreeMap<String, PrincipalLastActionInfo> getLastUserActions() throws Exception {
		final Map<String, PrincipalLastActionInfo> actions = new HashMap<String, PrincipalLastActionInfo>();
		List<Principal> principals = principalDao.findActivePrincipals();
		for(Principal principal : principals) {
			PrincipalLastActionInfo i = getLastUserActionForUser(principal);
			if(i != null) actions.put(principal.getName(), i);
		}
		
		TreeMap<String, PrincipalLastActionInfo> sortedActions = new TreeMap<String, PrincipalLastActionInfo>(new Comparator<String>() { 
					@Override
					public int compare(String a, String b) {
						if(actions.get(a).getTimestamp() > actions.get(b).getTimestamp()) return -1;
						else return 1;
					}
				});
		sortedActions.putAll(actions);
		return sortedActions;
	}
	
	@Transactional(readOnly = true)
	public UserLastLoginInfo getLastUserLogin() throws Exception {
		Auditing login = auditingDao.findMostRecentByType(AuditingType.LastUserLogin);
		if(login != null)
			return new UserLastLoginInfo(login.getPrincipal().getName(), login.getDate().getTime());
		return null;
	}
	
	@Transactional(readOnly = true)
	public UserLastUnsuccessfulLoginInfo getLastUnsuccessfulUserLogin() throws Exception {
		Auditing login = auditingDao.findMostRecentByType(AuditingType.LastUnsuccessfulUserLogin);
		if(login != null) {
			AuditingData data = unmarshal(login.getData());
			return new UserLastUnsuccessfulLoginInfo(login.getPrincipal().getName(), login.getDate().getTime(), Integer.parseInt(data.getData().split(":")[0]));
		}
		return null;
	}
	
	private List<Auditing> getByTypeOrdered(AuditingType type) throws Exception { 
		return auditingDao.findByTypeOrdered(type);
	}
	
	private long countByType(AuditingType type) throws Exception {
		return auditingDao.countByType(type);
	}
	
	@Transactional(readOnly = true)
	public TreeMap<String, UserLastLoginInfo> getLastUserLogins() throws Exception {
		TreeMap<String, UserLastLoginInfo> res = new TreeMap<String, UserLastLoginInfo>();
		List<Auditing> logins = getByTypeOrdered(AuditingType.LastUserLogin);
		for(Auditing login : logins)
			res.put(login.getPrincipal().getName(), new UserLastLoginInfo(login.getPrincipal().getName(), login.getDate().getTime()));
		return res;
	}
	
	@Transactional(readOnly = true)
	public TreeMap<String, UserLastUnsuccessfulLoginInfo> getLastUnsuccessfulUserLogins() throws Exception {
		TreeMap<String, UserLastUnsuccessfulLoginInfo> res = new TreeMap<String, UserLastUnsuccessfulLoginInfo>();
		List<Auditing> logins = getByTypeOrdered(AuditingType.LastUnsuccessfulUserLogin);
		for(Auditing login : logins)
			res.put(login.getPrincipal().getName(), new UserLastUnsuccessfulLoginInfo(login.getPrincipal().getName(), 
					login.getDate().getTime(), Integer.parseInt(login.getData())));
		return res;
	}
	
	@Transactional(readOnly = true)
	public TreeMap<String, AccountLockInfo> getAccountLocks() throws Exception {
		final Map<String, AccountLockInfo> locks = new HashMap<String, AccountLockInfo>();
		List<Principal> principals = principalDao.findActivePrincipals();
		for(Principal principal : principals) {
			if(!principalManager.isActiveStatusByActiveStatusAndName(principal.getName(), ActiveStatus.LOCKED)) continue;
			UserLastUnsuccessfulLoginInfo lastUnsuccessful = getLastUnsuccessfulLoginForUser(principal);
			long toUnlock = TimeUnit.MILLISECONDS.convert(accountLockPeriod, accountLockPeriodUnit) - (new Date().getTime() - lastUnsuccessful.getTimestamp());
			
			locks.put(principal.getName(), new AccountLockInfo(principal.getName(), lastUnsuccessful.getTimestamp(), 
					getAddressesForUser(AuditingType.LastUnsuccessfulUserLogin, principal), toUnlock));
		}
		
		TreeMap<String, AccountLockInfo> sortedlocks = new TreeMap<String, AccountLockInfo>(new Comparator<String>() { 
					@Override
					public int compare(String a, String b) {
						if(locks.get(a).getTimestamp() > locks.get(b).getTimestamp()) return -1;
						else return 1;
					}
				});
		sortedlocks.putAll(locks);
		return sortedlocks;
	}
	
	private List<IllegalAccessAuditingInfo> getIllegalAccessByType(AuditingType type) throws Exception {
		List<IllegalAccessAuditingInfo> res = new ArrayList<IllegalAccessAuditingInfo>();
		List<Auditing> entries = getByTypeOrdered(type);
		for(Auditing entry : entries)
			res.add(new IllegalAccessAuditingInfo(entry.getPrincipal().getName(), entry.getDate().getTime(), entry.getData()));
		return res;
	}
	
	@Transactional(readOnly = true)
	public long getIllegalRequestAttemptCount() throws Exception {
		return countByType(AuditingType.IllegalRequestAttempt);
	}
	
	@Transactional(readOnly = true)
	public long getLayerIllegalAccessAttemptCount() throws Exception {
		return countByType(AuditingType.LayerIllegalAccessAttempt);
	}
	
	@Transactional(readOnly = true)
	public long getLayerZoomIllegalAccessAttemptCount() throws Exception {
		return countByType(AuditingType.LayerZoomIllegalAccessAttempt);
	}
	
	
	@Transactional(readOnly = true)
	public List<IllegalAccessAuditingInfo> getIllegalRequests() throws Exception {
		return getIllegalAccessByType(AuditingType.IllegalRequestAttempt);
	}
	
	@Transactional(readOnly = true)
	public List<IllegalAccessAuditingInfo> getIllegalLayerAccesses() throws Exception {
		return getIllegalAccessByType(AuditingType.LayerIllegalAccessAttempt);
	}
	
	@Transactional(readOnly = true)
	public List<IllegalAccessAuditingInfo> getIllegalLayerZoomAccess() throws Exception {
		return getIllegalAccessByType(AuditingType.LayerZoomIllegalAccessAttempt);
	}
	
	@Transactional(readOnly = true)
	public List<IllegalAccessAuditingInfo> getDOSAttackAttempts() throws Exception {
		return getIllegalAccessByType(AuditingType.DOSAttack);
	}
	
	@Transactional
	public void updateUserAction(Principal principal) throws Exception {
		AuditingDao dao = auditingDao;
		Auditing lastUserAction = dao.findByTypeAndCreator(Auditing.AuditingType.LastUserAction, principal);
		
		JAXBContext ctx = JAXBContext.newInstance(AuditingData.class);
		Unmarshaller um = ctx.createUnmarshaller();
		AuditingData data = (AuditingData)um.unmarshal(new StringReader(lastUserAction.getData()));
		
		data.setTimestamp(Calendar.getInstance().getTimeInMillis());
		
		StringWriter sw = new StringWriter();
		Marshaller m = ctx.createMarshaller();
		m.marshal(data, sw);
		
		lastUserAction.setData(sw.toString());
		
		dao.update(lastUserAction);
	}
	
	@Transactional
	public void updateLastDataUpdate(UUID id, Class<Entity> dataType) throws Exception {
		AuditingDao dao = auditingDao;
		Auditing lastUpdate= dao.findLastDataUpdate();
		
		JAXBContext ctx = JAXBContext.newInstance(AuditingData.class);
		Unmarshaller um = ctx.createUnmarshaller();
		AuditingData data = (AuditingData)um.unmarshal(new StringReader(lastUpdate.getData()));
		
		data.setTimestamp(Calendar.getInstance().getTimeInMillis());
		data.setEntityId(id);
		data.setEntityType(dataType.getSimpleName());
		
		StringWriter sw = new StringWriter();
		Marshaller m = ctx.createMarshaller();
		m.marshal(data, sw);
		
		lastUpdate.setData(sw.toString());
		
		dao.update(lastUpdate);
	}
}
