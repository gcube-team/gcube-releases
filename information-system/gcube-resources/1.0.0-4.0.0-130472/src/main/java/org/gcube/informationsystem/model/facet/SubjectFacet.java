/**
 * 
 */
package org.gcube.informationsystem.model.facet;

import org.gcube.informationsystem.model.embedded.ValueSchema;
import org.gcube.informationsystem.model.entity.Facet;


/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * https://redmine.d4science.org/projects/bluebridge/wiki/Facets#Subject-Facet
 * Goal: to collect any topic-related information about the resource.
 */
public interface SubjectFacet extends ValueSchema, Facet {
	
	public static final String NAME = SubjectFacet.class.getSimpleName();
	public static final String DESCRIPTION = "Collect any topic-related information about the resource";
	public static final String VERSION = "1.0.0";
	
}
