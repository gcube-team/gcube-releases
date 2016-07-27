/**
 * 
 */
package org.gcube.informationsystem.model.facet;

import org.gcube.informationsystem.model.annotations.ISProperty;
import org.gcube.informationsystem.model.embedded.ValueSchema;
import org.gcube.informationsystem.model.entity.Facet;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * https://redmine.d4science.org/projects/bluebridge/wiki/Facets#Event-Facet
 */
public interface EventFacet extends Facet {

	public static final String NAME = EventFacet.class.getSimpleName();
	public static final String DESCRIPTION = "Event Facet";
	public static final String VERSION = "1.0.0";
	
	@ISProperty
	public ValueSchema getType();
	
	public void setType(ValueSchema type);
	
	@ISProperty
	public ValueSchema getDate();
	
	public void setDate(ValueSchema date);
}
