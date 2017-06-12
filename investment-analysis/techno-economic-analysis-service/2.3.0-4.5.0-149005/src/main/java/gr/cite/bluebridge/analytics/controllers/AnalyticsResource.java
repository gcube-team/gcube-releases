package gr.cite.bluebridge.analytics.controllers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.fasterxml.jackson.databind.ObjectMapper;

import gr.cite.bluebridge.analytics.logic.Evaluator;
import gr.cite.bluebridge.analytics.model.Consumption;
import gr.cite.bluebridge.analytics.model.Economics;
import gr.cite.bluebridge.analytics.model.Fish;
import gr.cite.bluebridge.analytics.model.FryGeneration;
import gr.cite.bluebridge.analytics.model.ModelInput;
import gr.cite.bluebridge.analytics.web.CustomResponseEntity;
import gr.cite.bluebridge.analytics.web.Parameters;
import gr.cite.bluebridge.analytics.web.SingletonHttpClient;
import gr.cite.bluebridge.endpoint.EndpointManager;
import gr.cite.bluebridge.endpoint.ServiceProfile;
import gr.cite.bluebridge.endpoint.exceptions.ServiceDiscoveryException;

@Controller
@RequestMapping("/")
public class AnalyticsResource {

	@Autowired	private ServiceProfile simulFishGrowthDataAPI;
	@Autowired	private SingletonHttpClient singletonHttpClient;
	@Autowired	private EndpointManager endpointManager;

	private static Logger logger = LoggerFactory.getLogger(AnalyticsResource.class);

	private static ObjectMapper mapper = new ObjectMapper();

	@RequestMapping(value = { "performAnalysis" }, method = RequestMethod.POST)
	public ResponseEntity<?> performAnalysis(@RequestBody Parameters parameters, @RequestHeader(value = "scope") String scope, @RequestHeader(value = "gcube-token") String token) {
		String invalidParameters = parameters.validate();

		if (invalidParameters.length() > 0) {
			logger.error(invalidParameters);
			return new ResponseEntity<>(invalidParameters, HttpStatus.BAD_REQUEST);
		}

		try {
			List<String> endpoints = endpointManager.getServiceEndpoints(scope, simulFishGrowthDataAPI);

			Map<String, Object> headers = new HashMap<>();
			headers.put("scope", scope);
			headers.put("gcube-token", token);

			Integer status = null;
			Response clientResponse = null;

			for (String endpoint : endpoints) {

				// Calculation of date to match the SimulFishGrowthDataAPI

				Calendar calendar = Calendar.getInstance();
				
				String fromDate = (calendar.get(Calendar.YEAR) + 1) + "0101";
				fromDate = fromDate.substring(2, fromDate.length());

				SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd", Locale.ENGLISH);
				calendar.setTime(sdf.parse(fromDate));
				calendar.add(Calendar.MONTH, parameters.getMaturity());
				
				Date date = calendar.getTime();
				String toDate = sdf.format(date);

				String resource = endpoint + "Scenario/execute/consumption/" + fromDate + "/" + toDate + "/217/750000/" + parameters.getModelId();

				logger.debug("Endpoint Url: " + endpoint);

				try {
					clientResponse = singletonHttpClient.doGet(resource, headers);
					status = clientResponse.getStatus();
				} catch (Exception e) {
					status = singletonHttpClient.exceptionHandler(e);
					endpointManager.removeServiceEndpoint(scope, simulFishGrowthDataAPI, endpoint);
					logger.warn("Cannot reach endpoint : " + status, e);
				}

				if (status == 200) {
					break;
				}
			}

			if (status != 200) {
				logger.warn("SimulfishGrowthDataAPI returned status " + status + " while requesting consumption");
				return new CustomResponseEntity<>(status, "Could not perform analysis. Service is down or has rejected the request.");
			} else {
				String consumptionString = clientResponse.readEntity(String.class);
				Consumption consumption = mapper.readValue(consumptionString, Consumption.class);

				ModelInput input = new ModelInput();

				Fish giltheadSeaBream = new Fish();
				giltheadSeaBream.setFish(parameters.getFishSpecies());
				giltheadSeaBream.setInitialPrice(parameters.getSellingPrice());
				giltheadSeaBream.setMixPercent(100d);
				input.getFishes().add(giltheadSeaBream);

				input.setTaxRate(parameters.getTaxRate());
				input.setDiscountRate(parameters.getDiscountRate());
				input.setInflationRate(parameters.getInflationRate());
				input.setMaturity(parameters.getMaturity());
				input.setFeedPrice(parameters.getFeedPrice());
				input.setFryPrice(parameters.getFryPrice());
				input.setOffShoreAquaFarm(parameters.getIsOffShoreAquaFarm());
				input.setConsumption(consumption);

				Map<Integer, FryGeneration> generationsPerYear = new HashMap<>();
				generationsPerYear.put(1, new FryGeneration(750000, 2.17));
				generationsPerYear.put(4, new FryGeneration(750000, 2.17));
				generationsPerYear.put(7, new FryGeneration(750000, 2.17));
				generationsPerYear.put(10, new FryGeneration(750000, 2.17));
				input.setGenerationsPerYear(generationsPerYear);

				Economics economics = new Evaluator().calculate(input);
				economics.setParameters(parameters);

				logger.info("Successful request!");

				return new CustomResponseEntity<Economics>(Status.OK, economics);
			}
		} catch (ServiceDiscoveryException e) {
			logger.error("Could not perform analysis. SimulFishGrowthDataAPI could not be discovered", e);
			return new CustomResponseEntity<>("Could not perform analysis. Server Internal Error", Status.NOT_FOUND);
		} catch (Exception e) {
			logger.error("Could not perform analysis. Internal Error in Techno Economic Analysis Service", e);
			return new CustomResponseEntity<>("Could not perform analysis. Server Internal Error", Status.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = { "testAnalysis" }, method = RequestMethod.POST)
	public ResponseEntity<?> testAnalysis(@RequestBody Parameters parameters, @RequestHeader(value = "fromDate") String fromDate, @RequestHeader(value = "scope") String scope,
			@RequestHeader(value = "gcube-token") String token) {

		parameters.print();

		try {
			List<String> endpoints = endpointManager.getServiceEndpoints(scope, simulFishGrowthDataAPI);

			Map<String, Object> headers = new HashMap<>();
			headers.put("scope", scope);
			headers.put("gcube-token", token);

			Integer status = null;
			Response clientResponse = null;

			for (String endpoint : endpoints) {

				// Calculation of date to match the SimulFishGrowthDataAPI

				// String fromDate = "160101";

				Calendar cal = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd", Locale.ENGLISH);
				cal.setTime(sdf.parse(fromDate));
				cal.add(Calendar.MONTH, parameters.getMaturity());
				Date date = cal.getTime();

				String toDate = sdf.format(date);

				String resource = endpoint + "Scenario/execute/consumption/" + fromDate + "/" + toDate + "/217/750000/" + parameters.getModelId();

				// logger.debug("Endpoint Url: " + endpoint);

				try {
					clientResponse = singletonHttpClient.doGet(resource, headers);
					status = clientResponse.getStatus();
				} catch (Exception e) {
					status = singletonHttpClient.exceptionHandler(e);
					endpointManager.removeServiceEndpoint(scope, simulFishGrowthDataAPI, endpoint);
					// logger.warn("Cannot reach endpoint : " + status, e);
				}

				if (status == 200) {
					break;
				}
			}
			String consumptionString = clientResponse.readEntity(String.class);
			return new CustomResponseEntity<>(consumptionString, Status.OK);

		} catch (Exception e) {
			return new CustomResponseEntity<>("Error", Status.INTERNAL_SERVER_ERROR);
		}
	}
}