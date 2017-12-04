package test;

import static java.util.concurrent.TimeUnit.*;
import static junit.framework.Assert.*;
import static org.gcube.smartgears.lifecycle.application.ApplicationState.*;

import java.util.concurrent.CountDownLatch;

import org.gcube.common.events.Hub;
import org.gcube.common.events.Observes;
import org.gcube.common.events.impl.DefaultHub;
import org.gcube.smartgears.lifecycle.application.ApplicationLifecycle;
import org.gcube.smartgears.lifecycle.application.ApplicationState;
import org.junit.Test;

public class LifecycleTest {

	Hub hub = new DefaultHub();
	ApplicationLifecycle lc = new ApplicationLifecycle(hub,"test");
	
	@Test 
	public void movesFromStateToState() throws Exception {
		
		ApplicationState state  = lc.state();
		lc.moveTo(active);
		assertEquals(active, lc.state());
		assertEquals(state,lc.previous());
	}
	
	@Test(expected=IllegalStateException.class)
	public void doesAllowIllegalTransitions() throws Exception {
		
		lc.moveTo(active);
		lc.moveTo(started);
	}
	
	@Test 
	public void producesEventsOnStateChanges() throws Exception {
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		Object verifier = new Object() {
			
			@Observes(ApplicationLifecycle.activation)
			void verifyStateChangeIsNotified(ApplicationLifecycle lifecycle) {
				latch.countDown();
			}
		};
		
		hub.subscribe(verifier);
		
		lc.moveTo(active);
		
		assertTrue(latch.await(100,MILLISECONDS));
	}
}
