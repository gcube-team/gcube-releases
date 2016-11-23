/**
 * 
 */
package org.gcube.informationsystem.model.embedded;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * Base Class for Embedded types. It creates an hierarchy which is useful
 * for management purpose.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public interface Embedded extends Serializable {
	
	public static final String NAME = "Embedded"; //Embedded.class.getSimpleName();
	
}
