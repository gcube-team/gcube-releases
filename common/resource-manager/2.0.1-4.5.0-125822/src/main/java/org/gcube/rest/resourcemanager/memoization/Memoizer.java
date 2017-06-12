package org.gcube.rest.resourcemanager.memoization;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Memoizer implements InvocationHandler {
	private static final Logger log = LoggerFactory.getLogger(Memoizer.class);
	private Long maximumSize;
	private Long duration;
	private TimeUnit unit;

	public Object build(Object object) {
		this.object = object;
		return Proxy.newProxyInstance(object.getClass().getClassLoader(), object.getClass().getInterfaces(), this);
	}

	public Memoizer() {
	}

	public Memoizer maximumSize(long maximumSize) {
		this.maximumSize = maximumSize;
		return this;
	}

	public Memoizer expireAfterWrite(long duration, TimeUnit unit) {
		this.duration = duration;
		this.unit = unit;
		return this;
	}

	private Object object;
	private Map<Method, Cache> caches = new HashMap<Method, Cache>();

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (method.getReturnType().equals(Void.TYPE)) {
			return invoke(method, args);
		} else {
			Cache cache = getCache(method);
			List<Object> key = new ArrayList<Object>();
			key.add(object);
			key.add(method);
			for (Object arg : args)
				key.add(arg);

			Object value = cache.get(key);

			if (value == null) {
				log.trace("cache miss for method: " + method.getDeclaringClass().getName() + "." + method.getName() + "(" + Arrays.asList(method.getGenericParameterTypes()).toString().replaceAll("(?:class |\\[|\\])", "") +")");
				value = invoke(method, args);
				cache.put(key, value);
			} else
				log.trace("cache hit for method: " + method.getDeclaringClass().getName() + "." + method.getName() + "(" + Arrays.asList(method.getGenericParameterTypes()).toString().replaceAll("(?:class |\\[|\\])", "") +")");
			
			return value;
		}
	}

	private Object invoke(Method method, Object[] args) throws Throwable {
		try {
			return method.invoke(object, args);
		} catch (InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	private synchronized Cache getCache(final Method m) {
		Cache cache = caches.get(m);
		if (cache == null) {
			// cache = new SimpleCache();
			log.info("Initializing cache for " + m.getDeclaringClass().getName() + "." +m.getName() + "(" + Arrays.asList(m.getGenericParameterTypes()).toString().replaceAll("(?:class |\\[|\\])", "") +")" + " with maximum size (" + maximumSize + ") and expiration after write (" + duration + unit.toString() +")");
			final Cache newCache = new GuavaCache.CacheBuilder().maximumSize(maximumSize).expireAfterWrite(duration, unit).build();
			cache = newCache;
			caches.put(m, cache);
			
			Thread t = new Thread() {
				public void run() {
					while (true) {
						log.info("Stats for cache: " + m.getDeclaringClass().getName() + "." +m.getName() + "(" + Arrays.asList(m.getGenericParameterTypes()).toString().replaceAll("(?:class |\\[|\\])", "") +")" + " with size (" + newCache.size() + "/" + maximumSize + ") and expiration after write (" + duration + unit.toString() +") " + newCache.stats().toString());
						try {
							Thread.sleep(600*1000);// XXX 600*1000
						} catch (InterruptedException e) {
						}
					}
				}
			};
			t.setDaemon(true);
			t.setName("Cache Stats");
			t.start();
		}
		return cache;
	}
}
