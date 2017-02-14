package org.gcube.common.mycontainer;

import java.util.HashMap;
import java.util.Map;

import org.mockito.Mockito;

/**
 * Stores and resolves implementations of container dependencies to use for testing purposes, typically mock
 * implementations defined in the scope of tests.
 * 
 * @author Fabio Simeoni
 *
 */
public class MyContainerDependencies {

	private static Map<Class<?>,Object> dependencies = new HashMap<Class<?>,Object>();
	
	/**
	 * Stores a dependency.
	 * @param dependencyClass the dependency {@link Class}
	 * @param dependency the dependency
	 */
	public static <T> void put(Class<T> dependencyClass, T dependency) {
		dependencies.put(dependencyClass,dependency);
	}
	
	/**
	 * Resolve a dependency from its {@link Class}.
	 * @param dependencyClass the dependency's {@link Class}
	 * @return the dependency
	 */
	@SuppressWarnings("all")
	public static <T> T resolve(Class<T> dependencyClass) {
		Object implementation = dependencies.get(dependencyClass);
		return  (T) (implementation==null?Mockito.mock(dependencyClass):implementation);
	}
}
