/**
 * 
 */
package org.gcube.informationsystem.model.entity.facet;

import java.net.URI;
import java.util.Calendar;

import org.gcube.informationsystem.impl.entity.facet.EventFacetImpl;
import org.gcube.informationsystem.model.annotations.ISProperty;
import org.gcube.informationsystem.model.entity.Facet;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#Event_Facet
 */
@JsonDeserialize(as=EventFacetImpl.class)
public interface EventFacet extends Facet {

	public static final String NAME = "EventFacet"; // EventFacet.class.getSimpleName();
	public static final String DESCRIPTION = "Event Facet";
	public static final String VERSION = "1.0.0";
	
	@ISProperty(mandatory=true, nullable=false)
	public Calendar getDate();
	
	public void setDate(Calendar date);
	
	@ISProperty(mandatory=true, nullable=false)
	public String getValue();
	
	public void setValue(String value);

	@ISProperty
	public URI getSchema();
	
	public void setSchema(URI schema);
}
