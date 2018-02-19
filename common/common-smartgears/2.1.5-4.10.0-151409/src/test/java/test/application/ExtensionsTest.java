package test.application;

import static app.Request.request;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;
import static org.gcube.smartgears.handlers.application.request.RequestError.invalid_request_error;
import static org.gcube.smartgears.lifecycle.application.ApplicationState.failed;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.smartgears.Constants;
import org.gcube.smartgears.context.application.ApplicationContext;
import org.gcube.smartgears.extensions.ApplicationExtension;
import org.gcube.smartgears.extensions.HttpExtension;
import org.gcube.smartgears.extensions.resource.RemoteResource;
import org.gcube.smartgears.handlers.application.request.RequestError;
import org.junit.Test;

import app.SomeApp;

import com.sun.jersey.api.client.UniformInterfaceException;

public class ExtensionsTest {

	String name = "name";
	String extension_path="/ext";
	

	@Test
	public void areInstalledAndInitialised() {
		

		final String response = "output";
		
		@SuppressWarnings("serial")
		ApplicationExtension extension = new HttpExtension(name,extension_path) {
		
			@Override
			public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
				assertNotNull(context()); //init has been invoked
				res.getWriter().write(response);
			}
		
		};


		
		SomeApp app = new SomeApp();
		
		app.extensions().set(extension);
		
		//we're only testing correct installation, not configuration now anything else
		app.bypassExtensionsDeployment();
		
		app.start();
		
		String actual = app.send(request().at(Constants.root_mapping+extension_path));
		
		assertEquals(response,actual);
	}
	
	@SuppressWarnings("serial")
	@XmlRootElement(name="unknown")
	static class UnknownExtension extends HttpExtension {
		
		UnknownExtension() {}
		
		public UnknownExtension(String name, String mapping) {
			super(name, mapping);
		}
		
		@Override
		public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		}
	}
	
	@Test
	public void failAppIfNotConfigured() {
		
		SomeApp app = new SomeApp();
		
		app.bypassHandlerDeployment();
		
		app.extensions().set(new UnknownExtension(name,extension_path));
		
		ApplicationContext context= app.start();
		
		assertEquals(failed,context.lifecycle().state());
				
	}
	
	@Test
	public void failAppIfConfiguredBadly() {
		
		SomeApp app = new SomeApp();
		
		RemoteResource extension = new RemoteResource();
		extension.name("");
		extension.mapping("");
		
		app.extensions().set(extension);
		
		app.bypassHandlerDeployment();
		
		ApplicationContext context= app.start();
		
		assertEquals(failed,context.lifecycle().state());
				
	}
	
	
	@Test
	public void throwErrorsConvertedInHttpResponses() {
		

		final RequestError error = invalid_request_error;
		
		@SuppressWarnings("serial")
		ApplicationExtension extension = new HttpExtension(name,extension_path) {
		
			@Override
			public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
				error.fire();
			}
		
		};

		SomeApp app = new SomeApp();
		
		app.extensions().set(extension);
		
		//we're only testing correct installation, not configuration now anything else
		app.bypassExtensionsDeployment();
		
		app.start();
		
		try {
			app.send(request().at(Constants.root_mapping+extension_path));
			fail();
		}
		catch(UniformInterfaceException e) {

			assertEquals(error.code(),e.getResponse().getStatus());	
		}
		
	}
	
	@Test
	public void areManagedLikeNativeServlets() {
		
		@SuppressWarnings("serial")
		ApplicationExtension extension = new HttpExtension(name,extension_path) {
			@Override
			public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
			}
		};

		SomeApp app = new SomeApp();
		
		app.extensions().set(extension);
		
		app.bypassExtensionsDeployment();
		
		//installs default filters
		app.useDefaultHandlers();
		
		app.start();
		
		//call in no scope
		try {
			app.send(request().at(Constants.root_mapping+extension_path).inScope(null));
			fail();
		}
		catch(UniformInterfaceException e) {

			assertEquals(e.getResponse().getEntity(String.class),invalid_request_error.code(),e.getResponse().getStatus());	
		}
	}
}
