package test.container;

import static junit.framework.Assert.*;
import static org.gcube.smartgears.Constants.*;
import static org.gcube.smartgears.lifecycle.container.ContainerState.*;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.gcube.common.events.Observes;
import org.gcube.common.resources.gcore.HostingNode;
import org.gcube.common.resources.gcore.Resources;
import org.gcube.smartgears.context.application.ApplicationContext;
import org.gcube.smartgears.context.container.ContainerContext;
import org.junit.Test;

import app.SomeApp;

public class ProfileManagementTest {

	@Test
	public void createsStoresAndPublishesAValidProfile() throws Exception {

		ContainerContext ctx = startAppAndGetContainerContext();
		
		HostingNode node = ctx.profile(HostingNode.class);

		assertNotNull(node);
		
		//assert profile has been created
		File profile = ctx.configuration().persistence().file(container_profile_file_path);
		assertTrue(profile.exists());

		assertFalse(node.scopes().isEmpty());

		Resources.validate(node);

	}

	@Test
	public void loadsAndUpdatesProfile() throws Exception {

		startAppAndGetContainerContext();
		
		SomeApp runtwice = new SomeApp();

		runtwice.dirtyRun();

		ContainerContext ctx = runtwice.start().container();

		assertNotNull(ctx.profile(HostingNode.class));

		HostingNode node = ctx.profile(HostingNode.class);
		
		Resources.validate(node);
	}
	
	
	@Test
	public void periodicallyUpdatesAndPublishesProfile() throws Exception {

		SomeApp app = new SomeApp();

		app.containerConfiguration().publicationFrequency(1);
		
		ContainerContext ctx = app.start().container();
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		assertEquals(active,ctx.lifecycle().state());
		
		ctx.events().subscribe(new Object() {
			
			@Observes
			void profileHasChangedAfterPeriodicUpdate(HostingNode ignore) {
				latch.countDown();
			}
			
		});
		
		if (!latch.await(4,TimeUnit.SECONDS))
				fail();
		
		ctx.lifecycle().moveTo(stopped); //should stop periodic updates
		
	}


	ContainerContext startAppAndGetContainerContext() {
		
		SomeApp app = new SomeApp();

		ApplicationContext appCtx = app.start();

		return appCtx.container();
	}
}
