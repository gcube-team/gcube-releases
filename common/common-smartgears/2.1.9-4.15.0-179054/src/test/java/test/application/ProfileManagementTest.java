package test.application;

import static junit.framework.Assert.*;
import static org.gcube.smartgears.Constants.*;

import java.io.File;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.Resources;
import org.gcube.smartgears.context.application.ApplicationContext;
import org.gcube.smartgears.handlers.application.lifecycle.ProfileManager;
import org.gcube.smartgears.lifecycle.application.ApplicationLifecycle;
import org.junit.BeforeClass;
import org.junit.Test;

import app.SomeApp;

public class ProfileManagementTest {

	public static ApplicationContext ctx;
	
	@BeforeClass
	public static void startApp() {
		
		SomeApp app = new SomeApp();

		app.handlers().set(new ProfileManager());

		ctx = app.start();
		
	}
	
	@Test
	public void createsStoresAndPublishesAValidProfile() throws Exception {

		GCoreEndpoint profile = ctx.profile(GCoreEndpoint.class); 
		
		assertNotNull(profile);
		
		//assert profile has been created
		File file = ctx.configuration().persistence().file(profile_file_path);
		assertTrue(file.exists());

		assertFalse(profile.scopes().isEmpty());

		Resources.validate(profile);
		
		//assert status matches lifecycle's current state
		ApplicationLifecycle lc = ctx.lifecycle();
		
		assertEquals(profile.profile().deploymentData().status(),lc.state().remoteForm());

	}
	
	@Test
	public void loadsAndUpdatesProfile() throws Exception {
		
		SomeApp runtwice = new SomeApp();

		runtwice.handlers().set(new ProfileManager());
		
		runtwice.dirtyRun();

		ApplicationContext ctx = runtwice.start();


		GCoreEndpoint profile = ctx.profile(GCoreEndpoint.class);
		
		assertNotNull(profile);

		Resources.validate(profile);
	}
	
}
