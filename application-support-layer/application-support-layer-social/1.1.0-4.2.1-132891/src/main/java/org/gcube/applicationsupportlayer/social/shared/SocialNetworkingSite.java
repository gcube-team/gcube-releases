package org.gcube.applicationsupportlayer.social.shared;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import org.gcube.common.portal.PortalContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.util.PortalUtil;

@SuppressWarnings("serial")
public class SocialNetworkingSite implements Serializable{
	private static final Logger _log = LoggerFactory.getLogger(SocialNetworkingSite.class);
	private String siteName;
	private String senderEmail;
	private String siteURL;
	private String siteLandingPagePath;
	/**
	 * 
	 * @param request make sure that is not expired when you call it
	 */
	public SocialNetworkingSite(HttpServletRequest request) {
		super();
		PortalContext context = PortalContext.getConfiguration();
		siteLandingPagePath = context.getSiteLandingPagePath(request);
		siteName = context.getGatewayName(request);
		senderEmail = context.getSenderEmail(request);
		siteURL = context.getGatewayURL(request);
	}
	/**
	 * 
	 * @param serverName e.g. myportal.mydomain.org
	 */
	public SocialNetworkingSite(final String serverName) {
		super();
		PortalContext context = PortalContext.getConfiguration();
		siteLandingPagePath = context.getSiteLandingPagePath(serverName);
		siteName = context.getGatewayName(serverName);
		senderEmail = context.getSenderEmail(serverName);
		boolean https = false;
		if (StringUtil.equalsIgnoreCase(
				Http.HTTPS, PortalUtil.getPortalProperties().get(PropsKeys.WEB_SERVER_PROTOCOL).toString())) {
			https = true;
		}
		int port = (Integer) PortalUtil.getPortalProperties().get(PropsKeys.WEB_SERVER_HTTPS_PORT);
		siteURL = context.getGatewayURL(serverName, port, https);
		_log.debug("SocialNetworkingSite by serverName instantiated = " + this.toString());
	}
	
	public SocialNetworkingSite(String siteName, String senderEmail,
			String siteURL, String siteLandingPagePath) {
		super();
		this.siteName = siteName;
		this.senderEmail = senderEmail;
		this.siteURL = siteURL;
		this.siteLandingPagePath = siteLandingPagePath;
	}

	public String getName() {
		return siteName;
	}

	public void setName(String siteName) {
		this.siteName = siteName;
	}

	public String getSenderEmail() {
		return senderEmail;
	}

	public void setSenderEmail(String senderEmail) {
		this.senderEmail = senderEmail;
	}

	public String getSiteURL() {
		return siteURL;
	}

	public void setSiteURL(String siteURL) {
		this.siteURL = siteURL;
	}

	public String getSiteLandingPagePath() {
		return siteLandingPagePath;
	}

	public void setSiteLandingPagePath(String siteLandingPagePath) {
		this.siteLandingPagePath = siteLandingPagePath;
	}
	@Override
	public String toString() {
		return "SocialNetworkingSite [siteName=" + siteName + ", senderEmail="
				+ senderEmail + ", siteURL=" + siteURL
				+ ", siteLandingPagePath=" + siteLandingPagePath + "]";
	}
	
	
}
