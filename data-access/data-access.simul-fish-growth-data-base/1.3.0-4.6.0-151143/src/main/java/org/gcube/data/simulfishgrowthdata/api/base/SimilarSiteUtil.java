package org.gcube.data.simulfishgrowthdata.api.base;

import java.util.List;

import org.gcube.data.simulfishgrowthdata.util.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.LongType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.i2s.fishgrowth.model.SimilarSite;

public class SimilarSiteUtil extends BaseUtil {

	public SimilarSite add(SimilarSite entity) throws Exception {
		Session session = null;

		try {
			session = HibernateUtil.openSession();
			session.beginTransaction();

			session.save(entity);

			session.flush();
			session.getTransaction().commit();
			return entity;
		} catch (Exception e) {
			logger.info(String.format("Could not add similar sites [%s]", entity), e);
			throw new Exception(String.format("Could not add similar sites [%s]", entity), e);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	public List<Long> getSimilarSites(Long siteId) throws Exception {
		Session session = null;

		try {
			session = HibernateUtil.openSession();
			session.beginTransaction();

			List<Long> list = getSimilarSites(session, siteId);

			session.getTransaction().commit();

			return list;
		} catch (Exception e) {
			logger.info(String.format("Could not retrieve similar sites for siteid [%s]", siteId), e);
			throw new Exception(String.format("Could not retrieve similar sites for siteid [%s]", siteId), e);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	public List<Long> getSimilarSitesExcludingMe(Long siteId) throws Exception {
		Session session = null;

		try {
			session = HibernateUtil.openSession();
			session.beginTransaction();

			List<Long> list = getSimilarSitesExcludingMe(session, siteId);

			session.getTransaction().commit();

			return list;
		} catch (Exception e) {
			logger.info(String.format("Could not retrieve similar sites exc me for siteid [%s]", siteId), e);
			throw new Exception(String.format("Could not retrieve similar sites exc me for siteid [%s]", siteId), e);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	public boolean delete(Long siteId) throws Exception {
		Session session = null;

		try {
			session = HibernateUtil.openSession();
			session.beginTransaction();

			Query q = session.createSQLQuery(_DELETE_ALL).setParameter("siteId", siteId);

			q.executeUpdate();

			session.flush();
			session.getTransaction().commit();
			return true;
		} catch (Exception e) {
			logger.info(String.format("Could not delete similar sites for siteid [%s]", siteId), e);
			throw new Exception(String.format("Could not delete similar sites for siteid [%s]", siteId), e);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	public boolean delete(Long siteId, Long similarId) throws Exception {
		Session session = null;

		try {
			session = HibernateUtil.openSession();
			session.beginTransaction();

			delete(session, siteId, similarId);

			session.flush();
			session.getTransaction().commit();

			return true;
		} catch (Exception e) {
			logger.info(
					String.format("Could not delete similar sites for siteid [%s] similarid [%s]", siteId, similarId),
					e);
			throw new Exception(
					String.format("Could not delete similar sites for siteid [%s] similarid [%s]", siteId, similarId),
					e);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	public void delete(Session session, Long siteId, Long similarId) {
		Query q = session.createSQLQuery(_DELETE_KEY).setParameter("siteId", siteId).setParameter("similarId",
				similarId);
		q.executeUpdate();
	}

	public List<Long> getSimilarSitesExcludingMe(Session session, Long siteId) {
		Query q = session.createSQLQuery(_GET_ALL_BUT_ME).addScalar("similarId", LongType.INSTANCE)
				.setParameter("siteId", siteId);
		List<Long> list = q.list();
		return list;
	}

	public List<Long> getSimilarSites(Session session, Long siteId) {
		Query q = session.createSQLQuery(_GET_ALL).addScalar("similarId", LongType.INSTANCE).setParameter("siteId",
				siteId);
		List<Long> list = q.list();
		return list;
	}

	public Long getGlobal(Session session, Long siteId) {
		Query q = session.createSQLQuery(_GET_GLOBAL).addScalar("similarId", LongType.INSTANCE).setParameter("siteId",
				siteId);
		List<Long> list = q.list();
		return list.isEmpty() ? null : list.get(0);
	}

	private static final String _GET_ALL = "SELECT DISTINCT s.similarId FROM SimilarSite s WHERE siteId=:siteId AND grade>0 ORDER BY s.similarId ASC";
	private static final String _GET_ALL_BUT_ME = "SELECT DISTINCT s.similarId FROM SimilarSite s WHERE siteId=:siteId AND  similarId<>:siteId AND grade>0 ORDER BY s.similarId ASC";
	private static final String _DELETE_ALL = "DELETE FROM SimilarSite WHERE siteId=:siteId OR similarSiteId=:siteId";
	private static final String _DELETE_KEY = "DELETE FROM SimilarSite WHERE siteId=:siteId AND similarId=:similarId";
	private static final String _GET_GLOBAL = "SELECT DISTINCT s.similarId FROM SimilarSite s WHERE siteId=:siteId AND grade=-1";
	private static final Logger logger = LoggerFactory.getLogger(SimilarSiteUtil.class);

}
