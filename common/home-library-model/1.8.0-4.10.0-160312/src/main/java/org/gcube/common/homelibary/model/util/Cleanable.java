package org.gcube.common.homelibary.model.util;


/**
 * Declare a method for explicitly releasing resources bound to the object.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public interface Cleanable {
	
	public void releaseResources();

}
