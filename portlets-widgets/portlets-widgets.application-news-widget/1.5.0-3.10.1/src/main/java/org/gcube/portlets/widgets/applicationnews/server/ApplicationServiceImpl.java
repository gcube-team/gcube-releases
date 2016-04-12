package org.gcube.portlets.widgets.applicationnews.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpSession;

import org.apache.commons.validator.routines.UrlValidator;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.applicationsupportlayer.social.ApplicationNewsManager;
import org.gcube.applicationsupportlayer.social.NewsManager;
import org.gcube.portal.databook.shared.ApplicationProfile;
import org.gcube.portlets.widgets.applicationnews.client.ApplicationService;
import org.gcube.portlets.widgets.applicationnews.shared.LinkPreview;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sun.net.ssl.HttpsURLConnection;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ApplicationServiceImpl extends RemoteServiceServlet implements	ApplicationService {
	private static final Logger _log = LoggerFactory.getLogger(ApplicationServiceImpl.class);


	private static final String TEST_SCOPE = "/gcube/devsec/devVRE";
	private static final String TEST_USER = "massimiliano.assante";
	private static final String USERNAME_ATTRIBUTE = "username";
	private boolean withinPortal = false;
	/**
	 * the current ASLSession
	 * @return the session
	 */
	private ASLSession getASLSession() {
		HttpSession httpSession = this.getThreadLocalRequest().getSession();
		String sessionID = httpSession.getId();
		String user = (String) httpSession.getAttribute(USERNAME_ATTRIBUTE);
		if (user == null) {
			_log.error(" STARTING IN TEST MODE - NO USER FOUND");
			ASLSession session = SessionManager.getInstance().getASLSession(sessionID, TEST_USER);
			session.setScope(TEST_SCOPE);

			return session;
		}
		else {
			_log.info("LIFERAY PORTAL DETECTED user=" + user);
			withinPortal = true;
		}
		return SessionManager.getInstance().getASLSession(sessionID, user);
	}

	
	public boolean publishAppNews(String portletClassName, final String textToShow, final String uriGETparams, final LinkPreview linkPreview) {
		NewsManager nma = new ApplicationNewsManager(getASLSession(), portletClassName);
		if (linkPreview == null) {
			if (uriGETparams.compareTo("") == 0)
				return nma.shareApplicationUpdate(textToShow);
			else
				return nma.shareApplicationUpdate(textToShow, uriGETparams);
		}
		//check if the http url of the image exists
		if (linkPreview.getLinkThumbnailUrl() == null || linkPreview.getLinkThumbnailUrl().isEmpty())
			return nma.shareApplicationUpdate(textToShow, uriGETparams, linkPreview.getTitle(), linkPreview.getDescription(), "");
		
		return nma.shareApplicationUpdate(textToShow, uriGETparams, linkPreview.getTitle(), linkPreview.getDescription(), getInputStreamFromImageUrl(linkPreview.getLinkThumbnailUrl()), linkPreview.getImageType());
	}
	/**
	 * 
	 * @param imageUrl
	 * @return
	 * @throws IOException 
	 */
 	@SuppressWarnings({ "restriction", "deprecation" })
	private InputStream getInputStreamFromImageUrl(String imageUrl) {
 		InputStream toReturn = null;
 		String[] schemes = {"http","https"};
		UrlValidator urlValidator = new UrlValidator(schemes);
		if (! urlValidator.isValid(imageUrl)) {
			_log.warn("url is NOT valid, returning nothing");
			return null;
		}
		_log.debug("url is valid");

		URL pageURL;
		URLConnection siteConnection = null;
		try {
			pageURL = new URL(imageUrl);
			if (pageURL.getProtocol().equalsIgnoreCase("https")) {
				System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
				java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider()); 
				trustAllHTTPSConnections();
				siteConnection = (HttpsURLConnection) pageURL.openConnection();
			}
			else
				siteConnection = (HttpURLConnection) pageURL.openConnection();
			
			toReturn = siteConnection.getInputStream();
		} catch (MalformedURLException e) {
			_log.error("url is not valid");
			return null;
		} catch (IOException e) {
			_log.error("url is not reachable");
			return null;
		}
		return toReturn;
 	}
	/**
	 * this method handles the non trusted https connections
	 */
	@SuppressWarnings({ "deprecation", "restriction" })
	private void trustAllHTTPSConnections() {
		// Create a trust manager that does not validate certificate chains  
		TrustManager[] trustAllCerts = new TrustManager[]{  
				new X509TrustManager() {  
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {  
						return null;  
					}  

					public void checkClientTrusted(  
							java.security.cert.X509Certificate[] certs, String authType) {  
					}  

					public void checkServerTrusted(  
							java.security.cert.X509Certificate[] certs, String authType) {  
					}  
				}  
		};  
		try {  
			SSLContext sc = SSLContext.getInstance("SSL");  
			sc.init(null, trustAllCerts, new java.security.SecureRandom());  
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());  
		} catch (Exception e) {  
			System.out.println("Error" + e);  
		}  
	}
	@Override
	public ApplicationProfile getApplicationProfile(String portletClassName) {
		ApplicationNewsManager nma = new ApplicationNewsManager(getASLSession(), portletClassName);
		return nma.getApplicationProfile();
	}
}
