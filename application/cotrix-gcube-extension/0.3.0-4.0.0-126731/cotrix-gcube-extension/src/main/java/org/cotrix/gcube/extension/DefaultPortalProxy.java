/**
 * 
 */
package org.cotrix.gcube.extension;

import java.net.URL;

import org.cotrix.gcube.stubs.News;
import org.cotrix.gcube.stubs.PortalUser;
import org.cotrix.gcube.stubs.RequestConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.cotrix.gcube.stubs.PortalUserContext.*;

/**
 * @author "Federico De Faveri federico.defaveri@fao.org"
 *
 */
public class DefaultPortalProxy implements PortalProxy {

	private static final Logger logger = LoggerFactory.getLogger(DefaultPortalProxy.class);

	private static final String USER_SERVICE_PATH = "/cotrix-gcube-portlet/user";
	private static final String NEWS_SERVICE_PATH = "/cotrix-gcube-portlet/news";
	private static final String SUCCESSFULL_RESPONSE = "OK"; 

	protected HttpClient httpClient = new DefaultHttpClient();

	protected String portalUrl;
	protected String sessionId;

	private String cookie;

	/**
	 * @param portalUrl
	 * @param sessionId
	 */
	public DefaultPortalProxy(String portalUrl, String sessionId) {
		this.portalUrl = portalUrl;
		this.sessionId = sessionId;
		this.cookie = RequestConstants.SESSION_PARAMETER_NAME+"="+sessionId;
	}

	/** 
	 * {@inheritDoc}
	 */
	@Override
	public PortalUser getPortalUser() {
		String response = doGet(USER_SERVICE_PATH);
		return deserialize(response);
	}

	/** 
	 * {@inheritDoc}
	 */
	@Override
	public void publish(String news) {
		News newsBean = new News(news);
		String json = newsBean.encoded();
		String response = doPost(NEWS_SERVICE_PATH, json);
		checkResponse(response);
	}

	private void checkResponse(String response) {
		if (SUCCESSFULL_RESPONSE.equals(response)) throw new RuntimeException("Request failed, cause: "+response);
	}

	private String doGet(String servicePath) {
		try {
			URL url = new URL(portalUrl + servicePath); 
			return httpClient.get(url, cookie);
		} catch(Exception e) {
			logger.error("Request failed for service path "+servicePath, e);
			throw new RuntimeException("Request failed for service path "+servicePath, e);
		}
	}

	private String doPost(String servicePath, String request) {
		try {
			URL url = new URL(portalUrl + servicePath); 
			return httpClient.post(url, cookie, request);
		} catch(Exception e) {
			logger.error("Request failed for service path "+servicePath, e);
			throw new RuntimeException("Request failed for service path "+servicePath, e);
		}
	}
}
