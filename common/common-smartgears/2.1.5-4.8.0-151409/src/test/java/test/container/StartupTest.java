package test.container;

import static org.gcube.smartgears.Constants.*;
import static org.gcube.smartgears.lifecycle.container.ContainerState.*;
import static org.junit.Assert.*;

import org.gcube.smartgears.context.application.ApplicationContext;
import org.junit.After;
import org.junit.Test;

import app.SomeApp;

public class StartupTest {

	SomeApp app = new SomeApp();
	
	@After
	public void teardown() {
		app.stop();
	}
	
	@Test(expected=RuntimeException.class)
	public void failsIfHomeIsNotConfigured() {
	
		System.clearProperty(ghn_home_property);
		
		app.start();
		
		assertFalse(app.isActive());
	}
	
	@Test(expected=RuntimeException.class)
	public void failsIfInstallationFolderIsInvalid() {
	
		System.setProperty(ghn_home_property,"foo");
		
		app.start();
		
		assertFalse(app.isActive());
		
	}
	
	@Test(expected=RuntimeException.class)
	public void failsIfConfigurationIsInvalid() {
	
		app.containerConfiguration().hostname(null);
		
		app.start();

		assertFalse(app.isActive());
		
	}
	
	@Test
	public void leavesContainerToActive() {
		
		ApplicationContext ctx = app.start();
		
		assertEquals(active,ctx.container().lifecycle().state());
		
		assertTrue(app.isActive());
	}
	
	
}
