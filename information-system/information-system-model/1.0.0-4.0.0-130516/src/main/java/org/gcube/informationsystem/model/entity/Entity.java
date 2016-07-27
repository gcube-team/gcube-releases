/**
 * 
 */
package org.gcube.informationsystem.model.entity;

import org.gcube.informationsystem.model.annotations.Abstract;
import org.gcube.informationsystem.model.annotations.ISProperty;
import org.gcube.informationsystem.model.embedded.Header;


/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
@Abstract
public interface Entity {
	
	public static final String NAME = Entity.class.getSimpleName();
	
	public static final String HEADER_PROPERTY = "header";
	
	@ISProperty(name=HEADER_PROPERTY, mandatory=true, nullable=false)
	public Header getHeader();
	
}
