package gr.cite.geoanalytics.security;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.http.Cookie;
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
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import gr.cite.gaap.servicelayer.ConfigurationManager;
import gr.cite.gaap.servicelayer.TaxonomyManager;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.global.TaxonomyConfig;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.Taxonomy;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTerm;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.dao.TaxonomyTermDao;
import gr.cite.geoanalytics.manager.PrincipalManager;
import gr.cite.geoanalytics.manager.UserManager;

public class GeoanalyticsPortalPreAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter
{

	private static Logger log = LoggerFactory.getLogger(GeoanalyticsPortalPreAuthenticationFilter.class);
	
	private UserDetailsService userDetailsService;
	private GeoanalyticsUserDetailsService geoanalyticsUserDetailsService;
	private UserManager userManager;
	private ConfigurationManager configurationManager;
	private TaxonomyManager taxonomyManager;
	private TaxonomyTermDao taxonomyTermDao;
	private PrincipalManager principalManager;
	
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
	public void setTaxonomyManager(TaxonomyManager taxonomyManager) {
		this.taxonomyManager = taxonomyManager;
	}
	
	@Inject
	public void setTaxonomyTermDao(TaxonomyTermDao taxonomyTermDao) {
		this.taxonomyTermDao = taxonomyTermDao;
	}
	
	@Override
	protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
		// Get the IP address of the user trying to use the site
		if(SecurityContextHolder.getContext() != null && 
				SecurityContextHolder.getContext().getAuthentication() != null &&
				SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null)
			return SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String userName = null;
		boolean usernameFieldInCookies = false;
		if(request.getCookies() != null) {
			for(Cookie c : request.getCookies()) {
			
				if(c.getName().equals("username")) {
					userName = c.getValue();
					try{
						userName = URLDecoder.decode(userName,"UTF-8");
					} catch(UnsupportedEncodingException e){
						e.printStackTrace();
					}
					usernameFieldInCookies = true;
					break;
				}
			}
		}
		if(userName == null)
			return null;
		
		UserDetails principal = null;
		try
    	{
			 try
			  {
				  principal = userDetailsService.loadUserByUsername(userName);
			  }catch(Exception e)
			  {
				  log.error("Could not retrieve user information", e);
				  throw new AuthenticationServiceException("Could not retrieve user information", e);
			  }
			 
			 /***********************/
				//TODO Temporary -- remove
				
			 List<String> layers = new ArrayList<>();
			List<TaxonomyConfig> layerTaxonomies = null;
			try {
				layerTaxonomies = configurationManager.retrieveTaxonomyConfig(TaxonomyConfig.Type.LAYERTAXONOMY, false);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			List<Taxonomy> taxonomies = new ArrayList<Taxonomy>();
			for(TaxonomyConfig lt : layerTaxonomies){
				taxonomies.add(taxonomyManager.findTaxonomyById(lt.getId(), false));
			}
			
			//Taxonomy layert = taxonomyManager.findTaxonomyById(layerTaxonomy.getId(), false);
			
			List<TaxonomyTerm> layerGranted = new ArrayList<TaxonomyTerm>();
			for(Taxonomy t : taxonomies) {
				layerGranted.addAll(taxonomyTermDao.findByTaxonomy(t));
			}
			layers = layerGranted.stream().map(x -> x.getName()).collect(Collectors.toList());
			
			GeoanalyticsPreauthenticationToken t = null;
		
			if(principal == null){
				List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
				t =  new GeoanalyticsPreauthenticationToken(principal, null, grantedAuthorities, layers);
			}else {
				/**************************/
				t =  new GeoanalyticsPreauthenticationToken(principal, null, principal.getAuthorities(), layers);
			}
            
			//GeoanalyticsPreauthenticationToken t =  new GeoanalyticsPreauthenticationToken(principal, null, principal.getAuthorities(), userManager.getAccessibleLayers(principalManager.getPrincpalByNameAndActivity(userName, null)));
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

