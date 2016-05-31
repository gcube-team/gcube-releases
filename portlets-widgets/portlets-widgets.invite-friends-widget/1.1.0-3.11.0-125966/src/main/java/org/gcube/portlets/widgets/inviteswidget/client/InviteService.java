package org.gcube.portlets.widgets.inviteswidget.client;

import org.gcube.portal.databook.shared.InviteOperationResult;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface InviteService extends RemoteService {
	InviteOperationResult sendInvite(String name, String lastName, String email) throws IllegalArgumentException;
}
