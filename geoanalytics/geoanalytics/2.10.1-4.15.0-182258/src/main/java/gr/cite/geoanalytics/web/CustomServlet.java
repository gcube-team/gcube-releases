package gr.cite.geoanalytics.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.ServiceException;
import gr.cite.clustermanager.trafficshaping.TrafficShaper;
import gr.cite.geoanalytics.util.http.CustomException;
import gr.cite.geoanalytics.web.auth.ServiceDiscovery;
import gr.cite.geoanalytics.web.auth.ServiceDiscoveryException;
import gr.cite.geoanalytics.web.auth.SocialNetworkingService;
import gr.cite.geoanalytics.web.auth.UserProfile;
import gr.cite.geoanalytics.web.caching.InMemoryCache;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

import static gr.cite.geoanalytics.dataaccess.entities.layer.dao.LayerVisualizationDaoImpl.log;

public abstract class CustomServlet extends HttpServlet {
    private final Logger logger = LoggerFactory.getLogger(CustomServlet.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    private String format = null;
    private InMemoryCache<String,UserProfile> userTokensCache = new InMemoryCache(15*60, 10, 10);

    TrafficShaper trafficShaper;
    SocialNetworkingService socialNetworkingService;
    private final String TOKEN_HEADER  = "gcube-token";
    private final String SCOPE_HEADER  = "gcube-scope";
//    static authservice that is initialized once, will see
    private Client jerseyClient;

    Map<String, Boolean> operrationsSupportedByTheProtocol = null;

    private class CustomServletResponseException {
        private int statusCode;
        private String message;

        public CustomServletResponseException(int statusCode, String message) {
            this.statusCode = statusCode;
            this.message = message;
        }

        public int getStatusCode() { return statusCode; }

        public void setStatusCode(int statusCode) { this.statusCode = statusCode; }

        public String getMessage() { return message; }

        public void setMessage(String message) { this.message = message; }
    }

    public CustomServlet(TrafficShaper trafficShaper, SocialNetworkingService socialNetworkingService) {
        this.trafficShaper = trafficShaper;
        this.socialNetworkingService = socialNetworkingService;
    }

    abstract void initializeSupportedOperationsMap();
    abstract void relay(HttpServletRequest request, HttpServletResponse response, String operationName) throws IOException;

    String getCaseInsensitiveParameter(String parameter, HttpServletRequest request) {
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            String paramName = entry.getKey();
            if (parameter.toLowerCase().equals(paramName.toLowerCase()))
                return entry.getValue()[0];
        }
        return null;
    }

    protected void isUserAuthenticated(HttpServletRequest request) throws ServiceException {
        logger.info("Authenticating user");
        boolean isAuthorized = false;
        String token = request.getHeader(TOKEN_HEADER);
        String scope = request.getHeader(SCOPE_HEADER);

        logger.info("Token: " + token	+ " scope: " + scope );
        if(token == null || token.equalsIgnoreCase("")) {
            logger.error("User token could not be retrieved");
            throw new ServiceException("User token could not be retrieved");
        }
        if(scope == null || scope.equalsIgnoreCase("")) {
            scope = request.getHeader("tenant");
            if(scope == null || scope.equalsIgnoreCase("")) {
                logger.error("User scope could not be retrieved");
                throw new ServiceException("User scope could not be retrieved");
            }

        }

        UserProfile userProfile = null;
        try {
            if(userTokensCache.get(token) == null) {
                userProfile = this.socialNetworkingService.getUserProfile(token, scope);
                userTokensCache.put(token, userProfile);
                logger.info("User token doesn\'t exist in cache");
            } else {
                userProfile = userTokensCache.get(token);
                logger.info("User token exists in cache");
            }
            logger.info("User is: " + userProfile.getFullname());
        } catch (ServiceDiscoveryException e) {
            logger.error(e.getMessage(), e);
            throw new ServiceException("Failed at retrieving user profile");
        }

        //call auth service
        isAuthorized = true;
        logger.info("User is authenticated");

//        return isAuthorized;
    }

    private boolean isOperationSupportedByTheProtocol(String operationName) throws CustomException {
        if(operrationsSupportedByTheProtocol != null && operrationsSupportedByTheProtocol.containsKey(operationName.toLowerCase()))
            return true;
        else
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Operation not supported by the protocol");
    }

    private boolean isOperationValid(HttpServletRequest request, String requestParameterName) throws CustomException {
        if(this.getCaseInsensitiveParameter(requestParameterName, request) != null) {
            return true;
        } else
            throw new CustomException(HttpStatus.BAD_REQUEST, "Invalid operation");
    }

    private boolean isOperationRequestImplemented(String requestTypeParameterName) throws CustomException {
        if(operrationsSupportedByTheProtocol.get(requestTypeParameterName.toLowerCase()))
            return true;
        else
            throw new CustomException(HttpStatus.NOT_IMPLEMENTED, "Operation not implemented");
    }

    void handleDoGet(HttpServletRequest request, HttpServletResponse response, String requestParameterName) throws IOException {
        this.format = null;

        try {
            this.isUserAuthenticated(request);
            this.isOperationValid(request, requestParameterName);

            String operationName = this.getCaseInsensitiveParameter(requestParameterName, request);
            this.isOperationSupportedByTheProtocol(operationName);
            this.isOperationRequestImplemented(operationName);

            this.relay(request, response, operationName);

        } catch (ServiceException e) {
            this.returnCustomErrorResponse(response, "User is anauthorized to view this resource", HttpStatus.UNAUTHORIZED);
        } catch (CustomException e) {
            this.returnCustomErrorResponse(response, e.getMessage(), HttpStatus.valueOf(e.getStatusCode()));
        } catch (RuntimeException e) {
            this.returnCustomErrorResponse(response, "An unexpected error occurred: " +e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    protected Client getClient() {
        if(jerseyClient == null) {
            ClientConfig cc = new ClientConfig();
            cc.register(MultiPartFeature.class);
            this.jerseyClient = ClientBuilder.newClient(cc);
        }

        return this.jerseyClient;
    }

    Response connect(String url, String requestFormat, String token) {

        return this.getClient().target(url).request(requestFormat).header(TOKEN_HEADER, token).get();//.get(String.class);
    }

    String buildFinalURL(HttpServletRequest request, String baseUrl, String serviceName,Collection<String> requiredParameters, Collection<String> optionalParameters, Map<String, String> interchangeableParameters) throws CustomException, IOException {
        StringBuilder urlsSb = new StringBuilder();
        urlsSb.append(baseUrl + "/" + serviceName);

        Enumeration<String> ps = request.getParameterNames();

        boolean first = true;

        Set<String> requiredParametersFound = new HashSet<>();
        String outputFormat=null;

        while (ps.hasMoreElements()) {
            String parameter = ps.nextElement();
            //Hold outputformat in a variable so that you can use this instead of the wfs protocol default outputformat. The default format causes an error
            if(parameter.toLowerCase().equalsIgnoreCase("OUTPUTFORMAT")) {
                outputFormat = parameter.toLowerCase();
                this.format = new StringBuilder(outputFormat).toString();
            }

            //Only parameters that are conformed to the protocol will be relayed to the geoserver
            if ( requiredParameters.contains(parameter.toLowerCase()) || (interchangeableParameters != null && interchangeableParameters.containsValue(parameter.toLowerCase())) ) {

                String parameterToBeAdded = interchangeableParameters != null && interchangeableParameters.containsValue(parameter.toLowerCase()) ? this.getInterchangeableParameterFromAlternativeName(interchangeableParameters, parameter) : parameter.toLowerCase();

                if ( !requiredParametersFound.contains(parameterToBeAdded) ) {
                    requiredParametersFound.add(parameterToBeAdded);
                } else {
                    throw new CustomException(HttpStatus.BAD_REQUEST, serviceName.toUpperCase() + " request is invalid. Parameter " + parameter + " must be defined once");
                }
            } else if (optionalParameters == null || !optionalParameters.contains(parameter.toLowerCase())) {
                log.debug("Unknown parameter " + parameter);
                continue;
            }

            String value = request.getParameter(parameter);
            if (value == null || value.trim().isEmpty()) {
                throw new CustomException(HttpStatus.BAD_REQUEST, serviceName.toUpperCase() + " request is invalid. Parameter " + parameter + " cannot be empty");
            }

            if (first) {
                urlsSb.append("?");
                first = false;
            } else {
                urlsSb.append("&");
            }

            urlsSb.append(URLEncoder.encode(parameter, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8"));
        }

        if (requiredParameters.size() != requiredParametersFound.size()) {
            requiredParameters.removeAll(requiredParametersFound);
            throw new CustomException(HttpStatus.BAD_REQUEST, serviceName.toUpperCase() + " request is invalid. Parameters missing: " + requiredParameters);
        }

        return urlsSb.toString();
    }

    void returnCustomErrorResponse(HttpServletResponse response, String message, HttpStatus status) throws IOException {
        //Don't change the order, otherwise everything will fall apart
        response.setStatus(status.value());
        response.setContentType("application/json");
        CustomServletResponseException csre = new CustomServletResponseException(status.value(), message);
        response.getOutputStream().write(mapper.writer().withDefaultPrettyPrinter().writeValueAsString(csre).getBytes(Charset.forName("UTF-8")));
//        response.getOutputStream().write(message.getBytes(Charset.forName("UTF-8")));
        response.getOutputStream().close();
    }

    String buildResponseForClientFromGeoserverResponse(Response geoserverResponse, HttpServletResponse response) throws CustomException {
        String responseFormat = geoserverResponse.getHeaders() != null && geoserverResponse.getHeaders().containsKey("Content-Type") ? geoserverResponse.getHeaders().get("Content-Type").toString() : this.getFormat();

        logger.info("Geoserver responded with mimetype: " + responseFormat);
        if(responseFormat.indexOf(MediaType.TEXT_XML) > -1)
            responseFormat = MediaType.TEXT_XML;

        response.setContentType(responseFormat);

        StringBuilder stringBuilder = null;
        try {
            stringBuilder = new StringBuilder( geoserverResponse.readEntity(String.class) );
        } catch (Exception e) {
            logger.error( e.getMessage() );
            throw new CustomException(HttpStatus.BAD_REQUEST,"Geoserver response parsing error");
        }

        return stringBuilder.toString();
    }

    private String getInterchangeableParameterFromAlternativeName(Map<String, String> interchangeableParameters, String param) {
        return interchangeableParameters.entrySet().stream().
                filter(entrySet -> entrySet.getValue().equalsIgnoreCase(param))
                .map(entryset -> entryset.getKey())
                .collect(Collectors.toList()).get(0);
    }

    public String getFormat() { return format; }
}