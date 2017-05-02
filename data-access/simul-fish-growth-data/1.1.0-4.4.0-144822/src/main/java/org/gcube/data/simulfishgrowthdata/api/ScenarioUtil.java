package org.gcube.data.simulfishgrowthdata.api;

import java.text.SimpleDateFormat;
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

import org.gcube.data.simulfishgrowthdata.calc.ConsumptionScenarioExecutor;
import org.gcube.data.simulfishgrowthdata.calc.ScenarioExecutor;
import org.gcube.data.simulfishgrowthdata.util.ConnectionUtil;
import org.gcube.data.simulfishgrowthdata.util.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.i2s.fishgrowth.model.Scenario;

@Path("/Scenario")
public class ScenarioUtil extends BaseUtil {
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response add(Scenario scenario) throws Exception {
		Session session = null;

		try {
			session = HibernateUtil.openSession();

			session.beginTransaction();

			session.save(scenario);

			session.flush();

			session.getTransaction().commit();
			return Response.status(Response.Status.OK).entity(scenario.getId()).build();
		} catch (Exception e) {
			logger.error(String.format("Could not add scenario [%s]", scenario), e);
			return Response.status(ConnectionUtil.Status.UNPROCESSABLE_ENTITY).entity(e).build();
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(Scenario scenario) throws Exception {
		Session session = null;

		try {
			session = HibernateUtil.openSession();

			session.beginTransaction();

			session.update(scenario);

			session.flush();

			session.getTransaction().commit();
			return Response.status(Response.Status.OK).entity(scenario).build();
		} catch (Exception e) {
			logger.error(String.format("Could not update scenario [%s]", scenario), e);
			return Response.status(ConnectionUtil.Status.UNPROCESSABLE_ENTITY).entity(e).build();
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	@DELETE
	@Path("/{id}")
	public Response delete(@PathParam("id") Long id) throws Exception {
		Session session = null;

		try {
			session = HibernateUtil.openSession();

			session.beginTransaction();

			Scenario scenario = (Scenario) session.get(Scenario.class, Long.valueOf(id));

			if (scenario != null) {
				session.delete(scenario);

				session.flush();
			}

			session.getTransaction().commit();
			return Response.status(Response.Status.OK).build();
		} catch (Exception e) {
			logger.error(String.format("Could not delete scenario [%s]", id), e);
			return Response.status(ConnectionUtil.Status.UNPROCESSABLE_ENTITY).entity(e).build();
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	@GET
	@Path("/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Scenario getScenario(@PathParam("id") Long id) throws Exception {
		Session session = null;

		try {
			session = HibernateUtil.openSession();

			session.beginTransaction();

			Scenario scenario = (Scenario) session.get(Scenario.class, Long.valueOf(id));

			session.getTransaction().commit();

			return scenario;
		} catch (Exception e) {
			logger.error(String.format("Could not retrieve scenario [%s]", id), e);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	@GET
	@Path("/all/{ownerId}")
	public List<Scenario> getScenarios(@PathParam("ownerId") String ownerId) throws Exception {
		Session session = null;

		try {
			logger.trace(String.format("start getScenarios"));
			session = HibernateUtil.openSession();
			logger.trace(String.format("session [%s]", session));

			session.beginTransaction();

			Query q = session.createQuery(_GET_ALL_ON_OWNERID).setParameter("ownerid", ownerId);

			List<Scenario> list = q.list();

			session.getTransaction().commit();

			logger.trace(String.format("return Scenarios %s", list));
			return list;
		} catch (Exception e) {
			logger.error(String.format("Could not retrieve scenarios for ownerid [%s]", ownerId), e);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	@GET
	@Path("/execute/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Scenario executeScenario(@PathParam("id") Long id) throws Exception {
		try {
			Scenario scenario = getScenario(id);
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("For [%s] I loaded [%s]", id, scenario));
			}
			new ScenarioExecutor(scenario).run();
			return scenario;
		} catch (Exception e) {
			logger.error(String.format("Could not execute scenario [%s]", id), e);
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	@GET
	@Path("/execute/consumption/{from}/{to}/{weight}/{count}/{modelid}")
	@Produces({ MediaType.APPLICATION_JSON })
	public String executeConsumptionScenario(@PathParam("from") String from, @PathParam("to") String to,
			@PathParam("weight") Integer weight, @PathParam("count") Integer count, @PathParam("modelid") Long modelId)
			throws Exception {
		SimpleDateFormat df = new SimpleDateFormat("yyMMdd");
		Scenario scenario = new Scenario();
		scenario.setStartDate(df.parse(from));
		scenario.setTargetDate(df.parse(to));
		scenario.setWeight(weight / 100.0);
		scenario.setFishNo(count);
		scenario.setModelerId(modelId);
		ConsumptionScenarioExecutor executor = new ConsumptionScenarioExecutor(scenario);
		executor.run();
		return scenario.getResultsGraphData();
	}

	private static final String _GET_ALL_ON_OWNERID = "FROM gr.i2s.fishgrowth.model.Scenario s WHERE s.ownerId = :ownerid ORDER BY s.designation ASC";
	private static final Logger logger = LoggerFactory.getLogger(ScenarioUtil.class);
}
