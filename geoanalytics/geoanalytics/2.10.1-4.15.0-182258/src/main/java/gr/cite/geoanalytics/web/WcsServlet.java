package gr.cite.geoanalytics.web;

import com.google.inject.Inject;
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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static gr.cite.geoanalytics.dataaccess.entities.layer.dao.LayerVisualizationDaoImpl.log;

@RestController
public class WcsServlet extends CustomServlet {
    private Logger logger = LoggerFactory.getLogger(WcsServlet.class);

    @Inject
    public WcsServlet(TrafficShaper trafficShaper, SocialNetworkingService socialNetworkingService) {
        super(trafficShaper, socialNetworkingService);
        this.initializeSupportedOperationsMap();
    }

    @Override
    void initializeSupportedOperationsMap() {
        operrationsSupportedByTheProtocol = new HashMap<>();
        operrationsSupportedByTheProtocol.put("GetCoverage".toLowerCase(), true);
        operrationsSupportedByTheProtocol.put("GetCapabilities".toLowerCase(), true);
        operrationsSupportedByTheProtocol.put("DescribeCoverage".toLowerCase(), true);
    }

    @Override
    @RequestMapping(value = { "/wcs" }, method = RequestMethod.GET)
    public @ResponseBody
    void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
                throw new IOException("Could not find any available geoserver to serve the WCS call for layer: " + layerId);

            if(operationName.toLowerCase().equalsIgnoreCase("GetCapabilities"))
                this.relayGetCapabilities(request, response, layerId, geoserverEndpoint);

            if(operationName.toLowerCase().equalsIgnoreCase("GetCoverage"))
                this.relayGetCoverage(request, response, layerId, geoserverEndpoint);

            if(operationName.toLowerCase().equalsIgnoreCase("DescribeCoverage"))
                this.relayDescribeCoverage(request, response, layerId, geoserverEndpoint);

        } catch (NoAvailableLayer noAvailableLayer) {
            String message ="A layer with name: " + layerId + " does not exist in the infrastructure!!!";
            this.returnCustomErrorResponse(response, message, HttpStatus.INTERNAL_SERVER_ERROR);
            logger.error(message);
        } catch (CustomException e) {
            logger.error(e.getMessage());
            this.returnCustomErrorResponse(response, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    void relayGetCapabilities(HttpServletRequest request, HttpServletResponse response, String layerId, String geoserverEndpoint) throws CustomException, IOException {
        //WCS 1.0.0 params
        Set<String> mandatoryParameters = Stream.of("SERVICE", "REQUEST").map(param -> param.toLowerCase()).collect(Collectors.toSet());
        Set<String> optionalParameters = Stream.of("VERSION").map(param -> param.toLowerCase()).collect(Collectors.toSet());

        String describeCoverageURL = this.buildFinalURL(request, geoserverEndpoint, "wcs", mandatoryParameters, optionalParameters, null);

        Response geoserverResponse = this.connect(describeCoverageURL, this.getFormat(), null);

        String clientResponse = this.buildResponseForClientFromGeoserverResponse(geoserverResponse, response);

        response.getWriter().println(clientResponse);
    }

    void relayGetCoverage(HttpServletRequest request, HttpServletResponse response, String layerId, String geoserverEndpoint) throws CustomException, IOException {
        Set<String> mandatoryParameters = Stream.of("VERSION", "SERVICE", "COVERAGE", "REQUEST", "CRS", "BBOX", "WIDTH", "HEIGHT", "FORMAT").map(param -> param.toLowerCase()).collect(Collectors.toSet());
        Set<String> optionalParameters = Stream.of("RESPONSE_CRS","INTERPOLATION", "EXCEPTIONS").map(param -> param.toLowerCase()).collect(Collectors.toSet());

        String describeCoverageURL = this.buildFinalURL(request, geoserverEndpoint, "wcs", mandatoryParameters, optionalParameters, null);

        Response geoserverResponse = this.connect(describeCoverageURL, this.getFormat(), null);

        String clientResponse = this.buildResponseForClientFromGeoserverResponse(geoserverResponse, response);

        response.getWriter().println(clientResponse);
    }

    void relayDescribeCoverage(HttpServletRequest request, HttpServletResponse response, String layerId, String geoserverEndpoint) throws IOException, CustomException {
        //WCS 1.0.0 params
        Set<String> mandatoryParameters = Stream.of("SERVICE", "REQUEST", "COVERAGE", "VERSION").map(param -> param.toLowerCase()).collect(Collectors.toSet());
        Map<String, String> interchangeableParameters = new HashMap<>();
        interchangeableParameters.put("COVERAGE".toLowerCase(), "COVERAGEID".toLowerCase());

        String describeCoverageURL = this.buildFinalURL(request, geoserverEndpoint, "wcs", mandatoryParameters, null, interchangeableParameters);

        Response geoserverResponse = this.connect(describeCoverageURL, this.getFormat(), null);

        String clientResponse = this.buildResponseForClientFromGeoserverResponse(geoserverResponse, response);

        response.getWriter().println(clientResponse);
    }
}
