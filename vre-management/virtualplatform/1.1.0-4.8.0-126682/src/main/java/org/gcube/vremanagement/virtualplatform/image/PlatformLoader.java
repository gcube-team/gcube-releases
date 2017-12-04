package org.gcube.vremanagement.virtualplatform.image;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 
 * Create and interface the classloader of a platform
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
class PlatformLoader {

	ParentLastClassLoader cl;
	
	PlatformLoader(File[] resources) throws MalformedURLException { 
		this.create(resources);
	}
	
	/**
	 * Creates and loads the given resources on a new classloader
	 * @param resources the list of jars/files to load
	 * @throws MalformedURLException if the jars list is not valid
	 */
	private void create(File[] resources) throws MalformedURLException {
		URL[] urls = new URL[resources.length];		
		for (int i = 0; i< resources.length; i++)
			urls[i] = new URL("file://" + resources[i].getAbsolutePath());
		this.cl = new ParentLastClassLoader(urls, this.getClass().getClassLoader());
	}
	
	/**
	 * Invokes a method on a class object with the given parameters
	 * @param obj the object
	 * @param method the name of the method to invoke
	 * @param params the parameters to pass to the method
	 * @throws Exception  if the invocation fails
	 */
	public Object invoke(Object obj, String method, Object ...  params) throws Exception {
		try {
			return obj.getClass().getMethod(method).invoke(obj, params);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * Loads the class with the specified name
	 * @param name the full qualified name of the class (e.g. "org.acme.foo")
	 * @return an instance of the class
	 * @throws ClassNotFoundException
	 */
	public Class<?> load (String name) throws ClassNotFoundException {
		return this.cl.loadClass(name);
	}
	
	/**
	 * Creates a new instance of the given class
	 * @param name full qualified name of the class (e.g. "org.acme.foo")
	 * @param params the parameters to pass in the constructor invocation, if any
	 * @return the newly created instance
	 * @throws Exception if the creation fails
	 */
	public Object getInstanceOf(String name, Object ...  params) throws Exception {		
		Constructor<?> constructor = this.cl.loadClass(name).getConstructor(new Class[] {});
		return constructor.newInstance((params !=null)?params:new Object[]{}); 
	}
	
}
