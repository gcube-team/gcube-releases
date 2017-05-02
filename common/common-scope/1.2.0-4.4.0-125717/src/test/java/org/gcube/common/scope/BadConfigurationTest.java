package org.gcube.common.scope;

import static org.junit.Assert.*;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.gcube.common.scope.api.ScopeProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BadConfigurationTest {

	private ClassLoader loader;
	
	@Before
	public void init() {
		loader = Thread.currentThread().getContextClassLoader();
	}
	
	@Test
	public void multipleAlternativeProvidersCannotBeConfigured() {
		
		try {
			addJarsToClasspath("alternativeprovider.jar","yetanotheralternativeprovider.jar");
			ScopeProvider.instance.set("shouldfail");
	
		}
		catch(Error e) {
			return;
		}
	
		fail();
	}
	
	private void addJarsToClasspath(String ... jars) {
		
		List<URL> jarUrls = new ArrayList<URL>();
		for (String jar : jars)
			jarUrls.add(loader.getResource(jar));
			
		URLClassLoader urlClassLoader
		 = new URLClassLoader(jarUrls.toArray(new URL[0]),loader);
		
		
		Thread.currentThread().setContextClassLoader(urlClassLoader);
	}
	
	@After
	public void teardown() {
		Thread.currentThread().setContextClassLoader(loader);
	}
}
