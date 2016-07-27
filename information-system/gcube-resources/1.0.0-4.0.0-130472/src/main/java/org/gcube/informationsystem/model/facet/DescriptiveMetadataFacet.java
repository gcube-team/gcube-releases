/**
 * 
 */
package org.gcube.informationsystem.model.facet;

import org.gcube.informationsystem.model.embedded.ValueSchema;
import org.gcube.informationsystem.model.entity.Facet;



/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * https://redmine.d4science.org/projects/bluebridge/wiki/Facets#Descriptive-Metadata-Facet
 * Goal: to collect any descriptive metadata about the resource.
 */
public interface DescriptiveMetadataFacet extends ValueSchema, Facet {
	
	public static final String NAME = DescriptiveMetadataFacet.class.getSimpleName();
	public static final String DESCRIPTION = "Collect any descriptive metadata about the resource";
	public static final String VERSION = "1.0.0";

}
