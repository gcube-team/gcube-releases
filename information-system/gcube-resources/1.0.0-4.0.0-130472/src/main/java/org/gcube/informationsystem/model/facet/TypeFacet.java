/**
 * 
 */
package org.gcube.informationsystem.model.facet;

import org.gcube.informationsystem.model.embedded.ValueSchema;
import org.gcube.informationsystem.model.entity.Facet;


/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * https://redmine.d4science.org/projects/bluebridge/wiki/Facets#Type-Facet
 * Goal: to collect any nature or genre information about the resource beyond 
 * that inferred by the facets.
 */
public interface TypeFacet extends ValueSchema, Facet {
	
	public static final String NAME = TypeFacet.class.getSimpleName();
	public static final String DESCRIPTION = "Collect any nature or genre information about the resource beyond";
	public static final String VERSION = "1.0.0";
	
}
