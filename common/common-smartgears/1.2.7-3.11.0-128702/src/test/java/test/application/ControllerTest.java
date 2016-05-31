package test.application;

import static app.Request.*;
import static org.gcube.smartgears.Constants.*;
import static org.gcube.smartgears.extensions.ApiResource.*;
import static org.gcube.smartgears.extensions.HttpExtension.Method.*;
import static org.gcube.smartgears.handlers.application.request.RequestError.*;
import static org.junit.Assert.*;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gcube.smartgears.extensions.ApiResource;
import org.gcube.smartgears.extensions.ApiSignature;
import org.gcube.smartgears.extensions.HttpController;
import org.gcube.smartgears.extensions.HttpExtension;
import org.junit.Test;

import app.Request;
import app.SomeApp;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;

public class ControllerTest {

	String name = "name";
	String extension_path = "/ext";
	String extension_mapping = "/ext/*";
	String resource_path = "/resource";

	@Test
	public void dispatchesToResource() {

		// returns a given type
		ApiSignature signature = handles(resource_path).with(method(GET).produces("text/plain"));

		// requires same type
		Request request = request().at(resource()).with(accept, "text/plain");

		SomeApp app = startAppWith(signature);

		app.send(request);
	}

	@Test
	public void toleratesTrainingSlashes() {

		// returns a given type
		ApiSignature signature = handles(resource_path).with(method(GET).produces("text/plain"));

		// requires same type
		Request request = request().at(resource()).with(accept, "text/plain");

		SomeApp app = startAppWith(signature);

		app.send(request);
	}

	@Test
	public void handlesUnknownResources() {

		ApiSignature signature = handles(resource_path);

		// points to not existing resource
		Request request = request().at(resource() + "/bad");

		SomeApp app = startAppWith(signature);

		try {
			app.send(request);
			fail();
		} catch (UniformInterfaceException e) {
			assertEquals(resource_notfound_error.code(), e.getResponse().getStatus());
		}
	}

	@Test
	public void handlesUnsupportedMethods() {

		ApiSignature signature = handles(resource_path).with(method(GET)).with(method(PUT));

		Request request = request().at(resource()).using(POST);

		SomeApp app = startAppWith(signature);

		try {
			app.send(request);
			fail();
		} catch (UniformInterfaceException e) {
			assertEquals(method_unsupported_error.code(), e.getResponse().getStatus());
			assertNotNull(e.getResponse().getHeaders().toString(),e.getResponse().getHeaders().get(allow));
		}
	}

	@Test
	public void enforcesAcceptHeaders() {

		ApiSignature signature = handles(resource_path).with(method(GET).produces("text/plain"));

		Request request = request().at(resource()).with(accept, "text/xml");

		SomeApp app = startAppWith(signature);

		try {
			app.send(request);
			fail();
		} catch (UniformInterfaceException e) {
			assertEquals(outgoing_contenttype_unsupported_error.code(), e.getResponse().getStatus());
			
		}
	}

	@Test
	public void enforcesAcceptHeadersEvenWhenResourceDeclaresNone() {

		ApiSignature signature = handles(resource_path).with(method(GET));

		Request request = request().at(resource()).with(accept, "text/xml");

		SomeApp app = startAppWith(signature);

		try {
			app.send(request);
			fail();
		} catch (UniformInterfaceException e) {
			assertEquals(outgoing_contenttype_unsupported_error.code(), e.getResponse().getStatus());
		}
	}
	
	@Test
	public void enforcesMultiValuedAcceptHeader() {

		ApiSignature signature = handles(resource_path).with(method(GET).produces("text/plain"));

		Request request = request().at(resource()).with(accept, "text/xml").with(accept,"text/plain");

		SomeApp app = startAppWith(signature);

		app.send(request);
		
	}

	@Test
	public void setsContentTypeIfUnsetAndUnambiguous() {

		ApiSignature signature = handles(resource_path).with(method(GET).produces("text/plain"));

		Request request = request().at(resource()).with(accept, "text/plain");

		SomeApp app = startAppWith(signature);

		ClientResponse response = app.httpSend(request);

		assertTrue(response.getHeaders().get(content_type).get(0).contains("text/plain"));
	}
	
	@Test
	public void doesntSetContentTypeIfUnsetButAmbiguous() {

		ApiSignature signature = handles(resource_path).with(method(GET).produces("text/plain","text/xml"));

		Request request = request().at(resource()).with(accept, "text/plain");

		SomeApp app = startAppWith(signature);
		
		ClientResponse response = app.httpSend(request);
		
		System.out.println(response.getHeaders());

		assertNull(response.getHeaders().get(content_type));

	}
	
	
	@Test
	public void enforcesMultiValuedContentTypeHeader() {

		ApiSignature signature = handles(resource_path).with(method(POST).accepts("application/xml"));

		Request request = request().at(resource()).using(POST).with(content_type, "text/xml").with(content_type,"text/plain");

		SomeApp app = startAppWith(signature);

		try {
			app.send(request);
			fail();
		} catch (UniformInterfaceException e) {
			assertEquals(incoming_contenttype_unsupported_error.code(), e.getResponse().getStatus());
		}
	}
	
	@Test
	public void acceptsContentTypeHeadersWhenResourceDeclaresNone() {

		ApiSignature signature = handles(resource_path).with(method(POST));

		Request request = request().at(resource()).using(POST).with(content_type, "text/xml");

		SomeApp app = startAppWith(signature);

		app.httpSend(request);
		
	}
	
	
	
	///////////////////////////////// helpers

	private String resource() {
		return extension_root + extension_path + resource_path;
	}

	SomeApp startAppWith(ApiSignature signature) {

		SomeApp app = new SomeApp();

		app.extensions().set(controllerWith(signature));

		app.bypassExtensionsDeployment();
		app.bypassHandlerDeployment();

		app.start();

		return app;
	}
	
	

	@SuppressWarnings("serial")
	HttpExtension controllerWith(final ApiSignature signature) {
		return new HttpController(name, extension_mapping) {

			{
				addResources(new ApiResource(signature) {

					@Override
					public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
							IOException {
						if (!supports(Method.valueOf(req.getMethod())))
							super.doGet(req, resp);
						else
							resp.getWriter().write(req.getMethod() + " invoked @ " + signature.mapping());
					}

					@Override
					public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
							IOException {
						if (!supports(Method.valueOf(req.getMethod())))
							super.doPost(req, resp);
						else
							resp.getWriter().write(req.getMethod() + " invoked @ " + signature.mapping());
					}

					@Override
					public void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
							IOException {
						if (!supports(Method.valueOf(req.getMethod())))
							super.doPut(req, resp);
						else
							resp.getWriter().write(req.getMethod() + " invoked @ " + signature.mapping());
					}

				});
			}

			@Override
			public String toString() {
				return "SUT controller";
			}
		};
	}
}
