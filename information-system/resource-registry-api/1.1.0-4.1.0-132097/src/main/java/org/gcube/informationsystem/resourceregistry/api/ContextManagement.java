/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.api;

import org.gcube.informationsystem.resourceregistry.api.exceptions.InternalException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextCreationException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextNotFoundException;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public interface ContextManagement {

	public String create(String parentContextUUID, String name) throws ContextCreationException, InternalException;
	
	public String read(String contextUUID) throws ContextNotFoundException, ContextException;
	
	public String rename(String contextUUID, String newName) throws ContextNotFoundException, ContextException;
	
	public String move(String newParentUUID, String contextToMoveUUID) throws ContextNotFoundException, ContextException;
	
	public String delete(String uuid) throws ContextNotFoundException, ContextException;
	
}