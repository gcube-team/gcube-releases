/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.server.util;

import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.gcube.common.portal.PortalContext;
import org.gcube.portlets.user.performfishanalytics.server.database.EntityManagerFactoryCreator;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.service.UserLocalServiceUtil;



/**
 * The Class WsUtil.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jan 11, 2019
 */
public class ContextUtil {

	protected static Logger log = LoggerFactory.getLogger(ContextUtil.class);

	public static final String PERFORM_SERVICE = "perform-service";
	protected static final String PERFORM_FISH_SERVICE_ATTRIBUTE = "PERFORM_FISH_SERVICE";
	protected static final String PERFORM_FISH_RESPONSE_ATTRIBUTE = "PERFORM_FISH_RESPONSE";
	protected static final String DATAMINER_SERVICE_ATTRIBUTE = "DATAMINER_SERVICE";
	public static String SERVICE_ENDPOINT_CATEGORY = "DataAnalysis";
	public static String SERVICE_ENDPOINT_NAME = "DataMiner";


	/**
	 * Checks if is within portal.
	 *
	 * @return true if you're running into the portal, false if in development
	 */
	public static boolean isWithinPortal() {
		try {
			UserLocalServiceUtil.getService();
			return true;
		}
		catch (Exception ex) {
			log.trace("Development Mode ON");
			return false;
		}
	}


	/**
	 * Gets the portal context.
	 *
	 * @param httpServletRequest the http servlet request
	 * @return the portal context
	 */
	public static PortalContextInfo getPortalContext(HttpServletRequest httpServletRequest){
		PortalContext pContext = PortalContext.getConfiguration();
		//USER
		GCubeUser user = pContext.getCurrentUser(httpServletRequest);
		String username = user.getUsername();
		String fullName = user.getFullname();
		String email = user.getEmail();
		String avatarID = user.getUserAvatarId();
		String avatarURL = user.getUserAvatarURL();
		//SESSION
		String currentScope = pContext.getCurrentScope(httpServletRequest);
		String userToken = pContext.getCurrentUserToken(httpServletRequest);
		long currGroupId = pContext.getCurrentGroupId(httpServletRequest);

		return new PortalContextInfo(username, fullName, email, avatarID, avatarURL, currentScope, userToken, currGroupId);
	}


	/**
	 * Gets the portal context.
	 *
	 * @param httpServletRequest the http servlet request
	 * @param overrideScope the override scope
	 * @return the portal context
	 */
	public static PortalContextInfo getPortalContext(HttpServletRequest httpServletRequest, String overrideScope){
		PortalContextInfo info = getPortalContext(httpServletRequest);
		info.setCurrentScope(overrideScope);
		return info;
	}


	/**
	 * Checks if is session expired.
	 *
	 * @param httpServletRequest the http servlet request
	 * @return true, if is session expired
	 * @throws Exception the exception
	 */
	public static boolean isSessionExpired(HttpServletRequest httpServletRequest) throws Exception {
		log.trace("workspace session validating...");
		return PortalContext.getConfiguration().getCurrentUser(httpServletRequest)==null;
	}


	/**
	 * Gets the entity factory.
	 *
	 * @param request the request
	 * @return the entity factory
	 * @throws Exception the exception
	 */
	private EntityManagerFactory getEntityFactory(HttpServletRequest request) throws Exception{
		PortalContextInfo pContext = getPortalContext(request);
		EntityManagerFactoryCreator.instanceLocalMode();
		return EntityManagerFactoryCreator.getEntityManagerFactory();
	}

	/**
	 * Gets the perform fish service.
	 *
	 * @param httpServletRequest the http servlet request
	 * @return the perform fish service
	 * @throws Exception the exception
	 */
	public static ServiceParameters getPerformFishService(HttpServletRequest httpServletRequest) throws Exception
	{
		PortalContextInfo pContext = getPortalContext(httpServletRequest);
		log.trace("PortalContextInfo: "+pContext);

		HttpSession session = httpServletRequest.getSession();

		ServiceParameters performFishService = (ServiceParameters) session.getAttribute(PERFORM_FISH_SERVICE_ATTRIBUTE);
		if (performFishService == null){
			log.info("Initializing the "+PERFORM_FISH_SERVICE_ATTRIBUTE+"...");
			GcoreEndpointReader gcoreEndPointReader = new GcoreEndpointReader(pContext.getCurrentScope(), PERFORM_SERVICE, "Application", "org.gcube.application.perform.service.PerformService");
			performFishService = new ServiceParameters(gcoreEndPointReader.getEndpointValue(), null, null, null);
			log.debug("Instancied peform-fish service: "+performFishService);
			session.setAttribute(PERFORM_FISH_SERVICE_ATTRIBUTE, performFishService);
		}

		return performFishService;
	}


	/**
	 * Save perform fish response.
	 *
	 * @param httpServletRequest the http servlet request
	 * @return the service parameters
	 * @throws Exception the exception
	 */
	public static ServiceParameters savePerformFishResponse(HttpServletRequest httpServletRequest) throws Exception
	{
		PortalContextInfo pContext = getPortalContext(httpServletRequest);
		log.trace("PortalContextInfo: "+pContext);

		HttpSession session = httpServletRequest.getSession();

		ServiceParameters performFishService = (ServiceParameters) session.getAttribute(PERFORM_FISH_RESPONSE_ATTRIBUTE);
		if (performFishService == null){
			log.info("Initializing the "+PERFORM_FISH_SERVICE_ATTRIBUTE+"...");
			GcoreEndpointReader gcoreEndPointReader = new GcoreEndpointReader(pContext.getCurrentScope(), PERFORM_SERVICE, "Application", "org.gcube.application.perform.service.PerformService");
			performFishService = new ServiceParameters(gcoreEndPointReader.getEndpointValue(), null, null, null);
			log.debug("Instancied peform-fish service: "+performFishService);
			session.setAttribute(PERFORM_FISH_SERVICE_ATTRIBUTE, performFishService);
		}

		return performFishService;
	}


	/**
	 * Gets the data miner service.
	 *
	 * @param httpServletRequest the http servlet request
	 * @return the data miner service
	 * @throws Exception the exception
	 */
	public static ServiceParameters getDataMinerService(HttpServletRequest httpServletRequest) throws Exception {

		PortalContextInfo pContext = getPortalContext(httpServletRequest);
		log.trace("PortalContextInfo: "+pContext);

		HttpSession session = httpServletRequest.getSession();

		ServiceParameters dataminerService = (ServiceParameters) session.getAttribute(DATAMINER_SERVICE_ATTRIBUTE);
		if (dataminerService == null){
			log.info("Initializing the "+DATAMINER_SERVICE_ATTRIBUTE+"...");
			ServiceEndpointReader reader = new ServiceEndpointReader(pContext.getCurrentScope(), SERVICE_ENDPOINT_NAME, SERVICE_ENDPOINT_CATEGORY);
			dataminerService = reader.readResource(false);
			log.debug("Instancied dataminer-service: "+dataminerService);
			session.setAttribute(DATAMINER_SERVICE_ATTRIBUTE, dataminerService);
		}

		return dataminerService;

	}



}
