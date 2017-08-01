package org.gcube.data.simulfishgrowthdata.api.base;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.gcube.data.simulfishgrowthdata.model.GlobalModelWrapper.IEnvValuesProvider;
import org.gcube.data.simulfishgrowthdata.util.HibernateUtil;
import org.hibernate.Session;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import gr.i2s.fishgrowth.model.Fcr;
import gr.i2s.fishgrowth.model.Modeler;
import gr.i2s.fishgrowth.model.Mortality;
import gr.i2s.fishgrowth.model.Scenario;
import gr.i2s.fishgrowth.model.ScenarioFull;
import gr.i2s.fishgrowth.model.Sfr;
import gr.i2s.fishgrowth.model.Site;
import junit.framework.TestCase;

public class ScenarioUtilTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
		String dbEndpointName = "SimulFishGrowth";
		String scope = "/gcube/preprod/preECO";

		HibernateUtil.configGently(dbEndpointName, scope);
	}

	public void testScenario() throws Exception {
		long modelerId = 97;
		ScenarioFull toAdd = new ScenarioFull();
		toAdd.setDesignation("junit test");
		toAdd.setOwnerId("junit");
		toAdd.setModelerId(modelerId);
		toAdd.setStatusId(ModelerUtil.STATUS_PENDING_KPI);

		ScenarioFullUtil util = new ScenarioFullUtil();
		Scenario pong = util.add(toAdd);
		assertNotNull("add entity", pong);
		long testId = pong.getId();
		pong = new ScenarioUtil().getScenario(testId);
		assertNotNull("get entity", pong);
		pong = util.getScenarioFull(testId);
		assertNotNull("get entity full", pong);
		assertEquals("modelerId", modelerId, pong.getModelerId());
		assertTrue("delete entity", util.delete(testId));

	}

	/**
	 * create site and model infrastructure and then create and execute scenario
	 * 
	 * @throws Exception
	 */
	public void testScenarioInfratructureFromScratch() throws Exception {
		Double startWeight = 2.5;
		Integer startPopulation = 50000;

		Session session = null;
		try {
			session = HibernateUtil.openSession();
			session.beginTransaction();

			// prepare sites and models. Get the first modelId given
			Long modelerId = null;
			List<Modeler> modelers = ModelerFullUtilTest.prepareModelsInfrastructureFromScratch(session,
					ModelerFullUtilTest.prepareSitesInfrastructureFromScratch(session, 5));
			if (modelers != null && !modelers.isEmpty()) {
				modelerId = modelers.get(0).getId();
			}
			if (modelerId != null) {
				// makeup the scenario
				ScenarioFull toAdd = new ScenarioFull();
				toAdd.setDesignation("junit test");
				toAdd.setOwnerId("junit");
				toAdd.setModelerId(modelerId);
				toAdd.setFishNo(startPopulation);
				toAdd.setWeight(startWeight);
				toAdd.setStatusId(ModelerUtil.STATUS_FAILED_KPI);
				GregorianCalendar start = new GregorianCalendar(2017, 0, 1);
				toAdd.setStartDate(start.getTime());
				GregorianCalendar target = (GregorianCalendar) start.clone();
				target.add(GregorianCalendar.MONTH, 1);
				toAdd.setTargetDate(target.getTime());

				// prepare
				ScenarioFullUtil util = new ScenarioFullUtil();
				Scenario pong = util.add(session, toAdd);
				assertNotNull("add entity", pong);
				long testId = pong.getId();
				pong = util.getScenarioFull(session, testId);
				assertNotNull("get entity full", pong);
				assertEquals("modelerId", modelerId, Long.valueOf(pong.getModelerId()));

				// plain execution
				Scenario executed = new ScenarioUtil().executeScenario(session, testId);
				assertNotNull("executed plain", executed);
				assertTrue("plain final weight", executed.getResultsWeight() > startWeight);
				double plainWeight = executed.getResultsWeight();

				// global model execution
				executed = new ScenarioUtil().executeScenario(session, testId, ScenarioUtil.KIND_EXECUTOR_GLOBAL_MODEL);
				assertNotNull("executed global model", executed);
				assertTrue("global model final weight", executed.getResultsWeight() > startWeight);
				double globalWeight = executed.getResultsWeight();

				assertFalse("final weight should differ", plainWeight == globalWeight);

				// clean
				// assertTrue("delete entity", util.delete(session, testId));
				// assertNull("delete entity double check", new
				// ScenarioUtil().getScenario(session, testId));
			}

			session.getTransaction().rollback();
		} finally {
			HibernateUtil.closeSession(session);
		}

	}

	public void testConsumptionInfratructureFromScratch() throws Exception {
		Session session = null;
		try {
			session = HibernateUtil.openSession();
			session.beginTransaction();

			// prepare sites and models. Get the first modelId given
			Long modelerId = null;
			List<Modeler> modelers = ModelerFullUtilTest.prepareModelsInfrastructureFromScratch(session,
					ModelerFullUtilTest.prepareSitesInfrastructureFromScratch(session, null));
			if (modelers != null && !modelers.isEmpty()) {
				modelerId = modelers.get(0).getId();
			}
			if (modelerId != null) {

				// yymmdd
				String from = "170101";
				String to = "170201";
				// grams*100 eg 205 -> 2.05 gr
				Integer weight = 205;
				Integer count = 1000000;
				String results = new ScenarioUtil().executeConsumptionScenario(session, from, to, weight, count,
						modelerId);
				assertNotNull(results);
				assertFalse(StringUtils.isEmpty(results));
				System.out.println("results: " + results);
				parse(results);
			}

			session.getTransaction().rollback();
		} finally {
			HibernateUtil.closeSession(session);
		}

	}

	public void testGeolocationInfratructureFromScratch() throws Exception {
		// run having a hibernate session
		Session session = null;
		try {
			session = HibernateUtil.openSession();
			session.beginTransaction();
			// prepare sites and models. These have temperature matching the
			// scenario
//			ModelerFullUtilTest.prepareModelsInfrastructureFromScratch(session,
//					ModelerFullUtilTest.prepareSitesInfrastructureFromScratch(session, null));
			{// we have a hibernate session
				// yymmdd
				String from = "160606";
				String to = "160707";
				// grams*100 eg 205 -> 2.05 gr
				Integer weight = 205;
				Integer count = 1000000;
				String latitude = "37.1"; //"37.97884504049713";
				String longitude = "23.7"; //"23.781005926430225";
				Long speciesId = 2L;
				final Integer acceptableSiteCount = 4;
				final Integer upToGrade = 5;
				// if we are out of a session we use the alternative that is
				// missing the session parameter
				String results = new ScenarioUtil().executeConsumptionScenario(session, from, to, weight, count,
						latitude, longitude, speciesId, acceptableSiteCount, upToGrade);
				assertNotNull(results);
				assertFalse(StringUtils.isEmpty(results));
				System.out.println("results: " + results);
				parse(results);
			}

			session.getTransaction().rollback();
		} finally {
			HibernateUtil.closeSession(session);
		}

		if (false) { // I don't want to test it
			// without a hibernate session
			// yymmdd
			String from = "170101";
			String to = "170201";
			// grams*100 eg 205 -> 2.05 gr
			Integer weight = 205;
			Integer count = 1000000;
			String latitude = "0";
			String longitude = "0";
			Long speciesId = 2L;
			final Integer acceptableSiteCount = 4;
			final Integer upToGrade = 4;
			System.out.println("Starting execution");
			long start = System.currentTimeMillis();
			String results = new ScenarioUtil().executeConsumptionScenario(from, to, weight, count, latitude, longitude,
					speciesId, acceptableSiteCount, upToGrade);
			long end = System.currentTimeMillis();
			System.out.println("Execution took " + (end - start));

			assertNotNull(results);
			assertFalse(StringUtils.isEmpty(results));
			System.out.println("results: " + results);
			parse(results);
		}

	}

	public void testGlobalModelConsumptionExecutor() throws Exception {
		// yymmdd
		String from = "170101";
		String to = "170201";
		// grams*100 eg 205 -> 2.05 gr
		Integer weight = 205;
		Integer count = 1000000;
		String latitude = "0";
		String longitude = "0";
		Long speciesId = 0L;
		final Integer acceptableSiteCount = 4;
		final Integer upToGrade = 4;
		String results = new ScenarioUtil().executeConsumptionScenario(from, to, weight, count, latitude, longitude,
				speciesId, acceptableSiteCount, upToGrade);
		assertNotNull(results);
		assertFalse(StringUtils.isEmpty(results));
		System.out.println("results: " + results);
		parse(results);
	}

	private void parse(String results) {
		JsonElement rootElement = new JsonParser().parse(results);
		assertNotNull(rootElement);
		JsonObject root = rootElement.getAsJsonObject();
		assertNotNull(root);

		{
			int testdays = 31 + 1;
			JsonArray daily = root.getAsJsonArray("daily");
			assertNotNull(daily);
			assertTrue(daily.size() == testdays);

			for (int dayCount = 0; dayCount < testdays; dayCount++) {
				JsonElement todayElement = daily.get(dayCount);
				assertNotNull(todayElement);
				assertTrue(todayElement.isJsonObject());
				JsonObject today = todayElement.getAsJsonObject();
				assertNotNull(today);
				String day = today.get("day").getAsString();
				assertTrue(StringUtils.isNotEmpty(day));
				Long bm = today.get("bm").getAsLong();
				assertTrue(bm >= 0);
				Double fcre = today.get("fcre").getAsDouble();
				assertTrue(fcre >= 0);
				Double fcrb = today.get("fcrb").getAsDouble();
				assertTrue(fcrb >= 0);
				Double food = today.get("food").getAsDouble();
				assertTrue(food >= 0);
				Long bmdead = today.get("bmdead").getAsLong();
				assertTrue(bmdead >= 0);
				Double mortality = today.get("mortality").getAsDouble();
				assertTrue(mortality >= 0);
			}
		}

		{
			int testmonths = 2;
			JsonArray monthly = root.getAsJsonArray("monthly");
			assertNotNull(monthly);
			assertTrue(monthly.size() == testmonths);

			for (int monthCount = 0; monthCount < testmonths; monthCount++) {
				JsonElement monthElement = monthly.get(monthCount);
				assertNotNull(monthElement);
				assertTrue(monthElement.isJsonObject());
				JsonObject thisMonth = monthElement.getAsJsonObject();
				assertNotNull(thisMonth);
				String month = thisMonth.get("month").getAsString();
				assertTrue(StringUtils.isNotEmpty(month));
				Double food = thisMonth.get("food").getAsDouble();
				assertTrue(food >= 0);
			}
		}

	}

	private IEnvValuesProvider createTestOxygenProvider() {
		return new IEnvValuesProvider() {

			@Override
			public Integer[] getValues(String latitiude, String longitude) {

				Integer[] toRet = new Integer[24];
				for (int i = 0; i < 24; i++) {
					toRet[i] = 20;
				}
				return toRet;
			}
		};
	}

	private IEnvValuesProvider createTestTempProvider() {
		return new IEnvValuesProvider() {

			@Override
			public Integer[] getValues(String latitude, String longitude) {
				Integer[] toRet = new Integer[24];
				int i = 0;
				toRet[i++] = 15;
				toRet[i++] = 15;
				toRet[i++] = 15;
				toRet[i++] = 15;
				toRet[i++] = 16;
				toRet[i++] = 16;
				toRet[i++] = 16;
				toRet[i++] = 16;
				toRet[i++] = 16;
				toRet[i++] = 16;
				toRet[i++] = 17;
				toRet[i++] = 17;
				toRet[i++] = 17;
				toRet[i++] = 17;
				toRet[i++] = 17;
				toRet[i++] = 17;
				toRet[i++] = 18;
				toRet[i++] = 18;
				toRet[i++] = 18;
				toRet[i++] = 18;
				toRet[i++] = 18;
				toRet[i++] = 18;
				toRet[i++] = 15;
				toRet[i++] = 15;
				return toRet;
			}
		};

	}

	public void debugging() throws Exception {
		Session session = null;
		try {
			session = HibernateUtil.openSession();
			session.beginTransaction();

			Scenario result = new ScenarioUtil().executeScenario(session, 5519L);

			session.getTransaction().commit();
		} finally {
			HibernateUtil.closeSession(session);
		}

	}

}
