package gr.cite.geoanalytics.mvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import gr.cite.gaap.datatransferobjects.AccountLockInfo;
import gr.cite.gaap.datatransferobjects.AccountingMessenger;
import gr.cite.gaap.datatransferobjects.AccountingSearchSelection;
import gr.cite.gaap.datatransferobjects.CustomerMessenger;
import gr.cite.gaap.datatransferobjects.CustomerSearchSelection;
import gr.cite.gaap.datatransferobjects.DocumentMessenger;
import gr.cite.gaap.datatransferobjects.GeocodeMessenger;
import gr.cite.gaap.datatransferobjects.GeocodeSystemMessenger;
import gr.cite.gaap.datatransferobjects.GeocodeSystemNameTransfer;
import gr.cite.gaap.datatransferobjects.GeocodeSystemSearchSelection;
import gr.cite.gaap.datatransferobjects.IllegalAccessAuditingInfo;
import gr.cite.gaap.datatransferobjects.LayerInfo;
import gr.cite.gaap.datatransferobjects.LayerStyleMessenger;
import gr.cite.gaap.datatransferobjects.PrincipalDeleteSelection;
import gr.cite.gaap.datatransferobjects.PrincipalMessenger;
import gr.cite.gaap.datatransferobjects.ServiceResponse;
import gr.cite.gaap.datatransferobjects.SystemToggleInfo;
import gr.cite.gaap.datatransferobjects.TenantActivationMessenger;
import gr.cite.gaap.datatransferobjects.TenantListInfo;
import gr.cite.gaap.datatransferobjects.ThemeCreation;
import gr.cite.gaap.datatransferobjects.UpdateResponse;
import gr.cite.gaap.datatransferobjects.UserLastLoginInfo;
import gr.cite.gaap.datatransferobjects.UserListInfo;
import gr.cite.gaap.datatransferobjects.UserSearchSelection;
import gr.cite.gaap.servicelayer.ConfigurationManager;
import gr.cite.gaap.servicelayer.DocumentManager;
import gr.cite.gaap.servicelayer.DocumentManager.DocumentInfo;
import gr.cite.gaap.servicelayer.GeocodeManager;
import gr.cite.gaap.utilities.HtmlUtils;
import gr.cite.geoanalytics.context.Configuration;
import gr.cite.geoanalytics.dataaccess.entities.accounting.Accounting;
import gr.cite.geoanalytics.dataaccess.entities.geocode.Geocode;
import gr.cite.geoanalytics.dataaccess.entities.geocode.GeocodeSystem;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.principal.PrincipalData;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.global.TaxonomyConfig;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.layer.LayerConfig;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.mapping.AttributeMappingConfig;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.mapping.SystemMappingConfig;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.presentation.SystemPresentationConfig;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.presentation.Theme;
import gr.cite.geoanalytics.dataaccess.entities.tag.Tag;
import gr.cite.geoanalytics.dataaccess.entities.tenant.Tenant;
import gr.cite.geoanalytics.dataaccess.entities.tenant.TenantActivation;
import gr.cite.geoanalytics.manager.AccountingManager;
import gr.cite.geoanalytics.manager.LayerManager;
import gr.cite.geoanalytics.manager.PrincipalManager;
import gr.cite.geoanalytics.manager.SystemManager;
import gr.cite.geoanalytics.manager.TenantManager;
import gr.cite.geoanalytics.manager.admin.AdministrationManager;
import gr.cite.geoanalytics.security.PasswordAuthentication;
import gr.cite.geoanalytics.security.SecurityContextAccessor;
import gr.cite.geoanalytics.util.http.CustomException;
import gr.cite.geoanalytics.util.http.CustomResponseEntity;

@Controller
public class AdminController  {
	@Autowired private AdministrationManager adminMan;
	@Autowired private ConfigurationManager configMan;
	@Autowired private SystemManager systemManager;
	@Autowired private TenantManager tenantManager;
	@Autowired private GeocodeManager taxonomyMan;
	@Autowired private LayerManager layerManager;
	@Autowired private DocumentManager documentMan;
	@Autowired private AccountingManager accountingMan;
	@Autowired private SecurityContextAccessor securityContextAccessor;
	@Autowired private Configuration configuration;
	@Autowired private PrincipalManager principalManager;
	@Autowired private PasswordAuthentication passwordEncoder;
	
	private static final Logger log = LoggerFactory.getLogger(AdminController.class);
		
	@RequestMapping("/admin")
	public String showHome(Model model) throws Exception
	{
		log.debug("Showing admin home...");
		return "redirect:/admin/home";
	}
	
	@RequestMapping("/admin/home")
	public String showInfo(Model model) throws Exception
	{
		log.debug("Showing admin info...");
		model.addAllAttributes(adminMan.getAllInfo());
		return "adminPane";
	}
	
	@RequestMapping("/admin/home/allLogins")
	public @ResponseBody Map<String, UserLastLoginInfo> getAllLogins() throws Exception
	{
		log.debug("Getting all logins...");
		return adminMan.getLastUserLogins();
	}
	
	@RequestMapping("/admin/home/alerts/accountLock")
	public @ResponseBody Map<String, AccountLockInfo> getAccountLocks() throws Exception
	{
		log.debug("getting account locks...");
		return adminMan.getAccountLocks();
	}
	
	@RequestMapping("/admin/home/alerts/illegalRequest")
	public @ResponseBody List<IllegalAccessAuditingInfo> getIllegalRequests() throws Exception
	{
		log.debug("Getting illegal requests...");
		return adminMan.getIllegalRequests();
	}
	
	@RequestMapping("/admin/home/alerts/illegalLayerAccess")
	public @ResponseBody List<IllegalAccessAuditingInfo> getIllegalLayerAccess() throws Exception
	{
		log.debug("Getting illegal layer access...");
		return adminMan.getIllegalLayerAccesses();
	}
	
	@RequestMapping("/admin/home/alerts/illegalLayerZoom")
	public @ResponseBody List<IllegalAccessAuditingInfo> getIllegalLayerZoom() throws Exception
	{
		log.debug("Getting illegal layer zoom...");
		return adminMan.getIllegalLayerAccesses();
	}
	
	@RequestMapping("/admin/users")
	public String showUserManagement(Model model) throws Exception
	{
		log.debug("Getting user management...");
		model.addAttribute(AdministrationManager.EntryType.SystemStatus.toString(), configMan.isSystemOnline());
		return "userManagement";
	}
	
	@RequestMapping("/admin/customers")
	
	public String showCustomerManagement(Model model) throws Exception
	{
		log.debug("Getting customer management...");
		model.addAttribute(AdministrationManager.EntryType.SystemStatus.toString(), configMan.isSystemOnline());
		return "customerManagement";
	}
	
	@RequestMapping("/admin/documents")
	public String showDocumentManagement(Model model) throws Exception
	{
		log.debug("Getting document management...");
		model.addAttribute(AdministrationManager.EntryType.SystemStatus.toString(), configMan.isSystemOnline());
		return "documentManagement";
	}
	
	@RequestMapping("/admin/taxonomies")
	public String showTaxonomyManagement(Model model) throws Exception
	{
		log.debug("Getting geocode management...");
		model.addAttribute(AdministrationManager.EntryType.SystemStatus.toString(), configMan.isSystemOnline());
		return "taxonomyManagement";
	}
	
	@RequestMapping("/admin/shapes")
	public String showShapeManagement(Model model) throws Exception
	{
		log.debug("Getting shape management...");
		model.addAttribute(AdministrationManager.EntryType.SystemStatus.toString(), configMan.isSystemOnline());
		return "shapeManagement";
	}
	
	@RequestMapping("/admin/import")
	public String showDataImport(Model model) throws Exception
	{
		log.debug("Getting data import...");
		model.addAttribute(AdministrationManager.EntryType.SystemStatus.toString(), configMan.isSystemOnline());
		return "dataImport";
	}
	
	@RequestMapping("/admin/accounting")
	public String showAccounting(Model model) throws Exception
	{
		log.debug("Getting accounting...");
		model.addAttribute(AdministrationManager.EntryType.SystemStatus.toString(), configMan.isSystemOnline());
		return "accountingManagement";
	}
	
	@RequestMapping("/admin/dbBackup")
	public String showDatabaseBackup(Model model) throws Exception
	{
		log.debug("Getting dbBackup...");
		model.addAttribute(AdministrationManager.EntryType.SystemStatus.toString(), configMan.isSystemOnline());
		return "dbBackup";
	}
	
	@RequestMapping("/admin/presentation")
	public String showPresentation(Model model) throws Exception
	{
		log.debug("Getting presentation...");
		model.addAttribute(AdministrationManager.EntryType.SystemStatus.toString(), configMan.isSystemOnline());
		return "presentation";
	}
	
	@RequestMapping(value = "/admin/users/list", method = RequestMethod.POST)
	public @ResponseBody Map<String, List<UserListInfo>> listUsers(@ModelAttribute UserSearchSelection userSearchSelection) throws Exception {
		log.debug("Retrieving users...");
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
		log.debug("Retrieving users has been succeeded");
		return res;
	}
	
	@RequestMapping(value = "/admin/users/search", method = RequestMethod.POST)
	public @ResponseBody Map<String, List<UserListInfo>> searchUsers(@ModelAttribute UserSearchSelection userSearchSelection) throws Exception {
		log.debug("Searching users...");
		Map<String, List<UserListInfo>> res = new HashMap<String, List<UserListInfo>>();
		List<String> customers = tenantManager.listTenants();
		List<String> active = principalManager.listActivePrincipalNames();
		List<String> noCustomerUsers = null;

		boolean searchByCustomers = userSearchSelection.tenantNames != null
				&& !userSearchSelection.tenantNames.isEmpty();
		boolean searchByUsers = userSearchSelection.principalNames != null
				&& !userSearchSelection.principalNames.isEmpty();

		if (searchByUsers && userSearchSelection.principalNames.size() > configuration.getApplicationConfig()
				.getMaxUserSearchTerms()){
			log.error("Search terms exceed limit!");
			throw new Exception("Search terms exceed limit!");
		}
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
		log.debug("Searching users has been succeeded");
		return res;
	}
	
	@RequestMapping(value = "/admin/customers/list", method = RequestMethod.POST)
	public @ResponseBody List<TenantListInfo> listCustomers() throws Exception {
		log.debug("Retrieving tenants...");
		List<String> all = tenantManager.listTenants();
		List<String> active = tenantManager.listActiveTenants();
		
		List<TenantListInfo> res = new ArrayList<TenantListInfo>();
		for(String c : all) {
			boolean a = active.contains(c);
			res.add(new TenantListInfo(c, null, null, a));
		}
		log.debug("Retrieving tenants has been succeeded");
		return res;
	}
	
	@RequestMapping(value = "/admin/customers/search", method = RequestMethod.POST)
	public @ResponseBody List<TenantListInfo> searchCustomers(@ModelAttribute CustomerSearchSelection customerSearchSelection) throws Exception {
		log.debug("Searching tenants...");
		boolean searchByCustomers = customerSearchSelection.tenantNames != null
				&& !customerSearchSelection.tenantNames.isEmpty();

		if (searchByCustomers && customerSearchSelection.tenantNames.size() > configuration.getApplicationConfig()
				.getMaxCustomerSearchTerms()){
			log.error("Search terms exceed limit!");
			throw new Exception("Search terms exceed limit!");
		}

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
		log.debug("Retrieving tenant activation...");
		Tenant c = tenantManager.findByName(customer);
		if(c == null){
			log.error("tenant " + customer + " not found");
			throw new Exception("tenant " + customer + " not found");
		}
		
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
			if(ca.getShapeID() != null) cm.setShape(ca.getShapeID().toString());
			response.add(cm);
		}
		log.debug("Retrieving tenant activation has been succeeded");
		return response;
	}
	
	@RequestMapping(value = "/admin/customers/addActivation", method = RequestMethod.POST, consumes="application/json")
	public @ResponseBody UpdateResponse addCustomerActivation(@RequestBody TenantActivationMessenger cam) throws Exception {
		log.debug("Adding tenant activation...");
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
				ca.setShapeID(UUID.fromString(cam.getShape()));
			}
			ca.setActivationConfig(HtmlUtils.htmlWeakEscape(cam.getActivationConfig().trim()));

			tenantManager.addActivationsToTenant(c, Collections.singletonList(ca));
			log.debug("Retrieving tenant activation has been succeeded");
			return new UpdateResponse(true, "Ok");
		} catch (Exception e) {
			log.error("Could not add tenant activation for customer " + cam.getTenant());
			return new UpdateResponse(false, "An error has occurred while adding activation: " + e.getMessage());
		}
	}
	
	@RequestMapping(value = "/admin/customers/updateActivation", method = RequestMethod.POST, consumes="application/json")
	public @ResponseBody UpdateResponse updateCustomerActivation(@RequestBody TenantActivationMessenger cam)
			throws Exception {
		log.debug("Updating tenant activation...");
		try {
			if (cam.getId() == null)
				throw new Exception("No id provided in order to update customer activation");

			TenantActivation ca = new TenantActivation();
			ca.setId(UUID.fromString(cam.getId()));
			ca.setStart(new Date(cam.getStartDate()));
			ca.setEnd(new Date(cam.getEndDate()));

			if (cam.getShape() != null) {
				ca.setShapeID(UUID.fromString(cam.getShape()));
			}
			if (cam.getActivationConfig() != null)
				ca.setActivationConfig(HtmlUtils.htmlWeakEscape(cam.getActivationConfig().trim()));

			tenantManager.updateActivation(ca);
			log.debug("Updating tenant activation has been succeeded");
			return new UpdateResponse(true, "Ok");
		} catch (Exception e) {
			log.error("Could not add tenant activation for customer " + cam.getTenant());
			return new UpdateResponse(false, "An error has occurred while adding activation: " + e.getMessage());
		}
	}
	
	@RequestMapping(value = "/admin/customers/show", method = RequestMethod.POST)
	public @ResponseBody List<TenantListInfo> showCustomers() throws Exception {
		log.debug("Retrieving tenants...");
		List<Tenant> all = tenantManager.all();
		List<String> active = tenantManager.listActiveTenants();
		
		List<TenantListInfo> res = new ArrayList<TenantListInfo>();
		for(Tenant c : all) {
			boolean a = active.contains(c.getName());
			res.add(new TenantListInfo(c.getName(), c.getEmail(), c.getCode(), a));
		}
		log.debug("Retrieving tenants has been succeeded");
		return res;
	}
	
	@RequestMapping(value = "/admin/users/show", method = RequestMethod.POST)
	public @ResponseBody PrincipalMessenger showUser(UserListInfo u) throws Exception {
		log.debug("Retrieving customers...");
		Principal principal = principalManager.findPrincipalByNameAndTenant(u.name, u.tenant);
		if(principal == null) throw new Exception("Principal " + u.name + " was not found");
				
		return new PrincipalMessenger(principal);
	}
	
	@RequestMapping(value = "/users/listUsers", method = RequestMethod.POST,  produces = { "application/json" })
	public @ResponseBody ServiceResponse listUsers() throws Exception {
		log.debug("Retrieving principals...");
		try {
		//	List<Principal> principals = principalManager.getPrincipals();
			List<String> principals = principalManager.listPrincipalNames();
			
			if (principals!=null && !principals.isEmpty()){
				log.debug("Retrieving principals has been succeeded");
				return new ServiceResponse(true, principals, "Principals returned");
			}
			log.error("No principals");
			return new ServiceResponse(true, null, "No principals");
		} catch(Exception e) {
			log.error("Error while retrieving principals");
			return new ServiceResponse(false, null, "Something did not worked fine on server");			
		}
	}
	
	
	//TODO fix the creation of user so that is compatible with the new Principal infrastructure
	@RequestMapping(value = "/admin/users/add", method = RequestMethod.POST, produces = { "application/json" })
	public @ResponseBody UpdateResponse addUser(PrincipalMessenger principalMessenger) throws Exception {
		log.debug("Adding principal: "+principalMessenger.getFullName());
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
			principal.setIsActive(principalMessenger.getIsActive());
			
			principal.setPrincipalData(principalData);
			//principal.setNotificationId(HtmlUtils.htmlEscape(principalMessenger.getNotificationId().trim()));
			//principal.setRights(HtmlUtils.htmlWeakEscape(principalMessenger.getRights().trim()));
			
			principalManager.create(principal, principalData);
		}catch(Exception e) {
			log.error("An error occurred while adding a user", e);
			return new UpdateResponse(false, e.getMessage());
		}
		log.debug("Principal has been added");
		return new UpdateResponse(true, "Ok");
	}
	
	@RequestMapping(value = "/admin/customers/add", method = RequestMethod.POST, produces = { "application/json" })
	public @ResponseBody UpdateResponse addCustomer(CustomerMessenger c) throws Exception {
		log.debug("Adding tenant: "+c.getName());
		try {	
			Principal creator = securityContextAccessor.getPrincipal();
			
			Tenant tenant = new Tenant();
			
			tenant.setCreator(creator);
			tenant.setName(HtmlUtils.htmlEscape(c.getName().trim()));
			tenant.setEmail(HtmlUtils.htmlEscape(c.geteMail().trim()));
			tenant.setCode(HtmlUtils.htmlEscape(c.getCode().trim()));
			
			tenantManager.update(tenant, null, true);
		}catch(Exception e) {
			log.error("An error occurred while adding a tenant", e);
			return new UpdateResponse(false, e.getMessage());
		}
		log.debug("Tenant has been added");
		return new UpdateResponse(true, "Ok");
	}
	
	//TODO fix the creation of user so that is compatible with the new Principal infrastructure
	@RequestMapping(value = "/admin/users/update", method = RequestMethod.POST, produces = { "application/json" })
	public @ResponseBody UpdateResponse updateUser(PrincipalMessenger principalMessenger) throws Exception {
		log.debug("Updating principal: "+principalMessenger.getFullName());
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
			principal.setIsActive(principalMessenger.getIsActive());
			principal.setPrincipalData(principalData);
			//principal.setNotificationId(HtmlUtils.htmlEscape(principalMessenger.getNotificationId().trim()));
			//principal.setRights(HtmlUtils.htmlWeakEscape(principalMessenger.getRights().trim()));

			principalManager.update(principal);
		} catch (Exception e) {
			log.error("An error occurred during principal update", e);
			return new UpdateResponse(false, e.getMessage());
		}
		log.debug("Principal has been updated");
		return new UpdateResponse(true, "Ok");
	}
	
	@RequestMapping(value = "/admin/customers/update", method = RequestMethod.POST, produces = { "application/json" })
	public @ResponseBody UpdateResponse updateCustomer(CustomerMessenger c) throws Exception {
		log.debug("Updating tenant: "+c.getName());
		try {
			Principal creator = securityContextAccessor.getPrincipal();
			
			Tenant tenant = new Tenant();
			
			tenant.setCreator(creator);
			tenant.setName(HtmlUtils.htmlEscape(c.getName().trim()));
			tenant.setEmail(HtmlUtils.htmlEscape(c.geteMail().trim()));
			tenant.setCode(HtmlUtils.htmlEscape(c.getCode().trim()));
			
			tenantManager.update(tenant, c.getOriginalName(), false);
		}catch(Exception e)	{
			log.error("An error occurred during tenant update", e);
			return new UpdateResponse(false, e.getMessage());
		}
		log.debug("Tenant has been updated");
		return new UpdateResponse(true, "Ok");
	}
	
	@RequestMapping(value = "/admin/users/delete", method = RequestMethod.POST, produces = { "application/json" })
	public @ResponseBody UpdateResponse deleteUsers(@ModelAttribute PrincipalDeleteSelection users) {
		log.debug("Deleting principals...");
		try {
			principalManager.deleteBySystemNameAndCustomer(users.toPairs());
		} catch (Exception e) {
			log.error("An error occurred while deleting principals", e);
			return new UpdateResponse(false, e.getMessage());
		}
		log.debug("Principals have been deleted");
		return new UpdateResponse(true, "Ok");
	}
	
	@RequestMapping(value = "/admin/customers/delete", method = RequestMethod.POST, produces = { "application/json" })
	public @ResponseBody UpdateResponse deleteCustomers(@ModelAttribute List<String> customers) {
		log.debug("Deleting tenants...");
		try {
			tenantManager.deleteTenants(customers);
		} catch (Exception e) {
			log.error("An error occurred while deleting tenants", e);
			return new UpdateResponse(false, e.getMessage());
		}
		log.debug("Tenants have been deleted");
		return new UpdateResponse(true, "Ok");
	}
	
	@RequestMapping(value = "/admin/customers/deleteActivations", method = RequestMethod.POST, produces = { "application/json" })
	public @ResponseBody UpdateResponse deleteCustomerActivations(@RequestParam("activations") List<String> activations) {
		log.debug("Deleting activate tenants...");
		try {
			tenantManager.deleteActivations(activations);
		} catch (Exception e) {
			log.error("An error occurred while deleting tenants", e);
			return new UpdateResponse(false, e.getMessage());
		}
		log.debug("Activate tenants have been deleted");
		return new UpdateResponse(true, "Ok");
	}
	
	@RequestMapping(value = "/admin/tags/listTags", method = RequestMethod.GET, produces={"application/json"})
	public @ResponseBody ResponseEntity<?> listTags(HttpServletRequest request) throws Exception {			
		log.debug("Retrieving all Tags");
		
		List<Map<String, String>> nodes = new ArrayList<>();

		try{
			List<Tag> allTags = layerManager.listAllTags();		
			
			for(Tag tag : allTags){
				Map<String, String> node = new HashMap<>();
				node.put("id", tag.getId().toString());
				node.put("name", tag.getName());
				node.put("description", tag.getDescription());
				nodes.add(node);
			}
		} catch (Exception e){
			return new CustomResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve tags. Please try again later.", e);	
		}
		
		log.debug("Tags have been retrieved successfully!");
		
		return new CustomResponseEntity<List<Map<String, String>>>(HttpStatus.OK, nodes);	
	}
	
	@RequestMapping(value = "/admin/tags/createTag", method = RequestMethod.POST, consumes={"application/json"}, produces = { "application/json" })
	public @ResponseBody ResponseEntity<?> createTag(@RequestBody Map<String, String> tagInfo, HttpServletRequest request) throws Exception {
		log.debug("Creating Tag with info : " + tagInfo);
		
		String name = tagInfo.get("name");
		String description = tagInfo.get("description");
		Principal creator = securityContextAccessor.getPrincipal();
		
		Tag tag = null;
		
		try{
			Assert.notNull(name, "Tag name cannot be empty.");
			Assert.hasLength(name, "Tag name cannot be empty.");
			
			layerManager.checkTagNotExists(name);
			tag = new Tag().withName(name).withDescription(description).withCreator(creator);
			layerManager.createTag(tag);
		} catch(CustomException e){
			return new CustomResponseEntity<String>(e.getStatusCode(), "Creation failed. " + e.getMessage(), e);
		} catch(IllegalArgumentException e){
			return new CustomResponseEntity<String>(HttpStatus.BAD_REQUEST, "Creation failed. Tag name cannot be empty.", e);	
		} catch(Exception e){
			return new CustomResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create tag with name \"" + name + "\"", e);	
		}

		log.debug("Tag has been created successfully");

		return new CustomResponseEntity<String>(HttpStatus.OK, tag.getId().toString());	
	}
	
	@RequestMapping(value = "/admin/tags/deleteTag", method = RequestMethod.POST, consumes={"application/json"}, produces = { "application/json" })
	public @ResponseBody ResponseEntity<?> deleteTag(@RequestBody String tagId, HttpServletRequest request) throws Exception {
		log.debug("Deleting Tag with id : " + tagId);
		
		try{
			Tag tag = layerManager.findTagById(tagId);		
			layerManager.deleteTag(tag);		
		}  catch (Exception e) {
			return new CustomResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete tag", e);	
		}
		
		log.debug("Tag has been deleted successfully!");
		
		return new CustomResponseEntity<String>(HttpStatus.OK, "Tag has been deleted successfully!");	
	}
	
	@RequestMapping(value = "/admin/tags/editTag", method = RequestMethod.POST, consumes={"application/json"}, produces = { "application/json" })
	public @ResponseBody ResponseEntity<?> editTag(@RequestBody Map<String, String> tagInfo, HttpServletRequest request) throws Exception {
		log.debug("Editing Tag with info : " + tagInfo);
		
		UUID id = UUID.fromString(tagInfo.get("id"));
		String name = tagInfo.get("name");
		String description = tagInfo.get("description");
		
		try{
			Assert.notNull(name, "Tag name cannot be empty.");
			Assert.hasLength(name, "Tag name cannot be empty.");

			Tag tag = layerManager.findTagById(id);
			
			if(Objects.equals(tag.getName(), name) && Objects.equals(tag.getDescription(), description)){
				throw new CustomException(HttpStatus.BAD_REQUEST, "No changes were submitted.");	
			}
			
			if(!tag.getName().equals(name)){		
				layerManager.checkTagNotExists(name);	
			}
			
			layerManager.editTag(tag, name, description);
		} catch(CustomException e){
			return new CustomResponseEntity<String>(e.getStatusCode(), "Update failed. " + e.getMessage(), e);
		} catch(IllegalArgumentException e){
			return new CustomResponseEntity<String>(HttpStatus.BAD_REQUEST, "Update failed. Tag name cannot be empty.", e);	
		} catch (Exception e) {
			return new CustomResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update tag information.", e);	
		}
		
		log.debug("Tag information has been updated successfully!");

		return new CustomResponseEntity<String>(HttpStatus.OK, "Tag information has been updated successfully!");	
	}
	
	@RequestMapping(value = "/admin/taxonomies/listTerms", method = RequestMethod.POST, consumes="application/json", produces = { "application/json" })
	public @ResponseBody ServiceResponse listGeocodes(@RequestBody GeocodeSystemNameTransfer tn, HttpServletRequest request) throws Exception {
		log.debug("Listing geocodes...");
		GeocodeSystem t = taxonomyMan.findGeocodeSystemByName(tn.getGeocodeSystemName(), false);
		if(t == null){
			log.error("Geocode " + tn.getGeocodeSystemName() + " does not exist");
			return new ServiceResponse(false, null, "Geocode " + tn.getGeocodeSystemName() + " does not exist");
		}
		List<String> terms = taxonomyMan.listTermsOfGeocodeSystem(t.getId().toString(), true);
		if(terms == null) {
			log.error("No terms retrieved");
			return new ServiceResponse(false, null, "No terms retrieved");
		}
		
		log.debug("Geocodes have been listed");
		return  new ServiceResponse(true, terms, "Terms succesfully retrieved");
	}
	
	@RequestMapping(value = "/admin/taxonomies/retrieveTerms", method = RequestMethod.POST, consumes="application/json", produces = { "application/json" })
	public @ResponseBody List<GeocodeMessenger> retrieveGeocodes(@RequestBody GeocodeSystemNameTransfer taxonomy, HttpServletRequest request) throws Exception {
		log.debug("Retrieving geocodes...");
		GeocodeSystem t = taxonomyMan.findGeocodeSystemByName(taxonomy.getGeocodeSystemName(), false);
		if(t == null){
			log.error("Geocode " + taxonomy.getGeocodeSystemName() + " does not exist");
			throw new Exception("Geocode " + taxonomy.getGeocodeSystemName() + " does not exist");
		}
		
		List<Geocode> tts = taxonomyMan.getGeocodesOfGeocodeSystem(t.getId().toString(), true, true);
		List<GeocodeMessenger> terms = new ArrayList<GeocodeMessenger>();
		for (Geocode tt : tts) {
			SystemMappingConfig smcfg = new SystemMappingConfig();
			smcfg.setMappingConfigs(configMan.getAttributeMappingsForTermId(tt.getId().toString()));
			GeocodeMessenger ttm = new GeocodeMessenger(tt, smcfg);
			terms.add(ttm);
		}
		log.debug("Geocodes have been retrieved");
		return terms;
	}
	
	@RequestMapping(value = "/admin/taxonomies/termDescendants", method = RequestMethod.POST, produces = { "application/json" })
	public @ResponseBody List<GeocodeMessenger> getTermDescendants(@RequestParam("taxonomy")String taxonomy,
																		   @RequestParam("term")String term) throws Exception {
		Geocode t = taxonomyMan.findTermByNameAndTaxonomy(taxonomy, term, false);
		if(t == null) throw new Exception("Term " + taxonomy + ":" + term + " does not exist");
		
		List<Geocode> tts = taxonomyMan.getChildrenOfGeocode(t.getId().toString(), true, true);
		List<GeocodeMessenger> terms = new ArrayList<GeocodeMessenger>();
		for(Geocode tt : tts) {
			SystemMappingConfig smcfg = new SystemMappingConfig();
			smcfg.setMappingConfigs(configMan.getAttributeMappingsForTermId(tt.getId().toString()));
			GeocodeMessenger ttm = new GeocodeMessenger(tt, smcfg);
			terms.add(ttm);
		}
		return terms;
	}
	
	@RequestMapping(value = "/admin/taxonomies/termSiblings", method = RequestMethod.POST, produces = { "application/json" })
	public @ResponseBody List<GeocodeMessenger> getTermSiblings(@RequestParam("taxonomy")String taxonomy,
																		   @RequestParam("term")String term) throws Exception {
		Geocode t = taxonomyMan.findTermByNameAndTaxonomy(taxonomy, term, false);
		if(t == null) throw new Exception("Term " + taxonomy + ":" + term + " does not exist");
		
		List<Geocode> tts = taxonomyMan.getSiblingsOfGeocode(t.getId().toString(), true, true);
		List<GeocodeMessenger> terms = new ArrayList<GeocodeMessenger>();
		for(Geocode tt : tts) {
			SystemMappingConfig smcfg = new SystemMappingConfig();
			smcfg.setMappingConfigs(configMan.getAttributeMappingsForTermId(tt.getId().toString()));
			GeocodeMessenger ttm = new GeocodeMessenger(tt, smcfg);
			terms.add(ttm);
		}
		return terms;
	}
	
	@RequestMapping(value = "/admin/taxonomies/termClassDescendants", method = RequestMethod.POST, produces = { "application/json" })
	public @ResponseBody List<GeocodeMessenger> getTermClassDescendants(@RequestParam("taxonomy")String taxonomy,
																		   @RequestParam("term")String term) throws Exception {
		Geocode t = taxonomyMan.findTermByNameAndTaxonomy(taxonomy, term, false);
		if(t == null) throw new Exception("Term " + taxonomy + ":" + term + " does not exist");
		
		List<Geocode> tts = taxonomyMan.getClassDescendantsOfGeocode(t.getId().toString(), true, true);
		List<GeocodeMessenger> terms = new ArrayList<GeocodeMessenger>();
		for(Geocode tt : tts) {
			SystemMappingConfig smcfg = new SystemMappingConfig();
			smcfg.setMappingConfigs(configMan.getAttributeMappingsForTermId(tt.getId().toString()));
			GeocodeMessenger ttm = new GeocodeMessenger(tt, smcfg);
			terms.add(ttm);
		}
		return terms;
	}
	
	@RequestMapping(value = "/admin/taxonomies/termClassSiblings", method = RequestMethod.POST, produces = { "application/json" })
	public @ResponseBody List<GeocodeMessenger> getTermClassSiblings(@RequestParam("taxonomy")String taxonomy,
																		   @RequestParam("term")String term) throws Exception {
		Geocode t = taxonomyMan.findTermByNameAndTaxonomy(taxonomy, term, false);
		if(t == null) throw new Exception("Term " + taxonomy + ":" + term + " does not exist");
		
		List<Geocode> tts = taxonomyMan.getClassSiblingsOfGeocode(t.getId().toString(), true, true);
		List<GeocodeMessenger> terms = new ArrayList<GeocodeMessenger>();
		for(Geocode tt : tts) {
			SystemMappingConfig smcfg = new SystemMappingConfig();
			smcfg.setMappingConfigs(configMan.getAttributeMappingsForTermId(tt.getId().toString()));
			GeocodeMessenger ttm = new GeocodeMessenger(tt, smcfg);
			terms.add(ttm);
		}
		return terms;
	}
	

	@RequestMapping(value = "/admin/taxonomies/search", method = RequestMethod.POST, consumes="application/json", produces = { "application/json" })
	public @ResponseBody ServiceResponse searchTaxonomies(@RequestBody GeocodeSystemSearchSelection geocodeSystemSearchSelection) throws Exception {
		log.debug("Searching for geocode system... ");
		try {
			List<GeocodeSystem> tax = new ArrayList<GeocodeSystem>();
			
			if (geocodeSystemSearchSelection.getGeocodeSystemNames() == null
					|| geocodeSystemSearchSelection.getGeocodeSystemNames().isEmpty()) {
				if (geocodeSystemSearchSelection.isActiveGeocodeSystems())
					tax = taxonomyMan.allGeocodeSystems(true);
				else
					tax = taxonomyMan.activeGeocodeSystems(true);
			} else {
				for (String n : geocodeSystemSearchSelection.getGeocodeSystemNames()) {
					GeocodeSystem t = taxonomyMan.findGeocodeSystemByName(n, true);
					if (t != null)
						tax.add(t);
				}
			}
	
			List<GeocodeSystemMessenger> res = new ArrayList<GeocodeSystemMessenger>();
			for (GeocodeSystem t : tax) {
				GeocodeSystemMessenger tm = new GeocodeSystemMessenger();
				tm.setName(t.getName());
				if (t.getTaxonomyClass() != null)
					tm.setTaxonomyClass(t.getTaxonomyClass().getName());
				tm.setUserTaxonomy(t.getIsUserTaxonomy());
				tm.setActive(t.getIsActive());
				tm.setExtraData(t.getExtraData());
				res.add(tm);
			}
			log.debug("Geocode Systems have been returned");
			return new ServiceResponse(true, res, "search results returned");
		} catch (Exception e) {
			log.error("Error while searching for geocode system");
			return new ServiceResponse(false, null, e.getMessage());
		}
	}
	
	@RequestMapping(value = "/admin/taxonomies/addTaxonomy", method = RequestMethod.POST, produces = { "application/json" })
	public @ResponseBody UpdateResponse addTaxonomy(@RequestBody GeocodeSystemMessenger t) throws Exception {
		log.debug("Adding GeocodeSystem: "+t.getName());
		try {
			Principal creator = securityContextAccessor.getPrincipal();

			GeocodeSystem taxonomy = new GeocodeSystem();

			GeocodeSystem tClass = null;
			if (t.getTaxonomyClass() != null) {
				tClass = taxonomyMan.findGeocodeSystemByName(t.getTaxonomyClass(), false);
				if (tClass == null){
					log.error("Specified GeocodeSystem class is not a valid GeocodeSystem");
					return new UpdateResponse(false, "Specified GeocodeSystem class is not a valid GeocodeSystem");
				}
			}
			taxonomy.setName(HtmlUtils.htmlEscape(t.getName().trim()));
			taxonomy.setIsActive(true);
			taxonomy.setIsUserTaxonomy(t.isUserTaxonomy());
			taxonomy.setCreator(creator);
			taxonomy.setTaxonomyClass(tClass);

			taxonomyMan.updateTaxonomy(taxonomy, null, true);

		} catch (Exception e) {
			log.error("An error occurred while adding a GeocodeSystem", e);
			return new UpdateResponse(false, e.getMessage());
		}
		log.debug("GeocodeSystem has been added");
		return new UpdateResponse(true, "Ok");
	}
	
	@RequestMapping(value = "/admin/taxonomies/updateTaxonomy", method = RequestMethod.POST, produces = { "application/json" })
	public @ResponseBody UpdateResponse updateTaxonomy(@RequestBody GeocodeSystemMessenger t) throws Exception {
		log.debug("Updating Geocodesystem: "+t.getName());
		try {
			Principal creator = securityContextAccessor.getPrincipal();

			GeocodeSystem tClass = null;
			if (t.getTaxonomyClass() != null) {
				tClass = taxonomyMan.findGeocodeSystemByName(t.getTaxonomyClass(), false);
				if (tClass == null){
					log.debug("Specified geocode system class is not a valid geocode system");
					return new UpdateResponse(false, "Specified taxonomy class is not a valid taxonomy");
				}
			}
			GeocodeSystem taxonomy = new GeocodeSystem();

			taxonomy.setCreator(creator);
			taxonomy.setName(HtmlUtils.htmlEscape(t.getName().trim()));
			taxonomy.setIsUserTaxonomy(t.isUserTaxonomy());
			taxonomy.setIsActive(t.isActive());
			taxonomy.setTaxonomyClass(tClass);
			if (t.getExtraData() != null && !t.getExtraData().trim().isEmpty())
				taxonomy.setExtraData(HtmlUtils.htmlWeakEscape(t.getExtraData()));

			taxonomyMan.updateTaxonomy(taxonomy, t.getOriginalName(), false);
		} catch (Exception e) {
			log.error("An error occurred during geocode system update", e);
			return new UpdateResponse(false, e.getMessage());
		}
		log.debug("GeocodeSystem has been updated");
		return new UpdateResponse(true, "Ok");
	}
	
	@RequestMapping(value = "/admin/taxonomies/deleteTaxonomies", method = RequestMethod.POST, consumes="application/json", produces = { "application/json" })
	public @ResponseBody UpdateResponse deleteTaxonomies(@RequestBody List<String> taxonomies) {
		log.debug("Deleting GeocodeSystems...");
		try {
			taxonomyMan.deleteTaxonomies(taxonomies);
		} catch (Exception e) {
			log.error("An error occurred while deleting geocode systems", e);
			return new UpdateResponse(false, e.getMessage());
		}
		log.debug("Geocodesystem has been deleted");
		return new UpdateResponse(true, "Ok");
	}
	
	@RequestMapping(value = "/admin/taxonomies/addTerm", method = RequestMethod.POST, consumes="application/json", produces = { "application/json" })
	public @ResponseBody UpdateResponse addGeocode(@RequestBody GeocodeMessenger t) throws Exception {
		log.debug("Adding geocode: "+t.getName());
		try {
			Principal creator = securityContextAccessor.getPrincipal();

			Geocode term = new Geocode();

			GeocodeSystem tTaxonomy = taxonomyMan.findGeocodeSystemByName(t.getTaxonomy(), false);
			if (tTaxonomy == null){
				log.error("Specified term geocode is not a valid geocode");
				return new UpdateResponse(false, "Specified term taxonomy is not a valid taxonomy");
			}

			Geocode tClass = null, tParent = null;
			if (t.getClassTaxonomy() != null && t.getClassTerm() != null) {
				tClass = taxonomyMan.findTermByNameAndTaxonomy(t.getClassTerm(), t.getClassTaxonomy(), false);
				if (tClass == null){
					log.error("Specified term geocode is not a valid geocode");
					return new UpdateResponse(false, "Specified taxonomy term class is not a valid taxonomy term");
				}
			}
			if (t.getParentTaxonomy() != null && t.getParentTerm() != null) {
				tParent = taxonomyMan.findTermByNameAndTaxonomy(t.getParentTerm(), t.getParentTaxonomy(), false);
				if (tParent == null){
					log.error("Specified term parent geocode is not a valid geocode");
					return new UpdateResponse(false, "Specified taxonomy term parent is not a valid taxonomy term");
				}
			}
			term.setName(HtmlUtils.htmlEscape(t.getName().trim()));
			term.setIsActive(true);
			if (t.getOrder() != null)
				term.setOrder(t.getOrder());
			term.setParent(tParent);
			term.setGeocodeClass(tClass);
			term.setGeocodeSystem(tTaxonomy);
			term.setCreator(creator);

			// TODO extra data
			taxonomyMan.updateTerm(term, null, null, true);

		} catch (Exception e) {
			log.error("An error occurred while adding a geocode", e);
			return new UpdateResponse(false, e.getMessage());
		}
		log.debug("Geocode has been added");
		return new UpdateResponse(true, "Ok");
	}
	
	@RequestMapping(value = "/admin/taxonomies/updateTerm", method = RequestMethod.POST, produces = { "application/json" })
	public @ResponseBody UpdateResponse updateGeocode(@RequestBody GeocodeMessenger t) throws Exception {
		log.debug("Updating geocode: "+t.getName());
		try {
			Principal creator = securityContextAccessor.getPrincipal();

			Geocode term = new Geocode();

			GeocodeSystem geocodeSystem = taxonomyMan.findGeocodeSystemByName(t.getTaxonomy(), false);
			if (geocodeSystem == null){
				log.error("There is no such geocode system");
				return new UpdateResponse(false, "Specified term taxonomy is not a valid taxonomy");
			}

			Geocode tClass = null, tParent = null;
			if (t.getClassTaxonomy() != null && t.getClassTerm() != null) {
				tClass = taxonomyMan.findTermByNameAndTaxonomy(t.getClassTerm(), t.getClassTaxonomy(), false);
				if (tClass == null){
					log.error("Specified taxonomy term class is not a valid taxonomy term");
					return new UpdateResponse(false, "Specified taxonomy term class is not a valid taxonomy term");
				}
			}
			if (t.getParentTaxonomy() != null && t.getParentTerm() != null) {
				tParent = taxonomyMan.findTermByNameAndTaxonomy(t.getParentTerm(), t.getParentTaxonomy(), false);
				if (tParent == null){
					log.error("Specified taxonomy term parent is not a valid taxonomy term");
					return new UpdateResponse(false, "Specified taxonomy term parent is not a valid taxonomy term");
				}
			}

			term.setName(HtmlUtils.htmlEscape(t.getName().trim()));
			term.setIsActive(true);
			term.setOrder(t.getOrder());
			term.setParent(tParent);
			term.setGeocodeClass(tClass);
			term.setGeocodeSystem(geocodeSystem);
			term.setCreator(creator);

			taxonomyMan.updateTerm(term, t.getOriginalName(), t.getOriginalTaxonomyName(), false);
		} catch (Exception e) {
			log.error("An error occurred during geocode update", e);
			return new UpdateResponse(false, e.getMessage());
		}
		log.debug("Geocode has been updated");;
		return new UpdateResponse(true, "Ok");
	}
	
	@RequestMapping(value = "/admin/taxonomies/deleteTerms", consumes="application/json", method = RequestMethod.POST, produces = { "application/json" })
	public @ResponseBody UpdateResponse deleteTerms(@RequestBody List<LayerInfo> terms) {
		log.debug("Deleting Layers...");
		try {
			taxonomyMan.deleteTerms(terms);
		} catch (Exception e) {
			log.error("An error occurred while deleting layers", e);
			return new UpdateResponse(false, e.getMessage());
		}
		log.debug("Layers have been deleted");
		return new UpdateResponse(true, "Ok");
	}
		
	@RequestMapping(value = "/admin/retrieveAttributes", method = RequestMethod.POST)
	public @ResponseBody List<String> getAttributes() throws Exception {
		log.debug("Retrieving attributes...");
		List<String> attributes = new ArrayList<String>();
		
		List<AttributeMappingConfig> mcfgs = configMan.getMappingConfig();
		for(AttributeMappingConfig mcfg : mcfgs)
			attributes.add(mcfg.getAttributeName());
		log.debug("Attributes have been retrieved");
		return attributes;
	}
	
	@RequestMapping(value = "/admin/documents/systemDocuments", method = RequestMethod.POST, produces = { "application/json" })
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
	
	@RequestMapping(value = "/admin/accounting/search", method = RequestMethod.POST, produces = { "application/json" })
	public @ResponseBody List<AccountingMessenger> searchAccounting(@ModelAttribute AccountingSearchSelection accountingSearchSelection) throws Exception {
		List<Accounting> acc = accountingMan.allAccounting(true);

		List<AccountingMessenger> res = new ArrayList<AccountingMessenger>();
		for (Accounting a : acc) {
			AccountingMessenger am = new AccountingMessenger(a);
			res.add(am);
		}
		return res;
	}
	
	@RequestMapping(value = "/admin/accounting/add", method = RequestMethod.POST, consumes="application/json", produces = { "application/json" })
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
	
	@RequestMapping(value = "/admin/accounting/update", method = RequestMethod.POST, consumes="application/json", produces = { "application/json" })
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
	
	@RequestMapping(value = "/admin/accounting/delete", method = RequestMethod.POST, produces = { "application/json" })
	public @ResponseBody UpdateResponse deleteAccounting(@RequestParam("accounting") List<String> accounting) throws Exception {
		try {
			accountingMan.deleteAccounting(accounting);
			return new UpdateResponse(true, "Ok");
		} catch (Exception e) {
			log.error("An error occurred while updating an accounting entry", e);
			return new UpdateResponse(false, e.getMessage());
		}
	}
	
	@RequestMapping(value = "/admin/taxonomies/listTaxonomyConfig", method = RequestMethod.POST, produces = { "application/json" })
	public @ResponseBody ServiceResponse listTaxonomyConfig() throws Exception {
		try {
			List<String> cTypes = configMan.listTaxonomyConfigTypes();
			return new ServiceResponse(true, cTypes, "cTypes returned");
		} catch (Exception e) {
			return new ServiceResponse(false, null, e.getMessage());
		}
	}
	
	@RequestMapping(value = "/admin/taxonomies/updateTaxonomyConfig", method = RequestMethod.POST, consumes="application/json", produces = { "application/json" })
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
			lsm.setTermId(lcfg.getLayerId());
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
	
	@RequestMapping(value = "/admin/presentation/addTheme", method = RequestMethod.POST, consumes="application/json", produces = { "application/json" })
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
	
	@RequestMapping(value = "/admin/presentation/getLayerStyle", method = RequestMethod.POST, consumes="application/json", produces = { "application/json" })
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
					? configMan.getTermStyle(theme, layer.getLayerId())
					: configMan.getDefaultTermStyle(layer.getLayerId());
			if (ts != null) {
				LayerStyleMessenger lsm = new LayerStyleMessenger();
				lsm.setTheme(theme != null && !theme.isEmpty() ? theme : SystemPresentationConfig.DEFAULT_THEME);
				lsm.setStyle(ts);
				lsm.setTermId(layer.getLayerId());
				lsm.setMinScale(layer.getMinScale());
				lsm.setMaxScale(layer.getMaxScale());
				lsm.setLayerName(layer.getName());
				themeStyles.add(lsm);
			}
		}
		return themeStyles;
	}
	
	@RequestMapping(value = "/admin/systemStatus", method = RequestMethod.POST, produces = { "application/json" })
	public @ResponseBody boolean systemStatus() throws Exception {
		return configMan.isSystemOnline();
	}
	
	@RequestMapping(value = "/admin/systemOnline", method = RequestMethod.POST, produces = { "application/json" })
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
	
	@RequestMapping(value = "/admin/systemOffline", method = RequestMethod.POST, produces = { "application/json" })
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
