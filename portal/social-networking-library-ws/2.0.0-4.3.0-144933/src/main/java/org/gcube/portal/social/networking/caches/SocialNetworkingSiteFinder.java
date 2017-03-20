package org.gcube.portal.social.networking.caches;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.gcube.applicationsupportlayer.social.shared.SocialNetworkingSite;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portal.social.networking.liferay.ws.GroupManagerWSBuilder;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.smartgears.ContextProvider;
import org.gcube.smartgears.context.application.ApplicationContext;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;
import org.gcube.vomanagement.usermanagement.model.VirtualGroup;
import org.jboss.netty.util.internal.ConcurrentHashMap;
import org.slf4j.LoggerFactory;

/**
 * When a notification needs to be sent, this class offers utility to discover (starting from the scope)
 * the site information needed to build up the SocialNetworkingSite object (which, for instance, contains the 
 * portal email).
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class SocialNetworkingSiteFinder {

	// Logger
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SocialNetworkingSiteFinder.class);

	// TTL of a cache entry
	private static final long TTL = 1000 * 60 * 60 * 8; // eight hours

	private static final String EMAIL_SENDER_SITE_CUSTOM_FIELD = "Emailsender";
	private static final String CATEGORY = "Portal";

	// these properties could be overwritten by the ones read from config.properties
	private static String PROD_FALLBACK_GATEWAY = "D4Science.org Gateway";
	private static String DEV_FALLBACK_GATEWAY = "gCube Dev4 Snapshot Gateway";
	private static String PREPROD_FALLBACK_GATEWAY = "gCube Preprod (dev) Gateway";

	/**
	 * Cache object
	 */
	private static Map<String, CacheBean> cache;

	/**
	 * Singleton object
	 */
	private static SocialNetworkingSiteFinder singleton = new SocialNetworkingSiteFinder();

	/**
	 * Build the singleton instance
	 */
	private SocialNetworkingSiteFinder(){

		// build cache
		cache = new ConcurrentHashMap<String, CacheBean>();

		// read fallback properties
		try{
			logger.info("Trying to read config.properties");
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			InputStream input = classLoader.getResourceAsStream("config.properties");
			Properties properties = new Properties();
			properties.load(input);
			PROD_FALLBACK_GATEWAY = properties.getProperty("PROD_FALLBACK_GATEWAY");
			DEV_FALLBACK_GATEWAY = properties.getProperty("DEV_FALLBACK_GATEWAY");
			PREPROD_FALLBACK_GATEWAY = properties.getProperty("PREPROD_FALLBACK_GATEWAY");
		}catch(Exception e){
			logger.warn("Failed to read config.properties...", e);
		}

	}

	/**
	 * Retrieve the singleton instance
	 */
	public static SocialNetworkingSiteFinder getInstance(){

		return singleton;

	}

	/**
	 * Retrieve the SocialNetworkingSite given the scope
	 * @param scope
	 * @return 
	 */
	public static SocialNetworkingSite getSocialNetworkingSiteFromScope(String scope){

		if(scope == null || scope.isEmpty())
			throw new IllegalArgumentException("Scope cannot be null/empty");

		if(cache.containsKey(scope) && !isExpired(cache.get(scope)))
			return (SocialNetworkingSite) cache.get(scope).getObject();
		else{
			SocialNetworkingSite site = discoverSite(scope);
			if(site != null)
				cache.put(scope, new CacheBean(System.currentTimeMillis(), site));
			return site;
		}

	}

	/**
	 * Check if a cache entry is expired.
	 * @param entry
	 * @return
	 */
	private static boolean isExpired(CacheBean entry){

		return System.currentTimeMillis() > entry.getTimestamp() + TTL;

	}

	/**
	 * Discover the site for this scope
	 * @param scope
	 * @return
	 */
	private static SocialNetworkingSite discoverSite(String scope) {

		try{

			logger.info("Requested site for scope " + scope);
			GroupManager groupManager = GroupManagerWSBuilder.getInstance().getGroupManager();
			long groupId = groupManager.getGroupIdFromInfrastructureScope(scope);

			GCubeGroup matchingGateway = null;

			if(groupManager.isVRE(groupId)){

				// get the Virtual groups for the groupid related to the scope

				List<VirtualGroup> virtualGroupsOfGroup = groupManager.getVirtualGroups(groupId);

				if(virtualGroupsOfGroup == null || virtualGroupsOfGroup.isEmpty())
					throw new Exception("It seems that the VRE is not linked to any VirtualGroups");

				// get the gateways
				List<GCubeGroup> gateways = groupManager.getGateways();

				if(gateways == null || gateways.isEmpty())
					throw new Exception("It seems there is no gateway here!");

				logger.info("Retrieved Gateways are " + gateways);

				// now, retrieve the virtual groups for each gateway and stop when a VG matches with one of the group
				// then, it is the gateway of interest
				ext_loop: for (GCubeGroup gateway : gateways) {
					List<VirtualGroup> gatewayVirtualGroups = groupManager.getVirtualGroups(gateway.getGroupId());
					if(gatewayVirtualGroups != null && !gatewayVirtualGroups.isEmpty()){
						for (VirtualGroup gatewayVirtualGroup : gatewayVirtualGroups) {
							if(virtualGroupsOfGroup.contains(gatewayVirtualGroup)){
								logger.info("Matching gateway for scope " + scope + " is " + gateway);
								matchingGateway = gateway;
								break ext_loop;
							}
						}
					}
				}

			}else{

				List<GCubeGroup> gateways = groupManager.getGateways();

				// vo and root vo cases are treated separately: in production environment services.d4science.org is used, instead
				// in dev next.d4science.org is used TODO better way...
				ApplicationContext ctx = ContextProvider.get(); // get this info from SmartGears
				String rootContext = "/"+ctx.container().configuration().infrastructure();
				String matchingGatewayName = null;
				if(isDevOrPreprod(rootContext)){
					matchingGatewayName = DEV_FALLBACK_GATEWAY;
				}else{
					matchingGatewayName = PROD_FALLBACK_GATEWAY;
				}

				// find the matching one among the gateways
				for (GCubeGroup gateway : gateways) {
					if(gateway.getGroupName().equals(matchingGatewayName)){
						matchingGateway = gateway;
						break;
					}
				}

				if(matchingGateway == null && isDevOrPreprod(rootContext)){

					logger.warn("Checking if it is the preprod environment");
					matchingGatewayName = PREPROD_FALLBACK_GATEWAY;
					// find the matching one among the gateways
					for (GCubeGroup gateway : gateways) {
						if(gateway.getGroupName().equals(matchingGatewayName)){
							matchingGateway = gateway;
							break;
						}
					}

				}

			}

			if(matchingGateway == null){
				logger.warn("There is no gateway for such scope. Returning null");
				return null;
			}else{
				String siteName = matchingGateway.getGroupName();
				String emailSender = (String)groupManager.readCustomAttr(matchingGateway.getGroupId(), EMAIL_SENDER_SITE_CUSTOM_FIELD);
				String siteLandingPagePath = matchingGateway.getFriendlyURL();
				String siteUrl = discoverHostOfServiceEndpoint(siteName);
				SocialNetworkingSite site = new SocialNetworkingSite(siteName, emailSender, siteUrl, siteLandingPagePath);
				logger.info("Site is " + site);
				return site;
			}

		}catch(Exception e){
			logger.error("Failed to determine the SocialNetworkingSite for scope " + scope, e);
		}

		return null;
	}

	private static boolean isDevOrPreprod(String rootContext) {
		return rootContext.equals("/gcube");
	}

	/**
	 * Retrieve endpoint host from IS for this gateway
	 * @return the host for the gateway
	 * @throws Exception
	 */
	private static String discoverHostOfServiceEndpoint(String gatewayName){

		String currentScope = ScopeProvider.instance.get();
		ApplicationContext ctx = ContextProvider.get(); // get this info from SmartGears
		ScopeProvider.instance.set("/"+ctx.container().configuration().infrastructure());
		String host = null;
		try{

			SimpleQuery query = queryFor(ServiceEndpoint.class);
			query.addCondition("$resource/Profile/Name/text() eq '"+ gatewayName +"'");
			query.addCondition("$resource/Profile/Category/text() eq '"+ CATEGORY +"'");
			DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
			List<ServiceEndpoint> toReturn = client.submit(query);
			for (ServiceEndpoint serviceEndpoint : toReturn) {
				host = "https://" + serviceEndpoint.profile().runtime().hostedOn();
				logger.info("Gateway host is " + host);
				break;
			}

		}catch(Exception e){
			logger.error("Error while retrieving host for the gateway " + gatewayName);
		}finally{
			ScopeProvider.instance.set(currentScope);
		}

		return host;

	}

}
