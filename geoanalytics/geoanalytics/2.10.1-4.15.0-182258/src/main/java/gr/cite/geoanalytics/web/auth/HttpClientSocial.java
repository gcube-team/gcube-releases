package gr.cite.geoanalytics.web.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.SocketTimeoutException;
import java.util.Map;

public class HttpClientSocial {
    private static final Logger _log = LoggerFactory.getLogger(HttpClientSocial.class);

    private static ObjectMapper mapper = new ObjectMapper();
    private static Integer HTTP_CONNECTION_TIMEOUT;

    private static Client singletonHttpClient;
    private String host;
    private String scope;
    private String token;
    private String userName;
    private String userFullName;
    private String userEmail;

    public HttpClientSocial() {
    }

    private static synchronized Client getSingletonHttpClient() {
        return singletonHttpClient == null ? singletonHttpClient = ClientBuilder.newClient() : singletonHttpClient;
    }

    public Response doGetCSV(String serviceUrl, Map<String, Object> headers) {
        _log.debug("GET request to: " + serviceUrl);
        this.augmentHeaders(headers);
        Client client = HttpClientSocial.getSingletonHttpClient();
        client.property(ClientProperties.CONNECT_TIMEOUT, HTTP_CONNECTION_TIMEOUT);
        client.register(JacksonFeature.class);

        WebTarget webTarget = client.target(getHost() + serviceUrl);
        Invocation.Builder builder = webTarget.request(MediaType.APPLICATION_OCTET_STREAM);

        for (Map.Entry<String, Object> entry : headers.entrySet()) {
            builder.header(entry.getKey(), entry.getValue());
        }

        return builder.get();
    }

    public Response doGet(String serviceUrl, Map<String, Object> headers) {
        _log.debug("GET request to: " + serviceUrl);
//		this.augmentHeaders(headers);
        Client client = HttpClientSocial.getSingletonHttpClient();
        client.property(ClientProperties.CONNECT_TIMEOUT, HTTP_CONNECTION_TIMEOUT);
        client.register(JacksonFeature.class);

        WebTarget webTarget = client.target(serviceUrl);
        Invocation.Builder builder = webTarget.request(MediaType.APPLICATION_JSON);

        builder.accept(MediaType.APPLICATION_JSON);
        for (Map.Entry<String, Object> entry : headers.entrySet()) {
            builder.header(entry.getKey(), entry.getValue());
        }

        return builder.get();
    }

    public Response doPost(String serviceUrl, Map<String, Object> headers, Object bodyObject) {
        _log.debug("POST request to: " + serviceUrl);
        _log.info("POST request to: " + serviceUrl);
        System.out.println("POST request to: " + serviceUrl);
        this.augmentHeaders(headers);
        Client client = HttpClientSocial.getSingletonHttpClient();
        client.property(ClientProperties.CONNECT_TIMEOUT, HTTP_CONNECTION_TIMEOUT);
        client.register(JacksonFeature.class);


        WebTarget webTarget = client.target(/*getHost() + */serviceUrl);
        Invocation.Builder builder = webTarget.request(MediaType.APPLICATION_JSON);

        builder.accept(MediaType.APPLICATION_JSON);
        for (Map.Entry<String, Object> entry : headers.entrySet()) {
            builder.header(entry.getKey(), entry.getValue());
        }

        Invocation invocation = builder.buildPost(Entity.entity(bodyObject, MediaType.APPLICATION_JSON_TYPE));
        return invocation.invoke();
    }

    public Response doDelete(String serviceUrl, Map<String, Object> headers) {
        _log.debug("DELETE request to: " + serviceUrl);
        this.augmentHeaders(headers);
        Client client = HttpClientSocial.getSingletonHttpClient();
        client.property(ClientProperties.CONNECT_TIMEOUT, HTTP_CONNECTION_TIMEOUT);
        client.register(JacksonFeature.class);

        WebTarget webTarget = client.target(getHost() + serviceUrl);
        Invocation.Builder builder = webTarget.request(MediaType.APPLICATION_JSON);

        builder.accept(MediaType.APPLICATION_JSON);
        for (Map.Entry<String, Object> entry : headers.entrySet()) {
            builder.header(entry.getKey(), entry.getValue());
        }

        Invocation invocation = builder.buildDelete();
        return invocation.invoke();
    }

    public Integer exceptionHandler(Exception e) {
        if (e instanceof BadRequestException) {
            return 400;
        } else if (e instanceof NotAuthorizedException) {
            return 401;
        } else if (e instanceof ForbiddenException) {
            return 403;
        } else if (e instanceof NotFoundException) {
            return 404;
        } else if (e instanceof NotAllowedException) {
            return 405;
        } else if (e instanceof NotAcceptableException) {
            return 406;
        } else if (e instanceof NotSupportedException) {
            return 415;
        } else if (e instanceof InternalServerErrorException) {
            return 500;
        } else if (e instanceof ServiceUnavailableException) {
            return 503;
        } else if (e instanceof SocketTimeoutException) {
            return 504;
        } else {
            return 500;
        }
    }

    public static String toJson(Object bodyObject) {
        String json = null;

        try {
            json = mapper.writeValueAsString(bodyObject);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return json;
    }

    public Integer getHTTP_CONNECTION_TIMEOUT() {
        return HTTP_CONNECTION_TIMEOUT;
    }

    public void setHTTP_CONNECTION_TIMEOUT(Integer httpConnectionTimeout) {
        HTTP_CONNECTION_TIMEOUT = httpConnectionTimeout;
    }

    public void augmentHeaders(Map<String, Object> headers) {
        headers.put("gcube-token", this.getToken());
        headers.put("username", this.getUserName());
        headers.put("userFullname", this.getUserFullName());
        headers.put("userEmail", this.getUserEmail());
        headers.put("gcube-user-scope", this.getScope());
        headers.put("Content-Type", "application/x-www-form-urlencoded");
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
