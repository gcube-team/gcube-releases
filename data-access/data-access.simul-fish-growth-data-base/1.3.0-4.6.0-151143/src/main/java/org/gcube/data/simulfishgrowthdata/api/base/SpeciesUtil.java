package org.gcube.data.simulfishgrowthdata.api.base;

import java.util.List;

import org.gcube.data.simulfishgrowthdata.util.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.i2s.fishgrowth.model.Species;

public class SpeciesUtil extends BaseUtil {

	public List<Species> getSpecieses() throws Exception {
		Session session = null;

		try {
			session = HibernateUtil.openSession();

			session.beginTransaction();

			Query q = session.createQuery(_GET_ALL);

			List<Species> list = q.list();

			session.getTransaction().commit();

			return list;
		} catch (Exception e) {
			logger.error("Could not retrieve species", e);
			throw new Exception("Could not retrieve species", e);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	private static final String _GET_ALL = "FROM gr.i2s.fishgrowth.model.Species s ORDER BY s.designation ASC";
	private static final Logger logger = LoggerFactory.getLogger(SpeciesUtil.class);
}
