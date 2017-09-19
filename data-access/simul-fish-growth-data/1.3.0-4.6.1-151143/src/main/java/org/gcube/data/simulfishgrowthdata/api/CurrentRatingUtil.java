package org.gcube.data.simulfishgrowthdata.api;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.i2s.fishgrowth.model.CurrentRating;

@Path("/CurrentRating")
public class CurrentRatingUtil extends BaseUtil {

	@GET
	@Path("/all")
	@Produces({ MediaType.APPLICATION_JSON })
	public List<CurrentRating> getCurrentRatings() throws Exception {
		try {
			return new org.gcube.data.simulfishgrowthdata.api.base.CurrentRatingUtil().getCurrentRatings();
		} catch (Exception e) {
			logger.info(String.format("Could not retrieve current ratings"), e);
			throw new Exception(String.format("Could not retrieve current ratings"), e);
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(CurrentRatingUtil.class);
}
