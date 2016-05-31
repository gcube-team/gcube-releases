package org.gcube.portlets.user.joinnew.client;

import java.util.ArrayList;

import org.gcube.portlets.user.joinnew.shared.VO;
import org.gcube.portlets.user.joinnew.shared.VRE;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface JoinNewServiceAsync {

	void getSelectedVRE(long organizationId,
			AsyncCallback<VRE> callback);

	void isUserRegistered(AsyncCallback<Boolean> callback);

	void getInfrastructureVOs(AsyncCallback<ArrayList<VO>> callback);

	void getRootVO(AsyncCallback<VO> callback);

	void addMembershipRequest(String scope, String optionalMessage,
			AsyncCallback<Void> callback);

	void loadLayout(String scope, String URL, AsyncCallback<Void> callback);

	void registerUser(String scope, long organizationid,
			AsyncCallback<Boolean> callback);

	void isExistingInvite(long organizationid, AsyncCallback<String> callback);
}
