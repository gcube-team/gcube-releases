package org.gcube.data.simulfishgrowthdata.api.base;

import java.util.List;

import org.gcube.data.simulfishgrowthdata.util.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.i2s.fishgrowth.model.Scenario;
import gr.i2s.fishgrowth.model.ScenarioFull;

public class ScenarioFullUtil extends BaseUtil {
	public Scenario add(ScenarioFull scenarioFull) throws Exception {
		return new ScenarioUtil().add(new Scenario(scenarioFull));
	}

	public Scenario add(Session session, ScenarioFull scenarioFull) throws Exception {
		return new ScenarioUtil().add(session, new Scenario(scenarioFull));
	}

	public Scenario update(ScenarioFull scenarioFull) throws Exception {
		return new ScenarioUtil().update(new Scenario(scenarioFull));
	}

	public boolean delete(final Session session, final Long id) throws Exception {
		return new ScenarioUtil().delete(session, id);
	}

	public boolean delete(Long id) throws Exception {
		return new ScenarioUtil().delete(id);
	}

	public ScenarioFull getScenarioFull(final Long id) throws Exception {
		Session session = null;

		try {
			session = HibernateUtil.openSession();

			session.beginTransaction();

			ScenarioFull scenarioFull =  getScenarioFull(session, id);

			session.getTransaction().commit();

			return scenarioFull;
		} catch (Exception e) {
			logger.info(String.format("Could not retrieve scenario full [%s]", id), e);
			throw new Exception(String.format("Could not retrieve scenario full [%s]", id), e);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	public ScenarioFull getScenarioFull(final Session session, final Long id) throws Exception {
		ScenarioFull scenarioFull = (ScenarioFull) session.get(ScenarioFull.class, Long.valueOf(id));
		return scenarioFull;
	}

	public List<ScenarioFull> getScenarioFulls(String ownerId, Integer start, Integer end) throws Exception {
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("reading %s for %s start %s end %s", "ScenarioFull", ownerId, start, end));
		}

		Session session = null;

		try {
			logger.trace(String.format("start getScenarioFulls with ownerid [%s]", ownerId));
			session = HibernateUtil.openSession();
			logger.trace(String.format("session [%s]", session));

			session.beginTransaction();

			Query q = session.createQuery(_GET_ALL_ON_OWNERID).setParameter("ownerid", ownerId);
			if (start > 0)
				q.setFirstResult(start);
			if (end > 0) {
				if (end < start)
					end = start + 1;
				q.setMaxResults(end - start);
			}

			List<ScenarioFull> list = q.list();

			session.getTransaction().commit();

			logger.trace(String.format("return ScenarioFulls %s", list));
			return list;
		} catch (Exception e) {
			logger.info(String.format("Could not retrieve scenario full for [%s] start [%s] [%s]", ownerId, start, end),
					e);
			throw new Exception(
					String.format("Could not retrieve scenario full for [%s] start [%s] [%s]", ownerId, start, end), e);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	public List<ScenarioFull> getScenarioFulls(String ownerId) throws Exception {
		Session session = null;

		try {
			logger.trace(String.format("start getScenarioFulls with ownerid [%s]", ownerId));
			session = HibernateUtil.openSession();
			logger.trace(String.format("session [%s]", session));

			session.beginTransaction();

			Query q = session.createQuery(_GET_ALL_ON_OWNERID).setParameter("ownerid", ownerId);

			List<ScenarioFull> list = q.list();

			session.getTransaction().commit();

			logger.trace(String.format("return ScenarioFulls %s", list));
			return list;
		} catch (Exception e) {
			logger.info(String.format("Could not retrieve retrieve scenario full for [%s]", ownerId), e);
			throw new Exception(String.format("Could not retrieve scenario full for [%s]", ownerId), e);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	public int getScenarioFullCount(String ownerId) throws Exception {
		Session session = null;

		try {
			logger.trace(String.format("start getScenarioFullCount with ownerid [%s]", ownerId));
			session = HibernateUtil.openSession();
			logger.trace(String.format("session [%s]", session));

			session.beginTransaction();

			Query q = session.createQuery(_GET_ALL_ON_OWNERID_COUNT).setParameter("ownerid", ownerId);

			Number count = (Number) q.uniqueResult();

			session.getTransaction().commit();

			logger.trace(String.format("return count %s", count));
			return count.intValue();
		} catch (Exception e) {
			logger.info(String.format("Could not retrieve scenario full count for [%s]", ownerId), e);
			throw new Exception(String.format("Could not retrieve scenario full count for [%s]", ownerId), e);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	private static final String _GET_ALL_ON_OWNERID = "FROM gr.i2s.fishgrowth.model.ScenarioFull s WHERE s.ownerId = :ownerid ORDER BY s.designation ASC";
	private static final String _GET_ALL_ON_OWNERID_COUNT = "SELECT count(*) FROM gr.i2s.fishgrowth.model.ScenarioFull s WHERE s.ownerId = :ownerid";
	private static final Logger logger = LoggerFactory.getLogger(ScenarioFullUtil.class);
}
