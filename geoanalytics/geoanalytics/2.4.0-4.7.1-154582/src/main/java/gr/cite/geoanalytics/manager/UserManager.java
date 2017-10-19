package gr.cite.geoanalytics.manager;

import org.springframework.stereotype.Service;

@Service
public class UserManager
{
	/*private CustomerManager customerManager;
	private ConfigurationManager configurationManager;
	private TaxonomyManager taxonomyManager;
	
	private UserDaoOld userDaoOld;
	private TenantDao tenantDao;
	private TenantActivationDao tenantActivationDao;
	private TaxonomyDao taxonomyDao;
	private TaxonomyTermDao taxonomyTermDao;
	private ShapeDao shapeDao;
	
	private JAXBContext rightsCtx = null;
	
	public UserManager() { }
	
	@Inject
	public UserManager(CustomerManager customerManager, ConfigurationManager configurationManager, 
			TaxonomyManager taxonomyManager)
	{
		this.customerManager = customerManager;
		this.configurationManager = configurationManager;
		this.taxonomyManager = taxonomyManager;
	}
	
	@Inject
	public void setUserDao(UserDaoOld userDaoOld) {
		this.userDaoOld = userDaoOld;
	}
	
	@Inject
	public void setCustomerDao(TenantDao tenantDao) {
		this.tenantDao = tenantDao;
	}
	
	@Inject
	public void setCustomerActivationDao(TenantActivationDao tenantActivationDao) {
		this.tenantActivationDao = tenantActivationDao;
	}
	
	@Inject
	public void setTaxonomyDao(TaxonomyDao taxonomyDao) {
		this.taxonomyDao = taxonomyDao;
	}
	
	@Inject
	public void setTaxonomyTermDao(TaxonomyTermDao taxonomyTermDao) {
		this.taxonomyTermDao = taxonomyTermDao;
	}
	
	@Inject
	public void setShapeDao(ShapeDao shapeDao) {
		this.shapeDao = shapeDao;
	}
	
	@Transactional(readOnly = true)
	public User systemUser() throws Exception
	{
		User sys = userDaoOld.systemUser();
		if(sys == null) throw new Exception("No system user found");
		return sys;
	}
	
	@Transactional(readOnly = true)
	public User findById(String id) throws Exception
	{
		return userDaoOld.read(UUID.fromString(id));
	}
	
	@Transactional(readOnly = true)
	public User findBySystemName(String name, boolean loadDetails) throws Exception
	{
		User u = userDaoOld.findBySystemName(name);
		if(loadDetails)
		{
			if(u.getTenant() != null) u.getTenant().getName();
			u.getCreator().getSystemName();
		}
		return u;
	}
	
	@Transactional(readOnly = true)
	public User findBySystemName(String name) throws Exception
	{
		return findBySystemName(name, false);
	}
	
	@Transactional(readOnly = true)
	public List<User> findByRole(String role, boolean loadDetails) throws Exception
	{
		if(rightsCtx == null) rightsCtx = JAXBContext.newInstance(UserRights.class);
		
		Unmarshaller um = rightsCtx.createUnmarshaller();
		
		List<User> users = userDaoOld.getAll();
		List<User> res = new ArrayList<User>();
		for(User u : users)
		{
			UserRights rights = (UserRights) um.unmarshal(new StringReader(u.getRights()));
			if(rights.getRoles().contains(role))
				res.add(u);
			if(loadDetails)
			{
				if(u.getTenant() != null) u.getTenant().getName();
				u.getCreator().getSystemName();
			}
		}
		return res;
	}
	
	@Transactional(readOnly = true)
	public List<User> findByRole(String role) throws Exception
	{
		return findByRole(role, false);
	}
	
	@Transactional(readOnly = true)
	public User findBySystemNameAndCustomer(String name, String customerName) throws Exception
	{
		User u =  userDaoOld.findBySystemName(name);
		if(u != null) u.getCreator().getSystemName();
		if(customerName == null || (u != null && u.getTenant() != null && u.getTenant().getName().equals(customerName)))
			return u;
		return null;
	}
	
	@Transactional(readOnly = true)
	public boolean isLocked(User u) throws Exception
	{
		if(u == null) throw new Exception("Unspecified user");
		if(rightsCtx == null) rightsCtx = JAXBContext.newInstance(UserRights.class);
		
		Unmarshaller um = rightsCtx.createUnmarshaller();
		UserRights rights = (UserRights) um.unmarshal(new StringReader(u.getRights()));
		
		return rights.isLocked();
		
	}
	
	private void setUserLocked(User u, boolean locked) throws Exception
	{
		if(u == null) throw new Exception("Unspecified user");
		if(rightsCtx == null) rightsCtx = JAXBContext.newInstance(UserRights.class);
		
		Unmarshaller um = rightsCtx.createUnmarshaller();
		UserRights rights = (UserRights) um.unmarshal(new StringReader(u.getRights()));
		
		rights.setLocked(locked);
		
		Marshaller m  = rightsCtx.createMarshaller();
		StringWriter sw = new StringWriter();
		m.marshal(rights, sw);
		
		u.setRights(sw.toString());
		
		userDaoOld.update(u);
	}
	
	@Transactional
	public void lockUser(User u) throws Exception
	{
		setUserLocked(u, true);
	}
	
	@Transactional
	public void unlockUser(User u) throws Exception
	{
		setUserLocked(u, false);
	}
	
	@Transactional(readOnly = true)
	public List<String> getAccessibleLayers(User u) throws Exception
	{
		Set<String> layers = new HashSet<String>();
		TaxonomyConfig geogTaxonomy = null;
		List<TaxonomyConfig> geogTaxonomys = configurationManager.retrieveTaxonomyConfig(TaxonomyConfig.Type.GEOGRAPHYTAXONOMY, false);

		if(geogTaxonomys == null) return new ArrayList<String>();
		
		List<TaxonomyTerm> defaultTerms = new ArrayList<TaxonomyTerm>();
		
		for (TaxonomyConfig geoTaxonomy : geogTaxonomys){
			defaultTerms.addAll(taxonomyManager.getTopmostTermsOfTaxonomy(geoTaxonomy.getId(), false));
		}
		
		for(TaxonomyTerm tt : defaultTerms)
		{
			LayerConfig lcfg = configurationManager.getLayerConfig(tt);
			if(lcfg != null) layers.add(lcfg.getName());
		}
			
		//TODO determine open data
		
		if(u == null) return new ArrayList<String>(layers);
		
		if(rightsCtx == null) rightsCtx = JAXBContext.newInstance(UserRights.class);
		Unmarshaller um = rightsCtx.createUnmarshaller();
		UserRights rights = (UserRights)um.unmarshal(new StringReader(u.getRights()));
		
		boolean activeForAll = u.getTenant() == null || rights.getRoles().contains("ROLE_admin") ||
				customerManager.isActiveForAll(u.getTenant());
		
		Set<TaxonomyTerm> grantedTerms = new HashSet<TaxonomyTerm>();
		if(!activeForAll)
		{
			List<Shape> shapes = customerManager.getGrantedShapes(u.getTenant());	
			for(Shape s : shapes)
				grantedTerms.add(shapeDao.findTermOfShape(s)); //top geography terms
		}else
			for(TaxonomyConfig gt : geogTaxonomys){
				grantedTerms.addAll(taxonomyManager.getTopmostTermsOfTaxonomy(gt.getId(), false));
			}
		
		List<TaxonomyConfig> layerTaxonomys = configurationManager.retrieveTaxonomyConfig(TaxonomyConfig.Type.LAYERTAXONOMY, false);
		
		List<Taxonomy> taxonomys = new ArrayList<Taxonomy>();
		for(TaxonomyConfig lt : layerTaxonomys){
			taxonomys.add(taxonomyManager.findTaxonomyById(lt.getId(), false));
		}
		
		//Taxonomy layert = taxonomyManager.findTaxonomyById(layerTaxonomy.getId(), false);
		
		List<TaxonomyTerm> layerGranted = new ArrayList<TaxonomyTerm>();
		if(!activeForAll)
		{
			for(TaxonomyTerm tt : grantedTerms){
				for(Taxonomy taxonomy : taxonomys){
					layerGranted.addAll(taxonomyTermDao.getActiveLinkedTerms(taxonomy, tt, TaxonomyTermLink.Verb.LayerFor));
				}
			}
		}else{
			for(Taxonomy taxonomy : taxonomys){
				layerGranted.addAll(taxonomyDao.getTerms(taxonomy));
			}
		}
		grantedTerms.addAll(layerGranted);
	
		TaxonomyConfig landUseTaxonomy = null;
		List<TaxonomyConfig> landUseTaxonomys = configurationManager.retrieveTaxonomyConfig(TaxonomyConfig.Type.LANDUSETAXONOMY, false);
		if (landUseTaxonomys != null){
			landUseTaxonomy = landUseTaxonomys.get(0);
		}
		Taxonomy lut = taxonomyManager.findTaxonomyById(landUseTaxonomy.getId(), false);
		
		List<TaxonomyTerm> landUseGranted = new ArrayList<TaxonomyTerm>();
		if(!activeForAll)
		{
			for(TaxonomyTerm tt : grantedTerms)
				landUseGranted.addAll(taxonomyTermDao.getActiveLinkedTerms(lut, tt, TaxonomyTermLink.Verb.LandUseFor));
		}else
			landUseGranted.addAll(taxonomyDao.getTerms(lut));
		grantedTerms.addAll(landUseGranted);
		
		TaxonomyConfig poiTaxonomy = null;
		List<TaxonomyConfig> poiTaxonomys = configurationManager.retrieveTaxonomyConfig(TaxonomyConfig.Type.POITAXONOMΥ, false);
		if (poiTaxonomys != null){
			poiTaxonomy = poiTaxonomys.get(0);
		}
		if(poiTaxonomy != null)
		{
			Taxonomy poit = taxonomyManager.findTaxonomyById(poiTaxonomy.getId(), false);
			
			List<TaxonomyTerm> poiGranted = new ArrayList<TaxonomyTerm>();
			if(!activeForAll)
			{
				for(TaxonomyTerm tt : grantedTerms)
						poiGranted.addAll(taxonomyTermDao.getActiveLinkedTerms(poit, tt, TaxonomyTermLink.Verb.POIFor));
			}else
				poiGranted.addAll(taxonomyDao.getTerms(poit));
		}
		
		TaxonomyConfig siteTaxonomy = null;
		List<TaxonomyConfig> siteTaxonomys = configurationManager.retrieveTaxonomyConfig(TaxonomyConfig.Type.SITETAXONOMY, false);
		if (siteTaxonomys != null){
			siteTaxonomy = siteTaxonomys.get(0);
		}
		if(siteTaxonomy != null)
		{
			Taxonomy sitet = taxonomyManager.findTaxonomyById(siteTaxonomy.getId(), false);
			
			List<TaxonomyTerm> siteGranted = new ArrayList<TaxonomyTerm>();
			if(!activeForAll)
			{
				for(TaxonomyTerm tt : grantedTerms)
					siteGranted.addAll(taxonomyTermDao.getActiveLinkedTerms(sitet, tt, TaxonomyTermLink.Verb.SiteFor));
			}else
				siteGranted.addAll(taxonomyDao.getTerms(sitet));
		}
		
		List<TaxonomyTerm> grantedTermDescendants = new ArrayList<TaxonomyTerm>();
		for(TaxonomyTerm tt : grantedTerms)
		{
			grantedTermDescendants.addAll(taxonomyManager.getChildrenOfTerm(tt.getId().toString(), true, false));
		}
		grantedTerms.addAll(grantedTermDescendants);
		
		for(TaxonomyTerm tt : grantedTerms)
		{
			LayerConfig lcfg = configurationManager.getLayerConfig(tt);
			if(lcfg != null) layers.add(lcfg.getName());
		}
		
		return new ArrayList<String>(layers);
	}
	
	@Transactional
	public void update(User u, boolean create) throws Exception
	{
		if(rightsCtx == null) rightsCtx = JAXBContext.newInstance(UserRights.class);
		
		Unmarshaller um = rightsCtx.createUnmarshaller();
		UserRights creatorRights = (UserRights) um.unmarshal(new StringReader(u.getCreator().getRights()));
		UserRights userRights = (UserRights) um.unmarshal(new StringReader(u.getRights()));
		
		if(!creatorRights.getRoles().contains("ROLE_admin"))
		{
			userRights.getLayers().retainAll(creatorRights.getLayers());
			userRights.getRoles().retainAll(creatorRights.getRoles());
		}
		
		Marshaller m = rightsCtx.createMarshaller();
		StringWriter sw = new StringWriter();
		m.marshal(userRights, sw);
		u.setRights(sw.toString());
		
		
		if(create) userDaoOld.create(u);
		else userDaoOld.update(u);
	}
	
	@Transactional(readOnly = true)
	public List<String> listUsers() throws Exception
	{
		return userDaoOld.listSystemNames();
	}
	
	@Transactional(readOnly = true)
	public List<String> listActiveUsers() throws Exception
	{
		return userDaoOld.listSystemNamesOfActive();
	}
	
	@Transactional(readOnly = true)
	public List<String> listUsersOfCustomer(String customerName, boolean activeCustomer) throws Exception
	{
		List<String> res = new ArrayList<String>();
		List<Tenant> cs = tenantDao.findByName(customerName);
		for(Tenant c : cs)
		{
			if(activeCustomer)
			{
				List<TenantActivation> cas = tenantActivationDao.findActive(c);
				if(cas != null && !cas.isEmpty())
					res.addAll(userDaoOld.listSystemNamesByCustomer(c));
			}else res.addAll(userDaoOld.listSystemNamesByCustomer(c));
		}
		return res;
	}
	
	@Transactional(readOnly = true)
	public List<String> searchUsers(List<String> userNames) throws Exception
	{
		return userDaoOld.searchByName(userNames);
	}
	
	@Transactional(readOnly = true)
	public List<String> searchUsersOfCustomer(List<String> userNames, String customerName, boolean activeCustomer) throws Exception
	{
		List<String> res = new ArrayList<String>();
		List<Tenant> cs = tenantDao.findByName(customerName);
		for(Tenant c : cs)
		{
			if(activeCustomer)
			{
				List<TenantActivation> cas = tenantActivationDao.findActive(c);
				if(cas != null && !cas.isEmpty())
					res.addAll(userDaoOld.searchByNameAndCustomer(userNames, c));
			}else res.addAll(userDaoOld.searchByNameAndCustomer(userNames, c));
		}
		return res;
	}
	
	
	
	@Transactional(rollbackFor={Exception.class})
	public void deleteBySystemNameAndCustomer(List<PrincipalTenantPair> users) throws Exception
	{
		for(PrincipalTenantPair p : users)
		{
			User u;
			if(p.getTenant() != null)
			{
				u = findBySystemNameAndCustomer(p.getPrincipal(), p.getTenant()); 
				if(u == null) throw new Exception("User " + p.getPrincipal() + " of customer " + p.getTenant() + " was not found");
			}else
			{
				u = findBySystemName(p.getPrincipal());
				if(u == null) throw new Exception("User " + p.getPrincipal() + " was not found");
				if(u.getTenant() != null) throw new Exception("Invalid user identity");
			}
			userDaoOld.delete(u);
		}
	}*/
}
