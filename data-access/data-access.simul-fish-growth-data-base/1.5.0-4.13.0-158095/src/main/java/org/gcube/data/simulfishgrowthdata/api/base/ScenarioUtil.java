package org.gcube.data.simulfishgrowthdata.api.base;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.cache.Cache;

import org.gcube.data.simulfishgrowthdata.calc.ConsumptionScenarioExecutor;
import org.gcube.data.simulfishgrowthdata.calc.GlobalModelConsumptionExecutor;
import org.gcube.data.simulfishgrowthdata.calc.GlobalModelScenarioExecutor;
import org.gcube.data.simulfishgrowthdata.calc.ScenarioExecutor;
import org.gcube.data.simulfishgrowthdata.calc.WhatIfAnalysisExecutor;
import org.gcube.data.simulfishgrowthdata.model.GlobalModelWrapper;
import org.gcube.data.simulfishgrowthdata.util.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.cite.geoanalytics.environmental.data.retriever.OxygenRetriever;
import gr.cite.geoanalytics.environmental.data.retriever.TemperatureRetriever;
import gr.i2s.fishgrowth.model.Modeler;
import gr.i2s.fishgrowth.model.Scenario;

public class ScenarioUtil extends BaseUtil {
	private Cache<String, String> cache;
	private TemperatureRetriever temperatureRetriever;
	private OxygenRetriever oxygenRetriever;
	String additionalSimilarityConstraint = null;

	public ScenarioUtil setAdditionalSimilarityConstraint(String additionalSimilarityConstraint) {
		this.additionalSimilarityConstraint = additionalSimilarityConstraint;
		return this;
	}

	public ScenarioUtil setCache(Cache<String, String> cache) {
		this.cache = cache;
		return this;
	}

	public ScenarioUtil setTemperatureRetriever(TemperatureRetriever temperatureRetriever) {
		this.temperatureRetriever = temperatureRetriever;
		return this;
	}

	public ScenarioUtil setOxygenRetriever(OxygenRetriever oxygenRetriever) {
		this.oxygenRetriever = oxygenRetriever;
		return this;
	}

	public Scenario add(final Session session, final Scenario scenario) throws Exception {
		session.save(scenario);
		session.flush();
		return scenario;
	}

	public Scenario add(final Scenario scenario) throws Exception {
		Session session = null;

		try {
			session = HibernateUtil.openSession();

			session.beginTransaction();

			add(session, scenario);

			session.getTransaction().commit();
			return scenario;
		} catch (Exception e) {
			logger.info(String.format("Could not add scenario [%s]", scenario), e);
			throw new Exception(String.format("Could not add scenario [%s]", scenario), e);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	public Scenario update(final Scenario scenario) throws Exception {
		Session session = null;

		try {
			session = HibernateUtil.openSession();

			session.beginTransaction();

			update(session, scenario);

			session.getTransaction().commit();
			return scenario;
		} catch (Exception e) {
			logger.info(String.format("Could not update scenario [%s]", scenario), e);
			throw new Exception(String.format("Could not update scenario [%s]", scenario), e);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	public Scenario update(final Session session, final Scenario scenario) throws Exception {
		session.update(scenario);
		session.flush();
		return scenario;
	}

	public boolean delete(final Session session, final Long id) throws Exception {

		Scenario scenario = getScenario(session, id);

		if (scenario != null) {
			session.delete(scenario);
			session.flush();
		}
		return true;

	}

	public boolean delete(Long id) throws Exception {
		Session session = null;

		try {
			session = HibernateUtil.openSession();

			session.beginTransaction();
			delete(session, id);
			session.getTransaction().commit();
			return true;
		} catch (Exception e) {
			logger.info(String.format("Could not delete scenario [%s]", id), e);
			return false;
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	public Scenario getScenario(Long id) throws Exception {
		Session session = null;

		try {
			session = HibernateUtil.openSession();

			session.beginTransaction();

			Scenario scenario = getScenario(session, id);

			session.getTransaction().commit();

			return scenario;
		} catch (Exception e) {
			logger.error(String.format("Could not retrieve scenario [%s]", id), e);
			throw new Exception(String.format("Could not retrieve scenario [%s]", id), e);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	public Scenario getScenario(final Session session, final Long id) throws Exception {
		Scenario scenario = (Scenario) session.get(Scenario.class, Long.valueOf(id));
		return scenario;
	}

	public List<Scenario> getScenarios(String ownerId) throws Exception {
		Session session = null;

		try {
			logger.trace(String.format("start getScenarios"));
			session = HibernateUtil.openSession();
			logger.trace(String.format("session [%s]", session));

			session.beginTransaction();

			Query q = session.createQuery(_GET_ALL_ON_OWNERID).setParameter("ownerid", ownerId);

			List<Scenario> list = q.list();

			session.getTransaction().commit();

			logger.trace(String.format("return Scenarios %s", list));
			return list;
		} catch (Exception e) {
			logger.info(String.format("Could not retrieve scenarios for [%s]", ownerId), e);
			throw new Exception(String.format("Could not retrieve scenarios for [%s]", ownerId), e);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	/**
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Scenario executeScenario(final Long id) throws Exception {
		Session session = null;

		try {
			logger.trace(String.format("start getScenarios"));
			session = HibernateUtil.openSession();
			logger.trace(String.format("session [%s]", session));

			session.beginTransaction();

			Scenario toRet = executeScenario(session, id);

			session.getTransaction().commit();

			logger.trace(String.format("return Scenarios %s", toRet));
			return toRet;
		} catch (Exception e) {
			logger.info(String.format("Could not retrieve scenarios for [%s]", id), e);
			throw new Exception(String.format("Could not retrieve scenarios for [%s]", id), e);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	/**
	 * if session is null then everyone who wants a session is responsible to
	 * create and release it
	 * 
	 * @param session
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Scenario executeScenario(final Session session, final Long id) throws Exception {
		try {
			Scenario scenario = getScenario(session, id);
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("For [%s] I loaded [%s]", id, scenario));
			}
			new WhatIfAnalysisExecutor(session, scenario)
					.setAdditionalSimilarityConstraint(additionalSimilarityConstraint).run();
			return scenario;
		} catch (Exception e) {
			logger.info(String.format("Could not execute scenario [%s]", id), e);
			throw new Exception(String.format("Could not execute scenario [%s]", id), e);
		}
	}

	static final public int KIND_EXECUTOR_OWN_DATA = 1;
	static final public int KIND_EXECUTOR_GLOBAL_MODEL = 2;

	/**
	 * execute scenario based on KIND method
	 * 
	 * @param id
	 * @param kind
	 * @return
	 * @throws Exception
	 */
	public Scenario executeScenario(Session session, Long id, int kind) throws Exception {
		if (kind == KIND_EXECUTOR_OWN_DATA) {
			return executeScenario(session, id);
		} else if (kind == KIND_EXECUTOR_GLOBAL_MODEL) {
			Scenario scenario = getScenario(session, id);
			Modeler modeler = new ModelerUtil().getModeler(session, scenario.getModelerId());
			GlobalModelWrapper globalModel = new GlobalModelWrapper(session, modeler)
					.setAdditionalSimilarityConstraint(additionalSimilarityConstraint).create();
			return executeScenario(session, id, globalModel);
		} else
			throw new Exception(String.format("Uknown kind [%s]", kind));
	}

	public Scenario executeScenario(final Session session, Long id, GlobalModelWrapper globalModel) throws Exception {
		try {
			Scenario scenario = getScenario(session, id);
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("For [%s] I loaded [%s]", id, scenario));
			}
			new GlobalModelScenarioExecutor(session, scenario, globalModel).run();
			return scenario;
		} catch (Exception e) {
			logger.info(String.format("Could not execute scenario [%s]", id), e);
			throw new Exception(String.format("Could not execute scenario [%s]", id), e);
		}
	}

	/**
	 * create a session and call executeConsumptionScenario(Session session,
	 * String from, String to, Integer weight, Integer count, Long modelId)
	 * 
	 * @param from
	 * @param to
	 * @param weight
	 * @param count
	 * @param modelId
	 * @return
	 * @throws Exception
	 */

	public String executeConsumptionScenario(String from, String to, Integer weight, Integer count, Long modelId)
			throws Exception {
		Session session = null;

		try {
			session = HibernateUtil.openSession();

			session.beginTransaction();

			String toRet = executeConsumptionScenario(session, from, to, weight, count, modelId);

			session.getTransaction().commit();

			return toRet;
		} catch (Exception e) {
			logger.error(String.format(
					"Could not executeConsumptionScenario(from [%s], to [%s], weight [%s], count [%s], modelId [%s])",
					from, to, weight, count, modelId), e);
			throw new Exception(String.format(
					"Could not executeConsumptionScenario(from [%s], to [%s], weight [%s], count [%s], modelId [%s])",
					from, to, weight, count, modelId), e);
		} finally {
			HibernateUtil.closeSession(session);
		}

	}

	public String executeConsumptionScenario(Session session, String from, String to, Integer weight, Integer count,
			Long modelId) throws Exception {
		SimpleDateFormat df = new SimpleDateFormat("yyMMdd");
		Scenario scenario = new Scenario();
		scenario.setStartDate(df.parse(from));
		scenario.setTargetDate(df.parse(to));
		scenario.setWeight(weight / 100.0);
		scenario.setFishNo(count);
		scenario.setModelerId(modelId);
		ConsumptionScenarioExecutor executor = new ConsumptionScenarioExecutor(session, scenario);
		executor.run();
		return scenario.getResultsGraphData();
	}

	public String executeConsumptionScenario(String from, String to, Integer weight, Integer count, String latitude,
			String longitude, Long speciesId, final Integer acceptableSiteCount, final Integer upToGrade)
			throws Exception {

		Session session = null;

		try {
			session = HibernateUtil.openSession();

			session.beginTransaction();

			String toRet = executeConsumptionScenario(session, from, to, weight, count, latitude, longitude, speciesId,
					acceptableSiteCount, upToGrade);

			session.getTransaction().commit();

			return toRet;
		} catch (Exception e) {
			logger.error(String.format(
					"Could not executeConsumptionScenario(from [%s], to [%s], weight [%s], count [%s], latitude [%s], longitude [%s], speciesId [%s])",
					from, to, weight, count, latitude, longitude, speciesId), e);
			throw new Exception(String.format(
					"Could not executeConsumptionScenario(from [%s], to [%s], weight [%s], count [%s], latitude [%s], longitude [%s], speciesId [%s])",
					from, to, weight, count, latitude, longitude, speciesId), e);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	public String executeConsumptionScenario(final Session session, final String from, final String to,
			final Integer weight, final Integer count, final String latitude, final String longitude,
			final Long speciesId, final Integer acceptableSiteCount, final Integer upToGrade) throws Exception {

		// cache the retrievers per work chunk
		SimpleDateFormat df = new SimpleDateFormat("yyMMdd");
		Scenario scenario = new Scenario();
		scenario.setStartDate(df.parse(from));
		scenario.setTargetDate(df.parse(to));
		scenario.setWeight(weight / 100.0);
		scenario.setFishNo(count);
		GlobalModelWrapper globalModel = new GlobalModelWrapper(session, latitude, longitude, speciesId,
				temperatureRetriever, oxygenRetriever, acceptableSiteCount, upToGrade, additionalSimilarityConstraint);

		String results = null;
		String cacheId = null; // valid only if cache != null

		// cache is optional
		if (cache != null) {
			// try the cache
			cacheId = globalModel.getMyId() + "_" + String.valueOf(weight) + "_" + String.valueOf(count) + "_" + from
					+ "_" + to;
			if (cache.containsKey(cacheId)) {
				results = cache.get(cacheId);
				cacheId = null; // signal that we don't want to put it in the
								// cache, it is already there
			}
		}

		if (results == null) {
			// not in cache so create it
			globalModel.create();
			GlobalModelConsumptionExecutor executor = new GlobalModelConsumptionExecutor(scenario, globalModel);
			executor.run();
			results = scenario.getResultsGraphData();
		}

		if (cacheId != null) {
			// cache it
			cache.put(cacheId, results);
		}

		return results;

	}

	public int updateStatusOnSite(Session session, Long siteId, Long statusId) {
		final SQLQuery q = session.createSQLQuery(_UPDATE_STATUS_ON_SITEID);
		q.setParameter("siteId", siteId);
		q.setParameter("statusId", statusId);
		return q.executeUpdate();

	}

	private static final String _UPDATE_STATUS_ON_SITEID = "UPDATE Scenario SET statusId=:statusId WHERE simulModelId IN (SELECT id FROM SimulModel WHERE siteId = :siteId)";
	private static final String _GET_ALL_ON_OWNERID = "FROM gr.i2s.fishgrowth.model.Scenario s WHERE s.ownerId = :ownerid ORDER BY s.designation ASC";
	private static final Logger logger = LoggerFactory.getLogger(ScenarioUtil.class);
}
