package org.gcube.data.simulfishgrowthdata.api;

import java.util.List;

import javax.websocket.server.PathParam;
import javax.ws.rs.core.Response;

import org.gcube.data.simulfishgrowthdata.util.ConnectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.i2s.fishgrowth.model.SimilarSite;

public class SimilarSiteUtil extends BaseUtil {

	public Response add(SimilarSite entity) throws Exception {
		try {
			entity = new org.gcube.data.simulfishgrowthdata.api.base.SimilarSiteUtil().add(entity);
			return Response.status(Response.Status.OK).entity(entity.getId()).build();
		} catch (Exception e) {
			logger.info(String.format("Could not add similar sites [%s]", entity), e);
			return Response.status(ConnectionUtil.Status.UNPROCESSABLE_ENTITY).entity(e).build();
		}
	}

	public List<Long> getSimilarSites(@PathParam("siteId") Long siteId) throws Exception {
		try {
			return new org.gcube.data.simulfishgrowthdata.api.base.SimilarSiteUtil().getSimilarSites(siteId);
		} catch (Exception e) {
			logger.info(String.format("Could not retrieve similar sites for siteid [%s]", siteId), e);
			throw new Exception(String.format("Could not retrieve similar sites for siteid [%s]", siteId), e);
		}
	}

	public List<Long> getSimilarSitesExcludingMe(@PathParam("siteId") Long siteId) throws Exception {
		try {
			return new org.gcube.data.simulfishgrowthdata.api.base.SimilarSiteUtil().getSimilarSitesExcludingMe(siteId);
		} catch (Exception e) {
			logger.info(String.format("Could not retrieve similar sites exc me for siteid [%s]", siteId), e);
			throw new Exception(String.format("Could not retrieve similar sites exc me for siteid [%s]", siteId), e);
		}
	}

	public Response delete(@PathParam("siteId") Long siteId) throws Exception {
		try {
			new org.gcube.data.simulfishgrowthdata.api.base.SimilarSiteUtil().delete(siteId);
			return Response.status(Response.Status.OK).build();
		} catch (Exception e) {
			logger.info(String.format("Could not delete similar sites for siteid [%s]", siteId), e);
			return Response.status(ConnectionUtil.Status.UNPROCESSABLE_ENTITY).entity(e).build();
		}
	}

	// @DELETE
	// @Path("/{id}/{similarId}")
	public Response delete(@PathParam("siteId") Long siteId, @PathParam("similarId") Long similarId) throws Exception {
		try {
			new org.gcube.data.simulfishgrowthdata.api.base.SimilarSiteUtil().delete(siteId, similarId);
			return Response.status(Response.Status.OK).build();
		} catch (Exception e) {
			logger.info(String.format("Could not delete similar sites for siteid [%s] similarid [%s]", siteId, similarId), e);
			return Response.status(ConnectionUtil.Status.UNPROCESSABLE_ENTITY).entity(e).build();
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(SimilarSiteUtil.class);

}
