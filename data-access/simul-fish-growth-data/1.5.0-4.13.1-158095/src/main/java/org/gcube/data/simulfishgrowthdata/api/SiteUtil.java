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

import org.gcube.data.simulfishgrowthdata.util.ConnectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.i2s.fishgrowth.model.Site;
import gr.i2s.fishgrowth.model.Usage;

@Path("/Site")
public class SiteUtil extends BaseUtil {
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response add(Site site) throws Exception {
		try {
			new org.gcube.data.simulfishgrowthdata.api.base.SiteUtil().add(site);
			return Response.status(Response.Status.OK).entity(site.getId()).build();
		} catch (Exception e) {
			logger.error(String.format("Could not add site [%s]", site), e);
			return Response.status(ConnectionUtil.Status.UNPROCESSABLE_ENTITY).entity(e).build();
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(Site site) throws Exception {
		try {
			new org.gcube.data.simulfishgrowthdata.api.base.SiteUtil().update(site);
			return Response.status(Response.Status.OK).entity(site).build();
		} catch (Exception e) {
			logger.error(String.format("Could not update site [%s]", site), e);
			return Response.status(ConnectionUtil.Status.UNPROCESSABLE_ENTITY).entity(e).build();
		}
	}

	@DELETE
	@Path("/{id}")
	public Response delete(@PathParam("id") Long id) throws Exception {
		try {
			new org.gcube.data.simulfishgrowthdata.api.base.SiteUtil().delete(id);
			return Response.status(Response.Status.OK).build();
		} catch (Exception e) {
			logger.error(String.format("Could not delete site [%s]", id), e);
			return Response.status(ConnectionUtil.Status.UNPROCESSABLE_ENTITY).entity(e).build();
		}
	}

	@GET
	@Path("/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Site getSite(@PathParam("id") Long id) throws Exception {
		try {
			return new org.gcube.data.simulfishgrowthdata.api.base.SiteUtil().getSite(id);
		} catch (Exception e) {
			logger.error(String.format("Could not retrieve site [%s]", id), e);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}

	@GET
	@Path("/all/{ownerId}")
	public List<Site> getSites(@PathParam("ownerId") String ownerId) throws Exception {
		try {
			return new org.gcube.data.simulfishgrowthdata.api.base.SiteUtil().getSites(ownerId);
		} catch (Exception e) {
			logger.error(String.format("Could not retrieve sites for ownerid [%s]", ownerId), e);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}

	@GET
	@Path("/usage/{ownerId}")
	public List<Usage> getUsage(@PathParam("ownerId") String ownerId) throws Exception {
		try {
			return new org.gcube.data.simulfishgrowthdata.api.base.SiteUtil().getUsage(ownerId);
		} catch (Exception e) {
			logger.error(String.format("Could not retrieve site usage for ownerid [%s]", ownerId), e);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(SiteUtil.class);
}
