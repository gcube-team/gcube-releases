package org.gcube.data.spd;

import javax.ws.rs.ApplicationPath;

import org.gcube.data.spd.model.Constants;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath(Constants.APPLICATION_ROOT_PATH)
public class SpeciesProductsDiscovery extends ResourceConfig {

	public SpeciesProductsDiscovery() {
		packages("org.gcube.data.spd.resources");
	}

}