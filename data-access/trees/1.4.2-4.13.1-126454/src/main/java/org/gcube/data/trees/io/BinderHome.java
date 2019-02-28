package org.gcube.data.trees.io;

import java.util.Map;

/**
 * Inspects the classpath for available {@link TreeBinder}s.
 * 
 * @author Fabio Simeoni
 *
 */
public interface BinderHome {


	/**
	 * Returns the binders available on the classpath.
	 * 
	 * @return the binders.
	 */
	Map<String,TreeBinder<?>> binders();
}
