package gr.cite.geoanalytics.dataaccess.geoserverbridge.geoservermanager.geowebcache;

import com.fasterxml.jackson.databind.ObjectMapper;

import gr.cite.geoanalytics.context.GeoServerBridgeConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Service
public class GwcClient {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected String service;
    private Client client;

    private GeoServerBridgeConfig geoServerBridgeConfig;

    private String geoserverEndpoint;
    private String user;
    private String password;

    private static final ObjectMapper mapper = new ObjectMapper();


    public GwcClient(GeoServerBridgeConfig geoServerBridgeConfig) {
        this.geoServerBridgeConfig = geoServerBridgeConfig;
        geoserverEndpoint = geoServerBridgeConfig.getGeoServerBridgeUrl();
        this.user = geoServerBridgeConfig.getGeoServerBridgeUser();
        this.password = geoServerBridgeConfig.getGeoServerBridgePassword();
        this.client = ClientBuilder.newClient();
        this.client.register( HttpAuthenticationFeature.basic(user, password));
        logger.debug("Initialized GwcClient");

//        HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("howtodoinjava", "password");

    }


    public boolean seedGeoTIFF(String seedRequest,String layerName) {
        logger.info("im sending to: " + geoserverEndpoint+"/gwc/rest/seed/"+ layerName +".json");
        Response response = client.target(geoserverEndpoint)
                .path("/gwc/rest/seed/"+ layerName +".json")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity( seedRequest, MediaType.APPLICATION_JSON_TYPE));
        logger.debug("Send seed request with response:"+ response.getStatus());
        if (response.getStatus() != 200 ) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatus());
        }
        return true;
    }

    public boolean deleteCachedLayer(String layerName) {
        Response response = client.target(geoserverEndpoint)
                .path("/gwc/rest/layers/"+layerName+".json")
                .request(MediaType.APPLICATION_JSON)
                .delete(Response.class);
        if (response.getStatus() != 200 ) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatus());
        }
        return true;
    }

    public Response getCachedLayers()  {
        Response response = client.target(geoserverEndpoint)
                .path("/gwc/rest/layers.xml")
                .request(MediaType.APPLICATION_XML)
                .get(Response.class);
        if(response.getStatus()==200) {
            return response;
        }
        else {
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatus());
        }
    }

}
