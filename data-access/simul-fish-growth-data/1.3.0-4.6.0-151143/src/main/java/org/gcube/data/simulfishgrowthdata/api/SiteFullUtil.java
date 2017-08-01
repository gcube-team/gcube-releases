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

import gr.i2s.fishgrowth.model.Site;
import gr.i2s.fishgrowth.model.SiteFull;

@Path("/SiteFull")
public class SiteFullUtil extends BaseUtil {

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response add(SiteFull siteFull) throws Exception {
		return new SiteUtil().add(siteFull);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(SiteFull siteFull) throws Exception {
		return new SiteUtil().update(new Site(siteFull));
	}

	@DELETE
	@Path("/{id}")
	public Response delete(@PathParam("id") Long id) throws Exception {
		return new SiteUtil().delete(id);
	}

	@GET
	@Path("/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public SiteFull getSiteFull(@PathParam("id") Long id) throws Exception {
		try {
			return new org.gcube.data.simulfishgrowthdata.api.base.SiteFullUtil().getSiteFull(id);
		} catch (Exception e) {
			logger.error(String.format("Could not get site full [%s]", id), e);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}

	@GET
	@Path("/all/{ownerId}/{start}/{end}")
	public List<SiteFull> getSiteFulls(@PathParam("ownerId") String ownerId, @PathParam("start") Integer start,
			@PathParam("end") Integer end) throws Exception {
		try {
			return new org.gcube.data.simulfishgrowthdata.api.base.SiteFullUtil().getSiteFulls(ownerId, start, end);
		} catch (Exception e) {
			logger.error(String.format("Could not get site full for [%s]", ownerId), e);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}

	@GET
	@Path("/all/{ownerId}")
	public List<SiteFull> getSiteFulls(@PathParam("ownerId") String ownerId) throws Exception {
		try {
			return new org.gcube.data.simulfishgrowthdata.api.base.SiteFullUtil().getSiteFulls(ownerId);
		} catch (Exception e) {
			logger.error(String.format("Could not get site full for [%s]", ownerId), e);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}

	@GET
	@Path("/count/{ownerId}")
	public int getSiteFullCount(@PathParam("ownerId") String ownerId) throws Exception {
		try {
			return new org.gcube.data.simulfishgrowthdata.api.base.SiteFullUtil().getSiteFullCount(ownerId);
		} catch (Exception e) {
			logger.error(String.format("Could not get site full count for [%s]", ownerId), e);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}

	@GET
	@Path("/similar/{id}/{dtemp}")
	@Produces({ MediaType.APPLICATION_JSON })
	public List<Long> getSiteFullSimilar(@PathParam("id") Long id, @PathParam("dtemp") Integer dtemp,
			@PathParam("doxygen") Integer doxygen) throws Exception {
		try {
			return new org.gcube.data.simulfishgrowthdata.api.base.SiteFullUtil().getSiteFullSimilar(id, dtemp,
					doxygen);
		} catch (Exception e) {
			logger.error(String.format("Could not get site full similar for [%s] dtemp [%s] doxygen [%s]", id, dtemp,
					doxygen), e);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(SiteFullUtil.class);
}
