/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace.acl;

import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.gcube.common.homelibrary.home.User;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public interface Capabilities {
	
	/**
	 * Check if the user have the capability for the specified operation.
	 * @param user the user to check.
	 * @param operation the operation to check.
	 * @return <code>true</code> if is enabled, <code>false</code> otherwise.
	 */
	boolean checkCapability(User user, Operation operation);
	
	/**
	 * Returns a list of capability for the specified user.
	 * @param user the user.
	 * @return the capability list.
	 */
	List<Capability> getCapabilities(User user);
	
	/**
	 * @return all capabilities for all the users.
	 */
	Set<Entry<User, List<Capability>>> getAllCapabilities();
	
	/**
	 * @return <code>true</code> if the element is shared.
	 */
	boolean isShared();

}
