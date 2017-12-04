package org.gcube.data.streams.delegates;

import org.gcube.data.streams.Stream;

/**
 * A listener of key events in the iteration of a target {@link Stream}.   
 * 
 * @author Fabio Simeoni
 *
 */
public interface StreamListener {

	/**
	 * Invoked after the first element of the target {@link Stream} has been iterated over. 
	 */
	void onStart();
	
	/**
	 * Invoked after the last element of the target {@link Stream} has been iterated over. 
	 */
	void onEnd();
	
	/**
	 * Invoked then stream is closed.
	 */
	void onClose();
}
