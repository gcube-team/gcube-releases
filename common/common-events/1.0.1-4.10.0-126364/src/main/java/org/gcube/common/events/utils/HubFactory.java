package org.gcube.common.events.utils;

import org.gcube.common.events.Hub;
import org.gcube.common.events.impl.DefaultHub;

/**
 * Convenience factory to share {@link Hub} instances.
 * Encouraged only in environments where better means of sharing like dependency injection are not
 * available.
 * 
 * @author Fabio Simeoni
 *
 */
public class HubFactory {

	private static Hub hub = new DefaultHub();
	
	/**
	 * Returns the shared {@link Hub} instance.
	 * @return the instance
	 */
	public static Hub hub() {
		return hub;
	}
	
	/**
	 * Sets the shared {@link Hub} instance, overriding the default one.
	 * <p>
	 * Typically used in testing to configure a test-driving instance.
	 * 
	 * @param hub the instance
	 */
	public static void configure(Hub hub) {
		HubFactory.hub=hub;
	}
}
