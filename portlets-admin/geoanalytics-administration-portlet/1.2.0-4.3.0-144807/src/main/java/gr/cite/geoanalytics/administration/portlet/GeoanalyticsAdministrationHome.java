package gr.cite.geoanalytics.administration.portlet;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLEncoder;
import java.util.UUID;
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
import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;

import gr.cite.geoanalytics.administration.portlet.utils.ServiceDiscovery;
import gr.cite.geoanalytics.administration.portlet.utils.ServiceProfile;

public class GeoanalyticsAdministrationHome extends GenericPortlet {
	protected String backendURL;
	protected String viewTemplate;
	protected String username;
	protected String user;
	protected String pass;

	private static ServiceProfile geoanalyticsProfile = ServiceProfile.createGeoanalyticsProfile();
	private static Log _log = LogFactoryUtil.getLog(GeoanalyticsAdministrationHome.class);

	public void init() {
		viewTemplate = getInitParameter("view-template");
		//backendURL = getInitParameter("back-end-url");

		Authenticator.setDefault(new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(user, pass.toCharArray());
			}
		});
	}

	public void doView(RenderRequest renderRequest, RenderResponse renderResponse)	throws IOException, PortletException {
		include(viewTemplate, renderRequest, renderResponse);
	}

	@Override
	public void processAction(ActionRequest actionRequest, ActionResponse actionResponse)	throws PortletException, IOException {
		super.processAction(actionRequest, actionResponse);
	}

	@Override
	public void serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)	throws PortletException, IOException {	
		PortalContext pContext = PortalContext.getConfiguration();
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(resourceRequest);
		String scope = pContext.getCurrentScope(httpServletRequest);
		String username = pContext.getCurrentUser(httpServletRequest).getUsername();
		String token = pContext.getCurrentUserToken(scope, username);		
		
		String dynamicBackendURL = null;
		try {
			dynamicBackendURL = ServiceDiscovery.fetchServiceEndpoint(scope, geoanalyticsProfile);
		} catch (Exception e) {
			_log.error("Could not fetch the geoanalytics service from IS. Will try to use the static one if exists");
		}	
		backendURL =  dynamicBackendURL != null ? dynamicBackendURL : getInitParameter("back-end-url");	
				
		InputStream is = resourceRequest.getPortletInputStream();
		byte[] postData = ByteStreams.toByteArray(is);
		is.close();
		int postDataLength = postData.length;
		String request = backendURL + resourceRequest.getResourceID();
		URL url = new URL(request);

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		
		String cookie = "";
		String jSessionId = (String) resourceRequest.getPortletSession().getAttribute("JSESSIONID");
		if (jSessionId != null && !jSessionId.isEmpty() && !jSessionId.equals("null")) {
			cookie = "JSESSIONID=" + URLEncoder.encode(jSessionId, "UTF-8");
		}

		try {
			String userUUID = null;
			if (resourceRequest.getRemoteUser() != null) {
				long userid = Long.parseLong(resourceRequest.getRemoteUser());
				User user = UserLocalServiceUtil.getUser(userid);
				userUUID = user.getUserUuid();
			} else{
				userUUID = (new UUID(0, 0)).toString();
			}

			if (userUUID != null){
				cookie += (!cookie.isEmpty() ? "; " : "") + "username=" + username;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		connection.setRequestProperty("tenant", scope);
		connection.setRequestProperty("gcube-token", token);
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setInstanceFollowRedirects(false);
		connection.setRequestMethod(resourceRequest.getMethod());
		connection.setRequestProperty("charset", resourceRequest.getCharacterEncoding());
		connection.setUseCaches(false);
		
		if (resourceRequest.getContentType() != null){
			connection.setRequestProperty("Content-Type", resourceRequest.getContentType());
		}
		if (!cookie.isEmpty()){
			connection.setRequestProperty("Cookie", cookie);
		}
		
		if (postDataLength > 0) {
			connection.setRequestProperty("Content-Length", Integer.toString(postDataLength));
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.write(postData);
		}

		try {
			connection.connect();
			int status = connection.getResponseCode();
					
			resourceResponse.setProperty(ResourceResponse.HTTP_STATUS_CODE, Integer.toString(status));	

			if (connection.getResponseCode() != 200) {
				throw new IOException("Response code: " + connection.getResponseCode() + " . response msg: " + connection.getResponseMessage());
			}

			if (connection.getContentType() != null) {
				resourceResponse.setContentType(connection.getContentType());
			}
			resourceResponse.setCharacterEncoding(connection.getContentEncoding());
			resourceResponse.setContentLength(connection.getContentLength());
			if (connection.getHeaderField("Content-Disposition") != null){
				resourceResponse.setProperty("Content-Disposition", connection.getHeaderField("Content-Disposition"));
			}
			ByteStreams.copy(connection.getInputStream(), resourceResponse.getPortletOutputStream());

			String cookieSessionId = getHeaderValue(connection.getHeaderField("Set-Cookie"), "JSESSIONID");
			if (cookieSessionId != null) {
				resourceRequest.getPortletSession().setAttribute("JSESSIONID", cookieSessionId);
			}
		} catch (Exception e) {
			String response = CharStreams.toString(new InputStreamReader(connection.getErrorStream(), Charsets.UTF_8));
			resourceResponse.getWriter().write(response);				
			_log.error("Response: " + response, e);				
		} 
		
		//super.serveResource(resourceRequest, resourceResponse);
	}

	@Override
	protected void doDispatch(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		super.doDispatch(request, response);
	}

	protected void include(String path, RenderRequest renderRequest, RenderResponse renderResponse)	throws IOException, PortletException {
		String url = null;
		if (renderRequest.getParameter("jspPage") == null || renderRequest.getParameter("jspPage").equals("./")){
			url = path;
		}else{
			url = path + renderRequest.getParameter("jspPage");
		}

		PortletRequestDispatcher portletRequestDispatcher = getPortletContext().getRequestDispatcher(url);

		if (portletRequestDispatcher == null) {
			_log.error(url + " is not a valid include");
		} else {
			portletRequestDispatcher.include(renderRequest, renderResponse);
		}
	}

	private static String getHeaderValue(String field, String name) {
		if (field != null){
			for (String value : field.split("; ")) {
				if (value.startsWith(name + "=")){
					return value.replace(name + "=", "");
				}
			}
		}
		return null;
	}
}