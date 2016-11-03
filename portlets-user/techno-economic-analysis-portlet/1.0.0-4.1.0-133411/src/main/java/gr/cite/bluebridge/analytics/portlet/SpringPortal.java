package gr.cite.bluebridge.analytics.portlet;

import gr.cite.bluebridge.analytics.utils.DatabaseCredentials;
import gr.cite.bluebridge.analytics.utils.DatabaseDiscovery;
import gr.cite.bluebridge.analytics.utils.Json;
import gr.cite.bluebridge.analytics.utils.ServiceDiscovery;
import gr.cite.bluebridge.analytics.utils.ServiceProfile;
import gr.cite.bluebridge.analytics.utils.SingletonHttpClient;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
 

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;

@Controller("helloWorldController")
@RequestMapping("VIEW")
public class SpringPortal {

	private static Log logger = LogFactoryUtil.getLog(SpringPortal.class);
	
	@Autowired
	private ServiceProfile performAnalysis;		
	
	@Autowired
	private ServiceProfile simulFishGrowthDataAPI;
	
	@Autowired
	private ServiceProfile  simulFishGrowthDatabase;
	
	@RenderMapping
	public String viewHomePage(RenderRequest request, RenderResponse response) {		
		logger.info("View Home Page");		
		ScopeHelper.setContext(request);
        return "view";
    }
	
	@ResourceMapping(value = "PerformAnalysis")
	public void performAnalysis(
			@RequestParam("modelId") Long modelId,
			@RequestParam("taxRate") Integer taxRate,
			@RequestParam("fishMix") Double fishMix,
			@RequestParam("feedPrice") Double feedPrice,
			@RequestParam("fryPrice") Double fryPrice,
			@RequestParam("sellingPrice") Double sellingPrice,
			@RequestParam("isOffShoreAquaFarm") Boolean isOffShoreAquaFarm,			
			ResourceRequest request, ResourceResponse response) {
		
		try {
			logger.info("Model Id: \"" + modelId + "\""); 
			logger.info("Tax Rate: \"" + taxRate + "\"");  
			logger.info("Fish Mix: \"" + fishMix + "\"");
			logger.info("Feed Price: \"" + feedPrice + "\"");  
			logger.info("Fry Price: \"" + fryPrice + "\""); 
			logger.info("Selling Price: \"" + sellingPrice + "\"");  
			logger.info("Is Off Shore Aqua Farm: \"" + isOffShoreAquaFarm+ "\"");
			
			String username = (String)request.getPortletSession().getAttribute(ScopeHelper.USERNAME_ATTRIBUTE, PortletSession.APPLICATION_SCOPE);
			logger.info("Current username: " + username);
			ASLSession session = SessionManager.getInstance().getASLSession(request.getPortletSession().getId(), username);
			
			String scope = session.getScope();
			String token = session.getSecurityToken();
			
			logger.info("Current scope: " + scope);
			logger.info("Current Token: " + token);	
						
			String serviceAnalysisUrl = ServiceDiscovery.fetchServiceEndpoint(scope, performAnalysis);
			
			Client client = SingletonHttpClient.getSingletonHttpClient().getClient();
			ClientResponse clientResponse = null;
			WebResource webResource = client.resource(serviceAnalysisUrl + "performAnalysis");	
			
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("modelId", modelId);
			map.put("fishMix", fishMix);
			map.put("taxRate", taxRate);
			map.put("feedPrice", feedPrice);
			map.put("fryPrice", fryPrice);
			map.put("sellingPrice", sellingPrice);
			map.put("isOffShoreAquaFarm", isOffShoreAquaFarm);
			
			String body = Json.buildJSON(map);
			
			clientResponse = webResource.
					accept("application/json").
					type(MediaType.APPLICATION_JSON).
					header("gcube-token", token).
					header("scope", scope).
					post(ClientResponse.class, body);
			
			Status status = clientResponse.getClientResponseStatus(); 
			String result = clientResponse.getEntity(String.class);	
			
			if (status != Status.OK) {
				logger.error("Did not manage to perform analysis " + result);				
				Map<String, Object> outputMap = new HashMap<String, Object>();
				Json.returnJson(response, outputMap);
				response.setProperty(ResourceResponse.HTTP_STATUS_CODE, Integer.toString(status.getStatusCode()));
			} else {
				Map<String, Object> outputMap = new ObjectMapper().readValue(result, Map.class);
				Json.returnJson(response, outputMap);
			}
		
		} catch (Exception e) {
			Json.returnJson(response, "Did not manage to perform analysis");			
			logger.error("Did not manage to perform analysis");
			logger.error(e);
			response.setProperty(ResourceResponse.HTTP_STATUS_CODE, Integer.toString(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
		}
	}
	
	@ResourceMapping(value = "SimulFishGrowthDataModel")
	public void SimulFishGrowthDataModel(ResourceRequest request, ResourceResponse response) {	
		String username = (String)request.getPortletSession().getAttribute(ScopeHelper.USERNAME_ATTRIBUTE, PortletSession.APPLICATION_SCOPE);
		ASLSession session = SessionManager.getInstance().getASLSession(request.getPortletSession().getId(), username);			
		String scope = session.getScope();
		String token = session.getSecurityToken();
		
		logger.info("Current username: " + username);
		logger.info("Current scope: " + scope);
		logger.info("Current Token: " + token);		
	
		try {
			// Fetch Database Credentials 
			
			DatabaseCredentials databaseCredentials = DatabaseDiscovery.fetchDatabaseCredentials(scope, simulFishGrowthDatabase);
			
			// Fetch Endpoint URL
			
			String serviceUrl = ServiceDiscovery.fetchServiceEndpoint(scope, simulFishGrowthDataAPI);
			serviceUrl = serviceUrl + "ModelerFull/all/" + scope.replaceAll("/", "_") + "?statuses=1";
			
			logger.info("Service Url: " + serviceUrl);
			
			// Models Request
			
			Client client = SingletonHttpClient.getSingletonHttpClient().getClient();
			ClientResponse clientResponse = null;
			WebResource webResource = client.resource(serviceUrl);	

			clientResponse = webResource.
								accept("application/json").
								type(MediaType.APPLICATION_JSON).
								header("gcube-token", token).
								header("scope", scope).
								header("dbname", databaseCredentials.getDbname()).
								header("dbuser", databaseCredentials.getDbuser()).
								header("dbhost", databaseCredentials.getDbhost()).
								header("dbpass", databaseCredentials.getDbpass()).
								get(ClientResponse.class);

			Status status = clientResponse.getClientResponseStatus();
			String result = clientResponse.getEntity(String.class);
			logger.info("result = " + result);

			logger.info("status = " + status.getStatusCode());
			if (status != Status.OK) {
				Json.returnJson(response,"Did not manage to get all models");
				logger.error("Did not manage to get all models");
				response.setProperty(ResourceResponse.HTTP_STATUS_CODE, Integer.toString(status.getStatusCode()));
			} else {
				PrintWriter writer = response.getWriter();
				writer.write(result);
				writer.close();		
			}
		} catch (Exception e) {
			Json.returnJson(response, "Could not find Endpoint");
			response.setProperty(ResourceResponse.HTTP_STATUS_CODE, Integer.toString(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
			logger.error(e);
		}
	}	
}
