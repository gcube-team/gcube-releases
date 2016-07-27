package org.gcube.applicationsupportlayer.social.shared;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import org.gcube.common.portal.PortalContext;

@SuppressWarnings("serial")
public class SocialNetworkingSite implements Serializable{

	private String siteName;
	private String senderEmail;
	private String siteURL;
	private String siteLandingPagePath;

	public SocialNetworkingSite(HttpServletRequest request) {
		super();
		PortalContext context = PortalContext.getConfiguration();
		siteLandingPagePath = context.getSiteLandingPagePath(request);
		siteName = context.getGatewayName(request);
		senderEmail = context.getSenderEmail(request);
		siteURL = context.getGatewayURL(request);
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
	
	
}
