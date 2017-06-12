package org.gcube.data.simulfishgrowthdata.api;

import java.util.List;

import javax.websocket.server.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.gcube.data.simulfishgrowthdata.util.ConnectionUtil;
import org.gcube.data.simulfishgrowthdata.util.DatabaseUtil;
import org.gcube.data.simulfishgrowthdata.util.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.LongType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.i2s.fishgrowth.model.SimilarSite;
import gr.i2s.fishgrowth.model.Site;

public class SimilarSiteUtil extends BaseUtil {

	public Response add(SimilarSite entity) throws Exception {
		Session session = null;

		try {
			session = HibernateUtil.openSession();
			session.beginTransaction();

			session.save(entity);

			session.flush();
			session.getTransaction().commit();
			return Response.status(Response.Status.OK).entity(entity.getId()).build();
		} catch (Exception e) {
			logger.error(String.format("Could not add [%s]", entity), e);
			return Response.status(ConnectionUtil.Status.UNPROCESSABLE_ENTITY).entity(e).build();
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	public List<Long> getSimilarSites(@PathParam("siteId") Long siteId) throws Exception {
		Session session = null;

		try {
			session = HibernateUtil.openSession();
			session.beginTransaction();

			List<Long> list = doGetSimilarSites(session, siteId);

			session.getTransaction().commit();

			return list;
		} catch (Exception e) {
			logger.error("Could not retrieve", e);
			throw e;
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	public List<Long> getSimilarSitesExcludingMe(@PathParam("siteId") Long siteId) throws Exception {
		Session session = null;

		try {
			session = HibernateUtil.openSession();
			session.beginTransaction();

			List<Long> list = doGetSimilarSitesExcludingMe(session, siteId);

			session.getTransaction().commit();

			return list;
		} catch (Exception e) {
			logger.error("Could not retrieve", e);
			throw e;
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	public Response delete(@PathParam("siteId") Long siteId) throws Exception {
		Session session = null;

		try {
			session = HibernateUtil.openSession();
			session.beginTransaction();

			Query q = session.createSQLQuery(_DELETE_ALL).setParameter("siteId", siteId);

			q.executeUpdate();

			session.flush();
			session.getTransaction().commit();
			return Response.status(Response.Status.OK).build();
		} catch (Exception e) {
			logger.error(String.format("Could not delete similar sites for site [%s]", siteId), e);
			return Response.status(ConnectionUtil.Status.UNPROCESSABLE_ENTITY).entity(e).build();
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	// @DELETE
	// @Path("/{id}/{similarId}")
	public Response delete(@PathParam("siteId") Long siteId, @PathParam("similarId") Long similarId) throws Exception {
		Session session = null;

		try {
			session = HibernateUtil.openSession();
			session.beginTransaction();

			doDelete(session, siteId, similarId);

			session.flush();
			session.getTransaction().commit();

			return Response.status(Response.Status.OK).build();
		} catch (Exception e) {
			logger.error(String.format("Could not delete similar sites for site [%s]", siteId), e);
			return Response.status(ConnectionUtil.Status.UNPROCESSABLE_ENTITY).entity(e).build();
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	public void doDelete(Session session, Long siteId, Long similarId) {
		Query q = session.createSQLQuery(_DELETE_KEY).setParameter("siteId", siteId).setParameter("similarId",
				similarId);
		q.executeUpdate();
	}

	public List<Long> doGetSimilarSitesExcludingMe(Session session, Long siteId) {
		Query q = session.createSQLQuery(_GET_ALL_BUT_ME).addScalar("similarId", LongType.INSTANCE)
				.setParameter("siteId", siteId).setParameter("global", DatabaseUtil.GLOBAL_OWNER);
		List<Long> list = q.list();
		return list;
	}

	public List<Long> doGetSimilarSites(Session session, Long siteId) {
		Query q = session.createSQLQuery(_GET_ALL).addScalar("similarId", LongType.INSTANCE)
				.setParameter("siteId", siteId).setParameter("global", DatabaseUtil.GLOBAL_OWNER);
		List<Long> list = q.list();
		return list;
	}

	private static final String _GET_ALL = "SELECT DISTINCT s.similarId FROM SimilarSite s INNER JOIN Site sl on (s.siteid=sl.id) AND sl.ownerid<>:global INNER JOIN Site sr on (s.similarid=sr.id) AND sr.ownerid<>:global WHERE siteId=:siteId ORDER BY s.similarId ASC";
	private static final String _GET_ALL_BUT_ME = "SELECT DISTINCT s.similarId FROM SimilarSite s INNER JOIN Site sl on (s.siteid=sl.id) AND sl.ownerid<>:global INNER JOIN Site sr on (s.similarid=sr.id) AND sr.ownerid<>:global WHERE siteId=:siteId AND similarId<>siteId ORDER BY s.similarId ASC";
	private static final String _DELETE_ALL = "DELETE FROM SimilarSite WHERE siteId=:siteId OR similarSiteId=:siteId";
	private static final String _DELETE_KEY = "DELETE FROM SimilarSite WHERE siteId=:siteId AND similarId=:similarId";
	private static final Logger logger = LoggerFactory.getLogger(SimilarSiteUtil.class);

}
