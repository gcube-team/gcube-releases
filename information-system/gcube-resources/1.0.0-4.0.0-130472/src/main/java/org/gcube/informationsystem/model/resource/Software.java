/**
 * 
 */
package org.gcube.informationsystem.model.resource;

import org.gcube.informationsystem.model.entity.Resource;


/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public interface Software extends Resource {

	public static final String NAME = Software.class.getSimpleName();
	public static final String DESCRIPTION = "Collect Software information through the list of its facets";
	public static final String VERSION = "1.0.0";
	
}
