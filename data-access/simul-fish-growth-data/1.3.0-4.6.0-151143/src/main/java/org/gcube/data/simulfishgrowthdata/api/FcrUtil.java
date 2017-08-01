package org.gcube.data.simulfishgrowthdata.api;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.gcube.data.simulfishgrowthdata.util.ConnectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.i2s.fishgrowth.model.Fcr;

@Path("/Fcr")
public class FcrUtil extends BaseUtil {

	@GET
	@Path("/all")
	@Produces({ MediaType.APPLICATION_JSON })
	public List<Fcr> getFcrs(@PathParam("modelId") Long modelId) throws Exception {
		try {
			return new org.gcube.data.simulfishgrowthdata.api.base.FcrUtil().getFcrs(modelId);
		} catch (Exception e) {
			logger.error(String.format("Could not retrieve fcr for modelid [%s]", modelId), e);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}

	@DELETE
	@Path("/{modelId}")
	public Response deleteAll(@PathParam("modelId") Long modelId) {
		try {
			int count = new org.gcube.data.simulfishgrowthdata.api.base.FcrUtil().deleteAll(modelId);
			return Response.status(Response.Status.OK).entity(count).build();
		} catch (Exception e) {
			logger.error("Could not delete", e);
			return Response.status(ConnectionUtil.Status.UNPROCESSABLE_ENTITY).entity(e).build();
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(FcrUtil.class);
}
