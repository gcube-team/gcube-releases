/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace.events;

import java.util.List;

import org.gcube.common.homelibrary.home.User;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public interface WorkspaceSentEvent extends WorkspaceEvent {
	
	/**
	 * @return the addressees.
	 */
	public List<User> getAddressees();

}
