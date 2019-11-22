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

import org.gcube.data.simulfishgrowthdata.util.ConnectionUtil;
import org.gcube.data.simulfishgrowthdata.util.UserFriendlyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

import gr.i2s.fishgrowth.model.Modeler;
import gr.i2s.fishgrowth.model.Usage;

@Path("/Modeler")
public class ModelerUtil extends BaseUtil {
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response add(Modeler modeler, @DefaultValue("1") @QueryParam("createSamples") Long createSamples)
			throws Exception {
		System.out.println("createSamples " + createSamples);
		try {
			modeler = new org.gcube.data.simulfishgrowthdata.api.base.ModelerUtil().add(modeler, createSamples != 0);
			return Response.status(Response.Status.OK).entity(modeler.getId()).build();
		} catch (Exception e) {
			logger.error(String.format("Could not add modeler [%s]", modeler), e);
			return Response.status(ConnectionUtil.Status.UNPROCESSABLE_ENTITY)
					.entity(Joiner.on(" ~ ").skipNulls().join(UserFriendlyException.getFriendlyTraceFrom(e))).build();
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(Modeler modeler, @DefaultValue("1") @QueryParam("createSamples") Long createSamples)
			throws Exception {
		System.out.println("createSamples " + createSamples);
		try {
			modeler = new org.gcube.data.simulfishgrowthdata.api.base.ModelerUtil().update(modeler, createSamples != 0);
			return Response.status(Response.Status.OK).entity(modeler).build();
		} catch (Exception e) {
			logger.error(String.format("Could not update modeler [%s]", modeler), e);
			return Response.status(ConnectionUtil.Status.UNPROCESSABLE_ENTITY)
					.entity(Joiner.on(" ~ ").skipNulls().join(UserFriendlyException.getFriendlyTraceFrom(e))).build();
		}
	}

	@DELETE
	@Path("/{id}")
	public Response delete(@PathParam("id") Long id) throws Exception {
		try {
			new org.gcube.data.simulfishgrowthdata.api.base.ModelerUtil().delete(id);
			return Response.status(Response.Status.OK).build();
		} catch (Exception e) {
			logger.error(String.format("Could not delete modeler [%s]", id), e);
			return Response.status(ConnectionUtil.Status.UNPROCESSABLE_ENTITY).entity(e).build();
		}
	}

	@GET
	@Path("/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Modeler getModeler(@PathParam("id") Long id) throws Exception {
		try {
			return new org.gcube.data.simulfishgrowthdata.api.base.ModelerUtil().getModeler(id);
		} catch (Exception e) {
			logger.error(String.format("Could not retrieve modeler [%s]", id), e);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}

	@GET
	@Path("/all/{ownerId}")
	public List<Modeler> getModelers(@PathParam("ownerId") String ownerId, @QueryParam("statuses") List<Long> statuses)
			throws Exception {
		try {
			return new org.gcube.data.simulfishgrowthdata.api.base.ModelerUtil().getModelers(ownerId, statuses);
		} catch (Exception e) {
			logger.error(String.format("Could not retrieve modelers for [%s]", ownerId), e);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}

	@DELETE
	@Path("/kpi/{id}")
	public void cleanKPIs(@PathParam("id") Long id) {
		try {
			new org.gcube.data.simulfishgrowthdata.api.base.ModelerUtil().cleanKPIs(id);
		} catch (Exception e) {
			logger.error(String.format("Could not clean KPIs for model [%s]", id), e);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}

	}

	@GET
	@Path("/usage/{ownerId}")
	public List<Usage> getUsage(@PathParam("ownerId") String ownerId) throws Exception {
		try {
			return new org.gcube.data.simulfishgrowthdata.api.base.ModelerUtil().getUsage(ownerId);
		} catch (Exception e) {
			logger.error(String.format("Could not retrieve site usage for ownerid [%s]", ownerId), e);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(ModelerUtil.class);

}
