package org.gcube.portlets.user.contactinformation.client;

import java.util.HashMap;

import org.gcube.portlets.user.contactinformation.shared.ContactType;
import org.gcube.portlets.user.contactinformation.shared.UserContext;

import com.google.gwt.user.client.rpc.AsyncCallback;
/**
 * 
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it)
 * @author Massimiliano Assante at ISTI-CNR 
 * (massimiliano.assante@isti.cnr.it)
 */
public interface ContactInfoServiceAsync {
	void getUserContext(String userid, AsyncCallback<UserContext> callback);

	void updateContactInformation(HashMap<ContactType, String> contactInfo,
			AsyncCallback<Boolean> callback);
}
