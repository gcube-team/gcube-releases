package gr.cite.geoanalytics.web;

import gr.cite.clustermanager.exceptions.NoAvailableLayer;
import gr.cite.clustermanager.trafficshaping.TrafficShaper;
import gr.cite.geoanalytics.util.http.CustomException;
import gr.cite.geoanalytics.web.auth.ServiceDiscovery;
import gr.cite.geoanalytics.web.auth.SocialNetworkingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static gr.cite.geoanalytics.dataaccess.entities.layer.dao.LayerVisualizationDaoImpl.log;

@RestController
public class WfsServlet extends CustomServlet {
    private Logger logger = LoggerFactory.getLogger(WfsServlet.class);
    //Dependencies

    @Inject
    public WfsServlet(TrafficShaper trafficShaper, SocialNetworkingService socialNetworkingService) {
        super(trafficShaper, socialNetworkingService);
        this.initializeSupportedOperationsMap();
    }

    @Override
    @RequestMapping(value = { "/wfs" }, method = RequestMethod.GET)
    public @ResponseBody void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("Request reached endpoint");

        this.handleDoGet(request, response, "request");

    }

    @Override
    void relay(HttpServletRequest request, HttpServletResponse response, String operationName) throws IOException {
        String layerId = this.getCaseInsensitiveParameter("layername", request);

        try {

            if(layerId == null || layerId.trim().equals(""))
                throw new NoAvailableLayer("Layer ID is empty");

            String geoserverEndpoint = trafficShaper.getAppropriateGosForLayer(layerId).getGeoserverEndpoint();

            if (geoserverEndpoint == null && !geoserverEndpoint.trim().equals(""))
                throw new IOException("Could not find any available geoserver to serve the WMS call for layer: " + layerId);

            if(operationName.toLowerCase().equalsIgnoreCase("GetFeature"))
                this.relayGetFeature(request, response, layerId, geoserverEndpoint);

            if(operationName.toLowerCase().equalsIgnoreCase("GetCapabilities"))
                this.relayGetCapabilities(request, response, layerId, geoserverEndpoint);

            if(operationName.toLowerCase().equalsIgnoreCase("DescribeFeatureType"))
                this.relayDescribeFeature(request, response, layerId, geoserverEndpoint);

        } catch (NoAvailableLayer noAvailableLayer) {
            String message ="A layer with name: " + layerId + " does not exist in the infrastructure!!!";
            this.returnCustomErrorResponse(response, message, HttpStatus.INTERNAL_SERVER_ERROR);
            logger.error(message);
        } catch (CustomException e) {
            logger.error(e.getMessage());
            this.returnCustomErrorResponse(response, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    private void relayGetFeature(HttpServletRequest request, HttpServletResponse response, String layerId, String geoserverEndpoint) throws IOException, CustomException {
        //WFS 1.0.0 params
        Set<String> mandatoryParameters = Stream.of("VERSION", "SERVICE", "TYPENAME", "REQUEST").map(param -> param.toLowerCase()).collect(Collectors.toSet());
        Set<String> optionalParameters = Stream.of("PROPERTYNAME", "FEATUREVERSION", "MAXFEATURES", "FEATUREID", "FILTER", "OUTPUTFORMAT", "BBOX", "EXCEPTIONS")
                .map(param -> param.toLowerCase()).collect(Collectors.toSet());

        String getCapabilitiesURL = this.buildFinalURL(request, geoserverEndpoint, "wfs", mandatoryParameters, optionalParameters, null);

        Response geoserverResponse = this.connect(getCapabilitiesURL, this.getFormat(), null);

        String clientResponse = this.buildResponseForClientFromGeoserverResponse(geoserverResponse, response);

        response.getWriter().println(clientResponse);
    }

    private void relayGetCapabilities(HttpServletRequest request, HttpServletResponse response, String layerId, String geoserverEndpoint) throws CustomException, IOException {
        //WFS 1.0.0 params
        Set<String> mandatoryParameters = Stream.of("SERVICE", "REQUEST").map(param -> param.toLowerCase()).collect(Collectors.toSet());
        Set<String> optionalParameters = Stream.of("VERSION").map(param -> param.toLowerCase()).collect(Collectors.toSet());
        Set<String> requiredParameters = new HashSet<>(mandatoryParameters);

        String getCapabilitiesURL = this.buildFinalURL(request, geoserverEndpoint, "wfs", mandatoryParameters, optionalParameters, null);

        Response geoserverResponse = this.connect(getCapabilitiesURL, this.getFormat(), null);

        String clientResponse = this.buildResponseForClientFromGeoserverResponse(geoserverResponse, response);

        response.getWriter().println(clientResponse);
    }

    private void relayDescribeFeature(HttpServletRequest request, HttpServletResponse response, String layerId, String geoserverEndpoint) throws IOException, CustomException {
        Set<String> mandatoryParameters = Stream.of("SERVICE", "VERSION", "request", "typeName").map(par -> par.toLowerCase()).collect(Collectors.toSet());
        Set<String> optionalParameters = Stream.of("exceptions", "outputFormat").map(par -> par.toLowerCase()).collect(Collectors.toSet());
        Map<String, String> interchangeableParameters = new HashMap<>();
        interchangeableParameters.put("typeName", "typeNames".toLowerCase());

        String describeCoverageURL = this.buildFinalURL(request, geoserverEndpoint, "wfs", mandatoryParameters, optionalParameters, interchangeableParameters);

        Response geoserverResponse = this.connect(describeCoverageURL, this.getFormat(), null);

        String clientResponse = this.buildResponseForClientFromGeoserverResponse(geoserverResponse, response);

        response.getWriter().println(clientResponse);
    }

    @Override
    protected void initializeSupportedOperationsMap() {
        operrationsSupportedByTheProtocol = new HashMap<>();
        operrationsSupportedByTheProtocol.put("GetFeature".toLowerCase(), true);
        operrationsSupportedByTheProtocol.put("GetCapabilities".toLowerCase(), true);
        operrationsSupportedByTheProtocol.put("DescribeFeatureType".toLowerCase(), true);
        operrationsSupportedByTheProtocol.put("LockFeature".toLowerCase(), false);
        operrationsSupportedByTheProtocol.put("Transaction".toLowerCase(), false);
    }
}
