package test;


import static org.gcube.common.events.Observes.Kind.*;
import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.gcube.common.events.Hub;
import org.gcube.common.events.Observes;
import org.gcube.common.events.impl.DefaultHub;
import org.gcube.common.events.impl.Event;
import org.gcube.common.events.impl.Observer;
import org.junit.Test;


@SuppressWarnings("unused")
public class HubTest {

	
	@Test 
	public void observersAreReflectivelyProcessed() throws Exception {
	
		Object o = new Object() {
			
			@Observes
			public void shouldBeIncluded(String event) {
			}
			
			public void shouldNotBeIncluded(String event) {
			}
		};
		
		List<Method> methods = Observer.observerMethodsOf(o);
		
		assertTrue(methods.contains(o.getClass().getMethod("shouldBeIncluded", String.class)));
		assertFalse(methods.contains(o.getClass().getMethod("shouldNotBeIncluded", String.class)));
	}
	
	@Test
	public void invalidObserversAreDetected() throws Exception {
	
		Object o = new Object() {
			
			@Observes
			public void tooManyParams(String event, int another) {}
			
		};
		
		try {
			Observer.observerMethodsOf(o);
			fail();
		}
		catch(IllegalArgumentException e) {}
		
		
		
		o = new Object() {
			
			@Observes
			public void tooFewParams() {}
			
		};
		
		try {
			Observer.observerMethodsOf(o);
			fail();
		}
		catch(IllegalArgumentException e) {}
		
		
		o = new Object() {
			
			@Observes(every=1000)
			public void notACollectionParam(String s) {}
			
		};
		
		try {
			Observer.observerMethodsOf(o);
			fail();
		}
		catch(IllegalArgumentException e) {}
		
		
		o = new Object() {
			
			@Observes(every=1000)
			public <T> void canNeverBeInvoked(T s) {}
			
		};
		
		try {
			Observer.observerMethodsOf(o);
			fail();
		}
		catch(IllegalArgumentException e) {}
	}
	
	@Test 
	public void subscribedObserversAreUnsubscribed() throws Exception {
		
		class A {

			@Observes
			public void m(String event)  {}

		}
		
		A o = new A();
		
		Hub hub = new DefaultHub();
		
		hub.subscribe(o);
		
		assertTrue(hub.unsubscribe(o));
	}
	
	@Test 
	public void observersAreNotified() throws Exception {
		
		final CountDownLatch latch = new CountDownLatch(2);
		
		Object o = new Object() {
			
			@Observes
			public void shouldBeInvoked(String event) {
				assertEquals("event",event);
				latch.countDown();
				
			}
			
			@Observes
			public void shouldBeInvokedToo(String event) {
				assertEquals("event",event);
				latch.countDown();
				
			}
			
			@Observes
			public void shouldntBeInvoked(int event) {
				fail();
				
			}
		}; 
		
		Hub hub = new DefaultHub();
		
		hub.subscribe(o);
		
		hub.fire("event");
		
		assertTrue(latch.await(50,TimeUnit.MILLISECONDS));
		
	}
	
	@Test 
	public void parametricObserversAreNotified() throws Exception {
		
		final Object wrappedButUndeliveredBecauseDifferentSpecialisation = new Event<List<Integer>>(Arrays.asList(1)){};
		
		final List<String> undelivered =  Arrays.asList("undelivered");
		final List<String> delivered =  Arrays.asList("delivered");
		
		//negative cases
		final Object undeliveredBecauseUnwrapped = undelivered;
		final Object wrappedButUndeliveredBecauseDeclaredTooSpecific = new Event<List<? extends String>>(undelivered){};
		final Object undeliveredBecauseTooGeneric = new Event<List<?>>(undelivered){};
		
		@SuppressWarnings("rawtypes")
		final Object undeliveredBecauseRaw = new Event<List>(undelivered){};
		
		//positive case
		final Object deliveredBecauseWrappedAndPerfectMatch = new Event<List<String>>(delivered){};
		
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		Object o = new Object() {
			
			@Observes
			public void observe1(List<String> e) {
				assertEquals(delivered,e);
				latch.countDown();
				
			}
			
		}; 
		
		Hub hub = new DefaultHub();
		
		hub.subscribe(o);
		
		hub.fire(undeliveredBecauseUnwrapped);
		hub.fire(wrappedButUndeliveredBecauseDifferentSpecialisation);
		hub.fire(undeliveredBecauseTooGeneric);
		hub.fire(wrappedButUndeliveredBecauseDeclaredTooSpecific);
		hub.fire(undeliveredBecauseRaw);
		
		hub.fire(deliveredBecauseWrappedAndPerfectMatch);
		
		
		assertTrue(latch.await(500,TimeUnit.MILLISECONDS));
		
	}
	
	@Test 
	public void wildcardObserversAreNotified() throws Exception {
		
		final Object wrappedButUndeliveredBecauseDifferentSpecialisation = new Event<List<Integer>>(Arrays.asList(1)){};
		
		final List<String> undelivered =  Arrays.asList("undelivered");
		final List<String> delivered =  Arrays.asList("delivered");
		
		final Object undeliveredBecauseUnwrapped = undelivered;
		final Object undeliveredBecauseTooGeneric = new Event<List<?>>(undelivered){};
		
		@SuppressWarnings("rawtypes")
		final Object undeliveredBecauseRaw = new Event<List>(undelivered){};
		
		final Object deliveredBecauseWrappedAndUpperBound = new Event<List<String>>(delivered){};
		final Object deliveredBecauseWrappedAndPerfectMatch = new Event<List<? extends String>>(delivered){};

		
		
		final CountDownLatch latch = new CountDownLatch(2);
		
		Object o = new Object() {
			
			@Observes
			public void observe1(List<? extends String> e) {
				assertEquals(delivered,e);
				latch.countDown();
				
			}
			
		}; 
		
		Hub hub = new DefaultHub();
		
		hub.subscribe(o);
		
		hub.fire(undeliveredBecauseUnwrapped);
		hub.fire(wrappedButUndeliveredBecauseDifferentSpecialisation);
		hub.fire(undeliveredBecauseTooGeneric);
		hub.fire(undeliveredBecauseRaw);
		
		hub.fire(deliveredBecauseWrappedAndPerfectMatch);
		hub.fire(deliveredBecauseWrappedAndUpperBound);
		
		assertTrue(latch.await(500,TimeUnit.MILLISECONDS));
		
	}
	
	@Test 
	public void rawObserversAreNotified() throws Exception {
		
		final Object deliveredEvenIfUnwrapped = Arrays.asList(1);
		final Object deliveredRegardlessOfSpecialisation = new Event<List<Integer>>(Arrays.asList(1)){};
		
		
		final CountDownLatch latch = new CountDownLatch(2);
		
		Object o = new Object() {
			
			@Observes @SuppressWarnings("rawtypes")
			public void observe1(List e) {
				latch.countDown();
				
			}
			
		}; 
		
		Hub hub = new DefaultHub();
		
		hub.subscribe(o);
		
		hub.fire(deliveredEvenIfUnwrapped);
		hub.fire(deliveredRegardlessOfSpecialisation);
		
		assertTrue(latch.await(500,TimeUnit.MILLISECONDS));
		
	}
	

	@Test 
	public void notificationsAreBasedOnQualifiers() throws Exception {
		
		final CountDownLatch latch = new CountDownLatch(5);
		
		Object o = new Object() {
			
			@Observes("1")
			public void one(String event){
				latch.countDown();
				
			}
			
			@Observes("2")
			public void two(String event) {
				latch.countDown();
			}
			
			@Observes
			public void either(String event) {
				latch.countDown();
			}
		}; 
		
		Hub hub = new DefaultHub();
		
		hub.subscribe(o);
		
		hub.fire("event","1");
		hub.fire("event","2");
		hub.fire("event");
		
		assertTrue(latch.await(500,TimeUnit.MILLISECONDS));
		
	}
	
	@Test 
	public void failuresFollowPolicy() {
	
		final Thread currentThread = Thread.currentThread();
		
		final RuntimeException rt = new RuntimeException();
		
		Object o = new Object() {
			
			@Observes
			public void one(String event){
				throw rt;
			}
			
			@Observes(kind=critical)
			public void three(boolean event) {
				throw rt;
			}
			
			@Observes(kind=critical)
			public void four(double event) throws Exception {
				throw new Exception(rt);
			}
			
		}; 
		
		Hub hub = new DefaultHub();
		
		hub.subscribe(o);
		
		hub.fire("unlucky");
		
		hub.fire(0);
		
		try {
			hub.fire(false);
		}
		catch(RuntimeException e) {
			assertEquals(rt,e);
		}
		
		try {
			hub.fire(0.0);
		}
		catch(RuntimeException e) {
			assertEquals(rt,e.getCause());
		}
	}
	
	@Test 
	public void onlyCriticalObserverRunSynchronously() {
	
		final Thread currentThread = Thread.currentThread();
		
		Object o = new Object() {
			
			@Observes
			public void one(String event){
				assertNotSame(currentThread,Thread.currentThread());
			}
			
			@Observes(kind=critical)
			public void two(String event) {
				assertSame(currentThread,Thread.currentThread());
			}
			
		}; 
		
		Hub hub = new DefaultHub();
		
		hub.subscribe(o);
		
		hub.fire("event");
	}

	@Test
	public void onlyResilientObserversRunIfACriticalOneFails() throws Exception {
	
		final CountDownLatch latch = new CountDownLatch(1);
		
		final RuntimeException rt = new RuntimeException();
		
		Object o = new Object() {
			
			@Observes
			public void safe(String event){
				fail();
			}
			
			@Observes(kind=resilient)
			public void resilient(String event){
				latch.countDown(); //if we get here the test fails
			}
			
			@Observes(kind=critical)
			public void critical(String event) throws Exception {
				Thread.sleep(50);
				throw rt;
			}
			
		}; 
		
		Hub hub = new DefaultHub();
		
		hub.subscribe(o);
		
		try {
			hub.fire("event");
			fail();
		}
		catch(Exception e) {
			assertSame(rt,e);
		}
		
		latch.await(500,TimeUnit.MILLISECONDS);
	}
	
	
	@Test
	public void nonCriticalDoNotBlockProducer() throws Exception {
	
		final CountDownLatch latch = new CountDownLatch(2);
		
		Object o = new Object() {
			
			@Observes
			public void one(String event) throws Exception {
				Thread.sleep(100);
				latch.countDown();
			}
			
			@Observes
			public void two(String event) throws Exception {
				Thread.sleep(100);
				latch.countDown();
			}
			
		}; 
		
		Hub hub = new DefaultHub();
		
		hub.subscribe(o);
		
		hub.fire("event");
		
		assertTrue(latch.await(500,TimeUnit.MILLISECONDS));
	}
	
	@Test
	public void eventsAreCoalesced() throws Exception {
		
		Hub hub = new DefaultHub();
		
		final AtomicInteger count=new AtomicInteger(0);
		
		hub.subscribe(new Object() {
			
			@Observes(every=100) 
			void coaleasced(List<Integer> events) {
				count.incrementAndGet();
			}
		});
		
		for (int i=0; i<100; i++) {
			hub.fire(i);
			Thread.sleep(10);
		}
		
		assertTrue(count.get()-10+" > 1",Math.abs(count.get()-10)<=1);
	
		
	}
	
	@Test
	public void eventsAreCoalescedUnderWildCards() throws Exception {
		
		Hub hub = new DefaultHub();
		
		final AtomicInteger count=new AtomicInteger(0);
		
		hub.subscribe(new Object() {
			
			@Observes(every=100) 
			void coaleasced(List<? extends Integer> events) {
				count.incrementAndGet();
			}
		});
		
		for (int i=0; i<100; i++) {
			hub.fire(i);
			Thread.sleep(10);
		}
		
		assertTrue(count.get()-10+" > 1",Math.abs(count.get()-10)<=1);
	
		
	}
}
