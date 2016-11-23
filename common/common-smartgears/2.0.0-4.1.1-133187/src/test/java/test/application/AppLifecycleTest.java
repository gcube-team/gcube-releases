package test.application;

import static org.junit.Assert.*;

import org.gcube.smartgears.context.application.ApplicationContext;
import org.gcube.smartgears.context.container.ContainerContext;
import org.gcube.smartgears.handlers.application.lifecycle.ProfileManager;
import org.gcube.smartgears.lifecycle.application.ApplicationState;
import org.gcube.smartgears.lifecycle.container.ContainerState;
import org.junit.Test;

import app.SomeApp;

public class AppLifecycleTest {

	@Test
	public void applicationGoesDownIfContainerDoes() {
		
		SomeApp app = new SomeApp();
		
		app.handlers().set(new ProfileManager());
		
		ApplicationContext actx = app.start();
		
		assertEquals(ApplicationState.active,actx.lifecycle().state());
		
		ContainerContext ctx = actx.container();

		assertEquals(ContainerState.active,ctx.lifecycle().state());
		
		ctx.lifecycle().moveTo(ContainerState.stopped);
		
		assertEquals(ApplicationState.stopped,actx.lifecycle().state());
	}
	
}
