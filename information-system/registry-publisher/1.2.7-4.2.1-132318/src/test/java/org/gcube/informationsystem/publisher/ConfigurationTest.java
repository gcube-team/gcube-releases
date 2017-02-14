package org.gcube.informationsystem.publisher;

import static org.junit.Assert.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import org.gcube.informationsystem.publisher.scope.IValidatorContext;
import org.gcube.informationsystem.publisher.scope.Validator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ConfigurationTest {

	private ClassLoader loader;
//	static final String relativePath="src/test/java/META-INF/services/org.gcube.informationsystem.publisher.scope.IValidatorContext";

	
	@Before
	public void init() {
		loader = Thread.currentThread().getContextClassLoader();
		
	}
	
	@Test
	public void alternativeProvidersCanBeConfigured() {
		addJarsToClasspath("customValidator.jar");
		List<IValidatorContext> impls=load();
		assertEquals(impls.size(), 1);
	}

	private List<IValidatorContext>  load(){
		ServiceLoader<IValidatorContext> loader = ServiceLoader.load(IValidatorContext.class);
		Iterator<IValidatorContext> iterator = loader.iterator();
		List<IValidatorContext> impls = new ArrayList<IValidatorContext>();
		 while(iterator.hasNext())
			 impls.add(iterator.next());
		 System.out.println("size: "+impls.size());
		 if(impls.size()==1){
			 IValidatorContext context = impls.get(0);
			 for( Validator validator : context.getValidators()){
				 System.out.println("implementation found: "+ validator.type());
			 }
		 }
		 return impls;
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
