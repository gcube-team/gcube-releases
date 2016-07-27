/**
 * 
 */
package org.gcube.informationsystem.model.entity;

import org.gcube.informationsystem.model.annotations.ISProperty;


/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public interface Context extends Entity {
	
	public static final String NAME = Context.class.getSimpleName();
	
	public static final String NAME_PROPERTY = "name";
	
	@ISProperty(name=NAME_PROPERTY, mandatory=true, nullable=false)
	public String getName();
	
	public void setName(String name);
}
