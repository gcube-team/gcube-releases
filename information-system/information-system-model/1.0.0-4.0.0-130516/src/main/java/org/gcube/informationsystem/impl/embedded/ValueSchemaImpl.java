/**
 * 
 */
package org.gcube.informationsystem.impl.embedded;

import java.net.URI;

import org.gcube.informationsystem.model.embedded.ValueSchema;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public class ValueSchemaImpl implements ValueSchema {

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
