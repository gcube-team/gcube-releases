package org.gcube.common.portal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.model.CustomAttributeKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.VirtualHost;
import com.liferay.portal.service.CompanyLocalServiceUtil;
import com.liferay.portal.service.LayoutSetLocalServiceUtil;
import com.liferay.portal.service.VirtualHostLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;




/**
 * Clients can obtain the single instance of the {@link PortalContext} by invoking its static method {@link #getConfiguration()}. 
 * The first invocation of the method triggers the initialisation of the instance.
 * 
 * @author Massimiliano Assante (ISTI-CNR)
 *
 */
public class PortalContext {
	private static final Logger _log = LoggerFactory.getLogger(PortalContext.class);

	private static final String DEFAULT_INFRA_NAME = "gcube";
	private static final String DEFAULT_VO_NAME = "devsec";
	private static final String DEFAULT_GATEWAY_NAME = "D4science Gateway";
	private static final String DEFAULT_GATEWAY_EMAIL = "do-not-reply@d4science.org";

	private static PortalContext singleton = new PortalContext();

	private String infra;
	private String vos;

	private PortalContext() {
		initialize();
	}
	/**
	 * 
	 * @return the instance
	 */
	public synchronized static PortalContext getConfiguration() {
		return singleton == null ? new PortalContext() : singleton;
	}

	private void initialize() {
		Properties props = new Properties();
		try {
			String propertyfile = getCatalinaHome() + File.separator + "conf" + File.separator + "infrastructure.properties";			
			File propsFile = new File(propertyfile);
			FileInputStream fis = new FileInputStream(propsFile);
			props.load( fis);
			infra  = props.getProperty(GCubePortalConstants.INFRASTRUCTURE_NAME);
			vos = props.getProperty(GCubePortalConstants.SCOPES);
		}
		catch(IOException e) {
			infra = DEFAULT_INFRA_NAME;
			vos = DEFAULT_VO_NAME;
			_log.error("infrastructure.properties file not found under $CATALINA_HOME/conf/ dir, setting default infrastructure Name " + infra + " and VO Name " + vos);
		}		
		_log.info("PortalContext configurator correctly initialized on " + infra);
	}
	/**
	 * 
	 * @return the infrastructure name in which your client runs
	 */
	public String getInfrastructureName() {
		return this.infra;
	}
	/**
	 * 
	 * @return the value of the scopes as it is in the property file (a string with comma separated vales)
	 */
	public String getVOsAsString() {
		return this.vos;
	}
	/**
	 * 
	 * @return the value of the scopes
	 */
	public List<String> getVOs() {
		List<String> toReturn = new ArrayList<String>();
		if (vos == null || vos.equals(""))
			return toReturn;
		String[] split = vos.split(",");
		for (int i = 0; i < split.length; i++) {
			toReturn.add(split[i].trim());
		}
		return toReturn;
	}
	/**
	 * 
	 * @deprecated use getConfiguration().getGatewayName() method
	 * read the portal instance name from a property file and returns it
	 */
	@Deprecated 
	public static String getPortalInstanceName() {
		return getConfiguration().getGatewayName();
	}
	/**
	 * 
	 * @param httpServletRequest
	 * @return the gateway URL until the first slash, e.g. http(s)://mynode.d4science.org:8080, if the URL uses standard http(s) port like 80 or 443 the port is not returned. 
	 */
	public String getGatewayURL(HttpServletRequest httpServletRequest) {
		String serverName =  httpServletRequest.getServerName();
		String toReturn = (httpServletRequest.isSecure()) ? "https://" : "http://" ;
		//server name
		toReturn += serverName;
		//port
		if (httpServletRequest.isSecure()) 
			toReturn +=  (httpServletRequest.getServerPort() == 443) ? "" : ":"+httpServletRequest.getServerPort() ;
		else
			toReturn +=  (httpServletRequest.getServerPort() == 80) ? "" : ":"+httpServletRequest.getServerPort() ;
		return toReturn;
	}
	/**
	 * @deprecated use getGatewayURL(HttpServletRequest httpServletRequest)
	 * @return the basic gateway url
	 */
	@Deprecated
	public String getGatewayURL() {
		Long defaultCompanyId = PortalUtil.getDefaultCompanyId();
		try {
			CompanyLocalServiceUtil.getCompany(defaultCompanyId);

			return PortalUtil.getPortalURL(CompanyLocalServiceUtil.getCompany(defaultCompanyId).getVirtualHostname(), 443, true);
		} catch (PortalException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return "";
	}
	/**
	 * 
	 * @param request
	 * @return the landing page path of the current Site e.g. "/group/i-marine"
	 * @throws PortalException
	 * @throws SystemException
	 */
	public String getSiteLandingPagePath(final HttpServletRequest request) {
		String sitePath = StringPool.BLANK;
		Group site;
		try {
			site = getSiteFromServletRequest(request);
			if (site.getPrivateLayoutsPageCount() > 0) {
				sitePath = getGroupFriendlyURL(request, site);
			} else	{
				_log.debug(site.getName() + " site doesn't have any private page. Default landing page will be used");
			}
		}catch (Exception e) {
			e.printStackTrace();
		} 
		return sitePath;
	}
	/**
	 * 
	 * @param request
	 * @return the current Group instance based on the request
	 * @throws PortalException
	 * @throws SystemException
	 */
	private Group getSiteFromServletRequest(final HttpServletRequest request) throws PortalException, SystemException {
		String serverName = request.getServerName();
		_log.debug("currentHost is " +  serverName);
		Group site = null;
		List<VirtualHost> vHosts = VirtualHostLocalServiceUtil.getVirtualHosts(0, VirtualHostLocalServiceUtil.getVirtualHostsCount());
		for (VirtualHost virtualHost : vHosts) {
			_log.debug("Found  " +  virtualHost.getHostname());
			if (virtualHost.getHostname().compareTo("localhost") != 0 && 
					virtualHost.getLayoutSetId() != 0 && 
					virtualHost.getHostname().compareTo(serverName) == 0) {
				long layoutSetId = virtualHost.getLayoutSetId();
				site = LayoutSetLocalServiceUtil.getLayoutSet(layoutSetId).getGroup();
				_log.debug("Found match! Your site is " +  site.getName());
				return site;
			}
		}
		return null;
	}
	/**
	 * @param request
	 * @param currentGroup
	 * @param isPrivate
	 * @param isUser
	 * @return
	 * @throws PortalException
	 * @throws SystemException
	 */
	private static String getGroupFriendlyURL(HttpServletRequest request, final Group currentGroup) throws PortalException, SystemException {
		String friendlyURL = GCubePortalConstants.PREFIX_GROUP_URL;
		StringBundler sb = new StringBundler();
		sb.append(friendlyURL).append(currentGroup.getFriendlyURL());
		return sb.toString();
	}
	/**
	 * 
	 * @param request the HttpServletRequest instance of your servlet
	 * @return the current Site Name based on the request
	 */
	public String getGatewayName(HttpServletRequest request) {
		String toReturn = DEFAULT_GATEWAY_NAME;
		try {
			Group currSite = getSiteFromServletRequest(request);
			toReturn = (String) new LiferayGroupManager().readCustomAttr(currSite.getGroupId(), CustomAttributeKeys.GATEWAY_SITE_NAME.getKeyName());			
		} catch (Exception e) {
			toReturn = DEFAULT_GATEWAY_NAME;
			_log.error("Could not read Site Custom Attr: " + CustomAttributeKeys.GATEWAY_SITE_NAME.getKeyName() + ", returning default Gateway Name " + toReturn);
		} 
		return toReturn;
	}

	/**
	 * read the infrastructure gateway name from a property file and returns it
	 * @deprecated use getGatewayName(HttpServletRequest request)
	 */
	@Deprecated
	public String getGatewayName() {
		//get the portles to look for from the property file
		Properties props = new Properties();
		String toReturn = "";

		try {
			String propertyfile =  getCatalinaHome() + File.separator + "conf" + File.separator + "gcube-data.properties";			
			File propsFile = new File(propertyfile);
			FileInputStream fis = new FileInputStream(propsFile);
			props.load( fis);
			toReturn = props.getProperty(GCubePortalConstants.GATEWAY_NAME);
		}
		//catch exception in case properties file does not exist
		catch(IOException e) {
			toReturn = DEFAULT_GATEWAY_NAME;
			_log.error("gcube-data.properties file not found under $CATALINA_HOME/conf dir, returning default Portal Name " + toReturn);
			return toReturn;
		}
		_log.debug("Returning Gateway Name: " + toReturn );
		return toReturn;
	}
	/**
	 * 
	 * @param request the HttpServletRequest instance of your servlet
	 * @return the sender (from) email address  for the current Site based on the request
	 */
	public String getSenderEmail(HttpServletRequest request) {
		String toReturn = DEFAULT_GATEWAY_EMAIL;
		try {
			Group currSite = getSiteFromServletRequest(request);
			toReturn = (String) new LiferayGroupManager().readCustomAttr(currSite.getGroupId(), CustomAttributeKeys.GATEWAY_SITE_EMAIL_SENDER.getKeyName());			
		} catch (Exception e) {
			toReturn = DEFAULT_GATEWAY_EMAIL;
			_log.error("Could not read Site Custom Attr: " + CustomAttributeKeys.GATEWAY_SITE_EMAIL_SENDER.getKeyName() + ", returning default Gateway Email Sender " + toReturn);
		} 
		return toReturn;
	}
	/**
	 * read the sender (from) email address for notifications name from a property file and returns it
	 * @deprecated use getSenderEmail(HttpServletRequest request)
	 */
	@Deprecated
	public String getSenderEmail() {
		//get the portles to look for from the property file
		Properties props = new Properties();
		String toReturn = "";

		try {
			String propertyfile = getCatalinaHome() + File.separator + "conf" + File.separator + "gcube-data.properties";			
			File propsFile = new File(propertyfile);
			FileInputStream fis = new FileInputStream(propsFile);
			props.load( fis);
			toReturn = props.getProperty(GCubePortalConstants.SENDER_EMAIL);
		}
		//catch exception in case properties file does not exist
		catch(IOException e) {
			toReturn = DEFAULT_GATEWAY_EMAIL;
			_log.error("gcube-data.properties file not found under $CATALINA_HOME/conf dir, returning default Email" + toReturn);
			return toReturn;
		}
		_log.debug("Returning SENDER_EMAIL: " + toReturn );
		return toReturn;
	}
	/**
	 * use org.gcube.vomanagement.usermanagement.impl.LiferayUserManager#getAdmin method
	 */
	@Deprecated
	public String getAdministratorUsername() {
		//get the portles to look for from the property file
		Properties props = new Properties();
		String toReturn = "";

		try {
			String propertyfile = getCatalinaHome() + File.separator + "conf" + File.separator + "gcube-data.properties";			
			File propsFile = new File(propertyfile);
			FileInputStream fis = new FileInputStream(propsFile);
			props.load( fis);
			toReturn = props.getProperty(GCubePortalConstants.ADMIN_USERNAME);
		}
		//catch exception in case properties file does not exist
		catch(IOException e) {
			toReturn = "massimiliano.assante";
			_log.error("gcube-data.properties file not found under $CATALINA_HOME/conf dir, returning default administrator" + toReturn);
			return toReturn;
		}
		_log.debug("Returning Administrator username: " + toReturn );
		return toReturn;
	}
	/**
	 * 
	 * @return $CATALINA_HOME
	 */
	private static String getCatalinaHome() {
		return (System.getenv("CATALINA_HOME").endsWith("/") ? System.getenv("CATALINA_HOME") : System.getenv("CATALINA_HOME")+"/");
	}

}
