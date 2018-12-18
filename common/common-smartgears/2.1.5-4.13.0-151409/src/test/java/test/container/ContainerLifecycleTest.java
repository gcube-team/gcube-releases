package test.container;

import static org.junit.Assert.*;

import org.gcube.smartgears.context.application.ApplicationContext;
import org.gcube.smartgears.context.container.ContainerContext;
import org.gcube.smartgears.lifecycle.application.ApplicationState;
import org.gcube.smartgears.lifecycle.container.ContainerState;
import org.gcube.smartgears.managers.ContainerManager;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

import app.SomeApp;

public class ContainerLifecycleTest {
	
	SomeApp app = new SomeApp();
	
	@After
	public void teardown() {
		app.stop();
	}
	
	@Test
	public void containerGoesToPartActiveWhenAppFails() {
		
		
		ApplicationContext actx = app.start();
		
		ContainerContext ctx = actx.container();

		assertEquals(ContainerState.active,ctx.lifecycle().state());
		
		actx.lifecycle().moveTo(ApplicationState.failed);
		
		assertEquals(ContainerState.partActive,ctx.lifecycle().state());
	}
	
	@Test
	public void containerGoesToPartActiveWhenAppStops() {
		
		ApplicationContext actx = app.start();
		
		ContainerContext ctx = actx.container();

		assertEquals(ContainerState.active,ctx.lifecycle().state());
		
		actx.lifecycle().moveTo(ApplicationState.stopped);
		
		assertEquals(ContainerState.partActive,ctx.lifecycle().state());
	}

	
	//used interactively to study shutdown
	@Ignore
	@Test
	public void containerShutsdown() throws Exception {
		
		ApplicationContext actx = app.start();
		
		ContainerContext ctx = actx.container();

		assertEquals(ContainerState.active,ctx.lifecycle().state());
		
		ContainerManager.instance.stop(true);
		app.stop();
		
		Thread.sleep(10000);
	}
}
