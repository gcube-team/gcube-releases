/**
 *
 */
package org.gcube.portlets.user.uriresolvermanager;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.user.uriresolvermanager.entity.Resolver;
import org.gcube.portlets.user.uriresolvermanager.entity.ServiceAccessPoint;
import org.gcube.portlets.user.uriresolvermanager.entity.ServiceParameter;
import org.gcube.portlets.user.uriresolvermanager.exception.IllegalArgumentException;
import org.gcube.portlets.user.uriresolvermanager.exception.UriResolverMapException;
import org.gcube.portlets.user.uriresolvermanager.readers.RuntimeResourceReader;
import org.gcube.portlets.user.uriresolvermanager.readers.UriResolverMapReader;
import org.gcube.portlets.user.uriresolvermanager.util.UrlEncoderUtil;
import org.gcube.portlets.user.urlshortener.UrlShortener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class UriResolverManager.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 6, 2016
 */
public class UriResolverManager {

	/**
	 * Time to reload Runtime Resource Configuration
	 */
	public static int RESET_DELAY = 15*60*1000; //15 MINUTES

	/**
	 * Time to reload Runtime Resource Configuration
	 */
	public static int RESET_TIME = RESET_DELAY; //15 MINUTES

	private UriResolverMapReader uriResolverMapReader;
	private Map<String, Resolver> applicationTypes;
	private String applicationType;
	private RuntimeResourceReader reader;

	/**
	 * A lock to prevent reader = null;
	 */
	private int usingReader = 0;

	private ServiceAccessPoint serviceAccessPoint;

	private Timer timer;

	/**
	 * Lock reader.
	 */
	public synchronized void lockReader() {
		usingReader++;
	}

	/**
	 * Release reader.
	 */
	public synchronized void releaseReader() {
		usingReader--;
	}

	/**
	 * Count readers.
	 *
	 * @return the int
	 */
	public synchronized int countReaders() {
		return usingReader;
	}

	public static final Logger logger = LoggerFactory.getLogger(UriResolverManager.class);


	/**
	 * Instantiates a new uri resolver manager.
	 * Precondition: set the scope into ScopeProvider {@link ScopeProvider#get()}
	 * The scope is used to look up the generic resource with name: {@link UriResolverMapReader#URI_RESOLVER_MAP_RESOURCE_NAME}, secondary type: {@link UriResolverMapReader#URIRESOLVERMAP_SECONDARY_TYPE} from IS to map ApplicationType with its Resolver
	 *
	 * @throws UriResolverMapException the uri resolver map exception
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public UriResolverManager() throws UriResolverMapException, IllegalArgumentException{
		try {

			String scope = ScopeProvider.instance.get();
			logger.info("UriResolverManager is using scope: "+scope+", read from ScopeProvider");

			if(scope == null)
				throw new UriResolverMapException("Scope is null, set scope into ScopeProvider!");

			this.uriResolverMapReader = new UriResolverMapReader();
			this.applicationTypes = uriResolverMapReader.getApplicationTypes();
			this.setTimerUriResolverReader(RESET_DELAY, RESET_TIME);
		} catch (UriResolverMapException e){
			logger.error("UriResolverMapException: ",e);
			throw e;
		} catch (Exception e) {
			logger.error("UriResolverManager: ",e);
			throw new UriResolverMapException("Map Application Type - Resources not found in IS");
		}
	}

	/**
	 * Sets the application type.
	 *
	 * @param applicationType the applicationType to set
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public void setApplicationType(String applicationType) throws IllegalArgumentException {
		if(!this.applicationTypes.containsKey(applicationType)){
			throw new IllegalArgumentException("Application type '"+applicationType +"' not found in Application Types: "+getApplicationTypes());

		}
		this.applicationType = applicationType;
	}

	/**
	 * Instance a UriResolverManager
	 * Precondition: set the scope provider {@link  ScopeProvider.instance.get()}
	 * The scope is used to look up the generic resource {@link UriResolverMapReader#URI_RESOLVER_MAP} available in the infrastructure to map ApplicationType with its Resolver
	 *
	 * @param applicationType a (valid) key Application Type {@link  UriResolverManager#getApplicationTypes()}
	 * @throws UriResolverMapException the uri resolver map exception
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public UriResolverManager(String applicationType) throws UriResolverMapException, IllegalArgumentException{
		this();
		setApplicationType(applicationType);
	}



	/**
	 * Gets the link.
	 *
	 * @param applicationType the application type
	 * @param parameters the map of the parameters sent as HTTP query string
	 * @param shortLink if true the link is shorted otherwise none
	 * @return the link
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws UriResolverMapException the uri resolver map exception
	 */
	public String getLink(String applicationType, Map<String, String> parameters, boolean shortLink) throws IllegalArgumentException, UriResolverMapException{
		this.applicationType = applicationType;
		return getLink(parameters, shortLink);
	}

	/**
	 * Gets the link.
	 *
	 * @param parameters the map of the parameters sent as HTTP query string
	 * @param shortLink if true the link is shorted otherwise none
	 * @return the link
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws UriResolverMapException the uri resolver map exception
	 */
	public String getLink(Map<String, String> parameters, boolean shortLink) throws IllegalArgumentException, UriResolverMapException{

		if(applicationType==null)
			throw new IllegalArgumentException("Application type is null");

		Resolver resolver = this.applicationTypes.get(applicationType);
		String link;

		if(parameters==null)
			throw new IllegalArgumentException("Input Map parameters is null");

		try {

			lockReader();

			if(reader==null){
				logger.info("Runtime Resource Reader is null, istancing...");
				reader = new RuntimeResourceReader(resolver.getResourceName());
			}

			if(resolver.getEntryName()==null || resolver.getEntryName().isEmpty()){
				logger.warn("The entryname to "+resolver.getResourceName() +" is null or empty, reading first Access Point!!");
				serviceAccessPoint = reader.getServiceAccessPoints().get(0);
			}else{
				logger.warn("Reading Access Point for Entry Name: "+resolver.getEntryName());
				serviceAccessPoint = reader.getServiceAccessPointForEntryName(resolver.getEntryName());
				if(serviceAccessPoint==null)
					throw new UriResolverMapException("Entry Name "+resolver.getEntryName() +" not found in Resource name: "+resolver.getResourceName());
			}

			List<ServiceParameter> resourceParameters = serviceAccessPoint.getServiceParameters();

			//CHECK PARAMETERS
			for (ServiceParameter serviceParameter : resourceParameters) {
				if(serviceParameter.isMandatory()){
					if(!parameters.containsKey(serviceParameter.getKey())){
						throw new IllegalArgumentException("Mandatory service key (parameter) '"+serviceParameter.getKey() +"' not found into input map");
					}
				}
			}

			String baseURI = serviceAccessPoint.getServiceUrl();
			releaseReader();
			String params = UrlEncoderUtil.encodeQuery(parameters);
			link = baseURI+"?"+params;
			logger.info("Created HTTP URI request (link): "+link);

			if(shortLink){
				try{
					logger.info("Shortner start..");
					UrlShortener shortener = new UrlShortener();
					link = shortener.shorten(link);
					logger.info("Shorted link is: "+link);
				}catch(Exception e){
					logger.warn("An error occurred during link shortening: ",e);
				}
			}
		} catch (IllegalArgumentException e){
			logger.error("Uri Resolver IllegalArgumentException: ", e);
			throw e;
		} catch (Exception e) {
			logger.error("Uri Resolver Exception: ", e);
			throw new UriResolverMapException("Uri Resolver error: " +e.getMessage());
		}

		return link;
	}

	/**
	 * Gets the application types.
	 *
	 * @return the Application Types available
	 */
	public Set<String> getApplicationTypes(){
		return this.applicationTypes.keySet();
	}

	/**
	 * Discovery service parameters.
	 *
	 * @param resolver the resolver
	 * @return the list
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws Exception the exception
	 */
	public List<ServiceParameter> discoveryServiceParameters(Resolver resolver) throws IllegalArgumentException, Exception{
		try {

			String scope = ScopeProvider.instance.get();
			logger.info("SiscoveryServiceParameters is using scope: "+scope+", read from ScopeProvider");

			if(scope == null)
				throw new UriResolverMapException("Scope is null, set scope into ScopeProvider!");

			if(resolver == null)
				throw new IllegalArgumentException("Resolver is null, set Resolver");

			RuntimeResourceReader reader = new RuntimeResourceReader(resolver.getResourceName());

			ServiceAccessPoint serviceAccessPoint = null;
			if(resolver.getEntryName()==null || resolver.getEntryName().isEmpty()){
				logger.warn("The entryname to "+resolver.getResourceName() +" is null or empty, reading first Access Point!!");
				serviceAccessPoint = reader.getServiceAccessPoints().get(0);
			}else{
				logger.info("Reading Access Point for entryname: "+resolver.getEntryName());
				serviceAccessPoint = reader.getServiceAccessPointForEntryName(resolver.getEntryName());
				if(serviceAccessPoint==null)
					throw new UriResolverMapException("Entry Name "+resolver.getEntryName() +" not found in Resource name: "+resolver.getResourceName());
			}

			return serviceAccessPoint.getServiceParameters();
		} catch (Exception e) {
			logger.error("Uri Resolver error: ", e);
			throw new UriResolverMapException("Uri Resolver error: " +e.getMessage());
		}
	}

	/**
	 * Gets the resolver.
	 *
	 * @param applicationType the application type
	 * @return the resolver
	 */
	public Resolver getResolver(String applicationType){
		return this.applicationTypes.get(applicationType);
	}

	/**
	 * Gets the capabilities.
	 *
	 * @return a map Application Type - Resolver
	 */
	public Map<String, Resolver> getCapabilities(){
		return this.applicationTypes;
	}

	/**
	 * Sets the timer uri resolver reader.
	 *
	 * @param delay the delay
	 * @param period the period
	 */
	public void setTimerUriResolverReader(long delay, long period) {

		cancelTimerUriResolverReader();

		timer = new Timer(true);

		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				logger.info("Timer Reset Runtime Resource running..");
				int counters = countReaders();
				if(counters==0){
					logger.info("Reader not locked, resetting");
					reader = null;
				}else
					logger.info("Reader locked, counters is/are:"+counters+", skipping");

			}
		}, delay, period);
	}



	/**
	 * Cancel timer uri resolver reader.
	 */
	public void cancelTimerUriResolverReader(){
		if(timer!=null)
			timer.cancel();
	}


	/**
	 * Invalid uri resolver reader.
	 */
	public void invalidUriResolverReader(){
		reader = null;
	}

	/*
	public static void main(String[] args) {
		try {
			UriResolverManager manager = new UriResolverManager();
			System.out.println(manager.getCapabilities());
			System.out.println(manager.getApplicationTypes());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
}
