package org.gcube.portlets.user.joinvre.client;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.gcube.portal.databook.shared.UserInfo;
import org.gcube.portlets.user.joinvre.shared.TabbedPage;
import org.gcube.portlets.user.joinvre.shared.VRE;
import org.gcube.portlets.user.joinvre.shared.VRECategory;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 */
@RemoteServiceRelativePath("JoinService")
public interface JoinService extends RemoteService {
	
	List<TabbedPage> isTabbedPanel(); 	
	
	LinkedHashMap<VRECategory, ArrayList<VRE>> getVREs();
	
	LinkedHashMap<VRECategory, ArrayList<VRE>> getPortalSitesMappedToVRE(String tabName);
	
	LinkedHashMap<VRECategory, ArrayList<VRE>> getVREsByOrganisation(String organisationName);
	
	LinkedHashMap<VRECategory, ArrayList<VRE>> getVREsByCategory(String categoryName);
	
	List<String> getAllOrganisations();
	
	ArrayList<String> getAllCategories();
	
	String joinVRE(Long vreId);
	
	VRE getSelectedVRE(Long vreId);
	
	void addMembershipRequest(VRE theVRE, String optionalMessage);
	
	boolean registerUser(String scope, long vreId, boolean isInvitation);
	
	String isExistingInvite(long siteId);
	
	String getTermsOfUse(long siteId);
	
	UserInfo readInvite(String inviteId, long siteId);
}
