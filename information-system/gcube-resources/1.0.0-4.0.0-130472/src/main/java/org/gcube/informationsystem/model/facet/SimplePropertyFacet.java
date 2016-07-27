/**
 * 
 */
package org.gcube.informationsystem.model.facet;

import org.gcube.informationsystem.model.embedded.ValueSchema;
import org.gcube.informationsystem.model.entity.Facet;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * https://redmine.d4science.org/projects/bluebridge/wiki/Facets#Any-Simple-Property-Facet
 * Base Interface for all facets described by a value and the schema of the 
 * value which can be retrieved from a URI.
 */
public interface SimplePropertyFacet extends ValueSchema, Facet {
	
	public static final String NAME = SimplePropertyFacet.class.getSimpleName();
	public static final String DESCRIPTION = "Base Interface for all facets described by a value and the schema of the value which can be retrieved from a URI";
	public static final String VERSION = "1.0.0";
	
}
