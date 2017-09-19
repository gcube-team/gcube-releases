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
		try {
			return new org.gcube.data.simulfishgrowthdata.api.base.ScenarioFullUtil().getScenarioFull(id);
		} catch (Exception e) {
			logger.error(String.format("Could not retrieve full scenario for [%s]", id), e);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}

	@GET
	@Path("/all/{ownerId}/{start}/{end}")
	public List<ScenarioFull> getScenarioFulls(@PathParam("ownerId") String ownerId, @PathParam("start") Integer start,
			@PathParam("end") Integer end) throws Exception {
		try {
			return new org.gcube.data.simulfishgrowthdata.api.base.ScenarioFullUtil().getScenarioFulls(ownerId, start, end);
		} catch (Exception e) {
			logger.error(String.format("Could not retrieve full scenarios for ownerid[%s]", ownerId), e);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}

	@GET
	@Path("/all/{ownerId}")
	public List<ScenarioFull> getScenarioFulls(@PathParam("ownerId") String ownerId) throws Exception {
		try {
			return new org.gcube.data.simulfishgrowthdata.api.base.ScenarioFullUtil().getScenarioFulls(ownerId);
		} catch (Exception e) {
			logger.error(String.format("Could not retrieve full scenarios for ownerid[%s]", ownerId), e);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}

	@GET
	@Path("/count/{ownerId}")
	public int getScenarioFullCount(@PathParam("ownerId") String ownerId) throws Exception {
		try {
			return new org.gcube.data.simulfishgrowthdata.api.base.ScenarioFullUtil().getScenarioFullCount(ownerId);
		} catch (Exception e) {
			logger.error(String.format("Could not retrieve full scenario count for ownerid[%s]", ownerId), e);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}

	@GET
	@Path("/execute/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Scenario executeScenario(@PathParam("id") Long id) throws Exception {
		return new ScenarioUtil().executeScenario(id);
	}

	private static final Logger logger = LoggerFactory.getLogger(ScenarioFullUtil.class);
}
