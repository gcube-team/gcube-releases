package org.gcube.data.streams.publishers;

import org.gcube.data.streams.Stream;

/**
 * Provides {@link Thread}s for the asynchronous publicaton of {@link Stream}.
 * @author Fabio Simeoni
 *
 */
public interface ThreadProvider {

	/**
	 * Provides a new {@link Thread} in which to execute the publication task.
	 * @param task the task
	 * @return the {@link Thread}
	 */
	Thread newThread(Runnable task);
}
