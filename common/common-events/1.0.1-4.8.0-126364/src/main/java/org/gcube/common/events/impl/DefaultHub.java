package org.gcube.common.events.impl;

import static org.gcube.common.events.impl.ReflectionUtils.*;
import static org.gcube.common.events.impl.Utils.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.gcube.common.events.Hub;
import org.gcube.common.events.Observes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default {@link Hub} implementation.
 * 
 * @author Fabio Simeoni
 * 
 */
public class DefaultHub implements Hub {

	private static final Logger log = LoggerFactory.getLogger(Hub.class);
	
	private final Map<Key, List<Observer>> subscriptions = new HashMap<Key, List<Observer>>();
	
	private final ExecutorService service;
	
	private volatile boolean terminated=false;
	
	public DefaultHub() {
		this.service= Executors.newCachedThreadPool();
	}
	
	public DefaultHub(ExecutorService service) {
		
		notNull("executor service", service);
		
		this.service= service;
		
		log.info("configured hub with executor service {}",service.getClass().getSimpleName());
	}
	
	
	@Override
	public synchronized void subscribe(Object object) {

		if (isTerminated())
			return;
		
		notNull("observer", object);

		for (Observer observer : Observer.observersFor(object,service)) 
			subscribe(observer);
	}

	@Override
	public synchronized boolean unsubscribe(Object observer) {

		if (isTerminated())
			return false;
		
		notNull("observer", observer);

		for (Key key : subscriptions.keySet())
			if (unsubscribe(observer, key))
				return true;

		return false;

	}

	@Override
	public synchronized void fire(Object event, String... qualifiers) {

		if (isTerminated())
			return;
		
		notNull("event", event);
		notNull("qualifiers", qualifiers);

		List<Observer> observers = new ArrayList<Observer>();
		
		for (Key key : subscriptions.keySet())
			if (key.matches(typeOf(event), qualifiers))
				observers.addAll(subscriptions.get(key));
		
		notifyObservers(observers,valueOf(event));
	}
	
	
	@Override
	public void waitFor(final Class<?> eventType) {
		
		if (isTerminated())
			return;
		
		waitFor(eventType,0);
	}
	
	@Override
	public void waitFor(Class<?> eventType, long duration, TimeUnit unit) {
		
		if (isTerminated())
			return;
		
		notNull("time unit", unit);
		
		if (duration<=0)
			throw new IllegalArgumentException("invalid duration: 0 ms");
		
		waitFor(eventType,unit.toMillis(duration));
		
	}
	
	@Override
	public void stop() {
		
		if (isTerminated())
			return;
		
		try {
			
			//give a margin to let 'concurrent' events to be delivered
			Thread.sleep(200);
			
			terminated=true;
			
			service.shutdown();
			service.awaitTermination(1000, TimeUnit.MILLISECONDS);
		}
		catch(InterruptedException e) {
			log.warn("cannot shutdown this hub",e);
		}
		
	}

	
	

	// helpers

	private void notifyObservers(List<Observer> observers, Object event) {
		
		List<Observer> critical = new ArrayList<Observer>();
		List<Observer> resilient = new ArrayList<Observer>();
		List<Observer> safe = new ArrayList<Observer>();
		
		List<Observer> target = null;
		
		for (Observer observer : observers) {
		
			switch(observer.kind()) {
				case critical: target=critical;break;
				case resilient: target=resilient;break;
				case safe: target=safe;
			}
			

			target.add(observer);
		}
		
		//execute criticals synchronously and sequentially
		for (Observer observer : critical)
			try {
				observer.onEvent(event);
			}
			catch (RuntimeException e) {
				notifyObserversAsynchronously(resilient, event);
				throw e;
			}
		
			safe.addAll(resilient);
			
			notifyObserversAsynchronously(safe, event);
		
	}
	
	
	private void notifyObserversAsynchronously(List<Observer> observers,final Object event) {
		
		List<Runnable> asynchronous = new ArrayList<Runnable>();
		
		for (final Observer observer : observers)
			asynchronous.add(new Runnable() {
				
				@Override
				public void run() {
					observer.onEvent(event);
				}
			});
		
		//execute asynchronous in parallel
		for (Runnable observer : asynchronous)
			service.submit(observer);
				
	}
	
	private void subscribe(Observer observer) {

		Key key = observer.key();

		List<Observer> observers = subscriptions.get(key);

		if (observers == null) {
			observers = new ArrayList<Observer>();
			subscriptions.put(key, observers);
		}

		observers.add(observer);
	}

	
	private boolean unsubscribe(Object observer, Key key) {

		List<Observer> observers = subscriptions.get(key);

		if (observers != null)
			for (Observer l : observers)
				if (l.equals(observer)) {
					observers.remove(l);
					return true;
				}
		return false;
	}
	
	
	private boolean waitFor(final Class<?> eventType, long duration) {
		
		notNull("event type", eventType);
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		Object watcher = new Object() {
			
			@Observes
			void onEvent() {
				log.trace("end of watch for event {}",eventType);
				latch.countDown();
			}
		};
		
		subscribe(watcher);
		
		log.info("subscribed watcher for event {}",eventType);
		
		boolean outcome = true;
		
		try {
			
			if (duration==0) 
				latch.await();
			else
				outcome= latch.await(duration, TimeUnit.MILLISECONDS);
			
		}
		catch(InterruptedException e) {
			log.error("watcher for event {} has been interrupted",eventType);
			outcome=false;
		}
		
		return outcome;

	}
	
	private boolean isTerminated() {
		if (terminated)
			log.trace("hub is terminated, operation request aborted");
		return terminated;
	}
	
}
