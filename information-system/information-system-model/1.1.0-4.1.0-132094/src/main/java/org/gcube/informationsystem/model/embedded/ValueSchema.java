/**
 * 
 */
package org.gcube.informationsystem.model.embedded;

import java.net.URI;

import org.gcube.informationsystem.impl.embedded.ValueSchemaImpl;
import org.gcube.informationsystem.model.annotations.ISProperty;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
@JsonDeserialize(as=ValueSchemaImpl.class)
public interface ValueSchema extends Embedded {
	
	public static final String NAME = "ValueSchema"; //ValueSchema.class.getSimpleName();
	
	@ISProperty
	public String getValue();
	
	public void setValue(String value);

	@ISProperty
	public URI getSchema();
	
	public void setSchema(URI schema);
	
}
