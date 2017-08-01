package gr.cite.geoanalytics.security;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import gr.cite.gaap.datatransferobjects.UserLastLoginInfo;
import gr.cite.gaap.datatransferobjects.UserLastUnsuccessfulLoginInfo;
import gr.cite.gaap.servicelayer.ConfigurationManager;
import gr.cite.gaap.servicelayer.GeocodeManager;
import gr.cite.geoanalytics.dataaccess.entities.ActiveStatus;
import gr.cite.geoanalytics.dataaccess.entities.geocode.Geocode;
import gr.cite.geoanalytics.dataaccess.entities.geocode.GeocodeSystem;
import gr.cite.geoanalytics.dataaccess.entities.geocode.dao.GeocodeDao;
import gr.cite.geoanalytics.dataaccess.entities.layer.Layer;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.global.TaxonomyConfig;
import gr.cite.geoanalytics.manager.AuditingManager;
import gr.cite.geoanalytics.manager.LayerManager;
import gr.cite.geoanalytics.manager.PrincipalManager;
import gr.cite.geoanalytics.manager.ProjectManager;
import gr.cite.geoanalytics.manager.UserManager;
import gr.cite.geoanalytics.manager.admin.AdministrationManager;
import gr.cite.geoanalytics.notifications.EventType;
import gr.cite.geoanalytics.notifications.NotificationManager;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class GeoanalyticsUserAuthenticationProvider extends DaoAuthenticationProvider {
	private static final Logger log = LoggerFactory.getLogger(GeoanalyticsUserAuthenticationProvider.class);
	
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
	
	
	
	@Resource
	private UserManager userManager;
	@Resource
	private ProjectManager projectManager;
	@Resource
	private AuditingManager auditingManager;
	@Resource
	private AdministrationManager administrationManager;
	@Resource
	private ConfigurationManager configurationManager;
	@Resource
	private NotificationManager notificationManager;
	@Resource
	private PrincipalManager principalManager;
	/*********************************/
	//TODO temporary
	@Resource
	private GeocodeManager taxonomyManager;
	@Resource
	private LayerManager layerManager;
	@Resource
	private GeocodeDao geocodeDao;
	/***********************************/
	
	public GeoanalyticsUserAuthenticationProvider() {
		super();
		log.info("Using default max acceptable login rate: " + maxAcceptableLoginRateDefault + " attempts in" + 
				accountLockCheckPeriodDefault + " " + accountLockCheckPeriodUnitDefault);
		log.info("Using default account lock duration : " + accountLockPeriodDefault + " " + accountLockPeriodUnitDefault);
		setHideUserNotFoundExceptions(false);
		
	}
	
	public GeoanalyticsUserAuthenticationProvider(long maxAcceptableLoginRate, long accountLockCheckPeriod, TimeUnit accountLockCheckPeriodUnit,
			long accountLockPeriod,  TimeUnit accountLockPeriodUnit) throws Exception {
		super();
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
		Principal principal = null;
		try {
			principal = principalManager.getPrincipalByNameAndActivity(authentication.getName(), null);
			if (principal != null) {
				if (principalManager.isActiveStatusByActiveStatusAndName(principal.getName(), ActiveStatus.LOCKED)) {
					UserLastLoginInfo lastSuccessful = administrationManager.getLastLoginForUser(principal);
					UserLastUnsuccessfulLoginInfo lastUnsuccessful = administrationManager
							.getLastUnsuccessfulLoginForUser(principal);
					long mostRecent = (lastSuccessful != null ? lastSuccessful.getTimestamp()
							: 0) > (lastUnsuccessful != null ? lastUnsuccessful.getTimestamp() : 0)
									? (lastSuccessful != null ? lastSuccessful.getTimestamp() : 0)
									: (lastUnsuccessful != null ? lastUnsuccessful.getTimestamp() : 0);
					if (new Date().getTime() - mostRecent > TimeUnit.MILLISECONDS.convert(accountLockPeriod,
							accountLockPeriodUnit))
						principalManager.setActivityStatus(principal, ActiveStatus.ACTIVE);
				}
			}
		} catch (Exception e) {
			log.error("Could not retrieve user information", e);
			throw new AuthenticationServiceException("Could not retrieve user information", e);
		}

		List<String> layers = null;
		try {
			Authentication auth = super.authenticate(authentication);
			if (auth != null) {
				boolean isAdmin = false;
				for (GrantedAuthority ga : auth.getAuthorities()) {
					if (ga.getAuthority().equals("ROLE_admin")) {
						isAdmin = true;
						break;
					}
				}
				if (!isAdmin) {
					boolean isSystemOnline;
					try {
						isSystemOnline = configurationManager.isSystemOnline();
					} catch (Exception e) {
						log.error("Could not determine system status", e);
						throw new AuthenticationServiceException("Could not determine system status");
					}
					if (!isSystemOnline)
						throw new AuthenticationServiceException(
								"The system is offline for maintenance. Please try again later.");
				}
				try {
					projectManager.updateWorkflows(principal);
				} catch (Exception e) {
					log.error("Could not update user workflows", e);
					e.printStackTrace();
					throw new AuthenticationServiceException("Could not update user workflows");
				}
				try {
					//notificationManager.register(principal.getNotificationId(), EventType.SystemShutDown);//TODO
					//notificationManager.register(principal.getNotificationId(), EventType.WorkflowTaskReminder);//TODO
				} catch (Exception e) {
					log.error("Could not register notification events", e);
					throw new AuthenticationServiceException("Could not register notification events");
				}
				try {
					//layers = userManager.getAccessibleLayers(principal);//TODO
				} catch (Exception e) {
					log.error("Could not retrieve layer information", e);
					throw new AuthenticationServiceException("Could not retrieve layer information");
				}
				try {
					auditingManager.auditLogin(principal);
				} catch (Exception e) {
					log.error("Could not audit successful login for user " + auth.getName());
				}
			}
	      

			/***********************/
			//TODO Temporary -- remove
			
			List<TaxonomyConfig> layerTaxonomies = null;
			try {
				layerTaxonomies = configurationManager.retrieveTaxonomyConfig(TaxonomyConfig.Type.LAYERTAXONOMY, false);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			List<GeocodeSystem> taxonomies = new ArrayList<GeocodeSystem>();
			for(TaxonomyConfig lt : layerTaxonomies){
				taxonomies.add(taxonomyManager.findGeocodeSystemById(lt.getId(), false));
			}
			
			//Taxonomy layert = taxonomyManager.findTaxonomyById(layerTaxonomy.getId(), false);
			
			List<Geocode> layerGranted = new ArrayList<Geocode>();
			for(GeocodeSystem t : taxonomies) {
				layerGranted.addAll(geocodeDao.findByGeocodeSystem(t));
			}
			layers = layerGranted.stream().map(x -> x.getName()).collect(Collectors.toList());
		
			/**************************/
			List<Layer> allLayers = new ArrayList<Layer>();
			
			try {
				allLayers = layerManager.getAllLayers();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			List<UUID> allLayersIDs = new ArrayList<UUID>();
			allLayersIDs = allLayers.stream().map(layer -> layer.getId()).collect(Collectors.toList());
			
			/**************************/
			
	        return new GeoanalyticsAuthenticationToken(auth.getPrincipal(), auth.getCredentials(), 
	        		auth.getAuthorities(), layers, allLayersIDs);
	      } catch (BadCredentialsException e) 
	      {
	    	try
	      	{
	    		UserLastUnsuccessfulLoginInfo ull = administrationManager.getLastUnsuccessfulLoginForUser(principal);
		    	int times = 0;
		    	if(new Date().getTime() - (ull != null ? ull.getTimestamp() : 0) < TimeUnit.MILLISECONDS.convert(accountLockCheckPeriod, accountLockCheckPeriodUnit))
		    	{
		    		times = (ull != null ? ull.getTimes() : 0) + 1;
		    		if(times > maxAcceptableLoginRate)
		    			principalManager.setActivityStatus(principal, ActiveStatus.LOCKED);
		    	}
		    	HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes())
		                   .getRequest(); 
		        auditingManager.auditUnsuccessfulLogin(principal, times, request.getRemoteAddr());
	      	}catch(Exception ee)
	      	{
	      		log.error("Could not audit login failure for user " + authentication.getName());
	      		//throw new AuthenticationServiceException("Could not audit login failure for user " + authentication.getName(), e);
	      	}
	        throw e;
	      }
	      catch(UsernameNotFoundException ue)
	      {
	    	  //TODO audit dos and block ips
	    	  
	    	  //hide UserNameNotFoundException and throw BadCredentialsException. this immitates the default behavior of AbstractUserAuthenticationProvider
	    	  //which is considered more secure, since it will prevent attackers from knowing what the real underlying problem was, thereby preventing a facilitation
	    	  //of their attack. If the username form is left blank however, there is no meaning in hiding UsernameNotFoundException so, only in this case, the exception is simply rethrown
	    	  if(authentication.getName().isEmpty()) throw ue;
	    	  throw new BadCredentialsException(messages.getMessage(
                      "AbstractUserDetailsAuthenticationProvider.badCredentials"));
	      }
	   }
}
