package org.gcube.portlets.user.contactinformation.client;

import org.gcube.portlets.user.contactinformation.shared.UserContext;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("contact")
public interface ContactInfoService extends RemoteService {
	UserContext getUserContext(String userid);
}
