package test.application;

import static app.Request.*;
import static org.gcube.smartgears.Constants.*;
import static org.gcube.smartgears.extensions.HttpExtension.Method.*;
import static org.junit.Assert.*;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBContext;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.Resources;
import org.gcube.smartgears.context.application.ApplicationContext;
import org.gcube.smartgears.extensions.resource.ConfigurationResource;
import org.gcube.smartgears.extensions.resource.FrontPageResource;
import org.gcube.smartgears.extensions.resource.LifecycleResource;
import org.gcube.smartgears.extensions.resource.LifecycleResource.State;
import org.gcube.smartgears.extensions.resource.ProfileResource;
import org.gcube.smartgears.extensions.resource.ScopesResource;
import org.gcube.smartgears.extensions.resource.ScopesResource.Scope;
import org.gcube.smartgears.extensions.resource.ScopesResource.Scopes;
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

		jaxb = JAXBContext.newInstance(Scopes.class, Scope.class, State.class);

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
	public void showsScopes() throws Exception {

		
		Request request = request().at(resource(ScopesResource.mapping));

		String outcome = app.send(request);

		Scopes scopes = (Scopes) jaxb.createUnmarshaller().unmarshal(new StringReader(outcome));

		Set<String> expectedUnordered = new HashSet<String>(context.profile(GCoreEndpoint.class).scopes().asCollection());
		Set<String> actualUnordered = new HashSet<String>(scopes.values);

		assertEquals(expectedUnordered, actualUnordered);
	}

	@Test
	public void addScope() throws Exception {
				
		String newscope = "/new/scope";
		
		assertFalse(context.profile(GCoreEndpoint.class).scopes().contains(newscope));

		StringWriter writer = new StringWriter();
		jaxb.createMarshaller().marshal(new Scope(newscope), writer);

		Request request = request().at(resource(ScopesResource.mapping)).using(POST)
				.with(content_type, application_xml).with(writer.toString());

		app.httpSend(request);

		assertTrue(context.profile(GCoreEndpoint.class).scopes().contains(newscope));

	}
	

	

	@Ignore
	// until removeScope is committed on commmon-gcore-resources, discuss first.
	@Test
	public void removeScope() throws Exception {
		
		String scope = "/gcube/devsec";

		assertTrue(context.profile(GCoreEndpoint.class).scopes().contains(scope));

		Scope wrapper = new Scope(scope);
		wrapper.delete = true;

		StringWriter writer = new StringWriter();
		jaxb.createMarshaller().marshal(wrapper, writer);

		Request request = request().at(resource(ScopesResource.mapping)).using(POST)
				.with(content_type, application_xml).with(writer.toString()).logging();

		app.httpSend(request);

		// assertFalse(context.profile().scopes().contains(scope));

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
		return extension_root + path + resource;
	}
}
