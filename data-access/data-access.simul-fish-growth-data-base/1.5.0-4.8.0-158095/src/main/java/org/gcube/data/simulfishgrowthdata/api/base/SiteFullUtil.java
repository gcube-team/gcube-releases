package org.gcube.data.simulfishgrowthdata.api.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.gcube.data.simulfishgrowthdata.util.DatabaseUtil;
import org.gcube.data.simulfishgrowthdata.util.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.i2s.fishgrowth.model.Site;
import gr.i2s.fishgrowth.model.SiteFull;

public class SiteFullUtil extends BaseUtil {

	String additionalSimilarityConstraint = null;

	public SiteFullUtil setAdditionalSimilarityConstraint(String additionalSimilarityConstraint) {
		this.additionalSimilarityConstraint = additionalSimilarityConstraint;
		return this;
	}

	public SiteFull getSiteFull(Long id) throws Exception {
		Session session = null;

		try {
			session = HibernateUtil.openSession();

			session.beginTransaction();

			SiteFull siteFull = (SiteFull) session.get(SiteFull.class, Long.valueOf(id));

			session.getTransaction().commit();

			return siteFull;
		} catch (Exception e) {
			logger.error(String.format("Could not retrieve full site for [%s]", id), e);
			throw new Exception("not found");
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	public List<SiteFull> getSiteFulls(String ownerId, Integer start, Integer end) throws Exception {
		if (logger.isTraceEnabled())
			logger.trace(String.format("reading %s for %s start %s end %s", "SiteFull", ownerId, start, end));

		Session session = null;

		try {
			if (logger.isTraceEnabled())
				logger.trace(String.format("start getSiteFulls"));
			session = HibernateUtil.openSession();
			if (logger.isTraceEnabled())
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

			List<SiteFull> list = q.list();

			session.getTransaction().commit();

			if (logger.isTraceEnabled())
				logger.trace(String.format("return SiteFulls %s", list));
			return list;
		} catch (Exception e) {
			logger.error(String.format("Could not retrieve full sites for ownerid[%s]", ownerId), e);
			throw new Exception("not found");
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	public List<SiteFull> getSiteFulls(String ownerId) throws Exception {
		Session session = null;

		try {
			if (logger.isTraceEnabled())
				logger.trace(String.format("start getSiteFulls"));
			session = HibernateUtil.openSession();
			if (logger.isTraceEnabled())
				logger.trace(String.format("session [%s]", session));

			session.beginTransaction();

			Query q = session.createQuery(_GET_ALL_ON_OWNERID).setParameter("ownerid", ownerId);

			List<SiteFull> list = q.list();

			session.getTransaction().commit();

			if (logger.isTraceEnabled())
				logger.trace(String.format("return SiteFulls %s", list));
			return list;
		} catch (Exception e) {
			logger.error(String.format("Could not retrieve full sites for ownerid[%s]", ownerId), e);
			throw new Exception("not found");
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	public int getSiteFullCount(String ownerId) throws Exception {
		Session session = null;

		try {
			if (logger.isTraceEnabled())
				logger.trace(String.format("start getSiteFulls"));
			session = HibernateUtil.openSession();
			if (logger.isTraceEnabled())
				logger.trace(String.format("session [%s]", session));

			session.beginTransaction();

			Query q = session.createQuery(_GET_ALL_ON_OWNERID_COUNT).setParameter("ownerid", ownerId);

			Number count = (Number) q.uniqueResult();

			session.getTransaction().commit();

			if (logger.isTraceEnabled())
				logger.trace(String.format("return count %s", count));
			return count.intValue();
		} catch (Exception e) {
			logger.error(String.format("Could not retrieve full site count for ownerid[%s]", ownerId), e);
			throw new Exception("not found");
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	public List<Long> getSiteFullSimilar(Session session, Site site, Integer dtemp, Integer dAnnualTemp,
			Integer doxygen) {
		List<Long> list = new ArrayList<Long>();
		if (site == null) {
			return list;
		}
		String additionalConstraint = additionalSimilarityConstraint;
		if (additionalConstraint == null || additionalConstraint.isEmpty()) {
			additionalConstraint = "1=1"; // a.k.a. "true"
		}
		Integer dAnnualOxygen = 1000; // not used
		Query q = session.createQuery(String.format(_GET_SIMILAR, additionalConstraint))
				.setParameter("id", site.getId()).setParameter("global", DatabaseUtil.GLOBAL_OWNER)
				.setParameter("janAFrom", site.getPeriodJanA() - dtemp)
				.setParameter("janATo", site.getPeriodJanA() + dtemp)
				.setParameter("janBFrom", site.getPeriodJanB() - dtemp)
				.setParameter("janBTo", site.getPeriodJanB() + dtemp)
				.setParameter("febAFrom", site.getPeriodFebA() - dtemp)
				.setParameter("febATo", site.getPeriodFebA() + dtemp)
				.setParameter("febBFrom", site.getPeriodFebB() - dtemp)
				.setParameter("febBTo", site.getPeriodFebB() + dtemp)
				.setParameter("marAFrom", site.getPeriodMarA() - dtemp)
				.setParameter("marATo", site.getPeriodMarA() + dtemp)
				.setParameter("marBFrom", site.getPeriodMarB() - dtemp)
				.setParameter("marBTo", site.getPeriodMarB() + dtemp)
				.setParameter("aprAFrom", site.getPeriodAprA() - dtemp)
				.setParameter("aprATo", site.getPeriodAprA() + dtemp)
				.setParameter("aprBFrom", site.getPeriodAprB() - dtemp)
				.setParameter("aprBTo", site.getPeriodAprB() + dtemp)
				.setParameter("mayAFrom", site.getPeriodMayA() - dtemp)
				.setParameter("mayATo", site.getPeriodMayA() + dtemp)
				.setParameter("mayBFrom", site.getPeriodMayB() - dtemp)
				.setParameter("mayBTo", site.getPeriodMayB() + dtemp)
				.setParameter("junAFrom", site.getPeriodJunA() - dtemp)
				.setParameter("junATo", site.getPeriodJunA() + dtemp)
				.setParameter("junBFrom", site.getPeriodJunB() - dtemp)
				.setParameter("junBTo", site.getPeriodJunB() + dtemp)
				.setParameter("julAFrom", site.getPeriodJulA() - dtemp)
				.setParameter("julATo", site.getPeriodJulA() + dtemp)
				.setParameter("julBFrom", site.getPeriodJulB() - dtemp)
				.setParameter("julBTo", site.getPeriodJulB() + dtemp)
				.setParameter("augAFrom", site.getPeriodAugA() - dtemp)
				.setParameter("augATo", site.getPeriodAugA() + dtemp)
				.setParameter("augBFrom", site.getPeriodAugB() - dtemp)
				.setParameter("augBTo", site.getPeriodAugB() + dtemp)
				.setParameter("sepAFrom", site.getPeriodSepA() - dtemp)
				.setParameter("sepATo", site.getPeriodSepA() + dtemp)
				.setParameter("sepBFrom", site.getPeriodSepB() - dtemp)
				.setParameter("sepBTo", site.getPeriodSepB() + dtemp)
				.setParameter("octAFrom", site.getPeriodOctA() - dtemp)
				.setParameter("octATo", site.getPeriodOctA() + dtemp)
				.setParameter("octBFrom", site.getPeriodOctB() - dtemp)
				.setParameter("octBTo", site.getPeriodOctB() + dtemp)
				.setParameter("novAFrom", site.getPeriodNovA() - dtemp)
				.setParameter("novATo", site.getPeriodNovA() + dtemp)
				.setParameter("novBFrom", site.getPeriodNovB() - dtemp)
				.setParameter("novBTo", site.getPeriodNovB() + dtemp)
				.setParameter("decAFrom", site.getPeriodDecA() - dtemp)
				.setParameter("decATo", site.getPeriodDecA() + dtemp)
				.setParameter("decBFrom", site.getPeriodDecB() - dtemp)
				.setParameter("decBTo", site.getPeriodDecB() + dtemp)
				.setParameter("yearFrom", site.getPeriodYear() - dAnnualTemp)
				.setParameter("yearTo", site.getPeriodYear() + dAnnualTemp)
				.setParameter("oxygenjanAFrom", site.getOxygenPeriodJanA() - doxygen)
				.setParameter("oxygenjanATo", site.getOxygenPeriodJanA() + doxygen)
				.setParameter("oxygenjanBFrom", site.getOxygenPeriodJanB() - doxygen)
				.setParameter("oxygenjanBTo", site.getOxygenPeriodJanB() + doxygen)
				.setParameter("oxygenfebAFrom", site.getOxygenPeriodFebA() - doxygen)
				.setParameter("oxygenfebATo", site.getOxygenPeriodFebA() + doxygen)
				.setParameter("oxygenfebBFrom", site.getOxygenPeriodFebB() - doxygen)
				.setParameter("oxygenfebBTo", site.getOxygenPeriodFebB() + doxygen)
				.setParameter("oxygenmarAFrom", site.getOxygenPeriodMarA() - doxygen)
				.setParameter("oxygenmarATo", site.getOxygenPeriodMarA() + doxygen)
				.setParameter("oxygenmarBFrom", site.getOxygenPeriodMarB() - doxygen)
				.setParameter("oxygenmarBTo", site.getOxygenPeriodMarB() + doxygen)
				.setParameter("oxygenaprAFrom", site.getOxygenPeriodAprA() - doxygen)
				.setParameter("oxygenaprATo", site.getOxygenPeriodAprA() + doxygen)
				.setParameter("oxygenaprBFrom", site.getOxygenPeriodAprB() - doxygen)
				.setParameter("oxygenaprBTo", site.getOxygenPeriodAprB() + doxygen)
				.setParameter("oxygenmayAFrom", site.getOxygenPeriodMayA() - doxygen)
				.setParameter("oxygenmayATo", site.getOxygenPeriodMayA() + doxygen)
				.setParameter("oxygenmayBFrom", site.getOxygenPeriodMayB() - doxygen)
				.setParameter("oxygenmayBTo", site.getOxygenPeriodMayB() + doxygen)
				.setParameter("oxygenjunAFrom", site.getOxygenPeriodJunA() - doxygen)
				.setParameter("oxygenjunATo", site.getOxygenPeriodJunA() + doxygen)
				.setParameter("oxygenjunBFrom", site.getOxygenPeriodJunB() - doxygen)
				.setParameter("oxygenjunBTo", site.getOxygenPeriodJunB() + doxygen)
				.setParameter("oxygenjulAFrom", site.getOxygenPeriodJulA() - doxygen)
				.setParameter("oxygenjulATo", site.getOxygenPeriodJulA() + doxygen)
				.setParameter("oxygenjulBFrom", site.getOxygenPeriodJulB() - doxygen)
				.setParameter("oxygenjulBTo", site.getOxygenPeriodJulB() + doxygen)
				.setParameter("oxygenaugAFrom", site.getOxygenPeriodAugA() - doxygen)
				.setParameter("oxygenaugATo", site.getOxygenPeriodAugA() + doxygen)
				.setParameter("oxygenaugBFrom", site.getOxygenPeriodAugB() - doxygen)
				.setParameter("oxygenaugBTo", site.getOxygenPeriodAugB() + doxygen)
				.setParameter("oxygensepAFrom", site.getOxygenPeriodSepA() - doxygen)
				.setParameter("oxygensepATo", site.getOxygenPeriodSepA() + doxygen)
				.setParameter("oxygensepBFrom", site.getOxygenPeriodSepB() - doxygen)
				.setParameter("oxygensepBTo", site.getOxygenPeriodSepB() + doxygen)
				.setParameter("oxygenoctAFrom", site.getOxygenPeriodOctA() - doxygen)
				.setParameter("oxygenoctATo", site.getOxygenPeriodOctA() + doxygen)
				.setParameter("oxygenoctBFrom", site.getOxygenPeriodOctB() - doxygen)
				.setParameter("oxygenoctBTo", site.getOxygenPeriodOctB() + doxygen)
				.setParameter("oxygennovAFrom", site.getOxygenPeriodNovA() - doxygen)
				.setParameter("oxygennovATo", site.getOxygenPeriodNovA() + doxygen)
				.setParameter("oxygennovBFrom", site.getOxygenPeriodNovB() - doxygen)
				.setParameter("oxygennovBTo", site.getOxygenPeriodNovB() + doxygen)
				.setParameter("oxygendecAFrom", site.getOxygenPeriodDecA() - doxygen)
				.setParameter("oxygendecATo", site.getOxygenPeriodDecA() + doxygen)
				.setParameter("oxygendecBFrom", site.getOxygenPeriodDecB() - doxygen)
				.setParameter("oxygendecBTo", site.getOxygenPeriodDecB() + doxygen)
				.setParameter("oxygenyearFrom", site.getOxygenPeriodYear() - dAnnualOxygen)
				.setParameter("oxygenyearTo", site.getOxygenPeriodYear() + dAnnualOxygen);

		list = q.list();
		return list;
	}

	private static final String _GET_ALL_ON_OWNERID = "FROM gr.i2s.fishgrowth.model.SiteFull s WHERE s.ownerId = :ownerid ORDER BY s.designation ASC";
	private static final String _GET_ALL_ON_OWNERID_COUNT = "SELECT count(*) FROM gr.i2s.fishgrowth.model.SiteFull s WHERE s.ownerId = :ownerid";
	private static final String _GET_SIMILAR = "SELECT id FROM gr.i2s.fishgrowth.model.SiteFull s WHERE " + " (%s) AND "
			+ " id <> :id AND " + " ownerId <> :global AND " + " s.periodJanA BETWEEN :janAFrom AND :janATo AND "
			+ " s.periodJanB BETWEEN :janBFrom AND :janBTo AND " + " s.periodFebA BETWEEN :febAFrom AND :febATo AND "
			+ " s.periodFebB BETWEEN :febBFrom AND :febBTo AND " + " s.periodMarA BETWEEN :marAFrom AND :marATo AND "
			+ " s.periodMarB BETWEEN :marBFrom AND :marBTo AND " + " s.periodAprA BETWEEN :aprAFrom AND :aprATo AND "
			+ " s.periodAprB BETWEEN :aprBFrom AND :aprBTo AND " + " s.periodMayA BETWEEN :mayAFrom AND :mayATo AND "
			+ " s.periodMayB BETWEEN :mayBFrom AND :mayBTo AND " + " s.periodJunA BETWEEN :junAFrom AND :junATo AND "
			+ " s.periodJunB BETWEEN :junBFrom AND :junBTo AND " + " s.periodJulA BETWEEN :julAFrom AND :julATo AND "
			+ " s.periodJulB BETWEEN :julBFrom AND :julBTo AND " + " s.periodAugA BETWEEN :augAFrom AND :augATo AND "
			+ " s.periodAugB BETWEEN :augBFrom AND :augBTo AND " + " s.periodSepA BETWEEN :sepAFrom AND :sepATo AND "
			+ " s.periodSepB BETWEEN :sepBFrom AND :sepBTo AND " + " s.periodOctA BETWEEN :octAFrom AND :octATo AND "
			+ " s.periodOctB BETWEEN :octBFrom AND :octBTo AND " + " s.periodNovA BETWEEN :novAFrom AND :novATo AND "
			+ " s.periodNovB BETWEEN :novBFrom AND :novBTo AND " + " s.periodDecA BETWEEN :decAFrom AND :decATo AND "
			+ " s.periodDecB BETWEEN :decBFrom AND :decBTo AND " + " s.periodYear BETWEEN :yearFrom AND :yearTo AND "
			+ " s.oxygenPeriodJanA BETWEEN :oxygenjanAFrom AND :oxygenjanATo AND "
			+ " s.oxygenPeriodJanB BETWEEN :oxygenjanBFrom AND :oxygenjanBTo AND "
			+ " s.oxygenPeriodFebA BETWEEN :oxygenfebAFrom AND :oxygenfebATo AND "
			+ " s.oxygenPeriodFebB BETWEEN :oxygenfebBFrom AND :oxygenfebBTo AND "
			+ " s.oxygenPeriodMarA BETWEEN :oxygenmarAFrom AND :oxygenmarATo AND "
			+ " s.oxygenPeriodMarB BETWEEN :oxygenmarBFrom AND :oxygenmarBTo AND "
			+ " s.oxygenPeriodAprA BETWEEN :oxygenaprAFrom AND :oxygenaprATo AND "
			+ " s.oxygenPeriodAprB BETWEEN :oxygenaprBFrom AND :oxygenaprBTo AND "
			+ " s.oxygenPeriodMayA BETWEEN :oxygenmayAFrom AND :oxygenmayATo AND "
			+ " s.oxygenPeriodMayB BETWEEN :oxygenmayBFrom AND :oxygenmayBTo AND "
			+ " s.oxygenPeriodJunA BETWEEN :oxygenjunAFrom AND :oxygenjunATo AND "
			+ " s.oxygenPeriodJunB BETWEEN :oxygenjunBFrom AND :oxygenjunBTo AND "
			+ " s.oxygenPeriodJulA BETWEEN :oxygenjulAFrom AND :oxygenjulATo AND "
			+ " s.oxygenPeriodJulB BETWEEN :oxygenjulBFrom AND :oxygenjulBTo AND "
			+ " s.oxygenPeriodAugA BETWEEN :oxygenaugAFrom AND :oxygenaugATo AND "
			+ " s.oxygenPeriodAugB BETWEEN :oxygenaugBFrom AND :oxygenaugBTo AND "
			+ " s.oxygenPeriodSepA BETWEEN :oxygensepAFrom AND :oxygensepATo AND "
			+ " s.oxygenPeriodSepB BETWEEN :oxygensepBFrom AND :oxygensepBTo AND "
			+ " s.oxygenPeriodOctA BETWEEN :oxygenoctAFrom AND :oxygenoctATo AND "
			+ " s.oxygenPeriodOctB BETWEEN :oxygenoctBFrom AND :oxygenoctBTo AND "
			+ " s.oxygenPeriodNovA BETWEEN :oxygennovAFrom AND :oxygennovATo AND "
			+ " s.oxygenPeriodNovB BETWEEN :oxygennovBFrom AND :oxygennovBTo AND "
			+ " s.oxygenPeriodDecA BETWEEN :oxygendecAFrom AND :oxygendecATo AND "
			+ " s.oxygenPeriodDecB BETWEEN :oxygendecBFrom AND :oxygendecBTo AND "
			+ " s.oxygenPeriodYear BETWEEN :oxygenyearFrom AND :oxygenyearTo " + " ORDER BY id ASC";
	private static final Logger logger = LoggerFactory.getLogger(SiteFullUtil.class);
}
