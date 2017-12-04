package org.gcube.data.simulfishgrowthdata.api;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.i2s.fishgrowth.model.Species;

@Path("/Species")
public class SpeciesUtil extends BaseUtil {

	@GET
	@Path("/all")
	@Produces({ MediaType.APPLICATION_JSON })
	public List<Species> getSpecieses() throws Exception {
		try {
			return new org.gcube.data.simulfishgrowthdata.api.base.SpeciesUtil().getSpecieses();
		} catch (Exception e) {
			logger.error("Could not retrieve", e);
			throw e;
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(SpeciesUtil.class);
}
