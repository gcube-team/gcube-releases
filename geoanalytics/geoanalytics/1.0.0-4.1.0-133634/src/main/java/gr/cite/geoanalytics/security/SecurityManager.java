package gr.cite.geoanalytics.security;

import gr.cite.gaap.datatransferobjects.user.RoleMessenger;
import gr.cite.gaap.datatransferobjects.user.RoleMessengerUpdate;
import gr.cite.gaap.datatransferobjects.user.UserManagementMessenger;
import gr.cite.gaap.datatransferobjects.user.UserMessenger;
import gr.cite.gaap.servicelayer.TaxonomyManager;
import gr.cite.geoanalytics.dataaccess.definitions.security.AccessRightDefinitions;
import gr.cite.geoanalytics.dataaccess.entities.ActiveStatus;
import gr.cite.geoanalytics.dataaccess.entities.principal.AccessRight;
import gr.cite.geoanalytics.dataaccess.entities.principal.AccessRightStatus;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.principal.PrincipalClass;
import gr.cite.geoanalytics.dataaccess.entities.principal.PrincipalMembership;
import gr.cite.geoanalytics.dataaccess.entities.principal.metadata.PrincipalMetadata;
import gr.cite.geoanalytics.dataaccess.entities.security.accesscontrol.dao.AccessControlDao;
import gr.cite.geoanalytics.dataaccess.entities.security.accessright.dao.AccessRightDao;
import gr.cite.geoanalytics.dataaccess.entities.security.principal.dao.PrincipalDao;
import gr.cite.geoanalytics.dataaccess.entities.security.principal.dao.PrincipalMembershipDao;
import gr.cite.geoanalytics.geoanalytics.security.util.AccessRightHierarchy;
import gr.cite.geoanalytics.geoanalytics.security.util.AccessRightHierarchyConverter;
import gr.cite.geoanalytics.geoanalytics.security.util.AccessRightInternalNode;
import gr.cite.geoanalytics.geoanalytics.security.util.AccessRightLeafNode;
import gr.cite.geoanalytics.geoanalytics.security.util.AccessRightLeafNode.AccessRightType;
import gr.cite.geoanalytics.geoanalytics.security.util.AccessRightNode;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collector;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@Service(value = "securityManager")
public class SecurityManager {
	private static Logger log = LoggerFactory.getLogger(TaxonomyManager.class);
	
	public static final String GROUP_USER = "User";
	public static final String GROUP_APPADMIN = "AppAdmin";
	public static final String GROUP_GUEST = "Guest";
	
	private static JAXBContext accessRightsDefinitionCtx = null;
	private static Object accessRightsDefinitionCtxLock = new Object();
	
	private static JAXBContext principalMetadataCtx = null;
	private static Object principalMetadataCtxLock = new Object();

	private static ReadWriteLock hierarchyLock = new ReentrantReadWriteLock();
	private static ReadWriteLock accessDataLock = new ReentrantReadWriteLock();

	private static AccessRightHierarchy hierarchy = null;
	private static List<AccessRight> accessRights = null;
	private static Map<UUID, Map<UUID, AccessRightStatus>> accessRightsLookup = new HashMap<UUID, Map<UUID, AccessRightStatus>>(); // principal -> (right -> status)

	private HttpServletRequest request = null;
	
	private PrincipalDao principalDao = null;
	private PrincipalMembershipDao principalMembershipDao = null;
	private AccessRightDao accessRightDao = null;
	private AccessControlDao accessControlDao = null;
	private SecurityContextAccessor securityContextAccessor;
	private SecurityManager self;
	
	private static File accessRightDefinitionsFile = null;

	private static final UUID ACCESS_RIGHT_USER_OPERATIONS = UUID.fromString("a9950793-a012-11e5-8dba-00155d10e309");
	
	
	private static enum AccessRightResolutionOperator {
		AND,
		OR
	}
	
	@CacheEvict(value = {"gr.cite.geoanalytics.security.principalCache", "gr.cite.geoanalytics.security.principalMetadataCache", "gr.cite.geoanalytics.security.principalRoleCache"}, allEntries = true)
	public void invalidateCaches() {
		
	}

	
	@Inject
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	@Inject
	public void setPrincipalDao(PrincipalDao principalDao) {
		this.principalDao = principalDao;
	}

	@Inject
	public void setPrincipalMembershipDaoDao(PrincipalMembershipDao principalMembershipDao) {
		this.principalMembershipDao = principalMembershipDao;
	}

	@Inject
	public void setAccessRightDao(AccessRightDao accessRightDao) {
		this.accessRightDao = accessRightDao;
	}

	@Inject
	public void setAccessControlDao(AccessControlDao accessControlDao) {
		this.accessControlDao = accessControlDao;
	}

	@Inject
	public void setSecurityContextAccessor(SecurityContextAccessor securityContextAccessor) {
		this.securityContextAccessor = securityContextAccessor;
	}
	
	@Resource(name = "accessRightDefinitionsFile")
	public void setAccessRightDefinitionsFile(File file) {
		accessRightDefinitionsFile = file;
	}

	private static Unmarshaller getAccessRightsDefinitionUnmarshaller() throws JAXBException {

		synchronized (accessRightsDefinitionCtxLock) {
			if (accessRightsDefinitionCtx == null)
				accessRightsDefinitionCtx = JAXBContext.newInstance(AccessRightDefinitions.class);
			return accessRightsDefinitionCtx.createUnmarshaller();
		}
	}
	
	private static AccessRightDefinitions unmarshalAccessRightDefinitions(String definition) throws JAXBException {

		AccessRightDefinitions definitions = (AccessRightDefinitions) getAccessRightsDefinitionUnmarshaller()
				.unmarshal(new StringReader(definition));

		return definitions;
	}
	
	private static Unmarshaller getPrincipalMetadataUnmarshaller() throws JAXBException {

		synchronized (principalMetadataCtxLock) {
			if (principalMetadataCtx == null)
				principalMetadataCtx = JAXBContext.newInstance(PrincipalMetadata.class);
			return principalMetadataCtx.createUnmarshaller();
		}
	}
	
	private static PrincipalMetadata unmarshalPrincipalMetadata(String metadata) throws JAXBException {
		
		return (PrincipalMetadata) getPrincipalMetadataUnmarshaller()
				.unmarshal(new StringReader(metadata));
	}

	private static Marshaller getPrincipalMetadataMarshaller() throws JAXBException {
		synchronized (principalMetadataCtxLock) {
			if(principalMetadataCtx == null)
				principalMetadataCtx = JAXBContext.newInstance(PrincipalMetadata.class);
			Marshaller marshaller =  principalMetadataCtx.createMarshaller();
			marshaller.setProperty("jaxb.fragment", Boolean.TRUE);
			return marshaller;
		}
	}
	
	private String marshalPrincipalMetadata(PrincipalMetadata metadata) throws JAXBException {
		Marshaller m = getPrincipalMetadataMarshaller();
		StringWriter sw = new StringWriter();
		m.marshal(metadata, sw);
		return sw.toString();
	}
	
	@PostConstruct
	private void prefetchData() throws Exception {

		boolean hierarchyLocked = false, accessDataLocked = false;
		AccessRightDefinitions accessRightDefs = null;
		try {
			hierarchyLock.writeLock().lock();
			hierarchyLocked = true;
			accessDataLock.writeLock().lock();
			accessDataLocked = true;
			accessRightDefs = unmarshalAccessRightDefinitions(IOUtils.toString(new BufferedInputStream(
					new FileInputStream(accessRightDefinitionsFile)), Charsets.UTF_8));
			hierarchy = AccessRightHierarchyConverter.createHierarchy(accessRightDefs);
			accessRights = accessRightDao.getAll();
			for (AccessRight ar : accessRights) {
				if (!accessRightsLookup.containsKey(ar.getPrincipal().getId()))
					accessRightsLookup.put(ar.getPrincipal().getId(), new HashMap<UUID, AccessRightStatus>());
				Map<UUID, AccessRightStatus> rMap = accessRightsLookup.get(ar.getPrincipal().getId());
				rMap.put(ar.getRight(), ar.getValue());
			}
		} finally {
			if (hierarchyLocked)
				hierarchyLock.writeLock().unlock();
			if (accessDataLocked)
				accessDataLock.writeLock().unlock();
		}
	}
	
	private boolean resolve(UUID rightId) throws SecurityException {

		boolean locked = false;
		try {
			accessDataLock.readLock().lock();
			locked = true;
			List<Principal> groups = securityContextAccessor.getRoles();
			boolean grant = false;
			for (Principal g : groups) {
				
				Map<UUID, AccessRightStatus> pMap = accessRightsLookup.get(g.getId());
				if (pMap == null)
					throw new SecurityException("Access right status for principal " + g.getId() + " not found");
				AccessRightStatus status = pMap.get(rightId);
				if (status == null)
					status = AccessRightStatus.NOT_GRANTED;
					//throw new SecurityException("Access right status for principal " + g.getId() + " and right " + rightId + " not found");
				if (status == AccessRightStatus.DENIED)
					return false;
				if (status == AccessRightStatus.GRANTED)
					grant = true;
			}
			return grant;
		} catch (Exception e) {
			throw new SecurityException(e);
		} finally {
			if (locked)
				accessDataLock.readLock().unlock();
		}
	}

	public boolean checkRight(UUID rightId) throws SecurityException {
		
		return checkRight(rightId, null);
	}
	
	public boolean checkRight(UUID rightId, AccessRightType rightType) throws SecurityException {
		return checkRight(rightId, rightType, AccessRightResolutionOperator.AND);
	}
	
	private boolean checkRight(UUID rightId, AccessRightType rightType, AccessRightResolutionOperator op) throws SecurityException {

		boolean locked = false;
		try {
			hierarchyLock.readLock().lock();
			locked = true;
			AccessRightNode n = hierarchy.lookup(rightId);
			if (n == null)
				throw new SecurityException("Access right " + rightId + " not found");
			if (n.isLeaf())
				return resolve(rightId);
			else {
				AccessRightInternalNode in = (AccessRightInternalNode) n;
				boolean grant = false;
				for (UUID c : in.getChildren()) {
					if(rightType != null) {
						AccessRightNode cn = hierarchy.lookup(c);
						if(cn.isLeaf()) {
							AccessRightLeafNode cln = (AccessRightLeafNode)cn;
							if(cln.getRightType() != rightType)
								continue;
						}
					}
					boolean cr = checkRight(c, rightType, op);
					if(op == AccessRightResolutionOperator.AND && !cr)
						return false;
					else if(cr)
						grant = true;
				}
				return grant;
			}
		} finally {
			if (locked)
				hierarchyLock.readLock().unlock();
		}
	}

	public boolean canViewUniverse() throws Exception {

		return canXUniverse(AccessRightType.View);
	}

	public boolean canEditUniverse() throws Exception {

		return canXUniverse(AccessRightType.Edit);
	}

	private boolean canXUniverse(AccessRightType rightType) throws SecurityException {
		boolean locked = false;
		try {
			hierarchyLock.readLock().lock();
			locked = true;
			for (AccessRightNode n : hierarchy.getTop()) {
				return checkHierarchyRight(rightType, n);
			}
			return true;
		} catch (Exception e) {
			throw new SecurityException(e);
		} finally {
			if (locked)
				hierarchyLock.readLock().unlock();
		}
	}
	
	// TODO: na dexetai ena
	
	private boolean checkHierarchyRight(AccessRightType rightType, AccessRightNode hierarchyAccessRightNode)  throws SecurityException{
		if (hierarchyAccessRightNode.isLeaf()) {
			AccessRightLeafNode ln = (AccessRightLeafNode) hierarchyAccessRightNode;
			if (ln.getRightType().equals(rightType)) {
				if (!checkRight(ln.getId(), rightType))
					return false;
			}
		} else {
			if (!checkRight(hierarchyAccessRightNode.getId(), rightType))
				return false;
		}
		return true;
	}
	
	public boolean canEditUserOperations() throws SecurityException, Exception {
		return canEditRight(ACCESS_RIGHT_USER_OPERATIONS, AccessRightType.Edit);
	}

	private boolean canEditRight(UUID userOperationsID, AccessRightType rightType) throws Exception {
		return canXRight(userOperationsID, rightType);
	}
	
	private boolean canXRight(UUID rightId, AccessRightType rightType) throws Exception {
		if (rightId == null) throw new Exception("Not a valid right id");
		boolean locked = false;
		try {
			hierarchyLock.readLock().lock();
			locked = true;
			
			AccessRightNode arn;
			if ((arn = hierarchy.lookup(rightId))==null) throw new SecurityException("Not a right with id: " + rightId);
			return checkHierarchyRight(rightType, arn);
		} catch (Exception e) {
			throw new SecurityException(e);
		} finally {
			if (locked)
				hierarchyLock.readLock().unlock();
		}
	}
	
	@Transactional(readOnly = true)
	public List<Principal> allPrincipals(boolean active, boolean loadDetails) throws Exception
	{
		List<Principal> all = !active ? principalDao.getAll() : principalDao.findActivePrincipals();
		if(loadDetails)
		{
			for(Principal principal : all)
				principalDao.loadDetails(principal);
		}
		return all;
	}
	
	
	@Transactional(readOnly = true)
	public Principal findPrincipalById(UUID id, boolean loadDetails) {
		Principal p = principalDao.read(id);
		if(loadDetails) principalDao.loadDetails(p);
		return p;
	}
	
	@Transactional(readOnly = true)
	public Principal findPrincipalByName(String name) throws Exception{
		return findPrincipalByName(name, false);
	}
	
	@Transactional(readOnly = true)
	public Principal findPrincipalByName(String name, boolean loadDetails) throws Exception{
		Principal p =  principalDao.findActivePrincipalByName(name);
		if(loadDetails)
			principalDao.loadDetails(p);
		return p;
	}

	
	
	@Transactional
	@CacheEvict(value = {"gr.cite.geoanalytics.security.principalCache", "gr.cite.geoanalytics.security.principalMetadataCache", "gr.cite.geoanalytics.security.principalRoleCache"}, allEntries = true)
	public void deletePrincipalMembershipByUser(Principal principal) throws Exception
	{
		Principal p;
		if ((p = findPrincipalById(principal.getId(), true)) == null) {
			log.error("User " + principal.getId() + " does not exist");
			throw new Exception("User " + principal.getId() + " does not exist");
		}
		principalDao.delete(principal);
	}
	
	@CacheEvict(value = {"gr.cite.geoanalytics.security.principalCache", "gr.cite.geoanalytics.security.principalMetadataCache", "gr.cite.geoanalytics.security.principalRoleCache"}, allEntries = true)
	@Transactional(rollbackFor = Exception.class)
	public void updatePrincipal(RoleMessengerUpdate rmu, List<String> roles) throws Exception {
		
		Principal ex = findPrincipalById(UUID.fromString(rmu.getId()), true);
		if (ex == null) {
			log.error("Principal " + rmu.getId() + " does not exist");
			throw new Exception("Principal " + rmu.getId() + " does not exist");
		}
		if (!ex.getName().equals(rmu.getName())) throw new Exception("Not a valid name");
		if ((!ex.getName().equals(rmu.getNameNew()))) {
			ex.setName(rmu.getNameNew());
			String[] pieces = ex.getUri().toString().split(rmu.getName());
			String newUri = pieces[0]+rmu.getNameNew();
			ex.setUri(URI.create(newUri).toString());
		}
		ex.setCreationDate(ex.getCreationDate());
		
		Map<String, Principal> rolesByName = new HashMap<String, Principal>();
		List<PrincipalMembership> rolesOfPrincipal = principalMembershipDao.findPrincipalMembershipByUser(ex);
		for (PrincipalMembership role: rolesOfPrincipal)
			rolesByName.put(role.getGroup().getName(), role.getGroup());
		for(PrincipalMembership rn: rolesOfPrincipal) {
			if (roles.contains(rn.getGroup().getName())) continue;
			else ex.getGroupsPrincipal().remove(rn);
		}
		for(String rn: roles) {
			if (rolesByName.get(rn)!=null) continue;
			else {
				PrincipalMembership pMembership = new PrincipalMembership();
				pMembership.setMember(ex);
				Principal newR = principalDao.findActivePrincipalByName(rn);
				pMembership.setGroup(newR);
				
				ex.getGroupsPrincipal().add(pMembership);
			}
		}
		principalDao.update(ex);
	}
	
	
	/*
	 *nameNew and Roles are not null 
	 */
	@Transactional(rollbackFor = Exception.class)
	public void createNewUser(RoleMessengerUpdate rmu) throws Exception {
		
		Principal newUser = new Principal();
		newUser.setClassId(PrincipalClass.ITEM.classCode());
		newUser.setIsActive(ActiveStatus.ACTIVE);
		newUser.setName(rmu.getNameNew());
		newUser.setUri(rmu.getUri().toString());
		principalDao.create(newUser);
		
		Set<PrincipalMembership> roles = new HashSet<>();
		for (String roleName: rmu.getRoles()) {
			Principal role = findPrincipalByName(roleName);
			
			PrincipalMembership principalMembership = new PrincipalMembership();
			principalMembership.setMember(newUser);
			principalMembership.setGroup(role);
			roles.add(principalMembership);
		}
		newUser.getGroupsPrincipal().addAll(roles);
		
		principalDao.update(newUser);
	}
	
	@Transactional(readOnly = true)
	public UserManagementMessenger getUsers() throws Exception {
		List<Principal> principals = allPrincipals(true, false);
		List<UserMessenger> users = new ArrayList<>();
		List<Principal> items = new ArrayList<>();
		principals.stream()
				  .filter(p-> PrincipalClass.fromIType(p.getClassId()) == PrincipalClass.ITEM)
				  .forEach(p-> {
					  try {
						  UserMessenger uM = new UserMessenger();
						  uM.setName(p.getName());
						  uM.setId(p.getId());
						  List<RoleMessenger> roles = new ArrayList<>();
						  findRolesByPrincipal(p).forEach(role ->{
							 RoleMessenger rM = new RoleMessenger();
							 rM.setName(role.getName());
							 rM.setId(role.getId());
							 roles.add(rM);
						  });
						  uM.setRoles(roles);
						  users.add(uM);
						  items.add(p);
					  } catch (Exception e) {
						log.error("Could not respond with error", e);
					}
				  });
		
		/* Just TEST. Remove above */
//		int i=0;
//		for (UserMessenger um: users) {
//			if (i==1) {
//				List<RoleMessenger> roles = users.get(0).getRoles();
//				List<RoleMessenger> rolesM = um.getRoles();
//				rolesM.addAll(roles);
//				um.setRoles(rolesM);
//			}
//			i++;
//		}
		/*                         */
		
		
		UserManagementMessenger messenger = new UserManagementMessenger();
		messenger.setUsers(users);
		
		principals.removeAll(items);
		List<RoleMessenger> rms = new ArrayList<>();
		for (Principal p: principals) {
			RoleMessenger rm = new RoleMessenger();
			rm.setId(p.getId());
			rm.setName(p.getName());
			rms.add(rm);
		}
		messenger.setAllRoles(rms);
		return messenger;
	}
	
	
	@Transactional(readOnly = true)
	public boolean isPrincipalAnonymous() {
		try {
			Principal p = securityContextAccessor.getPrincipal();
			PrincipalMetadata pm = getPrincipalMetadata(p);
			if(pm == null)
				return false;
			return pm.isAnonymous();
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Cacheable(value = "gr.cite.geoanalytics.security.principalCache", key = "#username.toString() + #active.toString")
	@Transactional(readOnly = true)
	public Principal findPrincipalBySystemName(String username, boolean active) {
		Principal p = principalDao.findPrincipalByNameAndActivityStatus(username, active ? ActiveStatus.ACTIVE : ActiveStatus.INACTIVE);
		principalDao.loadDetails(p);
		return p;
	}
	
	@Cacheable(value = "gr.cite.geoanalyticsaeus.security.principalCache", key = "#username.toString() + false.toString")
	@Transactional(readOnly = true)
	public Principal findPrincipalBySystemName(String username) {
		Principal p = principalDao.findPrincipalByNameAndActivityStatus(username, ActiveStatus.ACTIVE);
		principalDao.loadDetails(p);
		return p;
	}
	
	@Transactional(readOnly = true)
	public Principal findPrincipalByID(UUID id) {
		return principalDao.read(id);
	}
	
	
	@Transactional(readOnly = true)
	@Cacheable(value = "gr.cite.geoanalytics.security.principalMetadataCache", key = "#p")
	public PrincipalMetadata getPrincipalMetadata(Principal p) throws Exception {
		return p.getMetadata() != null ? unmarshalPrincipalMetadata(p.getMetadata()) : new PrincipalMetadata();
	}
	
	@Cacheable(value = "gr.cite.geoanalytics.security.principalRoleCache", key = "#principal")
	@Transactional(readOnly = true)
	public List<Principal> findRolesByPrincipal(Principal principal) throws ExecutionException {
		List<Principal> roles = principalMembershipDao.findRolesByPrincipal(principal);
		roles.forEach(r -> principalDao.loadDetails(r));
		return roles;
	}
	
	@Transactional(readOnly = true)
	public List<Principal> findAnonymousPrincipals() throws Exception {
		List<Principal> principals = principalDao.getAll();
		
		List<Principal> anonymous = new ArrayList<Principal>();
		for(Principal p : principals) {
			
			PrincipalMetadata pm = getPrincipalMetadata(p);
			if(pm == null) continue;
			if(pm.isAnonymous())
				anonymous.add(p);
		}
		
		return anonymous;
		
	}
	
	@Transactional(readOnly = true)
	public Principal findUniqueAnonymousPrincipal() throws Exception {
		
	//	return findPrincipalBySystemName("admin");
		List<Principal> ps = findAnonymousPrincipals();
		
		if(ps.isEmpty()) return null;
		if(ps.size() > 1) throw new Exception("Non unique anonymous principal");
		return ps.get(0);
		
	}
	
//	public String getPrincipalSelectedLanguage() {
//		try {
//			if(isPrincipalAnonymous()) {
//				if(request.getCookies() != null) {
//					Cookie guestLangC = Arrays.stream(request.getCookies()).filter(x -> x.getName().equalsIgnoreCase(Context.SC_DAM_GUEST_LANG_COOKIE)).findFirst().orElse(null);
//					String guestLang = guestLangC != null ? guestLangC.getValue() : null;
//					if(guestLang != null)
//						return guestLang;
//				}
//			}
//			String pms = securityContextAccessor.getPrincipal().getMetadata();
//			if(pms == null)
//				return Context.NoLanguageKey;
//			PrincipalMetadata pm = principalMetadataCache.get(securityContextAccessor.getPrincipal());
//			if(pm.getPreferences() == null)
//				return Context.NoLanguageKey;
//			return pm.getPreferences().getLanguage();
//		}catch(Exception e) {
//			throw new RuntimeException(e);
//		}
//	}
//	
//	@Transactional(rollbackFor=Exception.class)
//	public void switchPrincipalSelectedLanguage(Language language) throws Exception {
//		Principal p = securityContextAccessor.getPrincipal();
//		PrincipalMetadata pm = null;
//		PrincipalPreferences prefs = null;
//		if(p.getMetadata() == null) {
//			pm = new PrincipalMetadata();
//			pm.setAnonymous(false);
//		}else
//			pm = unmarshalPrincipalMetadata(p.getMetadata());
//		if(pm.getPreferences() == null) {
//			prefs = new PrincipalPreferences();
//			pm.setPreferences(prefs);
//		}else
//			prefs = pm.getPreferences();
//		prefs.setLanguage(language.getCode().toString());
//		p.setMetadata(marshalPrincipalMetadata(pm));
//		principalMetadataCache.put(p, pm);
//		principalDao.update(p);
//	}
}
