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
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
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

	public void init() {
		viewTemplate = getInitParameter("view-template");
		staticEndpoint = getInitParameter("back-end-url");

		geoanalyticsProfile = new ServiceProfile();
		geoanalyticsProfile.setServiceClass("geoanalytics");
		geoanalyticsProfile.setServiceName("geoanalytics-main-service");
		geoanalyticsProfile.setPathEndsWith("/");

		endpointManager = new EndpointManager();

		Authenticator.setDefault(new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(user, pass.toCharArray());
			}
		});
	}

	public void doView(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException {
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

		Integer status = null;

		try {
			List<String> endpoints = endpointManager.getServiceEndpoints(scope, geoanalyticsProfile);

			for (String endpoint : endpoints) {
				try {
					status = doRequest(endpoint, resourceRequest, resourceResponse);
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

	public Integer doRequest(String endpoint, ResourceRequest resourceRequest, ResourceResponse resourceResponse) throws IOException {
		InputStream is = resourceRequest.getPortletInputStream();
		byte[] postData = ByteStreams.toByteArray(is);
		is.close();

		String resourceURL = endpoint + resourceRequest.getResourceID();

		URL url = new URL(resourceURL);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

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
			ex.printStackTrace();
		}

		String scope = pContext.getCurrentScope(httpServletRequest);
		String token = pContext.getCurrentUserToken(scope, username);

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
				resourceResponse.getWriter().write(response);
			} else {
				resourceResponse.setCharacterEncoding(connection.getContentEncoding());
				resourceResponse.setContentLength(connection.getContentLength());
				if (connection.getContentType() != null) {
					resourceResponse.setContentType(connection.getContentType());
				}
				if (connection.getHeaderField("Content-Disposition") != null) {
					resourceResponse.setProperty("Content-Disposition", connection.getHeaderField("Content-Disposition"));
				}

				ByteStreams.copy(connection.getInputStream(), resourceResponse.getPortletOutputStream());
			}
		} catch (Exception e) {
			logger.error(null, e);
		}

		return status;
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

//		StringBuilder stringBuilder = new StringBuilder();
//		String[] splittedString = user.getFullname().trim().split("\\s+");
//		
//		for(int i=0;i<splittedString.length;i++){
//			if(splittedString[i].length() > 0)
//				stringBuilder.append(splittedString[i].charAt(0));
//		}
//		
//		return stringBuilder.toString();
		
		return  	user.getFirstName().substring(0, 1) 
				+ (	user.getMiddleName() != null && user.getMiddleName().length() > 0 ? user.getMiddleName().substring(0, 1) : "") 
				+ 	user.getLastName().substring(0, 1);
	}
}