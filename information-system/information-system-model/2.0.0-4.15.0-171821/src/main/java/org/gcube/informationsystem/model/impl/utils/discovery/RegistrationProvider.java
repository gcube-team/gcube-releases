package org.gcube.informationsystem.model.impl.utils.discovery;

import java.util.List;

/**
 * @author Luca Frosini (ISTI - CNR)
 * Any model which requires to register the defined types from marshalling and unmarshalling
 * must implement this interface returning the list of packages containing the interfaces
 * representing the model.
 */
public interface RegistrationProvider {
	
	/**
	 * This method must return the list of packages to be registered from marshalling/unmarshalling
	 * @return
	 */
	public List<Package> getPackagesToRegister();
	
}
