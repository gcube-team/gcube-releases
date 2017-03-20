package org.gcube.portlets.widgets.wsmail.client;

import java.util.ArrayList;

import org.gcube.portlets.widgets.wsmail.shared.CurrUserAndPortalUsersWrapper;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("mailWisdgetServlet")
public interface WsMailService extends RemoteService {
	CurrUserAndPortalUsersWrapper getWorkspaceUsers();
	
	boolean sendToById(ArrayList<String> listContactsId, ArrayList<String> listAttachmentsId, String subject, String body);
}
