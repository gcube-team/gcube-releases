package org.gcube.portlets.user.contactinformation.client;

import java.util.HashMap;

import org.gcube.portlets.user.contactinformation.shared.ContactType;
import org.gcube.portlets.user.contactinformation.shared.UserContext;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it)
 * @author Massimiliano Assante at ISTI-CNR 
 * (massimiliano.assante@isti.cnr.it)
 */
@RemoteServiceRelativePath("contact")
public interface ContactInfoService extends RemoteService {
	// retrieve current user information
	UserContext getUserContext(String userid);
	// update contact information
	boolean updateContactInformation(HashMap<ContactType, String> contactInfo);
}
