package org.gcube.data.simulfishgrowthdata.api.base;

import java.util.List;
import java.util.ListIterator;

import org.gcube.data.simulfishgrowthdata.model.GlobalModelWrapper;
import org.gcube.data.simulfishgrowthdata.util.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.i2s.fishgrowth.model.Modeler;
import gr.i2s.fishgrowth.model.ModelerFull;
import gr.i2s.fishgrowth.model.Scenario;

public class ModelerFullUtil extends BaseUtil {
	static final Long neutral = new Long(-1);

	public Modeler add(ModelerFull modelerFull) throws Exception {
		return new ModelerUtil().add(new Modeler(modelerFull));
	}

	public Modeler add(Session session, ModelerFull modelerFull) throws Exception {
		return add(session, modelerFull, true);
	}

	public Modeler add(Session session, ModelerFull modelerFull, boolean createSamples) throws Exception {
		return new ModelerUtil().add(session, new Modeler(modelerFull), createSamples);
	}

	public Modeler update(ModelerFull modelerFull) throws Exception {
		return update(modelerFull, true);
	}

	public Modeler update(ModelerFull modelerFull, boolean createSamples) throws Exception {
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("original %s", modelerFull));
		}

		Modeler copy = new Modeler(modelerFull);
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("copy %s", copy));
		}

		return new ModelerUtil().update(copy, createSamples);
	}

	public boolean delete(Long id) throws Exception {
		return new ModelerUtil().delete(id);
	}

	public boolean delete(Session session, Long id) throws Exception {
		return new ModelerUtil().delete(session, new ModelerUtil().getModeler(session, id));
	}

	public ModelerFull getModelerFull(Long id) throws Exception {
		Session session = null;

		try {
			session = HibernateUtil.openSession();

			session.beginTransaction();

			ModelerFull modelerFull = getModelerFull(session, id);

			session.getTransaction().commit();

			return modelerFull;
		} catch (Exception e) {
			logger.info(String.format("Could not retrieve modeler full [%s]", id), e);
			throw new Exception(String.format("Could not retrieve modeler full [%s]", id), e);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	public ModelerFull getModelerFull(final Session session, final Long id) throws Exception {

		ModelerFull modelerFull = (ModelerFull) session.get(ModelerFull.class, Long.valueOf(id));
		return modelerFull;
	}

	public List<ModelerFull> getModelerFulls(String ownerId, Integer start, Integer end, List<Long> status,
			Long species) throws Exception {
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("reading %s for %s start %s end %s", "ModelerFull", ownerId, start, end));
		}

		Session session = null;

		try {
			logger.trace(String.format("start getModelerFulls"));
			session = HibernateUtil.openSession();
			logger.trace(String.format("session [%s]", session));

			session.beginTransaction();

			Query q = session.createQuery(_GET_ALL_ON_OWNERID).setParameter("ownerid", ownerId)
					.setParameter("neutral", neutral)
					.setParameter("species", species == null ? neutral : species.longValue());
			if (start > 0)
				q.setFirstResult(start);
			if (end > 0) {
				if (end < start)
					end = start + 1;
				q.setMaxResults(end - start);
			}

			List<ModelerFull> list = q.list();

			if (status != null && !status.isEmpty())
				for (ListIterator<ModelerFull> iter = list.listIterator(); iter.hasNext();) {
					Modeler m = (Modeler) iter.next();
					if (!status.contains(m.getStatusId())) {
						iter.remove();
					}
				}

			session.getTransaction().commit();

			logger.trace(String.format("return ModelerFulls %s", list));
			return list;
		} catch (Exception e) {
			logger.info(String.format(
					"Could not retrieve modeler full for [%s] start [%s] end [%s] status [%s] species [%s]", ownerId,
					start, end, status, species), e);
			throw new Exception(String.format(
					"Could not retrieve modeler full for [%s] start [%s] end [%s] status [%s] species [%s]", ownerId,
					start, end, status, species), e);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	public List<ModelerFull> getModelerFulls(String ownerId, List<Long> status, Long species) throws Exception {
		Session session = null;

		try {
			logger.trace(String.format("start getModelerFulls"));
			session = HibernateUtil.openSession();
			logger.trace(String.format("session [%s]", session));

			session.beginTransaction();

			Query q = session.createQuery(_GET_ALL_ON_OWNERID).setParameter("ownerid", ownerId)
					.setParameter("neutral", neutral)
					.setParameter("species", species == null ? neutral : species.longValue());

			List<ModelerFull> list = q.list();

			logger.trace(String.format("status  requested [%s]", status));
			if (status != null && !status.isEmpty())
				for (ListIterator<ModelerFull> iter = list.listIterator(); iter.hasNext();) {
					Modeler m = (Modeler) iter.next();
					logger.trace(String.format("examining [%s]", m));
					if (!status.contains(m.getStatusId())) {
						iter.remove();
					}
				}

			session.getTransaction().commit();

			logger.trace(String.format("return ModelerFulls %s", list));
			return list;
		} catch (Exception e) {
			logger.info(String.format("Could not retrieve modeler full for [%s] status [%s] species [%s]", ownerId,
					status, species), e);
			throw new Exception(String.format("Could not retrieve modeler full for [%s] status [%s] species [%s]",
					ownerId, status, species), e);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	public int getModelerFullCount(String ownerId, Long species) throws Exception {
		Session session = null;

		try {
			logger.trace(String.format("start getModelerFulls"));
			session = HibernateUtil.openSession();
			logger.trace(String.format("session [%s]", session));

			session.beginTransaction();

			Query q = session.createQuery(_GET_ALL_ON_OWNERID_COUNT).setParameter("ownerid", ownerId)
					.setParameter("neutral", neutral)
					.setParameter("species", species == null ? neutral : species.longValue());

			Number count = (Number) q.uniqueResult();

			session.getTransaction().commit();

			logger.trace(String.format("return count %s", count));
			return count.intValue();
		} catch (Exception e) {
			logger.info(String.format("Could not retrieve modeler full count for [%s] species [%s]", ownerId, species),
					e);
			throw new Exception(String.format("Could not retrieve modeler full [%s] species [%s]", ownerId, species),
					e);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	static final public int KIND_GET_GLOBAL_MODEL_ON_SCENARIO = 0;
	static final public int KIND_GET_GLOBAL_MODEL_ON_MODELER = 1;

	// public GlobalModel getGlobalModel(final int kind, final Long id) throws
	// Exception {
	// return getGlobalModel(null, kind, id);
	// }
	//
	// public GlobalModel getGlobalModel(final Session session, final int kind,
	// final Long id) throws Exception {
	// Long modelerId;
	// if (kind == KIND_GET_GLOBAL_MODEL_ON_MODELER) {
	// modelerId = id;
	// } else if (kind == KIND_GET_GLOBAL_MODEL_ON_SCENARIO) {
	// Scenario scenario = session == null ? new ScenarioUtil().getScenario(id)
	// : new ScenarioUtil().getScenario(session, id);
	// modelerId = scenario.getModelerId();
	// } else {
	// throw new Exception(String.format("Unknown kind [%s]", kind));
	// }
	// Modeler modeler = session == null ? new
	// ModelerUtil().getModeler(modelerId)
	// : new ModelerUtil().getModeler(session, modelerId);
	//
	// GlobalModel toRet = null;
	//
	// Session localsession = null;
	// if (session == null) {
	// try {
	// localsession = HibernateUtil.openSession();
	// localsession.beginTransaction();
	//
	// toRet = new GlobalModel(localsession, modeler).create();
	// localsession.getTransaction().commit();
	// } finally {
	// HibernateUtil.closeSession(localsession);
	// }
	// } else {
	// toRet = new GlobalModel(session, modeler).create();
	// }
	//
	// return toRet;
	// }

	private static final String _GET_ALL_ON_OWNERID = "FROM gr.i2s.fishgrowth.model.ModelerFull s WHERE s.ownerId = :ownerid AND ((:neutral=:species) OR (s.speciesId=:species)) ORDER BY s.designation ASC";
	private static final String _GET_ALL_ON_OWNERID_COUNT = "SELECT count(*) FROM gr.i2s.fishgrowth.model.ModelerFull s WHERE s.ownerId = :ownerid AND ((:neutral=:species) OR (s.speciesId=:species))";
	private static final Logger logger = LoggerFactory.getLogger(ModelerFullUtil.class);
}
