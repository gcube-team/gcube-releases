package org.gcube.portlets.user.joinnew.client;

import java.util.ArrayList;

import org.gcube.portlets.user.joinnew.shared.VO;
import org.gcube.portlets.user.joinnew.shared.VRE;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface JoinNewService extends RemoteService {
	String isExistingInvite(long organizationid);

	boolean registerUser(String scope, long organizationid);

	VRE getSelectedVRE(long organizationId);

	Boolean isUserRegistered();

	ArrayList<VO> getInfrastructureVOs();

	VO getRootVO();

	void addMembershipRequest(String scope, String optionalMessage);

	void loadLayout(String scope, String URL);	
}
