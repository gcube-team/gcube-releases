/**
 * 
 */
package org.gcube.informationsystem.model.embedded;

import java.net.URI;

import org.gcube.informationsystem.model.annotations.ISProperty;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * Base Interface for all type described by a value and a schema retrieved 
 * from a URI.
 */
public interface ValueSchema extends Embedded {
	
	public static final String NAME = ValueSchema.class.getSimpleName();
	
	@ISProperty
	public String getValue();
	
	public void setValue(String value);

	@ISProperty
	public URI getSchema();
	
	public void setSchema(URI schema);
	
}
