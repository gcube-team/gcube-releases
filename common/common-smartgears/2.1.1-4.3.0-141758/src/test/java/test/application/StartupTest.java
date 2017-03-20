package test.application;

import static app.Request.request;
import static org.gcube.smartgears.Constants.profile_file_path;
import static org.gcube.smartgears.lifecycle.application.ApplicationState.active;
import static org.gcube.smartgears.lifecycle.application.ApplicationState.failed;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.Resource;
import org.gcube.informationsystem.publisher.ScopedPublisher;
import org.gcube.informationsystem.publisher.exception.RegistryNotFoundException;
import org.gcube.smartgears.Constants;
import org.gcube.smartgears.configuration.application.ApplicationConfiguration;
import org.gcube.smartgears.configuration.application.DefaultApplicationConfiguration;
import org.gcube.smartgears.context.application.ApplicationContext;
import org.gcube.smartgears.handlers.application.ApplicationEvent;
import org.gcube.smartgears.handlers.application.ApplicationLifecycleEvent.Start;
import org.gcube.smartgears.handlers.application.ApplicationLifecycleHandler;
import org.gcube.smartgears.handlers.application.RequestHandler;
import org.gcube.smartgears.persistence.DefaultPersistence;
import org.junit.After;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import utils.TestUtils.Box;
import app.SomeApp;

public class StartupTest {

	SomeApp app = new SomeApp();
	
	@After
	public void teardown() {
		app.stop();
	}
	
	@Test
	public void succeedsWithDefaults() throws Exception {
	
		
		app.useDefaultHandlers();
		app.useDefaultExtensions();
		
		ApplicationContext ctx = app.start();
		
		assertEquals(active,ctx.lifecycle().state());
		
		//profile is shared in servlet context
		assertEquals(ctx,ctx.application().getAttribute(Constants.context_attribute));
		
	}
	
	@Test
	public void stillStoresProfileWhenPublicationFails() throws Exception {
	
		app.useDefaultHandlers();
		
		ScopedPublisher failingPublisher = Mockito.mock(ScopedPublisher.class);
		when(failingPublisher.create(any(Resource.class), Matchers.anyListOf(String.class))).thenThrow(new RegistryNotFoundException());
		
		app.usePublisher(failingPublisher);
		
		ApplicationContext ctx = app.start();
		
		Thread.sleep(100); //a little bit of time for failure to propagate
		
		//application has failed
		assertEquals(failed,ctx.lifecycle().state());
		
		//profile has been created
		File file = ctx.configuration().persistence().file(profile_file_path);
		assertTrue(file.exists());
					
	}
	
	@Test 
	@SuppressWarnings("all")
	public void invokesLifecycleHandlers() {
	
		ApplicationLifecycleHandler witness = mock(ApplicationLifecycleHandler.class);
		
		app.handlers().set(witness);
		
		//as we're using mocks, let us bypass JAXB configuration mechanisms
		app.bypassHandlerDeployment();
		
		app.start();

		verify(witness).onEvent(any(ApplicationEvent.class));
	}
	
	@Test 
	@SuppressWarnings("all")
	public void registersRequestsHandlers() {
	
		Box<Boolean> handlerIsInvoked = new Box<Boolean>();
	
		RequestHandler witness = mock(RequestHandler.class);
		
		app.handlers().set(witness);
		
		app.bypassHandlerDeployment();
		
		app.start();
		
		app.send(request());

		//invoked for request and response
		verify(witness,times(2)).onEvent(isA(ApplicationEvent.class));
	}

	//@Ignore //inexplicable sometimes fails as configuration is not removed
	@Test
	public void failsIfConfigurationIsInvalid() {
		
		app.configuration().name(null).serviceClass(null).description(null).version(null).persistence(null);
		
		ApplicationContext ctx = app.start();
		
		assertEquals(failed,ctx.lifecycle().state());
	}
	
	@Test  
	public void failsIfHandlerFails() throws Throwable {
		
		ApplicationLifecycleHandler failingHandler = mock(ApplicationLifecycleHandler.class);
		doThrow(new RuntimeException("simulated handler failure")).when(failingHandler).onEvent(isA(Start.class));
		
		app.handlers().set(failingHandler);

		app.bypassHandlerDeployment();
		
		ApplicationContext ctx = app.start();
			
		assertEquals(failed,ctx.lifecycle().state());
		
	}
	
	@Test
	public void canUseExternalConfiguration() {
		
		app.asExternal();
		
		app.start();
		
		assertTrue(app.isActive());
	}
	
	
	@Test
	public void canUseMergedConfiguration() {
		
		ApplicationConfiguration config = new DefaultApplicationConfiguration();
		config.persistence(new DefaultPersistence(new File(".").getAbsolutePath()));
				
		ApplicationContext context = app.start();
				
		assertTrue(app.isActive());
		
		app.withExternal(config);
		
		assertEquals(config.persistence(),context.configuration().persistence());
	
	}
	

	@Test
	public void failsIfAllStartScopesAreInvalid() throws Exception {
		
		ApplicationConfiguration config = new DefaultApplicationConfiguration();
		
		//config.startScopes("bad/scope","even/badder");
		
		app.useDefaultHandlers();
						
		ApplicationContext context = app.start();
		
		app.withExternal(config);
		
		assertEquals(failed,context.lifecycle().state());
		
		
		
	}
	
	@Test
	public void canStartInVreScope() throws Exception {
		
		ApplicationConfiguration config = new DefaultApplicationConfiguration();
		
		//tring vre = "/"+app.containerConfiguration().infrastructure()+"/"+app.containerConfiguration().startVOs().get(0)+"/vre";
		
		//config.startScopes(vre,"/bad/scope");
		
		app.useDefaultHandlers();
		
		ApplicationContext context = app.start();
		
		app.withExternal(config);
		
		assertEquals(active,context.lifecycle().state());
		
		Set<String> runningScopes = new HashSet<>(context.profile(GCoreEndpoint.class).scopes().asCollection());
		
		//assertEquals(singleton(vre),runningScopes);
		
		
		
		
	}
	
	
	@Test(expected=RuntimeException.class)
	public void failsIfConfigurationIsMissing() {
		
		app.bypassConfigurationDeployment();

		app.start();
	}
}
