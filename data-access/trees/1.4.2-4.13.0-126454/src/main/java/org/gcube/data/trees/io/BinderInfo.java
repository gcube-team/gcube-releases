package org.gcube.data.trees.io;

import javax.xml.namespace.QName;

import org.gcube.data.trees.Constants;

/**
 * Describes a {@link TreeBinder}.
 * 
 * 
 * @author Fabio Simeoni
 *
 */
public interface BinderInfo {

	public static final QName TREE_FORM = new QName(Constants.TREE_NS,"tree");
	/**
	 * Returns the name of the binder.
	 * @return the name
	 */
	String name();
	
	/**
	 * Returns a description for the binder. 
	 * @return the description
	 */
	String description();
	
	/**
	 * Returns the name for the class of bound trees.
	 * 
	 * @return the tree type
	 */
	QName treeForm();
	
	/**
	 * Returns the name of the bound data type
	 *  
	 * @return the name
	 */
	QName type();
}
