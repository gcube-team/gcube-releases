/**
 * 
 */
package org.gcube.informationsystem.model.facet;

import org.gcube.informationsystem.model.embedded.ValueSchema;
import org.gcube.informationsystem.model.entity.Facet;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * https://redmine.d4science.org/projects/bluebridge/wiki/Facets#State-Facet
 */
public interface StateFacet extends ValueSchema, Facet {

	public static final String NAME = StateFacet.class.getSimpleName();
	public static final String DESCRIPTION = "State Information";
	public static final String VERSION = "1.0.0";
	
}
