package org.gcube.data.simulfishgrowthdata.api;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.gcube.data.simulfishgrowthdata.util.DatabaseUtil;
import org.gcube.data.simulfishgrowthdata.util.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.i2s.fishgrowth.model.Site;
import gr.i2s.fishgrowth.model.SiteFull;

@Path("/SiteFull")
public class SiteFullUtil extends BaseUtil {

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response add(SiteFull siteFull) throws Exception {
		return new SiteUtil().add(new Site(siteFull));
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(SiteFull siteFull) throws Exception {
		return new SiteUtil().update(new Site(siteFull));
	}

	@DELETE
	@Path("/{id}")
	public Response delete(@PathParam("id") Long id) throws Exception {
		return new SiteUtil().delete(id);
	}

	@GET
	@Path("/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public SiteFull getSiteFull(@PathParam("id") Long id) throws Exception {
		Session session = null;

		try {
			session = HibernateUtil.openSession();

			session.beginTransaction();

			SiteFull siteFull = (SiteFull) session.get(SiteFull.class, Long.valueOf(id));

			session.getTransaction().commit();

			return siteFull;
		} catch (Exception e) {
			logger.error(String.format("Could not retrieve full site for [%s]", id), e);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	@GET
	@Path("/all/{ownerId}/{start}/{end}")
	public List<SiteFull> getSiteFulls(@PathParam("ownerId") String ownerId, @PathParam("start") Integer start,
			@PathParam("end") Integer end) throws Exception {
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("reading %s for %s start %s end %s", "SiteFull", ownerId, start, end));
		}

		Session session = null;

		try {
			logger.trace(String.format("start getSiteFulls"));
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

			List<SiteFull> list = q.list();

			session.getTransaction().commit();

			logger.trace(String.format("return SiteFulls %s", list));
			return list;
		} catch (Exception e) {
			logger.error(String.format("Could not retrieve full sites for ownerid[%s]", ownerId), e);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	@GET
	@Path("/all/{ownerId}")
	public List<SiteFull> getSiteFulls(@PathParam("ownerId") String ownerId) throws Exception {
		Session session = null;

		try {
			logger.trace(String.format("start getSiteFulls"));
			session = HibernateUtil.openSession();
			logger.trace(String.format("session [%s]", session));

			session.beginTransaction();

			Query q = session.createQuery(_GET_ALL_ON_OWNERID).setParameter("ownerid", ownerId);

			List<SiteFull> list = q.list();

			session.getTransaction().commit();

			logger.trace(String.format("return SiteFulls %s", list));
			return list;
		} catch (Exception e) {
			logger.error(String.format("Could not retrieve full sites for ownerid[%s]", ownerId), e);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	@GET
	@Path("/count/{ownerId}")
	public int getSiteFullCount(@PathParam("ownerId") String ownerId) throws Exception {
		Session session = null;

		try {
			logger.trace(String.format("start getSiteFulls"));
			session = HibernateUtil.openSession();
			logger.trace(String.format("session [%s]", session));

			session.beginTransaction();

			Query q = session.createQuery(_GET_ALL_ON_OWNERID_COUNT).setParameter("ownerid", ownerId);

			Number count = (Number) q.uniqueResult();

			session.getTransaction().commit();

			logger.trace(String.format("return count %s", count));
			return count.intValue();
		} catch (Exception e) {
			logger.error(String.format("Could not retrieve full site count for ownerid[%s]", ownerId), e);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	@GET
	@Path("/similar/{id}/{dtemp}")
	@Produces({ MediaType.APPLICATION_JSON })
	public List<Long> getSiteFullSimilar(@PathParam("id") Long id, @PathParam("dtemp") Integer dtemp) throws Exception {
		Session session = null;

		try {
			logger.trace(String.format("start getSiteFullSimilar for [%s] with dtemp [%s]", id, dtemp));

			session = HibernateUtil.openSession();
			session.beginTransaction();

			SiteFull siteFull = (SiteFull) session.get(SiteFull.class, Long.valueOf(id));
			List<Long> list = doGetSiteFullSimilar(session, siteFull, dtemp);

			session.getTransaction().commit();

			logger.trace(String.format("return similars %s", list));
			return list;
		} catch (

		Exception e) {
			logger.error(String.format("Could not retrieve similar sites for [%s] and dtemp [%s]", id, dtemp), e);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	public List<Long> doGetSiteFullSimilar(Session session, Site site, Integer dtemp) {
		List<Long> list = new ArrayList<>();
		if (site==null) {
			return list;
		}
		Query q = session.createQuery(_GET_SIMILAR).setParameter("id", site.getId())
				.setParameter("global", DatabaseUtil.GLOBAL_OWNER)
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
				.setParameter("yearFrom", site.getPeriodYear() - dtemp)
				.setParameter("yearTo", site.getPeriodYear() + dtemp);

		list = q.list();
		return list;
	}

	private static final String _GET_ALL_ON_OWNERID = "FROM gr.i2s.fishgrowth.model.SiteFull s WHERE s.ownerId = :ownerid ORDER BY s.designation ASC";
	private static final String _GET_ALL_ON_OWNERID_COUNT = "SELECT count(*) FROM gr.i2s.fishgrowth.model.SiteFull s WHERE s.ownerId = :ownerid";
	private static final String _GET_SIMILAR = "SELECT id FROM gr.i2s.fishgrowth.model.SiteFull s WHERE "
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
			+ " s.periodDecB BETWEEN :decBFrom AND :decBTo AND " + " s.periodYear BETWEEN :yearFrom AND :yearTo "
			+ " ORDER BY id ASC";
	private static final Logger logger = LoggerFactory.getLogger(SiteFullUtil.class);
}
