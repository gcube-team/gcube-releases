package org.gcube.resources.federation.fhnmanager.occopus;

import java.util.Map;
import java.util.Set;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import org.gcube.resources.federation.fhnmanager.api.type.OccopusInstanceSet;
import org.gcube.resources.federation.fhnmanager.api.type.ServiceProfile;
import org.gcube.resources.federation.fhnmanager.occopus.model.CreateInfraResponse;
import org.gcube.resources.federation.fhnmanager.occopus.model.GetInfraResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OccopusClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(OccopusClient.class);

	private WebTarget client;

	public OccopusClient(String url) {
		Client client = ClientBuilder.newClient();
		this.client = client.target(url);
	}

	public CreateInfraResponse createInfrastructure(String description) {
		LOGGER.debug("Submitting infrastructure:\n" + description);
		return this.client.path("infrastructures/").request().accept("application/json").post(Entity.json(description),
				CreateInfraResponse.class);

	}

	public GetInfraResponse getInfrastructure(String id) {
		LOGGER.debug("Returning details for infrastructure:\n" + id);
		Map<String, OccopusInstanceSet> r = null;
		try {

			r = this.client.path("infrastructures").path(id).request().accept("application/json")
					.get(new GenericType<Map<String, OccopusInstanceSet>>() {
					});
		} catch (Exception ect) {
			return null;
		}

		GetInfraResponse res = new GetInfraResponse();
		res.setInstanceSets(r);

		return res;
	}

}
