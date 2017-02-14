package gr.cite.geoanalytics.administration.portlet;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;

import gr.cite.geoanalytics.administration.portlet.utils.ServiceDiscovery;
import gr.cite.geoanalytics.administration.portlet.utils.ServiceProfile;

/**
 * Portlet implementation class Home
 */
public class GeoanalyticsAdministrationHome extends GenericPortlet {
	protected String backendURL;
	protected String viewTemplate;
	protected String username;
	protected String user;
	protected String pass;

	private static ServiceProfile geoanalyticsProfile = ServiceProfile.createGeoanalyticsProfile();
	private static Log _log = LogFactoryUtil.getLog(GeoanalyticsAdministrationHome.class);
	//private static Gson gson = new Gson();

	public void init() {
		viewTemplate = getInitParameter("views");
		backendURL = getInitParameter("back-end-url");
		// username = getInitParameter("user-name");
		/*
		 * user = getInitParameter("username"); pass = getInitParameter("password");
		 */

		Authenticator.setDefault(new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(user, pass.toCharArray());
			}
		});
	}

	public void doView(RenderRequest renderRequest, RenderResponse renderResponse)	throws IOException, PortletException {		
		try {
			ScopeHelper.setContext(renderRequest);
		} catch(Exception e){
			
		}		
		
		String username = (String)renderRequest.getPortletSession().getAttribute(ScopeHelper.USERNAME_ATTRIBUTE, PortletSession.APPLICATION_SCOPE);
		ASLSession session = SessionManager.getInstance().getASLSession(renderRequest.getPortletSession().getId(), username);
		
		String scope = session.getScope();
		String token = session.getSecurityToken();	
		
/*		PortalContext pContext = PortalContext.getConfiguration();
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		String username = pContext.getCurrentUser(httpServletRequest).getUsername();
		String scope = pContext.getCurrentScope(httpServletRequest);
		String token = pContext.getCurrentUserToken(httpServletRequest);	*/
		
		
		String dynamicBackendURL = null;
		
		try {
			dynamicBackendURL = ServiceDiscovery.fetchServiceEndpoint(scope, geoanalyticsProfile);
		} catch (Exception e) {
			_log.error("Could not fetch the geoanalytics service from IS. Will try to use the static one if exists");
		}		 

		backendURL =  dynamicBackendURL != null ? dynamicBackendURL : backendURL;		

		String uri = backendURL	+ (renderRequest.getParameter("jspPage") == null || renderRequest.getParameter("jspPage").equals("./")
						? "" : renderRequest.getParameter("jspPage").replace(".jsp", ""));
		
		if (renderRequest.getParameter("getParams") != null){
			uri += "?" + renderRequest.getParameter("getParams");
		}
		
		URL obj = new URL(uri);
		HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
		conn.setRequestProperty("modelRequest", "true");

		String cookie = "";
		String jSessionId = (String) renderRequest.getPortletSession().getAttribute("JSESSIONID");
		if (jSessionId != null && !jSessionId.isEmpty() && !jSessionId.equals("null")) {
			cookie = "JSESSIONID=" + URLEncoder.encode(jSessionId, "UTF-8");
		}

		try {
			cookie += (!cookie.isEmpty() ? "; " : "") + "username=" + username;
			conn.setRequestProperty("Cookie", cookie);
			conn.setRequestProperty("gcube-token", token);
		} catch (Exception e) {
			e.printStackTrace();
		}

		include(viewTemplate, renderRequest, renderResponse);

		conn.setInstanceFollowRedirects(false);

		try {
			conn = connect(conn);
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				renderResponse.getWriter().write("Message: " + conn.getResponseMessage());
				renderResponse.getWriter().write(CharStreams.toString(new InputStreamReader(conn.getInputStream(), Charsets.UTF_8)));
			}
		} catch (IOException e) {
			_log.error("Could not connect to backend.");
		} catch (Exception e) {
			_log.error(e);
		}
	}

	private static HttpURLConnection connect(HttpURLConnection conn) throws IOException {
		int retries = 3;

		while (retries > 0) {
			int status = conn.getResponseCode();
			if (status != HttpURLConnection.HTTP_OK) {
				if (status != HttpURLConnection.HTTP_MOVED_TEMP && status != HttpURLConnection.HTTP_MOVED_PERM	&& status != HttpURLConnection.HTTP_SEE_OTHER){
					retries = 0;
				}else{
					retries--;
				}
			} else{
				retries = 0;
			}

			if (retries > 0) {
				String method = conn.getRequestMethod();
				String newUrl = conn.getHeaderField("Location");
				Map<String, List<String>> reqProps = conn.getRequestProperties();

				String cookies = conn.getHeaderField("Set-Cookie");

				conn = (HttpURLConnection) new URL(newUrl).openConnection();
				conn.setRequestMethod(method);

				for (Entry<String, List<String>> prop : reqProps.entrySet()) {
					conn.setRequestProperty(prop.getKey(), prop.getValue().get(0));
				}

				conn.setRequestProperty("Cookie", cookies);
			}
		}
		return conn;
	}

	@Override
	public void processAction(ActionRequest actionRequest, ActionResponse actionResponse)	throws PortletException, IOException {
		super.processAction(actionRequest, actionResponse);
	}

	@Override
	public void serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)	throws PortletException, IOException {		
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
		
		String username = (String)resourceRequest.getPortletSession().getAttribute(ScopeHelper.USERNAME_ATTRIBUTE, PortletSession.APPLICATION_SCOPE);
		ASLSession session = SessionManager.getInstance().getASLSession(resourceRequest.getPortletSession().getId(), username);		
		String scope = session.getScope();
		
/*		PortalContext pContext = PortalContext.getConfiguration();
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(resourceRequest);
		String scope = pContext.getCurrentScope(httpServletRequest);*/		
		
		connection.setRequestProperty("scope", scope);
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
			if (connection.getResponseCode() != 200) {
				if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
					resourceResponse.getWriter().write("Message: " + connection.getResponseMessage());
					resourceResponse.getWriter().write(CharStreams.toString(new InputStreamReader(connection.getInputStream(), Charsets.UTF_8)));
				}
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
		} catch (IOException ex) {
			_log.error(ex);
			resourceResponse.setProperty(ResourceResponse.HTTP_STATUS_CODE, Integer.toString(HttpServletResponse.SC_NOT_FOUND));
		} catch (Exception e) {
			_log.error(e);
			resourceResponse.setProperty(ResourceResponse.HTTP_STATUS_CODE, connection.getResponseCode() + "");				
		} 
		
		super.serveResource(resourceRequest, resourceResponse);
	}

	@Override
	protected void doDispatch(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		// TODO Auto-generated method stub
		super.doDispatch(request, response);
	}

	protected void include(String path, RenderRequest renderRequest, RenderResponse renderResponse)	throws IOException, PortletException {
		String url = null;
		if (renderRequest.getParameter("jspPage") == null || renderRequest.getParameter("jspPage").equals("./")){
			url = path + "admin.jsp";// TODO Change here care
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