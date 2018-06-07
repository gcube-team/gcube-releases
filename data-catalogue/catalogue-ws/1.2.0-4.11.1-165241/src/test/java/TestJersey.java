import java.io.File;
import java.io.IOException;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.ParseException;
import org.gcube.datacatalogue.catalogue.ws.ItemProfile;
import org.gcube.datacatalogue.catalogue.ws.Resource;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.json.simple.JSONObject;
import org.slf4j.LoggerFactory;

public class TestJersey extends JerseyTest {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TestJersey.class);

	//@Override
	protected Application configure() {
		logger.info("Configuring service...");
		forceSet(TestProperties.CONTAINER_PORT, "0");
		final ResourceConfig resourceConfig = new ResourceConfig(Resource.class, ItemProfile.class);
		resourceConfig.register(MultiPartFeature.class);
		return resourceConfig;
	}

	//@Override
	public void configureClient(ClientConfig config) {
		logger.info("Configuring client...");
		config.register(MultiPartFeature.class);
	}

	//@Test
	@SuppressWarnings("unchecked")
	public void test() throws ParseException, IOException {

		JSONObject obj = new JSONObject();
		obj.put("test", "value");
		final JSONObject createResource = target("api/resources/create/")
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.post(Entity.json(obj), JSONObject.class);
		logger.info(createResource.toJSONString());
	}

	//@Test
	public void testFile() throws ParseException, IOException {


		FileDataBodyPart fileDataBodyPart = new FileDataBodyPart("uploadFile", 
				new File("/Users/costantinoperciante/Desktop/rilascio_tess.doc"));

		final MultiPart multipart = new FormDataMultiPart()
		.field("foo", "bar")
		.bodyPart(fileDataBodyPart);

		final Response createResource = 
				target("api/resources/create/")
				.request()
				.post(Entity.entity(multipart, multipart.getMediaType()));
		logger.info(createResource.toString());
		//
		multipart.close();
	}

	//@Test
	public void testProfilesNames() throws ParseException, IOException {


		FileDataBodyPart fileDataBodyPart = new FileDataBodyPart("uploadFile", 
				new File("/Users/costantinoperciante/Desktop/rilascio_tess.doc"));

		final MultiPart multipart = new FormDataMultiPart()
		.field("foo", "bar")
		.bodyPart(fileDataBodyPart);

		final Response createResource = 
				target("api/resources/create/")
				.request()
				.post(Entity.entity(multipart, multipart.getMediaType()));
		logger.info(createResource.toString());
		//
		multipart.close();
	}

	//@Test
	public void testProfileNames() throws ParseException, IOException {

		final String profiles = 
				target("api/profiles/profile_names/")
				.queryParam("context", "/gcube/devNext/NextNext")
				.request()
				.get(String.class);

		logger.info("Response is " + profiles);

	}
	
	//@Test
	public void testProfileByName() throws ParseException, IOException {

		final String profiles = 
				target("api/profiles/profile/")
				.queryParam("context", "/gcube/devNext/NextNext")
				.queryParam("name", "SoBigData.eu: Dataset Metadata NextNext")
				.request(MediaType.APPLICATION_JSON)
				.get(String.class);

		logger.info("Response is " + profiles);

	}
	
	
	
}
