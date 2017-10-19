package org.gcube.portlets.admin.invitessent.client;

import java.util.ArrayList;

import org.gcube.portal.databook.shared.Invite;
import org.gcube.portal.databook.shared.InviteStatus;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("invites")
public interface InvitesService extends RemoteService {
  ArrayList<Invite> getInvites(InviteStatus[] statuses);
}
