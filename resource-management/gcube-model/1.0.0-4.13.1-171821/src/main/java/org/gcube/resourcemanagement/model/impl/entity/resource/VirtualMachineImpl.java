/**
 * 
 */
package org.gcube.resourcemanagement.model.impl.entity.resource;

import org.gcube.resourcemanagement.model.reference.entity.resource.VirtualMachine;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=VirtualMachine.NAME)
public class VirtualMachineImpl extends ServiceImpl implements VirtualMachine {

	/**
	 * Generated Serial version UID
	 */
	private static final long serialVersionUID = 4432884828103841956L;
	
}
