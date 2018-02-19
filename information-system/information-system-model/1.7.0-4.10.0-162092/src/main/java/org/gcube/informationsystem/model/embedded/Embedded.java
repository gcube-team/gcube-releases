/**
 * 
 */
package org.gcube.informationsystem.model.embedded;

import java.io.Serializable;

import org.gcube.informationsystem.model.ISManageable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author Luca Frosini (ISTI - CNR)
 * Base Class for Embedded types. It creates an hierarchy which is useful
 * for management purpose.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = ISManageable.CLASS_PROPERTY)
@JsonIgnoreProperties(ignoreUnknown=true)
public interface Embedded extends ISManageable, Serializable {
	
	public static final String NAME = "Embedded"; //Embedded.class.getSimpleName();
	public static final String DESCRIPTION = "This is the base class for Embedded";
	public static final String VERSION = "1.0.0";
}
