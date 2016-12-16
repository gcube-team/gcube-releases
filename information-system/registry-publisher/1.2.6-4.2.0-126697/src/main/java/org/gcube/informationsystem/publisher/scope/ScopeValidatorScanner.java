package org.gcube.informationsystem.publisher.scope;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScopeValidatorScanner {
	
	private static Logger log = LoggerFactory.getLogger(ScopeValidatorScanner.class);
	private static ClassLoader loader;
	
	public static IValidatorContext provider() {
		
		loader = Thread.currentThread().getContextClassLoader();
//		addJarsToClasspath("customValidator.jar","scope-validator.jar");
		try {
			IValidatorContext impl = null;
			ServiceLoader<IValidatorContext> loader = ServiceLoader.load(IValidatorContext.class);
			Iterator<IValidatorContext> iterator = loader.iterator();
			List<IValidatorContext> impls = new ArrayList<IValidatorContext>();
			 while(iterator.hasNext())
				 impls.add(iterator.next());
			 if (impls.size()==0) {
				 impl = new DefaultValidatorContext();
			 }
			 else if (impls.size()>1)
				 throw new Exception("mis-configured environment: detected multiple default validator "+impls);
			 else
				 impl=impls.get(0);
			 log.info("using scope validator "+impl);
			 return impl;
		} catch (Exception e) {
			throw new RuntimeException("could not configure scope validator", e);
		}
	}

	private static void addJarsToClasspath(String ... jars) {
		
		List<URL> jarUrls = new ArrayList<URL>();
		for (String jar : jars)
			jarUrls.add(loader.getResource(jar));
		URLClassLoader urlClassLoader
		 = new URLClassLoader(jarUrls.toArray(new URL[0]),loader);
		Thread.currentThread().setContextClassLoader(urlClassLoader);
	}

}
