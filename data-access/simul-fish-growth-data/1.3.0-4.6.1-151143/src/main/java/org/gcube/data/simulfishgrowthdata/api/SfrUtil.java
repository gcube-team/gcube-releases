package org.gcube.data.simulfishgrowthdata.api;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.gcube.data.simulfishgrowthdata.util.ConnectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.i2s.fishgrowth.model.Sfr;

@Path("/Sfr")
public class SfrUtil extends BaseUtil {

	@GET
	@Path("/all")
	public List<Sfr> getSfrs(@PathParam("modelId") Long modelId) throws Exception {
		try {
			return new org.gcube.data.simulfishgrowthdata.api.base.SfrUtil().getSfrs(modelId);
		} catch (Exception e) {
			logger.info(String.format("Could not retrieve sfr for modelid [%s]", modelId), e);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}

	@DELETE
	@Path("/{id}")
	public Response deleteAll(@PathParam("modelId") Long modelId) {
		try {
			int count = new org.gcube.data.simulfishgrowthdata.api.base.SfrUtil().deleteAll(modelId);
			return Response.status(Response.Status.OK).entity(count).build();
		} catch (Exception e) {
			logger.info(String.format("Could not delete sfr for modelid [%s]", modelId), e);
			return Response.status(ConnectionUtil.Status.UNPROCESSABLE_ENTITY).entity(e).build();
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(SfrUtil.class);
}
