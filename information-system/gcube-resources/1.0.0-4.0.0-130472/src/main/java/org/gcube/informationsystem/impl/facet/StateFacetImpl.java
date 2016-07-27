/**
 * 
 */
package org.gcube.informationsystem.impl.facet;

import java.net.URI;

import org.gcube.informationsystem.impl.entity.FacetImpl;
import org.gcube.informationsystem.model.facet.StateFacet;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public class StateFacetImpl extends FacetImpl implements StateFacet {
	
	protected String value;

	protected URI schema;

	
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
