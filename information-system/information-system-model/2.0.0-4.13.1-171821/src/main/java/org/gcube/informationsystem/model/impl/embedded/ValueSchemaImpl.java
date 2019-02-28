/**
 * 
 */
package org.gcube.informationsystem.model.impl.embedded;

import java.net.URI;

import org.gcube.informationsystem.model.reference.embedded.ValueSchema;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=ValueSchema.NAME)
public class ValueSchemaImpl extends EmbeddedImpl implements ValueSchema {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -7510493487587070094L;

	protected String value;

	protected URI schema;
	
	public ValueSchemaImpl(){
		super();
	}
	
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
