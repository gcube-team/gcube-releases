/**
 * 
 */
package org.gcube.informationsystem.impl.entity.facet;

import java.net.URI;

import org.gcube.informationsystem.impl.entity.FacetImpl;
import org.gcube.informationsystem.model.entity.facet.SubjectFacet;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=SubjectFacet.NAME)
public class SubjectFacetImpl extends FacetImpl implements SubjectFacet {
	
	/**
	 * Generated Serial version UID
	 */
	private static final long serialVersionUID = -266229852500187672L;

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
