/**
 * 
 */
package org.gcube.informationsystem.impl.entity.facet;

import java.net.URI;
import java.util.Calendar;

import org.gcube.informationsystem.impl.entity.FacetImpl;
import org.gcube.informationsystem.model.entity.facet.EventFacet;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
@JsonTypeName(value=EventFacet.NAME)
public class EventFacetImpl extends FacetImpl implements EventFacet {

	/**
	 * Generated Serial version UID
	 */
	private static final long serialVersionUID = -4130548762073254058L;
	
	protected Calendar date;
	protected String value;
	protected URI schema;
	
	public Calendar getDate(){
		return this.date;
	}
	
	public void setDate(Calendar date) {
		this.date = date;
	}
	
	/**
	 * @return the value
	 */
	@Override
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	@Override
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the schema
	 */
	@Override
	public URI getSchema() {
		return schema;
	}

	/**
	 * @param schema the schema to set
	 */
	@Override
	public void setSchema(URI schema) {
		this.schema = schema;
	}

}
