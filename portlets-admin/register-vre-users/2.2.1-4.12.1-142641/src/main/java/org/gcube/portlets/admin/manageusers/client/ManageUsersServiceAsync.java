package org.gcube.portlets.admin.manageusers.client;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.admin.manageusers.shared.PortalUserDTO;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>ManageUsersService</code>.
 */
public interface ManageUsersServiceAsync {
	

	void getAvailableUsers(AsyncCallback<ArrayList<PortalUserDTO>> callback);

	void registerUsers(List<PortalUserDTO> users2Register,
			AsyncCallback<Boolean> callback);
}
