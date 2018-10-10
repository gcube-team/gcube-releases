package org.gcube.data.simulfishgrowthdata.api;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.i2s.fishgrowth.model.Modeler;
import gr.i2s.fishgrowth.model.ModelerFull;

@Path("/ModelerFull")
public class ModelerFullUtil extends BaseUtil {

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response add(ModelerFull modelerFull, @DefaultValue("1") @QueryParam("createSamples") Long createSamples)
			throws Exception {
		System.out.println("createSamples " + createSamples);
		return new ModelerUtil().add(new Modeler(modelerFull), createSamples);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(ModelerFull modelerFull, @DefaultValue("1") @QueryParam("createSamples") Long createSamples)
			throws Exception {
		System.out.println("createSamples " + createSamples);
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("original %s", modelerFull));
		}

		Modeler copy = new Modeler(modelerFull);
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("copy %s", copy));
		}

		return new ModelerUtil().update(copy, createSamples);
	}

	@DELETE
	@Path("/{id}")
	public Response delete(@PathParam("id") Long id) throws Exception {
		return new ModelerUtil().delete(id);
	}

	@GET
	@Path("/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public ModelerFull getModelerFull(@PathParam("id") Long id) throws Exception {
		try {
			return new org.gcube.data.simulfishgrowthdata.api.base.ModelerFullUtil().getModelerFull(id);
		} catch (Exception e) {
			logger.error(String.format("Could not retrieve full modeler for [%s]", id), e);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}

	@GET
	@Path("/all/{ownerId}/{start}/{end}")
	public List<ModelerFull> getModelerFulls(@PathParam("ownerId") String ownerId, @PathParam("start") Integer start,
			@PathParam("end") Integer end, @QueryParam("status") List<Long> status, @QueryParam("species") Long species)
			throws Exception {
		try {
			return new org.gcube.data.simulfishgrowthdata.api.base.ModelerFullUtil().getModelerFulls(ownerId, start,
					end, status, species);
		} catch (Exception e) {
			logger.error(String.format("Could not retrieve full modeler for ownerid [%s]", ownerId), e);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}

	@GET
	@Path("/all/{ownerId}")
	public List<ModelerFull> getModelerFulls(@PathParam("ownerId") String ownerId,
			@QueryParam("status") List<Long> status, @QueryParam("species") Long species) throws Exception {
		try {
			return new org.gcube.data.simulfishgrowthdata.api.base.ModelerFullUtil().getModelerFulls(ownerId, status,
					species);
		} catch (Exception e) {
			logger.error(String.format("Could not retrieve full modeler for ownerid[%s]", ownerId), e);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}

	@GET
	@Path("/count/{ownerId}")
	public int getModelerFullCount(@PathParam("ownerId") String ownerId, @QueryParam("species") Long species)
			throws Exception {
		try {
			return new org.gcube.data.simulfishgrowthdata.api.base.ModelerFullUtil().getModelerFullCount(ownerId,
					species);
		} catch (Exception e) {
			logger.error(String.format("Could not retrieve full modeler count for ownerid[%s]", ownerId), e);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(ModelerFullUtil.class);
}
