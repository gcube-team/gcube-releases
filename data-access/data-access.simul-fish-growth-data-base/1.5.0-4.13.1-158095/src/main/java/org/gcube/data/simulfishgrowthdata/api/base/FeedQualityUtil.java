package org.gcube.data.simulfishgrowthdata.api.base;

import java.util.List;

import org.gcube.data.simulfishgrowthdata.util.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.i2s.fishgrowth.model.FeedQuality;

public class FeedQualityUtil extends BaseUtil {

	public List<FeedQuality> getFeedQualities() throws Exception {
		Session session = null;

		try {
			session = HibernateUtil.openSession();

			session.beginTransaction();

			Query q = session.createQuery(_GET_ALL);

			List<FeedQuality> list = q.list();

			session.getTransaction().commit();

			return list;
		} catch (Exception e) {
			logger.info(String.format("Could not retrieve feed qualities"), e);
			throw new Exception(String.format("Could not retrieve feed qualities"), e);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	// private static final String _GET_ALL_ON_OWNERID = "SELECT {SiteView.*}
	// FROM SiteView WHERE ownerid = :ownerid ORDER BY designation ASC";
	private static final String _GET_ALL = "FROM gr.i2s.fishgrowth.model.FeedQuality s ORDER BY s.aa ASC";
	private static final Logger logger = LoggerFactory.getLogger(FeedQualityUtil.class);
}
