package org.gcube.portlets;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
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
import javax.servlet.http.HttpServletRequest;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.utils.ServiceDiscovery;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;

/**
 * Portlet implementation class Home
 */
public class GeoanalyticsAdministrationHome extends GenericPortlet {
	protected String backendURL;
	protected String viewTemplate;
	protected String username;
	protected String user;
	protected String pass;

	private static Log _log = LogFactoryUtil.getLog(GeoanalyticsAdministrationHome.class);

	private Gson gson = new Gson();

	public void init() {
		viewTemplate = getInitParameter("views");
		backendURL = getInitParameter("back-end-url");
		//username = getInitParameter("user-name");
		/*user = getInitParameter("username");
		pass = getInitParameter("password");*/
		
		Authenticator.setDefault(new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(user, pass.toCharArray());
			}
		});
	}

	public void doView(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException {
		//gCube oriented code
		try {
			ScopeHelper.setContext(renderRequest);
		}
		catch(Exception e){
			
		}
		username = (String)renderRequest.getPortletSession().getAttribute(ScopeHelper.USERNAME_ATTRIBUTE, PortletSession.APPLICATION_SCOPE);
		ASLSession session = SessionManager.getInstance().getASLSession(renderRequest.getPortletSession().getId(), username);
		
		String scope = session.getScope();
		String token = session.getSecurityToken();
		
		String dynamicBackendURL = null;
		try {
			dynamicBackendURL = ServiceDiscovery.fetchServiceEndpoint(scope);
		} catch (Exception e1) {
			_log.error("Could not manage to fetch the geoanalytics service url from IS. Will try to use the static one if exists");
		}
		if (dynamicBackendURL != null)
			backendURL = dynamicBackendURL;
		
		String uri = backendURL + (renderRequest.getParameter("jspPage") == null || renderRequest.getParameter("jspPage").equals("./")? "" : renderRequest.getParameter("jspPage").replace(".jsp", ""));
		if (renderRequest.getParameter("getParams") != null)
			uri += "?" + renderRequest.getParameter("getParams");
		URL obj = new URL(uri);
		HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
		conn.setRequestProperty("modelRequest", "true");
		
		String cookie = "";
		String jSessionId = (String) renderRequest.getPortletSession().getAttribute("JSESSIONID");
		if (jSessionId != null && !jSessionId.isEmpty() && !jSessionId.equals("null")) {
			cookie = "JSESSIONID=" + URLEncoder.encode(jSessionId, "UTF-8");
	    }
		
		// get the username
		try {
//			String userUUID = null;
//			String screenName = null;
//			if(renderRequest.getRemoteUser() != null) {
//				long userid = Long.parseLong(renderRequest.getRemoteUser());
//				User user = UserLocalServiceUtil.getUser(userid);
////				userUUID = user.getUserUuid();
//				screenName = user.getScreenName();
//			}else
////				userUUID = (new UUID(0,0)).toString();
//				screenName = "guest";

			cookie += (!cookie.isEmpty()? "; " :"") + "username=" + username;
			conn.setRequestProperty("Cookie", cookie);
			conn.setRequestProperty("gcube-token", token);
		} catch (Exception e) {
			e.printStackTrace();
		}

		conn.setInstanceFollowRedirects(false);
		
		conn = connect(conn);
		include(viewTemplate, renderRequest, renderResponse);
		if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
			renderResponse.getWriter().write("error: " + conn.getResponseCode()); 
			renderResponse.getWriter().write("\nmsg: " + conn.getResponseMessage());
			try{
				renderResponse.getWriter().write(CharStreams.toString(new InputStreamReader(conn.getInputStream(), Charsets.UTF_8)));;
			} catch(Exception e) {}
			return;
		}
//		String model = CharStreams.toString(new InputStreamReader(conn.getInputStream()));
//		if (model != null) {
//			HashMap<String, Object> modelParams = gson.fromJson(model, new TypeToken<HashMap<String, Object>>() {
//			}.getType());
//	
//			for (Entry<String, Object> entry : modelParams.entrySet())
//				renderRequest.setAttribute(entry.getKey(), entry.getValue());
//		}
		
	}
	
	private static HttpURLConnection connect(HttpURLConnection conn) throws IOException {
		int retries = 3;

		while(retries > 0) {
			int status = conn.getResponseCode();
			if (status != HttpURLConnection.HTTP_OK) {
				if (status != HttpURLConnection.HTTP_MOVED_TEMP && status != HttpURLConnection.HTTP_MOVED_PERM
						&& status != HttpURLConnection.HTTP_SEE_OTHER)
					retries = 0;
				else
					retries--;
			} else
				retries = 0;
	
			if (retries > 0) {
	
				String method = conn.getRequestMethod();
				// get redirect url from "location" header field
				String newUrl = conn.getHeaderField("Location");
				Map<String, List<String>> reqProps = conn.getRequestProperties();
				
				
				// get the cookie if need, for login
				String cookies = conn.getHeaderField("Set-Cookie");
	
				// open the new connnection again
				conn = (HttpURLConnection) new URL(newUrl).openConnection();
				
				conn.setRequestMethod(method);
				
				for(Entry<String, List<String>> prop : reqProps.entrySet()){
					conn.setRequestProperty(prop.getKey(), prop.getValue().get(0));
				}
				
				conn.setRequestProperty("Cookie", cookies);
			}
		}
		return conn;
	}
	
	@Override
	public void processAction(ActionRequest actionRequest, ActionResponse actionResponse) throws PortletException, IOException {
		super.processAction(actionRequest, actionResponse);
	}
	
	@Override
	public void serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse) throws PortletException, IOException {
		try {
			InputStream is = resourceRequest.getPortletInputStream();
			byte[] postData = ByteStreams.toByteArray(is);
			is.close();
			int postDataLength = postData.length;
			String request = backendURL + resourceRequest.getResourceID();
			URL url = new URL(request);

			HttpURLConnection cox = (HttpURLConnection) url.openConnection();

			String cookie = "";
			String jSessionId = (String) resourceRequest.getPortletSession().getAttribute("JSESSIONID");
			if (jSessionId != null && !jSessionId.isEmpty() && !jSessionId.equals("null")) {
				cookie = "JSESSIONID=" + URLEncoder.encode(jSessionId, "UTF-8");
		    }

			try {
				String userUUID = null;
				if(resourceRequest.getRemoteUser() != null) {
					long userid = Long.parseLong(resourceRequest.getRemoteUser());
					User user = UserLocalServiceUtil.getUser(userid);
					userUUID = user.getUserUuid();
				}else
					userUUID = (new UUID(0,0)).toString();


				if (userUUID != null)
					cookie += (!cookie.isEmpty()? "; " :"") + "username=" + username;
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (!cookie.isEmpty())
		        cox.setRequestProperty("Cookie",cookie);

			
			cox.setDoOutput(true);
			cox.setDoInput(true);
			cox.setInstanceFollowRedirects(false);
			cox.setRequestMethod(resourceRequest.getMethod());
			if (resourceRequest.getContentType() != null)
				cox.setRequestProperty("Content-Type", resourceRequest.getContentType());
			cox.setRequestProperty("charset", resourceRequest.getCharacterEncoding());
			cox.setUseCaches(false);
			if (postDataLength > 0) {
				cox.setRequestProperty("Content-Length", Integer.toString(postDataLength));
				DataOutputStream wr = new DataOutputStream(cox.getOutputStream());
				wr.write(postData);
			}

			cox.connect();
			if (cox.getResponseCode() != 200) {
				throw new IOException("Response code: " + cox.getResponseCode() + " . response msg: " + cox.getResponseMessage());
			}
			if(cox.getContentType() != null)
				resourceResponse.setContentType(cox.getContentType());
			resourceResponse.setCharacterEncoding(cox.getContentEncoding());
			resourceResponse.setContentLength(cox.getContentLength());
			if (cox.getHeaderField("Content-Disposition") != null)
				resourceResponse.setProperty("Content-Disposition", cox.getHeaderField("Content-Disposition"));
			ByteStreams.copy(cox.getInputStream(), resourceResponse.getPortletOutputStream());
			
			String cookieSessionId = getHeaderValue(cox.getHeaderField("Set-Cookie"), "JSESSIONID");
			if (cookieSessionId != null) {
				resourceRequest.getPortletSession().setAttribute("JSESSIONID", cookieSessionId);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.serveResource(resourceRequest, resourceResponse);
	}
	
	@Override
	protected void doDispatch(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		// TODO Auto-generated method stub
		super.doDispatch(request, response);
	}

	protected void include(String path, RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException {
		String url = null;
		if (renderRequest.getParameter("jspPage") == null || renderRequest.getParameter("jspPage").equals("./"))
			url = path + "admin.jsp";//TODO Change here care
		else
			url = path + renderRequest.getParameter("jspPage");

		PortletRequestDispatcher portletRequestDispatcher = getPortletContext().getRequestDispatcher(url);

		if (portletRequestDispatcher == null) {
			_log.error(url + " is not a valid include");
		} else {
			portletRequestDispatcher.include(renderRequest, renderResponse);
		}
	}
	
	private static String getHeaderValue(String field, String name) {
		if (field != null)
			for (String value : field.split("; ")){
				if (value.startsWith(name + "="))
					return value.replace(name + "=", "");
			}
		return null;
	}
}
