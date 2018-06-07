/**
 * 
 */
package org.gcube.data.tr.neo.nodes;

/**
 * The binding mode for a {@link PersistentNode}.
 * 
 * @author Fabio Simeoni
 *
 */
public enum BindingMode {
	
	/**
	 * Binding mode for read-only operations.
	 */
	READ,
	/**
	 * Binding mode for update operations.
	 */
	UPDATE,
	
	/**
	 * Binding mode for add operations.
	 */
	ADD
}
