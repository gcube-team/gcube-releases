package org.gcube.informationsystem.resourceregistry.er;

import org.gcube.informationsystem.model.reference.ER;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;

public abstract class AbstractERManagementTest<Er extends ER> {

	public abstract Er create(Er er) throws ResourceRegistryException;
	
	public abstract Er update(Er er) throws ResourceRegistryException;
	
	public abstract Er read(Er er) throws ResourceRegistryException;
	
	public abstract boolean delete(Er er) throws ResourceRegistryException;
}
