package org.gcube.informationsystem.resource_checker.beans;

/**
 * Evaluate at which level the resource must be checked.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public enum ContextLevel {

	VO, // check only at VO level (and root vo)
	ALL // check at all levels
	
}
