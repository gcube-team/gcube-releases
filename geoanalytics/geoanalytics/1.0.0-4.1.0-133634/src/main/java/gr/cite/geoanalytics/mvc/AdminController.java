package gr.cite.geoanalytics.mvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import gr.cite.gaap.datatransferobjects.AccountLockInfo;
import gr.cite.gaap.datatransferobjects.AccountingMessenger;
import gr.cite.gaap.datatransferobjects.AccountingSearchSelection;
import gr.cite.gaap.datatransferobjects.TenantActivationMessenger;
import gr.cite.gaap.datatransferobjects.TenantListInfo;
import gr.cite.gaap.datatransferobjects.CustomerMessenger;
import gr.cite.gaap.datatransferobjects.CustomerSearchSelection;
import gr.cite.gaap.datatransferobjects.DocumentMessenger;
import gr.cite.gaap.datatransferobjects.IllegalAccessAuditingInfo;
import gr.cite.gaap.datatransferobjects.LayerStyleMessenger;
import gr.cite.gaap.datatransferobjects.LayerStyleUpdate;
import gr.cite.gaap.datatransferobjects.SystemToggleInfo;
import gr.cite.gaap.datatransferobjects.TaxonomyMessenger;
import gr.cite.gaap.datatransferobjects.TaxonomySearchSelection;
import gr.cite.gaap.datatransferobjects.TaxonomyTermInfo;
import gr.cite.gaap.datatransferobjects.TaxonomyTermLinkInfo;
import gr.cite.gaap.datatransferobjects.TaxonomyTermLinkMessenger;
import gr.cite.gaap.datatransferobjects.TaxonomyTermMessenger;
import gr.cite.gaap.datatransferobjects.ThemeCreation;
import gr.cite.gaap.datatransferobjects.UpdateResponse;
import gr.cite.gaap.datatransferobjects.PrincipalDeleteSelection;
import gr.cite.gaap.datatransferobjects.UserLastLoginInfo;
import gr.cite.gaap.datatransferobjects.UserListInfo;
import gr.cite.gaap.datatransferobjects.PrincipalMessenger;
import gr.cite.gaap.datatransferobjects.UserSearchSelection;
import gr.cite.gaap.geospatialbackend.GeospatialBackend;
import gr.cite.gaap.servicelayer.ConfigurationManager;
import gr.cite.gaap.servicelayer.DocumentManager;
import gr.cite.gaap.servicelayer.TaxonomyManager;
import gr.cite.gaap.servicelayer.DocumentManager.DocumentInfo;
import gr.cite.gaap.utilities.HtmlUtils;
import gr.cite.geoanalytics.context.Configuration;
import gr.cite.geoanalytics.manager.AccountingManager;
import gr.cite.geoanalytics.manager.TenantManager;
import gr.cite.geoanalytics.manager.ImportManager;
import gr.cite.geoanalytics.manager.PrincipalManager;
import gr.cite.geoanalytics.manager.SystemManager;
import gr.cite.geoanalytics.manager.admin.AdministrationManager;
import gr.cite.geoanalytics.security.PasswordAuthentication;
import gr.cite.geoanalytics.security.SecurityContextAccessor;
import gr.cite.geoanalytics.dataaccess.entities.ActiveStatus;
import gr.cite.geoanalytics.dataaccess.entities.accounting.Accounting;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.principal.PrincipalData;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.global.TaxonomyConfig;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.layer.LayerConfig;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.mapping.AttributeMappingConfig;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.mapping.SystemMappingConfig;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.presentation.SystemPresentationConfig;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.presentation.Theme;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.Taxonomy;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTerm;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTermLink;
import gr.cite.geoanalytics.dataaccess.entities.tenant.Tenant;
import gr.cite.geoanalytics.dataaccess.entities.tenant.TenantActivation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AdminController {
	private AdministrationManager adminMan;
	private ConfigurationManager configMan;
	private SystemManager systemManager;
	private TenantManager tenantManager;
	private TaxonomyManager taxonomyMan;
	private DocumentManager documentMan;
	private GeospatialBackend shapeMan;
	private ImportManager layerMan;
	private AccountingManager accountingMan;
	private SecurityContextAccessor securityContextAccessor;
	private Configuration configuration;
	private PrincipalManager principalManager;
	private PasswordAuthentication passwordEncoder;
	
	private static final Logger log = LoggerFactory.getLogger(AdminController.class);
	
	@Inject
	public AdminController(AdministrationManager adminMan, ConfigurationManager configMan,
			PrincipalManager principalManager, TenantManager customerMan, TaxonomyManager taxonomyMan,
			DocumentManager documentMan, GeospatialBackend shapeMan, ImportManager importMan, 
			AccountingManager accountingMan, SecurityContextAccessor securityContextAccessor)
	{
		this.adminMan = adminMan;
		this.configMan = configMan;
		this.principalManager = principalManager;
		this.tenantManager = customerMan;
		this.taxonomyMan = taxonomyMan;
		this.documentMan = documentMan;
		this.shapeMan = shapeMan;
		this.layerMan = importMan;
		this.accountingMan = accountingMan;
		this.securityContextAccessor = securityContextAccessor;
	}
	
	@Inject
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
	
	@Inject
	public void setSystemManager(SystemManager systemManager) {
		this.systemManager = systemManager;
	}
	
	@Inject
	public void setPasswordEncoder(PasswordAuthentication passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}
	
	@RequestMapping("/admin")
	public String showHome(Model model) throws Exception
	{
		return "redirect:/admin/home";
	}
	
	@RequestMapping("/admin/home")
	public String showInfo(Model model) throws Exception
	{
		model.addAllAttributes(adminMan.getAllInfo());
		return "adminPane";
	}
	
	@RequestMapping("/admin/home/allLogins")
	public @ResponseBody Map<String, UserLastLoginInfo> getAllLogins() throws Exception
	{
		return adminMan.getLastUserLogins();
	}
	
	@RequestMapping("/admin/home/alerts/accountLock")
	public @ResponseBody Map<String, AccountLockInfo> getAccountLocks() throws Exception
	{
		return adminMan.getAccountLocks();
	}
	
	@RequestMapping("/admin/home/alerts/illegalRequest")
	public @ResponseBody List<IllegalAccessAuditingInfo> getIllegalRequests() throws Exception
	{
		return adminMan.getIllegalRequests();
	}
	
	@RequestMapping("/admin/home/alerts/illegalLayerAccess")
	public @ResponseBody List<IllegalAccessAuditingInfo> getIllegalLayerAccess() throws Exception
	{
		return adminMan.getIllegalLayerAccesses();
	}
	
	@RequestMapping("/admin/home/alerts/illegalLayerZoom")
	public @ResponseBody List<IllegalAccessAuditingInfo> getIllegalLayerZoom() throws Exception
	{
		return adminMan.getIllegalLayerAccesses();
	}
	
	@RequestMapping("/admin/users")
	public String showUserManagement(Model model) throws Exception
	{
		model.addAttribute(AdministrationManager.EntryType.SystemStatus.toString(), configMan.isSystemOnline());
		return "userManagement";
	}
	
	@RequestMapping("/admin/customers")
	
	public String showCustomerManagement(Model model) throws Exception
	{
		model.addAttribute(AdministrationManager.EntryType.SystemStatus.toString(), configMan.isSystemOnline());
		return "customerManagement";
	}
	
	@RequestMapping("/admin/documents")
	public String showDocumentManagement(Model model) throws Exception
	{
		model.addAttribute(AdministrationManager.EntryType.SystemStatus.toString(), configMan.isSystemOnline());
		return "documentManagement";
	}
	
	@RequestMapping("/admin/taxonomies")
	public String showTaxonomyManagement(Model model) throws Exception
	{
		model.addAttribute(AdministrationManager.EntryType.SystemStatus.toString(), configMan.isSystemOnline());
		return "taxonomyManagement";
	}
	
	@RequestMapping("/admin/shapes")
	public String showShapeManagement(Model model) throws Exception
	{
		model.addAttribute(AdministrationManager.EntryType.SystemStatus.toString(), configMan.isSystemOnline());
		return "shapeManagement";
	}
	
	@RequestMapping("/admin/import")
	public String showDataImport(Model model) throws Exception
	{
		model.addAttribute(AdministrationManager.EntryType.SystemStatus.toString(), configMan.isSystemOnline());
		return "dataImport";
	}
	
	@RequestMapping("/admin/accounting")
	public String showAccounting(Model model) throws Exception
	{
		model.addAttribute(AdministrationManager.EntryType.SystemStatus.toString(), configMan.isSystemOnline());
		return "accountingManagement";
	}
	
	@RequestMapping("/admin/dbBackup")
	public String showDatabaseBackup(Model model) throws Exception
	{
		model.addAttribute(AdministrationManager.EntryType.SystemStatus.toString(), configMan.isSystemOnline());
		return "dbBackup";
	}
	
	@RequestMapping("/admin/presentation")
	public String showPresentation(Model model) throws Exception
	{
		model.addAttribute(AdministrationManager.EntryType.SystemStatus.toString(), configMan.isSystemOnline());
		return "presentation";
	}
	
	@RequestMapping(value = "/admin/users/list", method = RequestMethod.POST)
	public @ResponseBody Map<String, List<UserListInfo>> listUsers(@ModelAttribute UserSearchSelection userSearchSelection) throws Exception {
		Map<String, List<UserListInfo>> res = new HashMap<String, List<UserListInfo>>();
		List<String> tenantNames = tenantManager.listTenants();
		List<String> active = principalManager.listActivePrincipalNames();
		List<String> noCustomerUsers = null;
		
		boolean searchByCustomers = userSearchSelection.tenantNames != null && !userSearchSelection.tenantNames.isEmpty();
		boolean searchByUsers = userSearchSelection.principalNames != null && !userSearchSelection.principalNames.isEmpty();
		
		if(!searchByCustomers)
			noCustomerUsers = userSearchSelection.activePrincipal ? active : principalManager.listPrincipalNames();
		
		if(searchByCustomers)
			tenantNames.retainAll(userSearchSelection.tenantNames);
		
		for(String tenantName : tenantNames) {
			List<String> us = principalManager.listPrincipalNamesByTenantName(tenantName, userSearchSelection.activeTenants);
			res.put(tenantName, new ArrayList<UserListInfo>());
			for(String u : us) {
				boolean uIsActive = active.contains(u);
				if(!userSearchSelection.activePrincipal || uIsActive) 
					res.get(tenantName).add(new UserListInfo(u, tenantName, uIsActive));
			}
			if(!searchByCustomers) {   if(us.isEmpty() && userSearchSelection.activeTenants)
					us.addAll(principalManager.listPrincipalNamesByTenantName(tenantName, false));
				noCustomerUsers.removeAll(us);
			}
		}

		if (searchByUsers) {
			Map<String, List<UserListInfo>> tmp = res;
			res = new HashMap<String, List<UserListInfo>>();
			for (Map.Entry<String, List<UserListInfo>> e : tmp.entrySet()) {
				res.put(e.getKey(), new ArrayList<UserListInfo>());
				for (UserListInfo uli : e.getValue()) {
					if (userSearchSelection.principalNames.contains(uli.name))
						res.get(e.getKey()).add(uli);
				}
			}
		}

		if (!searchByCustomers) {
			for (String u : noCustomerUsers) {
				if (!searchByUsers || userSearchSelection.principalNames.contains(u)) {
					boolean uIsActive = active.contains(u);
					if (!userSearchSelection.activePrincipal || uIsActive) {
						if (!res.containsKey("None"))
							res.put("None", new ArrayList<UserListInfo>());
						res.get("None").add(new UserListInfo(u, null, uIsActive));
					}
				}
			}
		}

		return res;
	}
	
	@RequestMapping(value = "/admin/users/search", method = RequestMethod.POST)
	public @ResponseBody Map<String, List<UserListInfo>> searchUsers(@ModelAttribute UserSearchSelection userSearchSelection) throws Exception {
		Map<String, List<UserListInfo>> res = new HashMap<String, List<UserListInfo>>();
		List<String> customers = tenantManager.listTenants();
		List<String> active = principalManager.listActivePrincipalNames();
		List<String> noCustomerUsers = null;

		boolean searchByCustomers = userSearchSelection.tenantNames != null
				&& !userSearchSelection.tenantNames.isEmpty();
		boolean searchByUsers = userSearchSelection.principalNames != null
				&& !userSearchSelection.principalNames.isEmpty();

		if (searchByUsers && userSearchSelection.principalNames.size() > configuration.getApplicationConfig()
				.getMaxUserSearchTerms())
			throw new Exception("Search terms exceed limit!");
		// if(!searchByCustomers && !searchByUsers &&
		// !userSearchSelection.activeCustomers)
		// {
		// List<String> res =
		// userMan.searchByName(userSearchSelection.userNames);
		// if(userSearchSelection.activeUsers) res.retainAll(active);
		// return res;
		// }

		if (!searchByCustomers)
			noCustomerUsers = userSearchSelection.activePrincipal ? active : principalManager.listPrincipalNames();

		if (searchByCustomers)
			customers.retainAll(userSearchSelection.tenantNames);

		for (String c : customers) {
			List<String> us = principalManager.searchPrincipalNamesOfTenant(userSearchSelection.principalNames, c,
					userSearchSelection.activeTenants);
			res.put(c, new ArrayList<UserListInfo>());
			for (String u : us) {
				boolean uIsActive = active.contains(u);
				if (!userSearchSelection.activePrincipal || uIsActive)
					res.get(c).add(new UserListInfo(u, c, uIsActive));
			}
			if (!searchByCustomers) {
				if (us.isEmpty() && userSearchSelection.activeTenants)
					us.addAll(principalManager.listPrincipalNamesByTenantName(c, false));
				noCustomerUsers.removeAll(us);
			}
		}

		// if(searchByUsers)
		// {
		// Map<String, List<UserListInfo>> tmp = res;
		// res = new HashMap<String, List<UserListInfo>>();
		// for(Map.Entry<String, List<UserListInfo>> e : tmp.entrySet())
		// {
		// res.put(e.getKey(), new ArrayList<UserListInfo>());
		// for(UserListInfo uli : e.getValue())
		// {
		// if(userSearchSelection.userNames.contains(uli.name))
		// res.get(e.getKey()).add(uli);
		// }
		// }
		// }

		if (!searchByCustomers) {
			for (String u : noCustomerUsers) {
				boolean cond = false;
				boolean match = false;
				if (!searchByUsers || userSearchSelection.principalNames.contains(u))
					cond = true;
				else {
					for (String n : userSearchSelection.principalNames) {
						if (u.toLowerCase().matches(".*" + n.toLowerCase() + ".*")) {
							match = true;
							break;
						}
					}
				}
				if (cond == true || match == true) {
					boolean uIsActive = active.contains(u);
					if (!userSearchSelection.activePrincipal || uIsActive) {
						if (!res.containsKey("None"))
							res.put("None", new ArrayList<UserListInfo>());
						res.get("None").add(new UserListInfo(u, null, uIsActive));
					}
				}
			}
		}

		return res;
	}
	
	@RequestMapping(value = "/admin/customers/list", method = RequestMethod.POST)
	public @ResponseBody List<TenantListInfo> listCustomers() throws Exception {
		List<String> all = tenantManager.listTenants();
		List<String> active = tenantManager.listActiveTenants();
		
		List<TenantListInfo> res = new ArrayList<TenantListInfo>();
		for(String c : all) {
			boolean a = active.contains(c);
			res.add(new TenantListInfo(c, null, null, a));
		}
		return res;
	}
	
	@RequestMapping(value = "/admin/customers/search", method = RequestMethod.POST)
	public @ResponseBody List<TenantListInfo> searchCustomers(@ModelAttribute CustomerSearchSelection customerSearchSelection) throws Exception {
		boolean searchByCustomers = customerSearchSelection.tenantNames != null
				&& !customerSearchSelection.tenantNames.isEmpty();

		if (searchByCustomers && customerSearchSelection.tenantNames.size() > configuration.getApplicationConfig()
				.getMaxCustomerSearchTerms())
			throw new Exception("Search terms exceed limit!");

		Date startDate = null;
		Date endDate = null;
		if (customerSearchSelection.start != 0 && customerSearchSelection.end != 0) {
			startDate = new Date(customerSearchSelection.start);
			endDate = new Date(customerSearchSelection.end);
		}
		return tenantManager.searchTenants(customerSearchSelection.tenantNames, customerSearchSelection.activeTenants,
				startDate, endDate);
	}
	
	@RequestMapping(value = "/admin/customers/retrieveActivation", method = RequestMethod.POST)
	public @ResponseBody List<TenantActivationMessenger> 
			getCustomerActivation(@RequestParam("customer") String customer) throws Exception {
		Tenant c = tenantManager.findByName(customer);
		if(c == null) throw new Exception("Customer " + customer + " not found");
		
		List<TenantActivation> cas = tenantManager.getActivations(c, true);
		List<TenantActivationMessenger> response = new ArrayList<TenantActivationMessenger>();
		
		for(TenantActivation ca : cas) {
			TenantActivationMessenger cm = new TenantActivationMessenger();
			cm.setId(ca.getId().toString());
			cm.setTenant(ca.getTenant().getName());
			cm.setStartDate(ca.getStart().getTime());
			cm.setEndDate(ca.getEnd().getTime());
			cm.setActivationConfig(ca.getActivationConfig());
			cm.setActive(ca.getIsActive());
			if(ca.getShape() != null) cm.setShape(ca.getShape().getId().toString());
			response.add(cm);
		}
		return response;
	}
	
	@RequestMapping(value = "/admin/customers/addActivation", method = RequestMethod.POST, consumes="application/json")
	public @ResponseBody UpdateResponse addCustomerActivation(@RequestBody TenantActivationMessenger cam) throws Exception {
		try {
			Principal principal = securityContextAccessor.getPrincipal();

			Tenant c = null;
			if (cam.getTenant() != null)
				c = tenantManager.findByName(cam.getTenant());
			if (c == null)
				throw new Exception("Customer " + cam.getTenant() + " not found");

			TenantActivation ca = new TenantActivation();
			ca.setTenant(c);
			ca.setStart(new Date(cam.getStartDate()));
			ca.setEnd(new Date(cam.getEndDate()));
			ca.setCreator(principal);

			if (cam.getShape() != null) {
				Shape s = shapeMan.findShapeById(UUID.fromString(cam.getShape()));
				ca.setShape(s);
			}
			ca.setActivationConfig(HtmlUtils.htmlWeakEscape(cam.getActivationConfig().trim()));

			tenantManager.addActivationsToTenant(c, Collections.singletonList(ca));
			return new UpdateResponse(true, "Ok");
		} catch (Exception e) {
			log.error("Could not add customer activation for customer " + cam.getTenant());
			return new UpdateResponse(false, "An error has occurred while adding activation: " + e.getMessage());
		}
	}
	
	@RequestMapping(value = "/admin/customers/updateActivation", method = RequestMethod.POST, consumes="application/json")
	public @ResponseBody UpdateResponse updateCustomerActivation(@RequestBody TenantActivationMessenger cam)
			throws Exception {
		try {
			if (cam.getId() == null)
				throw new Exception("No id provided in order to update customer activation");

			TenantActivation ca = new TenantActivation();
			ca.setId(UUID.fromString(cam.getId()));
			ca.setStart(new Date(cam.getStartDate()));
			ca.setEnd(new Date(cam.getEndDate()));

			if (cam.getShape() != null) {
				Shape s = shapeMan.findShapeById(UUID.fromString(cam.getShape()));
				ca.setShape(s);
			}
			if (cam.getActivationConfig() != null)
				ca.setActivationConfig(HtmlUtils.htmlWeakEscape(cam.getActivationConfig().trim()));

			tenantManager.updateActivation(ca);
			return new UpdateResponse(true, "Ok");
		} catch (Exception e) {
			log.error("Could not add customer activation for customer " + cam.getTenant());
			return new UpdateResponse(false, "An error has occurred while adding activation: " + e.getMessage());
		}
	}
	
	@RequestMapping(value = "/admin/customers/show", method = RequestMethod.POST)
	public @ResponseBody List<TenantListInfo> showCustomers() throws Exception {
		List<Tenant> all = tenantManager.all();
		List<String> active = tenantManager.listActiveTenants();
		
		List<TenantListInfo> res = new ArrayList<TenantListInfo>();
		for(Tenant c : all) {
			boolean a = active.contains(c.getName());
			res.add(new TenantListInfo(c.getName(), c.getEmail(), c.getCode(), a));
		}
		return res;
	}
	
	@RequestMapping(value = "/admin/users/show", method = RequestMethod.POST)
	public @ResponseBody PrincipalMessenger showUser(UserListInfo u) throws Exception {
		Principal principal = principalManager.findPrincipalByNameAndTenant(u.name, u.tenant);
		if(principal == null) throw new Exception("Principal " + u.name + " was not found");
				
		return new PrincipalMessenger(principal);
	}
	
	//TODO fix the creation of user so that is compatible with the new Principal infrastructure
	@RequestMapping(value = "/admin/users/add", method = RequestMethod.POST)
	public @ResponseBody UpdateResponse addUser(PrincipalMessenger principalMessenger) throws Exception {
		try {
			Principal creator = securityContextAccessor.getPrincipal();
			
			if(principalManager.findPrincipalByNameAndTenant(principalMessenger.getSystemName(), principalMessenger.getTenant()) != null) {
				log.error("Principal " + principalMessenger.getSystemName() + " already exists");
				throw new Exception("Principal " + principalMessenger.getSystemName() + " already exists");
			}
			
			Principal principal = new Principal();
			PrincipalData principalData = new PrincipalData();
			if(principalMessenger.getTenant() != null) {
				Tenant tenant = tenantManager.findByName(principalMessenger.getTenant());
				if(tenant == null) {
					log.error("Tenant with name \"" + principalMessenger.getTenant() + "\" was not found");
					return new UpdateResponse(false, "Tenant with name \"" + principalMessenger.getTenant() + "\" was not found");
				}
				principal.setTenant(tenant);
			}
			
			principalData.setFullName(HtmlUtils.htmlEscape(principalMessenger.getFullName().trim()));
			principalData.setInitials(HtmlUtils.htmlEscape(principalMessenger.getInitials().trim()));
			principalData.setEmail(HtmlUtils.htmlEscape(principalMessenger.geteMail().trim()));
			principalData.setExpirationDate(new Date(principalMessenger.getExpirationDate()));
			principalData.setCredential(passwordEncoder.hash(principalMessenger.getCredential().trim().toCharArray()));
			
			principal.setCreator(creator);
			principal.setName(HtmlUtils.htmlEscape(principalMessenger.getSystemName().trim()));
			principal.setIsActive(ActiveStatus.fromCode(principalMessenger.getIsActive()));
			
			principal.setPrincipalData(principalData);
			//principal.setNotificationId(HtmlUtils.htmlEscape(principalMessenger.getNotificationId().trim()));
			//principal.setRights(HtmlUtils.htmlWeakEscape(principalMessenger.getRights().trim()));
			
			principalManager.create(principal, principalData);
		}catch(Exception e) {
			log.error("An error occurred while adding a user", e);
			return new UpdateResponse(false, e.getMessage());
		}
		return new UpdateResponse(true, "Ok");
	}
	
	@RequestMapping(value = "/admin/customers/add", method = RequestMethod.POST)
	public @ResponseBody UpdateResponse addCustomer(CustomerMessenger c) throws Exception {
		try {	
			Principal creator = securityContextAccessor.getPrincipal();
			
			Tenant tenant = new Tenant();
			
			tenant.setCreator(creator);
			tenant.setName(HtmlUtils.htmlEscape(c.getName().trim()));
			tenant.setEmail(HtmlUtils.htmlEscape(c.geteMail().trim()));
			tenant.setCode(HtmlUtils.htmlEscape(c.getCode().trim()));
			
			tenantManager.update(tenant, null, true);
		}catch(Exception e) {
			log.error("An error occurred while adding a customer", e);
			return new UpdateResponse(false, e.getMessage());
		}
		return new UpdateResponse(true, "Ok");
	}
	
	//TODO fix the creation of user so that is compatible with the new Principal infrastructure
	@RequestMapping(value = "/admin/users/update", method = RequestMethod.POST)
	public @ResponseBody UpdateResponse updateUser(PrincipalMessenger principalMessenger) throws Exception {
		try {
			Principal creator = securityContextAccessor.getPrincipal();

			Principal principalOld = principalManager.findPrincipalByNameAndTenant(principalMessenger.getSystemName(), principalMessenger.getOriginalCustomer());
			if (principalOld == null) {
				log.error("Principal " + principalMessenger.getSystemName() + " does not exist");
				return new UpdateResponse(false, "User " + principalMessenger.getSystemName() + " does not exist");
			}

			Principal principal = new Principal();
			PrincipalData principalData = new PrincipalData();
			if (principalMessenger.getTenant() != null) {
				Tenant tenant = tenantManager.findByName(principalMessenger.getTenant());
				if (tenant == null) {
					log.error("Tenant with name \"" + principalMessenger.getTenant() + "\" was not found");
					return new UpdateResponse(false, "Tenant with name \"" + principalMessenger.getTenant() + "\" was not found");
				}
				principal.setTenant(tenant);
			} else {
				principal.setTenant(null);
			}
			
			principalData.setFullName(HtmlUtils.htmlEscape(principalMessenger.getFullName().trim()));
			principalData.setInitials(HtmlUtils.htmlEscape(principalMessenger.getInitials().trim()));
			principalData.setEmail(HtmlUtils.htmlEscape(principalMessenger.geteMail().trim()));
			principalData.setExpirationDate(new Date(principalMessenger.getExpirationDate()));
			if(principalMessenger.getCredential() != null && !principalMessenger.getCredential().trim().isEmpty())
				principalData.setCredential(passwordEncoder.hash(principalMessenger.getCredential().trim().toCharArray()));
			else
				principalData.setCredential(principalOld.getPrincipalData().getCredential());
			
			principal.setId(principalOld.getId());
			principal.setCreationDate(principalOld.getCreationDate());
			principal.setCreator(creator);
			principal.setName(HtmlUtils.htmlEscape(principalMessenger.getSystemName().trim()));
			principal.setIsActive(ActiveStatus.fromCode(principalMessenger.getIsActive()));
			principal.setPrincipalData(principalData);
			//principal.setNotificationId(HtmlUtils.htmlEscape(principalMessenger.getNotificationId().trim()));
			//principal.setRights(HtmlUtils.htmlWeakEscape(principalMessenger.getRights().trim()));

			principalManager.update(principal);
		} catch (Exception e) {
			log.error("An error occurred during user update", e);
			return new UpdateResponse(false, e.getMessage());
		}
		return new UpdateResponse(true, "Ok");
	}
	
	@RequestMapping(value = "/admin/customers/update", method = RequestMethod.POST)
	public @ResponseBody UpdateResponse updateCustomer(CustomerMessenger c) throws Exception {
		try {
			Principal creator = securityContextAccessor.getPrincipal();
			
			Tenant tenant = new Tenant();
			
			tenant.setCreator(creator);
			tenant.setName(HtmlUtils.htmlEscape(c.getName().trim()));
			tenant.setEmail(HtmlUtils.htmlEscape(c.geteMail().trim()));
			tenant.setCode(HtmlUtils.htmlEscape(c.getCode().trim()));
			
			tenantManager.update(tenant, c.getOriginalName(), false);
		}catch(Exception e)	{
			log.error("An error occurred during customer update", e);
			return new UpdateResponse(false, e.getMessage());
		}
		return new UpdateResponse(true, "Ok");
	}
	
	@RequestMapping(value = "/admin/users/delete", method = RequestMethod.POST)
	public @ResponseBody UpdateResponse deleteUsers(@ModelAttribute PrincipalDeleteSelection users) {
		try {
			principalManager.deleteBySystemNameAndCustomer(users.toPairs());
		} catch (Exception e) {
			log.error("An error occurred while deleting users", e);
			return new UpdateResponse(false, e.getMessage());
		}
		return new UpdateResponse(true, "Ok");
	}
	
	@RequestMapping(value = "/admin/customers/delete", method = RequestMethod.POST)
	public @ResponseBody UpdateResponse deleteCustomers(@ModelAttribute List<String> customers) {
		try {
			tenantManager.deleteTenants(customers);
		} catch (Exception e) {
			log.error("An error occurred while deleting customers", e);
			return new UpdateResponse(false, e.getMessage());
		}
		return new UpdateResponse(true, "Ok");
	}
	
	@RequestMapping(value = "/admin/customers/deleteActivations", method = RequestMethod.POST)
	public @ResponseBody UpdateResponse deleteCustomerActivations(@RequestParam("activations") List<String> activations) {
		try {
			tenantManager.deleteActivations(activations);
		} catch (Exception e) {
			log.error("An error occurred while deleting customers", e);
			return new UpdateResponse(false, e.getMessage());
		}
		return new UpdateResponse(true, "Ok");
	}
	
	@RequestMapping(value = "/admin/taxonomies/listTaxonomies", method = RequestMethod.POST)
	public @ResponseBody List<String> listTaxonomies(boolean active) throws Exception {
		return taxonomyMan.listTaxonomies(active);
	}
	
	@RequestMapping(value = "/admin/taxonomies/listTerms", method = RequestMethod.POST)
	public @ResponseBody List<String> listTaxonomyTerms(String taxonomy) throws Exception {
		Taxonomy t = taxonomyMan.findTaxonomyByName(taxonomy, false);
		if(t == null) throw new Exception("Taxonomy " + taxonomy + " does not exist");
		return taxonomyMan.listTermsOfTaxonomy(t.getId().toString(), true);
	}
	
	@RequestMapping(value = "/admin/taxonomies/retrieveTerms", method = RequestMethod.POST)
	public @ResponseBody List<TaxonomyTermMessenger> retrieveTaxonomyTerms(String taxonomy) throws Exception {
		Taxonomy t = taxonomyMan.findTaxonomyByName(taxonomy, false);
		if(t == null) throw new Exception("Taxonomy " + taxonomy + " does not exist");
		
		List<TaxonomyTerm> tts = taxonomyMan.getTermsOfTaxonomy(t.getId().toString(), true, true);
		List<TaxonomyTermMessenger> terms = new ArrayList<TaxonomyTermMessenger>();
		for (TaxonomyTerm tt : tts) {
			SystemMappingConfig smcfg = new SystemMappingConfig();
			smcfg.setMappingConfigs(configMan.getAttributeMappingsForTermId(tt.getId().toString()));
			TaxonomyTermMessenger ttm = new TaxonomyTermMessenger(tt, smcfg);
			terms.add(ttm);
		}

		return terms;
	}
	
	@RequestMapping(value = "/admin/taxonomies/retrieveTermLinks", method = RequestMethod.POST)
	public @ResponseBody List<TaxonomyTermLinkMessenger> retrieveTaxonomyTermLinks(String taxonomy) throws Exception {
		Taxonomy t = taxonomyMan.findTaxonomyByName(taxonomy, false);
		if (t == null)
			throw new Exception("Taxonomy " + taxonomy + " does not exist");

		List<TaxonomyTermLink> ttls = taxonomyMan.getTermLinksOfTaxonomy(t.getId().toString(), true, true);
		List<TaxonomyTermLinkMessenger> links = new ArrayList<TaxonomyTermLinkMessenger>();
		for (TaxonomyTermLink ttl : ttls) {

			TaxonomyTermLinkMessenger ttlm = new TaxonomyTermLinkMessenger(ttl);
			links.add(ttlm);
		}

		return links;
	}
	
	@RequestMapping(value = "/admin/taxonomies/retrieveLinkVerbs", method = RequestMethod.POST)
	public @ResponseBody List<String> retrieveTaxonomyTermLinkVerbs() throws Exception {
		List<String> verbs = new ArrayList<String>();
		TaxonomyTermLink.Verb[] vals = TaxonomyTermLink.Verb.values();
		for(TaxonomyTermLink.Verb val : vals)
			verbs.add(val.toString());
		
		return verbs;
	}
	
	@RequestMapping(value = "/admin/taxonomies/termDescendants", method = RequestMethod.POST)
	public @ResponseBody List<TaxonomyTermMessenger> getTermDescendants(@RequestParam("taxonomy")String taxonomy,
																		   @RequestParam("term")String term) throws Exception {
		TaxonomyTerm t = taxonomyMan.findTermByNameAndTaxonomy(taxonomy, term, false);
		if(t == null) throw new Exception("Term " + taxonomy + ":" + term + " does not exist");
		
		List<TaxonomyTerm> tts = taxonomyMan.getChildrenOfTerm(t.getId().toString(), true, true);
		List<TaxonomyTermMessenger> terms = new ArrayList<TaxonomyTermMessenger>();
		for(TaxonomyTerm tt : tts) {
			SystemMappingConfig smcfg = new SystemMappingConfig();
			smcfg.setMappingConfigs(configMan.getAttributeMappingsForTermId(tt.getId().toString()));
			TaxonomyTermMessenger ttm = new TaxonomyTermMessenger(tt, smcfg);
			terms.add(ttm);
		}
		return terms;
	}
	
	@RequestMapping(value = "/admin/taxonomies/termSiblings", method = RequestMethod.POST)
	public @ResponseBody List<TaxonomyTermMessenger> getTermSiblings(@RequestParam("taxonomy")String taxonomy,
																		   @RequestParam("term")String term) throws Exception {
		TaxonomyTerm t = taxonomyMan.findTermByNameAndTaxonomy(taxonomy, term, false);
		if(t == null) throw new Exception("Term " + taxonomy + ":" + term + " does not exist");
		
		List<TaxonomyTerm> tts = taxonomyMan.getSiblingsOfTerm(t.getId().toString(), true, true);
		List<TaxonomyTermMessenger> terms = new ArrayList<TaxonomyTermMessenger>();
		for(TaxonomyTerm tt : tts) {
			SystemMappingConfig smcfg = new SystemMappingConfig();
			smcfg.setMappingConfigs(configMan.getAttributeMappingsForTermId(tt.getId().toString()));
			TaxonomyTermMessenger ttm = new TaxonomyTermMessenger(tt, smcfg);
			terms.add(ttm);
		}
		return terms;
	}
	
	@RequestMapping(value = "/admin/taxonomies/termClassDescendants", method = RequestMethod.POST)
	public @ResponseBody List<TaxonomyTermMessenger> getTermClassDescendants(@RequestParam("taxonomy")String taxonomy,
																		   @RequestParam("term")String term) throws Exception {
		TaxonomyTerm t = taxonomyMan.findTermByNameAndTaxonomy(taxonomy, term, false);
		if(t == null) throw new Exception("Term " + taxonomy + ":" + term + " does not exist");
		
		List<TaxonomyTerm> tts = taxonomyMan.getClassDescendantsOfTerm(t.getId().toString(), true, true);
		List<TaxonomyTermMessenger> terms = new ArrayList<TaxonomyTermMessenger>();
		for(TaxonomyTerm tt : tts) {
			SystemMappingConfig smcfg = new SystemMappingConfig();
			smcfg.setMappingConfigs(configMan.getAttributeMappingsForTermId(tt.getId().toString()));
			TaxonomyTermMessenger ttm = new TaxonomyTermMessenger(tt, smcfg);
			terms.add(ttm);
		}
		return terms;
	}
	
	@RequestMapping(value = "/admin/taxonomies/termClassSiblings", method = RequestMethod.POST)
	public @ResponseBody List<TaxonomyTermMessenger> getTermClassSiblings(@RequestParam("taxonomy")String taxonomy,
																		   @RequestParam("term")String term) throws Exception {
		TaxonomyTerm t = taxonomyMan.findTermByNameAndTaxonomy(taxonomy, term, false);
		if(t == null) throw new Exception("Term " + taxonomy + ":" + term + " does not exist");
		
		List<TaxonomyTerm> tts = taxonomyMan.getClassSiblingsOfTerm(t.getId().toString(), true, true);
		List<TaxonomyTermMessenger> terms = new ArrayList<TaxonomyTermMessenger>();
		for(TaxonomyTerm tt : tts) {
			SystemMappingConfig smcfg = new SystemMappingConfig();
			smcfg.setMappingConfigs(configMan.getAttributeMappingsForTermId(tt.getId().toString()));
			TaxonomyTermMessenger ttm = new TaxonomyTermMessenger(tt, smcfg);
			terms.add(ttm);
		}
		return terms;
	}
	
	@RequestMapping(value = "/admin/taxonomies/search", method = RequestMethod.POST)
	public @ResponseBody List<TaxonomyMessenger> searchTaxonomies(@ModelAttribute TaxonomySearchSelection taxonomySearchSelection) throws Exception {
		List<Taxonomy> tax = new ArrayList<Taxonomy>();
		if (taxonomySearchSelection.getTaxonomyNames() == null
				|| taxonomySearchSelection.getTaxonomyNames().isEmpty()) {
			if (taxonomySearchSelection.isActiveTaxonomies())
				tax = taxonomyMan.allTaxonomies(true);
			else
				tax = taxonomyMan.activeTaxonomies(true);
		} else {
			for (String n : taxonomySearchSelection.getTaxonomyNames()) {
				Taxonomy t = taxonomyMan.findTaxonomyByName(n, true);
				if (t != null)
					tax.add(t);
			}
		}

		List<TaxonomyMessenger> res = new ArrayList<TaxonomyMessenger>();
		for (Taxonomy t : tax) {
			TaxonomyMessenger tm = new TaxonomyMessenger();
			tm.setName(t.getName());
			if (t.getTaxonomyClass() != null)
				tm.setTaxonomyClass(t.getTaxonomyClass().getName());
			tm.setUserTaxonomy(t.getIsUserTaxonomy());
			tm.setActive(t.getIsActive());
			tm.setExtraData(t.getExtraData());
			res.add(tm);
		}
		return res;
	}
	
	@RequestMapping(value = "/admin/taxonomies/addTaxonomy", method = RequestMethod.POST)
	public @ResponseBody UpdateResponse addTaxonomy(TaxonomyMessenger t) throws Exception {
		try {
			Principal creator = securityContextAccessor.getPrincipal();

			Taxonomy taxonomy = new Taxonomy();

			Taxonomy tClass = null;
			if (t.getTaxonomyClass() != null) {
				tClass = taxonomyMan.findTaxonomyByName(t.getTaxonomyClass(), false);
				if (tClass == null)
					return new UpdateResponse(false, "Specified taxonomy class is not a valid taxonomy");
			}
			taxonomy.setName(HtmlUtils.htmlEscape(t.getName().trim()));
			taxonomy.setIsActive(true);
			taxonomy.setIsUserTaxonomy(t.isUserTaxonomy());
			taxonomy.setCreator(creator);
			taxonomy.setTaxonomyClass(tClass);

			taxonomyMan.updateTaxonomy(taxonomy, null, true);

		} catch (Exception e) {
			log.error("An error occurred while adding a taxonomy", e);
			return new UpdateResponse(false, e.getMessage());
		}
		return new UpdateResponse(true, "Ok");
	}
	
	@RequestMapping(value = "/admin/taxonomies/updateTaxonomy", method = RequestMethod.POST)
	public @ResponseBody UpdateResponse updateTaxonomy(TaxonomyMessenger t) throws Exception {
		try {
			Principal creator = securityContextAccessor.getPrincipal();

			Taxonomy tClass = null;
			if (t.getTaxonomyClass() != null) {
				tClass = taxonomyMan.findTaxonomyByName(t.getTaxonomyClass(), false);
				if (tClass == null)
					return new UpdateResponse(false, "Specified taxonomy class is not a valid taxonomy");
			}
			Taxonomy taxonomy = new Taxonomy();

			taxonomy.setCreator(creator);
			taxonomy.setName(HtmlUtils.htmlEscape(t.getName().trim()));
			taxonomy.setIsUserTaxonomy(t.isUserTaxonomy());
			taxonomy.setIsActive(t.isActive());
			taxonomy.setTaxonomyClass(tClass);
			if (t.getExtraData() != null && !t.getExtraData().trim().isEmpty())
				taxonomy.setExtraData(HtmlUtils.htmlWeakEscape(t.getExtraData()));

			taxonomyMan.updateTaxonomy(taxonomy, t.getOriginalName(), false);
		} catch (Exception e) {
			log.error("An error occurred during customer update", e);
			return new UpdateResponse(false, e.getMessage());
		}
		return new UpdateResponse(true, "Ok");
	}
	
	@RequestMapping(value = "/admin/taxonomies/deleteTaxonomies", method = RequestMethod.POST, consumes="application/json")
	public @ResponseBody UpdateResponse deleteTaxonomies(@RequestBody List<String> taxonomies) {
		try {
			taxonomyMan.deleteTaxonomies(taxonomies);
		} catch (Exception e) {
			log.error("An error occurred while deleting taxonomies", e);
			return new UpdateResponse(false, e.getMessage());
		}
		return new UpdateResponse(true, "Ok");
	}
	
	@RequestMapping(value = "/admin/taxonomies/addTerm", method = RequestMethod.POST)
	public @ResponseBody UpdateResponse addTaxonomyTerm(TaxonomyTermMessenger t) throws Exception {
		try {
			Principal creator = securityContextAccessor.getPrincipal();

			TaxonomyTerm term = new TaxonomyTerm();

			Taxonomy tTaxonomy = taxonomyMan.findTaxonomyByName(t.getTaxonomy(), false);
			if (tTaxonomy == null)
				return new UpdateResponse(false, "Specified term taxonomy is not a valid taxonomy");

			TaxonomyTerm tClass = null, tParent = null;
			if (t.getClassTaxonomy() != null && t.getClassTerm() != null) {
				tClass = taxonomyMan.findTermByNameAndTaxonomy(t.getClassTerm(), t.getClassTaxonomy(), false);
				if (tClass == null)
					return new UpdateResponse(false, "Specified taxonomy term class is not a valid taxonomy term");
			}
			if (t.getParentTaxonomy() != null && t.getParentTerm() != null) {
				tParent = taxonomyMan.findTermByNameAndTaxonomy(t.getParentTerm(), t.getParentTaxonomy(), false);
				if (tParent == null)
					return new UpdateResponse(false, "Specified taxonomy term parent is not a valid taxonomy term");
			}
			term.setName(HtmlUtils.htmlEscape(t.getName().trim()));
			term.setIsActive(true);
			if (t.getOrder() != null)
				term.setOrder(t.getOrder());
			term.setParent(tParent);
			term.setTaxonomyTermClass(tClass);
			term.setTaxonomy(tTaxonomy);
			term.setCreator(creator);

			// TODO extra data
			taxonomyMan.updateTerm(term, null, null, true);

		} catch (Exception e) {
			log.error("An error occurred while adding a taxonomy", e);
			return new UpdateResponse(false, e.getMessage());
		}
		return new UpdateResponse(true, "Ok");
	}
	
	@RequestMapping(value = "/admin/taxonomies/updateTerm", method = RequestMethod.POST)
	public @ResponseBody UpdateResponse updateTaxonomyTerm(TaxonomyTermMessenger t) throws Exception {
		try {
			Principal creator = securityContextAccessor.getPrincipal();

			TaxonomyTerm term = new TaxonomyTerm();

			Taxonomy tTaxonomy = taxonomyMan.findTaxonomyByName(t.getTaxonomy(), false);
			if (tTaxonomy == null)
				return new UpdateResponse(false, "Specified term taxonomy is not a valid taxonomy");

			TaxonomyTerm tClass = null, tParent = null;
			if (t.getClassTaxonomy() != null && t.getClassTerm() != null) {
				tClass = taxonomyMan.findTermByNameAndTaxonomy(t.getClassTerm(), t.getClassTaxonomy(), false);
				if (tClass == null)
					return new UpdateResponse(false, "Specified taxonomy term class is not a valid taxonomy term");
			}
			if (t.getParentTaxonomy() != null && t.getParentTerm() != null) {
				tParent = taxonomyMan.findTermByNameAndTaxonomy(t.getParentTerm(), t.getParentTaxonomy(), false);
				if (tParent == null)
					return new UpdateResponse(false, "Specified taxonomy term parent is not a valid taxonomy term");
			}

			term.setName(HtmlUtils.htmlEscape(t.getName().trim()));
			term.setIsActive(true);
			term.setOrder(t.getOrder());
			term.setParent(tParent);
			term.setTaxonomyTermClass(tClass);
			term.setTaxonomy(tTaxonomy);
			term.setCreator(creator);

			taxonomyMan.updateTerm(term, t.getOriginalName(), t.getOriginalTaxonomyName(), false);
		} catch (Exception e) {
			log.error("An error occurred during customer update", e);
			return new UpdateResponse(false, e.getMessage());
		}
		return new UpdateResponse(true, "Ok");
	}
	
	@RequestMapping(value = "/admin/taxonomies/deleteTerms", consumes="application/json", method = RequestMethod.POST)
	public @ResponseBody UpdateResponse deleteTerms(@RequestBody List<TaxonomyTermInfo> terms) {
		try {
			taxonomyMan.deleteTerms(terms);
		} catch (Exception e) {
			log.error("An error occurred while deleting taxonomies", e);
			return new UpdateResponse(false, e.getMessage());
		}
		return new UpdateResponse(true, "Ok");
	}
	
	@RequestMapping(value = "/admin/taxonomies/addTermLink", method = RequestMethod.POST)
	public @ResponseBody UpdateResponse addTaxonomyTermLink(TaxonomyTermLinkMessenger t) throws Exception {
		try {	
			Principal creator = securityContextAccessor.getPrincipal();
			
			taxonomyMan.updateTermLink(t.getSourceTermTaxonomy(), t.getSourceTerm(), 
					t.getDestTermTaxonomy(), t.getDestTerm(), null, null, null, null, 
					t.getVerb(), creator, true);
			
			return new UpdateResponse(true, "Ok");
		}catch(Exception e)	 {
			log.error("An error occurred while adding a taxonomy term link", e);
			return new UpdateResponse(false, e.getMessage());
		}
	}
	
	@RequestMapping(value = "/admin/taxonomies/updateTermLink", method = RequestMethod.POST)
	public @ResponseBody UpdateResponse updateTaxonomyTerm(TaxonomyTermLinkMessenger t) throws Exception {
		try {
			Principal creator = securityContextAccessor.getPrincipal();
			
			taxonomyMan.updateTermLink(t.getSourceTermTaxonomy(), t.getSourceTerm(), 
					t.getDestTermTaxonomy(), t.getDestTerm(), 
					t.getOrigSourceTermTaxonomy(), t.getOrigSourceTerm(),
					t.getOrigDestTermTaxonomy(), t.getOrigDestTerm(), 
					t.getVerb(), creator, true);
			
			return new UpdateResponse(true, "Ok");
		}catch(Exception e)	{
			log.error("An error occurred during customer update", e);
			return new UpdateResponse(false, e.getMessage());
		}
	}
	
	@RequestMapping(value = "/admin/taxonomies/deleteTermLinks", consumes="application/json", method = RequestMethod.POST)
	public @ResponseBody UpdateResponse deleteTermLinks(@RequestBody List<TaxonomyTermLinkInfo> links) {
		try {
			taxonomyMan.deleteTermLinks(links);
		}catch(Exception e) {
			log.error("An error occurred while deleting taxonomies", e);
			return new UpdateResponse(false, e.getMessage());
		}
		return new UpdateResponse(true, "Ok");
	}
	
	@RequestMapping(value = "/admin/retrieveAttributes", method = RequestMethod.POST)
	public @ResponseBody List<String> getAttributes() throws Exception {
		List<String> attributes = new ArrayList<String>();
		
		List<AttributeMappingConfig> mcfgs = configMan.getMappingConfig();
		for(AttributeMappingConfig mcfg : mcfgs)
			attributes.add(mcfg.getAttributeName());
		return attributes;
	}
	
	@RequestMapping(value = "/admin/documents/systemDocuments", method = RequestMethod.POST)
	public @ResponseBody List<DocumentMessenger> getSystemDocuments() throws Exception {
		List<Principal> admins = Collections.singletonList(principalManager.getSystemPrincipal()); //securityContextAccessor.getAdministrators();
		
		List<DocumentInfo> res = new ArrayList<DocumentInfo>();
		for(Principal principal : admins)
			res.addAll(documentMan.findByCreatorInfo(principal.getName()));

		List<DocumentMessenger> response = new ArrayList<DocumentMessenger>();

		for (DocumentInfo di : res) {
			DocumentMessenger dm = new DocumentMessenger();
			dm.setId(di.getDocument().getId().toString());
			dm.setCreator(di.getDocument().getCreator().getName());
			if (di.getDocument().getTenant() != null)
				dm.setTenant(di.getDocument().getTenant().getName());
			dm.setDescription(di.getDocument().getDescription());
			dm.setMimeType(di.getDocument().getMimeType());
			dm.setMimeSubType(di.getDocument().getMimeSubType());
			dm.setName(di.getDocument().getName());
			dm.setSize(di.getDocument().getSize());
			dm.setCreationDate(di.getDocument().getCreationDate().getTime());
			if (di.getProject() != null) {
				dm.setProjectId(di.getProject().getId().toString());
				dm.setProjectName(di.getProject().getName());
			}
			if (di.getShapes() != null) {
				List<String> shapeIds = new ArrayList<String>();
				List<String> shapeNames = new ArrayList<String>();
				for (Shape s : di.getShapes()) {
					shapeIds.add(s.getId().toString());
					shapeNames.add(s.getName());
				}
				dm.setShapeIds(shapeIds);
				dm.setShapeNames(shapeNames);
			}
			response.add(dm);
		}
		return response;
	}
	
	@RequestMapping(value = "/admin/accounting/search", method = RequestMethod.POST)
	public @ResponseBody List<AccountingMessenger> searchAccounting(@ModelAttribute AccountingSearchSelection accountingSearchSelection) throws Exception {
		List<Accounting> acc = accountingMan.allAccounting(true);

		List<AccountingMessenger> res = new ArrayList<AccountingMessenger>();
		for (Accounting a : acc) {
			AccountingMessenger am = new AccountingMessenger(a);
			res.add(am);
		}
		return res;
	}
	
	@RequestMapping(value = "/admin/accounting/add", method = RequestMethod.POST, consumes="application/json")
	public @ResponseBody UpdateResponse addAccounting(@RequestBody AccountingMessenger accountingMessenger) throws Exception {
		try{	
			Principal creator = securityContextAccessor.getPrincipal();
			
			Accounting accounting = new Accounting();
			
			if(accountingMessenger.getTenant() == null) return new UpdateResponse(false, "Tenant is mandatory");
			Tenant tenant = tenantManager.findByName(accountingMessenger.getTenant());
			if(tenant == null) return new UpdateResponse(false, "Tenant " + accountingMessenger.getTenant() + " not found");
			
			Principal user = null;
			if(accountingMessenger.getPrincipal() != null) {
				user = principalManager.findPrincipalByNameAndTenant(accountingMessenger.getPrincipal(), accountingMessenger.getTenant());
				if(user == null) return new UpdateResponse(false, "Principal " + accountingMessenger.getPrincipal() + " not found");
			}
			accounting.setCreator(creator);
			accounting.setTenant(tenant);
			accounting.setPrincipal(user);
			accounting.setDate(new Date(accountingMessenger.getDate()));
			accounting.setIsValid(true);
			accounting.setReferenceData(HtmlUtils.htmlEscape(accountingMessenger.getReferenceData().trim())); //TODO html weak escape?
			accounting.setType(accountingMessenger.getType());
			accounting.setUnits(accountingMessenger.getUnits());
			
			accountingMan.updateAccounting(accounting, true);
			
		}catch(Exception e)	{
			log.error("An error occurred while adding an accounting entry", e);
			return new UpdateResponse(false, e.getMessage());
		}
		return new UpdateResponse(true, "Ok");
	}
	
	@RequestMapping(value = "/admin/accounting/update", method = RequestMethod.POST, consumes="application/json")
	public @ResponseBody UpdateResponse updateAccounting(@RequestBody AccountingMessenger a) throws Exception {
		try {
			Accounting accounting = new Accounting();
			if (a.getId() == null)
				throw new Exception("No id provided for accounting entry under update");
			accounting.setId(UUID.fromString(a.getId()));
			if (a.getDate() != null)
				accounting.setDate(new Date(a.getDate()));
			if (a.getReferenceData() != null)
				accounting.setReferenceData(HtmlUtils.htmlEscape(a.getReferenceData().trim())); // TODO
																								// html
																								// weak
																								// escape?
			if (a.getType() != null)
				accounting.setType(a.getType());
			if (a.getUnits() != null)
				accounting.setUnits(a.getUnits());

			accountingMan.updateAccounting(accounting, false);
		} catch (Exception e) {
			log.error("An error occurred while updating an accounting entry", e);
			return new UpdateResponse(false, e.getMessage());
		}
		return new UpdateResponse(true, "Ok");
	}
	
	@RequestMapping(value = "/admin/accounting/delete", method = RequestMethod.POST)
	public @ResponseBody UpdateResponse deleteAccounting(@RequestParam("accounting") List<String> accounting) throws Exception {
		try {
			accountingMan.deleteAccounting(accounting);
			return new UpdateResponse(true, "Ok");
		} catch (Exception e) {
			log.error("An error occurred while updating an accounting entry", e);
			return new UpdateResponse(false, e.getMessage());
		}
	}
	
	@RequestMapping(value = "/admin/taxonomies/listTaxonomyConfig", method = RequestMethod.POST)
	public @ResponseBody List<String> listTaxonomyConfig() throws Exception {
		return configMan.listTaxonomyConfigTypes();
	}
	
	@RequestMapping(value = "/admin/taxonomies/updateTaxonomyConfig", method = RequestMethod.POST, consumes="application/json")
	public @ResponseBody UpdateResponse updateTaxonomyConfig(@RequestBody List<TaxonomyConfig> cfg) throws Exception {
		try {
			configMan.setTaxonomyConfig(cfg);
			return new UpdateResponse(true, "Ok");
		} catch (Exception e) {
			return new UpdateResponse(false, e.getMessage());
		}
	}
	
	@RequestMapping(value = "/admin/taxonomies/retrieveTaxonomyConfig", method = RequestMethod.POST)
	public @ResponseBody List<TaxonomyConfig> retrieveTaxonomyConfig() throws Exception {
		return configMan.retrieveTaxonomyConfig(true);
	}
	
	@RequestMapping(value = "/admin/presentation/listLayers", method = RequestMethod.POST)
	public @ResponseBody List<LayerStyleMessenger> listLayers() throws Exception {
		List<LayerStyleMessenger> layers = new ArrayList<LayerStyleMessenger>();
		for(LayerConfig lcfg : configMan.getLayerConfig()) {
			LayerStyleMessenger lsm = new LayerStyleMessenger();
			lsm.setLayerName(lcfg.getName());
			lsm.setMinScale(lcfg.getMinScale());
			lsm.setMaxScale(lcfg.getMaxScale());
			lsm.setTermId(lcfg.getTermId());
			layers.add(lsm);
		}
		return layers;
	}
	
	@RequestMapping(value = "/admin/presentation/listStyles", method = RequestMethod.POST)
	public @ResponseBody List<String> listStyles() throws Exception {
		return configMan.listLayerStyles();
	}
	
	@RequestMapping(value = "/admin/presentation/retrieveStyle", method = RequestMethod.POST, consumes="application/json")
	public @ResponseBody LayerStyleMessenger retrieveStyle(@RequestBody String name) throws Exception {
		LayerStyleMessenger s = new LayerStyleMessenger();
		String style = configMan.getLayerStyle(name);
		if(style == null) throw new Exception("Style " + name + " not found");
		s.setStyle(style);
		return s;
	}
	
	@RequestMapping(value = "/admin/presentation/updateStyle", method = RequestMethod.POST, consumes="application/json")
	public @ResponseBody UpdateResponse updateStyle(@RequestBody LayerStyleUpdate style) {
		try {
			style.setName(HtmlUtils.htmlEscape(style.getName().trim()));
			style.setStyle(HtmlUtils.htmlWeakEscape(style.getStyle().trim()));
			layerMan.updateLayerStyle(style.getName(), style.getOrigName(), style.getStyle());
			return new UpdateResponse(true, "Ok");
		} catch (Exception e) {
			return new UpdateResponse(false, e.getMessage());
		}
	}
	
	@RequestMapping(value = "/admin/presentation/removeStyles", method = RequestMethod.POST, consumes="application/json")
	public @ResponseBody UpdateResponse removeStyle(@RequestBody List<String> names) {
		try {
			configMan.removeLayerStyles(names);
			// layerMan.removeLayerStyles(names);
			return new UpdateResponse(true, "Ok");
		} catch (Exception e) {
			return new UpdateResponse(false, e.getMessage());
		}
	}
	
	@RequestMapping(value = "/admin/presentation/listThemes", method = RequestMethod.POST)
	public @ResponseBody List<String> listThemes() throws Exception {
		return configMan.listThemes();
	}
	
	@RequestMapping(value = "/admin/presentation/addTheme", method = RequestMethod.POST, consumes="application/json")
	public @ResponseBody UpdateResponse addTheme(@RequestBody ThemeCreation theme) {
		try {
			Theme t = new Theme();
			t.setTitle(HtmlUtils.htmlEscape(theme.getName().trim()));
			if (theme.getTemplate() != null) {
				configMan.addTheme(t, theme.getTemplate());
			} else
				configMan.addTheme(t);
			return new UpdateResponse(true, "Ok");
		} catch (Exception e) {
			return new UpdateResponse(false, e.getMessage());
		}
	}
	
	@RequestMapping(value = "/admin/presentation/removeThemes", method = RequestMethod.POST, consumes="application/json")
	public @ResponseBody UpdateResponse removeThemes(@RequestBody List<String> names) {
		try {
			layerMan.removeThemes(names);
			return new UpdateResponse(true, "Ok");
		} catch (Exception e) {
			return new UpdateResponse(false, e.getMessage());
		}
	}
	
	@RequestMapping(value = "/admin/presentation/getLayerStyle", method = RequestMethod.POST, consumes="application/json")
	public @ResponseBody String getTermStyle(@RequestBody LayerStyleMessenger layerStyle) throws Exception {
		if(layerStyle.getTheme() == null)
			return configMan.getDefaultTermStyle(layerStyle.getTermId());
		return configMan.getTermStyle(layerStyle.getTheme(), layerStyle.getTermId());	
	}
	
	@RequestMapping(value= "/admin/presentation/getThemeStyles", method = RequestMethod.POST)
	public @ResponseBody List<LayerStyleMessenger> getThemeStyles(@RequestParam(value="theme", required=false) String theme) throws Exception {
		List<LayerStyleMessenger> themeStyles = new ArrayList<LayerStyleMessenger>();
		List<LayerConfig> layers = configMan.getLayerConfig();
		for (LayerConfig layer : layers) {
			String ts = theme != null && !theme.isEmpty() && !theme.equals(SystemPresentationConfig.DEFAULT_THEME)
					? configMan.getTermStyle(theme, layer.getTermId())
					: configMan.getDefaultTermStyle(layer.getTermId());
			if (ts != null) {
				LayerStyleMessenger lsm = new LayerStyleMessenger();
				lsm.setTheme(theme != null && !theme.isEmpty() ? theme : SystemPresentationConfig.DEFAULT_THEME);
				lsm.setStyle(ts);
				lsm.setTermId(layer.getTermId());
				lsm.setMinScale(layer.getMinScale());
				lsm.setMaxScale(layer.getMaxScale());
				lsm.setLayerName(layer.getName());
				themeStyles.add(lsm);
			}
		}
		return themeStyles;
	}
	
	@RequestMapping(value = "/admin/presentation/updateLayerStyle", method = RequestMethod.POST, consumes="application/json")
	public @ResponseBody UpdateResponse updateLayerStyle(@RequestBody LayerStyleMessenger layerStyle) throws Exception {
		try {
			if (layerStyle.getLayerName() == null)
				return new UpdateResponse(false, "Missing layer name");
			if (layerStyle.getTermId() == null)
				return new UpdateResponse(false, "Missing term id");
			if (layerStyle.getStyle() == null)
				return new UpdateResponse(false, "Missing style reference");

			layerStyle.setLayerName(HtmlUtils.htmlEscape(layerStyle.getLayerName().trim()));
			layerStyle.setStyle(HtmlUtils.htmlEscape(layerStyle.getStyle().trim()));
			layerStyle.setTermId(HtmlUtils.htmlEscape(layerStyle.getTermId().trim()));
			if (layerStyle.getTheme() != null)
				layerStyle.setTheme(HtmlUtils.htmlEscape(layerStyle.getTheme().trim()));

			TaxonomyTerm tt = taxonomyMan.findTermById(layerStyle.getTermId(), false);
			if (tt == null)
				return new UpdateResponse(false, "Term not found");

			layerMan.configureLayer(tt, layerStyle.getTheme(), layerStyle.getStyle(), layerStyle.getMinScale(),
					layerStyle.getMaxScale());

			return new UpdateResponse(true, "Ok");
		} catch (Exception e) {
			log.error("An error has occurred while updating layer style configuration. theme:" + layerStyle.getTheme(),
					" layer:" + layerStyle.getLayerName() + " term: " + layerStyle.getTermId(), e);
			return new UpdateResponse(false, e.getMessage());
		}
	}
	
	
	@RequestMapping(value = "/admin/systemStatus", method = RequestMethod.POST)
	public @ResponseBody boolean systemStatus() throws Exception {
		return configMan.isSystemOnline();
	}
	
	@RequestMapping(value = "/admin/systemOnline", method = RequestMethod.POST)
	public @ResponseBody SystemToggleInfo systemOnline() throws Exception {
		if (configMan.isSystemOnline())
			return new SystemToggleInfo(true, false);
		try {
			configMan.bringUpSystem();
		} catch (Exception e) {
			log.error("Error while trying to bring system online", e);
			return new SystemToggleInfo(false, true);
		}
		return new SystemToggleInfo(true, false);
	}
	
	@RequestMapping(value = "/admin/systemOffline", method = RequestMethod.POST)
	public @ResponseBody SystemToggleInfo systemOffline() throws Exception {
		if (!configMan.isSystemOnline())
			return new SystemToggleInfo(false, false);
		final class ListenerInfo {
			Object lock = new Object();
			boolean error = false;
			boolean finished = false;
		}
		final ListenerInfo li = new ListenerInfo();
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					systemManager.bringDownSystem(new SystemManager.SystemStatusListener() {
						@Override
						public void onStatusChange() {
							li.finished = true;
							synchronized (li.lock) {
								li.lock.notify();
							}

						}
					});
				} catch (Exception e) {
					log.error("An error occurred while bringing system offline", e);
					e.printStackTrace();
					li.error = true;
				}
			}
		});
		t.start();

		while (li.finished == false) {
			try {
				synchronized (li.lock) {
					li.lock.wait();
				}
			} catch (InterruptedException e) {
			}
		}
		t.join();

		if (li.error == true)
			return new SystemToggleInfo(true, true);
		return new SystemToggleInfo(false, false);
	}
}
