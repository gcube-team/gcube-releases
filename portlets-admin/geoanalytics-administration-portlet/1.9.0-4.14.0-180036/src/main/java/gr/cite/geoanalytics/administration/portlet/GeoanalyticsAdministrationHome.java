package gr.cite.geoanalytics.administration.portlet;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletRequest;

import org.gcube.common.portal.PortalContext;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;

import gr.cite.bluebridge.endpoint.EndpointManager;
import gr.cite.bluebridge.endpoint.ServiceProfile;
import gr.cite.bluebridge.endpoint.exceptions.ServiceDiscoveryException;

public class GeoanalyticsAdministrationHome extends GenericPortlet {
	protected String staticEndpoint;
	protected String viewTemplate;
	protected String username;
	protected String user;
	protected String pass;

	private ServiceProfile geoanalyticsProfile;
	private EndpointManager endpointManager;

	private static Log logger = LogFactoryUtil.getLog(GeoanalyticsAdministrationHome.class);
	private static final int HTTP_CONNECTION_TIMEOUT = 15000;
	
	@Override
	public void init() {
		viewTemplate = getInitParameter("view-template");
		staticEndpoint =  getInitParameter("back-end-url");

		geoanalyticsProfile = new ServiceProfile();
		geoanalyticsProfile.setServiceClass("geoanalytics");
		geoanalyticsProfile.setServiceName("geoanalytics-main-service");
		geoanalyticsProfile.setPathEndsWith("/");

		endpointManager = new EndpointManager();

		Authenticator.setDefault(new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(user, pass.toCharArray());
			}
		});
	}

	@Override
	public void doView(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException {
		PortalContext.setUserInSession(renderRequest); //needed only if you have custom servlet that needs to know the current user in your war
		include(viewTemplate, renderRequest, renderResponse);
	}

	@Override
	public void processAction(ActionRequest actionRequest, ActionResponse actionResponse) throws PortletException, IOException {
		super.processAction(actionRequest, actionResponse);
	}

	@Override
	public void serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse) throws PortletException, IOException {
		PortalContext pContext = PortalContext.getConfiguration();
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(resourceRequest);
		String scope = pContext.getCurrentScope(httpServletRequest);
		
		if(liferayRequests(resourceRequest, resourceResponse)){
			
			Integer status = null;
			
			try {
				List<String> endpoints = endpointManager.getServiceEndpoints(scope, geoanalyticsProfile);

				for (String endpoint : endpoints) {
					logger.info("Discovered endpoint: " + endpoint);
					
					try {
						status = doRequest(endpoint, resourceRequest, resourceResponse);
						logger.info("status:" + status);
					} catch (Exception e) {
						endpointManager.removeServiceEndpoint(scope, geoanalyticsProfile, endpoint);
						logger.warn("Cannot reach endpoint : " + status, e);
					}

					if (status != null && status == 200) {
						break;
					}
				}
			} catch (ServiceDiscoveryException e) {
				logger.error(e.getMessage());
				try {
					status = doRequest(staticEndpoint, resourceRequest, resourceResponse);
				} catch (SocketTimeoutException ex) {
					resourceResponse.getWriter().write("Service is currently unavailable");
					resourceResponse.setProperty(ResourceResponse.HTTP_STATUS_CODE, "500");
				}
			}
		}
	}


	public Integer doRequest(String endpoint, ResourceRequest resourceRequest, ResourceResponse resourceResponse) throws IOException {
		PortalContext pContext = PortalContext.getConfiguration();
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(resourceRequest);

		GCubeUser user = pContext.getCurrentUser(httpServletRequest);
		String username = user.getUsername();
		String email = user.getEmail();
		String initials = getInitials(user);

		long id = user.getUserId();

		String uuid = null;
		try {
			uuid = UserLocalServiceUtil.getUserById(id).getUserUuid();
		} catch (SystemException | PortalException ex) {
			logger.info("Not logged in");
		}

		String scope = pContext.getCurrentScope(httpServletRequest);
		String token = pContext.getCurrentUserToken(scope, username);		
		String resourceUrl = buildResourceUrl(endpoint, resourceRequest);
		URL url = new URL(resourceUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();		
		connection.setRequestProperty("tenant", scope);
		connection.setRequestProperty("gcube-token", token);
		connection.setRequestProperty("username", username);
		connection.setRequestProperty("email", email);
		connection.setRequestProperty("initials", initials);
		connection.setRequestProperty("useruuid", uuid);
		connection.setRequestProperty("Content-Type", resourceRequest.getContentType());
		connection.setRequestProperty("charset", resourceRequest.getCharacterEncoding());
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setInstanceFollowRedirects(false);
		connection.setRequestMethod(resourceRequest.getMethod());
		connection.setUseCaches(false);
		connection.setConnectTimeout(HTTP_CONNECTION_TIMEOUT);
		
		InputStream is = resourceRequest.getPortletInputStream();
		byte[] postData = ByteStreams.toByteArray(is);
		is.close();
		
		if (postData.length > 0) {
			connection.setRequestProperty("Content-Length", Integer.toString(postData.length));
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.write(postData);
		} 

		Integer status = null;

		try {
			connection.connect();
			
			
			status = connection.getResponseCode();
			resourceResponse.setProperty(ResourceResponse.HTTP_STATUS_CODE, Integer.toString(status));

			if (connection.getResponseCode() != 200) {
				String response = CharStreams.toString(new InputStreamReader(connection.getErrorStream(), Charsets.UTF_8));
				logger.debug("Service response:" + response);
				logger.info("Service response:" + response);
				
				
				resourceResponse.getWriter().write(response);
			} else {
				logger.debug("Geoanalytics return with status 200: " + connection.getContentLength() + " bytes");
				logger.info("Geoanalytics return with status 200: " + connection.getContentLength() + " bytes");
				
				resourceResponse.setCharacterEncoding(connection.getContentEncoding());
				resourceResponse.setContentLength(connection.getContentLength());

				if (connection.getContentType() != null) {
					resourceResponse.setContentType(connection.getContentType());
				}
				if (connection.getHeaderField("Content-Disposition") != null) {
					resourceResponse.setProperty("Content-Disposition", connection.getHeaderField("Content-Disposition"));
				}
				if (connection.getHeaderField("filename") != null) {
					resourceResponse.setProperty("filename", connection.getHeaderField("filename"));
				}
				
				ByteStreams.copy(connection.getInputStream(), resourceResponse.getPortletOutputStream());
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return status;
	}
	
	protected String buildResourceUrl(String endpoint, ResourceRequest resourceRequest) {
		StringBuilder resourceUrl = new StringBuilder(endpoint + resourceRequest.getResourceID());
		
		if(resourceRequest.getMethod().toUpperCase().equals("GET")){		
			addQueryParameters(resourceUrl, resourceRequest);
		}

		return resourceUrl.toString();
	}
	
	protected void addQueryParameters(StringBuilder resourceUrl, ResourceRequest resourceRequest){
		if(!resourceUrl.toString().contains("?")){
			resourceUrl.append("?");
		}		
	
		resourceRequest.getParameterMap().entrySet().stream().forEach(entry -> {
			resourceUrl.append("&" + entry.getKey() + "=" + entry.getValue()[0]);	
		});		
	}
	
	@Override
	protected void doDispatch(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		super.doDispatch(request, response);
	}

	protected void include(String path, RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException {
		String url = null;
		if (renderRequest.getParameter("jspPage") == null || renderRequest.getParameter("jspPage").equals("./")) {
			url = path;
		} else {
			url = path + renderRequest.getParameter("jspPage");
		}

		PortletRequestDispatcher portletRequestDispatcher = getPortletContext().getRequestDispatcher(url);

		if (portletRequestDispatcher == null) {
			logger.error(url + " is not a valid include");
		} else {
			portletRequestDispatcher.include(renderRequest, renderResponse);
		}
	}

	private String getInitials(GCubeUser user) {		
		return  	user.getFirstName().substring(0, 1) 
				+ (	user.getMiddleName() != null && user.getMiddleName().length() > 0 ? user.getMiddleName().substring(0, 1) : "") 
				+ 	user.getLastName().substring(0, 1);
	}
	
	public boolean liferayRequests(ResourceRequest resourceRequest, ResourceResponse resourceResponse) throws IOException {
		boolean getLocale = ParamUtil.getBoolean(resourceRequest, "getLocale");
		boolean getPluginsTabVisibility = ParamUtil.getBoolean(resourceRequest, "getPluginsTabVisibility");

		JSONObject jsonObject = null;
		if (getLocale && getPluginsTabVisibility) {

			jsonObject = JSONFactoryUtil.createJSONObject();

			InputStream input = null;
			try {

				Properties props = new Properties();
				input = Thread.currentThread().getContextClassLoader().getResourceAsStream("configuration.properties");
				props.load(input);

				boolean pluginsTabVisibility = Boolean.parseBoolean( (String)props.get("publins.tab.is.visible") );

				jsonObject.put("pluginsTabVisibility", pluginsTabVisibility);
				jsonObject.put("locale", PortalUtil.getHttpServletRequest(resourceRequest).getLocale().toString());

			} catch (Exception e) {
				logger.error("Failed to read from configuration file");
				jsonObject.put("pluginsTabVisibility", true);
				jsonObject.put("locale", PortalUtil.getHttpServletRequest(resourceRequest).getLocale().toString());
			} finally {
				if(input != null)
					input.close();

				resourceResponse.getWriter().println(jsonObject);

				return false;
			}

		}

		return true;
	}
}