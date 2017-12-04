package org.gcube.portlets.user.vremembers.client;

import java.util.ArrayList;

import org.gcube.portlets.user.vremembers.shared.BelongingUser;
import org.gcube.portlets.user.vremembers.shared.VREGroup;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("memberservice")
public interface MembersService extends RemoteService {
  ArrayList<BelongingUser> getSiteUsers();
  VREGroup getVREGroupUsers(String teamId);
  VREGroup getVREManagers();
}
