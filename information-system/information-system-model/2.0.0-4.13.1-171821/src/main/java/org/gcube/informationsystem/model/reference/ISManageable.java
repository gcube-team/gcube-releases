/**
 * 
 */
package org.gcube.informationsystem.model.reference;

import org.gcube.informationsystem.model.reference.annotations.Abstract;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@Abstract
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = ISManageable.CLASS_PROPERTY)
public interface ISManageable {

	public static final String NAME = "ISManageable"; //ISManageable.class.getSimpleName();
	
	public static final String CLASS_PROPERTY = "@class";
	
	public static final String SUPERCLASSES_PROPERTY = "@superClasses";
	
}
