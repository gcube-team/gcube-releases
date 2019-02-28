package org.gcube.data.simulfishgrowthdata.api.base;

import java.util.List;

import org.gcube.data.simulfishgrowthdata.util.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.i2s.fishgrowth.model.Region;

public class RegionUtil extends BaseUtil {

	public List<Region> getRegions() throws Exception {
		Session session = null;

		try {
			session = HibernateUtil.openSession();

			session.beginTransaction();

			Query q = session.createQuery(_GET_ALL);

			List<Region> list = q.list();

			session.getTransaction().commit();

			return list;
		} catch (Exception e) {
			logger.error("Could not retrieve regions", e);
			throw new Exception("Could not retrieve regions", e);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	// private static final String _GET_ALL_ON_OWNERID = "SELECT {SiteView.*}
	// FROM SiteView WHERE ownerid = :ownerid ORDER BY designation ASC";
	private static final String _GET_ALL = "FROM gr.i2s.fishgrowth.model.Region s ORDER BY s.designation ASC";
	private static final Logger logger = LoggerFactory.getLogger(RegionUtil.class);
}
