/**
 * 
 */
package org.gcube.resourcemanagement.model.reference.entity.resource;

import org.gcube.resourcemanagement.model.impl.entity.resource.VirtualMachineImpl;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#Virtual_Machine
 */
@JsonDeserialize(as=VirtualMachineImpl.class)
public interface VirtualMachine extends Service {

	public static final String NAME = "VirtualMachine"; //VirtualMachine.class.getSimpleName();
	public static final String DESCRIPTION = "Collect Hosting Node information through the list of its facets";
	public static final String VERSION = "1.0.0";
	
}
