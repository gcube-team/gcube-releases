package gr.cite.geoanalytics.functions.functions;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.gcube.data.simulfishgrowthdata.api.base.ScenarioUtil;
import org.gcube.data.simulfishgrowthdata.util.HibernateUtil;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import gr.cite.bluebridge.analytics.logic.Evaluator;
import gr.cite.bluebridge.analytics.model.Consumption;
import gr.cite.bluebridge.analytics.model.Economics;
import gr.cite.bluebridge.analytics.model.Fish;
import gr.cite.bluebridge.analytics.model.FryGeneration;
import gr.cite.bluebridge.analytics.model.ModelInput;

public class NPVFunction implements Function, Serializable {

	private static final long serialVersionUID = -6915800403768631565L;

	private static final Logger logger = LoggerFactory.getLogger(NPVFunction.class);

	private String tenant;
	
	public static void main(String[] args) {
		
		long startTime = System.currentTimeMillis();
		
		
		
		String scope = "/gcube/preprod/preECO";
		final NPVFunction func = new NPVFunction(scope);
		double longitude = 23.7;
		double latitude = 37.9;

//		List<String> coords = new ArrayList<String>();
//		for(int i=0;i<100;i++)
//			coords.add("23.7,37.9");
		
		
		
		try {
//			coords.parallelStream().forEach(coord -> {
//				try {
//					func.execute(Double.parseDouble(coord.split(",")[0]), Double.parseDouble(coord.split(",")[1]));
//				} catch (Exception e) { e.getMessage();/*e.printStackTrace();*/}
//			});
			
			double result = func.execute(longitude, latitude);
			System.out.println(result);
			
		} catch (Exception e) {
			logger.error("Could not execute NPV function with lat long [" + latitude, ", " + longitude + "]", e);
		}
		
		
		System.out.println("Took: "+((double)(System.currentTimeMillis()-startTime)/1000));
	}

	public NPVFunction(String tenant){
		this.tenant = tenant;
	}
	
//	public NPVFunction setTenant(String tenant){
//		System.out.println("Setting tenant to "+tenant+ " on NPVFunction");
//		logger.debug("Setting tenant to "+tenant+ " on NPVFunction");
//		this.tenant = tenant;
//		return this;
//	}
	
	public double getDepreciatedNpv(double x, double y) throws Exception {
		Fish giltheadSeaBream = new Fish();
		giltheadSeaBream.setFish("Sea Bream");
		giltheadSeaBream.setInitialPrice(5.20d);
		giltheadSeaBream.setMixPercent(100d);

		ModelInput input = new ModelInput();

		input.getFishes().add(giltheadSeaBream);
		input.setTaxRate(29d);
		input.setDiscountRate(3.75d);
		input.setInflationRate("{ \"2018\" : \"0.65\" } ");
		input.setMaturity(18);
		input.setFeedPrice(1.15d);
		input.setFryPrice(0.2d);
		input.setOffShoreAquaFarm(true);

		Map<Integer, FryGeneration> generationsPerYear = new HashMap<>();
		generationsPerYear.put(1, new FryGeneration(750000, 2.17));
		generationsPerYear.put(4, new FryGeneration(750000, 2.17));
		generationsPerYear.put(7, new FryGeneration(750000, 2.17));
		generationsPerYear.put(10, new FryGeneration(750000, 2.17));
		input.setGenerationsPerYear(generationsPerYear);

//		String scope = "/gcube/preprod/preECO";

		Consumption consumption = getConsumptionFromSimulFishGrowthData(tenant, x, y, input.getMaturity());
		input.setConsumption(consumption);

		Economics economics = new Evaluator().calculate(input);

		return economics.getDepreciatedValues().getTargetIndicators().getNPV();
	}

	public Consumption getConsumptionFromSimulFishGrowthData(String tenant, double x, double y, int maturityMonths) throws Exception {
		String dbEndpoint = "SimulFishGrowth";

		HibernateUtil.configGently(dbEndpoint, tenant);
		String consumptionString = null;
		Session session = null;

		try {
			session = HibernateUtil.openSession();
			session.beginTransaction();

			// Calculation of date to match the SimulFishGrowthDataAPI

			Calendar calendar = Calendar.getInstance();

			String fromDate = (calendar.get(Calendar.YEAR) + 1) + "0101";
			fromDate = fromDate.substring(2, fromDate.length());

			SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd", Locale.ENGLISH);
			calendar.setTime(sdf.parse(fromDate));
			calendar.add(Calendar.MONTH, maturityMonths);

			Date date = calendar.getTime();
			String toDate = sdf.format(date);

			Integer weight = 217;
			Integer count = 750000;
			Long speciesId = 2L;
			String longitude = x + "";
			String latitude = y + "";

			consumptionString = new ScenarioUtil().executeConsumptionScenario(session, fromDate, toDate, weight, count, latitude, longitude, speciesId, 1, 5);

			session.getTransaction().rollback();
		} catch (Exception e) {
			throw new Exception("Failed to execute consumption scenario for long lat [" + x + ", " + y + "]", e);
		} finally {
			HibernateUtil.closeSession(session);
		}

		Consumption consumption = null;
		try {
			consumption = new ObjectMapper().readValue(consumptionString, Consumption.class);
		} catch (Exception e) {
			throw new Exception("Failed to retrieve consumption for long lat [" + x + ", " + y + "]", e);
		}

		return consumption;
	}

	@Override
	public double execute(double x, double y) throws Exception {
		double npv = 0;

		try {
			npv = getDepreciatedNpv(x, y);
		} catch (Exception e) {
			throw new Exception("Could not calculate NPV for long lat [" + x + ", " + y + "]", e);
		}

		return npv;
	}
}
