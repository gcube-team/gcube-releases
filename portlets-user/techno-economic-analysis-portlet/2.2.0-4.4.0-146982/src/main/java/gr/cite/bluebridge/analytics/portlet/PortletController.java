package gr.cite.bluebridge.analytics.portlet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

import org.gcube.common.portal.PortalContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import com.liferay.portal.util.PortalUtil;

import gr.cite.bluebridge.analytics.web.*;
import gr.cite.bluebridge.endpoint.*;
import gr.cite.bluebridge.endpoint.exceptions.*;

@Controller
@RequestMapping("VIEW")
public class PortletController {

	private static Logger logger = LoggerFactory.getLogger(PortletController.class);

	@Autowired private SingletonHttpClient singletonHttpClient;
	@Autowired private EndpointManager endpointManager;

	@Autowired private ServiceProfile simulFishGrowthDataAPI;
	@Autowired private ServiceProfile technoEconomicAnalysis;

	@RenderMapping
	public String viewHomePage(RenderRequest request, RenderResponse response) {
		return "analysis";
	}

	@ResourceMapping(value = "PerformAnalysis")
	public void performAnalysis(ResourceRequest request, ResourceResponse response, @RequestParam("parameters") Object parameters) {
		PortalContext pContext = PortalContext.getConfiguration();
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(request);
		String scope = pContext.getCurrentScope(httpServletRequest);
		String username = pContext.getCurrentUser(httpServletRequest).getUsername();
		String token = pContext.getCurrentUserToken(scope, username);

		logger.info("Performing analysis on scope \"" + scope + "\" with username \"" + username + "\"");

		try {
			List<String> endpoints = endpointManager.getServiceEndpoints(scope, technoEconomicAnalysis);

			Map<String, Object> headers = new HashMap<String, Object>();
			headers.put("scope", scope);
			headers.put("gcube-token", token);

			Integer status = null;
			Response clientResponse = null;

			for (String endpoint : endpoints) {
				String resource = endpoint + "performAnalysis";
				logger.info("Endpoint Url: " + endpoint);

				try {
					clientResponse = singletonHttpClient.doPost(resource, headers, parameters);
					status = clientResponse.getStatus();
				} catch (Exception e) {
					endpointManager.removeServiceEndpoint(scope, simulFishGrowthDataAPI, endpoint);
					logger.warn("Cannot reach endpoint : " + status, e);
				}

				if (status != null && status == 200) {
					break;
				}
			}

			String result = clientResponse.readEntity(String.class);

			if (status == 404 && result.contains("Tomcat")) {
				throw new Exception("Techno Economic Analysis service  discovered but Not Found");
			}

			PortletUtils.returnResponse(response, status, result);
		} catch (ServiceDiscoveryException e) {
			PortletUtils.returnResponseAsJson(response, HttpServletResponse.SC_NOT_FOUND, "Analysis failed! Portlet Internal Error");
			logger.error("Analysis failed! Techno Economic Analysis Service could not be discovered", e);
		} catch (Exception e) {
			PortletUtils.returnResponseAsJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Analysis failed! Portlet Internal Error");
			logger.error("Analysis failed due to server internal error", e);
		}
	}

	@ResourceMapping(value = "SimulFishGrowthDataModel")
	public void SimulFishGrowthDataModel(ResourceRequest request, ResourceResponse response) {
		PortalContext pContext = PortalContext.getConfiguration();
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(request);
		String scope = pContext.getCurrentScope(httpServletRequest);
		String username = pContext.getCurrentUser(httpServletRequest).getUsername();
		String token = pContext.getCurrentUserToken(scope, username);

		logger.info("Requesting models on scope \"" + scope + "\" with username \"" + username + "\"");

		try {
			List<String> endpoints = endpointManager.getServiceEndpoints(scope, simulFishGrowthDataAPI);

			Map<String, Object> headers = new HashMap<>();
			headers.put("scope", scope);
			headers.put("gcube-token", token);

			Integer status = null;
			Response clientResponse = null;

			for (String endpoint : endpoints) {
				String resource = endpoint + "ModelerFull/all/" + scope.replaceAll("/", "_") + "?status=1";
				logger.info("Endpoint Url: " + endpoint);

				try {
					clientResponse = singletonHttpClient.doGet(resource, headers);
					status = clientResponse.getStatus();
				} catch (Exception e) {
					status = singletonHttpClient.exceptionHandler(e);
					endpointManager.removeServiceEndpoint(scope, simulFishGrowthDataAPI, endpoint);
					logger.warn("Cannot reach endpoint : " + status, e);
				}

				if (status != null && status == 200) {
					break;
				}
			}

			String result = clientResponse.readEntity(String.class);

			if (status == 404 && result.contains("Tomcat")) {
				throw new Exception("SimulFishGrowthData discovered but Not Found");
			}

			PortletUtils.returnResponse(response, status, result);
		} catch (ServiceDiscoveryException e) {
			PortletUtils.returnResponseAsJson(response, HttpServletResponse.SC_NOT_FOUND, "Could not load Models. Portlet Internal Error");
			logger.error("Could not load Models. SimulFishGrowthData Endpoint could not be discovered", e);
		} catch (Exception e) {
			PortletUtils.returnResponseAsJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not load Models. Portlet Internal Error");
			logger.error("Could not load Models. Portlet Internal Error", e);
		}
	}
}