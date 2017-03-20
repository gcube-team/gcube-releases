package org.gcube.data.simulfishgrowthdata.api;

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

import org.gcube.data.simulfishgrowthdata.util.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.i2s.fishgrowth.model.Scenario;
import gr.i2s.fishgrowth.model.ScenarioFull;

@Path("/ScenarioFull")
public class ScenarioFullUtil extends BaseUtil {
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response add(ScenarioFull scenarioFull) throws Exception {
		return new ScenarioUtil().add(new Scenario(scenarioFull));
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(ScenarioFull scenarioFull) throws Exception {
		return new ScenarioUtil().update(new Scenario(scenarioFull));
	}

	@DELETE
	@Path("/{id}")
	public Response delete(@PathParam("id") Long id) throws Exception {
		return new ScenarioUtil().delete(id);
	}

	@GET
	@Path("/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public ScenarioFull getScenarioFull(@PathParam("id") Long id) throws Exception {
		Session session = null;

		try {
			session = HibernateUtil.openSession();

			session.beginTransaction();

			ScenarioFull scenarioFull = (ScenarioFull) session.get(ScenarioFull.class, Long.valueOf(id));

			session.getTransaction().commit();

			return scenarioFull;
		} catch (Exception e) {
			logger.error(String.format("Could not retrieve full scenario for [%s]", id), e);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	@GET
	@Path("/all/{ownerId}/{start}/{end}")
	public List<ScenarioFull> getScenarioFulls(@PathParam("ownerId") String ownerId, @PathParam("start") Integer start,
			@PathParam("end") Integer end) throws Exception {
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
			logger.error(String.format("Could not retrieve full scenarios for ownerid[%s]", ownerId), e);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	@GET
	@Path("/all/{ownerId}")
	public List<ScenarioFull> getScenarioFulls(@PathParam("ownerId") String ownerId) throws Exception {
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
			logger.error(String.format("Could not retrieve full scenarios for ownerid[%s]", ownerId), e);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	@GET
	@Path("/count/{ownerId}")
	public int getScenarioFullCount(@PathParam("ownerId") String ownerId) throws Exception {
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
			logger.error(String.format("Could not retrieve full scenario count for ownerid[%s]", ownerId), e);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	@GET
	@Path("/execute/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Scenario executeScenario(@PathParam("id") Long id) throws Exception {
		return new ScenarioUtil().executeScenario(id);
	}

	// private static final String _GET_ALL_ON_OWNERID = "SELECT
	// {ScenarioFullView.*}
	// FROM ScenarioFullView WHERE ownerid = :ownerid ORDER BY designation ASC";
	private static final String _GET_ALL_ON_OWNERID = "FROM gr.i2s.fishgrowth.model.ScenarioFull s WHERE s.ownerId = :ownerid ORDER BY s.designation ASC";
	private static final String _GET_ALL_ON_OWNERID_COUNT = "SELECT count(*) FROM gr.i2s.fishgrowth.model.ScenarioFull s WHERE s.ownerId = :ownerid";
	private static final Logger logger = LoggerFactory.getLogger(ScenarioFullUtil.class);
}
