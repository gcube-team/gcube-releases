/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.api;

import java.util.UUID;

import org.gcube.informationsystem.resourceregistry.api.exceptions.InternalException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextCreationException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextNotFoundException;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public interface ContextManagement {

	public String create(UUID parentContext, String name) throws ContextCreationException, InternalException;
	
	public String read(UUID context) throws ContextNotFoundException, ContextException;
	
	public String rename(UUID context, String newName) throws ContextNotFoundException, ContextException;
	
	public String move(UUID newParent, UUID contextToMove) throws ContextNotFoundException, ContextException;
	
	public boolean delete(UUID context) throws ContextNotFoundException, ContextException;
	
	
}