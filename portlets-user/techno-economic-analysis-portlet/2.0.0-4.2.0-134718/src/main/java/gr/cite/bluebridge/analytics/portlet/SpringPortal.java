package gr.cite.bluebridge.analytics.portlet;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
 

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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

import gr.cite.bluebridge.analytics.discovery.DatabaseCredentials;
import gr.cite.bluebridge.analytics.discovery.DatabaseDiscovery;
import gr.cite.bluebridge.analytics.discovery.ServiceDiscovery;
import gr.cite.bluebridge.analytics.discovery.ServiceProfile;
import gr.cite.bluebridge.analytics.discovery.exceptions.DatabaseDiscoveryException;
import gr.cite.bluebridge.analytics.discovery.exceptions.ServiceDiscoveryException;
import gr.cite.bluebridge.analytics.web.Json;
import gr.cite.bluebridge.analytics.web.SingletonHttpClient;

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
        return "analysis";
    }
	
	@ResourceMapping(value = "PerformAnalysis")
	public void performAnalysis(
			@RequestParam("modelId") Long modelId,
			@RequestParam("maturity") Integer maturity,
			@RequestParam("taxRate") Double  taxRate,
			@RequestParam("discountRate") Double  discountRate,
			@RequestParam("fishMix") Double fishMix,
			@RequestParam("feedPrice") Double feedPrice,
			@RequestParam("fryPrice") Double fryPrice,
			@RequestParam("sellingPrice") Double sellingPrice,
			@RequestParam("isOffShoreAquaFarm") Boolean isOffShoreAquaFarm,	
			@RequestParam("inflationRate") Object inflationRate,
			ResourceRequest request, ResourceResponse response) {
		
/*		logger.info("Model Id: " + modelId ); 
		logger.info("Tax Rate: " + taxRate ); 
		logger.info("Discount Rate: " + discountRate);
		logger.info("Fish Mix: " + fishMix);
		logger.info("Feed Price: " + feedPrice);  
		logger.info("Fry Price: " + fryPrice); 
		logger.info("Selling Price: " + sellingPrice);  
		logger.info("Is Off Shore Aqua Farm: " + isOffShoreAquaFarm);
		logger.info("InflationRate: " + inflationRate);*/
		
		try {
			String username = (String)request.getPortletSession().getAttribute(ScopeHelper.USERNAME_ATTRIBUTE, PortletSession.APPLICATION_SCOPE);
			logger.info("Current username: " + username);
			ASLSession session = SessionManager.getInstance().getASLSession(request.getPortletSession().getId(), username);
			
			String scope = session.getScope();
			String token = session.getSecurityToken();
			
			logger.info("Current scope: " + scope);
			logger.info("Current Token: " + token);	
						
			String serviceAnalysisUrl = ServiceDiscovery.fetchServiceEndpoint(performAnalysis);
			
			Client client = SingletonHttpClient.getSingletonHttpClient().getClient();
			ClientResponse clientResponse = null;
			WebResource webResource = client.resource(serviceAnalysisUrl + "performAnalysis");	
			
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("modelId", modelId);
			map.put("maturity", maturity);
			map.put("fishMix", fishMix);
			map.put("taxRate", taxRate);
			map.put("discountRate", discountRate);
			map.put("feedPrice", feedPrice);
			map.put("fryPrice", fryPrice);
			map.put("sellingPrice", sellingPrice);
			map.put("isOffShoreAquaFarm", isOffShoreAquaFarm);
			map.put("inflationRate", inflationRate);
			
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
				result = (result== null) ? "Could not perform analysis. " : result;
				Json.returnJson(response, status + result);
				response.setProperty(ResourceResponse.HTTP_STATUS_CODE, Integer.toString(status.getStatusCode()));
				logger.error(status + result);				
			} else {
				Map<String, Object> outputMap = new ObjectMapper().readValue(result, Map.class);
				Json.returnJson(response, outputMap);
				logger.info("Analysis has been successful!");
			}		
		} catch (ServiceDiscoveryException e) {
			Json.returnJson(response, "Analysis failed!" + " Techno Economic Analysis Service could not be discovered");	
			response.setProperty(ResourceResponse.HTTP_STATUS_CODE, Integer.toString(HttpServletResponse.SC_NOT_FOUND));
		} catch (Exception e) {
			Json.returnJson(response, "Analysis failed!" + " Server Internal Error");	
			logger.error("Analysis failed due to server internal error");
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
			
			DatabaseCredentials databaseCredentials = DatabaseDiscovery.fetchDatabaseCredentials(simulFishGrowthDatabase);
			
			// Fetch Endpoint URL
			
			String serviceUrl = ServiceDiscovery.fetchServiceEndpoint(simulFishGrowthDataAPI);
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
			
			if (status != Status.OK) {
				result = (result== null) ? "Error occured in SimulFishGrowthData Service. " : result;
				Json.returnJson(response, status + result);
				response.setProperty(ResourceResponse.HTTP_STATUS_CODE, Integer.toString(status.getStatusCode()));
				logger.error(status + result);
			} else {
				PrintWriter writer = response.getWriter();
				writer.write(result);
				writer.close();
				logger.info("Request for models has been successful!");
			}		
		} catch (ServiceDiscoveryException e) {
			Json.returnJson(response, "Could not load Models. "  + "SimulFishGrowthData Service could not be discovered");	
			response.setProperty(ResourceResponse.HTTP_STATUS_CODE, Integer.toString(HttpServletResponse.SC_NOT_FOUND));
		} catch (DatabaseDiscoveryException e) {
			Json.returnJson(response, "Could not load Models. "  + "SimulFishGrowth Database could not be discovered");
			response.setProperty(ResourceResponse.HTTP_STATUS_CODE, Integer.toString(HttpServletResponse.SC_NOT_FOUND));
		}  catch (Exception e) {
			Json.returnJson(response, "Could not load Models. "  + "Portlet Internal Error");
			response.setProperty(ResourceResponse.HTTP_STATUS_CODE, Integer.toString(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
			logger.error(e);
		}
	}	
}
