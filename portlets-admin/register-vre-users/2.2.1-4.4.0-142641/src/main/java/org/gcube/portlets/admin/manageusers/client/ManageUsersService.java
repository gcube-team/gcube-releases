package org.gcube.portlets.admin.manageusers.client;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.admin.manageusers.shared.PortalUserDTO;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("manageUsersServlet")
public interface ManageUsersService extends RemoteService {
	ArrayList<PortalUserDTO> getAvailableUsers();
	boolean registerUsers(List<PortalUserDTO> users2Register);
}
