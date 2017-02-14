package gr.cite.geoanalytics.security;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;

import gr.cite.gaap.servicelayer.ConfigurationManager;
import gr.cite.gaap.servicelayer.TaxonomyManager;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.global.TaxonomyConfig;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.Taxonomy;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTerm;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.dao.TaxonomyTermDao;
import gr.cite.geoanalytics.manager.PrincipalManager;
import gr.cite.geoanalytics.manager.UserManager;

public class GeoanalyticsPortalPreAuthenticationProvider extends PreAuthenticatedAuthenticationProvider {
	private static final Logger log = LoggerFactory.getLogger(GeoanalyticsPortalPreAuthenticationProvider.class);
	
	private UserManager userManager = null;
	private PrincipalManager principalManager= null;
	
	/*********************************/
	//TODO temporary
	@Resource
	private ConfigurationManager configurationManager;
	@Resource
	private TaxonomyManager taxonomyManager;
	@Resource
	private TaxonomyTermDao taxonomyTermDao;
	/***********************************/
	
	private static final long maxAcceptableLoginRateDefault = 20;
	private static final long accountLockCheckPeriodDefault = 40;
	private static final TimeUnit accountLockCheckPeriodUnitDefault = TimeUnit.SECONDS; 
	private static final long accountLockPeriodDefault = 30;
	private static final TimeUnit accountLockPeriodUnitDefault = TimeUnit.MINUTES; 
	
	
	private long maxAcceptableLoginRate = maxAcceptableLoginRateDefault;
	private long accountLockCheckPeriod = accountLockCheckPeriodDefault;
	private TimeUnit accountLockCheckPeriodUnit = accountLockCheckPeriodUnitDefault;
	private long accountLockPeriod = accountLockPeriodDefault;
	private TimeUnit accountLockPeriodUnit = accountLockPeriodUnitDefault;
	
	@Inject
	public void setPrincipalManager(PrincipalManager principalManager) {
		this.principalManager = principalManager;
	}
	
	@Inject
	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}
	
	public GeoanalyticsPortalPreAuthenticationProvider() {
		super();
		log.info("Using default max acceptable login rate: " + maxAcceptableLoginRateDefault + " attempts in" + 
				accountLockCheckPeriodDefault + " " + accountLockCheckPeriodUnitDefault);
		log.info("Using default account lock duration : " + accountLockPeriodDefault + " " + accountLockPeriodUnitDefault);
		
	}
	
	public GeoanalyticsPortalPreAuthenticationProvider(long maxAcceptableLoginRate, long accountLockCheckPeriod, TimeUnit accountLockCheckPeriodUnit,
			long accountLockPeriod,  TimeUnit accountLockPeriodUnit) throws Exception {
		if(maxAcceptableLoginRate <= 0) throw new Exception("Invalid maximum acceptable login rate: " + maxAcceptableLoginRate);
		if(accountLockCheckPeriod <= 0) throw new Exception("Invalid account lock check period: " + accountLockCheckPeriod);
		if(accountLockCheckPeriodUnit == null) throw new Exception("Invalid account lock check period unit");
		if(accountLockPeriod <= 0) throw new Exception("Invalid account lock period: " + accountLockPeriod);
		if(accountLockPeriodUnit == null) throw new Exception("Invalid account lock period unit");
		
		this.maxAcceptableLoginRate = maxAcceptableLoginRate;
		this.accountLockCheckPeriod = accountLockCheckPeriod;
		this.accountLockCheckPeriodUnit = accountLockCheckPeriodUnit;
		this.accountLockPeriod = accountLockPeriod;
		this.accountLockPeriodUnit = accountLockPeriodUnit;
		
		log.info("Using max acceptable login rate: " + maxAcceptableLoginRate + " attempts in" + 
				accountLockCheckPeriod+ " " + accountLockCheckPeriodUnit);
		log.info("Using account lock duration : " + accountLockPeriod + " " + accountLockPeriodUnit);
	}
	
	 @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		 if(authentication.isAuthenticated())
			 return authentication;
		 Principal principal = null;
		 try
		  {
			  principal = principalManager.getPrincipalByNameAndActivity(authentication.getName(), null);
		  }catch(Exception e)  {
			  log.error("Could not retrieve user information", e);
			  throw new AuthenticationServiceException("Could not retrieve user information", e);
		  }
		  
	      try  {
	        Authentication auth = super.authenticate(authentication);
	        if(auth != null) {
//	        	boolean isAdmin = false;
//	        	for(GrantedAuthority ga : auth.getAuthorities())
//	        	{
//	        		if(ga.getAuthority().equals(Role.ROLE_admin))
//	        		{
//	        			isAdmin = true;
//	        			break;
//	        		}
//	        	}
//	        	try
//	        	{
//	        		auditingManager.auditLogin(principal);
//	        	}catch(Exception e)
//	        	{
//	        		log.error("Could not audit successful login for user " + auth.getName());
//	        	}
	        }
	        if(auth == null)
	        	throw new BadCredentialsException("auth is null");
	        List<String> layers = null;
	        
	        /***********************/
			//TODO Temporary -- remove
			
			List<TaxonomyConfig> layerTaxonomies = null;
			try {
				layerTaxonomies = configurationManager.retrieveTaxonomyConfig(TaxonomyConfig.Type.LAYERTAXONOMY, false);
			} catch (Exception ee) {
				// TODO Auto-generated catch block
				ee.printStackTrace();
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
		
			/**************************/
			
	        try {
				//layers = userManager.getAccessibleLayers(principal);//TODO
	        }catch(Exception e) {
	        	throw new AuthenticationServiceException("Could not retrieve layers", e);
	        }
	        
	        
	        return new GeoanalyticsPreauthenticationToken(auth.getPrincipal(), auth.getCredentials(), auth.getAuthorities(), layers);
	      } catch (BadCredentialsException e) 
	      {
//	    	try
//	      	{
//	    		UserLastUnsuccessfulLoginInfo ull = administrationManager.getLastUnsuccessfulLoginForUser(principal);
//		    	int times = 0;
//		    	if(new Date().getTime() - (ull != null ? ull.getTimestamp() : 0) < TimeUnit.MILLISECONDS.convert(accountLockCheckPeriod, accountLockCheckPeriodUnit))
//		    	{
//		    		times = (ull != null ? ull.getTimes() : 0) + 1;
//		    		if(times > maxAcceptableLoginRate)
//		    			securityManager.lockUser(principal);
//		    	}
//		    	HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes())
//		                   .getRequest(); 
//		        auditingManager.auditUnsuccessfulLogin(principal, times, request.getRemoteAddr());
//	      	}catch(Exception ee)
//	      	{
//	      		log.error("Could not audit login failure for user " + authentication.getName());
//	      		//throw new AuthenticationServiceException("Could not audit login failure for user " + authentication.getName(), e);
//	      	}
	    	  
	        throw e;
	      }
	      catch(UsernameNotFoundException ue)
	      {
	    	  //TODO audit dos and block ips
	    	  
	    	  //hide UserNameNotFoundException and throw BadCredentialsException. this immitates the default behavior of AbstractUserAuthenticationProvider
	    	  //which is considered more secure, since it will prevent attackers from knowing what the real underlying problem was, thereby preventing a facilitation
	    	  //of their attack. If the username form is left blank however, there is no meaning in hiding UsernameNotFoundException so, only in this case, the exception is simply rethrown
	    	  if(authentication.getName().isEmpty()) throw ue;
	    	  throw new BadCredentialsException("user not found");
	      }
	   }
}
