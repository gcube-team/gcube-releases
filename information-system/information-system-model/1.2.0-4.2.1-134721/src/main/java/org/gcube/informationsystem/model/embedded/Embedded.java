/**
 * 
 */
package org.gcube.informationsystem.model.embedded;

import java.io.Serializable;

import org.gcube.informationsystem.impl.utils.Entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author Luca Frosini (ISTI - CNR)
 * Base Class for Embedded types. It creates an hierarchy which is useful
 * for management purpose.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = Entities.CLASS_PROPERTY)
@JsonIgnoreProperties(ignoreUnknown=true)
public interface Embedded extends Serializable {
	
	public static final String NAME = "Embedded"; //Embedded.class.getSimpleName();
	
}
