package gr.uoa.di.madgik.visualization.portlet;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.*;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import org.gcube.common.portal.PortalContext;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.uoa.di.madgik.visualization.endpoint.EndpointManager;
import gr.uoa.di.madgik.visualization.exceptions.ServiceDiscoveryException;
import gr.uoa.di.madgik.visualization.service.ServiceProfile;
import com.fasterxml.jackson.core.type.TypeReference;

public class VisualizationPortlet extends GenericPortlet {

    protected String staticEndpoint;
    protected String viewTemplate;
    protected String username;
    protected String user;
    protected String pass;

    private ServiceProfile analyticsProfile;
    private EndpointManager endpointManager;

    private static final ObjectMapper mapper = new ObjectMapper();

    private static Logger logger = LoggerFactory.getLogger(VisualizationPortlet.class);

    private static final int HTTP_CONNECTION_TIMEOUT = 15000;

    @Override
    public void init() {
        viewTemplate = getInitParameter("view-template");
        staticEndpoint = getInitParameter("back-end-url");

        analyticsProfile = new ServiceProfile();
        analyticsProfile.setServiceClass("DataAnalysis");
        analyticsProfile.setServiceName("data-analytics-visualization");
        analyticsProfile.setPathEndsWith("/");

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
        logger.info("Visualization portlet is rendering the main view");

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
       // HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(resourceRequest);
        HttpServletRequest httpServletRequest = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(resourceRequest));

        logger.info(httpServletRequest.toString());

        Enumeration<String> parameterNames = httpServletRequest.getParameterNames();

        while (parameterNames.hasMoreElements()) {

            String paramName = parameterNames.nextElement();
            logger.info(paramName);
            logger.info("n");

            String[] paramValues = httpServletRequest.getParameterValues(paramName);
            for (int i = 0; i < paramValues.length; i++) {
                String paramValue = paramValues[i];
                logger.info("t" + paramValue);
                logger.info("n");
            }

        }
        String scope = pContext.getCurrentScope(httpServletRequest);

//        logger.info("Serving resource for scope: " + scope);
//
//        logger.info("Is liferay request: " + liferayRequests(resourceRequest, resourceResponse));

        if (liferayRequests(resourceRequest, resourceResponse)) {
            try {
                List<String> endpoints = endpointManager.getServiceEndpoints(scope, analyticsProfile);

                for (String endpoint : endpoints) {
                    logger.info("Trying to contact endpoint: " + endpoint);

                    try {
                        int status = doRequest(endpoint, resourceRequest, resourceResponse);
                        if (isOKStatus(status)) {
                            break;
                        }
                    } catch (Exception e) {
                        endpointManager.removeServiceEndpoint(scope, analyticsProfile, endpoint);
                        logger.warn("Cannot reach endpoint", e);
                    }
                }
            } catch (ServiceDiscoveryException e) {
                logger.error(e.getMessage(), e);
                try {
                    doRequest(staticEndpoint, resourceRequest, resourceResponse);
                } catch (SocketTimeoutException ex) {
                    resourceResponse.getWriter().write("Service is currently unavailable");
                    resourceResponse.setProperty(ResourceResponse.HTTP_STATUS_CODE, "500");
                }
            }
        }
    }

    public int doRequest(String endpoint, ResourceRequest resourceRequest, ResourceResponse resourceResponse) throws IOException {
        PortalContext pContext = PortalContext.getConfiguration();
        HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(resourceRequest);

     //   HttpServletRequest httpServletRequest = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(resourceRequest));


        GCubeUser user = pContext.getCurrentUser(httpServletRequest);
        String username = user.getUsername();
        String email = user.getEmail();
        String initials = getInitials(user);
        long id = user.getUserId();


        String uuid = null;
        try {
            uuid = UserLocalServiceUtil.getUserById(id).getUserUuid();
        } catch (SystemException | PortalException ex) {
            logger.error(ex.getMessage(), ex);
        }


        String scope = pContext.getCurrentScope(httpServletRequest);
        String token = pContext.getCurrentUserToken(scope, username);
        String resourceUrl = buildResourceUrl(endpoint, resourceRequest);
        String method = getRequestMethod(httpServletRequest, resourceRequest);
//        logger.info("scope: " + scope);
//        logger.info("token: " + token);
//        logger.info("resourceUrl: " + resourceUrl);
//        logger.info("method: " + method);

        URL url = new URL(resourceUrl);


//        logger.info("Url: " + url);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("gcube-scope", scope);
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
        connection.setRequestMethod(method);
        connection.setUseCaches(false);
        connection.setConnectTimeout(HTTP_CONNECTION_TIMEOUT);

        InputStream is = resourceRequest.getPortletInputStream();
        byte[] postData = ByteStreams.toByteArray(is);
        is.close();

//        logger.info("Post data: " + postData.toString());

        if (postData.length > 0) {
            connection.setRequestProperty("Content-Length", Integer.toString(postData.length));
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.write(postData);
//            logger.info("Inside post data if: " + postData.toString());

        }

        int status = 500;

        try {
//            logger.info("Connection: " + connection.toString());

            connection.connect();
//            logger.info("After connect: " );

            status = connection.getResponseCode();
//            logger.info("status: " + status);

            resourceResponse.setProperty(ResourceResponse.HTTP_STATUS_CODE, Integer.toString(status));

            int responseCode = connection.getResponseCode();
            if (!isOKStatus(responseCode)) {
                String response = CharStreams.toString(new InputStreamReader(connection.getErrorStream(), Charsets.UTF_8));
                logger.debug("Service response:" + response);

                resourceResponse.getWriter().write(response);
            } else {
//                logger.debug("Back end service returned with status " + responseCode + ": " + connection.getContentLength() + " bytes");

                resourceResponse.setCharacterEncoding(connection.getContentEncoding());
                resourceResponse.setContentLength(connection.getContentLength());

                if (connection.getContentType() != null) {
                    resourceResponse.setContentType(connection.getContentType());
                }
                if (connection.getHeaderField("Content-Disposition") != null) {
                    resourceResponse.setProperty("Content-Disposition", connection.getHeaderField("Content-Disposition"));
                }
                if (connection.getHeaderField("name") != null) {
                    resourceResponse.setProperty("name", connection.getHeaderField("name"));
                }

                ByteStreams.copy(connection.getInputStream(), resourceResponse.getPortletOutputStream());
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
        logger.info("status: " + status);

        return status;
    }

    protected String buildResourceUrl(String endpoint, ResourceRequest resourceRequest) {
//        logger.info("ResourceUrl no parameters: " + endpoint);
//        logger.info("ResourceUrl no parameters: " + resourceRequest.toString());

        StringBuilder resourceUrl = new StringBuilder(endpoint);

//        logger.info("ResourceUrl no parameters: " + resourceUrl);
//        logger.info("Is get: " + resourceRequest.getMethod().toUpperCase().equals("GET"));
        resourceUrl.append(resourceRequest.getResourceID());

        if (resourceRequest.getMethod().toUpperCase().equals("GET")) {


//            logger.info(resourceRequest.getResourceID());
//            resourceUrl.append(resourceRequest.getResourceID());
//            logger.info(resourceUrl.toString());

//            String params  = resourceRequest.getResourceID().replace("\'", "");
//            logger.info(params);
            HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(resourceRequest);

            Enumeration<String> parameterNames = httpServletRequest.getParameterNames();
//            logger.info(parameterNames.toString());

            while (parameterNames.hasMoreElements()) {
                String param = parameterNames.nextElement();
//                logger.info("Parameter name -> " + param);
//                logger.info("Parameter value -> " + httpServletRequest.getParameterValues(param));
                String[] ids = httpServletRequest.getParameterValues(param);

                for(int j=0; j < ids.length; j++){
                    resourceUrl.append(param + "=" + ids[j]);
                    if((j+1) < ids.length)
                        resourceUrl.append("&");
                }

                if(parameterNames.hasMoreElements())
                    resourceUrl.append("&");
            }
//            logger.info("ResourceUrl inside if: " + resourceUrl);

            //addQueryParameters(resourceUrl, resourceRequest);
            System.out.println("SIZE:"+resourceRequest.getParameterMap().size());
   //         resource =resourceUrl.toString().replace(",","&");
        }
        String resource =resourceUrl.toString();

        logger.info("ResourceUrl with parameters: " + resource);

        return resource;
    }


    protected void addQueryParameters(StringBuilder resourceUrl, ResourceRequest resourceRequest)  {
        System.out.println("SIZE:"+resourceRequest.getParameterMap().size());
        if (!resourceUrl.toString().contains("?")) {
            resourceUrl.append("?");
        }

        try {
            String[] urlParts = resourceUrl.toString().split("\\?");
//            logger.info(resourceUrl.toString());

            if (urlParts.length > 1) {
                Map<String, Object> parameters = null;


                parameters = mapper.readValue(urlParts[1], new TypeReference<Map<String, Object>>() {});

//                logger.info(parameters.toString());
//
//                logger.info(urlParts.toString());

                resourceUrl.delete(resourceUrl.indexOf("?")+1,resourceUrl.length());

//                logger.info(parameters.toString());
                parameters.entrySet().stream().forEach(entry -> {
                    logger.info(entry.getKey()  +" , " + entry.getValue() );
                   if (entry.getValue() instanceof String) {
                        resourceUrl.append("&" + entry.getKey() + "=" + entry.getValue().toString());
                    }
                   else {

                       for (String node : (List<String>) entry.getValue()) {
                           resourceUrl.append("&" + entry.getKey() + "[]=" + node);
                       }
                   }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("I'm simple request");
        }


        resourceRequest.getParameterMap().entrySet().stream().forEach(entry -> {
//            logger.info("param: " + "&" + entry.getKey() + "=" + entry.getValue()[0]);

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
        return user.getFirstName().substring(0, 1)
                + (user.getMiddleName() != null && user.getMiddleName().length() > 0 ? user.getMiddleName().substring(0, 1) : "")
                + user.getLastName().substring(0, 1);
    }

    public boolean liferayRequests(ResourceRequest resourceRequest, ResourceResponse resourceResponse) throws IOException {
        boolean getLocale = ParamUtil.getBoolean(resourceRequest, "getLocale");

        if (getLocale) {
            JSONObject jsonObject = JSONFactoryUtil.createJSONObject();
            jsonObject.put("locale", PortalUtil.getHttpServletRequest(resourceRequest).getLocale().toString());

            resourceResponse.getWriter().println(jsonObject);

            return false;
        }

        return true;
    }

    private String getRequestMethod(HttpServletRequest httpServletRequest, ResourceRequest resourceRequest) {

        for (Enumeration<String> e = httpServletRequest.getHeaderNames(); e.hasMoreElements(); ) {
            String header = e.nextElement();
//            logger.info("Header " + header + " - " + httpServletRequest.getHeader(header));
        }

        String method = httpServletRequest.getHeader("gcube-request-method");
        return method == null || method.isEmpty() ? resourceRequest.getMethod() : method;
    }

    private boolean isOKStatus(int status) {
        return status == 200 || status == 201;
    }
}
