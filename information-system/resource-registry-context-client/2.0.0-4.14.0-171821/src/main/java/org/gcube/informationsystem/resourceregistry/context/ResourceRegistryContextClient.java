package org.gcube.informationsystem.resourceregistry.context;

import java.util.List;
import java.util.UUID;

import org.gcube.informationsystem.model.reference.entity.Context;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextAlreadyPresentException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextNotFoundException;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public interface ResourceRegistryContextClient {

	public Context create(Context context)
			throws ContextAlreadyPresentException, ResourceRegistryException;

	public String create(String context)
			throws ContextAlreadyPresentException, ResourceRegistryException;
	
	
	public Context read(Context context)
			throws ContextNotFoundException, ResourceRegistryException;
	
	public Context read(UUID uuid)
			throws ContextNotFoundException, ResourceRegistryException;
	
	public String read(String uuid)
			throws ContextNotFoundException, ResourceRegistryException;
	
	
	public Context update(Context context)
			throws ResourceRegistryException;

	public String update(String context)
			throws ResourceRegistryException;
	
	
	public boolean delete(Context context)
			throws ContextNotFoundException, ResourceRegistryException;
	
	public boolean delete(UUID uuid)
			throws ContextNotFoundException, ResourceRegistryException;
	
	public boolean delete(String uuid)
			throws ContextNotFoundException, ResourceRegistryException;
	
	public List<Context> all() throws ResourceRegistryException;
	
}
