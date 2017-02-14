package gr.cite.geoanalytics.mvc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.WKTWriter;


//import gr.cite.commons.util.datarepository.elements.RepositoryFile;
import gr.cite.gaap.datatransferobjects.AttributeInfo;
import gr.cite.gaap.datatransferobjects.DocumentMessenger;
import gr.cite.gaap.datatransferobjects.DocumentSearchSelection;
import gr.cite.gaap.datatransferobjects.DummyModel;
import gr.cite.gaap.datatransferobjects.GenericResponse;
import gr.cite.gaap.datatransferobjects.GenericResponse.Status;
import gr.cite.gaap.datatransferobjects.GeoLocation;
import gr.cite.gaap.datatransferobjects.GeoLocationTag;
import gr.cite.gaap.datatransferobjects.GeoSearchSelection;
import gr.cite.gaap.datatransferobjects.JSTREEToServerToken;
import gr.cite.gaap.datatransferobjects.LayerMessengerForJSTREE;
import gr.cite.gaap.datatransferobjects.NewProjectData;
import gr.cite.gaap.datatransferobjects.PrincipalProjectInfo;
import gr.cite.gaap.datatransferobjects.ServiceResponse;
import gr.cite.gaap.datatransferobjects.UserinfoObject;
import gr.cite.gaap.datatransferobjects.ProjectAttributeMessenger;
import gr.cite.gaap.datatransferobjects.ProjectGroupInfo;
import gr.cite.gaap.datatransferobjects.ProjectGroupMessenger;
import gr.cite.gaap.datatransferobjects.ProjectInfoMessenger;
import gr.cite.gaap.datatransferobjects.ProjectMessenger;
import gr.cite.gaap.datatransferobjects.ProjectParticipantInfo;
import gr.cite.gaap.datatransferobjects.ProjectSummary;
import gr.cite.gaap.datatransferobjects.TaxonomyMessengerForJSTree;
import gr.cite.gaap.datatransferobjects.TaxonomyTermMessenger;
import gr.cite.gaap.datatransferobjects.UserLastPasswordRequestInfo;
import gr.cite.gaap.datatransferobjects.WorkflowMessenger;
import gr.cite.gaap.datatransferobjects.WorkflowTaskMessenger;
import gr.cite.gaap.geospatialbackend.GeospatialBackend;
import gr.cite.gaap.servicelayer.ConfigurationManager;
import gr.cite.gaap.servicelayer.DescendantInclusionType;
import gr.cite.gaap.servicelayer.DocumentManager;
import gr.cite.gaap.servicelayer.DocumentManager.DocumentInfo;
import gr.cite.gaap.servicelayer.ShapeManager.GeographyHierarchy;
import gr.cite.gaap.servicelayer.TaxonomyManager;
import gr.cite.gaap.servicelayer.exception.DocumentNotFoundException;
import gr.cite.gaap.servicelayer.exception.UnauthorizedOperationException;
import gr.cite.gaap.utilities.HtmlUtils;
import gr.cite.gaap.utilities.PasswordGenerator;
import gr.cite.gaap.utilities.TaxonomyUtils;
import gr.cite.geoanalytics.context.Configuration;
import gr.cite.geoanalytics.context.GeoServerBridgeConfig;
import gr.cite.geoanalytics.dataaccess.dao.UUIDGenerator;
import gr.cite.geoanalytics.dataaccess.entities.ActiveStatus;
import gr.cite.geoanalytics.dataaccess.entities.document.Document;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.principal.PrincipalData;
import gr.cite.geoanalytics.dataaccess.entities.project.Project;
import gr.cite.geoanalytics.dataaccess.entities.security.principal.dao.PrincipalProjectInfoDao;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.global.TaxonomyConfig;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.layer.LayerBounds;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.layer.LayerConfig;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.Taxonomy;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTerm;
import gr.cite.geoanalytics.dataaccess.entities.tenant.Tenant;
import gr.cite.geoanalytics.dataaccess.entities.user.UserRights;
import gr.cite.geoanalytics.dataaccess.entities.workflow.Workflow;
import gr.cite.geoanalytics.dataaccess.entities.workflow.WorkflowTask;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.Bounds;
import gr.cite.geoanalytics.manager.AuditingManager;
import gr.cite.geoanalytics.manager.ExecutionManager;
import gr.cite.geoanalytics.manager.PrincipalManager;
import gr.cite.geoanalytics.manager.ProjectManager;
import gr.cite.geoanalytics.manager.ProjectManager.ProjectInfo;
import gr.cite.geoanalytics.manager.TenantManager;
import gr.cite.geoanalytics.manager.admin.AdministrationManager;
import gr.cite.geoanalytics.security.SecurityContextAccessor;
import gr.cite.geoanalytics.util.mail.MailFormatter;
import gr.cite.geoanalytics.util.mail.mailer.Mailer;
import gr.cite.geoanalytics.util.mail.types.MailParameter;
import gr.cite.geoanalytics.util.mail.types.MailType;

@Controller
public class HomeController
{
	private static final Logger log = LoggerFactory.getLogger(HomeController.class);
	
	private GeospatialBackend shapeManager;
	private TaxonomyManager taxonomyManager;
	private DocumentManager documentManager;
	private ProjectManager projectManager;
	private ConfigurationManager configurationManager;
	private AuditingManager auditingManager;
	private AdministrationManager administrationManager;
	private Configuration configuration;
	private ExecutionManager executionManager;
	private PrincipalManager principalManager;
	private TenantManager tenantManager;
	
	private static Map<UUID, UUID> documentTokens = new ConcurrentHashMap<UUID, UUID>(1000, 0.75f, 50);
	
	@Inject
	private PasswordGenerator passGen;
	
	private SecurityContextAccessor securityContextAccessor;
	private Mailer mailer;
	
	private JAXBContext rightsCtx = null;
	
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
	
	@Inject
	public HomeController(PrincipalManager principalManager, GeospatialBackend shapemanager, 
			TaxonomyManager taxonomyManager, DocumentManager documentManager, 
			ProjectManager projectManager, ConfigurationManager configurationManager, 
			AuditingManager auditingManager, AdministrationManager administrationManager, SecurityContextAccessor securityContextAccessor,
			Mailer mailer, TenantManager tenantManager) throws Exception
	{
		this.principalManager = principalManager;
		this.shapeManager = shapemanager;
		this.taxonomyManager = taxonomyManager;
		this.documentManager = documentManager;
		this.projectManager = projectManager;
		this.configurationManager = configurationManager;
		this.administrationManager = administrationManager;
		this.auditingManager = auditingManager;
		this.securityContextAccessor = securityContextAccessor;
		this.mailer = mailer;
		this.tenantManager = tenantManager;
		this.rightsCtx = JAXBContext.newInstance(UserRights.class);
	}
	
	@Inject
	public void setExecutionManager(ExecutionManager executionManager) {
		this.executionManager = executionManager;
	}
	
	@Inject
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
	
	private void auditAction(String action, UUID entityId, String entityType, Principal principal) {
		try {
			auditingManager.auditLastAction(action, entityId, entityType, principal);
		}catch(Exception e) {
			log.error("Could not audit action " + action + (principal != null ? "for user " + principal.getName() : ""), e);
		}
	}
	
	@RequestMapping({"/", "/home"})
	public String show(@RequestParam(value="lang", required=false) String lang, Model model, HttpServletResponse response) {
		try {
			Principal u = null;
			if (!securityContextAccessor.isAnonymous()) {
				u = securityContextAccessor.getPrincipal();
			}
			else {
				System.out.println("Anonymous user!");
			// if(u == null) throw new Exception("User " +
			// authUser.getUsername() + " not found");
			}

			Map<String, LayerConfig> layers = new HashMap<String, LayerConfig>();
			LayerBounds bounds = null;
			if (u != null) {
				// Unmarshaller um = rightsCtx.createUnmarshaller();
				//UserRights rights = (UserRights)um.unmarshal(new StringReader(u.getRights()));
				
				List<LayerConfig> layerConfigs = new ArrayList<LayerConfig>();
				//if(rights.getLayers() == null)
				//{
				//	layerConfigs = configurationManager.getLayerConfig();
				//}else
				//{
				//	for(String layer : rights.getLayers())
				//	{
				//		TaxonomyTerm tt = taxonomyManager.findTermByName(layer, false);
				//		LayerConfig lcfg = configurationManager.getLayerConfig(tt);
				//		if(lcfg != null) layerConfigs.add(lcfg);
				//	}
				//}
				List<String> layerNames = new ArrayList<String>();
				layerNames.addAll(securityContextAccessor.getLayers());
				List<LayerConfig> allCfgs = configurationManager.getLayerConfig();
				for (String layerName : layerNames) {
					for (LayerConfig cfg : allCfgs) {
						// TODO per-account min/max scale
						if (cfg.getName().equals(layerName)) {
							layerConfigs.add(cfg);
							LayerConfig addCfg = new LayerConfig();
							addCfg.setName(cfg.getName());
							addCfg.setMinScale(cfg.getMinScale());
							addCfg.setMaxScale(cfg.getMaxScale());
							layers.put(layerName, addCfg);
						}
					}

				}

				if (!layerConfigs.isEmpty()) {
					bounds = layerConfigs.get(0).getBoundingBox();
				}
				for (LayerConfig lc : layerConfigs) {
					// layerNames.add(lc.getName());
					bounds.mergeWith(lc.getBoundingBox());
				}
			}

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			List<TaxonomyConfig> infoCfgs = configurationManager.retrieveTaxonomyConfigByClass(TaxonomyConfig.Type.PROJECTINFOCATEGORYTAXONOMY);
			List<String> infoCategories = new ArrayList<String>();
			List<String> orderedInfoCategories = new ArrayList<String>();
			Set<String> unorderedInfoCategories = new HashSet<String>();
			for (int i = 0; i < infoCfgs.size(); i++) {
				for (TaxonomyConfig infoCfg : infoCfgs) {
					Taxonomy cfgTaxon = taxonomyManager.findTaxonomyById(infoCfg.getId(), true);

					Integer taxonOrder = TaxonomyUtils.getOrder(cfgTaxon, db);
					if (taxonOrder == null) {
						unorderedInfoCategories.add(cfgTaxon.getName());
						continue;
					}
					if (taxonOrder == i) {
						orderedInfoCategories.add(cfgTaxon.getName());
					}
				}
			}
			infoCategories.addAll(orderedInfoCategories);
			infoCategories.addAll(unorderedInfoCategories);
			
			Map<String, List<String>> categoryTaxonomyTypes = new HashMap<String, List<String>>();
			for(TaxonomyConfig infoCfg : infoCfgs)
			{
				List<Taxonomy> catTaxons = configurationManager.retrieveTaxonomiesByClass(TaxonomyConfig.Type.valueOf(infoCfg.getType()), u, 
						securityContextAccessor.isAdministrator()  ? DescendantInclusionType.ALL :
								(securityContextAccessor.isAnonymous() ? DescendantInclusionType.EXCLUDE_USER_TAXONOMIES : DescendantInclusionType.INCLUDE_TAXONOMIES_OF_USER), true);
				List<String> orderedCatCfgTypes = new ArrayList<String>();
				Set<String> unorderedCatCfgTypes = new HashSet<String>();
				List<String> catCfgTypes = new ArrayList<String>();
				for (int i = 0; i < catTaxons.size(); i++) {
					for (Taxonomy catTaxon : catTaxons) {
						Integer taxonOrder = TaxonomyUtils.getOrder(catTaxon, db);

						if (taxonOrder == null) {
							unorderedCatCfgTypes.add(catTaxon.getName());
							continue;
						}
						if (taxonOrder == i) {
							orderedCatCfgTypes.add(catTaxon.getName());
						}
					}
				}
				catCfgTypes.addAll(orderedCatCfgTypes);
				catCfgTypes.addAll(unorderedCatCfgTypes);
				categoryTaxonomyTypes.put(taxonomyManager.findTaxonomyById(infoCfg.getId(), false).getName(), catCfgTypes);
			}
			
			GeographyHierarchy geogHierarchy = shapeManager.getDefaultGeographyHierarchy();
			List<String> mainHierarchyNames = new ArrayList<String>();
			for(Taxonomy t : geogHierarchy.getMainHierarchy()) {
				mainHierarchyNames.add(t.getName());
			}
			
			List<String> altHierarchyNames = new ArrayList<String>();
			//TODO currently supporting only one alternative hierarchy
			if(!geogHierarchy.getAlternativeHierarchies().isEmpty()) {
				for(Taxonomy t : geogHierarchy.getAlternativeHierarchies().get(0)) {
					altHierarchyNames.add(t.getName());
				}
			}
			
			String planningTaxonomyName = configurationManager.retrieveTaxonomyConfig(TaxonomyConfig.Type.PLANNINGTAXONOMY, true).get(0).getId();
			String geographyTaxonomyName = configurationManager.retrieveTaxonomyConfig(TaxonomyConfig.Type.GEOGRAPHYTAXONOMY, true).get(0).getId();
			
			Set<String> userTaxons = new HashSet<String>();
			Map<String, String> customUserTaxonomyNames = new HashMap<String, String>();
			if(u != null) {
				List<TaxonomyConfig> infoCats = configurationManager.retrieveTaxonomyConfigByClass(TaxonomyConfig.Type.PROJECTINFOCATEGORYTAXONOMY);
				for(TaxonomyConfig infoCfg : infoCats) {
					List<TaxonomyConfig> cfgs = configurationManager.retrieveTaxonomyConfigByClass(TaxonomyConfig.Type.valueOf(infoCfg.getType())); 
					for(TaxonomyConfig cfg : cfgs) {
						Taxonomy t = taxonomyManager.findTaxonomyById(cfg.getId(), false);
						if(TaxonomyUtils.isEditable(t, db)) {
							userTaxons.add(t.getName());
						}
					}
				}
				Set<Taxonomy> additionalUserTaxons = projectManager.findProjectUserTaxonomies(u, true, customUserTaxonomyNames);
				for(Taxonomy aut : additionalUserTaxons) {
					userTaxons.add(aut.getName());
				}
			}
			
			model.addAttribute("layerDatastore", configurationManager.getLayerDatastore());
			model.addAttribute("projectInfoCategories", infoCategories);
			model.addAttribute("projectInfoCategoryTypes", categoryTaxonomyTypes);
			model.addAttribute("geographyTaxonomyType", geographyTaxonomyName);
			model.addAttribute("planningTaxonomyType", planningTaxonomyName);
			model.addAttribute("geographyHierarchy", mainHierarchyNames);
			model.addAttribute("altGeographyHierarchy", altHierarchyNames);
			model.addAttribute("projectEditableTaxonomies", userTaxons);
			model.addAttribute("customUserTaxonomyNames", customUserTaxonomyNames);
			model.addAttribute("layers", layers);
			model.addAttribute("bounds", bounds);
		}catch(Exception e) {
			log.error("An error has occured in the main page", e);
			e.printStackTrace();
			try { response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); }
			catch(IOException ee) { log.error("Could not respond with error", e); }
		}
		return "home";
	}
	
	@RequestMapping(value = "/login")
	public String showLogin(Model model) {
		return "login";
	}
	
	@RequestMapping(value = "/login_error")
	public String showLoginError(Model model) {
		model.addAttribute("error", "true");
		return "login";
	}
	
	@RequestMapping(value = "/resend_password")
	public String resendPassword() {
		return "resend_password";
	}
	
	@RequestMapping(value = "/resend_password", method=RequestMethod.POST)
	public String resendPassword(@RequestParam("username") String userName, 
								 @RequestParam("email") String email,
								 Model model,
								 HttpServletResponse response) {
		try {
			Principal principal = principalManager.getActivePrincipalByName(userName);
		    if(principal != null) {
			    if(principalManager.isActiveStatusByActiveStatusAndName(principal.getName(), ActiveStatus.LOCKED)) {
			    	UserLastPasswordRequestInfo lastUnsuccessful = administrationManager.getLastPasswordRequestForUser(principal);
				    if(new Date().getTime() - (lastUnsuccessful != null ? lastUnsuccessful.getTimestamp() : 0) > 
				    		TimeUnit.MILLISECONDS.convert(accountLockPeriod, accountLockPeriodUnit)) {
						principalManager.setActivityStatus(principal, ActiveStatus.ACTIVE);
					} else {
				    	model.addAttribute("error", "ACCOUNT_LOCKED");
				    	return "resend_password";
				    }
			    }
		    }else  {
		    	if(userName == null) {
					model.addAttribute("error", "NO_USERNAME"); //TODO fix dos
				} else {
					model.addAttribute("error", "EMAIL_NO_MATCH");
				}
		    	return "resend_password";
		    }try {
	    		UserLastPasswordRequestInfo ull = administrationManager.getLastPasswordRequestForUser(principal);
		    	int times = 0;
		    	if(new Date().getTime() - (ull != null ? ull.getTimestamp() : 0) < TimeUnit.MILLISECONDS.convert(accountLockCheckPeriod, accountLockCheckPeriodUnit)) {
		    		times = (ull != null ? ull.getTimes() : 0) + 1;
		    		if(times > maxAcceptableRate) {
		    			principalManager.setActivityStatus(principal, ActiveStatus.LOCKED);
		    			
		    		}
		    	}
		    	HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes())
		                   .getRequest(); 
		        auditingManager.auditPasswordRequest(principal, times, request.getRemoteAddr());
	      	}catch(Exception ee) {
	      		log.error("Could not audit password request for user " + principal.getName());
	      	}
			
			String origPass = null;
			try {
				if(!principal.getPrincipalData().getEmail().equals(email)){
					model.addAttribute("error", "EMAIL_NO_MATCH");
				}else {
					String tempPass = passGen.generate();
					origPass = principal.getPrincipalData().getCredential();
					principal.getPrincipalData().setCredential(tempPass);
					principalManager.update(principal);
					
					DateFormat format = new SimpleDateFormat("DD-MM-yyyy hh:mm:ss");
					MailFormatter formatter = MailFormatter.forType(MailType.PASSWORD_REQUEST).
							withParameter(MailParameter.DATETIME, format.format((new Date()))).
							withParameter(MailParameter.PASSWORD, tempPass).format();
					mailer.sendTo(principal.getPrincipalData().getEmail(), null, Collections.singletonList(principalManager.getSystemPrincipal().getPrincipalData().getEmail()), 
							formatter.getSubject(), formatter.getText());
				}
			}catch(Exception e) {
				principal.getPrincipalData().setCredential(origPass);
				principalManager.update(principal);
				log.error("Error while serving password request", e);
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
			
			return "resend_password";
			
		}catch(Exception e)
		{
			log.error("Error while serving password request", e);
			try { response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); }
			catch(IOException ee) { log.error("Could not respond with error", e); }
		}
		
		model.addAttribute("status", true);
		return "resend_password";
	}
	
	public GenericResponse checkAuth(Principal entityCreator, Principal principal) throws Exception {
		if(securityContextAccessor.isAdministrator()) {
			return null;
		}
		if(!entityCreator.getId().equals(principal.getId())) {
			return new GenericResponse(Status.Unauthorized, null, "Unauthorized access");
		}
		return null;
	}
	
	@RequestMapping(value = "/projects/listClients", method = RequestMethod.POST)
	public @ResponseBody GenericResponse listClients() {
		try {
			Principal principal = securityContextAccessor.getPrincipal();
			
			Set<String> res = projectManager.listProjectClients(principal);
			return new GenericResponse(Status.Success, res, "Ok");
		}catch(Exception e) {
			return new GenericResponse(Status.Failure, null, e.getMessage());
		}
	}
	
	private List<DocumentMessenger> createDocumentResponse(List<DocumentInfo> result) {
		List<DocumentMessenger> response = new ArrayList<DocumentMessenger>();
		
		for(DocumentInfo di : result) {
			DocumentMessenger dm = new DocumentMessenger();
			dm.setId(di.getDocument().getId().toString());
			dm.setCreator(di.getDocument().getCreator().getName());
			if(di.getDocument().getTenant() != null) {
				dm.setTenant(di.getDocument().getTenant().getName());
			}
			dm.setDescription(di.getDocument().getDescription());
			dm.setMimeType(di.getDocument().getMimeType());
			dm.setMimeSubType(di.getDocument().getMimeSubType());
			dm.setName(di.getDocument().getName());
			dm.setSize(di.getDocument().getSize());
			dm.setCreationDate(di.getDocument().getCreationDate().getTime());
			if(di.getProject() != null) {
				dm.setProjectId(di.getProject().getId().toString());
				dm.setProjectName(di.getProject().getName());
			}
			if(di.getShapes() != null) {
				List<String> shapeIds = new ArrayList<String>();
				List<String> shapeNames = new ArrayList<String>();
				for(Shape s : di.getShapes()) {
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
	
	@RequestMapping(value={"/documents/search"}, method=RequestMethod.POST, consumes="application/json")
	public @ResponseBody GenericResponse searchDocuments(@RequestBody DocumentSearchSelection selection) {
		try {
			Principal principal = securityContextAccessor.getPrincipal();
			
			List<DocumentInfo> docs = new ArrayList<DocumentInfo>();
			List<DocumentInfo> filtered = new ArrayList<DocumentInfo>();
			
			if(selection.getTerms() == null || selection.getTerms().isEmpty()) {
				if(selection.getProject() != null) {
					docs = documentManager.findByProjectInfo(UUID.fromString(selection.getProject()));
					if(selection.getCreator() != null || selection.getTenant() != null) {
						for(DocumentInfo d : docs) {	
							boolean cond = true;
							if(selection.getCreator() != null && !d.getDocument().getCreator().getName().equals(selection.getCreator())) {
								cond = false;
							}
							if(selection.getTenant() != null && !d.getDocument().getTenant().getName().equals(selection.getTenant())) {
								cond = false;
							}
							if(securityContextAccessor.isUser() && !d.getDocument().getTenant().getId().equals(principal.getTenant().getId())) {
								cond = false;
							}
							if(cond) {
								filtered.add(d);
							}
						}
					}else {
						for(DocumentInfo d : docs) {
							if(!securityContextAccessor.isUser() || d.getDocument().getTenant().getId().equals(principal.getTenant().getId())) {
								filtered.add(d);
							}
						}
					}
					
				}else if(selection.getTenant() != null || selection.getCreator() != null) {
					if(selection.getTenant() != null && selection.getCreator() != null) {
						docs = documentManager.findByCreatorAndCustomerInfo(selection.getCreator(), selection.getTenant());
					} else if(selection.getTenant() != null) {
						docs = documentManager.findByCustomerInfo(selection.getTenant());
					} else {
						docs = documentManager.findByCreatorInfo(selection.getCreator());
					}
					if(selection.getProject() != null) {
						for(DocumentInfo d : docs) {
							if(selection.getProject() != null && d.getProject().getId().toString().equals(selection.getProject())) {
								if(!securityContextAccessor.isUser() || d.getDocument().getTenant().getId().equals(principal.getTenant().getId())) {
									filtered.add(d);
								}
							}
						}
					}else {
						for(DocumentInfo d : docs) {
							if(!securityContextAccessor.isUser() || d.getDocument().getTenant().getId().equals(principal.getTenant().getId())) {
								filtered.add(d);
							}
						}
					}
				}else {
					for(DocumentInfo d : documentManager.allDocumentsInfo()) {
						if(!securityContextAccessor.isUser() || d.getDocument().getTenant().getId().equals(principal.getTenant().getId())) {
							filtered.add(d);
						}
					}
				}
			} else {
				if(selection.getTerms().size() > configuration.getApplicationConfig().getMaxDocumentSearchTerms()) {
					return new GenericResponse(Status.TermsExceedLimit, null, "Search terms exceed limit");
				}
				
				if(selection.getProject() != null) {
					docs = documentManager.searchDocumentsOfProjectInfo(selection.getTerms(), UUID.fromString(selection.getProject()));
				} else {
					docs = documentManager.searchDocumentsInfo(selection.getTerms());
				}
				
				if(selection.getCreator() != null || selection.getTenant() != null) {
					for(DocumentInfo d : docs) {
						if(selection.getCreator() != null || selection.getTenant() != null) {
							boolean cond = true;
							if(selection.getCreator() != null && !d.getDocument().getCreator().getName().equals(selection.getCreator())) {
								cond = false;
							}
							if(selection.getTenant() != null && !d.getDocument().getTenant().getName().equals(selection.getTenant())) {
								cond = false;
							}
							if(securityContextAccessor.isUser() && !d.getDocument().getTenant().getId().equals(principal.getTenant().getId())) {
								cond = false;
							}
							if(cond) {
								filtered.add(d);
							}
						}else {
							if(!securityContextAccessor.isUser() || d.getDocument().getTenant().getId().equals(principal.getTenant().getId())) {
								filtered.add(d);
							}
						}
					}
				}else {
					for(DocumentInfo d : docs)
					{
						if(!securityContextAccessor.isUser() || d.getDocument().getTenant().getId().equals(principal.getTenant().getId())) {
							filtered.add(d);
						}
					}
				}
			}
			
			auditAction("search", null, "Document", principal);
			
			return new GenericResponse(Status.Success, createDocumentResponse(filtered), "Ok");
		}catch(Exception e) {
			log.error("Error while searching documents", e);
			return new GenericResponse(Status.Failure, null, e.getMessage());
		}
	}
	
//	@RequestMapping(method = RequestMethod.POST, value = {"/documents/add"})
//	public @ResponseBody GenericResponse createDocument(
//			@RequestParam(value="name", required=false) String name,
//			@RequestParam(value="description") String description,
//			@RequestParam(value="file") MultipartFile file,
//			
//			HttpSession session,
//			MultipartHttpServletRequest request, 
//			HttpServletResponse response) {
//		try {
//			Principal creator = securityContextAccessor.getPrincipal();
//
//			MultipartFile f = request.getFile("file");
//			RepositoryFile rf = new RepositoryFile();
//			if(name == null) {
//				rf.setOriginalName(f.getOriginalFilename());
//			} else {
//				rf.setOriginalName(HtmlUtils.htmlEscape(name.trim()));
//			}
//			rf.setDataType(f.getContentType());
//			rf.setSize(f.getSize());
//			rf.setPermanent(true);
//			rf.setInputStream(f.getInputStream());		
//			
//
//			Document d = new Document();
//			d.setName(rf.getOriginalName());
//			d.setDescription(HtmlUtils.htmlEscape(description).trim());
//			d.setCreator(creator);
//			d.setTenant(creator.getTenant());
//			
//			documentManager.create(d, rf);
//			
//			auditAction("add", d.getId(), "Document", creator);
//			
//			return new GenericResponse(Status.Success, d.getId().toString(), "Ok");
//	
//		}catch(Exception e) {
//			return new GenericResponse(Status.Failure, null, e.getMessage());
//		}
//	}
//	
//	@RequestMapping(method = RequestMethod.POST, value = {"/documents/update"})
//	public @ResponseBody GenericResponse updateDocument(
//			@RequestParam(value="id") String id,
//			@RequestParam(value="name", required=false) String name,
//			@RequestParam(value="description", required=false) String description,
//			@RequestParam(value="file", required = false) MultipartFile file,
//			
//			HttpSession session,
//			MultipartHttpServletRequest request, 
//			HttpServletResponse response) {
//		try {
//			Principal creator = securityContextAccessor.getPrincipal();
//			
//			RepositoryFile rf= null;
//			if(file != null) {
//				rf = new RepositoryFile();
//				rf.setOriginalName(file.getOriginalFilename());
//				rf.setDataType(file.getContentType());
//				rf.setSize(file.getSize());
//				rf.setPermanent(true);
//				rf.setInputStream(file.getInputStream());		
//			}
//
//			Document d = new Document();
//			d.setId(UUID.fromString(id));
//			if(name != null) {
//				d.setName(HtmlUtils.htmlEscape(name.trim()));
//			}
//			if(description != null) {
//				d.setDescription(HtmlUtils.htmlEscape(description.trim()));
//			}
//			d.setCreator(creator);
//			d.setTenant(creator.getTenant());
//			
//			if(documentManager.update(d, rf, securityContextAccessor.isUser() ? creator : null) == false) {	
//				log.error("Attempt of unauthorized document update: " + "principal=" + creator.getId());
//				return new GenericResponse(Status.Unauthorized, null, "Illegal Access");
//			}
//			
//			auditAction("update", d.getId(), "Document", creator);
//			
//			return new GenericResponse(Status.Success, null, "Ok");
//	
//		}catch(Exception e) {
//			return new GenericResponse(Status.Failure, null, e.getMessage());
//		}
//	}
//	
//	private void doRetrieveDocument(String id,
//			HttpSession session, HttpServletRequest request, HttpServletResponse response, boolean attach) throws DocumentNotFoundException, UnauthorizedOperationException, Exception {
//		if(id == null) {
//			log.error("No id provided for document retrieval");
//			throw new IllegalArgumentException("No id provided for document retrieval");
//		}
//		
//		Principal u = securityContextAccessor.getPrincipal();
//		
//	    RepositoryFile rf = documentManager.getContentById(id);
//	    
//	    response.setContentType(rf.getDataType()); 
//	    response.setHeader("Content-Disposition",(attach?"attachment":"inline") +"; filename=\""+rf.getOriginalName()+"\"");
//	    
//	    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(rf.getLocalImage())));
//		BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
//		
//		byte[] buffer = new byte[1024];
//		int bread = 0;
//		try {
//			while((bread = bis.read(buffer)) != -1)
//			{
//				bos.write(buffer, 0, bread);
//			}
//		}finally {
//			bis.close();
//			bos.close();
//		}
//
//		auditAction("retrieve", UUID.fromString(id), "Document", u);
//	}
	
//	@RequestMapping(value="/documents/retrieve", method = RequestMethod.POST)
//	public void retrieveDocument(@RequestParam("id") String id,
//			HttpSession session, HttpServletRequest request, HttpServletResponse response) {
//		try {
//			doRetrieveDocument(id, session, request, response, true);
//		}catch(UnauthorizedOperationException e)  {
//	    	try { response.sendError(HttpServletResponse.SC_FORBIDDEN); }
//			catch(IOException ee) { log.error("Could not respond with error", ee); }
//	    	return;
//	    }catch(DocumentNotFoundException e) {
//	    	log.error("Error while retrieving document payload", e);
//			try { response.sendError(HttpServletResponse.SC_NOT_FOUND); }
//			catch(IOException ee) { log.error("Could not respond with error", ee); }
//	    }catch(IllegalArgumentException e) {
//	    	log.error("Error while retrieving document payload", e);
//			try { response.sendError(HttpServletResponse.SC_BAD_REQUEST); }
//			catch(IOException ee) { log.error("Could not respond with error", ee); }
//	    }
//		catch(Exception e) {
//			log.error("Error while retrieving document payload", e);
//			try { response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); }
//			catch(IOException ee) { log.error("Could not respond with error", ee); }
//		}
//	};
	
	@RequestMapping(value="/documents/retrievet", method=RequestMethod.POST, consumes="application/json")
	public @ResponseBody GenericResponse getDocumentToken(@RequestBody String id) {
		if(id == null) {
			log.error("No id provided for document retrieval");
			return new GenericResponse(Status.Failure, null, "No id provided for document retrieval");
		}
		Document d = null;
		try {
			d = documentManager.findByIdSecure(id, false);
		}catch(UnauthorizedOperationException uoe) {
			return new GenericResponse(Status.Unauthorized, null, "You are not authorized to retrieve this document");
		}catch(Exception e) {
			log.error("Error while creating token for document " + id, e);
			return new GenericResponse(Status.Failure, null, "Error while retrieving document");
		}
		
		if(d == null) {
			log.error("Cannot create token for document " + id + " because document was not found");
			return new GenericResponse(Status.NotFound, null, "Document " + id + " not found");
		}

		UUID token = UUID.randomUUID();
		documentTokens.put(token, UUID.fromString(id));
		
		HashMap<String, String> res = new HashMap<String, String>();
		res.put(token.toString(), d.getName());
		return new GenericResponse(Status.Success, res, "Ok");
	}
	
//	@RequestMapping(value="/documents/retrieveg", method = RequestMethod.GET)
//	public void retrieveDocumentGet(@RequestParam("t") String token,
//			HttpSession session, HttpServletRequest request, HttpServletResponse response) {
//		UUID tok = null, id = null;
//		try {
//			boolean br = false;
//			if(token == null) {
//				log.error("No token provided for document retrieval");
//				br = true;	
//			}
//			try {
//				if(br == false) {
//					tok = UUID.fromString(token);
//				}
//			}catch(IllegalArgumentException e) {
//				br = true;
//			}
//			
//			if(br == true) {
//				try { response.sendError(HttpServletResponse.SC_BAD_REQUEST); return;}
//				catch(IOException ee) { log.error("Could not respond with error", ee); return; }
//			}
//			
//			id = documentTokens.get(tok);
//			if(id == null) {
//				log.error("Provided token " + token + " does not match any document");
//				try { response.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
//				catch(IOException ee) { log.error("Could not respond with error", ee); return; }
//			}
//			
//			documentTokens.remove(tok);
//			doRetrieveDocument(id.toString(), session, request, response, false);
//			
//		}catch(UnauthorizedOperationException e) {
//	    	try { response.sendError(HttpServletResponse.SC_FORBIDDEN); }
//			catch(IOException ee) { log.error("Could not respond with error", ee); }
//	    	return;
//	    }catch(DocumentNotFoundException e) {
//	    	log.error("Error while retrieving document payload", e);
//			try { response.sendError(HttpServletResponse.SC_NOT_FOUND); }
//			catch(IOException ee) { log.error("Could not respond with error", ee); }
//	    }catch(Exception e) {
//			log.error("Error while retrieving document payload", e);
//			try { response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); return; }
//			catch(IOException ee) { log.error("Could not respond with error", ee); return;}
//		}
//	}
	
	@RequestMapping(value="/documents/delete", method = RequestMethod.POST)
	public GenericResponse deleteDocuments(@RequestParam("documents") List<String> documents) {
		
	    try {
	    	Principal u = securityContextAccessor.getPrincipal();
			try {
				documentManager.delete(documents, u);
			}catch(UnauthorizedOperationException e) {
				return new GenericResponse(Status.Unauthorized, null, "Illegal Access");
			}
	    	
	    	auditAction("delete", null, "Document", u);
	    }catch(Exception e)  {
	    	log.error("An error occurred while deleting documents", e);
			return new GenericResponse(Status.Failure, null, e.getMessage());
	    }
	    return new GenericResponse(Status.Success, null, "Ok");

	};
	
	@RequestMapping(value="/documents/attributeDocuments", method = RequestMethod.POST, consumes="application/json")
	public @ResponseBody GenericResponse attributeDocuments(@RequestBody Map<String, String> values) {
		try {
			Map<String, String> res = null;
			try {
				res = documentManager.attributeDocuments(values);
			}catch(UnauthorizedOperationException e) {
				return new GenericResponse(Status.Unauthorized, res, "Illegal Access");
			}
			return new GenericResponse(Status.Success, res, "Ok");
		}catch(Exception e) {
			log.error("Error while retrieving attribute documents");
			return new GenericResponse(Status.Failure, null, e.getMessage());
		}
	}
	
	@RequestMapping(value={"/projects/list"}, method=RequestMethod.POST)
	@Secured("ROLE_admin")
	public @ResponseBody GenericResponse listProjects() {
		try {
			Principal u = securityContextAccessor.getPrincipal();
			
			List<Map<String, String>> res = projectManager.listIdAndNameOfAllProjects();
			
			auditAction("list", null, "Project", u);
			
			return new GenericResponse(Status.Success, res, "Ok");
		}catch(Exception e) {
			log.error("Error while listing projects", e);
			return new GenericResponse(Status.Failure, null, e.getMessage());
		}
	}
	
	@RequestMapping(value={"/projects/summary"}, method=RequestMethod.POST, consumes={"application/json"}, produces={"application/json"})
	public @ResponseBody GenericResponse retrieveProjectSummaries(@RequestBody UserinfoObject psm) {
		try {
			Principal principal = null;
			try{
				principal = securityContextAccessor.getPrincipal();
			} catch(Exception e) {
				e.printStackTrace();
			}
			if(principal == null){
				throw new Exception();
			}
			
			List<ProjectSummary> res = projectManager.retrieveProjectSummariesForPrincipal(principal);
			
			auditAction("retrieveSummary", null, "Project", principal);
			
			return new GenericResponse(Status.Success, res, "Ok");
		} catch(Exception e) {
			log.error("Error while retrieving project summaries", e);
			return new GenericResponse(Status.Failure, null, e.getMessage());
		}
	}
	 
	@RequestMapping(value={"/projects/bbox"}, method=RequestMethod.POST, consumes={"application/json"})
	public @ResponseBody GenericResponse retrieveProjectBBOX(@RequestBody UserinfoObject uio) {
		try {
			Principal principal = null;
			try{
				Principal currentPrincipal = securityContextAccessor.getPrincipal();
				
				Tenant tenant = tenantManager.findByName(uio.getTenant());
				if(!currentPrincipal.getName().equals(uio.getFullname())){
					principal = principalManager.createPrincipalIfNotExists(uio, tenant);
				} else {
					principal = currentPrincipal;
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
			if(principal == null){
				throw new Exception();
			}
			
			String bbox = shapeManager.getBoundingBoxByProjectNameAndTenant(uio.getProjectName(), uio.getTenant());
			
//			auditAction("retrieveSummary", null, "Project", principal);
			
			return new GenericResponse(Status.Success, bbox, "Ok");
		} catch(Exception e) {
			log.error("Error while retrieving project summaries", e);
			return new GenericResponse(Status.Failure, null, e.getMessage());
		}
	}
	
	@RequestMapping(value={"/projects/bbox2"}, method=RequestMethod.POST, consumes={"application/json"})
	public @ResponseBody GenericResponse retrieveProjectBBOX2(@RequestBody UserinfoObject uio) {
		try {
			Principal principal = null;
			try{
				Principal currentPrincipal = securityContextAccessor.getPrincipal();
				
				Tenant tenant = tenantManager.findByName(uio.getTenant());
				if(!currentPrincipal.getName().equals(uio.getFullname())){
					principal = principalManager.createPrincipalIfNotExists(uio, tenant);
				} else {
					principal = currentPrincipal;
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
			if(principal == null){
				throw new Exception();
			}
			
			String bbox = shapeManager.getBoundingBoxByProjectName(uio.getProjectName());
			
//			auditAction("retrieveSummary", null, "Project", principal);
			
			return new GenericResponse(Status.Success, (Object)bbox, "Ok");
		} catch(Exception e) {
			log.error("Error while retrieving project summaries", e);
			return new GenericResponse(Status.Failure, null, e.getMessage());
		}
	}
	
	@RequestMapping(value={"/projects/brief"}, method=RequestMethod.POST, consumes={"application/json"})
	public @ResponseBody GenericResponse retrieveProjectInBrief(@RequestBody UserinfoObject psm) {
		try {
			Principal principal = null;
			try{
				Principal currentPrincipal = securityContextAccessor.getPrincipal();
				
				Tenant tenant = tenantManager.findByName(psm.getTenant());
				if(!currentPrincipal.getName().equals(psm.getFullname())){
					principal = principalManager.createPrincipalIfNotExists(psm, tenant);
				} else {
					principal = currentPrincipal;
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
			if(principal == null){
				throw new Exception();
			}
			
			List<ProjectSummary> res = projectManager.fetchProjectSummariesForPrincipal(principal, psm.getTenant());
			
			auditAction("retrieveSummary", null, "Project", principal);
			
			return new GenericResponse(Status.Success, res, "Ok");
		} catch(Exception e) {
			log.error("Error while retrieving project summaries", e);
			return new GenericResponse(Status.Failure, null, e.getMessage());
		}
	}
	
	@RequestMapping(value={"/projects/viewDetails"}, method=RequestMethod.POST, consumes={"application/json"})
	public @ResponseBody GenericResponse viewProjectDetails(@RequestBody UserinfoObject psm) {
		try {
			Principal principal = null;
			try{
				Principal currentPrincipal = securityContextAccessor.getPrincipal();
				
				Tenant tenant = tenantManager.findByName(psm.getTenant());
				if(!currentPrincipal.getName().equals(psm.getFullname())){
					principal = principalManager.createPrincipalIfNotExists(psm, tenant);
				} else {
					principal = currentPrincipal;
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
			if(principal == null){
				throw new Exception();
			}
			
			ProjectSummary res = projectManager.fetchProjectSummariesForProjectNameAndTenant(psm.getProjectName(), psm.getTenant());
			
			auditAction("retrieveSummary", null, "Project", principal);
			
			return new GenericResponse(Status.Success, res, "Ok");
		} catch(Exception e) {
			log.error("Error while retrieving project summaries", e);
			return new GenericResponse(Status.Failure, null, e.getMessage());
		}
	}
	
	@RequestMapping(value={"/projects/participants"}, method=RequestMethod.POST, consumes={"application/json"})
	public @ResponseBody GenericResponse retrieveProjectParticipants(@RequestBody UserinfoObject uio) {
		try {
			Principal principal = null;
			try{
				Principal currentPrincipal = securityContextAccessor.getPrincipal();
			
				Tenant tenant = tenantManager.findByName(uio.getTenant());
				if(!currentPrincipal.getName().equals(uio.getFullname())){
					principal = principalManager.createPrincipalIfNotExists(uio, tenant);
				} else {
					principal = currentPrincipal;
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
			if(principal == null){
				throw new Exception();
			}
			
			List<ProjectParticipantInfo> res = projectManager.fetchProjectParticipants(uio.getProjectName(), uio.getTenant());
			return new GenericResponse(Status.Success, res, "Ok");
		} catch(Exception e) {
			log.error("Error while retrieving project summaries", e);
			return new GenericResponse(Status.Failure, null, e.getMessage());
		}
	}
	
	@RequestMapping(value={"/projects/groupmembers"}, method=RequestMethod.POST, consumes={"application/json"})
	public @ResponseBody GenericResponse retrieveGroupMembers(@RequestBody UserinfoObject uio) {
		try {
			Principal principal = null;
			try{
				Principal currentPrincipal = securityContextAccessor.getPrincipal();
			
				Tenant tenant = tenantManager.findByName(uio.getTenant());
				if(!currentPrincipal.getName().equals(uio.getFullname())){
					principal = principalManager.createPrincipalIfNotExists(uio, tenant);
				} else {
					principal = currentPrincipal;
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
			if(principal == null){
				throw new Exception();
			}
			
			List<String> res = principalManager.listPrincipalNamesOfProjectGroupNameAndTenant(uio.getTenant(), uio.getGroupName());
			
			return new GenericResponse(Status.Success, res, "Ok");
		} catch(Exception e) {
			log.error("Error while retrieving project summaries", e);
			return new GenericResponse(Status.Failure, null, e.getMessage());
		}
	}
	
	@RequestMapping(value={"/projects/retrieve"}, method=RequestMethod.POST, consumes="application/json")
	public @ResponseBody GenericResponse retrieveProject(@RequestBody String id) {
		GenericResponse authError = null;
		try {
			Principal principal = securityContextAccessor.getPrincipal();
			ProjectInfo p = this.projectManager.findByIdInfo(id, true);
			
			if(p == null) {
				return new GenericResponse(Status.NotFound, null, "Project " + id + " not found");
			}
			
			authError = checkAuth(p.getProject().getCreator(), principal);
			if(authError != null) {
				log.error("Attempt of unauthorized project retrieval: project creator=" + p.getProject().getCreator().getId() + " principal=" + principal.getId());
				return authError;
			}
			
			Workflow w = projectManager.getWorkflowsOfProject(p.getProject()).get(0); //TODO single workflow
			
			ProjectInfoMessenger res = new ProjectInfoMessenger();
			
			ProjectMessenger pm = new ProjectMessenger();
			pm.setClient(p.getProject().getClient());
			pm.setName(p.getProject().getName());
			pm.setDescription(p.getProject().getDescription());
			pm.setId(p.getProject().getId().toString());
			pm.setStatus(p.getProject().getStatus());
			pm.setTemplate(p.getProject().getIsTemplate());
			pm.setShape(p.getShape() != null ? new WKTWriter().write(p.getShape().getGeography()) : null);
			res.setProjectMessenger(pm);
			
			WorkflowMessenger wm = new WorkflowMessenger();
			wm.setName(w.getName());
			wm.setDescription(w.getDescription());
			wm.setStatus(w.getStatus());
			wm.setStatusDate(w.getStatusDate().getTime());
			wm.setStartDate(w.getStartDate().getTime());
			if(w.getEndDate() != null) {
				wm.setEndDate(w.getEndDate().getTime());
			}
			if(w.getReminderDate() != null) {
				wm.setReminderDate(w.getReminderDate().getTime());
			}
			//wm.setExtraData(HtmlUtils.htmlWeakEscape(w.getExtraData().trim())); TODO
			res.setWorkflowMessenger(wm);
			
			auditAction("retrieve", null, "Project", principal);
			
			return new GenericResponse(Status.Success, res, "Ok");
		}catch(Exception e) {
			log.error("Error while retrieving project summaries", e);
			return new GenericResponse(Status.Failure, null, e.getMessage());
		}
	}
	
	@RequestMapping(value={"/projects/retrieveAllUsersInDB"}, method=RequestMethod.POST, consumes={"application/json"}, produces={"application/json"})
	public @ResponseBody GenericResponse retrieveUsers(@RequestBody UserinfoObject psm) {
		GenericResponse genericResponse = new GenericResponse();
		Principal principal = null;
		try{
			Principal currentPrincipal = securityContextAccessor.getPrincipal();
			
			Tenant tenant = tenantManager.findByName(psm.getTenant());
			if(!currentPrincipal.getName().equals(psm.getFullname())){
				principal = principalManager.createPrincipalIfNotExists(psm, tenant);
			} else {
				principal = currentPrincipal;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		try {
			if(principal == null){
				throw new Exception();
			}
			List<PrincipalProjectInfoDao> retObject = principalManager.collectInfoOfActivePrincipals(principal.getName(), psm.getTenant());
			auditAction("retrieve", null, "Principal", principal);
			return new GenericResponse(Status.Success, retObject, "Ok");
		}catch(Exception e)	 {
			log.error("An error occurred while creating a project", e);
			return new GenericResponse(Status.Failure, null, e.getMessage());
		}
	}
	
	@RequestMapping(value={"/projects/retrieveGroups"}, method=RequestMethod.POST, consumes={"application/json"}, produces={"application/json"})
	public @ResponseBody GenericResponse retrieveGroups(@RequestBody UserinfoObject psm) {
		Principal principal = null;
		Tenant tenant = null;
		try{
			Principal currentPrincipal = securityContextAccessor.getPrincipal();
			
			tenant = tenantManager.findByName(psm.getTenant());
			if(!currentPrincipal.getName().equals(psm.getFullname())){
				principal = principalManager.createPrincipalIfNotExists(psm, tenant);
			} else {
				principal = currentPrincipal;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		try {
			if(principal == null){
				throw new Exception();
			}
			List<ProjectGroupInfo> retObject = principalManager.collectInfoOfActiveProjectGroups(tenant, principal);
			auditAction("retrieve", null, "Principal", principal);
			return new GenericResponse(Status.Success, retObject, "Ok");
		}catch(Exception e)	 {
			log.error("An error occurred while creating a project", e);
			return new GenericResponse(Status.Failure, null, e.getMessage());
		}
	}
	
	@RequestMapping(value={"/projects/retrieveGroupsAndNumOfUsers"}, method=RequestMethod.POST, consumes={"application/json"}, produces={"application/json"})
	public @ResponseBody GenericResponse retrieveGroupsAndNumOfUsers(@RequestBody UserinfoObject psm) {
		Principal principal = null;
		Tenant tenant = null;
		try{
			Principal currentPrincipal = securityContextAccessor.getPrincipal();
			
			tenant = tenantManager.findByName(psm.getTenant());
			if(!currentPrincipal.getName().equals(psm.getFullname())){
				principal = principalManager.createPrincipalIfNotExists(psm, tenant);
			} else {
				principal = currentPrincipal;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		try {
			if(principal == null){
				throw new Exception();
			}
			List<PrincipalProjectInfoDao> retObject = principalManager.collectInfoOfActivePrincipalsAndNumOfMembersByTenant(tenant.getName(), principal);
			auditAction("retrieve", null, "Principal", principal);
			return new GenericResponse(Status.Success, retObject, "Ok");
		}catch(Exception e)	 {
			log.error("An error occurred while creating a project", e);
			return new GenericResponse(Status.Failure, null, e.getMessage());
		}
	}
	
	@RequestMapping(value={"/projects/newProjectGroup"}, method=RequestMethod.POST, consumes={"application/json"}, produces={"application/json"})
	public @ResponseBody GenericResponse newProjectGroup(@RequestBody UserinfoObject uo) {
		Principal principal = null;
		Tenant tenant=null;
		try{
			Principal currentPrincipal = securityContextAccessor.getPrincipal();
			
			tenant = tenantManager.findByName(uo.getTenant());
			if(!currentPrincipal.getName().equals(uo.getFullname())){
				principal = principalManager.createPrincipalIfNotExists(uo, tenant);
			} else {
				principal = currentPrincipal;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		try {
			if(principal == null){
				throw new Exception();
			}
			
			principalManager.createProjectGroup(uo, principal, tenant);
//			auditAction("retrieve", null, "Principal", principal);
			return new GenericResponse(Status.Success, true, "Ok");
		}catch(Exception e)	 {
			log.error("An error occurred while creating a project", e);
			return new GenericResponse(Status.Failure, null, e.getMessage());
		}
	}

	@RequestMapping(value={"/projects/deleteProjectGroup"}, method=RequestMethod.POST, consumes={"application/json"}, produces={"application/json"})
	public @ResponseBody GenericResponse deleteProjectGroup(@RequestBody UserinfoObject uo) {
		Principal principal = null;
		Tenant tenant=null;
		try{
			Principal currentPrincipal = securityContextAccessor.getPrincipal();
			
			tenant = tenantManager.findByName(uo.getTenant());
			if(!currentPrincipal.getName().equals(uo.getFullname())){
				principal = principalManager.createPrincipalIfNotExists(uo, tenant);
			} else {
				principal = currentPrincipal;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		try {
			if(principal == null){
				throw new Exception();
			}
			
			principalManager.deletePrincipalByNameAndTenantName(uo.getGroupName(), tenant.getName());
//			auditAction("retrieve", null, "Principal", principal);
			return new GenericResponse(Status.Success, true, "Ok");
		}catch(Exception e)	 {
			log.error("An error occurred while creating a project", e);
			return new GenericResponse(Status.Failure, null, e.getMessage());
		}
	}
	
	@RequestMapping(value={"/projects/add"}, method=RequestMethod.POST, consumes="application/json")
	public @ResponseBody GenericResponse addProject(@RequestBody ProjectInfoMessenger projectInfoMessenger) {
		GenericResponse genericResponse = new GenericResponse();
		try {
			Principal creator = securityContextAccessor.getPrincipal();
			genericResponse = this.projectManager.create(projectInfoMessenger);
			auditAction("add", null, "Project", creator);
		}catch(Exception e)	 {
			log.error("An error occurred while creating a project", e);
			return new GenericResponse(Status.Failure, null, e.getMessage());
		}
		return genericResponse;
	}
	
	@RequestMapping(value={"/projects/assignUsersToProjectGroup"}, method=RequestMethod.POST, consumes="application/json")
	public @ResponseBody GenericResponse assignUsersToProjectGroup(@RequestBody ProjectGroupMessenger pgm) {
		GenericResponse genericResponse = new GenericResponse();
		Principal principal = null;
		try {
			Principal currentPrincipal = securityContextAccessor.getPrincipal();
			
			Tenant tenant = tenantManager.findByName(pgm.getUio().getTenant());
			if(!currentPrincipal.getName().equals(pgm.getUio().getFullname())){
				principal = principalManager.createPrincipalIfNotExists(pgm.getUio(), tenant);
			} else {
				principal = currentPrincipal;
			}
			List<String> memberNames = Arrays.asList(pgm.getUsers());
			this.principalManager.assignPrincipalsToProjectGroup(memberNames, tenant, pgm.getUio().getGroupName());
			genericResponse = new GenericResponse(Status.Success, null, "Ok");
//			auditAction("add", null, "Project", creator);
		}catch(Exception e)	 {
			log.error("An error occurred while creating a project", e);
			return new GenericResponse(Status.Failure, null, e.getMessage());
		}
		return genericResponse;
	}
	
	@RequestMapping(value={"/projects/create"}, method=RequestMethod.POST, consumes="application/json")
	public @ResponseBody GenericResponse createProject(@RequestBody NewProjectData npd) {
		GenericResponse genericResponse = new GenericResponse();
		Principal creator = null;
		try {
//			Principal creator = securityContextAccessor.getPrincipal();
			Tenant tenant = tenantManager.findByName(npd.getUserinfoObject().getTenant());
			genericResponse = this.projectManager.createNewProject(npd, tenant);
			auditAction("add", null, "Project", (Principal)genericResponse.getResponse());
			genericResponse.setResponse(null);
		}catch(Exception e)	 {
			log.error("An error occurred while creating a project", e);
			return new GenericResponse(Status.Failure, null, e.getMessage());
		} 
		return genericResponse;
	}
	
	@RequestMapping(value={"/projects/updateProject"}, method=RequestMethod.POST, consumes="application/json")
	public @ResponseBody GenericResponse updateCurentProject(@RequestBody NewProjectData npd) {
		GenericResponse genericResponse = new GenericResponse();
		Principal principal = null;
		try {
			
			Principal currentPrincipal = securityContextAccessor.getPrincipal();
			
			Tenant tenant = tenantManager.findByName(npd.getUserinfoObject().getTenant());
			if(!currentPrincipal.getName().equals(npd.getUserinfoObject().getFullname())){
				principal = principalManager.createPrincipalIfNotExists(npd.getUserinfoObject(), tenant);
			} else {
				principal = currentPrincipal;
			}
//			principal = principalManager.createPrincipalIfNotExists(npd.getUserinfoObject(), tenant);
			if(npd.getNameAndDescriptionObject().getName() == null)throw new Exception("Project" + npd.getNameAndDescriptionObject().getName() + " not found");
			Project oldProject = projectManager.findByNameAndTenant(npd.getNameAndDescriptionObject().getOldName(), npd.getUserinfoObject().getTenant());
			if(oldProject == null) throw new Exception("Project" + npd.getNameAndDescriptionObject().getOldName() + " not found");
			npd.setOldprojectId(oldProject.getId());
			this.projectManager.updateCurrentProject(npd, oldProject);
			auditAction("update", null, "Project", principal);
			genericResponse = new GenericResponse(Status.Success, null, "Ok");
		}catch(Exception e)	 {
			log.error("An error occurred while creating a project", e);
			return new GenericResponse(Status.Failure, null, e.getMessage());
		} 
		return genericResponse;
	}
	
	@RequestMapping(value={"/projects/update"}, method=RequestMethod.POST, consumes="application/json")
	public @ResponseBody GenericResponse updateProject(@RequestBody ProjectInfoMessenger projectInfoMessenger) {
		GenericResponse genericResponse = new GenericResponse();
		try {
			Principal principal = securityContextAccessor.getPrincipal();
			genericResponse = projectManager.update(projectInfoMessenger);
			auditAction("update", null, "Project", principal);
		}catch(Exception e)	 {
			log.error("An error occurred while updating a project", e);
			return new GenericResponse(Status.Failure, null, e.getMessage());
		}
		return genericResponse;
	}
	
	@RequestMapping(value={"/projects/remove"}, method=RequestMethod.POST, consumes="application/json")
	public @ResponseBody GenericResponse removeProject(@RequestBody String id) {
		GenericResponse authError = null;
		try {
			if(id == null) {
				log.error("Project id not provided");
				return new GenericResponse(GenericResponse.Status.Failure, null, "Project id not provided");
			}
			Principal principal = securityContextAccessor.getPrincipal();
			
			Project p = projectManager.findById(id, true, true);
			if(p == null) {
				log.error("Project " + id + " does not exist");
				return new GenericResponse(GenericResponse.Status.NotFound, null, "Project " + id + " does not exist");
			}
			
			authError = checkAuth(p.getCreator(), principal);
			if(authError != null) {
				log.error("Attempt of unauthorized project removal: project creator=" + p.getCreator().getId() + " principal=" + principal.getId());
				return authError;
			}
			
			projectManager.delete(id, false);
			
			auditAction("remove", null, "Project", principal);
			
		}catch(Exception e)	{
			log.error("An error occurred while deleting a project", e);
			return new GenericResponse(Status.Failure, null, e.getMessage());
		}
		return new GenericResponse(Status.Success, null, "Ok");
	}
	
	@RequestMapping(value={"/projects/delete"}, method=RequestMethod.POST, consumes="application/json")
	public @ResponseBody GenericResponse deleteProject(@RequestBody UserinfoObject uio) {
		GenericResponse authError = null;
		Principal principal = null;
		try {
			if(uio.getProjectName() == null) {
				log.error("Project name not provided");
				return new GenericResponse(GenericResponse.Status.Failure, null, "Project name not provided");
			}
			Principal currentPrincipal = securityContextAccessor.getPrincipal();
			
			Tenant tenant = tenantManager.findByName(uio.getTenant());
			if(!currentPrincipal.getName().equals(uio.getFullname())){
				principal = principalManager.createPrincipalIfNotExists(uio, tenant);
			} else {
				principal = currentPrincipal;
			}
			
			Project p = projectManager.findByNameAndTenant(uio.getProjectName(), uio.getTenant());
			if(p == null) {
				log.error("Project " + uio.getProjectName() + " does not exist");
				return new GenericResponse(GenericResponse.Status.NotFound, null, "Project " + uio.getProjectName() + " does not exist");
			}
			
			projectManager.deleteProject(p.getId().toString(), false);
			
			auditAction("remove", null, "Project", principal);
			
		}catch(Exception e)	{
			log.error("An error occurred while deleting a project", e);
			return new GenericResponse(Status.Failure, null, e.getMessage());
		}
		return new GenericResponse(Status.Success, null, "Ok");
	}
	
	@RequestMapping(value={"/projects/retrieveInfo"}, method=RequestMethod.POST)
	public @ResponseBody GenericResponse retrieveProjectInfo(@RequestBody String id) {
		Map<String, AttributeInfo> info = new HashMap<String, AttributeInfo>();
		GenericResponse authError = null;
		try {
			Principal u = securityContextAccessor.getPrincipal();
			
			Project p = projectManager.findById(id, true, true);
			
			if(p == null) {
				log.error("Project " + id + " not found");
				return new GenericResponse(Status.NotFound, null, "Project " + id + " not found");
			}
			
			authError = checkAuth(p.getCreator(), u);
			if(authError != null) {
				log.error("Attempt of unauthorized project info retrieval: project creator=" + p.getCreator().getId() + " principal=" + u.getId());
				return authError;
			}
			
			info = projectManager.retrieveProjectInfo(p);
			
			auditAction("retrieveInfo", UUID.fromString(id), "Project", u);
		}catch(Exception e) {
			log.error("An error has occurred while retrieving project info", e);
			return new GenericResponse(Status.Failure, null, e.getMessage());
		}
		return new GenericResponse(Status.Success, info, "Ok");
		
	}
	
	@RequestMapping(value={"/projects/updateInfo"}, method=RequestMethod.POST)
	public @ResponseBody GenericResponse updateProjectInfo(@RequestBody ProjectAttributeMessenger pam) {
		GenericResponse authError = null;
		try {
			Principal principal = securityContextAccessor.getPrincipal();
			
			if(pam.getProjectId() == null)
			{
				log.error("Missing project id");
				return new GenericResponse(Status.Failure, null, "Missing project id");
			}
			if(pam.getAttribute() == null)
			{
				log.error("Missing attribute info");
				return new GenericResponse(Status.Failure, null, "Missing attribute info");
			}
			
			Project p = projectManager.findById(pam.getProjectId(), true, true);
			
			if(p == null)
			{
				log.error("Project " + pam.getProjectId() + " not found");
				return new GenericResponse(Status.Failure, null, "Project " + pam.getProjectId() + " not found");
			}
			
			authError = checkAuth(p.getCreator(), principal);
			if(authError != null)
			{
				log.error("Attempt of unauthorized project info retrieval: project creator=" + p.getCreator().getId() + " principal=" + principal.getId());
				return authError;
			}
			
			projectManager.updateProjectAttribute(p, principal, pam.getAttribute());
			
			auditAction("updateInfo", UUID.fromString(pam.getProjectId()), "Project", principal);
		}catch(Exception e)
		{
			log.error("An error has occurred while updating project info", e);
			return new GenericResponse(Status.Failure, null, e.getMessage());
		}
		return new GenericResponse(Status.Success, null, "Ok");
		
	}
	
	@RequestMapping(value={"/projects/addAttribute"}, method=RequestMethod.POST)
	public @ResponseBody GenericResponse addProjectAttribute(@RequestBody ProjectAttributeMessenger pam) {
		GenericResponse authError = null;
		try {
			Principal u = securityContextAccessor.getPrincipal();

			if (pam.getProjectId() == null) {
				log.error("Missing project id");
				return new GenericResponse(Status.Failure, null, "Missing project id");
			}
			if (pam.getAttribute() == null) {
				log.error("Missing attribute info");
				return new GenericResponse(Status.Failure, null, "Missing attribute info");
			}

			Project p = projectManager.findById(pam.getProjectId(), true, false);
			if (p == null) {
				log.error("Project " + pam.getProjectId() + " was not found");
				return new GenericResponse(Status.NotFound, null, "Project " + pam.getProjectId() + " was not found");
			}

			authError = checkAuth(p.getCreator(), u);
			if (authError != null) {
				log.error("Attempt of unauthorized project attribute addition: project creator="
						+ p.getCreator().getId() + " principal=" + u.getId());
				return authError;
			}

			projectManager.addProjectAttribute(p, u, pam.getAttribute(), pam.getAttributeClassType());

			auditAction("addAttribute", p.getId(), "Project", u);

			return new GenericResponse(Status.Success, null, "Ok");
		} catch (Exception e) {
			log.error("An error has occurred while adding a project attribute", e);
			return new GenericResponse(Status.Failure, null, e.getMessage());
		}
	}
	
	@RequestMapping(value={"/projects/removeAttribute"}, method=RequestMethod.POST)
	public @ResponseBody GenericResponse removeProjectAttribute(@RequestBody ProjectAttributeMessenger pam) {
		GenericResponse authError = null;
		try {
			Principal principal = securityContextAccessor.getPrincipal();

			if (pam.getProjectId() == null) {
				log.error("Missing project id");
				return new GenericResponse(Status.Failure, null, "Missing project id");
			}
			if (pam.getAttribute() == null) {
				log.error("Missing attribute info");
				return new GenericResponse(Status.Failure, null, "Missing attribute info");
			}

			Project p = projectManager.findById(pam.getProjectId(), true, false);
			if (p == null) {
				log.error("Project " + pam.getProjectId() + " was not found");
				return new GenericResponse(Status.NotFound, null, "Project " + pam.getProjectId() + " was not found");
			}

			authError = checkAuth(p.getCreator(), principal);
			if (authError != null) {
				log.error("Attempt of unauthorized project attribute removal: project creator=" + p.getCreator().getId()
						+ " principal=" + principal.getId());
				return authError;
			}

			projectManager.removeProjectAttribute(p, principal, pam.getAttribute());

			auditAction("removeAttribute", p.getId(), "Project", principal);

			return new GenericResponse(Status.Success, null, "Ok");
		} catch (Exception e) {
			log.error("An error has occurred while adding a project attribute", e);
			return new GenericResponse(Status.Failure, null, e.getMessage());
		}
	}
	
	@RequestMapping(value={"/projects/commonUserAttributes"}, method=RequestMethod.POST)
	public @ResponseBody GenericResponse retrieveCommonUserAttributes(@RequestBody String id) {
		List<AttributeInfo> info = new ArrayList<AttributeInfo>();
		GenericResponse authError = null;
		try {
			Principal principal = securityContextAccessor.getPrincipal();

			Project p = projectManager.findById(id, true, true);

			if (p == null) {
				log.error("Project " + id + " not found");
				return new GenericResponse(Status.Failure, null, "Project " + id + " not found");
			}

			authError = checkAuth(p.getCreator(), principal);
			if (authError != null) {
				log.error("Attempt of unauthorized project info retrieval: project creator=" + p.getCreator().getId()
						+ " principal=" + principal.getId());
				return authError;
			}

			info = projectManager.retrieveCommonUserAttributes(principal);

			auditAction("retrieveCommonUserAttributes", UUID.fromString(id), "Project", principal);
		} catch (Exception e) {
			log.error("An error has occurred while retrieving project info", e);
			return new GenericResponse(Status.Failure, null, e.getMessage());
		}
		return new GenericResponse(Status.Success, info, "Ok");
	}
	
	@RequestMapping(value={"/projects/retrieveTasks"}, method=RequestMethod.POST)
	public @ResponseBody GenericResponse retrieveProjectTasks(@RequestBody String id) {
		GenericResponse authError = null;
		try {
			if (id == null) {
				log.error("Project id not provided");
				return new GenericResponse(GenericResponse.Status.Failure, null, "Project id not provided");
			}
			Principal principal = securityContextAccessor.getPrincipal();

			Project p = projectManager.findById(id, true, true);
			if (p == null) {
				log.error("Project " + id + " does not exist");
				return new GenericResponse(GenericResponse.Status.NotFound, null, "Project " + id + " does not exist");
			}

			authError = checkAuth(p.getCreator(), principal);
			if (authError != null) {
				log.error("Attempt of unauthorized project task retrieval: project creator=" + p.getCreator().getId()
						+ " principal=" + principal.getId());
				return authError;
			}

			List<WorkflowTaskMessenger> res = projectManager.retrieveProjectTasks(p);

			auditAction("retrieveTasks", UUID.fromString(id), "Project", principal);

			return new GenericResponse(Status.Success, res, "Ok");
		} catch (Exception e) {
			return new GenericResponse(Status.Failure, null, e.getMessage());
		}
	}
	
	@RequestMapping(value={"/projects/addTask"}, method=RequestMethod.POST, consumes="application/json")
	public @ResponseBody GenericResponse addProjectTask(@RequestBody WorkflowTaskMessenger task) {
		GenericResponse authError = null;
		try {
			if (task == null || task.getName() == null) {
				log.error("Task " + (task != null ? "name " : "") + "not provided");
				return new GenericResponse(GenericResponse.Status.Failure, null,
						"Task " + (task != null ? "name " : "") + "not provided");
			}

			Principal creator = securityContextAccessor.getPrincipal();

			if (task.getProject() == null && task.getWorkflow() == null) {
				return new GenericResponse(GenericResponse.Status.Failure, null, "Illegal argument");
			}

			Project exP = null;
			Workflow exW = null;
			if (task.getProject() != null) {
				exP = projectManager.findById(task.getProject(), false, false);
				if (exP == null) {
					log.error("Project " + task.getProject() + " does not exist");
					return new GenericResponse(GenericResponse.Status.NotFound, null,
							"Project " + task.getProject() + " does not exist");
				}
			} else {
				exW = projectManager.findWorkflowById(task.getWorkflow(), true);
				if (exW == null) {
					log.error("Workflow " + task.getWorkflow() + " does not exist");
					return new GenericResponse(GenericResponse.Status.NotFound, null,
							"Workflow " + task.getWorkflow() + " does not exist");
				}
				exP = exW.getProject();
			}

			authError = checkAuth(exP.getCreator(), creator);
			if (authError != null) {
				log.error("Attempt of unauthorized project task creation: project creator=" + exP.getCreator().getId()
						+ " principal=" + creator.getId());
				return authError;
			}

			WorkflowTask t = new WorkflowTask();
			t.setCreator(creator);

			t.setName(HtmlUtils.htmlEscape(task.getName().trim()));
			if (task.getStartDate() != null) {
				t.setStartDate(new Date(task.getStartDate()));
			}
			if (task.getCritical() != null) {
				t.setCritical(task.getCritical());
			}
			t.setWorkflow(exW);

			if (task.getEndDate() != null) {
				if (task.getStartDate() != null) {
					if (task.getEndDate() <= task.getStartDate()) {
						return new GenericResponse(Status.InvalidDate, null, "Invalid start/end date");
					}
				}
				t.setEndDate(new Date(task.getEndDate()));
			}
			if (task.getReminderDate() != null) {
				if (task.getStartDate() != null) {
					if (task.getReminderDate() < task.getStartDate()) {
						return new GenericResponse(Status.InvalidDate, null, "Reminder date earlier than start date");
					}
				}

				if (task.getEndDate() != null) {
					if (task.getReminderDate() > task.getEndDate()) {
						return new GenericResponse(Status.InvalidDate, null, "Reminder date later than end date");
					}
				}

				t.setReminderDate(new Date(task.getReminderDate()));
			}

			projectManager.createTask(exP, t);

			auditAction("addTask", null, "Project", creator);
		} catch (Exception e) {
			log.error("An error occurred while adding a workflow task", e);
			return new GenericResponse(Status.Failure, null, e.getMessage());
		}
		return new GenericResponse(Status.Success, null, "Ok");
	}
	
	@RequestMapping(value={"/projects/updateTask"}, method=RequestMethod.POST, consumes="application/json")
	public @ResponseBody GenericResponse updateTask(@RequestBody WorkflowTaskMessenger task) {
		GenericResponse authError = null;
		try {
			if (task == null || task.getId() == null) {
				log.error("Task " + (task != null ? "id " : "") + "not provided");
				return new GenericResponse(GenericResponse.Status.Failure, null,
						"Task " + (task != null ? "id " : "") + "not provided");
			}

			Principal principal = securityContextAccessor.getPrincipal();

			WorkflowTask ex = projectManager.findTaskById(task.getId(), true);
			if (ex == null) {
				log.error("Task " + task.getId() + " does not exist");
				return new GenericResponse(GenericResponse.Status.NotFound, null,
						"Task " + task.getId() + " does not exist");
			}

			authError = checkAuth(ex.getCreator(), principal);
			if (authError != null) {
				log.error("Attempt of unauthorized task update: task creator=" + ex.getCreator().getId() + " principal="
						+ principal.getId());
				return authError;
			}

			WorkflowTask t = new WorkflowTask();
			t.setId(UUID.fromString(task.getId()));
			t.setCritical(task.getCritical());
			if (task.getStartDate() != null) {
				t.setStartDate(new Date(task.getStartDate()));
			}
			if (task.getEndDate() != null) {
				t.setEndDate(new Date(task.getEndDate()));
			}
			if (task.getReminderDate() != null) {
				t.setReminderDate(new Date(task.getReminderDate()));
			}
			if (task.getExtraData() != null) {
				t.setExtraData(HtmlUtils.htmlWeakEscape(task.getExtraData().trim()));
			}
			if (task.getStatus() != null) {
				t.setStatus(task.getStatus());
			}
			if (task.getName() != null) {
				t.setName(HtmlUtils.htmlEscape(task.getName().trim()));
			}

			if (task.getEndDate() != null) {
				if (task.getStartDate() != null) {
					if (task.getEndDate() <= task.getStartDate()) {
						return new GenericResponse(Status.InvalidDate, null, "Invalid start/end date");
					}
				}
				t.setEndDate(new Date(task.getEndDate()));
			}
			if (task.getReminderDate() != null) {
				if (task.getStartDate() != null) {
					if (task.getReminderDate() < task.getStartDate()) {
						return new GenericResponse(Status.InvalidDate, null, "Reminder date earlier than start date");
					}
				}

				if (task.getEndDate() != null) {
					if (task.getReminderDate() > task.getEndDate()) {
						return new GenericResponse(Status.InvalidDate, null, "Reminder date later than end date");
					}
				}

				t.setReminderDate(new Date(task.getReminderDate()));
			}

			projectManager.updateTask(t.getWorkflow().getProject(), t);

			auditAction("updateTask", null, "Project", principal);

		} catch (Exception e) {
			log.error("An error occurred while updating a task", e);
			return new GenericResponse(Status.Failure, null, e.getMessage());
		}
		return new GenericResponse(Status.Success, null, "Ok");
	}
	
	@RequestMapping(value={"/projects/removeTask"}, method=RequestMethod.POST, consumes="application/json")
	public @ResponseBody GenericResponse removeTask(@RequestBody String id) {
		GenericResponse authError = null;
		try {
			Principal principal = securityContextAccessor.getPrincipal();

			WorkflowTask t = projectManager.findTaskById(id, true);
			if (t == null) {
				log.error("Task " + id + " does not exist");
				return new GenericResponse(GenericResponse.Status.NotFound, null, "Task " + id + " does not exist");
			}

			authError = checkAuth(t.getCreator(), principal);
			if (authError != null) {
				log.error("Attempt of unauthorized task removal: task creator=" + t.getCreator().getId() + " principal="
						+ principal.getId());
				return authError;
			}

			projectManager.deleteTask(id);

			auditAction("removeTask", null, "Project", principal);

		} catch (Exception e) {
			log.error("An error occurred while deleting a project", e);
			return new GenericResponse(Status.Failure, null, e.getMessage());
		}
		return new GenericResponse(Status.Success, null, "Ok");
	}
	
	@RequestMapping(value={"/projects/retrieveDocuments"}, method=RequestMethod.POST, consumes="application/json")
	public @ResponseBody GenericResponse retrieveProjectDocuments(@RequestBody String id) {
		GenericResponse authError = null;
		try {
			Principal principal = securityContextAccessor.getPrincipal();

			Project p = projectManager.findById(id, true, true);
			if (p == null) {
				log.error("Project " + id + " does not exist");
				return new GenericResponse(GenericResponse.Status.ProjectNotFound, null,
						"Project " + id + " does not exist");
			}

			authError = checkAuth(p.getCreator(), principal);
			if (authError != null) {
				log.error("Attempt of unauthorized project document retrieval: project creator="
						+ p.getCreator().getId() + " principal=" + principal.getId());
				return authError;
			}

			List<DocumentMessenger> res = projectManager.retrieveProjectDocuments(p);

			auditAction("retrieveDocuments", UUID.fromString(id), "Project", principal);
			return new GenericResponse(Status.Success, res, "Ok");
		} catch (Exception e) {
			log.error("Error while retrieving project documents", e);
			return new GenericResponse(Status.Failure, null, e.getMessage());
		}
	}
	
	@RequestMapping(value={"/projects/addProjectDocument"}, method=RequestMethod.POST)
	public @ResponseBody GenericResponse addProjectDocument(
			@RequestParam("projectId") String projectId,
			@RequestParam("documentId") String documentId) {
		GenericResponse authError = null;
		try {
			Principal principal = securityContextAccessor.getPrincipal();

			Project p = projectManager.findById(projectId, true, true);
			if (p == null) {
				log.error("Project " + projectId + " does not exist");
				return new GenericResponse(GenericResponse.Status.ProjectNotFound, null,
						"Project " + projectId + " does not exist");
			}

			authError = checkAuth(p.getCreator(), principal);
			if (authError != null) {
				log.error("Attempt of unauthorized project document addition: project creator=" + p.getCreator().getId()
						+ " principal=" + principal.getId());
				return authError;
			}

			Document d = documentManager.findById(documentId, true);
			if (d == null) {
				log.error("Document " + documentId + " does not exist");
				return new GenericResponse(GenericResponse.Status.DocumentNotFound, null,
						"Document " + documentId + " does not exist");
			}

			authError = checkAuth(d.getCreator(), principal);
			if (authError != null) {
				log.error("Attempt of unauthorized project document addition: document creator="
						+ p.getCreator().getId() + " principal=" + principal.getId());
				return authError;
			}

			projectManager.addProjectDocument(projectId, documentId);

			auditAction("addProjectDocument", UUID.fromString(documentId), "ProjectDocument", principal);
			return new GenericResponse(Status.Success, null, "Ok");
		} catch (Exception e) {
			log.error("Error while adding project document", e);
			return new GenericResponse(Status.Failure, null, e.getMessage());
		}
	}
	
	@RequestMapping(value={"/projects/removeProjectDocument"}, method=RequestMethod.POST)
	public @ResponseBody GenericResponse removeProjectDocument(
			@RequestParam("projectId") String projectId,
			@RequestParam("documentId") String documentId) {
		GenericResponse authError = null;
		try {
			Principal principal = securityContextAccessor.getPrincipal();

			Project p = projectManager.findById(projectId, true, true);
			if (p == null) {
				log.error("Project " + projectId + " does not exist");
				return new GenericResponse(GenericResponse.Status.ProjectNotFound, null,
						"Project " + projectId + " does not exist");
			}

			authError = checkAuth(p.getCreator(), principal);
			if (authError != null) {
				log.error("Attempt of unauthorized project document removal: project creator=" + p.getCreator().getId()
						+ " principal=" + principal.getId());
				return authError;
			}

			Document d = documentManager.findById(documentId, true);
			if (d == null) {
				log.error("Document " + documentId + " does not exist");
				return new GenericResponse(GenericResponse.Status.DocumentNotFound, null,
						"Document " + documentId + " does not exist");
			}

			authError = checkAuth(d.getCreator(), principal);
			if (authError != null) {
				log.error("Attempt of unauthorized project document removal: document creator=" + p.getCreator().getId()
						+ " principal=" + principal.getId());
				return authError;
			}
			projectManager.removeProjectDocument(projectId, documentId);

			auditAction("removeProjectDocument", UUID.fromString(documentId), "ProjectDocument", principal);
			return new GenericResponse(Status.Success, null, "Ok");
		} catch (Exception e) {
			log.error("Error while removing project document", e);
			return new GenericResponse(Status.Failure, null, e.getMessage());
		}
	}
	
	@RequestMapping(value={"/projects/retrieveTaskDocuments"}, method=RequestMethod.POST, consumes="application/json")
	public @ResponseBody GenericResponse retrieveTaskDocuments(
			@RequestBody String taskId) {
		GenericResponse authError = null;
		try {
			if (taskId == null) {
				log.error("Task id not provided");
				return new GenericResponse(GenericResponse.Status.Failure, null, "Task id not provided");
			}

			Principal principal = securityContextAccessor.getPrincipal();

			WorkflowTask t = projectManager.findTaskById(taskId, true);
			if (t == null) {
				log.error("Task " + taskId + " does not exist");
				return new GenericResponse(GenericResponse.Status.NotFound, null, "Task " + taskId + " does not exist");
			}

			authError = checkAuth(t.getCreator(), principal);
			if (authError != null) {
				log.error("Attempt of unauthorized task document retrieval: task creator=" + t.getCreator().getId()
						+ " principal=" + principal.getId());
				return authError;
			}

			List<DocumentMessenger> ds = projectManager.retrieveWorkflowTaskDocuments(t);

			auditAction("retrieveTaskDocuments", UUID.fromString(taskId), "WorkflowTask", principal);

			return new GenericResponse(Status.Success, ds, "Ok");
		} catch (Exception e) {
			log.error("Error while retrieving document tasks", e);
			return new GenericResponse(Status.Failure, null, e.getMessage());
		}
	}
	
	@RequestMapping(value={"/projects/retrieveDocumentTasks"}, method=RequestMethod.POST)
	public @ResponseBody GenericResponse retrieveDocumentTasks(
			@RequestParam("projectId") String projectId,
			@RequestParam("documentId") String documentId) {
		GenericResponse authError = null;
		try {
			Principal principal = securityContextAccessor.getPrincipal();

			Project p = projectManager.findById(projectId, true, true);
			if (p == null) {
				log.error("Project " + projectId + " does not exist");
				return new GenericResponse(GenericResponse.Status.ProjectNotFound, null,
						"Project " + projectId + " does not exist");
			}

			authError = checkAuth(p.getCreator(), principal);
			if (authError != null) {
				log.error("Attempt of unauthorized project document task retrieval: project creator="
						+ p.getCreator().getId() + " principal=" + principal.getId());
				return authError;
			}

			Document d = documentManager.findById(documentId, true);
			if (d == null) {
				log.error("Document " + documentId + " does not exist");
				return new GenericResponse(GenericResponse.Status.DocumentNotFound, null,
						"Document " + documentId + " does not exist");
			}

			authError = checkAuth(d.getCreator(), principal);
			if (authError != null) {
				log.error("Attempt of unauthorized project document task retrieval: document creator="
						+ p.getCreator().getId() + " principal=" + principal.getId());
				return authError;
			}

			List<WorkflowTaskMessenger> res = projectManager.retrieveDocumentTasks(p, d);

			auditAction("retrieveDocumentTasks", UUID.fromString(documentId), "WorkflowTask", principal);

			return new GenericResponse(Status.Success, res, "Ok");
		} catch (Exception e) {
			log.error("Error while retrieving document tasks", e);
			return new GenericResponse(Status.Failure, null, e.getMessage());
		}
	}
	
	@RequestMapping(value={"/projects/addTaskDocument"}, method=RequestMethod.POST)
	public @ResponseBody GenericResponse addTaskDocument(
			@RequestParam("taskId") String taskId,
			@RequestParam("documentId") String documentId) {
		GenericResponse authError = null;
		try {
			Principal principal = securityContextAccessor.getPrincipal();

			WorkflowTask t = projectManager.findTaskById(taskId, true);
			if (t == null) {
				log.error("Task " + taskId + " does not exist");
				return new GenericResponse(GenericResponse.Status.TaskNotFound, null,
						"Task " + taskId + " does not exist");
			}

			authError = checkAuth(t.getPrincipal(), principal);
			if (authError != null) {
				log.error("Attempt of unauthorized project document task addition: task user="
						+ t.getPrincipal().getId() + " principal=" + principal.getId());
				return authError;
			}

			Document d = documentManager.findById(documentId, true);
			if (d == null) {
				log.error("Document " + documentId + " does not exist");
				return new GenericResponse(GenericResponse.Status.DocumentNotFound, null,
						"Document " + documentId + " does not exist");
			}

			authError = checkAuth(d.getCreator(), principal);
			if (authError != null) {
				log.error("Attempt of unauthorized project document task addition: document creator="
						+ d.getCreator().getId() + " principal=" + principal.getId());
				return authError;
			}

			GenericResponse.Status s = projectManager.addWorkflowTaskDocument(taskId, documentId);
			if (s != GenericResponse.Status.Success) {
				return new GenericResponse(s, null, "");
			}

			auditAction("addDocumentTask", UUID.fromString(documentId), "WorkflowTaskDocument", principal);
			return new GenericResponse(Status.Success, null, "Ok");
		} catch (Exception e) {
			log.error("Error while adding task document", e);
			return new GenericResponse(Status.Failure, null, e.getMessage());
		}
	}
	
	@RequestMapping(value={"/projects/removeTaskDocument"}, method=RequestMethod.POST)
	public @ResponseBody GenericResponse removeTaskDocument(
			@RequestParam("taskId") String taskId,
			@RequestParam("documentId") String documentId) {
		GenericResponse authError = null;
		try {
			Principal principal = securityContextAccessor.getPrincipal();

			WorkflowTask t = projectManager.findTaskById(taskId, true);
			if (t == null) {
				log.error("Task " + taskId + " does not exist");
				return new GenericResponse(GenericResponse.Status.TaskNotFound, null,
						"Task " + taskId + " does not exist");
			}

			authError = checkAuth(t.getPrincipal(), principal);
			if (authError != null) {
				log.error("Attempt of unauthorized project document task removal: task user=" + t.getPrincipal().getId()
						+ " principal=" + principal.getId());
				return authError;
			}

			Document d = documentManager.findById(documentId, true);
			if (d == null) {
				log.error("Document " + documentId + " does not exist");
				return new GenericResponse(GenericResponse.Status.DocumentNotFound, null,
						"Document " + documentId + " does not exist");
			}

			authError = checkAuth(d.getCreator(), principal);
			if (authError != null) {
				log.error("Attempt of unauthorized project document task removal: document creator="
						+ d.getCreator().getId() + " principal=" + principal.getId());
				return authError;
			}

			projectManager.removeWorkflowTaskDocument(taskId, documentId);

			auditAction("removeDocumentTask", UUID.fromString(documentId), "WorkflowTaskDocument", principal);
			return new GenericResponse(Status.Success, null, "Ok");
		} catch (Exception e) {
			log.error("Error while removing task document", e);
			return new GenericResponse(Status.Failure, null, e.getMessage());
		}
	}
	
	@RequestMapping(value={"/shapes/geoLocate"}, method=RequestMethod.POST)
	public @ResponseBody GenericResponse geoLocate(@RequestParam("x")double x, @RequestParam("y")double y) {
		try {
			List<TaxonomyTerm> terms = shapeManager.geoLocate(x, y);
			List<GeoLocationTag> tags = new ArrayList<GeoLocationTag>();
			for (TaxonomyTerm term : terms) {
				String classTermId = (term.getTaxonomyTermClass() != null
						? term.getTaxonomyTermClass().getId().toString() : null);
				String classTermTag = (term.getTaxonomyTermClass() != null ? term.getTaxonomyTermClass().getName()
						: null);
				Shape tts = taxonomyManager.getShapeOfTerm(term);
				Point centroid = tts.getGeography().getCentroid();
				Geometry b = tts.getGeography().getEnvelope();
				Bounds bounds = new Bounds(b.getCoordinates()[0].x, b.getCoordinates()[0].y, b.getCoordinates()[1].x,
						b.getCoordinates()[1].y, null);
				tags.add(new GeoLocationTag(term.getId().toString(), term.getName(), classTermId, classTermTag,
						centroid.getX(), centroid.getY(), bounds));
			}
			return new GenericResponse(Status.Success, new GeoLocation(tags, x, y, null), "Ok");
		} catch (Exception e) {
			log.error("Error while geo-locating point", e);
			return new GenericResponse(Status.Failure, null, e.getMessage());
		}
	}
	
	@RequestMapping(value={"/shapes/termLocate"}, method=RequestMethod.POST)
	public @ResponseBody GenericResponse termLocate(@RequestParam("term")GeoSearchSelection selection) {
		try {
			Principal principal = securityContextAccessor.getPrincipal();
			List<GeoLocation> shapes = shapeManager.termLocate(selection.getType(), selection.getTerm(), principal);
			return new GenericResponse(Status.Success, shapes, "Ok");
		} catch (Exception e) {
			log.error("Error while geo-locating term", e);
			return new GenericResponse(Status.Failure, null, e.getMessage());
		}
	}
	
	@RequestMapping(value={"/shapes/attributeLocate"}, method=RequestMethod.POST, consumes={"application/json"})
	public @ResponseBody GenericResponse attributeLocate(@RequestBody GeoSearchSelection selection) throws Exception {
		try {
			Principal principal = securityContextAccessor.getPrincipal();
			List<GeoLocation> shapes = shapeManager.attributeLocate(selection.getType(), selection.getAttributes(),
					principal);
			return new GenericResponse(Status.Success, shapes, "Ok");
		} catch (Exception e) {
			log.error("Error while geo-locating term", e);
			throw e; // TODO remove
			// return new UserRequestResponse(Status.Failure, null,
			// e.getMessage());
		}
	}	
	
	@RequestMapping(value= "/shapes/listTemplateLayers" ,method=RequestMethod.GET, produces={"application/json"})
	public @ResponseBody ServiceResponse getLayersOfType() {
		try {
			Set<String> templateLayers = new HashSet<>();
			List<String> accessLayers = securityContextAccessor.getLayers();
			for (String layerName : accessLayers) {
				TaxonomyTerm taxonomyTerm = taxonomyManager.findTermByName(layerName, false);
				if (taxonomyTerm != null) {	
					templateLayers.add(taxonomyTerm.getName());
				}
			}
			return new ServiceResponse(true, templateLayers, "");
		} catch (Exception e) {			
			log.error("Error while retrieving layers\n" + e);
			return new ServiceResponse(false, null, "Error while retrieving layers from server.");
		}
	}	
	
	@RequestMapping(value= "/shapes/listLayersByProject" ,method=RequestMethod.POST, consumes={"application/json"}, produces={"application/json"})
	public @ResponseBody Set<LayerMessengerForJSTREE> getLayersByProject(@RequestBody UserinfoObject uio) {
		Principal principal = null;
		try {
			Principal currentPrincipal = securityContextAccessor.getPrincipal();
			GeoServerBridgeConfig config = configuration.getGeoServerBridgeConfig();
			
			Tenant tenant = tenantManager.findByName(uio.getTenant());
			if(!currentPrincipal.getName().equals(uio.getFullname())){
				principal = principalManager.createPrincipalIfNotExists(uio, tenant);
			} else {
				principal = currentPrincipal;
			}
			Set<LayerMessengerForJSTREE> jstreeResponse = null;		
			
			jstreeResponse = new HashSet<LayerMessengerForJSTREE>();
			
			List<String> retrieveProjectLayers = projectManager.retrieveProjectLayers(uio.getProjectName(), uio.getTenant());
			for (String layerName : retrieveProjectLayers) {
				LayerMessengerForJSTREE token = new LayerMessengerForJSTREE();
				token.setChildren(false);
				token.setId("");
				token.setState(false, false, false);
				token.setText(layerName);
				jstreeResponse.add(token);
			}
			
			return jstreeResponse;
		} catch (Exception e) {
			log.error("Error while retrieving layers");
			return null;
		}
	}
	
	@RequestMapping(value= "/shapes/geoServerBridgeWorkspace" ,method=RequestMethod.POST, consumes={"application/json"}, produces={"application/json"})
	public @ResponseBody GenericResponse getGesoserverWorkspace(@RequestBody UserinfoObject uio) {
		Principal principal = null;
		try {
//			Principal currentPrincipal = securityContextAccessor.getPrincipal();
			GeoServerBridgeConfig config = configuration.getGeoServerBridgeConfig();
			
//			Tenant tenant = tenantManager.findByName(uio.getTenant());
//			if(!currentPrincipal.getName().equals(uio.getFullname())){
//				principal = principalManager.createPrincipalIfNotExists(uio, tenant);
//			} else {
//				principal = currentPrincipal;
//			}
			
			return new GenericResponse(Status.Success, config.getGeoServerBridgeWorkspace(), "Ok");
		} catch (Exception e) {
			log.error("Error while retrieving layers");
			return new GenericResponse(Status.Failure, null, "");
		}
	}
	
	
	@RequestMapping(value= "/shapes/listLayersOfTypeOrderedByTaxonomy" , method=RequestMethod.POST, consumes={"application/json"}, produces={"application/json"})
	public @ResponseBody Set<TaxonomyMessengerForJSTree> getLayersOfTypeOrderedByTaxonomy(@RequestBody JSTREEToServerToken requestINFO) {
		try {
			List<String> accessLayers = securityContextAccessor.getLayers();
			Set<TaxonomyMessengerForJSTree> jstreeResponse = new HashSet<TaxonomyMessengerForJSTree>();

			for (String l : accessLayers) {
				List<TaxonomyConfig> taxonomyConfigs = configurationManager
						.retrieveTaxonomyConfig(requestINFO.getType());
				if (taxonomyConfigs != null) {
					for (TaxonomyConfig taxonomyConfig : taxonomyConfigs) {
						String taxonomyName = taxonomyManager.findTaxonomyById(taxonomyConfig.getId(), false).getName();
						UUID taxonomyID = taxonomyManager.findTaxonomyById(taxonomyConfig.getId(), false).getId();
						if (taxonomyManager.findTermByNameAndTaxonomy(l, taxonomyName, false) != null) {
							TaxonomyMessengerForJSTree token = new TaxonomyMessengerForJSTree();
							token.setId(taxonomyID.toString());
							token.setText(taxonomyName);
							token.setChildren(true);
							jstreeResponse.add(token);
						}
					}
				}
			}
			return jstreeResponse;

		} catch (Exception e) {
			log.error("Error while retrieving layers for type " + requestINFO.getType());
			return null;
		}
	}
	
	@RequestMapping(value= "/shapes/listLayersOfType" , method=RequestMethod.POST, consumes={"application/json"}, produces={"application/json"})
	public @ResponseBody Set<TaxonomyMessengerForJSTree> getAllLayers(@RequestBody JSTREEToServerToken requestINFO) {
		try {
			List<String> accessLayers = securityContextAccessor.getLayers();
			Set<TaxonomyMessengerForJSTree> jstreeResponse = new HashSet<TaxonomyMessengerForJSTree>();
			
			for (String l : accessLayers) {
				TaxonomyMessengerForJSTree token = new TaxonomyMessengerForJSTree();
				token.setId("");
				token.setText(l);
				token.setChildren(false);
//						jstreeResponse.add(token);
				TaxonomyTerm t = taxonomyManager.findTermByName(l, true);
				if (t == null) {
					continue;
				}
				LayerConfig templateLayerConfig = configurationManager.getLayerConfig(t);
				if(templateLayerConfig==null){
					continue;
				}
				LayerBounds bounds = null;
				bounds = new LayerBounds(templateLayerConfig.getBoundingBox());
				double[] extent1 = requestINFO.getGeographyExtent();
				double[] extent2 = new double[4];
				extent2[0] = bounds.getMinX();
				extent2[1] = bounds.getMinY();
				extent2[2] = bounds.getMaxX();
				extent2[3] = bounds.getMaxY();
				if(projectManager.extentIntersects(extent1, extent2)){
					jstreeResponse.add(token);
				}
			}
			return jstreeResponse;

		} catch (Exception e) {
			log.error("Error while retrieving layers for type " + requestINFO.getType());
			e.printStackTrace();
			return null;
		}
	}
	
	@RequestMapping(value= "/shapes/listOfAllAvailableLayers" , method=RequestMethod.POST, consumes={"application/json"}, produces={"application/json"})
	public @ResponseBody Set<TaxonomyMessengerForJSTree> getAllAvailableLayers(@RequestBody JSTREEToServerToken requestINFO) {
		try {
			List<String> accessLayers = securityContextAccessor.getLayers();
			Set<TaxonomyMessengerForJSTree> jstreeResponse = new HashSet<TaxonomyMessengerForJSTree>();
			
			for (String l : accessLayers) {
				TaxonomyMessengerForJSTree token = new TaxonomyMessengerForJSTree();
				token.setId("");
				token.setText(l);
				token.setChildren(false);
				if(l.equals("") || l.equals("hereIsTheName")){continue;}
				TaxonomyTerm t = taxonomyManager.findTermByName(l, true);
				if (t == null) {
					continue;
				}
				LayerConfig templateLayerConfig = configurationManager.getLayerConfig(t);
				if(templateLayerConfig==null){
					continue;
				}
				jstreeResponse.add(token);
			}
			return jstreeResponse;

		} catch (Exception e) {
			log.error("Error while retrieving layers for type " + requestINFO.getType());
			e.printStackTrace();
			return null;
		}
	}
	
	@RequestMapping(value= "/shapes/listLayersOfTypeOrderedByTaxonomyID" , method=RequestMethod.POST, consumes={"application/json"}, produces={"application/json"})
	public @ResponseBody Set<LayerMessengerForJSTREE> getLayersOfTypeOrderedByTaxonomyID(@RequestBody JSTREEToServerToken requestINFO) {
		try {
			Set<LayerMessengerForJSTREE> jstreeResponse = null;

			TaxonomyConfig taxonomyConfig = configurationManager
					.retrieveTaxonomyConfigById(requestINFO.getTaxonomyID().toString());
			if (taxonomyConfig != null && taxonomyConfig.getType().equals(requestINFO.getType())) {
				jstreeResponse = new HashSet<LayerMessengerForJSTREE>();

				List<String> accessLayers = securityContextAccessor.getLayers();
				String taxonomyName = taxonomyManager.findTaxonomyById(taxonomyConfig.getId(), false).getName();
				for (String l : accessLayers) {
					TaxonomyTerm taxonomyTerm = taxonomyManager.findTermByNameAndTaxonomy(l, taxonomyName, false);
					if (taxonomyTerm != null) {
						LayerMessengerForJSTREE token = new LayerMessengerForJSTREE();
						token.setChildren(false);
						token.setId(taxonomyTerm.getId().toString());
						token.setState(false, false, false);
						token.setText(taxonomyTerm.getName());
						jstreeResponse.add(token);
					}
				}
			}

			return jstreeResponse;
		} catch (Exception e) {
			log.error("Error while retrieving layers for type " + requestINFO.getType());
			return null;
		}
	}
	
	@RequestMapping(value= "/shapes/calculateSample" , method=RequestMethod.POST, consumes={"application/json"})
	public @ResponseBody GenericResponse calculateSample(@RequestBody DummyModel dummyModel) {
		try {
			System.out.println("Layer 1 : " + dummyModel.getLayer1());
			// System.out.println("Layer 2 : " + dummyModel.getLayer2());

			this.executionManager.sampleCalulateModel(dummyModel);

			return new GenericResponse(Status.Success, null, "");
		} catch (Exception e) {
			log.error("Error while calculating model");
			return new GenericResponse(Status.Failure, null, e.getMessage());
		}
	}
	
	@RequestMapping(value = "/geography/terms", method = RequestMethod.POST)
	public @ResponseBody List<TaxonomyTermMessenger> retrieveTaxonomyTerms(@RequestBody String taxonomy) throws Exception {
		Taxonomy t = taxonomyManager.findTaxonomyByName(taxonomy, false);
		if (t == null) {
			throw new Exception("Taxonomy " + taxonomy + " does not exist");
		}

		if (!TaxonomyUtils.isGeographic(t)) {
			throw new Exception("Not a geographic taxonomy: " + taxonomy);
		}

		List<TaxonomyTerm> tts = taxonomyManager.getTermsOfTaxonomy(t.getId().toString(), true, true);
		List<TaxonomyTermMessenger> terms = new ArrayList<TaxonomyTermMessenger>();
		for (TaxonomyTerm tt : tts) {
			Shape ttShape = taxonomyManager.getShapeOfTerm(tt);
			String val = shapeManager.retrieveShapeAttributeByTaxonomy(ttShape, t.getId().toString()).getValue();
			TaxonomyTermMessenger ttm = new TaxonomyTermMessenger(tt, null, val);
			terms.add(ttm);
		}

		return terms;
	}
	
	@RequestMapping(value = "/geography/children", method = RequestMethod.POST)
	public @ResponseBody List<TaxonomyTermMessenger> retrieveTermChildren(@RequestBody String term) throws Exception {
		TaxonomyTerm t = taxonomyManager.findTermByName(term, true);
		if(t == null) {
			throw new Exception("Term " + term + " does not exist");
		}
		if(!TaxonomyUtils.isGeographic(t.getTaxonomy())) {
			throw new Exception("Not a geographic taxonomy: " + t.getTaxonomy().getName());
		}
				
		List<TaxonomyTerm> tts = taxonomyManager.getChildrenOfTerm(t.getId().toString(), true, true);
		List<TaxonomyTermMessenger> terms = new ArrayList<TaxonomyTermMessenger>();
		for(TaxonomyTerm tt : tts) {
			Shape ttShape = taxonomyManager.getShapeOfTerm(tt);
			String val = shapeManager.retrieveShapeAttributeByTaxonomy(ttShape, tt.getTaxonomy().getId().toString()).getValue();
			TaxonomyTermMessenger ttm = new TaxonomyTermMessenger(tt, null, val);
			terms.add(ttm);
		}
		
		return terms;
	}
	
	@RequestMapping(value = "/geography/attributeValues", method = RequestMethod.POST)
	public @ResponseBody Set<String> retrieveAttributeValues(@RequestBody String taxonomy) throws Exception {
		/* SearchAttributeValueMessenger sav,
		Taxonomy t = taxonomyManager.findTaxonomyByName(savm.getTaxonomy(), false);
		if(t == null) throw new Exception("Taxonomy " + savm.getTaxonomy() + " does not exist");
		
		TaxonomyConfig tcfg = configurationManager.retrieveTaxonomyConfigById(t.getId().toString());
		if(tcfg == null) throw new Exception("Taxonomy " + savm.getTaxonomy() + " not configured"); //just check if the client's request corresponds to a  valid searchable taxonomy
		
		Taxonomy geographic = taxonomyManager.findTaxonomyByName(savm.getGeographicTaxonomy(), false);
		if(geographic == null) throw new Exception("Taxonomy " + savm.getGeographicTaxonomy() + " does not exist");
		
		if(geographic.getExtraData() == null) throw new Exception("Not a geographic taxonomy: " + geographic);*/
		
		Taxonomy t = taxonomyManager.findTaxonomyByName(taxonomy, false);
		if(t == null) {
			throw new Exception("Taxonomy " + taxonomy + " does not exist");
		}
		
		TaxonomyConfig tcfg = configurationManager.retrieveTaxonomyConfigById(t.getId().toString());
		if(tcfg == null)
		 {
			throw new Exception("Taxonomy " + taxonomy + " not configured"); //just check if the client's request corresponds to a  valid searchable taxonomy
		}
		
		Set<String> values = shapeManager.getShapeAttributeValues(t);
		return values;
	}
	
/*	@RequestMapping(value={"/notifications"}, method=RequestMethod.POST)
	public @ResponseBody GenericResponse getNotifications(@RequestParam(value="eventType", required=false)String eventType) {
		try {
			Principal principal = securityContextAccessor.getPrincipal();
			if(eventType != null)
				return new GenericResponse(Status.Success, notificationManager.poll(principal.getNotificationId(), EventType.valueOf(eventType), true), "Ok");
			else
				return new GenericResponse(Status.Success, notificationManager.poll(principal.getNotificationId(), true), "Ok");
		}catch(Exception e)
		{
			log.error("Error while retrieving user notifications", e);
			return new GenericResponse(Status.Failure, null, e.getMessage());
		}
	}*/
	
}
