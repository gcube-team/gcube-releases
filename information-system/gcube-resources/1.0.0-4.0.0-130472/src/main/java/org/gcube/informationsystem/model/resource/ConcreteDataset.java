/**
 * 
 */
package org.gcube.informationsystem.model.resource;


/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public interface ConcreteDataset extends Dataset {

	public static final String NAME = ConcreteDataset.class.getSimpleName();
	public static final String DESCRIPTION = "Collect Dataset information through the list of its facets";
	public static final String VERSION = "1.0.0";
	
}
