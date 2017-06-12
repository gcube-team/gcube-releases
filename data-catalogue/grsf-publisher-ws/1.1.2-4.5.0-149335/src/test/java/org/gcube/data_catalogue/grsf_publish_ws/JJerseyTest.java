package org.gcube.data_catalogue.grsf_publish_ws;

import java.util.ArrayList;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.gcube.data_catalogue.grsf_publish_ws.json.input.FisheryRecord;
import org.gcube.data_catalogue.grsf_publish_ws.json.input.Resource;
import org.gcube.data_catalogue.grsf_publish_ws.json.input.StockRecord;
import org.gcube.data_catalogue.grsf_publish_ws.services.GrsfPublisherFisheryService;
import org.gcube.data_catalogue.grsf_publish_ws.services.GrsfPublisherStockService;
import org.gcube.data_catalogue.grsf_publish_ws.utils.groups.Fishery_Type;
import org.gcube.data_catalogue.grsf_publish_ws.utils.groups.Sources;
import org.gcube.data_catalogue.grsf_publish_ws.utils.groups.Status;
import org.gcube.data_catalogue.grsf_publish_ws.utils.groups.Stock_Type;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;

public class JJerseyTest extends JerseyTest{

	//@Override
	protected Application configure() {
		forceSet(TestProperties.CONTAINER_PORT, "0");
		return new ResourceConfig(GrsfPublisherFisheryService.class, GrsfPublisherStockService.class).property("jersey.config.beanValidation.enableOutputValidationErrorEntity.server", true);
	}

	//@Test
	public void testFishery() {
		FisheryRecord recordFishery = new FisheryRecord();
		recordFishery.setAuthor("Costantino Perciante");
		recordFishery.setAuthorContact("costantino.perciante@isti.cnr.it");
		recordFishery.setLicense("a caso una lincense");
		recordFishery.setDataOwner("data owner");
		recordFishery.setType(Fishery_Type.Fishing_Description);
		recordFishery.setDatabaseSources(new ArrayList<Resource<Sources>>(1));
		recordFishery.setSourceOfInformation(new ArrayList<Resource<String>>(1));
		recordFishery.setStatus(Status.Pending);
		Response res = target("fishery").path("/publish-product").request().post(Entity.entity(recordFishery, MediaType.APPLICATION_JSON));
		System.out.println("Result is " + res.readEntity(String.class));
	}

	//@Test
	public void testStock() {
		StockRecord stock = new StockRecord();
		stock.setAuthor("Costantino Perciante");
		stock.setAuthorContact("costantino.perciante@isti.cnr.it");
		stock.setType(Stock_Type.Assessment_Unit);
		stock.setDatabaseSources(null);
		stock.setStatus(Status.Pending);

		Response res = target("stock").path("/publish-product").request().post(Entity.entity(stock, MediaType.APPLICATION_JSON));

		System.out.println("Result is " + res);
	}

	//@Test
	public void getLicenses(){

		Response res = target("fishery").path("/get-licenses").request().get();
		System.out.println("Result is " + res);

	}
}
