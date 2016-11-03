/**
 * 
 */
package org.gcube.informationsystem.impl.embedded;

import java.net.URI;

import org.gcube.informationsystem.model.embedded.ValueSchema;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
@JsonTypeName(value=ValueSchema.NAME)
public class ValueSchemaImpl implements ValueSchema {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -7510493487587070094L;

	protected String value;

	protected URI schema;
	
	@Override
	public String getValue() {
		return this.value;
	}

	@Override
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public URI getSchema() {
		return this.schema;
	}

	@Override
	public void setSchema(URI schema) {
		this.schema = schema;
	}

}
