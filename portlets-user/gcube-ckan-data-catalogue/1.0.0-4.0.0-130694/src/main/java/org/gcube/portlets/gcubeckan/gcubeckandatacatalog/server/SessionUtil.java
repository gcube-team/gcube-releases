/**
 *
 */
package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.server;

import javax.servlet.http.HttpSession;

import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.shared.CkanConnectorAccessPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.service.UserLocalServiceUtil;


/**
 * The Class SessionUtil.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 10, 2016
 */
public class SessionUtil {

	public static final String CKAN_END_POINT = "CKAN_END_POINT";
	public static final String CKAN_ACCESS_POINT = "CKAN_ACCESS_POINT";

	private static Logger logger = LoggerFactory.getLogger(SessionUtil.class);

	/**
	 * Gets the ckan end point.
	 *
	 * @param session the session
	 * @param scope the scope
	 * @return the ckan end point
	 * @throws Exception the exception
	 */
	public static GcoreEndpointReader getCkanEndPoint(HttpSession session, String scope) throws Exception{

		String key = getKeyForSession(CKAN_END_POINT, scope);
		logger.debug("Getting GcoreEndpointReader for key: "+key +", from HttpSession");
		GcoreEndpointReader ckanEndPoint = (GcoreEndpointReader) session.getAttribute(key);
		logger.debug("GcoreEndpointReader for key: "+key +", found in session? "+(ckanEndPoint!=null));
		if(ckanEndPoint==null){
			logger.debug("GcoreEndpointReader is null, instancing new..");
			ckanEndPoint = new GcoreEndpointReader(scope);
			session.setAttribute(key, ckanEndPoint);
		}
		logger.debug("returning: "+ckanEndPoint);
		return ckanEndPoint;
	}

	/**
	 * Checks if is into portal.
	 *
	 * @return true, if is into portal
	 */
	public static boolean isIntoPortal() {
		try {
			UserLocalServiceUtil.getService();
			return true;
		}catch (Exception ex) {
			logger.debug("Development Mode ON");
			return false;
		}
	}

	/**
	 * Save ckan access point.
	 *
	 * @param session the session
	 * @param scope the scope
	 * @param ckAP the ck ap
	 */
	public static void saveCkanAccessPoint(HttpSession session, String scope, CkanConnectorAccessPoint ckAP) {
		String key = getKeyForSession(CKAN_ACCESS_POINT, scope);
		session.setAttribute(key, ckAP);
	}

	/**
	 * Gets the key for session.
	 *
	 * @param key the key
	 * @param scope the scope
	 * @return the key for session
	 */
	private static String getKeyForSession(String key, String scope){
		return key+scope;
	}

	/**
	 * Gets the ckan access point.
	 *
	 * @param session the session
	 * @param scope the scope
	 * @return the ckan access point
	 */
	public static CkanConnectorAccessPoint getCkanAccessPoint(HttpSession session, String scope) {
		String key = getKeyForSession(CKAN_ACCESS_POINT, scope);
		return (CkanConnectorAccessPoint) session.getAttribute(key);


	}
}
