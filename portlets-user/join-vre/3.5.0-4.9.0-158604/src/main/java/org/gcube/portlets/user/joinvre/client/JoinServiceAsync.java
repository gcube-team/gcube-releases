package org.gcube.portlets.user.joinvre.client;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.gcube.portal.databook.shared.UserInfo;
import org.gcube.portlets.user.joinvre.shared.TabbedPage;
import org.gcube.portlets.user.joinvre.shared.VRE;
import org.gcube.portlets.user.joinvre.shared.VRECategory;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 */
public interface JoinServiceAsync {

	void getVREs(AsyncCallback< LinkedHashMap<VRECategory, ArrayList<VRE>>> callback);
	
	void joinVRE(Long vreId, AsyncCallback<String> callback);

	void getSelectedVRE(Long vreId, AsyncCallback<VRE> callback);

	void addMembershipRequest(VRE theVRE, String optionalMessage, 
			AsyncCallback<Void> callback);

	void registerUser(String scope, long vreId, boolean isInvitation,
			AsyncCallback<Boolean> callback);

	void isExistingInvite(long siteId, AsyncCallback<String> callback);

	void readInvite(String inviteId, long siteId, AsyncCallback<UserInfo> callback);

	void getTermsOfUse(long siteId, AsyncCallback<String> callback);

	void isTabbedPanel(AsyncCallback<List<TabbedPage>> callback);

	void getPortalSitesMappedToVRE(String tabName, AsyncCallback<LinkedHashMap<VRECategory, ArrayList<VRE>>> callback);

	void getVREsByOrganisation(String organisationName,
			AsyncCallback<LinkedHashMap<VRECategory, ArrayList<VRE>>> callback);

	void getAllOrganisations(AsyncCallback<List<String>> callback);

	void getAllCategories(AsyncCallback<ArrayList<String>> callback);

	void getVREsByCategory(String categoryName, AsyncCallback<LinkedHashMap<VRECategory, ArrayList<VRE>>> callback);
}
