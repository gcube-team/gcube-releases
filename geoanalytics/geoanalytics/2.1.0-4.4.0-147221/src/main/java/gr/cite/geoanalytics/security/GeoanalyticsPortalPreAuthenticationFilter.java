package gr.cite.geoanalytics.security;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import gr.cite.geoanalytics.security.GeoanalyticsUserDetailsService;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import gr.cite.gaap.servicelayer.ConfigurationManager;
import gr.cite.gaap.servicelayer.GeocodeManager;
import gr.cite.geoanalytics.dataaccess.dao.UUIDGenerator;
import gr.cite.geoanalytics.dataaccess.entities.geocode.Geocode;
import gr.cite.geoanalytics.dataaccess.entities.geocode.GeocodeSystem;
import gr.cite.geoanalytics.dataaccess.entities.geocode.dao.GeocodeDao;
import gr.cite.geoanalytics.dataaccess.entities.layer.Layer;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.security.principal.dao.PrincipalDao;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.global.TaxonomyConfig;
import gr.cite.geoanalytics.dataaccess.entities.tenant.Tenant;
import gr.cite.geoanalytics.dataaccess.entities.tenant.dao.TenantDao;
import gr.cite.geoanalytics.manager.LayerManager;
import gr.cite.geoanalytics.manager.PrincipalManager;
import gr.cite.geoanalytics.manager.TenantManager;
import gr.cite.geoanalytics.manager.UserManager;

public class GeoanalyticsPortalPreAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter
{

	private static Logger log = LoggerFactory.getLogger(GeoanalyticsPortalPreAuthenticationFilter.class);
	
	private UserDetailsService userDetailsService;
	private GeoanalyticsUserDetailsService geoanalyticsUserDetailsService;
	private UserManager userManager;
	private ConfigurationManager configurationManager;
	private GeocodeManager taxonomyManager;
	private GeocodeDao geocodeDao;
	private PrincipalManager principalManager;
	private LayerManager layerManager;
	private TenantManager tenantManager;
	private PrincipalDao principalDao;
	
	@Inject
	public void setPrincipalDao(PrincipalDao principalDao) {
		this.principalDao = principalDao;
	}
	
	@Inject
	public void setTenantManager(TenantManager tenantManager) {
		this.tenantManager = tenantManager;
	}
	
	@Inject
	public void setLayerManager(LayerManager layerManager) {
		this.layerManager = layerManager;
	}
	
	@Inject
	public void setPrincipalManager(PrincipalManager principalManager) {
		this.principalManager = principalManager;
	}
	
	@Inject
	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}
	
	@Inject
	public void setGeoanalyticsUserDetailsService(GeoanalyticsUserDetailsService geoanalyticsUserDetailsService) {
		this.geoanalyticsUserDetailsService = geoanalyticsUserDetailsService;
	}
	
	@Inject
	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}
	
	@Inject
	public void setConfigurationManager(ConfigurationManager configurationManager) {
		this.configurationManager = configurationManager;
	}
	
	@Inject
	public void setTaxonomyManager(GeocodeManager taxonomyManager) {
		this.taxonomyManager = taxonomyManager;
	}
	
	@Inject
	public void setGeocodeDao(GeocodeDao geocodeDao) {
		this.geocodeDao = geocodeDao;
	}
	
	@Override
	protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
		// Get the IP address of the user trying to use the site
		if(SecurityContextHolder.getContext() != null && 
				SecurityContextHolder.getContext().getAuthentication() != null &&
				SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null)
			return SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		log.info("************** User attempting to connect *******************");
		
		
		String principalName = request.getHeader("username");
		String principalEmail = request.getHeader("email");
		String principalInitials = request.getHeader("initials");
		String userId = request.getHeader("useruuid");
		String tenantName = request.getHeader("tenant");
		log.info("Username: " + principalName);
		log.info("Email: " + principalEmail);
		log.info("VreUsrID: " + userId);
		log.info("Tenant: " + tenantName);
		
		
		if(principalName == null || principalEmail == null || principalInitials == null || userId == null || tenantName == null)
			return null;

		UUID userUUIDvreId = UUID.fromString(userId);
		
		log.info("User provided all required info. Continuing ..........");
		
		UserDetails principal = null;
		try
    	{
			 try
			  {
				 principal = ((GeoanalyticsUserDetailsService) userDetailsService).loadUserByVreUsrIdAndTenantOrCreateOrUpdate(userUUIDvreId, tenantName, principalName, principalEmail, principalInitials);
				  
			  }catch(Exception e)
			  {
				  log.error("Could not retrieve user information", e);
				  throw new AuthenticationServiceException("Could not retrieve user information", e);
			  }
			 
			 /***********************/
			 List<String> layers = new ArrayList<>();
//			List<TaxonomyConfig> layerTaxonomies = null;
			 List<TaxonomyConfig> layerTaxonomies = new ArrayList<TaxonomyConfig>();
			try {
//				layerTaxonomies = configurationManager.retrieveTaxonomyConfig(TaxonomyConfig.Type.LAYERTAXONOMY, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			List<GeocodeSystem> taxonomies = new ArrayList<GeocodeSystem>();
			for(TaxonomyConfig lt : layerTaxonomies){
				taxonomies.add(taxonomyManager.findGeocodeSystemById(lt.getId(), false));
			}
			
			List<Geocode> layerGranted = new ArrayList<Geocode>();
			for(GeocodeSystem t : taxonomies) {
				layerGranted.addAll(geocodeDao.findByGeocodeSystem(t));
			}
			layers = layerGranted.stream().map(x -> x.getName()).collect(Collectors.toList());
			
			GeoanalyticsPreauthenticationToken t = null;
			
			/**************************/
			List<Layer> allLayers = new ArrayList<Layer>();
			
			try {
//				allLayers = layerManager.getAllLayers();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			List<UUID> allLayersIDs = new ArrayList<UUID>();
			allLayersIDs = allLayers.stream().map(layer -> layer.getId()).collect(Collectors.toList());
		
			Tenant tenant = tenantManager.findByName(tenantName);
			
			if(principal == null){
				List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
				t =  new GeoanalyticsPreauthenticationToken(principal, null, grantedAuthorities, layers, allLayersIDs, userUUIDvreId, tenant.getId());
			}else {
				/**************************/
				t =  new GeoanalyticsPreauthenticationToken(principal, null, principal.getAuthorities(), layers, allLayersIDs, userUUIDvreId, tenant.getId());
			}
            
            t.setAuthenticated(true);
            return t;
    	   
    	     
    	 }catch(Exception e)
    	 {
    		 throw new AuthenticationServiceException("An error has occurred while authenticating portal user", e);
    	 }
	}



	@Override
	protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
		return "unsused";
	}
   
}

