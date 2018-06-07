package test.application;

import static app.Request.request;
import static org.gcube.smartgears.Constants.application_xml;
import static org.gcube.smartgears.Constants.content_type;
import static org.gcube.smartgears.extensions.HttpExtension.Method.GET;
import static org.gcube.smartgears.extensions.HttpExtension.Method.POST;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.Resources;
import org.gcube.smartgears.Constants;
import org.gcube.smartgears.context.application.ApplicationContext;
import org.gcube.smartgears.extensions.resource.ConfigurationResource;
import org.gcube.smartgears.extensions.resource.FrontPageResource;
import org.gcube.smartgears.extensions.resource.LifecycleResource;
import org.gcube.smartgears.extensions.resource.LifecycleResource.State;
import org.gcube.smartgears.extensions.resource.ProfileResource;
import org.gcube.smartgears.lifecycle.application.ApplicationState;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import app.Request;
import app.SomeApp;

public class RemoteResourceTest {

	static final String path = "/resource";
	static JAXBContext jaxb;
	static ApplicationContext context;
	
	static SomeApp app;

	@BeforeClass
	public static void setup() throws Exception {

		jaxb = JAXBContext.newInstance(State.class);

		app = new SomeApp();

		app.useDefaultHandlers();
		app.useDefaultExtensions();

		context = app.start();
	}

	@Test
	public void showsFrontpage() throws Exception {

		// unscoped request
		Request request = request().at(resource(FrontPageResource.mapping)).inScope(null);

		app.send(request);
		
		//Thread.sleep(50000); enable to check interactively in browser
	}

	@Test
	public void showsConfiguration() throws Exception {

		//unscoped request
		Request request = request().at(resource(ConfigurationResource.mapping)).inScope(null);

		app.send(request);
	}
	
	@Test
	public void showsProfile() throws Exception {

		//unscoped request
		Request request = request().at(resource(ProfileResource.mapping)).inScope(null);

		String outcome = app.send(request);

		GCoreEndpoint profile = Resources.unmarshal(GCoreEndpoint.class, new StringReader(outcome));

		assertEquals(context.profile(GCoreEndpoint.class).id(), profile.id());
		assertEquals(context.profile(GCoreEndpoint.class).profile().deploymentData().status(), profile.profile().deploymentData().status());
	}
	




	@Test
	public void currentState() throws Exception {

		Request request = request().at(resource(LifecycleResource.mapping)).using(GET).inScope(null);

		String outcome = app.send(request);
		
		State state = (State) jaxb.createUnmarshaller().unmarshal(new StringReader(outcome));

		assertEquals(context.lifecycle().state(),ApplicationState.valueOf(state.value));

	}
	
	@Ignore
	@Test
	public void changeState() throws Exception {

		ApplicationState newstate = ApplicationState.stopped;

		assertFalse(context.lifecycle().state()==newstate);

		Request request = request().at(resource(LifecycleResource.mapping)).using(POST).inScope(null)
				.with(content_type, application_xml).with("<state>stopped</state>");

		app.httpSend(request);

		assertTrue(context.lifecycle().state()==newstate);

		request = request().at(resource(LifecycleResource.mapping)).using(POST).inScope(null)
				.with(content_type, application_xml).with("<state>active</state>");
		
	}

	// helper
	private String resource(String resource) {
		return Constants.root_mapping + path + resource;
	}
}
