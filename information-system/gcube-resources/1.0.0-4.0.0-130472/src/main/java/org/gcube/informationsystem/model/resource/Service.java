/**
 * 
 */
package org.gcube.informationsystem.model.resource;

import org.gcube.informationsystem.model.entity.Resource;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public interface Service extends Resource {

	public static final String NAME = Service.class.getSimpleName();
	public static final String DESCRIPTION = "Collect Service information through the list of its facets";
	public static final String VERSION = "1.0.0";
	
}
