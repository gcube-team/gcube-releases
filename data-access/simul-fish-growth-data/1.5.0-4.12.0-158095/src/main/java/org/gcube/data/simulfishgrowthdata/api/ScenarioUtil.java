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

import org.gcube.data.simulfishgrowthdata.servlet.DatabaseFilter;
import org.gcube.data.simulfishgrowthdata.util.ConnectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.i2s.fishgrowth.model.Scenario;

@Path("/Scenario")
public class ScenarioUtil extends BaseUtil {
	// get from web.xml
	final public String additionalSimilarityConstraint = DatabaseFilter.additionalSimilarityConstraint;

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response add(Scenario scenario) throws Exception {
		try {
			new org.gcube.data.simulfishgrowthdata.api.base.ScenarioUtil().add(scenario);
			return Response.status(Response.Status.OK).entity(scenario.getId()).build();
		} catch (Exception e) {
			logger.error(String.format("Could not add scenario [%s]", scenario), e);
			return Response.status(ConnectionUtil.Status.UNPROCESSABLE_ENTITY).entity(e).build();
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(Scenario scenario) throws Exception {
		try {
			new org.gcube.data.simulfishgrowthdata.api.base.ScenarioUtil().update(scenario);
			return Response.status(Response.Status.OK).entity(scenario).build();
		} catch (Exception e) {
			logger.error(String.format("Could not update scenario [%s]", scenario), e);
			return Response.status(ConnectionUtil.Status.UNPROCESSABLE_ENTITY).entity(e).build();
		}
	}

	@DELETE
	@Path("/{id}")
	public Response delete(@PathParam("id") Long id) throws Exception {
		try {
			new org.gcube.data.simulfishgrowthdata.api.base.ScenarioUtil().delete(id);
			return Response.status(Response.Status.OK).build();
		} catch (Exception e) {
			logger.error(String.format("Could not delete scenario [%s]", id), e);
			return Response.status(ConnectionUtil.Status.UNPROCESSABLE_ENTITY).entity(e).build();
		}
	}

	@GET
	@Path("/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Scenario getScenario(@PathParam("id") Long id) throws Exception {
		try {
			return new org.gcube.data.simulfishgrowthdata.api.base.ScenarioUtil().getScenario(id);
		} catch (Exception e) {
			logger.error(String.format("Could not retrieve scenario [%s]", id), e);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}

	@GET
	@Path("/all/{ownerId}")
	public List<Scenario> getScenarios(@PathParam("ownerId") String ownerId) throws Exception {
		try {

			return new org.gcube.data.simulfishgrowthdata.api.base.ScenarioUtil().getScenarios(ownerId);
		} catch (Exception e) {
			logger.error(String.format("Could not retrieve scenarios for ownerid [%s]", ownerId), e);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}

	@GET
	@Path("/execute/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Scenario executeScenario(@PathParam("id") Long id) throws Exception {
		try {
			return new org.gcube.data.simulfishgrowthdata.api.base.ScenarioUtil()
					.setAdditionalSimilarityConstraint(additionalSimilarityConstraint).executeScenario(id);
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
		return new org.gcube.data.simulfishgrowthdata.api.base.ScenarioUtil()
				.setAdditionalSimilarityConstraint(additionalSimilarityConstraint)
				.executeConsumptionScenario(from, to, weight, count, modelId);
	}

	private static final String _GET_ALL_ON_OWNERID = "FROM gr.i2s.fishgrowth.model.Scenario s WHERE s.ownerId = :ownerid ORDER BY s.designation ASC";
	private static final Logger logger = LoggerFactory.getLogger(ScenarioUtil.class);
}
