package org.gcube.data.simulfishgrowthdata.api.base;

import java.util.List;

import org.gcube.data.simulfishgrowthdata.util.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.i2s.fishgrowth.model.Mortality;

public class MortalityUtil extends BaseUtil {

	public List<Mortality> getMortalities(Long modelId) throws Exception {
		Session session = null;

		try {
			session = HibernateUtil.openSession();

			session.beginTransaction();

			List<Mortality> list = getMortalities(session, modelId);

			session.getTransaction().commit();

			return list;
		} catch (Exception e) {
			logger.info(String.format("Could not retrieve mortalities for [%s]", modelId), e);
			throw new Exception(String.format("Could not retrieve mortalities for [%s]", modelId), e);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	public List<Mortality> getMortalities(final Session session, Long modelId) throws Exception {
		Query q = session.createQuery(_GET_ALL_ON_MODELER).setParameter("modelerId", modelId);
		List<Mortality> list = q.list();
		return list;
	}

	public int deleteAll(Long modelId) {
		Session session = null;

		try {
			session = HibernateUtil.openSession();

			session.beginTransaction();

			int count = deleteAll(session, modelId);

			session.getTransaction().commit();
			return count;
		} catch (Exception e) {
			logger.info(String.format("Could not delete mortlaities for [%s]", modelId), e);
			return 0;
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	public int deleteAll(Session session, Long modelId) {
		Query q = session.createQuery(_DEL_ALL_ON_MODELER).setParameter("modelerId", modelId);
		int count = q.executeUpdate();
		return count;
	}

	private static final String _GET_ALL_ON_MODELER = "FROM gr.i2s.fishgrowth.model.Mortality s WHERE s.modelerId = :modelerId ORDER BY s.temperature ASC, fromWeight DESC";
	private static final String _DEL_ALL_ON_MODELER = "DELETE FROM gr.i2s.fishgrowth.model.Mortality s WHERE s.modelerId = :modelerId";
	private static final Logger logger = LoggerFactory.getLogger(MortalityUtil.class);
}
