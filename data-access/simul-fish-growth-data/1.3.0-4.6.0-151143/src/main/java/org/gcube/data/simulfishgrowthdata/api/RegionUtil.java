package org.gcube.data.simulfishgrowthdata.api;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.i2s.fishgrowth.model.Region;

@Path("/Region")
public class RegionUtil extends BaseUtil {

	@GET
	@Path("/all")
	@Produces({ MediaType.APPLICATION_JSON })
	public List<Region> getRegions() throws Exception {
		try {
			return new org.gcube.data.simulfishgrowthdata.api.base.RegionUtil().getRegions();
		} catch (Exception e) {
			logger.error("Could not retrieve regions", e);
			throw new Exception("Could not retrieve regions", e);
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(RegionUtil.class);
}
