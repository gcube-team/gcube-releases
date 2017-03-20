package org.gcube.portlets;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;

/**
 * Portlet implementation class Home
 */
public class GeoanalyticsHome extends GenericPortlet {
	protected String viewTemplate;
	protected String backendURL;
	protected String user;
	protected String pass;

	private static Log _log = LogFactoryUtil.getLog(GeoanalyticsHome.class);

	public void init() {
		viewTemplate = getInitParameter("views");
		
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
		String tenant = pContext.getCurrentScope(httpServletRequest); 		
		String theUserName = pContext.getCurrentUser(httpServletRequest).getUsername();
		String token = pContext.getCurrentUserToken(tenant, theUserName);	
		
		UserManager um = new LiferayUserManager();
		GCubeUser user = null;
		try {
			user = um.getUserByUsername(theUserName);
		} catch (UserManagementSystemException e) {
			e.printStackTrace();
		} catch (UserRetrievalFault e) {
			e.printStackTrace();
		}
		String usernameForController = "";
		String email = "";
		email =user.getEmail();
		usernameForController = user.getFullname();
		String username = "";
		
		try{
			username = URLEncoder.encode(usernameForController,"UTF-8");
		} catch(UnsupportedEncodingException e){
			e.printStackTrace();
		}
		
		if(backendURL == null){
			try{
				backendURL = DiscoveryOfAppropriateService.discoverServiceNodes(tenant);	
				_log.info("Using discovered Geoanalytics Service at " + backendURL);
			} catch (Exception e){				
//				backendURL = getInitParameter("back-end-url");
//				_log.info("Using static-local Geoanalytics Service at "  + backendURL);
			}
			
			if (!backendURL.endsWith("/")){
				backendURL = backendURL + "/";
				_log.info("Service doesnt end with backSlash");
			}
		}
		
		boolean fetchTenantAndUsername = ParamUtil.getBoolean(resourceRequest, "fetchTenantAndUsername");
		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();
		if(fetchTenantAndUsername){
			jsonObject = retrieveTenantAndUsername(resourceRequest, tenant, usernameForController, email);
		}
		
		if(resourceRequest.getResourceID() != null){
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
					User theUser = UserLocalServiceUtil.getUser(userid);
					userUUID = theUser.getUserUuid();
				}else
					userUUID = (new UUID(0,0)).toString();


				if (userUUID != null)
					cookie += (!cookie.isEmpty()? "; " :"") + "username=" + username;
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (!cookie.isEmpty())
		        cox.setRequestProperty("Cookie",cookie);
			cox.setRequestProperty("gcube-token", token);
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
			try{
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
			} catch (Exception e) {
				try{
					String response = CharStreams.toString(new InputStreamReader(cox.getErrorStream(), Charsets.UTF_8));
					resourceResponse.getWriter().write(response);				
					_log.error("Response: " + response, e);			
				} catch(Exception ex){
					
				}
			} 
		}
		
		if(fetchTenantAndUsername) {
			resourceResponse.getWriter().println(jsonObject);
		}
	}
	
	@Override
	protected void doDispatch(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		super.doDispatch(request, response);
	}

	protected void include(String path, RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException {
		String url = null;
		if (renderRequest.getParameter("jspPage") == null || renderRequest.getParameter("jspPage").equals("./"))
			url = path + "view.jsp";
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
	
	private JSONObject retrieveTenantAndUsername(
			ResourceRequest resourceRequest, String tenant,
			String usernameForController, String email) {
		JSONObject jo = JSONFactoryUtil.createJSONObject();
		
		try{
			jo.put("tenant", tenant);
			jo.put("fullname", usernameForController);
			jo.put("email" ,email);
			jo.put("initials", "");
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return jo;
	}
}