package gr.cite.geoanalytics.functions.production;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.spi.CachingProvider;

import org.gcube.data.simulfishgrowthdata.api.base.ScenarioUtil;
import org.gcube.data.simulfishgrowthdata.util.HibernateUtil;
import org.hibernate.Session;

import com.fasterxml.jackson.databind.ObjectMapper;

import gr.cite.bluebridge.analytics.model.Consumption;
import gr.cite.geoanalytics.environmental.data.retriever.OxygenRetriever;
import gr.cite.geoanalytics.environmental.data.retriever.TemperatureRetriever;

public class ProductionEvaluator {

	private static final ObjectMapper mapper = new ObjectMapper();

	private final Integer upToGrade = 4;
	private final Integer acceptableCount = 1;

	private final Integer weight = 217;
	private final Integer count = 750000;
	
	private boolean useCache = false;
	private String cacheId = null; 
	private Cache<String, String> cache = null;
	
	private TemperatureRetriever temperatureRetriever = null;
	private OxygenRetriever oxygenRetriever = null;
	
	public ProductionEvaluator(boolean useCache) throws Exception {
		this.useCache = useCache;
		if (useCache) {
			cacheId = UUID.randomUUID().toString();
			CachingProvider provider = Caching.getCachingProvider();
			CacheManager cacheManager = provider.getCacheManager();
			MutableConfiguration<String, String> configuration = new MutableConfiguration<String, String>()
					.setTypes(String.class, String.class)
					.setStoreByValue(false)
					.setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(Duration.ETERNAL));
			cache = cacheManager.createCache(cacheId, configuration);
			temperatureRetriever = new TemperatureRetriever();
			oxygenRetriever = new OxygenRetriever();
		}
	}
	
	public void destroy() {
		if (useCache) {
			CachingProvider provider = Caching.getCachingProvider();
			CacheManager cacheManager = provider.getCacheManager();
			cacheManager.destroyCache(cacheId);
			cache = null;
			temperatureRetriever = null;
			oxygenRetriever = null;
		}
	}

	final public static String additionalSimilarityConstraint = "(ownerid not like '%TrainingLab%') and (ownerid not like '%junit%')";
	
	public Consumption getConsumptionFromSimulFishGrowthData(String tenant, Long speciesId, double x, double y, int maturityMonths)
			throws Exception {
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

			String longitude = x + "";
			String latitude = y + "";

			ScenarioUtil scenario = new ScenarioUtil();
			if (cache != null) scenario.setCache(cache);
			if (temperatureRetriever != null) scenario.setTemperatureRetriever(temperatureRetriever);
			if (oxygenRetriever != null) scenario.setOxygenRetriever(oxygenRetriever);
			scenario.setAdditionalSimilarityConstraint(additionalSimilarityConstraint);
			consumptionString = scenario.executeConsumptionScenario(session, fromDate, toDate, weight, count, latitude, longitude, speciesId, acceptableCount, upToGrade);

			session.getTransaction().commit();
		} catch (Exception e) {
			throw new Exception("Failed to execute consumption scenario for long lat [" + x + ", " + y + "]", e);
		} finally {
			HibernateUtil.closeSession(session);
		}

		Consumption consumption = null;
		try {
			consumption = mapper.readValue(consumptionString, Consumption.class);
		} catch (Exception e) {
			throw new Exception("Failed to retrieve consumption for long lat [" + x + ", " + y + "]", e);
		}

		return consumption;
	}
}
