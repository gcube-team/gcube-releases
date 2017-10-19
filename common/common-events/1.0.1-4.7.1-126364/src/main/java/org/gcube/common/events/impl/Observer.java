package org.gcube.common.events.impl;

import static java.util.Arrays.*;
import static org.gcube.common.events.impl.ReflectionUtils.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.gcube.common.events.Hub;
import org.gcube.common.events.Observes;
import org.gcube.common.events.Observes.Kind;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used internally to wrap arbitrary objects subscribed as observers.
 * 
 * @author Fabio Simeoni
 * 
 */
public class Observer {

	private static final Logger log = LoggerFactory.getLogger(Hub.class);

	final Object object;
	final Method method;
	final Kind kind;
	final Key key;

	private ExecutorService service;
	private Future<?> future;
	private long delay;

	private List<Object> accumulated = new ArrayList<Object>();

	public static List<Observer> observersFor(Object object, ExecutorService service) {

		List<Observer> observers = new ArrayList<Observer>();

		List<Method> methods = observerMethodsOf(object);

		if (methods.isEmpty())
			throw new IllegalArgumentException(object
					+ " is not an observer, none of its methods is annotated with @Observes");

		for (Method method : methods)
			observers.add(new Observer(object, method, service));

		return observers;
	}

	Observer(Object object, Method method, ExecutorService service) {

		method.setAccessible(true);

		this.object = object;
		this.method = method;
		this.service = service;

		Observes.Kind kind = method.getAnnotation(Observes.class).kind();

		this.kind = kind;

		Set<String> qualifiers = new HashSet<String>(asList(method.getAnnotation(Observes.class).value()));

		delay = method.getAnnotation(Observes.class).every();
		
		Type paramType = method.getGenericParameterTypes()[0];
		
		if (delay>0) 
			paramType=elementTypeOf(paramType);
		
		key = new Key(paramType, qualifiers);

	}

	public Kind kind() {
		return kind;
	}

	public void onEvent(Object event) {

		if (delay > 0)
			onEventDelayed(event);
		else
			onEventImmediate(event);
	}

	public void onEventImmediate(final Object event) {

		try {
			method.invoke(object, event);
		} catch (InvocationTargetException e) {
			rethrow(event, e.getCause());
		} catch (Exception e) {
			rethrow(event, e);
		}

	}

	public synchronized void onEventDelayed(final Object event) {

			accumulated.add(event);

			if (future == null || future.isDone())
				future = service.submit(new Runnable() {
	
					@Override
					public void run() {
	
		
						try {
							Thread.sleep(delay);
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
							return;
						}
	
						// pass a copy observers can freely manipulate
						List<Object> listEvent = new ArrayList<Object>(accumulated);
						accumulated.clear();
						onEventImmediate(listEvent);
	
					}
				});

	}

	private void rethrow(Object event, Throwable t) {

		String msg = "observer " + object + " failed to process event " + event + " with qualifiers "
				+ key.qualifiers();

		switch (kind) {

		case critical:
			if (t instanceof RuntimeException)
				throw RuntimeException.class.cast(t);
			else
				throw new RuntimeException(msg, t);

		default:
			log.error(msg, t);

		}
	}

	public Key key() {
		return key;
	}

	@Override
	public int hashCode() {
		return object.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return object.equals(obj);
	}

	// helper

	public static List<Method> observerMethodsOf(Object o) {

		List<Method> methods = new ArrayList<Method>();

		for (Method method : o.getClass().getDeclaredMethods())
			if (method.isAnnotationPresent(Observes.class)) {

				Type[] params = method.getGenericParameterTypes();
				
				if (params.length != 1)
					throw new IllegalArgumentException("observer method " + method
							+ " does not take a single parameter");

				if (containsVariable(params[0]))
					throw new IllegalArgumentException("observer method " + method
							+ " uses type variables, which inhibit event delivery");
				
				long delay = method.getAnnotation(Observes.class).every();

				//validate coalescing consumers
				if (delay > 0 && !isCollectionType(params[0]))
						throw new IllegalArgumentException(
							"observer method "
									+ method
									+ " expects multiple events at once but its parameter cannot be assigned to java.util.Collection parameter");
				
				methods.add(method);
			}

		return methods;
	}
	
	
}
